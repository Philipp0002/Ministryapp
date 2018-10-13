package tk.phili.dienst.dienst;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.koushikdutta.async.future.FutureCallback;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.squareup.picasso.Picasso;
import com.prof.rssparser.Article;
import com.prof.rssparser.Parser;

import java.util.ArrayList;

/**
 * Created by fipsi on 24.07.2017.
 */

public class Drawer {

    public static Bitmap bmp = null;
    public static DrawerTicker ticker;
    public static PrimaryDrawerItem i1 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.title_section1).withIcon(R.drawable.ic_class_black_24dp).withIconTintingEnabled(true);
    public static PrimaryDrawerItem i2 = new PrimaryDrawerItem().withIdentifier(2).withName(R.string.title_section8).withIcon(R.drawable.ic_map_black_24dp).withIconTintingEnabled(true);
    public static PrimaryDrawerItem i3 = new PrimaryDrawerItem().withIdentifier(3).withName(R.string.title_section3).withIcon(R.drawable.ic_description_black_24dp).withIconTintingEnabled(true);
    public static PrimaryDrawerItem i4 = new PrimaryDrawerItem().withIdentifier(4).withName(R.string.title_section4).withIcon(R.drawable.ic_thumb_up_black_24dp).withIconTintingEnabled(true);
    public static PrimaryDrawerItem i5 = new PrimaryDrawerItem().withIdentifier(5).withName(R.string.title_section7).withIcon(R.drawable.ic_video_library_black_24dp).withIconTintingEnabled(true);
    public static PrimaryDrawerItem i6 = new PrimaryDrawerItem().withIdentifier(6).withName(R.string.title_section9).withIcon(R.drawable.ic_today_black_24dp).withIconTintingEnabled(true);
    public static SecondaryDrawerItem i7 = new SecondaryDrawerItem().withIdentifier(7).withName(R.string.title_section5).withIcon(R.drawable.ic_settings_black_24dp).withIconTintingEnabled(true);

    public static ArrayList<Article> articles = null;
    public static AccountHeader headerResult = null;

    public static com.mikepenz.materialdrawer.Drawer result;

    public static void addDrawer(final Activity c, Toolbar tb, final int item){
        final SharedPreferences sp = c.getSharedPreferences("MainActivity", c.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();




        ticker = new DrawerTicker(c);

        ticker.setVisible(true);
        ticker.setText(sp.getString("LATEST_NEWS", c.getString(R.string.drawer_ticker_no_news)));

        Parser parser = null;
        if(articles == null){
            String urlString = null;
            if(c.getString(R.string.URL_end).equalsIgnoreCase("de")){
                urlString = "https://www.jw.org/de/aktuelle-meldungen/jw/rss/NewsSubsectionRSSFeed/feed";
                ticker.setURL("https://www.jw.org/de/aktuelle-meldungen/jw/");
            }else{
                ticker.setURL("https://www.jw.org/en/news/jw/");
                urlString = "https://www.jw.org/en/news/jw/rss/NewsSubsectionRSSFeed/feed";
            }
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
                .withHeaderBackground(R.drawable.headereng)
                .withSelectionListEnabledForSingleProfile(false)
               /* .addProfiles(
                        new ProfileDrawerItem().withName(" ").withEmail(sp.getString("motto", "ERR Code 12")).withIcon(c.getResources().getDrawable(R.drawable.jworg))
                )*/
                .withCompactStyle(true)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        return false;
                    }
                })
                .build();

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            if (bmp == null) {
                if (c.getString(R.string.URL_end).contains("de")) {
                    bmp = BitmapFactory.decodeResource(c.getResources(), R.drawable.headerdeu);
                } else {
                    bmp = BitmapFactory.decodeResource(c.getResources(), R.drawable.headereng);
                }
            }

            try {
                int statusBarHeight = getStatusBarHeight(c);
                Bitmap finalbmp = Bitmap.createBitmap(bmp, 0, statusBarHeight * 5, bmp.getWidth(), bmp.getHeight() - statusBarHeight * 5);
                headerResult.getHeaderBackgroundView().setImageBitmap(finalbmp);
            } catch (Exception e) {
                headerResult.getHeaderBackgroundView().setImageBitmap(bmp);
            }

        } else {
            if (c.getString(R.string.URL_end).contains("de")) {
                Picasso.with(headerResult.getHeaderBackgroundView().getContext()).load(R.drawable.headerdeu).into(headerResult.getHeaderBackgroundView());
            } else {
                Picasso.with(headerResult.getHeaderBackgroundView().getContext()).load(R.drawable.headereng).into(headerResult.getHeaderBackgroundView());
            }
        }
    }

        final Parser parsercopy = parser;
//create the drawer and remember the `Drawer` result object
        result = new DrawerBuilder()
                .withActivity((AppCompatActivity)c)
                .withToolbar(tb)
                .addDrawerItems(
                        ticker,
                        new DividerDrawerItem(),
                        i1,
                        i2,
                        i3,
                        i4,
                        i5,
                        i6,
                        new DividerDrawerItem(),
                        i7

                )
                .withSelectedItem(item)
                .withOnDrawerItemClickListener(new com.mikepenz.materialdrawer.Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        result.closeDrawer();
                        if(parsercopy != null){
                            parsercopy.cancel(true);
                        }
                        if(drawerItem.equals(i1)){
                            if(item == 1)return false;
                            c.startActivity(new Intent(c.getApplicationContext(), MainActivity.class));
                            ((AppCompatActivity)c).finish();
                        }
                        if(drawerItem.equals(i2)){
                            if(item == 2)return false;
                            c.startActivity(new Intent(c.getApplicationContext(), Gebiete.class));
                            ((AppCompatActivity)c).finish();
                        }
                        if(drawerItem.equals(i3)){
                            if(item == 3)return false;
                            c.startActivity(new Intent(c.getApplicationContext(), Notizen.class));
                            ((AppCompatActivity)c).finish();
                        }
                        if(drawerItem.equals(i4)){
                            if(item == 4)return false;
                            c.startActivity(new Intent(c.getApplicationContext(), Empfehlungen.class));
                            ((AppCompatActivity)c).finish();
                        }
                        if(drawerItem.equals(i5)){
                            if(item == 5)return false;
                            c.startActivity(new Intent(c.getApplicationContext(), VideoNew.class));
                            ((AppCompatActivity)c).finish();
                        }
                        if(drawerItem.equals(i6)){
                            if(item == 6)return false;
                            c.startActivity(new Intent(c.getApplicationContext(), Kalender.class));
                            ((AppCompatActivity)c).finish();
                        }
                        if(drawerItem.equals(i7)){
                            if(item == 7)return false;
                            c.startActivity(new Intent(c.getApplicationContext(), Settings.class));
                            ((AppCompatActivity)c).finish();
                        }
                        ((AppCompatActivity)c).overridePendingTransition(0, 0);
                        return true;
                    }
                })
                .withAccountHeader(headerResult)
                .withActionBarDrawerToggleAnimated(true)
                .build();





    }

    public static int getStatusBarHeight(Context c) {
        int height;

        Resources myResources = c.getResources();
        int idStatusBarHeight = myResources.getIdentifier(
                "status_bar_height", "dimen", "android");
        if (idStatusBarHeight > 0) {
            height = c.getResources().getDimensionPixelSize(idStatusBarHeight);
        }else{
            height = 0;
        }
        return height;
    }

    static class WebStringGetter extends AsyncTask<String, Void, String> {

        private Exception exception;
        public FutureCallback<String> fc;

        protected String doInBackground(String... urls) {
            return HttpUtils.getUrlAsString(urls[0]);//getContents(urls[0]);
        }

        protected void onPostExecute(String feed) {
            fc.onCompleted(new Exception(),feed);
        }
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
