package com.qiyue.qdmobile.ui.calllog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.github.snowdream.android.util.Log;
import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.api.SipManager;
import com.qiyue.qdmobile.api.SipProfile;
import com.qiyue.qdmobile.api.SipUri;
import com.qiyue.qdmobile.ui.SipHome.ViewPagerVisibilityListener;
import com.qiyue.qdmobile.ui.calllog.CallLogAdapter.OnCallLogAction;
import com.qiyue.qdmobile.widgets.CSSListFragment;

/**
 * Displays a list of call log entries.
 */
public class CallLogListFragment extends CSSListFragment implements ViewPagerVisibilityListener,
        CallLogAdapter.CallFetcher, OnCallLogAction {

    private static final String THIS_FILE = CallLogListFragment.class.getSimpleName();

    private boolean mShowOptionsMenu;
    private CallLogAdapter mAdapter;

    private boolean mDualPane;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void attachAdapter() {
        if (getListAdapter() == null) {
            if (mAdapter == null) {
                Log.d(THIS_FILE, "Attach call log adapter now");
                // Adapter
                mAdapter = new CallLogAdapter(getActivity(), this);
                mAdapter.setOnCallLogActionListener(this);
            }
            setListAdapter(mAdapter);
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {

        Log.i(THIS_FILE, "onCreateView()...");

        return inflater.inflate(R.layout.call_log_fragment, container, false);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.i(THIS_FILE, "onViewCreated()...");

        // View management
        mDualPane = getResources().getBoolean(R.bool.use_dual_panes);

        ListView lv = getListView();
        lv.setVerticalFadingEdgeEnabled(true);
        // lv.setCacheColorHint(android.R.color.transparent);
        if (mDualPane) {
            lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            lv.setItemsCanFocus(false);
        } else {
            lv.setChoiceMode(ListView.CHOICE_MODE_NONE);
            lv.setItemsCanFocus(true);
        }
        
    }
    
    @Override
    public void onResume() {
        super.onResume();
        fetchCalls();
    }

    @Override
    public void fetchCalls() {
        attachAdapter();
        if (isResumed()) {
            getLoaderManager().restartLoader(0, null, this);
        }
    }

    boolean alreadyLoaded = false;
    
    @SuppressLint("NewApi")
    @Override
    public void onVisibilityChanged(boolean visible) {

        Log.i(THIS_FILE, "onVisibilityChanged()...");

        if (mShowOptionsMenu != visible) {
            mShowOptionsMenu = visible;
            // Invalidate the options menu since we are changing the list of
            // options shown in it.
            Activity activity = getActivity();
            if (activity != null) {
                activity.invalidateOptionsMenu();
            }
        }
        
        if (visible) {
            attachAdapter();
            // Start loading
            if (!alreadyLoaded) {
                getLoaderManager().initLoader(0, null, this);
                alreadyLoaded = true;
            }
        }
        
        
        if (visible && isResumed()) {
            //getLoaderManager().restartLoader(0, null, this);
            ListView lv = getListView();
            if (lv != null && mAdapter != null) {
                final int checkedPos = lv.getCheckedItemPosition();
                if (checkedPos >= 0) {
                    // TODO post instead
                    Thread t = new Thread() {
                        public void run() {
                            final long[] selectedIds = mAdapter.getCallIdsAtPosition(checkedPos);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    viewDetails(checkedPos, selectedIds);  
                                }
                            });
                        };
                    };
                    t.start();
                }
            }
        }
    }


    private void deleteAllCalls() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(R.string.callLog_delDialog_title);
        alertDialog.setMessage(getString(R.string.callLog_delDialog_message));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.callLog_delDialog_yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().getContentResolver().delete(SipManager.CALLLOG_URI, null,
                                null);
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.callLog_delDialog_no),
                (DialogInterface.OnClickListener) null);
        try {
            alertDialog.show();
        } catch (Exception e) {
            Log.e(THIS_FILE, "error while trying to show deletion yes/no dialog");
        }
    }

    // Loader
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Log.i(THIS_FILE, "onCreateLoader()...");

        return new CursorLoader(getActivity(), SipManager.CALLLOG_URI, new String[]{
                CallLog.Calls._ID, CallLog.Calls.CACHED_NAME, CallLog.Calls.CACHED_NUMBER_LABEL,
                CallLog.Calls.CACHED_NUMBER_TYPE, CallLog.Calls.DURATION, CallLog.Calls.DATE,
                CallLog.Calls.NEW, CallLog.Calls.NUMBER, CallLog.Calls.TYPE,
                SipManager.CALLLOG_PROFILE_ID_FIELD
        },
                null, null,
                Calls.DEFAULT_SORT_ORDER);
    }


    @Override
    public void viewDetails(int position, long[] callIds) {

        if (mDualPane) {
            // If we are not currently showing a fragment for the new
            // position, we need to create and install a new one.
            CallLogDetailsFragment df = new CallLogDetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putLongArray(CallLogDetailsFragment.EXTRA_CALL_LOG_IDS, callIds);
            df.setArguments(bundle);
            // Execute a transaction, replacing any existing fragment
            // with this one inside the frame.
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.details, df, null);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();

            getListView().setItemChecked(position, true);
        } else {
            Intent it = new Intent(getActivity(), CallLogDetailsActivity.class);
            it.putExtra(CallLogDetailsFragment.EXTRA_CALL_LOG_IDS, callIds);
            getActivity().startActivity(it);
        }
    }

    @Override
    public void placeCall(String number, Long accId) {
        if (!TextUtils.isEmpty(number)) {
            Intent it = new Intent(Intent.ACTION_CALL);
            it.setData(SipUri.forgeSipUri(SipManager.PROTOCOL_CSIP, SipUri.getCanonicalSipContact(number, false)));
            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (accId != null) {
                it.putExtra(SipProfile.FIELD_ACC_ID, accId);
            }
            getActivity().startActivity(it);
        }
    }

    @Override
    public void changeCursor(Cursor c) {
        mAdapter.changeCursor(c);
    }
    
}
