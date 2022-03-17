package com.ree.mizer.communityapp.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.ree.mizer.communityapp.R;
import com.ree.mizer.communityapp.pojos.admin.Asset;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AssetAdapter extends RecyclerView.Adapter<AssetAdapter.ViewHolder>{
    Context context;
    ArrayList<Asset> assetsList;

    public AssetAdapter(@NonNull Context context, ArrayList<Asset> assetsList) {
        this.context = context;
        this.assetsList = assetsList;
    }

    @NonNull
    @Override
    public AssetAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_user_assets, parent, false);
        AssetAdapter.ViewHolder viewHolder = new AssetAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AssetAdapter.ViewHolder holder, int position) {
        holder.itemView.setTag(assetsList.get(position));

        Asset asset = assetsList.get(position);

        holder.tvDate.setText(asset.getIssueDate());
        holder.tvStatus.setText(asset.getStatus());
        holder.edtDescription.setText(asset.getDescription());
    }

    @Override
    public int getItemCount() {
        return assetsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvDate,tvStatus;
        EditText edtDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.edit_custom_user_assets_date);
            tvStatus = itemView.findViewById(R.id.edit_custom_user_assets_status);
            edtDescription = itemView.findViewById(R.id.edit_custom_user_assets_description);

            itemView.setOnClickListener(view -> {

                Asset currentFine = (Asset) view.getTag();
                displaySingleCrimeDialog(currentFine.getDescription(),view);
            });

        }
    }

    private void displaySingleCrimeDialog(String assets, View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Assets");
        builder.setMessage("Return these assets? "+assets);
        builder.setIcon(R.drawable.money_icon);
        builder.setPositiveButton("Yes", (dialog, which) -> {
            //Pay method
            dialog.cancel();
        })
                .setNegativeButton("No", ((dialog, which) -> dialog.cancel()));
        builder.setCancelable(false);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}

