package com.example.rajve.firebaseapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    //initialize toolbar
    private Toolbar mainToolBar;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();

        mainToolBar=(Toolbar) findViewById(R.id.main_Toolbar);
        setSupportActionBar(mainToolBar);
        //setting the titlebar Text
        getSupportActionBar().setTitle("Photo Blog");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //when the user opens up the app then onStart is called and it checks if the user is logged in or not
        //if not logged in then it executes this and sends him to the login activity
        FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser==null){
            sendToLogin();
        }
    }


    //inflate the main menu by this method and return true
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_logout_btn:
                logout();
                return true;
            case R.id.action_setting_btn:
                Intent settingsIntent=new Intent(MainActivity.this,SetupActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return false;
        }
    }


    private void logout() {
        mAuth.signOut();
        sendToLogin();

    }
    private void sendToLogin() {
        Intent loginIntent=new Intent(MainActivity.this,LoginActivity.class);
        startActivity(loginIntent);
        finish();
        //this finish function ensures that the user cannot come by pressing back button
        //as the intent will be removed
    }

}
