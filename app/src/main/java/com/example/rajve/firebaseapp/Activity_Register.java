package com.example.rajve.firebaseapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

public class Activity_Register extends AppCompatActivity {

    private EditText reg_email_field;
    private EditText reg_pass_field;
    private EditText reg_confirm_pass_field;
    private Button reg_btn;
    private Button reg_login_btn;
    private ProgressBar reg_progress;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth=FirebaseAuth.getInstance();


        setContentView(R.layout.activity__register);

        reg_email_field=(EditText)findViewById(R.id.reg_Email);
        reg_pass_field=(EditText)findViewById(R.id.reg_pass);
        reg_confirm_pass_field=(EditText)findViewById(R.id.reg_confirm_pass);
        reg_btn=(Button) findViewById(R.id.reg_btn);
        reg_login_btn=(Button) findViewById(R.id.reg_login_btn);
        reg_progress=(ProgressBar) findViewById(R.id.reg_progress);

        //when we press the create an account this function will work
        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //takes the email and password from edittext and convert them into string
                String email=reg_email_field.getText().toString();
                String pass=reg_pass_field.getText().toString();
                String confirm_pass=reg_confirm_pass_field.getText().toString();

                //if all the fields are not empty i.e they are filled then we shall proceed
                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(confirm_pass)){
                    //now we shall check is both the password and confirm password are equal or not, then only we can proceed
                    if (pass.equals(confirm_pass)){

                        //we shall show the progressbar
                        reg_progress.setVisibility(View.VISIBLE);

                        //create and account and add the fields in the firebase console auth
                        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    sendToMain();
                                }
                                else {
                                    String errorMessage=task.getException().getMessage();
                                    Toast.makeText(Activity_Register.this,"Error: "+errorMessage,Toast.LENGTH_LONG).show();
                                }
                                //and if it doesn't work then we shall not show the progressbar so we make it INVISIBLE
                                reg_progress.setVisibility(View.INVISIBLE);
                            }

                        });
                    }
                    else{
                        Toast.makeText(Activity_Register.this,"Please Ensure that Password and Confirm Password matches",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        reg_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //this will just destroy the current intent and send him to the login page
                finish();
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        //creating the instance for currentuser
        FirebaseUser currentUser=mAuth.getCurrentUser();
        //this is invoked when the user opens the app and checks is the user is already logged in or not
        //checking is the user is logged in then we send him to the main activity
        if(currentUser!=null){
            sendToMain();
        }
    }

    //function for to send from ActivityRegister to MainActivity and we finish so that user can't come back if he press the back button
    private void sendToMain() {
        Intent mainIntent=new Intent(Activity_Register.this,MainActivity.class);
        startActivity(mainIntent);
        finish();
    }


}
