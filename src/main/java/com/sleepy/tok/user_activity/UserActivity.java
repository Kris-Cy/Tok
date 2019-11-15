package com.sleepy.tok.user_activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import id.zelory.compressor.Compressor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sleepy.tok.R;
import com.sleepy.tok.Utils.Firebase;
import com.sleepy.tok.Utils.SectionsStatePagerAdapter;
import com.sleepy.tok.Utils.UniversalImageLoader;
import com.sleepy.tok.data_models.User;
import com.sleepy.tok.main_activity.MainActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class UserActivity extends AppCompatActivity {
    private static final String TAG = "UserActivity";
    private Context context;
    private Firebase firebase;
    private User userDataModel;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private StorageReference mStorageRef;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private DatabaseReference onlineRef;
    private ViewPager viewPager;
    private RelativeLayout relativeLayoutBottom, relativeLayoutMiddle;
    private SectionsStatePagerAdapter pagerAdapter;


    ImageView ivBackArrow, ivProfileImage;
    ImageButton btnChangePicture;
    TextView tvScreenName, tvUsername, tvBio;
    String userID;
    //String profilePicture = " ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Log.d(TAG, "onCreate: UserActivity Started");

        mAuth=FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
       // onlineRef = databaseReference.child("user").child(mAuth.getCurrentUser().getUid()).child("online");
        mStorageRef = FirebaseStorage.getInstance().getReference();

        context = UserActivity.this;
        firebase=new Firebase(context);

        ivBackArrow = findViewById(R.id.ivBackArrow);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvScreenName = findViewById(R.id.tvScreenName);
        tvUsername = findViewById(R.id.tvUsername);
        tvBio = findViewById(R.id.tvBio);
        btnChangePicture = findViewById(R.id.btnChangePicture);
        relativeLayoutBottom = findViewById(R.id.relLayoutUserBottom);
        relativeLayoutMiddle = findViewById(R.id.relLayoutUserMiddle);


        ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Back Button was clicked. going back to Main Activity.");

                if(relativeLayoutBottom.getVisibility()==View.GONE){
                    Intent reloadUserActivity = new Intent(context, UserActivity.class);
                    startActivity(reloadUserActivity);
                }
                else{
                    Intent backToMain = new Intent (context, MainActivity.class);
                    backToMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(backToMain);
                }
            }
        });

        //database relation
            FirebaseUser user = mAuth.getCurrentUser();
            userID=user.getUid();

            databaseReference = FirebaseDatabase.getInstance().getReference().child("user").child(userID);
            databaseReference.keepSynced(true);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String bio=dataSnapshot.child("bio").getValue().toString();
                    final String profile_photo=dataSnapshot.child("profile_photo").getValue().toString();
                    //String profile_photo_thumb=dataSnapshot.child("profile_photo_thumb").getValue().toString();
                    String username = dataSnapshot.child("username").getValue().toString();
                    String screen_name = dataSnapshot.child("screen_name").getValue().toString();

                    tvBio.setText(bio);
                    tvScreenName.setText(screen_name);
                    tvUsername.setText(username);


                    ivProfileImage = findViewById(R.id.ivProfileImage);
                    userDataModel = new User();

                   // Picasso.get().load(profile_photo).placeholder(R.drawable.ic_user_grey).into(ivProfileImage);

                    Picasso.get().load(profile_photo).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.ic_user_grey).into(ivProfileImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(profile_photo).placeholder(R.drawable.ic_user_grey).into(ivProfileImage);
                        }
                    });





                    Log.d(TAG, "onDataChange: User ID: "+userID+", User bio: "+bio+", User Profile Photo String: "+profile_photo+", Username: "+username+", Screen name: "+screen_name);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


   //setProfileImage();
    listOfSettingsOptions();
    addTheFragments();
    setBtnChangePicture();
    setProfileImage();
    }


    private void setBtnChangePicture() {
        btnChangePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: The change profile picture button has been clicked. Starting up the image crop library.");
                mStorageRef = FirebaseStorage.getInstance().getReference();

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(UserActivity.this);

            }


        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                final Uri resultUri = result.getUri();


                Log.d(TAG, "onActivityResult: adding the new profile picture to the storage base...");


                final StorageReference filepath = mStorageRef.child("user_profile_pictures/"+userID);
                final StorageReference thumbnailFilePath = mStorageRef.child("user_profile_pictures/").child("thumbnails").child(userID);

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                           // final String profilePicture = filepath.getDownloadUrl().toString();

                            Log.d(TAG, "onComplete: new profile picture has been added to the storage base. Now adding to the database");
                            mStorageRef.child("user_profile_pictures/"+userID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(final Uri uri) {
                                    // Got the download URL for 'users/me/profile.png'
                                    databaseReference.child("profile_photo").setValue(uri.toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull final Task<Void> task) {
                                            Toast.makeText(UserActivity.this, "Profile Photo Changed.", Toast.LENGTH_SHORT).show();

                                            File thumbFilePath = new File(resultUri.getPath());
                                            try {
                                                Bitmap thumbnailBitmap = new Compressor(UserActivity.this)
                                                        .setMaxHeight(200)
                                                        .setMaxWidth(200)
                                                        .setQuality(70)
                                                        .compressToBitmap(thumbFilePath);


                                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                                thumbnailBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                                byte[] thumbnailByte = baos.toByteArray();

                                                final UploadTask uploadTask = thumbnailFilePath.putBytes(thumbnailByte);
                                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception exception) {
                                                        // Handle unsuccessful uploads
                                                    }
                                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                                        // ...
                                                    thumbnailFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {
                                                            databaseReference.child("profile_photo_thumbnail").setValue(uri.toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    Log.d(TAG, "onComplete: uploaded new user thumbnail");
                                                                }
                                                            });
                                                        }
                                                    });

                                                    }
                                                });
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });
                        }

                        else {
                            Log.d(TAG, "onComplete: Everything failed. There's an error here.");
                            Toast.makeText(context, "An error has occurred. Profile picture not changed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void listOfSettingsOptions(){
        Log.d(TAG, "listOfSettingsOptions: making the list of options");

        ListView lvAccountSettingsOptions=findViewById(R.id.lvAccountSettingsOptions);

        ArrayList<String> options = new ArrayList<>();

        options.add ("Change your Screen Name");
        options.add ("Change your bio");
        options.add ("Sign Out");

        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_expandable_list_item_1, options);

        lvAccountSettingsOptions.setAdapter(adapter);

        lvAccountSettingsOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                setViewPager(position);
            }
        });
    }

    private void addTheFragments(){
        Log.d(TAG, "addTheFragments: adding the fragments...");
        pagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new ScreenNameFragment(), "Change your Screen Name");
        pagerAdapter.addFragment(new ChangeBioFragment(), "Change your bio");
        pagerAdapter.addFragment(new SignOutFragment(), "Sign out");
    }

    private void setViewPager (int fragmentNumber){
        Log.d(TAG, "setViewPager: setting up the ViewPager...");

        viewPager = findViewById(R.id.viewPager);
        relativeLayoutBottom = findViewById(R.id.relLayoutUserBottom);
        relativeLayoutMiddle = findViewById(R.id.relLayoutUserMiddle);

        relativeLayoutBottom.setVisibility(View.GONE);
        relativeLayoutMiddle.setVisibility(View.GONE);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(fragmentNumber);
    }

    private void setProfileImage() {
        Log.d(TAG, "setProfileImage: generating the profile image...");

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

