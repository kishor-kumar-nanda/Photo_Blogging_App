package com.example.rajve.firebaseapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

//the main purpose of this activity is that even if the user closes the app with out setting up the name and pic
//he will be send back to this activity to set up his details and start using it
public class SetupActivity extends AppCompatActivity {

    private CircleImageView setupImage;
    private Uri mainImageURI;

    private EditText setupName;
    private Button setupButton;
    private ProgressBar setupProgress;

    private StorageReference mStorageRef;
    private FirebaseAuth firebaseAuth; //we need this here to save the image with the User Id
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        Toolbar setupToolbar=findViewById(R.id.setup_toolbar);
        getSupportActionBar().setTitle("Account Details");

        setupImage= findViewById(R.id.setupImage);
        setupName=findViewById(R.id.setup_name);
        setupButton=findViewById(R.id.setup_btn);
        setupProgress=findViewById(R.id.setup_progress);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        firebaseAuth= FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();

        setupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //once the user clicks the button then we will take the name and convert it into string and store it in user_name
                String user_name=setupName.getText().toString();
                //now we can check if the name and profile pic are filled or not
                if (!TextUtils.isEmpty(user_name) && mainImageURI!=null){

                    //now we will upload the image to firestore
                    String userId=firebaseAuth.getCurrentUser().getUid();
                    setupProgress.setVisibility(View.VISIBLE);
                    //getting the image path by the reference of storage i.e mStorageRed and with in that we create a sub directory called Profile picture
                    //and inside the folder we can store the image with user id
                    StorageReference image_path = mStorageRef.child("Profile Pictures").child(userId + ".jpg");

                    //now to store the image uri in the path, and then if the task is successful then we should store it to the storage but for now we a re showing it as a toast
                    //else if it is not successful then show the error message as a toast, as the exception can be anything
                    image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if (task.isSuccessful()){
                                //once the image is uploaded then it will be stored in firestore

                            }
                            else {
                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(SetupActivity.this,"Error: "+errorMessage,Toast.LENGTH_LONG).show();
                            }
                            setupProgress.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });
        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //this check is the user has a version code of marshmello and greater or not
                //which will then ask the user to read and write to external storage
                if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.M){

                    //here we check if the permissions are granted or not
                    //is yes then shows a toast of already have permission
                    //else permission is denied
                    if (ContextCompat.checkSelfPermission(SetupActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE) !=PackageManager.PERMISSION_GRANTED ){
                        Toast.makeText(SetupActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                    else{
                        //so here the permission is successful now he can open up and can crop the image
                        ImageCropper();
                    }
                } else {
                    //this for the android versions lower than Marshmello
                    ImageCropper();
                }
            }
        });
    }

    private void ImageCropper() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(SetupActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //the CropImage.activity... will send the request code it will be checked with the request code
        //once there is a match then it will get the results
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();//this will get the result of the cropped image and save it in the mainImageURI

                //after we get the cropped image then we set it to the setupImage
                setupImage.setImageURI(mainImageURI);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }
    }
}
