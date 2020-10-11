package ca.carleton.comp3004f20.androidteamalpha.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class TaskActivity extends AppCompatActivity {

    private Spinner projects;
    private EditText dueDate, dueTime, weight, timeReq, timeSpent;
    private SwitchMaterial complete;
    private Button saveButton, deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        projects = findViewById(R.id.spinnerProjects);
        dueDate = findViewById(R.id.editTextDate);
        dueTime = findViewById(R.id.editTextTime);
        weight = findViewById(R.id.editTextWeight);
        timeReq = findViewById(R.id.editTextTimeReq);
        timeSpent = findViewById(R.id.editTextTimeSpent);
        complete = findViewById(R.id.switchComplete);

        saveButton = findViewById(R.id.btnSave);
        saveButton.setOnClickListener((f) -> saveTask());
        deleteButton = findViewById(R.id.btnDelete);
        deleteButton.setOnClickListener((f) -> deleteTask());
    }

    private void saveTask() {
        //TODO need to figure out project model structure before saving tasks
    }

    private void deleteTask() {
        //TODO once we have saving figured out
    }
}