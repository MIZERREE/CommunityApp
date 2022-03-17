package com.ree.mizer.communityapp.adapters;

import android.content.Context;
import com.ree.mizer.communityapp.R;
import com.ree.mizer.communityapp.pojos.admin.Funeral;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{
    Context context;
    ArrayList<Funeral> patientsList;

    public UserAdapter(@NonNull Context context, ArrayList<Funeral> patientsList) {
        this.context = context;
        this.patientsList = patientsList;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_admin_announcements, parent, false);
        UserAdapter.ViewHolder viewHolder = new UserAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        holder.itemView.setTag(patientsList.get(position));

        Funeral patient = patientsList.get(position);

        holder.tvHeading.setText(patient.getExtras());
        holder.tvName.setText(patient.getName());
        holder.tvAddress.setText(patient.getAddress());
        holder.tvWhenHappened.setText(patient.getWhenHappened());
        holder.tvBurial.setText(patient.getWhenBurial());
        holder.tvContacts.setText(patient.getContacts());
        holder.tvExtras.setText(patient.getExtras());
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
                    displaySingleCrimeDialog(currentAlert.getExtras(),currentAlert.getName(),currentAlert.getWhenHappened(),currentAlert.getAddress(),currentAlert.getWhenBurial(),currentAlert.getContacts(),view);

                }
            });

        }
    }

    private void displaySingleCrimeDialog(String heading,String name,String whenHappened, String address,String burial, String contact, View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle(heading);
        builder.setMessage("Deceased: "+name+"\nWhen did it happen: "+whenHappened+"\nAddress: "+address+"\nWhen is the burial: "+burial+"\nWho to contact: "+contact);
        builder.setIcon(R.drawable.info_icon);
        builder.setPositiveButton("Attending", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).setNeutralButton("Not attending", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setCancelable(false);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
