package com.example.dsc;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.dsc.Adapters.MessageAdapter;
import com.example.dsc.Model.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChatActivity extends AppCompatActivity {

    final String UID="hG7x1c1oZ7VzqgHFtcvgyT5SO6y2";
    RecyclerView msg;
    ImageButton btn_send;
    EditText message;

    MessageAdapter messageAdapter;
    List<Chat> mChat;
    DatabaseReference reference;
    String mg;
    ProgressBar chatProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        setContentView(R.layout.activity_chat);

        chatProgress=findViewById(R.id.chat_progress);
        chatProgress.setVisibility(View.VISIBLE);

        Toolbar toolbar=findViewById(R.id.tool_chat);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Q/A Chat");

        message=findViewById(R.id.message_send);
        btn_send=findViewById(R.id.msg_btn_send);
        msg=findViewById(R.id.recycler_message);

        msg.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        msg.setLayoutManager(linearLayoutManager);


        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals(""))
                    btn_send.setVisibility(View.GONE);
                else
                    btn_send.setVisibility(View.VISIBLE);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });




        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mg= message.getText().toString();
                if(mg.isEmpty()){
                    message.requestFocus();
                    message.setError("Your question can't be empty.");
                    return;

                }else
                {
                    sendMessage(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid().toString(),UID,mg);
                }
                message.setText("");
            }
        });

        readMesssage(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid().toString(),UID, String.valueOf(R.drawable.dsclogo));


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void sendMessage(String sender,String receiver,String message){
        reference= FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> hasMap = new HashMap<>();
        hasMap.put("sender",sender);
        hasMap.put("receiver",receiver);
        hasMap.put("message",message);

        reference.child("Chats").push().setValue(hasMap);
    }




    private void readMesssage(String myid, String userid,String imageUrl){
        mChat=new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for(DataSnapshot snap: dataSnapshot.getChildren()){
                    Chat chat=snap.getValue(Chat.class);
                    if((Objects.requireNonNull(chat).getReceiver().equals(myid) && chat.getSender().equals(userid) )|| (chat.getReceiver().equals(userid)&& chat.getSender().equals(myid))){
                        mChat.add(chat);
                        chatProgress.setVisibility(View.INVISIBLE);

                    }

                }
                messageAdapter= new MessageAdapter(ChatActivity.this,mChat,imageUrl);
                msg.setAdapter(messageAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
