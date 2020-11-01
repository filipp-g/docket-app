package ca.carleton.comp3004f20.androidteamalpha.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ProjectsFragment extends Fragment {
    private static final String EMAIL = "email";
    private static final String USER = "user";

    private String user;
    private String email;

    public ProjectsFragment() {
        // Required empty public constructor
    }

    public static ProjectsFragment newInstance(String email, String user) {
        ProjectsFragment fragment = new ProjectsFragment();
        Bundle args = new Bundle();
        args.putString(EMAIL, email);
        args.putString(USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_projects, container, false);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            getActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, new SignInFragment())
                    .commit();
            Toast.makeText(getActivity(), "Please sign in...", Toast.LENGTH_SHORT).show();
        } else {
            if (getArguments() != null) {
                email = getArguments().getString(EMAIL);
                user = getArguments().getString(USER);
            }

            DatabaseReference taskDatabase = FirebaseDatabase.getInstance().getReference().child(user);
            Query query = taskDatabase.child("tasks");

            FirebaseRecyclerOptions<Task> options = new FirebaseRecyclerOptions.Builder<Task>()
                    .setQuery(query, Task.class)
                    .setLifecycleOwner(this)
                    .build();

            TaskRecViewAdapter adapter = new TaskRecViewAdapter(options);

            RecyclerView projectsRecView = view.findViewById(R.id.projectsRecView);
            projectsRecView.setAdapter(adapter);
            projectsRecView.setLayoutManager(new LinearLayoutManager(getContext()));

            Button addTask = view.findViewById(R.id.btnAddTask);
            addTask.setOnClickListener(v -> getActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, TaskFragment.newInstance(email, user))
                    .commit()
            );
        }
        return view;
    }

}