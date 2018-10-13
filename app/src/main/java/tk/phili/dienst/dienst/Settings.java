package tk.phili.dienst.dienst;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;


public class Settings extends MainActivity {

    public SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public AlertDialog.Builder builder;

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    MenuItem bericht;
    public static boolean wannachange = false;

    private ActionBarDrawerToggle actionbartoggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(getResources().getString(R.string.title_section5));
   /*     if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }*/

        sp = getSharedPreferences("MainActivity", MODE_PRIVATE);
        editor = sp.edit();

        //builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder = new AlertDialog.Builder(Settings.this);




        /////////////////DRAWER/////////////////////////////////////////
        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_5);
        setSupportActionBar(toolbar);
        toolbar.bringToFront();

        Drawer.addDrawer(this, toolbar, 7);

        ////////////////////DRAWER/////////////////////////////////////////////////////////////////////////////

        ListView listview = (ListView) findViewById(R.id.listView);
        String[] cards = {getResources().getString(R.string.report_layout_settings), getResources().getString(R.string.shortcut_entry), getString(R.string.report_private_mode), getResources().getString(R.string.reset), getResources().getString(R.string.impressum), getResources().getString(R.string.licenses), getString(R.string.dsgvo_title)};
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) {
            cards = new String[]{getResources().getString(R.string.report_layout_settings), getString(R.string.report_private_mode), getResources().getString(R.string.reset), getResources().getString(R.string.impressum), getResources().getString(R.string.licenses), getString(R.string.dsgvo_title)};
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cards);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                String item = ((TextView)view).getText().toString();
                if(item.equalsIgnoreCase(getResources().getString(R.string.report_layout_settings))){
                    AlertDialog.Builder alert = new AlertDialog.Builder(Settings.this);

                    alert.setTitle(getString(R.string.report_layout_settings));
                    alert.setMessage(getString(R.string.report_layout_msg));

                    final LinearLayout layout = new LinearLayout(Settings.this);
                    layout.setOrientation(LinearLayout.VERTICAL);

                    View child = getLayoutInflater().inflate(R.layout.list_bericht, null);
                    layout.addView(child); // Notice this is an add method

                    final Spinner dropdown = new Spinner(Settings.this);
                    String[] items = new String[]{getString(R.string.report_layout_1), getString(R.string.report_layout_2)};
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(Settings.this, android.R.layout.simple_spinner_dropdown_item, items);
                    dropdown.setAdapter(adapter);
                    layout.addView(dropdown); // Another add method
                    alert.setView(layout);

                    dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            if(i == 0){
                                layout.removeViewAt(0);
                                View child = getLayoutInflater().inflate(R.layout.list_bericht, null);
                                layout.addView(child, 0);
                                child.findViewById(R.id.list_bericht_background).setBackgroundColor(Color.WHITE);

                            }else if(i == 1){
                                layout.removeViewAt(0);
                                View child = getLayoutInflater().inflate(R.layout.list_bericht_tiny, null);
                                layout.addView(child, 0);
                                child.findViewById(R.id.list_bericht_background).setBackgroundColor(Color.WHITE);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    alert.setPositiveButton(getString(R.string.action_save), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            int i = dropdown.getSelectedItemPosition();
                            editor.putInt("report_layout", i);
                            editor.commit();
                        }
                    });

                    alert.show();
                }
                if(item.equalsIgnoreCase(getString(R.string.report_private_mode))){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this)
                            .setTitle(getString(R.string.report_private_mode))
                            .setMessage(getString(R.string.report_private_onoff))
                            .setCancelable(true)
                            .setPositiveButton(getString(R.string.report_private_on), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    editor.putBoolean("private_mode", true);
                                    editor.commit();
                                }
                            }).setNegativeButton(getString(R.string.report_private_off), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    editor.putBoolean("private_mode", false);
                                    editor.commit();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                if(item.equalsIgnoreCase(getResources().getString(R.string.licenses))){
                    startActivity(new Intent(getApplicationContext(), Licenses.class));
                }
                if (item.equalsIgnoreCase(getString(R.string.dsgvo_title))) {
                    Intent i = new Intent(getApplicationContext(), DSGVOInfo.class);
                    i.putExtra("hastoaccept", false);
                    startActivity(i);
                }
                if(item.equalsIgnoreCase(getResources().getString(R.string.impressum))){
                    String url = "https://dienstapp.raffaelhahn.de/impressum.html#impr";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
                if(item.equalsIgnoreCase(getResources().getString(R.string.reset))){
                    builder
                            .setTitle(getResources().getString(R.string.resetdialog_title))
                            .setMessage(getResources().getString(R.string.resetdialog_message))
                            .setInverseBackgroundForced(true)
                            .setIcon(null)
                            .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    editor.clear();
                                    editor.commit();

                                    SharedPreferences sp1 = getSharedPreferences("Splash", MODE_PRIVATE);
                                    SharedPreferences.Editor editor1 = sp1.edit();
                                    editor1.clear();
                                    editor1.commit();

                                    

                                    final Snackbar snackbar = Snackbar
                                            .make(findViewById(R.id.drawer_10), getString(R.string.data_cleared), Snackbar.LENGTH_SHORT);
                                    snackbar.setAction("OK", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            snackbar.dismiss();
                                        }
                                    });

                                    snackbar.show();
                                }
                            })
                            .setNegativeButton(getString(R.string.no), null)
                            .show();
                }

                if(item.equalsIgnoreCase(getResources().getString(R.string.shortcut_entry))){
                    final CharSequence[] items = {getString(R.string.title_section1),getString(R.string.title_section8),getString(R.string.title_section3),getString(R.string.title_section4),getString(R.string.title_section7)};
// arraylist to keep the selected items
                    final ArrayList<Integer> seletedItems=new ArrayList<>();

                    AlertDialog dialog = new AlertDialog.Builder(Settings.this)
                            .setTitle(getString(R.string.shortcuts_dialog_title))
                            .setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                                    if (isChecked) {
                                        seletedItems.add(indexSelected);
                                    } else if (seletedItems.contains(indexSelected)) {
                                        seletedItems.remove(Integer.valueOf(indexSelected));
                                    }
                                }
                            }).setPositiveButton(getString(R.string.action_save), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    String s = "";
                                    for(int i : seletedItems){
                                        s = s+i;
                                    }
                                    editor.putString("SHORTCUTS", s);
                                    editor.commit();
                                    Shortcuts.updateShortcuts(s, Settings.this, false);
                                }
                            }).setNegativeButton(getString(R.string.delete_cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {}
                            }).create();
                    dialog.show();
                }



            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

}
