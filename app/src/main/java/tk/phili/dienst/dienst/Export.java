package tk.phili.dienst.dienst;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.zip.GZIPOutputStream;

/**
 * Created by fipsi on 04.08.2018.
 */

public class Export {

    public enum Format{
        MAEXPORT;
    }


    public static void exportPerson(Context c, Format format, long gebietUID, String houseNumber, int personUID) throws JSONException{
        SharedPreferences sp = c.getSharedPreferences("MainActivity", c.MODE_PRIVATE);

        String array_as_string = sp.getString("gebiete", "[]");
        JSONArray array = null;
        try {
            array = new JSONArray(array_as_string);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HashMap<Integer, String> idnames = new HashMap<Integer, String>();
        for (int i=0; i < array.length(); i++) {
            try {
                JSONObject o = array.getJSONObject(i);
                idnames.put(o.getInt("uid"), o.getString("address"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.d("GEBIET_CONVERTUUUU", idnames+"");

        String address = idnames.get((int)gebietUID);
        Log.d("GEBIET_CONVERTUUUU", address+" "+gebietUID);
        String firstname = getFirstName(sp, gebietUID, personUID, houseNumber);
        String lastname =  getLastName(sp, gebietUID, personUID, houseNumber);
        boolean isMale = isMale(sp, gebietUID, personUID, houseNumber);
        int interestType = getInterestType(sp, gebietUID, personUID, houseNumber);
        String lang = getLang(sp, gebietUID, personUID, houseNumber);
        String notes = getNotes(sp, gebietUID, personUID, houseNumber);
        int age = getAge(sp, gebietUID, personUID, houseNumber);
        ArrayList<String> pub = getPublications(sp, gebietUID, personUID, houseNumber);

        if(format == Format.MAEXPORT){
            age = (int) 5*(Math.round((int)age/5));
            int id = (int)getRandomNumberInRange(100000000, 999999999);
            JSONObject obj = new JSONObject();
            obj.put("FileVersion", 27);
            JSONObject objPerson = new JSONObject();
            JSONObject objP = new JSONObject();

            JSONObject objInf = new JSONObject();
            JSONObject objPI = new JSONObject();

            String pubs = "";
            for(String p : pub){
                pubs += p + "\n";
            }
            //objPI.put("Sc", "");
            objPI.put("Pub", pubs.trim());
            //objPI.put("Mov", "");
            objPI.put("PtI", isMale ? 1 : 0);
            objPI.put("D", 0);
            objPI.put("Ag", age);
            objPI.put("St", false);

            objInf.put("PI", objPI);
            objP.put("Inf", objInf);

            objP.put("TpI", isMale ? 1 : 0);
            objP.put("Add", address + " " + houseNumber);
            objP.put("N", (firstname +" " + lastname).trim());
            objP.put("Age", age);
            objP.put("Hidden", false);
            objP.put("Pinned", false);

            objP.put("Intd", (interestType == 1 || interestType == 4 || interestType == 5) ? true : false);
            objP.put("LstTry", 0);
            objP.put("Prio", 0);
            objP.put("VTm", 0);

            objP.put("Ton", lang);
            objP.put("Gen", notes);

            objP.put("Date", 0);
            objP.put("ID", id);
            objP.put("Type", interestType == 5 ? 1 : (interestType == 4) ? 2 : 0);

            objP.put("Lat", 0);
            objP.put("Lon", 0);

            objPerson.put("P", objP);

            obj.put("Person", objPerson);

            Log.d("GEBIET_CONVERTUUUU", obj.toString());

            try {

                File cachePath = new File(c.getCacheDir(), "exports");
                cachePath.mkdirs(); // don't forget to make the directory

                FileOutputStream stream = new FileOutputStream(cachePath + "/MAPerson"+id+".maexport"); // overwrites this image every time

                GZIPOutputStream gzipOutputStream = new GZIPOutputStream(stream);

                gzipOutputStream.write(convertToSmile(obj.toString().getBytes(), new JsonFactory(), new SmileFactory()));
                gzipOutputStream.close();
                stream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            File imagePath = new File(c.getCacheDir(), "exports");
            File newFile = new File(imagePath, "MAPerson"+id+".maexport");
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

    }

    public static String getFirstName(SharedPreferences sp, long idGebiet, int idHouseholder, String idHouse){
        String jsonstreet = sp.getString("Gebiet-"+idGebiet+"-HOUSES", "[]");
        try {
            JSONArray array = new JSONArray(jsonstreet);
            for (int i=0; i < array.length(); i++) {
                JSONObject o = array.getJSONObject(i);
                if(o.getString("number").equalsIgnoreCase(idHouse)){
                    JSONArray array2 = o.getJSONArray("householders");
                    for (int i2=0; i2 < array2.length(); i2++) {
                        JSONObject o2 = array2.getJSONObject(i2);
                        if(o2.getInt("uid") == idHouseholder) {
                            return o2.getString("first_name");
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getLastName(SharedPreferences sp, long idGebiet, int idHouseholder, String idHouse){
        String jsonstreet = sp.getString("Gebiet-"+idGebiet+"-HOUSES", "[]");
        try {
            JSONArray array = new JSONArray(jsonstreet);
            for (int i=0; i < array.length(); i++) {
                JSONObject o = array.getJSONObject(i);
                if(o.getString("number").equalsIgnoreCase(idHouse)){
                    JSONArray array2 = o.getJSONArray("householders");
                    for (int i2=0; i2 < array2.length(); i2++) {
                        JSONObject o2 = array2.getJSONObject(i2);
                        if(o2.getInt("uid") == idHouseholder) {
                            return o2.getString("last_name");
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isMale(SharedPreferences sp, long idGebiet, int idHouseholder, String idHouse){
        String jsonstreet = sp.getString("Gebiet-"+idGebiet+"-HOUSES", "[]");
        try {
            JSONArray array = new JSONArray(jsonstreet);
            for (int i=0; i < array.length(); i++) {
                JSONObject o = array.getJSONObject(i);
                if(o.getString("number").equalsIgnoreCase(idHouse)){
                    JSONArray array2 = o.getJSONArray("householders");
                    for (int i2=0; i2 < array2.length(); i2++) {
                        JSONObject o2 = array2.getJSONObject(i2);
                        if(o2.getInt("uid") == idHouseholder) {
                            return o2.getBoolean("male");
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static Integer getInterestType(SharedPreferences sp, long idGebiet, int idHouseholder, String idHouse){
        String jsonstreet = sp.getString("Gebiet-"+idGebiet+"-HOUSES", "[]");
        try {
            JSONArray array = new JSONArray(jsonstreet);
            for (int i=0; i < array.length(); i++) {
                JSONObject o = array.getJSONObject(i);
                if(o.getString("number").equalsIgnoreCase(idHouse)){
                    JSONArray array2 = o.getJSONArray("householders");
                    for (int i2=0; i2 < array2.length(); i2++) {
                        JSONObject o2 = array2.getJSONObject(i2);
                        if(o2.getInt("uid") == idHouseholder) {
                            return o2.getInt("type");
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getLang(SharedPreferences sp, long idGebiet, int idHouseholder, String idHouse){
        String jsonstreet = sp.getString("Gebiet-"+idGebiet+"-HOUSES", "[]");
        try {
            JSONArray array = new JSONArray(jsonstreet);
            for (int i=0; i < array.length(); i++) {
                JSONObject o = array.getJSONObject(i);
                if(o.getString("number").equalsIgnoreCase(idHouse)){
                    JSONArray array2 = o.getJSONArray("householders");
                    for (int i2=0; i2 < array2.length(); i2++) {
                        JSONObject o2 = array2.getJSONObject(i2);
                        if(o2.getInt("uid") == idHouseholder) {
                            return o2.getString("native_lang");
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getNotes(SharedPreferences sp, long idGebiet, int idHouseholder, String idHouse){
        String jsonstreet = sp.getString("Gebiet-"+idGebiet+"-HOUSES", "[]");
        try {
            JSONArray array = new JSONArray(jsonstreet);
            for (int i=0; i < array.length(); i++) {
                JSONObject o = array.getJSONObject(i);
                if(o.getString("number").equalsIgnoreCase(idHouse)){
                    JSONArray array2 = o.getJSONArray("householders");
                    for (int i2=0; i2 < array2.length(); i2++) {
                        JSONObject o2 = array2.getJSONObject(i2);
                        if(o2.getInt("uid") == idHouseholder) {
                            return o2.getString("notes");
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Integer getAge(SharedPreferences sp, long idGebiet, int idHouseholder, String idHouse){
        String jsonstreet = sp.getString("Gebiet-"+idGebiet+"-HOUSES", "[]");
        try {
            JSONArray array = new JSONArray(jsonstreet);
            for (int i=0; i < array.length(); i++) {
                JSONObject o = array.getJSONObject(i);
                if(o.getString("number").equalsIgnoreCase(idHouse)){
                    JSONArray array2 = o.getJSONArray("householders");
                    for (int i2=0; i2 < array2.length(); i2++) {
                        JSONObject o2 = array2.getJSONObject(i2);
                        if(o2.getInt("uid") == idHouseholder) {
                            return o2.getInt("age");
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static ArrayList<String> getPublications(SharedPreferences sp, long idGebiet, int idHouseholder, String idHouse){
        ArrayList<String> list = new ArrayList<String>();
        String jsonstreet = sp.getString("Gebiet-"+idGebiet+"-HOUSES", "[]");
        try {
            JSONArray array = new JSONArray(jsonstreet);
            for (int i=0; i < array.length(); i++) {
                JSONObject o = array.getJSONObject(i);
                if(o.getString("number").equalsIgnoreCase(idHouse)){
                    JSONArray array2 = o.getJSONArray("householders");
                    for (int i2=0; i2 < array2.length(); i2++) {
                        JSONObject o2 = array2.getJSONObject(i2);
                        if(o2.getInt("uid") == idHouseholder) {

                            JSONArray array3 = o2.getJSONArray("publications");
                            for (int i3=0; i3 < array3.length(); i3++) {
                                String o3 = array3.getString(i3);
                                list.add(o3);
                            }

                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static byte[] convertToSmile(byte[] json, JsonFactory jsonFactory, SmileFactory smileFactory)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        JsonGenerator jg = null;
        JsonParser jp = null;
        try {
            jg =  smileFactory.createGenerator(bos);
            jp = jsonFactory.createParser(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try{
            while (jp.nextToken() != null) {
                jg.copyCurrentEvent(jp);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                jg.close();
                jp.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bos.toByteArray();
    }

    public static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }


}
