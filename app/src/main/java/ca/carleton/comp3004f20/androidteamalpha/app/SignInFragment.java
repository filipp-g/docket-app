package ca.carleton.comp3004f20.androidteamalpha.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class SignInFragment extends Fragment {
    private FirebaseAuth mAuth;

    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        mAuth = FirebaseAuth.getInstance();

        ((MainActivity)getActivity()).hideBottomNav();

        Button signUp = view.findViewById(R.id.signUp);
        signUp.setOnClickListener(v ->
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new SignUpFragment())
                        .commit()
        );

        final EditText emailId = view.findViewById(R.id.editTextTextEmailAddress);
        final EditText passwordId = view.findViewById(R.id.editTextTextPassword);
        Button btnSignIn = view.findViewById(R.id.signIn);
        btnSignIn.setOnClickListener(v -> {
            String email = emailId.getText().toString();
            String password = passwordId.getText().toString();
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getActivity(), "Please enter credentials...", Toast.LENGTH_SHORT).show();
            } else {
                sign_in(mAuth, email, password);
            }
        });
        return view;
    }

    public void sign_in(FirebaseAuth mAuth, String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        getActivity()
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, new ProfileFragment())
                                .commit();
                        ((MainActivity)getActivity()).showBottomNav();
                    } else {
                        Toast.makeText(getActivity(), "Invalid Username/Password...", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}