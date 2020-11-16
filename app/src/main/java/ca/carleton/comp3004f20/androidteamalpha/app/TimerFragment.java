package ca.carleton.comp3004f20.androidteamalpha.app;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class TimerFragment extends Fragment {

    public TimerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timer, container, false);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            getActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, new SignInFragment())
                    .commit();
            Toast.makeText(getActivity(), "Please sign in...", Toast.LENGTH_SHORT).show();
        } else {
            // TODO
        }
        return view;
    }
}