package tk.phili.dienst.dienst;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.mikepenz.materialdrawer.model.AbstractDrawerItem;

import java.util.List;

/**
 * Created by fipsi on 02.04.2018.
 */

public class DrawerTicker extends AbstractDrawerItem<DrawerTicker, DrawerTicker.ViewHolder> {

    String text = "";
    String URL = null;
    ViewHolder viewHolder = null;
    boolean visible = true;
    Activity c;

    public DrawerTicker(Activity c) {
        this.c = c;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    @LayoutRes
    public int getLayoutRes() {
        return R.layout.drawertickerlayout;
    }

    public DrawerTicker setText(final String text){
        this.text = text;
        try{
            if(viewHolder != null){
                c.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        viewHolder.textView.setText(text);
                    }
                });

            }
        }catch(RuntimeException e){ //CalledFromWrongThreadExeption
            e.printStackTrace();
        }
        return this;
    }

    public DrawerTicker setURL(String URL){
        this.URL = URL;
        try{
            if(viewHolder != null){
                final String url = URL;
                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        c.startActivity(i);
                    }
                });

            }
        }catch(RuntimeException e){
            e.printStackTrace();
        }
        return this;
    }

    public void setVisible(final boolean visible){
        this.visible = visible;
        try {
            if (viewHolder != null) {
                c.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (visible) {
                            viewHolder.view.setVisibility(View.VISIBLE);
                        } else {
                            viewHolder.view.setVisibility(View.GONE);
                        }
                    }
                });

            }
        }catch(RuntimeException e){
            e.printStackTrace();
        }
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
                viewHolder.textView.setText(text);
            }
        });

        c.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (visible) {
                    viewHolder.view.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.view.setVisibility(View.GONE);
                }
            }
        });

        if(URL != null) {
            final String url = URL;
                    viewHolder.view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            c.startActivity(i);
                        }
                    });
        }

        //call the onPostBindView method to trigger post bind view actions (like the listener to modify the item if required)
        onPostBindView(this, holder.itemView);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private ScrollTextView textView;

        private ViewHolder(View view) {
            super(view);
            this.view = view;
            this.textView = view.findViewById(R.id.tickerview);
        }
    }
}