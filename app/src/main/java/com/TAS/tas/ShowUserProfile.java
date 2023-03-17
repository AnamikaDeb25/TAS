package com.TAS.tas;

import static android.os.Environment.DIRECTORY_PICTURES;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShowUserProfile extends AppCompatActivity {

    CircleImageView img;
    ProgressDialog pd;
    NewMember newMember;

    String name_result,url_result,uid_result;
    FirebaseDatabase database;
    DatabaseReference postnoref,followernoref;
    int postNo,folloeNo ;
    DatabaseReference databaseReference,databaseReference1,databaseReference2;


    DatabaseReference reference;
    RecyclerView recyclerView;
    Boolean likechecker = false;
    String currentuid;
    String Name , School , Email , Phn , link,usertoken ;

    TextView button,followers_tv,posts_tv;

    RequestMember requestMember;

    TextView nname, eemail,pphone,ssname,pics,postno,follow,followno,msg;
    DatabaseReference likeref,likelist,referenceDel,ntref,ntref2,db1,db3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user_profile);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();
        Intent intent = getIntent();
        currentuid = intent.getStringExtra("userId");
       newMember = new NewMember();
        database = FirebaseDatabase.getInstance();

        recyclerView = (RecyclerView) findViewById(R.id.rview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager =  new LinearLayoutManager(ShowUserProfile.this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        requestMember = new RequestMember();

        reference = database.getReference("All images").child(currentuid);
        likeref = database.getReference("post likes");

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("User");
        ntref = database.getReference("notification").child(currentuid);
        reference.keepSynced(true);
        db1 = database.getReference("All images").child(currentuid);
        db1.keepSynced(true);
        db3 = database.getReference("All posts");
        db3.keepSynced(true);
        databaseReference1 = database.getReference("followers").child(currentuid);
        databaseReference2  = database.getReference("followers");
        ntref2 = database.getReference("notification").child(currentuid);
        img = findViewById(R.id.dp);
        nname = findViewById(R.id.nname);
        ssname = findViewById(R.id.ssname);
        eemail = findViewById(R.id.eeame);
//        pphone = findViewById(R.id.pphone);
        pics = findViewById(R.id.pics);
        postno = findViewById(R.id.pstno);
        followno = findViewById(R.id.flwno);
        follow = findViewById(R.id.follow);
        msg =findViewById(R.id.msg);
        followers_tv = findViewById(R.id.flwer);
        ImageView back;
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowUserProfile.this,ImageViewofprofilepic.class);
                intent.putExtra("uid",currentuid);
                startActivity(intent);
            }
        });

        postnoref = database.getReference("All images").child(currentuid);
        followernoref = database.getReference("followers").child(currentuid);
        postnoref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postNo = (int)snapshot.getChildrenCount();
                postno.setText(Integer.toString(postNo));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        followernoref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                folloeNo = (int)snapshot.getChildrenCount();
                followno.setText(Integer.toString(folloeNo));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = follow.getText().toString();
                if (status.equals("Follow")){
                    follow();
                }
                else if (status.equals("Following")){
                    unFollow();
                }

            }
        });
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(currentuid).hasChild(currentUserId)){
                    follow.setText("Following");
                    follow.setBackground(getResources().getDrawable(R.drawable.btnbg));
                }else {
                    follow.setText("Follow");
                    follow.setBackground(getResources().getDrawable(R.drawable.btnbgsky));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth firebaseAuth;
                String myUid;
                firebaseAuth = FirebaseAuth.getInstance();
                myUid = firebaseAuth.getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User");
                ref.child(currentuid).child("BlockedUsers").orderByChild("uid").equalTo(myUid)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()){
                                    if(ds.exists()){
                                        Toast.makeText(ShowUserProfile.this, "You are blocked by the user", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                                databaseReference.child(currentuid).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        UserData userprofile = snapshot.getValue(UserData.class);
                                        if (userprofile != null){
                                            Name = userprofile.name;
                                            Intent intent = new Intent(ShowUserProfile.this,ChatActivity.class);
                                            intent.putExtra("hisUid",currentuid);
                                            intent.putExtra("userName",Name);
                                            startActivity(intent);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });




            }
        });

        databaseReference.child(currentuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserData userprofile = snapshot.getValue(UserData.class);
                if (userprofile != null){
                     Name = userprofile.name;
                     School = userprofile.school;
                     Email = userprofile.email;
                     Phn = userprofile.phno;
                     link = userprofile.getImage();
                    Picasso.get().load(link).placeholder(R.drawable.pict).into(img);
                    nname.setText(Name);
                    eemail.setText(Email);
                  //  pphone.setText("Mobile: "+Phn);
                    if (userprofile.getIsstu().equals("T")){
                        ssname.setVisibility(View.GONE);
                    }
                    else {
                        ssname.setText("School: "+School);
                    }

                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}});

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

    @Override
    public void onStart() {
        checkOnlineStatus("online");
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUidd = user.getUid();
        FirebaseDatabase firebaseDatabase1 = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference11 = firebaseDatabase1.getReference("User");

        databaseReference11.child(currentUidd).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserData userprofile = snapshot.getValue(UserData.class);
                if (userprofile != null) {
                    url_result = userprofile.image;
                    name_result = userprofile.name;
                    uid_result = userprofile.uid;

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseRecyclerOptions<PostMember> options =
                new FirebaseRecyclerOptions.Builder<PostMember>()
                        .setQuery(reference,PostMember.class).build();

        FirebaseRecyclerAdapter<PostMember, AdapterPost> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<PostMember, AdapterPost>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdapterPost holder, int position, @NonNull final PostMember model) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        final String currentUserid = user.getUid();
                        final  String postkey = getRef(position).getKey();

                        holder.SetPost(ShowUserProfile.this,model.getName(),model.getUrl(),model.getPostUri(),model.getTime(),model.getUid(),model.getType(),model.getDesc());
                        final String url = getItem(position).getPostUri();
                        final String name = getItem(position).getName();
                        //   final String url = getItem(position).getUrl();
                        final  String time = getItem(position).getTime();
                        final String type = getItem(position).getType();
                        final String userid = getItem(position).getUid();

                        holder.likeschecker(postkey);
                        holder.commentchecker(postkey);

                        holder.menuoptions.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showDialog(name,url,time,userid,postkey);
                            }
                        });
//
//
                        if (type.equals("iv")){
                            holder.iv_post.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(ShowUserProfile.this,ImageviewForPostpic.class);
                                    intent.putExtra("url",url);
                                    startActivity(intent);
                                }
                            });
                        }
                        holder.likebtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                likechecker = true;
                                likeref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (likechecker.equals(true)){
                                            if (snapshot.child(postkey).hasChild(currentUserid)){
                                                likeref.child(postkey).child(currentUserid).removeValue();
                                                likelist = database.getReference("like list").child(postkey).child(currentUserid);
                                                likelist.removeValue();
                                                ntref.child(currentUserid+"l").removeValue();

                                                likechecker = false;
                                            }else {

                                                likeref.child(postkey).child(currentUserid).setValue(true);
                                                newMember.setName(name);
                                                newMember.setUid(currentUserid);
                                                newMember.setUrl(url_result);
                                                newMember.setSeen("no");
                                                newMember.setText("Liked Your Post ");

                                                ntref.child(currentUserid+"l").setValue(newMember);
                                                sendNotification(userid,name_result);
//                                                sendNotification(userid,name_result);
                                                likechecker= false;


                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }

                                });

                            }
                        });

                        holder.commentbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(ShowUserProfile.this,CommentsActivity.class);
                                intent.putExtra("postkey",postkey);
                                intent.putExtra("name",name);
                                intent.putExtra("url",url);
                                intent.putExtra("uid",userid);
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public AdapterPost onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.post_layout,parent,false);

                        return new AdapterPost(view);



                    }
                };
        firebaseRecyclerAdapter.startListening();

        recyclerView.setAdapter(firebaseRecyclerAdapter);



    }
    private void showDialog(String name, String url, String time, String userid,String postkey) {
        final Dialog dialog = new Dialog(ShowUserProfile.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.post_options);

        TextView download = dialog.findViewById(R.id.download_tv_post);
        TextView share = dialog.findViewById(R.id.share_tv_post);
        TextView delete = dialog.findViewById(R.id.delete_tv_post);
        TextView copyurl = dialog.findViewById(R.id.copyurl_tv_post);
        TextView edit = dialog.findViewById(R.id.edit_post);
        TextView report = dialog.findViewById(R.id.report_tv_post);
        TextView block = dialog.findViewById(R.id.block_tv_post);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserid = user.getUid();


            delete.setVisibility(View.GONE);
            edit.setVisibility(View.GONE);

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ntRef;
                newMember.setName(name_result);
                newMember.setUid(currentUserid);
                newMember.setUrl(url_result);
                newMember.setSeen("no");
                newMember.setText("Report Your Post. You should remove it if there is any offensive picture");
                ntRef = database.getReference("notification").child(userid);
                String id = ntRef.child(currentUserid+"l").push().getKey();
                ntRef.child(id).setValue(newMember);
                Toast.makeText(ShowUserProfile.this, "Reported", Toast.LENGTH_SHORT).show();
                sendreportNotification(userid,name_result);
                DatabaseReference db = database.getReference("Report Post").child(userid);
                db.child(id).setValue(newMember);
                dialog.dismiss();
            }
        });
        block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap < String, String > hashMap = new HashMap<>();
                hashMap.put("uid",userid);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User");
                ref.child(currentUserid).child("BlockedUsers").child(userid).setValue(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                //blocked
                                Toast.makeText(ShowUserProfile.this,"User Blocked",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ShowUserProfile.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionListener permissionListener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        File direct =
                                new File(Environment
                                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                                        .getAbsolutePath() + "/" + "TAS" + "/");


                        if (!direct.exists()) {
                            direct.mkdir();
                            //  Toast.makeText(ImageViewActivity.this,"dir created for first time",Toast.LENGTH_SHORT).show();
                        }
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                        request.setTitle("Download");
                        request.setDescription("Downloading image....");
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalPublicDir(DIRECTORY_PICTURES,"TAS"+File.separator+name+System.currentTimeMillis() + ".jpg");
                        //   request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,name+System.currentTimeMillis() + ".jpg");
                        DownloadManager manager = (DownloadManager)ShowUserProfile.this.getSystemService(Context.DOWNLOAD_SERVICE);
                        manager.enqueue(request);
                        Toast.makeText(ShowUserProfile.this, "Downloading", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        Toast.makeText(ShowUserProfile.this,"No Permissions",Toast.LENGTH_SHORT).show();
                    }
                };
                TedPermission.with(ShowUserProfile.this).setPermissionListener(permissionListener).setPermissions(Manifest.permission.INTERNET,Manifest.permission.READ_EXTERNAL_STORAGE).check();
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String sharetext = name +"\n" +"\n"+ url;
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Intent.EXTRA_TEXT,sharetext);
                intent.setType("text/plain");
                startActivity(intent.createChooser(intent,"share via"));

                dialog.dismiss();

            }
        });
        copyurl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ClipboardManager cp = (ClipboardManager)ShowUserProfile.this.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("String",url);
                cp.setPrimaryClip(clip);
                clip.getDescription();
                Toast.makeText(ShowUserProfile.this, "", Toast.LENGTH_SHORT).show();

                dialog.dismiss();

            }
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.Bottomanim;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }



    void follow() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUidd = user.getUid();
        FirebaseDatabase firebaseDatabas = FirebaseDatabase.getInstance();
        DatabaseReference databaseReferen = firebaseDatabas.getReference("User");

        databaseReferen.child(currentUidd).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserData userprofile = snapshot.getValue(UserData.class);
                if (userprofile != null) {
                    url_result = userprofile.image;
                    name_result = userprofile.name;
                    uid_result = userprofile.uid;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
            follow.setText("Following");
            follow.setBackground(getResources().getDrawable(R.drawable.btnbg));

            requestMember.setUserid(user.getUid());
            requestMember.setUrl(link);
            requestMember.setName(name_result);
            databaseReference1.child(user.getUid()).setValue(requestMember);

        newMember.setName(name_result);
        newMember.setUid(uid_result);
        newMember.setUrl(url_result);
        newMember.setSeen("no");
        newMember.setText("Started Following you ");

        ntref2.child(user.getUid()+"f").setValue(newMember);


            sendNotification(currentuid,name_result);


    }


    private void unFollow() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();

        AlertDialog.Builder builder = new AlertDialog.Builder(ShowUserProfile.this);
        builder.setTitle("Unfollow")
                .setMessage("Are you sure you wantto Unfollow?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        databaseReference1.child(currentUserId).removeValue();
                        ntref2.child(currentUserId+"f").removeValue();
                        follow.setText("Follow");
                       // followers_tv.setText("0");
                        Toast.makeText(ShowUserProfile.this, "Unfollowed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.create();
        builder.show();
    }
    private void sendNotification(String userid,String name_result){

        FirebaseDatabase.getInstance().getReference().child("Tokens").child(userid).child("token").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                usertoken = snapshot.getValue().toString();
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
                        new FcmNotificationSender(usertoken,"TAS",name_result+" Started Following you",
                                getApplicationContext(),ShowUserProfile.this);

                notificationsSender.SendNotifications();

            }
        },2000);

    }

    private void sendNotificationlike(String userid, String name_result){

        FirebaseDatabase.getInstance().getReference().child("Tokens").child(userid).child("token")
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
                        new FcmNotificationSender(usertoken,"TAS",name_result+" Liked Your post ",
                                getApplicationContext(),ShowUserProfile.this);

                notificationsSender.SendNotifications();

            }
        },1000);

    }
    private void sendreportNotification(String userid, String name_result){

        FirebaseDatabase.getInstance().getReference().child("Tokens").child(userid).child("token")
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
                        new FcmNotificationSender(usertoken,"TAS",name_result+" Reported Your post ",
                                getApplicationContext(),ShowUserProfile.this);

                notificationsSender.SendNotifications();

            }
        },1000);

    }

}