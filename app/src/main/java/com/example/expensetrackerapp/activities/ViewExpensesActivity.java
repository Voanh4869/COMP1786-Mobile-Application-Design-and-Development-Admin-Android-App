package com.example.expensetrackerapp.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetrackerapp.R;
import com.example.expensetrackerapp.adapter.ExpenseAdapter;
import com.example.expensetrackerapp.database.DBHelper;
import com.example.expensetrackerapp.models.Expense;

import java.util.ArrayList;
import java.util.List;

public class ViewExpensesActivity extends AppCompatActivity implements ExpenseAdapter.OnExpenseClickListener {

    RecyclerView recyclerView;
    ExpenseAdapter adapter;
    List<Expense> expenseList;
    DBHelper dbHelper;
    int projectId;
    Button btnAddExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_expenses);

        projectId = getIntent().getIntExtra("project_id", -1);
        String projectName = getIntent().getStringExtra("project_name");
        if (projectName != null) {
            setTitle("Expenses: " + projectName);
        }

        dbHelper = new DBHelper(this);
        expenseList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerViewExpenses);
        btnAddExpense = findViewById(R.id.btnAddExpense);

        adapter = new ExpenseAdapter(expenseList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddExpenseActivity.class);
            intent.putExtra("project_id", projectId);
            startActivity(intent);
        });

        loadExpenses();
    }

    private void loadExpenses() {
        expenseList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_EXPENSE + " WHERE " + DBHelper.COL_EXP_PROJECT_ID + "=?", 
                new String[]{String.valueOf(projectId)});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_EXP_ID));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_EXP_DATE));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COL_EXP_AMOUNT));
                String currency = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_EXP_CURRENCY));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_EXP_TYPE));
                String method = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_EXP_PAYMENT_METHOD));
                String claimant = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_EXP_CLAIMANT));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_EXP_PAYMENT_STATUS));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_EXP_DESCRIPTION));
                String loc = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_EXP_LOCATION));

                expenseList.add(new Expense(id, projectId, date, amount, currency, type, method, claimant, status, desc, loc));
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExpenses();
    }

    @Override
    public void onEditClick(Expense expense) {
        Intent intent = new Intent(this, AddExpenseActivity.class);
        intent.putExtra("project_id", projectId);
        intent.putExtra("expense", expense);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Expense expense) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Expense")
                .setMessage("Are you sure you want to delete this expense?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.delete(DBHelper.TABLE_EXPENSE, DBHelper.COL_EXP_ID + "=?", new String[]{String.valueOf(expense.getId())});
                    loadExpenses();
                    Toast.makeText(this, "Expense deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }
}