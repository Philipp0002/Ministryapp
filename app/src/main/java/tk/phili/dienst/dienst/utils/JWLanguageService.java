package tk.phili.dienst.dienst.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import tk.phili.dienst.dienst.R;

public class JWLanguageService {

    private static final String SP_LANGUAGES_KEY = "languages";

    private static final String LANGS_URL = "https://www.jw.org/%s/languages";

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor sharedPreferencesEditor;

    public JWLanguageService(Context context) {
        sharedPreferences = context.getSharedPreferences("MainActivity", Context.MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();
    }

    public void getLanguages(JWCallback<List<JWLang>> callback, String langCode) {
        // DUMMY
    }

    public boolean hasLanguages() {
        // DUMMY
        return true;
    }

    public List<JWLang> getLanguages() {
        ArrayList<JWLang> languages = new ArrayList<>();
        languages.add(new JWLang("en", "E"));
        languages.add(new JWLang("de", "X"));
        languages.add(new JWLang("it", "I"));
        languages.add(new JWLang("fr", "F"));
        languages.add(new JWLang("pl", "P"));
        languages.add(new JWLang("tr", "TK"));
        languages.add(new JWLang("th", "SI"));
        languages.add(new JWLang("el", "G"));
        languages.add(new JWLang("ar", "A"));
        languages.add(new JWLang("da", "D"));
        languages.add(new JWLang("sv", "Z"));
        languages.add(new JWLang("ru", "U"));
        languages.add(new JWLang("uk", "K"));
        languages.add(new JWLang("ja", "J"));
        languages.add(new JWLang("ja", "J"));
        return languages;
    }

    public JWLang getCurrentLanguage(String fallbackJwLangCode) {
        String langCodeISO3 = Locale.getDefault().getISO3Language();
        JWLang currentLang = getLanguages().stream()
                .filter(lang -> lang.getSymbol().equals(langCodeISO3))
                .findFirst().orElse(null);

        if(currentLang == null) {
            String langCode = Locale.getDefault().getLanguage();
            currentLang = getLanguages().stream()
                    .filter(lang -> lang.getSymbol().equals(langCode))
                    .findFirst().orElse(null);
        }
        if(currentLang == null) {
            currentLang = new JWLang("unknown", fallbackJwLangCode);
        }
        return currentLang;
    }

    public void showNoLanguageInfo(Context context) {
        new MaterialAlertDialogBuilder(context, R.style.MaterialAlertDialogCenterStyle)
                .setIcon(R.drawable.ic_baseline_redo_24)
                .setTitle(context.getString(R.string.languages_unavailable_title))
                .setMessage(context.getString(R.string.languages_unavailable_body))
                .setPositiveButton(context.getString(R.string.ok), null)
                .show();
    }



    //TODO Improve
    /*public void getLanguages(JWCallback<List<JWLang>> callback, String langCode) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .build();

        String url = String.format(LANGS_URL, langCode);

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if(callback != null) callback.onError(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    if(callback != null) callback.onError(new IOException("Unexpected code " + response));
                    return;
                }

                Gson gson = new Gson();
                Type listType = new TypeToken<JWLangsResponse>() {}.getType();
                JWLangsResponse langsResponse = gson.fromJson(response.body().string(), listType);
                saveLanguages(langsResponse.getLanguages());
                if(callback != null) callback.onSuccess(langsResponse.getLanguages());
            }
        });
    }

    public void saveLanguages(List<JWLang> languages) {
        Gson gson = new Gson();
        String json = gson.toJson(languages);
        sharedPreferencesEditor.putString(SP_LANGUAGES_KEY, json);
        sharedPreferencesEditor.apply();
    }

    public boolean hasLanguages() {
        return sharedPreferences.getString(SP_LANGUAGES_KEY, null) != null;
    }

    public List<JWLang> getLanguages() {
        String json = sharedPreferences.getString(SP_LANGUAGES_KEY, null);
        if (json == null) {
            return null;
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<List<JWLang>>() {}.getType();
        return gson.fromJson(json, listType);
    }
    */

}
