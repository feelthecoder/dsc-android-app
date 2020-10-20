package com.example.dsc.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dsc.ActivityPDF;
import com.example.dsc.Model.TechSpandan;
import com.example.dsc.R;
import com.example.dsc.ResultActivity;
import com.example.dsc.SpandanEventActivity;
import com.example.dsc.TRegistrationActivity;
import com.example.dsc.ViewHolder.ViewSpace;
import com.example.dsc.ViewSpandanActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

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


public class SpandanFragment extends Fragment implements View.OnClickListener {
    
    Button view_result;
    RecyclerView recyclerView;
    DatabaseReference dRef,lRef;
    String link;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    LinearLayoutManager HorizontalLayout;
    ProgressBar spaProgress;



    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CircularImageView s1 = getView().findViewById(R.id.s11);
        CircularImageView s2 = getView().findViewById(R.id.s12);
        CircularImageView s3 = getView().findViewById(R.id.s13);
        CircularImageView s4 = getView().findViewById(R.id.s14);
        CircularImageView s5 = getView().findViewById(R.id.s15);
        CircularImageView s6 = getView().findViewById(R.id.s16);
        CircularImageView s7 = getView().findViewById(R.id.s17);
        CircularImageView s8 = getView().findViewById(R.id.s18);
        CircularImageView s9 = getView().findViewById(R.id.s19);
        CircularImageView s10 = getView().findViewById(R.id.s20);


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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_spandan, container, false);
        view_result=view.findViewById(R.id.btn_result_view);
        recyclerView=view.findViewById(R.id.spandan_recycler);
        dRef= FirebaseDatabase.getInstance().getReference("SpandanEvents");
        lRef= FirebaseDatabase.getInstance().getReference("About");
        RecyclerViewLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(RecyclerViewLayoutManager);
        spaProgress=view.findViewById(R.id.spandan_progress);
        spaProgress.setVisibility(View.VISIBLE);
        HorizontalLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        HorizontalLayout.setReverseLayout(true);
        HorizontalLayout.setStackFromEnd(true);
        recyclerView.setLayoutManager(HorizontalLayout);



        view_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), ResultActivity.class);
                startActivity(intent);
            }
        });
        loadSpandanEventsFromFirebase();


        return view;
    }

    private void loadSpandanEventsFromFirebase() {

        FirebaseRecyclerOptions<TechSpandan> options=
                new FirebaseRecyclerOptions.Builder<TechSpandan>()
                        .setQuery(dRef,TechSpandan.class).build();

        FirebaseRecyclerAdapter<TechSpandan,SpandanViewHolder> adapter = new FirebaseRecyclerAdapter<TechSpandan,SpandanViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final SpandanFragment.SpandanViewHolder spandanViewHolder, final int i, @NonNull final TechSpandan techSpandan) {
                final String spaID=getRef(i).getKey();
                assert spaID != null;
                dRef.child(spaID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("pic")){
                            spaProgress.setVisibility(View.INVISIBLE);
                            final String caption= Objects.requireNonNull(dataSnapshot.child("title").getValue()).toString();
                            final String desc= Objects.requireNonNull(dataSnapshot.child("description").getValue()).toString();
                            final String sDes= Objects.requireNonNull(dataSnapshot.child("shortdes").getValue()).toString();
                            final String link= Objects.requireNonNull(dataSnapshot.child("pic").getValue()).toString();
                            final String date = Objects.requireNonNull(dataSnapshot.child("date").getValue()).toString();

                            spandanViewHolder.mTitle.setText(caption);
                            spandanViewHolder.mDescription.setText(sDes);
                            spandanViewHolder.mDate.setText(date);


                            Random rnd = new Random();
                            int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                            spandanViewHolder.parent.setBackgroundColor(currentColor);

                            if(currentColor==Color.BLACK){
                                spandanViewHolder.mDate.setTextColor(Color.WHITE);

                            }
                            if(currentColor==Color.WHITE)
                            {
                                spandanViewHolder.mTitle.setTextColor(Color.BLACK);
                                spandanViewHolder.mDescription.setTextColor(Color.BLACK);
                            }
                            spandanViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent=new Intent(getActivity(), SpandanEventActivity.class);
                                    intent.putExtra("title",caption);
                                    intent.putExtra("image",link);
                                    intent.putExtra("description",desc);
                                    intent.putExtra("short",sDes);
                                    intent.putExtra("date",date);
                                    startActivity(intent);
                                }
                            });

                            spandanViewHolder.btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent=new Intent(getActivity(), TRegistrationActivity.class);
                                    intent.putExtra("title",caption);
                                    intent.putExtra("event","Spandan Tech Events");
                                    intent.putExtra("date",date);
                                    startActivity(intent);
                                }
                            });

                            setGuide("Spandan Events","Swipe to see more events and click to see details.", spandanViewHolder.mDescription,"swipeTS");
                            setGuide("Paricipate","Click to Participate in this competition.", spandanViewHolder.btn,"partTS");
                        }
                        else{
                            spaProgress.setVisibility(View.INVISIBLE);
                            Toast.makeText(getActivity(), "There are no events available currently", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public SpandanFragment.SpandanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.spandan_events_list,parent,false);
                return new SpandanFragment.SpandanViewHolder(view);
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.spandan_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about_spa: {
                lRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("aboutpdf")){
                            link= Objects.requireNonNull(dataSnapshot.child("aboutpdf").getValue()).toString();
                            Intent intent = new Intent(getActivity(), ActivityPDF.class);
                            intent.putExtra("link",link);
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(getContext(), "Unable to open more information file", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                break;
            }
            case R.id.send_mail_spa: {
                String subject="Write your subject.";
                String content="Write your message here.";
                final Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"spandan.dscrecbijnor@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                intent.putExtra(Intent.EXTRA_TEXT, content);
                startActivity(Intent.createChooser(intent, "Send Email"));
                break;

            }
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.s11:
                String dRef="2011";
                Intent intent_s11=new Intent(getActivity(), ViewSpandanActivity.class);
                intent_s11.putExtra("link",dRef);
                startActivity(intent_s11);
                break;
            case R.id.s12:
                String s12_link="2012";
                Intent intent_s12=new Intent(getActivity(), ViewSpandanActivity.class);
                intent_s12.putExtra("link",s12_link);
                startActivity(intent_s12);
                break;
            case R.id.s13:
                String s13_link="2013";
                Intent intent_s13=new Intent(getActivity(), ViewSpandanActivity.class);
                intent_s13.putExtra("link",s13_link);
                startActivity(intent_s13);
                break;
                case R.id.s14:
                    String s14_link="2014";
                    Intent intent_s14=new Intent(getActivity(), ViewSpandanActivity.class);
                    intent_s14.putExtra("link",s14_link);
                    startActivity(intent_s14);
                break;
            case R.id.s15:
                String s15_link="2015";
                Intent intent_s15=new Intent(getActivity(), ViewSpandanActivity.class);
                intent_s15.putExtra("link",s15_link);
                startActivity(intent_s15);
                break;
            case R.id.s16:
                String s16_link="2016";
                Intent intent_s16=new Intent(getActivity(), ViewSpandanActivity.class);
                intent_s16.putExtra("link",s16_link);
                startActivity(intent_s16);
                break;
            case R.id.s17:
                String s17_link="2017";
                Intent intent_s17=new Intent(getActivity(), ViewSpandanActivity.class);
                intent_s17.putExtra("link",s17_link);
                startActivity(intent_s17);
                break;
            case R.id.s18:
                String s18_link="2018";
                Intent intent_s18=new Intent(getActivity(), ViewSpandanActivity.class);
                intent_s18.putExtra("link",s18_link);
                startActivity(intent_s18);
                break;
            case R.id.s19:
                String s19_link="2019";
                Intent intent_s19=new Intent(getActivity(), ViewSpandanActivity.class);
                intent_s19.putExtra("link",s19_link);
                startActivity(intent_s19);
                break;
            case R.id.s20:
                String s20_link="2020";
                Intent intent_s20=new Intent(getActivity(), ViewSpandanActivity.class);
                intent_s20.putExtra("link",s20_link);
                startActivity(intent_s20);
                break;
            default:
        }
    }

private static class SpandanViewHolder extends RecyclerView.ViewHolder{

    TextView mTitle,mDescription,mDate;
    Button btn;
    CardView parent;

    public SpandanViewHolder(@NonNull View itemView) {
        super(itemView);
        parent=itemView.findViewById(R.id.card_spandan);
        mTitle=itemView.findViewById(R.id.txt_title_spandan);
        mDescription=itemView.findViewById(R.id.desc_short_spandan);
        mDate=itemView.findViewById(R.id.date_spandan);
        btn=itemView.findViewById(R.id.btn_participate_s);

    }
}
}