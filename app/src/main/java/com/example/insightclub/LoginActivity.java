package com.example.insightclub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {

    ProgressBar pb;

    TextView signup, Forgotpass;
    EditText email, password;
    Button login;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        signup = (TextView) findViewById(R.id.signup);
        Forgotpass = (TextView)findViewById(R.id.forgot_password__);
        email = (EditText)findViewById(R.id.email_text);
        password = (EditText)findViewById(R.id.passtext);
        login = (Button)findViewById(R.id.btn_login);
        pb = (ProgressBar)findViewById(R.id.pb);
        auth = FirebaseAuth.getInstance();




//      login button
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userlogin();
            }
        });


//        signup moving to signup page by click




        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,Signup.class));
            }
        });

        Forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,Resetpass.class));
            }
        });
    }


//    method to login

    private void userlogin() {
        String Email = email.getText().toString().trim();
        String Pass = password.getText().toString().trim();
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
        if (Pass.isEmpty()) {
            password.setError("password is required");
            password.requestFocus();
            return;
        }
        if (Pass.length() < 6) {
            password.setError("Min password length should be 6 characters!");
            password.requestFocus();
            return;
        }
        pb.setVisibility(View.VISIBLE);

        auth.signInWithEmailAndPassword(Email, Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // redirect to user profile
                    startActivity(new Intent(LoginActivity.this, Homepage.class));
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                    pb.setVisibility(View.GONE);


                } else {
                    Toast.makeText(LoginActivity.this, "Failed to login Please check your credentials", Toast.LENGTH_LONG).show();
                    pb.setVisibility(View.GONE);
                }
            }
        });


    }

//    forgot password activity



//    backpress overridden to exit the application from login page

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}



