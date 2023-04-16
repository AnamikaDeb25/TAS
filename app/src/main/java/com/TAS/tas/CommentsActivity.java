package com.TAS.tas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class CommentsActivity extends AppCompatActivity {


    ImageView usernameImageview;
    TextView usernameTextview;
    ImageView commentsBtn;
    EditText commentsEdittext;
    String url,name,post_key,userid,bundleuid;
    DatabaseReference Commentref,userCommentref,likesref,ntref;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    String name_result,Url,uid,email_result,usertoken;
    RecyclerView recyclerView;
    Boolean likeChecker = false;
    ConstraintLayout constraintLayout;
    NewMember newMember;

    CommentsMember commentsMember;

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
        setContentView(R.layout.activity_comments);

        commentsMember = new CommentsMember();


        constraintLayout = findViewById(R.id.cl);
        recyclerView = findViewById(R.id.recycler_view_comments);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);


        commentsBtn = findViewById(R.id.btn_comments);
        usernameImageview = findViewById(R.id.imageviewUser_comment);
        usernameTextview = findViewById(R.id.name_comments_tv);
        commentsEdittext = findViewById(R.id.et_comments);
        Bundle extras = getIntent().getExtras();

        if (extras != null){
            url = extras.getString("url");
            name = extras.getString("name");
            post_key = extras.getString("postkey");
            bundleuid = extras.getString("uid");
        }else {

        }


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userid = user.getUid();
        Commentref = database.getReference("All posts").child(post_key).child("Comments");
        Commentref.keepSynced(true);

        likesref = database.getReference("comment likes");
        likesref.keepSynced(true);
        userCommentref = database.getReference("User Posts").child(userid);

        newMember = new NewMember();
        ntref = database.getReference("notification").child(bundleuid);

        commentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkConnected()){
                    comment();
                }
                else {
                    showSnakbar();
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkOnlineStatus("online");
        Picasso.get().load(url).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.pict).into(usernameImageview);
        usernameTextview.setText(name);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("User").child(userid);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserData userprofile = snapshot.getValue(UserData.class);
                if (userprofile != null){
                    name_result = userprofile.name;
                    String School = userprofile.school;
                    email_result = userprofile.email;
                    String Phn = userprofile.phno;
                    Url = userprofile.getImage();
                    uid = userprofile.getUid();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseRecyclerOptions<CommentsMember> options = new FirebaseRecyclerOptions.Builder<CommentsMember>().setQuery(Commentref,CommentsMember.class).build();

        FirebaseRecyclerAdapter<CommentsMember,CommentsViewholder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<CommentsMember, CommentsViewholder>(options) {
                    @NonNull
                    @Override
                    protected void onBindViewHolder(@NonNull CommentsViewholder holder, int position, @NonNull CommentsMember model) {


                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String currentUserId = user.getUid();
                        final String postkey = getRef(position).getKey();
                        String time = getItem(position).getTime();

                        holder.setComment(getApplication(),model.getComment(),model.getTime(),model.getUrl(),model.getUsername(),model.getUid());
                        String uiid = model.getUid();
                        if (currentUserId.equals(uiid)){
                            holder.delete.setVisibility(View.VISIBLE);
                        }
                        else {
                            holder.delete.setVisibility(View.GONE);
                        }
                        holder.LikeChecker(postkey);

//                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                UserData userprofile = snapshot.getValue(UserData.class);
//                                if (userprofile != null){
//                                    String muid = userprofile.getUid();
//                                    if (muid.equals(currentUserId)){
//                                        holder.delete.setVisibility(View.VISIBLE);
//                                    }
//                                    else {
//                                        holder.delete.setVisibility(View.GONE);
//                                    }
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });
                        holder.delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(CommentsActivity.this);
                                builder.setTitle("Are you sure you want to delete the comment");
                                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Query query = Commentref.orderByChild("time").equalTo(time);
                                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()){

                                                    dataSnapshot1.getRef().removeValue();
                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }

                                        });
                                    }
                                });
                                builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                });
                                builder.create().show();


                            }
                        });
                        holder.likebutton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                likeChecker = true;

                                likesref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if (likeChecker.equals(true)){
                                            if (snapshot.child(postkey).hasChild(currentUserId)){
                                                likesref.child(postkey).child(currentUserId).removeValue();
                                                likeChecker = false;

                                            }else {
                                                likesref.child(postkey).child(currentUserId).setValue(true);
                                                likeChecker = false;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public CommentsViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_item,parent,false);

                        return new CommentsViewholder(view);
                    }
                };
        firebaseRecyclerAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }


    private void comment() {

        Calendar callfordate = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("dd MMM yy");
        final  String savedate = currentdate.format(callfordate.getTime());


        Calendar callfortime = Calendar.getInstance();
        SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:ss");
        final  String savetime = currenttime.format(callfortime.getTime());

        String time = savedate+" "+savetime;
        String comment = commentsEdittext.getText().toString();
        if (comment != null){

            commentsMember.setComment(comment);
            commentsMember.setUsername(name_result);
            commentsMember.setUid(uid);
            commentsMember.setTime(time);
            commentsMember.setUrl(Url);

            String pushkey = Commentref.push().getKey();
            Commentref.child(pushkey).setValue(commentsMember);

            commentsEdittext.setText("");
            newMember.setName(name_result);
            newMember.setUid(userid);
            newMember.setUrl(Url);
            newMember.setSeen("no");
            newMember.setText("Commented on your post: " + comment);

            String key = ntref.push().getKey();
            ntref.child(key).setValue(newMember);
            sendNotification(bundleuid,name_result,comment);

        }else {
            Toast.makeText(this, "Please write comment", Toast.LENGTH_SHORT).show();
        }

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


    private void sendNotification(String bundleuid,String name_result, String comment){

        FirebaseDatabase.getInstance().getReference().child("Tokens").child(bundleuid).child("token").addListenerForSingleValueEvent(new ValueEventListener() {
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
                        new FcmNotificationSender(usertoken,"TAS",name_result+" Commented on your post: "+comment,
                                getApplicationContext(),CommentsActivity.this);

                notificationsSender.SendNotifications();

            }
        },2000);

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
}