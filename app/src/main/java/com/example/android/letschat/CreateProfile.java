package com.example.android.letschat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class CreateProfile extends AppCompatActivity {

    private EditText name;
    private Button next;
    private ImageButton profilePic;
    private static final int PICK_IMAGE = 1;
    private String user_id,username,status="Hi there! I am using LetsChat",downloadUrl="default";
    private DatabaseReference mDatabase,databaseReference;
    private StorageReference mStorageRef;
    private Map<String,String> list;
    private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        next=(Button) findViewById(R.id.create_profile_next);
        name= (EditText) findViewById(R.id.editTextName);
        profilePic=(ImageButton) findViewById(R.id.create_profile_pic);
        FirebaseUser current_user= FirebaseAuth.getInstance().getCurrentUser();
        user_id=current_user.getPhoneNumber();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("usersList");
        mDatabase =FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list= (Map<String,String>) dataSnapshot.getValue();
                Log.i("Map", dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(list.containsKey(user_id)) {
            Intent mainIntent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(mainIntent);
            finish();
        }

        databaseReference.child(user_id).setValue("true");
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Intent galleryIntent = new Intent();
//                galleryIntent.setType("image/*");
//                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), PICK_IMAGE);
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1)
                        .start(CreateProfile.this);

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username=name.getText().toString();
                HashMap<String ,String > hashMap = new HashMap<>();
                hashMap.put("image",downloadUrl);
                hashMap.put("name",username);
                hashMap.put("status",status);
                hashMap.put("thumb_image","default");
                mDatabase.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.i("tasksuccesfull","loged in sucessfully");
                            Intent mainIntent = new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(mainIntent);
                            finish();
                        }
                    }
                });

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
          if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mProgressDialog= new ProgressDialog(CreateProfile.this);
                mProgressDialog.setTitle("Uploading image...");
                mProgressDialog.setMessage("Please wait while we upload and process the image");
                mProgressDialog.show();
                Uri resultUri = result.getUri();
                StorageReference filepath = mStorageRef.child( "profile_images").child(user_id+"dp.jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                     if(task.isSuccessful()){
                         downloadUrl = task.getResult().getDownloadUrl().toString();
                         mDatabase.child("image").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                             @Override
                             public void onComplete(@NonNull Task<Void> task) {
                                 if(task.isSuccessful()){
                                     mProgressDialog.dismiss();
                                     Toast.makeText(getApplicationContext(),"Success Uploading ",Toast.LENGTH_LONG).show();
                                 }

                             }
                         });
                     } else {
                         Toast.makeText(getApplicationContext(),"Error in uploading",Toast.LENGTH_LONG).show();
                         mProgressDialog.dismiss();

                     }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


}
