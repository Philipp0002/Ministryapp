package tk.phili.dienst.dienst.calendar;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.github.dewinjm.monthyearpicker.MonthFormat;
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialog;
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tk.phili.dienst.dienst.R;
import tk.phili.dienst.dienst.drawer.Drawer;
import tk.phili.dienst.dienst.uiwrapper.FragmentCommunicationPass;
import tk.phili.dienst.dienst.uiwrapper.WrapperActivity;
import tk.phili.dienst.dienst.utils.AdaptiveUtils;
import tk.phili.dienst.dienst.utils.Utils;

public class CalendarFragment extends Fragment {

    CompactCalendarView compactCalendarView;
    public SharedPreferences sp;
    private SharedPreferences.Editor editor;
    GregorianCalendar cal;
    TextView tbv;
    TextView noCal;
    ListView eventList;
    TextView calendarDayText;
    Toolbar toolbar;

    //FORMAT
    //IDʷDAYʷMONTHʷYEARʷHOURʷMINUTEʷDIENSTPARTNERʷBESCHREIBUNG

    FragmentCommunicationPass fragmentCommunicationPass;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentCommunicationPass = (FragmentCommunicationPass) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_calendar, null);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        sp = getContext().getSharedPreferences("MainActivity", Context.MODE_PRIVATE);
        editor = sp.edit();

        toolbar = view.findViewById(R.id.toolbar);
        noCal = view.findViewById(R.id.no_kal);
        eventList = view.findViewById(R.id.event_liste);
        calendarDayText = view.findViewById(R.id.calendar_day_text);

        fragmentCommunicationPass.onDataPass(this, WrapperActivity.FRAGMENTPASS_TOOLBAR, toolbar);

        compactCalendarView = view.findViewById(R.id.compactcalendar_view);
        cal = new GregorianCalendar();
        tbv = view.findViewById(R.id.toolbar_title);
        refreshAll();
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                List<Event> events = compactCalendarView.getEvents(dateClicked);
                cal.setTimeInMillis(dateClicked.getTime());
                refreshDay();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                cal.setTimeInMillis(firstDayOfNewMonth.getTime());
                refreshDay();
            }
        });
        compactCalendarView.setUseThreeLetterAbbreviation(true);

        view.findViewById(R.id.imageButton).setOnClickListener(__ -> {
            int idmax = 0;
            Set<String> set = sp.getStringSet("Calendar", new HashSet<>());
            for (String s : set) {
                int id = Integer.parseInt(s.split("ʷ")[0]);
                if (id > idmax) {
                    idmax = id;
                }
            }
            idmax++;

            showEditDialog(idmax,
                    cal.get(Calendar.DAY_OF_MONTH),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.YEAR),
                    null, null, null, null);
        });

        final SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy");

        tbv.setText(dateFormat.format(cal.getTime()));

        tbv.setOnClickListener(v -> {

            MonthYearPickerDialog simpleDatePickerDialog;

            try {
                ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(getContext(), R.style.AppThemeDark);
                Constructor constructor = MonthYearPickerDialog.class.getDeclaredConstructor(Context.class,
                        int.class,
                        int.class,
                        int.class,
                        MonthFormat.class,
                        MonthYearPickerDialog.OnDateSetListener.class);

                constructor.setAccessible(true);
                simpleDatePickerDialog = (MonthYearPickerDialog) constructor.newInstance(
                        contextThemeWrapper,
                        R.style.DialogStyleBasic,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        MonthFormat.SHORT,
                        (MonthYearPickerDialog.OnDateSetListener) (year, monthOfYear) -> {
                            cal.set(Calendar.MONTH, monthOfYear);
                            cal.set(Calendar.YEAR, year);
                            tbv.setText(dateFormat.format(cal.getTime()));
                            compactCalendarView.setCurrentDate(new Date(cal.getTimeInMillis()));
                            refreshDay();
                        });
                Method method = MonthYearPickerDialog.class.getDeclaredMethod("createTitle", String.class);
                method.setAccessible(true);
                method.invoke(simpleDatePickerDialog, getString(R.string.select_month_year));
                simpleDatePickerDialog.show();
                simpleDatePickerDialog
                        .getButton(DialogInterface.BUTTON_POSITIVE)
                        .setTextColor(ContextCompat.getColor(contextThemeWrapper, R.color.settings_title));

                simpleDatePickerDialog
                        .getButton(DialogInterface.BUTTON_NEGATIVE)
                        .setTextColor(ContextCompat.getColor(contextThemeWrapper, R.color.settings_title));
            } catch (Exception e) {
                e.printStackTrace();
            }

        });


        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED
                            || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat
                                .requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR}, 1001);
                        return;
                    } else {
                        try {
                            Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                                    new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, false, null, null, null, null);
                            startActivityForResult(intent, GET_ACCOUNT_NAME_REQUEST);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(getContext(), R.string.calendar_activate_gcal_not_available, Toast.LENGTH_LONG).show();
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
        };

        if (!sp.contains("CalendarSyncActive")) {
            new MaterialAlertDialogBuilder(new ContextThemeWrapper(getContext(), R.style.AppThemeDark), R.style.MaterialAlertDialogCenterStyle)
                    .setTitle(R.string.calendar_activate_gcal_title)
                    .setMessage(R.string.calendar_activate_gcal_msg)
                    .setIcon(R.drawable.ic_baseline_sync_24)
                    .setPositiveButton(R.string.yes, dialogClickListener)
                    .setNegativeButton(R.string.no, dialogClickListener)
                    .show();
        } else {
            if (sp.getBoolean("CalendarSyncActive", false)) {
                if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat
                            .requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR}, 1001);
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
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    //refreshList();
                    try {
                        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                                new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, false, null, null, null, null);
                        startActivityForResult(intent, GET_ACCOUNT_NAME_REQUEST);
                    } catch (ActivityNotFoundException e) {
                        // TODO
                        Toast.makeText(getContext(), R.string.calendar_activate_gcal_not_available, Toast.LENGTH_LONG).show();
                        editor.putBoolean("CalendarSyncActive", false);
                        editor.commit();
                    }


                } else { // Permission Denied
                    Toast.makeText(getContext(), getString(R.string.calendar_activate_gcal_not_working), Toast.LENGTH_LONG).show();
                    editor.putBoolean("CalendarSyncActive", false);
                    editor.commit();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_ACCOUNT_NAME_REQUEST && resultCode == Activity.RESULT_OK) {
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            GOOGLE_ACCOUNT_NAME = accountName;
            editor.putString("CalendarSyncGacc", GOOGLE_ACCOUNT_NAME);
            editor.commit();
            query_calendar();
        } else if (requestCode == GET_ACCOUNT_NAME_REQUEST && resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(getContext(), R.string.calendar_activate_gcal_not_working, Toast.LENGTH_LONG).show();
            editor.putBoolean("CalendarSyncActive", false);
            editor.commit();
        }
    }


    public void query_calendar() {
        String[] EVENT_PROJECTION = new String[]{
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.ACCOUNT_NAME,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.OWNER_ACCOUNT,
                CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
        };
        int PROJECTION_ID_INDEX = 0;
        int PROJECTION_ACCOUNT_NAME_INDEX = 1;
        int PROJECTION_DISPLAY_NAME_INDEX = 2;
        int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
        int PROJECTION_CALENDAR_ACCESS_LEVEL = 4;
        String targetAccount = GOOGLE_ACCOUNT_NAME;
        Cursor cur;
        ContentResolver cr = getContext().getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) AND ("
                + CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL + " = ?))";
        String[] selectionArgs = new String[]{targetAccount,
                "com.google",
                Integer.toString(CalendarContract.Calendars.CAL_ACCESS_OWNER)};
        int permissionCheck = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_CALENDAR);
        final List<String> accountNameList = new ArrayList<>();
        final List<Integer> calendarIdList = new ArrayList<>();
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
            if (cur != null) {
                while (cur.moveToNext()) {
                    long calendarId = 0;
                    String accountName = null;
                    String displayName = null;
                    String ownerAccount = null;
                    int accessLevel = 0;
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
                    accountNameList.add(displayName);
                    calendarIdList.add((int) calendarId);
                }
                cur.close();
            }
            if (calendarIdList.size() != 0) {
                MaterialAlertDialogBuilder adb = new MaterialAlertDialogBuilder(new ContextThemeWrapper(getContext(), R.style.AppThemeDark));
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
            } else {
                Toast toast = Toast.makeText(getContext(), R.string.calendar_activate_not_found, Toast.LENGTH_LONG);
                toast.show();
                editor.putBoolean("CalendarSyncActive", false);
                editor.commit();
            }
        } else {
            Toast.makeText(getContext(), R.string.calendar_activate_gcal_not_working, Toast.LENGTH_LONG).show();
            editor.putBoolean("CalendarSyncActive", false);
            editor.commit();
        }
    }


    public void refreshDay() {

        final SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy");
        tbv.setText(dateFormat.format(cal.getTime()));

        List<Event> events = compactCalendarView.getEvents(cal.getTimeInMillis());
        if (events.isEmpty()) {
            noCal.setVisibility(View.VISIBLE);
        } else {
            noCal.setVisibility(View.INVISIBLE);
        }
        final CalendarList adapterlist = new CalendarList(getContext(), CalendarFragment.this, events);
        eventList.setAdapter(adapterlist);

        String s = java.text.DateFormat.getDateInstance(DateFormat.SHORT).format(cal.getTime());
        s = android.text.format.DateFormat.format("EEEE", cal.getTime()) + ", " + s;
        calendarDayText.setText(s);

    }


    public void refreshAll() {

        compactCalendarView.removeAllEvents();

        Set<String> set = sp.getStringSet("Calendar", new HashSet<String>());
        for (String s : set) {
            int day = Integer.parseInt(s.split("ʷ")[1]);
            int month = Integer.parseInt(s.split("ʷ")[2]);
            int year = Integer.parseInt(s.split("ʷ")[3]);

            GregorianCalendar cal = new GregorianCalendar(year, month, day);
            Event event = new Event(Color.GREEN, cal.getTimeInMillis(), s);
            compactCalendarView.addEvent(event);
        }

        refreshDay();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshAll();
        fragmentCommunicationPass.onDataPass(this, WrapperActivity.FRAGMENTPASS_TOOLBAR, toolbar);
    }

    public void showEditDialog(long id, int day, int month, int year, Integer hour, Integer minute, String partner, String notes) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        CalendarAddDialog newFragment;

        if (hour != null) {
            newFragment = CalendarAddDialog.newInstance(id, day, month, year, hour, minute, partner, notes);
        } else {
            newFragment = CalendarAddDialog.newInstance(id, day, month, year);
        }

        int screenWidth = getResources().getConfiguration().screenWidthDp;
        if (AdaptiveUtils.LARGE_SCREEN_WIDTH_SIZE <= screenWidth) {
            // The device is using a large layout, so show the fragment as a dialog
            newFragment.show(fragmentManager, "dialog");
        } else {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.add(R.id.drawer_layout, newFragment)
                    .addToBackStack(null).commit();
        }

        newFragment.dismissCallback = () -> {
            refreshAll();
            Utils.hideKeyboard(getActivity());
        };

    }


}
