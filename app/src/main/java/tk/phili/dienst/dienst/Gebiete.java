package tk.phili.dienst.dienst;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.SingleLineTransformationMethod;
import android.util.ArraySet;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Gebiete extends AppCompatActivity {

    public SharedPreferences sp;
    private SharedPreferences.Editor editor;

    GebieteAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gebiete);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_5);
        setSupportActionBar(toolbar);

        toolbar.bringToFront();

        Drawer.addDrawer(this, toolbar, 2);

        sp = getSharedPreferences("MainActivity", MODE_PRIVATE);
        editor = sp.edit();



        updateList();



        findViewById(R.id.imageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(Gebiete.this);

                alert.setTitle(getString(R.string.gebiet_add));
                alert.setMessage(getString(R.string.gebiet_add_msg));

// Set an EditText view to get user input
                final EditText input = new EditText(Gebiete.this);
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

                        if(names.isEmpty()){
                            findViewById(R.id.no_gebiet).setVisibility(View.VISIBLE);
                        }else{
                            findViewById(R.id.no_gebiet).setVisibility(View.GONE);
                        }

                        JSONArray obj1 = new JSONArray();
                        editor.putString("Gebiet-"+(max+1)+"-HOUSES", obj1.toString());
                        editor.commit();

                        adapter.gebietenamen.clear();
                        adapter.gebieteids.clear();

                        adapter.gebietenamen.addAll(names);
                        adapter.gebieteids.addAll(uids);

                        adapter.notifyDataSetChanged();

                    }
                });

                alert.setNegativeButton(getString(R.string.gebiet_add_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();
            }
        });





    }

    public void updateList(){
        String array_as_string = sp.getString("gebiete", "[]");
        JSONArray array = null;
        try {
            array = new JSONArray(array_as_string);
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

        if(names.isEmpty()){
            findViewById(R.id.no_gebiet).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.no_gebiet).setVisibility(View.GONE);
        }

        adapter = new GebieteAdapter(this, names, uids);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

}
