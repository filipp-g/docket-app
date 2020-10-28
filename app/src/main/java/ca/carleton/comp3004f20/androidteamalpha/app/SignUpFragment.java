package ca.carleton.comp3004f20.androidteamalpha.app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpFragment extends Fragment {
    private Button button;
    private FirebaseAuth mAuth;

    public SignUpFragment() {
        // Required empty public constructor
    }

    public static SignUpFragment newInstance(String param1, String param2) {
        SignUpFragment fragment = new SignUpFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        mAuth = FirebaseAuth.getInstance();

        final EditText emailId = view.findViewById(R.id.editTextTextEmailAddress);
        final EditText passwordId = view.findViewById(R.id.editTextTextPassword);
        final EditText nameId = view.findViewById(R.id.editTextTextName);
        Button btnSignIn = view.findViewById(R.id.SignUp);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String email = emailId.getText().toString();
                String password = passwordId.getText().toString();
                String name = nameId.getText().toString();
                if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter credentials...", Toast.LENGTH_SHORT).show();
                } else {
                    sign_up(mAuth, email, password, name);
                }
            }
        });
        return view;
    }

    public void sign_up(final FirebaseAuth mAuth, final String email, String password, final String name) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put("email", email);
                            childUpdates.put("name", name);

                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                            mDatabase.push().setValue(name);
                            mDatabase.child("users").push().updateChildren(childUpdates);

                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, new SignInFragment()).commit();
                        } else {
                            Toast.makeText(getActivity(), "Failed to create new user", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}