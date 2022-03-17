package com.ree.mizer.communityapp.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ree.mizer.communityapp.R;
import com.ree.mizer.communityapp.pojos.admin.Funeral;
import com.ree.mizer.communityapp.roles.admin.AdminDashActivity;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FuneralAdapter extends RecyclerView.Adapter<FuneralAdapter.ViewHolder>{
    Context context;
    ArrayList<Funeral> patientsList;

    public FuneralAdapter(@NonNull Context context, ArrayList<Funeral> patientsList) {
        this.context = context;
        this.patientsList = patientsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_admin_announcements, parent, false);
        FuneralAdapter.ViewHolder viewHolder = new FuneralAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemView.setTag(patientsList.get(position));

        Funeral patient = patientsList.get(position);

        holder.tvHeading.setText(patient.getExtras());
        holder.tvName.setText(patient.getName());
        holder.tvAddress.setText(patient.getAddress());
        holder.tvWhenHappened.setText(patient.getWhenHappened());
        holder.tvBurial.setText(patient.getWhenBurial());
        holder.tvContacts.setText(patient.getContacts());
        holder.tvExtras.setText(patient.getExtras());
        if(patient.getAnnouncer().equals("user")){
            holder.tvHeading.setTextColor(Color.BLUE);
            holder.tvHeading.setBackgroundColor(Color.GRAY);
        }
    }

    @Override
    public int getItemCount() {
        return patientsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvHeading;
        TextView tvName ;
        TextView tvAddress ;
        TextView tvWhenHappened ;
        TextView tvBurial;
        TextView tvContacts;
        TextView tvExtras;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeading = itemView.findViewById(R.id.edit_custom_heading);
            tvName = itemView.findViewById(R.id.edit_custom_name);
            tvAddress = itemView.findViewById(R.id.edit_custom_address);
            tvBurial = itemView.findViewById(R.id.edit_custom_burial);
            tvContacts = itemView.findViewById(R.id.edit_custom_contacts);
            tvExtras = itemView.findViewById(R.id.edit_custom_extras);
            tvWhenHappened = itemView.findViewById(R.id.edit_custom_when_happened);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Funeral currentAlert = (Funeral) view.getTag();
                    if(context == AdminDashActivity.context){
                        displaySingleCrimeDialog(currentAlert.getExtras(),currentAlert.getName(),currentAlert.getWhenHappened(),currentAlert.getAddress(),currentAlert.getWhenBurial(),currentAlert.getContacts(),currentAlert.getFuneralID(),view);
                    }


                }
            });

        }
    }

    private void displaySingleCrimeDialog(String heading, String name,String whenHappened, String address,String burial, String contact,String announcementID, View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Funeral");
        builder.setMessage("Deceased: "+name+"\nWhen did it happen: "+whenHappened+"\nAddress: "+address+"\nWhen is the burial: "+burial+"\nWho to contact: "+contact);
        builder.setIcon(R.drawable.info_icon);
        builder.setNegativeButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).setPositiveButton("Approve", (dialog, which)->{
            approveAnnouncement(announcementID);
        }).setNeutralButton("Delete",(d,w)->{
            deleteAnnouncement(announcementID);
        });
        builder.setCancelable(false);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void approveAnnouncement(String announcementID){
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("announcer", "admin");
        AdminDashActivity.FUNERAL_REFERENCE.child(announcementID).updateChildren(userMap);
    }

    private void deleteAnnouncement(String announcementID){
        AdminDashActivity.FINES_REFERENCE.child(announcementID).removeValue();
    }
}
