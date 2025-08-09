package tk.phili.dienst.dienst.videos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.downloader.internal.DownloadRequestQueue;
import com.downloader.request.DownloadRequest;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import tk.phili.dienst.dienst.R;
import tk.phili.dienst.dienst.utils.JWLang;
import tk.phili.dienst.dienst.utils.JWLanguageService;

/**
 * Created by fipsi on 04.03.2018.
 */

public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public List<Object> items;
    private final Context context;
    private final SelectionCallback selectionCallback;
    private Field downloadRequestsField;

    public VideoAdapter(Context context, List<Object> items, SelectionCallback selectionCallback) {
        this.items = items;
        this.context = context;
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

    private boolean containsDifferentLanguages() {
        if (items.isEmpty()) {
            return false;
        }

        String firstLanguageCode = null;
        for (Object item : items) {
            if (item instanceof JWVideo) {
                JWVideo video = (JWVideo) item;
                if (firstLanguageCode == null) {
                    firstLanguageCode = video.getJWLanguage();
                } else if (!firstLanguageCode.equals(video.getJWLanguage())) {
                    return true;
                }
            }
        }
        return false;
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

            if(containsDifferentLanguages()) {
                String jwLanguageCode = video.getJWLanguage();
                JWLang jwLanguage = new JWLanguageService(context).getLanguageByLangcode(jwLanguageCode);
                holder.language.setText(jwLanguage.getLocalizedLanguageName());
                holder.language.setVisibility(View.VISIBLE);
            } else {
                holder.language.setVisibility(View.GONE);
            }

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
        public TextView title, time, language;

        public ImageView actionIndicator;
        public View mainView;

        public LinearProgressIndicator downloadProgressBar;

        public VideoViewHolder(View itemView) {
            super(itemView);
            mainView = itemView;
            title = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.previewImage);
            time = itemView.findViewById(R.id.time);
            language = itemView.findViewById(R.id.language);

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
