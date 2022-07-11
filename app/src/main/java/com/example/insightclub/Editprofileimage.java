package com.example.insightclub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.insightclub.Models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Editprofileimage extends AppCompatActivity {

    CircleImageView profileimage;
    EditText phonehere;
    EditText namehere;
    EditText emailhere;
    Button btn_out;
    TextView changeimage;
    private FirebaseStorage storage;
    private DatabaseReference databaseReference;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private Uri imageUri;
    private  String myUri = "";
    private StorageTask uploadTask;
    private StorageReference storageProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofileimage);

        profileimage = findViewById(R.id.profile_image);
        phonehere = findViewById(R.id.phonehere);
        namehere = findViewById(R.id.namehere);
        emailhere = findViewById(R.id.emailhere);
        btn_out = findViewById(R.id.logouthere);
        changeimage = findViewById(R.id.change_profile_btn);

        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User");
        storageProfile = FirebaseStorage.getInstance().getReference().child("Profile pic");
        database = FirebaseDatabase.getInstance();

        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User userModel = snapshot.getValue(User.class);
                        namehere.setText(userModel.getFullname());
                        emailhere.setText(userModel.getEmail());
                        phonehere.setText(userModel.getPhone());

//                        Glide.with(Editprofileimage.this).load(userModel.getProfileImg()).into(profileimage);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        btn_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(Editprofileimage.this,LoginActivity.class));
            }
        });

        changeimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadProfileImage();


            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data.getData() != null) {
            Uri profileUri = data.getData();
            profileimage.setImageURI(profileUri);

            final StorageReference reference = storage.getReference().child("profile_pic")
                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));

            reference.putFile(profileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(Editprofileimage.this,"Uploaded",Toast.LENGTH_SHORT).show();

                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                                    .child("profileImg").setValue(uri.toString());
                        }
                    });
                }
            });
        }

    }

    private void uploadProfileImage() {
      Intent intent = new Intent();
       intent.setAction(Intent.ACTION_GET_CONTENT);
       intent.setType("image/*");
       startActivityForResult(intent,33);
    }
}