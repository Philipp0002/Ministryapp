package tk.phili.dienst.dienst.uiwrapper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.navigationrail.NavigationRailView;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import tk.phili.dienst.dienst.R;
import tk.phili.dienst.dienst.calendar.CalendarFragment;
import tk.phili.dienst.dienst.calendar.CalendarWorker;
import tk.phili.dienst.dienst.dailytext.DailytextFragment;
import tk.phili.dienst.dienst.drawer.Drawer;
import tk.phili.dienst.dienst.notes.NotesFragment;
import tk.phili.dienst.dienst.report.ReportFragment;
import tk.phili.dienst.dienst.samplepresentations.SamplePresentationsFragment;
import tk.phili.dienst.dienst.settings.GDPRInfo;
import tk.phili.dienst.dienst.utils.AdaptiveUtils;
import tk.phili.dienst.dienst.utils.Shortcuts;
import tk.phili.dienst.dienst.videos.VideoFragment;

public class WrapperActivity extends AppCompatActivity implements FragmentCommunicationPass {

    public static final String FRAGMENTPASS_TOOLBAR = "TOOLBAR";

    //ADAPTIVE
    private DrawerLayout drawerLayout;
    private NavigationView modalNavDrawer;
    private NavigationRailView navRail;
    private NavigationView navDrawer;
    private Configuration configuration;
    private FragmentContainerView fragmentContainerView;

    public Drawer drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);

        int darkMode = getSharedPreferences("MainActivity", Context.MODE_PRIVATE).getInt("dark_mode", 0);
        int mode = 0;
        if (darkMode == 0) {
            mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        } else if (darkMode == 1) {
            mode = AppCompatDelegate.MODE_NIGHT_YES;
        } else if (darkMode == 2) {
            mode = AppCompatDelegate.MODE_NIGHT_NO;
        }
        AppCompatDelegate.setDefaultNightMode(mode);

        if (!getSharedPreferences("Splash", Context.MODE_PRIVATE).getBoolean("dsgvo_accept", false)) {
            startActivity(new Intent(this, GDPRInfo.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        }

        setContentView(R.layout.activity_wrapper);

        fragmentContainerView = findViewById(R.id.fragment_container_view);

        drawer = new Drawer();

        if(savedInstanceState == null) {
            String s = getIntent().getStringExtra("shortcut_started");

            Class clazz = null;

            if(s == null || s.equals("MainActivity")) {
                clazz = ReportFragment.class;
            }else if(s.equals("Notizen")) {
                clazz =  NotesFragment.class;
            }else if(s.equals("Empfehlungen")) {
                clazz = SamplePresentationsFragment.class;
            }else if(s.equals("Videos")) {
                clazz = VideoFragment.class;
            }else if(s.equals("Tagestext")) {
                clazz =  DailytextFragment.class;
            }else if(s.equals("Kalender")) {
                clazz =  CalendarFragment.class;
            }

            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container_view, clazz, null)
                    .commit();
        }else{
            drawer.initialized = false;
        }

        createNotificationChannel();

        PeriodicWorkRequest.Builder builder =
                new PeriodicWorkRequest.Builder(CalendarWorker.class, 15,
                        TimeUnit.MINUTES);
        PeriodicWorkRequest periodicWorkRequest = builder.build();
        WorkManager.getInstance(getApplicationContext())
                .enqueueUniquePeriodicWork(
                        "Calendar",
                        ExistingPeriodicWorkPolicy.KEEP,
                        periodicWorkRequest
                );

        try {
            Shortcuts.updateShortcuts(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDataPass(Fragment fragment, String tag, Object object) {
        if(tag == FRAGMENTPASS_TOOLBAR){
            Toolbar toolbar = (Toolbar) object;
            toolbar.bringToFront();

            drawerLayout = findViewById(R.id.drawer_layout);
            modalNavDrawer = findViewById(R.id.modal_nav_drawer);
            navRail = findViewById(R.id.nav_rail);
            navDrawer = findViewById(R.id.nav_drawer);
            configuration = getResources().getConfiguration();

            int screenWidth = configuration.screenWidthDp;
            AdaptiveUtils.updateNavigationViewLayout(
                    screenWidth, drawerLayout, modalNavDrawer, navRail, navDrawer,
                    toolbar, this);

            drawer.manageDrawers(this, fragment, drawerLayout, modalNavDrawer, navRail, navDrawer);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        drawer.initialized = false;
    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channelCalendar = new NotificationChannel("calendar", getString(R.string.title_calendar), NotificationManager.IMPORTANCE_HIGH);
            channelCalendar.setDescription(getString(R.string.title_calendar));
            NotificationChannel channelReportTimer = new NotificationChannel("reportTimer", getString(R.string.timer_report_title), NotificationManager.IMPORTANCE_DEFAULT);
            channelReportTimer.setDescription(getString(R.string.timer_report_title));
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannels(Arrays.asList(channelCalendar, channelReportTimer));
        }
    }
}