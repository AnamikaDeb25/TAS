package com.TAS.tas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class NotificationActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ntRef;
    String userid;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        TextView bk = findViewById(R.id.bk);
        bk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userid = user.getUid();

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.rv_new);
        ntRef = database.getReference("notification").child(userid);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        ntRef.keepSynced(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        Query query = ntRef.orderByChild("seen").equalTo("yes");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                    dataSnapshot1.getRef().removeValue();
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }
    private void checkOnlineStatus(String status){
        DatabaseReference bdRef = FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus",status);
        //update value of onlineStatus
        bdRef.updateChildren(hashMap);

    }
    @Override protected void onResume() {
        super.onResume();
        checkOnlineStatus("online");
    }
    @Override protected void onPause() {
        super.onPause();
        String timestamp = String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(timestamp);
    }
    @Override protected void onStart() {
        super.onStart();
        checkOnlineStatus("online");
        FirebaseRecyclerOptions<NewMember> options1 = new FirebaseRecyclerOptions.Builder<NewMember>().setQuery(ntRef,NewMember.class).build();
        FirebaseRecyclerAdapter<NewMember,NewViewHolder> firebaseRecyclerAdapter1 = new FirebaseRecyclerAdapter<NewMember, NewViewHolder>(options1) {
                    @NonNull @Override protected void onBindViewHolder(@NonNull NewViewHolder holder, int position, @NonNull NewMember model) {
                        holder.setNt(getApplication(),model.getUrl(),model.getName(),model.getText(),model.getUid(),model.getSeen());
                        String name = getItem(position).getName();
                        String uid = getItem(position).getUid();
                        String url = getItem(position).getUrl();
                        String seen = getItem(position).getSeen();

                        holder.nametv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (userid.equals(uid)) {
                                    Intent intent = new Intent(NotificationActivity.this,EditProfile.class);
                                    startActivity(intent);

                                }else {
                                    Intent intent = new Intent(NotificationActivity.this,ShowUserProfile.class);
                                    intent.putExtra("userId",uid);
                                    startActivity(intent);
                                }

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public NewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_layout,parent,false);
                        return new NewViewHolder(view);
                    }
                };
        firebaseRecyclerAdapter1.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter1);
    }
}