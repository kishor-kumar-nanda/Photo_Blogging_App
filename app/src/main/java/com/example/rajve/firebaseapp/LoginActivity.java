package com.example.rajve.firebaseapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText loginPasswordText,loginEmailText;
    private FirebaseAuth mAuth;
    private ProgressBar loginProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginEmailText = (EditText) findViewById(R.id.loginEmail);
        loginPasswordText=(EditText) findViewById(R.id.loginPassword);
        Button loginButton = (Button) findViewById(R.id.loginButton);
        Button loginRegistration = (Button) findViewById(R.id.loginRegistration);
        loginProgress=(ProgressBar) findViewById(R.id.loginProgress);
        mAuth=FirebaseAuth.getInstance();

        //this will be invoked when user wants to register and he wil be send to the register activity
        loginRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoRegActivity=new Intent(LoginActivity.this,Activity_Register.class);
                startActivity(gotoRegActivity);
            }
        });

        //when the user enters the id and password and click on login then this will be invoked
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Take the entered string and convert them to String
                String loginEmail=loginEmailText.getText().toString();
                String loginPassword=loginPasswordText.getText().toString();

                //this check that if the email and password are not empty then it will show the progress bar else he will not be allowed
                if (!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPassword)){
                    //making the progressbar visible when the user press the login button and after checking the email & password
                    loginProgress.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(loginEmail,loginPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //this task if for the results and checks for the results
                            if (task.isSuccessful()){
                                //when the task is successful this will send it to main activity
                                sendToMainActivity();
                            }
                            //else the error message is shown
                            else{
                                String errorMessage=task.getException().getMessage();
                                Toast.makeText(LoginActivity.this,"Error is "+errorMessage,Toast.LENGTH_LONG).show();
                            }
                            //when if parts executes the it will be send back to main activity with the progress bar but when it is not successful then toast is show and progressbar is made invisible
                            loginProgress.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser= mAuth.getCurrentUser();
        //if the user is logged in then we shall send him to the main activity
        if (currentUser!=null){
            sendToMainActivity();
        }
    }

    private void sendToMainActivity() {
        Intent mainIntent=new Intent(LoginActivity.this,MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
