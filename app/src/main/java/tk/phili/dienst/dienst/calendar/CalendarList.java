package tk.phili.dienst.dienst.calendar;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;

import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tk.phili.dienst.dienst.R;

public class CalendarList extends ArrayAdapter<String> {

    //DECLARATIONS
    List<Event> events;
    Context c;
    CalendarFragment calendarFragment;
    LayoutInflater inflater;

    public CalendarList(Context context, CalendarFragment calendarFragment, List<Event> events) {
        super(context, R.layout.kalender_item, (List) events);
        this.c = context;
        this.calendarFragment = calendarFragment;
        this.events = events;
    }

    public class ViewHolder {
        View mainView;
        TextView dateTv;
        TextView partnerTv;
        TextView notesTv;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.kalender_item, null);
        }

        final ViewHolder holder = new ViewHolder();
        holder.dateTv = (TextView) convertView.findViewById(R.id.see_kalender_date);
        holder.mainView = convertView;
        holder.notesTv = (TextView) convertView.findViewById(R.id.see_kalender_notes);
        holder.partnerTv = (TextView) convertView.findViewById(R.id.see_kalender_partner);

        final int id = Integer.parseInt(((String) events.get(position).getData()).split("ʷ")[0]);
        final int day = Integer.parseInt(((String) events.get(position).getData()).split("ʷ")[1]);
        final int month = Integer.parseInt(((String) events.get(position).getData()).split("ʷ")[2]);
        final int year = Integer.parseInt(((String) events.get(position).getData()).split("ʷ")[3]);
        final int hour = Integer.parseInt(((String) events.get(position).getData()).split("ʷ")[4]);
        final int minute = Integer.parseInt(((String) events.get(position).getData()).split("ʷ")[5]);
        final String dienstpartner = ((String) events.get(position).getData()).split("ʷ")[6];
        final String beschreibung = ((String) events.get(position).getData()).split("ʷ")[7];

        GregorianCalendar cal = new GregorianCalendar(year, month, day, hour, minute);
        Date newDate = new Date(cal.getTimeInMillis());
        String s = java.text.DateFormat.getTimeInstance(DateFormat.SHORT).format(newDate);

        holder.dateTv.setText(s);

        if (dienstpartner == null || dienstpartner.trim().isEmpty()) {
            holder.partnerTv.setText(getContext().getResources().getString(R.string.no_partner));
        } else {
            holder.partnerTv.setText(dienstpartner);
        }

        if (beschreibung == null || beschreibung.trim().isEmpty()) {
            holder.notesTv.setVisibility(View.GONE);
        } else {
            holder.notesTv.setVisibility(View.VISIBLE);
        }
        holder.notesTv.setText(beschreibung);

        holder.mainView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                String[] entries = new String[]{c.getString(R.string.kalender_menu_edit), c.getString(R.string.kalender_menu_delete)};
                new MaterialAlertDialogBuilder(new ContextThemeWrapper(getContext(), R.style.AppThemeDark))
                        .setTitle(c.getString(R.string.kalender_menu_title))
                        .setItems(entries, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    calendarFragment.showEditDialog(id,
                                            day,
                                            month,
                                            year,
                                            hour, minute,
                                            !dienstpartner.isEmpty() ? dienstpartner : null,
                                            !beschreibung.isEmpty() ? beschreibung : null);
                                } else if (which == 1) {
                                    new MaterialAlertDialogBuilder(new ContextThemeWrapper(getContext(), R.style.AppThemeDark), R.style.MaterialAlertDialogCenterStyle)
                                            .setMessage(R.string.kalender_menu_delete_msg)
                                            .setTitle((c.getString(R.string.kalender_menu_delete_title)))
                                            .setPositiveButton(c.getString(R.string.delete_ok), new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int idd) {
                                                    SharedPreferences sp = c.getSharedPreferences("MainActivity", c.MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = sp.edit();

                                                    Set<String> set = sp.getStringSet("Calendar", new HashSet<String>());

                                                    Set<String> newSet = new HashSet<String>();
                                                    for (String s : set) {
                                                        if (Integer.parseInt(s.split("ʷ")[0]) != id) {
                                                            newSet.add(s);
                                                        }
                                                    }

                                                    editor.putStringSet("Calendar", newSet);

                                                    Set<String> setShown = sp.getStringSet("Calendar_Shown", new HashSet<String>());
                                                    Set<String> newSetShown = new HashSet<String>();
                                                    for (String s : setShown) {
                                                        if (Integer.parseInt(s) != id) {
                                                            newSetShown.add(s);
                                                        }
                                                    }
                                                    editor.putStringSet("Calendar_Shown", newSetShown);

                                                    editor.apply();
                                                    if (sp.getBoolean("CalendarSyncActive", false)) {
                                                        boolean gCalExistsEvent = false;
                                                        String gAccEvent = null;
                                                        Long calendarIdEvent = (long) -1;
                                                        Long eventIdEvent = (long) -1;
                                                        Set<String> setSync = sp.getStringSet("CalendarSync", new HashSet<String>());
                                                        HashSet<String> newsetSync = new HashSet<String>();
                                                        for (String s : setSync) {
                                                            if (Integer.parseInt(s.split("ʷ")[0]) == id) {
                                                                gCalExistsEvent = true;
                                                                gAccEvent = s.split("ʷ")[1];
                                                                calendarIdEvent = Long.parseLong(s.split("ʷ")[2]);
                                                                eventIdEvent = Long.parseLong(s.split("ʷ")[3]);
                                                            } else {
                                                                newsetSync.add(s);
                                                            }
                                                        }
                                                        editor.putStringSet("CalendarSync", newsetSync);
                                                        editor.apply();

                                                        if (gCalExistsEvent) {
                                                            long eventId = eventIdEvent;
                                                            // 刪除活動
                                                            ContentResolver cr = getContext().getContentResolver();
                                                            // 因為targetSDK=25，所以要在Apps運行時檢查權限
                                                            int permissionCheck = ContextCompat.checkSelfPermission(getContext(),
                                                                    Manifest.permission.WRITE_CALENDAR);
                                                            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                                                                Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
                                                                cr.delete(uri, null, null);
                                                            }
                                                        }
                                                    }

                                                    calendarFragment.refreshAll();
                                                }
                                            })
                                            .setNegativeButton(c.getString(R.string.delete_cancel), new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                }
                                            }).setIcon(R.drawable.ic_warning_black_24dp).show();


                                }
                            }
                        })
                        .show();

                return false;
            }
        });

        return convertView;
    }


}
