package ca.carleton.comp3004f20.androidteamalpha.app;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.widget.Button;

public class Notification extends Application {
    public static final String TEST_CHANNEL = "testChannel";
    public static final String ASSIGNMENT_CHANNEL = "assignmentChannel";
    public static final String LECTURE_CHANNEL = "lectureChannel";
    public static final String EXAM_CHANNEL = "examChannel";
    public static final String STUDY_CHANNEL = "studyChannel";
    public static final String DO_CHANNEL = "doChannel";

    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel testChannel = new NotificationChannel(
                    TEST_CHANNEL,
                    "test channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            testChannel.setDescription("test coming up");

            NotificationChannel assignmentChannel = new NotificationChannel(
                    ASSIGNMENT_CHANNEL,
                    "assignment channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            assignmentChannel.setDescription("assignment due coming up");

            NotificationChannel lectureChannel = new NotificationChannel(
                    LECTURE_CHANNEL,
                    "lecture channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            lectureChannel.setDescription("go to lecture");

            NotificationChannel examChannel = new NotificationChannel(
                    EXAM_CHANNEL,
                    "test channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            examChannel.setDescription("exam coming up");

            NotificationChannel studyChannel = new NotificationChannel(
                    STUDY_CHANNEL,
                    "test channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            studyChannel.setDescription("test coming up");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(testChannel);
            manager.createNotificationChannel(assignmentChannel);
            manager.createNotificationChannel(lectureChannel);
            manager.createNotificationChannel(examChannel);
            manager.createNotificationChannel(studyChannel);
        }
    }

}
