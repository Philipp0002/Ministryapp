package tk.phili.dienst.dienst.notes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import tk.phili.dienst.dienst.R;
import tk.phili.dienst.dienst.drawer.Drawer;

public class Notizen extends AppCompatActivity{

    public SharedPreferences spp;
    public SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private EditText notesEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity3);
        setTitle(getResources().getString(R.string.title_section3));
        sp = getSharedPreferences("MainActivity3",  Context.MODE_PRIVATE );
        editor = sp.edit();
        spp = getSharedPreferences("Notizen", MODE_PRIVATE); //LEGACY NOTES SUPPORT
        if(spp.contains("NOTES")){
            editor.putString("NOTES", sp.getString("NOTES","")+"\n"+spp.getString("NOTES", ""));
            editor.commit();
            SharedPreferences.Editor edit2 = spp.edit();
            edit2.remove("NOTES");
            edit2.apply();
        }

        notesEditText = findViewById(R.id.notes);
        if(sp.contains("NOTES")){
            notesEditText.setText(sp.getString("NOTES", "0"));
        }

        /////////////////DRAWER/////////////////////////////////////////
        // Initializing Toolbar and setting it as the actionbar
        Toolbar toolbar = findViewById(R.id.toolbar_2);
        setSupportActionBar(toolbar);
        toolbar.bringToFront();
        Drawer.addDrawer(this, toolbar, 3);

        Linkify.addLinks(notesEditText, Linkify.WEB_URLS);
        CharSequence text = TextUtils.concat(notesEditText.getText(), "\u200B");
        notesEditText.setText(text);

        notesEditText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                EditText add_button = findViewById(R.id.notes);
                editor.putString("NOTES", add_button.getText().toString().replace("\u200B", "") + "");
                editor.commit();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Linkify.addLinks(notesEditText, Linkify.WEB_URLS);
            }
        });
        notesEditText.requestFocus();
    }

    @Override
    protected void onResume() {
        InputMethodManager imm = (InputMethodManager)   getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        super.onResume();
    }
}
