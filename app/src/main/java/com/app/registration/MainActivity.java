package com.app.registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class MainActivity extends AppCompatActivity {

    Button signUpBtn;
    EditText emailField, passwordField, nameField, mobileNumberField;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference usersRef = database.getReference("Users");
    private FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent homeIntent = new Intent(MainActivity.this, Home.class);
            homeIntent.putExtra("userId", currentUser.getUid());
            startActivity(homeIntent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        signUpBtn = findViewById(R.id.sBtn);
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        nameField = findViewById(R.id.name);
        mobileNumberField = findViewById(R.id.mobileNumber);
    }

    public void createUser(View view){
        final String email = emailField.getText().toString();
        final String password = passwordField.getText().toString();
        final String name = nameField.getText().toString();
        final String mobileNumber = mobileNumberField.getText().toString();


        //VALIDATING FIELDS
        if(email.matches("") || password.matches("") || name.matches("") || mobileNumber.matches("")){
            Toast.makeText(MainActivity.this, "Enter both email and password",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //CREATING USER
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            //STORING USERDETAILS TO DATABASE
                            usersRef.child(user.getUid()).child("name").setValue(name);
                            usersRef.child(user.getUid()).child("MobileNumber").setValue(mobileNumber);
                            usersRef.child(user.getUid()).child("Email").setValue(email);
                            Toast.makeText(MainActivity.this, "Created User succesfully", Toast.LENGTH_SHORT).show();

                            Intent homeIntent = new Intent(MainActivity.this, Home.class);
                            homeIntent.putExtra("userId", user.getUid());
                            startActivity(homeIntent);
                        } else {
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void goToLogin(View view) {
        Intent i = new Intent(MainActivity.this, Login.class);
        startActivity(i);
    }
}