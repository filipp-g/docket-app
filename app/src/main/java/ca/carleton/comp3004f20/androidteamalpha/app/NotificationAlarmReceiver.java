package ca.carleton.comp3004f20.androidteamalpha.app;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

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
        PendingIntent projectsIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, ProjectsFragment.class), PendingIntent.FLAG_UPDATE_CURRENT);

        try {
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
                                createNotification(context, task, projectsIntent);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
        } catch (Exception e) {

        }
    }

    private void createNotification(Context context, DataSnapshot task, PendingIntent intent) {
        String name = task.child("name").getValue().toString();
        String dueDate = task.child("dueDate").getValue().toString();
        String dueTime = task.child("dueTime").getValue().toString();
        NotificationCompat.Builder builder = new NotificationCompat
                .Builder(context, "DEFAULT_CHANNEL_ID")
                .setSmallIcon(R.drawable.ic_projects)
                .setContentTitle(name + " is due soon!")
                .setContentText("Due: " + dueDate + " at " + dueTime)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(intent)
                .setAutoCancel(true);
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(0, builder.build());
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
