package tk.phili.dienst.dienst.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import androidx.core.content.FileProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Random;
import java.util.Set;

/**
 * Created by fipsi on 04.08.2018.
 */

public class Export {

    public enum Format{
        MAEXPORT, DIENSTAPP_GLOBAL;
    }

    public static void exportGlobal(Context c, Format format){
        SharedPreferences sp = c.getSharedPreferences("MainActivity", c.MODE_PRIVATE);
        SharedPreferences sp2 = c.getSharedPreferences("MainActivity3", c.MODE_PRIVATE);
        JSONObject obj = new JSONObject();
        for(String key : sp.getAll().keySet()){
            Object value = sp.getAll().get(key);
            try {
                if(value instanceof Set){
                    JSONArray array = new JSONArray();
                    for(String s : (Set<String>)value){
                        array.put(s);
                    }
                    obj.put(key, array);
                }else {
                    obj.put(key, value);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        for(String key : sp2.getAll().keySet()){
            Object value = sp2.getAll().get(key);
            try {
                obj.put(key, value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        long millis = System.currentTimeMillis();
        try {

            File cachePath = new File(c.getCacheDir(), "exports");
            cachePath.mkdirs(); // don't forget to make the directory

            FileOutputStream stream = new FileOutputStream(cachePath + "/MINISTRYExport-"+millis+".minapp"); // overwrites this image every time

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(stream);
            outputStreamWriter.write(obj.toString());
            outputStreamWriter.close();
            stream.close();


        } catch (IOException e) {
            e.printStackTrace();
        }

        File imagePath = new File(c.getCacheDir(), "exports");
        File newFile = new File(imagePath, "MINISTRYExport-"+millis+".minapp");
        Uri contentUri = FileProvider.getUriForFile(c, "tk.phili.dienst.dienst.fileprovider", newFile);

        if (contentUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, c.getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            c.startActivity(Intent.createChooser(shareIntent, "Choose an app"));
        }
    }




    public static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }


}
