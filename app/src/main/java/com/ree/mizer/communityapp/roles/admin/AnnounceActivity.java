package com.ree.mizer.communityapp.roles.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ree.mizer.communityapp.R;
import com.ree.mizer.communityapp.adapters.AdminFuneralAdapter;
import com.ree.mizer.communityapp.adapters.AdminGeneralAdapter;
import com.ree.mizer.communityapp.pojos.MyNotifications;
import com.ree.mizer.communityapp.pojos.admin.Funeral;
import com.ree.mizer.communityapp.pojos.admin.GeneralAnnouncement;
import com.ree.mizer.communityapp.roles.household.HouseHoldDashActivity;

public class AnnounceActivity extends AppCompatActivity {
    EditText edtName, edtAddress, edtWhenBurial, edtContacts, edtExtras, edtHeading, edtDescription;
    DatePicker datePicker;
    String whenHappened;
    AutoCompleteTextView txtSearch;

    //firebase
    DatabaseReference funeralReference, generalReference;

    ArrayList<Funeral> searchedFuneralList;
    ArrayList<Funeral> funerals = new ArrayList<>();
    ArrayList<GeneralAnnouncement> generals = new ArrayList<>();
    Funeral funeral;
    GeneralAnnouncement generalAnnouncement;

    DataSnapshot snap;

    private ProgressDialog loadingBar;
    private ProgressBar progressBarLoadFunerals,progressBarLoadGeneral;

    RecyclerView rclFuneral,rclGeneral;
    RecyclerView.LayoutManager layoutManagerFunerals, layoutManagerGeneral;
    String date;

    Button btnSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announce);

        funeralReference = FirebaseDatabase.getInstance().getReference("Funeral announcements");
        generalReference = FirebaseDatabase.getInstance().getReference("General announcements");

        edtName = findViewById(R.id.admin_text_funeral_name);
        edtAddress = findViewById(R.id.admin_text_funeral_address);
        datePicker = findViewById(R.id.datePickerFuneral);
        edtWhenBurial = findViewById(R.id.admin_text_funeral_when_burial);
        edtExtras = findViewById(R.id.admin_text_funeral_extras);
        edtContacts = findViewById(R.id.admin_text_funeral_contact_number);
        txtSearch = findViewById(R.id.admin_text_search_funeral);
        rclFuneral = findViewById(R.id.listFunerals);
        rclGeneral = findViewById(R.id.listGenerals);
        edtHeading = findViewById(R.id.admin_text_general_heading);
        edtDescription = findViewById(R.id.admin_text_general_announcement);
        progressBarLoadFunerals = findViewById(R.id.simpleProgressBarAnn);
        progressBarLoadGeneral = findViewById(R.id.simpleProgressGeneral);


        loadingBar = new ProgressDialog(this);
        layoutManagerFunerals = new LinearLayoutManager(this);
        layoutManagerGeneral = new LinearLayoutManager(this);
        rclFuneral.setLayoutManager(layoutManagerFunerals);
        rclGeneral.setLayoutManager(layoutManagerGeneral);

        //current date
        String currentDate = new SimpleDateFormat("EEE dd-MM-yyyy", Locale.getDefault()).format(new Date());
        date = currentDate;

        //For searching funeral
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 populateSearch(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        funeralReference.addListenerForSingleValueEvent(eventListener);
        getAllAllFuneralAnnouncements();
        getAllAllGeneralAnnouncements();

    }

    public void announceFuneral(View view){
        //loading bar
        loadingBar.setTitle("Announcing funeral");
        loadingBar.setMessage("Please wait ...");
        loadingBar.show();

        //alert
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Success");
        builder.setMessage("Funeral Announcement sent to everyone in the group");
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
        if(edtName.getText().toString().trim().equals("") || edtName.getText().toString().trim().isEmpty()){
            edtName.setError("Enter deceased name");
            loadingBar.dismiss();
        }else if(edtAddress.getText().toString().trim().equals("") || edtAddress.getText().toString().trim().isEmpty()){
            edtAddress.setError("Enter deceased address");
            loadingBar.dismiss();
        }else if(edtWhenBurial.getText().toString().trim().equals("") || edtWhenBurial.getText().toString().trim().isEmpty()){
            edtWhenBurial.setText("No date");
            loadingBar.dismiss();
        }else if(edtContacts.getText().toString().trim().equals("") || edtContacts.getText().toString().trim().isEmpty()){
            edtContacts.setError("Who to contact?");
            loadingBar.dismiss();
        }else if(edtExtras.getText().toString().trim().equals("") || edtExtras.getText().toString().trim().isEmpty()){
            edtExtras.setText(" ");
            loadingBar.dismiss();
        }else{
            String name = edtName.getText().toString().trim();
            String address = edtAddress.getText().toString().trim();
            whenHappened = datePicker.getDayOfMonth()+"/"+ (datePicker.getMonth() + 1)+"/"+datePicker.getYear();
            String whenBurial = edtWhenBurial.getText().toString().trim();
            String contacts = edtContacts.getText().toString().trim();
            String extras = edtExtras.getText().toString().trim();

            FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
            String funeralID = rootNode.getReference("Funeral announcements").push().getKey();
            funeralReference = rootNode.getReference("Funeral announcements").child(funeralID);

            Funeral funeral = new Funeral(funeralID, FirebaseAuth.getInstance().getCurrentUser().getUid(),name,address,whenHappened,whenBurial,contacts,extras,"admin");
            funeralReference.setValue(funeral).addOnCompleteListener(v->{
                loadingBar.dismiss();
                alertDialog.show();
                String reporter = AdminDashActivity.LOGGED_IN_ADMIN.getFullNames();
                //notify everyone
                MyNotifications notifications = new MyNotifications(AnnounceActivity.this);
                notifications.sendNotificationToReports(reporter,"Funeral report","Reported new funeral, log in for more details");

                clearView();
            }).addOnFailureListener(v-> Toast.makeText(this, "Something went wrong, try again", Toast.LENGTH_SHORT).show());
        }
    }

    public void announceGeneral(View view){
        //loading bar
        loadingBar.setTitle("Announcing");
        loadingBar.setMessage("Please wait ...");
        loadingBar.show();

        //alert
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Success");
        builder.setMessage("General Announcement sent to everyone in the group");
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
        if(edtHeading.getText().toString().trim().equals("") || edtHeading.getText().toString().trim().isEmpty()){
            edtHeading.setError("Enter heading");
            loadingBar.dismiss();
        }else if(edtDescription.getText().toString().trim().equals("") || edtDescription.getText().toString().trim().isEmpty()){
            edtDescription.setError("Enter description");
            loadingBar.dismiss();
        }else{
            String heading = edtHeading.getText().toString().trim();
            String description = edtDescription.getText().toString().trim();

            FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
            String generalID = rootNode.getReference("General announcements").push().getKey();
            generalReference = rootNode.getReference("General announcements").child(generalID);

            GeneralAnnouncement generalAnnouncement = new GeneralAnnouncement(generalID,date,heading,description,"admin");
            generalReference.setValue(generalAnnouncement).addOnCompleteListener(v->{
                loadingBar.dismiss();
                alertDialog.show();
                clearViewGeneral();

                String reporter = AdminDashActivity.LOGGED_IN_ADMIN.getFullNames();
                //notify everyone
                MyNotifications notifications = new MyNotifications(AnnounceActivity.this);
                notifications.sendNotificationToReports(reporter,"General announcement","posted an announcement, log in for more details");

            }).addOnFailureListener(v-> Toast.makeText(this, "Something went wrong, try again", Toast.LENGTH_SHORT).show());
        }
    }

    /*
    Start of search funeral
     */
    private void populateSearch(DataSnapshot snapshot) {
        ArrayList<String> names = new ArrayList<>();
        if(snapshot.exists()){
            for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                String name = dataSnapshot.child("name").getValue(String.class);
                names.add(name);
            }

            ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,names);
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
        Query query = funeralReference.orderByChild("name").equalTo(name);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    searchedFuneralList = new ArrayList<>();

                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        Funeral funeral = dataSnapshot.getValue(Funeral.class);
                        searchedFuneralList.add(funeral);
                        String name=funeral.getName();
                        displaySingleFuneralDialog(funeral.getName(),funeral.getAddress(),funeral.getWhenHappened(),funeral.getWhenBurial(),funeral.getContacts(),funeral.getExtras());
                    }
                    AdminFuneralAdapter adapter = new AdminFuneralAdapter(AnnounceActivity.this, searchedFuneralList);

                }else{
                    Log.d("user","No data found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void displaySingleFuneralDialog(String name, String address, String whenHappened, String whenBurial, String contacts, String extra){
        final AlertDialog.Builder builder = new AlertDialog.Builder(AnnounceActivity.this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_single_funeral, viewGroup, false);
        Button cancel=dialogView.findViewById(R.id.button_custom_single_cancel);
        EditText edtSingName, edtSingAddress, edtSingWhenHap, edSingWhenBur, edtSingContact, edtSingExtra;

        Button update=dialogView.findViewById(R.id.button_custom_single_update);
        edtSingName = dialogView.findViewById(R.id.edit_custom_single_name);
        edtSingAddress = dialogView.findViewById(R.id.edit_custom_single_address);
        edtSingWhenHap = dialogView.findViewById(R.id.edit_custom_single_when_happened);
        edSingWhenBur = dialogView.findViewById(R.id.edit_custom_single_when_burial);
        edtSingContact = dialogView.findViewById(R.id.edit_custom_single_contacts);
        edtSingExtra = dialogView.findViewById(R.id.edit_custom_single_extra);

        edtSingName.setText(name);
        edtSingAddress.setText(address);
        edtSingWhenHap.setText(whenHappened);
        edSingWhenBur.setText(whenBurial);
        edtSingContact.setText(contacts);
        edtSingExtra.setText(extra);

        builder.setView(dialogView);
        builder.setCancelable(false);

        final AlertDialog alertDialog = builder.create();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    //*******************************End of search funeral**********************************

    public void getAllAllFuneralAnnouncements() {

        progressBarLoadFunerals.setVisibility(View.VISIBLE);
        funeralReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBarLoadFunerals.setVisibility(View.GONE);
                funerals = new ArrayList<>();

                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    funeral = dataSnapshot1.getValue(Funeral.class);
                    funerals.add(funeral);
                }
                Collections.reverse(funerals);
                AdminFuneralAdapter adapter = new AdminFuneralAdapter(AnnounceActivity.this, funerals);
                rclFuneral.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void getAllAllGeneralAnnouncements() {

        progressBarLoadGeneral.setVisibility(View.VISIBLE);
        generalReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBarLoadGeneral.setVisibility(View.GONE);
                generals = new ArrayList<>();

                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    generalAnnouncement = dataSnapshot1.getValue(GeneralAnnouncement.class);
                    generals.add(generalAnnouncement);
                }
                Collections.reverse(funerals);
                AdminGeneralAdapter adapter = new AdminGeneralAdapter(AnnounceActivity.this, generals);
                rclGeneral.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void backFromAnnounce(View view){
        this.finish();
    }


    private void clearView(){
        edtName.setText("");
        edtAddress.setText("");
        edtWhenBurial.setText("");
        edtContacts.setText("");
        edtExtras.setText("");
    }

    private void clearViewGeneral(){
        edtDescription.setText("");
        edtHeading.setText("");
    }
}