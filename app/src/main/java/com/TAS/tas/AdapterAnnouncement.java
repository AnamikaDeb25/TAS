package com.TAS.tas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterAnnouncement extends RecyclerView.Adapter<AdapterAnnouncement.MyHolder> {

    Context context;
    List<ModelAnnouncement> announcementList;
    FirebaseAuth firebaseAuth;
    String myUid;


    public AdapterAnnouncement(Context context, List<ModelAnnouncement> announcementList){
        this.context = context;
        this.announcementList = announcementList;

        firebaseAuth = FirebaseAuth.getInstance();
        myUid = firebaseAuth.getUid();
    }




    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.layoutshowannouncement, parent,false);
        return new AdapterAnnouncement.MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String userImage = announcementList.get(position).getUrl();
        String hisUID = announcementList.get(position).getUid();
        String userName = announcementList.get(position).getName();
        String announcement = announcementList.get(position).getAnnouncement();
        String time = announcementList.get(position).getTime();

        holder.name.setText(userName);
        holder.announcement.setText(announcement);

        try {
            Picasso.get().load(userImage).networkPolicy(NetworkPolicy.OFFLINE).fit().placeholder(R.drawable.pict).into(holder.pic);
        }
        catch (Exception e){ }

    }

    @Override
    public int getItemCount() {
        return announcementList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        ImageView pic;
        TextView name, announcement;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            pic = itemView.findViewById(R.id.pic);
            name = itemView.findViewById(R.id.n);
            announcement = itemView.findViewById(R.id.a);

        }
    }
}
