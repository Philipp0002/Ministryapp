package tk.phili.dienst.dienst;

import android.*;
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
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class VideoNew extends AppCompatActivity {

    public SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private Toolbar toolbar;

    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
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
        setContentView(R.layout.activity_video_new);

        setTitle(getResources().getString(R.string.title_section7));

        sp = getSharedPreferences("MainActivity", MODE_PRIVATE);
        editor = sp.edit();

        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_55);
        setSupportActionBar(toolbar);
        toolbar.bringToFront();
        Drawer.addDrawer(this, toolbar, 5);

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
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat
                    .requestPermissions(VideoNew.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1001);
            return;
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
                    Log.d("Dienstapp-s", s);
                    id.add(Integer.parseInt(s.split(";")[1]));
                    title.add(s.split(";")[2]);
                    length.add(s.split(";")[3].replace("-", ":"));
                    mb.add(s.split(";")[4] + "MB");
                    url.add(s.split(";")[5]);
                    File file = new File(Environment.getExternalStorageDirectory() + "/" + "MINISTRY" + "/" + s.split(";")[2].replace("?", "")+".mp4");
                    if (file.exists()) {
                        isDownloaded.add(true);
                    }else{
                        isDownloaded.add(false);
                    }
                }
            }

            VideoAdapter adapter = new VideoAdapter(this, id, title, length, mb, url, isDownloaded);
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

            if(idivider != null){
                recyclerView.removeItemDecoration(idivider);
            }
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.addItemDecoration(idivider = new DividerItemDecoration(VideoNew.this,
                    DividerItemDecoration.VERTICAL));
            idivider.setDrawable(ContextCompat.getDrawable(VideoNew.this, R.drawable.divider));

        }
    }

    public void refreshListData(){
        final ProgressDialog dialog = ProgressDialog.show(VideoNew.this, "",
                getString(R.string.vid_wait), true);
        dialog.setCancelable(false);

        WebStringGetter wsg = new WebStringGetter();
        wsg.fc = new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {
                if(!result.equalsIgnoreCase("ERROR")) {
                    editor.putString("Videos", result);
                    editor.commit();
                    dialog.cancel();
                    refreshList();
                }else{
                    dialog.cancel();
                    new AlertDialog.Builder(VideoNew.this)
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
        wsg.execute("https://dienstapp.raffaelhahn.de/Videos.php");
    }

    static class WebStringGetter extends AsyncTask<String, Void, String> {

        private Exception exception;
        private FutureCallback<String> fc;

        protected String doInBackground(String... urls) {
            return HttpUtils.getUrlAsString(urls[0]);//getContents(urls[0]);
        }

        protected void onPostExecute(String feed) {
            fc.onCompleted(new Exception(),feed);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1001:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { // Permission Granted
                    refreshList();
                } else { // Permission Denied
                    Toast.makeText(VideoNew.this, getString(R.string.not_accepted_videonew), Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }



}
