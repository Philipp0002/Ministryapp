package tk.phili.dienst.dienst.newreport;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import tk.phili.dienst.dienst.utils.LocalDateAdapter;

public class ReportManager {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;

    private static String SP_REPORTS_KEY = "reports";

    public ReportManager(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences("MainActivity", Context.MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();
    }

    /**
     * Deletes a report from database
     * @param report Report to delete
     * @return true if item existed
     */
    public boolean deleteReport(Report report){
        List<Report> reports = getReports();
        boolean removed = reports.removeIf(report::equals);

        if(removed){
            saveReports(reports);
        }

        return removed;
    }

    /**
     * Adds a report to the database.
     * Report needs a pre-set ID.
     * @param report Report to add
     */
    public void createReport(Report report){
        List<Report> reports = getReports();
        reports.add(report);
        saveReports(reports);
    }

    /**
     * Provides the next id to set when
     * creating a report object
     * @return next id
     */
    public long getNextId(){
        List<Report> reports = getReports();
        long highest = 0;
        for(Report r : reports){
            if(highest < r.getId()){
                highest = r.getId();
            }
        }
        return highest+1;
    }

    /**
     * Get a report by its id
     * @param id id to be looked up
     * @return report or null if doesnt exist
     */
    public Report getReportById(long id){
        for(Report report : getReports()){
            if(report.getId() == id){
                return report;
            }
        }
        return null;
    }

    /**
     * Retrieve all report objects
     * @return all report objects
     */
    public List<Report> getReports(){
        String allReportsJson = sharedPreferences.getString(SP_REPORTS_KEY, null);

        Type listType = new TypeToken<List<Report>>() {}.getType();
        List<Report> reports = getGson().fromJson(allReportsJson, listType);
        Collections.sort(reports, getReportComparator());
        return reports;
    }

    /**
     * Retrieve all report objects for
     * a certain month and year
     * @return all report objects
     */
    public List<Report> getReports(int month, int year){
        ArrayList<Report> reports = new ArrayList<>();
        List<Report> allReports = getReports();

        for(Report report : allReports){
            if(report.getDate().getMonth() == Month.of(month) && report.getDate().getYear() == year){
                reports.add(report);
            }
        }

        Collections.sort(reports, getReportComparator());
        return reports;
    }

    /**
     * Retrieve a summary of all reports
     * for a given month and year
     * @return Report object that contains
     * all summarized data
     */
    public Report getSummary(int month, int year){
        List<Report> allReportsMonth = getReports(month, year);

        Report summarizedReport = new Report();
        summarizedReport.setType(Report.Type.SUMMARY);
        for(Report report : allReportsMonth){
            if(report.getDate().getMonth() == Month.of(month) && report.getDate().getYear() == year){

                summarizedReport.setMinutes(summarizedReport.getMinutes() + report.getMinutes());
                summarizedReport.setBibleStudies(summarizedReport.getBibleStudies() + report.getBibleStudies());
                summarizedReport.setVideos(summarizedReport.getVideos() + report.getVideos());
                summarizedReport.setReturnVisits(summarizedReport.getReturnVisits() + report.getReturnVisits());
                summarizedReport.setPlacements(summarizedReport.getPlacements() + report.getPlacements());

            }
        }
        return summarizedReport;
    }

    private Comparator<Report> getReportComparator(){
        return (o1, o2) -> {
            if(o1.getType() == Report.Type.CARRY_ADD){
                return -1;
            }
            if(o1.getType() == Report.Type.CARRY_SUB){
                return 1;
            }

            if(o2.getType() == Report.Type.CARRY_ADD){
                return 1;
            }
            if(o2.getType() == Report.Type.CARRY_SUB){
                return -1;
            }

            int yearComp = Integer.compare(o1.getDate().getYear(), o2.getDate().getYear());
            if(yearComp != 0){
                return yearComp;
            }

            int monthComp = Integer.compare(o1.getDate().getMonthValue(), o2.getDate().getMonthValue());
            if(monthComp != 0){
                return monthComp;
            }

            int dayComp = Integer.compare(o1.getDate().getDayOfMonth(), o2.getDate().getDayOfMonth());
            if(dayComp != 0){
                return dayComp;
            }

            return 0;
        };
    }

    private Gson getGson(){
        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
    }

    private void saveReports(List<Report> reports){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
        Type listType = new TypeToken<List<Report>>() {}.getType();
        String json = gson.toJson(reports, listType);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("reports", json);
        editor.apply();
    }

    public int getReportLayoutSetting(){
        return sharedPreferences.getInt("report_layout", 0);
    }

}
