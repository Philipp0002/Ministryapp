package tk.phili.dienst.dienst.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.android.material.shape.ShapeAppearancePathProvider;

public class MaskedMaterialCardView extends MaterialCardView {
    private ShapeAppearancePathProvider pathProvider = new ShapeAppearancePathProvider();
    private Path path = new Path();
    private RectF rectF = new RectF(0f, 0f, 0f, 0f);

    public MaskedMaterialCardView(Context context) {
        super(context);
    }

    public MaskedMaterialCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MaskedMaterialCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.clipPath(path);
        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        recalculatePath();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void recalculatePath() {
        rectF.right = (float) getWidth();
        rectF.bottom = (float) getHeight();
        pathProvider.calculatePath(getShapeAppearanceModel(), 1f, rectF, path);
    }

    @Override
    public void setShapeAppearanceModel(@NonNull ShapeAppearanceModel shapeAppearanceModel) {
        super.setShapeAppearanceModel(shapeAppearanceModel);
        recalculatePath();
    }
}
