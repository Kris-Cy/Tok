package com.sleepy.tok.getting_started;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import id.zelory.compressor.Compressor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sleepy.tok.R;
import com.sleepy.tok.Utils.Firebase;
import com.sleepy.tok.data_models.User;
import com.sleepy.tok.main_activity.MainActivity;
import com.sleepy.tok.user_activity.UserActivity;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class SetupProfileActivity extends AppCompatActivity {
    private static final String TAG = "SetupProfileActivity";
    private Context context;
    private Firebase firebase;
    private User userDataModel;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String userID;
    private String email, password;
    private StorageReference mStorageRef;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    EditText etChooseScreenName, etChooseBio;
    ImageView ivProfilePhoto;
    Button btnUploadProfileImage, btnCreateProfile;
    String username, ScreenName, bio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: setup profile started.");
        setContentView(R.layout.activity_setup_profile);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("user");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        user = mAuth.getCurrentUser();
        userDataModel= new User();
        context = SetupProfileActivity.this;
        firebase = new Firebase(context);
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        username = getIntent().getStringExtra("username");

        ivProfilePhoto = findViewById(R.id.ivProfilePhoto);
        etChooseBio=findViewById(R.id.etChooseBio);
        etChooseScreenName = findViewById(R.id.etChooseScreenName);
        btnCreateProfile=findViewById(R.id.btnCreateProfile);
        btnUploadProfileImage = findViewById(R.id.btnUploadProfileImage);


        ScreenName = etChooseScreenName.getText().toString().trim();
        bio = etChooseBio.getText().toString().trim();

        setBtnUploadProfileImage();
        setBtnCreateProfile(ScreenName, bio);
    }

    private void setBtnUploadProfileImage() {
        btnUploadProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: The change profile picture button has been clicked. Starting up the image crop library.");
                mStorageRef = FirebaseStorage.getInstance().getReference();

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(SetupProfileActivity.this);

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
                        ivProfilePhoto.setImageURI(resultUri);
                    }
                }
            }

    private void setBtnCreateProfile(final String screenName, final String bio){

        btnCreateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if((etChooseScreenName.getText().toString().isEmpty()) || (etChooseBio.getText().toString().isEmpty())){
                    if((etChooseScreenName.getText().toString().isEmpty())){
                        Log.d(TAG, "onClick: screen name is empty... uploading a default");
                        databaseReference.child(userID).child("screen_name").setValue(username);
                    }
                    else{
                        databaseReference.child(userID).child("screen_name").setValue(etChooseScreenName.getText().toString().trim());
                    }
                    if ((etChooseBio.getText().toString().isEmpty())) {
                        Log.d(TAG, "onClick: bio is empty... uploading a default");
                        databaseReference.child(userID).child("bio").setValue("Hey there! I didn't feel like creating a bio");
                    }
                    else {
                        databaseReference.child(userID).child("bio").setValue(etChooseBio.getText().toString().trim());
                    }

                }
                else{
                    databaseReference.child(userID).child("screen_name").setValue(etChooseScreenName.getText().toString().trim());
                    databaseReference.child(userID).child("bio").setValue(etChooseBio.getText().toString().trim());
                }
                Log.d(TAG, "onClick: Let's upload the default image...");
                uploadDefaultImage();

                Intent goToMain = new Intent(context, MainActivity.class);
                goToMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(goToMain);
            }
        });
    }

    private void uploadDefaultImage(){
        final StorageReference filepath = mStorageRef.child("user_profile_pictures/"+userID);
        final StorageReference thumbnailFilePath = mStorageRef.child("user_profile_pictures/").child("thumbnails").child(userID);


        Bitmap defaultBitmap = ((BitmapDrawable) ivProfilePhoto.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        defaultBitmap.compress (Bitmap.CompressFormat.JPEG,100,baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = filepath.putBytes(data);
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
                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(final Uri uri) {
                        databaseReference.child(userID).child("profile_photo").setValue(uri.toString().trim()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Bitmap thumbnailBitmap = ((BitmapDrawable) ivProfilePhoto.getDrawable()).getBitmap();

                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                thumbnailBitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
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
                                                databaseReference.child(userID).child("profile_photo_thumbnail").setValue(uri.toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Log.d(TAG, "onComplete: uploaded new user thumbnail");
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
