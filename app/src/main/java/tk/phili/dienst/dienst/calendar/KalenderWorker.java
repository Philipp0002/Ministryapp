package tk.phili.dienst.dienst.calendar;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import tk.phili.dienst.dienst.R;

public class KalenderWorker extends Worker {

    public KalenderWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }


    @NonNull
    @Override
    public Result doWork() {
        run(getApplicationContext());

        // Indicate success or failure with your return value:
        return Result.success();
    }

    public static void run(Context context) {
        SharedPreferences sp = context.getSharedPreferences("MainActivity", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();

        int pref_time = sp.getInt("Calendar_Time", 1);
        int pref_unit = sp.getInt("Calendar_Unit", 0);

        Calendar cal = Calendar.getInstance();
        Date d = Calendar.getInstance().getTime();
        cal.setTime(d);

        Set<String> set = sp.getStringSet("Calendar", new HashSet<>());
        Set<String> setShown = sp.getStringSet("Calendar_Shown", new HashSet<>());
        for (String s : set) {
            int id = Integer.parseInt(s.split("ʷ")[0]);
            int day = Integer.parseInt(s.split("ʷ")[1]);
            int month = Integer.parseInt(s.split("ʷ")[2]);
            int year = Integer.parseInt(s.split("ʷ")[3]);
            int hour = Integer.parseInt(s.split("ʷ")[4]);
            int minute = Integer.parseInt(s.split("ʷ")[5]);
            String partner = s.split("ʷ")[6];
            String description = s.split("ʷ")[7];

            if (!setShown.contains(id + "")) {
                if (new GregorianCalendar(year, month, day, hour, minute).before(Calendar.getInstance())) {
                    continue;
                }
                if (pref_unit == 0) { //hour
                    if (cal.get(Calendar.DAY_OF_MONTH) == day &&
                            cal.get(Calendar.MONTH) == month &&
                            cal.get(Calendar.YEAR) == year) {
                        if (hour - pref_time <= cal.get(Calendar.HOUR_OF_DAY)) {
                            sendNotification(context, id, day, month, year, hour, minute, partner, description);
                            setShown.add(id + "");
                            edit.putStringSet("Calendar_Shown", setShown);
                            edit.commit();
                        }
                    }
                } else if (pref_unit == 1) { //day
                    if (cal.get(Calendar.DAY_OF_MONTH) >= day - pref_time &&
                            cal.get(Calendar.MONTH) == month &&
                            cal.get(Calendar.YEAR) == year) {
                        sendNotification(context, id, day, month, year, hour, minute, partner, description);
                        setShown.add(id + "");
                        edit.putStringSet("Calendar_Shown", setShown);
                        edit.commit();
                    }
                } else { //error
                    edit.putInt("Calendar_Unit", 0);
                    edit.commit();
                }
            }
        }
    }


    public static void sendNotification(Context context, int id, int day, int month, int year, int hour, int minute, String partner, String description) {
        Intent i = new Intent(context, Calendar.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, FLAG_IMMUTABLE);

        String time = android.text.format.DateFormat.getTimeFormat(context).format(new GregorianCalendar(year, month, day, hour, minute).getTime());
        String date = android.text.format.DateFormat.getDateFormat(context).format(new GregorianCalendar(year, month, day, hour, minute).getTime());

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "calendar")
                .setContentTitle(context.getString(R.string.calendar_notification_title).replace("%a", partner).replace("%b", time))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.calendar_notification_msg).replace("%a", date)))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentText(context.getString(R.string.calendar_notification_msg).replace("%a", day + "." + month + "." + year))
                .setSmallIcon(R.drawable.dienstapp_icon_nobckgrnd)
                .setContentIntent(pendingIntent);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        //TODO FIND BETTER WAY OF ID
        notificationManager.notify(id, mBuilder.build());
    }


}
