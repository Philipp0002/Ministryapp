package tk.phili.dienst.dienst;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.tylersuehr.chips.Chip;
import com.tylersuehr.chips.ChipsInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ChangeGebietHouseholder extends AppCompatActivity {

    String name;
    Integer idHouseholder;
    String idHouse;
    Integer idGebiet;
    boolean change;
    public SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_gebiet_householder);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        idHouseholder = getIntent().getIntExtra("idhouseholder", -1);
        idHouse = getIntent().getStringExtra("idhouse");
        idGebiet = getIntent().getIntExtra("idgebiet", -1);
        change = getIntent().getBooleanExtra("change", false);
        sp = getSharedPreferences("MainActivity", MODE_PRIVATE);
        editor = sp.edit();

        changeState(change);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean ok = true;
                if(change){
                    try {
                        ok = save();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if(ok)
                    changeState(!change);
            }
        });



    }

    public void changeState(boolean changeType){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        int displayedChild = 0;
        if(changeType){
            displayedChild = 1;
        }
        ((ViewSwitcher)findViewById(R.id.switcher1)).setDisplayedChild(displayedChild);
        ((ViewSwitcher)findViewById(R.id.switcher2)).setDisplayedChild(displayedChild);
        ((ViewSwitcher)findViewById(R.id.switcher3)).setDisplayedChild(displayedChild);
        ((ViewSwitcher)findViewById(R.id.switcher4)).setDisplayedChild(displayedChild);
        ((ViewSwitcher)findViewById(R.id.switcher5)).setDisplayedChild(displayedChild);
        ((ViewSwitcher)findViewById(R.id.switcher6)).setDisplayedChild(displayedChild);
        ((ViewSwitcher)findViewById(R.id.switcher7)).setDisplayedChild(displayedChild);

        if(changeType){
            change = true;
            fab.setImageResource(R.drawable.ic_save_black_24dp);
            ((EditText)findViewById(R.id.nameedit)).setText((getLastName() + " " + getFirstName()).trim());
            ((EditText)findViewById(R.id.nameedit)).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

                @Override
                public void afterTextChanged(Editable editable) {
                    getSupportActionBar().setTitle(((EditText)findViewById(R.id.nameedit)).getText());
                }
            });
            Spinner malefemale = findViewById(R.id.genderedit);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.gebiet_house_genders));
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            malefemale.setAdapter(dataAdapter);
            Spinner interest = findViewById(R.id.typeedit);
            ArrayAdapter<String> interestAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.gebiet_house_interest_types));
            interestAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            interest.setAdapter(interestAdapter);
            interest.setSelection(getInterestType());
            if(isMale()){ malefemale.setSelection(0);
            }else{ malefemale.setSelection(1); }
            ((EditText)findViewById(R.id.langedit)).setText(getLang());
            ((EditText)findViewById(R.id.notesedit)).setText(getNotes());
            ((EditText)findViewById(R.id.ageedit)).setText(getAge() == 0 ? "" : getAge()+"");

            ChipsInputLayout cil = findViewById(R.id.publicationsedit);
            for(final String s : getPublications()) {
                cil.addSelectedChip(new PublikationChip(s));
            }


            /*final ChipsInput input = findViewById(R.id.publicationsedit);
            input.setShowChipDetailed(false);
            final RecyclerView recyclerView = (RecyclerView) input.findViewById(R.id.chips_recycler);
            recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener(){
                public void onChildViewAttachedToWindow(View view) {
                    Log.d("HEYYYY", "asdffffff");
                    final ChipsInputEditText chipsInputEditText = (ChipsInputEditText)recyclerView.getChildAt(recyclerView.getChildCount() - 1);
                    chipsInputEditText.addTextChangedListener(new TextWatcher() {
                        public void afterTextChanged(Editable s) {
                            if(chipsInputEditText.getText().toString().contains(",")){
                                input.addChip(chipsInputEditText.getText().toString().split(",")[0], "Publikation");
                            }
                        }
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                        public void onTextChanged(CharSequence s, int start, int before, int count) {}
                    });

                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            for(final String s : getPublications()) {
                                ChangeGebietHouseholder.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        input.addChip(s, "Publikation");
                                    }
                                });
                            }
                        }
                    }, 1000);


                    recyclerView.removeOnChildAttachStateChangeListener(this);
                }
                public void onChildViewDetachedFromWindow(View view) {}
            });*/
            setTitle(getString(R.string.gebiet_householder_edit));

        }else{
            change = false;
            fab.setImageResource(R.drawable.ic_create_black_24dp);
            getSupportActionBar().setTitle(idHouse);
            ((TextView)findViewById(R.id.name)).setText((getLastName() + " " + getFirstName()).trim());
            ((TextView)findViewById(R.id.gender)).setText(isMale() ? getResources().getStringArray(R.array.gebiet_house_genders)[0] : getResources().getStringArray(R.array.gebiet_house_genders)[1]);

            String pubs = "";
            for(final String s : getPublications()) {
                pubs += s + "\n";
            }
            ((TextView)findViewById(R.id.publications)).setText(pubs.trim());
            ChipsInputLayout cil = findViewById(R.id.publicationsedit);
            cil.clearSelectedChips();
            cil.clearFilteredChips();

            ((TextView)findViewById(R.id.type)).setText(getResources().getStringArray(R.array.gebiet_house_interest_types)[getInterestType()]);
            ((TextView)findViewById(R.id.lang)).setText(getLang());
            ((TextView)findViewById(R.id.notes)).setText(getNotes());
            ((TextView)findViewById(R.id.age)).setText(getAge() == 0 ? "" : getAge()+"");
        }

    }


    public boolean save() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("uid", idHouseholder);
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
                return false;
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
        /*ChipsInput in = findViewById(R.id.publicationsedit);
        for(ChipInterface inter : in.getSelectedChipList()){
            arrays.put(inter.getLabel());
        }*/
        obj.put("publications", arrays);

        EditText notes = findViewById(R.id.notesedit);
        if(!notes.getText().toString().isEmpty()){
            obj.put("notes", notes.getText().toString());
        }


        JSONArray start = new JSONArray();
        JSONObject dishouse = new JSONObject();
        dishouse.put("number", idHouse);
        JSONArray householders = new JSONArray();

        String jsonstreet = sp.getString("Gebiet-"+idGebiet+"-HOUSES", "[]");
        try {
            JSONArray array = new JSONArray(jsonstreet);
            for (int i=0; i < array.length(); i++) {
                JSONObject o = array.getJSONObject(i);
                if(o.getString("number").equalsIgnoreCase(idHouse)){


                    Log.d("AYYYY", "d");
                    boolean done = false;
                    JSONArray array2 = o.getJSONArray("householders");
                    for (int i2=0; i2 < array2.length(); i2++) {
                        JSONObject o2 = array2.getJSONObject(i2);
                        if(o2.getInt("uid") == idHouseholder) {
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
        editor.putString("Gebiet-"+idGebiet+"-HOUSES", start.toString());
        editor.commit();
        return true;
    }

    public String getFirstName(){
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

    public String getLastName(){
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

    public boolean isMale(){
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

    public Integer getInterestType(){
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

    public String getLang(){
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

    public String getNotes(){
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

    public Integer getAge(){
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

    public ArrayList<String> getPublications(){
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
}
