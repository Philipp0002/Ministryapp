package tk.phili.dienst.dienst.newreport;

import android.content.Context;

import java.text.DateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;

import tk.phili.dienst.dienst.R;

public class Report {

    private long id;
    private LocalDate date;
    private long minutes;
    private int placements;
    private int returnVisits;
    private int videos;
    private int bibleStudies;
    private String annotation;
    private Type type = Type.NORMAL;

    public Report(){
        this(0, null, 0, 0, 0,0,0,null);
    }

    /**
     * Create a report object with type Type.Normal.
     * @param id Report ID
     * @param date Date of Report
     * @param minutes Minutes in ministry
     * @param placements Amount of placements
     * @param returnVisits Amount of return visits
     * @param videos Amount of videos played
     * @param bibleStudies Amount of bible studies
     * @param annotation Annotations by the user
     */
    public Report(long id, LocalDate date, long minutes, int placements, int returnVisits, int videos, int bibleStudies, String annotation) {
        this(id, date,minutes, placements, returnVisits, videos, bibleStudies, annotation, Type.NORMAL);
    }

    /**
     * Create a report object.
     * @param id Report ID
     * @param date Date of Report
     * @param minutes Minutes in ministry
     * @param placements Amount of placements
     * @param returnVisits Amount of return visits
     * @param videos Amount of videos played
     * @param bibleStudies Amount of bible studies
     * @param annotation Annotations by the user
     * @param type Type of report
     */
    public Report(long id, LocalDate date, long minutes, int placements, int returnVisits, int videos, int bibleStudies, String annotation, Type type) {
        this.id = id;
        this.date = date;
        this.minutes = minutes;
        this.placements = placements;
        this.returnVisits = returnVisits;
        this.videos = videos;
        this.bibleStudies = bibleStudies;
        this.annotation = annotation;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getFormattedDate(Context context){
        if(getType() == Type.NORMAL) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
            return getDate().format(dateTimeFormatter);
        }else{
            return context.getString(R.string.carryover);
        }
    }

    public long getMinutes() {
        return minutes;
    }

    public void setMinutes(long minutes) {
        this.minutes = minutes;
    }

    public String[] getFormattedHoursAndMinutes(Context context){
        String minutesString = "";
        String timeFormatString = context.getString(R.string.title_activity_stunden);

        if(minutes % 60 == 0){
            minutesString = ((minutes / 60)+ "");
        }else{
            if(minutes < 60){
                minutesString = minutes + "";
                timeFormatString = context.getString(R.string.minutes);
            }else{
                minutesString = LocalTime.MIN.plus(
                        Duration.ofMinutes( getMinutes() )
                ).toString();
            }
        }

        return new String[]{ minutesString, timeFormatString};
    }

    public int getPlacements() {
        return placements;
    }

    public void setPlacements(int placements) {
        this.placements = placements;
    }

    public int getReturnVisits() {
        return returnVisits;
    }

    public void setReturnVisits(int returnVisits) {
        this.returnVisits = returnVisits;
    }

    public int getVideos() {
        return videos;
    }

    public void setVideos(int videos) {
        this.videos = videos;
    }

    public int getBibleStudies() {
        return bibleStudies;
    }

    public void setBibleStudies(int bibleStudies) {
        this.bibleStudies = bibleStudies;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    enum Type {
        NORMAL, CARRY_SUB, CARRY_ADD;
    }
}
