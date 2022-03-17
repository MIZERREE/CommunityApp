package com.ree.mizer.communityapp.roles.admin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ree.mizer.communityapp.R;
import com.ree.mizer.communityapp.adapters.UserManagerAdapter;
import com.ree.mizer.communityapp.login.RegisterActivity;
import com.ree.mizer.communityapp.pojos.User;

import java.util.ArrayList;


public class AdminUserFragment extends Fragment {

    ArrayList<User> searchedUserList;
    RecyclerView recyclerUsers;
    User user;
    AutoCompleteTextView txtSearch;

    RecyclerView.LayoutManager layoutManagerUser;
    Button btnViewAll,btnAddNewUser;

    TextView tvGroup;

    private ProgressBar progressBarLoad;

    public Context context;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_user, container, false);

        recyclerUsers = view.findViewById(R.id.management_listData);
        tvGroup = view.findViewById(R.id.tv_admin_group_name);
        btnAddNewUser = view.findViewById(R.id.admin_button_add);

        layoutManagerUser = new LinearLayoutManager(context);
        recyclerUsers.setLayoutManager(layoutManagerUser);

        //For searching user
        /*ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                populateSearch(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        AdminDashActivity.USER_REFERENCE.addListenerForSingleValueEvent(eventListener);*/

        btnAddNewUser.setOnClickListener(v ->{
            addNewUser();
        });


        if(AdminDashActivity.LOGGED_IN_ADMIN_GROUP.equals("Supper")){
            searchAllUsers();
        }else{
            searchAllUserInMyGroup();
        }
        return view;
    }

    /*
   Start of search user
    */
    private void populateSearch(DataSnapshot snapshot) {
        ArrayList<String> names = new ArrayList<>();
        if(snapshot.exists()){
            for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                String name = dataSnapshot.child("fullNames").getValue(String.class);
                names.add(name);
            }

            ArrayAdapter adapter = new ArrayAdapter(context,android.R.layout.simple_list_item_1,names);
            txtSearch.setAdapter(adapter);
            txtSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String name = txtSearch.getText().toString();
                    searchUser(name);
                }
            });

        }else{
            Log.d("users","No data found");
        }
    }

    private void searchUser(String name) {

        Query query = AdminDashActivity.USER_REFERENCE.orderByChild("fullNames").equalTo(name);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    searchedUserList = new ArrayList<>();

                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        User user = dataSnapshot.getValue(User.class);
                        searchedUserList.add(user);
                    }
                    //adapter
                    UserManagerAdapter adapter = new UserManagerAdapter(context,searchedUserList);
                    recyclerUsers.setAdapter(adapter);

                }else{
                    Log.d("user","No data found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /*
     * end of search user*/
    public void searchAllUserInMyGroup() {
        tvGroup.setText("Group: "+AdminDashActivity.LOGGED_IN_ADMIN_GROUP);
        AdminDashActivity.USER_REFERENCE.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                searchedUserList = new ArrayList<>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    User u = dataSnapshot1.getValue(User.class);
                    String uid = dataSnapshot1.getKey();
                    u.setUserID(uid);
                    if(AdminDashActivity.LOGGED_IN_ADMIN_GROUP.equals(u.getGroup())){
                        searchedUserList.add(u);
                    }

                }
                UserManagerAdapter adapter = new UserManagerAdapter(context,searchedUserList);
                recyclerUsers.setAdapter(adapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Opsss.... Something is wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void searchAllUsers() {
        tvGroup.setText("Supper Admin manages all group");
        AdminDashActivity.USER_REFERENCE.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                searchedUserList = new ArrayList<>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    User u = dataSnapshot1.getValue(User.class);
                    String uid = dataSnapshot1.getKey();
                    u.setUserID(uid);
                    searchedUserList.add(u);

                }
                UserManagerAdapter adapter = new UserManagerAdapter(context,searchedUserList);
                recyclerUsers.setAdapter(adapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Opsss.... Something is wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addNewUser(){
        startActivity(new Intent(context, AdminAddUserActivity.class));
    }
}