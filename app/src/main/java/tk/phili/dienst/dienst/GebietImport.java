package tk.phili.dienst.dienst;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.SingleLineTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.tylersuehr.chips.Chip;
import com.tylersuehr.chips.ChipsInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class GebietImport extends AppCompatActivity {

    String filePath;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    Spinner malefemale;
    Spinner interest;

    public SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gebiet_import);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sp = getSharedPreferences("MainActivity", MODE_PRIVATE);
        editor = sp.edit();

        filePath = getIntent().getData().getEncodedPath();

        malefemale = findViewById(R.id.genderedit);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.gebiet_house_genders));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        malefemale.setAdapter(dataAdapter);
        interest = findViewById(R.id.typeedit);
        ArrayAdapter<String> interestAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.gebiet_house_interest_types));
        interestAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        interest.setAdapter(interestAdapter);


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat
                    .requestPermissions(GebietImport.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
        }else{
            process(filePath);
        }

        findViewById(R.id.import_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImportSelection();
            }
        });


        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.import_info_title))
                .setMessage(getString(R.string.import_info_msg))
                //set positive button
                .setPositiveButton("OK", null).show();
    }

    public void openImportSelection() {

        String array_as_string = sp.getString("gebiete", "[]");
        JSONArray array = null;
        try {
            array = new JSONArray(array_as_string);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ArrayList<String> names = new ArrayList<String>();
        final ArrayList<Integer> uids = new ArrayList<Integer>();
        for (int i=0; i < array.length(); i++) {
            try {
                JSONObject o = array.getJSONObject(i);
                names.add(o.getString("address"));
                uids.add(o.getInt("uid"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        names.add("+ " + getString(R.string.gebiet_add));


        // Creating and Building the Dialog
        final AlertDialog dialogT = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.import_select_territory))
                .setSingleChoiceItems(names.toArray(new CharSequence[names.size()]), -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        dialog.dismiss();

                        if(item == names.size()-1){
                            newTerritory();
                        }else{
                            chooseHouse(uids.get(item));
                        }
                    }
                }).create();
        dialogT.show();
    }

    public void chooseHouse(final long uid){
        String array_as_string = sp.getString("Gebiet-"+uid+"-HOUSES", "[]");
        JSONArray arrayunsorted = null;
        try {
            arrayunsorted = new JSONArray(array_as_string);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray array = new JSONArray();
        List<JSONObject> jsonValues = new ArrayList<JSONObject>();
        for (int i = 0; i < arrayunsorted.length(); i++) {
            try {
                jsonValues.add(arrayunsorted.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Collections.sort( jsonValues, new Comparator<JSONObject>() {
            //You can change "Name" with "ID" if you want to sort by ID
            private static final String KEY_NAME = "number";

            int extractInt(String s) {
                String num = s.replaceAll("\\D", "");
                // return 0 if no digits found
                return num.isEmpty() ? 0 : Integer.parseInt(num);
            }

            @Override
            public int compare(JSONObject a, JSONObject b) {
                String valA = new String();
                String valB = new String();

                try {
                    valA = a.getString(KEY_NAME);
                    valB = b.getString(KEY_NAME);
                }catch (JSONException e) {
                    e.printStackTrace();
                }

                return extractInt(valA) - extractInt(valB);
                //return valA.compareTo(valB);
                //if you want to change the sort order, simply use the following:
                //return -valA.compareTo(valB);
            }
        });

        for (int i = 0; i < arrayunsorted.length(); i++) {
            array.put(jsonValues.get(i));
        }

        final ArrayList<String> numbers = new ArrayList<String>();
        for (int i=0; i < array.length(); i++) {
            try {
                JSONObject o = array.getJSONObject(i);
                numbers.add(o.getString("number"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        numbers.add("+ " + getString(R.string.gebiet_house_add));

        final AlertDialog dialogT = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.import_select_house))
                .setSingleChoiceItems(numbers.toArray(new CharSequence[numbers.size()]), -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        dialog.dismiss();

                        if(item == numbers.size()-1){
                            newHouse(uid);
                        }else{
                            try {
                                finalSave(uid, numbers.get(item-1));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).create();
        dialogT.show();

    }

    public void newHouse(final long uid){
        AlertDialog.Builder alert = new AlertDialog.Builder(GebietImport.this);

        alert.setTitle(getString(R.string.gebiet_house_add));
        alert.setMessage(getString(R.string.gebiet_house_add_msg));

        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        };

        final EditText input = new EditText(GebietImport.this);
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4), filter});
        alert.setView(input);

        alert.setPositiveButton(getString(R.string.action_save), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                String houseNumber = value.replace(" ", "").toUpperCase();
                String array_as_string = sp.getString("Gebiet-"+uid+"-HOUSES", "[]");
                JSONArray array = null;
                try {
                    array = new JSONArray(array_as_string);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i=0; i < array.length(); i++) {
                    try {
                        JSONObject o = array.getJSONObject(i);
                        if(o.getString("number").equalsIgnoreCase(houseNumber)){
                            Toast.makeText(GebietImport.this, getString(R.string.gebiet_house_add_invalid), Toast.LENGTH_LONG).show();
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                JSONObject houseObj = new JSONObject();
                try {
                    houseObj.put("number", houseNumber);
                    houseObj.put("householders", new JSONArray());
                    array.put(houseObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                editor.putString("Gebiet-"+uid+"-HOUSES", array.toString());
                editor.commit();
                try {
                    finalSave(uid, houseNumber);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        alert.setNegativeButton(getString(R.string.gebiet_add_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {     }
        });

        alert.show();
    }

    public void finalSave(final long uid, final String houseNumber) throws JSONException{

        String array_as_string = sp.getString("Gebiet-"+uid+"-HOUSES", "[]");
        JSONArray arraya = null;
        try {
            arraya = new JSONArray(array_as_string);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int max = 0;
        for (int i=0; i < arraya.length(); i++) {
            try {
                JSONObject o = arraya.getJSONObject(i);
                if(o.getString("number").equalsIgnoreCase(houseNumber)) {
                    JSONArray array1 = o.getJSONArray("householders");
                    for (int i1 = 0; i1 < array1.length(); i1++) {
                        try {
                            JSONObject obj = array1.getJSONObject(i1);
                            int uidi = obj.getInt("uid");
                            if (uidi > max) {
                                max = uidi;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        max++;




        JSONObject obj = new JSONObject();
        obj.put("uid", max);
        if(!((EditText)findViewById(R.id.nameedit)).getText().toString().isEmpty()){
            String fullname = ((EditText)findViewById(R.id.nameedit)).getText().toString();
            if(fullname.trim().contains(" ")){
                obj.put("last_name", fullname.split(" ")[0]);
                obj.put("first_name", fullname.split(" ")[1]);
            }else{
                obj.put("last_name", fullname);
            }
        }

        Spinner malefemale = findViewById(R.id.genderedit);
        if(malefemale.getSelectedItemPosition() == 0){
            obj.put("male", true);
        }else{
            obj.put("male", false);
        }

        EditText age = findViewById(R.id.ageedit);
        if(!age.getText().toString().isEmpty()){
            try {
                obj.put("age", Integer.parseInt(age.getText().toString()));
            }catch(NumberFormatException e){
                age.setError(getString(R.string.gebiet_house_not_valid_age));
            }
        }

        Spinner interest = findViewById(R.id.typeedit);
        obj.put("type", interest.getSelectedItemPosition());

        EditText lang = findViewById(R.id.langedit);
        if(!lang.getText().toString().isEmpty()){
            obj.put("native_lang", lang.getText().toString());
        }


        JSONArray arrays = new JSONArray();
        ChipsInputLayout cil = findViewById(R.id.publicationsedit);
        for(Chip pubc : cil.getSelectedChips()){
            arrays.put(pubc.getTitle());
        }

        obj.put("publications", arrays);

        EditText notes = findViewById(R.id.notesedit);
        if(!notes.getText().toString().isEmpty()){
            obj.put("notes", notes.getText().toString());
        }


        JSONArray start = new JSONArray();
        JSONObject dishouse = new JSONObject();
        dishouse.put("number", houseNumber);
        JSONArray householders = new JSONArray();

        String jsonstreet = sp.getString("Gebiet-"+uid+"-HOUSES", "[]");
        try {
            JSONArray array = new JSONArray(jsonstreet);
            for (int i=0; i < array.length(); i++) {
                JSONObject o = array.getJSONObject(i);
                if(o.getString("number").equalsIgnoreCase(houseNumber)){


                    Log.d("AYYYY", "d");
                    boolean done = false;
                    JSONArray array2 = o.getJSONArray("householders");
                    for (int i2=0; i2 < array2.length(); i2++) {
                        JSONObject o2 = array2.getJSONObject(i2);
                        if(o2.getInt("uid") == max) {
                            householders.put(obj);
                            Log.d("AYYYY", "a");
                            done = true;
                        }else{
                            householders.put(o2);
                            Log.d("AYYYY", "b");
                        }
                    }
                    if(!done){
                        householders.put(obj);
                    }



                }else{
                    Log.d("AYYYY", "c");
                    start.put(o);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        dishouse.put("householders", householders);
        start.put(dishouse);
        Log.d("AYYYYY", start.toString());
        editor.putString("Gebiet-"+uid+"-HOUSES", start.toString());
        editor.commit();

        finish();
        Intent i = new Intent(GebietImport.this, Gebiete.class);
        GebietImport.this.startActivity(i);
    }

    public void newTerritory(){
        AlertDialog.Builder alert = new AlertDialog.Builder(GebietImport.this);

        alert.setTitle(getString(R.string.gebiet_add));
        alert.setMessage(getString(R.string.gebiet_add_msg));

// Set an EditText view to get user input
        final EditText input = new EditText(GebietImport.this);
        input.setTransformationMethod(new SingleLineTransformationMethod());
        alert.setView(input);

        alert.setPositiveButton(getString(R.string.action_save), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                String array_as_string = sp.getString("gebiete", "[]");
                JSONArray array = null;
                try {
                    array = new JSONArray(array_as_string);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                long max = 0;
                for (int i=0; i < array.length(); i++) {
                    try {
                        JSONObject o = array.getJSONObject(i);
                        if(o.getLong("uid") > max){
                            max = o.getLong("uid");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                JSONObject obj = new JSONObject();
                try {
                    obj.put("address", value);
                    obj.put("uid", max+1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                array.put(obj);

                editor.putString("gebiete", array.toString());
                editor.commit();

                ArrayList<String> names = new ArrayList<String>();
                ArrayList<Integer> uids = new ArrayList<Integer>();
                for (int i=0; i < array.length(); i++) {
                    try {
                        JSONObject o = array.getJSONObject(i);
                        names.add(o.getString("address"));
                        uids.add(o.getInt("uid"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                JSONArray obj1 = new JSONArray();
                editor.putString("Gebiet-"+(max+1)+"-HOUSES", obj1.toString());
                editor.commit();

                chooseHouse(max+1);

            }
        });

        alert.setNegativeButton(getString(R.string.gebiet_add_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    process(filePath);
                } else {
                    // Permission Denied
                    Toast.makeText(GebietImport.this, getString(R.string.not_accepted_import), Toast.LENGTH_LONG)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void process(String filePath){
        String s = decompile(filePath);
        int i = 0;
        while(s.contains("\"PI\"")){
            s = s.replaceFirst("\"PI\"", "\""+i+"\"");
            i++;
        }
        Log.d("HALLOOOOO", s);
        ((ViewSwitcher)findViewById(R.id.switcher1)).setDisplayedChild(1);
        ((ViewSwitcher)findViewById(R.id.switcher2)).setDisplayedChild(1);
        ((ViewSwitcher)findViewById(R.id.switcher3)).setDisplayedChild(1);
        ((ViewSwitcher)findViewById(R.id.switcher4)).setDisplayedChild(1);
        ((ViewSwitcher)findViewById(R.id.switcher5)).setDisplayedChild(1);
        ((ViewSwitcher)findViewById(R.id.switcher6)).setDisplayedChild(1);
        ((ViewSwitcher)findViewById(R.id.switcher7)).setDisplayedChild(1);

        try {
            JSONObject obj1 = new JSONObject(s);
            JSONObject objPerson = obj1.getJSONObject("Person");
            JSONObject objP = objPerson.getJSONObject("P");

            String notes = "";

            if(objP.has("N"))((EditText)findViewById(R.id.nameedit)).setText(objP.getString("N"));
            if(objP.has("Age"))((EditText)findViewById(R.id.ageedit)).setText(objP.getString("Age"));
            if(objP.has("Gen"))notes = objP.getString("Gen");
            if(objP.has("Add"))notes += "\n"+getString(R.string.import_address)+": "+objP.getString("Add");
            if(objP.has("Ph"))notes += "\nTel: "+objP.getString("Ph");
            if(objP.has("Phh"))notes += "\nTel: "+objP.getString("Phh");
            if(objP.has("Eml"))notes += "\nEmail: "+objP.getString("Eml");

            ((EditText)findViewById(R.id.notesedit)).setText(notes.trim());

            String lang = "";
            if(objP.has("Ton")) lang = objP.getString("Ton");
            if(objP.has("Lan")) lang = lang + " " + objP.getString("Lan");
            ((EditText)findViewById(R.id.langedit)).setText(lang.trim());

            int type = objP.getInt("Type");
            if(type == 15 || type == 10 || type == 6 || type == 3 || type == 7 || type == 2 || type == 11){
                interest.setSelection(4); //BIBELSTUDIUM
            }else if(type == 13 || type == 1 || type == 5 || type == 9){
                interest.setSelection(5); //RÜCKBESUCH
            }

            int gender = objP.getInt("TpI"); //1 = male; 0 = female
            if(gender == 0){
                malefemale.setSelection(1);
            }


            if(objP.has("Inf")) {
                JSONObject objInf = objP.getJSONObject("Inf");
                JSONArray objPI_names = objInf.names();
                if(objPI_names != null) {
                    ChipsInputLayout cil = findViewById(R.id.publicationsedit);
                    Log.d("GEBIET_CONVERTUUUU_NAME", objPI_names.toString());

                    for (int ix = 0; ix < objPI_names.length(); ix++) {
                        JSONObject objPI = objInf.getJSONObject(objPI_names.getString(ix));
                        Log.d("GEBIET_CONVERTUUUU_NAME", objPI.toString());
                        if (objPI.has("Sc")) {
                            String a = objPI.getString("Sc");
                            String[] lines;
                            if (a.contains("\n")) {
                                lines = a.split("\\r?\\n");
                            } else {
                                lines = new String[]{a};
                            }
                            for (String str : lines) {
                                cil.addSelectedChip(new PublikationChip(str));
                                Log.d("GEBIET_CONVERTUUUU", "str");
                            }
                            Log.d("GEBIET_CONVERTUUUU", "a");
                        }
                        if (objPI.has("Pub")) {
                            String a = objPI.getString("Pub");
                            String[] lines;
                            if (a.contains("\n")) {
                                lines = a.split("\\r?\\n");
                            } else {
                                lines = new String[]{a};
                            }
                            for (String str : lines) {
                                cil.addSelectedChip(new PublikationChip(str));
                                Log.d("GEBIET_CONVERTUUUU", "str");
                            }
                            Log.d("GEBIET_CONVERTUUUU", "a");
                        }
                        if (objPI.has("Mov")) {
                            String a = objPI.getString("Mov");
                            String[] lines;
                            if (a.contains("\n")) {
                                lines = a.split("\\r?\\n");
                            } else {
                                lines = new String[]{a};
                            }
                            for (String str : lines) {
                                cil.addSelectedChip(new PublikationChip(str));
                                Log.d("GEBIET_CONVERTUUUU", "str");
                            }
                            Log.d("GEBIET_CONVERTUUUU", "a");
                        }
                    }
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public String decompile(String filePath){
        File file = new File(filePath);
        int size = (int) file.length();
        byte[] bytes = new byte[size];

        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        GZIPInputStream gis = null;
        try {
            gis = new GZIPInputStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }


        byte[] buffer = new byte[1024];
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int len;
        try {
            while ((len = gis.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            gis.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] data = out.toByteArray();

        String s = new String(convertToJson(data, new JsonFactory(), new SmileFactory()));

        return s;
    }


    public byte[] convertToJson(byte[] smile, JsonFactory jsonFactory, SmileFactory smileFactory)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        JsonParser sp = null;
        JsonGenerator jg = null;
        try {
            sp = smileFactory.createParser(smile);
            jg = jsonFactory.createGenerator(bos);
        } catch (IOException e) {
            e.printStackTrace();
        }


        try{
            while (sp.nextToken() != null)
            {
                jg.copyCurrentEvent(sp);
            }
        }
        catch (Exception e)
        {
            Toast.makeText(this, "ERRÖR", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            //log.error("Error while converting smile to json", e);
        }finally {
            try {
                sp.close();
                jg.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bos.toByteArray();
    }





}
