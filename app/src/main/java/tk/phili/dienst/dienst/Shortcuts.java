package tk.phili.dienst.dienst;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;

import java.util.ArrayList;

public class Shortcuts {

    public static void updateShortcuts(Context context) {
        if (Build.VERSION.SDK_INT >= 25) {
            ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);


            Intent i = new Intent(context, Splash.class);
            i.putExtra("Activity", "MainActivity");
            ShortcutInfo id1 = new ShortcutInfo.Builder(context, "id1")
                    .setShortLabel(context.getString(R.string.title_section1))
                    .setLongLabel(context.getString(R.string.title_section1))
                    .setIcon(Icon.createWithResource(context, R.drawable.ic_class_black_24dp))
                    .setIntent(i.setAction(Intent.ACTION_VIEW))
                    .build();

            i.putExtra("Activity", "Notizen");
            ShortcutInfo id2 = new ShortcutInfo.Builder(context, "id2")
                    .setShortLabel(context.getString(R.string.title_section3))
                    .setLongLabel(context.getString(R.string.title_section3))
                    .setIcon(Icon.createWithResource(context, R.drawable.ic_description_black_24dp))
                    .setIntent(i.setAction(Intent.ACTION_VIEW))
                    .build();

            i.putExtra("Activity", "Empfehlungen");
            ShortcutInfo id3 = new ShortcutInfo.Builder(context, "id3")
                    .setShortLabel(context.getString(R.string.title_section4))
                    .setLongLabel(context.getString(R.string.title_section4))
                    .setIcon(Icon.createWithResource(context, R.drawable.ic_thumb_up_black_24dp))
                    .setIntent(i.setAction(Intent.ACTION_VIEW))
                    .build();

            i.putExtra("Activity", "Videos");
            ShortcutInfo id4 = new ShortcutInfo.Builder(context, "id4")
                    .setShortLabel(context.getString(R.string.title_section7))
                    .setLongLabel(context.getString(R.string.title_section7))
                    .setIcon(Icon.createWithResource(context, R.drawable.ic_video_library_black_24dp))
                    .setIntent(i.setAction(Intent.ACTION_VIEW))
                    .build();

            i.putExtra("Activity", "Tagestext");
            ShortcutInfo id5 = new ShortcutInfo.Builder(context, "id5")
                    .setShortLabel(context.getString(R.string.title_tt))
                    .setLongLabel(context.getString(R.string.title_tt))
                    .setIcon(Icon.createWithResource(context, R.drawable.ic_baseline_event_available_24px))
                    .setIntent(i.setAction(Intent.ACTION_VIEW))
                    .build();

            i.putExtra("Activity", "Kalender");
            ShortcutInfo id6 = new ShortcutInfo.Builder(context, "id6")
                    .setShortLabel(context.getString(R.string.title_section9))
                    .setLongLabel(context.getString(R.string.title_section9))
                    .setIcon(Icon.createWithResource(context, R.drawable.ic_today_black_24dp))
                    .setIntent(i.setAction(Intent.ACTION_VIEW))
                    .build();

            ArrayList<ShortcutInfo> shortcuts = new ArrayList<>();
            shortcuts.add(id6);
            shortcuts.add(id5);
            shortcuts.add(id4);
            shortcuts.add(id3);
            shortcuts.add(id2);
            shortcuts.add(id1);

            shortcutManager.setDynamicShortcuts(shortcuts);
        }
    }
}
