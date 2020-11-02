package ca.carleton.comp3004f20.androidteamalpha.app;

import android.graphics.Color;
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

public class TaskRecViewAdapter extends FirebaseRecyclerAdapter<Task, TaskRecViewAdapter.ViewHolder> {
    private String user, email;

    public TaskRecViewAdapter(@NonNull FirebaseRecyclerOptions<Task> options, String email, String user) {
        super(options);
        this.email = email;
        this.user = user;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_item_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull Task task) {
        viewHolder.taskComplete.setChecked(task.isComplete());
        viewHolder.txtTaskName.setText(task.getName());
        viewHolder.txtDueDate.setText("Due: " + task.getDueDate());

        int timeDiff = task.getTimeSpent() - task.getTimeRequired();
        if (timeDiff < 0) {
            viewHolder.txtTimeLeft.setText(timeDiff + "h");
            viewHolder.txtTimeLeft.setTextColor(Color.RED);
        }

        viewHolder.getView().setOnLongClickListener(view -> {
            FragmentActivity activity = (FragmentActivity) view.getContext();
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, TaskFragment.newInstance(
                            email, user, this.getItem(viewHolder.getLayoutPosition())))
                    .commit();
            return true;
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private CheckBox taskComplete;
        private TextView txtTaskName, txtDueDate, txtTimeLeft;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            taskComplete = itemView.findViewById(R.id.chkBoxComplete);
            txtTaskName = itemView.findViewById(R.id.txtTaskName);
            txtDueDate = itemView.findViewById(R.id.txtDueDate);
            txtTimeLeft = itemView.findViewById(R.id.txtTimeLeft);
        }

        public View getView() {
            return view;
        }
    }
}
