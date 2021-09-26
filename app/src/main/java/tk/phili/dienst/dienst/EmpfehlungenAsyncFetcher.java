package tk.phili.dienst.dienst;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class EmpfehlungenAsyncFetcher extends AsyncTask<Void, Void, Void> {

    public String language;
    public JSONObject response;
    public Runnable futurerun;

    @Override
    protected Void doInBackground(Void... voids) {

        runEmpfehlungenGet();
        if(futurerun != null)
            futurerun.run();

        return null;
    }

    public void runEmpfehlungenGet() {
        JSONObject obj1 = null;
        try {
            obj1 = Utils.readJsonFromUrl("https://ministryapp.de/samplepresentations.php?lang="+language);
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