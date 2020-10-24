package ca.carleton.comp3004f20.androidteamalpha.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ActivitySignUp extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        final EditText emailId = findViewById(R.id.editTextTextEmailAddress);
        final EditText passwordId = findViewById(R.id.editTextTextPassword);
        final EditText nameId = findViewById(R.id.editTextTextName);
        Button btnSignUp = findViewById(R.id.SignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String email = emailId.getText().toString();
                String password = passwordId.getText().toString();
                String name = nameId.getText().toString();

                sign_up(mAuth, email, password, name);
            }
        });
    }

    public void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void sign_up(final FirebaseAuth mAuth, final String email, String password, final String name) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put("email", email);
                            childUpdates.put("name", name);

                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                            mDatabase.push().setValue(name);
                            mDatabase.child("users").push().updateChildren(childUpdates);

                            System.out.println("email and password is saved");
                            openMainActivity();
                        } else {
                            System.out.println("email and password is not saved");
                        }
                    }
                });
    }
}