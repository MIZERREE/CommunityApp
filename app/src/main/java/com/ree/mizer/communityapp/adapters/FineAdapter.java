package com.ree.mizer.communityapp.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.ree.mizer.communityapp.R;
import com.ree.mizer.communityapp.pojos.admin.Fine;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FineAdapter extends RecyclerView.Adapter<FineAdapter.ViewHolder>{
    Context context;
    ArrayList<Fine> fineList;

    public FineAdapter(@NonNull Context context, ArrayList<Fine> fineList) {
        this.context = context;
        this.fineList = fineList;
    }

    @NonNull
    @Override
    public FineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_user_fine, parent, false);
        FineAdapter.ViewHolder viewHolder = new FineAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FineAdapter.ViewHolder holder, int position) {
        holder.itemView.setTag(fineList.get(position));

        Fine fine = fineList.get(position);

        holder.tvDate.setText(fine.getDate());
        holder.tvDueDate.setText(fine.getDueDate());
        holder.tvAmount.setText(fine.getAmount());
        holder.tvStatus.setText(fine.getStatus());
        holder.edtDescription.setText(fine.getDescription());
    }

    @Override
    public int getItemCount() {
        return fineList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        String fineID, memberID, date, dueDate, amount, description, status;

        TextView tvDate, tvDueDate,tvAmount,tvStatus;
        EditText edtDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.edit_custom_user_fine_date);
            tvDueDate = itemView.findViewById(R.id.edit_custom_user_fine_due_date);
            tvAmount = itemView.findViewById(R.id.edit_custom_user_fine_amount);
            tvStatus = itemView.findViewById(R.id.edit_custom_user_fine_status);
            edtDescription = itemView.findViewById(R.id.edit_custom_user_fine_description);

            itemView.setOnClickListener(view -> {

                Fine currentFine = (Fine) view.getTag();
                //displaySingleCrimeDialog(currentFine.getAmount(),view);
            });

        }
    }

    private void displaySingleCrimeDialog(String amount, View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("R"+amount);
        builder.setMessage("Pay this fine?");
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
