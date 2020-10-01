package ca.carleton.comp3004f20.androidteamalpha.app;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalenderActivity extends AppCompatActivity {

    CompactCalendarView compactCalendar;
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM- yyyy", Locale.getDefault());

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
                    calenderEventCounter.returnStartEvent(), calenderEventCounter.returnName() + " start date");
            Event endEvent = new Event(calenderEventCounter.returnColour(),
                    calenderEventCounter.returnEndEvent(), calenderEventCounter.returnName() + " end date");
            compactCalendar.addEvent(startEvent);
            compactCalendar.addEvent(endEvent);
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
    }

    public void openMainActivity() {
        Intent intent = new Intent(this, MainMenu.class);
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


}