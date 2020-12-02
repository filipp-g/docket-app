package ca.carleton.comp3004f20.androidteamalpha.app;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TaskFragment extends Fragment {
    private static final String TASK = "taskObj";
    private Task task;

    private DatabaseReference projectDatabase;
    private DatabaseReference taskDatabase;

    private Spinner projectSpinner;
    private EditText nameEdit, dueDateEdit, dueTimeEdit, weightEdit, timeReqEdit, timeSpentEdit;
    private SwitchMaterial completeSwitch;

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

        projectDatabase = FirebaseDatabase.getInstance()
                .getReference()
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("projects");
        initElements(view);

        taskDatabase = FirebaseDatabase.getInstance()
                .getReference()
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
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
        dueDateEdit.setOnClickListener(v ->
                Utilities.showDatePickerDialog(getActivity(), dueDateEdit)
        );
        dueTimeEdit = view.findViewById(R.id.editTextTime);
        dueTimeEdit.setOnClickListener(v ->
                Utilities.showTimePickerDialog(getActivity(), dueTimeEdit)
        );

        weightEdit = view.findViewById(R.id.editTextWeight);
        timeReqEdit = view.findViewById(R.id.editTextTimeReq);
        timeSpentEdit = view.findViewById(R.id.editTextTimeSpent);
        completeSwitch = view.findViewById(R.id.switchComplete);

        Button saveButton = view.findViewById(R.id.btnSave);
        saveButton.setOnClickListener(f -> saveTask());

        Button deleteButton = view.findViewById(R.id.btnDelete);
        if (task == null) {
            deleteButton.setVisibility(View.INVISIBLE);
        } else {
            deleteButton.setOnClickListener(f -> deleteTask());
        }
    }

    private void populateProjects(View view) {
        projectDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> projectList = new ArrayList<>();
                for (DataSnapshot dataSnap : snapshot.getChildren()){
                    Project project = dataSnap.getValue(Project.class);
                    projectList.add(project.getName());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        getActivity(), R.layout.projects_spinner_item, projectList
                );

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                projectSpinner = view.findViewById(R.id.spinnerProjects);
                projectSpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void populateTask(Task task) {
        nameEdit.setText(task.getName());
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
                    Toast.makeText(getContext(), "Task saved", Toast.LENGTH_SHORT).show();
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

    private void launchProjectsFragment() {
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new ProjectsFragment())
                .commit();
    }
}