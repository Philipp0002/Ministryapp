package tk.phili.dienst.dienst.calendar;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
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
import android.provider.CalendarContract;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import tk.phili.dienst.dienst.R;
import tk.phili.dienst.dienst.drawer.Drawer;
import tk.phili.dienst.dienst.utils.MenuTintUtils;

public class CalendarAddDialog extends DialogFragment implements Toolbar.OnMenuItemClickListener {

    Calendar myCalendar = null;
    long id;
    public SharedPreferences sp;
    private SharedPreferences.Editor editor;

    boolean collapsed = false;

    Toolbar toolbar;
    EditText dateView;
    EditText partnerView;
    EditText notesView;

    Runnable dismissCallback;

    //FORMAT
    //IDʷDAYʷMONTHʷYEARʷHOURʷMINUTEʷDIENSTPARTNERʷBESCHREIBUNG

    static CalendarAddDialog newInstance(long id, int day, int month, int year, int hour, int minute, String partner, String notes) {
        CalendarAddDialog f = new CalendarAddDialog();

        Bundle args = new Bundle();
        args.putLong("id", id);
        args.putInt("day", day);
        args.putInt("month", month);
        args.putInt("year", year);
        if (partner != null) args.putString("partner", partner);
        if (notes != null) args.putString("notes", notes);
        f.setArguments(args);

        return f;
    }

    static CalendarAddDialog newInstance(long id, int day, int month, int year) {
        return newInstance(id, day, month, year, 0, 0, null, null);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogStyle);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_calendar_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = view.findViewById(R.id.toolbar);
        dateView = view.findViewById(R.id.add_calendar_date);
        partnerView = view.findViewById(R.id.add_calendar_partner);
        notesView = view.findViewById(R.id.add_calendar_notes);

        toolbar.inflateMenu(R.menu.save);
        MenuTintUtils.tintAllIcons(toolbar.getMenu(), Color.WHITE);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setNavigationOnClickListener(view1 -> {
            dismiss();
            Drawer.hideKeyboard(getActivity());
        });

        sp = getContext().getSharedPreferences("MainActivity", MODE_PRIVATE);
        editor = sp.edit();

        int day = getArguments().getInt("day", 1);
        int month = getArguments().getInt("month", 1);
        int year = getArguments().getInt("year", 1);
        final int hour = getArguments().getInt("hour", 0);
        int minute = getArguments().getInt("minute", 0);
        id = getArguments().getLong("id", 0);

        String partner = getArguments().getString("partner");
        String notes = getArguments().getString("notes");

        if (partner != null) {
            if (!partner.trim().isEmpty()) {
                partnerView.setText(partner);
            }
        }
        if (notes != null) {
            if (!notes.trim().isEmpty()) {
                notesView.setText(notes);
            }
        }

        myCalendar = new GregorianCalendar(year, month, day, hour, minute);
        Date newDate = new Date(myCalendar.getTimeInMillis());
        String s = java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.SHORT, java.text.DateFormat.SHORT).format(newDate);
        dateView.setText(s);

        final TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour_of_day, int min) {
                myCalendar.set(Calendar.HOUR_OF_DAY, hour_of_day);
                myCalendar.set(Calendar.MINUTE, min);
                Date newDate = new Date(myCalendar.getTimeInMillis());
                String s = java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.SHORT, java.text.DateFormat.SHORT).format(newDate);
                dateView.setText(s);

                partnerView.requestFocus();
            }
        };
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);


                new TimePickerDialog(getContext(),
                        time,
                        myCalendar.get(Calendar.HOUR_OF_DAY),
                        myCalendar.get(Calendar.MINUTE),
                        DateFormat.is24HourFormat(getContext()))
                        .show();

                partnerView.requestFocus();
            }
        };


        dateView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View v, boolean hasFocus) {
                if (ViewCompat.isAttachedToWindow(v)) {
                    if (hasFocus) {
                        DatePickerDialog dpd = new DatePickerDialog(getContext(), date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH));
                        dpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                partnerView.requestFocus();
                            }
                        });
                        dpd.show();
                    }
                }
            }
        });

    }

    public void save() {
        Set<String> set = sp.getStringSet("Calendar", new HashSet<String>());
        int day = myCalendar.get(Calendar.DAY_OF_MONTH);
        int month = myCalendar.get(Calendar.MONTH);
        int year = myCalendar.get(Calendar.YEAR);
        int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = myCalendar.get(Calendar.MINUTE);
        String dienstpartner = partnerView.getText().toString().isEmpty() ? " " : partnerView.getText().toString();
        String beschreibung = notesView.getText().toString().isEmpty() ? " " : notesView.getText().toString();

        Set<String> newSet = new HashSet<String>();
        for (String s : set) {
            if (Integer.parseInt(s.split("ʷ")[0]) != id) {
                newSet.add(s);
            }
        }
        newSet.add(id + "ʷ" + day + "ʷ" + month + "ʷ" + year + "ʷ" + hour + "ʷ" + minute + "ʷ" + dienstpartner + "ʷ" + beschreibung);

        editor.putStringSet("Calendar", newSet);
        editor.commit();

        if (sp.getBoolean("CalendarSyncActive", false)) {
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
                ContentResolver cr = getContext().getContentResolver();
                ContentValues values = new ContentValues();
                values.put(CalendarContract.Events.DTSTART, currentTimeMillis);
                values.put(CalendarContract.Events.DTEND, endTimeMillis);
                values.put(CalendarContract.Events.TITLE, getString(R.string.calendar_event_title).replace("%a", dienstpartner));
                values.put(CalendarContract.Events.DESCRIPTION, beschreibung + "");
                values.put(CalendarContract.Events.CALENDAR_ID, calendarId);
                values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getDisplayName());
                // 因為targetSDK=25，所以要在Apps運行時檢查權限
                int permissionCheck = ContextCompat.checkSelfPermission(getContext(),
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
                ContentResolver cr = getContext().getContentResolver();
                ContentValues values = new ContentValues();
                values.put(CalendarContract.Events.DTSTART, currentTimeMillis);
                values.put(CalendarContract.Events.DTEND, endTimeMillis);
                values.put(CalendarContract.Events.TITLE, getString(R.string.calendar_event_title).replace("%a", dienstpartner));
                values.put(CalendarContract.Events.DESCRIPTION, beschreibung + "");
                // 因為targetSDK=25，所以要在Apps運行時檢查權限
                int permissionCheck = ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.WRITE_CALENDAR);
                // 如果使用者給了權限便開始更新日歷
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
                    cr.update(uri, values, null, null);
                }
            }
        }

        dismiss();
        if (dismissCallback != null)
            dismissCallback.run();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            save();
            return true;
        }
        return false;
    }

}
