package com.sleepy.tok.getting_started;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sleepy.tok.R;
import com.sleepy.tok.main_activity.MainActivity;

public class SplashScreen extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Context context;
    private static final String TAG = "SplashScreen";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_splash);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        context = SplashScreen.this;
        }

        @Override
        public void onStart() {
            super.onStart();
            // Check if a user is signed in (non-null) and, if one is, head to the main activities of the app.
            FirebaseUser currentUser = mAuth.getCurrentUser();

            if (currentUser!=null)
            {
                Log.d(TAG, "onStart: This user is signed in: "+ currentUser.getUid());
                Intent goToMain =new Intent(context, MainActivity.class);
                startActivity(goToMain);
                finish();
            }

            //if one isn't, go to the activity that asks you to sign up or log in.
            else
            {
                Intent goToSignUpOrLogin = new Intent(context, SignUpOrLogIn.class);
                startActivity (goToSignUpOrLogin);
                finish();
            }
        }
}
