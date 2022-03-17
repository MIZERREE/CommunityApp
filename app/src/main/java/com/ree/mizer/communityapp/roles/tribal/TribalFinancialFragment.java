package com.ree.mizer.communityapp.roles.tribal;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ree.mizer.communityapp.R;
import com.ree.mizer.communityapp.pojos.User;
import com.ree.mizer.communityapp.pojos.admin.FinancialContribute;
import com.ree.mizer.communityapp.roles.admin.AdminDashActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class TribalFinancialFragment extends Fragment {
    ArrayList<User> searchedUserList;
    double totalContributions = 0;

    String date,selectedGroup;
    EditText edtAddress, edtContact,edtAmount;
    Button btnSelectGroup,btnContribute;
    TextView tvDate, tvGroup,tvAllGroupMembers, tvTotContributions;
    AutoCompleteTextView txtSearch;
    //Spinner spGroups;

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
        View view = inflater.inflate(R.layout.fragment_tribal_financial, container, false);

        tvDate = view.findViewById(R.id.tribal_text_contribute_date);
        txtSearch = view.findViewById(R.id.tribal_text_cont_name);
        edtAddress = view.findViewById(R.id.tribal_text_cont_address);
        edtContact = view.findViewById(R.id.tribal_text_cont_phoneNumber);
        edtAmount = view.findViewById(R.id.tribal_text_cont_amount);
        btnContribute = view.findViewById(R.id.tribal_btn_contribute);
        tvGroup = view.findViewById(R.id.tribal_tv_group_name);
        tvAllGroupMembers = view.findViewById(R.id.tribal_tv_all_members);
        tvTotContributions = view.findViewById(R.id.tribal_tv_tot_contributions);

        //Display group name
        searchAllUserInMyGroup();
        calculateTotalContributions();

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

        btnContribute.setOnClickListener(v -> contribute());

        AdminDashActivity.USER_REFERENCE.addListenerForSingleValueEvent(eventListener);
        loadingBar = new ProgressDialog(context);

        //current date
        String currentDate = new SimpleDateFormat("EEE dd-MM-yyyy", Locale.getDefault()).format(new Date());
        date = currentDate;
        tvDate.setText(currentDate);

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
                    User user = null;
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        user = dataSnapshot.getValue(User.class);

                        searchedUserList.add(user);
                        //displaySingleFuneralDialog(funeral.getName(),funeral.getAddress(),funeral.getWhenHappened(),funeral.getWhenBurial(),funeral.getContacts(),funeral.getExtras());
                    }
                    //AdminFuneralAdapter adapter = new AdminFuneralAdapter(FinancialActivity.this, searchedUserList);
                    if(user.getGroup().equals(AdminDashActivity.LOGGED_IN_ADMIN_GROUP)){
                        edtAddress.setText(searchedUserList.get(0).getAddress());
                        edtContact.setText(searchedUserList.get(0).getPhoneNumber());
                    }else{
                        Toast.makeText(context, "That member is not in your group", Toast.LENGTH_SHORT).show();
                    }


                }else{
                    Log.d("user","No data found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchAllUserInMyGroup() {
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
                tvAllGroupMembers.setText("Total members: "+searchedUserList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Opsss.... Something is wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateTotalContributions(){
        AdminDashActivity.FINANCIAL_CONTRIBUTIONS.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    FinancialContribute finance = dataSnapshot1.getValue(FinancialContribute.class);
                    if(AdminDashActivity.LOGGED_IN_ADMIN_GROUP.equals(finance.getGroupName())){
                        totalContributions+=Double.parseDouble(finance.getAmount());
                    }
                }
                tvTotContributions.setText("Total contributions: R"+totalContributions);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Oops.... Something is wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
     * end of search user*/

    public void contribute(){
        //loading bar
        loadingBar.setTitle("Updating");
        loadingBar.setMessage("Please wait ...");
        loadingBar.show();

        //alert
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Success");
        builder.setMessage("Finance updated");
        builder.setIcon(R.drawable.info_icon);
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
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
        }else if(edtContact.getText().toString().trim().equals("") || edtContact.getText().toString().trim().isEmpty()){
            edtContact.setError("contacts can not be empty");
            loadingBar.dismiss();
        }else if(edtAmount.getText().toString().trim().equals("") || edtAmount.getText().toString().trim().isEmpty()){
            edtAmount.setError("Hou much is the member contributing");
            loadingBar.dismiss();
        }else{
            String name = txtSearch.getText().toString().trim();
            String amount = edtAmount.getText().toString().trim();
            String contributorID = searchedUserList.get(0).getUserID();


            FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
            String financialID = rootNode.getReference("Financial contributions").push().getKey();
            DatabaseReference financialReference = rootNode.getReference("Financial contributions").child(financialID);

            FinancialContribute financialContribute  = new FinancialContribute(financialID,contributorID,name,date,amount,AdminDashActivity.LOGGED_IN_ADMIN_GROUP,"okay");
            financialReference.setValue(financialContribute).addOnCompleteListener(v->{
                loadingBar.dismiss();
                alertDialog.show();
                clearView();
            }).addOnFailureListener(v-> Toast.makeText(context, "Something went wrong, try again", Toast.LENGTH_SHORT).show());
        }
    }

    private void financialBalance(){

    }

    private void confirmGroup(){
        //alert
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Group");
        builder.setMessage("You selected "+selectedGroup+" group");
        builder.setIcon(R.drawable.info_icon);
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loadingBar.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        //end of alert dialog
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        alertDialog.show();
    }

    private void clearView() {
        txtSearch.setText("");
        edtAmount.setText("");
        edtContact.setText("");
        edtAddress.setText("");
    }
}