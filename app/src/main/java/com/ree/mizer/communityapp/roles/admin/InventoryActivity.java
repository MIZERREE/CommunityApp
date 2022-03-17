package com.ree.mizer.communityapp.roles.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ree.mizer.communityapp.R;
import com.ree.mizer.communityapp.adapters.AssetAdapter;
import com.ree.mizer.communityapp.adapters.InventoryAdapter;
import com.ree.mizer.communityapp.pojos.admin.Asset;
import com.ree.mizer.communityapp.pojos.admin.Inventory;

import java.util.ArrayList;
import java.util.Collections;

import static com.ree.mizer.communityapp.roles.admin.AdminDashActivity.context;

public class InventoryActivity extends AppCompatActivity {

    private ProgressDialog loadingBar;

    private ProgressBar progressBarLoadFAssets;
    DatabaseReference assetsReference;
    ArrayList<Asset> requestedAssetsArrayList;
    ArrayList<Asset> assetsArrayListOut;
    ArrayList<Inventory> inventoryArrayList;
    Asset asset;
    Inventory inventory;

    private RecyclerView assetsRecyclerViewRequest, inventoryRecyclerViewIn,inventoryRecyclerViewOut;
    RecyclerView.LayoutManager layoutManagerAssetsRequests, layoutManagerInventory, layoutManagerOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        progressBarLoadFAssets = findViewById(R.id.simpleProgressBarUsersReq);
        assetsRecyclerViewRequest = findViewById(R.id.admin_rcl_req_assets);
        inventoryRecyclerViewIn = findViewById(R.id.admin_rcl_in_inv);
        inventoryRecyclerViewOut = findViewById(R.id.admin_rcl_out_inv);

        loadingBar = new ProgressDialog(this);

        layoutManagerAssetsRequests = new LinearLayoutManager(this);
        assetsRecyclerViewRequest.setLayoutManager(layoutManagerAssetsRequests);

        layoutManagerInventory = new LinearLayoutManager(this);
        inventoryRecyclerViewIn.setLayoutManager(layoutManagerInventory);

        layoutManagerOut = new LinearLayoutManager(this);
        inventoryRecyclerViewOut.setLayoutManager(layoutManagerOut);

        getAssetsRequests();
        getInventory();
        getAssetsIssued();
    }

    private void getAssetsRequests(){
        progressBarLoadFAssets.setVisibility(View.VISIBLE);
        assetsReference = FirebaseDatabase.getInstance().getReference("Assets");
        assetsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBarLoadFAssets.setVisibility(View.GONE);
                requestedAssetsArrayList = new ArrayList<>();

                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    asset = dataSnapshot1.getValue(Asset.class);
                    if(asset.getStatus().equals("waiting")){
                        requestedAssetsArrayList.add(asset);
                    }
                }
                Collections.reverse(requestedAssetsArrayList);
                AssetAdapter adapter = new AssetAdapter(InventoryActivity.this, requestedAssetsArrayList);
                assetsRecyclerViewRequest.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getAssetsIssued(){
        progressBarLoadFAssets.setVisibility(View.VISIBLE);
        assetsReference = FirebaseDatabase.getInstance().getReference("Assets");
        assetsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBarLoadFAssets.setVisibility(View.GONE);
                assetsArrayListOut = new ArrayList<>();

                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    asset = dataSnapshot1.getValue(Asset.class);
                    if(asset.getStatus().equals("issued")){
                        assetsArrayListOut.add(asset);
                    }

                }
                Collections.reverse(assetsArrayListOut);
                AssetAdapter adapter = new AssetAdapter(InventoryActivity.this, assetsArrayListOut);
                inventoryRecyclerViewOut.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    private void getInventory(){
        progressBarLoadFAssets.setVisibility(View.VISIBLE);
        assetsReference = FirebaseDatabase.getInstance().getReference("Inventory");
        assetsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBarLoadFAssets.setVisibility(View.GONE);
                inventoryArrayList = new ArrayList<>();

                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    inventory = dataSnapshot1.getValue(Inventory.class);
                    inventoryArrayList.add(inventory);

                }
                Collections.reverse(inventoryArrayList);
                InventoryAdapter adapter = new InventoryAdapter(InventoryActivity.this, inventoryArrayList);
                inventoryRecyclerViewIn.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void addAsset(View view){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ViewGroup viewGroup = view.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_add_inventory, viewGroup, false);
        Button btnAdd = dialogView.findViewById(R.id.tbn_inv_add);
        ImageView imgBack = dialogView.findViewById(R.id.img_cancel);
        TextInputLayout edtName, edtValue;

        edtName = dialogView.findViewById(R.id.edt_inv_item_name);
        edtValue = dialogView.findViewById(R.id.edt_inv_item_value);

        builder.setView(dialogView);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();

        imgBack.setOnClickListener(v -> alertDialog.dismiss());

        btnAdd.setOnClickListener(v -> {
            if(TextUtils.isEmpty(edtName.getEditText().getText())){
                Toast.makeText(context, "Please enter  name before you continue", Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(edtValue.getEditText().getText())){
                Toast.makeText(context, "Please enter value of items", Toast.LENGTH_SHORT).show();
            }else{

                FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
                String inventoryID = rootNode.getReference("Inventory").push().getKey();
                DatabaseReference inventoryReference = rootNode.getReference("Inventory").child(inventoryID);


                Inventory inventory = new Inventory(inventoryID,edtName.getEditText().getText().toString(),Integer.parseInt(edtValue.getEditText().getText().toString()));
                inventoryReference.setValue(inventory).addOnCompleteListener(z->{
                    Toast.makeText(context, "Item added to inventory", Toast.LENGTH_SHORT).show();
                    alertDialog.show();
                }).addOnFailureListener(z-> {
                    loadingBar.dismiss();
                    Toast.makeText(context, "Something went wrong, try again", Toast.LENGTH_SHORT).show();});


            }

            //******************EOU************************************
            alertDialog.dismiss();
        });

        alertDialog.show();
    }
}