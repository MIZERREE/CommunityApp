package com.ree.mizer.communityapp.roles.admin;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ree.mizer.communityapp.R;
import com.ree.mizer.communityapp.adapters.AdminGeneralAdapter;
import com.ree.mizer.communityapp.adapters.FuneralAdapter;
import com.ree.mizer.communityapp.pojos.admin.Funeral;
import com.ree.mizer.communityapp.pojos.admin.GeneralAnnouncement;
import com.ree.mizer.communityapp.pojos.user.Incident;

import java.util.ArrayList;
import java.util.Collections;


public class AdminDashFragment extends Fragment {

    private ProgressBar progressBarLoadIncidents;

    RecyclerView recyclerViewFunerals, recyclerViewGeneral, recyclerViewIncidents;
    ArrayList<Funeral> funerals = new ArrayList<>();
    ArrayList<GeneralAnnouncement> generals = new ArrayList<>();
    GeneralAnnouncement generalAnnouncement;
    Funeral funeral;
    RecyclerView.LayoutManager layoutManagerFunerals;
    RecyclerView.LayoutManager layoutManagerMeetings;
    RecyclerView.LayoutManager layoutManagerIncidents;
    private ProgressBar progressBarLoad,progressBarLoadGeneral;

    ArrayList<GeneralAnnouncement> generalAnnouncementArrayList;
    Incident incident;

    public static int userAnnouncements;

    Button btnAnnounce;

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
        View view = inflater.inflate(R.layout.fragment_admin_dash, container, false);

        recyclerViewFunerals = view.findViewById(R.id.admin_rcl_funerals);
        recyclerViewGeneral = view.findViewById(R.id.admin_rcl_gen_announcements);
        recyclerViewIncidents = view.findViewById(R.id.admin_rcl_reports_dash);
        progressBarLoadIncidents = view.findViewById(R.id.simpleProgressBarIncidentsDash);
        progressBarLoad = view.findViewById(R.id.simpleProgressBarAdminFun);
        progressBarLoadGeneral = view.findViewById(R.id.simpleProgressBarAdminGen);
        btnAnnounce = view.findViewById(R.id.user_button_announce);

        layoutManagerFunerals = new LinearLayoutManager(context);
        layoutManagerMeetings = new LinearLayoutManager(context);
        layoutManagerIncidents = new LinearLayoutManager(context);
        recyclerViewFunerals.setLayoutManager(layoutManagerFunerals);
        recyclerViewGeneral.setLayoutManager(layoutManagerMeetings);
        recyclerViewIncidents.setLayoutManager(layoutManagerIncidents);

        getAllAllIncidentsReport();
        getAllAllFuneralAnnouncements();
        getAllAllGeneralAnnouncements();

        btnAnnounce.setOnClickListener(v ->{
            announcements();
        });

        return  view;
    }

    public void getAllAllGeneralAnnouncements() {

        progressBarLoadGeneral.setVisibility(View.VISIBLE);
        AdminDashActivity.GENERAL_REFERENCE.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBarLoadGeneral.setVisibility(View.GONE);
                generals = new ArrayList<>();

                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    generalAnnouncement = dataSnapshot1.getValue(GeneralAnnouncement.class);
                    generals.add(generalAnnouncement);
                    if(generalAnnouncement.getAnnouncer().equals("user")){
                        userAnnouncements = userAnnouncements+1;
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

    public void getAllAllIncidentsReport() {

        progressBarLoadIncidents.setVisibility(View.VISIBLE);
        AdminDashActivity.INCIDENT_REFERENCE.addValueEventListener(new ValueEventListener() {
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
                AdminGeneralAdapter adapter = new AdminGeneralAdapter(context, generalAnnouncementArrayList);
                recyclerViewIncidents.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void getAllAllFuneralAnnouncements() {
        progressBarLoad.setVisibility(View.VISIBLE);
        AdminDashActivity.FUNERAL_REFERENCE.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBarLoad.setVisibility(View.GONE);
                funerals = new ArrayList<>();

                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    funeral = dataSnapshot1.getValue(Funeral.class);
                    funerals.add(funeral);
                    if(funeral.getAnnouncer().equals("user")){
                        userAnnouncements = userAnnouncements+1;
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


    public void announcements(){
        startActivity(new Intent(context,AnnounceActivity.class));
    }
}