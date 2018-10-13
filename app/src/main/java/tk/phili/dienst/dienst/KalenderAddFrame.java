package tk.phili.dienst.dienst;

import android.animation.Animator;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import io.codetail.animation.ViewAnimationUtils;

public class KalenderAddFrame extends AppCompatActivity {

    Calendar myCalendar = null;
    int id;
    public SharedPreferences sp;
    private SharedPreferences.Editor editor;

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
        String s = java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.SHORT, java.text.DateFormat.MEDIUM).format(newDate);
        ((EditText)findViewById(R.id.add_calendar_date)).setText(s);

        final TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour_of_day, int min) {
                myCalendar.set(Calendar.HOUR_OF_DAY, hour_of_day);
                myCalendar.set(Calendar.MINUTE, min);
                Date newDate = new Date(myCalendar.getTimeInMillis());
                String s = java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.SHORT, java.text.DateFormat.MEDIUM).format(newDate);
                ((TextView)findViewById(R.id.add_calendar_date)).setText(s);
            }
        };
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                new TimePickerDialog(KalenderAddFrame.this, time, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(KalenderAddFrame.this))
                    .show();
            }
        };

        findViewById(R.id.add_calendar_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(KalenderAddFrame.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
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
                finish();
            }
        });
    }

}
