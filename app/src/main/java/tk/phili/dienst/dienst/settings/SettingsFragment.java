package tk.phili.dienst.dienst.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.marcoscg.licenser.Library;
import com.marcoscg.licenser.License;
import com.marcoscg.licenser.LicenserDialog;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import tk.phili.dienst.dienst.R;
import tk.phili.dienst.dienst.dailytext.widget.DailytextWorker;
import tk.phili.dienst.dienst.report.ReportManager;
import tk.phili.dienst.dienst.uiwrapper.FragmentCommunicationPass;
import tk.phili.dienst.dienst.uiwrapper.WrapperActivity;
import tk.phili.dienst.dienst.utils.JWLanguageService;
import tk.phili.dienst.dienst.utils.Utils;


public class SettingsFragment extends Fragment {

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private Toolbar toolbar;
    private FragmentCommunicationPass fragmentCommunicationPass;
    private SettingsAdapter settingsAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentCommunicationPass = (FragmentCommunicationPass) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, null);
    }

    private String getDarkModeString(int mode) {
        switch (mode) {
            case 1:
                return SettingsFragment.this.getString(R.string.dark_mode_dark);
            case 2:
                return SettingsFragment.this.getString(R.string.dark_mode_light);
            default:
                return SettingsFragment.this.getString(R.string.dark_mode_auto);
        }
    }

    private String getReportLayoutString(int mode) {
        if (mode == 0) {
            return SettingsFragment.this.getString(R.string.report_layout_1);
        } else {
            return SettingsFragment.this.getString(R.string.report_layout_2);
        }
    }

    private void showResetDialog() {
        new MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialogCenterStyle)
                .setTitle(getResources().getString(R.string.resetdialog_title))
                .setMessage(getResources().getString(R.string.resetdialog_message))
                .setIcon(R.drawable.ic_baseline_delete_forever_24)
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    editor.clear();
                    editor.apply();

                    SharedPreferences sp1 = requireContext().getSharedPreferences("Splash", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = sp1.edit();
                    editor1.clear();
                    editor1.apply();

                    SharedPreferences sp2 = requireContext().getSharedPreferences("MainActivity3", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor2 = sp2.edit();
                    editor2.clear();
                    editor2.apply();


                    final Snackbar snackbar = Snackbar
                            .make(requireView(), getString(R.string.data_cleared), Snackbar.LENGTH_SHORT);
                    snackbar.setAction(android.R.string.ok, view1 -> snackbar.dismiss());

                    snackbar.show();
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

    private void showLicencesDialog() {
        String htmlContent = new LicenserDialog(getContext(), R.style.ThemeOverlay_Material3Expressive_Dialog)
                .setTitle(getString(R.string.licenses))
                .setLibrary(new Library("AndroidX Support Libraries",
                        "https://developer.android.com/jetpack/androidx",
                        License.Companion.getAPACHE2()))
                .setLibrary(new Library("CompactCalendarView",
                        "https://github.com/SundeepK/CompactCalendarView",
                        License.Companion.getMIT()))
                .setLibrary(new Library("Licenser",
                        "https://github.com/marcoscgdev/Licenser",
                        License.Companion.getMIT()))
                .setLibrary(new Library("Material Components",
                        "https://github.com/material-components/material-components-android",
                        License.Companion.getAPACHE2()))
                .setLibrary(new Library("RSS Parser",
                        "https://github.com/prof18/RSS-Parser",
                        License.Companion.getAPACHE2()))
                .setLibrary(new Library("Month and Year Picker",
                        "https://github.com/dewinjm/monthyear-picker",
                        License.Companion.getAPACHE2()))
                .setLibrary(new Library("Kotlin",
                        "https://github.com/JetBrains/kotlin",
                        License.Companion.getAPACHE2()))
                .setLibrary(new Library("PRDownloader",
                        "https://github.com/amitshekhariitbhu/PRDownloader",
                        License.Companion.getAPACHE2()))
                .getHtmlContent();
        final LinearLayout layout = new LinearLayout(requireContext());
        WebView webView = new WebView(requireContext());
        layout.addView(webView);
        webView.loadData(Base64.encodeToString(htmlContent.getBytes(StandardCharsets.UTF_8), Base64.NO_PADDING),
                "text/html; charset=UTF-8", "base64");
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.licenses))
                .setView(layout)
                .setPositiveButton(getString(R.string.ok), (dialog, whichButton) -> {
                })
                .show();
    }

    private void showDarkModeDialog(SettingsAdapter.BasicSetting settingsItem) {
        final CharSequence[] selectionItems = {getString(R.string.dark_mode_auto), getString(R.string.dark_mode_dark), getString(R.string.dark_mode_light)};

        int checkedItem = 0;
        if (sp.contains("dark_mode")) {
            checkedItem = sp.getInt("dark_mode", 0);
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.dark_mode)
                .setSingleChoiceItems(selectionItems, checkedItem, (dialog, item) -> {
                    editor.putInt("dark_mode", item);
                    editor.apply();
                    settingsItem.setDescription(getDarkModeString(item));
                    settingsAdapter.notifyItemChanged(settingsAdapter.items.indexOf(settingsItem));

                    int mode = 0;
                    if (item == 0) {
                        mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                    } else if (item == 1) {
                        mode = AppCompatDelegate.MODE_NIGHT_YES;
                    } else if (item == 2) {
                        mode = AppCompatDelegate.MODE_NIGHT_NO;
                    }
                    AppCompatDelegate.setDefaultNightMode(mode);

                    dialog.dismiss();
                })
                .create()
                .show();
    }

    private void showSystemNotificationSettings() {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, getContext().getPackageName())
                    .putExtra(Settings.EXTRA_CHANNEL_ID, "calendar");
        } else {
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");

            intent.putExtra("app_package", getContext().getPackageName());
            intent.putExtra("app_uid", getContext().getApplicationInfo().uid);

            intent.putExtra("android.provider.extra.APP_PACKAGE", getContext().getPackageName());
        }
        startActivity(intent);
    }

    private void showReportLayoutDialog(SettingsAdapter.BasicSetting settingsItem) {
        ReportManager reportManager = new ReportManager(requireContext());

        final LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(Utils.dpToPx(16), 0, Utils.dpToPx(16), 0);

        int layoutId = 0;
        if (reportManager.getReportLayoutSetting() == 0) {
            layoutId = R.layout.report_item;
        } else if (reportManager.getReportLayoutSetting() == 1) {
            layoutId = R.layout.report_item_tiny;
        }

        View reportLayout = getLayoutInflater().inflate(layoutId, null);
        layout.addView(reportLayout);

        View reportLayoutSpinner = getLayoutInflater().inflate(R.layout.report_layout_spinner, null);
        layout.addView(reportLayoutSpinner);

        MaterialAutoCompleteTextView spinnerView = reportLayoutSpinner.findViewById(R.id.spinner);

        String[] items = new String[]{getString(R.string.report_layout_1), getString(R.string.report_layout_2)};
        spinnerView.setSimpleItems(items);
        spinnerView.setListSelection(reportManager.getReportLayoutSetting());
        spinnerView.setText(items[reportManager.getReportLayoutSetting()]);
        ((ArrayAdapter) spinnerView.getAdapter()).getFilter().filter(null);
        AtomicInteger selectedItem = new AtomicInteger(reportManager.getReportLayoutSetting());
        spinnerView.setOnItemClickListener((__, ___, i, ____) -> {
            if (i == 0) {
                layout.removeViewAt(0);
                View reportLayout1 = getLayoutInflater().inflate(R.layout.report_item, null);
                layout.addView(reportLayout1, 0);
            } else if (i == 1) {
                layout.removeViewAt(0);
                View reportLayout1 = getLayoutInflater().inflate(R.layout.report_item_tiny, null);
                layout.addView(reportLayout1, 0);
            }
            selectedItem.set(i);
        });

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.report_layout_settings))
                .setMessage(getString(R.string.report_layout_msg))
                .setView(layout)
                .setPositiveButton(getString(R.string.action_save), (dialog, whichButton) -> {
                    editor.putInt("report_layout", selectedItem.get());
                    editor.apply();
                    settingsItem.setDescription(getReportLayoutString(selectedItem.get()));
                    settingsAdapter.notifyItemChanged(settingsAdapter.items.indexOf(settingsItem));
                })
                .show();
    }

    private String getLocalizedLanguageNameByLangSetting(String spKey) {
        JWLanguageService languageService = new JWLanguageService(requireContext());
        String langCode = sp.getString(spKey, "0");
        if (langCode.equals("0")) {
            return languageService.getCurrentLanguage("E").getLocalizedLanguageName();
        }

        return languageService.getLanguageByLangcode(langCode).getLocalizedLanguageName();
    }

    private void showCalendarTimeOfNotificationDialog() {
        LayoutInflater inflater = SettingsFragment.this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.notification_time_popup, null);

        final EditText edt = dialogView.findViewById(R.id.time);
        MaterialAutoCompleteTextView spinnerView = dialogView.findViewById(R.id.unit);

        String[] units = {getResources().getString(R.string.calendar_notification_hour), getResources().getString(R.string.calendar_notification_day)};
        spinnerView.setSimpleItems(units);
        try {
            spinnerView.setListSelection(sp.getInt("Calendar_Unit", 0));
            spinnerView.setText(units[sp.getInt("Calendar_Unit", 0)]);
        } catch (Exception e) {
        }
        ((ArrayAdapter) spinnerView.getAdapter()).getFilter().filter(null);
        AtomicInteger selectedItem = new AtomicInteger(sp.getInt("Calendar_Time", 1));
        spinnerView.setOnItemClickListener((___, ____, i, _____) -> {
            selectedItem.set(i);
        });

        edt.setText(sp.getInt("Calendar_Time", 1) + "");


        new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setTitle(getString(R.string.calendar_time_of_notification))
                .setPositiveButton(getString(R.string.action_save), (dialog, whichButton) -> {
                    editor.putInt("Calendar_Time", Integer.parseInt(edt.getText().toString()));
                    editor.putInt("Calendar_Unit", selectedItem.get());
                    editor.remove("Calendar_Shown");
                    editor.apply();
                })
                .create()
                .show();
    }

    private void disconnectGoogleCalendar(SettingsAdapter.BasicSetting item) {
        if (sp.contains("CalendarSyncActive")) {
            if (sp.getBoolean("CalendarSyncActive", false)) {
                DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:

                            editor.remove("CalendarSyncActive");
                            editor.remove("CalendarSyncGacc");
                            editor.remove("CalendarSyncTCID");
                            editor.remove("CalendarSync");
                            editor.apply();
                            Toast.makeText(getContext(), R.string.calendar_gcal_reset_to_default, Toast.LENGTH_LONG).show();

                            settingsAdapter.items.remove(item);
                            settingsAdapter.notifyDataSetChanged();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                };

                new MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialogCenterStyle)
                        .setTitle(R.string.calendar_gcal_remove_title)
                        .setMessage(R.string.calendar_gcal_remove_msg)
                        .setIcon(R.drawable.ic_baseline_sync_disabled_24)
                        .setPositiveButton(R.string.yes, dialogClickListener)
                        .setNegativeButton(R.string.no, dialogClickListener)
                        .show();
            } else {
                Toast.makeText(getContext(), R.string.calendar_gcal_reset_to_default, Toast.LENGTH_LONG).show();
                editor.remove("CalendarSyncActive");
                editor.remove("CalendarSyncGacc");
                editor.remove("CalendarSyncTCID");
                editor.remove("CalendarSync");
                editor.apply();

                settingsAdapter.items.remove(item);
                settingsAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        toolbar = view.findViewById(R.id.toolbar);

        fragmentCommunicationPass.onDataPass(this, WrapperActivity.FRAGMENTPASS_TOOLBAR, toolbar);

        toolbar.setTitle(getResources().getString(R.string.title_settings));

        sp = requireContext().getSharedPreferences("MainActivity", Context.MODE_PRIVATE);
        editor = sp.edit();

        ArrayList<Object> items = new ArrayList<>();

        items.add(getString(R.string.pref_general));
        items.add(new SettingsAdapter.BasicSetting(R.drawable.ic_baseline_backup_24, getString(R.string.export), null, item -> Export.exportGlobal(requireContext())));
        items.add(new SettingsAdapter.BasicSetting(R.drawable.ic_baseline_delete_forever_24, getString(R.string.reset), null, item -> showResetDialog()));
        items.add(new SettingsAdapter.BasicSetting(R.drawable.dark_mode_24px, getString(R.string.dark_mode), getDarkModeString(sp.getInt("dark_mode", 0)), this::showDarkModeDialog));

        items.add(getString(R.string.title_report));
        items.add(new SettingsAdapter.BasicSetting(R.drawable.ic_baseline_style_24, getString(R.string.report_layout_settings), getReportLayoutString(sp.getInt("report_layout", 0)), this::showReportLayoutDialog));
        items.add(new SettingsAdapter.SwitchSetting(R.drawable.ic_baseline_privacy_tip_24, getString(R.string.report_private_mode), null, sp.getBoolean("private_mode", false), item -> {
            editor.putBoolean("private_mode", ((SettingsAdapter.SwitchSetting) item).isChecked());
            editor.apply();
        }));

        items.add(getString(R.string.language));
        items.add(new SettingsAdapter.BasicSetting(R.drawable.ic_thumb_up_black_24dp, getString(R.string.title_sample_presentations), getLocalizedLanguageNameByLangSetting("sample_presentations_locale"), item -> showLanguagePicker("sample_presentations_locale", null, item)));
        items.add(new SettingsAdapter.BasicSetting(R.drawable.ic_baseline_event_available_24px, getString(R.string.title_dailytext), getLocalizedLanguageNameByLangSetting("tt_locale"), item -> showLanguagePicker("tt_locale", () -> {
            OneTimeWorkRequest nowRequest = new OneTimeWorkRequest.Builder(DailytextWorker.class).build();
            WorkManager.getInstance(requireContext()).enqueue(nowRequest);
        }, item)));
        items.add(new SettingsAdapter.BasicSetting(R.drawable.ic_video_library_black_24dp, getString(R.string.title_videos), getLocalizedLanguageNameByLangSetting("videos_locale"), item -> showLanguagePicker("videos_locale", null, item)));

        items.add(getString(R.string.title_calendar));
        if (sp.contains("CalendarSyncActive")) {
            items.add(new SettingsAdapter.BasicSetting(R.drawable.ic_baseline_sync_disabled_24, getString(R.string.calendar_gcal_setting), null, this::disconnectGoogleCalendar));
        }
        items.add(new SettingsAdapter.BasicSetting(R.drawable.ic_timer_black_24dp, getString(R.string.calendar_time_of_notification), null, item -> showCalendarTimeOfNotificationDialog()));
        items.add(new SettingsAdapter.BasicSetting(R.drawable.notification_settings_24px, getString(R.string.calendar_notification), null, item -> showSystemNotificationSettings()));

        items.add(getString(R.string.pref_legal));
        items.add(new SettingsAdapter.BasicSetting(R.drawable.ic_baseline_info_24, getString(R.string.imprint), null, item -> {
            String url = "https://ministryapp.de/imprint";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }));
        items.add(new SettingsAdapter.BasicSetting(R.drawable.attribution_24px, getString(R.string.licenses), null, item -> showLicencesDialog()));
        items.add(new SettingsAdapter.BasicSetting(R.drawable.policy_24px, getString(R.string.gdpr_title), null, item -> {
            Intent i = new Intent(getContext(), GDPRInfo.class);
            i.putExtra("hastoaccept", false);
            startActivity(i);
        }));


        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        settingsAdapter = new SettingsAdapter(requireContext(), items);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(settingsAdapter);


        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.recyclerView), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Apply the insets as a margin to the view. This solution sets only the
            // bottom, left, and right dimensions, but you can apply whichever insets are
            // appropriate to your layout. You can also update the view padding if that's
            // more appropriate.

            v.setPadding(insets.left, v.getPaddingTop(), insets.right, insets.bottom);

            // Return CONSUMED if you don't want the window insets to keep passing
            // down to descendant views.
            return WindowInsetsCompat.CONSUMED;
        });

    }

    public void showLanguagePicker(String spKey, Runnable callback, SettingsAdapter.BasicSetting settingsItem) {
        final TreeMap<String, String> langCode = new TreeMap<>();

        langCode.put(getString(R.string.language_default), "0");
        JWLanguageService languageService = new JWLanguageService(requireContext());
        languageService.getLanguages().forEach(
                jwLang -> langCode.put(jwLang.getLocalizedLanguageName(), jwLang.getLangcode())
        );

        int checkedIndex = langCode.values().stream()
                .collect(Collectors.toList())
                .indexOf(sp.getString(spKey, "0"));

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.language)
                .setSingleChoiceItems(langCode.keySet().toArray(String[]::new), checkedIndex, (dialog, item) -> {
                    if (langCode.keySet().toArray()[item].equals(getString(R.string.language_default))) {
                        editor.remove(spKey);
                    } else {
                        editor.putString(spKey, (String) langCode.values().toArray()[item]);
                    }
                    editor.apply();

                    if (callback != null) callback.run();

                    if (settingsItem != null) {
                        settingsItem.setDescription(getLocalizedLanguageNameByLangSetting(spKey));
                        settingsAdapter.notifyItemChanged(settingsAdapter.items.indexOf(settingsItem));
                    }

                    dialog.dismiss();
                })
                .create()
                .show();
    }


    @Override
    public void onResume() {
        super.onResume();
        fragmentCommunicationPass.onDataPass(this, WrapperActivity.FRAGMENTPASS_TOOLBAR, toolbar);
    }


}
