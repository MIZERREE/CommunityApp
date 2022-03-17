package com.ree.mizer.communityapp.roles.household;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ree.mizer.communityapp.R;
import com.ree.mizer.communityapp.pojos.admin.FinancialContribute;
import com.ree.mizer.communityapp.pojos.admin.Fine;
import com.ree.mizer.communityapp.roles.admin.AdminDashActivity;

import java.util.ArrayList;
import java.util.Collections;


public class FinancilaFragment extends Fragment {

    String userFullNames, userAddress, userPhone, userID, date;

    private EditText edtName,edtAddress, edtAmount, edtPhone;
    private TextView tvDate, tvGroupName;
    public Context context;
    double totalContributions = 0;
    TextView tvTotContributions;
    RecyclerView financialRecyclerView;
    RecyclerView.LayoutManager layoutManagerFines;
    private ProgressBar progressBarLoad;
    ArrayList<FinancialContribute> financialList = new ArrayList<>();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_financila, container, false);

        tvGroupName = view.findViewById(R.id.user_tv_group_name);
        tvTotContributions = view.findViewById(R.id.user_tv_tot_contributions);
        financialRecyclerView = view.findViewById(R.id.user_rcy_my_contributions);
        progressBarLoad = view.findViewById(R.id.simpleProgressBarCont);

        /*edtName = view.findViewById(R.id.user_text_cont_name);
        edtAddress = view.findViewById(R.id.user_text_cont_address);
        edtPhone = view.findViewById(R.id.user_text_cont_phoneNumber);
        tvDate = view.findViewById(R.id.admin_text_contribute_date);
        edtAmount = view.findViewById(R.id.user_text_cont_amount);

        //current date
        String currentDate = new SimpleDateFormat("EEE dd-MM-yyyy", Locale.getDefault()).format(new Date());
        date = currentDate;
        tvDate.setText(currentDate);*/

        layoutManagerFines = new LinearLayoutManager(context);
        financialRecyclerView.setLayoutManager(layoutManagerFines);

        getLoggedInUser();
        calculateTotalContributions();

        return view;
    }

    private void getLoggedInUser(){
        if (HouseHoldDashActivity.LOGGED_IN_USER != null){
            userFullNames = HouseHoldDashActivity.LOGGED_IN_USER.getFullNames()+" "+HouseHoldDashActivity.LOGGED_IN_USER.getSurname();
            userAddress = HouseHoldDashActivity.LOGGED_IN_USER.getAddress();
            userPhone = HouseHoldDashActivity.LOGGED_IN_USER.getPhoneNumber();

            userID = HouseHoldDashActivity.LOGGED_IN_USER.getUserID();
            /*edtName.setText(userFullNames);
            edtAddress.setText(userAddress);
            edtPhone.setText(userPhone);*/

            tvGroupName.setText(HouseHoldDashActivity.LOGGED_IN_USER_GROUP+" Group");
        }else{
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void calculateTotalContributions(){
        HouseHoldDashActivity.FINANCIAL_CONTRIBUTIONS.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    FinancialContribute finance = dataSnapshot1.getValue(FinancialContribute.class);
                    assert finance != null;
                    if(HouseHoldDashActivity.LOGGED_IN_USER.getFullNames().equals(finance.getContributorName())){
                        totalContributions+=Double.parseDouble(finance.getAmount());
                    }
                }
                tvTotContributions.setText("Your total contributions to the group is R"+totalContributions);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Oops.... Something is wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAllContributions(){
        progressBarLoad.setVisibility(View.VISIBLE);
        AdminDashActivity.FINANCIAL_CONTRIBUTIONS.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBarLoad.setVisibility(View.GONE);
                financialList = new ArrayList<>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    FinancialContribute finance = dataSnapshot1.getValue(FinancialContribute.class);
                    if(HouseHoldDashActivity.LOGGED_IN_USER.getFullNames().equals(finance.getContributorName())){

                        financialList.add(finance);
                    }
                }
                /*Collections.reverse(financialList);
                FinancialContAdapter adapter = new FinancialContAdapter(context, financialList);
                financialRecyclerView.setAdapter(adapter);*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Oops.... Something is wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }


}