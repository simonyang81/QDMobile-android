package com.qiyue.qdmobile.ui.dialpad;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.utils.Log;

/**
 *
 */
public class DialerLayout extends LinearLayout {

    private static final String THIS_FILE = "DialerLayout";
    // All these values are in dpi
    // 45 is the height of call button but we can take 1dpi at max
    private static final int MIN_BTNS_HEIGHT = 43;
    private static final int LIST_BTNS_HEIGHT = 100;
    private static final int LIST_DIGITS_HEIGHT = 100;
    private static final int LIST_DIALPAD_HEIGHT = 85 * 4;
    private static final int LIST_MIN_HEIGHT = 160;

    private boolean forceNoList = false;
    private boolean canShowList = false;

    private float expectedBtnHeightFactor;

    private OnAutoCompleteListVisibilityChangedListener onAutoCompleteListVisibilityChangedListener;

    public void setAutoCompleteListVisibiltyChangedListener(OnAutoCompleteListVisibilityChangedListener listener) {
        onAutoCompleteListVisibilityChangedListener = listener;
    }

    /**
     * Interface for notifying listeners when the value of canShowList changes
     */
    public interface OnAutoCompleteListVisibilityChangedListener {
        public void onAutoCompleteListVisibiltyChanged();
    }

    public DialerLayout(Context context) {
        super(context);
        init();
    }

    public DialerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Cache weights
        int btnsWeight = getResources().getInteger(R.integer.dialpad_layout_weight_additional_buttons);
        int padWeight = getResources().getInteger(R.integer.dialpad_layout_weight_dialpad);
        int digitsWeight = getResources().getInteger(R.integer.dialpad_layout_weight_digits);

        expectedBtnHeightFactor = (btnsWeight * 1.0f) / ((btnsWeight + padWeight + digitsWeight) * 1.0f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        float density = dm.density;

        // If we detect that height for pad will not be enough... force it's height instead of having weight
        View dialerCallBar = findViewById(R.id.dialerCallBar);
        if (dialerCallBar != null) {
            LayoutParams lp = (LayoutParams) dialerCallBar.getLayoutParams();
            int orientation = LinearLayout.HORIZONTAL;
            if (dialerCallBar instanceof LinearLayout) {
                orientation = ((LinearLayout) dialerCallBar).getOrientation();
            }
            if (height * expectedBtnHeightFactor < MIN_BTNS_HEIGHT * density) {
                if (orientation == LinearLayout.HORIZONTAL) {
                    lp.height = (int) (MIN_BTNS_HEIGHT * density);
                    lp.weight = 0;
                } else {
                    lp.weight = getResources().getInteger(R.integer.dialpad_layout_weight_additional_buttons);
                }
            } else {
                if (orientation == LinearLayout.HORIZONTAL) {
                    lp.height = 0;
                } else {
                    lp.width = 0;
                }
                lp.weight = getResources().getInteger(R.integer.dialpad_layout_weight_additional_buttons);
            }
            dialerCallBar.setLayoutParams(lp);
        }

        // remember the current state of canShowList
        boolean previousState = canShowList;

        // If we detect that height is enough to show the list
        if (!forceNoList &&
                height > (LIST_BTNS_HEIGHT + LIST_DIALPAD_HEIGHT + LIST_DIGITS_HEIGHT + LIST_MIN_HEIGHT) * density) {
            Log.d(THIS_FILE, "We force height to show list");
            canShowList = true;
            for (int i = 0; i < getChildCount(); i++) {
                View v = getChildAt(i);
                int id = v.getId();
                if (id == R.id.dialerCallBar) {
                    LayoutParams lp = (LayoutParams) v.getLayoutParams();
                    lp.height = (int) (LIST_BTNS_HEIGHT * density);
                    lp.weight = 0;
                    v.setLayoutParams(lp);
                } else if (id == R.id.dialPad) {
                    LayoutParams lp = (LayoutParams) v.getLayoutParams();
                    lp.height = (int) (LIST_DIALPAD_HEIGHT * density);
                    lp.weight = 0;
                    v.setLayoutParams(lp);
                } else if (id == R.id.topField) {
                    LayoutParams lp = (LayoutParams) v.getLayoutParams();
                    lp.height = (int) (LIST_DIGITS_HEIGHT * density);
                    lp.weight = 0;
                    v.setLayoutParams(lp);
                } else if (id == R.id.autoCompleteList) {
                    v.setVisibility(VISIBLE);
                }
            }
        } else {
            canShowList = false;
        }

        // if the state of canShowList has changed and we have a listener then notify it
        if (previousState != canShowList && onAutoCompleteListVisibilityChangedListener != null) {
            onAutoCompleteListVisibilityChangedListener.onAutoCompleteListVisibiltyChanged();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setForceNoList(boolean noList) {
        forceNoList = noList;
    }

    public boolean canShowList() {
        return canShowList;
    }

}
