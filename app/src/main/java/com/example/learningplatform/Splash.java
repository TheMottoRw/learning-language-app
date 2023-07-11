package com.example.learningplatform;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.learn.R;

public class Splash extends AppCompatActivity {

    int SPLASH_DISPLAY_LENGTH = 0;
    private Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //check if user does not exist then register
        SPLASH_DISPLAY_LENGTH = 3000;

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent;
                Log.d("UserInfo", Utils.getUser(Splash.this, "id"));
                if (Utils.getUser(Splash.this, "id").equals("")) {
                    mainIntent = new Intent(Splash.this, Login.class);
                } else {
                    if(Utils.getUser(Splash.this,"user_type").equals("Admin"))
                        mainIntent = new Intent(Splash.this, AdminActivity.class);
                    else
                        mainIntent = new Intent(Splash.this,ModulesActivity.class);
                }
                startActivity(mainIntent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

}
