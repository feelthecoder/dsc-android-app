package com.example.dsc.AppUI;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dsc.R;
import com.example.dsc.SettingsActivity.EditProfileActivity;
import com.example.dsc.SettingsActivity.ViewProfileActivity;
import com.example.dsc.login;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import static android.view.View.INVISIBLE;

public class SUserFragment extends Fragment {
    String password;
    String oldpass;
    ProgressBar progressBar;
    AuthCredential credential;
    FirebaseUser user;
    String providerId;

    String[] list_user={"View Profile","Edit Profile","Forgot Password","Change Password","Verify Account","Delete Account"};
    private String email;
    ListView listView = null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_s_user, container, false);

        ArrayAdapter adapter = new ArrayAdapter<String>(requireContext(),R.layout.list_details, list_user);
        listView = view.findViewById(R.id.user_list);
        progressBar=view.findViewById(R.id.prog_user_set);
        listView.setAdapter(adapter);



        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        user = FirebaseAuth.getInstance().getCurrentUser();
        email= Objects.requireNonNull(user).getEmail();

        for (UserInfo profile : user.getProviderData()) {
            providerId = profile.getProviderId();
        }


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent intent = new Intent(getActivity(), ViewProfileActivity.class);
                    startActivity(intent);
                } else if (position == 1) {
                    Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                    startActivity(intent);
                } else if (position == 2) {
                    Toast.makeText(getActivity(), providerId, Toast.LENGTH_SHORT).show();
                    if(providerId.equals("password"))
                            forgotScreen();
                    else
                        Toast.makeText(getActivity(), "You are logged in with phone/otp or google", Toast.LENGTH_SHORT).show();
                } else if (position == 3) {
                    if(providerId.equals("password"))
                        changePassword();
                    else
                        Toast.makeText(getActivity(), "You are logged in with phone/otp or google", Toast.LENGTH_SHORT).show();

            }
                else
                if(position==4) {
                    if(providerId.equals("password"))
                        verifyEmail();
                    else
                        Toast.makeText(getActivity(), "You are logged in with phone/otp or google", Toast.LENGTH_SHORT).show();
                }
                else
                if(position==5){
                  deleteAccount();
                }
            }
        });

        return view;
    }

    private void verifyEmail() {

        if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
            Toast.makeText(getActivity(), "Your Email is already verified.", Toast.LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setTitle("Verify Email");
            alertDialog.setMessage("Enter Your Email");
            final EditText input = new EditText(getActivity());
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            alertDialog.setView(input);
            alertDialog.setPositiveButton("Confirm",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            email = input.getText().toString();
                            if (email.isEmpty()) {
                                input.setError("Email is required");
                                input.requestFocus();
                                return;
                            }
                            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                input.setError("Invalid Email");
                                input.requestFocus();
                                return;
                            }
                            progressBar.setVisibility(View.VISIBLE);
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            assert user != null;
                            user.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressBar.setVisibility(INVISIBLE);
                                            if (task.isSuccessful()) {

                                                Toast.makeText(getActivity(), "Verification Email Sent.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getActivity(), "Error : Unable to verify, try again after some time", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        }
                    });
            alertDialog.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            alertDialog.show();

        }
    }

    private void changePassword() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Change Password");
        alertDialog.setMessage("Enter passwords ");
        alertDialog.setIcon(R.drawable.forgot);
        final EditText input = new EditText(getActivity());
        input.setHint("Old Password");
        final EditText input_old = new EditText(getActivity());
        input_old.setHint("New Password");
        input_old.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(input);
        layout.addView(input_old);
        alertDialog.setView(layout);

        alertDialog.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        password = input.getText().toString();
                        oldpass = input_old.getText().toString();
                        if (password.isEmpty()) {
                            input.setError("Password is required");
                            input.requestFocus();
                            return;
                        }
                        if (password.length() < 7) {
                            input.setError("Password length should be greater than 7");
                            input.requestFocus();
                            return;
                        }
                        if (oldpass.isEmpty()) {
                            input.setError("Previous password is required");
                            input.requestFocus();
                            return;
                        }
                        if (password.equals(oldpass)) {
                            input.setError("It can not be same as old paswword.");
                            input.requestFocus();
                            return;
                        }

                        progressBar.setVisibility(View.VISIBLE);

                        AuthCredential credential = EmailAuthProvider.getCredential(email, oldpass);

                        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressBar.setVisibility(INVISIBLE);
                                if (task.isSuccessful()) {
                                    user.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (!task.isSuccessful()) {
                                                Snackbar snackbar_fail = Snackbar
                                                        .make(listView, "Something went wrong. Please try again later", Snackbar.LENGTH_LONG);
                                                snackbar_fail.show();
                                            } else {
                                                Snackbar snackbar_su = Snackbar
                                                        .make(listView, "Password Successfully Changed.", Snackbar.LENGTH_LONG);
                                                snackbar_su.show();
                                            }
                                        }
                                    });
                                } else {
                                    Snackbar snackbar_su = Snackbar
                                            .make(listView, "Authentication Failed!", Snackbar.LENGTH_LONG);
                                    snackbar_su.show();
                                }
                            }
                        });
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    private void deleteAccount() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        if(providerId.equals("google.com")){

            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

            alert.setTitle("Delete Account");

            alert.setMessage("Are you sure to delete your account ? You will lose your personal data on our servers.");
            alert.setIcon(R.drawable.sharp_account_circle_black_18dp);



            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getContext());
                    if (acct != null) {
                        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
                        progressBar.setVisibility(View.VISIBLE);
                        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    DatabaseReference db=FirebaseDatabase.getInstance().getReference("https://developer-student-club-22121.firebaseio.com/");
                                    db.child("Users").child(user.getUid()).removeValue();
                                    db.child("Achievment").child(user.getUid()).removeValue();
                                    db.child("UserFormHistory").child(user.getUid()).removeValue();
                                    progressBar.setVisibility(INVISIBLE);
                                    Toast.makeText(getActivity(), "Your account is deleted!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getActivity(), login.class));
                                    getActivity().finish();

                                }
                                else
                                {
                                    Toast.makeText(getActivity(), "Unable to delete account", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(INVISIBLE);
                                }
                            }
                        });
                    }

                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                }
            });

            alert.show();


        }


        if(providerId.equals("password")){

            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

            alert.setTitle("Delete Account");

            alert.setMessage("Are you sure to delete your account ? You will lose your personal data on our servers.");
            alert.setIcon(R.drawable.sharp_account_circle_black_18dp);
            final EditText input = new EditText(getActivity());
            input.setHint("Enter Email");
            final EditText input_old = new EditText(getActivity());
            input_old.setHint("Enter Password");
            input_old.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.addView(input);
            layout.addView(input_old);
            alert.setView(layout);



            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    email = input.getText().toString();
                    password= input_old.getText().toString();
                    if (email.isEmpty()) {
                        input.setError("Email is required");
                        input.requestFocus();
                        return;
                    }
                    if (password.length() < 7) {
                        input_old.setError("Password length should be greater than 7");
                        input_old.requestFocus();
                        return;
                    }
                    if (password.isEmpty()) {
                        input_old.setError("Password is required");
                        input_old.requestFocus();
                        return;
                    }

                        credential = EmailAuthProvider
                                .getCredential(email, password);

                    progressBar.setVisibility(View.VISIBLE);


                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    user.delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        DatabaseReference db=FirebaseDatabase.getInstance().getReference("https://developer-student-club-22121.firebaseio.com/");
                                                        db.child("Users").child(user.getUid()).removeValue();
                                                        db.child("Achievment").child(user.getUid()).removeValue();
                                                        db.child("UserFormHistory").child(user.getUid()).removeValue();
                                                        progressBar.setVisibility(INVISIBLE);
                                                        Toast.makeText(getActivity(), "Your account is deleted!", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(getActivity(), login.class));
                                                        getActivity().finish();
                                                    }
                                                }
                                            });

                                }
                            });
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                }
            });

            alert.show();

        }


        if (providerId.equals("phone")){

            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

            alert.setTitle("Delete Account");

            alert.setMessage("Are you sure to delete your account ? You will lose your personal data on our servers.");
            alert.setIcon(R.drawable.sharp_account_circle_black_18dp);




            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    progressBar.setVisibility(View.VISIBLE);

                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        DatabaseReference db=FirebaseDatabase.getInstance().getReference("https://developer-student-club-22121.firebaseio.com/");
                                        db.child("Users").child(user.getUid()).removeValue();
                                        db.child("Achievment").child(user.getUid()).removeValue();
                                        db.child("UserFormHistory").child(user.getUid()).removeValue();
                                        progressBar.setVisibility(INVISIBLE);
                                        Toast.makeText(getActivity(), "Your account is deleted!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getActivity(), login.class));
                                        getActivity().finish();
                                    }
                                }
                            });

                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                }
            });

            alert.show();


        }





    }

    private void forgotScreen() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Forgot Password");
        alertDialog.setMessage("Enter Your Email");
        alertDialog.setIcon(R.drawable.forgot);
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        email = input.getText().toString();
                        if (email.isEmpty()) {
                            input.setError("Email is required");
                            input.requestFocus();
                            return;
                        }
                        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            input.setError("Invalid Email");
                            input.requestFocus();
                            return;
                        }
                        progressBar.setVisibility(View.VISIBLE);
                        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), "Entered Email does not matches with our records, error.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }
}

