package tk.phili.dienst.dienst.settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.downloader.internal.DownloadRequestQueue;
import com.downloader.request.DownloadRequest;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.shape.ShapeAppearanceModel;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import tk.phili.dienst.dienst.R;
import tk.phili.dienst.dienst.report.Report;
import tk.phili.dienst.dienst.utils.JWLang;
import tk.phili.dienst.dienst.utils.JWLanguageService;
import tk.phili.dienst.dienst.utils.Utils;
import tk.phili.dienst.dienst.videos.JWVideo;
import tk.phili.dienst.dienst.videos.JWVideoCategory;

/**
 * Created by fipsi on 04.03.2018.
 */

public class SettingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public List<Object> items;
    private final Context context;

    public SettingsAdapter(Context context, List<Object> items) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            // TODO STRING
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.settings_header_item, parent, false);
            return new HeaderItemViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.settings_item, parent, false);
            return new SettingsItemViewHolder(v);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof String) {
            return 0;
        } else if (items.get(position) instanceof BasicSetting) {
            return 1;
        }
        return -1;
    }


    /**
     * @param position The position of the item in the adapter.
     * @return 0 if in the middle, -1 if first item in group, 1 if last item in group.
     */
    public int getPositionInGroup(int position) {
        if (position == 0) {
            return -1; // First item in group
        }
        if (position - 1 >= 0 && items.get(position - 1) instanceof String) {
            return -1; // First item in group
        }
        if (position + 1 < items.size() && items.get(position + 1) instanceof String) {
            return 1; // Last item in group
        }
        if (position == items.size() - 1) {
            return 1; // Last item in group
        }
        return 0; // In the middle of a group
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder _holder, int position) {
        if (_holder instanceof HeaderItemViewHolder) {
            HeaderItemViewHolder holder = (HeaderItemViewHolder) _holder;

            holder.mainView.setText(items.get(position).toString());
        } else if (_holder instanceof SettingsItemViewHolder) {
            SettingsItemViewHolder holder = (SettingsItemViewHolder) _holder;
            BasicSetting setting = (BasicSetting) items.get(position);

            holder.titleView.setText(setting.getTitle());
            holder.descriptionView.setText(setting.getDescription());
            holder.iconView.setImageResource(setting.getIcon());

            if (setting.getDescription() == null) {
                holder.descriptionView.setVisibility(View.GONE);
            } else {
                holder.descriptionView.setVisibility(View.VISIBLE);
            }

            int positionInGroup = getPositionInGroup(position);
            ShapeAppearanceModel.Builder shapeBuilder = new ShapeAppearanceModel.Builder()
                    .setAllCornerSizes(Utils.dpToPx(16));
            int connectingCornersSize = Utils.dpToPx(4);

            if (positionInGroup <= 0) {
                shapeBuilder.setBottomRightCornerSize(connectingCornersSize);
                shapeBuilder.setBottomLeftCornerSize(connectingCornersSize);
            }
            if (positionInGroup >= 0) {
                shapeBuilder.setTopRightCornerSize(connectingCornersSize);
                shapeBuilder.setTopLeftCornerSize(connectingCornersSize);
            }
            holder.mainView.setShapeAppearanceModel(shapeBuilder.build());

            if (setting instanceof SwitchSetting) {
                holder.switchView.setVisibility(View.VISIBLE);
                SwitchSetting switchSetting = (SwitchSetting) setting;
                holder.switchView.setChecked(switchSetting.isChecked());
                holder.switchView.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    switchSetting.setChecked(isChecked);
                    if (switchSetting.getSelectionCallback() != null) {
                        switchSetting.getSelectionCallback().onSelected(switchSetting);
                    }
                });
            } else {
                holder.switchView.setVisibility(View.GONE);
            }

            holder.mainView.setOnClickListener(v -> {
                if (setting instanceof SwitchSetting) {
                    SwitchSetting switchSetting = (SwitchSetting) setting;
                    switchSetting.setChecked(!switchSetting.isChecked());
                    holder.switchView.setChecked(switchSetting.isChecked());
                }
                if (setting.getSelectionCallback() != null) {
                    setting.getSelectionCallback().onSelected(setting);
                }
            });


        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class SettingsItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView iconView;
        public TextView titleView, descriptionView;

        public MaterialSwitch switchView;
        public MaterialCardView mainView;


        public SettingsItemViewHolder(View itemView) {
            super(itemView);
            mainView = (MaterialCardView) itemView;
            titleView = itemView.findViewById(R.id.title);
            descriptionView = itemView.findViewById(R.id.description);
            iconView = itemView.findViewById(R.id.icon);
            switchView = itemView.findViewById(R.id.switchView);
        }
    }

    public static class HeaderItemViewHolder extends RecyclerView.ViewHolder {
        public TextView mainView;

        public HeaderItemViewHolder(View itemView) {
            super(itemView);
            mainView = (TextView) itemView;
        }
    }

    @Data
    @AllArgsConstructor
    public static class BasicSetting {
        @DrawableRes
        public Integer icon;

        public String title;
        public String description;
        public SelectionCallback<BasicSetting> selectionCallback;
    }

    public static class SwitchSetting extends BasicSetting {
        @Getter
        @Setter
        public boolean checked;

        public SwitchSetting(@DrawableRes Integer icon, String title, String description, boolean checked, SelectionCallback<BasicSetting> selectionCallback) {
            super(icon, title, description, selectionCallback);
            this.checked = checked;
        }
    }

    public interface SelectionCallback<T> {
        void onSelected(T item);
    }


}
