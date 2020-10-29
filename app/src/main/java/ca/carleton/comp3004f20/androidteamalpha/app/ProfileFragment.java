package ca.carleton.comp3004f20.androidteamalpha.app;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private static final String EMAIL = "email";
    private static final String USER= "user";

    private String email;
    private String user;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String email, String user) {
        ProfileFragment fragment = new ProfileFragment();
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
        if (getArguments() != null) {
            email = getArguments().getString(EMAIL);
            user = getArguments().getString(USER);
        }

        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
}