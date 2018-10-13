package tk.phili.dienst.dienst;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Kalender extends AppCompatActivity {

    CompactCalendarView compactCalendarView;
    public SharedPreferences sp;
    private SharedPreferences.Editor editor;
    GregorianCalendar cal;
    boolean initialized = false;

    //FORMAT
    //IDʷDAYʷMONTHʷYEARʷHOURʷMINUTEʷDIENSTPARTNERʷBESCHREIBUNG

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kalender);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.bringToFront();
        Drawer.addDrawer(this, toolbar, 6);
        compactCalendarView = findViewById(R.id.compactcalendar_view);
        cal = new GregorianCalendar();
        refreshAll();
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                List<Event> events = compactCalendarView.getEvents(dateClicked);
                cal.setTimeInMillis(dateClicked.getTime());
                Log.d("lololololol1", ""+dateClicked.getTime());
                refreshDay();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                cal.setTimeInMillis(firstDayOfNewMonth.getTime());
                Log.d("lololololol2", ""+firstDayOfNewMonth.getTime());
                refreshDay();
            }
        });
        compactCalendarView.setUseThreeLetterAbbreviation(true);

        findViewById(R.id.imageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(Kalender.this, KalenderAddFrame.class);
                float x = findViewById(R.id.imageButton).getX()+findViewById(R.id.imageButton).getWidth()/2;
                float y = findViewById(R.id.imageButton).getY()+findViewById(R.id.imageButton).getHeight()/2;
                mainIntent.putExtra("xReveal",x);
                mainIntent.putExtra("yReveal",y);

                mainIntent.putExtra("day",cal.get(Calendar.DAY_OF_MONTH));
                mainIntent.putExtra("month",cal.get(Calendar.MONTH));
                mainIntent.putExtra("year",cal.get(Calendar.YEAR));

                int idmax = 0;
                Set<String> set = sp.getStringSet("Calendar", new HashSet<String>());
                for(String s : set){
                    int id = Integer.parseInt(s.split("ʷ")[0]);
                    if(id > idmax){
                        idmax = id;
                    }
                }
                idmax++;
                mainIntent.putExtra("id", idmax);

                startActivity(mainIntent);
            }
        });
    }

    public static boolean yearrunonce = false;
    public static boolean monthrunonce = false;
    public void refreshDay(){

        //YEAR SPINNER
        Spinner spinneryear = (Spinner) findViewById(R.id.spinner_nav_year);
        ArrayAdapter<CharSequence> adapteryear = ArrayAdapter.createFromResource(this,
                R.array.year_array, R.layout.spinner_main);
        adapteryear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinneryear.setAdapter(adapteryear);
        spinneryear.setSelection(Math.abs(2016-cal.get(Calendar.YEAR)));
        yearrunonce = false;
        spinneryear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(!yearrunonce){
                    yearrunonce = true;
                    return;
                }
                int yearint = 2016+position;
                cal.set(Calendar.YEAR, yearint);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                compactCalendarView.setCurrentDate(new Date(cal.getTimeInMillis()));
                refreshDay();
            }
            @Override public void onNothingSelected(AdapterView<?> parentView) { }
        });

        //MONTH SPINNER
        Spinner spinner = (Spinner) findViewById(R.id.spinner_nav);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.months_array, R.layout.spinner_main);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(cal.get(Calendar.MONTH));
        monthrunonce = false;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(!monthrunonce){
                    monthrunonce = true;
                    return;
                }
                cal.set(Calendar.MONTH, position);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                compactCalendarView.setCurrentDate(new Date(cal.getTimeInMillis()));
                refreshDay();
            }
            @Override public void onNothingSelected(AdapterView<?> parentView) { }
        });

        List<Event> events = compactCalendarView.getEvents(cal.getTimeInMillis());
        if(events.isEmpty()){
            findViewById(R.id.no_kal).setVisibility(View.VISIBLE);
        }else {
            findViewById(R.id.no_kal).setVisibility(View.INVISIBLE);
        }
        final KalenderList adapterlist = new KalenderList(Kalender.this, events);
        ((ListView)findViewById(R.id.event_liste)).setAdapter(adapterlist);

    }


    public void refreshAll(){
        sp = getSharedPreferences("MainActivity", MODE_PRIVATE);
        editor = sp.edit();

        compactCalendarView.removeAllEvents();

        Set<String> set = sp.getStringSet("Calendar", new HashSet<String>());
        for(String s : set){
            int id = Integer.parseInt(s.split("ʷ")[0]);
            int day = Integer.parseInt(s.split("ʷ")[1]);
            int month = Integer.parseInt(s.split("ʷ")[2]);
            int year = Integer.parseInt(s.split("ʷ")[3]);
            int hour = Integer.parseInt(s.split("ʷ")[4]);
            int minute = Integer.parseInt(s.split("ʷ")[5]);
            String dienstpartner = s.split("ʷ")[6];
            String beschreibung = s.split("ʷ")[7];

            GregorianCalendar cal = new GregorianCalendar(year, month, day);
            Event ev1 = new Event(Color.GREEN, cal.getTimeInMillis(), s);
            compactCalendarView.addEvent(ev1);
        }


        refreshDay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshAll();
    }

}
