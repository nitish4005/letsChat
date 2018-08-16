package com.example.android.letschat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class CreateProfile extends AppCompatActivity {

    private EditText name;
    private Button next;
    private ImageButton profilePicButton;
    private static final int PICK_IMAGE = 1;
    private String user_id,username,status="Hi there! I am using LetsChat",downloadUrl="default",thumb_downloadurl="default";
    private DatabaseReference mDatabase;
    private CircleImageView profilePicture;
    private StorageReference mStorageRef;
//    private Map<String,String> list;
    private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        next=(Button) findViewById(R.id.create_profile_next);
        name= (EditText) findViewById(R.id.editTextName);
        profilePicture=(CircleImageView) findViewById(R.id.profile_image);
        profilePicButton=(ImageButton) findViewById(R.id.create_profile_pic);
        FirebaseUser current_user= FirebaseAuth.getInstance().getCurrentUser();
        user_id=current_user.getPhoneNumber();
//        list = new HashMap<String, String>();
//        databaseReference = FirebaseDatabase.getInstance().getReference().child("usersList");
        mDatabase =FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//               // GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {};
//                Log.i("TEst",dataSnapshot.getValue()+"");
//                list =  (Map<String,String>) dataSnapshot.getValue();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        Log.i("TEst","aftercall");
//        if(list.containsKey(user_id)) {
//            Intent mainIntent = new Intent(getApplicationContext(),MainActivity.class);
//            startActivity(mainIntent);
//            finish();
//        }
//
//        databaseReference.child(user_id).setValue("true");
        profilePicButton.setOnClickListener(new View.OnClickListener() {
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
                hashMap.put("thumb_image",thumb_downloadurl);
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
                File thumb_filepath= new File(resultUri.getPath());
                Bitmap thumb_Bitmap = null;
                try {
                     thumb_Bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(50)
                            .compressToBitmap(thumb_filepath);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                thumb_Bitmap.compress(Bitmap.CompressFormat.JPEG,75,baos);
                final byte[] thumb_byte=baos.toByteArray();

                StorageReference filepath = mStorageRef.child( "profile_images").child(user_id+"dp.jpg");
                final StorageReference thumb_FilePath = mStorageRef.child("profile_images").child("thumbs").child(user_id+"dp.jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                     if(task.isSuccessful()){
                         downloadUrl = task.getResult().getDownloadUrl().toString();

                         UploadTask uploadTask = thumb_FilePath.putBytes(thumb_byte);
                         uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                             @Override
                             public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                 thumb_downloadurl = thumb_task.getResult().getDownloadUrl().toString();

                                 if(thumb_task.isSuccessful()){
                                     Map updateMap = new HashMap();
                                     updateMap.put("image",downloadUrl);
                                     updateMap.put("thumb_image",thumb_downloadurl);

                                     mDatabase.updateChildren(updateMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                         @Override
                                         public void onComplete(@NonNull Task<Void> task) {
                                             if(task.isSuccessful()){
                                                 mProgressDialog.dismiss();
                                                 Picasso.with(CreateProfile.this).load(thumb_downloadurl).placeholder(R.drawable.default_pic).into(profilePicture);
                                                 Toast.makeText(getApplicationContext(),"Success Uploading ",Toast.LENGTH_LONG).show();
                                             }

                                         }
                                     });

                                 }else {
                                     Toast.makeText(getApplicationContext(),"Error in uploading thumbnails",Toast.LENGTH_LONG).show();
                                     mProgressDialog.dismiss();
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
