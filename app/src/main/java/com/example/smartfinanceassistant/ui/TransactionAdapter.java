package com.example.smartfinanceassistant.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartfinanceassistant.R;
import com.example.smartfinanceassistant.models.Expense;

import java.util.List;

public class TransactionAdapter extends
        RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private List<Expense> expenses;

    public TransactionAdapter(List<Expense> expenses) {
        this.expenses = expenses;
    }

    public void updateList(List<Expense> newExpenses) {
        this.expenses = newExpenses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override  // Pollymorphism
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder, int position) {
        Expense expense = expenses.get(position);

        holder.tvTitle.setText(expense.title);
        holder.tvCategory.setText(expense.category);
        holder.tvDate.setText(expense.date);

        // Amount color based on type
        if (expense.type.equals("income")) {
            holder.tvAmount.setText("+ ₹" +
                    String.format("%.2f", expense.amount));
            holder.tvAmount.setTextColor(
                    holder.itemView.getContext()
                            .getResources().getColor(
                                    android.R.color.holo_green_dark));
        } else {
            holder.tvAmount.setText("- ₹" +
                    String.format("%.2f", expense.amount));
            holder.tvAmount.setTextColor(
                    holder.itemView.getContext()
                            .getResources().getColor(
                                    android.R.color.holo_red_dark));
        }
    }

    @Override
    public int getItemCount() {
        return expenses != null ? expenses.size() : 0;
    }

    public static class ViewHolder extends
            RecyclerView.ViewHolder {
        TextView tvTitle, tvCategory,
                tvAmount, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle    = itemView.findViewById(R.id.tvItemTitle);
            tvCategory = itemView.findViewById(R.id.tvItemCategory);
            tvAmount   = itemView.findViewById(R.id.tvItemAmount);
            tvDate     = itemView.findViewById(R.id.tvItemDate);
        }
    }
}