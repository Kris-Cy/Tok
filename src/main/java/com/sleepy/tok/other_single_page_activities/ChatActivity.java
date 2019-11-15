package com.sleepy.tok.other_single_page_activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sleepy.tok.R;
import com.sleepy.tok.Utils.MessagesAdapter;
import com.sleepy.tok.data_models.Messages;
import com.sleepy.tok.main_activity.MainActivity;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    private static final int GAL_PIC=1;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessagesAdapter adapter;
    String otherUserID;
    ImageView ivBackArrow, ivOtherUserThumbnail;
    TextView tvOtherUserScreenName;
    RecyclerView rvMessagesList;
    EditText etMessage;
    ImageButton btnPictureMessage, btnSend;


    private DatabaseReference databaseReference, userDatabaseReference, messagesDatabaseReference;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        otherUserID = getIntent().getStringExtra("Selected userID");
        Log.d(TAG, "onCreate: Starting the chatting activity. Other person's user ID is: "+otherUserID);
        setContentView(R.layout.activity_chat);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("user");
        storageReference = FirebaseStorage.getInstance().getReference();

        tvOtherUserScreenName = findViewById(R.id.tvOtherUserScreenName);
        ivBackArrow = findViewById(R.id.ivBackArrow);
        ivOtherUserThumbnail = findViewById(R.id.ivOtherUserThumbnail);
        etMessage=findViewById(R.id.etMessage);
        btnPictureMessage = findViewById(R.id.btnPictureMessage);
        btnSend = findViewById(R.id.btnSend);

        adapter = new MessagesAdapter(messagesList);

        rvMessagesList = findViewById(R.id.rvMessagesList);
        linearLayoutManager = new LinearLayoutManager(this);

        rvMessagesList.setHasFixedSize(true);
        rvMessagesList.setLayoutManager(linearLayoutManager);

        rvMessagesList.setAdapter(adapter);
        generateMessages();


        otherUserID = getIntent().getStringExtra("Selected userID");

        userDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String otherUserScreenName = dataSnapshot.child(otherUserID).child("screen_name").getValue().toString();
                String otherUserThumbnail = dataSnapshot.child(otherUserID).child("profile_photo_thumbnail").getValue().toString();

                tvOtherUserScreenName.setText(otherUserScreenName);
                Picasso.get().load(otherUserThumbnail).placeholder(R.drawable.ic_user_grey).into(ivOtherUserThumbnail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
                etMessage.setText(null);
            }
        });


        generateChatTable();
       // setBtnPictureMessage();
        backButton();
    }

    public void backButton() {
        ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backToMain = new Intent(ChatActivity.this, MainActivity.class);
                backToMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(backToMain);
            }
        });

    }

    private void generateChatTable(){
        databaseReference.child("chats").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(otherUserID)){
                    String timeStarted = Calendar.getInstance().getTime().toString();

                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", false);
                    chatAddMap.put ("time_started", timeStarted);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("chats/"+currentUser.getUid()+"/"+otherUserID, chatAddMap);
                    chatUserMap.put("chats/"+otherUserID+"/"+currentUser.getUid(), chatAddMap);

                    databaseReference.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError!=null){
                                Log.d(TAG, "CHAT_LOG"+databaseError.getMessage().toString());
                            }
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(){
        String message = etMessage.getText().toString();

        if(!TextUtils.isEmpty(message)){

            String currentUserRef = "messages/" + currentUser.getUid() + "/" + otherUserID;
            String otherUserRef = "messages/" + otherUserID + "/" + currentUser.getUid();
            String timeSent = Calendar.getInstance().getTime().toString();

            DatabaseReference userMsgPush = databaseReference.child("messages").child(currentUser.getUid()).child(otherUserID).push();
            String pushID = userMsgPush.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("message_type", "text");
            messageMap.put("time_sent", timeSent);
            messageMap.put("from", currentUser.getUid());

            Map messageUserMap = new HashMap();
            messageUserMap.put(currentUserRef+"/"+pushID, messageMap);
            messageUserMap.put(otherUserRef+"/"+pushID, messageMap);

            databaseReference.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError!=null){
                        Log.d(TAG, "CHAT_LOG"+databaseError.getMessage().toString());
                    }
                }
            });


        }
    }

    private void generateMessages(){
        otherUserID = getIntent().getStringExtra("Selected userID");
        databaseReference.child("messages").child(currentUser.getUid()).child(otherUserID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Messages messages = dataSnapshot.getValue(Messages.class);

                    messagesList.add(messages);
                    adapter.notifyDataSetChanged();

                    rvMessagesList.scrollToPosition((messagesList.size())-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setBtnPictureMessage(){
        btnPictureMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent openGallery = new Intent();
                openGallery.setType("image/*");
                openGallery.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(openGallery, "Choose a picture"), GAL_PIC);
            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GAL_PIC && resultCode==RESULT_OK){
            Uri picUri = data.getData();

            final String currentUserRef = "messages/" + currentUser.getUid() + "/" + otherUserID;
            final String otherUserRef = "messages/" + otherUserID + "/" + currentUser.getUid();

            DatabaseReference userMsgPush = databaseReference.child("messages").child(currentUser.getUid()).child(otherUserID).push();
            final String pushID = userMsgPush.getKey();

            final StorageReference filepath = storageReference.child("image_messages").child(currentUser.getUid()).child(pushID);
            filepath.putFile(picUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String timeSent = Calendar.getInstance().getTime().toString();

                                Map messageMap = new HashMap();
                                messageMap.put("message", uri);
                                messageMap.put("seen", false);
                                messageMap.put("message_type", "image");
                                messageMap.put("time_sent", timeSent);
                                messageMap.put("from", currentUser.getUid());

                                Map messageUserMap = new HashMap();
                                messageUserMap.put(currentUserRef+"/"+pushID, messageMap);
                                messageUserMap.put(otherUserRef+"/"+pushID, messageMap);

                                databaseReference.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        if (databaseError!=null){
                                            Log.d(TAG, "CHAT_LOG"+databaseError.getMessage().toString());
                                        }
                                    }
                                });
                            }
                        });



                    }
                }
            });

        }
    }



}

