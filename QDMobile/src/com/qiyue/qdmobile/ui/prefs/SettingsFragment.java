package com.qiyue.qdmobile.ui.prefs;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.snowdream.android.util.Log;
import com.qiyue.qdmobile.BasFragment;
import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.ui.prefs.cupcake.PrefsLoaderActivity;

/**
 * Created by simon on 4/16/15.
 */
public class SettingsFragment extends BasFragment {

    private static final String TAG = SettingsFragment.class.getSimpleName();

    private View mEasyConfig, mNetwork, mMedia, mUserInterface, mCallOptions, mFilters;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.i(TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.i(TAG, "onActivityCreated()");

        mEasyConfig     = getActivity().findViewById(R.id.rl_settings_easy_config);
        mNetwork        = getActivity().findViewById(R.id.rl_settings_net);
        mMedia          = getActivity().findViewById(R.id.rl_settings_media);
        mUserInterface  = getActivity().findViewById(R.id.rl_settings_user_interface);
        mCallOptions    = getActivity().findViewById(R.id.rl_settings_call_options);
        mFilters        = getActivity().findViewById(R.id.rl_settings_filters);

        setListener();
    }

    private void setListener() {
        if (mEasyConfig != null) {
            mEasyConfig.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), PrefsFast.class));
                    getActivity().overridePendingTransition(R.anim.fragment_slide_in_from_bottom, R.anim.fragment_slide_out_to_top);
                }
            });
        }

        if (mNetwork != null) {
            mNetwork.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startPrefsLoaderActivity(PrefsLogic.TYPE_NETWORK);
                }
            });
        }

        if (mMedia != null) {
            mMedia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startPrefsLoaderActivity(PrefsLogic.TYPE_MEDIA);
                }
            });
        }

        if (mUserInterface != null) {
            mUserInterface.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startPrefsLoaderActivity(PrefsLogic.TYPE_UI);
                }
            });
        }

        if (mCallOptions != null) {
            mCallOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startPrefsLoaderActivity(PrefsLogic.TYPE_CALLS);
                }
            });
        }

        if (mFilters != null) {
            mFilters.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), PrefsFilters.class));
                    getActivity().overridePendingTransition(R.anim.fragment_slide_in_from_bottom, R.anim.fragment_slide_out_to_top);
                }
            });
        }

    }

    private void startPrefsLoaderActivity(int preferenceType) {
        Intent it = new Intent(getActivity(), PrefsLoaderActivity.class);
        it.putExtra(PrefsLogic.EXTRA_PREFERENCE_TYPE, preferenceType);
        startActivity(it);
        getActivity().overridePendingTransition(R.anim.fragment_slide_in_from_bottom, R.anim.fragment_slide_out_to_top);
    }

}
