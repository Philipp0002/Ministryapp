package tk.phili.dienst.dienst.videos;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tk.phili.dienst.dienst.R;
import tk.phili.dienst.dienst.drawer.Drawer;
import tk.phili.dienst.dienst.utils.HttpUtils;
import tk.phili.dienst.dienst.utils.MenuTintUtils;

public class VideoActivity extends AppCompatActivity {

    public SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private Toolbar toolbar;

    VideoAdapter adapter;

    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            Long dwnId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            if(adapter!=null){
                adapter.pendingDownload.remove(dwnId);
            }
            final Snackbar snackbar = Snackbar.make(findViewById(R.id.drawer_55), getString(R.string.title_videos_success), Snackbar.LENGTH_LONG);
            snackbar.setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();
            refreshList();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        setTitle(getResources().getString(R.string.title_section7));

        sp = getSharedPreferences("MainActivity", MODE_PRIVATE);
        editor = sp.edit();

        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_55);
        setSupportActionBar(toolbar);
        toolbar.bringToFront();
        Drawer.addDrawer(this, toolbar, 6);

        if(!sp.contains("Videos")){
            refreshListData();
        }else{
            refreshList();
        }
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(onComplete);
        }catch (Exception e){}
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(onComplete);
        }catch (Exception e){}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.videos, menu);
        MenuTintUtils.tintAllIcons(menu, Color.WHITE);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_refresh_vid) {
            refreshListData();
        }
        return super.onOptionsItemSelected(item);
    }


    DividerItemDecoration idivider = null;
    public void refreshList(){

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat
                    .requestPermissions(VideoActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
            return;
        }

        File a = new File(this.getExternalFilesDir(Environment.DIRECTORY_MOVIES).getAbsolutePath());
        for(File f : a.listFiles()){
            for(File f1 : f.listFiles()){
            }
        }

        String fullListString = sp.getString("Videos", "");

        if(!fullListString.isEmpty()){
            ArrayList<String> all = new ArrayList<String>();

            if(fullListString.contains("___")){
                for(String s : fullListString.split("___")){
                    all.add(s);
                }
            }else{
                all.add(fullListString);
            }


            List<Integer> id = new ArrayList<Integer>();
            List<String> title = new ArrayList<String>();
            List<String> length = new ArrayList<String>();
            List<String> mb = new ArrayList<String>();
            List<String> url = new ArrayList<String>();
            List<Boolean> isDownloaded = new ArrayList<Boolean>();
            for(String s : all){
                if(s.split(";")[0].equalsIgnoreCase(getString(R.string.URL_end))) {
                    id.add(Integer.parseInt(s.split(";")[1]));
                    title.add(s.split(";")[2]);
                    length.add(s.split(";")[3].replace("-", ":"));
                    mb.add(s.split(";")[4] + "MB");
                    url.add(s.split(";")[5]);
                    File file = new File(this.getExternalFilesDir(Environment.DIRECTORY_MOVIES).getAbsolutePath(), "MINISTRY" + "/" + s.split(";")[2].replace("?", "")+".mp4");
                    if (file.exists()) {
                        isDownloaded.add(true);
                    }else{
                        isDownloaded.add(false);
                    }
                }
            }

            if(adapter == null){
               adapter = new VideoAdapter(this, id, title, length, mb, url, isDownloaded);

                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
            }else{
                adapter.id.clear();
                adapter.id.addAll(id);
                adapter.title.clear();
                adapter.title.addAll(title);
                adapter.length.clear();
                adapter.length.addAll(length);
                adapter.mb.clear();
                adapter.mb.addAll(mb);
                adapter.url.clear();
                adapter.url.addAll(url);
                adapter.isDownloaded.clear();
                adapter.isDownloaded.addAll(isDownloaded);
                adapter.notifyDataSetChanged();
            }



            /*if(idivider != null){
                recyclerView.removeItemDecoration(idivider);
            }*/

            /*recyclerView.addItemDecoration(idivider = new DividerItemDecoration(VideoNew.this,
                    DividerItemDecoration.VERTICAL));
            idivider.setDrawable(ContextCompat.getDrawable(VideoNew.this, R.drawable.divider));*/

        }
    }

    public void refreshListData(){
        final ProgressDialog dialog = ProgressDialog.show(VideoActivity.this, "",
                getString(R.string.vid_wait), true);
        dialog.setCancelable(false);

        final WebStringGetter wsg = new WebStringGetter();
        wsg.fc = new Runnable() {
            @Override
            public void run() {
                if(!wsg.response.equalsIgnoreCase("ERROR")) {
                    editor.putString("Videos", wsg.response);
                    editor.commit();
                    dialog.cancel();
                    refreshList();
                }else{
                    dialog.cancel();
                    new AlertDialog.Builder(VideoActivity.this)
                            .setTitle(R.string.video_refresh_error_title)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setCancelable(false)
                            .setMessage(R.string.video_refresh_error_msg)
                            .show();
                }
            }
        };
        wsg.execute("https://ministryapp.de/Videos.php");
    }

    static class WebStringGetter extends AsyncTask<String, Void, String> {

        //private FutureCallback<String> fc;
        private Runnable fc;
        public String response;

        protected String doInBackground(String... urls) {
            return HttpUtils.getUrlAsString(urls[0]);//getContents(urls[0]);
        }

        protected void onPostExecute(String feed) {
            response = feed;
            fc.run();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1001:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) { // Permission Granted
                    refreshList();
                } else { // Permission Denied
                    Toast.makeText(VideoActivity.this, getString(R.string.not_accepted_videonew), Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }



}
