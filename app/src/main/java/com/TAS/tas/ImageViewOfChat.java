package com.TAS.tas;

import static android.os.Environment.DIRECTORY_PICTURES;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DownloadManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class ImageViewOfChat extends AppCompatActivity {


    ImageView btndownload,btnback;
    PhotoView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        imageView = findViewById(R.id.chatimageView);
        btnback = findViewById(R.id.backk);
        btndownload = findViewById(R.id.download);
        TextView gethisname = findViewById(R.id.gethisname);
        gethisname.setVisibility(View.GONE);
        gethisname.setEnabled(false);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_FULLSCREEN);


        String url = getIntent().getStringExtra("url");
        Picasso.get().load(url).placeholder(R.drawable.ic_baseline_image_24).into(imageView);


        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        btndownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionListener permissionListener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        Download(url);
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {

                    }
                };
                TedPermission.with(ImageViewOfChat.this).setPermissionListener(permissionListener).setPermissions(Manifest.permission.INTERNET,Manifest.permission.READ_EXTERNAL_STORAGE).check();

            }
        });

    }

    private void Download(String url) {
        File direct =
                new File(Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        .getAbsolutePath() + "/" + "TAS" + "/");


        if (!direct.exists()) {
            direct.mkdir();
          //  Toast.makeText(ImageViewActivity.this,"dir created for first time",Toast.LENGTH_SHORT).show();
        }

        Uri uri = Uri.parse(url);
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalPublicDir(DIRECTORY_PICTURES,"TAS"+File.separator+"PIC.jpg");
        downloadManager.enqueue(request);
        Toast.makeText(ImageViewOfChat.this,"Image is downloading...",Toast.LENGTH_SHORT).show();
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