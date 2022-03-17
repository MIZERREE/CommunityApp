package com.ree.mizer.communityapp.roles.tribal;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.ree.mizer.communityapp.pojos.admin.Funeral;
import com.ree.mizer.communityapp.pojos.admin.GeneralAnnouncement;
import com.ree.mizer.communityapp.pojos.user.Incident;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;


public class TribalDashboadFragment extends Fragment {

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

    public Context context;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context=context;
    }

    public TribalDashboadFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tribal_dashboad, container, false);

        //trying to subscribe to notifications
        FirebaseMessaging.getInstance().subscribeToTopic("tribal");

        GENERAL_REFERENCE = FirebaseDatabase.getInstance().getReference("General announcements");
        INCIDENT_REFERENCE = FirebaseDatabase.getInstance().getReference("Incident reports");
        FINANCIAL_CONTRIBUTIONS = FirebaseDatabase.getInstance().getReference("Financial contributions");
        funeralReference = FirebaseDatabase.getInstance().getReference("Funeral announcements");

        edtName = view.findViewById(R.id.text_tribal_name);
        edtDescription = view.findViewById(R.id.text_tribal_description);
        recyclerViewFunerals = view.findViewById(R.id.tribal_rcl_funerals);
        recyclerViewGeneral = view.findViewById(R.id.tribal_rcl_gen_announcements);
        recyclerViewIncidents = view.findViewById(R.id.tribal_rcl_reports_dash);
        progressBarLoad = view.findViewById(R.id.simpleProgressBarTribal);
        progressBarLoadGeneral = view.findViewById(R.id.simpleProgressBarTribalGen);
        progressBarLoadIncidents = view.findViewById(R.id.simpleProgressBarIncidentsDashTribal);
        btnAnnounce = view.findViewById(R.id.tribal_btn_broadcast);

        layoutManagerFunerals = new LinearLayoutManager(context);
        layoutManagerMeetings = new LinearLayoutManager(context);
        layoutManagerIncidents = new LinearLayoutManager(context);
        recyclerViewFunerals.setLayoutManager(layoutManagerFunerals);
        recyclerViewGeneral.setLayoutManager(layoutManagerMeetings);
        recyclerViewIncidents.setLayoutManager(layoutManagerIncidents);


        loadingBar = new ProgressDialog(context);

        //current date
        String currentDate = new SimpleDateFormat("EEE dd-MM-yyyy", Locale.getDefault()).format(new Date());
        date = currentDate;

        btnAnnounce.setOnClickListener(v->{
            broadcast();
        });

        getAllAllFuneralAnnouncements();
        getAllAllGeneralAnnouncements();
        getAllAllIncidentsReport();

        return view;
    }

    public void broadcast(){

        //alert
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
            }).addOnFailureListener(v-> Toast.makeText(context, "Something went wrong, try again", Toast.LENGTH_SHORT).show());
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
                AdminGeneralAdapter adapter = new AdminGeneralAdapter(context, generals);
                recyclerViewGeneral.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}