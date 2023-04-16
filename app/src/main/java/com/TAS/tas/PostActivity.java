package com.TAS.tas;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.anstrontechnologies.corehelper.AnstronCoreHelper;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

    ImageView imageView,back;
    private Uri selectedUri,pic;
    private  static final int PICK_FILE = 1;
    private static final int STORAGE_REQUEST_CODE = 200;
    UploadTask uploadTask;
    EditText etdesc;
    TextView btnchoosefile,btnuploadfile;
    VideoView videoView;
    String url,name;
    GoogleProgressBar progressBar;
    StorageReference storageReference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference db1,db2,db3;
    ConstraintLayout constraintLayout;
    String StoragePermissions[];
    AnstronCoreHelper coreHelper;

    MediaController mediaController;
    String type;
    PostMember postmember;
    public boolean isNetworkConnected(){
        boolean connected = false;
        try {

            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ninfo = cm.getActiveNetworkInfo();
            connected = ninfo != null && ninfo.isAvailable() && ninfo.isConnected();
            return connected;


        }catch (Exception e){

            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return connected;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mediaController = new MediaController(this);
        StoragePermissions = new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE};

        imageView = findViewById(R.id.iv_post);
        back = findViewById(R.id.b);
        progressBar = findViewById(R.id.pb);
       // videoView= findViewById(R.id.vv_post);
        constraintLayout = findViewById(R.id.clpost);
        btnchoosefile = findViewById(R.id.btn_choosefile_post);
        btnuploadfile = findViewById(R.id.btn_uploadfile_post);
        etdesc = findViewById(R.id.et_decs_post);
        postmember = new PostMember();

        storageReference = FirebaseStorage.getInstance().getReference("User posts");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        coreHelper = new AnstronCoreHelper(this);

        db1 = database.getReference("All images").child(currentuid);
        db3 = database.getReference("All posts");
        db3.keepSynced(true);

        btnuploadfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkConnected()){
                    Dopost();
                }
                else {
                    showSnakbar();
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnchoosefile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

    }

    private boolean checkStoragePermission(){
        boolean resul = ContextCompat.checkSelfPermission(PostActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return resul;
    }
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(PostActivity.this,StoragePermissions,STORAGE_REQUEST_CODE);
    }

    private void chooseImage() { //choosing image for post
            if (!checkStoragePermission()){
                requestStoragePermission();
            }
            else {
                chooseImagee();
            }

    }
 private void chooseImagee() { //choosing image for post
                Intent intent = new Intent(Intent.ACTION_PICK /* MediaStore.Images.Media.EXTERNAL_CONTENT_URI*/);
               intent.setType("image/*");
                 startActivityForResult(intent,PICK_FILE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //checking if we have selected any file
        if (requestCode == PICK_FILE || resultCode == RESULT_OK || data != null || data.getData() != null){
            selectedUri = data.getData();
              try {
                        Bitmap imagebitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedUri);
                        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                        String filename = String.format("%d.jpg",System.currentTimeMillis());
                        File finalfile = new File(path,filename);
                        FileOutputStream fileOutputStream = new FileOutputStream(finalfile);
                        imagebitmap.compress(Bitmap.CompressFormat.JPEG,20,fileOutputStream);
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        pic = Uri.fromFile(finalfile);
                        Picasso.get().load(pic).networkPolicy(NetworkPolicy.OFFLINE).fit().into(imageView);
                        imageView.setVisibility(View.VISIBLE);
                        type = "iv";
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
        }

    }


    private String getFileExt(Uri uri){ //will tell the type of sellected file like mp4 or something else
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType((contentResolver.getType(uri))); }
    @Override
    protected void onStart() {
        super.onStart();
        checkOnlineStatus("online");
    }
    private void Dopost() {
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
                    Toast.makeText(PostActivity.this, "error", Toast.LENGTH_SHORT).show();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}});
        final String desc = etdesc.getText().toString();

        Calendar cdate = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("dd MMM yy");
        final  String savedate = currentdate.format(cdate.getTime());

        Calendar ctime = Calendar.getInstance();
        SimpleDateFormat currenttime = new SimpleDateFormat("hh:mm aa");
        final String savetime = currenttime.format(ctime.getTime());


        final String time = savedate +" "+ savetime;


        if (TextUtils.isEmpty(desc) || selectedUri != null){

            progressBar.setVisibility(View.VISIBLE);

            final StorageReference reference = storageReference.child(coreHelper.getFileNameFromUri(pic));
            uploadTask = reference.putFile(pic);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw  task.getException();
                    }

                   // file.delete();
                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();

                        if (type.equals("iv")){
                            postmember.setDesc(desc);
                            postmember.setName(name);
                            postmember.setPostUri(downloadUri.toString());
                            postmember.setTime(time);
                            postmember.setUid(currentuid);
                            postmember.setUrl(url);
                            postmember.setType("iv");

                            String id1 = db3.push().getKey();
                            db1.child(id1).setValue(postmember);
                            // for both

                            db3.child(id1).setValue(postmember);

                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(PostActivity.this, "Post uploaded", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(PostActivity.this, "error", Toast.LENGTH_SHORT).show();
                        }


                    }
                    finish();

                }
            });

        }else {
            Toast.makeText(this, "Please fill all Fields", Toast.LENGTH_SHORT).show();
        }

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
    protected void onPause() {
        super.onPause();
        String timestamp = String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(timestamp);
    }


    private void showSnakbar() {
        Snackbar.make(constraintLayout,"Not Connected To The Network",Snackbar.LENGTH_LONG)
                .setAction("Turn On Mobile data", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                    }
                })
                .setActionTextColor(getResources().getColor(R.color.green))
                .show();
    }

}