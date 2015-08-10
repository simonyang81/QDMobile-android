package com.qiyue.qdmobile;

import android.support.v4.app.Fragment;

import com.github.snowdream.android.util.Log;

import java.lang.reflect.Field;

/**
 * Created by simon on 8/10/15.
 */
public class BasFragment extends Fragment {

    private static final String TAG = BasFragment.class.getSimpleName();

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (Exception e) {
            Log.e(TAG, "onDetach(), " + e.toString());
        }
    }
}
