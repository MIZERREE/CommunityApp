package com.ree.mizer.communityapp.roles.tribal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ree.mizer.communityapp.R;
import com.ree.mizer.communityapp.adapters.AdminGeneralAdapter;
import com.ree.mizer.communityapp.adapters.FuneralAdapter;
import com.ree.mizer.communityapp.pojos.admin.Asset;
import com.ree.mizer.communityapp.pojos.admin.Funeral;
import com.ree.mizer.communityapp.pojos.admin.GeneralAnnouncement;
import com.ree.mizer.communityapp.pojos.fcm.FcmNotificationsSender;
import com.ree.mizer.communityapp.pojos.user.Incident;
import com.ree.mizer.communityapp.roles.household.HouseHoldDashActivity;
import com.ree.mizer.communityapp.roles.household.UserAnnounceActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class BroadcastActivity extends AppCompatActivity {

    private EditText edtName, edtDescription;

    DatabaseReference generalReference;
    private ProgressDialog loadingBar;

    String date;

    RecyclerView recyclerViewFunerals, recyclerViewGeneral,recyclerViewIncidents;
    ArrayList<Funeral> funerals = new ArrayList<>();;
    ArrayList<GeneralAnnouncement> generals = new ArrayList<>();;
    Funeral funeral;
    GeneralAnnouncement generalAnnouncement;
    private Button btnAnnounce;

    RecyclerView.LayoutManager layoutManagerFunerals;
    RecyclerView.LayoutManager layoutManagerMeetings;
    RecyclerView.LayoutManager layoutManagerIncidents;

    DatabaseReference funeralReference, GENERAL_REFERENCE, INCIDENT_REFERENCE, FINANCIAL_CONTRIBUTIONS;

    private ProgressBar progressBarLoadIncidents;
    private ProgressBar progressBarLoad, progressBarLoadGeneral;

    ArrayList<GeneralAnnouncement> generalAnnouncementArrayList;
    Incident incident;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast);

        //trying to subscribe to notifications
        FirebaseMessaging.getInstance().subscribeToTopic("tribal");

        GENERAL_REFERENCE = FirebaseDatabase.getInstance().getReference("General announcements");
        INCIDENT_REFERENCE = FirebaseDatabase.getInstance().getReference("Incident reports");
        FINANCIAL_CONTRIBUTIONS = FirebaseDatabase.getInstance().getReference("Financial contributions");
        funeralReference = FirebaseDatabase.getInstance().getReference("Funeral announcements");

        edtName = findViewById(R.id.text_tribal_name);
        edtDescription = findViewById(R.id.text_tribal_description);
        recyclerViewFunerals = findViewById(R.id.tribal_rcl_funerals);
        recyclerViewGeneral = findViewById(R.id.tribal_rcl_gen_announcements);
        recyclerViewIncidents = findViewById(R.id.tribal_rcl_reports_dash);
        progressBarLoad = findViewById(R.id.simpleProgressBarTribal);
        progressBarLoadGeneral = findViewById(R.id.simpleProgressBarTribalGen);
        progressBarLoadIncidents = findViewById(R.id.simpleProgressBarIncidentsDashTribal);

        layoutManagerFunerals = new LinearLayoutManager(this);
        layoutManagerMeetings = new LinearLayoutManager(this);
        layoutManagerIncidents = new LinearLayoutManager(this);
        recyclerViewFunerals.setLayoutManager(layoutManagerFunerals);
        recyclerViewGeneral.setLayoutManager(layoutManagerMeetings);
        recyclerViewIncidents.setLayoutManager(layoutManagerIncidents);


        loadingBar = new ProgressDialog(this);

        //current date
        String currentDate = new SimpleDateFormat("EEE dd-MM-yyyy", Locale.getDefault()).format(new Date());
        date = currentDate;

        getAllAllFuneralAnnouncements();
        getAllAllGeneralAnnouncements();
        getAllAllIncidentsReport();

    }

    public void broadcast(View view){

        //alert
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tribal");
        builder.setMessage("A broadcast message was sent to everyone in the group");
        builder.setIcon(R.drawable.info_icon);
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        //end of alert dialog

        if(edtName.getText().toString().trim().isEmpty()){
            edtName.setError("Enter name");
        }else if(edtDescription.getText().toString().isEmpty()){
            edtDescription.setError("Enter message");
        }else{
            FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
            String generalID = rootNode.getReference("General announcements").push().getKey();
            generalReference = rootNode.getReference("General announcements").child(generalID);

            GeneralAnnouncement generalAnnouncement = new GeneralAnnouncement(generalID,date,"Tribal Announcement",edtDescription.getText().toString().trim(),"tribal");
            generalReference.setValue(generalAnnouncement).addOnCompleteListener(v->{
                loadingBar.dismiss();
                alertDialog.show();

                //push notification
                FcmNotificationsSender notificationsSender = new FcmNotificationsSender("/topics/news","Tribal announcement ", "Tribal authority just posted an announcement, log in for more details"
                        ,getApplicationContext(), BroadcastActivity.this);
                notificationsSender.SendNotifications();

            }).addOnFailureListener(v-> Toast.makeText(this, "Something went wrong, try again", Toast.LENGTH_SHORT).show());
            alertDialog.show();
            edtDescription.setText("");
            edtName.setText("");
        }
    }

    public void getAllAllFuneralAnnouncements() {
        progressBarLoad.setVisibility(View.VISIBLE);
        funeralReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBarLoad.setVisibility(View.GONE);
                funerals = new ArrayList<>();

                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    funeral = dataSnapshot1.getValue(Funeral.class);
                    if(funeral.getAnnouncer().equals("admin") || funeral.getAnnouncer().equals("tribal")){
                        funerals.add(funeral);
                    }

                }
                Collections.reverse(funerals);
                FuneralAdapter adapter = new FuneralAdapter(BroadcastActivity.this, funerals);
                recyclerViewFunerals.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void getAllAllIncidentsReport() {

        progressBarLoadIncidents.setVisibility(View.VISIBLE);
        INCIDENT_REFERENCE.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBarLoadIncidents.setVisibility(View.GONE);
                generalAnnouncementArrayList = new ArrayList<>();

                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    incident = dataSnapshot1.getValue(Incident.class);
                    if(incident.getStatus().equals("approved")){
                        generalAnnouncement = new GeneralAnnouncement(incident.getIncidentID(),incident.getDate(),"Incident Report",incident.getDescription(),"Community member");
                        generalAnnouncementArrayList.add(generalAnnouncement);
                    }
                }
                Collections.reverse(generalAnnouncementArrayList);
                AdminGeneralAdapter adapter = new AdminGeneralAdapter(BroadcastActivity.this, generalAnnouncementArrayList);
                recyclerViewIncidents.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void getAllAllGeneralAnnouncements() {

        progressBarLoadGeneral.setVisibility(View.VISIBLE);
        GENERAL_REFERENCE.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBarLoadGeneral.setVisibility(View.GONE);
                generals = new ArrayList<>();

                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    generalAnnouncement = dataSnapshot1.getValue(GeneralAnnouncement.class);
                    //assert generalAnnouncement != null;
                    if(generalAnnouncement.getAnnouncer().equals("admin") || generalAnnouncement.getAnnouncer().equals("tribal")){
                        generals.add(generalAnnouncement);
                    }

                }
                Collections.reverse(generals);
                AdminGeneralAdapter adapter = new AdminGeneralAdapter(BroadcastActivity.this, generals);
                recyclerViewGeneral.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}