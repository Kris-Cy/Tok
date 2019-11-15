package com.sleepy.tok.main_activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sleepy.tok.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class RequestsFragment extends Fragment {
    private static final String TAG = "RequestsFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: the Requests fragment.");
        View view = inflater.inflate(R.layout.fragment_requests, container, false);
        return view;
    }
}
