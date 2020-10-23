package com.feelthecoder.dsc.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.feelthecoder.dsc.EventActivity.CRegistrationActivity;
import com.feelthecoder.dsc.EventActivity.CompeteDetailActivity;
import com.feelthecoder.dsc.Model.CompeteGet;
import com.feelthecoder.dsc.R;
import com.feelthecoder.dsc.ViewHolder.ViewSpace;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;

public class CompeteFragment extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference dRef;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    LinearLayoutManager HorizontalLayout;
    RelativeLayout connect;
    ProgressBar competeProgress;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_compete, container, false);
        recyclerView=view.findViewById(R.id.compete_recycler);
        connect=view.findViewById(R.id.no_connect_compete);
        competeProgress=view.findViewById(R.id.compete_progress);
        dRef= FirebaseDatabase.getInstance().getReference("Competitions");
        RecyclerViewLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(RecyclerViewLayoutManager);
        HorizontalLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        HorizontalLayout.setReverseLayout(true);
        HorizontalLayout.setStackFromEnd(true);
        recyclerView.setLayoutManager(HorizontalLayout);
        if(isOnline()) {
            competeProgress.setVisibility(View.VISIBLE);
            getCompete();
            connect.setVisibility(View.INVISIBLE);
        }
        else
        {
            connect.setVisibility(View.VISIBLE);
        }
        return view;
    }

    private void getCompete() {
        FirebaseRecyclerOptions<CompeteGet> options =
                new FirebaseRecyclerOptions.Builder<CompeteGet>()
                        .setQuery(dRef, CompeteGet.class).build();

        FirebaseRecyclerAdapter<CompeteGet, CompeteViewHolder> adapter = new FirebaseRecyclerAdapter<CompeteGet, CompeteFragment.CompeteViewHolder>(options) {



            @Override
            protected void onBindViewHolder(@NonNull final CompeteFragment.CompeteViewHolder competeViewHolder, int i, @NonNull final CompeteGet competeUpload) {
                final String postID = getRef(i).getKey();
                assert postID != null;
                dRef.child(postID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("title")) {

                            competeProgress.setVisibility(View.INVISIBLE);
                            final String Caption = Objects.requireNonNull(dataSnapshot.child("title").getValue()).toString();
                            final String link = Objects.requireNonNull(dataSnapshot.child("pic").getValue()).toString();
                            final String shortdes = Objects.requireNonNull(dataSnapshot.child("shortdes").getValue()).toString();
                            final String desc=Objects.requireNonNull(dataSnapshot.child("description").getValue()).toString();
                            final String date=Objects.requireNonNull(dataSnapshot.child("date").getValue()).toString();

                            competeViewHolder.mTitle.setText(Caption);
                            competeViewHolder.mShort.setText(shortdes);
                            competeViewHolder.mDate.setText(date);


                            Random rnd = new Random();
                            int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                            competeViewHolder.parent.setBackgroundColor(currentColor);

                            if(currentColor==Color.BLACK){
                                competeViewHolder.mDate.setTextColor(Color.WHITE);

                            }
                            if(currentColor==Color.WHITE)
                            {
                                competeViewHolder.mTitle.setTextColor(Color.BLACK);
                                competeViewHolder.mShort.setTextColor(Color.BLACK);
                            }


                            competeViewHolder.part.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent=new Intent(getActivity(), CRegistrationActivity.class);
                                    intent.putExtra("title",Caption);
                                    intent.putExtra("event","Competition");
                                    intent.putExtra("date",date);
                                    startActivity(intent);
                                }
                            });

                            competeViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent=new Intent(getActivity(), CompeteDetailActivity.class);
                                    intent.putExtra("title",Caption);
                                    intent.putExtra("image",link);
                                    intent.putExtra("short",shortdes);
                                    intent.putExtra("description",desc);
                                    intent.putExtra("date",date);
                                    startActivity(intent);
                                }
                            });

                            setGuide("Competition","Swipe to see more competition and click to see details.", competeViewHolder.mShort,"swipeC");
                            setGuide("Paricipate","Click to Participate in this competition.", competeViewHolder.part,"partC");


                        }
                        else
                        {
                            competeProgress.setVisibility(View.INVISIBLE);
                            Toast.makeText(getActivity(), "There are no competitions available currently", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public CompeteFragment.CompeteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.compete_list_layout,parent,false);
                return new CompeteFragment.CompeteViewHolder(view);
            }
        };


        recyclerView.setAdapter(adapter);
        ViewSpace itemDecoration = new ViewSpace(getContext(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);
        adapter.startListening();

    }

    private void setGuide(String name,String msg,View view,String key){
        SharedPreferences mPrefs=getActivity().getSharedPreferences("MyGuide",0);
        String is=mPrefs.getString(key,"no");
        if(is.equals("no")){
            new GuideView.Builder(getActivity())
                    .setTitle(name)
                    .setContentText(msg)
                    .setTargetView(view)
                    .setGravity(Gravity.center)
                    .setDismissType(DismissType.outside) //optional - default dismissible by TargetView
                    .build()
                    .show();

            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString(key,"yes");
            editor.apply();
        }
    }


    private static class CompeteViewHolder extends RecyclerView.ViewHolder {

        TextView mTitle, mShort,mDate;
        Button part;
        CardView parent;


        public CompeteViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.txt_title_compete);
            mShort = itemView.findViewById(R.id.desc_short_compete);
            parent=itemView.findViewById(R.id.card_compete);
            mDate = itemView.findViewById(R.id.date_compete);
            part=itemView.findViewById(R.id.btn_participate_c);
        }

    }

    private boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            return false;
        }
        return true;

    }
}


