package com.qiyue.qdmobile.widgets.contactbadge;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;

public abstract class ContactBadgeContract {

    public ContactBadgeContract(Context context, AttributeSet attrs, int defStyle, QuickContactBadge topBadge) {
    }

    /**
     * Return the image view object wrapped for compatibility
     * @return The wrapped object
     */
    public abstract ImageView getImageView();

    /**
     * @see android.widget.QuickContactBadge#assignContactUri(Uri)
     * @param uri
     */
    public abstract void assignContactUri(Uri uri);
}
