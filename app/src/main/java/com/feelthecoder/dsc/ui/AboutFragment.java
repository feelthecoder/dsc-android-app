package com.feelthecoder.dsc.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.feelthecoder.dsc.AboutActivity.BoardActivity;
import com.feelthecoder.dsc.AboutActivity.DesignActivity;
import com.feelthecoder.dsc.AboutActivity.ManagementActivity;
import com.feelthecoder.dsc.AboutActivity.MembersActivity;
import com.feelthecoder.dsc.AboutActivity.TechnicalActivity;
import com.feelthecoder.dsc.ActivityPDF;
import com.feelthecoder.dsc.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static com.feelthecoder.dsc.R.layout.fragment_about;

public class AboutFragment extends Fragment {

    DatabaseReference dRef;
    String link;
    String[] mobileArray = {"About DSC","Board","Management Team","Technical Team",
            "Designer Team","Members","Privacy Policy"};
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(fragment_about, container, false);
        dRef= FirebaseDatabase.getInstance().getReference("About");
        ArrayAdapter adapter = new ArrayAdapter<String>(getContext(),R.layout.list_details, mobileArray);
        ListView listView = view.findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    dRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild("about")){
                                link=dataSnapshot.child("about").getValue().toString();

                                Intent intent=new Intent(getActivity(), ActivityPDF.class);
                                intent.putExtra("link",link);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });



                }
                else
                if(position==1){
                    startActivity(new Intent(getActivity(), BoardActivity.class));
                }
                else
                if(position==2){
                    startActivity(new Intent(getActivity(), ManagementActivity.class));
                }
                else
                if(position==3){
                    startActivity(new Intent(getActivity(), TechnicalActivity.class));
                }
                else
                if(position==4){
                    startActivity(new Intent(getActivity(), DesignActivity.class));
                }
                else
                if(position==5){
                    startActivity(new Intent(getActivity(), MembersActivity.class));
                }
                else
                if(position==6){
                    dRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild("policy")){
                                link=dataSnapshot.child("policy").getValue().toString();

                                Intent intent=new Intent(getActivity(), ActivityPDF.class);
                                intent.putExtra("link",link);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
            }
        });

        return view;
    }
}

