package tk.phili.dienst.dienst.calendar;

import android.Manifest;
import android.animation.Animator;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import android.provider.CalendarContract;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.appbar.AppBarLayout;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import io.codetail.animation.ViewAnimationUtils;
import tk.phili.dienst.dienst.R;
import tk.phili.dienst.dienst.utils.MenuTintUtils;

public class KalenderAddFrame extends AppCompatActivity {

    Calendar myCalendar = null;
    int id;
    public SharedPreferences sp;
    private SharedPreferences.Editor editor;

    boolean collapsed = false;

    //FORMAT
    //IDʷDAYʷMONTHʷYEARʷHOURʷMINUTEʷDIENSTPARTNERʷBESCHREIBUNG

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kalender_add_frame);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sp = getSharedPreferences("MainActivity", MODE_PRIVATE);
        editor = sp.edit();

        int day = getIntent().getIntExtra("day", 1);
        int month = getIntent().getIntExtra("month", 1);
        int year = getIntent().getIntExtra("year", 1);
        final int hour = getIntent().getIntExtra("hour", 0);
        int minute = getIntent().getIntExtra("minute", 0);
        id = getIntent().getIntExtra("id", 0);

        String partner = getIntent().getStringExtra("partner");
        String notes = getIntent().getStringExtra("notes");

        if(partner!=null){
            if(!partner.trim().isEmpty()){
                ((EditText)findViewById(R.id.add_calendar_partner)).setText(partner);
            }
        }
        if(notes!=null){
            if(!notes.trim().isEmpty()){
                ((EditText)findViewById(R.id.add_calendar_notes)).setText(notes);
            }
        }

        myCalendar = new GregorianCalendar(year, month, day, hour, minute);
        Date newDate = new Date(myCalendar.getTimeInMillis());
        String s = java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.SHORT, java.text.DateFormat.SHORT).format(newDate);
        ((EditText)findViewById(R.id.add_calendar_date)).setText(s);

        final TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour_of_day, int min) {
                myCalendar.set(Calendar.HOUR_OF_DAY, hour_of_day);
                myCalendar.set(Calendar.MINUTE, min);
                Date newDate = new Date(myCalendar.getTimeInMillis());
                String s = java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.SHORT, java.text.DateFormat.SHORT).format(newDate);
                ((TextView)findViewById(R.id.add_calendar_date)).setText(s);

                findViewById(R.id.add_calendar_partner).requestFocus();
            }
        };
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                /*MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                        .setTimeFormat(DateFormat.is24HourFormat(KalenderAddFrame.this) ? TimeFormat.CLOCK_24H : TimeFormat.CLOCK_12H)
                        .setHour(myCalendar.get(Calendar.HOUR_OF_DAY))
                        .setMinute(myCalendar.get(Calendar.MINUTE))
                        .build();

                materialTimePicker.show(getSupportFragmentManager(), "time_picker");*/

                new TimePickerDialog(KalenderAddFrame.this, time, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(KalenderAddFrame.this))
                    .show();

                findViewById(R.id.add_calendar_partner).requestFocus();
            }
        };


        findViewById(R.id.add_calendar_date).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View v, boolean hasFocus) {
                if(ViewCompat.isAttachedToWindow(v)) {
                    if (hasFocus) {
                        DatePickerDialog dpd = new DatePickerDialog(KalenderAddFrame.this, date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH));
                        dpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                findViewById(R.id.add_calendar_partner).requestFocus();
                            }
                        });
                        dpd.show();
                    }
                }
            }
        });


        final View view = findViewById(R.id.revealLayoutKal);
        view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);
                if(getIntent().hasExtra("xReveal") && getIntent().hasExtra("yReveal")){
                    View myView = findViewById(R.id.revealLayoutKal);

                    // get the center for the clipping circle
                    int cx = (int)getIntent().getFloatExtra("xReveal", 0);
                    int cy = (int)getIntent().getFloatExtra("yReveal", 0);

                    // get the final radius for the clipping circle
                    int dx = myView.getWidth();
                    int dy = myView.getHeight();
                    float finalRadius = (float) Math.hypot(dx, dy);

                    // Android native animator
                    Animator animator = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);
                    animator.setInterpolator(new AccelerateDecelerateInterpolator());
                    animator.setDuration(500);
                    animator.start();
                }
            }
        });

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        ((AppBarLayout)findViewById(R.id.app_bar)).addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (Math.abs(verticalOffset)-appBarLayout.getTotalScrollRange() == 0) {
                    collapsed = true;
                    supportInvalidateOptionsMenu();
                }
                else {
                    collapsed = false;
                    supportInvalidateOptionsMenu();
                }
            }
        });
    }

    public void save(){
        Set<String> set = sp.getStringSet("Calendar", new HashSet<String>());
        int day = myCalendar.get(Calendar.DAY_OF_MONTH);
        int month = myCalendar.get(Calendar.MONTH);
        int year = myCalendar.get(Calendar.YEAR);
        int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = myCalendar.get(Calendar.MINUTE);
        String dienstpartner = ((EditText)findViewById(R.id.add_calendar_partner)).getText().toString().isEmpty() ? " " : ((EditText)findViewById(R.id.add_calendar_partner)).getText().toString();
        String beschreibung = ((EditText)findViewById(R.id.add_calendar_notes)).getText().toString().isEmpty() ? " " : ((EditText)findViewById(R.id.add_calendar_notes)).getText().toString();

        Set<String> newSet = new HashSet<String>();
        for(String s : set){
            if(Integer.parseInt(s.split("ʷ")[0]) != id){
                newSet.add(s);
            }
        }
        newSet.add(id+"ʷ"+day+"ʷ"+month+"ʷ"+year+"ʷ"+hour+"ʷ"+minute+"ʷ"+dienstpartner+"ʷ"+beschreibung);

        editor.putStringSet("Calendar", newSet);
        editor.commit();

        if(sp.getBoolean("CalendarSyncActive", false)) {
            String gAcc = sp.getString("CalendarSyncGacc", "");
            Long calendarId = sp.getLong("CalendarSyncTCID", -1);

            long eventID = -1;

            Calendar cal = GregorianCalendar.getInstance();
            cal.set(year, month, day, hour, minute);

            boolean gCalExistsEvent = false;
            String gAccEvent = null;
            Long calendarIdEvent = (long) -1;
            Long eventIdEvent = (long) -1;
            Set<String> setSync = sp.getStringSet("CalendarSync", new HashSet<String>());
            for (String s : setSync) {
                if (Integer.parseInt(s.split("ʷ")[0]) == id) {
                    gCalExistsEvent = true;
                    gAccEvent = s.split("ʷ")[1];
                    calendarIdEvent = Long.parseLong(s.split("ʷ")[2]);
                    eventIdEvent = Long.parseLong(s.split("ʷ")[3]);
                }
            }

            if (!gCalExistsEvent) {
                long currentTimeMillis = cal.getTimeInMillis();
                // 設定活動結束時間為15分鐘後
                long endTimeMillis = currentTimeMillis + 900000;
                // 新增活動
                ContentResolver cr = getContentResolver();
                ContentValues values = new ContentValues();
                values.put(CalendarContract.Events.DTSTART, currentTimeMillis);
                values.put(CalendarContract.Events.DTEND, endTimeMillis);
                values.put(CalendarContract.Events.TITLE, getString(R.string.calendar_event_title).replace("%a", dienstpartner));
                values.put(CalendarContract.Events.DESCRIPTION, beschreibung + "");
                values.put(CalendarContract.Events.CALENDAR_ID, calendarId);
                values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getDisplayName());
                // 因為targetSDK=25，所以要在Apps運行時檢查權限
                int permissionCheck = ContextCompat.checkSelfPermission(KalenderAddFrame.this,
                        Manifest.permission.WRITE_CALENDAR);
                // 如果使用者給了權限便開始新增日歷
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
                    // 返回新建活動的ID
                    if (uri != null) {
                        eventID = Long.parseLong(uri.getLastPathSegment());
                    }
                }


                setSync.add(id + "ʷ" + gAcc + "ʷ" + calendarId + "ʷ" + eventID);
                editor.putStringSet("CalendarSync", setSync);
                editor.commit();
            } else {

                long currentTimeMillis = cal.getTimeInMillis();
                // 設定活動結束時間為15分鐘後
                long endTimeMillis = currentTimeMillis + 900000;
                long eventId = eventIdEvent;
                // 取得在EditText的標題
                // 更新活動
                ContentResolver cr = getContentResolver();
                ContentValues values = new ContentValues();
                values.put(CalendarContract.Events.DTSTART, currentTimeMillis);
                values.put(CalendarContract.Events.DTEND, endTimeMillis);
                values.put(CalendarContract.Events.TITLE, getString(R.string.calendar_event_title).replace("%a", dienstpartner));
                values.put(CalendarContract.Events.DESCRIPTION, beschreibung + "");
                // 因為targetSDK=25，所以要在Apps運行時檢查權限
                int permissionCheck = ContextCompat.checkSelfPermission(KalenderAddFrame.this,
                        Manifest.permission.WRITE_CALENDAR);
                // 如果使用者給了權限便開始更新日歷
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
                    cr.update(uri, values, null, null);
                }
            }
        }

        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(collapsed)
            getMenuInflater().inflate(R.menu.bericht_add_frame, menu);
        MenuTintUtils.tintAllIcons(menu, Color.WHITE);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            save();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
