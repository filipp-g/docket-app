package ca.carleton.comp3004f20.androidteamalpha.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class TaskRecViewAdapter extends FirebaseRecyclerAdapter<Task, TaskRecViewAdapter.ViewHolder> {

    public TaskRecViewAdapter(@NonNull FirebaseRecyclerOptions<Task> options) {
        super(options);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull Task task) {
        viewHolder.taskComplete.setChecked(task.isComplete());
        viewHolder.txtTaskName.setText(task.getName());
        viewHolder.txtDueDate.setText(task.getDueDate());
//        viewHolder.txtTimeLeft.setText(task.getTimeSpent());     //TODO for some reason this throws exception
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox taskComplete;
        private TextView txtTaskName, txtDueDate, txtTimeLeft;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            taskComplete = itemView.findViewById(R.id.chkBoxComplete);
            txtTaskName = itemView.findViewById(R.id.txtTaskName);
            txtDueDate = itemView.findViewById(R.id.txtDueDate);
            txtTimeLeft = itemView.findViewById(R.id.txtTimeLeft);
        }
    }
}
