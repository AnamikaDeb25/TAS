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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TeacherLogin extends AppCompatActivity {

    TextView gosignin;
    EditText useremaill, userpassss;
    TextView login;
    String eemail,ppassword;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    DatabaseReference reference;
    LottieAnimationView lottieAnimationVieww;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_teacher_login);


        useremaill = findViewById(R.id.eemail);
        userpassss = findViewById(R.id.passss);
        login = findViewById(R.id.ssubmit);
        lottieAnimationVieww = findViewById(R.id.loadingg);
        firebaseAuth = FirebaseAuth.getInstance();

        TextView forget = findViewById(R.id.forget);
        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TeacherLogin.this,ForgetPassword.class));
            }
        });
        gosignin = findViewById(R.id.teachersign);
        gosignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherLogin.this, TeacherSignin.class);
                startActivity(intent);
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eemail = useremaill.getText().toString().trim();
                ppassword = userpassss.getText().toString().trim();

                if (eemail.isEmpty()) {
                    useremaill.setError("Enter your email");
                    useremaill.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(eemail).matches()) {
                    useremaill.setError("Please enter a valid email");
                    useremaill.requestFocus();
                    return;
                }
                if (ppassword.isEmpty()) {
                    userpassss.setError("Enter your password");
                    userpassss.requestFocus();
                    return;
                }
                if (ppassword.length() < 6) {
                    userpassss.setError("Min password length is 6 characters");
                    userpassss.requestFocus();
                    return;
                } //else login();
                else {
                       gologin();


                }
            }
        });

    }

    private void gologin() {
        lottieAnimationVieww.setVisibility(View.VISIBLE);
        firebaseAuth.signInWithEmailAndPassword(eemail,ppassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                UserData userData = snapshot.getValue(UserData.class);
                                // Toast.makeText(TeacherLogin.this,"id is   "+userData.getUid(),Toast.LENGTH_LONG).show();
                                String isstu = userData.isstu;
                                String T = "T", F = "F";
                                if (isstu.equals(T)){

                                    startActivity(new Intent(TeacherLogin.this, Sbottomnavi.class));
                                    lottieAnimationVieww.setVisibility(View.GONE);

                                    finish(); }
                                if (isstu.equals(F)){
                                    startActivity(new Intent(TeacherLogin.this, Tbottomnavi.class));
                                    lottieAnimationVieww.setVisibility(View.GONE);
                                    finish();}
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    else Toast.makeText(TeacherLogin.this,"Failed to Login! Check your details",Toast.LENGTH_LONG).show();
                    lottieAnimationVieww.setVisibility(View.GONE);
                }
            });

    }



    }

