package tk.phili.dienst.dienst;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import java.util.HashMap;
import java.util.List;

/**
 * Created by fipsi on 04.03.2018.
 */

public class GebieteHousesAdapter extends RecyclerView.Adapter<GebieteHousesAdapter.ViewHolder> {

    public List<String> numbers;
    public List<Integer> householders;
    public List<Boolean> collapsed;
    public ArrayList<ArrayList<String>> householdernames;
    public ArrayList<ArrayList<Integer>> householderids;
    public int gebietid;
    private GebieteHouses context;

    public SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public GebieteHousesAdapter(GebieteHouses context, int gebietid, List<String> numbers, List<Integer> householders, List<Boolean> collapsed, ArrayList<ArrayList<String>> householdernames, ArrayList<ArrayList<Integer>> householderids) {
        this.numbers = numbers;
        this.householders = householders;
        this.householdernames = householdernames;
        this.householderids = householderids;
        this.collapsed = collapsed;
        this.gebietid = gebietid;
        this.context = context;

        sp = context.getSharedPreferences("MainActivity", context.MODE_PRIVATE);
        editor = sp.edit();
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gebiete_houses_list_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(final ViewHolder holder, final int position) {

        final String number = numbers.get(position);
        final Integer householdercount = householders.get(position);

        holder.numberView.setText(number);
        holder.householderView.setText(householdercount + " " + context.getString(R.string.gebiet_house_wohnungsinhaber));

        if(collapsed.get(position)){
            holder.recyclerView.setVisibility(View.GONE);
            holder.collapseImage.setRotation(-90);
        }else {
            holder.collapseImage.setRotation(0);
            holder.recyclerView.setVisibility(View.VISIBLE);
            GebieteHouseholdersAdapter adapter = new GebieteHouseholdersAdapter(context, number, gebietid, householdernames.get(position), householderids.get(position));
            RecyclerView recyclerView = (RecyclerView) holder.recyclerView;
            recyclerView.setAdapter(adapter);
            recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        }

        holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                CharSequence colors[] = new CharSequence[]{context.getString(R.string.gebiet_house_rename), context.getString(R.string.gebiet_house_delete)};

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(context.getString(R.string.gebiet_house_change).replace("xx", number));
                builder.setItems(colors, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){



                            AlertDialog.Builder alert = new AlertDialog.Builder(context);

                            alert.setTitle(context.getString(R.string.gebiet_house_change).replace("xx", number));
                            alert.setMessage(context.getString(R.string.gebiet_house_add_msg_change));

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

                            final EditText input = new EditText(context);
                            input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4), filter});
                            alert.setView(input);

                            input.setText(number);

                            alert.setPositiveButton(context.getString(R.string.action_save), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    String value = input.getText().toString();
                                    String houseNumber = value.replace(" ", "").toUpperCase();
                                    String array_as_string = sp.getString("Gebiet-"+gebietid+"-HOUSES", "[]");
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
                                                Toast.makeText(context, context.getString(R.string.gebiet_house_add_invalid), Toast.LENGTH_LONG).show();
                                                return;
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    JSONArray arraynew = new JSONArray();
                                    for (int i=0; i < array.length(); i++) {
                                        try {
                                            JSONObject o = array.getJSONObject(i);
                                            if(o.getString("number").equalsIgnoreCase(number)){
                                                o.put("number", houseNumber);
                                            }
                                            arraynew.put(o);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }


                                    editor.putString("Gebiet-"+gebietid+"-HOUSES", arraynew.toString());
                                    editor.commit();
                                    context.updateList(null, false);

                                }
                            });

                            alert.setNegativeButton(context.getString(R.string.gebiet_add_cancel), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {     }
                            });

                            alert.show();
                        }else if(which == 1){
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(R.string.gebiet_housenumber_delete_sure)
                                    .setTitle((context.getString(R.string.gebiet_delete_sure_title)).replace("xx", number))
                                    .setPositiveButton(context.getString(R.string.delete_ok), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int idd) {
                                            JSONArray start = new JSONArray();
                                            String jsonstreet = sp.getString("Gebiet-"+gebietid+"-HOUSES", "[]");
                                            try {
                                                JSONArray array = new JSONArray(jsonstreet);
                                                for (int i=0; i < array.length(); i++) {
                                                    JSONObject o = array.getJSONObject(i);
                                                    if(!o.getString("number").equalsIgnoreCase(number)){
                                                        start.put(o);
                                                    }
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            editor.putString("Gebiet-"+gebietid+"-HOUSES", start.toString());
                                            editor.commit();
                                            context.updateList(null, false);
                                        }
                                    })
                                    .setNegativeButton(context.getString(R.string.delete_cancel), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                        }
                                    }).setIcon(R.drawable.ic_warning_black_24dp).show();
                        }
                    }
                });
                builder.show();
                return false;
            }
        });

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collapsed.set(position, !collapsed.get(position));
                if(collapsed.get(position)){
                    holder.recyclerView.setVisibility(View.GONE);
                    holder.collapseImage.setRotation(-90);
                }else {
                    holder.collapseImage.setRotation(0);
                    holder.recyclerView.setVisibility(View.VISIBLE);
                    GebieteHouseholdersAdapter adapter = new GebieteHouseholdersAdapter(context, number, gebietid, householdernames.get(position), householderids.get(position));
                    RecyclerView recyclerView = (RecyclerView) holder.recyclerView;
                    recyclerView.setAdapter(adapter);
                    recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                }
            }
        });
    }

    @Override public int getItemCount() {
        return numbers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public ImageView collapseImage;
        public TextView numberView;
        public TextView householderView;
        public RecyclerView recyclerView;
        public LinearLayout linearLayout;
        public View mainView;

        public ViewHolder(View itemView) {
            super(itemView);
            mainView = itemView;
            numberView = itemView.findViewById(R.id.housenumber);
            householderView = itemView.findViewById(R.id.householdercount);
            imageView = itemView.findViewById(R.id.householderimg);
            collapseImage = itemView.findViewById(R.id.collapseimg);
            recyclerView = itemView.findViewById(R.id.recyclerViewHouseholders);
            linearLayout = itemView.findViewById(R.id.contentCollapser);
        }
    }
}
