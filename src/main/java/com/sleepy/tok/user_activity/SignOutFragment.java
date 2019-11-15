package com.sleepy.tok.user_activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.sleepy.tok.R;
import com.sleepy.tok.getting_started.LoginActivity;
import com.sleepy.tok.getting_started.SignUpOrLogIn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SignOutFragment extends Fragment {
    private static final String TAG = "SignOutFragment";
    private FirebaseAuth mAuth;
    Button btnSignOut;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "Signing out...");
        View view = inflater.inflate(R.layout.fragment_sign_out, container, false);

        btnSignOut = view.findViewById(R.id.btnSignOut);
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth = FirebaseAuth.getInstance();
                Intent kickToSignUpLogin = new Intent (getActivity(), SignUpOrLogIn.class);

                mAuth.signOut();
                kickToSignUpLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(kickToSignUpLogin);
                getActivity().finish();
            }
        });



        return view;
    }
}
