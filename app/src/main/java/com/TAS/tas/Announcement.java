package com.TAS.tas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class Announcement extends AppCompatActivity {

    EditText editText;
    Button button;
    String name, url;
    ImageView bck;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);
        editText = findViewById(R.id.announcement);
        button = findViewById(R.id.done);
        bck = findViewById(R.id.bck);

        bck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference  databaseReference = firebaseDatabase.getReference("User");

        databaseReference.child(currentuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserData userprofile = snapshot.getValue(UserData.class);
                if (userprofile != null) {
                    name = userprofile.name;
                    url = userprofile.image;
                }else {
                    Toast.makeText(Announcement.this, "error", Toast.LENGTH_SHORT).show();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}});
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar callfordate = Calendar.getInstance();
                SimpleDateFormat currentdate = new SimpleDateFormat("dd MMM yy");
                final  String savedate = currentdate.format(callfordate.getTime());

                Calendar callfortime = Calendar.getInstance();
                SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:ss");
                final  String savetime = currenttime.format(callfortime.getTime());

                String timestamp = savedate+" "+savetime;
                String an = editText.getText().toString().trim();
                if (an.isEmpty()){
                    editText.setError("Write your announcement");
                    editText.requestFocus();
                }
                else {
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
                HashMap<String , Object> hashMap = new HashMap<>();
                hashMap.put("name",name);
                hashMap.put("url",url);
                hashMap.put("uid",currentuid);
                hashMap.put("announcement",an);
                hashMap.put("time",timestamp);

                databaseRef.child("Announcements").push().setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        FcmNotificationSender notificationsSender = new FcmNotificationSender("/topics/all","TAS","You have an announcement from "+name,
                                        getApplicationContext(),Announcement.this);

                        notificationsSender.SendNotifications();
                        Toast.makeText(Announcement.this, "Announcement Created", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });}
            }
        });

    }
    private void checkOnlineStatus(String status){
        DatabaseReference bdRef = FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus",status);
        //update value of onlineStatus
        bdRef.updateChildren(hashMap);

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkOnlineStatus("online");
    }
    @Override
    protected void onStart() {
        checkOnlineStatus("online");
        super.onStart();
    }
    @Override
    protected void onPause() {
        super.onPause();
        String timestamp = String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(timestamp);
    }
}