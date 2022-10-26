package tk.phili.dienst.dienst.utils;

import android.content.Context;
import android.util.AttributeSet;

import tk.phili.dienst.dienst.R;

public class MaterialYouSwitchPreference extends com.yarolegovich.mp.MaterialSwitchPreference {

    public MaterialYouSwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MaterialYouSwitchPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MaterialYouSwitchPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected int getLayout() {
        return R.layout.view_switch_preference_you;
    }
}
