package ca.carleton.comp3004f20.androidteamalpha.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpFragment extends Fragment {
    private FirebaseAuth mAuth;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        mAuth = FirebaseAuth.getInstance();

        ((MainActivity) getActivity()).hideBottomNav();

        final EditText emailId = view.findViewById(R.id.editTextTextEmailAddress);
        final EditText passwordId = view.findViewById(R.id.editTextTextPassword);
        final EditText nameId = view.findViewById(R.id.editTextTextName);

        Button btnSignUp = view.findViewById(R.id.signUp);
        btnSignUp.setOnClickListener(v -> {
            String email = emailId.getText().toString();
            String password = passwordId.getText().toString();
            String name = nameId.getText().toString();
            if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
                Toast.makeText(getActivity(), "Please enter credentials...", Toast.LENGTH_SHORT).show();
            } else {
                sign_up(mAuth, email, password, name);
            }
        });

        Button cancel = view.findViewById(R.id.cancelBtn);
        cancel.setOnClickListener(v ->
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new SignInFragment())
                        .commit()
        );

        return view;
    }

    private void sign_up(FirebaseAuth mAuth, String email, String password, String name) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), createTask -> {
                    if (createTask.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build();

                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(updateTask -> {
                                    DatabaseReference settingsRef = FirebaseDatabase.getInstance()
                                            .getReference()
                                            .child(user.getDisplayName())
                                            .child("settings");
                                    settingsRef.child("notify_enabled").setValue(false);
                                    settingsRef.child("notify_alarm_time").setValue("09:00");
                                    settingsRef.child("notify_due_period").setValue(24);

                                    getActivity()
                                            .getSupportFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.container, new ProfileFragment())
                                            .commit();
                                    ((MainActivity) getActivity()).showBottomNav();
                                });
                    } else {
                        Toast.makeText(getActivity(), "Failed to create new user", Toast.LENGTH_SHORT).show();
                        System.out.println(createTask.getException());
                    }
                });
    }
}