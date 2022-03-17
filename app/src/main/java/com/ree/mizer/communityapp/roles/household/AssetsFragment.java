package com.ree.mizer.communityapp.roles.household;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ree.mizer.communityapp.R;
import com.ree.mizer.communityapp.adapters.AssetAdapter;
import com.ree.mizer.communityapp.adapters.FineAdapter;
import com.ree.mizer.communityapp.pojos.admin.Asset;
import com.ree.mizer.communityapp.pojos.admin.Fine;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class AssetsFragment extends Fragment {
    String TAG = "*** Community App ***", userFullNames, userAddress;
    String userID,  date;
    private ProgressBar  progressBarLoadFAssets;
    DatabaseReference assetsReference;
    ArrayList<Asset> assetsArrayList = new ArrayList<>();
    Asset asset;

    private RecyclerView assetsRecyclerView;
    RecyclerView.LayoutManager layoutManagerAssets;

    private EditText edRequester, edAddress, edAssets, edExtras;
    private TextView tvDate;
    private Button btnRequest;

    private ProgressDialog loadingBar;

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
        View view= inflater.inflate(R.layout.fragment_assets, container, false);

        loadingBar = new ProgressDialog(context);

        edRequester = view.findViewById(R.id.user_text_asset_name);
        edAddress = view.findViewById(R.id.user_text_asset_address);
        edAssets = view.findViewById(R.id.user_text_asset_description);
        edExtras = view.findViewById(R.id.user_text_asset_extra);
        tvDate = view.findViewById(R.id.user_text_asset_date);
        btnRequest = view.findViewById(R.id.user_btn_asset_request);
        progressBarLoadFAssets = view.findViewById(R.id.simpleProgressBarAssets);
        assetsRecyclerView = view.findViewById(R.id.user_rcl_assets_history);

        //current date
        String currentDate = new SimpleDateFormat("EEE dd-MM-yyyy", Locale.getDefault()).format(new Date());
        date = currentDate;
        tvDate.setText(currentDate);

        layoutManagerAssets = new LinearLayoutManager(context);
        assetsRecyclerView.setLayoutManager(layoutManagerAssets);

        getLoggedInUser();
        getAssetsHistory();

        btnRequest.setOnClickListener(m->{
            requestAsset();
        });

        return view;
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

    public void requestAsset(){
        //loading bar
        loadingBar.setTitle("Request assets");
        loadingBar.setMessage("Requesting assets...");
        loadingBar.show();

        //alert
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Success");
        builder.setMessage("Assets request sent to admin ");
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
        if(edAssets.getText().toString().trim().equals("") || edAssets.getText().toString().trim().isEmpty()){
            edAssets.setError("what are you requesting");
            loadingBar.dismiss();
        }else if(edExtras.getText().toString().trim().equals("") || edExtras.getText().toString().trim().isEmpty()){
            edExtras.setError("field can not be empty ");
            loadingBar.dismiss();
        }else{
            String name = userFullNames;
            String address = userAddress;
            String memberID = userID;
            String description = edAssets.getText().toString().trim();
            String extra = edExtras.getText().toString().trim();


            FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
            String assetID = rootNode.getReference("Assets").push().getKey();
            DatabaseReference assetsReference = rootNode.getReference("Assets").child(assetID);

            Asset asset = new Asset(assetID,memberID,date,name,address,description,"no image",extra,"waiting");
            assetsReference.setValue(asset).addOnCompleteListener(v->{
                updateUser();
                loadingBar.dismiss();
                alertDialog.show();
                clearView();
            }).addOnFailureListener(v-> Toast.makeText(context, "Something went wrong, try again", Toast.LENGTH_SHORT).show());
        }
    }

    private void clearView() {
        edExtras.setText("");
        edAssets.setText("");
    }

    private void getLoggedInUser(){
        if (HouseHoldDashActivity.LOGGED_IN_USER != null){
            userFullNames = HouseHoldDashActivity.LOGGED_IN_USER.getFullNames()+" "+HouseHoldDashActivity.LOGGED_IN_USER.getSurname();
            userAddress = HouseHoldDashActivity.LOGGED_IN_USER.getAddress();

            userID = HouseHoldDashActivity.LOGGED_IN_USER.getUserID();
            edRequester.setText(userFullNames);
            edAddress.setText(userAddress);
        }else{
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUser(){
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("hasAssets", "waiting");
        HouseHoldDashActivity.USER_REFERENCE.child(userID).updateChildren(userMap);
    }
}