package ca.carleton.comp3004f20.androidteamalpha.app;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.ceil;

public class TimerFragment extends Fragment implements AdapterView.OnItemClickListener {

    // TIMER VARIABLES
    private FloatingActionButton start;
    private FloatingActionButton stop;
    private FloatingActionButton pause;
    private FrameLayout mainView;
    private Chronometer chronometer;
    private Spinner taskSpinner;
    private boolean running = false;
    private long pauseOffset = 0;
    private List<String> listOfTasks = new ArrayList<>();
    private List<String> tasksID = new ArrayList<>();
    private List<Integer> tasksTimeSpent = new ArrayList<>();
    private List<Integer> tasksTimeRequired = new ArrayList<>();
    private int taskLength = 0;
    private DatabaseReference taskDatabase;

    // FRAGMENT SETUP FUNCTIONS
    public TimerFragment() {
        // Required empty public constructor
    }


    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timer, container, false);

        // Get component ids
        start = view.findViewById(R.id.fab_play);
        stop = view.findViewById(R.id.fab_stop);
        pause = view.findViewById(R.id.fab_pause);

        mainView = view.findViewById(R.id.main);
        chronometer = view.findViewById(R.id.chronometer);

        taskDatabase = FirebaseDatabase.getInstance()
                .getReference()
                .child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
                .child("tasks");

        start.setOnClickListener(v -> {
            if (!running) {
                chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                chronometer.start();
                running = true;
            } else {
                pause();
            }
        });

        pause.setOnClickListener(v -> {
            if (running) {
                pause();
            }
        });

        stop.setOnClickListener(v -> {
            long millis = SystemClock.elapsedRealtime() - chronometer.getBase();
            int h = (int)(millis/3600000);
            int m = (int)(millis - h*3600000)/60000;
            int s= (int)(millis - h*3600000- m*60000)/1000 ;

            chronometer.setBase(SystemClock.elapsedRealtime());
            pauseOffset = 0;

            for (int counter = 0; counter < taskLength; counter++) {
                String databaseId = listOfTasks.get(counter);
                String localId = tasksID.get(counter);
                int localTimeSpent = Integer.parseInt(tasksTimeSpent.get(counter).toString());
                int localTimeRequired = Integer.parseInt(tasksTimeRequired.get(counter).toString());
                if (taskSpinner.getSelectedItem().toString().contains(databaseId)) {
                    int totalNumberOfHoursSpends = h + localTimeSpent;
                    taskDatabase.child(localId).child("timeSpent").setValue(totalNumberOfHoursSpends);

                    if (localTimeSpent >= localTimeRequired) {
                        taskDatabase.child(localId).child("complete").setValue(true);
                        Toast.makeText(getContext(),"time spend: " + h + ":" + m + ":" + s +
                                        "\ntotal hours spend: " + totalNumberOfHoursSpends +
                                        "\n\n\nThe task is now complete",
                                Toast.LENGTH_LONG).show();
                    } else {
                        int hoursLeft = localTimeRequired - localTimeSpent;
                        Toast.makeText(getContext(),"time spend: " + h + ":" + m + ":" + s +
                                "\ntotal hours spend: " + totalNumberOfHoursSpends + "\n\n\n" +
                                hoursLeft + " hours left on the task", Toast.LENGTH_LONG).show();
                    }

                    break;
                }
            }
        });

        FirebaseDatabase.getInstance()
                .getReference()
                .child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
                .child("tasks")
                .orderByChild("dueDate")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        populateTasks(view, dataSnapshot);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        return view;
    }

    private void pause() {
        chronometer.stop();
        pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
        running = false;
    }

    private void populateTasks(View view, DataSnapshot dataSnapshot) {
        for (DataSnapshot task : dataSnapshot.getChildren()) {
            String name = task.child("name").getValue().toString();
            String id = task.child("id").getValue().toString();
            int timeSpent = Integer.parseInt(task.child("timeSpent").getValue().toString());
            int timeRequired = Integer.parseInt(task.child("timeRequired").getValue().toString());
            listOfTasks.add(name);
            tasksID.add(id);
            tasksTimeSpent.add(timeSpent);
            tasksTimeRequired.add(timeRequired);
            taskLength++;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getActivity(), R.layout.projects_spinner_item, listOfTasks);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        taskSpinner = view.findViewById(R.id.task_Spinner);
        taskSpinner.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
    }
}