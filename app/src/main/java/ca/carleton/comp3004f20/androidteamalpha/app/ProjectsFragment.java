package ca.carleton.comp3004f20.androidteamalpha.app;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class ProjectsFragment extends Fragment {

    public ProjectsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_projects, container, false);

        DatabaseReference taskDatabase = FirebaseDatabase.getInstance()
                .getReference()
                .child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        Query query = taskDatabase.child("tasks").orderByChild("dueDate");

        FirebaseRecyclerOptions<Task> options = new FirebaseRecyclerOptions.Builder<Task>()
                .setQuery(query, Task.class)
                .setLifecycleOwner(this)
                .build();

        TaskRecViewAdapter adapter = new TaskRecViewAdapter(options);

        RecyclerView projectsRecView = view.findViewById(R.id.projectsRecView);
        projectsRecView.setAdapter(adapter);
        projectsRecView.setLayoutManager(new LinearLayoutManager(getContext()));
        FloatingActionButton addTask = view.findViewById(R.id.btnAddTask);
        addTask.setOnClickListener(v -> getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, TaskFragment.newInstance(null))
                .commit()
        );

        FloatingActionButton deleteTask = view.findViewById(R.id.btnDeleteTask);
        deleteTask.setOnClickListener(v -> {
            int numberOfTasks = projectsRecView.getChildCount();
            ArrayList<String> selectedTaskNames = new ArrayList<String>();
            ArrayList<Integer> selectTaskPositions = new ArrayList<Integer>();
            for (int i = 0; i < numberOfTasks; i++) {
                View taskView = projectsRecView.getChildAt(i);
                TextView taskName = taskView.findViewById(R.id.txtTaskName);
                CheckBox checkBox = taskView.findViewById(R.id.chkBoxComplete);
                if (checkBox.isChecked()) {
                    selectedTaskNames.add((String) taskName.getText());
                    selectTaskPositions.add(i);
                }
            }
            if (selectedTaskNames.isEmpty()) {
                Toast.makeText(getActivity(), "No task selected...", Toast.LENGTH_SHORT).show();
            } else {
                deleteTasks(adapter, selectedTaskNames, selectTaskPositions);
            }
        });
        return view;
    }

    private void deleteTasks(TaskRecViewAdapter adapter, ArrayList<String> selectedTaskNames,
                             ArrayList<Integer> selectTaskPositions) {
        String message = "Are you sure you want to delete?\n";
        for (String task : selectedTaskNames) {
            message += task + "\n";
        }

        new AlertDialog.Builder(getContext())
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    for (Integer taskPosition : selectTaskPositions) {
                        adapter.getRef(taskPosition).removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Deleting tasks from list...", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

}