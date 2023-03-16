package tk.phili.dienst.dienst.calendar;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;

import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.DateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tk.phili.dienst.dienst.R;

public class CalendarList extends ArrayAdapter<String> {

    //DECLARATIONS
    List<Event> events;
    Context context;
    CalendarFragment calendarFragment;
    LayoutInflater inflater;

    public CalendarList(Context context, CalendarFragment calendarFragment, List<Event> events) {
        super(context, R.layout.calendar_item, (List) events);
        this.context = context;
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
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.calendar_item, null);
        }

        final ViewHolder holder = new ViewHolder();
        holder.dateTv = (TextView) convertView.findViewById(R.id.calendar_item_date);
        holder.mainView = convertView;
        holder.notesTv = (TextView) convertView.findViewById(R.id.calendar_item_notes);
        holder.partnerTv = (TextView) convertView.findViewById(R.id.calendar_item_partner);

        final int id = Integer.parseInt(((String) events.get(position).getData()).split("ʷ")[0]);
        final int day = Integer.parseInt(((String) events.get(position).getData()).split("ʷ")[1]);
        final int month = Integer.parseInt(((String) events.get(position).getData()).split("ʷ")[2]);
        final int year = Integer.parseInt(((String) events.get(position).getData()).split("ʷ")[3]);
        final int hour = Integer.parseInt(((String) events.get(position).getData()).split("ʷ")[4]);
        final int minute = Integer.parseInt(((String) events.get(position).getData()).split("ʷ")[5]);
        final String partner = ((String) events.get(position).getData()).split("ʷ")[6];
        final String description = ((String) events.get(position).getData()).split("ʷ")[7];

        GregorianCalendar cal = new GregorianCalendar(year, month, day, hour, minute);
        Date newDate = new Date(cal.getTimeInMillis());
        String formattedDate = java.text.DateFormat.getTimeInstance(DateFormat.SHORT).format(newDate);

        holder.dateTv.setText(formattedDate);

        if (partner == null || partner.trim().isEmpty()) {
            holder.partnerTv.setText(getContext().getResources().getString(R.string.no_partner));
        } else {
            holder.partnerTv.setText(partner);
        }

        if (description == null || description.trim().isEmpty()) {
            holder.notesTv.setVisibility(View.GONE);
        } else {
            holder.notesTv.setVisibility(View.VISIBLE);
        }
        holder.notesTv.setText(description);

        holder.mainView.setOnLongClickListener(view -> {

            String[] entries = new String[]{context.getString(R.string.calendar_menu_edit), context.getString(R.string.calendar_menu_delete)};
            new MaterialAlertDialogBuilder(new ContextThemeWrapper(getContext(), R.style.AppThemeDark))
                    .setTitle(context.getString(R.string.calendar_menu_title))
                    .setItems(entries, (dialog, which) -> {
                        if (which == 0) {
                            calendarFragment.showEditDialog(id,
                                    day,
                                    month,
                                    year,
                                    hour,
                                    minute,
                                    partner.isEmpty() ? null : partner,
                                    description.isEmpty() ? null : description);
                        } else if (which == 1) {
                            new MaterialAlertDialogBuilder(new ContextThemeWrapper(getContext(), R.style.AppThemeDark), R.style.MaterialAlertDialogCenterStyle)
                                    .setMessage(R.string.calendar_menu_delete_msg)
                                    .setTitle((context.getString(R.string.calendar_menu_delete_title)))
                                    .setPositiveButton(context.getString(R.string.delete_ok), (d, e) -> {
                                        SharedPreferences sp = context.getSharedPreferences("MainActivity", context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sp.edit();

                                        Set<String> calendarItemsSet = sp.getStringSet("Calendar", new HashSet<>());
                                        Set<String> newCalendarItemsSet = new HashSet<>();
                                        for (String calendarItem : calendarItemsSet) {
                                            if (Integer.parseInt(calendarItem.split("ʷ")[0]) != id) {
                                                newCalendarItemsSet.add(calendarItem);
                                            }
                                        }
                                        editor.putStringSet("Calendar", newCalendarItemsSet);

                                        Set<String> calendarItemsShownSet = sp.getStringSet("Calendar_Shown", new HashSet<>());
                                        Set<String> newCalendarItemsShownSet = new HashSet<>();
                                        for (String shownCalendarItem : calendarItemsShownSet) {
                                            if (Integer.parseInt(shownCalendarItem) != id) {
                                                newCalendarItemsShownSet.add(shownCalendarItem);
                                            }
                                        }
                                        editor.putStringSet("Calendar_Shown", newCalendarItemsShownSet);
                                        editor.apply();

                                        if (sp.getBoolean("CalendarSyncActive", false)) {
                                            boolean gCalExistsEvent = false;
                                            String gAccEvent = null;
                                            Long calendarIdEvent = (long) -1;
                                            Long eventIdEvent = (long) -1;
                                            Set<String> setSync = sp.getStringSet("CalendarSync", new HashSet<>());
                                            HashSet<String> newsetSync = new HashSet<String>();
                                            for (String s1 : setSync) {
                                                if (Integer.parseInt(s1.split("ʷ")[0]) == id) {
                                                    gCalExistsEvent = true;
                                                    gAccEvent = s1.split("ʷ")[1];
                                                    calendarIdEvent = Long.parseLong(s1.split("ʷ")[2]);
                                                    eventIdEvent = Long.parseLong(s1.split("ʷ")[3]);
                                                } else {
                                                    newsetSync.add(s1);
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
                                    })
                                    .setNegativeButton(context.getString(R.string.cancel), (dialog1, id1) -> {
                                    })
                                    .setIcon(R.drawable.ic_warning_black_24dp)
                                    .show();


                        }
                    })
                    .show();

            return false;
        });

        return convertView;
    }


}
