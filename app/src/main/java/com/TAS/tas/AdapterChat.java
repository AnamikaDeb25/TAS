package com.TAS.tas;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
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
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterChat extends  RecyclerView.Adapter<AdapterChat.MyHolder> {


    private static final int    MSG_TYPE_LEFT = 0;
    private static final int    MSG_TYPE_RIGHT = 1;
    Boolean isImageFitToScreen= true;
    Context context;
    List<ModelChat> chatList;

    FirebaseUser fUser;




    public AdapterChat(Context context, List<ModelChat> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, parent,false);
            return new MyHolder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, parent,false);
            return new MyHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        //get data
        String message = chatList.get(position).getMessage();
        String audio = chatList.get(position).getAudio();
        String type = chatList.get(position).getType();
        String timeStamp = chatList.get(position).getTimestamp();


        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime = DateFormat.format("dd MMM hh:mm aa",cal).toString();




        if (type.equals("t")){
            holder.lt.setVisibility(View.VISIBLE);
            holder.li.setVisibility(View.GONE);
            holder.la.setVisibility(View.GONE);
            holder.messageTv.setText(message);
            holder.timeTv.setText(dateTime);
        }
        if (type.equals("i")){
            holder.lt.setVisibility(View.GONE);
            holder.li.setVisibility(View.VISIBLE);
            holder.la.setVisibility(View.GONE);
            holder.timeTv2.setText(dateTime);
            Picasso.get().load(message).networkPolicy(NetworkPolicy.OFFLINE).fit().placeholder(R.drawable.acc).into(holder.image);
        }
        if (type.equals("a")){
            holder.lt.setVisibility(View.GONE);
            holder.li.setVisibility(View.GONE);
            holder.la.setVisibility(View.VISIBLE);
            holder.timeTv3.setText(dateTime);
        }
  //     Picasso.get().load(message).into(holder.image);

        //click to show delete
        holder.messageLAyout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete message");
                builder.setMessage("Are you sure you want to delete this message?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if (type.equals("i")){
                            StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(message);
                            reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                                }
                            });
                            deleteMessage(holder.getAdapterPosition());
                        }
                        else  deleteMessage(holder.getAdapterPosition());

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
                return false;
            }
        });
        if (type.equals("i")){
            holder.messageLAyout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ImageViewOfChat.class);
                    intent.putExtra("url",message);
                    context.startActivity(intent);
                }
            });
        }

        if (type.equals("a")){
        holder.messageLAyout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != (PackageManager.PERMISSION_GRANTED)) {
                    ActivityCompat.requestPermissions((Activity) context,new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },30);
                }
                else{
                    holder.messageLAyout.setEnabled(false);
                    holder.messageLAyout.setClickable(false);
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    holder.audiostatus.setText("Playing...");
                    holder.play.setImageResource(R.drawable.pause_black);
                    try {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(message);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                holder.play.setImageResource(R.drawable.play_black);
                                holder.audiostatus.setText("Audio");
                                mediaPlayer.stop();
                                holder.messageLAyout.setEnabled(true);
                                holder.messageLAyout.setClickable(true);
                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }}


            }

        });}


        if (position == chatList.size()-1){
            if (chatList.get(position).isSeen()){
                holder.isSeenTv.setText("Seen");
                holder.isSeenTv.setTextColor(Color.BLUE);
            }
            else {
                holder.isSeenTv.setText("Delivered");
                holder.isSeenTv.setTextColor(Color.GRAY);

            }
        }
        else {
            holder.isSeenTv.setVisibility(View.GONE);
        }
    }

    private void deleteMessage(int position) {
        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String msgTimeStamp = chatList.get(position).getTimestamp();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
        Query query= dbRef.orderByChild("timestamp").equalTo(msgTimeStamp);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.child("Sender").getValue().equals(myUid)) {
                        ds.getRef().removeValue();
//                        HashMap<String, Object> hashMap = new HashMap<>();
//                        hashMap.put("message", "This message was deleted");
//                        ds.getRef().updateChildren(hashMap);

                        Toast.makeText(context,"message deleted",Toast.LENGTH_SHORT).show();

                }else {
                        Toast.makeText(context,"something went wrong",Toast.LENGTH_SHORT).show();
                    }

            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(fUser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }
    }

    class MyHolder extends RecyclerView.ViewHolder{

        TextView messageTv, timeTv,timeTv2,timeTv3,isSeenTv,audiostatus;
        ImageView image,play;
        LinearLayout lt;
        LinearLayout li,la;
        LinearLayout messageLAyout;//for clicklistener to show delete



        public MyHolder(@NonNull View itemView) {
            super(itemView);

            lt = itemView.findViewById(R.id.o1);
            li = itemView.findViewById(R.id.o2);
            la =itemView.findViewById(R.id.o3);

            play = itemView.findViewById(R.id.aaudio);
            audiostatus = itemView.findViewById(R.id.audiostatus);
            messageTv = itemView.findViewById(R.id.messageTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            timeTv2 = itemView.findViewById(R.id.timeTv2);
            timeTv3=  itemView.findViewById(R.id.timeTv3);
            isSeenTv = itemView.findViewById(R.id.isSeenTv);
            messageLAyout = itemView.findViewById(R.id.messageLayout);
            image =itemView.findViewById(R.id.iimage);
        }
    }
}
