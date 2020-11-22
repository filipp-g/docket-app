package ca.carleton.comp3004f20.androidteamalpha.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ExecutionException;

import static java.lang.Math.ceil;

public class TimerFragment extends Fragment {

    // TIMER VARIABLES
    private FloatingActionButton fab;
    private TextView timerCounterText;
    private EditText timerEditText;
    private int timerMinutes = 30;
    private boolean timerStopped = true;
    private FrameLayout mainView;

    // ASYNC VARIABLES
    private AsyncCountdownTimerTask timerTask;

    // TIMER FUNCTIONS
    private void showTimerEditDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());

        View view = getLayoutInflater().inflate(R.layout.dialog_edit_timer, null, false);
        final EditText timerMinsEditText = view.findViewById(R.id.timer_mins_edit_text);
        timerMinsEditText.setText(String.valueOf(timerMinutes));
        dialogBuilder.setView(view);

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        dialog.setOnDismissListener(d -> {
            timerMinutes = Integer.parseInt(timerMinsEditText.getText().toString());
            resetTimer();
        });
    }
    private void startTimer() {
        fab.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_stop, getActivity().getTheme()));
        timerStopped = false;
        timerTask = new AsyncCountdownTimerTask();
        timerTask.execute(timerMinutes);
    }
    private void stopTimer() {
        if (timerTask != null) {
            if (timerTask.isRunning()) timerTask.setRunning(false);
            try {
                // Wait for task to finish
                timerTask.get();
            } catch (ExecutionException e) {
                // Do nothing
            } catch (InterruptedException e) {
                // Do nothing
            }
        }

        fab.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play, getActivity().getTheme()));
        timerStopped = true;
    }
    private void logTime() {
        // Todo: log time to firebase / etc
        Snackbar.make(mainView, "Logged " + String.valueOf(timerMinutes) + " minutes", Snackbar.LENGTH_SHORT).show();
    }
    private void updateTimer(int minutes, int seconds) {
        timerCounterText.setText(StringUtils.leftPad(String.valueOf(minutes), 2, "0") + ":" + StringUtils.leftPad(String.valueOf(seconds), 2, "0"));
    }
    private void resetTimer() {
        stopTimer();
        updateTimer(timerMinutes, 0);
    }

    // FRAGMENT SETUP FUNCTIONS
    public TimerFragment() {
        // Required empty public constructor
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timer, container, false);

        // Get component ids
        fab = view.findViewById(R.id.fab_play);
        timerCounterText = view.findViewById(R.id.timer_counter_text);
        timerEditText = view.findViewById(R.id.timer_edit_text);
        mainView = view.findViewById(R.id.main);

        // Setup action listeners
        fab.setOnLongClickListener(v -> {
            resetTimer();
            logTime();
            return true;
        });

        fab.setOnClickListener(v -> {
            if (timerStopped) startTimer();
            else resetTimer();
        });

        timerEditText.setFocusableInTouchMode(false);
        timerEditText.setOnClickListener(v -> showTimerEditDialog());
        timerCounterText.setOnClickListener(v -> showTimerEditDialog());

        return view;
    }

    // Countdown Timer Task
    private class AsyncCountdownTimerTask extends AsyncTask<Integer, Integer, Boolean> {

        private static final int CHECKS_PER_SECOND = 30;
        private boolean running = true;
        private int totalSeconds;

        public synchronized void setRunning(boolean running) {
            this.running = running;
        }

        public boolean isRunning() {
            return running;
        }

        @Override
        protected Boolean doInBackground(Integer... integers) {
            totalSeconds = integers[0]*60;
            int seconds = totalSeconds;
            int secondsLast = seconds;
            long millisStart = System.currentTimeMillis();

            while (seconds > 0) {
                long sleepTimeMillis = 1000/CHECKS_PER_SECOND;

                try {
                    Thread.sleep(sleepTimeMillis);
                } catch (InterruptedException e) {
                    // Stop execution
                    break;
                }

                // Todo: fix this code, timer will be off <1 second
                long millisCurrent = System.currentTimeMillis();
                if (running) {
                    seconds = totalSeconds - (int)ceil((double)(millisCurrent - millisStart) / 1000);

                    if (seconds < secondsLast) {
                        secondsLast = seconds;
                        publishProgress(seconds);
                    }
                    else continue;
                } else {
                    break;
                }
            }

            return seconds == 0;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            updateTimer(values[0] / 60, values[0] % 60);
        }

        @Override
        protected void onPostExecute(Boolean notInterrupted) {
            super.onPostExecute(notInterrupted);

            if (notInterrupted) {
                resetTimer();
                logTime();
            }
        }
    }
}