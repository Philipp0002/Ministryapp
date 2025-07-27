package tk.phili.dienst.dienst.videos;

import static android.os.Environment.DIRECTORY_MOVIES;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import tk.phili.dienst.dienst.R;
import tk.phili.dienst.dienst.utils.BehaviorSubject;

/**
 * Created by fipsi on 04.03.2018.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    public List<Video> videos;
    public HashMap<Integer, Drawable> drawables = new HashMap<>();
    private final Activity context;
    private final VideoFragment videoFragment;

    public VideoAdapter(VideoFragment videoFragment, List<Video> videos) {
        this.videos = videos;
        this.context = videoFragment.requireActivity();
        this.videoFragment = videoFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_single, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Video video = videos.get(position);

        File file = new File(context.getExternalFilesDir(DIRECTORY_MOVIES).getAbsolutePath(), "MINISTRY" + "/" + video.getName().replace("?", "") + ".mp4");

        holder.title.setText(video.getName());
        holder.downloadProgressBar.setVisibility(View.GONE);

        StringBuilder timeBuilder = new StringBuilder(video.getLength());
        if (file.exists()) {
            holder.image.setVisibility(View.VISIBLE);
            holder.actionIndicator.setImageResource(R.drawable.ic_play_arrow_black_24dp);

            if (!drawables.containsKey(position)) {
                new Thread(() -> {
                    Bitmap bMap = ThumbnailUtils.createVideoThumbnail(file.getPath(), MediaStore.Video.Thumbnails.MINI_KIND);
                    final Drawable d = new BitmapDrawable(context.getResources(), bMap);
                    drawables.put(position, d);
                    context.runOnUiThread(() -> {
                        holder.image.setImageDrawable(d);
                    });
                }).start();
            } else {
                context.runOnUiThread(() -> {
                    holder.image.setImageDrawable(drawables.get(position));
                });
            }

        } else {
            holder.image.setVisibility(View.GONE);
            holder.actionIndicator.setImageResource(R.drawable.ic_cloud_download_black_24dp);
            timeBuilder.append(" (")
                    .append(video.getMbSize())
                    .append(" MB)");
            holder.image.setImageDrawable(null);

            if (videoFragment.videoDownloadProgress.containsKey(video.getId())) {
                videoFragment.videoDownloadProgress.get(video.getId())
                        .subscribe(new BehaviorSubject.Subscriber<Float>() {
                            @Override
                            public void onNext(Float value) {
                                holder.downloadProgressBar.setVisibility(View.VISIBLE);
                                holder.downloadProgressBar.setProgress((int) (value * 100));
                            }

                            @Override
                            public void onDestroy() {
                                holder.downloadProgressBar.setVisibility(View.GONE);
                            }
                        });

            }
        }
        holder.time.setText(timeBuilder.toString());

        holder.mainView.setOnClickListener(view -> {
            if (file.exists()) {
                Uri contentUri = FileProvider.getUriForFile(context, "tk.phili.dienst.dienst.fileprovider", file);
                if (file.exists()) {
                    openFile(context, contentUri);
                } else {
                    Toast.makeText(context, context.getString(R.string.videonew_lostfile), Toast.LENGTH_SHORT).show();
                }
            } else {
                new MaterialAlertDialogBuilder(context, R.style.MaterialAlertDialogCenterStyle)
                        .setTitle(context.getString(R.string.ask_sure))
                        .setMessage(context.getString(R.string.download_text).replace("%a", video.getName()))
                        .setIcon(R.drawable.ic_baseline_cloud_download_24)
                        .setPositiveButton(R.string.download_ok, (dialog, which) -> {
                            videoFragment.downloadVideo(video);
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .create()
                        .show();

            }
        });

        holder.mainView.setOnLongClickListener(view -> {
            showDeleteDialog(video.getName());
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView title, time;

        public ImageView actionIndicator;
        public View mainView;

        public LinearProgressIndicator downloadProgressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            mainView = itemView;
            title = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.previewImage);
            time = itemView.findViewById(R.id.time);

            actionIndicator = itemView.findViewById(R.id.actionIndicator);

            downloadProgressBar = itemView.findViewById(R.id.progress_download_indeterminate);
        }
    }

    public static void openFile(Context context, Uri contentUri) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, contentUri);
            intent.setDataAndType(contentUri, "video/mp4");
            intent.setClipData(ClipData.newRawUri("", contentUri));
            intent.addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            context.startActivity(intent);
        } catch (Exception e) {
            new MaterialAlertDialogBuilder(context, R.style.MaterialAlertDialogCenterStyle)
                    .setTitle(R.string.error)
                    .setIcon(R.drawable.ic_baseline_error_outline_24)
                    .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss())
                    .setCancelable(false)
                    .setMessage(R.string.video_open_error)
                    .show();
        }
    }

    public void showDeleteDialog(final String vidname) {
        final File file = new File(context
                .getExternalFilesDir(DIRECTORY_MOVIES)
                .getAbsolutePath()
                + "/" + "MINISTRY" + "/" + vidname.replace("?", "") + ".mp4");

        if (file.exists()) {
            new MaterialAlertDialogBuilder(context, R.style.MaterialAlertDialogCenterStyle)
                    .setTitle(context.getString(R.string.ask_sure))
                    .setMessage(context.getString(R.string.delete_text).replace("%a", vidname))
                    .setIcon(R.drawable.ic_baseline_delete_24)
                    .setPositiveButton(R.string.delete_ok,
                            (dialog, which) -> {
                                file.delete();
                                videoFragment.refreshList(null);
                            })
                    .setNegativeButton(R.string.cancel, null)
                    .create()
                    .show();
        }

    }


}
