package tk.phili.dienst.dienst.drawer;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.navigationrail.NavigationRailView;

import tk.phili.dienst.dienst.calendar.Kalender;
import tk.phili.dienst.dienst.dailytext.DailytextActivity;
import tk.phili.dienst.dienst.notes.Notes;
import tk.phili.dienst.dienst.report.ReportActivity;
import tk.phili.dienst.dienst.samplepresentations.SamplePresentationsActivity;
import tk.phili.dienst.dienst.settings.SettingsActivity;
import tk.phili.dienst.dienst.videos.VideoActivity;
import tk.phili.dienst.dienst.R;

public class DrawerNew {

    public static Object[][] positionMapping = new Object[][] {
            { ReportActivity.class, 0, R.id.drawer_report },
            { Notes.class, 1, R.id.drawer_notes },
            { SamplePresentationsActivity.class, 2, R.id.drawer_samplepresentations },
            { DailytextActivity.class, 3, R.id.drawer_dailytext },
            { VideoActivity.class, 4, R.id.drawer_videos },
            { Kalender.class, 5, R.id.drawer_calendar },
            { SettingsActivity.class, 6, R.id.drawer_settings }
    };

    public static void manageDrawers(Activity activity,
                                     @NonNull NavigationView modalNavDrawer,
                                     @NonNull NavigationRailView navRail,
                                     @NonNull NavigationView navDrawer){

        View titleHeaderNav = activity.getLayoutInflater().inflate(R.layout.drawerheaderlayout, null);
        View titleHeaderNavModal = activity.getLayoutInflater().inflate(R.layout.drawerheaderlayout, null);
        modalNavDrawer.addHeaderView(titleHeaderNavModal);
        navDrawer.addHeaderView(titleHeaderNav);

        for(Object[] mapping : positionMapping){
            if(((Class)mapping[0]).isInstance(activity)){
                modalNavDrawer.setCheckedItem(modalNavDrawer.getMenu().getItem((int)mapping[1]));
                navRail.getMenu().getItem((int)mapping[1]).setChecked(true);
                navDrawer.setCheckedItem(navDrawer.getMenu().getItem((int)mapping[1]));
                break;
            }
        }

        modalNavDrawer.setNavigationItemSelectedListener(item -> {
            for(Object[] mapping : positionMapping){
                if((int)mapping[2] == item.getItemId()){
                    onItemClicked(activity, (Class)mapping[0]);
                    break;
                }
            }
            return true;
        });

        navDrawer.setNavigationItemSelectedListener(item -> {
            for(Object[] mapping : positionMapping){
                if((int)mapping[2] == item.getItemId()){
                    onItemClicked(activity, (Class)mapping[0]);
                    break;
                }
            }
            return true;
        });

        navRail.setOnItemSelectedListener(item -> {
            for(Object[] mapping : positionMapping){
                if((int)mapping[2] == item.getItemId()){
                    onItemClicked(activity, (Class)mapping[0]);
                    break;
                }
            }
            return true;
        });

    }

    private static void onItemClicked(Activity activity, Class toOpen){
        activity.startActivity(new Intent(activity, toOpen));
        hideKeyboard(activity);
        activity.finish();
        activity.overridePendingTransition(0, 0);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
