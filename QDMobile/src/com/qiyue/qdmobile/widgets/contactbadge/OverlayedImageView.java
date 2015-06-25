package com.qiyue.qdmobile.widgets.contactbadge;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

public class OverlayedImageView extends ImageView {

    private QuickContactBadge topBadge;

    public OverlayedImageView(Context context) {
        this(context, null, 0, null);
    }

    public OverlayedImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0, null);
    }

    public OverlayedImageView(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, null);
    }

    public OverlayedImageView(Context context, AttributeSet attrs, int defStyle,
            QuickContactBadge topBadge) {
        super(context, attrs, defStyle);
        this.topBadge = topBadge;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        topBadge.overlay(canvas, this);
    }

}
