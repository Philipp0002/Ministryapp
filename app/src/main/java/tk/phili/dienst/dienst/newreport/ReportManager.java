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
     * Retrieve all report objects in unsorted order
     * @return all report objects
     */
    public List<Report> getReports(){
        String allReportsJson = sharedPreferences.getString(SP_REPORTS_KEY, null);

        Type listType = new TypeToken<List<Report>>() {}.getType();
        List<Report> reports = getGson().fromJson(allReportsJson, listType);
        Collections.sort(reports, getReportComparator());
        return reports;
    }

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

    public Report getSummary(int month, int year){
        List<Report> allReportsMonth = getReports(month, year);

        Report summarizedReport = new Report();
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
            int yearComp = Integer.compare(o1.getDate().getYear(), o2.getDate().getYear());
            if(yearComp != 0){
                return yearComp;
            }

            int monthComp = Integer.compare(o1.getDate().getMonthValue(), o2.getDate().getMonthValue());
            if(monthComp != 0){
                return monthComp;
            }

            if(o1.getType() == Report.Type.CARRY_ADD){
                return -1;
            }
            if(o1.getType() == Report.Type.CARRY_SUB){
                return 1;
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

    public int getReportLayoutSetting(){
        return sharedPreferences.getInt("report_layout", 0);
    }

}
