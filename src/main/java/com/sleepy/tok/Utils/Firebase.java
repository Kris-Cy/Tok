package com.sleepy.tok.Utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.sleepy.tok.R;
import com.sleepy.tok.data_models.User;
import com.sleepy.tok.getting_started.SetupProfileActivity;

import androidx.annotation.NonNull;

public class Firebase {
    private static final String TAG = "Firebase";
    private Context mContext;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private String userID;
    private String append = "";

    //constructor.
    public Firebase(Context context) {
        mContext = context;
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
    }

    //Creates a new authentication user, then runs the method to add them to the database, then sends a verification email.
    public void signUpWithEmail (final String email, final String username, final String password){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener (new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(mContext, "Sign Up successful!", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            userID=user.getUid();
                            addUser(" Hey there!", email, " ", userID, username, username);
                            logInWithEmail(email,password);
                            Intent goToSetup = new Intent(mContext, SetupProfileActivity.class);
                            goToSetup.putExtra("userID", userID);
                            goToSetup.putExtra("email", email);
                            goToSetup.putExtra("password",password);
                            goToSetup.putExtra("username", username);
                            goToSetup.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            mContext.startActivity(goToSetup);
                            //sendVerificationEmail();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(mContext, "Sign Up failed.", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    //The method that logs an authenticated user in.
    public void logInWithEmail(final String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            userID=user.getUid();

                            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( new OnSuccessListener<InstanceIdResult>() {
                                @Override
                                public void onSuccess(InstanceIdResult instanceIdResult) {
                                    String deviceToken = instanceIdResult.getToken();
                                    databaseReference = FirebaseDatabase.getInstance().getReference().child("user");
                                    databaseReference.child(userID).child("device_token").setValue(deviceToken);
                                }
                            });



                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(mContext, "Failed to sign in.", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    //adds user to database.
    public void addUser (String bio, String email, String profile_photo, String user_id, String username, String screen_name){
        User user = new User (" ", email, " "," ", userID, username, " ");
        FirebaseDatabase.getInstance().getReference();

        Log.d(TAG, "addUser: adding the user to the 'user' table of the database...");
        databaseReference.child(mContext.getString(R.string.dbname_user))
                .child(userID)
                .setValue(user);
        Log.d(TAG, "addUser: successfully added (user)!");
}

    //sends the verification email.
    public void sendVerificationEmail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            Log.d(TAG, "sendVerificationEmail: sending...");
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Log.d(TAG, "onComplete: email has been sent!");
                                Toast.makeText(mContext, "Sent verification email", Toast.LENGTH_SHORT).show();

                            }
                            else {
                                Log.d(TAG, "onComplete: email has not been sent. :(");
                                Toast.makeText(mContext, "Couldn't send verification email", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}



