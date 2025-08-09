package tk.phili.dienst.dienst.videos;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.request.DownloadRequest;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.loadingindicator.LoadingIndicator;

import java.io.File;
import java.util.ArrayList;

import tk.phili.dienst.dienst.R;
import tk.phili.dienst.dienst.report.ReportRecyclerAdapter;
import tk.phili.dienst.dienst.uiwrapper.FragmentCommunicationPass;
import tk.phili.dienst.dienst.uiwrapper.WrapperActivity;
import tk.phili.dienst.dienst.utils.JWCallback;
import tk.phili.dienst.dienst.utils.JWLanguageService;

@SuppressLint({"NotifyDataSetChanged", "InflateParams"})
public class VideoFragment extends Fragment implements Toolbar.OnMenuItemClickListener {

    private Toolbar toolbar;

    private VideoAdapter downloadedAdapter;
    private VideoService videoService;

    private JWLanguageService languageService;

    private FragmentCommunicationPass fragmentCommunicationPass;

    private View videosEmptyView;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentCommunicationPass = (FragmentCommunicationPass) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        toolbar = view.findViewById(R.id.toolbar);
        FloatingActionButton fab = view.findViewById(R.id.video_add_button);
        videoService = new VideoService(requireContext());
        languageService = new JWLanguageService(requireContext());
        videosEmptyView = view.findViewById(R.id.videosEmptyView);

        fragmentCommunicationPass.onDataPass(this, WrapperActivity.FRAGMENTPASS_TOOLBAR, toolbar);

        toolbar.setTitle(getResources().getString(R.string.title_videos));

        fab.setOnClickListener(view1 -> showCategory(VideoService.CATEGORY_VOD));


        RecyclerView downloadedRecycler = view.findViewById(R.id.recyclerView);
        downloadedAdapter = new VideoAdapter(requireContext(), new ArrayList<>(videoService.getDownloadedVideos()), new VideoAdapter.SelectionCallback() {
            @Override
            public void onVideoSelected(JWVideo video, VideoAdapter adapter) {
                Uri contentUri = FileProvider.getUriForFile(
                        requireContext(),
                        "tk.phili.dienst.dienst.fileprovider",
                        video.getFile(requireContext())
                );
                playVideo(contentUri);
            }

            @Override
            public void onCategorySelected(JWVideoCategory category, VideoAdapter adapter) {
                 // Shouldnt happen
            }
        });
        downloadedRecycler.setAdapter(downloadedAdapter);
        downloadedRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        videosEmptyView.setVisibility(downloadedAdapter.items.isEmpty() ? View.VISIBLE : View.GONE);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getAdapterPosition();
                if (deleteVideo((JWVideo) downloadedAdapter.items.get(pos))) {
                    downloadedAdapter.items.remove(pos);
                    downloadedAdapter.notifyDataSetChanged();
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(downloadedRecycler);
    }

    @SuppressLint("InflateParams")
    private void showCategory(String category) {
        final View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_video_selection, null);
        RecyclerView videoSelectionRecycler = dialogView.findViewById(R.id.videoSelectionRecycler);
        LinearLayout errorLayout = dialogView.findViewById(R.id.errorContainer);
        LoadingIndicator loadingIndicator = dialogView.findViewById(R.id.loadingIndicator);
        Button retryButton = dialogView.findViewById(R.id.retryButton);
        videoSelectionRecycler.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.VISIBLE);


        VideoAdapter videoAdapter = new VideoAdapter(requireContext(), new ArrayList<>(), new VideoAdapter.SelectionCallback() {
            @Override
            public void onVideoSelected(JWVideo video, VideoAdapter adapter) {
                new MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialogCenterStyle)
                        .setTitle(requireContext().getString(R.string.download_quality))
                        .setIcon(R.drawable.ic_baseline_cloud_download_24)
                        .setItems(video.getFiles().stream()
                                .map(file -> file.getLabel() + " (" + (file.getFilesize() / 1024 / 1024) + " MB)")
                                .toArray(String[]::new), (dialog, which) -> {
                            if (which >= 0 && which < video.getFiles().size()) {
                                downloadVideo(video, video.getFiles().get(which), adapter);
                            } else {
                                Toast.makeText(getContext(), R.string.video_exception, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .create()
                        .show();
            }

            @Override
            public void onCategorySelected(JWVideoCategory category, VideoAdapter adapter) {
                showCategory(category.getKey());
            }
        });
        videoSelectionRecycler.setAdapter(videoAdapter);
        videoSelectionRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        SharedPreferences sp = getContext().getSharedPreferences("MainActivity", Context.MODE_PRIVATE);
        JWCallback<JWVideoCategory> callback = new JWCallback<JWVideoCategory>() {
            @Override
            public void onSuccess(JWVideoCategory result) {
                videoAdapter.items.addAll(result.getSubcategories());
                videoAdapter.items.addAll(result.getMedia());
                requireActivity().runOnUiThread(() -> {
                    videoAdapter.notifyDataSetChanged();
                    videoSelectionRecycler.setVisibility(View.VISIBLE);
                    errorLayout.setVisibility(View.GONE);
                    loadingIndicator.setVisibility(View.GONE);
                });
            }

            @Override
            public void onError(Exception e) {
                requireActivity().runOnUiThread(() -> {
                    videoSelectionRecycler.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    loadingIndicator.setVisibility(View.GONE);
                });
            }
        };
        videoService.getVideoCategory(
                callback,
                sp.getString("videos_locale", languageService.getCurrentLanguage("E").getLangcode()),
                category
        );

        retryButton.setOnClickListener(view -> {
            videoSelectionRecycler.setVisibility(View.GONE);
            errorLayout.setVisibility(View.GONE);
            loadingIndicator.setVisibility(View.VISIBLE);
            videoService.getVideoCategory(
                    callback,
                    sp.getString("videos_locale", languageService.getCurrentLanguage("E").getLangcode()),
                    category
            );
        });


        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        dialog.setContentView(dialogView);
        dialog.show();
        dialog.getBehavior().setState(STATE_EXPANDED);
        dialog.getBehavior().setSkipCollapsed(true);
    }

    public void downloadVideo(JWVideo video, JWVideoFile videoFile, @Nullable RecyclerView.Adapter adapter) {
        DownloadRequest request = PRDownloader
                .download(
                        videoFile.getProgressiveDownloadURL(),
                        requireContext().getFilesDir().getPath(),
                        "videos/" + video.getNaturalKey() + ".mp4"
                )
                .setTag(video.getNaturalKey())
                .build();

        request.start(new OnDownloadListener() {
            @Override
            public void onDownloadComplete() {
                videoService.addDownloadedVideo(video);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                    downloadedAdapter.items.clear();
                    downloadedAdapter.items.addAll(videoService.getDownloadedVideos());
                    downloadedAdapter.notifyDataSetChanged();
                    videosEmptyView.setVisibility(downloadedAdapter.items.isEmpty() ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            public void onError(com.downloader.Error error) {
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                Toast.makeText(getContext(), R.string.video_exception, Toast.LENGTH_SHORT).show();
            }
        });

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public boolean deleteVideo(JWVideo video) {
        if (video.isDownloaded(requireContext())) {
            boolean deleted = new File(requireContext().getFilesDir(), "videos/" + video.getNaturalKey() + ".mp4").delete();
            if (deleted) {
                videoService.removeDownloadedVideo(video);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentCommunicationPass.onDataPass(this, WrapperActivity.FRAGMENTPASS_TOOLBAR, toolbar);
    }

    public void playVideo(Uri contentUri) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, contentUri);
            intent.setDataAndType(contentUri, "video/mp4");
            intent.setClipData(ClipData.newRawUri("", contentUri));
            intent.addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            requireContext().startActivity(intent);
        } catch (Exception e) {
            new MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialogCenterStyle)
                    .setTitle(R.string.error)
                    .setIcon(R.drawable.ic_baseline_error_outline_24)
                    .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss())
                    .setCancelable(false)
                    .setMessage(R.string.video_open_error)
                    .show();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh_vid) {
            //refreshListData();
            return true;
        }
        return false;
    }


}
