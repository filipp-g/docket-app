package ca.carleton.comp3004f20.androidteamalpha.app;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarFragment extends Fragment {
    private static final String TAG = "ViewDatabase";
    private static final String EMAIL = "email";
    private static final String USER = "user";

    private String email;
    private String user;

    private CompactCalendarView compactCalendar;
    private NotificationManagerCompat notificationManager;
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM- yyyy", Locale.getDefault());
    private String userName = "filipp";

    public List<CalenderEvent> calenderListOfEvents = new ArrayList<>();

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public CalendarFragment() {
        // Required empty public constructor
    }

    public static CalendarFragment newInstance(String email, String user) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putString(EMAIL, email);
        args.putString(USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, new SignInFragment()).commit();
            Toast.makeText(getActivity(), "Please sign in...", Toast.LENGTH_SHORT).show();
        } else {
            if (getArguments() != null) {
                email = getArguments().getString(EMAIL);
                user = getArguments().getString(USER);
                userName = user;
            }

            final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            assert actionBar != null;
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setTitle(null);

            compactCalendar = view.findViewById(R.id.compactcalendar_view);
            compactCalendar.setUseThreeLetterAbbreviation(true);

            mAuth = FirebaseAuth.getInstance();

            FirebaseDatabase.getInstance().getReference().child(user).child("tasks")
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

            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user == null) {
                        Log.d(TAG, "log in: " + user.getEmail());
                    } else {
                        Log.d(TAG, "log out: " + user.getEmail());
                    }
                }
            };

            Button addTask = view.findViewById(R.id.btnAddTask);
            addTask.setOnClickListener(v -> getActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, TaskFragment.newInstance(email, user, null))
                    .commit()
            );

            compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
                @Override
                public void onDayClick(Date dateClicked) {
                    Context context = getActivity().getApplicationContext();
                    Toast.makeText(context, dateClicked.toString(), Toast.LENGTH_SHORT).show();
                    System.out.println(dateClicked.getTime());
                }

                @Override
                public void onMonthScroll(Date firstDayOfNewMonth) {
                    actionBar.setTitle(dateFormatMonth.format(firstDayOfNewMonth));
                }
            });

            ImageButton overViewButton = (ImageButton) view.findViewById(R.id.overviewButton);
            overViewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = OverviewFragment.newInstance(email, userName);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
                }
            });
        }
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

            /*if (calenderEventCounter.getEndEvent().getTime() - calenderEventCounter.
                    getStartEvent().getTime().getDay() == numberOfDaysBeforeReminder) {
                if (calenderEventCounter.getTask().getName().toUpperCase().contains("test")) {
                    sendNotification(calenderEventCounter.getTask().getName(), "Test is coming on " + numberOfDaysBeforeReminder, 1, ca.carleton.comp3004f20.androidteamalpha.app.Notification.TEST_CHANNEL, Color.RED);
                }

                if (calenderEventCounter.getTask().getName().toUpperCase().contains("assignment")) {
                    sendNotification(calenderEventCounter.getTask().getName(), "Assignment is coming on " + numberOfDaysBeforeReminder, 1, ca.carleton.comp3004f20.androidteamalpha.app.Notification.ASSIGNMENT_CHANNEL, Color.GREEN);
                }

                if (calenderEventCounter.getTask().getName().toUpperCase().contains("exam")) {
                    sendNotification(calenderEventCounter.getTask().getName(), "Ecture is coming on " + numberOfDaysBeforeReminder, 1, ca.carleton.comp3004f20.androidteamalpha.app.Notification.TEST_CHANNEL, Color.CYAN);
                }
            }*/
        }
    }

    public CalenderEvent getFirstEvent() {
        return calenderListOfEvents.get(0);
    }

    public void sendNotification(String title, String message, int id, String idChannel, int color) {
        Notification notification = new NotificationCompat.Builder(getActivity(), idChannel)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        notificationManager.notify(id, notification);
    }
}