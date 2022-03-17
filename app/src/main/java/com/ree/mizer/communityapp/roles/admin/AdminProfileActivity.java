package com.ree.mizer.communityapp.roles.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.ree.mizer.communityapp.R;
import com.ree.mizer.communityapp.pojos.User;
import com.ree.mizer.communityapp.roles.household.HouseHoldDashActivity;

public class AdminProfileActivity extends AppCompatActivity {

    TextView txtFirstName,txtLastName, txtAddress,txtPhone,txtDateRegistered,txtDateOfBirth,txtVillage,txtEmail,txtGroup, txtRole, txtStatus, txtHasAssets, txtHasFines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        txtFirstName = findViewById(R.id.admin_update_full_name);
        txtLastName = findViewById(R.id.admin_update_last_name);
        txtAddress = findViewById(R.id.admin_update_address);
        txtPhone = findViewById(R.id.admin_update_phone);
        txtDateRegistered = findViewById(R.id.admin_update_DOR);
        txtDateOfBirth = findViewById(R.id.admin_update_DOB);
        txtVillage = findViewById(R.id.admin_update_village);
        txtEmail = findViewById(R.id.admin_update_email);
        txtGroup = findViewById(R.id.admin_update_group);
        txtRole = findViewById(R.id.admin_update_role);
        txtStatus = findViewById(R.id.admin_update_status);
        txtHasAssets = findViewById(R.id.admin_update_has_assets);
        txtHasFines = findViewById(R.id.admin_update_has_fines);

        profile();
    }

    private void profile(){
        User user = AdminDashActivity.LOGGED_IN_ADMIN;

        txtFirstName.setText(user.getFullNames());
        txtLastName.setText(user.getSurname());
        txtAddress.setText(user.getAddress());
        txtPhone.setText(user.getPhoneNumber());
        txtDateRegistered.setText(user.getDateOfRegistration());
        txtDateOfBirth.setText(user.getDateOfBirth());
        txtVillage.setText(user.getVillage());
        txtEmail.setText(user.getEmail());
        txtStatus.setText(user.getStatus());
        txtHasAssets.setText(user.getHasAssets());
        txtHasFines.setText(user.getHasFines());
        txtGroup.setText(user.getGroup());
        txtRole.setText(user.getRole());
    }
}