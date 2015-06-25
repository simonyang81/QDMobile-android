package com.qiyue.qdmobile.widgets.contactbadge;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.QuickContactBadge;

@TargetApi(5)
public class ContactBadge5 extends ContactBadgeContract {

    QuickContactBadge badge;

    public ContactBadge5(Context context, AttributeSet attrs, int defStyle,
                         com.qiyue.qdmobile.widgets.contactbadge.QuickContactBadge topBadge) {
        super(context, attrs, defStyle, topBadge);
        badge = new OverlayedQuickContactBadge(context, attrs, defStyle, topBadge);
    }
    
    @Override
    public ImageView getImageView() {
        return badge;
    }

    @Override
    public void assignContactUri(Uri uri) {
        badge.assignContactUri(uri);

    }

}
