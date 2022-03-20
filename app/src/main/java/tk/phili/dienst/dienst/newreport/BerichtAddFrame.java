package tk.phili.dienst.dienst.newreport;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import tk.phili.dienst.dienst.R;
import tk.phili.dienst.dienst.utils.MenuTintUtils;

public class BerichtAddFrame extends AppCompatActivity {

    Calendar myCalendar = null;
    DatePickerDialog.OnDateSetListener date = null;
    public SharedPreferences sp;
    private SharedPreferences.Editor editor;
    int id = 0;

    boolean collapsed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_bericht_add_frame);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_add_bericht);
        setSupportActionBar(toolbar);

        sp = getSharedPreferences("MainActivity", MODE_PRIVATE);
        editor = sp.edit();

        toolbar.setTitle(R.string.add_bericht);



        id = getIntent().getIntExtra("id", Integer.MAX_VALUE);

        if(id == Integer.MAX_VALUE) {
            ((CollapsingToolbarLayout)findViewById(R.id.toolbar_layout_bericht)).setTitle(getString(R.string.add_bericht));
            id = 0;
            if (sp.contains("BERICHTE")) {
                Set<String> berichte = sp.getStringSet("BERICHTE", null);
                for (String s : berichte) {
                    String inti = s.split(";")[0];
                    Integer inte = Integer.parseInt(inti);
                    if (id <= inte) {
                        id = inte + 1;
                    }
                }
            }
            myCalendar = Calendar.getInstance();
            ((EditText)findViewById(R.id.add_bericht_date)).setText(DateFormat.getDateInstance(DateFormat.DEFAULT).format(new Date()));
        }else{
            ((CollapsingToolbarLayout)findViewById(R.id.toolbar_layout_bericht)).setTitle(getString(R.string.change_bericht));
            //id+";"+date+";"+hours+";"+minutes+";"+abgaben+";"+rück+";"+videos+";"+studien
            Set<String> berichte = sp.getStringSet("BERICHTE", null);
            for (String s : berichte) {
                Integer idThis = Integer.parseInt(s.split(";")[0]);
                if(idThis.intValue() == id){
                    String date = s.split(";")[1];
                    String hours = s.split(";")[2];
                    String minutes = s.split(";")[3];
                    String abgaben = s.split(";")[4];
                    String rück = s.split(";")[5];
                    String videos = s.split(";")[6];
                    String studien = s.split(";")[7];
                    String anmerkungen = "";
                    try {
                        anmerkungen = s.split(";")[8];
                    }catch (Exception e){}

                    DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
                    Date dateObj = null;
                    try {
                        dateObj = df.parse(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dateObj);
                    myCalendar = cal;

                    DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
                    Date dateIN = null;
                    try {
                        dateIN = format.parse(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT);
                    String dateOUT = dateFormatter.format(dateIN);

                    ((EditText)findViewById(R.id.add_bericht_date)).setText(dateOUT);
                    ((EditText)findViewById(R.id.add_bericht_hours)).setText(hours.equals("0") ? "" : hours);
                    ((EditText)findViewById(R.id.add_bericht_minutes)).setText(minutes.equals("00") ? "" : minutes);
                    ((EditText)findViewById(R.id.add_bericht_abgaben)).setText(abgaben.equals("0") ? "" : abgaben);
                    ((EditText)findViewById(R.id.add_bericht_returns)).setText(rück.equals("0") ? "" : rück);
                    ((EditText)findViewById(R.id.add_bericht_videos)).setText(videos.equals("0") ? "" : videos);
                    ((EditText)findViewById(R.id.add_bericht_studies)).setText(studien.equals("0") ? "" : studien);
                    ((EditText)findViewById(R.id.add_bericht_annotation)).setText(anmerkungen);
                }
            }
        }



        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                findViewById(R.id.add_bericht_hours).requestFocus();
                int month = monthOfYear+1;
                updateDate();
            }

        };



        findViewById(R.id.add_bericht_date).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(ViewCompat.isAttachedToWindow(v)) {
                    if (hasFocus) {
                        DatePickerDialog dpd = new DatePickerDialog(BerichtAddFrame.this, date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH));
                        dpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                findViewById(R.id.add_bericht_hours).requestFocus();
                            }
                        });
                        dpd.show();
                        //v.clearFocus();
                    }
                }
            }
        });

        ((EditText)findViewById(R.id.add_bericht_minutes)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String text = ((EditText)findViewById(R.id.add_bericht_minutes)).getText().toString();
                if(!text.isEmpty()){
                    try {
                        int i = Integer.parseInt(text);
                        if (i > 59) {
                            ((EditText) findViewById(R.id.add_bericht_minutes)).setTextColor(Color.RED);
                        } else {
                            ((EditText) findViewById(R.id.add_bericht_minutes)).setTextColor(Color.BLACK);
                        }
                    }catch(Exception e){
                        ((EditText) findViewById(R.id.add_bericht_minutes)).setTextColor(Color.RED);
                        e.printStackTrace();
                    }
                }

            }
        });

        ((EditText)findViewById(R.id.add_bericht_hours)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String text = ((EditText)findViewById(R.id.add_bericht_hours)).getText().toString();
                if(!text.isEmpty()){
                    try {
                        int i = Integer.parseInt(text);
                        if(i > 24){
                            ((EditText)findViewById(R.id.add_bericht_hours)).setTextColor(Color.RED);
                        }else{
                            ((EditText)findViewById(R.id.add_bericht_hours)).setTextColor(Color.BLACK);
                        }
                    }catch(Exception e){
                        ((EditText)findViewById(R.id.add_bericht_hours)).setTextColor(Color.RED);
                        e.printStackTrace();
                    }
                }

            }
        });

        final EditText etDesc = ((EditText)findViewById(R.id.add_bericht_annotation));
        Linkify.addLinks(etDesc, Linkify.WEB_URLS);
        CharSequence text = TextUtils.concat(etDesc.getText(), "\u200B");
        if(etDesc.getText().toString().isEmpty()){
            text = "";
        }
        etDesc.setText(text);
        etDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Linkify.addLinks(etDesc, Linkify.WEB_URLS);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = ((EditText)findViewById(R.id.add_bericht_annotation)).getText().toString();
                if(!text.isEmpty()){
                    if(text.contains(";")){
                        etDesc.setText(text.replace(";", ""));
                    }
                }

            }
        });
        //etDesc.setLinksClickable(true);
        //etDesc.setAutoLinkMask(Linkify.WEB_URLS);
        //etDesc.setMovementMethod(LinkMovementMethod.getInstance());
//If the edit text contains previous text with potential links


        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        ((AppBarLayout)findViewById(R.id.app_bar_rueck)).addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (Math.abs(verticalOffset)-appBarLayout.getTotalScrollRange() == 0) {
                    collapsed = true;
                    supportInvalidateOptionsMenu();
                }
                else {
                    collapsed = false;
                    supportInvalidateOptionsMenu();
                }
            }
        });

    }

    public void showError(final String messagebox) {
        AlertDialog.Builder builder = new AlertDialog.Builder(BerichtAddFrame.this);
        builder.setTitle(getString(R.string.error));
        builder.setMessage(messagebox);

        String positiveText = "OK";
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        String negativeText = "";
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(false);

    }

    public void save(){
        //
        String text = ((EditText)findViewById(R.id.add_bericht_minutes)).getText().toString();
        if(!text.isEmpty()) {
            int i = Integer.parseInt(text);
            if (i > 59) {
                showError(getString(R.string.onehour));
                return;
            }
        }
        //
        String text2 = ((EditText)findViewById(R.id.add_bericht_hours)).getText().toString();
        if(!text2.isEmpty()) {
            int i = Integer.parseInt(text2);
            if (i > 24) {
                showError(getString(R.string.twentyfourhours));
                return;
            }
        }
        //
        DateFormat formatter = DateFormat.getDateInstance(DateFormat.DEFAULT);
        Date dateIN = null;
        try {
            dateIN = formatter.parse(((EditText)findViewById(R.id.add_bericht_date)).getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);



        String date = format.format(dateIN);
        String hours = "0";
        String minutes = "0";
        String abgaben = "0";
        String rück = "0";
        String videos = "0";
        String studien = "0";
        String anmerkungen = "";

        if(!((EditText)findViewById(R.id.add_bericht_hours)).getText().toString().isEmpty()){
            hours = ""+ ((EditText)findViewById(R.id.add_bericht_hours)).getText().toString();
        }
        if(!((EditText)findViewById(R.id.add_bericht_minutes)).getText().toString().isEmpty()){
            minutes = ""+ ((EditText)findViewById(R.id.add_bericht_minutes)).getText().toString();
        }
        if(!((EditText)findViewById(R.id.add_bericht_abgaben)).getText().toString().isEmpty()){
            abgaben = ""+((EditText)findViewById(R.id.add_bericht_abgaben)).getText().toString();
        }
        if(!((EditText)findViewById(R.id.add_bericht_returns)).getText().toString().isEmpty()){
            rück = ""+((EditText)findViewById(R.id.add_bericht_returns)).getText().toString();
        }
        if(!((EditText)findViewById(R.id.add_bericht_videos)).getText().toString().isEmpty()){
            videos = ""+ ((EditText)findViewById(R.id.add_bericht_videos)).getText().toString();
        }
        if(!((EditText)findViewById(R.id.add_bericht_studies)).getText().toString().isEmpty()){
            studien = ""+ ((EditText)findViewById(R.id.add_bericht_studies)).getText().toString();
        }
        if(!((EditText)findViewById(R.id.add_bericht_annotation)).getText().toString().replace("\u200B", "").isEmpty()){
            anmerkungen = ""+ ((EditText)findViewById(R.id.add_bericht_annotation)).getText().toString().replace("\u200B", "");
        }

        if(Integer.parseInt(minutes) < 10){
            minutes = "0"+minutes;
        }


        Set<String> berichte = new HashSet<>();
        if(sp.contains("BERICHTE")) {
            Set<String> berichteold = sp.getStringSet("BERICHTE", null);
            if(berichteold != null && !(berichteold.isEmpty())){
                for(String s : berichteold){
                    if(!s.startsWith(id+";"))
                    berichte.add(s);
                }
            }
        }

        String toset = id+";"+date+";"+hours+";"+minutes+";"+abgaben+";"+rück+";"+videos+";"+studien+";"+anmerkungen;
        berichte.add(toset);
        editor.putStringSet("BERICHTE", berichte);
        editor.commit();

        finish();
    }



    public void updateDate(){

        /*String rday = day;
        String rmonth = month;
        String ryear = year;

        if(day.length() == 1){
            rday = "0"+rday;
        }
        if(month.length() == 1){
            rmonth = "0"+rmonth;
        }
        ((EditText)findViewById(R.id.add_bericht_date)).setText(rday+"."+rmonth+"."+ryear);*/
        ((EditText)findViewById(R.id.add_bericht_date)).setText(DateFormat.getDateInstance(DateFormat.DEFAULT).format(myCalendar.getTime()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(collapsed)
            getMenuInflater().inflate(R.menu.bericht_add_frame, menu);
        MenuTintUtils.tintAllIcons(menu, Color.WHITE);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            save();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
