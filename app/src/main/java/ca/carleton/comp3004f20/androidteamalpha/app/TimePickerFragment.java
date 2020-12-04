package ca.carleton.comp3004f20.androidteamalpha.app;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Locale;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    private EditText mEditText;

    public TimePickerFragment(EditText mEditText) {
        this.mEditText = mEditText;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int hour, minute;
        String text = mEditText.getText().toString();
        if (text.isEmpty()) {
            final Calendar c = Calendar.getInstance();
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
        } else {
            hour = Integer.parseInt(text.substring(0, text.indexOf(":")));
            minute = Integer.parseInt(text.substring(text.indexOf(":") + 1, text.indexOf(":") + 3));
        }
        return new TimePickerDialog(getActivity(), this, hour, minute, false);
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mEditText.setText(String.format(Locale.CANADA, "%02d:%02d", hourOfDay, minute));
    }
}
