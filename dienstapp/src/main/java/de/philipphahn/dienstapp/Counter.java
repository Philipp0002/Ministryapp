package de.philipphahn.dienstapp;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.percent.PercentFrameLayout;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Counter extends WearableActivity {

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.getDefault());

    private TextView mClock;
    private TextView mClockTitle;
    private TextView mCounter;
    private BoxInsetLayout mLayout;

    private PercentFrameLayout mClockLayout;
    private PercentFrameLayout mFabs;

    private FloatingActionButton mFAB1;
    private FloatingActionButton mFAB2;

    private boolean running = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);
        setAmbientEnabled();

        mClock = (TextView) findViewById(R.id.clock_counter);
        mClockTitle = (TextView) findViewById(R.id.clock_counter_title);
        mCounter = (TextView) findViewById(R.id.time_counter);
        mClockLayout = (PercentFrameLayout) findViewById(R.id.clock_counter_layout);
        mLayout = (BoxInsetLayout) findViewById(R.id.container_counter);

        mFabs = (PercentFrameLayout) findViewById(R.id.action_counter_layout);

        mFAB1 = (FloatingActionButton) findViewById(R.id.FAB1_counter);
        mFAB2 = (FloatingActionButton) findViewById(R.id.FAB2_counter);

        mFAB1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(running){
                    mFAB1.setImageResource(R.drawable.play);
                    mFAB2.setImageResource(R.drawable.delete);
                    mFAB2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F44336")));
                }else{
                    mFAB1.setImageResource(R.drawable.pause);
                    mFAB2.setImageResource(R.drawable.done);
                    mFAB2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00BCD4")));
                }
                running = !running;
            }
        });

        mFAB2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(running){
                    //TODO SET DONE
                }else{
                    //TODO SET TIMER BACK
                    running = false;
                }
            }
        });
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        if (isAmbient()) {
            mCounter.setTextColor(Color.WHITE);
            mLayout.setBackgroundColor(Color.BLACK);
            mClockLayout.setVisibility(View.VISIBLE);
            mClock.setText(AMBIENT_DATE_FORMAT.format(new Date()));
            mFabs.setVisibility(View.GONE);
        } else {
            mCounter.setTextColor(Color.BLACK);
            mLayout.setBackgroundColor(Color.WHITE);
            mClockLayout.setVisibility(View.GONE);
            mFabs.setVisibility(View.VISIBLE);
        }
    }
}
