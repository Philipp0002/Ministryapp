package tk.phili.dienst.dienst;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class Notizen extends AppCompatActivity{


    public SharedPreferences spp;
    public SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private Toolbar toolbar;
    MenuItem bericht;

    private ActionBarDrawerToggle actionbartoggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity3);
        setTitle(getResources().getString(R.string.title_section3));
        sp = getSharedPreferences("MainActivity3",  Context.MODE_PRIVATE );
        editor = sp.edit();
        spp = getSharedPreferences("Notizen", MODE_PRIVATE); //ALTE NOTIZEN //WEGEN MAILS
        if(spp.contains("NOTES")){
            editor.putString("NOTES", sp.getString("NOTES","")+"\n"+spp.getString("NOTES", ""));
            editor.commit();
            SharedPreferences.Editor edit2 = spp.edit();
            edit2.remove("NOTES");
            edit2.commit();
        }
        if(sp.contains("NOTES")){
            EditText add_button = (EditText) findViewById(R.id.notes);
            add_button.setText(sp.getString("NOTES", "0"));
        }



        /////////////////DRAWER/////////////////////////////////////////
        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_2);
        setSupportActionBar(toolbar);
        toolbar.bringToFront();
        Drawer.addDrawer(this, toolbar, 3);

        EditText et = (EditText)findViewById(R.id.notes);
        et.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                // you can call or do what you want with your EditText here
                EditText add_button = (EditText) findViewById(R.id.notes);
                editor.putString("NOTES", add_button.getText() + "");
                editor.commit();

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity3, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

            if (actionbartoggle.onOptionsItemSelected(item)) {
                return true;
            }
         /*   if(item.getItemId() == R.id.action_save) {
                EditText add_button = (EditText) findViewById(R.id.notes);
                editor.putString("NOTES", add_button.getText() + "");
                editor.commit();
                return true;
            }*/



        return super.onOptionsItemSelected(item);
    }


}
