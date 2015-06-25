package com.qiyue.qdmobile.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class MaxScaleImageView extends ImageView {

    public MaxScaleImageView(Context context) {
        this(context, null);
    }
    
    public MaxScaleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public MaxScaleImageView(Context context, AttributeSet attrs, int style) {
        super(context, attrs);
    }
    
    private float mMaxScale = 4.0f;
    
    public void setImageMaxScale(float maxScale) {
        mMaxScale = maxScale;
        updateScale();
    }
    
    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        updateScale();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        updateScale();
    }
    
    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        updateScale();
    }
    
    private void updateScale() {
        Drawable d = getDrawable();
        if (d instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) d;

            // Don't upscale if more than 2x larger
            if (getWidth() > mMaxScale * bd.getIntrinsicWidth()
                    && getHeight() > mMaxScale * bd.getIntrinsicHeight()) {
                setScaleType(ScaleType.MATRIX);
                Matrix trans = new Matrix();
                Matrix scale = new Matrix();
                trans.setTranslate((getWidth() - mMaxScale * bd.getIntrinsicWidth())/2, (getHeight() - mMaxScale * bd.getIntrinsicHeight())/2);
                scale.setScale(mMaxScale, mMaxScale);
                Matrix m = new Matrix();
                
                if (isInEditMode()) {
                    // WTF? Edit mode consider inversed matrix??
                    m.setConcat(scale, trans);
                } else {
                    m.setConcat(trans, scale);
                }
                setImageMatrix(m);
            } else {
                setScaleType(ScaleType.CENTER_CROP);
            }
        }
    }
    
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            updateScale();
        }
    }
}
