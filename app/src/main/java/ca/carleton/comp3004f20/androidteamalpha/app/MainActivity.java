package ca.carleton.comp3004f20.androidteamalpha.app;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private String userName;
    private String email;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavMethod);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, getProfileFragment()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflator = getMenuInflater();
        inflator.inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_signout:
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    FirebaseAuth.getInstance().signOut();
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new SignInFragment()).commit();
                Toast.makeText(this, "Signing Out", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            initialize();

            Fragment fragment = null;

            switch (item.getItemId()) {
                case R.id.nav_projects:
                    fragment = ProjectsFragment.newInstance(email, userName);
                    break;
                case R.id.nav_overview:
                    fragment = CalendarFragment.newInstance(email, userName);
                    break;
                case R.id.nav_timer:
                    fragment = TimerFragment.newInstance(email, userName);
                    break;
                default:
                    fragment = getProfileFragment();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
            return true;
        }
    };

    private Fragment getProfileFragment() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return SignInFragment.newInstance(email, userName);
        }
        return ProfileFragment.newInstance(email, userName);
    }

    private void getName(DataSnapshot dataSnapshot) {
        email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        for (DataSnapshot user : dataSnapshot.getChildren()) {
            String emailFromDatabase = user.child("email").getValue().toString();
            if (emailFromDatabase.equals(email)) {
                userName = user.child("name").getValue().toString();
            }
        }
    }

    private void initialize() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseDatabase.getInstance().getReference().child("users")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            getName(dataSnapshot);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
        }
    }
}
