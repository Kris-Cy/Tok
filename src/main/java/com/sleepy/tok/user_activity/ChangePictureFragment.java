package com.sleepy.tok.user_activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.sleepy.tok.R;
import com.sleepy.tok.getting_started.LoginActivity;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ChangePictureFragment extends Fragment {
    private static final String TAG = "ChangePictureFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "the change picture option was selected.");
        View view = inflater.inflate(R.layout.activity_user, container, false);

        Intent goToActivity = new Intent(getActivity(), ChangePictureActivity.class);
        goToActivity.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(goToActivity);

        return view;
    }
}
