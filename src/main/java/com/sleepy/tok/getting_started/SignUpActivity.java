package com.sleepy.tok.getting_started;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.sleepy.tok.R;
import com.sleepy.tok.Utils.Firebase;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";
    private Firebase firebase;
    private FirebaseAuth mAuth;
    private Context context;
            EditText etEmail, etUsername, etPassword, etConfirmPassword;
            Button btnSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Log.d(TAG, "onCreate: Sign Up Activity Start");
        context=SignUpActivity.this;
        firebase = new Firebase(context);
        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etUsername=findViewById(R.id.etUsername);
        etPassword=findViewById(R.id.etPassword);
        etConfirmPassword=findViewById(R.id.etConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString().trim(),
                        username = etUsername.getText().toString().trim(),
                        password = etPassword.getText().toString().trim(),
                        confirmPassword = etConfirmPassword.getText().toString().trim();

                if (areFieldsEmpty()) {
                    Toast.makeText(context, "Please enter all fields", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (password.equals(confirmPassword)) {
                        firebase.signUpWithEmail(email, username, password);
                    } else {
                        Toast.makeText(context, "Passwords must match", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }

    private boolean areFieldsEmpty(){
        String email = etEmail.getText().toString().trim(),
                username = etUsername.getText().toString().trim(),
                password = etPassword.getText().toString().trim(),
                confirmPassword = etConfirmPassword.getText().toString().trim();

        if(email.equals("") || username.equals("") || password.equals("") || confirmPassword.equals("")) {
            return true;
        }
        return false;
    }
}
