package ca.carleton.comp3004f20.androidteamalpha.app;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roomorama.caldroid.CaldroidFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment {

    private CompactCalendarView compactCalendar;
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM- yyyy", Locale.getDefault());

    public List<CalenderEvent> calenderListOfEvents = new ArrayList<>();

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle(null);

        compactCalendar = view.findViewById(R.id.compactcalendar_view);

        FirebaseDatabase.getInstance()
                .getReference()
                .child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
                .child("tasks")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        getEvents(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        FloatingActionButton addTask = view.findViewById(R.id.btnAddTask);
        addTask.setOnClickListener(v -> getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, TaskFragment.newInstance(null))
                .commit()
        );

        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Context context = getActivity().getApplicationContext();
                FragmentActivity activity = (FragmentActivity) view.getContext();
                for (int counter = 0; counter < calenderListOfEvents.size(); counter++) {
                    if (calenderListOfEvents.get(counter).getEndEventAslong().getTime() == dateClicked.getTime()) {
                        activity.getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, TaskFragment.newInstance(
                                        calenderListOfEvents.get(counter).getTask()))
                                .commit();
                        Toast.makeText(context, "          " + calenderListOfEvents.get(counter).getTask().getName() + "\nDue Time: " +
                                calenderListOfEvents.get(counter).getTask().getDueTime(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, TaskFragment.newInstance(new Task(dateClicked)))
                        .commit();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                actionBar.setTitle(dateFormatMonth.format(firstDayOfNewMonth));
            }
        });

        ImageButton overViewButton = view.findViewById(R.id.overviewButton);
        overViewButton.setOnClickListener(v ->
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new OverviewFragment())
                        .commit()
        );
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getEvents(DataSnapshot snapshot) {
        calenderListOfEvents.clear();
        for (DataSnapshot task : snapshot.getChildren()) {
            String completeString = task.child("complete").getValue().toString();
            boolean complete = Boolean.parseBoolean(completeString);
            String due_time = task.child("dueTime").getValue().toString();
            String due_date = task.child("dueDate").getValue().toString();
            String task_id = task.child("id").getValue().toString();
            String task_name = task.child("name").getValue().toString();
            String projectId = task.child("projectId").getValue().toString();
            int time_required = Integer.parseInt(task.child("timeRequired").getValue().toString());
            int time_spent = Integer.parseInt(task.child("timeSpent").getValue().toString());
            int weight = Integer.parseInt(task.child("weight").getValue().toString());
            try {
                Task taskObject = new Task(task_id, task_name, projectId, due_date, due_time,
                        weight, time_required, time_spent, complete);
                CalenderEvent calenderEvent = new CalenderEvent();
                calenderEvent.setTask(taskObject);
                calenderEvent.setEndEvent(taskObject.getDueDateAsSimpleDate());
                calenderListOfEvents.add(calenderEvent);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        for (int counter = 0; counter < calenderListOfEvents.size(); counter++) {
            CalenderEvent calenderEventCounter = calenderListOfEvents.get(counter);
            Event endEvent = new Event(Color.GREEN,
                    calenderEventCounter.getEndEvent(),
                    calenderEventCounter.returnName() + " end date");
            compactCalendar.addEvent(endEvent);
        }
    }
}