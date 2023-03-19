package tk.phili.dienst.dienst.samplepresentations;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import tk.phili.dienst.dienst.utils.Utils;

public class SamplePresentationsAsyncFetcher extends AsyncTask<Void, Void, Void> {

    public String language;
    public JSONObject response;
    public Runnable futurerun;

    @Override
    protected Void doInBackground(Void... voids) {

        runGetSamplePresentations();
        if (futurerun != null)
            futurerun.run();

        return null;
    }

    public void runGetSamplePresentations() {
        JSONObject obj1 = null;
        try {
            obj1 = Utils.readJsonFromUrl("https://ministryapp.de/samplepresentations.php?lang=" + language);
        } catch (java.io.FileNotFoundException e) {

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (obj1 != null) {
            response = obj1;
        } else {
            response = null;
        }
    }
}