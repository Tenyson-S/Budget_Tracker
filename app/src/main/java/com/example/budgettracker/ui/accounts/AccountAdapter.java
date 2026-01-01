package com.example.budgettracker.ui.accounts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgettracker.R;
import com.example.budgettracker.data.entity.AccountEntity;

import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> {

    private final List<AccountEntity> accounts;
    // Listener for actions if needed

    public AccountAdapter(List<AccountEntity> accounts) {
        this.accounts = accounts;
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account_card, parent, false);
        return new AccountViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        AccountEntity account = accounts.get(position);
        holder.tvName.setText(account.name);
        holder.tvType.setText(account.type);
        holder.tvBalance.setText("₹ " + account.balance);
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    static class AccountViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvType, tvBalance;

        AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvAccountName);
            tvType = itemView.findViewById(R.id.tvAccountType);
            tvBalance = itemView.findViewById(R.id.tvAccountBalance);
        }
    }
}
