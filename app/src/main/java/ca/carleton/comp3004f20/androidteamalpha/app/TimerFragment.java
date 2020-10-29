package ca.carleton.comp3004f20.androidteamalpha.app;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimerFragment extends Fragment {
    private static final String EMAIL = "email";
    private static final String USER= "user";

    private String email;
    private String user;

    public TimerFragment() {
        // Required empty public constructor
    }

    public static TimerFragment newInstance(String email, String user) {
        TimerFragment fragment = new TimerFragment();
        Bundle args = new Bundle();
        args.putString(EMAIL, email);
        args.putString(USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timer, container, false);

        if (getArguments() != null) {
            email = getArguments().getString(EMAIL);
            user = getArguments().getString(USER);
        }

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, SignInFragment.newInstance(email, user)).commit();
            Toast.makeText(getActivity(), "Please sign in...", Toast.LENGTH_SHORT).show();
        } else {
            // TODO
        }
        return view;
    }
}