package com.example.uberredesigned;

import android.content.Intent;
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

public class CustomerLoginActivity extends AppCompatActivity {

    private EditText mCustomerEmail, mCustomerPassword;
    private Button mCustomerLogin, mCustomerRegistration;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(CustomerLoginActivity.this, MapActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        mCustomerEmail = findViewById(R.id.customerEmail);
        mCustomerPassword = findViewById(R.id.customerPassword);

        mCustomerLogin = findViewById(R.id.customerLogin);
        mCustomerRegistration = findViewById(R.id.customerRegistration);

        mCustomerRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mCustomerEmail.getText().toString();
                final String password = mCustomerPassword.getText().toString();
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(CustomerLoginActivity.this
                                , new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(CustomerLoginActivity.this
                                                    , "Sign Up Error Check Email or Password"
                                                    , Toast.LENGTH_SHORT)
                                                    .show();
                                        } else {
                                            String userId = mAuth.getCurrentUser().getUid();
                                            DatabaseReference currentUserDB = FirebaseDatabase
                                                    .getInstance()
                                                    .getReference()
                                                    .child("Users")
                                                    .child("Customers")
                                                    .child(userId);
                                            currentUserDB.setValue(true);
                                        }
                                    }
                                });
            }
        });

        mCustomerLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mCustomerEmail.getText().toString();
                final String password = mCustomerPassword.getText().toString();

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(CustomerLoginActivity.this
                                , new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(CustomerLoginActivity.this
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
