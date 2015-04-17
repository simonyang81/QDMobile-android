package com.qiyue.qdmobile.widgets;

import com.qiyue.qdmobile.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.LinearLayout;

/**
 * An entry in the call log.
 */
public class CheckableListItemView extends LinearLayout implements Checkable {
    public CheckableListItemView(Context context) {
        super(context);
    }

    public CheckableListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
//
//    public CallLogListItemView(Context context, AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//    }

    @Override
    public void requestLayout() {
        // We will assume that once measured this will not need to resize
        // itself, so there is no need to pass the layout request to the parent
        // view (ListView).
        forceLayout();
    }

    
    // Checkable behavior
	private boolean checked = false;
    
	@Override
	public boolean isChecked() {
		return checked;
	}
	
	@Override
	public void setChecked(boolean aChecked) {
		if(checked == aChecked) {
			return;
		}
		checked = aChecked;
		setBackgroundResource(checked? R.drawable.abs__list_longpressed_holo : R.drawable.transparent);
	}
	
	@Override
	public void toggle() {
		setChecked(!checked);
	}
}
