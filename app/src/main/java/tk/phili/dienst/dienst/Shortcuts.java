package tk.phili.dienst.dienst;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by fipsi on 05.01.2017.
 */

public class Shortcuts {

    public static void updateShortcuts(String shortcutids, Context context, boolean silent){
        if(Build.VERSION.SDK_INT >= 25) {
            ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);


            Intent i = new Intent(context, Splash.class);
            ShortcutInfo id1 = null;
            ShortcutInfo id2 = null;
            ShortcutInfo id3 = null;
            ShortcutInfo id4 = null;
            ShortcutInfo id5 = null;
            if(shortcutids.contains("0")) {
                i.putExtra("Activity", "MainActivity");
                id1 = new ShortcutInfo.Builder(context, "id1")
                        .setShortLabel(context.getString(R.string.title_section1))
                        .setLongLabel(context.getString(R.string.title_section1))
                        .setIcon(Icon.createWithResource(context, R.drawable.ic_class_black_24dp))
                        .setIntent(i.setAction(Intent.ACTION_VIEW))
                        .build();
            }

            if(shortcutids.contains("1")) {
                i.putExtra("Activity", "Gebiete");
                id2 = new ShortcutInfo.Builder(context, "id2")
                        .setShortLabel(context.getString(R.string.title_section8))
                        .setLongLabel(context.getString(R.string.title_section8))
                        .setIcon(Icon.createWithResource(context, R.drawable.ic_map_black_24dp))
                        .setIntent(i.setAction(Intent.ACTION_VIEW))
                        .build();
            }

            if(shortcutids.contains("2")) {
                i.putExtra("Activity", "Notizen");
                id3 = new ShortcutInfo.Builder(context, "id3")
                        .setShortLabel(context.getString(R.string.title_section3))
                        .setLongLabel(context.getString(R.string.title_section3))
                        .setIcon(Icon.createWithResource(context, R.drawable.ic_description_black_24dp))
                        .setIntent(i.setAction(Intent.ACTION_VIEW))
                        .build();
            }

            if(shortcutids.contains("3")) {
                i.putExtra("Activity", "Empfehlungen");
                id4 = new ShortcutInfo.Builder(context, "id4")
                        .setShortLabel(context.getString(R.string.title_section4))
                        .setLongLabel(context.getString(R.string.title_section4))
                        .setIcon(Icon.createWithResource(context, R.drawable.ic_thumb_up_black_24dp))
                        .setIntent(i.setAction(Intent.ACTION_VIEW))
                        .build();
            }

            if(shortcutids.contains("4")) {
                i.putExtra("Activity", "Videos");
                id5 = new ShortcutInfo.Builder(context, "id5")
                        .setShortLabel(context.getString(R.string.title_section7))
                        .setLongLabel(context.getString(R.string.title_section7))
                        .setIcon(Icon.createWithResource(context, R.drawable.ic_video_library_black_24dp))
                        .setIntent(i.setAction(Intent.ACTION_VIEW))
                        .build();
            }

            ArrayList<ShortcutInfo> shortcuts = new ArrayList<>();
            if(id5 != null){shortcuts.add(id5);}
            if(id4 != null){shortcuts.add(id4);}
            if(id3 != null){shortcuts.add(id3);}
            if(id2 != null){shortcuts.add(id2);}
            if(id1 != null){shortcuts.add(id1);}

            if(shortcuts.isEmpty()){
                shortcutManager.removeAllDynamicShortcuts();
            }else {
                shortcutManager.setDynamicShortcuts(shortcuts);
            }
        }else{
            if(!silent) {
                Toast.makeText(context, context.getString(R.string.shortcuts_not_availible), Toast.LENGTH_LONG).show();
            }
        }
    }
}
