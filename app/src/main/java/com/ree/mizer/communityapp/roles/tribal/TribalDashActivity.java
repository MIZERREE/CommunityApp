package com.ree.mizer.communityapp.roles.tribal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.ree.mizer.communityapp.R;
import com.ree.mizer.communityapp.roles.household.AssetsFragment;
import com.ree.mizer.communityapp.roles.household.FinancilaFragment;
import com.ree.mizer.communityapp.roles.household.PayFinesFragment;
import com.ree.mizer.communityapp.roles.household.ProfileFragment;
import com.ree.mizer.communityapp.roles.household.ReportIncidentFragment;
import com.ree.mizer.communityapp.roles.household.UserDashFragment;

public class TribalDashActivity extends AppCompatActivity {

    //bottom navigation bar
    ChipNavigationBar chipNavigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tribal_dash);

        chipNavigationBar = findViewById(R.id.bottom_nav_menu_tribal);

        //navigation bar
        //default fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.bottom_nav_menu_tribal,new UserDashFragment()).commit();
        chipNavigationBar.setItemSelected(R.id.bottom_nav_tribal_dashboard,true);
        bottomMenu();
    }

    private void bottomMenu() {

        chipNavigationBar.setOnItemSelectedListener(i -> {
            Fragment fragment = null;
            switch (i){
                case R.id.bottom_nav_tribal_dashboard:
                    fragment = new TribalDashboadFragment();
                    break;
                case R.id.bottom_nav_tribal_financial:
                    fragment = new TribalFinancialFragment();
                    break;
                case R.id.bottom_nav_tribal_fines:
                    fragment = new TribalFinesFragment();
                    break;
                case R.id.bottom_nav_tribal_assets:
                    fragment = new AssetsFragment();
                    break;

            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
        });
    }
}