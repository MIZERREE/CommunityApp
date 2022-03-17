package com.ree.mizer.communityapp.roles.admin.groups;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ree.mizer.communityapp.R;
import com.ree.mizer.communityapp.pojos.admin.Funeral;
import com.ree.mizer.communityapp.pojos.admin.Group;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GroupsActivity extends AppCompatActivity {

    ImageView imgAddGroup, imgBack;
    Button btnSelectGroup;
    Spinner spGroups;

    String selectedGroup,date;

    DatabaseReference groupReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        groupReference = FirebaseDatabase.getInstance().getReference("Groups");

        spGroups = findViewById(R.id.sp_group_admin);
        imgAddGroup = findViewById(R.id.image_add_group);
        imgBack = findViewById(R.id.image_back_group);
        btnSelectGroup = findViewById(R.id.admin_btn_select_);


        //current date
        String currentDate = new SimpleDateFormat("EEE dd-MM-yyyy", Locale.getDefault()).format(new Date());
        date = currentDate;

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.community_groups, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spGroups.setAdapter(adapter);

        //spinner select group
        spGroups.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGroup = spGroups.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        imgBack.setOnClickListener(v->{
            this.finish();
        });

        imgAddGroup.setOnClickListener(v->{
            addGroup();
        });
    }

    private void addGroup(){
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(GroupsActivity.this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.cutsom_new_group, viewGroup, false);

        EditText edtName=dialogView.findViewById(R.id.edt_new_group);
        Button btnAdd=dialogView.findViewById(R.id.btn_add_group);
        Button btnClose=dialogView.findViewById(R.id.btn_close);


        builder.setView(dialogView);
        builder.setCancelable(false);

        final AlertDialog alertDialog = builder.create();
        btnClose.setOnClickListener(v -> alertDialog.dismiss());
        btnAdd.setOnClickListener(v -> {
            if(edtName.getText().toString().trim().equals("") || edtName.getText().toString().trim().isEmpty()){
                edtName.setError("Enter group name");
            }else {
                newGroup(edtName.getText().toString().trim());
            }

        });

        alertDialog.show();
    }

    private void newGroup(String name){

        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        String groupID = rootNode.getReference("Groups").push().getKey();
        groupReference = rootNode.getReference("Groups").child(groupID);

        Group group = new Group(groupID,name,date,"active");
        groupReference.setValue(group).addOnCompleteListener(v -> {
            Toast.makeText(this, "Group added", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(v -> Toast.makeText(this, "Something went wrong, try again", Toast.LENGTH_SHORT).show());


    }

}