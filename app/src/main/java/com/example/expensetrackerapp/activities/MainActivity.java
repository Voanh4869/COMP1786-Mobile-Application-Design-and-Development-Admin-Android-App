package com.example.expensetrackerapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expensetrackerapp.R;

public class MainActivity extends AppCompatActivity {

    Button btnAdd, btnView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = findViewById(R.id.btnAdd);
        btnView = findViewById(R.id.btnView);

        btnAdd.setOnClickListener(v -> {
            startActivity(new Intent(this, AddProjectActivity.class));
        });

        btnView.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ViewProjectsActivity.class));
        });
    }
}