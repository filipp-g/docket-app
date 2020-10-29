package ca.carleton.comp3004f20.androidteamalpha.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProjectListActivity extends AppCompatActivity {

    private String USERNAME;
    private DatabaseReference taskDatabase;
    private TaskRecViewAdapter adapter;
    private RecyclerView projectsRecView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);

        USERNAME = getIntent().getStringExtra("NAME");
        taskDatabase = FirebaseDatabase.getInstance().getReference().child(USERNAME).child("tasks");

        FirebaseRecyclerOptions<Task> options = new FirebaseRecyclerOptions.Builder<Task>()
                .setQuery(taskDatabase, Task.class)
                .build();

        adapter = new TaskRecViewAdapter(options);

        projectsRecView = findViewById(R.id.projectsRecView);
        projectsRecView.setAdapter(adapter);
        projectsRecView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}