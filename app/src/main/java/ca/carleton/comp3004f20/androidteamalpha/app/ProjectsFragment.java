package ca.carleton.comp3004f20.androidteamalpha.app;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.widget.LinearLayout.VERTICAL;

public class ProjectsFragment extends Fragment {

    public ProjectsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_projects, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Projects");

        DatabaseReference projectDatabase = FirebaseDatabase.getInstance()
                .getReference()
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("projects");

        FirebaseRecyclerOptions<Project> options = new FirebaseRecyclerOptions.Builder<Project>()
                .setQuery(projectDatabase, Project.class)
                .setLifecycleOwner(this)
                .build();

        ProjectRecViewAdapter adapter = new ProjectRecViewAdapter(options);
        DividerItemDecoration divider = new DividerItemDecoration(view.getContext(), VERTICAL);

        RecyclerView projectsRecView = view.findViewById(R.id.projectsRecView);
        projectsRecView.setAdapter(adapter);
        projectsRecView.addItemDecoration(divider);
        projectsRecView.setLayoutManager(new LinearLayoutManager(getContext()));

        FloatingActionButton addProject = view.findViewById(R.id.btnAddProject);
        addProject.setOnClickListener(v -> {
                    int numberOfExistingProjects = projectsRecView.getChildCount();
                    final EditText projectText = new EditText(getContext());

                    new AlertDialog.Builder(getContext())
                            .setMessage("Enter Project Name")
                            .setIcon(android.R.drawable.ic_input_add)
                            .setView(projectText)
                            .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                                String projectName = projectText.getText().toString();
                                if (projectName.isEmpty()) {
                                    Toast.makeText(getContext(), "Project name is empty...", Toast.LENGTH_LONG).show();
                                } else {
                                    for (int i = 0; i < numberOfExistingProjects; i++) {
                                        TextView projectNameText = projectsRecView.getChildAt(i).findViewById(R.id.txtProjectName);
                                        if (projectNameText.equals(projectName)) {
                                            Toast.makeText(getContext(), "Project Already exists...", Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                    }

                                    Project project = new Project();
                                    project.setName(projectName);

                                    DatabaseReference pushRef = projectDatabase.push();
                                    project.setId(pushRef.getKey());
                                    pushRef.setValue(project, (error, ref) -> {
                                        if (error == null) {
                                            Toast.makeText(getContext(), "Project saved", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                }
        );

        FloatingActionButton deleteProject = view.findViewById(R.id.btnDeleteProject);
        deleteProject.setOnClickListener(v -> {
            int numberOfProjects = projectsRecView.getChildCount();
            ArrayList<String> selectedProjectNames = new ArrayList<String>();
            ArrayList<Integer> selectProjectPositions = new ArrayList<Integer>();
            for (int i = 0; i < numberOfProjects; i++) {
                View projectView = projectsRecView.getChildAt(i);
                TextView projectName = projectView.findViewById(R.id.txtProjectName);
                CheckBox checkBox = projectView.findViewById(R.id.chkBoxComplete);
                if (checkBox.isChecked()) {
                    selectedProjectNames.add((String) projectName.getText());
                    selectProjectPositions.add(i);
                }
            }
            if (selectedProjectNames.isEmpty()) {
                Toast.makeText(getActivity(), "No project selected...", Toast.LENGTH_SHORT).show();
            } else {
                deleteProjects(adapter, selectedProjectNames, selectProjectPositions);
            }
        });
        return view;
    }

    private void deleteProjects(ProjectRecViewAdapter adapter, ArrayList<String> selectedProjectNames,
                                ArrayList<Integer> selectProjectPositions) {
        String message = "Are you sure you want to delete?\n";
        for (String project : selectedProjectNames) {
            message += project + "\n";
        }

        new AlertDialog.Builder(getContext())
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    for (String project : selectedProjectNames) {
                        DatabaseReference taskDatabase = FirebaseDatabase.getInstance()
                                .getReference()
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        Query query = taskDatabase.child("tasks").orderByChild("projectId").equalTo(project);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot child : snapshot.getChildren()) {
                                    child.getRef().removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    for (Integer projectPosition : selectProjectPositions) {
                        adapter.getRef(projectPosition).removeValue().addOnCompleteListener(project -> {
                            if (project.isSuccessful()) {
                                Toast.makeText(getActivity(), "Deleting projects from list...", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

}