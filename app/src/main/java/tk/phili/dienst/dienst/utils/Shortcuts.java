package tk.phili.dienst.dienst.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;

import java.util.ArrayList;

import tk.phili.dienst.dienst.R;
import tk.phili.dienst.dienst.uiwrapper.WrapperActivity;

public class Shortcuts {

    public static void updateShortcuts(Context context) {
        if (Build.VERSION.SDK_INT >= 25) {
            ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);

            int maxShortcuts = shortcutManager.getMaxShortcutCountPerActivity();

            Intent i = new Intent(context, WrapperActivity.class);
            i.putExtra("Activity", "MainActivity");
            ShortcutInfo id1 = new ShortcutInfo.Builder(context, "id1")
                    .setShortLabel(context.getString(R.string.title_report))
                    .setLongLabel(context.getString(R.string.title_report))
                    .setIcon(
                            Icon.createWithResource(context, R.drawable.ic_class_black_24dp))
                    .setIntent(i.setAction(Intent.ACTION_VIEW))
                    .build();

            i.putExtra("Activity", "Notizen");
            ShortcutInfo id2 = new ShortcutInfo.Builder(context, "id2")
                    .setShortLabel(context.getString(R.string.title_notes))
                    .setLongLabel(context.getString(R.string.title_notes))
                    .setIcon(
                            Icon.createWithResource(context, R.drawable.ic_description_black_24dp))
                    .setIntent(i.setAction(Intent.ACTION_VIEW))
                    .build();

            i.putExtra("Activity", "Empfehlungen");
            ShortcutInfo id3 = new ShortcutInfo.Builder(context, "id3")
                    .setShortLabel(context.getString(R.string.title_sample_presentations))
                    .setLongLabel(context.getString(R.string.title_sample_presentations))
                    .setIcon(
                            Icon.createWithResource(context, R.drawable.ic_thumb_up_black_24dp))
                    .setIntent(i.setAction(Intent.ACTION_VIEW))
                    .build();

            i.putExtra("Activity", "Videos");
            ShortcutInfo id4 = new ShortcutInfo.Builder(context, "id4")
                    .setShortLabel(context.getString(R.string.title_videos))
                    .setLongLabel(context.getString(R.string.title_videos))
                    .setIcon(
                            Icon.createWithResource(context, R.drawable.ic_video_library_black_24dp))
                    .setIntent(i.setAction(Intent.ACTION_VIEW))
                    .build();

            i.putExtra("Activity", "Tagestext");
            ShortcutInfo id5 = new ShortcutInfo.Builder(context, "id5")
                    .setShortLabel(context.getString(R.string.title_dailytext))
                    .setLongLabel(context.getString(R.string.title_dailytext))
                    .setIcon(
                            Icon.createWithResource(context, R.drawable.ic_baseline_event_available_24px))
                    .setIntent(i.setAction(Intent.ACTION_VIEW))
                    .build();

            i.putExtra("Activity", "Kalender");
            ShortcutInfo id6 = new ShortcutInfo.Builder(context, "id6")
                    .setShortLabel(context.getString(R.string.title_calendar))
                    .setLongLabel(context.getString(R.string.title_calendar))
                    .setIcon(
                            Icon.createWithResource(context, R.drawable.ic_today_black_24dp))
                    .setIntent(i.setAction(Intent.ACTION_VIEW))
                    .build();

            ArrayList<ShortcutInfo> shortcuts = new ArrayList<>();
            if (maxShortcuts >= 6) shortcuts.add(id6);
            if (maxShortcuts >= 5) shortcuts.add(id5);
            if (maxShortcuts >= 4) shortcuts.add(id4);
            if (maxShortcuts >= 3) shortcuts.add(id3);
            if (maxShortcuts >= 2) shortcuts.add(id2);
            if (maxShortcuts >= 1) shortcuts.add(id1);

            shortcutManager.setDynamicShortcuts(shortcuts);
        }
    }
}
