package tk.phili.dienst.dienst.videos;

import static android.os.Environment.DIRECTORY_MOVIES;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.request.DownloadRequest;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

import tk.phili.dienst.dienst.R;
import tk.phili.dienst.dienst.uiwrapper.FragmentCommunicationPass;
import tk.phili.dienst.dienst.uiwrapper.WrapperActivity;
import tk.phili.dienst.dienst.utils.BehaviorSubject;
import tk.phili.dienst.dienst.utils.HttpUtils;
import tk.phili.dienst.dienst.utils.MenuTintUtils;
import tk.phili.dienst.dienst.utils.Utils;

public class VideoFragment extends Fragment implements Toolbar.OnMenuItemClickListener {

    public SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private Toolbar toolbar;

    VideoAdapter adapter;
    ArrayList<Video> videos;
    HashMap<Integer, BehaviorSubject<Float>> videoDownloadProgress;


    FragmentCommunicationPass fragmentCommunicationPass;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentCommunicationPass = (FragmentCommunicationPass) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_video, null);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        toolbar = view.findViewById(R.id.toolbar);

        fragmentCommunicationPass.onDataPass(this, WrapperActivity.FRAGMENTPASS_TOOLBAR, toolbar);

        toolbar.inflateMenu(R.menu.videos);
        MenuTintUtils.tintAllIcons(toolbar.getMenu(), Color.WHITE);
        toolbar.setOnMenuItemClickListener(this);

        toolbar.setTitle(getResources().getString(R.string.title_videos));

        sp = getActivity().getSharedPreferences("MainActivity", Context.MODE_PRIVATE);
        editor = sp.edit();

        videos = new ArrayList<>();
        videoDownloadProgress = new HashMap<>();
        if (!sp.contains("Videos")) {
            refreshListData();
        } else {
            refreshList(null);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentCommunicationPass.onDataPass(this, WrapperActivity.FRAGMENTPASS_TOOLBAR, toolbar);
    }

    protected boolean isSafe() {
        return !(this.isRemoving() || this.getActivity() == null || this.isDetached() || !this.isAdded() || this.getView() == null);
    }

    public void refreshList(String searchTerm) {
        if(!isSafe())
            return;
        String fullListString = sp.getString("Videos", "");

        if (!fullListString.isEmpty()) {
            ArrayList<String> all = new ArrayList<String>();

            if (fullListString.contains("___")) {
                for (String s : fullListString.split("___")) {
                    all.add(s);
                }
            } else {
                all.add(fullListString);
            }


            videos.clear();
            for (String s : all) {
                videos.add(
                        new Video(
                                s.split(";")[0],
                                Integer.parseInt(s.split(";")[1]),
                                s.split(";")[2],
                                s.split(";")[3].replace("-", ":"),
                                Integer.parseInt(s.split(";")[4]),
                                s.split(";")[5]
                        )
                );
            }
            ArrayList<Video> videosFiltered = videos.stream()
                    .filter(video -> video.getLang().equalsIgnoreCase(getString(R.string.URL_end))
                            && (searchTerm == null || video.getName().toLowerCase().contains(searchTerm.toLowerCase())))
                    .collect(Collectors.toCollection(ArrayList::new));
            if (adapter == null) {
                adapter = new VideoAdapter(getActivity(), this, videosFiltered);

                RecyclerView recyclerView = getView().findViewById(R.id.recyclerView);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            } else {
                adapter.videos.clear();
                adapter.videos.addAll(videosFiltered);
                adapter.notifyDataSetChanged();
            }

        }
    }

    public void refreshListData() {
        LinearLayout layout = new LinearLayout(getContext());
        LinearProgressIndicator progressIndicator = new LinearProgressIndicator(getContext());
        progressIndicator.setIndeterminate(true);
        int marginHoriz = Utils.dpToPx(16);
        int marginVert = Utils.dpToPx(8);
        layout.addView(progressIndicator);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)progressIndicator.getLayoutParams();
        params.setMargins(marginHoriz, marginVert, marginHoriz, marginVert);

        AlertDialog dialog = new MaterialAlertDialogBuilder(new ContextThemeWrapper(getContext(), R.style.AppThemeDark), R.style.MaterialAlertDialogCenterStyle)
                .setTitle(getString(R.string.video_fetching_infos))
                .setView(layout)
                .setCancelable(false)
                .create();
        dialog.show();

        final WebStringGetter wsg = new WebStringGetter();
        wsg.fc = () -> {
            if (!wsg.response.equalsIgnoreCase("ERROR")) {
                editor.putString("Videos", wsg.response);
                editor.commit();
                dialog.cancel();
                refreshList(null);
            } else {
                dialog.cancel();
                new MaterialAlertDialogBuilder(new ContextThemeWrapper(getContext(), R.style.AppThemeDark), R.style.MaterialAlertDialogCenterStyle)
                        .setTitle(R.string.video_refresh_error_title)
                        .setIcon(R.drawable.ic_baseline_signal_cellular_connected_no_internet_4_bar_24)
                        .setPositiveButton(R.string.ok, (dialog1, which) -> dialog1.dismiss())
                        .setCancelable(false)
                        .setMessage(R.string.video_refresh_error_msg)
                        .show();
            }
        };
        wsg.execute("https://ministryapp.de/Videos.php");
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh_vid) {
            refreshListData();
            return true;
        }
        return false;
    }

    public int downloadVideo(Video video) {
        DownloadRequest request = PRDownloader
                .download(video.getDownloadURL(), getContext().getExternalFilesDir(DIRECTORY_MOVIES).getPath(), "MINISTRY/" + video.getName().replace("?", "") + ".mp4")
                .build();

        int downloadId = request
                .setOnProgressListener(progress -> {
                    BehaviorSubject<Float> videoDownloadProgressbar = videoDownloadProgress
                            .get(video.getId());

                    if (videoDownloadProgressbar != null && progress != null) {
                        videoDownloadProgressbar.next((float) progress.totalBytes / (float) progress.currentBytes);
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        BehaviorSubject<Float> videoDownloadProgressbar = videoDownloadProgress
                                .get(video.getId());
                        if (videoDownloadProgressbar != null) {
                            videoDownloadProgressbar.destroy();
                            videoDownloadProgress.remove(video.getId());
                        }
                        refreshList(null);
                    }

                    @Override
                    public void onError(Error error) {
                        BehaviorSubject<Float> videoDownloadProgressbar = videoDownloadProgress
                                .get(video.getId());
                        if (videoDownloadProgressbar != null) {
                            videoDownloadProgressbar.destroy();
                            videoDownloadProgress.remove(video.getId());
                        }
                        refreshList(null);
                        Toast.makeText(getContext(), R.string.video_exception, Toast.LENGTH_SHORT).show();
                    }
                });

        videoDownloadProgress.put(video.getId(), new BehaviorSubject<>(0F));
        refreshList(null);

        return downloadId;

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
