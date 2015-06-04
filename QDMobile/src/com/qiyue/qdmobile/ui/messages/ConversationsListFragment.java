package com.qiyue.qdmobile.ui.messages;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;
import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.api.SipMessage;
import com.qiyue.qdmobile.service.SipNotifications;
import com.qiyue.qdmobile.ui.PickupSipUri;
import com.qiyue.qdmobile.ui.SipHome.ViewPagerVisibilityListener;
import com.qiyue.qdmobile.ui.messages.ConversationsAdapter.ConversationListItemViews;
import com.qiyue.qdmobile.utils.Constants;
import com.qiyue.qdmobile.utils.Log;
import com.qiyue.qdmobile.widgets.CSSListFragment;

import java.security.acl.LastOwnerException;

/**
 * This activity provides a list view of existing conversations.
 */
public class ConversationsListFragment extends CSSListFragment implements ViewPagerVisibilityListener {

    private static final String TAG = ConversationsListFragment.class.getSimpleName();

    // IDs of the context menu items for the list of conversations.
    public static final int MENU_DELETE               = 0;
    public static final int MENU_VIEW                 = 1;

//    private boolean mDualPane;

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
//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                android.util.Log.d(TAG, "ListView onItemClick()...");
//
//                ConversationListItemViews cri = (ConversationListItemViews) view.getTag();
//                viewDetails(position, cri);
//
//            }
//        });

        View.OnClickListener addClickButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAddMessage();
            }
        };

//        lv.setOnCreateContextMenuListener(this);

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

//        Intent pickupIntent = new Intent(getActivity(), PickupSipUri.class);
//        startActivityForResult(pickupIntent, Constants.PICKUP_SIP_URI);
//        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        viewDetails(null);
    }

//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        // View management
//        mDualPane = getResources().getBoolean(R.bool.use_dual_panes);
//
//        // Modify list view
//        ListView lv = getListView();
//        lv.setVerticalFadingEdgeEnabled(true);
//        // lv.setCacheColorHint(android.R.color.transparent);
//        if (mDualPane) {
//            lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//            lv.setItemsCanFocus(false);
//        } else {
//            lv.setChoiceMode(ListView.CHOICE_MODE_NONE);
//            lv.setItemsCanFocus(true);
//        }
//    }

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

//        TODO
//        if (mDualPane) {
//            // If we are not currently showing a fragment for the new
//            // position, we need to create and install a new one.
//            MessageFragment df = new MessageFragment();
//            df.setArguments(b);
//            // Execute a transaction, replacing any existing fragment
//            // with this one inside the frame.
//            FragmentTransaction ft = getFragmentManager().beginTransaction();
//            ft.replace(R.id.details, df, null);
//            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//            ft.commit();
//
//            getListView().setItemChecked(position, true);
//        } else {
//            Intent it = new Intent(getActivity(), MessageActivity.class);
//            it.putExtras(b);
//            startActivity(it);
//        }
    }


    // Options
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem writeMenu = menu.add(R.string.menu_compose_new);
        writeMenu.setIcon(R.drawable.ic_menu_msg_compose_holo_dark).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        writeMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onClickAddMessage();
                return true;
            }
        });

        if (getListAdapter() != null && getListAdapter().getCount() > 0) {

            MenuItem deleteAllMenu = menu.add(R.string.menu_delete_all);
            deleteAllMenu.setIcon(android.R.drawable.ic_menu_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            deleteAllMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    confirmDeleteThread(null);
                    return true;
                }
            });
        }
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

//    @Override
//    public boolean onContextItemSelected(android.view.MenuItem item) {
//        AdapterView.AdapterContextMenuInfo info =
//                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//
//        if (info.position > 0) {
//            ConversationListItemViews cri = (ConversationListItemViews) info.targetView.getTag();
//
//            if(cri != null) {
//                switch (item.getItemId()) {
//                case MENU_DELETE: {
//                    confirmDeleteThread(cri.getRemoteNumber());
//                    break;
//                }
//                case MENU_VIEW: {
//                    viewDetails(info.position, cri);
//                    break;
//                }
//                default:
//                    break;
//                }
//            }
//        }
//        return super.onContextItemSelected(item);
//    }
//
////    @Override
//    public void onListItemClick(ListView l, View v, int position, long id) {
//        ConversationListItemViews cri = (ConversationListItemViews) v.getTag();
//        viewDetails(position, cri);
//    }


	/*

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();


        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case MENU_COMPOSE_NEW:
                createNewMessage();
                break;
            case MENU_DELETE_ALL:
            	confirmDeleteThread(null);
            	break;
            default:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        if (position == 0) {
            createNewMessage();
        } else {
            ConversationListItemViews tag = (ConversationListItemViews) v.getTag();
            if (tag.from.equals("SELF")) {
                openThread(tag.to, tag.fromFull);
            } else {
                openThread(tag.from, tag.fromFull);
            }
        }
    }
    */

    private void confirmDeleteThread(final String from) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.confirm_dialog_title)
            .setIcon(android.R.drawable.ic_dialog_alert)
        .setCancelable(true)
        .setPositiveButton(R.string.delete, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(TextUtils.isEmpty(from)) {
				    getActivity().getContentResolver().delete(SipMessage.MESSAGE_URI, null, null);
				}else {
				    Builder threadUriBuilder = SipMessage.THREAD_ID_URI_BASE.buildUpon();
				    threadUriBuilder.appendEncodedPath(from);
				    getActivity().getContentResolver().delete(threadUriBuilder.build(), null, null);
				}
			}
		})
        .setNegativeButton(R.string.no, null)
        .setMessage(TextUtils.isEmpty(from)
                ? R.string.confirm_delete_all_conversations
                        : R.string.confirm_delete_conversation)
        .show();
    }

    boolean alreadyLoaded = false;

    @Override
    public void onVisibilityChanged(boolean visible) {

        if(visible) {
            attachAdapter();
            // Start loading
            if(!alreadyLoaded) {
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
                            if(c != null) {
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
        if(mAdapter != null) {
            mAdapter.changeCursor(c);
        }
    }


}
