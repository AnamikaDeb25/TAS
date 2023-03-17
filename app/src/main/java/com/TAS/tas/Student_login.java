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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class Student_login extends AppCompatActivity {

    EditText useremail, userpass;
    TextView login;
    TextView forget, signup;
    String email,password;
    FirebaseAuth mAuth;
//    FirebaseUser user;
//    DatabaseReference reference;
    LottieAnimationView lottieAnimationView;

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        if (FirebaseAuth.getInstance().getCurrentUser() != null){
//            startActivity(new Intent(Student_login.this,StudentHome.class));
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(R.layout.activity_student_login);

        useremail = findViewById(R.id.email);
        userpass = findViewById(R.id.password);
        forget = findViewById(R.id.forget);
        signup = findViewById(R.id.signup);
        login = findViewById(R.id.go);
        lottieAnimationView = findViewById(R.id.load);
        mAuth = FirebaseAuth.getInstance();

        TextView forget = findViewById(R.id.forget);
        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Student_login.this,ForgetPassword.class));
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Student_login.this,studentSignin.class);
                startActivity(intent);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = useremail.getText().toString().trim();
                password = userpass.getText().toString().trim();


                if (email.isEmpty()){
                    useremail.setError("Enter your email");
                    useremail.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    useremail.setError("Please enter a valid email");
                    useremail.requestFocus();
                    return;
                }
                if (password.isEmpty()){
                    userpass.setError("Enter your password");
                    userpass.requestFocus();
                    return;
                }
                if (password.length()<6){
                    userpass.setError("Min password length is 6 characters");
                    userpass.requestFocus();
                    return;}

                else login();

            }
        });
    }

    private void login() {
        lottieAnimationView.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    DatabaseReference firebaseDatabase =   FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    firebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UserData userprofile = snapshot.getValue(UserData.class);
                            String isstu = userprofile.isstu;
                            String t = "T" , f ="F";
                            if (isstu.equals(t)){
                                startActivity(new Intent(Student_login.this, Sbottomnavi.class));
                                lottieAnimationView.setVisibility(View.GONE);
                                finish();}
                            if (isstu.equals(f)){
                                startActivity(new Intent(Student_login.this, Tbottomnavi.class));
                                lottieAnimationView.setVisibility(View.GONE);
                                finish();}

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else Toast.makeText(Student_login.this,"Failed to Login! Check your details",Toast.LENGTH_LONG).show();
                lottieAnimationView.setVisibility(View.GONE);

            }
        });
    }
}
