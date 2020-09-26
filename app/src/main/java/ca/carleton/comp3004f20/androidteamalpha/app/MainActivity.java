package ca.carleton.comp3004f20.androidteamalpha.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

    private FirebaseAuth mAuth;
    private DatabaseReference taskDatabase;
    private DatabaseReference projectDatabase;
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private String user = "michael Balcerzak";
    private List<Task> listOfTasks;
    private List<Project> listOfProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        taskDatabase = FirebaseDatabase.getInstance().getReference().child(user).child("task");
        projectDatabase = FirebaseDatabase.getInstance().getReference().child(user).child("project");
        mAuth = FirebaseAuth.getInstance();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("task_id", 00000003);
        childUpdates.put("task_name", "asdfasd retergsdgjhsdfg");
        childUpdates.put("weight", 0.50);
        childUpdates.put("time_required", 10);

        final EditText emailId = findViewById(R.id.editTextTextEmailAddress);
        final EditText passwordId = findViewById(R.id.editTextTextPassword);
        Button btnSignIn = findViewById(R.id.button);
        btnSignIn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String email = emailId.getText().toString();
                String password = passwordId.getText().toString();

                sign_in(mAuth, "balcerzak.michael@gmail.com", "mX10baz3");
            }
        });

        taskDatabase.push().setValue(childUpdates);
    }

    public void onStart() {
        super.onStart();
    }

    public void sign_up(final FirebaseAuth mAuth, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            System.out.println("email and password is saved");
                        } else {
                            System.out.println("email and password is not saved");
                        }
                    }
                });
    }
    public void sign_in(final FirebaseAuth mAuth, String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            System.out.println("email and password is right");
                        } else {
                            System.out.println("email and password is wrong");
                        }
                    }
                });
    }
}