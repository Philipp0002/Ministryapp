package de.philipphahn.dienstapp.listview;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import de.philipphahn.dienstapp.R;

public class ListViewRowView extends FrameLayout implements WearableListView.OnCenterProximityListener {

    private ImageView image;
    private TextView text;

    public ListViewRowView(Context context) {
        super(context);
        View.inflate(context, R.layout.wearablelistview_image_text_item, this);
        image = (ImageView) findViewById(R.id.image);
        text = (TextView) findViewById(R.id.text);
    }

    @Override
    public void onCenterPosition(boolean b) {
        image.animate().scaleX(1.2f).scaleY(1.2f).alpha(1).setDuration(200);;
        text.animate().scaleX(1.2f).scaleY(1.2f).alpha(1).setDuration(200);;
    }

    @Override
    public void onNonCenterPosition(boolean b) {
        image.animate().scaleX(1f).scaleY(1f).alpha(0.6f).setDuration(200);
        text.animate().scaleX(1f).scaleY(1f).alpha(0.6f).setDuration(200);
    }

    public ImageView getImage() {
        return image;
    }

    public TextView getText() {
        return text;
    }
}
