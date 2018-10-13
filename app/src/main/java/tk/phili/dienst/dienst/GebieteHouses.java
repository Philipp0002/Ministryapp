package tk.phili.dienst.dienst;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GebieteHouses extends AppCompatActivity {

    String name;
    Integer id;
    public SharedPreferences sp;
    private SharedPreferences.Editor editor;

    String lastjsonstate = "";

    GebieteHousesAdapter adapter;

    String lastsearch = null;
    Toolbar toolbar;

    ArrayList<Integer> filterlist = new ArrayList<Integer>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gebiete_houses);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        filterlist.add(0);
        filterlist.add(1);
        filterlist.add(2);
        filterlist.add(3);
        filterlist.add(4);
        filterlist.add(5);

        name = getIntent().getStringExtra("name");
        id = getIntent().getIntExtra("id", -1);
        sp = getSharedPreferences("MainActivity", MODE_PRIVATE);
        editor = sp.edit();

        setTitle(name);
        supportPostponeEnterTransition();

        updateList(null, true);

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(GebieteHouses.this);

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

                final EditText input = new EditText(GebieteHouses.this);
                input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4), filter});
                alert.setView(input);

                alert.setPositiveButton(getString(R.string.action_save), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    String value = input.getText().toString();
                    String houseNumber = value.replace(" ", "").toUpperCase();
                    String array_as_string = sp.getString("Gebiet-"+id+"-HOUSES", "[]");
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
                                Toast.makeText(GebieteHouses.this, getString(R.string.gebiet_house_add_invalid), Toast.LENGTH_LONG).show();
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

                    editor.putString("Gebiet-"+id+"-HOUSES", array.toString());
                    editor.commit();
                    updateList(lastsearch, true);

                    }
                });

                alert.setNegativeButton(getString(R.string.gebiet_add_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {     }
                });

                alert.show();
            }
        });

        final ImageView backdrop = ((ImageView)findViewById(R.id.backdrop));

        if(sp.contains("Gebiet-" + id + "-MAP")){
            Bitmap bmp = base64ToBitmap(sp.getString("Gebiet-"+id+"-MAP", ""));
            backdrop.setImageBitmap(bmp);
        }else{
            Drawer.WebStringGetter wsg = new Drawer.WebStringGetter();
            wsg.fc = new FutureCallback<String>() {
                @Override
                public void onCompleted(Exception e, String result) {
                    try {
                        JSONArray json = new JSONArray(result);
                        if (json.length() != 0) {
                            JSONObject obj = json.getJSONObject(0);
                            double lat = Double.parseDouble(obj.getString("lat"));
                            double lon = Double.parseDouble(obj.getString("lon"));
                            editor.putString("Gebiet-" + id + "-MAP-lat", lat+"");
                            editor.putString("Gebiet-" + id + "-MAP-lon", lon+"");
                            editor.commit();
                            Target target = new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    backdrop.setImageBitmap(bitmap);
                                    backdrop.getLayoutParams().height = 400;
                                    String encoded = bitmapToBase64(bitmap);
                                    editor.putString("Gebiet-" + id + "-MAP", encoded);
                                    editor.commit();
                                }

                                @Override
                                public void onBitmapFailed(Drawable errorDrawable) {
                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {
                                }
                            };
                            backdrop.setTag(target);
                            Picasso.with(backdrop.getContext()).load("http://minel0l.lima-city.de/karte/staticmap.php?center=" + lat + "," + lon + "&zoom=20&size=" + 500 + "x" + 400 + "&maptype=mapnik").into(target);
                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

                }
            };
            try {
                wsg.execute("https://nominatim.openstreetmap.org/search?q="+ URLEncoder.encode(name, "UTF-8") +"&format=jsonv2&accept-language=DE");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        backdrop.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        ((ImageView)findViewById(R.id.backdrop)).getViewTreeObserver().removeOnPreDrawListener(this);
                        supportStartPostponedEnterTransition();
                        return true;
                    }
                }
        );

    }


    //boolean ist für änderung des collapsed states
    public void updateList(@Nullable String search, boolean updateCollapsedState){

        /*boolean updateCollapsedState = true;
        for(boolean bool : args){
            updateCollapsedState = false;
        }*/

        boolean hideUnnötige = false;
        if(search != null) {
            hideUnnötige = true;
        }

        String array_as_string = sp.getString("Gebiet-"+id+"-HOUSES", "[]");
        lastjsonstate = array_as_string;
        Log.d("HEYYY", array_as_string);
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

        ArrayList<String> numbers = new ArrayList<String>();
        ArrayList<Integer> householders = new ArrayList<Integer>();
        ArrayList<Boolean> collapsed = new ArrayList<Boolean>();
        ArrayList<ArrayList<String>> householdernames = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<Integer>> householderids = new ArrayList<ArrayList<Integer>>();
        for (int i=0; i < array.length(); i++) {
            try {
                JSONObject o = array.getJSONObject(i);
                //
                ArrayList<String> householdernames1 = new ArrayList<String>();
                ArrayList<Integer> householderids1 = new ArrayList<Integer>();
                JSONArray array1 = o.getJSONArray("householders");
                for (int i1=0; i1 < array1.length(); i1++) {
                    try {
                        JSONObject obj = array1.getJSONObject(i1);
                        String name = null;
                        if(obj.has("first_name") && obj.has("last_name")) {
                            name = obj.getString("first_name") + " " + obj.getString("last_name");
                        }else{
                            name = obj.has("last_name") ? obj.getString("last_name") : "-";
                        }


                        //SEARCH-FEATURE
                        if((search != null && name.toLowerCase().contains(search)) || search == null) {
                            if((obj.has("type") && filterlist.contains(obj.getInt("type"))) || !obj.has("type")) {
                                householdernames1.add(name);
                                householderids1.add(obj.getInt("uid"));
                            }
                        }




                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if(!(householdernames1.isEmpty() && hideUnnötige)){
                    numbers.add(o.getString("number"));
                    householders.add(o.getJSONArray("householders").length());

                    if(hideUnnötige){
                        collapsed.add(false);
                    }else{
                        collapsed.add(true);
                        householdernames1.add(getString(R.string.gebiet_house_add_wohnungsinhaber));
                        householderids1.add(Integer.MAX_VALUE);
                    }

                    householdernames.add(householdernames1);
                    householderids.add(householderids1);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(adapter == null) {
            adapter = new GebieteHousesAdapter(GebieteHouses.this, id, numbers, householders, collapsed, householdernames, householderids);
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            recyclerView.setAdapter(adapter);
            recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            ((AppBarLayout)findViewById(R.id.app_bar)).addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                    if (Math.abs(verticalOffset)-appBarLayout.getTotalScrollRange() == 0)
                    {
                        if(Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
                            View v = getWindow().getDecorView();
                            v.setSystemUiVisibility(View.GONE);
                        } else if(Build.VERSION.SDK_INT >= 19) {
                            //for new api versions.
                            View decorView = getWindow().getDecorView();
                            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                            decorView.setSystemUiVisibility(uiOptions);
                        }
                    }
                    else
                    {
                        if(Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
                            View v = getWindow().getDecorView();
                            v.setSystemUiVisibility(View.VISIBLE);
                        } else if(Build.VERSION.SDK_INT >= 19) {
                            //for new api versions.
                            View decorView = getWindow().getDecorView();
                            int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
                            decorView.setSystemUiVisibility(uiOptions);
                        }


                    }
                }
            });
        }else{
            adapter.numbers.clear();
            adapter.householders.clear();
            adapter.householdernames.clear();
            adapter.householderids.clear();

            if(updateCollapsedState)
            adapter.collapsed.clear();

            adapter.numbers.addAll(numbers);
            adapter.householders.addAll(householders);
            adapter.householdernames.addAll(householdernames);
            adapter.householderids.addAll(householderids);

            if(updateCollapsedState)
            adapter.collapsed.addAll(collapsed);

            adapter.notifyDataSetChanged();
        }

    }



    @Override
    protected void onResume() {
        super.onResume();
        String array_as_string = sp.getString("Gebiet-"+id+"-HOUSES", "[]");
        if(!array_as_string.equalsIgnoreCase(lastjsonstate)){
            updateList(lastsearch, true);
        }
    }

    private Bitmap base64ToBitmap(String b64) {
        byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.gebiete, menu);

        final MenuItem filterMenu = menu.findItem( R.id.action_filter);
        filterMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                boolean[] b = {filterlist.contains(0), filterlist.contains(1), filterlist.contains(2), filterlist.contains(3), filterlist.contains(4), filterlist.contains(5)};

                AlertDialog dialog = new AlertDialog.Builder(GebieteHouses.this)
                        .setTitle(getString(R.string.filter_territory))
                        .setMultiChoiceItems(GebieteHouses.this.getResources().getStringArray(R.array.gebiet_house_interest_types), b, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                                if (isChecked) {
                                    filterlist.add(indexSelected);
                                } else if (filterlist.contains(indexSelected)) {
                                    filterlist.remove(Integer.valueOf(indexSelected));
                                }
                            }
                        }).setPositiveButton(getString(R.string.action_save), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                updateList(lastsearch, false);
                            }
                        }).setCancelable(false).create();
                dialog.show();


                return true;
            }
        });


        MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
        SearchView searchView = (SearchView) myActionMenuItem.getActionView();

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AppBarLayout)findViewById(R.id.app_bar)).setExpanded(false, true);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    updateList(lastsearch = null, true);
                } else {
                    updateList(lastsearch = newText.toLowerCase(), true);
                }
                return true;
            }
        });

        MenuTintUtils.tintAllIcons(menu, Color.WHITE);

        return true;
    }


}
