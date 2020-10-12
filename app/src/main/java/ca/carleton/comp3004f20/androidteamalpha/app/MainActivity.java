package ca.carleton.comp3004f20.androidteamalpha.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button button;

    private FirebaseAuth mAuth;
    private DatabaseReference taskDatabase;
    private DatabaseReference projectDatabase;
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    CalenderActivity calenderActivity;

    private String user = "michael Balcerzak";
    private List<Task> listOfTasks;
    private List<Project> listOfProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button signUp = (Button) findViewById(R.id.SignUp);
        signUp.setOnClickListener(v -> openActivitySignUp());

        taskDatabase = FirebaseDatabase.getInstance().getReference().child(user).child("task");
        projectDatabase = FirebaseDatabase.getInstance().getReference().child(user).child("project");
        mAuth = FirebaseAuth.getInstance();

        final EditText emailId = findViewById(R.id.editTextTextEmailAddress);
        final EditText passwordId = findViewById(R.id.editTextTextPassword);
        Button btnSignIn = findViewById(R.id.signIn);
        btnSignIn.setOnClickListener(v -> {
            String email = emailId.getText().toString();
            String password = passwordId.getText().toString();

            sign_in(mAuth, email, password);
        });

    }

    public void openActivitySignUp() {
        Intent intent = new Intent(this, ActivitySignUp.class);
        startActivity(intent);
    }

    public void onStart() {
        super.onStart();
    }

    public void sign_in(final FirebaseAuth mAuth, String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        openMainMenu();
                    } else {
                        System.out.println("email and password is wrong");
                    }
                });
    }

    public void openMainMenu() {
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
    }
}