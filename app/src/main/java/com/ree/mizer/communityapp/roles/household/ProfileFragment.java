package com.ree.mizer.communityapp.roles.household;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ree.mizer.communityapp.R;
import com.ree.mizer.communityapp.pojos.User;


public class ProfileFragment extends Fragment {

    TextView txtFirstName,txtLastName, txtAddress,txtPhone,txtDateRegistered,txtDateOfBirth,txtVillage,txtEmail,txtGroup, txtRole, txtStatus, txtHasAssets, txtHasFines;

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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        txtFirstName = view.findViewById(R.id.user_update_full_name);
        txtLastName = view.findViewById(R.id.user_update_last_name);
        txtAddress = view.findViewById(R.id.user_update_address);
        txtPhone = view.findViewById(R.id.user_update_phone);
        txtDateRegistered = view.findViewById(R.id.user_update_DOR);
        txtDateOfBirth = view.findViewById(R.id.user_update_DOB);
        txtVillage = view.findViewById(R.id.user_update_village);
        txtEmail = view.findViewById(R.id.user_update_email);
        txtGroup = view.findViewById(R.id.user_update_group);
        txtRole = view.findViewById(R.id.user_update_role);
        txtStatus = view.findViewById(R.id.user_update_status);
        txtHasAssets = view.findViewById(R.id.user_update_has_assets);
        txtHasFines = view.findViewById(R.id.user_update_has_fines);

        profile();

        return view;
    }

    private void profile(){
        User user = HouseHoldDashActivity.LOGGED_IN_USER;

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