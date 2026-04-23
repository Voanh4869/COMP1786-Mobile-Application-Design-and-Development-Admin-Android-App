package com.example.expensetrackerapp.activities;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expensetrackerapp.database.DBHelper;
import com.example.expensetrackerapp.R;
import com.example.expensetrackerapp.models.Project;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class AddProjectActivity extends AppCompatActivity {

    EditText etName, etDesc, etStart, etEnd, etManager, etBudget;
    Spinner spStatus;
    Button btnSave;
    DBHelper dbHelper;
    Project existingProject;

    String[] statusList = {"Active", "Completed", "On Hold"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        dbHelper = new DBHelper(this);

        etName = findViewById(R.id.etName);
        etDesc = findViewById(R.id.etDesc);
        etStart = findViewById(R.id.etStart);
        etEnd = findViewById(R.id.etEnd);
        etManager = findViewById(R.id.etManager);
        etBudget = findViewById(R.id.etBudget);
        spStatus = findViewById(R.id.spStatus);
        btnSave = findViewById(R.id.btnSave);

        // Make date fields non-editable by keyboard
        etStart.setFocusable(false);
        etEnd.setFocusable(false);

        etStart.setOnClickListener(v -> showDatePicker(etStart));
        etEnd.setOnClickListener(v -> showDatePicker(etEnd));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, statusList);
        spStatus.setAdapter(adapter);

        existingProject = (Project) getIntent().getSerializableExtra("project");
        if (existingProject != null) {
            setTitle("Edit Project");
            fillFields(existingProject);
            btnSave.setText("Update Project");
        }

        btnSave.setOnClickListener(v -> saveProject());
    }

    private void showDatePicker(EditText editText) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String date = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year1);
                    editText.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void fillFields(Project project) {
        etName.setText(project.getName());
        etDesc.setText(project.getDescription());
        etStart.setText(project.getStartDate());
        etEnd.setText(project.getEndDate());
        etManager.setText(project.getManager());
        etBudget.setText(String.valueOf(project.getBudget()));
        
        int statusIndex = Arrays.asList(statusList).indexOf(project.getStatus());
        if (statusIndex >= 0) {
            spStatus.setSelection(statusIndex);
        }
    }

    private void saveProject() {

        String name = etName.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        String start = etStart.getText().toString().trim();
        String end = etEnd.getText().toString().trim();
        String manager = etManager.getText().toString().trim();
        String budgetStr = etBudget.getText().toString().trim();
        String status = spStatus.getSelectedItem().toString();

        if (name.isEmpty() || desc.isEmpty() || start.isEmpty() ||
                end.isEmpty() || manager.isEmpty() || budgetStr.isEmpty()) {

            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double budget;
        try {
            budget = Double.parseDouble(budgetStr);
        } catch (Exception e) {
            Toast.makeText(this, "Invalid budget value", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_NAME, name);
        values.put(DBHelper.COL_DESC, desc);
        values.put(DBHelper.COL_START, start);
        values.put(DBHelper.COL_END, end);
        values.put(DBHelper.COL_MANAGER, manager);
        values.put(DBHelper.COL_STATUS, status);
        values.put(DBHelper.COL_BUDGET, budget);

        long result;
        if (existingProject != null) {
            result = db.update(DBHelper.TABLE_PROJECT, values, DBHelper.COL_ID + "=?", 
                    new String[]{String.valueOf(existingProject.getId())});
        } else {
            result = db.insert(DBHelper.TABLE_PROJECT, null, values);
        }

        if (result != -1) {
            Toast.makeText(this, existingProject != null ? "Project Updated!" : "Project Saved!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error saving project", Toast.LENGTH_SHORT).show();
        }
    }
}