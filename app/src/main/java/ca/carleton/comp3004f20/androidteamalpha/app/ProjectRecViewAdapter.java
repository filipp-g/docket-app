package ca.carleton.comp3004f20.androidteamalpha.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ProjectRecViewAdapter extends FirebaseRecyclerAdapter<Project, ProjectRecViewAdapter.ViewHolder> {
    public ProjectRecViewAdapter(@NonNull FirebaseRecyclerOptions<Project> options) {
        super(options);
    }

    @NonNull
    @Override
    public ProjectRecViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_item_project, parent, false);
        return new ProjectRecViewAdapter.ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ProjectRecViewAdapter.ViewHolder viewHolder, int i, @NonNull Project project) {
        viewHolder.projectComplete.setChecked(false);
        viewHolder.txtProjectName.setText(project.getName());

        DatabaseReference taskDatabase = FirebaseDatabase.getInstance()
                .getReference()
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        Query query = taskDatabase.child("tasks").orderByChild("projectId").equalTo(project.getName());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long numTasks = snapshot.getChildrenCount();
                viewHolder.txtNumberOfTasks.setText(numTasks + (numTasks == 1 ? " task" : " tasks"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        viewHolder.getView().setOnLongClickListener(view -> {
            FragmentActivity activity = (FragmentActivity) view.getContext();
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, TasksFragment.newInstance(
                            this.getItem(viewHolder.getLayoutPosition())))
                    .commit();
            return true;
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private CheckBox projectComplete;
        private TextView txtProjectName, txtNumberOfTasks;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            projectComplete = itemView.findViewById(R.id.chkBoxComplete);
            txtProjectName = itemView.findViewById(R.id.txtProjectName);
            txtNumberOfTasks = itemView.findViewById(R.id.txtNumberOfTasks);
        }

        public View getView() {
            return view;
        }
    }

}
