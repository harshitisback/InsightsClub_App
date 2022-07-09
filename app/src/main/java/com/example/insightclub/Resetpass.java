package com.example.insightclub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Resetpass extends AppCompatActivity {


    ImageView back;
    EditText email;
    ProgressBar pb;
    View resetButton;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpass);
        back = (ImageView) findViewById(R.id.back);
        email = (EditText) findViewById(R.id.emailReset);
        pb = (ProgressBar) findViewById(R.id.pgbar);
        resetButton = (View) findViewById(R.id.rectangle_2);
        auth = FirebaseAuth.getInstance();


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Resetpass.this, LoginActivity.class));
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetpass();
            }
        });
    }

    private void resetpass() {
        String Email = email.getText().toString().trim();
        if (Email.isEmpty()) {
            email.setError("Email is required");
            email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            email.setError("Please enter a valid emial");
            email.requestFocus();
            return;
        }

        pb.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(Email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Resetpass.this, "Check your email to reset the password", Toast.LENGTH_LONG).show();
                    pb.setVisibility(View.GONE);
                    startActivity(new Intent(Resetpass.this, LoginActivity.class));
                } else {
                    Toast.makeText(Resetpass.this, "Something is Wrong Try again", Toast.LENGTH_LONG).show();
                    pb.setVisibility(View.GONE);
                }
            }
        });
    }
}