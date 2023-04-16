package com.TAS.tas;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ImageViewofprofilepic extends AppCompatActivity {

    ImageView btndownload,btnback;
    PhotoView imageView;
    TextView gethisname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //can't take ss
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_image_view);

        imageView = findViewById(R.id.chatimageView);
        btnback = findViewById(R.id.backk);
        btndownload = findViewById(R.id.download);
        btndownload.setVisibility(View.GONE);
        btndownload.setEnabled(false);
        gethisname = findViewById(R.id.gethisname);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_FULLSCREEN);

        String uid = getIntent().getStringExtra("uid");
        DatabaseReference  userDbRef = FirebaseDatabase.getInstance().getReference("User");
        Query userQuery = userDbRef.orderByChild("uid").equalTo(uid);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    String image = "" + ds.child("image").getValue();
                    String name = "" + ds.child("name").getValue();
                    gethisname.setText(name);
                    try {
                        Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.acc).into(imageView);
                    }
                    catch (Exception e){
                        Picasso.get().load(R.drawable.acc).networkPolicy(NetworkPolicy.OFFLINE).into(imageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
