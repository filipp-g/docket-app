package ca.carleton.comp3004f20.androidteamalpha.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

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
        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
            }
        });

        Button newButton;

        HorizontalScrollView scrollView = (HorizontalScrollView) findViewById(R.id.scrollable);

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout topLayout = new LinearLayout(this);
        topLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout bottomLayout = new LinearLayout(this);
        bottomLayout.setOrientation(LinearLayout.HORIZONTAL);

        for (int counter = 0; counter < 20; counter++) {
            newButton = new Button(this);
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

        mainLayout.addView(topLayout );
        mainLayout.addView(bottomLayout);

        scrollView.addView(mainLayout);

    }

    public void openCalenderActivity() {
        Intent intent = new Intent(this, CalenderActivity.class);
        startActivity(intent);
    }

    public void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


}