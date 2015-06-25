package com.qiyue.qdmobile.widgets.contactbadge;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

@TargetApi(5)
public class OverlayedQuickContactBadge extends android.widget.QuickContactBadge {

    private QuickContactBadge topBadge;

    public OverlayedQuickContactBadge(Context context) {
        super(context);
    }

    public OverlayedQuickContactBadge(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OverlayedQuickContactBadge(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public OverlayedQuickContactBadge(Context context, AttributeSet attrs, int defStyle,
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
