package tk.phili.dienst.dienst;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TableLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;

public class BerichtList extends ArrayAdapter<String>{

    //DECLARATIONS
    String[] ids,dates,hours,abgaben,rück,videos,bibelstudien = {};
    Context c;
    LayoutInflater inflater;

    public BerichtList(Context context, String[] ids, String[] dates, String[] hours, String[] abgaben, String[] rück, String[] videos, String[] bibelstudien) {
        super(context, R.layout.list_bericht, ids);
        this.c = context;
        this.ids = ids;
        this.dates = dates;
        this.hours = hours;
        this.abgaben = abgaben;
        this.rück = rück;
        this.videos = videos;
        this.bibelstudien = bibelstudien;
    }

    public class ViewHolder{
        CardView cardv;
        TextView dateTv;
        TextView stundenTv;
        TextView abgabenTv;
        TextView rückTv;
        TextView videosTv;
        TextView studienTv;
    }

    public String getIdofPosition(int position){
        return ids[position];
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView==null){
            inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            int layout = c.getSharedPreferences("MainActivity", c.MODE_PRIVATE).getInt("report_layout", 0);

            if(layout == 0) {
                convertView = inflater.inflate(R.layout.list_bericht, null);
            }else if(layout == 1) {
                convertView = inflater.inflate(R.layout.list_bericht_tiny, null);
            }
        }

        final ViewHolder holder = new ViewHolder();
        holder.cardv = (CardView) convertView.findViewById(R.id.card_view);
        holder.dateTv = (TextView) convertView.findViewById(R.id.bericht_date);
        holder.stundenTv = (TextView) convertView.findViewById(R.id.bericht_stunden_count);
        holder.abgabenTv = (TextView) convertView.findViewById(R.id.bericht_brosch_count);
        holder.rückTv = (TextView) convertView.findViewById(R.id.bericht_rueck_count);
        holder.videosTv = (TextView) convertView.findViewById(R.id.bericht_videos_count);
        holder.studienTv = (TextView) convertView.findViewById(R.id.bericht_studies_count);

        if(hours[position].endsWith("min")){
            holder.stundenTv.setText(hours[position].replace("min", ""));
            ((TextView) convertView.findViewById(R.id.bericht_stunden_info)).setText(c.getString(R.string.minutes));
        }else{ holder.stundenTv.setText(hours[position]); }
        holder.dateTv.setText(dates[position]);
        holder.abgabenTv.setText(abgaben[position]);
        holder.rückTv.setText(rück[position]);
        holder.videosTv.setText(videos[position]);
        holder.studienTv.setText(bibelstudien[position]);

        String date = dates[position];
        if(holder.cardv != null){
            holder.cardv.setCardBackgroundColor(getColor(date));
        }
        if(date.startsWith("32.") || date.startsWith("0.")){
            holder.dateTv.setText(c.getString(R.string.carryover));
        }


        return convertView;
    }



    public int getColor(String date) {
        if(date.startsWith("32.") || date.startsWith("0.")){
            return Color.BLACK;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        try {
            Calendar c = Calendar.getInstance();
            Date dateobj = formatter.parse(date);
            c.setTime(dateobj);

            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
            if(dayOfWeek == Calendar.MONDAY){
                return Color.parseColor("#861E6A");
            }else if(dayOfWeek == Calendar.TUESDAY){
                return Color.parseColor("#AA1656");
            }else if(dayOfWeek == Calendar.WEDNESDAY){
                return Color.parseColor("#E5AF4F");
            }else if(dayOfWeek == Calendar.THURSDAY){
                return Color.parseColor("#C275DF");
            }else if(dayOfWeek == Calendar.FRIDAY){
                return Color.parseColor("#1279BF");
            }else if(dayOfWeek == Calendar.SATURDAY){
                return Color.parseColor("#F5BE25");
            }else if(dayOfWeek == Calendar.SUNDAY){
                return Color.parseColor("#7885CB");
            }
        } catch (ParseException e) { }


        return 0;
    }

}
