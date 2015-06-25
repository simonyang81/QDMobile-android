package com.qiyue.qdmobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.github.snowdream.android.util.Log;
import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.ui.dialpad.DialerFragment;
import com.qiyue.qdmobile.utils.Constants;

/**
 * Created by Simon on 6/6/15.
 */
public class FlashFragment extends Fragment {

    private static final String TAG = FlashFragment.class.getSimpleName();

    private final int SPLASH_DISPLAY_LENGTH = 10;

    private View mProgressBar, mImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.i(TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.fragment_flash, container, false);

        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.i(TAG, "onActivityCreated()");

        mProgressBar    = getActivity().findViewById(R.id.flash_loading);
        mImageView      = getActivity().findViewById(R.id.flash_image);

        Intent i = getActivity().getIntent();

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.5f, 1.0f);
        alphaAnimation.setDuration(1500);
        mImageView.startAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        gotoMainScreen();
                    }
                }, SPLASH_DISPLAY_LENGTH);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

    }

    private void gotoMainScreen() {

        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ((SipHome) getActivity()).setMenuButton(SipHome.MenuButton.dialpad);

        DialerFragment mDialpadFragment
                = (DialerFragment) getActivity().getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_DIALPAD);
        if (mDialpadFragment == null) {
            mDialpadFragment = new DialerFragment();
        }
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.replace(R.id.content_frame, mDialpadFragment, Constants.FRAGMENT_TAG_DIALPAD);

        ft.commit();

        ((SipHome) getActivity()).showMenuWithAnimator();

    }


}
