package com.sleepy.tok.search_activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sleepy.tok.R;
import com.sleepy.tok.data_models.Friends;
import com.sleepy.tok.data_models.User;
import com.sleepy.tok.main_activity.MainActivity;
import com.sleepy.tok.user_activity.UserActivity;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class OtherUserActivity extends AppCompatActivity {
    private static final String TAG = "OtherUserActivity";
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private StorageReference mStorageRef;
    private FirebaseDatabase database;
    private DatabaseReference friendRequestDatabase;
    private DatabaseReference friendDatabase;
    private DatabaseReference notificationsDatabase;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;
    //private DatabaseReference onlineRef;
    private String areWeFriends;


    TextView tvOtherUserScreenName, tvOtherUserUsername, tvOtherUserBio, tvNumberOfFriends, tvNumberOfMutualFriends;
    ImageView ivOtherUserProfilePhoto, ivBackArrow;
    Button btnAddFriend, btnDeclineFriend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user);
        Log.d(TAG, "onCreate: Starting Activity to view other user's profile");
        final String selectedUserID = getIntent().getStringExtra("Selected userID");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("user").child(selectedUserID);
        //onlineRef=databaseReference.child("online");
        friendRequestDatabase = database.getReference().child("friend_req");
        friendDatabase = database.getReference().child("friends");
        notificationsDatabase = database.getReference().child("notifications");
        currentUser=FirebaseAuth.getInstance().getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        ivOtherUserProfilePhoto = findViewById(R.id.ivOtherUserProfilePhoto);
        ivBackArrow = findViewById(R.id.ivBackArrow);
        tvOtherUserScreenName = findViewById(R.id.tvOtherUserScreenName);
        tvOtherUserUsername = findViewById(R.id.tvOtherUserUsername);
        tvOtherUserBio = findViewById(R.id.tvOtherUserBio);
        tvNumberOfFriends = findViewById(R.id.tvNumberOfFriends);
        tvNumberOfMutualFriends = findViewById(R.id.tvNumberOfMutualFriends);
        btnAddFriend = findViewById(R.id.btnAddFriend);
        btnDeclineFriend = findViewById(R.id.btnDeclineFriend);
        btnDeclineFriend.setVisibility(View.GONE);


        areWeFriends = "no";
        
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: checking for friendship status...");
                String bio=dataSnapshot.child("bio").getValue().toString();
                String profile_photo=dataSnapshot.child("profile_photo").getValue().toString();
                String username = dataSnapshot.child("username").getValue().toString();
                String screen_name = dataSnapshot.child("screen_name").getValue().toString();

                tvOtherUserBio.setText(bio);
                tvOtherUserScreenName.setText(screen_name);
                tvOtherUserUsername.setText(username);
                Picasso.get().load(profile_photo).placeholder(R.drawable.ic_user_grey).into(ivOtherUserProfilePhoto);

                friendRequestDatabase.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(selectedUserID)){
                            String req_type = dataSnapshot.child(selectedUserID).child("req_type").getValue().toString();

                            if(req_type.equals("received")){
                                Log.d(TAG, "onDataChange: the user being observed has asked the user currently signed in.");
                                btnAddFriend.setEnabled(true);
                                areWeFriends="I've been asked";
                                btnAddFriend.setText("Accept Friend Request");

                                btnDeclineFriend.setVisibility(View.VISIBLE);
                                btnDeclineFriend.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                });
                            }
                            else if(req_type.equals("sent")){
                                Log.d(TAG, "onDataChange: the user currently signed in has asked the user being observed.");
                                btnAddFriend.setEnabled(true);
                                areWeFriends="I've asked";
                                btnAddFriend.setText("Cancel Friend Request");

                                btnAddFriend.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                friendRequestDatabase.child(currentUser.getUid()).child(selectedUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        friendRequestDatabase.child(selectedUserID).child(currentUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                btnAddFriend.setText("Add Friend");
                                                btnAddFriend.setEnabled(true);
                                                areWeFriends="no";
                                                Toast.makeText(OtherUserActivity.this, "Request cancelled", Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    }
                                });
                            }
                        });
                            }
                        }
                        else {
                            friendDatabase.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(selectedUserID)){
                                        btnAddFriend.setEnabled(true);
                                        areWeFriends="yes";
                                        btnAddFriend.setText("Remove Friend");
                                        btnAddFriend.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                friendDatabase.child(currentUser.getUid()).child(selectedUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        friendDatabase.child(selectedUserID).child(currentUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                btnAddFriend.setEnabled(false);
                                                                areWeFriends="no";
                                                                btnAddFriend.setText("Add Friend");
                                                                Toast.makeText(OtherUserActivity.this, "You are no longer friends",Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    btnAddFriend.setEnabled(false);

                    //----------------------------This is the 'Not Friends' state---------------------------------
                if (areWeFriends.equals("no")){
                    friendRequestDatabase.child(currentUser.getUid()).child(selectedUserID).child("req_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                friendRequestDatabase.child(selectedUserID).child(currentUser.getUid()).child("req_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        btnAddFriend.setEnabled(true);
                                        areWeFriends="I've asked";
                                        btnAddFriend.setText("Cancel Friend Request");

                                        HashMap<String, String> notificationInfo = new HashMap<>();
                                        notificationInfo.put("from", currentUser.getUid());
                                        notificationInfo.put("type", "Friend Request");

                                        notificationsDatabase.child(selectedUserID).push().setValue(notificationInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                                        Toast.makeText(OtherUserActivity.this, "Request sent", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            else{
                                Toast.makeText(OtherUserActivity.this, "Failed to send Friend Request", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "onComplete: failed to send request");
                            }
                        }
                    });
                }


                //------------------------------------------To cancel friend request------------------------------------------
                if(areWeFriends.equals("I've asked")){
                    friendRequestDatabase.child(currentUser.getUid()).child(selectedUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            friendRequestDatabase.child(selectedUserID).child(currentUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    btnAddFriend.setEnabled(true);
                                    areWeFriends="no";
                                    btnAddFriend.setText("Add Friend");
                                    Toast.makeText(OtherUserActivity.this, "Request cancelled", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    });
                }

                //-----------------------------------Request received--------------------------------------------------------

                if(areWeFriends.equals("I've been asked")){

                    final String friendsDate = DateFormat.getDateInstance().format(new Date());
                    friendDatabase.child(currentUser.getUid()).child(selectedUserID).child("friends_since").setValue(friendsDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            friendDatabase.child(selectedUserID).child(currentUser.getUid()).child("friends_since").setValue(friendsDate).addOnCompleteListener(new OnCompleteListener<Void>() {

                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    friendRequestDatabase.child(currentUser.getUid()).child(selectedUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            friendRequestDatabase.child(selectedUserID).child(currentUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    String bio = tvOtherUserBio.getText().toString();
                                                    String screenName = tvOtherUserScreenName.getText().toString().trim();
                                                    String selectedUserThumbnail = getIntent().getStringExtra("Selected user thumbnail");

                                                    addFriend(bio, selectedUserThumbnail, selectedUserID, screenName);

                                                    btnAddFriend.setEnabled(true);
                                                    areWeFriends="yes";
                                                    btnAddFriend.setText("Remove Friend");
                                                    Toast.makeText(OtherUserActivity.this, "You are now friends!", Toast.LENGTH_SHORT).show();
                                                    btnDeclineFriend.setVisibility(View.GONE);

                                                    btnAddFriend.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            friendDatabase.child(currentUser.getUid()).child(selectedUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    friendDatabase.child(selectedUserID).child(currentUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            btnAddFriend.setEnabled(false);
                                                                            areWeFriends="no";

                                                                            Toast.makeText(OtherUserActivity.this, "You are no longer friends",Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }
                                                    });

                                                }
                                            });
                                        }
                                    });
                                }
                            });
                    }
                });

            }
        }
        });



        setIvBackArrow();
    }

    private void setIvBackArrow(){
        ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Back Button was clicked. going back to Main Activity.");
                    Intent backToMain = new Intent (OtherUserActivity.this, MainActivity.class);
                    backToMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(backToMain);

            }
        });
    }

    public void addFriend (String bio, String profile_photo_thumbnail, String user_id, String screen_name){
        Friends friends = new Friends(tvOtherUserBio.getText().toString(), getIntent().getStringExtra("Selected user thumbnail"), getIntent().getStringExtra("Selected userID"), tvOtherUserScreenName.getText().toString().trim());
        friendDatabase.child(currentUser.getUid()).child(getIntent().getStringExtra("Selected userID")).setValue(friends);
        friendDatabase.child(getIntent().getStringExtra("Selected userID")).child(currentUser.getUid()).setValue(friends);
    }

  /*  @Override
    protected void onStart() {
        super.onStart();

        onlineRef.setValue(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        onlineRef.setValue(false);
    }*/
}
