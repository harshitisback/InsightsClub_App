package com.example.insightclub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.insightclub.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class Signup extends AppCompatActivity {

    Button btn_signup;
    EditText Fullname;
    EditText Email;
    EditText Password;
    EditText ConfirmPass;
    EditText PhoneNum;
    ProgressBar pb;
    ImageView Profilepic;
    private FirebaseAuth auth;
    FirebaseStorage storage;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();
        btn_signup = (Button) findViewById(R.id.signupButton);
        Fullname = (EditText)findViewById(R.id.fullname);
        Email = (EditText)findViewById(R.id.email);
        Password = (EditText)findViewById(R.id.password);
        ConfirmPass = (EditText)findViewById(R.id.confirm_password);
        PhoneNum = (EditText)findViewById(R.id.phone_num);
        Profilepic = (ImageView) findViewById(R.id.profile_pic_oval);
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        pb = (ProgressBar)findViewById(R.id.progressbar);



//        database.getReference().child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        User userModel = snapshot.getValue(User.class);
//
//
//                        assert userModel != null;
//                        Glide.with(Signup.this).load(userModel.getProfileImg()).into(Profilepic);
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });


//      profile pic upload

        Profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 33);

            }
        });


//      signup button listner
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                getting all the values from the Edittext

                String email = Email.getText().toString().trim();
                String pass = Password.getText().toString().trim();
                String Name = Fullname.getText().toString().trim();
                String confpass = ConfirmPass.getText().toString().trim();
                String phone = PhoneNum.getText().toString().trim();

//                setting the conditions for all the Edittext

                if (Name.isEmpty()) {
                    Fullname.setError("Your name is required");
                    Fullname.requestFocus();
                    return;
                }
                if (email.isEmpty()) {
                    Email.setError("Your Email is required");
                    Email.requestFocus();
                    return;
                }
                if (pass.isEmpty()) {
                    Password.setError("Your Pass is required");
                    Password.requestFocus();
                    return;
                }
                if (phone.isEmpty()) {
                    PhoneNum.setError("Enter the phone number");
                    PhoneNum.requestFocus();
                    return;
                }
                if (phone.length()<10){
                    PhoneNum.setError("it must be 10 digit");
                    PhoneNum.requestFocus();
                    return;
                }
                if (confpass.isEmpty()) {
                    ConfirmPass.setError("Confirm Your Password ");
                    ConfirmPass.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Email.setError("Please Provide Valid email");
                    Email.requestFocus();
                    return;
                }
                if (pass.length() < 6) {
                    Password.setError("Min Password length should be 6 char");
                    Password.requestFocus();
                    return;
                }
                if (!confpass.equals(pass)) {
                    ConfirmPass.setError("Your Password should be same");
                    ConfirmPass.requestFocus();
                    return;
                }

                auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(Name, email,phone);
                            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                    "+91" + phone,
                                    60,
                                    TimeUnit.SECONDS,
                                    Signup.this,
                                    new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                        @Override
                                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                                        }

                                        @Override
                                        public void onVerificationFailed(@NonNull FirebaseException e) {

                                        }

                                        @Override
                                        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//                                            super.onCodeSent(s, forceResendingToken);
//                                                        startActivity(new Intent(Signup.this,OtpVerify.class));
                                            Intent intent = new Intent(getApplicationContext(),OtpVerify.class);
                                            intent.putExtra("backotp",s);
                                            startActivity(intent);
                                        }
                                    }
                            );
                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {



                                        startActivity(new Intent(Signup.this,OtpVerify.class));
                                        Toast.makeText(Signup.this, "User has been registered successfully", Toast.LENGTH_LONG).show();
                                        pb.setVisibility(view.VISIBLE);
                                        // redirect to login page

                                        pb.setVisibility(view.GONE);


                                    } else {
                                        Toast.makeText(Signup.this, "Failed to register try again!", Toast.LENGTH_LONG).show();
                                        pb.setVisibility(view.GONE);
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(Signup.this, "Failed to register try again!", Toast.LENGTH_LONG).show();
                            pb.setVisibility(view.GONE);
                        }

                    }
                });



            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data.getData() != null) {
            Uri profileUri = data.getData();
            Profilepic.setImageURI(profileUri);

            final StorageReference reference = storage.getReference().child("profile_picture")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            reference.putFile(profileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(Signup.this,"Uploaded",Toast.LENGTH_SHORT).show();

                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                                    .child("profileImg").setValue(uri.toString());

                            Toast.makeText(Signup.this,"Profile Picture Uploaded",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }
}