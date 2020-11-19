package ca.carleton.comp3004f20.androidteamalpha.app;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavMethod);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, getInitialFragment())
                .commit();

        createNotificationChannel();

        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0,
                new Intent(this, NotificationAlarmReceiver.class), 0);

        AlarmManager alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        // this triggers the alarm on app launch
        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0, alarmIntent);

        //TODO scheduling notifications works but with ~5min delay, so not great for demos
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.set(Calendar.HOUR_OF_DAY, 19);
//        calendar.set(Calendar.MINUTE, 41);
//        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                AlarmManager.INTERVAL_DAY, alarmIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_signout) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                FirebaseAuth.getInstance().signOut();
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new SignInFragment()).commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod = item -> {
        Fragment fragment;

        switch (item.getItemId()) {
            case R.id.nav_projects:
                fragment = new ProjectsFragment();
                break;
            case R.id.nav_overview:
                fragment = new CalendarFragment();
                break;
            case R.id.nav_timer:
                fragment = new TimerFragment();
                break;
            default:
                fragment = new ProfileFragment();
                break;
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        return true;
    };

    private Fragment getInitialFragment() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return new SignInFragment();
        }
        return new ProfileFragment();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "DEFAULT_CHANNEL_ID", "default",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("The default notification channel");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    protected void showBottomNav() {
        bottomNavigationView.setVisibility(View.VISIBLE);
    }

    protected void hideBottomNav() {
        bottomNavigationView.setVisibility(View.GONE);
    }

}
