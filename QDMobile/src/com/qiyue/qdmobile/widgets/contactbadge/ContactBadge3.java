package com.qiyue.qdmobile.widgets.contactbadge;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ContactBadge3 extends ContactBadgeContract {

    private OverlayedImageView imageView;

    public ContactBadge3(Context context, AttributeSet attrs, int defStyle,
            QuickContactBadge topBadge) {
        super(context, attrs, defStyle, topBadge);
        imageView = new OverlayedImageView(context, attrs, defStyle, topBadge);

    }

    @Override
    public ImageView getImageView() {
        return imageView;
    }

    @Override
    public void assignContactUri(Uri uri) {
        // Nothing to do
    }

}
