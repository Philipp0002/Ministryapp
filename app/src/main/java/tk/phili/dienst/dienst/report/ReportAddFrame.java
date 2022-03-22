package tk.phili.dienst.dienst.report;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import tk.phili.dienst.dienst.R;
import tk.phili.dienst.dienst.utils.MenuTintUtils;
import tk.phili.dienst.dienst.utils.Utils;

public class ReportAddFrame extends AppCompatActivity {

    Calendar myCalendar = null;
    DatePickerDialog.OnDateSetListener date = null;

    boolean collapsed = false;

    private Report report;
    ReportManager reportManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bericht_add_frame);

        final Toolbar toolbar = findViewById(R.id.toolbar_add_bericht);
        setSupportActionBar(toolbar);

        toolbar.setTitle(R.string.add_bericht);

        reportManager = new ReportManager(this);

        long id = getIntent().getLongExtra("id", Long.MAX_VALUE);

        if (id == Long.MAX_VALUE) {
            ((CollapsingToolbarLayout) findViewById(R.id.toolbar_layout_bericht)).setTitle(getString(R.string.add_bericht));

            myCalendar = Calendar.getInstance();
            ((EditText) findViewById(R.id.add_bericht_date)).setText(DateFormat.getDateInstance(DateFormat.DEFAULT).format(new Date()));

            long nextId = reportManager.getNextId();
            report = new Report();
            report.setId(nextId);
            report.setDate(LocalDate.now());
            report.setType(Report.Type.NORMAL);
        } else {
            ((CollapsingToolbarLayout) findViewById(R.id.toolbar_layout_bericht)).setTitle(getString(R.string.change_bericht));
            report = reportManager.getReportById(id);
        }

        myCalendar = new GregorianCalendar();
        myCalendar.set(Calendar.YEAR, report.getDate().getYear());
        myCalendar.set(Calendar.MONTH, report.getDate().getMonthValue()-1);
        myCalendar.set(Calendar.DAY_OF_MONTH, report.getDate().getDayOfMonth());

        int hours = report.getMinutes() < 60 ? 0 : (int) (report.getMinutes() / 60);
        long minutes = report.getMinutes() % 60;
        int placements = report.getPlacements();
        int returnVisits = report.getReturnVisits();
        int videos = report.getVideos();
        int bibleStudies = report.getBibleStudies();

        ((EditText) findViewById(R.id.add_bericht_date)).setText(report.getFormattedDate(this));
        ((EditText) findViewById(R.id.add_bericht_hours)).setText(hours == 0 ? "" : Integer.toString(hours));
        ((EditText) findViewById(R.id.add_bericht_minutes)).setText(minutes == 0 ? "" : Long.toString(hours));
        ((EditText) findViewById(R.id.add_bericht_abgaben)).setText(placements == 0 ? "" : Integer.toString(placements));
        ((EditText) findViewById(R.id.add_bericht_returns)).setText(returnVisits == 0 ? "" : Integer.toString(returnVisits));
        ((EditText) findViewById(R.id.add_bericht_videos)).setText(videos == 0 ? "" : Integer.toString(videos));
        ((EditText) findViewById(R.id.add_bericht_studies)).setText(bibleStudies == 0 ? "" : Integer.toString(bibleStudies));
        ((EditText) findViewById(R.id.add_bericht_annotation)).setText(report.getAnnotation());


        date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            findViewById(R.id.add_bericht_hours).requestFocus();
            updateDate();
        };


        findViewById(R.id.add_bericht_date).setOnFocusChangeListener((v, hasFocus) -> {
            if (ViewCompat.isAttachedToWindow(v)) {
                if (hasFocus) {
                    DatePickerDialog dpd = new DatePickerDialog(ReportAddFrame.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH));
                    dpd.setOnCancelListener(dialog -> findViewById(R.id.add_bericht_hours).requestFocus());
                    dpd.show();
                }
            }
        });

        ((EditText) findViewById(R.id.add_bericht_minutes)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = ((EditText) findViewById(R.id.add_bericht_minutes)).getText().toString();
                if (!text.isEmpty()) {
                    try {
                        int i = Integer.parseInt(text);
                        if (i > 59) {
                            ((EditText) findViewById(R.id.add_bericht_minutes)).setTextColor(Color.RED);
                        } else {
                            ((EditText) findViewById(R.id.add_bericht_minutes)).setTextColor(Color.BLACK);
                        }
                    } catch (Exception e) {
                        ((EditText) findViewById(R.id.add_bericht_minutes)).setTextColor(Color.RED);
                        e.printStackTrace();
                    }
                }

            }
        });

        ((EditText) findViewById(R.id.add_bericht_hours)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = ((EditText) findViewById(R.id.add_bericht_hours)).getText().toString();
                if (!text.isEmpty()) {
                    try {
                        int i = Integer.parseInt(text);
                        if (i > 24) {
                            ((EditText) findViewById(R.id.add_bericht_hours)).setTextColor(Color.RED);
                        } else {
                            ((EditText) findViewById(R.id.add_bericht_hours)).setTextColor(Color.BLACK);
                        }
                    } catch (Exception e) {
                        ((EditText) findViewById(R.id.add_bericht_hours)).setTextColor(Color.RED);
                        e.printStackTrace();
                    }
                }

            }
        });

        final EditText etDesc = findViewById(R.id.add_bericht_annotation);
        Linkify.addLinks(etDesc, Linkify.WEB_URLS);
        CharSequence text = TextUtils.concat(etDesc.getText(), "\u200B");
        if (etDesc.getText().toString().isEmpty()) {
            text = "";
        }
        etDesc.setText(text);
        etDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Linkify.addLinks(etDesc, Linkify.WEB_URLS);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = ((EditText) findViewById(R.id.add_bericht_annotation)).getText().toString();
                if (!text.isEmpty()) {
                    if (text.contains(";")) {
                        etDesc.setText(text.replace(";", ""));
                    }
                }

            }
        });


        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        ((AppBarLayout) findViewById(R.id.app_bar_rueck)).addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                    collapsed = true;
                    supportInvalidateOptionsMenu();
                } else {
                    collapsed = false;
                    supportInvalidateOptionsMenu();
                }
            }
        });

    }

    public void showError(final String messagebox) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ReportAddFrame.this);
        builder.setTitle(getString(R.string.error));
        builder.setMessage(messagebox);

        String positiveText = "OK";
        builder.setPositiveButton(positiveText,
                (dialog, which) -> {

                });

        String negativeText = "";
        builder.setNegativeButton(negativeText,
                (dialog, which) -> {
                    // negative button logic
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(false);

    }

    public void save() {

        String text = ((EditText) findViewById(R.id.add_bericht_minutes)).getText().toString();
        if (!text.isEmpty()) {
            int i = Integer.parseInt(text);
            if (i > 59) {
                showError(getString(R.string.onehour));
                return;
            }
        }

        String text2 = ((EditText) findViewById(R.id.add_bericht_hours)).getText().toString();
        if (!text2.isEmpty()) {
            int i = Integer.parseInt(text2);
            if (i > 24) {
                showError(getString(R.string.twentyfourhours));
                return;
            }
        }

        int minutes = Utils.parseInt(text).orElse(0);
        int hours = Utils.parseInt(text2).orElse(0);
        int placements = Utils.parseInt(((EditText) findViewById(R.id.add_bericht_abgaben)).getText().toString()).orElse(0);
        int returnVisits = Utils.parseInt(((EditText) findViewById(R.id.add_bericht_returns)).getText().toString()).orElse(0);
        int videos = Utils.parseInt(((EditText) findViewById(R.id.add_bericht_videos)).getText().toString()).orElse(0);
        int bibleStudies = Utils.parseInt(((EditText) findViewById(R.id.add_bericht_studies)).getText().toString()).orElse(0);
        String annotation = ((EditText) findViewById(R.id.add_bericht_annotation)).getText().toString();

        LocalDate date = LocalDateTime.ofInstant(myCalendar.toInstant(), ZoneId.systemDefault()).toLocalDate();

        report.setMinutes(minutes + (hours*60));
        report.setPlacements(placements);
        report.setReturnVisits(returnVisits);
        report.setVideos(videos);
        report.setBibleStudies(bibleStudies);
        report.setAnnotation(annotation);
        report.setDate(date);

        reportManager.deleteReport(report);
        reportManager.createReport(report);

        finish();
    }


    public void updateDate() {
        ((EditText) findViewById(R.id.add_bericht_date)).setText(DateFormat.getDateInstance(DateFormat.DEFAULT).format(myCalendar.getTime()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (collapsed)
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
