package tk.phili.dienst.dienst.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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

        findViewById(R.id.import_do).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(GlobalImport.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat
                            .requestPermissions(GlobalImport.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
                }else{
                    process(filePath);
                    startActivity(new Intent(GlobalImport.this, Splash.class));
                    finish();
                }
            }
        });

        findViewById(R.id.import_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    process(filePath);
                    startActivity(new Intent(GlobalImport.this, Splash.class));
                    finish();
                } else {
                    // Permission Denied
                    Toast.makeText(GlobalImport.this, getString(R.string.not_accepted_import), Toast.LENGTH_LONG)
                            .show();
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    public void process(Uri filePath){

        try {
            InputStream inputStream = getContentResolver().openInputStream(filePath);
            String s = convertStreamToString(inputStream);
            JSONObject obj = new JSONObject(s);
            //Log.d("aasdasfasdasd", "c");
            Iterator<String> keys = obj.keys();

            SharedPreferences sp = getSharedPreferences("MainActivity", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = sp.edit();
            SharedPreferences sp2 = getSharedPreferences("MainActivity3", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit2 = sp2.edit();
            //Log.d("aasdasfasdasd", "d");
            while(keys.hasNext()) {
                String key = keys.next();
                //Log.d("aasdasfasdasd", key);
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

            edit.commit();
            edit2.commit();

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
