package com.ree.mizer.communityapp.roles.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ree.mizer.communityapp.R;
import com.ree.mizer.communityapp.adapters.AdminGeneralAdapter;
import com.ree.mizer.communityapp.pojos.admin.GeneralAnnouncement;
import com.ree.mizer.communityapp.pojos.user.Incident;

import java.util.ArrayList;
import java.util.Collections;

public class IncidentActivity extends AppCompatActivity {

    private ProgressBar progressBarLoadIncidents;
    //firebase
    DatabaseReference incidentsReference;

    ArrayList<Incident> incidentArrayList;
    ArrayList<GeneralAnnouncement> generalAnnouncementArrayList;
    GeneralAnnouncement generalAnnouncement;
    Incident incident;

    RecyclerView rclIncidents;
    RecyclerView.LayoutManager layoutManagerIncidents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident);

        incidentsReference = FirebaseDatabase.getInstance().getReference("Incident reports");

        progressBarLoadIncidents = findViewById(R.id.simpleProgressBarIncidents);
        rclIncidents = findViewById(R.id.admin_rcl_reports);

        layoutManagerIncidents = new LinearLayoutManager(this);
        rclIncidents.setLayoutManager(layoutManagerIncidents);

        getAllAllIncidentsReport();
    }

    public void getAllAllIncidentsReport() {

        progressBarLoadIncidents.setVisibility(View.VISIBLE);
        incidentsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBarLoadIncidents.setVisibility(View.GONE);
                generalAnnouncementArrayList = new ArrayList<>();

                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    incident = dataSnapshot1.getValue(Incident.class);
                    generalAnnouncement = new GeneralAnnouncement(incident.getIncidentID(),incident.getDate(),"Incident Report",incident.getDescription(),"Community member");
                    generalAnnouncementArrayList.add(generalAnnouncement);
                }
                Collections.reverse(generalAnnouncementArrayList);
                AdminGeneralAdapter adapter = new AdminGeneralAdapter(IncidentActivity.this, generalAnnouncementArrayList);
                rclIncidents.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}