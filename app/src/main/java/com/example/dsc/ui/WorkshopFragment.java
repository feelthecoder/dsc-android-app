package com.example.dsc.ui;

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

import com.example.dsc.Model.WorkGet;
import com.example.dsc.R;
import com.example.dsc.ViewHolder.ViewSpace;
import com.example.dsc.EventActivity.WRegistrationActivity;
import com.example.dsc.EventActivity.WorkshopDetailActivity;
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

public class WorkshopFragment extends Fragment {
    RecyclerView recyclerView;
    DatabaseReference dRef;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    LinearLayoutManager HorizontalLayout;
    RelativeLayout connect;
    ProgressBar workProgress;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_workshps, container, false);
        recyclerView=view.findViewById(R.id.work_recycler);
        connect=view.findViewById(R.id.no_connect_work);
        dRef= FirebaseDatabase.getInstance().getReference("Workshops");
        workProgress=view.findViewById(R.id.work_progress);
        workProgress.setVisibility(View.VISIBLE);
        RecyclerViewLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(RecyclerViewLayoutManager);
        HorizontalLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        HorizontalLayout.setReverseLayout(true);
        HorizontalLayout.setStackFromEnd(true);
        recyclerView.setLayoutManager(HorizontalLayout);
        if(isOnline()) {
            getWorkshop();
            connect.setVisibility(View.INVISIBLE);
        }
        else
        {
            connect.setVisibility(View.VISIBLE);
        }
        return view;
    }

    private void getWorkshop() {
        FirebaseRecyclerOptions<WorkGet> options =
                new FirebaseRecyclerOptions.Builder<WorkGet>()
                        .setQuery(dRef, WorkGet.class).build();

        FirebaseRecyclerAdapter<WorkGet, WorkshopFragment.WorkshopViewHolder> adapter = new FirebaseRecyclerAdapter<WorkGet, WorkshopViewHolder>(options) {



            @Override
            protected void onBindViewHolder(@NonNull final WorkshopFragment.WorkshopViewHolder workshopViewHolder, int i, @NonNull final WorkGet workUpload) {
                final String postID = getRef(i).getKey();
                assert postID != null;
                dRef.child(postID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("title")) {
                            workProgress.setVisibility(View.INVISIBLE);
                            final String Caption = Objects.requireNonNull(dataSnapshot.child("title").getValue()).toString();
                            final String link = Objects.requireNonNull(dataSnapshot.child("pic").getValue()).toString();
                            final String shortdes = Objects.requireNonNull(dataSnapshot.child("shortdes").getValue()).toString();
                            final String desc=Objects.requireNonNull(dataSnapshot.child("description").getValue()).toString();
                            final String date=Objects.requireNonNull(dataSnapshot.child("date").getValue()).toString();

                            workshopViewHolder.mTitle.setText(Caption);
                            workshopViewHolder.mShort.setText(shortdes);
                            workshopViewHolder.mDate.setText(date);


                            Random rnd = new Random();
                            int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                            workshopViewHolder.parent.setBackgroundColor(currentColor);

                            if(currentColor==Color.BLACK){
                                workshopViewHolder.mDate.setTextColor(Color.WHITE);

                            }
                            if(currentColor==Color.WHITE)
                            {
                                workshopViewHolder.mTitle.setTextColor(Color.BLACK);
                                workshopViewHolder.mShort.setTextColor(Color.BLACK);
                            }


                            workshopViewHolder.part.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent=new Intent(getActivity(), WRegistrationActivity.class);
                                    intent.putExtra("title",Caption);
                                    intent.putExtra("event","Workshop");
                                    intent.putExtra("date",date);

                                    startActivity(intent);
                                }
                            });

                            workshopViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent=new Intent(getActivity(), WorkshopDetailActivity.class);
                                    intent.putExtra("title",Caption);
                                    intent.putExtra("image",link);
                                    intent.putExtra("short",shortdes);
                                    intent.putExtra("description",desc);
                                    intent.putExtra("date",date);
                                    startActivity(intent);
                                }
                            });
                            setGuide("Workshops","Swipe to see more workshops and click to see details.", workshopViewHolder.mShort,"swipeW");
                            setGuide("Paricipate","Click to Participate in this workshop.", workshopViewHolder.part,"partW");



                        }
                        else
                        {
                            workProgress.setVisibility(View.INVISIBLE);
                            Toast.makeText(getActivity(), "There are no workshops available currently", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public WorkshopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.workshop_list,parent,false);
                return new WorkshopFragment.WorkshopViewHolder(view);
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

    private static class WorkshopViewHolder extends RecyclerView.ViewHolder {

        TextView mTitle, mShort,mDate;
        Button part;
        CardView parent;


        public WorkshopViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.txt_title_workshop);
            mShort = itemView.findViewById(R.id.desc_short_workshop);
            parent=itemView.findViewById(R.id.card_workshop);
            mDate = itemView.findViewById(R.id.date_workshop);
            part=itemView.findViewById(R.id.btn_participate_w);
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



