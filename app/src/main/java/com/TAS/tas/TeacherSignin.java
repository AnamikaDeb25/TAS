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

public class TeacherSignin extends AppCompatActivity {
    TextView gologin;
   // CheckBox check;
    EditText teachername, teacheremail,teacherpass,scl;
    TextView create;
    LottieAnimationView lottieAnimationView;

    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_signin);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);

        auth = FirebaseAuth.getInstance();

        lottieAnimationView= findViewById(R.id.lottie);
        lottieAnimationView.setVisibility(View.GONE);

        teachername = findViewById(R.id.teachername);
        teacheremail = findViewById(R.id.teacheremail);
        teacherpass = findViewById(R.id.teacherpass);
        create = findViewById(R.id.createteacher);
        scl = findViewById(R.id.scl);


        gologin = findViewById(R.id.gologinpage);
        gologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherSignin.this,TeacherLogin.class);
                startActivity(intent);
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = teachername.getText().toString().trim();
                String school = scl.getText().toString().trim();
                String email = teacheremail.getText().toString().trim();
                String password = teacherpass.getText().toString().trim();
                String phno = " ";
                String isstu = "F";
                String onlineStatus = "online";
                String typingTo = "noOne";
               // Toast.makeText(TeacherSignin.this, phno, Toast.LENGTH_LONG).show();
                if (name.isEmpty()) {
                    teachername.setError("Enter name");
                    teachername.requestFocus();
                    return;
                }
                if (school.isEmpty()) {
                    scl.setError("Enter School name");
                    scl.requestFocus();
                    return;
                }
                if (email.isEmpty()) {
                    teacheremail.setError("Enter email");
                    teacheremail.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    teacheremail.setError("Enter a valid email");
                    teacheremail.requestFocus();
                    return;
                }
                if (password.isEmpty()) {
                    teacherpass.setError("Enter password");
                    teacherpass.requestFocus();
                    return;
                }
                if (password.length() < 6) {
                    teacherpass.setError("Min password length should be 6 characters");
                    teacherpass.requestFocus();
                    return;
                } else {
                    addDataToFirebase(name, password, email, isstu, phno, onlineStatus, typingTo,school);
                }
            }});


}



    private void addDataToFirebase(String name, String password, String email,String isstu,String phno,String onlineStatus, String typingTo,String school) {
        lottieAnimationView.setVisibility(View.VISIBLE);
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser user = auth.getCurrentUser();
                    String uid = user.getUid();
                    UserData teacher = new UserData(name,password,email,isstu,phno,onlineStatus,typingTo,uid,school);
                    FirebaseDatabase.getInstance().getReference("User")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(teacher).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {

                           if (task.isSuccessful()){
                               FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("uid").setValue(uid);
                                Toast.makeText(TeacherSignin.this,"Registered Successfully",Toast.LENGTH_LONG).show();
                                lottieAnimationView.setVisibility(View.GONE);
                                Intent intent = new Intent(TeacherSignin.this, OtpVerificationteachers.class);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                Toast.makeText(TeacherSignin.this,"User already exsist",Toast.LENGTH_LONG).show();
                                lottieAnimationView.setVisibility(View.GONE);
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(TeacherSignin.this,"Something went wrong!! Try again",Toast.LENGTH_LONG).show();
                    lottieAnimationView.setVisibility(View.GONE);
                }
            }
        });
    }




    }