package tk.phili.dienst.dienst.report;

import static tk.phili.dienst.dienst.report.ReportTimer.TimerState.PAUSED;
import static tk.phili.dienst.dienst.report.ReportTimer.TimerState.RUNNING;
import static tk.phili.dienst.dienst.report.ReportTimer.TimerState.STOPPED;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.time.LocalDate;
import java.util.Date;

public class ReportTimer {

    private static final String KEY_TEMP_MILLIS = "timer_temp_millis";
    private static final String KEY_STATE = "timer_state";
    private static final String KEY_START_MILLIS = "timer_start_millis";

    private Context context;

    public ReportTimer(Context context) {
        this.context = context;
    }

    private SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences("MainActivity", Context.MODE_PRIVATE);
    }

    private SharedPreferences.Editor getSharedPreferencesEditor() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.edit();
    }

    private long getTempMillis() {
        SharedPreferences sp = getSharedPreferences();
        if(sp.contains(KEY_TEMP_MILLIS)) {
            return sp.getLong(KEY_TEMP_MILLIS, 0);
        }
        return 0;
    }

    private void setTempMillis(long tempMillis){
        SharedPreferences.Editor editor = getSharedPreferencesEditor();
        editor.putLong(KEY_TEMP_MILLIS, tempMillis);
        editor.apply();
    }

    private long getStartMillis() {
        SharedPreferences sp = getSharedPreferences();
        if(sp.contains(KEY_START_MILLIS)) {
            return sp.getLong(KEY_START_MILLIS, 0);
        }
        return 0;
    }

    private void setStartMillis(long startMillis){
        SharedPreferences.Editor editor = getSharedPreferencesEditor();
        editor.putLong(KEY_START_MILLIS, startMillis);
        editor.apply();
    }

    public TimerState getTimerState() {
        SharedPreferences sp = getSharedPreferences();
        if(sp.contains(KEY_STATE)) {
            return TimerState.valueOf(sp.getString(KEY_STATE, STOPPED.name()));
        }
        return STOPPED;
    }

    private void setTimerState(TimerState timerState){
        SharedPreferences.Editor editor = getSharedPreferencesEditor();
        editor.putString(KEY_STATE, timerState.name());
        editor.apply();
    }

    private void updateReportFragment() {
        ReportFragment fragment = ReportFragment.INSTANCE;
        if(fragment == null){
            return;
        }

        fragment.updateList();
    }

    public void startTimer() {
        TimerState timerState = getTimerState();
        Log.d("TIMERRR", "timerState " + timerState.name());
        if(timerState == RUNNING) {
            return;
        }

        if(timerState == STOPPED){
            setTempMillis(0);
        }

        setTimerState(RUNNING);
        setStartMillis(System.currentTimeMillis());
        updateReportFragment();
    }

    public void pauseTimer() {
        TimerState timerState = getTimerState();
        if(timerState != RUNNING) {
            return;
        }

        setTimerState(PAUSED);

        long timer = System.currentTimeMillis() - getStartMillis();
        setTempMillis(getTempMillis() + timer);

        setStartMillis(0);
        updateReportFragment();
    }

    public long getTimer(){
        TimerState timerState = getTimerState();
        switch (timerState){
            case STOPPED:
                return 0;
            case RUNNING:
                return System.currentTimeMillis() - getStartMillis() + getTempMillis();
            case PAUSED:
                return getTempMillis();
            default:
                return 0;
        }
    }

    public long stopTimer() {
        long timer = getTimer();
        setTimerState(STOPPED);
        setTempMillis(0);
        updateReportFragment();
        return timer;
    }

    public long stopTimerAndSave() {
        long timer = stopTimer();
        ReportManager reportManager = new ReportManager(context);

        Report report = new Report();
        report.setDate(LocalDate.now());
        report.setMinutes(timer / 1000 / 60);
        report.setId(reportManager.getNextId());

        reportManager.createReport(report);
        updateReportFragment();

        return timer;
    }



    public enum TimerState {
        RUNNING, PAUSED, STOPPED;
    }

}
