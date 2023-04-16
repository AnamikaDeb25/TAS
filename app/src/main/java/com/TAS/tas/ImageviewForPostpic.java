package com.TAS.tas;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ImageviewForPostpic extends AppCompatActivity {

    ImageView btndownload,btnback;
    PhotoView imageView;
    TextView gethisname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_view);

        imageView = findViewById(R.id.chatimageView);
        btnback = findViewById(R.id.backk);
        btndownload = findViewById(R.id.download);
        btndownload.setVisibility(View.GONE);
        btndownload.setEnabled(false);
        gethisname = findViewById(R.id.gethisname);
        gethisname.setVisibility(View.GONE);
        gethisname.setEnabled(false);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_FULLSCREEN);
        String url = getIntent().getStringExtra("url");
        try {
            Picasso.get().load(url).networkPolicy(NetworkPolicy.OFFLINE).fit().placeholder(R.drawable.ic_baseline_image_24).into(imageView);
        }
        catch (Exception e){
            Picasso.get().load(R.drawable.ic_baseline_image_24).networkPolicy(NetworkPolicy.OFFLINE).fit().into(imageView);
        }
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
