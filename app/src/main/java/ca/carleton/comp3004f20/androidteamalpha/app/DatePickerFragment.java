package ca.carleton.comp3004f20.androidteamalpha.app;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Locale;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private EditText mEditText;

    public DatePickerFragment(EditText mEditText) {
        this.mEditText = mEditText;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int year, month, day;
        String text = mEditText.getText().toString();
        if (text.isEmpty()) {
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        } else {
            year = Integer.parseInt(text.substring(0, 4));
            month = Integer.parseInt(text.substring(5, 7)) - 1;
            day = Integer.parseInt(text.substring(8, 10));
        }
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        mEditText.setText(
                String.format(Locale.CANADA, "%d-%02d-%02d", year, month + 1, day)
        );
    }
}
