package com.example.budgettracker.ui.accounts;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgettracker.R;
import com.example.budgettracker.data.entity.AccountEntity;

import java.util.ArrayList;

public class AccountsFragment extends Fragment {

    private AccountsViewModel viewModel;
    private RecyclerView rvAccounts;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_accounts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AccountsViewModel.class);
        rvAccounts = view.findViewById(R.id.rvAccounts);
        rvAccounts.setLayoutManager(new LinearLayoutManager(getContext()));
        
        view.findViewById(R.id.fabAddAccount).setOnClickListener(v -> showAddAccountDialog());

        viewModel.getAllAccounts().observe(getViewLifecycleOwner(), accounts -> {
            if (accounts != null) {
                AccountAdapter adapter = new AccountAdapter(accounts);
                rvAccounts.setAdapter(adapter);
            } else {
                rvAccounts.setAdapter(new AccountAdapter(new ArrayList<>()));
            }
        });
    }

    private void showAddAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add New Account");

        final EditText input = new EditText(requireContext());
        input.setHint("Account Name (e.g., Savings, Wallet)");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (!TextUtils.isEmpty(name)) {
                // Defaulting type to 'General' for now, could add spinner in dialog later
                viewModel.addAccount(name, "General");
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
