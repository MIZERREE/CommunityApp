package com.ree.mizer.communityapp.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ree.mizer.communityapp.R;
import com.ree.mizer.communityapp.pojos.admin.Funeral;
import com.ree.mizer.communityapp.pojos.admin.Inventory;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder>{
    Context context;
    ArrayList<Inventory> inventoryArrayList;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    public InventoryAdapter(@NonNull Context context, ArrayList<Inventory> inventoryArrayList) {
        this.context = context;
        this.inventoryArrayList = inventoryArrayList;

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Inventory");
    }

    @NonNull
    @Override
    public InventoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_view_inventory, parent, false);
        InventoryAdapter.ViewHolder viewHolder = new InventoryAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryAdapter.ViewHolder holder, int position) {
        holder.itemView.setTag(inventoryArrayList.get(position));

        Inventory storage = inventoryArrayList.get(position);

        holder.tvItemName.setText(storage.getDescription());
        holder.tvItemValue.setText(""+storage.getTotal());
    }

    @Override
    public int getItemCount() {
        return inventoryArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvItemName;
        TextView tvItemValue ;
        ImageView imageView ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tv_item_name);
            tvItemValue = itemView.findViewById(R.id.tv_item_value);
            imageView = itemView.findViewById(R.id.img_item);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Inventory funeral = (Inventory) view.getTag();
                   // displaySingleFuneralDialog(funeral.getName(),funeral.getAddress(),funeral.getWhenHappened(),funeral.getWhenBurial(),funeral.getContacts(),funeral.getExtras(),view,funeral.getFuneralID());

                }
            });

        }
    }

}
