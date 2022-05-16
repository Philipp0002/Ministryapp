package tk.phili.dienst.dienst.report;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import tk.phili.dienst.dienst.R;
import tk.phili.dienst.dienst.uiwrapper.FragmentCommunicationPass;
import tk.phili.dienst.dienst.uiwrapper.WrapperActivity;
import tk.phili.dienst.dienst.utils.MenuTintUtils;

public class ReportFragment extends Fragment implements Toolbar.OnMenuItemClickListener{

    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;
    public static AlertDialog.Builder builder;

    Calendar calendarShow;

    RoundCornerProgressBar rpb;
    RecyclerView reportsRecycler;
    RecyclerView summarizedRecycler;
    ReportRecyclerAdapter reportRecyclerAdapter;
    ReportRecyclerAdapter summarizedRecyclerAdapter;
    ConstraintLayout goalView;

    ReportManager reportManager;
    Toolbar toolbar;
    ExtendedFloatingActionButton addBerichtButton;
    RelativeLayout upswipy;
    MaterialButton swipeUpShare;
    SlidingUpPanelLayout slidingLayout;
    MaterialButton swipeUpCarryOver;
    TextView goalText;
    FragmentCommunicationPass fragmentCommunicationPass;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentCommunicationPass = (FragmentCommunicationPass) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_report, null);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        toolbar = view.findViewById(R.id.toolbar);
        addBerichtButton = view.findViewById(R.id.addBerichtButton);
        upswipy = view.findViewById(R.id.upswipy);
        swipeUpShare = view.findViewById(R.id.swipe_up_share);
        slidingLayout = view.findViewById(R.id.sliding_layout);
        swipeUpCarryOver = view.findViewById(R.id.swipe_up_carryover);
        goalText = view.findViewById(R.id.goaltext);

        fragmentCommunicationPass.onDataPass(this, WrapperActivity.FRAGMENTPASS_TOOLBAR, toolbar);

        toolbar.inflateMenu(R.menu.main);
        MenuTintUtils.tintAllIcons(toolbar.getMenu(), Color.WHITE);
        toolbar.setOnMenuItemClickListener(this);


        sp = getContext().getSharedPreferences("MainActivity", Context.MODE_PRIVATE);
        editor = sp.edit();

        if (!sp.contains("reports") && sp.contains("BERICHTE")) {
            ReportFormatConverter.convertToNewFormat(sp);
        }
        reportManager = new ReportManager(getContext());


        rpb = view.findViewById(R.id.progress_goal);

        reportsRecycler = view.findViewById(R.id.bericht_liste);
        summarizedRecycler = view.findViewById(R.id.swipe_up_bericht);
        goalView = view.findViewById(R.id.goalview);
        initList();

        if (sp.getBoolean("private_mode", false)) {
            view.findViewById(R.id.private_block).setVisibility(View.VISIBLE);
            view.findViewById(R.id.private_disable).setOnClickListener(__ -> view.findViewById(R.id.private_block).setVisibility(View.GONE));
        }


        final SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy");

        calendarShow = Calendar.getInstance();

        final TextView tbv = view.findViewById(R.id.toolbar_title);
        tbv.setText(dateFormat.format(calendarShow.getTime()));


        tbv.setOnClickListener(v -> {

            MonthYearPickerDialogFragment dialogFragment = MonthYearPickerDialogFragment.getInstance(calendarShow.get(Calendar.MONTH), calendarShow.get(Calendar.YEAR), getString(R.string.select_month_year));

            dialogFragment.setOnDateSetListener((year, monthOfYear) -> {

                calendarShow.set(Calendar.MONTH, monthOfYear);
                calendarShow.set(Calendar.YEAR, year);
                tbv.setText(dateFormat.format(calendarShow.getTime()));
                updateList();
            });

            dialogFragment.show(getActivity().getSupportFragmentManager(), null);

        });


        ((SlidingUpPanelLayout) view.findViewById(R.id.sliding_layout)).setParallaxOffset(100);


        addBerichtButton.setOnClickListener(v -> {
            Intent mainIntent = new Intent(getContext(), ReportAddFrame.class);
            float x = v.getX() + v.getWidth() / 2;
            float y = v.getY() + v.getHeight() / 2;
            mainIntent.putExtra("xReveal", x);
            mainIntent.putExtra("yReveal", y);

            mainIntent.putExtra("id", Integer.MAX_VALUE);

            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(getActivity(), v, "bericht_add_frame");
            startActivity(mainIntent, options.toBundle());
        });


        Animation anim = android.view.animation.AnimationUtils.loadAnimation(addBerichtButton.getContext(), R.anim.slide_in_bottom);
        anim.setDuration(1000L);
        upswipy.startAnimation(anim);

        swipeUpShare.setOnClickListener(v -> {
            if (swipeUpShare.getAlpha() != 0F) {
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);


                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

                alert.setTitle(getString(R.string.title_section6));
                alert.setMessage(getString(R.string.bericht_type_name));

                View input_view = LayoutInflater.from(getContext())
                        .inflate(R.layout.bericht_send_input, null, false);
                final EditText input = ((TextInputLayout) input_view.findViewById(R.id.name_text_field)).getEditText();
                alert.setView(input_view);
                input.setText(sp.getString("lastSendName", ""));

                alert.setPositiveButton(getString(R.string.title_activity_senden), (dialog, whichButton) -> {
                    String value = input.getText().toString();
                    if (!value.isEmpty()) {
                        sendReport(value);
                    }

                });

                alert.setNegativeButton(getString(R.string.gebiet_add_cancel), (dialog, whichButton) -> {
                });

                alert.show();
            }
        });

        swipeUpCarryOver.setOnClickListener(v -> {
            if (swipeUpCarryOver.getAlpha() != 0F) {
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

                alert.setTitle(getString(R.string.carryover));
                alert.setMessage(getString(R.string.carryover_msg));

                alert.setPositiveButton(getString(R.string.ok), (dialog, whichButton) -> {
                    if (carry()) {
                        updateList();
                    }
                });

                alert.setNegativeButton(getString(R.string.delete_cancel), (dialog, whichButton) -> {
                });

                alert.show();
            }
        });

        slidingLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                view.findViewById(R.id.swipe_up_text).setAlpha(1 - slideOffset);
                view.findViewById(R.id.swipe_up_lefticon).setRotation(slideOffset * 180);
                view.findViewById(R.id.swipe_up_righticon).setAlpha(1 - slideOffset);
                swipeUpShare.setAlpha(slideOffset);
                swipeUpCarryOver.setAlpha(slideOffset);

            }


            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

            }
        });

        updateList();
    }


    @Override
    public void onResume() {
        super.onResume();
        updateList();
    }

    public boolean carry() {
        Report summarizedReport = reportManager.getSummary(calendarShow.get(Calendar.MONTH) + 1, calendarShow.get(Calendar.YEAR));

        int minutes = (int) summarizedReport.getMinutes() % 60;
        if (minutes != 0) {
            long nextId = reportManager.getNextId();

            LocalDate date1 = LocalDate.of(calendarShow.get(Calendar.YEAR), calendarShow.get(Calendar.MONTH) + 1, 1);
            Report report1 = new Report();
            report1.setId(nextId);
            report1.setMinutes(-minutes);
            report1.setDate(date1);
            report1.setType(Report.Type.CARRY_SUB);

            LocalDate date2 = LocalDate.of(calendarShow.get(Calendar.YEAR), calendarShow.get(Calendar.MONTH) + 1, 1);
            date2 = date2.plusMonths(1);
            Report report2 = new Report();
            report2.setId(nextId);
            report2.setMinutes(minutes);
            report2.setDate(date2);
            report2.setType(Report.Type.CARRY_ADD);

            reportManager.createReport(report1);
            reportManager.createReport(report2);
            return true;
        } else {
            return false;
        }
    }

    public void initList() {
        reportRecyclerAdapter = new ReportRecyclerAdapter(getContext(), Arrays.asList()) {
            @Override
            public void onClicked(Report report, View view) {

                if (slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    return;
                }

                if (report.getType() != Report.Type.NORMAL) {
                    return;
                }
                Intent mainIntent = new Intent(getActivity(), ReportAddFrame.class);
                float x = view.getX() + view.getWidth() / 2;
                float y = view.getY() + view.getHeight() / 2;
                mainIntent.putExtra("xReveal", x);
                mainIntent.putExtra("yReveal", y);

                mainIntent.putExtra("id", report.getId());

                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(getActivity(), view, "bericht_add_frame");
                startActivity(mainIntent, options.toBundle());
            }
        };
        reportRecyclerAdapter.setHasStableIds(true);
        reportsRecycler.setAdapter(reportRecyclerAdapter);
        reportsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getAdapterPosition();
                if (deleteReport(reportRecyclerAdapter.reports.get(pos))) {
                    updateList();
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(reportsRecycler);

        reportsRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    if (addBerichtButton.isExtended())
                        addBerichtButton.shrink();
                } else {
                    if (!addBerichtButton.isExtended())
                        addBerichtButton.extend();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    public void updateList() {
        if (reportRecyclerAdapter == null) {
            initList();
        }
        List<Report> reports = reportManager.getReports(calendarShow.get(Calendar.MONTH) + 1, calendarShow.get(Calendar.YEAR));
        reportRecyclerAdapter.reports = reports;
        reportRecyclerAdapter.notifyDataSetChanged();


        updateSummary();
    }

    public boolean deleteReport(Report report) {
        boolean deleted = reportManager.deleteReport(report);
        if (deleted) {
            Snackbar.make(getView().findViewById(R.id.coord), R.string.bericht_undo_1, Snackbar.LENGTH_LONG)
                    .setAction(R.string.bericht_undo_2, v -> {
                        reportManager.createReport(report);
                        updateList();
                    }).show();
        }
        return deleted;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_goal) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());

            View input_view = LayoutInflater.from(getContext())
                    .inflate(R.layout.goal_set_input, null, false);
            final EditText edt = ((TextInputLayout) input_view.findViewById(R.id.name_text_field)).getEditText();
            dialogBuilder.setView(input_view);

            if (sp.contains("goal") && !sp.getString("goal", "0").equals("0")) {
                edt.setText(sp.getString("goal", "0"));
            }

            dialogBuilder.setTitle(getString(R.string.goal_set));
            dialogBuilder.setMessage(getString(R.string.goal_msg));
            dialogBuilder.setPositiveButton(getString(R.string.OK), (dialog, whichButton) -> {
                try {
                    Integer.parseInt(edt.getText().toString());
                } catch (Exception e) {
                    Toast.makeText(getContext(), getString(R.string.goal_invalid), Toast.LENGTH_LONG).show();
                    return;
                }
                editor.putString("goal", edt.getText().toString());
                editor.commit();
                updateSummary();
            });
            dialogBuilder.setNegativeButton(getString(R.string.goal_no), (dialog, whichButton) -> {
                editor.putString("goal", "0");
                editor.commit();
                updateSummary();
            });
            AlertDialog b = dialogBuilder.create();
            b.show();
            return true;
        }
        return false;
    }

    public static class ReverseInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float paramFloat) {
            return Math.abs(paramFloat - 1f);
        }
    }

    public void updateSummary() {
        Report summarizedReport = reportManager.getSummary(calendarShow.get(Calendar.MONTH) + 1, calendarShow.get(Calendar.YEAR));

        summarizedRecyclerAdapter = new ReportRecyclerAdapter(getContext(), Arrays.asList(summarizedReport));
        summarizedRecycler.setAdapter(summarizedRecyclerAdapter);
        summarizedRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        if (summarizedReport.getMinutes() % 60 == 0) {
            swipeUpCarryOver.setVisibility(View.GONE);
        } else {
            swipeUpCarryOver.setVisibility(View.VISIBLE);
        }

        if (sp.contains("goal") && !"0".equals(sp.getString("goal", "0"))) {
            goalView.setVisibility(View.VISIBLE);
            int goal = Integer.parseInt(sp.getString("goal", "0"));
            float percent = (float) summarizedReport.getMinutes() / ((float) goal * (float) 60) * 100;
            rpb.setProgress(percent);
            if ((int) percent == 100) {
                goalText.setText(getString(R.string.goal_text_reached));
            }
            if ((goal * 60) - summarizedReport.getMinutes() > 0) {
                if (summarizedReport.getMinutes() % 60 == 0) {
                    if ((goal) - (summarizedReport.getMinutes() / 60) == 1) {
                        goalText.setText(getString(R.string.goal_text_1).replace("%a", "" + (goal - (summarizedReport.getMinutes() / 60))));
                    } else {
                        goalText.setText(getString(R.string.goal_text_mult).replace("%a", "" + (goal - (summarizedReport.getMinutes() / 60))));
                    }
                } else {
                    Report report = new Report();
                    report.setMinutes((goal * 60) - summarizedReport.getMinutes());
                    report.setType(Report.Type.NORMAL);
                    goalText.setText(getString(R.string.goal_text_minutes).replace("%a", report.getFormattedHoursAndMinutes(getContext())[0]));
                }
            } else if ((goal * 60) - summarizedReport.getMinutes() < 0) {
                goalText.setText(getString(R.string.goal_text_reached_more).replace("%a", LocalTime.MIN.plus(
                        Duration.ofMinutes(summarizedReport.getMinutes())
                ).toString()));
            }
        } else {
            goalView.setVisibility(View.GONE);
        }

    }


    public void sendReport(String name) {
        editor.putString("lastSendName", name);
        editor.commit();
        if (!name.matches("")) {
            Report summarizedReport = reportManager.getSummary(calendarShow.get(Calendar.MONTH) + 1, calendarShow.get(Calendar.YEAR));
            String text = getResources().getString(R.string.reportfor) + name + "\n" + getResources().getString(R.string.reportmonth) + new DateFormatSymbols().getMonths()[calendarShow.get(Calendar.MONTH)] + "\n==============\n";

            String[] formattedTime = summarizedReport.getFormattedHoursAndMinutes(getContext());
            text = text + formattedTime[1] + ": " + formattedTime[0] + "\n";

            text = text + getResources().getString(R.string.reportplace) + summarizedReport.getPlacements() + "\n";
            text = text + getResources().getString(R.string.reportvisits) + summarizedReport.getReturnVisits() + "\n";
            text = text + getResources().getString(R.string.reportvideo) + summarizedReport.getVideos() + "\n";
            text = text + getResources().getString(R.string.reportstudy) + summarizedReport.getBibleStudies() + "\n";
            text = text + "==============\n" + getResources().getString(R.string.reportsentvia);
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, text);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, R.string.sendreport_text + ""));
        }
    }

    public int getMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH);
    }

    public int getYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }


}
