package com.example.android.loginfirebase;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private String emailId;

    private Button sendPasswordResetEmail, signOut;
    private TextView nameTextView, emailTextView, genderTextView, dobTextView, bloodGroupTextView,
            mobileNumberTextView, addressTextView;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        databaseReference = firebaseDatabase.getReference("users");

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            emailId = user.getEmail();
        }

        progressBar = findViewById(R.id.profile_progressBar);
        nameTextView = findViewById(R.id.profile_name_textView);
        emailTextView = findViewById(R.id.profile_email_textView);
        genderTextView = findViewById(R.id.profile_gender_textView);
        dobTextView = findViewById(R.id.profile_dob_textView);
        bloodGroupTextView = findViewById(R.id.profile_blood_group_textView);
        mobileNumberTextView = findViewById(R.id.profle_mobile_number_textView);
        addressTextView = findViewById(R.id.profile_address_textView);
        sendPasswordResetEmail = findViewById(R.id.send_password_reset_email);
        signOut = findViewById(R.id.sign_out);

        displayUserInformation();

        sendPasswordResetEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPasswordResetEmail();
            }
        });
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    private void displayUserInformation() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            databaseReference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserInformation userInformation = dataSnapshot.getValue(UserInformation.class);

                    // Check for null
                    if (userInformation == null) {
                        Log.e(TAG, "User data is null!");
                    } else {
                        // Display user information
                        nameTextView.setText(userInformation.name);
                        emailTextView.setText(userInformation.email);
                        genderTextView.setText(userInformation.gender);
                        dobTextView.setText(userInformation.dob);
                        bloodGroupTextView.setText(userInformation.bloodGroup);
                        mobileNumberTextView.setText(userInformation.mobile);
                        addressTextView.setText(userInformation.address);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Failed to read value
                    Log.e(TAG, "Failed to read user", databaseError.toException());
                }
            });
        } else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }

    // Send password reset email
    public void sendPasswordResetEmail() {
        progressBar.setVisibility(View.VISIBLE);
        if (!emailId.equals("")) {
            auth.sendPasswordResetEmail(emailId)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Reset password email is sent!", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(MainActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        }
    }

    //sign out method
    public void signOut() {
        auth.signOut();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
}
