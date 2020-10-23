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

import com.example.dsc.Model.TrainGet;
import com.example.dsc.R;
import com.example.dsc.EventActivity.TRegistrationActivity;
import com.example.dsc.EventActivity.TrainDetailActivity;
import com.example.dsc.ViewHolder.ViewSpace;
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


public class TrainingFragment extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference dRef;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    LinearLayoutManager HorizontalLayout;
    RelativeLayout connect;
    ProgressBar trainProgress;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_training, container, false);
        recyclerView=view.findViewById(R.id.train_recycler);
        connect=view.findViewById(R.id.no_connect_train);
        trainProgress=view.findViewById(R.id.train_progress);
        trainProgress.setVisibility(View.VISIBLE);
        dRef= FirebaseDatabase.getInstance().getReference("Training");
        RecyclerViewLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(RecyclerViewLayoutManager);
        HorizontalLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        HorizontalLayout.setReverseLayout(true);
        HorizontalLayout.setStackFromEnd(true);
        recyclerView.setLayoutManager(HorizontalLayout);
        if(isOnline()) {
            getTraining();
            connect.setVisibility(View.INVISIBLE);
        }else{
            connect.setVisibility(View.VISIBLE);
        }
        return view;
    }

    private void getTraining() {
        FirebaseRecyclerOptions<TrainGet> options =
                new FirebaseRecyclerOptions.Builder<TrainGet>()
                        .setQuery(dRef, TrainGet.class).build();

        FirebaseRecyclerAdapter<TrainGet, TrainingFragment.TrainViewHolder> adapter = new FirebaseRecyclerAdapter<TrainGet, TrainingFragment.TrainViewHolder>(options) {



            @Override
            protected void onBindViewHolder(@NonNull final TrainingFragment.TrainViewHolder trainViewHolder, final int i, @NonNull final TrainGet trainUpload) {
                final String postID = getRef(i).getKey();
                assert postID != null;
                dRef.child(postID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("title")) {
                            trainProgress.setVisibility(View.INVISIBLE);
                            final String Caption = Objects.requireNonNull(dataSnapshot.child("title").getValue()).toString();
                            final String link = Objects.requireNonNull(dataSnapshot.child("pic").getValue()).toString();
                            final String shortdes = Objects.requireNonNull(dataSnapshot.child("shortdes").getValue()).toString();
                            final String desc=Objects.requireNonNull(dataSnapshot.child("description").getValue()).toString();
                            final String date=Objects.requireNonNull(dataSnapshot.child("date").getValue()).toString();






                            trainViewHolder.mTitle.setText(Caption);
                            trainViewHolder.mShort.setText(shortdes);
                            trainViewHolder.mDate.setText(date);


                            Random rnd = new Random();
                            int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                            trainViewHolder.parent.setBackgroundColor(currentColor);

                            if(currentColor==Color.BLACK){
                                trainViewHolder.mDate.setTextColor(Color.WHITE);

                            }
                            if(currentColor==Color.WHITE)
                            {
                                trainViewHolder.mTitle.setTextColor(Color.BLACK);
                                trainViewHolder.mShort.setTextColor(Color.BLACK);
                            }


                            trainViewHolder.part.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent=new Intent(getActivity(), TRegistrationActivity.class);
                                    intent.putExtra("title",Caption);
                                    intent.putExtra("event","Training");
                                    intent.putExtra("date",date);
                                    startActivity(intent);
                                }
                            });

                            trainViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent=new Intent(getActivity(), TrainDetailActivity.class);
                                    intent.putExtra("title",Caption);
                                    intent.putExtra("image",link);
                                    intent.putExtra("short",shortdes);
                                    intent.putExtra("description",desc);
                                    intent.putExtra("date",date);
                                    startActivity(intent);
                                }
                            });
                            setGuide("Trainings","Swipe to see more trainings and click to see details.", trainViewHolder.mShort,"swipeT");
                            setGuide("Paricipate","Click to Participate in this training.", trainViewHolder.part,"partT");



                        }
                        else
                        {
                            trainProgress.setVisibility(View.INVISIBLE);
                            Toast.makeText(getActivity(), "There are no trainings available currently", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public TrainingFragment.TrainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.train_list_layout,parent,false);
                return new TrainingFragment.TrainViewHolder(view);
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


    private static class TrainViewHolder extends RecyclerView.ViewHolder {

        TextView mTitle, mShort,mDate;
        Button part;
        CardView parent;


        public TrainViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.txt_title_train);
            mShort = itemView.findViewById(R.id.desc_short_train);
            parent=itemView.findViewById(R.id.card_train_e);
            mDate = itemView.findViewById(R.id.date_train);
            part=itemView.findViewById(R.id.btn_participate_t);
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


