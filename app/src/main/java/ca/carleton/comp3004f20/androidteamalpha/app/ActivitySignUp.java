package ca.carleton.comp3004f20.androidteamalpha.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

public class ActivitySignUp extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        final EditText emailId = findViewById(R.id.editTextTextEmailAddress);
        final EditText passwordId = findViewById(R.id.editTextTextPassword);
        Button btnSignUp = findViewById(R.id.SignUp);
        btnSignUp.setOnClickListener(v -> {
            String email = emailId.getText().toString();
            String password = passwordId.getText().toString();

            sign_up(mAuth, email, password);
        });
    }

    public void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void sign_up(final FirebaseAuth mAuth, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        System.out.println("email and password is saved");
                        openMainActivity();
                    } else {
                        System.out.println("email and password is not saved");
                    }
                });
    }
}