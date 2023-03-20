package tk.phili.dienst.dienst.report;

import static android.app.PendingIntent.FLAG_IMMUTABLE;
import static tk.phili.dienst.dienst.report.ReportTimer.TimerState.PAUSED;
import static tk.phili.dienst.dienst.report.ReportTimer.TimerState.RUNNING;
import static tk.phili.dienst.dienst.report.ReportTimer.TimerState.STOPPED;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import tk.phili.dienst.dienst.R;
import tk.phili.dienst.dienst.Splash;

public class ReportTimer {

    private static final int NOTIFICATION_ID = 9658654;
    private static final String KEY_TEMP_MILLIS = "timer_temp_millis";
    private static final String KEY_STATE = "timer_state";
    private static final String KEY_START_MILLIS = "timer_start_millis";

    private ReportFragment reportFragment;

    private Context context;

    public ReportTimer(Context context, ReportFragment reportFragment) {
        this.context = context;
        this.reportFragment = reportFragment;
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
        if (sp.contains(KEY_TEMP_MILLIS)) {
            return sp.getLong(KEY_TEMP_MILLIS, 0);
        }
        return 0;
    }

    private void setTempMillis(long tempMillis) {
        SharedPreferences.Editor editor = getSharedPreferencesEditor();
        editor.putLong(KEY_TEMP_MILLIS, tempMillis);
        editor.apply();
    }

    private long getStartMillis() {
        SharedPreferences sp = getSharedPreferences();
        if (sp.contains(KEY_START_MILLIS)) {
            return sp.getLong(KEY_START_MILLIS, 0);
        }
        return 0;
    }

    private void setStartMillis(long startMillis) {
        SharedPreferences.Editor editor = getSharedPreferencesEditor();
        editor.putLong(KEY_START_MILLIS, startMillis);
        editor.apply();
    }

    public TimerState getTimerState() {
        SharedPreferences sp = getSharedPreferences();
        if (sp.contains(KEY_STATE)) {
            return TimerState.valueOf(sp.getString(KEY_STATE, STOPPED.name()));
        }
        return STOPPED;
    }

    private void setTimerState(TimerState timerState) {
        SharedPreferences.Editor editor = getSharedPreferencesEditor();
        editor.putString(KEY_STATE, timerState.name());
        editor.apply();
    }

    private void updateReportFragment(long reportId) {
        if (reportFragment == null) {
            return;
        }

        reportFragment.updateList();

        if (reportId != -1) {
            reportFragment.scrollToReportId(reportId);
            reportFragment.showEditDialog(reportId);
        }
    }

    public void startTimer() {
        TimerState timerState = getTimerState();
        if (timerState == RUNNING) {
            return;
        }

        if (timerState == STOPPED) {
            setTempMillis(0);
        }

        setTimerState(RUNNING);
        setStartMillis(System.currentTimeMillis());
        updateReportFragment(-1);
        sendNotification();
    }

    public void pauseTimer() {
        TimerState timerState = getTimerState();
        if (timerState != RUNNING) {
            return;
        }

        setTimerState(PAUSED);

        long timer = System.currentTimeMillis() - getStartMillis();
        setTempMillis(getTempMillis() + timer);

        setStartMillis(0);
        updateReportFragment(-1);
        removeNotification();
    }

    public long getTimer() {
        TimerState timerState = getTimerState();
        switch (timerState) {
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
        updateReportFragment(-1);
        removeNotification();
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
        updateReportFragment(report.getId());

        return timer;
    }

    private void sendNotification() {
        Intent i = new Intent(context, Splash.class);
        i.putExtra("Activity", "MainActivity");
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, FLAG_IMMUTABLE);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "reportTimer")
                .setContentTitle(context.getString(R.string.timer_running_title))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentText(context.getString(R.string.title_running_content))
                .setSmallIcon(R.drawable.ic_timer_black_24dp)
                .setUsesChronometer(true)
                .setShowWhen(true)
                .setWhen(System.currentTimeMillis() - getTimer())
                .setSilent(true)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void removeNotification() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(NOTIFICATION_ID);
    }


    public enum TimerState {
        RUNNING, PAUSED, STOPPED;
    }

}
