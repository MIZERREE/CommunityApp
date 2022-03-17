package com.ree.mizer.communityapp.roles.household;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ree.mizer.communityapp.R;
import com.ree.mizer.communityapp.pojos.MyNotifications;
import com.ree.mizer.communityapp.pojos.User;
import com.ree.mizer.communityapp.pojos.admin.Asset;
import com.ree.mizer.communityapp.pojos.fcm.FcmNotificationsSender;
import com.ree.mizer.communityapp.pojos.user.Incident;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class ReportIncidentFragment extends Fragment {

    String TAG = "*** Community App ***", userFullNames;
    String incidentID, userID, description, location, status, date;
    Button btnReport, btnTest;

    private EditText edReporter, edDescription, edLocation;
    private TextView tvDate;

    ArrayList<User> searchedUserList;

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
        View view = inflater.inflate(R.layout.fragment_report_incident, container, false);

        edDescription = view.findViewById(R.id.user_text_report_description);
        edReporter = view.findViewById(R.id.user_text_report_name);
        edLocation = view.findViewById(R.id.user_text_report_location);
        btnReport = view.findViewById(R.id.user_btn_report);
        btnTest = view.findViewById(R.id.user_btn_test);
        tvDate = view.findViewById(R.id.text_incident_date);

        //Notifications
        //FirebaseMessaging.getInstance().subscribeToTopic("news");

        btnReport.setOnClickListener(v->reportIncident());
        btnTest.setOnClickListener(v -> {
            if(edDescription.getText().equals("") || edDescription.getText().toString().isEmpty()){
                Toast.makeText(context, "Enter text first ", Toast.LENGTH_SHORT).show();
            }else{
                FcmNotificationsSender notificationsSender = new FcmNotificationsSender("/topics/news","Notification test", edDescription.getText().toString()
                        ,context,getActivity());
                notificationsSender.SendNotifications();
            }

        });

        //current date
        String currentDate = new SimpleDateFormat("EEE dd-MM-yyyy", Locale.getDefault()).format(new Date());
        date = currentDate;
        tvDate.setText(currentDate);

        //get logged in user
        getLoggedInUser();

        return view;
    }

    private void getLoggedInUser(){
        if (HouseHoldDashActivity.LOGGED_IN_USER != null){
            userFullNames = HouseHoldDashActivity.LOGGED_IN_USER.getFullNames()+" "+HouseHoldDashActivity.LOGGED_IN_USER.getSurname();

            userID = HouseHoldDashActivity.LOGGED_IN_USER.getUserID();
            edReporter.setText(userFullNames);
        }else{
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void reportIncident(){
        //alert
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Incident report");
        builder.setMessage("Incident reported to admin and will be posted soon");
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
        if(edReporter.getText().toString().trim().equals("") || edReporter.getText().toString().trim().isEmpty()){
            edReporter.setError("Enter  name");
        }else if(edLocation.getText().toString().trim().equals("") || edLocation.getText().toString().trim().isEmpty()){
            edLocation.setError("Where did this happen?");
        }else if(edDescription.getText().toString().trim().equals("") || edDescription.getText().toString().trim().isEmpty()){
            edDescription.setError("What happened");
        }else{
            String reporter = edReporter.getText().toString().trim();
            String location = edLocation.getText().toString().trim();
            String description = edDescription.getText().toString().trim();;


            FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
            String incidentID = rootNode.getReference("Incident reports").push().getKey();
            DatabaseReference incidentReference = rootNode.getReference("Incident reports").child(incidentID);

            Incident incident = new Incident(incidentID, userID,date, description, location, "approved");
            incidentReference.setValue(incident).addOnCompleteListener(v->{
                alertDialog.show();
                clearView();

                //notify everyone
                MyNotifications notifications = new MyNotifications(context);
                notifications.sendNotificationToReports(reporter,"Incident report","Reported and incident, log in for more details");

                //push notification
                FcmNotificationsSender notificationsSender = new FcmNotificationsSender("/topics/news","Incident report", "A user reported an incident please sign in for more details"
                        ,context,getActivity());
                notificationsSender.SendNotifications();

            }).addOnFailureListener(v-> Toast.makeText(context, "Something went wrong, try again", Toast.LENGTH_SHORT).show());
        }
    }

    private void clearView() {
        edReporter.setText("");
        edLocation.setText("");
        edDescription.setText("");
    }

}