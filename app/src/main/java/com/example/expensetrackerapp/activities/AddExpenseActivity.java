package com.example.expensetrackerapp.activities;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expensetrackerapp.R;
import com.example.expensetrackerapp.database.DBHelper;
import com.example.expensetrackerapp.models.Expense;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class AddExpenseActivity extends AppCompatActivity {

    EditText etDate, etAmount, etClaimant, etDescription, etLocation;
    Spinner spCurrency, spType, spPaymentMethod, spPaymentStatus;
    Button btnSave;
    DBHelper dbHelper;
    int projectId = -1;
    Expense existingExpense;

    String[] currencies = {"USD", "EUR", "GBP", "VND"};
    String[] types = {"Travel", "Equipment", "Materials", "Services", "Software/Licenses", "Labour costs", "Utilities", "Miscellaneous"};
    String[] methods = {"Cash", "Credit Card", "Bank Transfer", "Cheque"};
    String[] statuses = {"Paid", "Pending", "Reimbursed"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        dbHelper = new DBHelper(this);
        projectId = getIntent().getIntExtra("project_id", -1);
        existingExpense = (Expense) getIntent().getSerializableExtra("expense");

        initViews();
        setupSpinners();

        if (existingExpense != null) {
            setTitle("Edit Expense");
            fillFields(existingExpense);
            btnSave.setText("Update Expense");
        } else {
            setTitle("Add Expense");
        }

        etDate.setOnClickListener(v -> showDatePicker());
        btnSave.setOnClickListener(v -> saveExpense());
    }

    private void initViews() {
        etDate = findViewById(R.id.etExpDate);
        etAmount = findViewById(R.id.etExpAmount);
        spCurrency = findViewById(R.id.spExpCurrency);
        etClaimant = findViewById(R.id.etExpClaimant);
        etDescription = findViewById(R.id.etExpDescription);
        etLocation = findViewById(R.id.etExpLocation);
        spType = findViewById(R.id.spExpType);
        spPaymentMethod = findViewById(R.id.spExpPaymentMethod);
        spPaymentStatus = findViewById(R.id.spExpPaymentStatus);
        btnSave = findViewById(R.id.btnSaveExpense);
    }

    private void setupSpinners() {
        spCurrency.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, currencies));
        spType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, types));
        spPaymentMethod.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, methods));
        spPaymentStatus.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, statuses));
    }

    private void fillFields(Expense expense) {
        etDate.setText(expense.getDate());
        etAmount.setText(String.valueOf(expense.getAmount()));
        etClaimant.setText(expense.getClaimant());
        etDescription.setText(expense.getDescription());
        etLocation.setText(expense.getLocation());

        spCurrency.setSelection(Arrays.asList(currencies).indexOf(expense.getCurrency()));
        spType.setSelection(Arrays.asList(types).indexOf(expense.getType()));
        spPaymentMethod.setSelection(Arrays.asList(methods).indexOf(expense.getPaymentMethod()));
        spPaymentStatus.setSelection(Arrays.asList(statuses).indexOf(expense.getPaymentStatus()));
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            etDate.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d", day, month + 1, year));
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveExpense() {
        String date = etDate.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String currency = spCurrency.getSelectedItem().toString();
        String claimant = etClaimant.getText().toString().trim();
        String type = spType.getSelectedItem().toString();
        String method = spPaymentMethod.getSelectedItem().toString();
        String status = spPaymentStatus.getSelectedItem().toString();
        String desc = etDescription.getText().toString().trim();
        String loc = etLocation.getText().toString().trim();

        if (date.isEmpty() || amountStr.isEmpty() || claimant.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_EXP_PROJECT_ID, projectId);
        values.put(DBHelper.COL_EXP_DATE, date);
        values.put(DBHelper.COL_EXP_AMOUNT, amount);
        values.put(DBHelper.COL_EXP_CURRENCY, currency);
        values.put(DBHelper.COL_EXP_TYPE, type);
        values.put(DBHelper.COL_EXP_PAYMENT_METHOD, method);
        values.put(DBHelper.COL_EXP_CLAIMANT, claimant);
        values.put(DBHelper.COL_EXP_PAYMENT_STATUS, status);
        values.put(DBHelper.COL_EXP_DESCRIPTION, desc);
        values.put(DBHelper.COL_EXP_LOCATION, loc);

        long result;
        if (existingExpense != null) {
            result = db.update(DBHelper.TABLE_EXPENSE, values, DBHelper.COL_EXP_ID + "=?", 
                    new String[]{String.valueOf(existingExpense.getId())});
        } else {
            result = db.insert(DBHelper.TABLE_EXPENSE, null, values);
        }

        if (result != -1) {
            Toast.makeText(this, "Expense Saved", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error saving expense", Toast.LENGTH_SHORT).show();
        }
    }
}