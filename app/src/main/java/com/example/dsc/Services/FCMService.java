package com.example.dsc.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import com.example.dsc.NotificationActivity;
import com.example.dsc.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class FCMService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            NotificationChannel channel= new NotificationChannel("FCMN","UPDATE", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("To make genral updates to app users.");
            NotificationManager manager= getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
    }

    public void showNotification(String title,String msg){

        Intent i= new Intent(this, NotificationActivity.class);
        String mes=title+" "+" "+msg;
        i.putExtra("name",msg);

        PendingIntent intent= PendingIntent.getActivity(getApplicationContext(),100,i,PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder builder= new NotificationCompat.Builder(this,"FCMN")
                .setContentTitle(title)
                .setSmallIcon(R.drawable.dsclogo)
                .setAutoCancel(true)
                .setContentIntent(intent)
                .setContentText(msg);
        NotificationManagerCompat manager= NotificationManagerCompat.from(this);
        manager.notify(999,builder.build());


    }
}
