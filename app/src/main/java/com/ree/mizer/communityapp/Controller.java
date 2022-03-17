package com.ree.mizer.communityapp;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ree.mizer.communityapp.pojos.User;
import com.ree.mizer.communityapp.roles.admin.AdminDashActivity;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class Controller {

    DatabaseReference userReference;
    ArrayList<User> searchedUserList;
    int numOfUsers = 0;

    public Controller() {
        //funeralReference = FirebaseDatabase.getInstance().getReference("Funeral announcements");
        userReference = FirebaseDatabase.getInstance().getReference("Users");
    }

    public void getAllInActiveUser() {

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                searchedUserList = new ArrayList<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    User u = dataSnapshot1.getValue(User.class);
                    String uid = dataSnapshot1.getKey();
                    u.setUserID(uid);
                    searchedUserList.add(u);
                    if (u.getStatus().equals("Not active")) {
                        numOfUsers++;
                        //menuUsers.setTitle("Users "+numOfUsers);
                        Log.d("**********Community app", " number of users");
                    }
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }
}
