package ca.carleton.comp3004f20.androidteamalpha.app;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TaskFragment extends Fragment {
    private static final String TASK = "taskObj";
    private Task task;

    private DatabaseReference taskDatabase;

    private Spinner projectSpinner;
    private EditText nameEdit, dueDateEdit, dueTimeEdit, weightEdit, timeReqEdit, timeSpentEdit;
    private SwitchMaterial completeSwitch;
    private Button saveButton, deleteButton;

    public TaskFragment() {
        // Required empty public constructor
    }

    public static TaskFragment newInstance(Task task) {
        TaskFragment fragment = new TaskFragment();
        Bundle args = new Bundle();
        args.putSerializable(TASK, task);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task, container, false);

        if (getArguments() != null) {
            task = (Task) getArguments().getSerializable(TASK);
        }

        taskDatabase = FirebaseDatabase.getInstance()
                .getReference()
                .child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
                .child("tasks");
        initElements(view);

        if (task == null) {
            task = new Task();
        } else {
            populateTask(task);
        }

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
        if (task == null) {
            deleteButton.setVisibility(View.INVISIBLE);
        } else {
            deleteButton.setOnClickListener(f -> deleteTask());
        }
    }


    private void populateProjects(View view) {
        List<String> projectList = new ArrayList<>();
        projectList.add("super unnecessary long name project");
        projectList.add("comp3203");        //TODO put real projects

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getActivity(), R.layout.projects_spinner_item, projectList
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        projectSpinner = view.findViewById(R.id.spinnerProjects);
        projectSpinner.setAdapter(adapter);
    }

    private void populateTask(Task task) {
        nameEdit.setText(task.getName());
//        projectSpinner            //TODO set project name
        dueDateEdit.setText(task.getDueDate());
        dueTimeEdit.setText(task.getDueTime());
        weightEdit.setText("" + task.getWeight());
        timeReqEdit.setText("" + task.getTimeRequired());
        timeSpentEdit.setText("" + task.getTimeSpent());
        completeSwitch.setChecked(task.isComplete());
    }

    private void saveTask() {
        if (nameEdit.getText().toString().isEmpty()) {
            nameEdit.setError("Name is required");
            return;
        }
        task.setName(nameEdit.getText().toString());
        task.setProjectId(projectSpinner.getSelectedItem().toString());

        if (dueDateEdit.getText().toString().isEmpty()) {
            dueDateEdit.setError("Date is required");
            return;
        }
        task.setDueDate(dueDateEdit.getText().toString());
        task.setDueTime(dueTimeEdit.getText().toString());

        if (!weightEdit.getText().toString().isEmpty()) {
            task.setWeight(Integer.parseInt(weightEdit.getText().toString()));
        }
        if (!timeReqEdit.getText().toString().isEmpty()) {
            task.setTimeRequired(Integer.parseInt(timeReqEdit.getText().toString()));
        }
        if (!timeSpentEdit.getText().toString().isEmpty()) {
            task.setTimeSpent(Integer.parseInt(timeSpentEdit.getText().toString()));
        }
        task.setComplete(completeSwitch.isChecked());

        if (task.getId().isEmpty()) {
            DatabaseReference pushRef = taskDatabase.push();
            task.setId(pushRef.getKey());
            pushRef.setValue(task, (error, ref) -> {
                if (error == null) {
                    launchProjectsFragment();
                } else {
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            taskDatabase.child(task.getId()).setValue(task);
            launchProjectsFragment();
        }
    }

    private void deleteTask() {
        new AlertDialog.Builder(getContext())
                .setMessage("Are you sure you want to delete " + task.getName() + "?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    taskDatabase.child(task.getId()).removeValue();
                    Toast.makeText(getContext(), task.getName() + " deleted", Toast.LENGTH_SHORT).show();
                    launchProjectsFragment();
                })
                .setNegativeButton(android.R.string.no, null).show();
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

        @NonNull
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
                year = Integer.parseInt(text.substring(0, 4));
                month = Integer.parseInt(text.substring(5, 7)) - 1;
                day = Integer.parseInt(text.substring(8, 10));
            }
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            mEditText.setText(
                    String.format(Locale.CANADA, "%d-%02d-%02d", year, month + 1, day)
            );
        }
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
        private EditText mEditText;

        public TimePickerFragment(EditText mEditText) {
            this.mEditText = mEditText;
        }

        @NonNull
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
            return new TimePickerDialog(getActivity(), this, hour, minute, false);
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mEditText.setText(String.format(Locale.CANADA, "%02d:%02d", hourOfDay, minute));
        }
    }

    private void launchProjectsFragment() {
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new ProjectsFragment())
                .commit();
    }
}