package tk.phili.dienst.dienst.utils;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;

import lombok.Getter;
import lombok.Setter;

public class LockableVisibilityMaterialButton extends MaterialButton {

    @Getter
    @Setter
    private boolean visibilityLocked = false;

    public LockableVisibilityMaterialButton(@NonNull Context context) {
        super(context);
    }

    public LockableVisibilityMaterialButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LockableVisibilityMaterialButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setVisibility(int visibility) {
        if (!visibilityLocked) {
            super.setVisibility(visibility);
        }
    }


}
