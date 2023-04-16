package com.TAS.tas;

import static android.os.Environment.DIRECTORY_PICTURES;

import android.Manifest;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfilestu extends AppCompatActivity {


    //45.53min



    CircleImageView img;
    ImageView fab;
    ProgressDialog pd;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_REQUEST_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_REQUEST_CODE = 400;

    String cameraPermissions[];
    String StoragePermissions[];
    Uri image_uri,pic;
    FirebaseStorage storage;

    FirebaseDatabase database;
    DatabaseReference postnoref,fnoref;
    int postNo,fNo ;

    String name_result,url_result,uid_result,usertoken;

    StorageReference storageReference;
    String storagepath = "Users_profile_pic/";
    FirebaseUser user;
    String userId;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;


    DatabaseReference reference;
    RecyclerView recyclerView;
    Boolean likechecker = false;


    TextView nname, eemail,pphone,ssname,pics,postno,follower;
    DatabaseReference likeref,storyRef,likelist,referenceDel,ntref,db1,db2,db3;

    UserData usermember;
    NewMember newMember;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("User");
        databaseReference.keepSynced(true);
        database = FirebaseDatabase.getInstance();

        recyclerView = (RecyclerView) findViewById(R.id.rview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager =  new LinearLayoutManager(EditProfilestu.this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();
        reference = database.getReference("All images").child(currentuid);
        reference.keepSynced(true);
        likeref = database.getReference("post likes");
        likeref.keepSynced(true);
        db1 = database.getReference("All images").child(currentuid);
        db1.keepSynced(true);
        db3 = database.getReference("All posts");
        db3.keepSynced(true);
        newMember = new NewMember();
        ntref = database.getReference("notification").child(currentuid);





        cameraPermissions = new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        StoragePermissions = new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE};

        img = findViewById(R.id.dp);
        fab = findViewById(R.id.fab);
        pd = new ProgressDialog(EditProfilestu.this);
        nname = findViewById(R.id.nname);
        ssname = findViewById(R.id.ssname);
        eemail = findViewById(R.id.eeame);
        pphone = findViewById(R.id.pphone);
        pics = findViewById(R.id.pics);
        postno = findViewById(R.id.pstno);
        follower = findViewById(R.id.follower);
        userId = user.getUid();
        usermember = new UserData();
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
                Intent intent = new Intent(EditProfilestu.this,ImageViewofprofilepic.class);
                intent.putExtra("uid",currentuid);
                startActivity(intent);
            }
        });

        postnoref = database.getReference("All images").child(userId);
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
        fnoref = database.getReference("followers").child(userId);
        fnoref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fNo = (int)snapshot.getChildrenCount();
                follower.setText(Integer.toString(fNo));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserData userprofile = snapshot.getValue(UserData.class);
                if (userprofile != null){
                    String Name = userprofile.name;
                    String School = userprofile.school;
                    String Email = userprofile.email;
                    String Phn = userprofile.phno;
                    String link = userprofile.getImage();
                    Picasso.get().load(link).networkPolicy(NetworkPolicy.OFFLINE).fit().placeholder(R.drawable.pict).into(img);
                    nname.setText(Name);
                    eemail.setText(Email);
                    pphone.setText("Mobile: "+Phn);
                    ssname.setVisibility(View.GONE);
                   //
                  //  pp.setVisibility(View.GONE);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}});


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showditProfile();
            }
        });
    }

    private boolean checkStoragePermission(){
        boolean resul = ContextCompat.checkSelfPermission(EditProfilestu.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return resul;
    }
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(EditProfilestu.this,StoragePermissions,STORAGE_REQUEST_CODE);
    }
    private boolean checkCameraPermission(){
        boolean resul = ContextCompat.checkSelfPermission(EditProfilestu.this,Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean resul1 = ContextCompat.checkSelfPermission(EditProfilestu.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return resul && resul1;
    }
    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(EditProfilestu.this,cameraPermissions,CAMERA_REQUEST_CODE);
    }

private void showditProfile(){
    final Dialog dialog = new Dialog(EditProfilestu.this);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setContentView(R.layout.editprofiledialog);

    TextView edtpic = dialog.findViewById(R.id.edtpp);
    TextView editname = dialog.findViewById(R.id.edit_name);
    EditText name = dialog.findViewById(R.id.et_name);
    Button nn = dialog.findViewById(R.id.btn_edit_name);
    TextView editschool = dialog.findViewById(R.id.edit_postscl);
    EditText Schoolname = dialog.findViewById(R.id.et_scl);
    Button ss = dialog.findViewById(R.id.btn_edit_scl);
    TextView phn =  dialog.findViewById(R.id.edit_post_phn);
    phn.setVisibility(View.GONE);
    editschool.setVisibility(View.GONE);
    Schoolname.setVisibility(View.GONE);
    ss.setVisibility(View.GONE);

    databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            UserData userprofile = snapshot.getValue(UserData.class);
            if (userprofile != null){
                String Phnn = userprofile.phno;
                if (Phnn.equals(" ")){
                    phn.setVisibility(View.VISIBLE);
                    phn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(EditProfilestu.this,OtpVerificationStudent.class));
                        }
                    });
                }
            }

        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {}});


    edtpic.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            pd.setMessage("Uploading Profile Picture");
            showImagepicDialog();
        }
    });
    editname.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            pd.setMessage("Updating Name");
            name.setVisibility(View.VISIBLE);
            Schoolname.setVisibility(View.GONE);
            nn.setVisibility(View.VISIBLE);
            ss.setVisibility(View.GONE);
            nn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pd.show();
                    String value = name.getText().toString().trim();
                    if (value.isEmpty()){
                        name.setError("Name is required");
                        name.requestFocus();
                        pd.dismiss();
                    }
                    else {
                    pd.show();
                        DatabaseReference firebaseDatabase =   FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        firebaseDatabase.child("name").setValue(value);
                        pd.dismiss();
                        Toast.makeText(EditProfilestu.this,"Updated",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

                }}
            });



        }
    });

    dialog.show();
    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    dialog.getWindow().getAttributes().windowAnimations = R.style.Bottomanim;
    dialog.getWindow().setGravity(Gravity.BOTTOM);
}

    private void showNamePhoneUpdateDialog(String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfilestu.this);
        builder.setTitle("Update "+key);
        //set layout of dialog
        LinearLayout linearLayout = new LinearLayout (EditProfilestu.this);
        linearLayout.setOrientation(LinearLayout. VERTICAL);
        linearLayout.setPadding (10, 10,  10,  10);
        linearLayout.setBackgroundColor(Color.DKGRAY);
        EditText editText = new EditText (EditProfilestu.this);
        editText.setPadding(42,30,30,50);
        editText.setHint ("Enter "+key);
        editText.setTextColor(Color.WHITE);
        linearLayout.addView(editText);
        builder.setView(linearLayout);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value = editText.getText().toString().trim();
                pd.show();
                if (!TextUtils.isEmpty(value)){
                    DatabaseReference firebaseDatabase =   FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    firebaseDatabase.child(key).setValue(value);
                    //UserData userData = new UserData();

                    pd.dismiss();
                    }
                else {
                    pd.dismiss();
                    Toast.makeText(EditProfilestu.this,"Please enter "+key,Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
    private void showImagepicDialog() {
        String options[] = {"Camera","Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfilestu.this);
        builder.setTitle("Select image from");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    if (!checkCameraPermission()){
                        requestCameraPermission();
                    }
                    else {
                        pickFromCamera();
                    }
                }
                else if (which == 1){
                    if (!checkStoragePermission()){
                        requestStoragePermission();
                    }
                    else {
                        pickFromGallery();
                    }
                }
            }
        });
        builder.create().show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //this method called when user press allow or deny permission from request dialog

        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length > 0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted){
                        pickFromCamera();
                    }
                    else {
                        //permission denied
                        Toast.makeText(EditProfilestu.this,"Please enable camera & Storage Permission",Toast.LENGTH_LONG).show();
                    }
                }

            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length > 0){
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted){
                        pickFromGallery();
                    }
                    else {
                        //permission denied
                        Toast.makeText(EditProfilestu.this,"Please enable Storage Permission",Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //this method will be called after picking image from camera
        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_PICK_GALLERY_REQUEST_CODE){
                //image picked
                image_uri = data.getData();
                uploadProfilepic(image_uri);

            }
            if (requestCode == IMAGE_PICK_CAMERA_REQUEST_CODE){
                uploadProfilepic(image_uri);
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfilepic(Uri uri) {
        pd.show();
        String filepathandname = storagepath+" "+ user.getUid();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
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
        } catch (IOException e) {
            e.printStackTrace();
        }

        StorageReference storageReference2nd = storageReference.child(filepathandname);

        storageReference2nd.putFile(pic).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                Uri downloadUri = uriTask.getResult();

                if (uriTask.isSuccessful()){
                    HashMap<String , Object> results = new HashMap<>();
                    UserData userData = new UserData();
                    userData.setImage(downloadUri.toString());
                    results.put("image",downloadUri.toString());

                    databaseReference.child(user.getUid()).updateChildren(results)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    pd.dismiss();
                                    Toast.makeText(EditProfilestu.this,"Image updated",Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(EditProfilestu.this,"Something is wrong",Toast.LENGTH_SHORT).show();

                        }
                    });

                }
                else {
                    pd.dismiss();
                    Toast.makeText(EditProfilestu.this,"Something is wrong",Toast.LENGTH_SHORT).show();
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(EditProfilestu.this,e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void pickFromCamera() {
        //intent of picking image from device
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Temp pic");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Temp description");
        //put image uri
        image_uri = EditProfilestu.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        //intent to start camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_REQUEST_CODE);

    }
    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,IMAGE_PICK_GALLERY_REQUEST_CODE);
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
    public void onStart() {
        checkOnlineStatus("online");
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();
        databaseReference.child(currentuid).addListenerForSingleValueEvent(new ValueEventListener() {
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

                        holder.SetPost(EditProfilestu.this,model.getName(),model.getUrl(),model.getPostUri(),model.getTime(),model.getUid(),model.getType(),model.getDesc());
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
                                    Intent intent = new Intent(EditProfilestu.this,ImageviewForPostpic.class);
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
                                Intent intent = new Intent(EditProfilestu.this,CommentsActivity.class);
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
    @Override
    protected void onPause() {
        super.onPause();
        String timestamp = String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(timestamp);
    }

    private void showDialog(String name, String url, String time, String userid,String postkey) {
        final Dialog dialog = new Dialog(EditProfilestu.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.post_options);

        TextView download = dialog.findViewById(R.id.download_tv_post);
        TextView share = dialog.findViewById(R.id.share_tv_post);
        TextView delete = dialog.findViewById(R.id.delete_tv_post);
        TextView copyurl = dialog.findViewById(R.id.copyurl_tv_post);
        TextView edit = dialog.findViewById(R.id.edit_post);
        EditText captionEt = dialog.findViewById(R.id.et_caption);
        Button button = dialog.findViewById(R.id.btn_edit_caption);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserid = user.getUid();

        if (userid.equals(currentUserid)){
            delete.setVisibility(View.VISIBLE);
            edit.setVisibility(View.VISIBLE);
        }else {
            delete.setVisibility(View.GONE);
            edit.setVisibility(View.GONE);
        }
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

                        FirebaseDatabase.getInstance().getReference()
                                .child("All posts")
                                .child(postkey)
                                .updateChildren(map)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        Toast.makeText(EditProfilestu.this, "Updated", Toast.LENGTH_SHORT).show();
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

                            Toast.makeText(EditProfilestu.this, "Deleted", Toast.LENGTH_SHORT).show();
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

                            Toast.makeText(EditProfilestu.this, "Deleted", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(EditProfilestu.this, "Deleted", Toast.LENGTH_SHORT).show();
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
                        DownloadManager manager = (DownloadManager) EditProfilestu.this.getSystemService(Context.DOWNLOAD_SERVICE);
                        manager.enqueue(request);
                        Toast.makeText(EditProfilestu.this, "Downloading", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        Toast.makeText(EditProfilestu.this,"No Permissions",Toast.LENGTH_SHORT).show();
                    }
                };
                TedPermission.with(EditProfilestu.this).setPermissionListener(permissionListener).setPermissions(Manifest.permission.INTERNET,Manifest.permission.READ_EXTERNAL_STORAGE).check();
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

                ClipboardManager cp = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("String",url);
                cp.setPrimaryClip(clip);
                clip.getDescription();
                Toast.makeText(EditProfilestu.this, "", Toast.LENGTH_SHORT).show();

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
                                getApplicationContext(), EditProfilestu.this);

                notificationsSender.SendNotifications();

            }
        },1000);

    }
}