package tk.phili.dienst.dienst;

import androidx.multidex.MultiDexApplication;

import java.util.Timer;
import java.util.TimerTask;

import tk.phili.dienst.dienst.calendar.KalenderWorker;
import tk.phili.dienst.dienst.drawer.Drawer;

public class MinistryApplication extends MultiDexApplication {

    Timer t = null;

    @Override
    public void onCreate() {
        super.onCreate();

        runTimer();
    }

    public void runTimer(){
        if(t == null){
            t = new Timer();
            t.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    KalenderWorker.run(getApplicationContext());
                }
            }, 0, 600000);
        }
    }

}
