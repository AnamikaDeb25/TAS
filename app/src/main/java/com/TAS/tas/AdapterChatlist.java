package com.TAS.tas;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class AdapterChatlist extends RecyclerView.Adapter<AdapterChatlist.MyHolder>{


    Context context;
    List<UserData> userDataList;
    private HashMap<String, String > lastMessageMap;
    private HashMap<String , String > lastMessageMaptime;
    FirebaseAuth firebaseAuth;
    String myUid;





    public AdapterChatlist(Context context, List<UserData> userDataList) {
        this.context = context;
        this.userDataList = userDataList;
        lastMessageMap = new HashMap<>();
        lastMessageMaptime = new HashMap<>();
        firebaseAuth = FirebaseAuth.getInstance();
        myUid = firebaseAuth.getUid();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View  view = LayoutInflater.from(context).inflate(R.layout.row_chatlist,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String hisUID = userDataList.get(position).getUid();
        String userImage = userDataList.get(position).getImage();
        String UserName = userDataList.get(position).getName();
        String lastMessage = lastMessageMap.get(hisUID);
        String lastMessagetime = lastMessageMaptime.get(hisUID);

        holder.nameTv.setText(UserName);

        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Chats");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if (chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUID)){
                        if (!chat.isSeen){
                            holder.nameTv.setTextColor(Color.BLUE);
                            holder.lastMessageTv.setTextColor(Color.DKGRAY);
                            holder.lastMessageTime.setTextColor(Color.DKGRAY);
                            holder.lastMessageTime.setTypeface(null, Typeface.BOLD);
                            holder.lastMessageTv.setTypeface(null, Typeface.BOLD);
                            holder.nameTv.setTypeface(null, Typeface.BOLD);

                        }
                        else {
                            holder.nameTv.setTextColor(Color.BLACK);
                            holder.lastMessageTv.setTextColor(Color.GRAY);
                            holder.lastMessageTime.setTextColor(Color.GRAY);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        if (lastMessage ==null || lastMessage.equals("default") ){
            holder.lastMessageTv.setVisibility(View.GONE);
            holder.lastMessageTime.setVisibility(View.GONE);
        }
        if ( lastMessagetime == null || lastMessagetime.equals("default")){
            holder.lastMessageTime.setVisibility(View.GONE);
        }
        else {
            holder.lastMessageTv.setVisibility(View.VISIBLE);
            holder.lastMessageTv.setText(lastMessage);
            holder.lastMessageTime.setText(lastMessagetime);
            holder.lastMessageTime.setVisibility(View.VISIBLE);
        }
        try{
            Picasso.get().load(userImage).placeholder(R.drawable.pict).into(holder.profileIv);
        }catch (Exception e){
            Picasso.get().load(R.drawable.pict).into(holder.profileIv);
        }
        if (userDataList.get(position).getOnlineStatus().equals("online")){
            holder.onlineStatusIv.setImageResource(R.drawable.circle_online);
        }
        else {
            holder.onlineStatusIv.setImageResource(R.drawable.circle_offline);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User");
                ref.child(hisUID).child("BlockedUsers").orderByChild("uid").equalTo(myUid)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()){
                                    if(ds.exists()){
                                        Toast.makeText(context, "You are blocked by the user", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                                Intent intent = new Intent(context,ChatActivity.class);
                                intent.putExtra("hisUid",hisUID);
                                context.startActivity(intent);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });






            }
        });
        holder.profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,ImageViewofprofilepic.class);
                intent.putExtra("uid",hisUID);
                context.startActivity(intent);
            }
        });
    }



    public void setLastMessageMap(String userId,String lastMessage){
        lastMessageMap.put(userId,lastMessage);
    }
    public void setLastMessageMaptime(String userId, String  lastMessagetime){
        lastMessageMaptime.put(userId,lastMessagetime);
    }
    @Override
    public int getItemCount() {
        return userDataList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{


        ImageView profileIv, onlineStatusIv;
        TextView nameTv,lastMessageTv,lastMessageTime;

        public MyHolder(@NonNull View itemView) {
            super(itemView);


            profileIv = itemView.findViewById(R.id.profileIv);
            onlineStatusIv = itemView.findViewById(R.id.onlineStatusIv);
            nameTv = itemView.findViewById(R.id.namechat);
            lastMessageTv = itemView.findViewById(R.id.lastMessage);
            lastMessageTime = itemView.findViewById(R.id.lastMessagetime);

        }
    }

}
