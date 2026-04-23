package com.example.expensetrackerapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetrackerapp.R;
import com.example.expensetrackerapp.models.Expense;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    private List<Expense> expenseList;
    private OnExpenseClickListener listener;

    public interface OnExpenseClickListener {
        void onEditClick(Expense expense);
        void onDeleteClick(Expense expense);
    }

    public ExpenseAdapter(List<Expense> expenseList, OnExpenseClickListener listener) {
        this.expenseList = expenseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Expense expense = expenseList.get(position);

        holder.tvType.setText(expense.getType());
        holder.tvAmount.setText(String.format("%s %.2f", expense.getCurrency(), expense.getAmount()));
        holder.tvDate.setText(expense.getDate());
        holder.tvClaimant.setText("Claimant: " + expense.getClaimant());
        holder.tvStatus.setText("Status: " + expense.getPaymentStatus());

        // Condition for Description
        if (expense.getDescription() != null && !expense.getDescription().trim().isEmpty()) {
            holder.tvDescription.setText("Description: " + expense.getDescription());
            holder.tvDescription.setVisibility(View.VISIBLE);
        } else {
            holder.tvDescription.setVisibility(View.GONE);
        }

        // Condition for Location
        if (expense.getLocation() != null && !expense.getLocation().trim().isEmpty()) {
            holder.tvLocation.setText("Location: " + expense.getLocation());
            holder.tvLocation.setVisibility(View.VISIBLE);
        } else {
            holder.tvLocation.setVisibility(View.GONE);
        }

        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(expense));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(expense));
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvAmount, tvDate, tvClaimant, tvStatus, tvDescription, tvLocation;
        ImageButton btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tvExpType);
            tvAmount = itemView.findViewById(R.id.tvExpAmount);
            tvDate = itemView.findViewById(R.id.tvExpDate);
            tvClaimant = itemView.findViewById(R.id.tvExpClaimant);
            tvStatus = itemView.findViewById(R.id.tvExpStatus);
            tvDescription = itemView.findViewById(R.id.tvExpDescription);
            tvLocation = itemView.findViewById(R.id.tvExpLocation);
            btnEdit = itemView.findViewById(R.id.btnEditExpense);
            btnDelete = itemView.findViewById(R.id.btnDeleteExpense);
        }
    }
}