package tk.phili.dienst.dienst;

import androidx.multidex.MultiDexApplication;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.downloader.PRDownloader;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import tk.phili.dienst.dienst.calendar.CalendarWorker;
import tk.phili.dienst.dienst.dailytext.widget.DailytextWorker;

public class MinistryApplication extends MultiDexApplication {

    Timer t = null;

    @Override
    public void onCreate() {
        super.onCreate();

        runTimer();

        PRDownloader.initialize(getApplicationContext());

        startDailyWork();
    }

    public void runTimer() {
        if (t == null) {
            t = new Timer();
            t.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    CalendarWorker.run(getApplicationContext());
                }
            }, 0, 600000);
        }
    }


    private void startDailyWork() {
        // Start one time worker
        OneTimeWorkRequest nowRequest = new OneTimeWorkRequest.Builder(DailytextWorker.class).build();
        WorkManager.getInstance(this).enqueue(nowRequest);

        // Start periodic work
        LocalTime targetTime = LocalTime.of(1, 0);
        long currentMillis = System.currentTimeMillis();
        long targetMillisToday = LocalDate.now()
                .atTime(targetTime)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        if (currentMillis > targetMillisToday) {
            // Zielzeit heute schon vorbei -> morgen starten
            targetMillisToday += TimeUnit.DAYS.toMillis(1);
        }
        long initialDelay = targetMillisToday - currentMillis;

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                DailytextWorker.class,
                1, TimeUnit.DAYS
        )
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "TagestextUpdate",
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
        );
    }


}
