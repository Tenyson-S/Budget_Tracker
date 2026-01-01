package com.example.budgettracker.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgettracker.R;

import java.util.List;

public class SuggestionAdapter
        extends RecyclerView.Adapter<SuggestionAdapter.ViewHolder> {

    private final List<String> suggestions;

    public SuggestionAdapter(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv;

        ViewHolder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tvSuggestion);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_suggestion, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder, int position) {

        holder.tv.setText("• " + suggestions.get(position));
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }
}
