package com.sleepy.tok.main_activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sleepy.tok.R;
import com.sleepy.tok.Utils.Tok;
import com.sleepy.tok.other_single_page_activities.ChatActivity;
import com.sleepy.tok.data_models.User;
import com.sleepy.tok.search_activity.OtherUserActivity;
import com.sleepy.tok.search_activity.UsersViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FriendsFragment extends Fragment {
    private static final String TAG = "FriendsFragment";
    private DatabaseReference databaseReference;
    private Tok tok;
    private DatabaseReference userDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String userID;
    private RecyclerView rvAllFriendsList;
    private ArrayList<User> allFriendsArrayList;
    private FirebaseRecyclerOptions options;
    private FirebaseRecyclerAdapter<User, UsersViewHolder> friendsAdapter;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: the Friends fragment.");
        final View view = inflater.inflate(R.layout.fragment_friends, container, false);
        tok = new Tok();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userID = currentUser.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("friends").child(userID);
        databaseReference.keepSynced(true);
        userDatabase = FirebaseDatabase.getInstance().getReference().child("user");
        userDatabase.keepSynced(true);
        rvAllFriendsList = view.findViewById(R.id.rvAllFriendsList);


            allFriendsArrayList = new ArrayList<User>();
            rvAllFriendsList.setHasFixedSize(true);
            rvAllFriendsList.setLayoutManager(new LinearLayoutManager(view.getContext()));

            options = new FirebaseRecyclerOptions.Builder<User>().setQuery(databaseReference, User.class).build();

        friendsAdapter = new FirebaseRecyclerAdapter<User, UsersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final UsersViewHolder usersViewHolder, final int position, @NonNull final User user) {
                final String friendID =getRef(position).getKey() ;

                userDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String friendName = dataSnapshot.child(friendID).child("screen_name").getValue().toString();
                        String friendBio = dataSnapshot.child(friendID).child("bio").getValue().toString();
                        String friendProfileThumb = dataSnapshot.child(friendID).child("profile_photo_thumbnail").getValue().toString();

                        usersViewHolder.tvOtherUserScreenName.setText(friendName);
                        usersViewHolder.tvOtherUserBio.setText(friendBio);
                        Picasso.get().load(friendProfileThumb).placeholder(R.drawable.ic_user_grey).into(usersViewHolder.ivOtherUserThumbnail);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                /*
                usersViewHolder.tvOtherUserScreenName.setText(user.getScreen_name());
                usersViewHolder.tvOtherUserBio.setText(user.getBio());
                Picasso.get().load(user.getProfile_photo_thumbnail()).placeholder(R.drawable.ic_user_grey).into(usersViewHolder.ivOtherUserThumbnail);
                 */
                final String userID = getRef(position).getKey();

                usersViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {

                        CharSequence options[] = new CharSequence[]{"Open profile","Open Chat"};

                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setTitle("What do you want to do?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i==0){
                                    Intent goToOtherUser = new Intent(view.getContext(), OtherUserActivity.class);
                                    goToOtherUser.putExtra("Selected userID", userID);
                                    goToOtherUser.putExtra("Selected user thumbnail", user.getProfile_photo_thumbnail());
                                    startActivity(goToOtherUser);
                                }
                                else{
                                    Intent goToChat = new Intent(view.getContext(), ChatActivity.class);
                                    goToChat.putExtra("Selected userID", userID);
                                    goToChat.putExtra("Selected user thumbnail", user.getProfile_photo_thumbnail());
                                    startActivity(goToChat);
                                }
                            }
                        });
                        builder.show();

                    }
                });

            }

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new UsersViewHolder((LayoutInflater.from(view.getContext()).inflate(R.layout.layout_users_single, parent, false)));
            }
        };

        rvAllFriendsList.setAdapter(friendsAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        friendsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        friendsAdapter.stopListening();
    }
}
