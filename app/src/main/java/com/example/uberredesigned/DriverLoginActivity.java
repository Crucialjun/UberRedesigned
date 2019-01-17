package com.example.uberredesigned;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverLoginActivity extends AppCompatActivity {
    private EditText mDriverEmail,mDriverPassword;
    private Button mDriverLogin,mDriverRegistration;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null) {
                    Intent intent = new Intent(DriverLoginActivity.this, MapActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        mDriverEmail = findViewById(R.id.driverEmail);
        mDriverPassword = findViewById(R.id.driverPassword);

        mDriverLogin = findViewById(R.id.driverLogin);
        mDriverRegistration = findViewById(R.id.driverRegistration);

        mDriverRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mDriverEmail.getText().toString();
                final String password = mDriverPassword.getText().toString();
                mAuth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(DriverLoginActivity.this
                                , new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()) {
                            Toast.makeText(DriverLoginActivity.this
                                    , "Sign Up Error Check Email or Password"
                                    , Toast.LENGTH_SHORT)
                                    .show();
                        }else{
                            String userId = mAuth.getCurrentUser().getUid();
                            DatabaseReference currentUserDB = FirebaseDatabase
                                    .getInstance()
                                    .getReference()
                                    .child("Users")
                                    .child("Drivers")
                                    .child(userId);
                            currentUserDB.setValue(true);
                        }
                    }
                });
            }
        });

        mDriverLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mDriverEmail.getText().toString();
                final String password = mDriverPassword.getText().toString();

                mAuth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener(DriverLoginActivity.this
                                , new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(DriverLoginActivity.this
                                    , "Sign In Error Check Email or Password"
                                    , Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);

    }
}
