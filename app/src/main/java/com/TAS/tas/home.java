package com.TAS.tas;

import static android.os.Environment.DIRECTORY_PICTURES;
import static android.view.View.GONE;

import android.Manifest;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.Handler;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
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
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class home extends Fragment implements View.OnClickListener {
    TextView createpost;
    CircleImageView circleImageView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference  databaseReference;
    RecyclerView recyclerView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference,likeref,storyRef,likelist,referenceDel,ntRef,db1,db2,db3;
    Boolean likechecker = false;
    NewMember newMember;
    String name_result,url_result,uid_result,usertoken;

    public home() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_posts);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        LinearLayoutManager linearLayoutManager =  new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        reference = database.getReference("All posts");
        reference.keepSynced(true);
        likeref = database.getReference("post likes");
        likeref.keepSynced(true);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("User");
        db1 = database.getReference("All images").child(currentuid);
        db3 = database.getReference("All posts");
        db1.keepSynced(true);
        databaseReference.keepSynced(true);

        newMember = new NewMember();

        createpost = view.findViewById(R.id.createPost);
        createpost.setOnClickListener(this);


        circleImageView = view.findViewById(R.id.imageView9);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),EditProfile.class));
            }
        });
        //  documentReference = db.collection("user").document(currentuid);
        databaseReference.child(currentuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserData userprofile = snapshot.getValue(UserData.class);
                if (userprofile != null) {
                    String url = userprofile.image;
                    try {
                        Picasso.get().load(url).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.pict).into(circleImageView);
                    }
                    catch (Exception e){
                        Picasso.get().load(R.drawable.pict).networkPolicy(NetworkPolicy.OFFLINE).into(circleImageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.createPost:
                boolean isFirstRun = this.getActivity().getSharedPreferences("PREFERENCE",Context.MODE_PRIVATE).getBoolean("isFirstRun",true);
                if (isFirstRun){
                    AlertDialog.Builder b = new AlertDialog.Builder(getContext());
                    b.setCancelable(false);
                    b.setTitle("Privacy Policy");
                    b.setMessage(Html.fromHtml("Do you accept our privacy policy?"));
                    b.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    b.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getContext().getSharedPreferences("PREFERENCE",Context.MODE_PRIVATE).edit().putBoolean("isFirstRun",false).apply();
                            Intent intent = new Intent(getActivity(),PostActivity.class);
                            startActivity(intent);
                        }
                    });
                    b.setNeutralButton("View privacy policy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent ii = new Intent(getActivity(),PrivacyPolicy.class);
                            startActivity(ii);
                        }
                    });
                    AlertDialog a = b.create();
                    a.show();
                    //((TextView)a.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
                }
                else startActivity(new Intent(getActivity(),PostActivity.class));
                break;
        }

    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuiddd = user.getUid();
        databaseReference.child(currentuiddd).addListenerForSingleValueEvent(new ValueEventListener() {
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
        FirebaseRecyclerOptions<PostMember> options = new FirebaseRecyclerOptions.Builder<PostMember>().setQuery(reference,PostMember.class).build();
        FirebaseRecyclerAdapter<PostMember, AdapterPost> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<PostMember, AdapterPost>(options) {
                    @Override protected void onBindViewHolder(@NonNull AdapterPost holder, int position, @NonNull final PostMember model) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        final String currentUserid = user.getUid();
                        final  String postkey = getRef(position).getKey();

                        holder.SetPost(getActivity(),model.getName(),model.getUrl(),model.getPostUri(),model.getTime(),model.getUid(),model.getType(),model.getDesc());
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
                        holder.imageViewprofile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getActivity(),ShowUserProfile.class);
                                intent.putExtra("userId",userid);
                                getActivity().startActivity(intent);
                            }
                        });
                        holder.tv_nameprofile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getActivity(),ShowUserProfile.class);
                                intent.putExtra("userId",userid);
                                getActivity().startActivity(intent);
                            }
                        });
                        holder.tv_time.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getActivity(),ShowUserProfile.class);
                                intent.putExtra("userId",userid);
                                getActivity().startActivity(intent);
                            }
                        });
                        if (type.equals("iv")){
                            holder.iv_post.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getActivity(),ImageviewForPostpic.class);
                                    intent.putExtra("url",url);
                                    getActivity().startActivity(intent);
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
                                           //     ntRef.child(currentUserid+"l").removeValue();
                                                likechecker = false;
                                            }else {

                                                likeref.child(postkey).child(currentUserid).setValue(true);

                                                newMember.setName(name_result);
                                                newMember.setUid(currentUserid);
                                                newMember.setUrl(url_result);
                                                newMember.setSeen("no");
                                                newMember.setText("Liked Your Post ");
                                                ntRef = database.getReference("notification").child(userid);
//                                                String key = ntRef.push().getKey();
//                                                ntRef.child(key).setValue(newMember);
                                                String id = ntRef.child(currentUserid+"l").push().getKey();
                                                ntRef.child(id).setValue(newMember);

//
                                              sendNotification(userid,name_result);
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
                                Intent intent = new Intent(getActivity(),CommentsActivity.class);
                                intent.putExtra("postkey",postkey);
                                intent.putExtra("name",name);
                                intent.putExtra("url",url);
                                intent.putExtra("uid",userid);
                                startActivity(intent);
                            }
                        });
                        holder.tv_comment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getActivity(),CommentsActivity.class);
                                intent.putExtra("postkey",postkey);
                                intent.putExtra("name",name);
                                intent.putExtra("url",url);
                                intent.putExtra("uid",userid);
                                startActivity(intent);
                            }
                        });
                        holder.tv_share.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String sharetext = name +" shared a picture in TAS app\nClick to view the picture\n" + url+"\n"+"\n"+
                                        "Download the app from playstore \nlink- https://play.google.com/store/apps/details?id=com.TAS.tas";
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra(Intent.EXTRA_TEXT,sharetext);
                                intent.setType("text/plain");
                                startActivity(intent.createChooser(intent,"share via"));
                            }
                        });
                        holder.sharebtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String sharetext = name +" shared a picture in TAS app\nClick to view the picture\n" + url+"\n"+"\n"+
                                        "Download the app from playstore \nlink- https://play.google.com/store/apps/details?id=com.TAS.tas";
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra(Intent.EXTRA_TEXT,sharetext);
                                intent.setType("text/plain");
                                startActivity(intent.createChooser(intent,"share via"));
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
        firebaseRecyclerAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void showDialog(String name, String url, String time, String userid,String postkey) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.post_options);

        TextView download = dialog.findViewById(R.id.download_tv_post);
        TextView share = dialog.findViewById(R.id.share_tv_post);
        TextView delete = dialog.findViewById(R.id.delete_tv_post);
        TextView copyurl = dialog.findViewById(R.id.copyurl_tv_post);
        TextView edit = dialog.findViewById(R.id.edit_post);
        TextView report = dialog.findViewById(R.id.report_tv_post);
        TextView block = dialog.findViewById(R.id.block_tv_post);
        EditText captionEt = dialog.findViewById(R.id.et_caption);
        Button button = dialog.findViewById(R.id.btn_edit_caption);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserid = user.getUid();

        if (userid.equals(currentUserid)){
            delete.setVisibility(View.VISIBLE);
            edit.setVisibility(View.VISIBLE);
            report.setVisibility(View.GONE);
            block.setVisibility(View.GONE);
        }else {
            delete.setVisibility(View.GONE);
            edit.setVisibility(View.GONE);
            report.setVisibility(View.VISIBLE);
            block.setVisibility(View.VISIBLE);
        }
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newMember.setName(name_result);
                newMember.setUid(currentUserid);
                newMember.setUrl(url_result);
                newMember.setSeen("no");
                newMember.setText("Report Your Post. You should remove it if there is any offensive picture");
                ntRef = database.getReference("notification").child(userid);
                String id = ntRef.child(currentUserid+"l").push().getKey();
                ntRef.child(id).setValue(newMember);
                DatabaseReference db = database.getReference("Report Post").child(userid);
                db.child(id).setValue(newMember);
                Toast.makeText(getContext(), "Reported", Toast.LENGTH_SHORT).show();
                sendreportNotification(userid,name_result);
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
                                Toast.makeText(getContext(),"User Blocked",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                captionEt.setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        Map<String,Object> map = new HashMap<>();
                        map.put("desc",captionEt.getText().toString());

                        FirebaseDatabase.getInstance().getReference().child("All posts").child(postkey).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(getActivity(), "Updated", Toast.LENGTH_SHORT).show();
                                      //  dialog.dismiss();
                                    }
                                });

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String currentuid = user.getUid();
                        FirebaseDatabase.getInstance().getReference()
                                .child("All images").child(currentuid)
                                .child(postkey)
                                .updateChildren(map)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                       // Toast.makeText(getActivity(), "Updated", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                });


                    }
                });


            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Query query = db1.orderByChild("time").equalTo(time);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                            dataSnapshot1.getRef().removeValue();

                            Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                Query query3 = db3.orderByChild("time").equalTo(time);
                query3.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                            dataSnapshot1.getRef().removeValue();

                            Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
                            }
                        });

                dialog.dismiss();
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
                        DownloadManager manager = (DownloadManager)getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                        manager.enqueue(request);
                        Toast.makeText(getActivity(), "Downloading", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        Toast.makeText(getActivity(),"No Permissions",Toast.LENGTH_SHORT).show();
                    }
                };
                TedPermission.with(getActivity()).setPermissionListener(permissionListener).setPermissions(Manifest.permission.INTERNET,Manifest.permission.READ_EXTERNAL_STORAGE).check();
                }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String sharetext = name +" shared a picture in TAS app\nClick to view the picture\n" + url+"\n"+"\n"+
                        "Download the app from playstore \nlink- https://play.google.com/store/apps/details?id=com.TAS.tas";
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

                ClipboardManager cp = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("String",url);
                cp.setPrimaryClip(clip);
                clip.getDescription();
                Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();

                dialog.dismiss();

            }
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.Bottomanim;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    private void sendNotification(String userid, String name_result){

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
                                getContext(),getActivity());

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
                                getContext(),getActivity());

                notificationsSender.SendNotifications();

            }
        },1000);

    }
}