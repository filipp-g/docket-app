package ca.carleton.comp3004f20.androidteamalpha.app;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskFragment extends Fragment {
    private static final String EMAIL = "email";
    private static final String USER= "user";

    private String email;
    private String user;

    private String USERNAME;
    private DatabaseReference taskDatabase;

    private Spinner projectSpinner;
    private EditText nameEdit, dueDateEdit, dueTimeEdit, weightEdit, timeReqEdit, timeSpentEdit;
    private SwitchMaterial completeSwitch;
    private Button saveButton, deleteButton;

    public TaskFragment() {
        // Required empty public constructor
    }

    public static TaskFragment newInstance(String email, String user) {
        TaskFragment fragment = new TaskFragment();
        Bundle args = new Bundle();
        args.putString(EMAIL, email);
        args.putString(USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task, container, false);

        if (getArguments() != null) {
            email = getArguments().getString(EMAIL);
            user = getArguments().getString(USER);
        }

        taskDatabase = FirebaseDatabase.getInstance().getReference().child(user).child("tasks");
        initElements(view);

        return view;
    }

    private void initElements(View view) {
        nameEdit = view.findViewById(R.id.editTextName);
        populateProjects(view);

        dueDateEdit = view.findViewById(R.id.editTextDate);
        dueDateEdit.setOnClickListener(f -> showDatePickerDialog());
        dueTimeEdit = view.findViewById(R.id.editTextTime);
        dueTimeEdit.setOnClickListener(f -> showTimePickerDialog());

        weightEdit = view.findViewById(R.id.editTextWeight);
        timeReqEdit = view.findViewById(R.id.editTextTimeReq);
        timeSpentEdit = view.findViewById(R.id.editTextTimeSpent);
        completeSwitch = view.findViewById(R.id.switchComplete);

        saveButton = view.findViewById(R.id.btnSave);
        saveButton.setOnClickListener(f -> saveTask());
        deleteButton = view.findViewById(R.id.btnDelete);
        deleteButton.setOnClickListener(f -> deleteTask());
    }


    private void populateProjects(View view) {
        List<String> projectList = new ArrayList<>();
        projectList.add("long name project");
        projectList.add("comp3203");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item, projectList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        projectSpinner = view.findViewById(R.id.spinnerProjects);
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

        DatabaseReference pushRef = taskDatabase.push();
        pushRef.setValue(
                new Task(pushRef.getKey(), name, project, dueDate, dueTime, weight,
                        timeReq, timeSpent, complete),
                (error, ref) -> {
                    if (error == null) {
                        Toast.makeText(getActivity().getApplicationContext(), "Task saved", Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, ProjectsFragment.newInstance(email, user)).commit();
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private void deleteTask() {
        //TODO once we have saving figured out
    }

    private void showDatePickerDialog() {
        DialogFragment newFragment = new TaskFragment.DatePickerFragment(dueDateEdit);
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    private void showTimePickerDialog() {
        DialogFragment newFragment = new TaskFragment.TimePickerFragment(dueTimeEdit);
        newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
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