package com.qiyue.qdmobile.ui.incall;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ListAdapter;

import com.github.snowdream.android.util.Log;

import java.util.ArrayList;

public class InCallInfoGrid extends FrameLayout {

    private static final String THIS_FILE = "InCallInfoGrid";

    private final ArrayList<View> mItems = new ArrayList<View>();

    public InCallInfoGrid(Context context) {
        this(context, null);
    }

    public InCallInfoGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private int minColumnWidth = 400;
    private int minRowHeight = 400;
    private ListAdapter mAdapter;
    private CallDataObserver mObserver;

    /**
     * Set the minimum size of a cell
     * @param w minimum width of a cell
     * @param h minimum height of a cell
     */
    public void setMinCellSize(int w, int h) {
        minColumnWidth = w;
        minRowHeight = h;
    }

    public void removeViewAt(int position) {
        if (position < 0 || position >= mItems.size()) {
            Log.w(THIS_FILE, "Trying to remove unknown view at " + position);
        } else {
            final View ii = mItems.get(position);
            if (ii instanceof InCallCard) {
                ((InCallCard) ii).terminate();
            }
            mItems.remove(position);
        }

        super.removeViewAt(position);
    }


    public void setAdapter(ListAdapter adapter) {

        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mObserver);
            terminate();
        }

        mAdapter = adapter;

        if (mAdapter != null) {
            if (mObserver == null) {
                mObserver = new CallDataObserver();
            }
            mAdapter.registerDataSetObserver(mObserver);
            populate();
        }
    }

    private final Handler handler = new Handler();
    private final Runnable postLayout = new Runnable() {
        @Override
        public void run() {
            populate();
        }
    };

    @Override
    synchronized protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {
            handler.postDelayed(postLayout, 100);
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    synchronized void populate() {

        if (mAdapter == null) {
            return;
        }

        // Also, don't populate until we are attached to a window.  This is to
        // avoid trying to populate before we have restored our view hierarchy
        // state and conflicting with what is restored.
        if (getWindowToken() == null) {
            return;
        }

        int count = mAdapter.getCount();
        Log.d(THIS_FILE, "Populate " + count + " children");


        // Compute num of columns 
        int width = getWidth() - (getPaddingRight() + getPaddingLeft());
        int height = getHeight() - (getPaddingTop() + getPaddingBottom());
        int cellWidth = width;
        int cellHeight = height;
        int numRows = 1;
        int numColumns = 1;
        if (count > 0) {
            int possibleColumns = (int) FloatMath.floor((width * 1.0f) / (minColumnWidth * 1.0f));
            if (possibleColumns <= 0) {
                possibleColumns = 1;
            }
            numColumns = Math.min(possibleColumns, count);
            numRows = count / numColumns;

            Log.v(THIS_FILE, "Render a grid of " + numColumns + " x " + numRows);

            cellWidth = width / numColumns;
            cellHeight = height / numRows;

            // TODO : warn - scroll - other?if we are outside bounds with min cell height
            if (cellHeight < minRowHeight) {
                Log.d(THIS_FILE, "May render weird... min height not correct " + cellHeight);
            }
            if (cellHeight <= 0 || cellWidth <= 0) {
                Log.w(THIS_FILE, "The call grid cannot render " + cellHeight + "x" + cellWidth
                        + " for " + width + "x" + height);
                cellWidth = minColumnWidth;
                cellHeight = minRowHeight;
            }
        }

        // Add it if needed.
        int curIndex = -1;
        for (curIndex = 0; curIndex < mAdapter.getCount(); curIndex++) {

            View ii = null;
            if (mItems.size() > curIndex) {
                ii = mItems.get(curIndex);
            }
            ii = mAdapter.getView(curIndex, ii, this);
            if (mItems.size() > curIndex) {
                mItems.set(curIndex, ii);
            } else {
                mItems.add(ii);
            }

            // Set layout of the view
            int posX = curIndex % numColumns;
            int posY = curIndex / numColumns;
            LayoutParams lp = (LayoutParams) ii.getLayoutParams();
            if (lp == null) {
                lp = new FrameLayout.LayoutParams(cellWidth, cellHeight);
            } else {
                lp.height = cellHeight;
                lp.width = cellWidth;
            }
            lp.leftMargin = posX * cellWidth;
            lp.topMargin = posY * cellHeight;
            ii.setLayoutParams(lp);
            // Append to parent if needed
            ViewParent p = ii.getParent();
            if (p == null) {
                addView(ii);
            } else {
                if (p == this) {
                    updateViewLayout(ii, lp);
                } else {
                    Log.w(THIS_FILE, "Call card already attached to somebody else");
                }
            }
            ii.forceLayout();
        }

        // Remove useles
        if (mItems.size() > mAdapter.getCount()) {
            for (curIndex = mItems.size() - 1; curIndex >= mAdapter.getCount(); curIndex--) {
                removeViewAt(curIndex);
            }
        }

    }


    private class CallDataObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            populate();
        }

        @Override
        public void onInvalidated() {
            populate();
        }
    }


    public synchronized void terminate() {
        for (int i = mItems.size() - 1; i >= 0; i--) {
            removeViewAt(i);
        }
        mItems.clear();
    }

}
