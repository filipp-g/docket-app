package ca.carleton.comp3004f20.androidteamalpha.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Button calenderButton = (Button) findViewById(R.id.calender);
        calenderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalenderActivity();
            }
        });

        Button addTaskButton = findViewById(R.id.btnAddTask);
        addTaskButton.setOnClickListener((x) -> addTask());
    }

    public void openCalenderActivity() {
        Intent intent = new Intent(this, CalenderActivity.class);
        startActivity(intent);
    }

    private void addTask() {
        Intent intent = new Intent(this, TaskActivity.class);
        startActivity(intent);
    }
}