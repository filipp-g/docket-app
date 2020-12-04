package ca.carleton.comp3004f20.androidteamalpha.app;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private EditText nameEdit, emailEdit, alertTimeEdit, notifyPeriodEdit;
    private SwitchMaterial notifySwitch;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Profile");

        RoundedBitmapDrawable mDrawable = RoundedBitmapDrawableFactory.create(
                getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.homer)
        );
        mDrawable.setCircular(true);
        ((ImageView) view.findViewById(R.id.imgProfile)).setImageDrawable(mDrawable);

        nameEdit = view.findViewById(R.id.editTextName);
        emailEdit = view.findViewById(R.id.editTextEmail);
        notifyPeriodEdit = view.findViewById(R.id.editDuePeriod);

        alertTimeEdit = view.findViewById(R.id.editAlarmTime);
        alertTimeEdit.setOnClickListener(v ->
                Utilities.showTimePickerDialog(getActivity(), alertTimeEdit)
        );

        notifySwitch = view.findViewById(R.id.switchEnable);
        notifySwitch.setOnCheckedChangeListener((v, isChecked) -> {
            alertTimeEdit.setEnabled(isChecked);
            notifyPeriodEdit.setEnabled(isChecked);
        });

        FloatingActionButton saveProfileBtn = view.findViewById(R.id.btnSaveProfile);
        saveProfileBtn.setOnClickListener(v -> saveProfile());

        initElements();
        return view;
    }

    private void initElements() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        nameEdit.setText(user.getDisplayName());
        emailEdit.setText(user.getEmail());

        FirebaseDatabase.getInstance()
                .getReference()
                .child(user.getUid())
                .child("settings")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        notifySwitch.setChecked(snapshot.child("notify_enabled").getValue(boolean.class));
                        alertTimeEdit.setText(snapshot.child("notify_alarm_time").getValue(String.class));
                        notifyPeriodEdit.setText(snapshot.child("notify_due_period").getValue(Long.class).toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void saveProfile() {
        if (validateElements()) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            user.updateEmail(emailEdit.getText().toString())
                    .addOnCompleteListener(updateEmailTask -> {
                        if (updateEmailTask.isSuccessful()) {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(nameEdit.getText().toString())
                                    .build();
                            user.updateProfile(profileUpdates);

                            DatabaseReference settingsRef = FirebaseDatabase.getInstance()
                                    .getReference()
                                    .child(user.getUid())
                                    .child("settings");
                            settingsRef.child("notify_enabled").setValue(notifySwitch.isChecked());
                            settingsRef.child("notify_alarm_time").setValue(alertTimeEdit.getText().toString());
                            settingsRef.child("notify_due_period").setValue(Integer.parseInt(notifyPeriodEdit.getText().toString()));

                            if (notifySwitch.isChecked()) {
                                String time = alertTimeEdit.getText().toString();
                                int hour = Integer.parseInt(time.substring(0, time.indexOf(":")));
                                int minute = Integer.parseInt(time.substring(time.indexOf(":") + 1));
                                ((MainActivity) getActivity()).setNotificationAlarm(hour, minute);
                            } else {
                                ((MainActivity) getActivity()).cancelNotificationAlarms();
                            }
                            Toast.makeText(getActivity(), "Profile updated", Toast.LENGTH_SHORT).show();
                        } else {
                            emailEdit.setError(updateEmailTask.getException().getMessage());
                        }
                    });
        }
    }

    private boolean validateElements() {
        if (checkInputError(nameEdit, "Name can't be empty"))
            return false;
        if (checkInputError(emailEdit, "Email can't be empty"))
            return false;
        if (notifySwitch.isChecked()) {
            if (checkInputError(alertTimeEdit, "Set alert time"))
                return false;
            if (checkInputError(notifyPeriodEdit, "Set notify period"))
                return false;
        }
        return true;
    }

    private boolean checkInputError(EditText input, String message) {
        if (input.getText().toString().isEmpty()) {
            input.setError(message);
            return true;
        }
        return false;
    }
}