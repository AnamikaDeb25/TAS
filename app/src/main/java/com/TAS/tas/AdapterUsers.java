package com.TAS.tas;
import android.content.Context;
import android.content.Intent;
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
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import java.util.List;
public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder>{


    Context context;
    List<UserData> userList;
    FirebaseAuth firebaseAuth;
    String myUid;
    public AdapterUsers(Context context, List<UserData> userList){
        this.context = context;
        this.userList = userList;
        firebaseAuth = FirebaseAuth.getInstance();
        myUid = firebaseAuth.getUid();
    }
    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.raw_user, parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //get data
        String userImage = userList.get(position).getImage();
        String hisUID = userList.get(position).getUid();
        String userName = userList.get(position).getName();
        String userEmail = userList.get(position).getEmail();
       // String userSchool = userList.get(position).getSchool();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
        //set data
        holder.mNameTv.setText(userName);
        holder.mEmailTv.setText(userEmail);
        //  holder.mSchool.setText(userSchool);
        try {
            Picasso.get().load(userImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.pict).into(holder.mAvatarTv);
        }
        catch (Exception e){ }
        checkIsBlocked(hisUID,holder,position);

        holder.gochat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User");
                ref.child(hisUID).child("BlockedUsers").orderByChild("uid").equalTo(myUid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds: snapshot.getChildren()){
                                    if(ds.exists()){
                                        Toast.makeText(context, "You are blocked by the user", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                                databaseReference.orderByChild("name").equalTo(userList.get(holder.getAdapterPosition()).getName()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot ds : snapshot.getChildren()){
                                            String hisUID = ds.child("uid").getValue(String.class).toString();
                                            // Toast.makeText(context,"id     "+hisUID,Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(context,ChatActivity.class);
                                            intent.putExtra("hisUid",hisUID);
                                            intent.putExtra("userName",userName);
                                            context.startActivity(intent);
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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.orderByChild("name").equalTo(userList.get(holder.getAdapterPosition()).getName()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()){
                            String uid = ds.child("uid").getValue(String.class).toString();
                            Intent intent = new Intent(context,ShowUserProfile.class);
                            intent.putExtra("userId",uid);
                            context.startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
    private void checkIsBlocked(String hisUID, MyHolder holder, int position) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User");
        ref.child(myUid).child("BlockedUsers").orderByChild("uid").equalTo(hisUID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            if(ds.exists()){
                              //  holder.mAvatarTv.setImageResource(R.drawable.ic_baseline_block_24);
                                userList.get(position).setBlocked(true);
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
        return userList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        ImageView mAvatarTv;
        TextView mNameTv, mEmailTv,mSchool,gochat;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            mAvatarTv = itemView.findViewById(R.id.avatar);
            mNameTv = itemView.findViewById(R.id.nameTv);
            mEmailTv = itemView.findViewById(R.id.emailTv);
            gochat = itemView.findViewById(R.id.gochat);
         //   mSchool = itemView.findViewById(R.id.schoolTv);
        }
    }
}
