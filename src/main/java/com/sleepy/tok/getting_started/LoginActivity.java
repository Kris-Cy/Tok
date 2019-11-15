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
import com.google.firebase.auth.FirebaseUser;
import com.sleepy.tok.R;
import com.sleepy.tok.Utils.Firebase;
import com.sleepy.tok.main_activity.MainActivity;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;
    private Context context;
    private Firebase firebase;
EditText etLoginEmail, etLoginPassword;
Button btnLoginLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate: Starting Login Activity");
        mAuth=FirebaseAuth.getInstance();
        context = LoginActivity.this;
        firebase=new Firebase(context);

        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLoginLogin = findViewById(R.id.btnLoginLogin);



        btnLoginLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = mAuth.getCurrentUser();
                String email= etLoginEmail.getText().toString().trim(),
                       password = etLoginPassword.getText().toString().trim();

               if (etLoginEmail.getText().toString().equals("")|| etLoginPassword.getText().toString().equals("")) {
                   Log.d(TAG, "onClick: NullPointerException. Something is blank.");
                   Toast.makeText(context, "Please enter all fields", Toast.LENGTH_SHORT).show();
               }
               else{
                   Log.d(TAG, "nothing is blank.");
                   firebase.logInWithEmail(email, password);
                   Intent goToMain = new Intent(context, MainActivity.class);
                   goToMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                   startActivity(goToMain);
               }
            }
        });

    }
}
