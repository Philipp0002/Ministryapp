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

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.shape.ShapeAppearanceModel;

import java.text.DateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Setter;
import tk.phili.dienst.dienst.R;
import tk.phili.dienst.dienst.report.Report;
import tk.phili.dienst.dienst.utils.Utils;

public class CalendarList extends RecyclerView.Adapter<CalendarList.ViewHolder> {

    //DECLARATIONS
    @Setter
    private List<Event> events;
    private final Context context;
    private final CalendarFragment calendarFragment;

    public CalendarList(Context context, CalendarFragment calendarFragment, List<Event> events) {
        this.context = context;
        this.calendarFragment = calendarFragment;
        this.events = events;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView mainView;
        TextView dateTv;
        TextView partnerTv;
        TextView notesTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTv = (TextView) itemView.findViewById(R.id.calendar_item_date);
            mainView = (MaterialCardView) itemView;
            notesTv = (TextView) itemView.findViewById(R.id.calendar_item_notes);
            partnerTv = (TextView) itemView.findViewById(R.id.calendar_item_partner);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ShapeAppearanceModel.Builder shapeBuilder = new ShapeAppearanceModel.Builder()
                .setAllCornerSizes(Utils.dpToPx(16));
        int connectingCornersSize = Utils.dpToPx(4);

        if(events.size() > 1) {
            if (position != events.size() - 1) {
                shapeBuilder.setBottomRightCornerSize(connectingCornersSize);
                shapeBuilder.setBottomLeftCornerSize(connectingCornersSize);
            }
            if(position != 0) {
                shapeBuilder.setTopRightCornerSize(connectingCornersSize);
                shapeBuilder.setTopLeftCornerSize(connectingCornersSize);
            }
        }
        holder.mainView.setShapeAppearanceModel(shapeBuilder.build());


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
            holder.partnerTv.setText(context.getResources().getString(R.string.no_partner));
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
            new MaterialAlertDialogBuilder(context)
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
                            new MaterialAlertDialogBuilder(context, R.style.MaterialAlertDialogCenterStyle)
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
                                                ContentResolver cr = context.getContentResolver();
                                                // 因為targetSDK=25，所以要在Apps運行時檢查權限
                                                int permissionCheck = ContextCompat.checkSelfPermission(context,
                                                        Manifest.permission.WRITE_CALENDAR);
                                                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                                                    Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
                                                    cr.delete(uri, null, null);
                                                }
                                            }
                                        }

                                        calendarFragment.refreshAll();
                                    })
                                    .setNegativeButton(context.getString(R.string.cancel), null)
                                    .setIcon(R.drawable.ic_warning_black_24dp)
                                    .show();


                        }
                    })
                    .show();

            return false;
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }


}
