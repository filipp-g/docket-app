package ca.carleton.comp3004f20.androidteamalpha.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TaskActivity extends AppCompatActivity {

    private String USERNAME;

    private DatabaseReference taskDatabase;

    private Spinner projectSpinner;
    private EditText nameEdit, dueDateEdit, dueTimeEdit, weightEdit, timeReqEdit, timeSpentEdit;
    private SwitchMaterial completeSwitch;
    private Button saveButton, deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        Intent intent = getIntent();
        USERNAME = intent.getStringExtra("NAME");

        taskDatabase = FirebaseDatabase.getInstance().getReference().child(USERNAME).child("tasks");
        initElements();
    }

    private void initElements() {
        nameEdit = findViewById(R.id.editTextName);
        populateProjects();

        dueDateEdit = findViewById(R.id.editTextDate);
        dueDateEdit.setOnClickListener(f -> showDatePickerDialog());
        dueTimeEdit = findViewById(R.id.editTextTime);
        dueTimeEdit.setOnClickListener(f -> showTimePickerDialog());

        weightEdit = findViewById(R.id.editTextWeight);
        timeReqEdit = findViewById(R.id.editTextTimeReq);
        timeSpentEdit = findViewById(R.id.editTextTimeSpent);
        completeSwitch = findViewById(R.id.switchComplete);

        saveButton = findViewById(R.id.btnSave);
        saveButton.setOnClickListener(f -> saveTask());
        deleteButton = findViewById(R.id.btnDelete);
        deleteButton.setOnClickListener(f -> deleteTask());
    }

    private void populateProjects() {
        List<String> projectList = new ArrayList<>();
        projectList.add("long name project");
        projectList.add("comp3203");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, projectList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        projectSpinner = findViewById(R.id.spinnerProjects);
        projectSpinner.setAdapter(adapter);
    }

    private void saveTask() {
        String name = nameEdit.getText().toString();
        if (name.isEmpty()) {
            nameEdit.setError("Name is required");
            return;
        }
        String project = projectSpinner.getSelectedItem().toString();
        String dueDate = dueDateEdit.getText().toString();
        String dueTime = dueTimeEdit.getText().toString();

        int weight = 0, timeReq = 0, timeSpent = 0;
        if (!weightEdit.getText().toString().isEmpty()) {
            weight = Integer.parseInt(weightEdit.getText().toString());
        }
        if (!timeReqEdit.getText().toString().isEmpty()) {
            timeReq = Integer.parseInt(timeReqEdit.getText().toString());
        }
        if (!timeSpentEdit.getText().toString().isEmpty()) {
            timeSpent = Integer.parseInt(timeSpentEdit.getText().toString());
        }
        boolean complete = completeSwitch.isChecked();

        String normalDueDate = dueDate;

        DatabaseReference pushRef = taskDatabase.push();
        pushRef.setValue(
                new Task(pushRef.getKey(), name, project, "Nov 25 2020", dueTime, weight,
                timeReq, timeSpent, complete),
                (error, ref) -> {
                    if (error == null) {
                        Toast.makeText(getApplicationContext(), "Task saved", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private void deleteTask() {
        //TODO once we have saving figured out
    }

    private void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment(dueDateEdit);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void showTimePickerDialog() {
        DialogFragment newFragment = new TimePickerFragment(dueTimeEdit);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        private EditText mEditText;
        private String[] shortMonths;

        public DatePickerFragment(EditText mEditText) {
            this.mEditText = mEditText;
            this.shortMonths = new DateFormatSymbols().getShortMonths();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int year, month, day;
            String text = mEditText.getText().toString();
            if (text.isEmpty()) {
                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
            } else {
                year = Integer.parseInt(text.substring(7));
                month = Arrays.asList(shortMonths).indexOf(text.substring(0, 3));
                day = Integer.parseInt(text.substring(4, 6));
            }
            return new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT, this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            mEditText.setText(
                    String.format(Locale.CANADA, "%s %02d %d", shortMonths[month], day, year)
            );
        }
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
        private EditText mEditText;

        public TimePickerFragment(EditText mEditText) {
            this.mEditText = mEditText;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int hour, minute;
            String text = mEditText.getText().toString();
            if (text.isEmpty()) {
                final Calendar c = Calendar.getInstance();
                hour = c.get(Calendar.HOUR_OF_DAY);
                minute = c.get(Calendar.MINUTE);
            } else {
                hour = Integer.parseInt(text.substring(0, text.indexOf(":")));
                minute = Integer.parseInt(text.substring(text.indexOf(":") + 1, text.indexOf(":") + 3));
            }
            return new TimePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT,
                    this, hour, minute, false);
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String AMPM = hourOfDay > 12 ? "PM" : "AM";     //TODO fix AM/PM issue
            mEditText.setText(
                    String.format(Locale.CANADA, "%d:%02d %s", hourOfDay % 12, minute, AMPM)
            );
        }
    }
}