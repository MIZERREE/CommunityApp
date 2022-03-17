package com.ree.mizer.communityapp.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ree.mizer.communityapp.R;
import com.ree.mizer.communityapp.pojos.admin.GeneralAnnouncement;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdminGeneralAdapter extends RecyclerView.Adapter<AdminGeneralAdapter.ViewHolder>{
    Context context;
    ArrayList<GeneralAnnouncement> generalList;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    public AdminGeneralAdapter(@NonNull Context context, ArrayList<GeneralAnnouncement> generalList) {
        this.context = context;
        this.generalList = generalList;

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("General announcements");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_admin_general, parent, false);
        AdminGeneralAdapter.ViewHolder viewHolder = new AdminGeneralAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemView.setTag(generalList.get(position));

        GeneralAnnouncement announcement = generalList.get(position);

        holder.tvHeading.setText(announcement.getHeading());
        holder.tvDescription.setText(announcement.getAnnouncement());
        holder.tvDate.setText(announcement.getDate());
        if(announcement.getAnnouncer().equals("user")){
            holder.tvHeading.setTextColor(Color.BLUE);
            holder.tvHeading.setBackgroundColor(Color.GRAY);
        }

    }

    @Override
    public int getItemCount() {
        return generalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvHeading;
        TextView tvDescription ;
        TextView tvDate ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeading = itemView.findViewById(R.id.edit_custom_gen_heading);
            tvDate = itemView.findViewById(R.id.edit_custom_gen_date);
            tvDescription = itemView.findViewById(R.id.edit_custom_gen_announcement);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    /*Funeral funeral = (Funeral) view.getTag();
                    displaySingleFuneralDialog(funeral.getName(),funeral.getAddress(),funeral.getWhenHappened(),funeral.getWhenBurial(),funeral.getContacts(),funeral.getExtras(),view,funeral.getFuneralID());*/

                }
            });

        }
    }


    private void displaySingleFuneralDialog(String name, String address, String whenHappened, String whenBurial, String contacts, String extra, View view, String key){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        ViewGroup viewGroup = view.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.custom_single_funeral, viewGroup, false);
        Button btnCancel=dialogView.findViewById(R.id.button_custom_single_cancel);
        EditText edtSingName, edtSingAddress, edtSingWhenHap, edSingWhenBur, edtSingContact, edtSingExtra;

        Button btnUpdate = dialogView.findViewById(R.id.button_custom_single_update);
        Button btnDelete = dialogView.findViewById(R.id.button_custom_single_delete);
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
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //***********************Ready to update*******************
                if(TextUtils.isEmpty(edtSingName.getText())){
                    Toast.makeText(context, "Please enter  name before you continue", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(edtSingAddress.getText())){
                    Toast.makeText(context, "Please enter your address before you continue", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(edtSingWhenHap.getText())){
                    Toast.makeText(context, "When did the funeral happen", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(edSingWhenBur.getText().toString())){
                    Toast.makeText(context, "When is the burial", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(edtSingContact.getText().toString())){
                    Toast.makeText(context, "Who to contact", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(edtSingExtra.getText().toString())){
                    Toast.makeText(context, "Extras missing", Toast.LENGTH_SHORT).show();
                }else{
                    HashMap<String, Object> userMap = new HashMap<>();
                    userMap.put("address", edtSingAddress.getText().toString().trim());
                    userMap.put("contacts", edtSingContact.getText().toString().trim());
                    userMap.put("extras", edtSingExtra.getText().toString().trim());
                    userMap.put("name", edtSingName.getText().toString().trim());
                    userMap.put("whenBurial", edSingWhenBur.getText().toString().trim());
                    userMap.put("whenHappened", edtSingWhenHap.getText().toString().trim());


                    databaseReference.child(key).updateChildren(userMap);
                    Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show();

                }

                //******************EOU************************************
                alertDialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Do you really want to remove this funeral announcement?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                databaseReference.child(key).removeValue();
                                alertDialog.dismiss();
                                dialog.dismiss();
                                Toast.makeText(context, "Funeral announcement removed", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                alertDialog.dismiss();
                                dialog.dismiss();
                            }
                        });
                // Create the AlertDialog object and return it
                builder.create();
                builder.show();
            }
        });
        alertDialog.show();
    }

}
