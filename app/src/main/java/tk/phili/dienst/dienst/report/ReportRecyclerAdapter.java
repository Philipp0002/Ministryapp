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

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.shape.ShapeAppearanceModel;

import java.util.List;

import tk.phili.dienst.dienst.R;
import tk.phili.dienst.dienst.utils.Utils;


public class ReportRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    List<Report> reports;
    private final ReportManager reportManager;
    private final ReportTimer reportTimer;

    public ReportRecyclerAdapter(Context context, List<Report> reports, ReportTimer reportTimer) {
        this.context = context;
        this.reports = reports;
        this.reportTimer = reportTimer;
        this.reportManager = new ReportManager(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            int layoutId = 0;
            if (reportManager.getReportLayoutSetting() == 0) {
                layoutId = R.layout.report_item;
            } else if (reportManager.getReportLayoutSetting() == 1) {
                layoutId = R.layout.report_item_tiny;
            }
            View v = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);

            return new Holder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_item_timer, parent, false);

            return new TimerHolder(v);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return reports.get(position).getType() == Report.Type.TIMER ? 1 : 0;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder _holder, int position) {
        if (_holder instanceof Holder) {
            Holder holder = (Holder) _holder;
            Report report = reports.get(position);

            ShapeAppearanceModel.Builder shapeBuilder = new ShapeAppearanceModel.Builder()
                    .setAllCornerSizes(Utils.dpToPx(32));
            int connectingCornersSize = Utils.dpToPx(16);

            int realPosition = position;
            int realListSize = reports.size();
            if(reports.get(0).getType() == Report.Type.TIMER) {
                realPosition--;
                realListSize--;
            }

            if(realListSize > 1) {
                if (realPosition != realListSize - 1) {
                    shapeBuilder.setBottomRightCornerSize(connectingCornersSize);
                    shapeBuilder.setBottomLeftCornerSize(connectingCornersSize);
                }
                if(realPosition != 0) {
                    shapeBuilder.setTopRightCornerSize(connectingCornersSize);
                    shapeBuilder.setTopLeftCornerSize(connectingCornersSize);
                }
            }
            holder.cardView.setShapeAppearanceModel(shapeBuilder.build());



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

            if (report.getAnnotation() == null || report.getAnnotation().isEmpty()) {
                holder.annotation.setVisibility(View.GONE);
            } else {
                holder.annotation.setVisibility(View.VISIBLE);
            }

            if (color != null) {
                holder.cardView.setCardBackgroundColor(getColor(report));
            }

            holder.cardView.setOnClickListener(v -> onClicked(report, v));
        } else {
            TimerHolder holder = (TimerHolder) _holder;
            Report report = new Report();
            report.setMinutes(reportTimer.getTimer() / 1000 / 60);

            if (reportTimer.getTimerState() == ReportTimer.TimerState.PAUSED) {
                holder.timerPauseButton.setIcon(context.getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp));
                holder.timerPauseButton.setText(R.string.timer_continue);
            } else {
                holder.timerPauseButton.setIcon(context.getResources().getDrawable(R.drawable.ic_baseline_pause_24));
                holder.timerPauseButton.setText(R.string.timer_pause);
            }

            String[] display = report.getFormattedHoursAndMinutes(context);
            holder.time.setText(display[0]);
            holder.timeInfo.setText(display[1]);

            holder.timerPauseButton.setOnClickListener(v -> {
                if (reportTimer.getTimerState() == ReportTimer.TimerState.RUNNING) {
                    reportTimer.pauseTimer();
                } else {
                    reportTimer.startTimer();
                }
            });

            holder.timerStopButton.setOnClickListener(v -> reportTimer.stopTimerAndSave());
        }
    }

    /*
    Can be overridden
     */
    public void onClicked(Report report, View view) {
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    @Override
    public long getItemId(int position) {
        return reports.get(position).getId();
    }

    public Integer getColor(Report report) {
        if (report.getType() == Report.Type.SUMMARY) {
            return null;
        }
        if (report.getType() != Report.Type.NORMAL) {
            return Color.BLACK;
        }
        int dayOfWeek = report.getDate().getDayOfWeek().getValue();
        if (dayOfWeek == 1) {
            return Color.parseColor("#861E6A");
        } else if (dayOfWeek == 2) {
            return Color.parseColor("#AA1656");
        } else if (dayOfWeek == 3) {
            return Color.parseColor("#E5AF4F");
        } else if (dayOfWeek == 4) {
            return Color.parseColor("#C275DF");
        } else if (dayOfWeek == 5) {
            return Color.parseColor("#1279BF");
        } else if (dayOfWeek == 6) {
            return Color.parseColor("#F5BE25");
        } else if (dayOfWeek == 7) {
            return Color.parseColor("#7885CB");
        }

        return 0;
    }

    public class Holder extends RecyclerView.ViewHolder {

        public MaterialCardView cardView;
        public TextView date, time, timeInfo, placements, returnVisits, videos, bibleStudies, annotation;

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

    public class TimerHolder extends RecyclerView.ViewHolder {

        public MaterialCardView cardView;
        public TextView time, timeInfo;
        public MaterialButton timerStopButton, timerPauseButton;

        public TimerHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.card_view);
            time = itemView.findViewById(R.id.bericht_stunden_count);
            timeInfo = itemView.findViewById(R.id.bericht_stunden_info);
            timerStopButton = itemView.findViewById(R.id.bericht_timer_stop);
            timerPauseButton = itemView.findViewById(R.id.bericht_timer_pause);
        }
    }
}
