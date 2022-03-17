package com.ree.mizer.communityapp.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ree.mizer.communityapp.R;
import com.ree.mizer.communityapp.javaMail.JavaMailAPI;
import com.ree.mizer.communityapp.pojos.MyNotifications;
import com.ree.mizer.communityapp.pojos.User;
import com.ree.mizer.communityapp.roles.admin.AdminDashActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

public class

UserManagerAdapter extends RecyclerView.Adapter<UserManagerAdapter.ViewHolder>{

    Context context;
    ArrayList<User> userArrayList;
    //For notifications
    private RequestQueue mRequestQueue;
    private String URL = "https://fcm.googleapis.com/fcm/send";

    //spinner user groups
    String selectedGroup, selectedStatus, selectedRole;
    int sp_position;

    DatabaseReference userReference;

    public UserManagerAdapter(@NonNull Context context, ArrayList<User> userArrayList) {
        this.context = context;
        this.userArrayList = userArrayList;
        userReference = FirebaseDatabase.getInstance().getReference("Users");
        //For notifications
        mRequestQueue = Volley.newRequestQueue(context);
    }

    @NonNull
    @Override
    public UserManagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_admin_users, parent, false);
        UserManagerAdapter.ViewHolder viewHolder = new UserManagerAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserManagerAdapter.ViewHolder holder, int position) {
        holder.itemView.setTag(userArrayList.get(position));

        User user = userArrayList.get(position);

        holder.txtFirstName.setText(user.getFullNames());
        holder.txtLastName.setText(user.getSurname());
        holder.txtAddress.setText(user.getAddress());
        holder.txtPhone.setText(user.getPhoneNumber());
        holder.txtDateRegistered.setText(user.getDateOfRegistration());
        holder.txtDateOfBirth.setText(user.getDateOfBirth());
        holder.txtVillage.setText(user.getVillage());
        holder.txtEmail.setText(user.getEmail());
        holder.txtStatus.setText(user.getStatus());
        holder.txtHasAssets.setText(user.getHasAssets());
        holder.txtHasFines.setText(user.getHasFines());
        holder.txtGroup.setText(user.getGroup());
        holder.txtRole.setText(user.getRole());

        holder.btnUpdate.setText("Update User ("+user.getFullNames()+")");

        holder.btnUpdate.setOnClickListener(v->{
            updateUser(user.getUserID(),holder.txtFirstName.getText().toString(),holder.txtLastName.getText().toString(),holder.txtAddress.getText().toString(),
                    holder.txtPhone.getText().toString(),holder.txtVillage.getText().toString(),holder.txtRole.getText().toString());
        });
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtFirstName,txtLastName, txtAddress,txtPhone,txtDateRegistered,txtDateOfBirth,txtVillage,txtEmail,txtGroup, txtRole, txtStatus, txtHasAssets, txtHasFines;
        Button btnUpdate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFirstName = itemView.findViewById(R.id.admin_update_full_name);
            txtLastName = itemView.findViewById(R.id.admin_update_last_name);
            txtAddress = itemView.findViewById(R.id.admin_update_address);
            txtPhone = itemView.findViewById(R.id.admin_update_phone);
            txtDateRegistered = itemView.findViewById(R.id.admin_update_DOR);
            txtDateOfBirth = itemView.findViewById(R.id.admin_update_DOB);
            txtVillage = itemView.findViewById(R.id.admin_update_village);
            txtEmail = itemView.findViewById(R.id.admin_update_email);
            txtGroup = itemView.findViewById(R.id.admin_update_group);
            txtRole = itemView.findViewById(R.id.admin_update_role);
            txtStatus = itemView.findViewById(R.id.admin_update_status);
            txtHasAssets = itemView.findViewById(R.id.admin_update_has_assets);
            txtHasFines = itemView.findViewById(R.id.admin_update_has_fines);
            btnUpdate = itemView.findViewById(R.id.button_admin_update);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    User user = (User) view.getTag();
                    String names = user.getFullNames()+" "+user.getSurname();
                    displaySingleCrimeDialog(names,user.getStatus(),user.getGroup(),user.getRole(),user.getHasFines(),user.getHasAssets(), user.getUserID(), user.getEmail());
                }
            });

        }
    }

    private void displaySingleCrimeDialog(String name, String status,String group, String role, String fines,String assets, String userID, String userEmail ){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.user_icon);
        builder.setTitle("User Information");
        //builder.setMessage("Status: "+status+"\nGroup: "+group+"\nMember fined?: "+fines+"\nHas assets?: "+assets);

        // set the custom layout
        View custom_layout = View.inflate(context,R.layout.custom_admin_activate_user,null);
        builder.setView(custom_layout);

        //find views
        TextView tvName = custom_layout.findViewById(R.id.ed_name);
        TextView tvFines = custom_layout.findViewById(R.id.ed_fines);
        TextView tvAssets = custom_layout.findViewById(R.id.ed_assets);
        TextView tvMessage = custom_layout.findViewById(R.id.tv_msg);
        //TextView tvRole = custom_layout.findViewById(R.id.tv_role);
        SwitchCompat swStatus = custom_layout.findViewById(R.id.sp_status);
        Spinner spGroups = custom_layout.findViewById(R.id.sp_group);
        Spinner spRoles = custom_layout.findViewById(R.id.sp_role);
        //Spinner spRole = custom_layout.findViewById(R.id.sp_role);
        Button btnCancel = custom_layout.findViewById(R.id.tbn_close);
        Button btnUpdate = custom_layout.findViewById(R.id.tbn_update);

        //Get all groups from admin
        ArrayList<String> groupList = new ArrayList<>();
        for(int x=0; x<AdminDashActivity.groupsArrayList.size(); x++){
            groupList.add(AdminDashActivity.groupsArrayList.get(x).getGroupName());
        }

        //User roles
        String[] userRoles = {"Admin","Tribal", "Household"};

        //assigning values
        tvName.setText(name);
        tvFines.setText(fines);
        tvAssets.setText(assets);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter adapter = new ArrayAdapter(context,android.R.layout.simple_spinner_item,groupList);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spGroups.setAdapter(adapter);


        /*
        *Start of User Role*/

        ArrayAdapter roleAdapter = new ArrayAdapter(context,android.R.layout.simple_spinner_item,userRoles);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRoles.setAdapter(roleAdapter);

        /*
         *End of User Role*/


        //set group status
        if(group.equals("Thoto")){
            spGroups.setSelection(0);
        }else if(group.equals("Mampheko")){
            spGroups.setSelection(1);
        }else if(group.equals("Sethaseng")){
            spGroups.setSelection(2);
        }else if(group.equals("Naledi")){
            spGroups.setSelection(3);
        }else if(group.equals("Mabokeng")){
            spGroups.setSelection(4);
        }else if(group.equals("Mabemane")){
            spGroups.setSelection(5);
        }else if(group.equals("Mokgoloti")){
            spGroups.setSelection(6);
        }else if(group.equals("No group")){
            spGroups.setSelection(7);
        }else if(group.equals("Sephukubje")){
            spGroups.setSelection(8);
        }else if(group.equals("Mamaila family society")){
            spGroups.setSelection(9);
        }

        //set user role
        if(role.equals("Household")){
            spRoles.setSelection(2);
        }else if(role.equals("Tribal")){
            spRoles.setSelection(1);
        }else if(role.equals("Admin")){
            spRoles.setSelection(0);
        }

        //spinner select group
        spGroups.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGroup = spGroups.getSelectedItem().toString();
                Log.d("UserManagement: ","Group is: "+group+" selected is: "+selectedGroup);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spRoles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRole = spRoles.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //switch status
        swStatus.setText(status);
        if (swStatus.getText().toString().equals("Active"))
            swStatus.setChecked(true);

        swStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                swStatus.setText("Active");
            }else {
                swStatus.setText("Not active");
            }
            selectedStatus = swStatus.getText().toString();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        //Button close
        btnCancel.setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        //Button update
        btnUpdate.setOnClickListener(v->{
            AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
            builder2.setIcon(R.drawable.user_icon);
            builder2.setTitle("Member update");
            builder2.setMessage("Are you sure you want to update user information for: "+name);
            builder2.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MyNotifications notifications = new MyNotifications(context);
                    notifications.sendNotification("Community app", "Nothing was changed in your account");
                    dialog.dismiss();
                }
            }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    updateUserNGroup(userID,selectedStatus,selectedGroup,selectedRole);
                    String email = userEmail;
                    String message = "Dear, "+name+"\nYour Mamaila community app account was updated, please use you email and password to sign in to the system.";
                    String subject = "Account update";


                    MyNotifications notifications = new MyNotifications(context);
                    notifications.sendNotification("Community app", name+"'s account was updated");

                    //send email
                    sendEmail(email,subject, message);

                    alertDialog.dismiss();
                    Toast.makeText(context, "Member updated", Toast.LENGTH_SHORT).show();
                }
            });

            AlertDialog alertDialog2 = builder2.create();
            alertDialog2.setCancelable(false);
            alertDialog2.show();


        });
    }

    //send email
    private void sendEmail(String mEmail, String mSubject, String mMessage) {

        JavaMailAPI javaMailAPI = new JavaMailAPI(context, mEmail, mSubject, mMessage);

        javaMailAPI.execute();
    }

    private void updateUserNGroup(String userID, String status, String group, String role){
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("status", status);
        userMap.put("group", group);
        userMap.put("role", role);
        userReference.child(userID).updateChildren(userMap);
    }

    public void updateUser(String userID,String fullNames,String surname,String address,String phoneNumber,String  village,String role){
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("fullNames", fullNames);
        userMap.put("surname", surname);
        userMap.put("address", address);
        userMap.put("phoneNumber", phoneNumber);
        userMap.put("village", village);
        userMap.put("role", role);
        userReference.child(userID).updateChildren(userMap);
    }

}
