package tk.phili.dienst.dienst.drawer;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.navigationrail.NavigationRailView;

import tk.phili.dienst.dienst.calendar.Kalender;
import tk.phili.dienst.dienst.dailytext.DailytextActivity;
import tk.phili.dienst.dienst.notes.NotesFragment;
import tk.phili.dienst.dienst.report.ReportFragment;
import tk.phili.dienst.dienst.samplepresentations.SamplePresentationsFragment;
import tk.phili.dienst.dienst.settings.SettingsActivity;
import tk.phili.dienst.dienst.uiwrapper.WrapperActivity;
import tk.phili.dienst.dienst.videos.VideoActivity;
import tk.phili.dienst.dienst.R;

public class DrawerNew {

    private static boolean initialized = false;

    public static Object[][] positionMapping = new Object[][] {
            { ReportFragment.class, 0, R.id.drawer_report },
            { NotesFragment.class, 1, R.id.drawer_notes },
            { SamplePresentationsFragment.class, 2, R.id.drawer_samplepresentations },
            { DailytextActivity.class, 3, R.id.drawer_dailytext },
            { VideoActivity.class, 4, R.id.drawer_videos },
            { Kalender.class, 5, R.id.drawer_calendar },
            { SettingsActivity.class, 6, R.id.drawer_settings }
    };

    public static void manageDrawers(WrapperActivity activity,
                                     @NonNull DrawerLayout drawerLayout,
                                     @NonNull NavigationView modalNavDrawer,
                                     @NonNull NavigationRailView navRail,
                                     @NonNull NavigationView navDrawer){

        if(!initialized) {
            View titleHeaderNav = activity.getLayoutInflater().inflate(R.layout.drawerheaderlayout, null);
            View titleHeaderNavModal = activity.getLayoutInflater().inflate(R.layout.drawerheaderlayout, null);
            modalNavDrawer.addHeaderView(titleHeaderNavModal);
            navDrawer.addHeaderView(titleHeaderNav);
            initialized = true;
        }

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
                    drawerLayout.closeDrawers();
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

    private static void onItemClicked(WrapperActivity activity, Class toOpen){
        activity.getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_view, toOpen, null)
                .commit();
        hideKeyboard(activity);
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
