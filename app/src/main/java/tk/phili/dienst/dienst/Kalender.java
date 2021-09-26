package tk.phili.dienst.dienst;

import android.Manifest;
import android.accounts.AccountManager;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dewinjm.monthyearpicker.MonthYearPickerDialog;
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

public class Kalender extends AppCompatActivity {

    CompactCalendarView compactCalendarView;
    public SharedPreferences sp;
    private SharedPreferences.Editor editor;
    GregorianCalendar cal;
    boolean initialized = false;
    TextView tbv;

    //FORMAT
    //IDʷDAYʷMONTHʷYEARʷHOURʷMINUTEʷDIENSTPARTNERʷBESCHREIBUNG

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kalender);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.bringToFront();
        setTitle("");
        Drawer.addDrawer(this, toolbar, 7);
        compactCalendarView = findViewById(R.id.compactcalendar_view);
        cal = new GregorianCalendar();
        tbv = findViewById(R.id.toolbar_title);
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

        final SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy");

        tbv.setText(dateFormat.format(cal.getTime()));


        tbv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MonthYearPickerDialogFragment dialogFragment = MonthYearPickerDialogFragment.getInstance(cal.get(Calendar.MONTH), cal.get(Calendar.YEAR), getString(R.string.select_month_year));

                dialogFragment.setOnDateSetListener(new MonthYearPickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(int year, int monthOfYear) {

                        cal.set(Calendar.MONTH, monthOfYear);
                        cal.set(Calendar.YEAR, year);
                        tbv.setText(dateFormat.format(cal.getTime()));
                        compactCalendarView.setCurrentDate(new Date(cal.getTimeInMillis()));
                        refreshDay();
                    }
                });

                dialogFragment.show(getSupportFragmentManager(), null);

            }
        });

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        if (ContextCompat.checkSelfPermission(Kalender.this, android.Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED
                                || ContextCompat.checkSelfPermission(Kalender.this, android.Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat
                                    .requestPermissions(Kalender.this, new String[]{android.Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR}, 1001);
                            return;
                        }else{
                            try {
                                Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                                        new String[] { GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE }, false, null, null, null, null);
                                startActivityForResult(intent, GET_ACCOUNT_NAME_REQUEST );  //GET_ACCOUNT_NAME_REQUEST是一個自訂的int, 用作分辨所返回的結果
                            } catch (ActivityNotFoundException e) {
                                Toast.makeText(Kalender.this, R.string.calendar_activate_gcal_not_available, Toast.LENGTH_LONG).show();
                                editor.putBoolean("CalendarSyncActive", false);
                                editor.commit();
                            }
                        }

                        editor.putBoolean("CalendarSyncActive", true);
                        editor.commit();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        editor.putBoolean("CalendarSyncActive", false);
                        editor.commit();
                        break;
                }
            }
        };

        if(!sp.contains("CalendarSyncActive")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Kalender.this);
            builder.setTitle(R.string.calendar_activate_gcal_title).setMessage(R.string.calendar_activate_gcal_msg).setPositiveButton(R.string.yes, dialogClickListener)
                    .setNegativeButton(R.string.no, dialogClickListener).show();
        }else{
            if(sp.getBoolean("CalendarSyncActive", false)){
                if (ContextCompat.checkSelfPermission(Kalender.this, android.Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(Kalender.this, android.Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat
                            .requestPermissions(Kalender.this, new String[]{android.Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR}, 1001);
                    return;
                }
            }
        }


    }

    static final int GET_ACCOUNT_NAME_REQUEST = 1;
    static String GOOGLE_ACCOUNT_NAME = "";
    static int targetCalendarId = -1;
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1001:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) { // Permission Granted
                    //refreshList();
                    try {
                        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                                new String[] { GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE }, false, null, null, null, null);
                        startActivityForResult(intent, GET_ACCOUNT_NAME_REQUEST );  //GET_ACCOUNT_NAME_REQUEST是一個自訂的int, 用作分辨所返回的結果
                    } catch (ActivityNotFoundException e) {
                        // TODO
                        Toast.makeText(Kalender.this, R.string.calendar_activate_gcal_not_available, Toast.LENGTH_LONG).show();
                        editor.putBoolean("CalendarSyncActive", false);
                        editor.commit();
                    }


                } else { // Permission Denied
                     Toast.makeText(Kalender.this, getString(R.string.calendar_activate_gcal_not_working), Toast.LENGTH_LONG).show();
                     editor.putBoolean("CalendarSyncActive", false);
                    editor.commit();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_ACCOUNT_NAME_REQUEST && resultCode == RESULT_OK) {
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            //Toast.makeText(Kalender.this, accountName, Toast.LENGTH_LONG).show();
            GOOGLE_ACCOUNT_NAME = accountName;
            editor.putString("CalendarSyncGacc", GOOGLE_ACCOUNT_NAME);
            editor.commit();
            query_calendar();
        }else if (requestCode == GET_ACCOUNT_NAME_REQUEST && resultCode == RESULT_CANCELED) {
            Toast.makeText(Kalender.this, R.string.calendar_activate_gcal_not_working, Toast.LENGTH_LONG).show();
            editor.putBoolean("CalendarSyncActive", false);
            editor.commit();
        }
    }


    public void query_calendar() {
        // 設定要返回的資料
        String[] EVENT_PROJECTION = new String[]{
                CalendarContract.Calendars._ID,                             // 0 日歷ID
                CalendarContract.Calendars.ACCOUNT_NAME,                // 1 日歷所屬的帳戶名稱
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,       // 2 日歷名稱
                CalendarContract.Calendars.OWNER_ACCOUNT,                  // 3 日歷擁有者
                CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,       // 4 對此日歷所擁有的權限
        };
        // 根據上面的設定，定義各資料的索引，提高代碼的可讀性
        int PROJECTION_ID_INDEX = 0;
        int PROJECTION_ACCOUNT_NAME_INDEX = 1;
        int PROJECTION_DISPLAY_NAME_INDEX = 2;
        int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
        int PROJECTION_CALENDAR_ACCESS_LEVEL = 4;
        // 取得在EditText的帳戶名稱
        String targetAccount = GOOGLE_ACCOUNT_NAME;
        // 查詢日歷
        Cursor cur;
        ContentResolver cr = getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        // 定義查詢條件，找出屬於上面Google帳戶及可以完全控制的日歷
        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) AND ("
                + CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL + " = ?))";
        String[] selectionArgs = new String[]{targetAccount,
                "com.google",
                Integer.toString(CalendarContract.Calendars.CAL_ACCESS_OWNER)};
        // 因為targetSDK=25，所以要在Apps運行時檢查權限
        int permissionCheck = ContextCompat.checkSelfPermission(Kalender.this,
                Manifest.permission.READ_CALENDAR);
        // 建立List來暫存查詢的結果
        final List<String> accountNameList = new ArrayList<>();
        final List<Integer> calendarIdList = new ArrayList<>();
        // 如果使用者給了權限便開始查詢日歷
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
            if (cur != null) {
                while (cur.moveToNext()) {
                    long calendarId = 0;
                    String accountName = null;
                    String displayName = null;
                    String ownerAccount = null;
                    int accessLevel = 0;
                    // 取得所需的資料
                    calendarId = cur.getLong(PROJECTION_ID_INDEX);
                    accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
                    displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
                    ownerAccount = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);
                    accessLevel = cur.getInt(PROJECTION_CALENDAR_ACCESS_LEVEL);
                    Log.i("query_calendar", String.format("calendarId=%s", calendarId));
                    Log.i("query_calendar", String.format("accountName=%s", accountName));
                    Log.i("query_calendar", String.format("displayName=%s", displayName));
                    Log.i("query_calendar", String.format("ownerAccount=%s", ownerAccount));
                    Log.i("query_calendar", String.format("accessLevel=%s", accessLevel));
                    // 暫存資料讓使用者選擇
                    accountNameList.add(displayName);
                    calendarIdList.add((int) calendarId);
                }
                cur.close();
            }
            if (calendarIdList.size() != 0) {
                // 建立一個Dialog讓使用者選擇日歷
                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                adb.setTitle("Kalender wählen"); //TODO MULTI LANG
                CharSequence items[] = accountNameList.toArray(new CharSequence[accountNameList.size()]);
                adb.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        targetCalendarId = calendarIdList.get(which);
                        editor.putLong("CalendarSyncTCID", targetCalendarId);
                        editor.commit();
                        dialog.dismiss();
                    }
                });
                adb.setNegativeButton("CANCEL", null);
                adb.show();
            }
            else {
                Toast toast = Toast.makeText(this, R.string.calendar_activate_not_found, Toast.LENGTH_LONG);
                toast.show();
                editor.putBoolean("CalendarSyncActive", false);
                editor.commit();
            }
        }
        else {
            Toast.makeText(Kalender.this, R.string.calendar_activate_gcal_not_working, Toast.LENGTH_LONG).show();
            editor.putBoolean("CalendarSyncActive", false);
            editor.commit();
        }
    }



    public void refreshDay(){

        final SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy");
        tbv.setText(dateFormat.format(cal.getTime()));

        List<Event> events = compactCalendarView.getEvents(cal.getTimeInMillis());
        if(events.isEmpty()){
            findViewById(R.id.no_kal).setVisibility(View.VISIBLE);
        }else {
            findViewById(R.id.no_kal).setVisibility(View.INVISIBLE);
        }
        final KalenderList adapterlist = new KalenderList(Kalender.this, events);
        ((ListView)findViewById(R.id.event_liste)).setAdapter(adapterlist);

        String s = java.text.DateFormat.getDateInstance(DateFormat.SHORT).format(cal.getTime());
        s = android.text.format.DateFormat.format("EEEE", cal.getTime()) + ", " + s;
        ((TextView)findViewById(R.id.calendar_day_text)).setText(s);

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