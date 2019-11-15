package com.sleepy.tok.getting_started;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.sleepy.tok.R;

public class SignUpOrLogIn extends AppCompatActivity {
private static final String TAG = "SignUpOrLogIn";
Context context = SignUpOrLogIn.this;
Button btnSignUp, btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_or_log_in);

        btnSignUp=findViewById(R.id.btnSignUp);
        btnLogin=findViewById(R.id.btnLogin);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Attempting to navigate to SignUpActivity...");
                Intent goToSignup = new Intent(context, SignUpActivity.class);
                startActivity(goToSignup);
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: attemting to navigate to LoginActivity...");
                Intent goToLogin = new Intent(context, LoginActivity.class);
                startActivity(goToLogin);
                finish();
            }
        });
    }
}
