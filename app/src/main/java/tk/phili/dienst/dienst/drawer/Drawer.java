package tk.phili.dienst.dienst.drawer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.prof.rssparser.Article;
import com.prof.rssparser.Parser;

import java.util.ArrayList;

import tk.phili.dienst.dienst.R;
import tk.phili.dienst.dienst.calendar.CalendarFragment;
import tk.phili.dienst.dienst.dailytext.DailytextFragment;
import tk.phili.dienst.dienst.notes.NotesFragment;
import tk.phili.dienst.dienst.samplepresentations.SamplePresentationsFragment;
import tk.phili.dienst.dienst.settings.SettingsActivity;
import tk.phili.dienst.dienst.videos.VideoFragment;

/**
 * Created by fipsi on 24.07.2017.
 */

public class Drawer {

    public static DrawerTicker ticker;
    public static DrawerHeader header;
    public static PrimaryDrawerItem i1 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.title_section1).withIcon(R.drawable.ic_class_black_24dp).withIconTintingEnabled(true).withSelectedColor(Color.parseColor("#e7e7e7"));
    public static PrimaryDrawerItem i3 = new PrimaryDrawerItem().withIdentifier(3).withName(R.string.title_section3).withIcon(R.drawable.ic_description_black_24dp).withIconTintingEnabled(true).withSelectedColor(Color.parseColor("#e7e7e7"));
    public static PrimaryDrawerItem i4 = new PrimaryDrawerItem().withIdentifier(4).withName(R.string.title_section4).withIcon(R.drawable.ic_thumb_up_black_24dp).withIconTintingEnabled(true).withSelectedColor(Color.parseColor("#e7e7e7"));
    public static PrimaryDrawerItem i5 = new PrimaryDrawerItem().withIdentifier(5).withName(R.string.title_tt).withIcon(R.drawable.ic_baseline_event_available_24px).withIconTintingEnabled(true).withSelectedColor(Color.parseColor("#e7e7e7"));
    public static PrimaryDrawerItem i6 = new PrimaryDrawerItem().withIdentifier(6).withName(R.string.title_section7).withIcon(R.drawable.ic_video_library_black_24dp).withIconTintingEnabled(true).withSelectedColor(Color.parseColor("#e7e7e7"));
    public static PrimaryDrawerItem i7 = new PrimaryDrawerItem().withIdentifier(7).withName(R.string.title_section9).withIcon(R.drawable.ic_today_black_24dp).withIconTintingEnabled(true).withSelectedColor(Color.parseColor("#e7e7e7"));
    public static SecondaryDrawerItem i8 = new SecondaryDrawerItem().withIdentifier(8).withName(R.string.title_section5).withIcon(R.drawable.ic_settings_black_24dp).withIconTintingEnabled(true).withSelectedColor(Color.parseColor("#e7e7e7"));

    public static ArrayList<Article> articles = null;
    public static AccountHeader headerResult = null;

    public static com.mikepenz.materialdrawer.Drawer result;

    public static void addDrawer(final Activity c, Toolbar tb, final int item){
        final SharedPreferences sp = c.getSharedPreferences("MainActivity", c.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();

        ticker = new DrawerTicker(c);
        header = new DrawerHeader(c);

        ticker.setVisible(true);
        ticker.setText(sp.getString("LATEST_NEWS", c.getString(R.string.drawer_ticker_no_news)));

        Parser parser = null;
        String urlString = null;
        if(c.getString(R.string.URL_end).equalsIgnoreCase("de")){
            urlString = "https://www.jw.org/de/nachrichten/jw/rss/NewsSubsectionRSSFeed/feed.xml";
            ticker.setURL("https://www.jw.org/de/nachrichten/jw/");
        }else if(c.getString(R.string.URL_end).equalsIgnoreCase("it")){
            urlString = "https://www.jw.org/it/news/jw-news/rss/NewsSubsectionRSSFeed/feed.xml";
            ticker.setURL("https://www.jw.org/it/news/jw-news/");
        }else{
            urlString = "https://www.jw.org/en/news/jw/rss/NewsSubsectionRSSFeed/feed.xml";
            ticker.setURL("https://www.jw.org/en/news/jw/");
        }
        if(articles == null){

            parser = new Parser();
            parser.execute(urlString);
            parser.onFinish(new Parser.OnTaskCompleted() {

                @Override
                public void onTaskCompleted(ArrayList<Article> list) {
                    articles = list;
                    setRSSFeed(c, editor);
                }

                @Override
                public void onError() {
                    ticker.setVisible(true);
                    ticker.setText(sp.getString("LATEST_NEWS", c.getString(R.string.drawer_ticker_no_news)));
                }
            });
        }else{
            setRSSFeed(c, editor);
        }



    if(headerResult == null) {
        headerResult = new AccountHeaderBuilder()
                .withActivity(c)
                .withSelectionListEnabledForSingleProfile(false)
                .withCompactStyle(true)
                .withOnAccountHeaderListener((view, profile, current) -> false)
                .build();

    }

    IDrawerItem[] items = new IDrawerItem[]{header,ticker,
                    new DividerDrawerItem(),
                    i1,
                    i3,
                    i4,
                    i5,
                    i6,
                    i7,
                    new DividerDrawerItem(),
                    i8};

        final Parser parsercopy = parser;
//create the drawer and remember the `Drawer` result object
        result = new DrawerBuilder()
                .withActivity((AppCompatActivity)c)
                .withToolbar(tb)
                .addDrawerItems(
                        items
                )
                .withSelectedItem(item)
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    result.closeDrawer();
                    if(parsercopy != null){
                        parsercopy.cancel(true);
                    }
                    if(drawerItem.equals(header) || drawerItem.equals(ticker))
                        return false;

                    if(drawerItem.equals(i1)){
                        if(item == 1)return false;
                        //c.startActivity(new Intent(c.getApplicationContext(), ReportActivity.class));
                    }
                    if(drawerItem.equals(i3)){
                        if(item == 3)return false;
                        c.startActivity(new Intent(c.getApplicationContext(), NotesFragment.class));
                    }
                    if(drawerItem.equals(i4)){
                        if(item == 4)return false;
                        c.startActivity(new Intent(c.getApplicationContext(), SamplePresentationsFragment.class));
                    }
                    if(drawerItem.equals(i5)){
                        if(item == 5)return false;
                        c.startActivity(new Intent(c.getApplicationContext(), DailytextFragment.class));
                    }
                    if(drawerItem.equals(i6)){
                        if(item == 6)return false;
                        c.startActivity(new Intent(c.getApplicationContext(), VideoFragment.class));
                    }
                    if(drawerItem.equals(i7)){
                        if(item == 7)return false;
                        c.startActivity(new Intent(c.getApplicationContext(), CalendarFragment.class));
                    }
                    if(drawerItem.equals(i8)){
                        if(item == 8)return false;
                        c.startActivity(new Intent(c.getApplicationContext(), SettingsActivity.class));
                    }
                    hideKeyboard(c);
                    c.finish();
                    c.overridePendingTransition(0, 0);
                    return true;
                })
                .withDisplayBelowStatusBar(false)
                .withActionBarDrawerToggleAnimated(true)
                .build();

                result.getDrawerLayout().setStatusBarBackgroundColor(Color.parseColor("#1e1518"));
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

    public static void setRSSFeed(Context c, SharedPreferences.Editor editor){
        ticker.setVisible(true);
        String toset = "++"+c.getString(R.string.drawer_ticker_news)+"++ ";

        int i = 0;
        for(Article a : articles){
            i++;
            if(i == 3)
                break;

            toset += a.getTitle().replace("NEWS RELEASES | " , "").replace("PRESSEMITTEILUNGEN | ", "") + " ++ ";
        }

        toset = toset.substring(0, toset.length()-3) + " ++"+c.getString(R.string.drawer_ticker_news)+"++";

        editor.putString("LATEST_NEWS", toset);
        editor.commit();

        ticker.setText(toset);
    }


}
