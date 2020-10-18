package com.example.dsc.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dsc.ClaimActivity;
import com.example.dsc.Model.LeaderSimple;
import com.example.dsc.R;
import com.example.dsc.ViewHolder.ViewSpace;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LeaderboardFragment extends Fragment {


    RecyclerView recyclerView;
    Button btn;
    DatabaseReference lRef,uRef;
    ProgressBar progressBar;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_leaderboard, container, false);
        lRef= FirebaseDatabase.getInstance().getReference("Achievment");
        uRef= FirebaseDatabase.getInstance().getReference("Users");
        recyclerView=view.findViewById(R.id.releader);
        btn=view.findViewById(R.id.btnclaim);
        progressBar=view.findViewById(R.id.progress_leader);
        progressBar.setVisibility(View.VISIBLE);
        loadDataToArrayList();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), ClaimActivity.class);
                startActivity(intent);
            }
        });
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        return view;
    }



    private void loadDataToArrayList() {
        Query queryRef = lRef.orderByChild("DP");
        FirebaseRecyclerOptions<LeaderSimple> options=
                new FirebaseRecyclerOptions.Builder<LeaderSimple>()
                        .setQuery(queryRef,LeaderSimple.class).build();

        FirebaseRecyclerAdapter<LeaderSimple, LeaderboardFragment.LeaderViewHolder> adapter = new FirebaseRecyclerAdapter<LeaderSimple, LeaderboardFragment.LeaderViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final LeaderboardFragment.LeaderViewHolder leaderViewHolder, final int i, @NonNull final LeaderSimple leaderSimple) {
                final String leaderID=getRef(i).getKey();
                assert leaderID != null;
                lRef.child(leaderID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("DP")){
                            progressBar.setVisibility(View.INVISIBLE);

                            final String name= Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                            final String image= Objects.requireNonNull(dataSnapshot.child("image").getValue()).toString();
                            final String quizWon= Objects.requireNonNull(dataSnapshot.child("quizwon").getValue()).toString();
                            final String skills= Objects.requireNonNull(dataSnapshot.child("skills").getValue()).toString();
                            final String points = Objects.requireNonNull(dataSnapshot.child("DP").getValue()).toString();

                            leaderViewHolder.name.setText(name);
                            leaderViewHolder.points.setText(points);
                            Glide.with(getContext()).load(image).into(leaderViewHolder.image);

                        }
                        else{
                            Toast.makeText(getActivity(), "Leaderboard not available", Toast.LENGTH_SHORT).show();
                        }

                    }



                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public LeaderboardFragment.LeaderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.list_content_leader,parent,false);
                return new LeaderboardFragment.LeaderViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        ViewSpace itemDecoration = new ViewSpace(getContext(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);
        adapter.startListening();
    }


    private static class LeaderViewHolder extends RecyclerView.ViewHolder{

        TextView name,points;
        CircularImageView image;

        public LeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.leader_name);
            points=itemView.findViewById(R.id.leader_points);
            image=itemView.findViewById(R.id.leader_image);
            

        }
    }
}

