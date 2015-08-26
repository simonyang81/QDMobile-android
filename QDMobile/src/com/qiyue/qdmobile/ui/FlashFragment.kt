package com.qiyue.qdmobile.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation

import com.github.snowdream.android.util.Log
import com.qiyue.qdmobile.R
import com.qiyue.qdmobile.ui.dialpad.DialerFragment
import com.qiyue.qdmobile.utils.Constants

import kotlinx.android.synthetic.fragment_flash.*

/**
 * Created by Simon on 6/6/15.
 */
public class FlashFragment : Fragment() {

    private val SPLASH_DISPLAY_LENGTH = 10

//    private var mProgressBar: View? = null
//    private var mImageView: View? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        Log.i(TAG, "onCreateView()")
        val view = inflater!!.inflate(R.layout.fragment_flash, container, false)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Log.i(TAG, "onActivityCreated()")

//        mProgressBar = getActivity().findViewById(R.id.flash_loading)
//        mImageView = getActivity().findViewById(R.id.flash_image)

//        val i = getActivity().getIntent()

        val alphaAnimation = AlphaAnimation(0.5f, 1.0f)
        alphaAnimation.setDuration(1500)
        flash_image.startAnimation(alphaAnimation)
        alphaAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
            }

            override fun onAnimationEnd(animation: Animation) {

                Handler().postDelayed(object : Runnable {
                    override fun run() {
                        gotoMainScreen()
                    }
                }, SPLASH_DISPLAY_LENGTH.toLong())
            }

            override fun onAnimationRepeat(animation: Animation) {
            }
        })

    }

    private fun gotoMainScreen() {

        val fm = getActivity().getSupportFragmentManager()
        val ft = fm.beginTransaction()

        (getActivity() as SipHome).setMenuButton(SipHome.MenuButton.dialpad)

        var mDialpadFragment
                = getActivity().getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_DIALPAD)
        if (mDialpadFragment == null) {
            mDialpadFragment = DialerFragment()
        }
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        ft.replace(R.id.content_frame, mDialpadFragment, Constants.FRAGMENT_TAG_DIALPAD)

        ft.commitAllowingStateLoss()

        (getActivity() as SipHome).showMenuWithAnimator()

    }

    companion object {
        private val TAG = javaClass<FlashFragment>().getSimpleName()
    }


}
