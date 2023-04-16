package com.TAS.tas;

import static android.view.View.GONE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView  sendBtn,cambtn,micbtn,play,back;
    CircleImageView profileIv;
    TextView nameTv, userStatusTv;
    EditText messageEt;
    GoogleProgressBar progressBar;
    ImageView more;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userDbRef;
    DatabaseReference rootref1;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    //for checking msg is seen or not
    ValueEventListener valueEventListener;
    DatabaseReference userRefForSeen;
    List<ModelChat> chatList;
    AdapterChat adapterChat;

    String hisUid, myUid,usertoken;
    String userName;
    Boolean isBlocked = false;

    Uri uri,pic;
    private static final int PICK_IMAGE = 1;

    MediaRecorder mediaRecorder;
    public static String filename= "recorded.3gp";
    String file = Environment.getExternalStorageDirectory()+ File.separator +Environment.DIRECTORY_DCIM+File.separator+filename;
    UploadTask uploadTask;
    ModelChat messageMember;
    ConstraintLayout constraintLayout;
    LinearLayout linearLayout;




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
        //can't take ss
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_chat);

        messageMember = new ModelChat();

        progressBar = findViewById(R.id.ppp);

        recyclerView = findViewById(R.id.chat_recyclerView);
        profileIv = findViewById(R.id.dpchat);
        play = findViewById(R.id.aaudio);
        sendBtn = findViewById(R.id.sendBtn);
        nameTv = findViewById(R.id.namechat);
        userStatusTv = findViewById(R.id.userstatus);
        messageEt = findViewById(R.id.messageEt);
        cambtn = findViewById(R.id.cam_sendMessage);
        micbtn = findViewById(R.id.mic_sendMessage);
        back = findViewById(R.id.bk);
        constraintLayout = findViewById(R.id.z);
        more = findViewById(R.id.more);
        linearLayout = findViewById(R.id.chatLayout);
        linearLayout.setVisibility(View.VISIBLE);
        UserData userData;
        userData = new UserData();
        //layout for recycler view
        LinearLayoutManager linearLayoutManager =  new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        DatabaseReference reference = database.getReference("Chats");
        reference.keepSynced(true);


        mediaRecorder = new MediaRecorder();

        String message = messageMember.getMessage();

        micbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionListener permissionListener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        mediaRecorder.setOutputFile(file);
                        createDialog();
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {

                    }
                };
                TedPermission.with(ChatActivity.this).setPermissionListener(permissionListener).setPermissions(Manifest.permission.INTERNET,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO).check();

            }
        });
        cambtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionListener permissionListener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent,PICK_IMAGE);
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {

                    }
                };
                TedPermission.with(ChatActivity.this).setPermissionListener(permissionListener).setPermissions(Manifest.permission.INTERNET,Manifest.permission.WRITE_EXTERNAL_STORAGE).check();
                }
        });
        profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(ChatActivity.this,ImageViewofprofilepic.class);
               intent.putExtra("uid",hisUid);
               startActivity(intent);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = messageEt.getText().toString().trim();
                if (TextUtils.isEmpty(message)){
                    Toast.makeText(ChatActivity.this, "Cannot sent empty message", Toast.LENGTH_SHORT).show();
                }
                else{
                    if (isNetworkConnected()){
                        sendMessage(message);
                    }
                    else {
                        showSnakbar();
                    }

                }
                //reset edittext after sending message
                messageEt.setText("");

            }
        });

        Intent intent = getIntent();
        hisUid = intent.getStringExtra("hisUid");
        userName = intent.getStringExtra("userName");


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        userDbRef = firebaseDatabase.getReference("User");


        //search user to get their info
        Query userQuery = userDbRef.orderByChild("uid").equalTo(hisUid);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String name = "" + ds.child("name").getValue();
                    String image = "" + ds.child("image").getValue();
                    String typingStatus = "" + ds.child("typingTo").getValue();
                    nameTv.setText(name);


                    //check typing status
                    if (typingStatus.equals(myUid)) {
                        userStatusTv.setText("Typing...");
                        userStatusTv.setTextColor(Color.GREEN);
                    }
                    else {
                        //get onlinestatus value
                        String onlineStatus = "" + ds.child("onlineStatus").getValue();
                        if (onlineStatus.equals("online")) {
                            userStatusTv.setText("Online");
                            userStatusTv.setTextColor(Color.WHITE);

                        } else {
                            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                            cal.setTimeInMillis(Long.parseLong(onlineStatus));
                            String dateTime = DateFormat.format("dd MMM hh:mm aa", cal).toString();
                            userStatusTv.setText("Last seen on " + dateTime);
                            userStatusTv.setTextColor(Color.LTGRAY);
                        }
                    }

                    try {
                        Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.acc).into(profileIv);
                    }
                    catch (Exception e){
                        Picasso.get().load(R.drawable.acc).networkPolicy(NetworkPolicy.OFFLINE).into(profileIv);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        messageEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {

                if (s.toString().trim().length() == 0){
                    checkTypingStatus("noOne");
                }
                else {
                    checkTypingStatus(hisUid);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });





        FirebaseAuth firebaseAuth;
        String myUid;
        firebaseAuth = FirebaseAuth.getInstance();
        myUid = firebaseAuth.getUid();
        more.setImageResource(R.drawable.ic_baseline_check_circle_outline_24);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBlocked){
                    unblock();
                }else {
                    block();
                }
            }
        });////////////////////////////
        readMessages();
        seenMessages();
        checkIsBlocked();
        imBlockedorNot();

    }
    private void imBlockedorNot(){
        //he blocked me or not
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(" ");
        ref.child(hisUid).child("BlockedUsers").orderByChild("uid").equalTo(myUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            if(ds.exists()){
                                Toast.makeText(ChatActivity.this, "You are blocked by the user", Toast.LENGTH_SHORT).show();
                                linearLayout.setVisibility(GONE);
                                return;
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void checkIsBlocked() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User");
        ref.child(firebaseAuth.getUid()).child("BlockedUsers").orderByChild("uid").equalTo(hisUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            if(ds.exists()){
                                more.setImageResource(R.drawable.ic_baseline_block_24);
                                isBlocked = true;
                                linearLayout.setVisibility(GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void block() {
        //block by adding uid to current users blockedusers node
        HashMap < String, String > hashMap = new HashMap<>();
        hashMap.put("uid",hisUid);
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setTitle("Block User").setMessage("Are you sure you want to block").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User");
                        ref.child(myUid).child("BlockedUsers").child(hisUid).setValue(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        //blocked
                                        Toast.makeText(ChatActivity.this,"User Blocked",Toast.LENGTH_SHORT).show();
                                        more.setImageResource(R.drawable.ic_baseline_block_24);
                                        linearLayout.setVisibility(GONE);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ChatActivity.this,"Failed"+e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).setNegativeButton("No", null);
        builder.create();
        builder.show();




    }

    private void unblock() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setTitle("Unblock User").setMessage("Are you sure you want to unblock").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User");
                ref.child(myUid).child("BlockedUsers").orderByChild("uid").equalTo(hisUid)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds : snapshot.getChildren()){
                                    if (ds.exists()){
                                        ds.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(ChatActivity.this,"User Unblocked",Toast.LENGTH_SHORT).show();
                                                more.setImageResource(R.drawable.ic_baseline_check_circle_outline_24);
                                                linearLayout.setVisibility(View.VISIBLE);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(ChatActivity.this,"Failed"+e.getMessage(),Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        }).setNegativeButton("No",null);
        builder.create();
        builder.show();

    }

    private void createDialog() {

        LayoutInflater inflater = LayoutInflater.from(ChatActivity.this);
        View view = inflater.inflate(R.layout.record_layout, null);

        TextView start = view.findViewById(R.id.btn_start_rec);
        TextView stop = view.findViewById(R.id.btn_stop_rec);
        TextView send = view.findViewById(R.id.btn_send_rec);
        TextView status = view.findViewById(R.id.status);

        AlertDialog alertDialog = new AlertDialog.Builder(ChatActivity.this)
                .setView(view)
                .create();

        alertDialog.show();
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop.setVisibility(View.VISIBLE);
                start.setVisibility(GONE);
                send.setVisibility(GONE);
                status.setText("Audio is recording..");
                try {
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                } catch ( IOException e) {
                    e.printStackTrace();
                }
              //  Toast.makeText(ChatActivity.this,"Audio Recording...",Toast.LENGTH_SHORT).show();
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send.setVisibility(View.VISIBLE);
                stop.setVisibility(GONE);
                start.setVisibility(GONE);

                if (mediaRecorder != null){
                    mediaRecorder.stop();
                    mediaRecorder.reset();
                    mediaRecorder.release();
                    status.setText("Recording Stopped");
                 //   Toast.makeText(ChatActivity.this,"Recording Stopped",Toast.LENGTH_SHORT).show();
                }
                else {Toast.makeText(ChatActivity.this,"Recording null......",Toast.LENGTH_SHORT).show();
                start.setVisibility(View.VISIBLE);
                stop.setVisibility(GONE);
                send.setVisibility(GONE);
                status.setText("Record Audio");
            }}
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send.setVisibility(GONE);
                status.setText("Sending Audio");
                progressBar.setVisibility(View.VISIBLE);
                Uri audiofile = Uri.fromFile(new File(file));
                StorageReference storageReference = FirebaseStorage.getInstance().getReference("Audio files");
                final StorageReference reference = storageReference.child(System.currentTimeMillis() + filename);
                uploadTask = reference.putFile(audiofile);

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        return reference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference();
                            String timestamp = String.valueOf(System.currentTimeMillis());

                            HashMap<String , Object> hashMapone = new HashMap<>();
                            hashMapone.put("Sender",myUid);
                            hashMapone.put("receiver",hisUid);
                            hashMapone.put("message",downloadUri.toString());
                            hashMapone.put("timestamp",timestamp);
                            hashMapone.put("type","a");
                            hashMapone.put("isSeen",false);
                            databaseReference2.child("Chats").push().setValue(hashMapone);

                            DatabaseReference database = FirebaseDatabase.getInstance().getReference("User").child(myUid);
                            database.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    UserData userData = snapshot.getValue(UserData.class);
                                    if (userData != null){
                                        String Name = userData.name;
                                        sendNotification(hisUid,Name);
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    alertDialog.dismiss();
                                    Toast.makeText(ChatActivity.this, "file sent", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(GONE);
                                }
                            },1000); }
                    }
                });

                DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("ChatList")
                        .child(myUid)
                        .child(hisUid);
                chatRef1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatRef1.child("id").setValue(hisUid);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("ChatList")
                        .child(hisUid)
                        .child(myUid);

                chatRef2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            chatRef2.child("id").setValue(myUid);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == PICK_IMAGE || resultCode == RESULT_OK ||
                data != null || data.getData() != null) {
            uri = data.getData();

            try {
                Bitmap imagebitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                String filename = String.format("%d.jpg",System.currentTimeMillis());
                File finalfile = new File(path,filename);
                FileOutputStream fileOutputStream = new FileOutputStream(finalfile);
                imagebitmap.compress(Bitmap.CompressFormat.JPEG,20,fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                pic = Uri.fromFile(finalfile);

                String url = pic.toString();
                Intent intent = new Intent(ChatActivity.this,SendImage.class);
                intent.putExtra("u",url);
                intent.putExtra("n",userName);
                intent.putExtra("ruid",hisUid);
                intent.putExtra("suid",myUid);
                startActivity(intent);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }

    }


    private void seenMessages() {
        userRefForSeen = FirebaseDatabase.getInstance().getReference("Chats");
        valueEventListener = userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if (chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid)){
                        HashMap<String , Object> hassSeenHarshMap = new HashMap<>();
                        hassSeenHarshMap.put("isSeen",true);
                        ds.getRef().updateChildren(hassSeenHarshMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void readMessages() {
        chatList = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if (chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid) ||
                            chat.getReceiver().equals(hisUid) && chat.getSender().equals(myUid) ){
                        chatList.add(chat);
                    }
                    adapterChat = new AdapterChat(ChatActivity.this,chatList);
                    adapterChat.notifyDataSetChanged();
                    recyclerView.setAdapter(adapterChat);
                    recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void sendMessage(String message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String timestamp = String.valueOf(System.currentTimeMillis());

        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("Sender",myUid);
        hashMap.put("receiver",hisUid);
        hashMap.put("message",message);
        hashMap.put("timestamp",timestamp);
        hashMap.put("type","t");
        hashMap.put("isSeen",false);
        databaseReference.child("Chats").push().setValue(hashMap);

        DatabaseReference database = FirebaseDatabase.getInstance().getReference("User").child(myUid);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserData userData = snapshot.getValue(UserData.class);
                if (userData != null){
                    String Name = userData.name;
                    sendNotification(hisUid,Name,message);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(myUid)
                .child(hisUid);
        chatRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    chatRef1.child("id").setValue(hisUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(hisUid)
                .child(myUid);

        chatRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    chatRef2.child("id").setValue(myUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

      }


    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){
            myUid = user.getUid();

        }
        else {
            startActivity(new Intent(this,Starting.class));
            finish();
        }
    }
    private void checkOnlineStatus(String status){
        DatabaseReference bdRef = FirebaseDatabase.getInstance().getReference("User").child(myUid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus",status);
        //update value of onlineStatus
        bdRef.updateChildren(hashMap);

    }
    private void checkTypingStatus(String typing){
        DatabaseReference bdRef = FirebaseDatabase.getInstance().getReference("User").child(myUid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("typingTo",typing);
        //update value of onlineStatus
        bdRef.updateChildren(hashMap);

    }



    private void sendNotification(String hisUid, String receiver_name, String message){

        FirebaseDatabase.getInstance().getReference().child("Tokens").child(hisUid).child("token")
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
                        new FcmNotificationSender(usertoken,"TAS",receiver_name+": " + message,
                                getApplicationContext(),ChatActivity.this);

                notificationsSender.SendNotifications();

            }
        },1000);

    }
    private void sendNotification(String hisUid, String name) {
        FirebaseDatabase.getInstance().getReference().child("Tokens").child(hisUid).child("token")
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
                        new FcmNotificationSender(usertoken,"TAS",name +" sent you an audio",
                                getApplicationContext(),ChatActivity.this);

                notificationsSender.SendNotifications();

            }
        },1000);
    }



    @Override
    protected void onStart() {
        checkUserStatus();
        checkOnlineStatus("online");
        checkIsBlocked();
        imBlockedorNot();
        super.onStart();

    }
    @Override
    protected void onPause() {
        super.onPause();
        //lastseen
        String timestamp = String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(timestamp);
        checkTypingStatus("noOne");
        userRefForSeen.removeEventListener(valueEventListener);
    }
    @Override
    protected void onResume() {
        checkOnlineStatus("online");
        imBlockedorNot();
        checkIsBlocked();
        super.onResume();
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