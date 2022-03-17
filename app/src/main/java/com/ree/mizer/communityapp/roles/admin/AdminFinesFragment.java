package com.ree.mizer.communityapp.roles.admin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ree.mizer.communityapp.R;
import com.ree.mizer.communityapp.adapters.FineAdapter;
import com.ree.mizer.communityapp.login.LoginActivity;
import com.ree.mizer.communityapp.pojos.User;
import com.ree.mizer.communityapp.pojos.admin.Fine;
import com.ree.mizer.communityapp.pojos.admin.Funeral;
import com.ree.mizer.communityapp.pojos.admin.Meeting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class AdminFinesFragment extends Fragment {

    RecyclerView.LayoutManager layoutManagerFines;
    Button btnFine;

    private ProgressBar progressBarLoad;

    String date, userID, dueDate;
    EditText edtAddress, edtName,edtAmount,edtDescription,edtDueDate;
    TextView tvDate;
    AutoCompleteTextView txtSearch;
    ArrayList<User> searchedUserList;
    RecyclerView recyclerFines;
    ArrayList<Fine> fineArrayList = new ArrayList<>();
    Fine fine;
    RecyclerView recyclerViewFines ;
    ArrayList<Fine> fineList = new ArrayList<>();
    ArrayList<Meeting> meetings = new ArrayList<>();

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
        View view = inflater.inflate(R.layout.fragment_admin_fines, container, false);

        edtAddress = view.findViewById(R.id.admin_text_fine_address);
        edtName = view.findViewById(R.id.admin_text_fine_name);
        edtAmount = view.findViewById(R.id.admin_text_fine_amount);
        edtDescription = view.findViewById(R.id.admin_text_fine_description);
        edtDueDate = view.findViewById(R.id.admin_text_fine_due_date);
        tvDate = view.findViewById(R.id.admin_text_fine_date);
        txtSearch = view.findViewById(R.id.admin_text_search_fine_member);
        recyclerFines = view.findViewById(R.id.rcyFinedMembers);
        progressBarLoad = view.findViewById(R.id.simpleProgressBarFines);
        btnFine = view.findViewById(R.id.btn_admin_fine_user);

        //For searching user
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                populateSearch(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        AdminDashActivity.USER_REFERENCE.addListenerForSingleValueEvent(eventListener);
        loadingBar = new ProgressDialog(context);

        //current date
        String currentDate = new SimpleDateFormat("EEE dd-MM-yyyy", Locale.getDefault()).format(new Date());
        date = currentDate;
        tvDate.setText(currentDate);

        layoutManagerFines = new LinearLayoutManager(context);
        recyclerFines.setLayoutManager(layoutManagerFines);
        getAllFines();

        btnFine.setOnClickListener(this::fineMember);

        return  view;
    }


    private void getAllFines() {
        progressBarLoad.setVisibility(View.VISIBLE);
        AdminDashActivity.FINES_REFERENCE.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBarLoad.setVisibility(View.GONE);
                fineList = new ArrayList<>();

                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    fine = dataSnapshot1.getValue(Fine.class);
                    fineList.add(fine);
                }
                Collections.reverse(fineList);
                FineAdapter adapter = new FineAdapter(context, fineList);
                recyclerFines.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
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
                    edtAddress.setText(searchedUserList.get(0).getAddress());
                    edtName.setText(searchedUserList.get(0).getFullNames());

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

    public void fineMember(View view){
        //loading bar
        loadingBar.setTitle("Fines");
        loadingBar.setMessage("Please wait ...");
        loadingBar.show();

        //alert
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Success");
        builder.setMessage("Member Fined");
        builder.setIcon(R.drawable.info_icon);
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loadingBar.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        //end of alert dialog

        //validations
        if(txtSearch.getText().toString().trim().equals("") || txtSearch.getText().toString().trim().isEmpty()){
            txtSearch.setError("Search Member first");
            loadingBar.dismiss();
        }else if(edtAddress.getText().toString().trim().equals("") || edtAddress.getText().toString().trim().isEmpty()){
            edtAddress.setError("Address can not be empty");
            loadingBar.dismiss();
        }else if(edtName.getText().toString().trim().equals("") || edtName.getText().toString().trim().isEmpty()){
            edtName.setError("Member name can not be empty");
            loadingBar.dismiss();
        }else if(edtAmount.getText().toString().trim().equals("") || edtAmount.getText().toString().trim().isEmpty()){
            edtAmount.setError("Hou much is the member being fined");
            loadingBar.dismiss();
        }else if(edtDueDate.getText().toString().trim().equals("") || edtName.getText().toString().trim().isEmpty()) {
            edtDueDate.setError("field can not be empty");
            loadingBar.dismiss();
        }else{
            String dueDate = edtDueDate.getText().toString().trim();
            String description = edtDescription.getText().toString().trim();
            String amount = edtAmount.getText().toString().trim();
            String memberID = searchedUserList.get(0).getUserID();


            FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
            String fineID = rootNode.getReference("Fines").push().getKey();
            DatabaseReference fineReference = rootNode.getReference("Fines").child(fineID);

            Fine fine = new Fine(fineID,memberID,date,dueDate,amount,description,"active");
            fineReference.setValue(fine).addOnCompleteListener(v->{
                updateUser();
                loadingBar.dismiss();
                alertDialog.show();
                clearView();
            }).addOnFailureListener(v-> {
                loadingBar.dismiss();
                Toast.makeText(context, "Something went wrong, try again", Toast.LENGTH_SHORT).show();});
        }
    }

    private void clearView() {
        txtSearch.setText("");
        edtAmount.setText("");
        edtName.setText("");
        edtAddress.setText("");
        edtDescription.setText("");
    }

    private void updateUser(){
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("hasFines", "Yes");
        AdminDashActivity.USER_REFERENCE.child((searchedUserList.get(0).getUserID())).updateChildren(userMap);
    }


}