package tk.phili.dienst.dienst.settings;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
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
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.marcoscg.licenser.Library;
import com.marcoscg.licenser.License;
import com.marcoscg.licenser.LicenserDialog;
import com.yarolegovich.mp.MaterialEditTextPreference;
import com.yarolegovich.mp.MaterialStandardPreference;
import com.yarolegovich.mp.MaterialSwitchPreference;
import com.yarolegovich.mp.io.StorageModule;
import com.yarolegovich.mp.io.UserInputModule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import tk.phili.dienst.dienst.R;
import tk.phili.dienst.dienst.dailytext.widget.DailytextWorker;
import tk.phili.dienst.dienst.dailytext.widget.TagestextWidget;
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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        toolbar = view.findViewById(R.id.toolbar);

        fragmentCommunicationPass.onDataPass(this, WrapperActivity.FRAGMENTPASS_TOOLBAR, toolbar);

        toolbar.setTitle(getResources().getString(R.string.title_settings));

        sp = getContext().getSharedPreferences("MainActivity", Context.MODE_PRIVATE);
        editor = sp.edit();

        SettingsStorageModule ssm = new SettingsStorageModule();
        SettingsInputModule sim = new SettingsInputModule();

        MaterialEditTextPreference reportLayoutEdit = view.findViewById(R.id.report_layout_settings);
        MaterialStandardPreference exportSetting = view.findViewById(R.id.export);
        MaterialStandardPreference resetSetting = view.findViewById(R.id.reset);
        MaterialEditTextPreference darkModeSetting = view.findViewById(R.id.darkMode);
        MaterialSwitchPreference privateModeSwitch = view.findViewById(R.id.report_private_mode);
        MaterialStandardPreference languageSamplPresSetting = view.findViewById(R.id.language_empf);
        MaterialStandardPreference languageDailyTextSetting = view.findViewById(R.id.language_tt);
        MaterialStandardPreference languageVideosSetting = view.findViewById(R.id.language_videos);
        MaterialStandardPreference gcalResetSetting = view.findViewById(R.id.calendar_gcal_reset);
        MaterialStandardPreference calTimeOfNotifySetting = view.findViewById(R.id.calendar_time_of_notification);
        MaterialStandardPreference notificationSetting = view.findViewById(R.id.calendar_notification);
        MaterialStandardPreference imprintSetting = view.findViewById(R.id.impressum);
        MaterialStandardPreference licensesSetting = view.findViewById(R.id.licenses);
        MaterialStandardPreference gdprSetting = view.findViewById(R.id.dsgvo_title);

        reportLayoutEdit.setUserInputModule(sim);
        reportLayoutEdit.setStorageModule(ssm);
        darkModeSetting.setUserInputModule(sim);
        darkModeSetting.setStorageModule(ssm);
        privateModeSwitch.setStorageModule(ssm);

        setIcon(exportSetting, R.drawable.ic_baseline_backup_24);
        setIcon(resetSetting, R.drawable.ic_baseline_delete_forever_24);
        setIcon(darkModeSetting, R.drawable.dark_mode_24px);
        setIcon(reportLayoutEdit, R.drawable.ic_baseline_style_24);
        setIcon(privateModeSwitch, R.drawable.ic_baseline_privacy_tip_24);
        setIcon(languageSamplPresSetting, R.drawable.ic_thumb_up_black_24dp);
        setIcon(languageDailyTextSetting, R.drawable.ic_baseline_event_available_24px);
        setIcon(languageVideosSetting, R.drawable.ic_video_library_black_24dp);
        setIcon(gcalResetSetting, R.drawable.ic_baseline_sync_disabled_24);
        setIcon(calTimeOfNotifySetting, R.drawable.ic_timer_black_24dp);
        setIcon(notificationSetting, R.drawable.notification_settings_24px);
        setIcon(imprintSetting, R.drawable.ic_baseline_info_24);
        setIcon(licensesSetting, R.drawable.attribution_24px);
        setIcon(gdprSetting, R.drawable.policy_24px);

        languageSamplPresSetting.setOnClickListener(__ -> {
            showLanguagePicker("sample_presentations_locale", null);
        });

        languageDailyTextSetting.setOnClickListener(__ -> {
            showLanguagePicker("tt_locale", () -> {
                OneTimeWorkRequest nowRequest = new OneTimeWorkRequest.Builder(DailytextWorker.class).build();
                WorkManager.getInstance(requireContext()).enqueue(nowRequest);
            });
        });

        languageVideosSetting.setOnClickListener(__ -> {
            showLanguagePicker("videos_locale", null);
        });

        resetSetting.setOnClickListener(__ ->
                new MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialogCenterStyle)
                        .setTitle(getResources().getString(R.string.resetdialog_title))
                        .setMessage(getResources().getString(R.string.resetdialog_message))
                        .setIcon(R.drawable.ic_baseline_delete_forever_24)
                        .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                            editor.clear();
                            editor.apply();

                            SharedPreferences sp1 = getContext().getSharedPreferences("Splash", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = sp1.edit();
                            editor1.clear();
                            editor1.apply();

                            SharedPreferences sp2 = getContext().getSharedPreferences("MainActivity3", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor2 = sp2.edit();
                            editor2.clear();
                            editor2.apply();


                            final Snackbar snackbar = Snackbar
                                    .make(getView(), getString(R.string.data_cleared), Snackbar.LENGTH_SHORT);
                            snackbar.setAction(android.R.string.ok, view1 -> snackbar.dismiss());

                            snackbar.show();
                        })
                        .setNegativeButton(getString(R.string.no), null)
                        .show());

        imprintSetting.setOnClickListener(__ -> {
            String url = "https://ministryapp.de/imprint";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });

        licensesSetting.setOnClickListener(__ ->
        {
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
                    .setLibrary(new Library("MaterialPreferences",
                            "https://github.com/yarolegovich/MaterialPreferences",
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
        });

        gdprSetting.setOnClickListener(__ -> {
            Intent i = new Intent(getContext(), GDPRInfo.class);
            i.putExtra("hastoaccept", false);
            startActivity(i);
        });

        exportSetting.setOnClickListener(__ -> Export.exportGlobal(getContext()));

        if (!sp.contains("CalendarSyncActive")) {
            gcalResetSetting.setVisibility(GONE);
        }

        gcalResetSetting.setOnClickListener(v -> {
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
                                getActivity().runOnUiThread(() -> gcalResetSetting.setVisibility(GONE));
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
                    getActivity().runOnUiThread(() -> gcalResetSetting.setVisibility(GONE));
                }
            }
        });

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            notificationSetting.setVisibility(GONE);
        }

        notificationSetting.setOnClickListener(__ -> {
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
        });

        calTimeOfNotifySetting.setOnClickListener(__ -> {
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


        });

    }

    public void showLanguagePicker(String spKey, Runnable callback) {
        final TreeMap<String, String> langCode = new TreeMap<>();

        langCode.put(getString(R.string.language_default), "0");
        JWLanguageService languageService = new JWLanguageService(requireContext());
        languageService.getLanguages().forEach(jwLang -> {
            String name = new Locale(jwLang.getSymbol()).getDisplayName(Locale.getDefault());
            langCode.put(name, jwLang.getLangcode());
        });

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

                    if(callback != null) callback.run();

                    dialog.dismiss();
                })
                .create()
                .show();
    }


    public class SettingsInputModule implements UserInputModule {

        @Override
        public void showEditTextInput(String key, CharSequence title, CharSequence defaultValue, final Listener<String> listener) {
            if (key.equalsIgnoreCase("report_layout")) {
                ReportManager reportManager = new ReportManager(getContext());

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
                            if (selectedItem.get() == 0) {
                                listener.onInput(SettingsFragment.this.getString(R.string.report_layout_1));
                            } else {
                                listener.onInput(SettingsFragment.this.getString(R.string.report_layout_2));
                            }
                        })
                        .show();

            } else if (key.equalsIgnoreCase("dark_mode")) {
                final CharSequence[] items = {getString(R.string.dark_mode_auto), getString(R.string.dark_mode_dark), getString(R.string.dark_mode_light)};

                int checkedItem = 0;
                if (sp.contains("dark_mode")) {
                    checkedItem = sp.getInt("dark_mode", 0);
                }

                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.dark_mode)
                        .setSingleChoiceItems(items, checkedItem, (dialog, item) -> {
                            editor.putInt("dark_mode", item);
                            editor.apply();
                            listener.onInput(items[item].toString());

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
        }

        @Override
        public void showSingleChoiceInput(String key, CharSequence title, CharSequence[] displayItems, CharSequence[] values, int selected, Listener<String> listener) {

        }

        @Override
        public void showMultiChoiceInput(String key, CharSequence title, CharSequence[] displayItems, CharSequence[] values, boolean[] defaultSelection, Listener<Set<String>> listener) {

        }

        @Override
        public void showColorSelectionInput(String key, CharSequence title, int defaultColor, Listener<Integer> color) {

        }
    }


    public class SettingsStorageModule implements StorageModule {

        @Override
        public void saveBoolean(String key, boolean value) {
            if (key.equalsIgnoreCase("private_mode")) {
                editor.putBoolean("private_mode", value);
            }
            editor.apply();
        }

        @Override
        public void saveString(String key, String value) {

        }

        @Override
        public void saveInt(String key, int value) {

        }

        @Override
        public void saveStringSet(String key, Set<String> value) {

        }

        @Override
        public boolean getBoolean(String key, boolean defaultVal) {
            if (key.equalsIgnoreCase("private_mode")) {
                return sp.getBoolean("private_mode", defaultVal);
            }
            return false;
        }

        @Override
        public String getString(String key, String defaultVal) {
            if (key.equalsIgnoreCase("report_layout")) {
                if (sp.getInt("report_layout", 0) == 0) {
                    return SettingsFragment.this.getString(R.string.report_layout_1);
                } else {
                    return SettingsFragment.this.getString(R.string.report_layout_2);
                }
            } else if (key.equalsIgnoreCase("dark_mode")) {
                switch (sp.getInt("dark_mode", 0)) {
                    case 1:
                        return SettingsFragment.this.getString(R.string.dark_mode_dark);
                    case 2:
                        return SettingsFragment.this.getString(R.string.dark_mode_light);
                    default:
                        return SettingsFragment.this.getString(R.string.dark_mode_auto);
                }
            }
            return null;
        }

        @Override
        public int getInt(String key, int defaultVal) {
            return 0;
        }

        @Override
        public Set<String> getStringSet(String key, Set<String> defaultVal) {
            return null;
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {

        }

        @Override
        public void onRestoreInstanceState(Bundle savedState) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentCommunicationPass.onDataPass(this, WrapperActivity.FRAGMENTPASS_TOOLBAR, toolbar);
    }

    public void setIcon(Object preference, int drawableId) {
        try {
            Class<?> c = Class.forName("com.yarolegovich.mp.AbsMaterialPreference");
            Field f = c.getDeclaredField("icon");
            f.setAccessible(true);
            AppCompatImageView imageView = (AppCompatImageView) f.get(preference);
            imageView.setVisibility(VISIBLE);
            //imageView.setColorFilter(Color.argb(255, 255, 255, 255));
            imageView.setImageResource(drawableId);
            //ImageViewCompat.setImageTintList(imageView, ContextCompat.getColorStateList(requireContext(), R.attr.colorPrimary));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
