package com.feelthecoder.dsc.ui;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.feelthecoder.dsc.ChatActivity;
import com.feelthecoder.dsc.FormsActivity;
import com.feelthecoder.dsc.Main1Activity;
import com.feelthecoder.dsc.Model.SlideModel;
import com.feelthecoder.dsc.Model.TechData;
import com.feelthecoder.dsc.Model.WishDay;
import com.feelthecoder.dsc.NotificationActivity;
import com.feelthecoder.dsc.PeopleLikedActivity;
import com.feelthecoder.dsc.PostTechActivity;
import com.feelthecoder.dsc.R;
import com.feelthecoder.dsc.ResultActivity;
import com.feelthecoder.dsc.SettingsActivity.ViewProfileActivity;
import com.feelthecoder.dsc.ViewHolder.ViewSpace;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private ViewFlipper viewFlipper;
    private DatabaseReference databaseReference,dRef,dRefe;
    private RecyclerView recyclerView;
    private List<SlideModel> slideLists;
    public Fragment fm =null;
    CircularImageView fb,insta,twitter,utube,web,gmail;
    FloatingActionButton floatingActionButton ;
    FirebaseAuth mAuth;
    String currentUserID;
    Boolean mProcessLike=false;
    DatabaseReference aRef;
    private Animation likeAnimate;
    String date_;
    TextView dp_won_user,quiz_won;
    TextView fest;
    FirebaseRecyclerOptions<TechData> options;
    View view;
    FirebaseRecyclerAdapter<TechData,TechViewHolder> adapter;

    ProgressBar postProress;



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        floatingActionButton = ((Main1Activity) getActivity()).getFloatingActionButton();
        RelativeLayout s1 = getView().findViewById(R.id.edu_scroll);
        RelativeLayout s2 =getView().findViewById(R.id.train_scroll);
        RelativeLayout s3 =getView().findViewById(R.id.workshop_scroll);
        RelativeLayout s4 =getView().findViewById(R.id.event_scroll);
        RelativeLayout s5 =getView().findViewById(R.id.quiz_scroll);
        RelativeLayout s6 =getView().findViewById(R.id.compete_scroll);
        RelativeLayout s7 =getView().findViewById(R.id.tech_scroll);
        RelativeLayout s8 =getView().findViewById(R.id.project_scroll);
        RelativeLayout s9 =getView().findViewById(R.id.leaderboard_scroll);
        RelativeLayout s10 =getView().findViewById(R.id.donate_scroll);
        RelativeLayout s11 =getView().findViewById(R.id.about_scroll);
        RelativeLayout s12 =getView().findViewById(R.id.forms_scroll);
        RelativeLayout s13 =getView().findViewById(R.id.result_scroll);



        s1.setOnClickListener(this);
        s2.setOnClickListener(this);
        s3.setOnClickListener(this);
        s4.setOnClickListener(this);
        s5.setOnClickListener(this);
        s6.setOnClickListener(this);
        s7.setOnClickListener(this);
        s8.setOnClickListener(this);
        s9.setOnClickListener(this);
        s10.setOnClickListener(this);
        s11.setOnClickListener(this);
        s12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(), FormsActivity.class);
                startActivity(i);
            }
        });
        s13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(), ResultActivity.class);
                startActivity(i);

            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        initialize(view);

      postProress=view.findViewById(R.id.progress_post);
        fb=view.findViewById(R.id.facebook);
        insta=view.findViewById(R.id.insta);
        twitter=view.findViewById(R.id.twitter);
        utube=view.findViewById(R.id.youtube);
        web=view.findViewById(R.id.web);
        gmail=view.findViewById(R.id.gmail);
        recyclerView=view.findViewById(R.id.post_tech);
        fest=view.findViewById(R.id.fest);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAuth=FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();
        dp_won_user=view.findViewById(R.id.dp_won_user);
        quiz_won=view.findViewById(R.id.won_ch);
        dRefe=FirebaseDatabase.getInstance().getReference("MainData");
        dRef=FirebaseDatabase.getInstance().getReference("MainData").child("TechPosts");
        aRef=FirebaseDatabase.getInstance().getReference("Achievment").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        implementClick();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        date_ = simpleDateFormat.format(new Date());
        String[] actual_date=date_.split("/");
        String formateed_date;
        String day=actual_date[0];
        String month=actual_date[1];
        formateed_date=day+"/"+month;
        putDateToMain(formateed_date);
        final TextView txt=view.findViewById(R.id.dsc_note);
        dRefe.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("mText")){
                    String rText= Objects.requireNonNull(dataSnapshot.child("mText").getValue()).toString();
                    txt.setText(rText);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        txt.setSelected(true);

        aRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("dp")){
                    String dp= Objects.requireNonNull(dataSnapshot.child("dp").getValue()).toString();
                    dp_won_user.setText("DP : "+dp);
                    String quizwon= Objects.requireNonNull(dataSnapshot.child("quizwon").getValue()).toString();
                    quiz_won.setText("You won "+quizwon+" quiz challenges.");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        databaseReference = FirebaseDatabase.getInstance().getReference("Switcher");
        slideLists = new ArrayList<>();
        postProress.setVisibility(View.VISIBLE);
        setPostTech();
        return view;
    }



    private void putDateToMain(String formateed_date) {
        int pos=0;
        for(int i=0;i< WishDay.dates.length;i++){
            if(formateed_date.equals(WishDay.dates[i])){
                pos=i;
                break;
            }
        }
        fest.setText(WishDay.fest[pos]);

    }

    private void setPostTech() {


        options=
                new FirebaseRecyclerOptions.Builder<TechData>()
                        .setQuery(dRef,TechData.class).build();
           adapter = new FirebaseRecyclerAdapter<TechData, TechViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final TechViewHolder techViewHolder, final int i, @NonNull final TechData techData) {
                final String postID=getRef(i).getKey();
                assert postID != null;
                final DatabaseReference likeRef=FirebaseDatabase.getInstance().getReference("MainData").child("TechPosts").child(postID).child("LIKES");
                final DatabaseReference userRef=FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                dRef.child(postID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("tImg")){
                            loadDrawableLike(likeRef,techViewHolder);

                            final String caption= Objects.requireNonNull(dataSnapshot.child("tCaption").getValue()).toString();
                            final String desc= Objects.requireNonNull(dataSnapshot.child("tDescription").getValue()).toString();
                            final String img= Objects.requireNonNull(dataSnapshot.child("tImg").getValue()).toString();
                            final String comment= Objects.requireNonNull(dataSnapshot.child("tComment").getValue()).toString()+" Comments";
                            final String like = Objects.requireNonNull(dataSnapshot.child("tLike").getValue()).toString() + " Likes";
                            final String share= Objects.requireNonNull(dataSnapshot.child("tShare").getValue()).toString()+" Shares";


                            likeAnimate = AnimationUtils.loadAnimation(getActivity(),R.anim.blink);
                            final int shareall = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("tShare").getValue()).toString());
                            final int totallike = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("tLike").getValue()).toString());

                            final String[] image = new String[1];
                            final String[] name = new String[1];
                            userRef.addValueEventListener(new ValueEventListener() {
                               @Override
                               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                   if(dataSnapshot.hasChild("profileImageUrl")) {
                                       image[0] = Objects.requireNonNull(dataSnapshot.child("profileImageUrl").getValue()).toString();
                                       name[0] = Objects.requireNonNull(dataSnapshot.child("displayName").getValue()).toString();
                                   }
                                   else
                                   {
                                       Toast.makeText(getActivity(), "Complete your profile to like a post.", Toast.LENGTH_SHORT).show();
                                   }
                               }

                               @Override
                               public void onCancelled(@NonNull DatabaseError databaseError) {

                               }
                           }) ;


                            techViewHolder.mTitle.setText(caption);
                            techViewHolder.mDescription.setText(desc);
                            techViewHolder.mLike.setText(like);
                            techViewHolder.mComment.setText(comment);
                            techViewHolder.mShare.setText(share);

                            Glide.with(requireContext()).load(img).into(techViewHolder.mImage);

                            postProress.setVisibility(View.INVISIBLE);





                            techViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent=new Intent(getActivity(), PostTechActivity.class);
                                    intent.putExtra("title",caption);
                                    intent.putExtra("image",img);
                                    intent.putExtra("description",desc);
                                    intent.putExtra("likes",like);
                                    intent.putExtra("comment",comment);
                                    intent.putExtra("post",postID);
                                    intent.putExtra("share",share);
                                    startActivity(intent);
                                }
                            });


                            techViewHolder.like.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mProcessLike=true;
                                    likeRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(mProcessLike)
                                            {
                                                if(dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).hasChild("like")){
                                                    dRef.child(postID).child("tLike").setValue(""+(totallike-1));
                                                    likeRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("like").removeValue();
                                                    likeRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("name").removeValue();
                                                    likeRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("image").removeValue();
                                                    techViewHolder.like.setAnimation(likeAnimate);
                                                    mProcessLike=false;
                                                }
                                                else{
                                                    dRef.child(postID).child("tLike").setValue(""+(totallike+1));
                                                    likeRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("like").setValue("Liked");
                                                    likeRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("name").setValue(name[0]);
                                                    likeRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("image").setValue(image[0]);
                                                    techViewHolder.like.setAnimation(likeAnimate);
                                                    mProcessLike=false;
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });


                                }
                            });
                            techViewHolder.share.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent a= new Intent(Intent.ACTION_SEND);
                                    final String appPackageName= getContext().getPackageName();
                                    String strAppLink;
                                    try{
                                        strAppLink="https://play.google.com/store/apps/details?id=" +appPackageName;
                                    }
                                    catch(android.content.ActivityNotFoundException exc){
                                        strAppLink="https://play.google.com/store/apps/details?id=" +appPackageName;
                                    }
                                    a.setType("text/link");
                                    String shareBody=desc+". Download this app to view this post."+strAppLink;
                                    String shareSub=caption;
                                    a.putExtra(Intent.EXTRA_SUBJECT,shareSub);
                                    a.putExtra(Intent.EXTRA_TEXT,shareBody);
                                    dRef.child(postID).child("tShare").setValue(""+(shareall+1));
                                    startActivity(Intent.createChooser(a,"Share Using"));

                                }
                            });
                            techViewHolder.comment.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent=new Intent(getActivity(), PostTechActivity.class);
                                    intent.putExtra("title",caption);
                                    intent.putExtra("image",img);
                                    intent.putExtra("description",desc);
                                    intent.putExtra("likes",like);
                                    intent.putExtra("comment",comment);
                                    intent.putExtra("post",postID);
                                    intent.putExtra("share",share);
                                    startActivity(intent);

                                }
                            });
                            techViewHolder.mLike.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent in=new Intent(getActivity(), PeopleLikedActivity.class);
                                    in.putExtra("post",postID);
                                    startActivity(in);
                                }
                            });
                            techViewHolder.mShare.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent in=new Intent(getActivity(), PeopleLikedActivity.class);
                                    in.putExtra("post",postID);
                                    startActivity(in);
                                }
                            });
                            techViewHolder.mComment.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent=new Intent(getActivity(), PostTechActivity.class);
                                    intent.putExtra("title",caption);
                                    intent.putExtra("image",img);
                                    intent.putExtra("description",desc);
                                    intent.putExtra("post",postID);
                                    intent.putExtra("likes",like);
                                    intent.putExtra("comment",comment);
                                    intent.putExtra("share",share);
                                    startActivity(intent);
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
            public TechViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.tech_card_post,parent,false);
                return new HomeFragment.TechViewHolder(view);
            }
        };


        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        ViewSpace itemDecoration = new ViewSpace(getContext(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);
        adapter.startListening();

    }


    public static String FACEBOOK_URL = "https://www.facebook.com/dscrecbijnor";
    public static String FACEBOOK_PAGE_ID = "dscrecbijnor";

    //method to get the right URL to use in the intent
    public String getFacebookPageURL(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                return "fb://page/" + FACEBOOK_PAGE_ID;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL; //normal web url
        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

    }

    private void implementClick() {
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                String facebookUrl = getFacebookPageURL(getActivity());
                facebookIntent.setData(Uri.parse(facebookUrl));
                startActivity(facebookIntent);

            }
        });
        insta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.instagram.com/dscrecbijnor/");
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                likeIng.setPackage("com.instagram.android");

                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.instagram.com/dscrecbijnor/")));
                }
            }
        });
        utube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.youtube.com/channel/UCF8ZVIfyf8u4w9SaYaUmezA?view_as=subscriber");
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                likeIng.setPackage(" com.google.android.youtube");

                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.youtube.com/channel/UCF8ZVIfyf8u4w9SaYaUmezA?view_as=subscriber")));
                }

            }
        });
        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("https://twitter.com/dscrec");
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                likeIng.setPackage(" com.twitter.android");

                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://twitter.com/dscrec")));
                }
            }
        });
        web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.dscrecbijnor.com");
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                likeIng.setPackage(" /data/app/com.google.android.googlequicksearchbox-1.apk=com.google.android.googlequicksearchbox");

                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.dscrecbijnor.com")));
                }

            }
        });
        gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subject="Write your subject.";
                String content="Write your message here.";
                final Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"dscrecbijnor@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                intent.putExtra(Intent.EXTRA_TEXT, content);
                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();


        if(isOnline()) {
            usingFirebaseDatabase();
        }
        else {
            new AlertDialog.Builder(getActivity()).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Connection Problem")
                    .setMessage("Your Internet connection is turned off or is slow !  please check for better experience. ")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
        }
    }

    private void loadDrawableLike(DatabaseReference likeRef, final TechViewHolder techViewHolder) {
        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).hasChild("like")){
                    techViewHolder.like.setImageResource(R.drawable.ic_liked);

                }else
                {
                    SharedPreferences mPrefs=getContext().getSharedPreferences("MyPrefs",0);
                    String is=mPrefs.getString("mode","not");
                    if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES||is.equals("dark")){
                        techViewHolder.like.setImageResource(R.drawable.ic_like_white);

                    }
                    else
                    {
                        techViewHolder.like.setImageResource(R.drawable.ic_like);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public static class TechViewHolder extends RecyclerView.ViewHolder{

        TextView mTitle,mDescription,mLike,mShare,mComment;
        ImageView mImage,like,share,comment;
        CardView parent;

        public TechViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitle=itemView.findViewById(R.id.tech_caption);
            parent=itemView.findViewById(R.id.parent_tech);
            mDescription=itemView.findViewById(R.id.tech_desc);
            mImage=itemView.findViewById(R.id.tech_img);
            mLike=itemView.findViewById(R.id.txt_likes);
            mComment=itemView.findViewById(R.id.txt_comment);
            mShare=itemView.findViewById(R.id.txt_share);
            like=itemView.findViewById(R.id.likes_tech);
            comment=itemView.findViewById(R.id.comment_tech);
            share=itemView.findViewById(R.id.share_tech);
        }
    }

    private void initialize(View view) {
        viewFlipper = view.findViewById(R.id.imgswitch);
    }

    private void usingFirebaseDatabase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                           slideLists.clear();
                           if(dataSnapshot.exists()){
                               for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                   SlideModel model = snapshot.getValue(SlideModel.class);

                                   slideLists.add(model);

                               }
                               usingFirebaseImages(slideLists);
                           }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getActivity(), "Check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void usingFirebaseImages(List<SlideModel> slideLists) {
            for (int i = 0; i < slideLists.size(); i++) {
                String downloadImageUrl = slideLists.get(i).getImageUrl();
                flipImages(downloadImageUrl);
            }

    }

    private boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            Toast.makeText(getActivity(), "No Internet connection!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;

    }

    @SuppressLint("ClickableViewAccessibility")
    public void flipImages(String imageUrl) {
        ImageView imageView = new ImageView(getActivity());
        imageView.setMaxWidth(ImageView.MEASURED_STATE_MASK);
        Glide.with(requireContext()).load(imageUrl).into(imageView);
        imageView.setAdjustViewBounds(true);
        Animation imgAnimationIn = AnimationUtils.
                loadAnimation(getActivity(), R.anim.slide_in_right);
        imgAnimationIn.setDuration(700);
        Animation imgAnimationOut = AnimationUtils.
                loadAnimation(getActivity(), R.anim.slide_out_left);
        imgAnimationOut.setDuration(700);

        viewFlipper.addView(imageView);

        viewFlipper.setFlipInterval(2500);
        viewFlipper.setAutoStart(true);

        viewFlipper.startFlipping();
        viewFlipper.setInAnimation(imgAnimationIn);
        viewFlipper.setOutAnimation(imgAnimationOut);
    viewFlipper.setOnTouchListener(new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                {
                   viewFlipper.stopFlipping();
                    break;
                }
                case MotionEvent.ACTION_UP:
                {
                    viewFlipper.startFlipping();
                    break;
                }


            }
            return true;
        }
    });

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main1, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public void onClick(View v) {
        if(floatingActionButton!=null) {
            switch (v.getId()) {
                case R.id.edu_scroll:
                    fm = new EducationFragment();
                    floatingActionButton.setVisibility(View.INVISIBLE);
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.nav_edu);
                    ((Main1Activity) requireActivity()).setFragmentObject(fm);
                    break;
                case R.id.train_scroll:
                    floatingActionButton.setVisibility(View.INVISIBLE);
                    fm = new TrainingFragment();
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.nav_train);
                    ((Main1Activity) requireActivity()).setFragmentObject(fm);
                    break;
                case R.id.workshop_scroll:
                    floatingActionButton.setVisibility(View.INVISIBLE);
                    fm = new WorkshopFragment();
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.nav_workshop);
                    ((Main1Activity) requireActivity()).setFragmentObject(fm);
                    break;
                case R.id.event_scroll:
                    floatingActionButton.setVisibility(View.INVISIBLE);
                    fm = new EventsFragment();
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.nav_events);
                    ((Main1Activity) requireActivity()).setFragmentObject(fm);
                    break;
                case R.id.quiz_scroll:
                    floatingActionButton.setVisibility(View.INVISIBLE);
                    fm = new QuizFragment();
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.nav_quiz);
                    ((Main1Activity) requireActivity()).setFragmentObject(fm);
                    break;
                case R.id.compete_scroll:
                    floatingActionButton.setVisibility(View.INVISIBLE);
                    fm = new CompeteFragment();
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.nav_compete);
                    ((Main1Activity) requireActivity()).setFragmentObject(fm);
                    break;
                case R.id.tech_scroll:
                    floatingActionButton.setVisibility(View.INVISIBLE);
                    fm = new SpandanFragment();
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.nav_tech);
                    ((Main1Activity) requireActivity()).setFragmentObject(fm);
                    break;
                case R.id.project_scroll:
                    floatingActionButton.setVisibility(View.INVISIBLE);

                    fm = new ProjectFragment();
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.nav_project);
                    ((Main1Activity) requireActivity()).setFragmentObject(fm);
                    break;
                case R.id.leaderboard_scroll:
                    floatingActionButton.setVisibility(View.INVISIBLE);
                    fm = new LeaderboardFragment();
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.nav_leader);
                    ((Main1Activity) requireActivity()).setFragmentObject(fm);
                    break;
                case R.id.donate_scroll:
                    floatingActionButton.setVisibility(View.INVISIBLE);
                    fm = new DonateFragment();
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.nav_donate);
                    ((Main1Activity) requireActivity()).setFragmentObject(fm);
                    break;
                case R.id.about_scroll:
                    floatingActionButton.setVisibility(View.INVISIBLE);
                    fm = new AboutFragment();
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.nav_about);
                    ((Main1Activity) requireActivity()).setFragmentObject(fm);
                    break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuu: {
                Intent intent=new Intent(getActivity(), NotificationActivity.class);
                intent.putExtra("title","x");
                intent.putExtra("message","x");
                intent.putExtra("activity","x");
                startActivity(intent);
                break;
            }
            case R.id.msg: {
                Intent link=new Intent(getActivity(),ChatActivity.class);
                startActivity(link);
                break;
            }
            case R.id.profi: {
                Intent i = new Intent(getActivity(), ViewProfileActivity.class);
                startActivity(i);
                break;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

}
