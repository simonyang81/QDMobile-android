package com.qiyue.qdmobile.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.LinearLayout;

import com.qiyue.qdmobile.R;

public class DialerCallBar extends LinearLayout implements OnClickListener, OnLongClickListener {

    private static final String TAG = DialerCallBar.class.getSimpleName();

    public interface OnDialActionListener {
        /**
         * The make call button has been pressed
         */
        void placeCall();

        /**
         * The video button has been pressed
         */
        void placeVideoCall();

        /**
         * The delete button has been pressed
         */
        void deleteChar();

        /**
         * The delete button has been long pressed
         */
        void deleteAll();
    }

    private OnDialActionListener actionListener;

    public DialerCallBar(Context context) {
        this(context, null, 0);
    }

    public DialerCallBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DialerCallBar(Context context, AttributeSet attrs, int style) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.dialpad_additional_buttons, this, true);
        findViewById(R.id.dialVideoButton).setOnClickListener(this);
        findViewById(R.id.dialButton).setOnClickListener(this);
        findViewById(R.id.deleteButton).setOnClickListener(this);
        findViewById(R.id.deleteButton).setOnLongClickListener(this);

        if (getOrientation() == LinearLayout.VERTICAL) {
            LayoutParams lp;
            for (int i = 0; i < getChildCount(); i++) {
                lp = (LayoutParams) getChildAt(i).getLayoutParams();
                int w = lp.width;
                lp.width = lp.height;
                lp.height = w;
                lp.gravity = Gravity.CENTER_HORIZONTAL;
                // Added for clarity but not necessary
                getChildAt(i).setLayoutParams(lp);

            }
        }
    }

    /**
     * Set a listener for this widget actions
     *
     * @param l the listener called back when some user action is done on this widget
     */
    public void setOnDialActionListener(OnDialActionListener l) {
        actionListener = l;
    }

    /**
     * Set the action buttons enabled or not
     */
    public void setEnabled(boolean enabled) {
//        findViewById(R.id.dialButton).setEnabled(enabled);
        findViewById(R.id.dialVideoButton).setEnabled(enabled);
        findViewById(R.id.deleteButton).setEnabled(enabled);
    }

    /**
     * Set the video capabilities
     *
     * @param enabled whether the client is able to make video calls
     */
    public void setVideoEnabled(boolean enabled) {

        Log.d(TAG, "setVideoEnabled(), enabled: " + enabled);

        findViewById(R.id.dialVideoButton).setVisibility(enabled ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        if (actionListener != null) {
            int viewId = v.getId();
            if (viewId == R.id.dialVideoButton) {
                actionListener.placeVideoCall();
            } else if (viewId == R.id.dialButton) {
                actionListener.placeCall();
            } else if (viewId == R.id.deleteButton) {
                actionListener.deleteChar();
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (actionListener != null) {
            int viewId = v.getId();
            if (viewId == R.id.deleteButton) {
                actionListener.deleteAll();
                v.setPressed(false);
                return true;
            }
        }
        return false;
    }

}
