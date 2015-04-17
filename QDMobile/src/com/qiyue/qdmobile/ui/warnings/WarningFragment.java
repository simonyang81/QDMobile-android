package com.qiyue.qdmobile.ui.warnings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.actionbarsherlock.app.SherlockFragment;
import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.ui.warnings.WarningUtils.OnWarningChanged;
import com.qiyue.qdmobile.ui.warnings.WarningUtils.WarningBlockView;
import com.qiyue.qdmobile.utils.Log;

import java.util.ArrayList;
import java.util.List;

public class WarningFragment extends SherlockFragment implements OnWarningChanged {

    private static final String THIS_FILE = "WarningFragment";
    private List<String> warnList = new ArrayList<String>();
    private ViewGroup viewContainer = null;
    
    
    public void setWarningList(List<String> list) {
        warnList.clear();
        warnList.addAll(list);
        
        bindView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.warning_container, container,false);
        viewContainer = (ViewGroup) v.findViewById(R.id.container); 
        bindView();
        return v;
    }
    
    private void bindView() {
        if(viewContainer != null) {
            viewContainer.removeAllViews();
            for(String warn : warnList) {
                Log.d(THIS_FILE, "Add " + warn + " warning");
                WarningBlockView v = WarningUtils.getViewForWarning(getActivity(), warn);
                if(v != null) {
                    v.setOnWarnChangedListener(this);
                    v.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                    viewContainer.addView(v);
                }
            }
        }
    }
    
    private OnWarningChanged onWChangedListener;

    @Override
    public void onWarningRemoved(String warnKey) {
        if(onWChangedListener != null) {
            onWChangedListener.onWarningRemoved(warnKey);
        }
    }
    /**
     * @param onWChangedListener the onWChangedListener to set
     */
    public void setOnWarningChangedListener(OnWarningChanged onWChangedListener) {
        this.onWChangedListener = onWChangedListener;
    }
}
