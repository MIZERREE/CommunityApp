package com.ree.mizer.communityapp.roles.household;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ree.mizer.communityapp.R;
import com.ree.mizer.communityapp.adapters.AdminGeneralAdapter;
import com.ree.mizer.communityapp.adapters.FuneralAdapter;
import com.ree.mizer.communityapp.pojos.User;
import com.ree.mizer.communityapp.pojos.admin.Funeral;
import com.ree.mizer.communityapp.pojos.admin.GeneralAnnouncement;
import com.ree.mizer.communityapp.pojos.admin.Meeting;
import com.ree.mizer.communityapp.pojos.user.Incident;
import com.ree.mizer.communityapp.roles.admin.AdminDashActivity;

import java.util.ArrayList;
import java.util.Collections;


public class UserDashFragment extends Fragment {

    RecyclerView recyclerViewFunerals, recyclerViewGeneral,recyclerViewIncidents;
    ArrayList<Funeral> funerals = new ArrayList<>();;
    ArrayList<GeneralAnnouncement> generals = new ArrayList<>();;
    Funeral funeral;
    GeneralAnnouncement generalAnnouncement;
    private Button btnAnnounce;

    RecyclerView.LayoutManager layoutManagerFunerals;
    RecyclerView.LayoutManager layoutManagerMeetings;
    RecyclerView.LayoutManager layoutManagerIncidents;

    DatabaseReference funeralReference;

    private ProgressBar progressBarLoadIncidents;
    private ProgressBar progressBarLoad, progressBarLoadGeneral;

    ArrayList<GeneralAnnouncement> generalAnnouncementArrayList;
    Incident incident;

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
        View view =  inflater.inflate(R.layout.fragment_user_dash, container, false);

        recyclerViewFunerals = view.findViewById(R.id.user_rcl_funerals);
        recyclerViewGeneral = view.findViewById(R.id.user_rcl_gen_announcements);
        recyclerViewIncidents = view.findViewById(R.id.user_rcl_reports_dash);
        progressBarLoad = view.findViewById(R.id.simpleProgressBarUsers);
        progressBarLoadGeneral = view.findViewById(R.id.simpleProgressBarUsersGen);
        progressBarLoadIncidents = view.findViewById(R.id.simpleProgressBarIncidentsDashUser);
        btnAnnounce = view.findViewById(R.id.user_button_announce);

        funeralReference = FirebaseDatabase.getInstance().getReference("Funeral announcements");

        layoutManagerFunerals = new LinearLayoutManager(context);
        layoutManagerMeetings = new LinearLayoutManager(context);
        layoutManagerIncidents = new LinearLayoutManager(context);
        recyclerViewFunerals.setLayoutManager(layoutManagerFunerals);
        recyclerViewGeneral.setLayoutManager(layoutManagerMeetings);
        recyclerViewIncidents.setLayoutManager(layoutManagerIncidents);

        btnAnnounce.setOnClickListener(v -> makeAnnouncement());

        getAllAllFuneralAnnouncements();
        getAllAllGeneralAnnouncements();
        getAllAllIncidentsReport();

        return view;
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
                FuneralAdapter adapter = new FuneralAdapter(context, funerals);
                recyclerViewFunerals.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void getAllAllIncidentsReport() {

        progressBarLoadIncidents.setVisibility(View.VISIBLE);
        HouseHoldDashActivity.INCIDENT_REFERENCE.addValueEventListener(new ValueEventListener() {
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
                AdminGeneralAdapter adapter = new AdminGeneralAdapter(context, generalAnnouncementArrayList);
                recyclerViewIncidents.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void getAllAllGeneralAnnouncements() {

        progressBarLoadGeneral.setVisibility(View.VISIBLE);
        HouseHoldDashActivity.GENERAL_REFERENCE.addValueEventListener(new ValueEventListener() {
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
                AdminGeneralAdapter adapter = new AdminGeneralAdapter(context, generals);
                recyclerViewGeneral.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void makeAnnouncement(){
        startActivity(new Intent(context, UserAnnounceActivity.class));
    }

}