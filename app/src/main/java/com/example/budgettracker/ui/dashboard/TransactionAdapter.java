package com.example.budgettracker.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgettracker.R;
import com.example.budgettracker.data.entity.TransactionEntity;

import java.util.List;

public class TransactionAdapter
        extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private final List<TransactionEntity> transactions;
    private final OnTransactionActionListener listener;

    public TransactionAdapter(List<TransactionEntity> transactions,
                              OnTransactionActionListener listener) {
        this.transactions = transactions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder, int position) {

        TransactionEntity t = transactions.get(position);

        holder.tvTitle.setText(t.title);
        // Format date
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault());
        holder.tvDate.setText(sdf.format(new java.util.Date(t.date))); 

        holder.tvAmount.setText(
                String.format(java.util.Locale.getDefault(), "₹%.2f", t.amount)
        );
        
        // Icon Mapping
        int iconRes;
        switch (t.category) {
            case "Food":
                iconRes = R.drawable.ic_food;
                break;
            case "Rent":
                iconRes = R.drawable.ic_rent;
                break;
            case "Electric Bills":
                iconRes = R.drawable.ic_electric;
                break;
            case "Subscriptions":
                iconRes = R.drawable.ic_subscription;
                break;
            default:
                iconRes = R.drawable.ic_default_category;
                break;
        }
        holder.ivIcon.setImageResource(iconRes);

        holder.itemView.setOnLongClickListener(v -> {
            listener.onTransactionLongClicked(t);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return transactions == null ? 0 : transactions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvDate, tvAmount;
        android.widget.ImageView ivIcon;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            ivIcon = itemView.findViewById(R.id.ivIcon);
        }
    }
}
