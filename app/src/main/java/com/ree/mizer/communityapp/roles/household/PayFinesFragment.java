package com.ree.mizer.communityapp.roles.household;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ree.mizer.communityapp.R;
import com.ree.mizer.communityapp.adapters.FineAdapter;
import com.ree.mizer.communityapp.pojos.User;
import com.ree.mizer.communityapp.pojos.admin.Fine;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class PayFinesFragment extends Fragment {

    private User loggedUser;

    private ProgressBar progressBarLoad, progressBarLoadFines;
    private double totalFines = 0;
    private RecyclerView finesRecyclerView;
    RecyclerView.LayoutManager layoutManagerFines;

    String userFullNames, userAddress;

    DatabaseReference finesReference;
    ArrayList<Fine> fineArrayList = new ArrayList<>();
    Fine fine;

    private TextView tvTotFines,tvDate;
    private Button btnTotFines;
    private EditText edtName,edtAddress, edtAmount;

    private String date, userID;

    private ProgressDialog loadingBar;
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
        View view = inflater.inflate(R.layout.fragment_pay_fines, container, false);

        progressBarLoad = view.findViewById(R.id.simpleProgressBarPay);
        progressBarLoadFines = view.findViewById(R.id.simpleProgressBarFines);
        /*edtName = view.findViewById(R.id.user_text_fine_full_names);
        edtAddress = view.findViewById(R.id.user_text_fine_address);
        tvDate = view.findViewById(R.id.user_text_pay_fine_date);
        edtAmount = view.findViewById(R.id.user_text_fine_amount);*/
        finesRecyclerView = view.findViewById(R.id.rcy_my_fines);

        loadingBar = new ProgressDialog(context);
        layoutManagerFines = new LinearLayoutManager(context);
        finesRecyclerView.setLayoutManager(layoutManagerFines);


        /*current date
        String currentDate = new SimpleDateFormat("EEE dd-MM-yyyy", Locale.getDefault()).format(new Date());
        date = currentDate;
        tvDate.setText(currentDate);*/

        getLoggedInUser();
        displayFines();

        return view;
    }

    /*private void payFines(){
        //loading bar
        loadingBar.setTitle("Fine clearance");
        loadingBar.setMessage("Please wait ...");
        loadingBar.show();
        //alert
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pending");
        builder.setMessage("Fines conditionally cleared please wait for admin to verify your request");
        builder.setIcon(R.drawable.info_icon);
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //loadingBar.cancel();

            }
        });
        AlertDialog alertDialog = builder.create();
        //end of alert dialog

        //validations
        if(edtDescription.getText().toString().trim().equals("") || edtDescription.getText().toString().trim().isEmpty()){
            edtDescription.setText("None");
            loadingBar.dismiss();
        }else if(edtAddress.getText().toString().trim().equals("") || edtAddress.getText().toString().trim().isEmpty()){
            edtAddress.setError("Address can not be empty");
            loadingBar.dismiss();
        }else if(edtName.getText().toString().trim().equals("") || edtName.getText().toString().trim().isEmpty()){
            edtName.setError("Member name can not be empty");
            loadingBar.dismiss();
        }else if(edtAmount.getText().toString().trim().equals("") || edtAmount.getText().toString().trim().isEmpty()){
            edtAmount.setError("Hou much are you paying?");
            loadingBar.dismiss();
        }else{
            String description = edtDescription.getText().toString().trim();
            String amount = edtAmount.getText().toString().trim();
            String memberID = loggedUser.getUserID();


            FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
            String fineID = rootNode.getReference("Fines").push().getKey();
            DatabaseReference fineReference = rootNode.getReference("Fines").child(memberID).child(fineID);

            Fine fine = new Fine(fineID,memberID,date,"no due date",amount,description,"active");
            fineReference.setValue(fine).addOnCompleteListener(v->{
                updateUser();
                loadingBar.dismiss();
                alertDialog.show();
                clearView();
            }).addOnFailureListener(v-> {
                loadingBar.dismiss();
                Toast.makeText(this, "Something went wrong, try again", Toast.LENGTH_SHORT).show();});
        }
    }*/

    private void getTotalFine(){

        progressBarLoadFines.setVisibility(View.VISIBLE);
        finesReference = FirebaseDatabase.getInstance().getReference("Fines").child(userID);
        finesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBarLoadFines.setVisibility(View.GONE);
                fineArrayList = new ArrayList<>();

                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    fine = dataSnapshot1.getValue(Fine.class);
                    fineArrayList.add(fine);
                }
                Collections.reverse(fineArrayList);
                FineAdapter adapter = new FineAdapter(context, fineArrayList);
                finesRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }

    private void displayFines(){
        getTotalFine();
        //calculate fine
        for(int x =0;x<fineArrayList.size();x++){
            try{
                totalFines+=Double.parseDouble(fineArrayList.get(x).getAmount());
            }catch (Exception e){
                Toast.makeText(context, fineArrayList.get(x).getAmount(), Toast.LENGTH_SHORT).show();
            }
        }
        /*tvTotFines.setText("Your total fines are R"+totalFines);
        btnTotFines.setText("R" + totalFines);
        edtAmount.setText("R"+totalFines);*/
    }

    private void getLoggedInUser(){
        if (HouseHoldDashActivity.LOGGED_IN_USER != null){
            userFullNames = HouseHoldDashActivity.LOGGED_IN_USER.getFullNames()+" "+HouseHoldDashActivity.LOGGED_IN_USER.getSurname();
            userAddress = HouseHoldDashActivity.LOGGED_IN_USER.getAddress();

            userID = HouseHoldDashActivity.LOGGED_IN_USER.getUserID();
            //edtName.setText(userFullNames);
            //edtAddress.setText(userAddress);
            loggedUser = HouseHoldDashActivity.LOGGED_IN_USER;
        }else{
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }
}