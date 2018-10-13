package tk.phili.dienst.dienst;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fipsi on 04.03.2018.
 */

public class GebieteHouseholdersAdapter extends RecyclerView.Adapter<GebieteHouseholdersAdapter.ViewHolder> {

    public List<String> names;
    public List<Integer> ids;
    public int idgebiet;
    public String number;
    private GebieteHouses context;

    public SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public GebieteHouseholdersAdapter(GebieteHouses context, String number, int idgebiet, List<String> names, List<Integer> ids) {
        this.names = names;
        this.ids = ids;
        this.idgebiet = idgebiet;
        this.number = number;
        this.context = context;

        sp = context.getSharedPreferences("MainActivity", context.MODE_PRIVATE);
        editor = sp.edit();
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gebiete_houses_householders_list_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(final ViewHolder holder, int position) {
        final String name = names.get(position);
        final int id = ids.get(position);
        if(id == Integer.MAX_VALUE){
            holder.householderView.setTypeface(holder.householderView.getTypeface(), Typeface.BOLD);
            holder.imageView.setImageResource(R.drawable.ic_add_black_24dp);
            holder.householderView.setText(name);
            holder.stateView.setText("");
            holder.mainView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String array_as_string = context.getSharedPreferences("MainActivity", context.MODE_PRIVATE).getString("Gebiet-"+idgebiet+"-HOUSES", "[]");
                    JSONArray array = null;
                    try {
                        array = new JSONArray(array_as_string);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    int max = 0;
                    for (int i=0; i < array.length(); i++) {
                        try {
                            JSONObject o = array.getJSONObject(i);
                            if(o.getString("number").equalsIgnoreCase(number)) {
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


                    Intent intent = new Intent(context, ChangeGebietHouseholder.class);
                    intent.putExtra("idgebiet", idgebiet);
                    intent.putExtra("idhouse", number);
                    intent.putExtra("idhouseholder", max+1);
                    intent.putExtra("change", true);
                    context.startActivity(intent);
                }
            });

        }else{

            holder.stateView.setText(context.getResources().getStringArray(R.array.gebiet_house_interest_types)[getInterestType(id,idgebiet,number)]);

            holder.householderView.setText(name);
            holder.mainView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ChangeGebietHouseholder.class);
                    intent.putExtra("idgebiet", idgebiet);
                    intent.putExtra("idhouse", number);
                    intent.putExtra("idhouseholder", id);
                    intent.putExtra("change", false);
                    context.startActivity(intent);
                }
            });

            holder.mainView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    CharSequence colors[] = new CharSequence[]{context.getString(R.string.gebiet_householder_delete), context.getString(R.string.export_share)};

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(name);
                    builder.setItems(colors, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(which == 0){
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage(R.string.gebiet_housenumber_delete_sure)
                                        .setTitle((context.getString(R.string.gebiet_delete_sure_title)).replace("xx", name))
                                        .setPositiveButton(context.getString(R.string.delete_ok), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int idd) {
                                                JSONArray start = new JSONArray();
                                                JSONObject dishouse = new JSONObject();
                                                try {
                                                    dishouse.put("number", number);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                JSONArray householders = new JSONArray();

                                                String jsonstreet = sp.getString("Gebiet-"+idgebiet+"-HOUSES", "[]");
                                                try {
                                                    JSONArray array = new JSONArray(jsonstreet);
                                                    for (int i=0; i < array.length(); i++) {
                                                        JSONObject o = array.getJSONObject(i);
                                                        if(o.getString("number").equalsIgnoreCase(number)){
                                                            boolean done = false;
                                                            JSONArray array2 = o.getJSONArray("householders");
                                                            for (int i2=0; i2 < array2.length(); i2++) {
                                                                JSONObject o2 = array2.getJSONObject(i2);
                                                                if(o2.getInt("uid") != id) {
                                                                    householders.put(o2);
                                                                }
                                                            }
                                                        }else{
                                                            start.put(o);
                                                        }
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                                try {
                                                    dishouse.put("householders", householders);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                start.put(dishouse);
                                                editor.putString("Gebiet-"+idgebiet+"-HOUSES", start.toString());
                                                editor.commit();
                                                context.updateList(null, false);
                                            }
                                        })
                                        .setNegativeButton(context.getString(R.string.delete_cancel), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                            }
                                        }).setIcon(R.drawable.ic_warning_black_24dp).show();
                            }if(which == 1){
                                try {
                                    Export.exportPerson(context, Export.Format.MAEXPORT, idgebiet, number, id);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    builder.show();
                    return false;
                }
            });

        }


        
    }

    @Override public int getItemCount() {
        return names.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView householderView;
        public TextView stateView;
        public ImageView imageView;
        public View mainView;

        public ViewHolder(View itemView) {
            super(itemView);
            mainView = itemView;
            imageView = itemView.findViewById(R.id.householderimg);
            householderView = itemView.findViewById(R.id.householdername);
            stateView = itemView.findViewById(R.id.householderstate);
        }
    }

    //COPIED FROM ChangeGebietHouseholder.java
    public Integer getInterestType(int idHouseholder, int idGebiet, String idHouse){
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


}
