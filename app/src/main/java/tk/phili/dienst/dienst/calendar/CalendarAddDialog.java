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
import android.os.Build;
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
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import tk.phili.dienst.dienst.R;
import tk.phili.dienst.dienst.utils.MenuTintUtils;
import tk.phili.dienst.dienst.utils.Utils;

public class CalendarAddDialog extends DialogFragment implements Toolbar.OnMenuItemClickListener {

    Calendar calendar = null;
    long id;
    public SharedPreferences sp;
    private SharedPreferences.Editor editor;

    Toolbar toolbar;
    EditText dateView, partnerView, notesView;

    Runnable dismissCallback;

    private OnBackInvokedCallback backInvokedCallback;

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
    public void dismiss() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && backInvokedCallback != null) {
            getActivity().getOnBackInvokedDispatcher().unregisterOnBackInvokedCallback(backInvokedCallback);
        }
        if (getShowsDialog()) {
            getDialog().dismiss();
        } else {
            int mBackStackId = -1;
            try {
                Field field = DialogFragment.class.getDeclaredField("mBackStackId");
                field.setAccessible(true);
                field.get(this);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (mBackStackId >= 0) {
                getParentFragmentManager().popBackStack(mBackStackId,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
            } else {
                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                ft.remove(this);
                ft.commit();
            }
        }
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
            Utils.hideKeyboard(getActivity());
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

        calendar = new GregorianCalendar(year, month, day, hour, minute);
        Date newDate = new Date(calendar.getTimeInMillis());
        String s = java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.SHORT, java.text.DateFormat.SHORT).format(newDate);
        dateView.setText(s);

        final TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour_of_day, int min) {
                calendar.set(Calendar.HOUR_OF_DAY, hour_of_day);
                calendar.set(Calendar.MINUTE, min);
                Date newDate = new Date(calendar.getTimeInMillis());
                String s = java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.SHORT, java.text.DateFormat.SHORT).format(newDate);
                dateView.setText(s);

                partnerView.requestFocus();
            }
        };
        final DatePickerDialog.OnDateSetListener date = (view12, year1, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year1);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            new TimePickerDialog(getContext(),
                    time,
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    DateFormat.is24HourFormat(getContext()))
                    .show();

            partnerView.requestFocus();
        };


        dateView.setOnFocusChangeListener((v, hasFocus) -> {
            if (ViewCompat.isAttachedToWindow(v)) {
                if (hasFocus) {
                    DatePickerDialog dpd = new DatePickerDialog(getContext(), date, calendar
                            .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH));
                    dpd.setOnCancelListener(dialog -> partnerView.requestFocus());
                    dpd.show();
                }
            }
        });

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            backInvokedCallback = () -> dismiss();
            getActivity().getOnBackInvokedDispatcher().registerOnBackInvokedCallback(
                    OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                    backInvokedCallback
            );
        }

    }

    public void save() {
        Set<String> set = sp.getStringSet("Calendar", new HashSet<String>());
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
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
                long endTimeMillis = currentTimeMillis + 900000;
                ContentResolver cr = getContext().getContentResolver();
                ContentValues values = new ContentValues();
                values.put(CalendarContract.Events.DTSTART, currentTimeMillis);
                values.put(CalendarContract.Events.DTEND, endTimeMillis);
                values.put(CalendarContract.Events.TITLE, getString(R.string.calendar_event_title).replace("%a", dienstpartner));
                values.put(CalendarContract.Events.DESCRIPTION, beschreibung + "");
                values.put(CalendarContract.Events.CALENDAR_ID, calendarId);
                values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getDisplayName());
                int permissionCheck = ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.WRITE_CALENDAR);
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
                    if (uri != null) {
                        eventID = Long.parseLong(uri.getLastPathSegment());
                    }
                }


                setSync.add(id + "ʷ" + gAcc + "ʷ" + calendarId + "ʷ" + eventID);
                editor.putStringSet("CalendarSync", setSync);
                editor.commit();
            } else {

                long currentTimeMillis = cal.getTimeInMillis();
                long endTimeMillis = currentTimeMillis + 900000;
                long eventId = eventIdEvent;
                ContentResolver cr = getContext().getContentResolver();
                ContentValues values = new ContentValues();
                values.put(CalendarContract.Events.DTSTART, currentTimeMillis);
                values.put(CalendarContract.Events.DTEND, endTimeMillis);
                values.put(CalendarContract.Events.TITLE, getString(R.string.calendar_event_title).replace("%a", dienstpartner));
                values.put(CalendarContract.Events.DESCRIPTION, beschreibung + "");
                int permissionCheck = ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.WRITE_CALENDAR);
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
                    cr.update(uri, values, null, null);
                }
            }
        }

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat
                    .requestPermissions(getActivity(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100001);
            return;
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
