package com.qiyue.qdmobile.ui.account;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.github.snowdream.android.util.Log;
import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.api.SipProfile;
import com.qiyue.qdmobile.ui.account.AccountsEditListAdapter.AccountRowTag;
import com.qiyue.qdmobile.ui.account.AccountsEditListAdapter.OnCheckedRowListener;
import com.qiyue.qdmobile.widgets.CSSListFragment;
import com.qiyue.qdmobile.widgets.DragnDropListView;
import com.qiyue.qdmobile.widgets.DragnDropListView.DropListener;
import com.qiyue.qdmobile.wizards.BasePrefsWizard;
import com.qiyue.qdmobile.wizards.WizardChooser;
import com.qiyue.qdmobile.wizards.WizardUtils;
import com.qiyue.qdmobile.wizards.WizardUtils.WizardInfo;

import java.util.ArrayList;

public class AccountsEditListFragment extends CSSListFragment implements OnCheckedRowListener {

    private boolean dualPane;
    private Long curCheckPosition = SipProfile.INVALID_ID;
    private final Handler mHandler = new Handler();
    private AccountStatusContentObserver statusObserver = null;
    private View mHeaderView;
    private AccountsEditListAdapter mAdapter;

    class AccountStatusContentObserver extends ContentObserver {

        public AccountStatusContentObserver(Handler h) {
            super(h);
        }

        public void onChange(boolean selfChange) {
            ((BaseAdapter) getListAdapter()).notifyDataSetChanged();
        }
    }

    private final static String CURRENT_CHOICE = "curChoice";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView lv = getListView();

        lv.setCacheColorHint(Color.TRANSPARENT);

        View detailsFrame = getActivity().findViewById(R.id.details);
        dualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;


        if (savedInstanceState != null) {
            curCheckPosition = savedInstanceState.getLong(CURRENT_CHOICE, SipProfile.INVALID_ID);
        }
        setListShown(false);
        if (mAdapter == null) {
            if (mHeaderView != null) {
                lv.addHeaderView(mHeaderView, null, true);
            }
            mAdapter = new AccountsEditListAdapter(getActivity(), null);
            mAdapter.setOnCheckedRowListener(this);
            //getListView().setEmptyView(getActivity().findViewById(R.id.progress_container));
            //getActivity().findViewById(android.R.id.empty).setVisibility(View.GONE);
            setListAdapter(mAdapter);
            registerForContextMenu(lv);

            // Prepare the loader.  Either re-connect with an existing one,
            // or start a new one.
            getLoaderManager().initLoader(0, null, this);

            lv.setVerticalFadingEdgeEnabled(true);
        }

        if (dualPane) {
            // In dual-pane mode, the list view highlights the selected item.
            lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            //lv.setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_LEFT);
            lv.setVerticalScrollBarEnabled(false);
            lv.setFadingEdgeLength(50);

            updateCheckedItem();
        } else {
            lv.setVerticalScrollBarEnabled(true);
            lv.setFadingEdgeLength(100);
        }
    }

    private static final int CHOOSE_WIZARD = 0;
    private static final int CHANGE_WIZARD = 1;

    private static final String THIS_FILE = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Use custom drag and drop view
        View v = inflater.inflate(R.layout.accounts_edit_list, container, false);

        final DragnDropListView lv = (DragnDropListView) v.findViewById(android.R.id.list);

        lv.setGrabberId(R.id.grabber);
        // Setup the drop listener
        lv.setOnDropListener(new DropListener() {
            @Override
            public void drop(int from, int to) {
                Log.d(THIS_FILE, "Drop from " + from + " to " + to);
                int hvC = lv.getHeaderViewsCount();
                from = Math.max(0, from - hvC);
                to = Math.max(0, to - hvC);

                int i;
                // First of all, compute what we get before move
                ArrayList<Long> orderedList = new ArrayList<Long>();
                CursorAdapter ad = (CursorAdapter) getListAdapter();
                for (i = 0; i < ad.getCount(); i++) {
                    orderedList.add(ad.getItemId(i));
                }
                // Then, invert in the current list the two items ids
                Long moved = orderedList.remove(from);
                orderedList.add(to, moved);

                // Finally save that in db
                if (getActivity() != null) {
                    ContentResolver cr = getActivity().getContentResolver();
                    for (i = 0; i < orderedList.size(); i++) {
                        Uri uri = ContentUris.withAppendedId(SipProfile.ACCOUNT_ID_URI_BASE, orderedList.get(i));
                        ContentValues cv = new ContentValues();
                        cv.put(SipProfile.FIELD_PRIORITY, i);
                        cr.update(uri, cv, null, null);
                    }
                }
            }
        });

        OnClickListener addClickButtonListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAddAccount();
            }
        };
        // Header view
        mHeaderView = inflater.inflate(R.layout.generic_add_header_list, container, false);
        mHeaderView.setOnClickListener(addClickButtonListener);

        // Empty view
        Button bt = (Button) v.findViewById(android.R.id.empty);
        bt.setOnClickListener(addClickButtonListener);

        return v;
    }

    private void updateCheckedItem() {
        if (curCheckPosition != SipProfile.INVALID_ID) {
            for (int i = 0; i < getListAdapter().getCount(); i++) {
                long profId = getListAdapter().getItemId(i);
                if (profId == curCheckPosition) {
                    getListView().setItemChecked(i, true);
                }
            }
        } else {
            for (int i = 0; i < getListAdapter().getCount(); i++) {
                getListView().setItemChecked(i, false);
            }
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(CURRENT_CHOICE, curCheckPosition);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (statusObserver == null) {
            statusObserver = new AccountStatusContentObserver(mHandler);
            getActivity().getContentResolver().registerContentObserver(SipProfile.ACCOUNT_STATUS_URI, true, statusObserver);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (statusObserver != null) {
            getActivity().getContentResolver().unregisterContentObserver(statusObserver);
            statusObserver = null;
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        ListView lv = getListView();
        lv.setItemChecked(position, true);

        curCheckPosition = id;
        Cursor c = (Cursor) getListAdapter().getItem(position - lv.getHeaderViewsCount());
        showDetails(id, c.getString(c.getColumnIndex(SipProfile.FIELD_WIZARD)));
    }

    /**
     * Helper function to show the details of a selected item, either by
     * displaying a fragment in-place in the current UI, or starting a
     * whole new activity in which it is displayed.
     */
    private void showDetails(long profileId, String wizard) {

        Intent intent = new Intent();
        //intent.setClass(getActivity(), AccountEdit.class);
        intent.setClass(getActivity(), BasePrefsWizard.class);
        if (profileId != SipProfile.INVALID_ID) {
            intent.putExtra(SipProfile.FIELD_ID, profileId);
        }
        intent.putExtra(SipProfile.FIELD_WIZARD, wizard);
        startActivity(intent);
    }


    @Override
    public void onToggleRow(AccountRowTag tag) {
        ContentValues cv = new ContentValues();
        cv.put(SipProfile.FIELD_ACTIVE, !tag.activated);
        getActivity().getContentResolver()
                .update(ContentUris.withAppendedId(SipProfile.ACCOUNT_ID_URI_BASE, tag.accountId), cv, null, null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), SipProfile.ACCOUNT_URI, new String[]{
                SipProfile.FIELD_ID + " AS " + BaseColumns._ID,
                SipProfile.FIELD_ID,
                SipProfile.FIELD_DISPLAY_NAME,
                SipProfile.FIELD_WIZARD,
                SipProfile.FIELD_ACTIVE
        }, null, null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        super.onLoadFinished(loader, data);
        // Select correct item if any
        updateCheckedItem();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {

            if (requestCode == CHOOSE_WIZARD) {
                // Wizard has been choosen, now create an account
                String wizardId = data.getStringExtra(WizardUtils.ID);
                if (wizardId != null) {
                    showDetails(SipProfile.INVALID_ID, wizardId);
                }
            } else if (requestCode == CHANGE_WIZARD) {
                // Change wizard done for this account.
                String wizardId = data.getStringExtra(WizardUtils.ID);
                long accountId = data.getLongExtra(Intent.EXTRA_UID, SipProfile.INVALID_ID);

                if (wizardId != null && accountId != SipProfile.INVALID_ID) {
                    ContentValues cv = new ContentValues();
                    cv.put(SipProfile.FIELD_WIZARD, wizardId);
                    getActivity().getContentResolver().update(
                            ContentUris.withAppendedId(SipProfile.ACCOUNT_ID_URI_BASE, accountId),
                            cv, null, null);

                }
            }
        }

    }


//    // Context menu stuff
//    // Activate / deactive menu
//    public static final int MENU_ITEM_ACTIVATE = Menu.FIRST;
//    // Modify the account
//    public static final int MENU_ITEM_MODIFY = Menu.FIRST + 1;
//    // Delete the account
//    public static final int MENU_ITEM_DELETE = Menu.FIRST + 2;
//    // Change the wizard of the account
//    public static final int MENU_ITEM_WIZARD = Menu.FIRST + 3;

//    /**
//     * Retrieve sip account from a given context menu info pressed
//     * @param cmi The context menu info to retrieve infos from
//     * @return corresponding sip profile if everything goes well, null if not able to retrieve profile
//     */
//    private SipProfile profileFromContextMenuInfo(ContextMenuInfo cmi) {
//        AdapterView.AdapterContextMenuInfo info;
//        try {
//            info = (AdapterView.AdapterContextMenuInfo) cmi;
//        } catch (ClassCastException e) {
//            Log.e(THIS_FILE, "bad menuInfo", e);
//            return null;
//        }
//        Cursor c = (Cursor) getListAdapter().getItem(info.position - getListView().getHeaderViewsCount());
//        if (c == null) {
//            // For some reason the requested item isn't available, do nothing
//            return null;
//        }
//        return new SipProfile(c);
//    }

//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        final SipProfile account = profileFromContextMenuInfo(menuInfo);
//        if (account == null) {
//            return;
//        }
//        WizardInfo wizardInfos = WizardUtils.getWizardClass(account.wizard);
//
//        // Setup the menu header
//        menu.setHeaderTitle(account.display_name);
//        if (wizardInfos != null) {
//            menu.setHeaderIcon(wizardInfos.icon);
//        }
//
//        menu.add(0, MENU_ITEM_ACTIVATE, 0, account.active ? R.string.deactivate_account
//                : R.string.activate_account);
//        menu.add(0, MENU_ITEM_MODIFY, 0, R.string.modify_account);
//        menu.add(0, MENU_ITEM_DELETE, 0, R.string.delete_account);
//        menu.add(0, MENU_ITEM_WIZARD, 0, R.string.choose_wizard);
//
//    }

//    @Override
//    public boolean onContextItemSelected(android.view.MenuItem item) {
//        final SipProfile account = profileFromContextMenuInfo(item.getMenuInfo());
//        if (account == null) {
//            // For some reason the requested item isn't available, do nothing
//            return super.onContextItemSelected(item);
//        }
//
//        switch (item.getItemId()) {
//            case MENU_ITEM_DELETE: {
//                getActivity().getContentResolver().delete(ContentUris.withAppendedId(SipProfile.ACCOUNT_ID_URI_BASE, account.id), null, null);
//                return true;
//            }
//            case MENU_ITEM_MODIFY: {
//                showDetails(account.id, account.wizard);
//                return true;
//            }
//            case MENU_ITEM_ACTIVATE: {
//                ContentValues cv = new ContentValues();
//                cv.put(SipProfile.FIELD_ACTIVE, !account.active);
//                getActivity().getContentResolver().update(ContentUris.withAppendedId(SipProfile.ACCOUNT_ID_URI_BASE, account.id), cv, null, null);
//                return true;
//            }
//            case MENU_ITEM_WIZARD: {
//                Intent it = new Intent(getActivity(), WizardChooser.class);
//                it.putExtra(Intent.EXTRA_UID, account.id);
//                startActivityForResult(it, CHANGE_WIZARD);
//                return true;
//            }
//        }
//        return super.onContextItemSelected(item);
//
//    }

    private void onClickAddAccount() {
        showDetails(SipProfile.INVALID_ID, "BASIC");
    }

    @Override
    public void changeCursor(Cursor c) {
        if (mAdapter != null) {
            mAdapter.changeCursor(c);
        }
    }


}
