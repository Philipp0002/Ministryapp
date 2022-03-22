package tk.phili.dienst.dienst.drawer;


import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.materialdrawer.model.AbstractDrawerItem;

import java.util.List;

import tk.phili.dienst.dienst.R;

/**
 * Created by fipsi on 02.04.2018.
 */

public class DrawerHeader extends AbstractDrawerItem<DrawerHeader, DrawerHeader.ViewHolder> {

    String text = "";
    ViewHolder viewHolder = null;
    Activity c;

    public DrawerHeader(Activity c) {
        this.c = c;
    }

    @Override
    public int getType() {
        return 1;
    }

    @Override
    @LayoutRes
    public int getLayoutRes() {
        return R.layout.drawerheaderlayout;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);
        Context ctx = holder.itemView.getContext();

        //get our viewHolder
        viewHolder = (ViewHolder) holder;

        c.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewHolder.view.setVisibility(View.VISIBLE);
                viewHolder.appname.setTypeface(Typeface.createFromAsset(c.getAssets(), "HammersmithOne-Regular.ttf"));
                ViewGroup.LayoutParams params = viewHolder.spacing.getLayoutParams();
                params.height = getStatusBarHeight(c);
                viewHolder.spacing.requestLayout();
            }
        });



        //call the onPostBindView method to trigger post bind view actions (like the listener to modify the item if required)
        onPostBindView(this, holder.itemView);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView appname;
        private ImageView spacing;

        private ViewHolder(View view) {
            super(view);
            this.view = view;
            this.appname = view.findViewById(R.id.app_text_header);
            this.spacing = view.findViewById(R.id.statusbar_below);
        }
    }

    public static int getStatusBarHeight(Activity c) {
        Rect rectangle = new Rect();
        Window window = c.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight = rectangle.top;

        if(Build.VERSION.SDK_INT <= 18){
            return 0;
        }
        return statusBarHeight;
    }
}