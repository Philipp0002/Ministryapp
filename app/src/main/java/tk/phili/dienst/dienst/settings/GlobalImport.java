package tk.phili.dienst.dienst.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import tk.phili.dienst.dienst.R;
import tk.phili.dienst.dienst.Splash;

public class GlobalImport extends AppCompatActivity {

    Uri filePath;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_import);
        ((Toolbar)findViewById(R.id.toolbar)).setTitle(getString(R.string.import_title));
        setTitle(getString(R.string.import_title));
        filePath = getIntent().getData();

        findViewById(R.id.import_do).setOnClickListener(view -> {
                process(filePath);
                startActivity(new Intent(GlobalImport.this, Splash.class));
                finish();
        });

        findViewById(R.id.import_cancel).setOnClickListener(view -> finish());


    }


    public void process(Uri filePath){

        try {
            InputStream inputStream = getContentResolver().openInputStream(filePath);
            String s = convertStreamToString(inputStream);
            JSONObject obj = new JSONObject(s);
            Iterator<String> keys = obj.keys();

            SharedPreferences sp = getSharedPreferences("MainActivity", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = sp.edit();
            SharedPreferences sp2 = getSharedPreferences("MainActivity3", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit2 = sp2.edit();
            while(keys.hasNext()) {
                String key = keys.next();
                Object val = obj.get(key);

                if(key.equalsIgnoreCase("NOTES")){
                    edit2.putString("NOTES", (String) val);
                }else{
                    if(val instanceof String)
                        edit.putString(key, (String) val);
                    if(val instanceof Boolean)
                        edit.putBoolean(key, (Boolean) val);
                    if(val instanceof Integer)
                        edit.putInt(key, (Integer) val);
                    if(val instanceof Float)
                        edit.putFloat(key, (Float) val);
                    if(val instanceof Long)
                        edit.putLong(key, (Long) val);
                    if(val instanceof JSONArray) {
                        Set<String> set = new HashSet<String>();
                        JSONArray arr = (JSONArray) val;
                        for (int i = 0 ; i < arr.length(); i++) {
                            String string = arr.getString(i);
                            set.add(string);
                        }
                        edit.putStringSet(key, set);
                    }
                }
            }

            edit.apply();
            edit2.apply();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.import_error_fnf, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.import_error, Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.import_error, Toast.LENGTH_LONG).show();
        }


    }

    public static String convertStreamToString(InputStream is) throws IOException {
        // http://www.java2s.com/Code/Java/File-Input-Output/ConvertInputStreamtoString.htm
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        Boolean firstLine = true;
        while ((line = reader.readLine()) != null) {
            if(firstLine){
                sb.append(line);
                firstLine = false;
            } else {
                sb.append("\n").append(line);
            }
        }
        reader.close();
        return sb.toString();
    }


}
