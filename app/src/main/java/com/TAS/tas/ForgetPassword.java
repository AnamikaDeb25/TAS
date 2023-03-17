package com.TAS.tas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;

import org.jetbrains.annotations.NotNull;

public class ForgetPassword extends AppCompatActivity {
    Button reset;
    EditText email;
    FirebaseAuth auth;
    GoogleProgressBar googleProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        reset = findViewById(R.id.reset);
        email=findViewById(R.id.emailid);

        ConstraintLayout constraintLayout = findViewById(R.id.cll);

        googleProgressBar = findViewById(R.id.progress);

        ImageView back = findViewById(R.id.b);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        auth = FirebaseAuth.getInstance();
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = email.getText().toString().trim();
                if (Email.isEmpty()){
                    email.setError("Email is required");
                    email.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
                    email.setError("Enter a valid email");
                    email.requestFocus();
                    return;
                }
                else {
                    googleProgressBar.setVisibility(View.VISIBLE);
                    auth.sendPasswordResetEmail(Email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Snackbar.make(constraintLayout,"Check your mail to reset password",Snackbar.LENGTH_LONG).show();
                                Toast.makeText(ForgetPassword.this,"Check your mail to reset password",Toast.LENGTH_LONG).show();
                                googleProgressBar.setVisibility(View.GONE);
                            }
                            else Toast.makeText(ForgetPassword.this,"Something went wrong! Try again",Toast.LENGTH_LONG).show();
                            googleProgressBar.setVisibility(View.GONE);
                        }
                    });
                }


            }
        });
    }



}