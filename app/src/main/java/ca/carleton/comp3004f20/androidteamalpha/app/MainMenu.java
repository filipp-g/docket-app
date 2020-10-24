package ca.carleton.comp3004f20.androidteamalpha.app;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.Iterator;
import java.util.Objects;

public class MainMenu extends AppCompatActivity {
    private static final String TAG = "ViewDatabase";
    int counter = 0;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String name;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

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

        Button calenderButton = findViewById(R.id.calender);
        calenderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalenderActivity();
            }
        });
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
            }
        });

        HorizontalScrollView scrollView = findViewById(R.id.scrollable);

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        final LinearLayout topLayout = new LinearLayout(this);
        topLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout bottomLayout = new LinearLayout(this);
        bottomLayout.setOrientation(LinearLayout.HORIZONTAL);

        for (counter = 0; counter < 20; counter++) {
            Button newButton = new Button(this);
            newButton.setId(counter);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(370,370);
            params.leftMargin = 40;
            params.topMargin = 40;

            newButton.setLayoutParams(params);
            newButton.setText(String.valueOf(counter));

            if (counter % 2 == 0) {
                topLayout.addView(newButton);
            } else {
                bottomLayout.addView(newButton);
            }
        }

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

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

        mainLayout.addView(topLayout );
        mainLayout.addView(bottomLayout);

        scrollView.addView(mainLayout);
    }

    private void getName(DataSnapshot dataSnapshot) {
        Intent intent = getIntent();
        String email = intent.getStringExtra("EMAIL");
        System.out.println(email);

        for (DataSnapshot user : dataSnapshot.getChildren()) {
            String emailFromDatabase = user.child("email").getValue().toString();
            if (emailFromDatabase.equals(email)) {
                name = user.child("name").getValue().toString();
            }
        }
    }

    public void openCalenderActivity() {
        Intent intent = new Intent(this, CalenderActivity.class);
        intent.putExtra("NAME", name);
        startActivity(intent);
    }

    public void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


}