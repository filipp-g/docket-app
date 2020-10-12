package ca.carleton.comp3004f20.androidteamalpha.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class TaskActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;

    private Spinner project;
    private EditText dueDate, dueTime, weight, timeReq, timeSpent;
    private SwitchMaterial complete;
    private Button saveButton, deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        initElements();
    }

    private void initElements() {
        project = findViewById(R.id.spinnerProjects);  //TODO populate with projects

        dueDate = findViewById(R.id.editTextDate);
        dueDate.setOnClickListener(f -> showDatePickerDialog());
        dueTime = findViewById(R.id.editTextTime);
        dueTime.setOnClickListener(f -> showTimePickerDialog());

        weight = findViewById(R.id.editTextWeight);
        timeReq = findViewById(R.id.editTextTimeReq);
        timeSpent = findViewById(R.id.editTextTimeSpent);
        complete = findViewById(R.id.switchComplete);

        saveButton = findViewById(R.id.btnSave);
        saveButton.setOnClickListener(f -> saveTask());
        deleteButton = findViewById(R.id.btnDelete);
        deleteButton.setOnClickListener(f -> deleteTask());
    }

    private void saveTask() {
        //TODO figure out how to store project
//        Date date = dueDate
    }

    private void deleteTask() {
        //TODO once we have saving figured out
    }

    private void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment(dueDate);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void showTimePickerDialog() {
        DialogFragment newFragment = new TimePickerFragment(dueTime);
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