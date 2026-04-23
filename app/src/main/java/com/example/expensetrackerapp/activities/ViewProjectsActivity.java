package com.example.expensetrackerapp.activities;

import android.content.ContentValues;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetrackerapp.R;
import com.example.expensetrackerapp.adapter.ProjectAdapter;
import com.example.expensetrackerapp.database.DBHelper;
import com.example.expensetrackerapp.models.Project;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ViewProjectsActivity extends AppCompatActivity implements ProjectAdapter.OnProjectClickListener {

    RecyclerView recyclerView;
    ProjectAdapter adapter;
    List<Project> projectList;
    DBHelper dbHelper;

    EditText etSearchName, etSearchDate, etSearchOwner;
    Spinner spSearchStatus;
    Button btnResetSearch, btnAddProjectFromView;

    String[] statusSearchList = {"All Statuses", "Active", "Completed", "On Hold"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_projects);

        initViews();
        setupSearchListeners();

        dbHelper = new DBHelper(this);
        projectList = new ArrayList<>();

        adapter = new ProjectAdapter(projectList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnAddProjectFromView.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddProjectActivity.class);
            startActivity(intent);
        });

        loadProjects();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewProjects);
        etSearchName = findViewById(R.id.etSearchName);
        etSearchDate = findViewById(R.id.etSearchDate);
        etSearchOwner = findViewById(R.id.etSearchOwner);
        spSearchStatus = findViewById(R.id.spSearchStatus);
        btnResetSearch = findViewById(R.id.btnResetSearch);
        btnAddProjectFromView = findViewById(R.id.btnAddProjectFromView);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, statusSearchList);
        spSearchStatus.setAdapter(statusAdapter);
    }

    private void setupSearchListeners() {
        TextWatcher commonWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadProjects();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };

        etSearchName.addTextChangedListener(commonWatcher);
        etSearchOwner.addTextChangedListener(commonWatcher);
        etSearchDate.addTextChangedListener(commonWatcher);

        etSearchDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) -> {
                String date = String.format(Locale.getDefault(), "%02d/%02d/%04d", day, month + 1, year);
                etSearchDate.setText(date);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        spSearchStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadProjects();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnResetSearch.setOnClickListener(v -> {
            etSearchName.setText("");
            etSearchDate.setText("");
            etSearchOwner.setText("");
            spSearchStatus.setSelection(0);
            loadProjects();
        });
    }

    private void loadProjects() {
        projectList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        StringBuilder query = new StringBuilder("SELECT * FROM " + DBHelper.TABLE_PROJECT + " WHERE 1=1");
        List<String> args = new ArrayList<>();

        String nameSearch = etSearchName.getText().toString().trim();
        if (!nameSearch.isEmpty()) {
            query.append(" AND (" + DBHelper.COL_NAME + " LIKE ? OR " + DBHelper.COL_DESC + " LIKE ?)");
            args.add("%" + nameSearch + "%");
            args.add("%" + nameSearch + "%");
        }

        String ownerSearch = etSearchOwner.getText().toString().trim();
        if (!ownerSearch.isEmpty()) {
            query.append(" AND " + DBHelper.COL_MANAGER + " LIKE ?");
            args.add("%" + ownerSearch + "%");
        }

        String dateSearch = etSearchDate.getText().toString().trim();
        if (!dateSearch.isEmpty()) {
            query.append(" AND " + DBHelper.COL_START + " = ?");
            args.add(dateSearch);
        }

        String statusSearch = spSearchStatus.getSelectedItem().toString();
        if (!statusSearch.equals("All Statuses")) {
            query.append(" AND " + DBHelper.COL_STATUS + " = ?");
            args.add(statusSearch);
        }

        Cursor cursor = db.rawQuery(query.toString(), args.toArray(new String[0]));

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_NAME));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_DESC));
                String start = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_START));
                String end = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_END));
                String manager = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_MANAGER));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_STATUS));
                double budget = cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COL_BUDGET));

                projectList.add(new Project(id, name, desc, start, end, manager, status, budget));
            } while (cursor.moveToNext());
        }

        cursor.close();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onEditClick(Project project) {
        Intent intent = new Intent(this, AddProjectActivity.class);
        intent.putExtra("project", project);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Project project) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Project")
                .setMessage("Are you sure you want to delete this project and all its expenses?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.delete(DBHelper.TABLE_EXPENSE, DBHelper.COL_EXP_PROJECT_ID + "=?", new String[]{String.valueOf(project.getId())});
                    db.delete(DBHelper.TABLE_PROJECT, DBHelper.COL_ID + "=?", new String[]{String.valueOf(project.getId())});
                    loadProjects();
                    Toast.makeText(this, "Project deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onProjectClick(Project project) {
        Intent intent = new Intent(this, ViewExpensesActivity.class);
        intent.putExtra("project_id", project.getId());
        intent.putExtra("project_name", project.getName());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProjects();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_projects_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_reset_db) {
            resetDatabase();
            return true;
        } else if (item.getItemId() == R.id.action_upload) {
            if (isNetworkAvailable()) {
                uploadDataToCloud();
            } else {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (item.getItemId() == R.id.action_sync_from_cloud) {
            if (isNetworkAvailable()) {
                syncFromCloud();
            } else {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                android.net.Network network = connectivityManager.getActiveNetwork();
                if (network == null) return false;
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
                return capabilities != null && (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET));
            } else {
                android.net.NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                return activeNetwork != null && activeNetwork.isConnected();
            }
        }
        return false;
    }

    private void syncFromCloud() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://expensetrackerapp-5f7a7-default-rtdb.firebaseio.com/");
        DatabaseReference myRef = database.getReference("data_sync");

        myRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.execSQL("DELETE FROM " + DBHelper.TABLE_EXPENSE);
                    db.execSQL("DELETE FROM " + DBHelper.TABLE_PROJECT);

                    for (DataSnapshot projectSnap : snapshot.getChildren()) {
                        ContentValues pValues = new ContentValues();
                        pValues.put(DBHelper.COL_NAME, projectSnap.child("name").getValue(String.class));
                        pValues.put(DBHelper.COL_DESC, projectSnap.child("description").getValue(String.class));
                        pValues.put(DBHelper.COL_START, projectSnap.child("startDate").getValue(String.class));
                        pValues.put(DBHelper.COL_END, projectSnap.child("endDate").getValue(String.class));
                        pValues.put(DBHelper.COL_MANAGER, projectSnap.child("manager").getValue(String.class));
                        pValues.put(DBHelper.COL_STATUS, projectSnap.child("status").getValue(String.class));
                        pValues.put(DBHelper.COL_BUDGET, projectSnap.child("budget").getValue(Double.class));

                        long pId = db.insert(DBHelper.TABLE_PROJECT, null, pValues);

                        DataSnapshot expensesSnap = projectSnap.child("expenses");
                        if (expensesSnap.exists()) {
                            for (DataSnapshot expSnap : expensesSnap.getChildren()) {
                                ContentValues eValues = new ContentValues();
                                eValues.put(DBHelper.COL_EXP_PROJECT_ID, pId);
                                eValues.put(DBHelper.COL_EXP_DATE, expSnap.child("date").getValue(String.class));
                                eValues.put(DBHelper.COL_EXP_AMOUNT, expSnap.child("amount").getValue(Double.class));
                                eValues.put(DBHelper.COL_EXP_CURRENCY, expSnap.child("currency").getValue(String.class));
                                eValues.put(DBHelper.COL_EXP_TYPE, expSnap.child("type").getValue(String.class));
                                eValues.put(DBHelper.COL_EXP_PAYMENT_METHOD, expSnap.child("method").getValue(String.class));
                                eValues.put(DBHelper.COL_EXP_CLAIMANT, expSnap.child("claimant").getValue(String.class));
                                eValues.put(DBHelper.COL_EXP_PAYMENT_STATUS, expSnap.child("status").getValue(String.class));
                                // FIXED: Read description and location from Firebase
                                eValues.put(DBHelper.COL_EXP_DESCRIPTION, expSnap.child("description").getValue(String.class));
                                eValues.put(DBHelper.COL_EXP_LOCATION, expSnap.child("location").getValue(String.class));
                                
                                db.insert(DBHelper.TABLE_EXPENSE, null, eValues);
                            }
                        }
                    }
                    loadProjects();
                    Toast.makeText(ViewProjectsActivity.this, "Data synced from cloud!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ViewProjectsActivity.this, "No data found on cloud", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ViewProjectsActivity.this, "Sync failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadDataToCloud() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://expensetrackerapp-5f7a7-default-rtdb.firebaseio.com/");
        DatabaseReference myRef = database.getReference("data_sync");

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        Cursor projectCursor = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_PROJECT, null);
        List<Map<String, Object>> cloudData = new ArrayList<>();

        if (projectCursor.moveToFirst()) {
            do {
                int projectId = projectCursor.getInt(projectCursor.getColumnIndexOrThrow(DBHelper.COL_ID));
                Map<String, Object> projectMap = new HashMap<>();
                projectMap.put("name", projectCursor.getString(projectCursor.getColumnIndexOrThrow(DBHelper.COL_NAME)));
                projectMap.put("description", projectCursor.getString(projectCursor.getColumnIndexOrThrow(DBHelper.COL_DESC)));
                projectMap.put("startDate", projectCursor.getString(projectCursor.getColumnIndexOrThrow(DBHelper.COL_START)));
                projectMap.put("endDate", projectCursor.getString(projectCursor.getColumnIndexOrThrow(DBHelper.COL_END)));
                projectMap.put("manager", projectCursor.getString(projectCursor.getColumnIndexOrThrow(DBHelper.COL_MANAGER)));
                projectMap.put("status", projectCursor.getString(projectCursor.getColumnIndexOrThrow(DBHelper.COL_STATUS)));
                projectMap.put("budget", projectCursor.getDouble(projectCursor.getColumnIndexOrThrow(DBHelper.COL_BUDGET)));

                Cursor expCursor = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_EXPENSE + " WHERE " + DBHelper.COL_EXP_PROJECT_ID + "=?", 
                        new String[]{String.valueOf(projectId)});
                
                List<Map<String, Object>> expensesList = new ArrayList<>();
                if (expCursor.moveToFirst()) {
                    do {
                        Map<String, Object> expMap = new HashMap<>();
                        expMap.put("date", expCursor.getString(expCursor.getColumnIndexOrThrow(DBHelper.COL_EXP_DATE)));
                        expMap.put("amount", expCursor.getDouble(expCursor.getColumnIndexOrThrow(DBHelper.COL_EXP_AMOUNT)));
                        expMap.put("currency", expCursor.getString(expCursor.getColumnIndexOrThrow(DBHelper.COL_EXP_CURRENCY)));
                        expMap.put("type", expCursor.getString(expCursor.getColumnIndexOrThrow(DBHelper.COL_EXP_TYPE)));
                        expMap.put("method", expCursor.getString(expCursor.getColumnIndexOrThrow(DBHelper.COL_EXP_PAYMENT_METHOD)));
                        expMap.put("claimant", expCursor.getString(expCursor.getColumnIndexOrThrow(DBHelper.COL_EXP_CLAIMANT)));
                        expMap.put("status", expCursor.getString(expCursor.getColumnIndexOrThrow(DBHelper.COL_EXP_PAYMENT_STATUS)));
                        // FIXED: Include description and location in upload
                        expMap.put("description", expCursor.getString(expCursor.getColumnIndexOrThrow(DBHelper.COL_EXP_DESCRIPTION)));
                        expMap.put("location", expCursor.getString(expCursor.getColumnIndexOrThrow(DBHelper.COL_EXP_LOCATION)));
                        
                        expensesList.add(expMap);
                    } while (expCursor.moveToNext());
                }
                expCursor.close();
                
                projectMap.put("expenses", expensesList);
                cloudData.add(projectMap);

            } while (projectCursor.moveToNext());
        }
        projectCursor.close();

        if (cloudData.isEmpty()) {
            Toast.makeText(this, "No data to upload", Toast.LENGTH_SHORT).show();
            return;
        }

        myRef.setValue(cloudData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ViewProjectsActivity.this, "All projects and expenses uploaded to Firebase!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(ViewProjectsActivity.this, "Upload failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void resetDatabase() {
        new AlertDialog.Builder(this)
                .setTitle("Reset Database")
                .setMessage("This will delete ALL projects and ALL expenses. Are you sure?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.execSQL("DELETE FROM " + DBHelper.TABLE_EXPENSE);
                    db.execSQL("DELETE FROM " + DBHelper.TABLE_PROJECT);
                    loadProjects();
                    Toast.makeText(this, "Database reset", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }
}