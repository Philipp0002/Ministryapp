package tk.phili.dienst.dienst.report;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.dewinjm.monthyearpicker.MonthFormat;
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialog;
import com.github.dewinjm.monthyearpicker.Presenter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import tk.phili.dienst.dienst.R;
import tk.phili.dienst.dienst.uiwrapper.FragmentCommunicationPass;
import tk.phili.dienst.dienst.uiwrapper.WrapperActivity;
import tk.phili.dienst.dienst.utils.AdaptiveUtils;
import tk.phili.dienst.dienst.utils.Utils;

public class ReportFragment extends Fragment implements Toolbar.OnMenuItemClickListener {

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy");

    private Calendar calendarShow;

    private Timer reportUpdateTimer;

    private LinearProgressIndicator goalProgress;
    private RecyclerView reportsRecycler;
    private ReportRecyclerAdapter reportRecyclerAdapter;
    private View goalView;

    private ReportManager reportManager;
    private Toolbar toolbar;
    private FloatingActionButton reportAddFab;
    private Button reportShareButton, carryOverButton, summaryButton, toolbarTitle;
    private TextView goalText;
    private FragmentCommunicationPass fragmentCommunicationPass;
    private View privateBlock, privateDisable, noReportView;

    private ReportTimer reportTimer;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentCommunicationPass = (FragmentCommunicationPass) context;
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_report, null);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = requireContext().getSharedPreferences("MainActivity", Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        reportTimer = new ReportTimer(getContext(), this);
        toolbar = view.findViewById(R.id.toolbar);
        reportAddFab = view.findViewById(R.id.reportAddFab);
        reportShareButton = view.findViewById(R.id.reportShare);
        carryOverButton = view.findViewById(R.id.reportCarryover);
        summaryButton = view.findViewById(R.id.reportSummary);
        goalText = view.findViewById(R.id.reportGoalText);
        privateBlock = view.findViewById(R.id.reportPrivateModeContainer);
        privateDisable = view.findViewById(R.id.reportPrivateModeDisable);
        toolbarTitle = view.findViewById(R.id.toolbar_title);
        noReportView = view.findViewById(R.id.reportsEmptyView);

        toolbar.setOnMenuItemClickListener(this);

        reportManager = new ReportManager(requireContext());

        goalProgress = view.findViewById(R.id.reportGoalProgress);

        reportsRecycler = view.findViewById(R.id.reportsRecycler);
        goalView = view.findViewById(R.id.reportGoalContainer);

        //Restore configuration
        if (savedInstanceState != null) {
            calendarShow = (Calendar) savedInstanceState.getSerializable("calendarShow");

            if (sp.getBoolean("private_mode", false) && !savedInstanceState.getBoolean("private_mode_clicked")) {
                privateBlock.setVisibility(View.VISIBLE);
                privateDisable.setOnClickListener(__ -> privateBlock.setVisibility(View.GONE));
            }
        } else {
            calendarShow = Calendar.getInstance();

            if (sp.getBoolean("private_mode", false)) {
                privateBlock.setVisibility(View.VISIBLE);
                privateDisable.setOnClickListener(__ -> privateBlock.setVisibility(View.GONE));
            }
        }

        initList();

        toolbarTitle.setText(dateFormat.format(calendarShow.getTime()));

        toolbarTitle.setOnClickListener(v -> showMonthYearPicker());


        reportAddFab.setOnClickListener(v -> showEditDialog(null));
        summaryButton.setOnClickListener(v -> showSummaryDialog());

        reportShareButton.setOnClickListener(v -> {
            View input_view = LayoutInflater.from(getContext())
                    .inflate(R.layout.report_send_input, null, false);
            final EditText input = ((TextInputLayout) input_view.findViewById(R.id.name_text_field)).getEditText();
            final MaterialSwitch detailedSwitch = input_view.findViewById(R.id.detailed_report_switch);
            final MaterialSwitch activeSwitch = input_view.findViewById(R.id.active_switch);
            input.setText(sp.getString("lastSendName", ""));
            detailedSwitch.setChecked(sp.getBoolean("lastSendDetailed", false));
            activeSwitch.setChecked(sp.getBoolean("lastSendActive", true));

            if (detailedSwitch.isChecked()) {
                activeSwitch.setVisibility(View.GONE);
            }

            detailedSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                activeSwitch.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                if (!isChecked) {
                    activeSwitch.setChecked(true);
                }
            });

            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.title_section6))
                    .setMessage(getString(R.string.report_input_name))
                    .setView(input_view)
                    .setPositiveButton(getString(R.string.title_activity_send), (dialog, whichButton) -> {
                        String value = input.getText().toString();
                        if (!value.isEmpty()) {
                            sendReport(value, detailedSwitch.isChecked(), activeSwitch.isChecked());
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show();
        });

        carryOverButton.setOnClickListener(v -> {
            if (carryOverButton.getAlpha() != 0F) {
                new MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialogCenterStyle)
                        .setIcon(R.drawable.ic_baseline_redo_24)
                        .setTitle(getString(R.string.carryover))
                        .setMessage(getString(R.string.carryover_msg))
                        .setPositiveButton(getString(R.string.ok), (dialog, whichButton) -> {
                            if (carry()) {
                                updateList();
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), null)
                        .show();
            }
        });


        reportUpdateTimer = new Timer();
        reportUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (reportTimer.getTimerState() == ReportTimer.TimerState.RUNNING
                        && getActivity() != null) {
                    getActivity().runOnUiThread(() -> updateList());
                }
            }
        }, 30 * 1000, 60 * 1000);

        goalView.setOnClickListener((a) -> openGoalEditDialog());

        updateList();
    }

    public void scrollToReportId(long reportId) {
        int reportPos = -1;
        int i = 0;
        for (Report report : reportRecyclerAdapter.reports) {
            if (report.getId() == reportId) {
                reportPos = i;
                break;
            }
            i++;
        }

        if (reportPos != -1) {
            reportsRecycler.smoothScrollToPosition(reportPos);
        }
    }

    private void showMonthYearPicker() {
        MonthYearPickerDialog simpleDatePickerDialog;

        try {
            Constructor<MonthYearPickerDialog> constructor = MonthYearPickerDialog.class.getDeclaredConstructor(Context.class,
                    int.class,
                    int.class,
                    int.class,
                    MonthFormat.class,
                    MonthYearPickerDialog.OnDateSetListener.class);

            constructor.setAccessible(true);
            simpleDatePickerDialog = constructor.newInstance(
                    requireContext(),
                    R.style.DialogStyleBasic,
                    calendarShow.get(Calendar.YEAR),
                    calendarShow.get(Calendar.MONTH),
                    MonthFormat.SHORT,
                    (MonthYearPickerDialog.OnDateSetListener) (year, monthOfYear) -> {
                        calendarShow.set(Calendar.MONTH, monthOfYear);
                        calendarShow.set(Calendar.YEAR, year);
                        toolbarTitle.setText(dateFormat.format(calendarShow.getTime()));
                        updateList();
                    });
            Method method = MonthYearPickerDialog.class.getDeclaredMethod("createTitle", String.class);
            method.setAccessible(true);
            method.invoke(simpleDatePickerDialog, getString(R.string.select_month_year));

            // Set calendar of library to dayofMonth = 1 to prevent bugs on the 31st of month
            Field presenterField = MonthYearPickerDialog.class.getDeclaredField("presenter");
            presenterField.setAccessible(true);
            Presenter presenter = (Presenter) presenterField.get(simpleDatePickerDialog);

            Field currentDateField = Presenter.class.getDeclaredField("currentDate");
            currentDateField.setAccessible(true);
            calendarShow.set(Calendar.DAY_OF_MONTH, 1);
            currentDateField.set(presenter, calendarShow);

            simpleDatePickerDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        reportUpdateTimer.cancel();
        reportUpdateTimer.purge();
        reportUpdateTimer = null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("calendarShow", calendarShow);
        if (privateBlock.getVisibility() != View.VISIBLE) {
            outState.putBoolean("private_mode_clicked", true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentCommunicationPass.onDataPass(this, WrapperActivity.FRAGMENTPASS_TOOLBAR, toolbar);
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
        reportRecyclerAdapter = new ReportRecyclerAdapter(getContext(), Collections.emptyList(), reportTimer) {
            @Override
            public void onClicked(Report report, View view) {

                if (report.getType() != Report.Type.NORMAL) {
                    return;
                }
                showEditDialog(report.getId());
            }
        };
        reportRecyclerAdapter.setHasStableIds(true);
        reportsRecycler.setAdapter(reportRecyclerAdapter);
        int screenWidth = getResources().getConfiguration().screenWidthDp;
        if (AdaptiveUtils.LARGE_SCREEN_WIDTH_SIZE <= screenWidth) {
            reportsRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        } else {
            reportsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public int getSwipeDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof ReportRecyclerAdapter.TimerHolder) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

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
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                /*if (dy > 0) {
                    if (reportAddFab.isExtended())
                        reportAddFab.shrink();
                } else {
                    if (!reportAddFab.isExtended())
                        reportAddFab.extend();
                }*/
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    @SuppressLint("InflateParams")
    private void showSummaryDialog() {
        final View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_report_summary, null);
        RecyclerView reportSummaryRecycler = dialogView.findViewById(R.id.reportSummaryRecycler);

        Report summarizedReport = reportManager.getSummary(calendarShow.get(Calendar.MONTH) + 1, calendarShow.get(Calendar.YEAR));

        ReportRecyclerAdapter summarizedRecyclerAdapter = new ReportRecyclerAdapter(requireContext(), Collections.singletonList(summarizedReport), null);
        reportSummaryRecycler.setAdapter(summarizedRecyclerAdapter);
        reportSummaryRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        dialog.setContentView(dialogView);
        dialog.show();
        dialog.getBehavior().setState(STATE_EXPANDED);
        dialog.getBehavior().setSkipCollapsed(true);
    }

    public void updateList() {
        toolbar.getMenu().clear();
        if (reportTimer.getTimerState() == ReportTimer.TimerState.STOPPED) {
            toolbar.inflateMenu(R.menu.main_timer);
        }
        toolbar.inflateMenu(R.menu.main);

        if (reportRecyclerAdapter == null) {
            initList();
        }
        List<Report> reports = reportManager.getReports(calendarShow.get(Calendar.MONTH) + 1, calendarShow.get(Calendar.YEAR));
        if (reportTimer.getTimerState() != ReportTimer.TimerState.STOPPED) {
            Report report = new Report();
            report.setId(-1);
            report.setType(Report.Type.TIMER);
            reports.add(0, report);
        }
        reportRecyclerAdapter.reports = reports;
        reportRecyclerAdapter.notifyDataSetChanged();

        if (reports.isEmpty()) {
            noReportView.setVisibility(View.VISIBLE);
        } else {
            noReportView.setVisibility(View.GONE);
        }

        updateSummary();
    }

    public boolean deleteReport(Report report) {
        boolean deleted = reportManager.deleteReport(report);
        if (deleted) {
            Snackbar.make(reportAddFab, R.string.report_undo_1, Snackbar.LENGTH_LONG)
                    .setAction(R.string.report_undo_2, v -> {
                        reportManager.createReport(report);
                        updateList();
                    }).show();
        }
        return deleted;
    }

    public void openGoalEditDialog() {
        View input_view = LayoutInflater.from(requireContext())
                .inflate(R.layout.goal_set_input, null, false);
        final EditText edt = ((TextInputLayout) input_view.findViewById(R.id.name_text_field)).getEditText();


        if (sp.contains("goal") && !sp.getString("goal", "0").equals("0")) {
            edt.setText(sp.getString("goal", "0"));
        }
        new MaterialAlertDialogBuilder(requireContext())
                .setView(input_view)
                .setTitle(getString(R.string.goal_set))
                .setMessage(getString(R.string.goal_msg))
                .setPositiveButton(getString(R.string.OK), (dialog, whichButton) -> {
                    try {
                        Integer.parseInt(edt.getText().toString());
                    } catch (Exception e) {
                        Toast.makeText(requireContext(), getString(R.string.goal_invalid), Toast.LENGTH_LONG).show();
                        return;
                    }
                    editor.putString("goal", edt.getText().toString());
                    editor.commit();
                    updateSummary();
                })
                .setNegativeButton(getString(R.string.goal_no), (dialog, whichButton) -> {
                    editor.putString("goal", "0");
                    editor.commit();
                    updateSummary();
                })
                .create()
                .show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_goal) {
            openGoalEditDialog();
            return true;
        } else if (item.getItemId() == R.id.action_timer) {
            reportTimer.startTimer();
        }
        return false;
    }

    public void updateSummary() {
        Report summarizedReport = reportManager.getSummary(calendarShow.get(Calendar.MONTH) + 1, calendarShow.get(Calendar.YEAR));

        if (summarizedReport.getMinutes() % 60 == 0) {
            carryOverButton.setVisibility(View.GONE);
        } else {
            carryOverButton.setVisibility(View.VISIBLE);
        }

        GoalState goalState = reportManager.getGoalState(calendarShow.get(Calendar.MONTH) + 1, calendarShow.get(Calendar.YEAR));
        if (goalState.isHasGoal()) {
            goalView.setVisibility(View.VISIBLE);
            goalProgress.setProgress((int) goalState.getProgressPercent());
            goalText.setText(goalState.getLocalizedGoalText());
        } else {
            goalView.setVisibility(View.GONE);
        }
        reportsRecycler.post(() -> {

            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) goalView.getLayoutParams();

            reportsRecycler.setPadding(reportsRecycler.getPaddingLeft(),
                    goalState.isHasGoal() ? goalView.getHeight() + mlp.topMargin : 0,
                    reportsRecycler.getPaddingRight(),
                    reportsRecycler.getPaddingBottom());
            /*Utils.setMargins(reportsRecycler,
                    0, goalState.isHasGoal() ? Utils.dpToPx(16) : 0, 0, 0);*/
        });
    }


    public void sendReport(String name, boolean detailed, boolean wasActive) {
        if (detailed) {
            wasActive = true;
        }

        editor.putString("lastSendName", name);
        editor.putBoolean("lastSendDetailed", detailed);
        editor.putBoolean("lastSendActive", wasActive);
        editor.commit();
        if (!name.matches("")) {
            Report summarizedReport = reportManager.getSummary(calendarShow.get(Calendar.MONTH) + 1, calendarShow.get(Calendar.YEAR));
            String text = getResources().getString(R.string.reportfor) + name + "\n" + getResources().getString(R.string.reportmonth) + new DateFormatSymbols().getMonths()[calendarShow.get(Calendar.MONTH)] + "\n==============\n";

            if (detailed) {
                String[] formattedTime = summarizedReport.getFormattedHoursAndMinutes(requireContext());
                text += formattedTime[1] + ": " + formattedTime[0] + "\n";
            }
            if (wasActive) {
                if (!detailed) {
                    text += getResources().getString(R.string.reportActive) + "\n";
                }
                text += getResources().getString(R.string.reportstudy) + summarizedReport.getBibleStudies() + "\n";
            } else {
                text += getResources().getString(R.string.reportNotActive) + "\n";
            }
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

    public void showEditDialog(Long id) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        ReportAddDialog newFragment;

        if (id != null) {
            newFragment = ReportAddDialog.newInstance(id);
        } else {
            newFragment = ReportAddDialog.newInstance();
        }

        int screenWidth = getResources().getConfiguration().screenWidthDp;
        if (AdaptiveUtils.LARGE_SCREEN_WIDTH_SIZE <= screenWidth) {
            // The device is using a large layout, so show the fragment as a dialog
            newFragment.show(fragmentManager, "dialog");
        } else {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.add(R.id.drawer_layout, newFragment)
                    .addToBackStack(null).commit();
        }

        newFragment.dismissCallback = () -> {
            updateList();
            Utils.hideKeyboard(getActivity());
        };

    }


}
