package com.sleepy.tok.user_activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sleepy.tok.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class ChangePictureActivity extends AppCompatActivity {
    private StorageReference mStorageRef;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_picture);
        context = ChangePictureActivity.this;
        mStorageRef = FirebaseStorage.getInstance().getReference();

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(this);
    }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();

                    StorageReference filepath = mStorageRef.child("user_profile_pictures/").child("profile_image.jpg");
                    filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Profile Picture changed.", Toast.LENGTH_SHORT).show();
                            }
                        else{
                            Toast.makeText(context, "An error has occurred. Profile picture not changed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }
        }

}
