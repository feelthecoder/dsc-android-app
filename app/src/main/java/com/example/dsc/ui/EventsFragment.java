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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dsc.EventDetailActivity;
import com.example.dsc.Model.EventGet;
import com.example.dsc.R;
import com.example.dsc.RegistrationActivity;
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


public class EventsFragment extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference dRef;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    LinearLayoutManager HorizontalLayout;
    RelativeLayout connect;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_events, container, false);
        recyclerView=view.findViewById(R.id.events_recycler);
        connect=view.findViewById(R.id.no_connect_event);
        dRef= FirebaseDatabase.getInstance().getReference("Events");
        RecyclerViewLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(RecyclerViewLayoutManager);
        HorizontalLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        HorizontalLayout.setReverseLayout(true);
        HorizontalLayout.setStackFromEnd(true);
        recyclerView.setLayoutManager(HorizontalLayout);
        if(isOnline()) {
            getEevnts();
            connect.setVisibility(View.INVISIBLE);
        }
        else
        {
            connect.setVisibility(View.VISIBLE);
        }
        return view;
    }

    private void getEevnts() {
        FirebaseRecyclerOptions<EventGet> options =
                new FirebaseRecyclerOptions.Builder<EventGet>()
                        .setQuery(dRef, EventGet.class).build();

        FirebaseRecyclerAdapter<EventGet, EventsFragment.EventViewHolder> adapter = new FirebaseRecyclerAdapter<EventGet, EventsFragment.EventViewHolder>(options) {



            @Override
            protected void onBindViewHolder(@NonNull final EventsFragment.EventViewHolder eventViewHolder, int i, @NonNull final EventGet eventUpload) {
                final String postID = getRef(i).getKey();
                assert postID != null;
                dRef.child(postID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("title")) {
                            final String Caption = Objects.requireNonNull(dataSnapshot.child("title").getValue()).toString();
                            final String link = Objects.requireNonNull(dataSnapshot.child("pic").getValue()).toString();
                            final String shortdes = Objects.requireNonNull(dataSnapshot.child("shortdes").getValue()).toString();
                            final String desc=Objects.requireNonNull(dataSnapshot.child("description").getValue()).toString();
                            final String date=Objects.requireNonNull(dataSnapshot.child("date").getValue()).toString();

                            eventViewHolder.mTitle.setText(Caption);
                            eventViewHolder.mShort.setText(shortdes);
                            eventViewHolder.mDate.setText(date);


                            Random rnd = new Random();
                            int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                            eventViewHolder.parent.setBackgroundColor(currentColor);

                            if(currentColor==Color.BLACK){
                                eventViewHolder.mDate.setTextColor(Color.WHITE);

                            }
                            if(currentColor==Color.WHITE)
                            {
                                eventViewHolder.mTitle.setTextColor(Color.BLACK);
                                eventViewHolder.mShort.setTextColor(Color.BLACK);
                            }


                            eventViewHolder.part.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent=new Intent(getActivity(), RegistrationActivity.class);
                                    intent.putExtra("title",Caption);
                                    intent.putExtra("event","Event");
                                    intent.putExtra("date",date);
                                    startActivity(intent);
                                }
                            });

                            eventViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent=new Intent(getActivity(), EventDetailActivity.class);
                                    intent.putExtra("title",Caption);
                                    intent.putExtra("image",link);
                                    intent.putExtra("short",shortdes);
                                    intent.putExtra("description",desc);
                                    intent.putExtra("date",date);
                                    startActivity(intent);
                                }
                            });

                            setGuide("Events","Swipe to see more events and click to see details.", eventViewHolder.mShort,"swipeE");
                            setGuide("Paricipate","Click to Participate in this event.", eventViewHolder.part,"partE");



                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public EventsFragment.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_layout,parent,false);
                return new EventsFragment.EventViewHolder(view);
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
                    .setGravity(Gravity.center)
                    .setTargetView(view)
                    .setDismissType(DismissType.outside) //optional - default dismissible by TargetView
                    .build()
                    .show();

            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString(key,"yes");
            editor.apply();
        }
    }


    private static class EventViewHolder extends RecyclerView.ViewHolder {

        TextView mTitle, mShort,mDate;
        Button part;
        CardView parent;


        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.txt_title_event);
            mShort = itemView.findViewById(R.id.desc_short_event);
            parent=itemView.findViewById(R.id.card_event);
            mDate = itemView.findViewById(R.id.date_event);
            part=itemView.findViewById(R.id.btn_participate);
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