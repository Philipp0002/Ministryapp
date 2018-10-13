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
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import java.util.List;

/**
 * Created by fipsi on 04.03.2018.
 */

public class GebieteAdapter extends RecyclerView.Adapter<GebieteAdapter.ViewHolder> {

    public List<String> gebietenamen;
    public List<Integer> gebieteids;
    private Gebiete context;

    public GebieteAdapter(Gebiete context, List<String> gebietenamen, List<Integer> gebieteids) {
        this.gebietenamen = gebietenamen;
        this.gebieteids = gebieteids;
        this.context = context;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gebiete_list_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(final ViewHolder holder, int position) {

        final SharedPreferences sp = context.getSharedPreferences("MainActivity", context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();

        final String name = gebietenamen.get(position);
        final Integer id = gebieteids.get(position);

        holder.text.setText(name);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.image.setTransitionName("gebiete_details_img");
        }
        holder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, GebieteHouses.class);
                i.putExtra("name", name);
                i.putExtra("id", id);
                ActivityOptionsCompat options = ActivityOptionsCompat. makeSceneTransitionAnimation(context, (View)holder.image, "gebiete_details_img");
                context.startActivity(i, options.toBundle());
            }
        });

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(context, holder.button);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.gebiete_list, popup.getMenu());
                popup.show();

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId() == R.id.action_gebiet_maps){
                            CharSequence colors[] = null;
                            if(sp.getString("Gebiet-" + id + "-MAP-lat", null) != null) {
                                colors = new CharSequence[]{context.getString(R.string.gebiet_maps_google), context.getString(R.string.gebiet_maps_osm)};
                            }else{
                                colors = new CharSequence[]{context.getString(R.string.gebiet_maps_google)};
                            }

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle(context.getString(R.string.gebiet_maps_title));
                            builder.setItems(colors, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(which == 0){
                                        //GOOGLE MAPS
                                        Uri gmmIntentUri = Uri.parse("geo:0,0?q="+Uri.encode(name));
                                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                        mapIntent.setPackage("com.google.android.apps.maps");
                                        if (mapIntent.resolveActivity(context.getPackageManager()) == null) {
                                            Toast.makeText(context, context.getString(R.string.gebiet_maps_no_map_app), Toast.LENGTH_SHORT);
                                            String url = "https://google.com/maps/search/"+name;
                                            Intent i = new Intent(Intent.ACTION_VIEW);
                                            i.setData(Uri.parse(url));
                                            context.startActivity(i);
                                        }else{
                                            context.startActivity(mapIntent);
                                        }
                                    }else if(which == 1){
                                        //OPEN STREET MAP
                                        String url = "https://www.openstreetmap.org/#map=19/"+Double.parseDouble(sp.getString("Gebiet-" + id + "-MAP-lat", "0"))+"/"+Double.parseDouble(sp.getString("Gebiet-" + id + "-MAP-lon", "0"));
                                        Intent i = new Intent(Intent.ACTION_VIEW);
                                        i.setData(Uri.parse(url));
                                        context.startActivity(i);
                                    }
                                }
                            });
                            builder.show();

                        }else if(item.getItemId() == R.id.action_gebiet_delete){
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(R.string.gebiet_delete_sure)
                                    .setTitle((context.getString(R.string.gebiet_delete_sure_title)).replace("xx", name))
                                    .setPositiveButton(context.getString(R.string.delete_ok), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int idd) {
                                            editor.remove("Gebiet-" + id + "-MAP-lat");
                                            editor.remove("Gebiet-" + id + "-MAP-lon");
                                            editor.remove("Gebiet-" + id + "-MAP");
                                            String array_as_string = sp.getString("gebiete", "[]");
                                            JSONArray array = null;
                                            try {
                                                array = new JSONArray(array_as_string);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            JSONArray newArray = new JSONArray();

                                            for (int i=0; i < array.length(); i++) {
                                                try {
                                                    JSONObject o = array.getJSONObject(i);
                                                    if(o.getLong("uid") != id){
                                                       newArray.put(o);
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            editor.putString("gebiete", newArray.toString());
                                            editor.commit();
                                            context.updateList();
                                        }
                                    })
                                    .setNegativeButton(context.getString(R.string.delete_cancel), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                        }
                                    }).setIcon(R.drawable.ic_warning_black_24dp).show();
                        }/*else if(item.getItemId() == R.id.action_gebiet_send){
                            //TODO SEND
                        }*/
                        return false;
                    }
                });
            }
        });

        if(!sp.contains("Gebiet-"+id+"-MAP")) {
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
                                    holder.image.setImageBitmap(bitmap);
                                    holder.image.getLayoutParams().height = 400;
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
                            holder.image.setTag(target);
                            Picasso.with(context).load("http://minel0l.lima-city.de/karte/staticmap.php?center=" + lat + "," + lon + "&zoom=20&size=" + 500 + "x" + 400 + "&maptype=mapnik").into(target);
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
        }else{
            Bitmap bmp = base64ToBitmap(sp.getString("Gebiet-"+id+"-MAP", ""));
            holder.image.getLayoutParams().height = 400;
            holder.image.setImageBitmap(bmp);
        }



    }

    @Override public int getItemCount() {
        return gebietenamen.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public ImageButton button;
        public TextView text;
        public View mainView;

        public ViewHolder(View itemView) {
            super(itemView);
            mainView = itemView;
            button = itemView.findViewById(R.id.menu);
            image = itemView.findViewById(R.id.imagemap);
            text = itemView.findViewById(R.id.textmap);
        }
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private Bitmap base64ToBitmap(String b64) {
        byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }
}
