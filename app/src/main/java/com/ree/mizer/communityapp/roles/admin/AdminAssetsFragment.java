package com.ree.mizer.communityapp.roles.admin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ree.mizer.communityapp.R;
import com.ree.mizer.communityapp.adapters.AssetAdapter;
import com.ree.mizer.communityapp.pojos.User;
import com.ree.mizer.communityapp.pojos.admin.Asset;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class AdminAssetsFragment extends Fragment {
    ArrayList<User> searchedUserList;

    String date;
    EditText edtAddress, edtName,edtDescription, edtAnythingElse;
    TextView tvDate;
    AutoCompleteTextView txtSearch;
    private NumberPicker pckTents, pckChairs, pckPots, pckPlates, pckCups, pckTables, pckCloth, pckSpades;
    CheckBox chkTent, chkChair, chkPots,chkSpades,chkCloth,chkTables,chkCups,chkPlates;
    String tentsID="", chairsID="", potsID="", platesID="", cupsID="", tablesID="", clothID="", spadeID="";
    Button btnAssign;
    ImageView imgAsset;

    int potsNum = 0; int chairsNum=0; int tentsNum=0; int platesNum=0; int cupsNum=0; int tablesNum=0; int clothNum=0; int  spadesNum=0;

    private Uri imageUri;
    String imageUploadUri = "no image uri";
    private ProgressBar assetUploadBar;

    private StorageReference imgStorageReference;
    private DatabaseReference imgDatabaseReference;

    private ProgressDialog loadingBar;

    private ProgressBar progressBarLoadFAssets;
    DatabaseReference assetsReference;
    ArrayList<Asset> assetsArrayList;
    Asset asset;

    private RecyclerView assetsRecyclerView;
    RecyclerView.LayoutManager layoutManagerAssets;

    int PICK_IMAGE_REQUEST = 101;

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
        View view = inflater.inflate(R.layout.fragment_admin_assets, container, false);

        imgStorageReference = FirebaseStorage.getInstance().getReference("assets images");

        tvDate = view.findViewById(R.id.text_admin_assets_date);
        txtSearch = view.findViewById(R.id.admin_text_search_asset);
        edtAddress = view.findViewById(R.id.admin_text_asset_address);
        edtName = view.findViewById(R.id.admin_text_asset_name);
        edtDescription = view.findViewById(R.id.admin_text_description_asset);
        edtAnythingElse = view.findViewById(R.id.admin_text_optional_text);
        chkTent = view.findViewById(R.id.admin_chk_tents);
        chkChair = view.findViewById(R.id.admin_chk_chairs);
        chkPots = view.findViewById(R.id.admin_chk_pots);
        chkSpades = view.findViewById(R.id.admin_chk_spades);
        chkCloth = view.findViewById(R.id.admin_chk_cloth);
        chkTables = view.findViewById(R.id.admin_chk_tables);
        chkCups = view.findViewById(R.id.admin_chk_cups);
        chkPlates = view.findViewById(R.id.admin_chk_plates);
        btnAssign = view.findViewById(R.id.admin_btn_issue_asset);
        progressBarLoadFAssets = view.findViewById(R.id.simpleProgressBarAdAssets);
        assetsRecyclerView = view.findViewById(R.id.admin_rcl_assets_list);
        assetUploadBar = view.findViewById(R.id.asset_progress_bar);
        imgAsset = view.findViewById(R.id.admin_img_assets);
        pckTents = view.findViewById(R.id.pck_tents);
        pckChairs = view.findViewById(R.id.pck_chairs);
        pckPots = view.findViewById(R.id.pck_pots);
        pckPlates = view.findViewById(R.id.pck_plates);
        pckCups = view.findViewById(R.id.pck_cups);
        pckTables = view.findViewById(R.id.pck_tables);
        pckCloth = view.findViewById(R.id.pck_cloth);
        pckSpades = view.findViewById(R.id.pck_spades);

        //For searching user
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                populateSearch(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        AdminDashActivity.USER_REFERENCE.addListenerForSingleValueEvent(eventListener);
        loadingBar = new ProgressDialog(context);

        //current date
        String currentDate = new SimpleDateFormat("EEE dd-MM-yyyy", Locale.getDefault()).format(new Date());
        date = currentDate;
        tvDate.setText(currentDate);

        layoutManagerAssets = new LinearLayoutManager(context);
        assetsRecyclerView.setLayoutManager(layoutManagerAssets);

        btnAssign.setOnClickListener(v ->{
            assignAssets();

        });

        imgAsset.setOnClickListener(v-> {
            mGetContent.launch("image/*");
        });

        assignCheckBoxes();
        initializeNumberPicker();
        getAssetsHistory();

        return view;
    }

    private void assignCheckBoxes() {
        chkPots.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if(isChecked){
                potsNum = pckPots.getValue();
                edtDescription.setText(edtDescription.getText().toString()+potsNum+" Pots, ");
            }

        });
        chkChair.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if(isChecked){
                chairsNum = pckChairs.getValue();
                edtDescription.setText(edtDescription.getText().toString()+chairsNum+" Chairs, ");
            }
        });
        chkTent.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if(isChecked){

                tentsNum = pckTents.getValue();
                edtDescription.setText(edtDescription.getText().toString()+tentsNum+" Tents, ");
            }
        });

        chkPlates.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if(isChecked){
                platesNum = pckPlates.getValue();
                edtDescription.setText(edtDescription.getText().toString()+platesNum+" Plates, ");
            }
        });
        chkCups.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if(isChecked){
                cupsNum = pckCups.getValue();
                edtDescription.setText(edtDescription.getText().toString()+cupsNum+" Cups, ");
            }
        });
        chkTables.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if(isChecked){
                tablesNum = pckTables.getValue();
                edtDescription.setText(edtDescription.getText().toString()+tablesNum+" Tables, ");
            }
        });
        chkCloth.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if(isChecked){
                clothNum = pckCloth.getValue();
                edtDescription.setText(edtDescription.getText().toString()+clothNum+" Cloth, ");
            }
        });
        chkSpades.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if(isChecked){
                spadesNum = pckSpades.getValue();
                edtDescription.setText(edtDescription.getText().toString()+spadesNum+" Plates, ");
            }
        });
    }

    private void initializeNumberPicker() {
        pckTents.setMaxValue(AdminDashActivity.TENS);
        pckTents.setMinValue(0);

        pckChairs.setMaxValue(AdminDashActivity.CHAIRS);
        pckChairs.setMinValue(0);

        pckPots.setMaxValue(AdminDashActivity.POTS);
        pckPots.setMinValue(0);

        pckPlates.setMaxValue(AdminDashActivity.PLATES);
        pckPlates.setMinValue(0);

        pckCups.setMaxValue(AdminDashActivity.CUPS);
        pckCups.setMinValue(0);

        pckTables.setMaxValue(AdminDashActivity.TABLES);
        pckTents.setMinValue(0);

        pckCloth.setMaxValue(AdminDashActivity.CLOTH);
        pckCloth.setMinValue(0);

        pckSpades.setMaxValue(AdminDashActivity.SPADES);
        pckSpades.setMinValue(0);
    }

    /*
    Start of search user
     */
    private void populateSearch(DataSnapshot snapshot) {
        ArrayList<String> names = new ArrayList<>();
        if(snapshot.exists()){
            for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                String name = dataSnapshot.child("fullNames").getValue(String.class);
                names.add(name);
            }

            ArrayAdapter adapter = new ArrayAdapter(context,android.R.layout.simple_list_item_1,names);
            txtSearch.setAdapter(adapter);
            txtSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String name = txtSearch.getText().toString();
                    searchUser(name);
                }
            });

        }else{
            Log.d("users","No data found");
        }
    }

    private void searchUser(String name) {
        Query query = AdminDashActivity.USER_REFERENCE.orderByChild("fullNames").equalTo(name);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    searchedUserList = new ArrayList<>();

                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        User user = dataSnapshot.getValue(User.class);
                        searchedUserList.add(user);
                        //displaySingleFuneralDialog(funeral.getName(),funeral.getAddress(),funeral.getWhenHappened(),funeral.getWhenBurial(),funeral.getContacts(),funeral.getExtras());
                    }
                    //AdminFuneralAdapter adapter = new AdminFuneralAdapter(FinancialActivity.this, searchedUserList);
                    edtAddress.setText(searchedUserList.get(0).getAddress());
                    edtName.setText(searchedUserList.get(0).getFullNames()+" "+searchedUserList.get(0).getSurname());

                }else{
                    Log.d("user","No data found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /*
     * end of search user*/

    public void assignAssets(){
        //loading bar
        loadingBar.setTitle("Assigning assets");
        loadingBar.setMessage("Please wait ...");
        loadingBar.show();

        //alert
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Success");
        builder.setMessage("Assets assigned to "+ searchedUserList.get(0).getFullNames()+" "+searchedUserList.get(0).getSurname());
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
        if(txtSearch.getText().toString().trim().equals("") || txtSearch.getText().toString().trim().isEmpty()){
            txtSearch.setError("Search Member first");
            loadingBar.dismiss();
        }else if(edtAddress.getText().toString().trim().equals("") || edtAddress.getText().toString().trim().isEmpty()){
            edtAddress.setError("Member must have address");
            loadingBar.dismiss();
        }else if(edtName.getText().toString().trim().equals("") || edtName.getText().toString().trim().isEmpty()){
            edtName.setError("Member's name'");
            loadingBar.dismiss();
        }else if(edtDescription.getText().toString().trim().equals("") || edtDescription.getText().toString().trim().isEmpty()){
            edtDescription.setError("Enter description");
            loadingBar.dismiss();
        }else if(edtAnythingElse.getText().toString().trim().equals("") || edtAnythingElse.getText().toString().trim().isEmpty()){
            edtAnythingElse.setError("Field can not be empty");
            loadingBar.dismiss();
        }else{
            String name = txtSearch.getText().toString().trim();
            String address = searchedUserList.get(0).getAddress();
            String memberID = searchedUserList.get(0).getUserID();
            String description = edtDescription.getText().toString().trim();
            String extra = edtAnythingElse.getText().toString().trim();


            FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
            String assetID = rootNode.getReference("Assets").push().getKey();
            DatabaseReference assetsReference = rootNode.getReference("Assets").child(assetID);

            uploadImage();

            Asset asset = new Asset(assetID,memberID,date,name,address,description,imageUploadUri,extra,"issued");
            assetsReference.setValue(asset).addOnCompleteListener(v->{
                updateUser();
                loadingBar.dismiss();
                alertDialog.show();
                clearView();
            }).addOnFailureListener(v-> Toast.makeText(context, "Something went wrong, try again", Toast.LENGTH_SHORT).show())
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    subtractFromInventory();
                }
            });
        }
    }

    private void getAssetsHistory(){
            progressBarLoadFAssets.setVisibility(View.VISIBLE);
            assetsReference = FirebaseDatabase.getInstance().getReference("Assets");
            assetsReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    progressBarLoadFAssets.setVisibility(View.GONE);
                    assetsArrayList = new ArrayList<>();

                    for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                    {
                        asset = dataSnapshot1.getValue(Asset.class);
                        assetsArrayList.add(asset);
                    /*if(asset.getStatus().equals("issued")){
                        assetsArrayList.add(asset);
                    }*/

                    }
                    Collections.reverse(assetsArrayList);
                    AssetAdapter adapter = new AssetAdapter(context, assetsArrayList);
                    assetsRecyclerView.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

    }

    private void clearView() {
        txtSearch.setText("");
        edtName.setText("");
        edtDescription.setText("");
        edtAddress.setText("");
    }

    private void updateUser(){
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("hasAssets", "Yes");
        AdminDashActivity.USER_REFERENCE.child((searchedUserList.get(0).getUserID())).updateChildren(userMap);
    }

    //*****Start of image upload********

    private String getFileExtension(Uri imageUri){
        ContentResolver resolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(resolver.getType(imageUri));
    }

    //-------Upload image v2----------
    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new  ActivityResultCallback<Uri>(){

        @Override
        public void onActivityResult(Uri result) {
            if(result!=null){
                imgAsset.setImageURI(result);
                imageUri = result;
                imageUploadUri = result.toString();
            }
        }
    });

    private  void  uploadImage(){
        if (imageUri != null) {

            StorageReference fileReference = imgStorageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));

            fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(context, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                    Handler handler = new Handler();
                    handler.postDelayed(() -> assetUploadBar.setProgress(0),5000);
                    imageUploadUri = taskSnapshot.getUploadSessionUri().toString();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                    assetUploadBar.setProgress((int)progress);
                }
            });
        }
    }

    //-------eov2--------------

    //Subtraction from inventory
    private void subtractFromInventory(){

        int tents = AdminDashActivity.TENS - pckTents.getValue();
        int chairs = AdminDashActivity.CHAIRS - pckChairs.getValue();
        int pots = AdminDashActivity.POTS -  pckPots.getValue();
        int plates = AdminDashActivity.PLATES - pckPlates.getValue();
        int cups = AdminDashActivity.CUPS - pckCups.getValue();
        int tables = AdminDashActivity.TABLES - pckTables.getValue();
        int cloth = AdminDashActivity.TABLES - pckTables.getValue();
        int spades = AdminDashActivity.SPADES - pckSpades.getValue();

        for(int x=0; x<AdminDashActivity.inventoryArrayList.size(); x++){
            if(AdminDashActivity.inventoryArrayList.get(x).getDescription().equals("Pots")){
                potsID = AdminDashActivity.inventoryArrayList.get(x).getId();
            }else if(AdminDashActivity.inventoryArrayList.get(x).getDescription().equals("Plates")){
                platesID = AdminDashActivity.inventoryArrayList.get(x).getId();
            }else if(AdminDashActivity.inventoryArrayList.get(x).getDescription().equals("Cups")){
                cupsID = AdminDashActivity.inventoryArrayList.get(x).getId();
            }else if(AdminDashActivity.inventoryArrayList.get(x).getDescription().equals("Chairs")){
                chairsID = AdminDashActivity.inventoryArrayList.get(x).getId();
            }else if(AdminDashActivity.inventoryArrayList.get(x).getDescription().equals("Tables")){
                tablesID = AdminDashActivity.inventoryArrayList.get(x).getId();
            }else if(AdminDashActivity.inventoryArrayList.get(x).getDescription().equals("Tents")){
                tentsID = AdminDashActivity.inventoryArrayList.get(x).getId();
            }else if(AdminDashActivity.inventoryArrayList.get(x).getDescription().equals("Spades")){
                spadeID = AdminDashActivity.inventoryArrayList.get(x).getId();
            }else if(AdminDashActivity.inventoryArrayList.get(x).getDescription().equals("White cloths")){
                clothID = AdminDashActivity.inventoryArrayList.get(x).getId();
            }
        }
        //alert
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Assign assets");
        builder.setMessage("Pots: "+pckPots.getValue()+"\nPlates: "+pckPlates.getValue()+"\nCups: "+
                pckCups.getValue()+"\nChairs: "+pckChairs.getValue()+"\nTables: "+pckTables.getValue()+
                "\nTents: "+pckTents.getValue()+"\nSpades: "+pckSpades.getValue()+"\nCloth: "+pckTables.getValue());
        builder.setIcon(R.drawable.info_icon);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                HashMap<String, Object> potsMap = new HashMap<>();
                HashMap<String, Object> platesMap = new HashMap<>();
                HashMap<String, Object> cupsMap = new HashMap<>();
                HashMap<String, Object> chairsMap = new HashMap<>();
                HashMap<String, Object> tablesMap = new HashMap<>();
                HashMap<String, Object> tentsMap = new HashMap<>();
                HashMap<String, Object> spadesMap = new HashMap<>();
                HashMap<String, Object> clothMap = new HashMap<>();

                potsMap.put("total", pots);
                platesMap.put("total", plates);
                cupsMap.put("total", cups);
                chairsMap.put("total", chairs);
                tablesMap.put("total", tables);
                tentsMap.put("total", tents);
                spadesMap.put("total", spades);
                clothMap.put("total", cloth);

                AdminDashActivity.INVENTORY_REFERENCE.child(potsID).updateChildren(potsMap);
                AdminDashActivity.INVENTORY_REFERENCE.child(platesID).updateChildren(platesMap);
                AdminDashActivity.INVENTORY_REFERENCE.child(cupsID).updateChildren(cupsMap);
                AdminDashActivity.INVENTORY_REFERENCE.child(chairsID).updateChildren(chairsMap);
                AdminDashActivity.INVENTORY_REFERENCE.child(tablesID).updateChildren(tablesMap);
                AdminDashActivity.INVENTORY_REFERENCE.child(tentsID).updateChildren(tentsMap);
                AdminDashActivity.INVENTORY_REFERENCE.child(spadeID).updateChildren(spadesMap);
                AdminDashActivity.INVENTORY_REFERENCE.child(clothID).updateChildren(clothMap);
                dialog.cancel();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        //end of alert dialog

    }
    //end of subtraction
}