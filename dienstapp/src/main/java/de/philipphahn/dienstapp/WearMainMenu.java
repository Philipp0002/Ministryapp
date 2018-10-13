package de.philipphahn.dienstapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.philipphahn.dienstapp.listview.ListViewAdapter;
import de.philipphahn.dienstapp.listview.ListViewItem;

public class WearMainMenu extends Activity implements WearableListView.ClickListener {

    private List<ListViewItem> viewItemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear_main_menu);

        WearableListView wearableListView = (WearableListView) findViewById(R.id.main_menu_list);

        viewItemList.add(new ListViewItem(R.drawable.ic_launcher, "Empfehlungen"));

        wearableListView.setAdapter(new ListViewAdapter(this, viewItemList));
        wearableListView.setClickListener(this);
    }

    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        //Toast.makeText(this, "Click on " + viewItemList.get(viewHolder.getLayoutPosition()).text, Toast.LENGTH_SHORT).show();
        if(viewItemList.get(viewHolder.getLayoutPosition()).text.equals("Empfehlungen")){
            startActivity(new Intent(getApplicationContext(), Counter.class));
        }
    }

    @Override
    public void onTopEmptyRegionClick() {

    }
}
