package com.example.budgettracker.ui.analysis;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgettracker.R;
import com.example.budgettracker.logic.CategorySpent;

import java.util.ArrayList;
import java.util.List;

public class CategoryAnalysisAdapter extends RecyclerView.Adapter<CategoryAnalysisAdapter.ViewHolder> {

    private List<CategorySpent> items = new ArrayList<>();
    private double totalExpense = 1.0; // Avoid division by zero

    public void submitList(List<CategorySpent> list, double total) {
        this.items = list;
        this.totalExpense = total > 0 ? total : 1.0;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_analysis, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategorySpent item = items.get(position);
        holder.bind(item, totalExpense);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvAmount, tvPercent;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategoryName);
            tvAmount = itemView.findViewById(R.id.tvCategoryAmount);
            tvPercent = itemView.findViewById(R.id.tvCategoryPercent);
            progressBar = itemView.findViewById(R.id.pbCategory);
        }

        public void bind(CategorySpent item, double total) {
            tvCategory.setText(item.category);
            tvAmount.setText(String.format("₹%.0f", item.amount));

            int percent = (int) ((item.amount / total) * 100);
            tvPercent.setText(percent + "%");
            progressBar.setProgress(percent);
        }
    }
}
