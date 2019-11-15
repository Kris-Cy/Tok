package com.sleepy.tok.search_activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.sleepy.tok.R;
import com.sleepy.tok.Utils.Tok;
import com.sleepy.tok.data_models.User;
import com.sleepy.tok.main_activity.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AllUsersActivity extends AppCompatActivity {
    private static final String TAG = "AllUsersActivity";
    private DatabaseReference databaseReference;
    private DatabaseReference onlineRef;
ImageView ivBackArrow;

RecyclerView rvAllUsersList;
private ArrayList<User> AllUsersArrayList;
private FirebaseRecyclerOptions<User> options;
private FirebaseRecyclerAdapter<User, UsersViewHolder> usersAdapter;
private Tok tok;


    @Override
    protected void onStart() {
        super.onStart();
        usersAdapter.startListening();
       // onlineRef.setValue(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        usersAdapter.stopListening();
        //onlineRef.setValue(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        Log.d(TAG, "onCreate: Firing up the all users activity!");
        tok = new Tok();
        ivBackArrow = findViewById(R.id.ivBackArrow);
        ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backButton();
            }
        });
        databaseReference = FirebaseDatabase.getInstance().getReference().child("user");
        // onlineRef = databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online");
        ivBackArrow = findViewById(R.id.ivBackArrow);
        rvAllUsersList = findViewById(R.id.rvAllUsersList);

        //if (tok.connectedToInternet()) {
            AllUsersArrayList = new ArrayList<User>();
            rvAllUsersList.setHasFixedSize(true);
            rvAllUsersList.setLayoutManager(new LinearLayoutManager(this));
            options = new FirebaseRecyclerOptions.Builder<User>().setQuery(databaseReference, User.class).build();


            usersAdapter = new FirebaseRecyclerAdapter<User, UsersViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull UsersViewHolder usersViewHolder, final int position, @NonNull final User user) {
                    usersViewHolder.tvOtherUserScreenName.setText(user.getScreen_name());
                    usersViewHolder.tvOtherUserBio.setText(user.getBio());
                    Picasso.get().load(user.getProfile_photo_thumbnail()).placeholder(R.drawable.ic_user_grey).into(usersViewHolder.ivOtherUserThumbnail);

                    final String userID = getRef(position).getKey();

                    usersViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent goToOtherUser = new Intent(AllUsersActivity.this, OtherUserActivity.class);
                            goToOtherUser.putExtra("Selected userID", userID);
                            goToOtherUser.putExtra("Selected user thumbnail", user.getProfile_photo_thumbnail());
                            startActivity(goToOtherUser);
                        }
                    });

                }

                @NonNull
                @Override
                public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    return new UsersViewHolder((LayoutInflater.from(AllUsersActivity.this).inflate(R.layout.layout_users_single, parent, false)));
                }
            };

            rvAllUsersList.setAdapter(usersAdapter);

       // }

       /* else{
            Toast.makeText(this, "Couldn't load data",Toast.LENGTH_SHORT).show();
        }*/
    }


    public void backButton() {
        Intent backToMain = new Intent(this, MainActivity.class);
        backToMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(backToMain);
    }
}



