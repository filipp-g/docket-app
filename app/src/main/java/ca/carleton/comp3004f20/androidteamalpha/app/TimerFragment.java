package ca.carleton.comp3004f20.androidteamalpha.app;

import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TimerFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String TASK = "taskObj";

    // TIMER VARIABLES
    private FloatingActionButton start;
    private FloatingActionButton stop;
    private FloatingActionButton pause;
    private Chronometer chronometer;
    private Chronometer chronometerSeconds;
    private Spinner taskSpinner;
    private boolean running = false;
    private long pauseOffsetSeconds = 0;
    private long pauseOffsetMinites = 0;
    private List<String> listOfTasks = new ArrayList<>();
    private List<String> tasksID = new ArrayList<>();
    private List<Integer> tasksTimeSpent = new ArrayList<>();
    private List<Integer> tasksTimeRequired = new ArrayList<>();
    private List<Integer> tasksMinutes = new ArrayList<>();
    private int taskLength = 0;
    private DatabaseReference taskDatabase;

    private static final int minInLong = 3600000;
    private static final int hourInLong = 60000;

    // FRAGMENT SETUP FUNCTIONS
    public TimerFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timer, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Timer");

        // Get component ids
        start = view.findViewById(R.id.fab_play);
        stop = view.findViewById(R.id.fab_stop);
        pause = view.findViewById(R.id.fab_pause);

        chronometer = view.findViewById(R.id.chronometer);
        chronometerSeconds = view.findViewById(R.id.chronometerSeconds);

        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long time = SystemClock.elapsedRealtime() - chronometer.getBase();
                int hours = (int) (time / minInLong);
                int minutes = (int) (time - hours * minInLong) / hourInLong;
                chronometer.setText(String.format("%02d:%02d", hours, minutes));
            }
        });

        chronometerSeconds.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long time = SystemClock.elapsedRealtime() - chronometer.getBase();
                int hours = (int) (time / minInLong);
                int minutes = (int) (time - hours * minInLong) / hourInLong;
                int seconds = (int) (time - hours * minInLong - minutes * hourInLong) / 1000;
                chronometer.setText(String.format("%02d", seconds));
            }
        });

        taskDatabase = FirebaseDatabase.getInstance()
                .getReference()
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("tasks");

        start.setOnClickListener(v -> {
            if (!running) {
                chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffsetMinites);
                chronometer.start();
                chronometerSeconds.setBase(SystemClock.elapsedRealtime() - pauseOffsetSeconds);
                chronometerSeconds.start();
                running = true;
            } else {
                pauseOffsetMinites = pause(chronometer);
                pauseOffsetSeconds = pause(chronometerSeconds);
            }
        });

        pause.setOnClickListener(v -> {
            if (running) {
                pauseOffsetMinites = pause(chronometer);
                pauseOffsetSeconds = pause(chronometerSeconds);
            }
        });

        stop.setOnClickListener(v -> {
            chronometerSeconds.stop();
            chronometer.stop();
            long millis = SystemClock.elapsedRealtime() - chronometer.getBase();
            int h = (int) (millis / minInLong);
            int m = (int) (millis - h * minInLong) / hourInLong;
            int s = (int) (millis - h * minInLong - m * hourInLong) / 1000;

            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometerSeconds.setBase(SystemClock.elapsedRealtime());
            pauseOffsetSeconds = 0;
            pauseOffsetMinites = 0;

            for (int counter = 0; counter < taskLength; counter++) {
                String databaseId = listOfTasks.get(counter);
                if (taskSpinner.getSelectedItem().toString().contains(databaseId)) {
                    String localId = tasksID.get(counter);
                    int localTimeSpent = Integer.parseInt(tasksTimeSpent.get(counter).toString());
                    int localTimeRequired = Integer.parseInt(tasksTimeRequired.get(counter).toString());
                    localTimeSpent += m;

                    if (h > 0) {
                        localTimeSpent = +h * 60;
                    }


                    taskDatabase.child(localId).child("timeSpent").setValue(localTimeSpent);

                    if (localTimeSpent >= localTimeRequired) {
                        taskDatabase.child(localId).child("complete").setValue(true);
                        Toast.makeText(getContext(), "time spend: " + h + ":" + m + ":" + s +
                                        "\ntotal time spend: " + (h * 60 + m) +
                                        "\n\n\nThe task is now complete",
                                Toast.LENGTH_LONG).show();
                    } else {
                        int hoursLeft = localTimeRequired - localTimeSpent;
                        Toast.makeText(getContext(), "time spend: " + h + ":" + m + ":" + s +
                                "\ntotal time spend: " + (h * 60 + m) + "\n\n\n" + hoursLeft +
                                " minites left on the task", Toast.LENGTH_LONG).show();
                    }

                    break;
                }
            }
        });

        FirebaseDatabase.getInstance()
                .getReference()
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("tasks")
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

    private long pause(Chronometer chronometer) {
        chronometer.stop();
        long pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
        running = false;

        return pauseOffset;
    }

    private void populateTasks(View view, DataSnapshot dataSnapshot) {
        for (DataSnapshot task : dataSnapshot.getChildren()) {
            String name = task.child("name").getValue().toString();
            String id = task.child("id").getValue().toString();
            int timeSpent = Integer.parseInt(task.child("timeSpent").getValue().toString());
            int timeRequired = Integer.parseInt(task.child("timeRequired").getValue().toString());
            int timeSpentHours = timeSpent / 60;
            listOfTasks.add(name);
            tasksID.add(id);
            tasksTimeSpent.add(timeSpent);
            tasksTimeRequired.add(timeRequired);
            tasksMinutes.add(timeSpent - timeSpentHours * 60);
            taskLength++;
        }

        System.out.println(listOfTasks);

        if (getActivity() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    getActivity(), R.layout.projects_spinner_item, listOfTasks);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            taskSpinner = view.findViewById(R.id.task_Spinner);
            taskSpinner.setAdapter(adapter);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
    }
}