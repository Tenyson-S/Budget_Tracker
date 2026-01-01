package com.example.budgettracker.ui.more;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.budgettracker.R;
import com.example.budgettracker.data.repository.TransactionRepository;

public class MoreFragment extends Fragment {

    private TransactionRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_more, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = new TransactionRepository(requireContext());

        view.findViewById(R.id.cardClearData).setOnClickListener(v -> showClearDataDialog());
        view.findViewById(R.id.cardAbout).setOnClickListener(v -> showAboutDialog());
    }

    private void showClearDataDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Clear All Data")
                .setMessage("Are you sure you want to delete all transactions? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    repository.deleteAll();
                    Toast.makeText(getContext(), "All data cleared", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("About Budget Tracker")
                .setMessage("Version 1.0\n\nA simple budget tracking application to help you manage your finances using the 50/30/20 rule.")
                .setPositiveButton("OK", null)
                .show();
    }
}
