package ca.carleton.comp3004f20.androidteamalpha.app;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.EventLog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import ca.carleton.comp3004f20.androidteamalpha.app.Notification.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalenderActivity extends AppCompatActivity {

    CompactCalendarView compactCalendar;
    private NotificationManagerCompat notificationManager;
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM- yyyy", Locale.getDefault());
    private int numberOfdaysBeforeReminder = 3;

    public List<CalenderEvent> calenderListOfEvents;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle(null);

        compactCalendar = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        compactCalendar.setUseThreeLetterAbbreviation(true);

        calenderListOfEvents = new ArrayList<>();

        for (int counter = 0; counter < calenderListOfEvents.size(); counter++) {
            CalenderEvent calenderEventCounter = calenderListOfEvents.get(counter);
            Event startEvent = new Event(calenderEventCounter.returnColour(),
                    calenderEventCounter.returnStartEvent().getTimeInMillis(),
                    calenderEventCounter.returnName() + " start date");
            Event endEvent = new Event(calenderEventCounter.returnColour(),
                    calenderEventCounter.returnEndEvent().getTimeInMillis(),
                    calenderEventCounter.returnName() + " end date");
            compactCalendar.addEvent(startEvent);
            compactCalendar.addEvent(endEvent);

            if (calenderEventCounter.returnEndEvent().getTime().getDay() - calenderEventCounter.returnStartEvent().getTime().getDay() == numberOfdaysBeforeReminder) {
                if (calenderEventCounter.getTask().getName().toUpperCase().contains("test")) {
                    sendNotification(calenderEventCounter.getTask().getName(), "Test is coming on " + numberOfdaysBeforeReminder, 1, ca.carleton.comp3004f20.androidteamalpha.app.Notification.TEST_CHANNEL, Color.RED);
                }

                if (calenderEventCounter.getTask().getName().toUpperCase().contains("assignment")) {
                    sendNotification(calenderEventCounter.getTask().getName(), "Assignment is coming on " + numberOfdaysBeforeReminder, 1, ca.carleton.comp3004f20.androidteamalpha.app.Notification.ASSIGNMENT_CHANNEL, Color.GREEN);
                }

                if (calenderEventCounter.getTask().getName().toUpperCase().contains("exam")) {
                    sendNotification(calenderEventCounter.getTask().getName(), "Ecture is coming on " + numberOfdaysBeforeReminder, 1, ca.carleton.comp3004f20.androidteamalpha.app.Notification.TEST_CHANNEL, Color.CYAN);
                }
            }
        }


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

    public void addEvent(int startYear, int startMonth, int startDay,
                    int endYear, int endMonth, int endDay) {
        CalenderEvent calenderEvent = new CalenderEvent();
        calenderEvent.setStartEvent(startYear, startMonth, startDay);
        calenderEvent.setEndEvent(endYear, endMonth, endDay);

        calenderListOfEvents.add(calenderEvent);
    }

    public CalenderEvent getEvent() {
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