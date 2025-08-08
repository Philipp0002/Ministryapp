package tk.phili.dienst.dienst.dailytext.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import okhttp3.Dns;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tk.phili.dienst.dienst.utils.JWLanguageService;

public class DailytextWorker extends Worker {

    public DailytextWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {

            SharedPreferences sp = getApplicationContext().getSharedPreferences("MainActivity", Context.MODE_PRIVATE);

            JWLanguageService languageService = new JWLanguageService(getApplicationContext());
            String jwLang = sp.getString("tt_locale", languageService.getCurrentLanguage("E").getLangcode());
            String url = "https://www.jw.org/finder?srcid=jwlshare&wtlocale=" + jwLang + "&alias=daily-text&date=" + LocalDate.now().toString();

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .callTimeout(60, TimeUnit.SECONDS)
                    .followRedirects(true)
                    .followSslRedirects(true)
                    .dns(Dns.SYSTEM)
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            // OkHttp Request
            Response response = client.newCall(request).execute();

            if (response.isSuccessful() && response.body() != null) {
                Document document = Jsoup.parse(response.body().string());

                Element contentElement = document.getElementsByClass("tabContent").get(1);
                Element dayElement = contentElement.getElementsByTag("h2").get(0);
                Element textElement = contentElement.getElementsByClass("themeScrp").get(0);

                String day = dayElement.text();
                String text = textElement.text();

                // Hier den Tagestext ins SharedPreferences speichern
                sp.edit().putString("dailytext_day", day).apply();
                sp.edit().putString("dailytext_text", text).apply();

                // Widget aktualisieren
                AppWidgetManager manager = AppWidgetManager.getInstance(getApplicationContext());
                ComponentName widget = new ComponentName(getApplicationContext(), TagestextWidget.class);
                for (int appWidgetId : manager.getAppWidgetIds(widget)) {
                    TagestextWidget.updateAppWidget(getApplicationContext(), manager, appWidgetId);
                }

                return Result.success();
            } else {
                return Result.retry();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.retry();
        }
    }
}