package com.example.budgettracker.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.budgettracker.R;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgettracker.logic.RecurringExpenseInsight;

import java.util.List;

public class RecurringExpensesAdapter
        extends RecyclerView.Adapter<RecurringExpensesAdapter.ViewHolder> {

    private final List<RecurringExpenseInsight> items;

    public RecurringExpensesAdapter(List<RecurringExpenseInsight> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder, int position) {

        RecurringExpenseInsight item = items.get(position);

        holder.title.setText(item.title);
        holder.subtitle.setText(
                holder.itemView.getContext().getString(
                        R.string.recurring_item_detail,
                        item.frequency,
                        item.totalPrice
                )
        );

    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, subtitle;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(android.R.id.text1);
            subtitle = itemView.findViewById(android.R.id.text2);
        }
    }
}
