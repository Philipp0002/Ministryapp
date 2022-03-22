package tk.phili.dienst.dienst.dailytext;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import tk.phili.dienst.dienst.utils.Utils;

public class DailytextJSONAsyncFetcher extends AsyncTask<Void, Void, Void> {

    public int day;
    public int month;
    public int year;
    public String lang;
    public JSONObject response;
    public Runnable futurerun;

    @Override
    protected Void doInBackground(Void... voids) {

        runTagestextGet();
        if(futurerun != null)
            futurerun.run();

        return null;
    }

    public void runTagestextGet() {
        JSONObject obj1 = null;
        try {
            obj1 = Utils.readJsonFromUrl("https://ministryapp.de/dailytext_json.php?d=" + day + "&m=" + month + "&y=" + year + "&l=" + lang);
        } catch (java.io.FileNotFoundException e) {

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (obj1 != null){
            response = obj1;
        }else{
            response = null;
        }
    }
}