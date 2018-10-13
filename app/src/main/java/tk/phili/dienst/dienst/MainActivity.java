package tk.phili.dienst.dienst;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.transition.Fade;
import android.support.transition.Transition;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;
import at.grabner.circleprogress.UnitPosition;

public class MainActivity extends AppCompatActivity {

    private boolean wannaup = true;

    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;
    public static AlertDialog.Builder builder;
    MenuItem bericht;

    private Toolbar toolbar;

    Set<String> lastlist = null;

    private ActionBarDrawerToggle actionbartoggle;

    private Spinner spinner;
    private Spinner spinneryear;

    public static String monthforsending = "";
    public static String monthselected = "";

    public static String yearforsending = "";
    public static String yearselected = "";

    private boolean is_add_hour_clicked = false;
    private boolean is_sub_hour_clicked = false;
    CircleProgressView cpv;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = this.getWindow();

        sp = getPreferences(Context.MODE_PRIVATE);
        editor = sp.edit();

        yearselected = "_" + getYear();
        yearforsending = getYear() + "";
        monthselected = "_" + getMonth();
        monthforsending = getMonth() + "";

        Set<String> allHashTags = new HashSet<String>();

        if (!sp.contains("SHORTCUTS")) {
            editor.putString("SHORTCUTS", "012");
            editor.commit();
            Shortcuts.updateShortcuts("012", this, true);
        }

        int layout = sp.getInt("report_layout", 0);
        ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
        if(layout == 0) {
            stub.setLayoutResource(R.layout.list_bericht);
        }else if(layout == 1) {
            stub.setLayoutResource(R.layout.list_bericht_tiny);
        }
        View inflated = stub.inflate();

        monthselected = "_" + getMonth();
        yearselected = "_" + getYear();
        mTitle = getTitle();

        //boolean updated = updateToNewSystem();

        if(sp.getBoolean("private_mode", false)){
            findViewById(R.id.private_block).setVisibility(View.VISIBLE);
            ((Button)findViewById(R.id.private_disable)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    findViewById(R.id.private_block).setVisibility(View.GONE);
                }
            });
        }

        cpv = (CircleProgressView) findViewById(R.id.circle_goal);
        cpv.setTextMode(TextMode.PERCENT);
        cpv.setBarColor(Color.YELLOW, Color.GREEN);
        cpv.setRimColor(Color.LTGRAY);
        cpv.setOuterContourSize(0);
        cpv.setInnerContourSize(0);
        cpv.setAutoTextSize(true);
        cpv.setTextScale(0.7F);
        cpv.setUnitVisible(true);
        cpv.setUnit("%");
        cpv.setUnitToTextScale(1F);
        cpv.setTextColorAuto(true);
        cpv.setUnitPosition(UnitPosition.RIGHT_TOP);
        cpv.setValueAnimated(0F);
        cpv.setRimWidth(15);
        cpv.setBarWidth(15);


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    if(true) {
        spinneryear = (Spinner) findViewById(R.id.spinner_nav_year);

// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.year_array, R.layout.spinner_main);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinneryear.setAdapter(adapter);


        spinneryear.setSelection(Math.abs(2016-Integer.parseInt(yearselected.substring(1))));

        spinneryear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                int yearint = 2016+position;
                yearselected = "_" + yearint;
                yearforsending = yearint + "";
                updateList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });

    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        spinner = (Spinner) findViewById(R.id.spinner_nav);

// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.months_array, R.layout.spinner_main);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);


        spinner.setSelection(getMonth());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                monthselected = "_" + position;
                monthforsending = position + "";
                updateList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });


        /////////////////DRAWER/////////////////////////////////////////
        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.bringToFront();

        ((SlidingUpPanelLayout)findViewById(R.id.sliding_layout)).setParallaxOffset(100);

        ((FloatingActionButton)findViewById(R.id.addBerichtButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainActivity.this, BerichtAddFrame.class);
                float x = findViewById(R.id.addBerichtButton).getX()+findViewById(R.id.addBerichtButton).getWidth()/2;
                float y = findViewById(R.id.addBerichtButton).getY()+findViewById(R.id.addBerichtButton).getHeight()/2;
                mainIntent.putExtra("xReveal",x);
                mainIntent.putExtra("yReveal",y);

                mainIntent.putExtra("id", Integer.MAX_VALUE);

                startActivity(mainIntent);
                //overridePendingTransition(0,0); // Maybe add again if didn't fix
            }
        });


        Animation anim = android.view.animation.AnimationUtils.loadAnimation(findViewById(R.id.addBerichtButton).getContext(),  R.anim.slide_in_bottom);
        anim.setDuration(1000L);
        findViewById(R.id.upswipy).startAnimation(anim);

        Drawer.addDrawer(this, toolbar, 1);

        ((Button)findViewById(R.id.swipe_up_share)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((TextView) findViewById(R.id.swipe_up_share)).getAlpha() != 0F){
                    ((SlidingUpPanelLayout)findViewById(R.id.sliding_layout)).setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

                    alert.setTitle(getString(R.string.title_section6));
                    alert.setMessage(getString(R.string.bericht_type_name));

                    final EditText input = new EditText(MainActivity.this);
                    alert.setView(input);

                    alert.setPositiveButton(getString(R.string.title_activity_senden), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String value = input.getText().toString();
                            if(!value.isEmpty()) {
                                sendReport(value);
                            }

                        }
                    });

                    alert.setNegativeButton(getString(R.string.gebiet_add_cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {     }
                    });

                    alert.show();
                }
            }
        });

        ((Button)findViewById(R.id.swipe_up_carryover)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((TextView) findViewById(R.id.swipe_up_carryover)).getAlpha() != 0F){
                    ((SlidingUpPanelLayout)findViewById(R.id.sliding_layout)).setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

                    alert.setTitle(getString(R.string.carryover));
                    alert.setMessage(getString(R.string.carryover_msg));

                    alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            carry();
                        }
                    });

                    alert.setNegativeButton(getString(R.string.delete_cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {     }
                    });

                    alert.show();
                }
            }
        });

        ((SlidingUpPanelLayout)findViewById(R.id.sliding_layout)).addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                ((TextView) findViewById(R.id.swipe_up_text)).setAlpha(1 - slideOffset);
                ((ImageView) findViewById(R.id.swipe_up_lefticon)).setRotation(slideOffset*180);
                ((ImageView) findViewById(R.id.swipe_up_righticon)).setAlpha(1 - slideOffset);
                ((TextView) findViewById(R.id.swipe_up_share)).setAlpha(slideOffset);
                ((TextView) findViewById(R.id.swipe_up_carryover)).setAlpha(slideOffset);
            }

            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

            }
        });

        updateList();

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
    }

    public void carry(){
        int m = Integer.parseInt(monthforsending) + 1;
        int y = Integer.parseInt(yearforsending);
        int id = 0;

        Set<String> berichteID = sp.getStringSet("BERICHTE", null);
        if(berichteID == null){
            Toast.makeText(this, getString(R.string.carryover_null), Toast.LENGTH_LONG).show();
            return;
        }
        for (String s : berichteID) {
            String inti = s.split(";")[0];
            Integer inte = Integer.parseInt(inti);
            if (id <= inte) {
                id = inte + 1;
            }
        }
        Set<String> berichte1 = sp.getStringSet("BERICHTE", null);
        ArrayList<String> berichtem = sortByDate(berichte1);
        int hours = 0;
        int minutes = 0;
        int abgaben = 0;
        int rück = 0;
        int videos = 0;
        int studien = 0;
        for (String s : berichtem) {
            String[] s1 = s.split(";");
            String date = s1[1];
            String stunden = s1[2];
            String minuten = s1[3];
            String abgabenn = s1[4];
            String rückbe = s1[5];
            String vid = s1[6];
            String studs = s1[7];

            //YEAR MONTH CHECK
            String year = date.split(Pattern.quote("."))[2];
            String month = date.split(Pattern.quote("."))[1];
            int i = Integer.parseInt(month);
            i--;
            if (year.equals(yearforsending + "") && i == Integer.parseInt(monthforsending)) {
                hours = hours + Integer.parseInt(stunden);
                minutes = minutes + Integer.parseInt(minuten);
                abgaben = abgaben + Integer.parseInt(abgabenn);
                rück = rück + Integer.parseInt(rückbe);
                videos = videos + Integer.parseInt(vid);
                studien = studien + Integer.parseInt(studs);
            }
        }

        if(minutes >= 60){
            int times = minutes / 60;
            hours = hours + times;
            int minutesub = times*60;
            minutes = minutes - minutesub;
        }

        Set<String> berichte = new HashSet<>();
        if(sp.contains("BERICHTE")) {
            Set<String> berichteold = sp.getStringSet("BERICHTE", null);
            if(berichteold != null && !(berichteold.isEmpty())){
                for(String s : berichteold){
                    berichte.add(s);
                }
            }
        }

        String toset = id+";32."+m+"."+y+";"+0+";"+(-minutes)+";"+0+";"+0+";"+0+";"+0;
        berichte.add(toset);

        m++;
        if(m == 13){
            m = 1;
            y++;
        }

        String toset2 = id+";0."+m+"."+y+";"+0+";"+(minutes)+";"+0+";"+0+";"+0+";"+0;
        berichte.add(toset2);
        editor.putStringSet("BERICHTE", berichte);
        editor.commit();

        updateList();
    }


    public ArrayList<String> sortByDate(Set<String> list){
        ArrayList<String> listsorted = new ArrayList<String>();
        int day = 0;
        while (day <= 32) {
            for (String s : list) {
                String day_s = s.split(";")[1].split(Pattern.quote("."))[0];
                Integer int_day_s = Integer.parseInt(day_s);
                if (int_day_s == day) {
                    listsorted.add(s);
                }
            }
            day++;
        }
        return listsorted;
    }


    public void updateList(){
        try {
            if (sp.contains("BERICHTE")) {
                Set<String> berichte1 = sp.getStringSet("BERICHTE", null);
                ArrayList<String> berichte = sortByDate(berichte1);
                ArrayList<String> ids = new ArrayList<String>();
                ArrayList<String> dates = new ArrayList<String>();
                ArrayList<String> hours = new ArrayList<String>();
                ArrayList<String> abgaben = new ArrayList<String>();
                ArrayList<String> rück = new ArrayList<String>();
                ArrayList<String> videos = new ArrayList<String>();
                ArrayList<String> studien = new ArrayList<String>();
                for (String s : berichte) {
                    String[] s1 = s.split(";");
                    String id = s1[0];
                    String date = s1[1];
                    String stunden = s1[2];
                    String minuten = s1[3];
                    String abgabenn = s1[4];
                    String rückbe = s1[5];
                    String vid = s1[6];
                    String studs = s1[7];

                    //YEAR MONTH CHECK
                    String year = date.split(Pattern.quote("."))[2];
                    String month = date.split(Pattern.quote("."))[1];
                    int i = Integer.parseInt(month);
                    i--;
                    if (year.equals(yearforsending + "") && i == Integer.parseInt(monthforsending)) {
                        ids.add(id);
                        dates.add(date);
                        if (Integer.parseInt(minuten) != 0) {
                            if (Integer.parseInt(stunden) != 0) {
                                hours.add(stunden + ":" + minuten);
                            } else {
                                hours.add(minuten + "min");
                            }
                        } else {
                            hours.add(stunden);
                        }
                        abgaben.add(abgabenn);
                        rück.add(rückbe);
                        videos.add(vid);
                        studien.add(studs);
                    }
                }
                final String[] idset = ids.toArray(new String[ids.size()]);
                final String[] dateset = dates.toArray(new String[dates.size()]);
                String[] hoursset = hours.toArray(new String[hours.size()]);
                String[] abgabenset = abgaben.toArray(new String[abgaben.size()]);
                String[] rückset = rück.toArray(new String[rück.size()]);
                String[] videoset = videos.toArray(new String[videos.size()]);
                String[] studienset = studien.toArray(new String[studien.size()]);

                if (ids.isEmpty()) {
                    if (findViewById(R.id.no_bericht_img) != null) {
                        findViewById(R.id.no_bericht_img).setVisibility(View.VISIBLE);
                        findViewById(R.id.no_bericht_text).setVisibility(View.VISIBLE);
                    }
                } else {
                    if (findViewById(R.id.no_bericht_img) != null) {
                        findViewById(R.id.no_bericht_img).setVisibility(View.GONE);
                        findViewById(R.id.no_bericht_text).setVisibility(View.GONE);
                    }
                }

                final BerichtList bl = new BerichtList(this, idset, dateset, hoursset, abgabenset, rückset, videoset, studienset);
                ((ListView) findViewById(R.id.bericht_liste)).setAdapter(bl);
                updateInsgesamt();

                ((ListView) findViewById(R.id.bericht_liste)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String date = dateset[i];
                        if(!date.startsWith("32.") && !date.startsWith("0.")) {
                            Intent mainIntent = new Intent(MainActivity.this, BerichtAddFrame.class);
                            float x = view.getX() + view.getWidth() / 2;
                            float y = view.getY() + view.getHeight() / 2;
                            mainIntent.putExtra("xReveal", x);
                            mainIntent.putExtra("yReveal", y);

                            mainIntent.putExtra("id", Integer.parseInt(idset[i]));
                            MainActivity.this.startActivity(mainIntent);
                        }
                    }
                });


                SwipeDismissListViewTouchListener touchListener =
                        new SwipeDismissListViewTouchListener(
                                ((ListView) findViewById(R.id.bericht_liste)),
                                new SwipeDismissListViewTouchListener.DismissCallbacks() {
                                    @Override
                                    public boolean canDismiss(int position) {
                                        return true;
                                    }

                                    @Override
                                    public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                        for (int position : reverseSortedPositions) {

                                            //your_arraylist.remove(position);
                                            Set<String> berichte = new HashSet<>();
                                            if (sp.contains("BERICHTE")) {
                                                Set<String> berichteold = sp.getStringSet("BERICHTE", null);
                                                Animation anim = android.view.animation.AnimationUtils.loadAnimation(findViewById(R.id.addBerichtButton).getContext(),  android.R.anim.slide_out_right);
                                                anim.setDuration(200L);
                                                anim.setAnimationListener(new Animation.AnimationListener() {
                                                    @Override
                                                    public void onAnimationStart(Animation animation) { }

                                                    @Override
                                                    public void onAnimationEnd(Animation animation) {
                                                        findViewById(R.id.addBerichtButton).setVisibility(View.INVISIBLE);
                                                    }

                                                    @Override
                                                    public void onAnimationRepeat(Animation animation) { }
                                                });
                                                findViewById(R.id.addBerichtButton).startAnimation(anim);

                                                Timer t = new Timer();
                                                t.schedule(new TimerTask() {
                                                    @Override
                                                    public void run() {
                                                        if(findViewById(R.id.addBerichtButton) != null){
                                                            final Animation anim = android.view.animation.AnimationUtils.loadAnimation(findViewById(R.id.addBerichtButton).getContext(),  android.R.anim.slide_out_right);
                                                            anim.setDuration(200L);
                                                            anim.setAnimationListener(new Animation.AnimationListener() {
                                                                @Override
                                                                public void onAnimationStart(Animation animation) { }

                                                                @Override
                                                                public void onAnimationEnd(Animation animation) {
                                                                    findViewById(R.id.addBerichtButton).setVisibility(View.VISIBLE);
                                                                }

                                                                @Override
                                                                public void onAnimationRepeat(Animation animation) { }
                                                            });
                                                            anim.setInterpolator(new ReverseInterpolator());

                                                            MainActivity.this.runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    findViewById(R.id.addBerichtButton).startAnimation(anim);
                                                                }
                                                            });

                                                        }
                                                    }
                                                }, 3500);

                                                Snackbar.make(findViewById(R.id.coord), R.string.bericht_undo_1, Snackbar.LENGTH_LONG).setAction(R.string.bericht_undo_2, new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        editor.putStringSet("BERICHTE", lastlist);
                                                        editor.commit();
                                                        editor.apply();
                                                        bl.notifyDataSetChanged();
                                                        updateList();
                                                        updateInsgesamt();
                                                    }
                                                }).show();
                                                if (berichteold != null && !(berichteold.isEmpty())) {
                                                    lastlist = berichteold;
                                                    for (String s : berichteold) {
                                                        if (!s.startsWith(bl.getIdofPosition(position))) {
                                                            berichte.add(s);
                                                        }
                                                    }
                                                }
                                            }
                                            editor.putStringSet("BERICHTE", berichte);
                                            editor.commit();
                                            editor.apply();
                                            bl.notifyDataSetChanged();
                                            updateList();
                                            updateInsgesamt();

                                        }


                                    }
                                });

                ((ListView) findViewById(R.id.bericht_liste)).setOnTouchListener(touchListener);
                ((ListView) findViewById(R.id.bericht_liste)).setOnScrollListener(touchListener.makeScrollListener());

            } else {
                if (findViewById(R.id.no_bericht_img) != null) {
                    findViewById(R.id.no_bericht_img).setVisibility(View.VISIBLE);
                    findViewById(R.id.no_bericht_text).setVisibility(View.VISIBLE);
                }
                if(findViewById(R.id.goalview) != null) {
                    findViewById(R.id.goalview).setVisibility(View.GONE);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.error)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setCancelable(false)
                    .setMessage(R.string.bericht_error)
                    .show();
        }
    }

    public class ReverseInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float paramFloat) {
            return Math.abs(paramFloat -1f);
        }
    }

    public void updateInsgesamt(){
        if(sp.contains("BERICHTE")) {
            Set<String> berichte1 = sp.getStringSet("BERICHTE", null);
            ArrayList<String> berichte = sortByDate(berichte1);
            int hours = 0;
            int minutes = 0;
            int abgaben = 0;
            int rück = 0;
            int videos = 0;
            int studien = 0;
            for (String s : berichte) {
                String[] s1 = s.split(";");
                String date = s1[1];
                String stunden = s1[2];
                String minuten = s1[3];
                String abgabenn = s1[4];
                String rückbe = s1[5];
                String vid = s1[6];
                String studs = s1[7];

                //YEAR MONTH CHECK
                String year = date.split(Pattern.quote("."))[2];
                String month = date.split(Pattern.quote("."))[1];
                int i = Integer.parseInt(month);
                i--;
                if (year.equals(yearforsending + "") && i == Integer.parseInt(monthforsending)) {
                    hours = hours + Integer.parseInt(stunden);
                    minutes = minutes + Integer.parseInt(minuten);
                    abgaben = abgaben + Integer.parseInt(abgabenn);
                    rück = rück + Integer.parseInt(rückbe);
                    videos = videos + Integer.parseInt(vid);
                    studien = studien + Integer.parseInt(studs);
                }
            }


            if(minutes >= 60){
                int times = minutes / 60;
                hours = hours + times;
                int minutesub = times*60;
                minutes = minutes - minutesub;
            }

            String minutestring = "";
            if(minutes < 10){
                minutestring = "0"+minutes;
            }else{
                minutestring = ""+minutes;
            }

            if(sp.contains("goal") && !sp.getString("goal", "0").equals("0")){
                findViewById(R.id.goalview).setVisibility(View.VISIBLE);
                int goal = Integer.parseInt(sp.getString("goal", "0"));
                float percent = hours * 100 / goal;
                cpv.setValueAnimated(percent);
                TextView tv = (TextView) findViewById(R.id.goaltext);
                if(goal - hours == 0){
                    tv.setText(getString(R.string.goal_text_reached));
                }
                if(goal - hours > 0){
                    if(goal - hours == 1) {
                        tv.setText(getString(R.string.goal_text_1).replace("%a", ""+(goal - hours)));
                    }else{
                        tv.setText(getString(R.string.goal_text_mult).replace("%a", ""+(goal - hours)));
                    }
                }
                if(goal - hours < 0){
                    tv.setText(getString(R.string.goal_text_reached_more).replace("%a", ""+Math.abs(goal - hours)));
                }
            }else{
                findViewById(R.id.goalview).setVisibility(View.GONE);
            }


            if(minutes != 0){
                if(hours != 0) {
                    ((TextView)findViewById(R.id.swipe_up_bericht).findViewById(R.id.bericht_stunden_count)).setText(hours + ":" + minutestring);
                }else{
                    ((TextView)findViewById(R.id.swipe_up_bericht).findViewById(R.id.bericht_stunden_count)).setText(minutestring);
                    ((TextView)findViewById(R.id.swipe_up_bericht).findViewById(R.id.bericht_stunden_info)).setText(getString(R.string.minutes));
                }
            }else{
                ((TextView)findViewById(R.id.swipe_up_bericht).findViewById(R.id.bericht_stunden_count)).setText(hours+"");
            }
            ((TextView)findViewById(R.id.swipe_up_bericht).findViewById(R.id.bericht_brosch_count)).setText(abgaben+"");
            ((TextView)findViewById(R.id.swipe_up_bericht).findViewById(R.id.bericht_rueck_count)).setText(rück+"");
            ((TextView)findViewById(R.id.swipe_up_bericht).findViewById(R.id.bericht_videos_count)).setText(videos+"");
            ((TextView)findViewById(R.id.swipe_up_bericht).findViewById(R.id.bericht_studies_count)).setText(studien+"");
            ((TextView)findViewById(R.id.swipe_up_bericht).findViewById(R.id.bericht_date)).setText(getString(R.string.insgesamt));

            ((SlidingUpPanelLayout)findViewById(R.id.sliding_layout)).addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
                @Override
                public void onPanelSlide(View panel, float slideOffset) {
                    ((TextView) findViewById(R.id.swipe_up_text)).setAlpha(1 - slideOffset);
                    ((ImageView) findViewById(R.id.swipe_up_lefticon)).setRotation(slideOffset*180);
                    ((ImageView) findViewById(R.id.swipe_up_righticon)).setAlpha(1 - slideOffset);
                    ((TextView) findViewById(R.id.swipe_up_share)).setAlpha(slideOffset);

                }

                @Override
                public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

                }
            });
        }


    }




    public boolean isConnectedtoNet(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if((connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED) ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        }else {
            return false;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuTintUtils.tintAllIcons(menu, Color.WHITE);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_goal) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.goal_popup, null);
            dialogBuilder.setView(dialogView);

            final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);

            if(sp.contains("goal") && !sp.getString("goal", "0").equals("0")){
                edt.setText(sp.getString("goal", "0"));
            }

            dialogBuilder.setTitle(getString(R.string.goal_set));
            dialogBuilder.setMessage(getString(R.string.goal_msg));
            dialogBuilder.setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    try{
                        Integer.parseInt(edt.getText().toString());
                    }catch(Exception e){
                        Toast.makeText(MainActivity.this, getString(R.string.goal_invalid), Toast.LENGTH_LONG).show();
                        return;
                    }
                    editor.putString("goal", edt.getText().toString());
                    editor.commit();
                    updateInsgesamt();
                }
            });
            dialogBuilder.setNegativeButton(getString(R.string.goal_no), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    editor.putString("goal", "0");
                    editor.commit();
                    updateInsgesamt();
                }
            });
            AlertDialog b = dialogBuilder.create();
            b.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void sendReport(String name){
        if(!name.toString().matches("")) {
            String hourstring = "0";
            String abgabenstring = "0";
            String rückstring = "0";
            String videosstring = "0";
            String studystring = "0";

            if(sp.contains("BERICHTE")) {
                Set<String> berichte1 = sp.getStringSet("BERICHTE", null);
                int hours = 0;
                int minutes = 0;
                int abgaben = 0;
                int rück = 0;
                int videos = 0;
                int studien = 0;
                for (String s : berichte1) {
                    String[] s1 = s.split(";");
                    String date = s1[1];
                    String stunden = s1[2];
                    String minuten = s1[3];
                    String abgabenn = s1[4];
                    String rückbe = s1[5];
                    String vid = s1[6];
                    String studs = s1[7];

                    //YEAR MONTH CHECK
                    String year = date.split(Pattern.quote("."))[2];
                    String month = date.split(Pattern.quote("."))[1];
                    int i = Integer.parseInt(month);
                    i--;
                    if (year.equals(MainActivity.yearforsending + "") && i == Integer.parseInt(MainActivity.monthforsending)) {
                        hours = hours + Integer.parseInt(stunden);
                        minutes = minutes + Integer.parseInt(minuten);
                        abgaben = abgaben + Integer.parseInt(abgabenn);
                        rück = rück + Integer.parseInt(rückbe);
                        videos = videos + Integer.parseInt(vid);
                        studien = studien + Integer.parseInt(studs);
                    }
                }

                if(minutes >= 60){
                    int times = minutes / 60;
                    hours = hours + times;
                    int minutesub = times*60;
                    minutes = minutes - minutesub;
                }

                String minutestring = "";
                if(minutes < 10){
                    minutestring = "0"+minutes;
                }else{
                    minutestring = ""+minutes;
                }






                if(minutes != 0){
                    if(hours != 0) {
                        hourstring = hours + ":" + minutestring;
                    }else{
                        hourstring =  minutestring + "min";
                    }
                }else{
                    hourstring =  hours+"";
                }
                abgabenstring = abgaben+"";
                rückstring = rück+"";
                videosstring = videos+"";
                studystring =  studien+"";

            }




            String text = getResources().getString(R.string.reportfor) + name + "\n"+ getResources().getString(R.string.reportmonth) + getMonthnametoID(Integer.parseInt(MainActivity.monthforsending)) + "\n==============\n";
            if(!hourstring.endsWith("min")) {
                text = text + getResources().getString(R.string.reporthours) + hourstring + "\n";
            }else{
                text = text + getResources().getString(R.string.reportminutes) + hourstring.replace("min", "") + "\n";
            }
            text = text + getResources().getString(R.string.reportplace) + abgabenstring + "\n";
            text = text + getResources().getString(R.string.reportvisits) + rückstring + "\n";
            text = text + getResources().getString(R.string.reportvideo) + videosstring + "\n";
            text = text + getResources().getString(R.string.reportstudy) + studystring + "\n";
            text = text + "==============\n" + getResources().getString(R.string.reportsentvia);
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, text);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, R.string.sendreport_text + ""));
        }
    }

    public String getMonthnametoID(int MonthID){
        switch (MonthID){
            case 0:
                return getResources().getString(R.string.monat1);

            case 1:
                return getResources().getString(R.string.monat2);

            case 2:
                return getResources().getString(R.string.monat3);

            case 3:
                return getResources().getString(R.string.monat4);

            case 4:
                return getResources().getString(R.string.monat5);

            case 5:
                return getResources().getString(R.string.monat6);

            case 6:
                return getResources().getString(R.string.monat7);

            case 7:
                return getResources().getString(R.string.monat8);

            case 8:
                return getResources().getString(R.string.monat9);

            case 9:
                return getResources().getString(R.string.monat10);

            case 10:
                return getResources().getString(R.string.monat11);

            case 11:
                return getResources().getString(R.string.monat12);
        }
        return "ERROR";

    }





    private boolean is1 = true;
    private boolean is2 = false;
    private boolean is3 = false;
    public void onSectionAttached(int number) {

    }

    public int getDay(){
        Calendar calendar = Calendar.getInstance();
        int thisMonth = calendar.get(Calendar.DAY_OF_MONTH);
        return thisMonth;
    }

    public int getMonth(){
        Calendar calendar = Calendar.getInstance();
        int thisMonth = calendar.get(Calendar.MONTH);
        return thisMonth;
    }

    public int getYear(){
        Calendar calendar = Calendar.getInstance();
        int thisYear = calendar.get(Calendar.YEAR);
       return thisYear;
    }


    public void openWebURL( String inURL ) {
        Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse(inURL) ); startActivity( browse ); }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }





}
