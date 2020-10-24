package ca.carleton.comp3004f20.androidteamalpha.app;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.DocumentsContract;
import android.util.EventLog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ca.carleton.comp3004f20.androidteamalpha.app.Notification.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalenderActivity extends AppCompatActivity {
    private static final String TAG = "ViewDatabase";

    private CompactCalendarView compactCalendar;
    private NotificationManagerCompat notificationManager;
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM- yyyy", Locale.getDefault());
    private String userName = "filipp";

    public List<CalenderEvent> calenderListOfEvents = new ArrayList<>();

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        Intent intent = getIntent();
        userName = intent.getStringExtra("NAME");
        System.out.println(userName);

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle(null);

        compactCalendar = findViewById(R.id.compactcalendar_view);
        compactCalendar.setUseThreeLetterAbbreviation(true);

        mAuth = FirebaseAuth.getInstance();

        FirebaseDatabase.getInstance().getReference().child(userName).child("tasks")
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

        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Context context = getApplicationContext();
                Toast.makeText(context, dateClicked.toString(), Toast.LENGTH_SHORT).show();
                System.out.println(dateClicked.getTime());
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                actionBar.setTitle(dateFormatMonth.format(firstDayOfNewMonth));
            }
        });

        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
            }
        });
        Button chartButton = (Button) findViewById(R.id.chartButton);
        chartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChartActivity();
            }
        });
    }

    public void openMainActivity() {
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
    }

    public void openChartActivity() {
        Intent intent = new Intent(this, ChartActivity.class);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getEvents(DataSnapshot snapshot) {

        calenderListOfEvents.clear();
        for (DataSnapshot task : snapshot.getChildren()) {
            String completeString = task.child("complete").getValue().toString();
            boolean complete = Boolean.parseBoolean(completeString);
            String due_time  = task.child("due_Time").getValue().toString();
            String due_date = task.child("due_date").getValue().toString();
            String task_id = task.child("task_id").getValue().toString();
            String task_name  = task.child("task_name").getValue().toString();
            int time_required  = Integer.parseInt(task.child("time_required").getValue().toString());
            int time_spent = Integer.parseInt(task.child("time_spent").getValue().toString());
            float weight  = Integer.parseInt(task.child("weight").getValue().toString());
            try {
                Task taskObject = new Task(complete, due_date, due_time, task_id, task_name, time_required, time_spent, weight);
                CalenderEvent calenderEvent = new CalenderEvent();
                calenderEvent.setTask(taskObject);
                calenderEvent.setEndEvent(taskObject.getDue_Date());
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
        Notification notification = new NotificationCompat.Builder(this, idChannel)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        notificationManager.notify(id, notification);
    }
}