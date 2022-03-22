package tk.phili.dienst.dienst.report;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tk.phili.dienst.dienst.R;


public class ReportRecyclerAdapter extends RecyclerView.Adapter<ReportRecyclerAdapter.Holder>{

    Context context;
    List<Report> reports;
    ReportManager reportManager;

    public ReportRecyclerAdapter(Context context, List<Report> reports){
        this.context = context;
        this.reports = reports;
        this.reportManager = new ReportManager(context);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = 0;
        if(reportManager.getReportLayoutSetting() == 0) {
            layoutId = R.layout.list_bericht;
        }else if(reportManager.getReportLayoutSetting() == 1) {
            layoutId = R.layout.list_bericht_tiny;
        }
        View v = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);

        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Report report = reports.get(position);

        String[] formattedTime = report.getFormattedHoursAndMinutes(context);
        Integer color = getColor(report);

        holder.date.setText(report.getFormattedDate(context));
        holder.time.setText(formattedTime[0]);
        holder.timeInfo.setText(formattedTime[1]);
        holder.bibleStudies.setText(Integer.toString(report.getBibleStudies()));
        holder.annotation.setText(report.getAnnotation());
        holder.returnVisits.setText(Integer.toString(report.getReturnVisits()));
        holder.placements.setText(Integer.toString(report.getPlacements()));
        holder.videos.setText(Integer.toString(report.getVideos()));

        if(report.getAnnotation() == null || report.getAnnotation().isEmpty()){
            holder.annotation.setVisibility(View.GONE);
        }else{
            holder.annotation.setVisibility(View.VISIBLE);
        }

        if(color != null) {
            holder.cardView.setCardBackgroundColor(getColor(report));
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClicked(report, v);
            }
        });
    }

    /*
    Can be overridden
     */
    public void onClicked(Report report, View view){ }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    @Override
    public long getItemId(int position) {
        return reports.get(position).getId();
    }

    public Integer getColor(Report report) {
        if(report.getType() == Report.Type.SUMMARY){
            return null;
        }
        if(report.getType() != Report.Type.NORMAL){
            return Color.BLACK;
        }
        int dayOfWeek = report.getDate().getDayOfWeek().getValue();
        if(dayOfWeek == 1){
            return Color.parseColor("#861E6A");
        }else if(dayOfWeek == 2){
            return Color.parseColor("#AA1656");
        }else if(dayOfWeek == 3){
            return Color.parseColor("#E5AF4F");
        }else if(dayOfWeek == 4){
            return Color.parseColor("#C275DF");
        }else if(dayOfWeek == 5){
            return Color.parseColor("#1279BF");
        }else if(dayOfWeek == 6){
            return Color.parseColor("#F5BE25");
        }else if(dayOfWeek == 7){
            return Color.parseColor("#7885CB");
        }

        return 0;
    }

    public class Holder extends RecyclerView.ViewHolder {

        public CardView cardView;
        public TextView date;
        public TextView time;
        public TextView timeInfo;
        public TextView placements;
        public TextView returnVisits;
        public TextView videos;
        public TextView bibleStudies;
        public TextView annotation;

        public Holder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.card_view);
            date = itemView.findViewById(R.id.bericht_date);
            time = itemView.findViewById(R.id.bericht_stunden_count);
            timeInfo = itemView.findViewById(R.id.bericht_stunden_info);
            placements = itemView.findViewById(R.id.bericht_brosch_count);
            returnVisits = itemView.findViewById(R.id.bericht_rueck_count);
            videos = itemView.findViewById(R.id.bericht_videos_count);
            bibleStudies = itemView.findViewById(R.id.bericht_studies_count);
            annotation = itemView.findViewById(R.id.bericht_desc);
        }

    }
}
