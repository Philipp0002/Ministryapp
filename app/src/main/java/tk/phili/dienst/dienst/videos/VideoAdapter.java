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
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.downloader.PRDownloader;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

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
    private Activity context;
    private VideoFragment videoFragment;

    public VideoAdapter(Activity context, VideoFragment videoFragment, List<Video> videos) {
        this.videos = videos;
        this.context = context;
        this.videoFragment = videoFragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_single, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        Video video = videos.get(position);

        holder.title.setText(video.getName());
        holder.time.setText(video.getLength());
        holder.mb.setText(video.getMbSize() + " MB");

        holder.imageGradient.setVisibility(View.INVISIBLE);
        File file = new File(context.getExternalFilesDir(DIRECTORY_MOVIES).getAbsolutePath(), "MINISTRY" + "/" + video.getName().replace("?", "") + ".mp4");
        if (file.exists()) {
            holder.downloadedImg.setVisibility(View.VISIBLE);
            holder.notDownloadedImg.setVisibility(View.INVISIBLE);
            holder.mb.setVisibility(View.INVISIBLE);
            if (!drawables.containsKey(position)) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        Bitmap bMap = ThumbnailUtils.createVideoThumbnail(file.getPath(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                        final Drawable d = new BitmapDrawable(context.getResources(), bMap);
                        drawables.put(position, d);
                        context.runOnUiThread(() -> {
                            holder.imageGradient.setVisibility(View.VISIBLE);
                            holder.image.setImageDrawable(d);
                        });
                        return null;
                    }
                }.execute();
            } else {
                context.runOnUiThread(() -> {
                    holder.imageGradient.setVisibility(View.VISIBLE);
                    holder.image.setImageDrawable(drawables.get(position));
                });
            }
        } else {
            holder.downloadedImg.setVisibility(View.INVISIBLE);
            holder.notDownloadedImg.setVisibility(View.VISIBLE);
            holder.mb.setVisibility(View.VISIBLE);
            holder.image.setImageDrawable(null);

            if (videoFragment.videoDownloadProgress.containsKey(video.getId())) {
                videoFragment.videoDownloadProgress.get(video.getId())
                        .subscribe(new BehaviorSubject.Subscriber<Float>() {
                            @Override
                            public void onNext(Float value) {
                                holder.downloadProgressBar.setVisibility(View.VISIBLE);
                                holder.downloadProgressBar.setProgress(value * 100);
                            }

                            @Override
                            public void onDestroy() {
                                holder.downloadProgressBar.setVisibility(View.INVISIBLE);
                            }
                        });

            }
        }


        holder.mainView.setOnClickListener(view -> {
            if (file.exists()) {
                Uri contentUri = FileProvider.getUriForFile(context, "tk.phili.dienst.dienst.fileprovider", file);
                if (file.exists()) {
                    openFile(context, contentUri);
                } else {
                    Toast.makeText(context, context.getString(R.string.videonew_lostfile), Toast.LENGTH_SHORT).show();
                }
            } else {
                new MaterialAlertDialogBuilder(new ContextThemeWrapper(context, R.style.AppThemeDark), R.style.MaterialAlertDialogCenterStyle)
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
        public ImageView image, imageGradient;
        public TextView title, time, mb;

        public ImageView notDownloadedImg, downloadedImg;
        public View mainView;

        public RoundCornerProgressBar downloadProgressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            mainView = itemView;
            title = itemView.findViewById(R.id.txt);
            image = itemView.findViewById(R.id.listpreviewimage);
            imageGradient = itemView.findViewById(R.id.listpreviewimagegradient);
            time = itemView.findViewById(R.id.time);
            mb = itemView.findViewById(R.id.mb);

            notDownloadedImg = itemView.findViewById(R.id.imageView6);
            downloadedImg = itemView.findViewById(R.id.imageView8);

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
            new MaterialAlertDialogBuilder(new ContextThemeWrapper(context, R.style.AppThemeDark), R.style.MaterialAlertDialogCenterStyle)
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
            new MaterialAlertDialogBuilder(new ContextThemeWrapper(context, R.style.AppThemeDark), R.style.MaterialAlertDialogCenterStyle)
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
