package com.tharuke.educhat.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tharuke.educhat.R;

public class SplashScreenActivity extends AppCompatActivity {

//    private static final String TAG = SplashScreenActivity.class.getCanonicalName();
//    String userName, password;
    //firebase auth
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    private Context context;
    Thread splashThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spash_screen);
//        final ProgressBar pbAppLoading = (ProgressBar) findViewById(R.id.pbAppLoading);
//        this.context = this;

//        pbAppLoading.postDelayed(new Runnable() {
//            public void run() {
//                pbAppLoading.setVisibility(View.VISIBLE);
//            }
//        }, 1000);
        StartSplashScreen();
    }

    private void StartSplashScreen() {

        splashThread = new Thread() {
            @Override
            public void run() {
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser firebaseUser = mAuth.getCurrentUser();

                try {
                    int waited = 0;
                    while (waited < 3500) {
                        sleep(100);
                        waited += 100;
                    }

                    if (firebaseUser != null) {
                        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                    } else {
                        startActivity(new Intent(SplashScreenActivity.this,LoginActivity.class));
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    SplashScreenActivity.this.finish();
                }
            }
        };
        splashThread.start();
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                // Initialize Firebase Auth
//                mAuth = FirebaseAuth.getInstance();
//                firebaseUser = mAuth.getCurrentUser();
//
//                if (firebaseUser != null) {
//                    Intent intent = new Intent(SplashScreenActivity.this,MainActivity.class);
//                    startActivity(intent);
//                    finish();
//                } else {
//                    Intent intent = new Intent(SplashScreenActivity.this,LoginActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
//
//            }
//        }).start();
//    }
}