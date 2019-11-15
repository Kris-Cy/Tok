package com.sleepy.tok.user_activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sleepy.tok.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ScreenNameFragment extends Fragment {
    private static final String TAG = "ScreenNameDialog";
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    EditText etScreenDialogLine;
    Button btnCancel, btnSave;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen_name, container, false);

        etScreenDialogLine = view.findViewById(R.id.etScreenDialogLine);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnSave = view.findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = mAuth.getCurrentUser();
                String userID = user.getUid();
                databaseReference = FirebaseDatabase.getInstance().getReference().child("user").child(userID);

                String screen_name = etScreenDialogLine.getText().toString().trim();
                databaseReference.child("screen_name").setValue(screen_name).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Intent reloadUser = new Intent(getActivity(), UserActivity.class);
                            reloadUser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(reloadUser);
                        }

                        else {
                            Toast.makeText(getActivity(), "Sorry, couldn't change that. Try again!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reloadUser = new Intent(getActivity(), UserActivity.class);
                reloadUser.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(reloadUser);
            }
        });

        return view;
    }
}


