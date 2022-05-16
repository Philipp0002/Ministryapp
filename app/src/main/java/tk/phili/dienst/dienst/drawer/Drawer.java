package tk.phili.dienst.dienst.drawer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.navigationrail.NavigationRailView;
import com.prof.rssparser.Article;
import com.prof.rssparser.Parser;

import java.util.ArrayList;

import tk.phili.dienst.dienst.calendar.CalendarFragment;
import tk.phili.dienst.dienst.dailytext.DailytextFragment;
import tk.phili.dienst.dienst.notes.NotesFragment;
import tk.phili.dienst.dienst.report.ReportFragment;
import tk.phili.dienst.dienst.samplepresentations.SamplePresentationsFragment;
import tk.phili.dienst.dienst.settings.SettingsFragment;
import tk.phili.dienst.dienst.uiwrapper.WrapperActivity;
import tk.phili.dienst.dienst.videos.VideoFragment;
import tk.phili.dienst.dienst.R;

public class Drawer {

    private static boolean initialized = false;

    private static ArrayList<Article> articles = null;
    private static Parser parser = null;
    private static String urlString = null;
    private static String tickerURL = null;

    public static Object[][] positionMapping = new Object[][] {
            { ReportFragment.class, 0, R.id.drawer_report },
            { NotesFragment.class, 1, R.id.drawer_notes },
            { SamplePresentationsFragment.class, 2, R.id.drawer_samplepresentations },
            { DailytextFragment.class, 3, R.id.drawer_dailytext },
            { VideoFragment.class, 4, R.id.drawer_videos },
            { CalendarFragment.class, 5, R.id.drawer_calendar },
            { SettingsFragment.class, 6, R.id.drawer_settings }
    };

    public static void manageDrawers(WrapperActivity activity,
                                     @NonNull Fragment fragment,
                                     @NonNull DrawerLayout drawerLayout,
                                     @NonNull NavigationView modalNavDrawer,
                                     @NonNull NavigationRailView navRail,
                                     @NonNull NavigationView navDrawer){

        if(!initialized) {
            final SharedPreferences sp = activity.getSharedPreferences("MainActivity", Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sp.edit();

            Typeface typeface = Typeface.createFromAsset(activity.getAssets(), "HammersmithOne-Regular.ttf");
            View titleHeaderNav = activity.getLayoutInflater().inflate(R.layout.drawerheaderlayout, null);
            View titleHeaderNavModal = activity.getLayoutInflater().inflate(R.layout.drawerheaderlayout, null);

            TextView titleTextHeaderNav = titleHeaderNav.findViewById(R.id.app_text_header);
            TextView titleTextHeaderNavModal = titleHeaderNavModal.findViewById(R.id.app_text_header);

            titleTextHeaderNav.setTypeface(typeface);
            titleTextHeaderNavModal.setTypeface(typeface);

            modalNavDrawer.addHeaderView(titleHeaderNavModal);
            navDrawer.addHeaderView(titleHeaderNav);



            View tickerHeaderNav = activity.getLayoutInflater().inflate(R.layout.drawertickerlayout, null);
            View tickerHeaderNavModal = activity.getLayoutInflater().inflate(R.layout.drawertickerlayout, null);

            TextView tickerTextNav = tickerHeaderNav.findViewById(R.id.tickerview);
            TextView tickerTextNavModal = tickerHeaderNavModal.findViewById(R.id.tickerview);

            String tickerDisplayText = sp.getString("LATEST_NEWS", activity.getString(R.string.drawer_ticker_no_news));

            tickerTextNav.setText(tickerDisplayText);
            tickerTextNavModal.setText(tickerDisplayText);

            modalNavDrawer.addHeaderView(tickerHeaderNavModal);
            navDrawer.addHeaderView(tickerHeaderNav);



            if(activity.getString(R.string.URL_end).equalsIgnoreCase("de")){
                urlString = "https://www.jw.org/de/nachrichten/jw/rss/NewsSubsectionRSSFeed/feed.xml";
                tickerURL = "https://www.jw.org/de/nachrichten/jw/";
            }else if(activity.getString(R.string.URL_end).equalsIgnoreCase("it")){
                urlString = "https://www.jw.org/it/news/jw-news/rss/NewsSubsectionRSSFeed/feed.xml";
                tickerURL = "https://www.jw.org/it/news/jw-news/";
            }else{
                urlString = "https://www.jw.org/en/news/jw/rss/NewsSubsectionRSSFeed/feed.xml";
                tickerURL = "https://www.jw.org/en/news/jw/";
            }
            View.OnClickListener urlClickListener = view -> {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(tickerURL));
                activity.startActivity(i);
            };
            tickerTextNav.setOnClickListener(urlClickListener);
            tickerTextNavModal.setOnClickListener(urlClickListener);

            parser = new Parser();
            parser.execute(urlString);
            parser.onFinish(new Parser.OnTaskCompleted() {

                @Override
                public void onTaskCompleted(ArrayList<Article> list) {
                    articles = list;
                    String toset = "++"+activity.getString(R.string.drawer_ticker_news)+"++ ";

                    int i = 0;
                    for(Article a : articles){
                        i++;
                        if(i == 3)
                            break;

                        toset += a.getTitle().replace("NEWS RELEASES | " , "").replace("PRESSEMITTEILUNGEN | ", "") + " ++ ";
                    }

                    toset = toset.substring(0, toset.length()-3) + " ++"+activity.getString(R.string.drawer_ticker_news)+"++";

                    editor.putString("LATEST_NEWS", toset);
                    editor.commit();

                    tickerTextNav.setText(toset);
                    tickerTextNavModal.setText(toset);
                }

                @Override
                public void onError() { }
            });


            initialized = true;
        }

        for(Object[] mapping : positionMapping){
            if(((Class)mapping[0]).isInstance(fragment)){
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
