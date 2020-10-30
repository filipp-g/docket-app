package ca.carleton.comp3004f20.androidteamalpha.app;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProjectsFragment extends Fragment {
    private static final String TAG = "ViewDatabase";
    private static final String EMAIL = "email";
    private static final String USER= "user";

    int counter = 0;
    int numOfTasks = 0;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String user;
    private String email;

    public ProjectsFragment() {
        // Required empty public constructor
    }

    public static ProjectsFragment newInstance(String email, String user) {
        ProjectsFragment fragment = new ProjectsFragment();
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
        View view = inflater.inflate(R.layout.fragment_projects, container, false);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, new SignInFragment()).commit();
            Toast.makeText(getActivity(), "Please sign in...", Toast.LENGTH_SHORT).show();
        } else {
            if (getArguments() != null) {
                email = getArguments().getString(EMAIL);
                user = getArguments().getString(USER);
            }

            Button signUp = (Button) view.findViewById(R.id.btnAddTask);
            signUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, TaskFragment.newInstance(email, user)).commit();
                }
            });

            FirebaseDatabase.getInstance().getReference().child(user).child("tasks")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            getNum(dataSnapshot);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
        }

        return view;
    }

    private void getNum(DataSnapshot dataSnapshot) {
        numOfTasks = (int) dataSnapshot.getChildrenCount();

        HorizontalScrollView scrollView = (HorizontalScrollView) getView().findViewById(R.id.scrollable);

        LinearLayout mainLayout = new LinearLayout(getActivity());
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        final LinearLayout topLayout = new LinearLayout(getActivity());
        topLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout bottomLayout = new LinearLayout(getActivity());
        bottomLayout.setOrientation(LinearLayout.HORIZONTAL);

        for (DataSnapshot task : dataSnapshot.getChildren()) {
            Button newButton = new Button(getActivity());
            newButton.setId(counter);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(370,370);
            params.leftMargin = 40;
            params.topMargin = 40;

            newButton.setLayoutParams(params);
            newButton.setText(task.child("name").getValue().toString());

            if (counter % 2 == 0) {
                topLayout.addView(newButton);
            } else {
                bottomLayout.addView(newButton);
            }
            counter++;
        }

        mainLayout.addView(topLayout);
        mainLayout.addView(bottomLayout);

        scrollView.addView(mainLayout);
    }
}