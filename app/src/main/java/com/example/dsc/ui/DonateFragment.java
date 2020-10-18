package com.example.dsc.ui;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dsc.DonateHistoryActivity;
import com.example.dsc.Model.Donate;
import com.example.dsc.NotificationActivity;
import com.example.dsc.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shreyaspatil.EasyUpiPayment.EasyUpiPayment;
import com.shreyaspatil.EasyUpiPayment.listener.PaymentStatusListener;
import com.shreyaspatil.EasyUpiPayment.model.TransactionDetails;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;


public class DonateFragment extends Fragment implements PaymentStatusListener {

    Button pay;
    EditText amount;
    String payment;
    DatabaseReference write;
    Double amo;
    String mail_Sent="0";

    EasyUpiPayment easyUpiPayment;

    private static final String CHANNEL_ID="DONATE";
    private static final String CHANNEL_NAME="Donation";
    private static final String CHANNEL_DESC="This Channel shows notifications about successful donation done.";


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_donate, container, false);
        amount=view.findViewById(R.id.gpay);
        pay=view.findViewById(R.id.paytm);
        write=FirebaseDatabase.getInstance().getReference("Donation").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                payment=amount.getText().toString().trim();
                if(payment.isEmpty()){
                    amount.setError("Donation amount is required");
                    amount.requestFocus();
                    return;
                }
                amo= Double.valueOf(payment);
                paymentDonate();


            }
        });

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            NotificationChannel channel= new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager=getActivity().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        return view;
    }


    private void paymentDonate() {
        String note="Payment done to DSC for donation";
        Random rand= new Random();
        String refNo=String.valueOf(rand.nextInt(9))+String.valueOf(rand.nextInt(9))+String.valueOf(rand.nextInt(9))+String.valueOf(rand.nextInt(9))+String.valueOf(rand.nextInt(9))+String.valueOf(rand.nextInt(9))+String.valueOf(rand.nextInt(9))+String.valueOf(rand.nextInt(9))+String.valueOf(rand.nextInt(9))+String.valueOf(rand.nextInt(9));
        String TnsID=String.valueOf(rand.nextInt(9))+String.valueOf(rand.nextInt(9))+String.valueOf(rand.nextInt(9))+String.valueOf(rand.nextInt(9))+String.valueOf(rand.nextInt(9))+String.valueOf(rand.nextInt(9))+String.valueOf(rand.nextInt(9))+String.valueOf(rand.nextInt(9))+String.valueOf(rand.nextInt(9))+String.valueOf(rand.nextInt(9))+String.valueOf(rand.nextInt(9))+String.valueOf(rand.nextInt(9))+String.valueOf(rand.nextInt(9));

        easyUpiPayment = new EasyUpiPayment.Builder()
                .with(getActivity())
                .setPayeeVpa("9536516618@ybl")
                .setPayeeName("Developer Student Club Bijnor")
                .setTransactionId(TnsID)
                .setTransactionRefId(refNo)
                .setDescription(note)
                .setAmount(String.valueOf(amo))
                .build();

        easyUpiPayment.setPaymentStatusListener(this);
        easyUpiPayment.startPayment();


    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.donate_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.donated: {
                Intent intent=new Intent(getActivity(), DonateHistoryActivity.class);
                startActivity(intent);
                break;

            }
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }


    private void displayNotification() {

        Intent intent= new Intent(getContext(), NotificationActivity.class);
        intent.putExtra("title","Donation Successful");
        intent.putExtra("message","You have successfully donated to DSC fund. Happy Coding.");
        intent.putExtra("activity","DonateHistoryActivity.class");

        PendingIntent pendingIntent=PendingIntent.getActivity(getContext(),100,intent,PendingIntent.FLAG_CANCEL_CURRENT);



        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity(),CHANNEL_ID)
                .setSmallIcon(R.drawable.dsclogo)
                .setContentTitle("Donation Successful")
                .setContentText("You have successfully donated to DSC fund. Happy Coding.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true);

        NotificationManagerCompat managerCompat=NotificationManagerCompat.from(getActivity());
        managerCompat.notify(1,mBuilder.build());
    }

    @Override
    public void onTransactionCompleted(TransactionDetails transactionDetails) {
        if(transactionDetails.getStatus().equals("SUCCESS")) {
            String status = transactionDetails.getStatus();
            String tID = transactionDetails.getTransactionId();
            String rID = transactionDetails.getTransactionRefId();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date date = new Date();
            String datee = String.valueOf(formatter.format(date));

            Donate model = new Donate("DSC475FOP12RECB", status,payment, tID, rID, datee,mail_Sent);

            write.child(Objects.requireNonNull(write.push().getKey())).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {
                        amount.getText().clear();
                        displayNotification();
                        Toast.makeText(getActivity(), "Transaction Receipt Saved to Donation Box", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else if(transactionDetails.getStatus().equals("SUBMITTED")){
            Toast.makeText(getActivity(), "Transaction Submitted !", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getActivity(), "Transaction Failed !", Toast.LENGTH_SHORT).show();
        }




    }

    @Override
    public void onTransactionSuccess() {
        Toast.makeText(getActivity(), "Thank you for donation. ", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onTransactionSubmitted() {
        Toast.makeText(getActivity(), "Transaction Pending ", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onTransactionFailed() {
        Toast.makeText(getActivity(), "Transaction Failed ", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onTransactionCancelled() {
        Toast.makeText(getActivity(), "Transaction Cancelled ", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onAppNotFound() {
        Toast.makeText(getActivity(), "No Payment app found.", Toast.LENGTH_SHORT).show();
    }


}

