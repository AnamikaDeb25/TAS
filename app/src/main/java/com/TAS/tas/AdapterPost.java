package com.TAS.tas;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


public class AdapterPost extends RecyclerView.ViewHolder {

    ImageView imageViewprofile,iv_post;
    TextView tv_name,tv_desc,tv_likes,tv_comment,tv_time,tv_nameprofile,tv_share;
    ImageButton likebtn,menuoptions,commentbtn,sharebtn;
    DatabaseReference likesref,commentref;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    int likescount,commentcount;


    public AdapterPost(@NonNull View itemView) {
        super(itemView);
    }

    public void SetPost(FragmentActivity activity, String name, String url, String postUri, String time, String uid, String type, String desc){

       // SimpleExoPlayer exoPlayer;
        imageViewprofile = itemView.findViewById(R.id.ivprofile_item);
        iv_post = itemView.findViewById(R.id.iv_post_item);
        tv_desc = itemView.findViewById(R.id.tv_desc_post);
        commentbtn = itemView.findViewById(R.id.commentbutton_posts);
        likebtn = itemView.findViewById(R.id.likebutton_posts);
        tv_likes = itemView.findViewById(R.id.tv_likes_post);
        menuoptions = itemView.findViewById(R.id.morebutton_posts);
        tv_time = itemView.findViewById(R.id.tv_time_post);
        tv_nameprofile = itemView.findViewById(R.id.tv_name_post);
        tv_comment = itemView.findViewById(R.id.tv_comment_post);
        sharebtn = itemView.findViewById(R.id.sharetbutton_posts);
        tv_share = itemView.findViewById(R.id.tv_share_post);
            Picasso.get().load(url).networkPolicy(NetworkPolicy.OFFLINE).fit().placeholder(R.drawable.pict).into(imageViewprofile);
            Picasso.get().load(postUri).networkPolicy(NetworkPolicy.OFFLINE).fit().placeholder(R.drawable.ic_baseline_image_24).into(iv_post);
            tv_time.setText(time);
            tv_nameprofile.setText(name);

            if (!desc.equals("")){
                tv_desc.setVisibility(View.VISIBLE);
                tv_desc.setText(desc);
            }
            else {
                tv_desc.setVisibility(View.GONE);
            }
        }
    public void likeschecker(final String postkey) {
        likebtn = itemView.findViewById(R.id.likebutton_posts);
        likesref = database.getReference("post likes");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = user.getUid();

        likesref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postkey).hasChild(uid)) {
                    likebtn.setImageResource(R.drawable.ic_like);
                    likescount = (int) snapshot.child(postkey).getChildrenCount();
                    if (likescount == 1 || likescount ==0){
                        tv_likes.setText(Integer.toString(likescount) + " Like");
                    }
                    else {
                        tv_likes.setText(Integer.toString(likescount) + " Likes");
                    }
                } else {
                    likebtn.setImageResource(R.drawable.ic_dislike);
                    likescount = (int) snapshot.child(postkey).getChildrenCount();
                    if (likescount == 1 || likescount ==0){
                        tv_likes.setText(Integer.toString(likescount) + " Like");
                    }
                    else {
                        tv_likes.setText(Integer.toString(likescount) + " Likes");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void commentchecker(final String postkey) {
        tv_comment = itemView.findViewById(R.id.tv_comment_post);
        commentref = database.getReference("All posts").child(postkey).child("Comments");
        commentref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentcount = (int) snapshot.getChildrenCount();
                tv_comment.setText(Integer.toString(commentcount)+" Comments");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
}
