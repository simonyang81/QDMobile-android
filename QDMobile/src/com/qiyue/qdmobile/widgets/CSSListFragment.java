package com.qiyue.qdmobile.widgets;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.qiyue.qdmobile.R;

public abstract class CSSListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private View mListContainer = null;
    private View mProgressContainer = null;
    private boolean mListShown = false;

    @Override
    public void setListShown(boolean shown) {
        setListShown(shown, true);
    }

    @Override
    public void setListShownNoAnimation(boolean shown) {
        setListShown(shown, false);
    }

    private void setListShown(boolean shown, boolean animate) {
        ensureCustomList();
        if (mListShown == shown) {
            return;
        }
        mListShown = shown;
        if (mListContainer != null && mProgressContainer != null) {
            if (shown) {
                if (animate) {
                    mListContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
                }
                mListContainer.setVisibility(View.VISIBLE);
                mProgressContainer.setVisibility(View.GONE);
            } else {
                if (animate) {
                    mListContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
                }
                mListContainer.setVisibility(View.GONE);
                mProgressContainer.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Make sure our private reference to views are correct.
     */
    private void ensureCustomList() {
        if (mListContainer != null) {
            return;
        }
        mListContainer = getView().findViewById(R.id.listContainer);
        mProgressContainer = getView().findViewById(R.id.progressContainer);
    }

    public abstract Loader<Cursor> onCreateLoader(int loader, Bundle args);


    /**
     * {@inheritDoc}
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        changeCursor(data);
        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        changeCursor(null);
    }

    /**
     * Request a cursor change to the adapter. <br/>
     * To be implemented by extenders.
     *
     * @param c the new cursor to replace the old one
     */
    public abstract void changeCursor(Cursor c);


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // When we will recycle this view, the stored shown and list containers becomes invalid
        mListShown = false;
        mListContainer = null;
        super.onActivityCreated(savedInstanceState);
    }
}
