package ca.carleton.comp3004f20.androidteamalpha.app;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OverviewFragment extends Fragment {
    int counter = 0;
    int numOfTasks = 0;

    private OverviewViewModel mViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.overview_fragment, container, false);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            getActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, new SignInFragment())
                    .commit();
            Toast.makeText(getActivity(), "Please sign in...", Toast.LENGTH_SHORT).show();
        } else {
            Button addTask = (Button) view.findViewById(R.id.btnAddTask);
            addTask.setOnClickListener(v -> getActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, TaskFragment.newInstance(null))
                    .commit()
            );

            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
                    .child("tasks")
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

        ImageButton overViewButton = (ImageButton) view.findViewById(R.id.calenderButton);
        overViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new CalendarFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(OverviewViewModel.class);
        // TODO: Use the ViewModel
    }

    private void getNum(DataSnapshot dataSnapshot) {
        numOfTasks = (int) dataSnapshot.getChildrenCount();

        HorizontalScrollView scrollView = (HorizontalScrollView) getView().findViewById(R.id.horizontalScrollView);

        LinearLayout mainLayout = new LinearLayout(getActivity());
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout topLayout = new LinearLayout(getActivity());
        topLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout bottomLayout = new LinearLayout(getActivity());
        bottomLayout.setOrientation(LinearLayout.HORIZONTAL);

        for (DataSnapshot task : dataSnapshot.getChildren()) {
            System.out.println(task.child("weight").getValue().toString());
            Task taskObject = new Task(task.child("id").getValue().toString(),
                    task.child("name").getValue().toString(), task.child("projectId").getValue().toString(),
                    task.child("dueDate").getValue().toString(), task.child("dueTime").getValue().toString(),
                    Integer.parseInt(task.child("weight").getValue().toString()),
                    Integer.parseInt(task.child("timeRequired").getValue().toString()),
                    Integer.parseInt(task.child("timeSpent").getValue().toString()),
                    Boolean.parseBoolean(task.child("complete").getValue().toString()));
            Button newButton = new Button(getActivity());
            newButton.setId(counter);
            newButton.setOnClickListener(view -> getActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, TaskFragment.newInstance(taskObject))
                    .commit());
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