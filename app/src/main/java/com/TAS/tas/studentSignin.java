package com.TAS.tas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

public class studentSignin extends AppCompatActivity {
    EditText sname,semail, spass;
    String name, password, email;
    TextView submit;
    TextView login;
    LottieAnimationView lottieAnimationView;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);

        auth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_student_signin);
        sname = findViewById(R.id.name);
        semail = findViewById(R.id.email);
        spass = findViewById(R.id.password);
        submit = findViewById(R.id.go);
        login = findViewById(R.id.login);
        lottieAnimationView = findViewById(R.id.load);



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(studentSignin.this,Student_login.class);
                startActivity(intent);
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateUser();
            }
        });

    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        if (auth.getCurrentUser() != null){
//            startActivity(new Intent(this,StudentHome.class));
//        }
//
//    }

    private void validateUser() {
        name = sname.getText().toString().trim();
        password = spass.getText().toString().trim();
        email = semail.getText().toString().trim();
        String isstu = "T";
        String phno = " ";
        String onlineStatus = "online";
        String typingTo = "noOne";

        if (name.isEmpty()){
            sname.setError("Name is required");
            sname.requestFocus();
            return;
        }
        if (email.isEmpty()){
            semail.setError("Email is required");
            semail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            semail.setError("Please enter a valid email");
            semail.requestFocus();
            return;
        }
        if (password.isEmpty()){
            spass.setError("Password is required");
            spass.requestFocus();
            return;
        }
        if (password.length()<6){
            spass.setError("Min password length should be 6 characters");
            spass.requestFocus();
            return;
        }
        else {
            addDataToFirebase(name,password,email,isstu,phno,onlineStatus,typingTo);}
    }

    private void addDataToFirebase(String name, String password, String email, String isstu, String phno,String onlineStatus,String typingTo) {
        lottieAnimationView.setVisibility(View.VISIBLE);
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser userr = auth.getCurrentUser();
                    String uid = userr.getUid();
                    UserData user = new UserData(name,password,email,isstu,phno,onlineStatus,typingTo,uid);
                    FirebaseDatabase.getInstance().getReference("User")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {

                            if (task.isSuccessful()){
                                FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("uid").setValue(uid);
                                Toast.makeText(studentSignin.this,"Registered Successfully",Toast.LENGTH_LONG).show();
                                lottieAnimationView.setVisibility(View.GONE);
                                Intent intent = new Intent(studentSignin.this, OtpVerificationStudent.class);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                Toast.makeText(studentSignin.this,"User already exsist",Toast.LENGTH_LONG).show();
                                lottieAnimationView.setVisibility(View.GONE);
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(studentSignin.this,"Something went wrong!! Try again",Toast.LENGTH_LONG).show();
                    lottieAnimationView.setVisibility(View.GONE);
                }
            }
        });
    }
}