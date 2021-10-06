package tk.phili.dienst.dienst;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.akexorcist.roundcornerprogressbar.IconRoundCornerProgressBar;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialog;
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;
import at.grabner.circleprogress.UnitPosition;

public class MainActivity extends AppCompatActivity {

    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;
    public static AlertDialog.Builder builder;

    private Toolbar toolbar;

    Set<String> lastlist = null;

    BerichtList bl;

    Calendar calendarShow;

    //CircleProgressView cpv;

    RoundCornerProgressBar rpb;

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

        mTitle = getTitle();

        if(sp.getBoolean("private_mode", false)){
            findViewById(R.id.private_block).setVisibility(View.VISIBLE);
            ((Button)findViewById(R.id.private_disable)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    findViewById(R.id.private_block).setVisibility(View.GONE);
                }
            });
        }


        rpb = findViewById(R.id.progress_goal);
        /*cpv = (CircleProgressView) findViewById(R.id.circle_goal);
        cpv.setTextMode(TextMode.PERCENT);
        cpv.setBarColor(Color.RED, Color.YELLOW, Color.GREEN);
        cpv.setRimColor(Color.WHITE);
        cpv.setOuterContourSize(0);
        cpv.setInnerContourSize(0);
        cpv.setAutoTextSize(true);
        cpv.setTextScale(0.7F);
        cpv.setUnitVisible(true);
        cpv.setUnit("%");
        cpv.setUnitToTextScale(1F);
        cpv.setUnitColor(Color.WHITE);
        cpv.setTextColor(Color.WHITE);
        cpv.setUnitPosition(UnitPosition.RIGHT_TOP);
        cpv.setValueAnimated(0F);
        cpv.setRimWidth(15);
        cpv.setBarWidth(15);*/


        final SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy");

        calendarShow = Calendar.getInstance();

        final TextView tbv = findViewById(R.id.toolbar_title);
        tbv.setText(dateFormat.format(calendarShow.getTime()));


        tbv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MonthYearPickerDialogFragment dialogFragment = MonthYearPickerDialogFragment.getInstance(calendarShow.get(Calendar.MONTH), calendarShow.get(Calendar.YEAR), getString(R.string.select_month_year));

                dialogFragment.setOnDateSetListener(new MonthYearPickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(int year, int monthOfYear) {

                        calendarShow.set(Calendar.MONTH, monthOfYear);
                        calendarShow.set(Calendar.YEAR, year);
                        tbv.setText(dateFormat.format(calendarShow.getTime()));
                        updateList();
                    }
                });

                dialogFragment.show(getSupportFragmentManager(), null);

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

        findViewById(R.id.swipe_up_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((TextView) findViewById(R.id.swipe_up_share)).getAlpha() != 0F){
                    ((SlidingUpPanelLayout)findViewById(R.id.sliding_layout)).setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);


                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

                    alert.setTitle(getString(R.string.title_section6));
                    alert.setMessage(getString(R.string.bericht_type_name));

                    //final EditText input = new EditText(MainActivity.this);
                    View input_view = LayoutInflater.from(MainActivity.this)
                            .inflate(R.layout.bericht_send_input, null, false);
                    final EditText input = ((TextInputLayout)input_view.findViewById(R.id.name_text_field)).getEditText();
                    alert.setView(input_view);
                    input.setText(sp.getString("lastSendName", ""));

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

        findViewById(R.id.swipe_up_carryover).setOnClickListener(new View.OnClickListener() {
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
                findViewById(R.id.swipe_up_text).setAlpha(1 - slideOffset);
                findViewById(R.id.swipe_up_lefticon).setRotation(slideOffset*180);
                findViewById(R.id.swipe_up_righticon).setAlpha(1 - slideOffset);
                findViewById(R.id.swipe_up_share).setAlpha(slideOffset);
                findViewById(R.id.swipe_up_carryover).setAlpha(slideOffset);

                if(slideOffset == 0){
                    findViewById(R.id.swipe_up_share).setVisibility(View.GONE);
                    findViewById(R.id.swipe_up_carryover).setVisibility(View.GONE);
                }else{
                    findViewById(R.id.swipe_up_share).setVisibility(View.VISIBLE);
                    findViewById(R.id.swipe_up_carryover).setVisibility(View.VISIBLE);
                }
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
        int m = calendarShow.get(Calendar.MONTH)+1;
        int y = calendarShow.get(Calendar.YEAR);
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
            if (year.equals(calendarShow.get(Calendar.YEAR) + "") && i == calendarShow.get(Calendar.MONTH)) {
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
                ArrayList<String> anmerkungen = new ArrayList<String>();
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
                    String anmerkung = "";
                    try {
                        anmerkung = s1[8];
                    }catch (Exception e){}

                    if(minuten.contains("-")){
                        continue;
                    }

                    //YEAR MONTH CHECK
                    String year = date.split(Pattern.quote("."))[2];
                    String month = date.split(Pattern.quote("."))[1];
                    String day = date.split(Pattern.quote("."))[0];
                    int i = Integer.parseInt(month);
                    i--;
                    if (year.equals(calendarShow.get(Calendar.YEAR) + "") && i == calendarShow.get(Calendar.MONTH)) {
                        ids.add(id);

                        DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
                        Date dateIN = format.parse(date);

                        DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT);
                        String dateOUT = dateFormatter.format(dateIN);

                        dates.add(dateOUT);
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
                        anmerkungen.add(anmerkung);
                    }
                }
                final String[] idset = ids.toArray(new String[ids.size()]);
                final String[] dateset = dates.toArray(new String[dates.size()]);
                String[] hoursset = hours.toArray(new String[hours.size()]);
                String[] abgabenset = abgaben.toArray(new String[abgaben.size()]);
                String[] rückset = rück.toArray(new String[rück.size()]);
                String[] videoset = videos.toArray(new String[videos.size()]);
                String[] studienset = studien.toArray(new String[studien.size()]);
                String[] anmerkungenset = anmerkungen.toArray(new String[anmerkungen.size()]);

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

                    bl = new BerichtList(this, idset, dateset, hoursset, abgabenset, rückset, videoset, studienset, anmerkungenset);
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
                                                anim.setInterpolator(android.view.animation.AnimationUtils.loadInterpolator(MainActivity.this, android.R.anim.accelerate_interpolator));
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

                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                findViewById(R.id.acmaincorners).setBackgroundResource(R.drawable.sliding_up_corners);
                                                            }
                                                        });
                                                    }
                                                }, 3500);

                                                Snackbar snackbar = Snackbar.make(findViewById(R.id.coord), R.string.bericht_undo_1, Snackbar.LENGTH_LONG).setAction(R.string.bericht_undo_2, new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        editor.putStringSet("BERICHTE", lastlist);
                                                        editor.commit();
                                                        editor.apply();
                                                        bl.notifyDataSetChanged();
                                                        updateList();
                                                        updateInsgesamt();
                                                    }
                                                });
                                                snackbar.show();

                                                findViewById(R.id.acmaincorners).setBackgroundColor(Color.WHITE);

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
                if (year.equals(calendarShow.get(Calendar.YEAR) + "") && i == calendarShow.get(Calendar.MONTH)) {
                    try { hours = hours + Integer.parseInt(stunden);
                    }catch(Exception e){e.printStackTrace();}
                    try { minutes = minutes + Integer.parseInt(minuten);
                    }catch(Exception e){e.printStackTrace();}
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
                float percent = (float)(((double)hours + ((double)minutes/60)) * 100 / (double)goal);
                //cpv.setValueAnimated(percent);
                rpb.setProgress(percent);
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
                }else if(goal - hours < 0){
                    tv.setText(getString(R.string.goal_text_reached_more).replace("%a", ""+Math.abs(goal - hours)));
                }
            }else{
                findViewById(R.id.goalview).setVisibility(View.GONE);
            }


            if(minutes > 0){
                if(hours > 0) {
                    ((TextView)findViewById(R.id.swipe_up_bericht).findViewById(R.id.bericht_stunden_count)).setText(hours + ":" + minutestring);
                    ((TextView)findViewById(R.id.swipe_up_bericht).findViewById(R.id.bericht_stunden_info)).setText(getString(R.string.title_activity_stunden));
                }else{
                    ((TextView)findViewById(R.id.swipe_up_bericht).findViewById(R.id.bericht_stunden_count)).setText(minutestring);
                    ((TextView)findViewById(R.id.swipe_up_bericht).findViewById(R.id.bericht_stunden_info)).setText(getString(R.string.minutes));
                }
            }else{
                ((TextView)findViewById(R.id.swipe_up_bericht).findViewById(R.id.bericht_stunden_count)).setText(hours+"");
                ((TextView)findViewById(R.id.swipe_up_bericht).findViewById(R.id.bericht_stunden_info)).setText(getString(R.string.title_activity_stunden));
            }
            ((TextView)findViewById(R.id.swipe_up_bericht).findViewById(R.id.bericht_brosch_count)).setText(abgaben+"");
            ((TextView)findViewById(R.id.swipe_up_bericht).findViewById(R.id.bericht_rueck_count)).setText(rück+"");
            ((TextView)findViewById(R.id.swipe_up_bericht).findViewById(R.id.bericht_videos_count)).setText(videos+"");
            ((TextView)findViewById(R.id.swipe_up_bericht).findViewById(R.id.bericht_studies_count)).setText(studien+"");
            ((TextView)findViewById(R.id.swipe_up_bericht).findViewById(R.id.bericht_date)).setText(getString(R.string.insgesamt));

            ((SlidingUpPanelLayout)findViewById(R.id.sliding_layout)).addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
                @Override
                public void onPanelSlide(View panel, float slideOffset) {
                    findViewById(R.id.swipe_up_text).setAlpha(1 - slideOffset);
                    findViewById(R.id.swipe_up_lefticon).setRotation(slideOffset*180);
                    findViewById(R.id.swipe_up_righticon).setAlpha(1 - slideOffset);
                    findViewById(R.id.swipe_up_share).setAlpha(slideOffset);
                    findViewById(R.id.swipe_up_carryover).setAlpha(slideOffset);

                }

                @Override
                public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

                }
            });
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

            View input_view = LayoutInflater.from(MainActivity.this)
                    .inflate(R.layout.goal_set_input, null, false);
            final EditText edt = ((TextInputLayout)input_view.findViewById(R.id.name_text_field)).getEditText();
            dialogBuilder.setView(input_view);

            //final View dialogView = inflater.inflate(R.layout.goal_popup, null);
            //dialogBuilder.setView(dialogView);

            //final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);

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
        editor.putString("lastSendName", name);
        editor.commit();
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
                    if (year.equals(calendarShow.get(Calendar.YEAR) + "") && i == calendarShow.get(Calendar.MONTH)) {
                        try {
                            hours = hours + Integer.parseInt(stunden);
                        }catch(Exception e){ e.printStackTrace(); }
                        try {
                            minutes = minutes + Integer.parseInt(minuten);
                        }catch(Exception e){ e.printStackTrace(); }
                        try {
                            abgaben = abgaben + Integer.parseInt(abgabenn);
                        }catch(Exception e){ e.printStackTrace(); }
                        try {
                            rück = rück + Integer.parseInt(rückbe);
                        }catch(Exception e){ e.printStackTrace(); }
                        try {
                            videos = videos + Integer.parseInt(vid);
                        }catch(Exception e){ e.printStackTrace(); }
                        try {
                            studien = studien + Integer.parseInt(studs);
                        }catch(Exception e){ e.printStackTrace(); }
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



                if(hours != 0 && minutes != 0){
                    hourstring = hours + ":" + minutestring;
                }else if(hours != 0 && minutes == 0){
                    hourstring =  hours+"";
                }else if(hours == 0 && minutes != 0){
                    hourstring =  minutestring + "min";
                }else{
                    hourstring =  hours+"";
                }


                /*if(minutes != 0){
                    if(hours != 0) {
                        hourstring = hours + ":" + minutestring;
                    }else{
                        hourstring =  minutestring + "min";
                    }
                }else{
                    hourstring =  hours+"";
                }*/
                abgabenstring = abgaben+"";
                rückstring = rück+"";
                videosstring = videos+"";
                studystring =  studien+"";


            }



            String text = getResources().getString(R.string.reportfor) + name + "\n"+ getResources().getString(R.string.reportmonth) + new DateFormatSymbols().getMonths()[calendarShow.get(Calendar.MONTH)] + "\n==============\n";
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
