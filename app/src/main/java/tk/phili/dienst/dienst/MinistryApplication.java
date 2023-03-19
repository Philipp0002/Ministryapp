package tk.phili.dienst.dienst;

import androidx.multidex.MultiDexApplication;

import com.downloader.PRDownloader;

import java.util.Timer;
import java.util.TimerTask;

import tk.phili.dienst.dienst.calendar.CalendarWorker;

public class MinistryApplication extends MultiDexApplication {

    Timer t = null;

    @Override
    public void onCreate() {
        super.onCreate();

        runTimer();

        PRDownloader.initialize(getApplicationContext());
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

}
