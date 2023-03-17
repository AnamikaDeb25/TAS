package com.TAS.tas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (FirebaseAuth.getInstance().getCurrentUser() != null){
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    databaseReference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            UserData userData = dataSnapshot.getValue(UserData.class);
                            String isstu = userData.isstu;
                            String T = "T", F = "F";
                            if (isstu.equals(T)){
                                startActivity(new Intent(MainActivity.this, Sbottomnavi.class));
                                finish(); }
                            if (isstu.equals(F)){
                                startActivity(new Intent(MainActivity.this, Tbottomnavi.class));
                                finish();}
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //  FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(MainActivity.this,Starting.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }else{
                    Intent intent = new Intent(MainActivity.this,Starting.class);
                    startActivity(intent);
                    finish();
                }
            }
        },1000);


    }

    }
