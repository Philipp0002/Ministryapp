package tk.phili.dienst.dienst.videos;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.downloader.PRDownloader;
import com.downloader.internal.DownloadRequestQueue;
import com.downloader.request.DownloadRequest;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tk.phili.dienst.dienst.R;

/**
 * Created by fipsi on 04.03.2018.
 */

public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public List<Object> items;
    public HashMap<Integer, Drawable> drawables = new HashMap<>();
    private final Activity context;
    private final VideoFragment videoFragment;
    private final SelectionCallback selectionCallback;
    private Field downloadRequestsField;

    public VideoAdapter(VideoFragment videoFragment, List<Object> items, SelectionCallback selectionCallback) {
        this.items = items;
        this.context = videoFragment.requireActivity();
        this.videoFragment = videoFragment;
        this.selectionCallback = selectionCallback;

        try {
            downloadRequestsField = DownloadRequestQueue.class.getDeclaredField("currentRequestMap");
            downloadRequestsField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_category_item, parent, false);
            return new CategoryViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);
            return new VideoViewHolder(v);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof JWVideoCategory ? 0 : 1;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder _holder, int position) {
        if (_holder instanceof CategoryViewHolder) {
            CategoryViewHolder holder = (CategoryViewHolder) _holder;
            JWVideoCategory category = (JWVideoCategory) items.get(position);

            holder.title.setText(category.getName());
            if (category.getImages() != null
                    && category.getImages().getPnr() != null
                    && category.getImages().getPnr().getLg() != null) {
                Glide.with(context)
                        .load(category.getImages().getPnr().getLg())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.image);
            }

            holder.itemView.setOnClickListener(view -> {
                if (selectionCallback != null) {
                    selectionCallback.onCategorySelected(category, this);
                }
            });

        } else if (_holder instanceof VideoViewHolder) {
            VideoViewHolder holder = (VideoViewHolder) _holder;
            JWVideo video = (JWVideo) items.get(position);

            holder.title.setText(video.getTitle());
            holder.time.setText(video.getDurationFormattedHHMM());

            holder.downloadProgressBar.setVisibility(View.GONE);
            try {
                Map<Integer, DownloadRequest> downloads = (Map<Integer, DownloadRequest>) downloadRequestsField.get(DownloadRequestQueue.getInstance());
                downloads.values().stream().filter(d -> d.getTag() == video.getNaturalKey()).findFirst().ifPresent(downloadRequest -> {
                    holder.downloadProgressBar.setVisibility(View.VISIBLE);
                    holder.downloadProgressBar.setIndeterminate(true);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            holder.itemView.setOnClickListener(view -> {
                if (selectionCallback != null) {
                    selectionCallback.onVideoSelected(video, this);
                }
            });

            if (video.getImages() != null
                    && video.getImages().getPnr() != null
                    && video.getImages().getPnr().getLg() != null) {
                Glide.with(context)
                        .load(video.getImages().getPnr().getLg())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.image);
            }

            if (video.isDownloaded(context)) {
                holder.actionIndicator.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            } else {
                holder.actionIndicator.setImageResource(R.drawable.ic_cloud_download_black_24dp);
            }

        }
        /*Video video = items.get(position);

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
        });*/
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder _holder) {
        if (_holder instanceof CategoryViewHolder) {
            CategoryViewHolder holder = (CategoryViewHolder) _holder;
            Glide.with(context).clear(holder.image);
            holder.image.setImageDrawable(null);
        } else if (_holder instanceof VideoViewHolder) {
            VideoViewHolder holder = (VideoViewHolder) _holder;
            Glide.with(context).clear(holder.image);
            holder.image.setImageDrawable(null);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView title, time;

        public ImageView actionIndicator;
        public View mainView;

        public LinearProgressIndicator downloadProgressBar;

        public VideoViewHolder(View itemView) {
            super(itemView);
            mainView = itemView;
            title = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.previewImage);
            time = itemView.findViewById(R.id.time);

            actionIndicator = itemView.findViewById(R.id.actionIndicator);

            downloadProgressBar = itemView.findViewById(R.id.progress_download_indeterminate);
        }
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView title;
        public View mainView;


        public CategoryViewHolder(View itemView) {
            super(itemView);
            mainView = itemView;
            title = itemView.findViewById(R.id.category_title);
            image = itemView.findViewById(R.id.category_background);
        }
    }

    public interface SelectionCallback {
        void onVideoSelected(JWVideo video, VideoAdapter adapter);

        void onCategorySelected(JWVideoCategory category, VideoAdapter adapter);
    }


}
