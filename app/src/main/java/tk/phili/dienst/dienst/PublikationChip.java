package tk.phili.dienst.dienst;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.tylersuehr.chips.Chip;

/**
 * Created by fipsi on 30.03.2018.
 */

public class PublikationChip extends Chip {

    String publikation;

    public PublikationChip(String publikation) {
        this.publikation = publikation;
    }

    @Nullable
    @Override
    public Object getId() {
        return null;
    }

    @NonNull
    @Override
    public String getTitle() {
        return publikation;
    }

    @Nullable
    @Override
    public String getSubtitle() {
        return null;
    }

    @Nullable
    @Override
    public Uri getAvatarUri() {
        return null;
    }

    @Nullable
    @Override
    public Drawable getAvatarDrawable() {
        return null;
    }
}
