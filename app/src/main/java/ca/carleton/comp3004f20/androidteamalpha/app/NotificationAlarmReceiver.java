package ca.carleton.comp3004f20.androidteamalpha.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.Calendar;

public class NotificationAlarmReceiver extends BroadcastReceiver {
    private final String[] shortMonths = new DateFormatSymbols().getShortMonths();

    @Override
    public void onReceive(Context context, Intent intent) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
                .child("tasks")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Calendar calendar = Calendar.getInstance();
                        for (DataSnapshot task : snapshot.getChildren()) {
                            calendar.setTimeInMillis(System.currentTimeMillis());
                            calendar = setDueDate(calendar, task.child("dueDate").getValue().toString());
                            calendar = setDueTime(calendar, task.child("dueTime").getValue().toString());

                            if (taskIsDueSoon(calendar)) {
                                createNotification(task);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    private void createNotification(DataSnapshot task) {
        String name = task.child("name").getValue().toString();
        String dueDate = task.child("dueDate").getValue().toString();
        String dueTime = task.child("dueTime").getValue().toString();
        System.out.println(name + " is due on " + dueDate + " at " + dueTime);
    }

    // task is due in less than 24 hours
    private boolean taskIsDueSoon(Calendar calendar) {
        float now = (float) Calendar.getInstance().getTimeInMillis() / 3600000;
        float due = (float) calendar.getTimeInMillis() / 3600000;
        return due - now > 0 && due - now < 24;
    }

    private Calendar setDueDate(Calendar calendar, String dueDate) {
        if (!dueDate.isEmpty()) {
            int year = Integer.parseInt(dueDate.substring(7));
            int month = Arrays.asList(shortMonths).indexOf(dueDate.substring(0, 3));
            int day = Integer.parseInt(dueDate.substring(4, 6));
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
        }
        return calendar;
    }

    private Calendar setDueTime(Calendar calendar, String dueTime) {
        if (!dueTime.isEmpty()) {
            int hour = Integer.parseInt(dueTime.substring(0, dueTime.indexOf(":")));
            int minute = Integer.parseInt(dueTime.substring(dueTime.indexOf(":") + 1, dueTime.indexOf(":") + 3));
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
        }
        return calendar;
    }
}
