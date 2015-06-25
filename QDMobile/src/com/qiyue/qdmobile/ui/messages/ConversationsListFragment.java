package com.qiyue.qdmobile.ui.messages;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.github.snowdream.android.util.Log;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;
import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.api.SipMessage;
import com.qiyue.qdmobile.service.SipNotifications;
import com.qiyue.qdmobile.ui.SipHome.ViewPagerVisibilityListener;
import com.qiyue.qdmobile.ui.messages.ConversationsAdapter.ConversationListItemViews;
import com.qiyue.qdmobile.widgets.CSSListFragment;


/**
 * This activity provides a list view of existing conversations.
 */
public class ConversationsListFragment extends CSSListFragment implements ViewPagerVisibilityListener {

    private static final String TAG = ConversationsListFragment.class.getSimpleName();

    // IDs of the context menu items for the list of conversations.
    public static final int MENU_DELETE               = 0;
    public static final int MENU_VIEW                 = 1;

    private ConversationsAdapter mAdapter;
    private FloatingActionButton mCreateMsg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {

        Log.i(TAG, "onCreateView()...");

        View v = inflater.inflate(R.layout.message_list_fragment, container, false);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.i(TAG, "onActivityCreated()...");

        ListView lv = (ListView) getActivity().findViewById(android.R.id.list);

        View.OnClickListener addClickButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAddMessage();
            }
        };

        mCreateMsg = (FloatingActionButton) getActivity().findViewById(R.id.fab_create_messages);
        mCreateMsg.setOnClickListener(addClickButtonListener);

        mCreateMsg.attachToListView(lv, new ScrollDirectionListener() {
            @Override
            public void onScrollDown() {
                Log.d(TAG, "onScrollDown()");
                mCreateMsg.show(true);
            }

            @Override
            public void onScrollUp() {
                Log.d(TAG, "onScrollUp()");
                mCreateMsg.hide(true);
            }
        }, new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.d(TAG, "onScrollStateChanged()");
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.d(TAG, "onScroll()");
            }
        });

        setHasOptionsMenu(true);
        onVisibilityChanged(true);
    }

    private void attachAdapter() {
        // Header view add
        if (mAdapter == null) {
            // Adapter
            mAdapter = new ConversationsAdapter(getActivity(), null);
            setListAdapter(mAdapter);

            mAdapter.setCallbackListener(new ConversationsAdapter.ConversationItemClick() {
                @Override
                public void callback(View view, final int position) {
                    ConversationListItemViews cri = (ConversationListItemViews) view.getTag();
                    viewDetails(cri);
                }
            });
        }

    }

    private void onClickAddMessage() {
        viewDetails(null);
    }

    @Override
    public void onResume() {
        super.onResume();

        SipNotifications nManager = new SipNotifications(getActivity());
        nManager.cancelMessages();
    }

    public void viewDetails(ConversationListItemViews cri) {
        String number = null;
        String fromFull = null;
        if (cri != null) {
            number = cri.getRemoteNumber();
            fromFull = cri.fromFull;
        }
        viewDetails(number, fromFull);
    }

    public void viewDetails(String number, String fromFull) {

        Log.i(TAG, "viewDetails()...");

        Bundle b = MessageFragment.getArguments(number, fromFull);
        Intent it = new Intent(getActivity(), MessageActivity.class);
        it.putExtras(b);
        startActivity(it);
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), SipMessage.THREAD_URI, null, null, null, null);
    }

    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info =
            (AdapterView.AdapterContextMenuInfo) menuInfo;
        if (info.position > 0) {
            menu.add(0, MENU_VIEW, 0, R.string.menu_view);
            menu.add(0, MENU_DELETE, 0, R.string.menu_delete);
        }
    }

    boolean alreadyLoaded = false;

    @Override
    public void onVisibilityChanged(boolean visible) {

        if (visible) {
            attachAdapter();
            // Start loading
            if (!alreadyLoaded) {
                getLoaderManager().initLoader(0, null, this);
                alreadyLoaded = true;
            }
        }

        if (visible && isResumed()) {
            ListView lv = getListView();
            if (lv != null && mAdapter != null) {
                final int checkedPos = lv.getCheckedItemPosition();
                if (checkedPos >= 0) {
                    // TODO post instead
                    Thread t = new Thread() {
                        public void run() {
                            Cursor c = (Cursor) mAdapter.getItem(checkedPos - getListView().getHeaderViewsCount());
                            if (c != null) {
                                String from = c.getString(c.getColumnIndex(SipMessage.FIELD_FROM));
                                String to = c.getString(c.getColumnIndex(SipMessage.FIELD_TO));
                                final String fromFull = c.getString(c.getColumnIndex(SipMessage.FIELD_FROM_FULL));
                                String number = from;
                                if (SipMessage.SELF.equals(number)) {
                                    number = to;
                                }
                                final String nbr = number;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        viewDetails(nbr, fromFull);
                                    }
                                });
                            }
                        };
                    };
                    t.start();
                }
            }
        }
    }

    @Override
    public void changeCursor(Cursor c) {
        if (mAdapter != null) {
            mAdapter.changeCursor(c);
        }
    }


}
