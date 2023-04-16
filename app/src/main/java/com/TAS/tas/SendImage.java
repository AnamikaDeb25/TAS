package com.TAS.tas;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

import java.util.HashMap;

public class SendImage extends AppCompatActivity {


    String url,receiver_name,sender_uid,receiver_uid;
    ImageView imageView;
    Uri imageurl;
    GoogleProgressBar progressBar;
    ImageView button;
    UploadTask uploadTask;

    TextView textView;
    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    DatabaseReference rootRef1,rootRef2;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    String  usertoken;

    private  Uri uri;
    ModelChat messageMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_image);

        messageMember = new ModelChat();
        storageReference = firebaseStorage.getInstance().getReference("Message Images");

        imageView = findViewById(R.id.iv_sendImage);
        button = findViewById(R.id.btn_sendimage);
        progressBar = findViewById(R.id.pb_sendimage);
        textView = findViewById(R.id.tv_dont);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            url = bundle.getString("u");
            receiver_name = bundle.getString("n");
            receiver_uid = bundle.getString("ruid");
            sender_uid = bundle.getString("suid");
        }else {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }

        Picasso.get().load(url).networkPolicy(NetworkPolicy.OFFLINE).fit().into(imageView);
        imageurl = Uri.parse(url);

        rootRef1 = database.getReference("Chats").child(sender_uid).child(receiver_uid);
        rootRef2 = database.getReference("Chats").child(receiver_uid).child(sender_uid);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendImage();
                textView.setVisibility(View.VISIBLE);
            }
        });
    }
    private String getFileExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType((contentResolver.getType(uri)));
    }
    private void sendImage() {
        if (imageurl != null){
            progressBar.setVisibility(View.VISIBLE);
            button.setVisibility(View.INVISIBLE);
            final StorageReference reference = storageReference.child(System.currentTimeMillis()+ "."+getFileExt(imageurl));
            uploadTask = reference.putFile(imageurl);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw  task.getException();
                    }

                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        String timestamp = String.valueOf(System.currentTimeMillis());

                        HashMap<String , Object> hashMap = new HashMap<>();
                        hashMap.put("Sender",sender_uid);
                        hashMap.put("receiver",receiver_uid);
                        hashMap.put("message",downloadUri.toString());
                        hashMap.put("timestamp",timestamp);
                        hashMap.put("type","i");
                        hashMap.put("isSeen",false);
                        databaseReference.child("Chats").push().setValue(hashMap);

                        DatabaseReference database = FirebaseDatabase.getInstance().getReference("User").child(sender_uid);
                        database.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                UserData userData = snapshot.getValue(UserData.class);
                                if (userData != null){
                                    String Name = userData.name;
                                    sendNotification(receiver_uid,Name);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        progressBar.setVisibility(View.INVISIBLE);
                        textView.setVisibility(View.INVISIBLE);



                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                               finish();
                            }
                        },2000);

                    }

                }
            });



        }else {
            Toast.makeText(this, "Please select something", Toast.LENGTH_SHORT).show();
        }
        DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(sender_uid)
                .child(receiver_uid);
        chatRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    chatRef1.child("id").setValue(receiver_uid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(receiver_uid)
                .child(sender_uid);

        chatRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    chatRef2.child("id").setValue(sender_uid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendNotification(String receiver_uid, String name) {
        FirebaseDatabase.getInstance().getReference().child("Tokens").child(receiver_uid).child("token")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                          usertoken = snapshot.getValue(String.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                FcmNotificationSender notificationsSender =
                        new FcmNotificationSender(usertoken,"TAS",name+" sent you an image " ,
                                getApplicationContext(),SendImage.this);

                notificationsSender.SendNotifications();

            }
        },1000);
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