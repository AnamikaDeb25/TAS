package com.TAS.tas;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class User extends Fragment {

    RecyclerView recyclerView;
    AdapterUsers adapterUsers;
    List<UserData> userList;
    String usertoken;


    public User() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        //setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        android.widget.SearchView searchView = view.findViewById(R.id.sss);
        DatabaseReference reff = FirebaseDatabase.getInstance().getReference("User");
        reff.keepSynced(true);

        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //called when user press search icon on keyboard
                if (!TextUtils.isEmpty(s.trim())){
                    searchUsers(s);
                }
                else {
                    getAllUsers();
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                if (!TextUtils.isEmpty(s)){
                    searchUsers(s);
                }
                else {
                    getAllUsers();
                }
                return false;
            }
        });
        recyclerView = view.findViewById(R.id.users_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //init user list
        userList = new ArrayList<>();
        //get All user
        getAllUsers();
        return view;
    }
    private void getAllUsers() {
        //get Current user
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = fUser.getUid();
        //get path
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User");
        //get all
        ref.orderByChild("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               userList.clear();
               for (DataSnapshot ds: snapshot.getChildren()){
                   UserData userData = ds.getValue(UserData.class);
                   String UID = ds.child("uid").getValue(String.class).toString();
                   if (!UID.equals(userId)){
                       userList.add(userData);
                   }
                   //adapter
                   adapterUsers = new AdapterUsers(getActivity(),userList);
                   recyclerView.setAdapter(adapterUsers);
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void searchUsers(String query) {
        //get Current user
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = fUser.getUid();
        //get path
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User");
        //get all data
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    UserData userData = ds.getValue(UserData.class);
                    //searched user
                     if (!userData.getUid().equals(userId)){
                    if (userData.getName().toLowerCase().contains(query.toLowerCase()) ||
                    userData.getEmail().toLowerCase().contains(query.toLowerCase())){
                        userList.add(userData);
                    }


                     }
                    //adapter
                    adapterUsers = new AdapterUsers(getActivity(),userList);
                    //refresh user
                    adapterUsers.notifyDataSetChanged();
                    recyclerView.setAdapter(adapterUsers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}



