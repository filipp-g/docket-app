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

import java.util.Calendar;

public class NotificationAlarmReceiver extends BroadcastReceiver {
    private final int MILLIS_IN_HOUR = 3600000;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return;
        }

        PendingIntent projectsIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, ProjectsFragment.class), PendingIntent.FLAG_UPDATE_CURRENT);

        FirebaseDatabase.getInstance()
                .getReference()
                .child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
                .child("tasks")
                .addListenerForSingleValueEvent(new ValueEventListener() {
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
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void createNotification(Context context, DataSnapshot task, PendingIntent intent) {
        String name = task.child("name").getValue().toString();
        String dueDateString = Utilities.formatReadableDate(task.child("dueDate").getValue().toString());
        String dueTime = task.child("dueTime").getValue().toString();
        if (!dueTime.isEmpty()) {
            dueDateString += " at " + dueTime;
        }
        NotificationCompat.Builder builder = new NotificationCompat
                .Builder(context, "DEFAULT_CHANNEL_ID")
                .setSmallIcon(R.drawable.ic_projects)
                .setContentTitle(name + " is due soon!")
                .setContentText("Due: " + dueDateString)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(intent)
                .setAutoCancel(true);
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(0, builder.build());
    }

    // task is due in less than 24 hours
    private boolean taskIsDueSoon(Calendar calendar) {
        float now = (float) Calendar.getInstance().getTimeInMillis() / MILLIS_IN_HOUR;
        float due = (float) calendar.getTimeInMillis() / MILLIS_IN_HOUR;
        return due - now > 0 && due - now < 24;
    }

    private Calendar setDueDate(Calendar calendar, String dueDate) {
        if (!dueDate.isEmpty()) {
            calendar.set(Calendar.YEAR, Integer.parseInt(dueDate.substring(0, 4)));
            calendar.set(Calendar.MONTH, Integer.parseInt(dueDate.substring(5, 7)) - 1);
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dueDate.substring(8, 10)));
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
