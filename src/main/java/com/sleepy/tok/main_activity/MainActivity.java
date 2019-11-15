package com.sleepy.tok.main_activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sleepy.tok.R;
import com.sleepy.tok.Utils.SectionsPagerAdapter;
import com.sleepy.tok.Utils.UniversalImageLoader;
import com.sleepy.tok.getting_started.LoginActivity;
import com.sleepy.tok.search_activity.AllUsersActivity;
import com.sleepy.tok.user_activity.UserActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseUser User;
    ImageView ivUserButton;
    ImageView ivSearch;
    private DatabaseReference onlineRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Starting up main activity");
        context = MainActivity.this;
        ivSearch=findViewById(R.id.ivSearchButton);
        FirebaseApp.initializeApp(this);
        mAuth= FirebaseAuth.getInstance();
        User=mAuth.getCurrentUser();
//        onlineRef = FirebaseDatabase.getInstance().getReference().child("user").child(User.getUid()).child("online");

        setViewPager();
        waitForOnline();
        userButtonPressed();
        initImageLoader();
        setIvSearch();

    }

    private void setViewPager(){
        ViewPager viewPager=findViewById(R.id.MainViewPager);

        //declare a SectionsPagerAdapter like this (the getSupportFragmentManager is important)
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());

       // adapter.addingFragment(new RequestsFragment());
        adapter.addingFragment(new ChatsFragment());
        adapter.addingFragment(new FriendsFragment());
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        //tabLayout.getTabAt(0).setText("Requests");
        tabLayout.getTabAt(0).setText("Chats");
        tabLayout.getTabAt(1).setText("Friends");
    }

    private void waitForOnline(){
        if(User!=null){
            Log.d(TAG, "onCreate: A user is signed in");
        }
        else{
           /* Log.d(TAG, "onCreate: A user is not signed in. Going back to login screen.");
            Intent kickToLogin = new Intent(context, LoginActivity.class);
            context.startActivity(kickToLogin);*/
        }

    }

    private void userButtonPressed(){
        ivUserButton = findViewById(R.id.ivUserButton);
        ivUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToUser = new Intent(context, UserActivity.class);
                startActivity(goToUser);
            }
        });
    }

    //Universal Image Loader has to be initialized somewhere before it can be used; why not here?
    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(context);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());

    }

    private void setIvSearch(){
        ivSearch = findViewById(R.id.ivSearchButton);
        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToAllUsers = new Intent(MainActivity.this, AllUsersActivity.class);
                startActivity(goToAllUsers);
            }
        });
    }
/*
    @Override
    protected void onStart() {
        super.onStart();

        onlineRef.setValue(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        onlineRef.setValue(false);
    }
    */
}
