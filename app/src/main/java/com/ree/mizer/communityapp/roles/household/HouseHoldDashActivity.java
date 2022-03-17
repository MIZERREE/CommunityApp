package com.ree.mizer.communityapp.roles.household;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.ree.mizer.communityapp.R;
import com.ree.mizer.communityapp.pojos.User;

public class HouseHoldDashActivity extends AppCompatActivity {

    //bottom navigation bar
    ChipNavigationBar chipNavigationBar;

    public static User LOGGED_IN_USER;
    public static String LOGGED_IN_USER_GROUP;

    public static DatabaseReference USER_REFERENCE,  FUNERAL_REFERENCE, ASSETS_REFERENCE, FINE_REFERENCE, HISTORY_REFERENCE, INCIDENT_REFERENCE, GENERAL_REFERENCE,FINANCIAL_CONTRIBUTIONS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_hold_dash);

        USER_REFERENCE = FirebaseDatabase.getInstance().getReference("Users");
        GENERAL_REFERENCE = FirebaseDatabase.getInstance().getReference("General announcements");
        INCIDENT_REFERENCE = FirebaseDatabase.getInstance().getReference("Incident reports");
        FINANCIAL_CONTRIBUTIONS = FirebaseDatabase.getInstance().getReference("Financial contributions");

        chipNavigationBar = findViewById(R.id.bottom_nav_menu);

        //navigation bar
        //default fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new UserDashFragment()).commit();
        chipNavigationBar.setItemSelected(R.id.bottom_nav_dashboard,true);
        bottomMenu();

        getLoggedInUser();


    }

    private void bottomMenu() {

        chipNavigationBar.setOnItemSelectedListener(i -> {
            Fragment fragment = null;
            switch (i){
                case R.id.bottom_nav_dashboard:
                    fragment = new UserDashFragment();
                    break;
                case R.id.bottom_nav_financial:
                    fragment = new FinancilaFragment();
                    break;
                case R.id.bottom_nav_fines:
                    fragment = new PayFinesFragment();
                    break;
                case R.id.bottom_nav_assets:
                    fragment = new AssetsFragment();
                    break;
                case R.id.bottom_nav_incidents:
                    fragment = new ReportIncidentFragment();
                    break;
                case R.id.bottom_nav_profile:
                    fragment = new ProfileFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
        });
    }

    private void getLoggedInUser(){
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        USER_REFERENCE.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {

                    User u = dataSnapshot1.getValue(User.class);
                    String uid = dataSnapshot1.getKey();

                    if(UID.equals(uid)){
                        LOGGED_IN_USER = u;
                        LOGGED_IN_USER_GROUP = u.getGroup();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getAllFines(){

    }

}