package tk.phili.dienst.dienst.videos;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tk.phili.dienst.dienst.R;
import tk.phili.dienst.dienst.uiwrapper.FragmentCommunicationPass;
import tk.phili.dienst.dienst.uiwrapper.WrapperActivity;
import tk.phili.dienst.dienst.utils.HttpUtils;
import tk.phili.dienst.dienst.utils.MenuTintUtils;

public class VideoFragment extends Fragment implements Toolbar.OnMenuItemClickListener{

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
            final Snackbar snackbar = Snackbar.make(getView(), getString(R.string.title_videos_success), Snackbar.LENGTH_LONG);
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

    FragmentCommunicationPass fragmentCommunicationPass;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentCommunicationPass = (FragmentCommunicationPass) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_video, null);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        fragmentCommunicationPass.onDataPass(this, WrapperActivity.FRAGMENTPASS_TOOLBAR, toolbar);

        toolbar.inflateMenu(R.menu.videos);
        MenuTintUtils.tintAllIcons(toolbar.getMenu(), Color.WHITE);
        toolbar.setOnMenuItemClickListener(this);

        toolbar.setTitle(getResources().getString(R.string.title_section7));

        sp = getActivity().getSharedPreferences("MainActivity", Context.MODE_PRIVATE);
        editor = sp.edit();


        if(!sp.contains("Videos")){
            refreshListData();
        }else{
            refreshList();
        }
        getActivity().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        fragmentCommunicationPass.onDataPass(this, WrapperActivity.FRAGMENTPASS_TOOLBAR, toolbar);
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            getActivity().unregisterReceiver(onComplete);
        }catch (Exception e){}
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            getActivity().unregisterReceiver(onComplete);
        }catch (Exception e){}
    }

    public void refreshList(){
        File a = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES).getAbsolutePath());
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
                    File file = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES).getAbsolutePath(), "MINISTRY" + "/" + s.split(";")[2].replace("?", "")+".mp4");
                    if (file.exists()) {
                        isDownloaded.add(true);
                    }else{
                        isDownloaded.add(false);
                    }
                }
            }

            if(adapter == null){
               adapter = new VideoAdapter(getActivity(), this, id, title, length, mb, url, isDownloaded);

                RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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

        }
    }

    public void refreshListData(){
        final ProgressDialog dialog = ProgressDialog.show(getContext(), "",
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
                    new AlertDialog.Builder(getContext())
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

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if(item.getItemId() == R.id.action_refresh_vid) {
            refreshListData();
            return true;
        }
        return false;
    }

    static class WebStringGetter extends AsyncTask<String, Void, String> {

        private Runnable fc;
        public String response;

        protected String doInBackground(String... urls) {
            return HttpUtils.getUrlAsString(urls[0]);
        }

        protected void onPostExecute(String feed) {
            response = feed;
            fc.run();
        }
    }

}
