package com.feelthecoder.dsc;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.feelthecoder.dsc.Model.CommentModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PostTechActivity extends AppCompatActivity {
    TextView caption,like,share,comment_i,desc;
    EditText editText;
    ImageView img,pic;
    RecyclerView recyclerView;
    String url,title,story,likes,shares,comments;
    DatabaseReference commentRef,addUser,commentT;
    String postID;
    private String name,comm,pics;
    private int come;
     String id;
     ProgressBar commentProgress;


     public static Activity fa;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SharedPreferences mPrefs=getSharedPreferences("MyPrefs",0);
        String is=mPrefs.getString("mode","not");
        if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES||is.equals("dark")){
            setTheme(R.style.DarkTheme);

        }
        else
        {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_tech);
        Toolbar toolbar=findViewById(R.id.tool_comment);
        commentProgress=findViewById(R.id.comment_progresse);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        caption=findViewById(R.id.tech_caption_t);
        like=findViewById(R.id.txt_likes_tech);
        share=findViewById(R.id.txt_share_tech);
        comment_i=findViewById(R.id.txt_comment_tech);
        desc=findViewById(R.id.tech_desc_tech);
        editText=findViewById(R.id.tech_comment);
        editText.requestFocus();
        img=findViewById(R.id.tech_send_comment);
        pic=findViewById(R.id.tech_img_tech);
        recyclerView=findViewById(R.id.comment_recycler);
        url= Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("image")).toString();
        Glide.with(getApplicationContext()).load(url).into(pic);
        title= Objects.requireNonNull(getIntent().getExtras().get("title")).toString();
        caption.setText(title);
        story= Objects.requireNonNull(getIntent().getExtras().get("description")).toString();
        desc.setText(story);
        likes= Objects.requireNonNull(getIntent().getExtras().get("likes")).toString();
        like.setText(likes);
        shares= Objects.requireNonNull(getIntent().getExtras().get("share")).toString();
        share.setText(shares);
        comments= Objects.requireNonNull(getIntent().getExtras().get("comment")).toString();
        comment_i.setText(comments);
        postID= Objects.requireNonNull(getIntent().getExtras().get("post")).toString();
        commentRef= FirebaseDatabase.getInstance().getReference("MainData").child("TechPosts").child(postID).child("COMMENTS");
        addUser=FirebaseDatabase.getInstance().getReference("Users");
        commentT=FirebaseDatabase.getInstance().getReference("MainData").child("TechPosts").child(postID);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        commentProgress.setVisibility(View.VISIBLE);

        fa=this;

        loadComments();

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals(""))
                    img.setVisibility(View.GONE);
                else
                    img.setVisibility(View.VISIBLE);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComments();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

    }

    private void addComments() {
        comm = editText.getText().toString();
       if(comm.isEmpty()) {
            editText.setError("Comment can't be empty!");
            editText.requestFocus();
            return;
        }
        addUser.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("profileImageUrl")) {
                    name = Objects.requireNonNull(dataSnapshot.child("displayName").getValue()).toString();
                    pics = Objects.requireNonNull(dataSnapshot.child("profileImageUrl").getValue()).toString();


                    commentT.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String tComment = Objects.requireNonNull(dataSnapshot.child("tComment").getValue()).toString();
                            come = Integer.parseInt(tComment);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else
                {
                    Toast.makeText(PostTechActivity.this, "Complete your profile to post a comment.", Toast.LENGTH_SHORT).show();
                    return;
                }

                id=FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
                CommentModel commentModel = new CommentModel(comm, name, pics,id);
                commentRef.child(Objects.requireNonNull(commentRef.push().getKey())).setValue(commentModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            commentT.child("tComment").setValue(""+(come + 1));
                            comment_i.setText(come+1+" Comments");
                            Snackbar snackbar = Snackbar
                                    .make(findViewById(R.id.rel_post), "Comment Posted : " + comm, Snackbar.LENGTH_LONG);
                            snackbar.show();
                            editText.setText("");
                        } else {
                            Snackbar snackbar = Snackbar
                                    .make(findViewById(R.id.rel_post), "Can't post comment", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        }


            private void loadComments() {
        FirebaseRecyclerOptions<CommentModel> options =
                new FirebaseRecyclerOptions.Builder<CommentModel>()
                        .setQuery(commentRef, CommentModel.class).build();

                FirebaseRecyclerAdapter<CommentModel,PostTechActivity.CommentViewHolder> adapter= new FirebaseRecyclerAdapter<CommentModel,PostTechActivity.CommentViewHolder>(options) {



            @Override
            protected void onBindViewHolder(@NonNull final PostTechActivity.CommentViewHolder commentViewHolder, int i, @NonNull final CommentModel commentModel) {
                final String commentID =getRef(i).getKey();
                assert commentID != null;
                commentRef.child(commentID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("comment")) {
                            final String name = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                            final String link = Objects.requireNonNull(dataSnapshot.child("pic").getValue()).toString();
                            final String comment = Objects.requireNonNull(dataSnapshot.child("comment").getValue()).toString();
                            final String current_user=dataSnapshot.child("id").getValue().toString();

                            commentViewHolder.name.setText(name);
                            commentViewHolder.comme.setText(comment);
                            Glide.with(PostTechActivity.this).load(link).into(commentViewHolder.imageView);
                            commentProgress.setVisibility(View.INVISIBLE);
                            commentViewHolder.parent.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    if (Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid().toString().equals(current_user)) {
                                                        commentT.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                String tComment = Objects.requireNonNull(dataSnapshot.child("tComment").getValue()).toString();
                                                                come = Integer.parseInt(tComment);
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                                        commentRef.child(commentID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    commentT.child("tComment").setValue(""+(come - 1));
                                                                    comment_i.setText(come-1+" Comments");
                                                                    Snackbar snackbar = Snackbar
                                                                            .make(findViewById(R.id.rel_post), "Comment Deleted!", Snackbar.LENGTH_LONG);
                                                                    snackbar.show();
                                                                }
                                                            }
                                                        });

                                                    }
                                    else
                                    {
                                        Toast.makeText(PostTechActivity.this, "You can't delete this ", Toast.LENGTH_SHORT).show();
                                    }
                                    return true;
                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public PostTechActivity.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_layout, parent, false);
                PostTechActivity.CommentViewHolder commentViewHolder= new CommentViewHolder(view);
                return commentViewHolder;
            }

        };
                commentProgress.setVisibility(View.INVISIBLE);
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }


   private static class CommentViewHolder extends RecyclerView.ViewHolder {

    TextView name, comme;
    CircularImageView imageView;
    RelativeLayout parent;


    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.name_comment);
        comme = itemView.findViewById(R.id.comment_main);
        imageView = itemView.findViewById(R.id.dp_comment);
        parent=itemView.findViewById(R.id.coment_relative_id);
    }
}



}

