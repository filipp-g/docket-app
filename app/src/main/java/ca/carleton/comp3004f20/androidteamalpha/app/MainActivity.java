package ca.carleton.comp3004f20.androidteamalpha.app;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_TEXT = "ca.carleton.comp3004f20.androidteamalpha.app.EXTRA_TEXT";
    String name;

    private Button button;

    private FirebaseAuth mAuth;

    CalenderActivity calenderActivity;

    private String user = "michael Balcerzak";
    private List<Task> listOfTasks;
    private List<Project> listOfProject;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button signUp = (Button) findViewById(R.id.SignUp);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivitySignUp();
            }
        });

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();

        final EditText emailId = findViewById(R.id.editTextTextEmailAddress);
        final EditText passwordId = findViewById(R.id.editTextTextPassword);
        Button btnSignIn = findViewById(R.id.signIn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String email = emailId.getText().toString();
                String password = passwordId.getText().toString();

                sign_in(mAuth, email, password);
            }
        });
    }

    public void openActivitySignUp() {
        Intent intent = new Intent(this, ActivitySignUp.class);
        startActivity(intent);
    }

    public void onStart() {
        super.onStart();
    }

    public void sign_in(final FirebaseAuth mAuth, final String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            openMainMenu(email);
                        } else {
                            System.out.println("email and password is wrong");
                        }
                    }
                });
    }

    public void openMainMenu(final String email) {
        Intent intent = new Intent(this, MainMenu.class);
        intent.putExtra("EMAIL", email);
        startActivity(intent);
    }
}