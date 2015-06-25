package com.qiyue.qdmobile.widgets.contactbadge;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.github.snowdream.android.util.Log;
import com.qiyue.qdmobile.utils.Compatibility;

import java.lang.reflect.Constructor;

public class QuickContactBadge extends FrameLayout {

    private static final String THIS_FILE = "QuickContactBadgeCompat";
    private ContactBadgeContract badge;


    public QuickContactBadge(Context context) {
        this(context, null, 0);
    }

    public QuickContactBadge(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuickContactBadge(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        String className = "com.qiyue.qdmobile.widgets.contactbadge.ContactBadge";
        if (Compatibility.isCompatible(5)) {
            className += "5";
        } else {
            className += "3";
        }

        try {
            Class<? extends ContactBadgeContract> wrappedClass = Class.forName(className)
                    .asSubclass(ContactBadgeContract.class);
            Constructor<? extends ContactBadgeContract> constructor = wrappedClass.getConstructor(
                    Context.class, AttributeSet.class, int.class, QuickContactBadge.class);
            badge = constructor.newInstance(context, attrs, defStyle, this);
        } catch (Exception e) {
            Log.e(THIS_FILE, "Problem when trying to load for compat mode");
        }
        if (badge != null) {
            ImageView imageView = badge.getImageView();
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);
            addView(imageView, params);
        }

        setDrawable();
    }


    public ImageView getImageView() {
        if(badge != null) {
            return badge.getImageView();
        }
        return null;
    }

    public void assignContactUri(Uri uri) {
        if(badge != null) {
            badge.assignContactUri(uri);
        }
    }

    public enum ArrowPosition {
        LEFT,
        RIGHT,
        NONE
    };

    private ArrowPosition arrowPos = ArrowPosition.NONE;

    public void setPosition(ArrowPosition position) {
        arrowPos = position;
        setDrawable();
        invalidate();
    }

    private void setDrawable() {
        setWillNotDraw(arrowPos == ArrowPosition.NONE);
    }

    public void overlay(Canvas c, ImageView img) {
        if (arrowPos != ArrowPosition.NONE) {
            
            int x_border = (arrowPos == ArrowPosition.LEFT) ? 0 : img.getWidth();
            int x_inside = x_border + ((arrowPos == ArrowPosition.LEFT) ? 1 : -1 ) * (int)(img.getWidth() * 0.2f);
            int y_top = (int) (img.getHeight() * 0.2f);
            int y_bottom = (int) (img.getHeight() * 0.6f);
            c.save();
            
            Path path = new Path();   
            path.setFillType(Path.FillType.EVEN_ODD);
            path.moveTo(x_border, y_top);
            path.lineTo(x_inside, (y_top + y_bottom)/2);
            path.lineTo(x_border, y_bottom);
            path.lineTo(x_border, y_top);
            path.close();

            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStrokeWidth(0);
            paint.setColor(android.graphics.Color.BLACK);     
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setAntiAlias(true);

            c.drawPath(path, paint);
            c.restore();
        }
    }
    

}
