package tk.phili.dienst.dienst;

import android.animation.Animator;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import io.codetail.animation.ViewAnimationUtils;

public class BerichtAddFrame extends AppCompatActivity {

    Calendar myCalendar = null;
    DatePickerDialog.OnDateSetListener date = null;
    public SharedPreferences sp;
    private SharedPreferences.Editor editor;
    int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bericht_add_frame);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_add_bericht);
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

                    ((EditText)findViewById(R.id.add_bericht_date)).setText(date);
                    ((EditText)findViewById(R.id.add_bericht_hours)).setText(hours.equals("0") ? "" : hours);
                    ((EditText)findViewById(R.id.add_bericht_minutes)).setText(minutes.equals("00") ? "" : minutes);
                    ((EditText)findViewById(R.id.add_bericht_abgaben)).setText(abgaben.equals("0") ? "" : abgaben);
                    ((EditText)findViewById(R.id.add_bericht_returns)).setText(rück.equals("0") ? "" : rück);
                    ((EditText)findViewById(R.id.add_bericht_videos)).setText(videos.equals("0") ? "" : videos);
                    ((EditText)findViewById(R.id.add_bericht_studies)).setText(studien.equals("0") ? "" : studien);
                }
            }
        }

        myCalendar = Calendar.getInstance();

        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                int month = monthOfYear+1;
                updateDate(""+dayOfMonth, ""+month, ""+year);
            }

        };

        ((EditText)findViewById(R.id.add_bericht_date)).setText(android.text.format.DateFormat.format("dd.MM.yyyy", new java.util.Date()));
        ((EditText)findViewById(R.id.add_bericht_date)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(BerichtAddFrame.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
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
                    int i = Integer.parseInt(text);
                    if(i > 59){
                        ((EditText)findViewById(R.id.add_bericht_minutes)).setTextColor(Color.RED);
                    }else{
                        ((EditText)findViewById(R.id.add_bericht_minutes)).setTextColor(Color.BLACK);
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
                    int i = Integer.parseInt(text);
                    if(i > 24){
                        ((EditText)findViewById(R.id.add_bericht_hours)).setTextColor(Color.RED);
                    }else{
                        ((EditText)findViewById(R.id.add_bericht_hours)).setTextColor(Color.BLACK);
                    }
                }

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        final View view = findViewById(R.id.revealLayout);
        view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);
                if(getIntent().hasExtra("xReveal") && getIntent().hasExtra("yReveal")){
                    View myView = findViewById(R.id.revealLayout);

                    // get the center for the clipping circle
                    int cx = (int)getIntent().getFloatExtra("xReveal", 0);
                    int cy = (int)getIntent().getFloatExtra("yReveal", 0);

                    // get the final radius for the clipping circle
                    int dx = myView.getWidth();
                    int dy = myView.getHeight();
                    float finalRadius = (float) Math.hypot(dx, dy);

                    // Android native animator
                    Animator animator = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);
                    animator.setInterpolator(new AccelerateDecelerateInterpolator());
                    animator.setDuration(500);
                    animator.start();
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
        String date = ((EditText)findViewById(R.id.add_bericht_date)).getText().toString();
        String hours = "0";
        String minutes = "0";
        String abgaben = "0";
        String rück = "0";
        String videos = "0";
        String studien = "0";

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

        String toset = id+";"+date+";"+hours+";"+minutes+";"+abgaben+";"+rück+";"+videos+";"+studien;
        berichte.add(toset);
        editor.putStringSet("BERICHTE", berichte);
        editor.commit();

        finish();
    }



    public void updateDate(String day, String month, String year){

        String rday = day;
        String rmonth = month;
        String ryear = year;

        if(day.length() == 1){
            rday = "0"+rday;
        }
        if(month.length() == 1){
            rmonth = "0"+rmonth;
        }
        ((EditText)findViewById(R.id.add_bericht_date)).setText(rday+"."+rmonth+"."+ryear);
    }
}
