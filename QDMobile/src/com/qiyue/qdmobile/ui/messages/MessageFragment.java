package com.qiyue.qdmobile.ui.messages;

import android.app.Activity;
import android.content.*;
import android.database.Cursor;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;

import com.github.snowdream.android.util.Log;
import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.api.ISipService;
import com.qiyue.qdmobile.api.SipMessage;
import com.qiyue.qdmobile.api.SipProfile;
import com.qiyue.qdmobile.api.SipUri;
import com.qiyue.qdmobile.models.CallerInfo;
import com.qiyue.qdmobile.service.SipNotifications;
import com.qiyue.qdmobile.service.SipService;
import com.qiyue.qdmobile.ui.PickupSipUri;
import com.qiyue.qdmobile.utils.AccountUtils;
import com.qiyue.qdmobile.utils.Constants;
import com.qiyue.qdmobile.utils.SmileyParser;
import com.qiyue.qdmobile.utils.clipboard.ClipboardWrapper;

public class MessageFragment extends ListFragment implements
                                                    LoaderManager.LoaderCallbacks<Cursor>,
                                                    OnClickListener {

    private static final String THIS_FILE = MessageFragment.class.getSimpleName();

    private String remoteFrom;
    private TextView fullFromText;
    private EditText bodyInput;
    private Button sendButton;
    private SipNotifications notifications;
    private MessageAdapter mAdapter;

    public interface OnQuitListener {
        void onQuit();
    }

    private OnQuitListener quitListener;
    private ClipboardWrapper clipboardManager;

    public void setOnQuitListener(OnQuitListener l) {
        quitListener = l;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        getListView().setOnCreateContextMenuListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SmileyParser.init(getActivity());
        notifications = new SipNotifications(getActivity());
        clipboardManager = ClipboardWrapper.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.i(THIS_FILE, "onCreateView()...");

        View v = inflater.inflate(R.layout.compose_message_activity, container, false);

        fullFromText = (TextView) v.findViewById(R.id.subject);
        bodyInput = (EditText) v.findViewById(R.id.embedded_text_editor);
        sendButton = (Button) v.findViewById(R.id.send_button);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setDivider(null);
        sendButton.setOnClickListener(this);

        // Setup from args
        String from = getArguments().getString(SipMessage.FIELD_FROM);
        String fullFrom = getArguments().getString(SipMessage.FIELD_FROM_FULL);
        if (fullFrom == null) {
            fullFrom = from;
        }
        setupFrom(from, fullFrom);
        if (remoteFrom == null) {
            chooseSipUri();
        }

        mAdapter = new MessageAdapter(getActivity(), null);
        getListView().setAdapter(mAdapter);
        mAdapter.setFullFrom(fullFrom);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        getActivity().bindService(new Intent(getActivity(), SipService.class), connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDetach() {
        try {
            getActivity().unbindService(connection);
        } catch (Exception e) {
            // Just ignore that
        }
        service = null;
        super.onDetach();
    }

    @Override
    public void onResume() {
        Log.d(THIS_FILE, "Resume compose message act");
        super.onResume();
        notifications.setViewingMessageFrom(remoteFrom);
    }

    @Override
    public void onPause() {
        super.onPause();
        notifications.setViewingMessageFrom(null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(THIS_FILE, "On activity result " + requestCode);
        if (requestCode == Constants.PICKUP_SIP_URI) {
            if (resultCode == Activity.RESULT_OK) {
                String from = data.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                setupFrom(from, from);
            }
            if (TextUtils.isEmpty(remoteFrom)) {
                if (quitListener != null) {
                    quitListener.onQuit();
                }
            } else {
                loadMessageContent();
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // Service connection
    private ISipService service;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            service = ISipService.Stub.asInterface(arg1);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            service = null;
        }
    };

    private void loadMessageContent() {
        getLoaderManager().restartLoader(0, getArguments(), this);

        String from = getArguments().getString(SipMessage.FIELD_FROM);

        if (!TextUtils.isEmpty(from)) {
            ContentValues args = new ContentValues();
            args.put(SipMessage.FIELD_READ, true);
            getActivity().getContentResolver().update(SipMessage.MESSAGE_URI, args,
                    SipMessage.FIELD_FROM + "=?", new String[]{
                            from
                    });
        }
    }

    public static Bundle getArguments(String from, String fromFull) {
        Bundle bundle = new Bundle();
        if (from != null) {
            bundle.putString(SipMessage.FIELD_FROM, from);
            bundle.putString(SipMessage.FIELD_FROM_FULL, fromFull);
        }

        return bundle;
    }


    private void setupFrom(String from, String fullFrom) {
        Log.d(THIS_FILE, "setupFrom(), from: " + from + "; fullFrom: " + fullFrom);
        if (from != null) {
            if (remoteFrom != from) {
                remoteFrom = from;

                CallerInfo callerInfo = CallerInfo.getCallerInfoFromSipUri(getActivity(), fullFrom);
                if (callerInfo != null && callerInfo.contactExists) {
                    fullFromText.setText(callerInfo.name);
                } else {
                    fullFromText.setText(SipUri.getDisplayedSimpleContact(fullFrom));
                }
                loadMessageContent();
                notifications.setViewingMessageFrom(remoteFrom);
            }
        }
    }

    private void chooseSipUri() {
        Intent pickupIntent = new Intent(getActivity(), PickupSipUri.class);
        startActivityForResult(pickupIntent, Constants.PICKUP_SIP_URI);
    }

    private void sendMessage() {

        if (service == null) {
            Log.e(THIS_FILE, "sendMessage(), the service is null...");
            return;
        }

        try {
            SipProfile acc = AccountUtils.getAccount();

            if (acc != null && acc.id != SipProfile.INVALID_ID) {
                try {
                    String textToSend = bodyInput.getText().toString();
                    if (!TextUtils.isEmpty(textToSend)) {
                        service.sendMessage(textToSend, remoteFrom, (int) acc.id);
                        bodyInput.getText().clear();
                    }
                } catch (RemoteException e) {
                    Log.e(THIS_FILE, "Not able to send message");
                }
            }

        } catch (Exception e) {
            Log.e(THIS_FILE, "sendMessage(), " + e.toString());
        }

    }

    @Override
    public void onClick(View v) {
        int clickedId = v.getId();
        if (clickedId == R.id.subject) {
            chooseSipUri();
        } else if (clickedId == R.id.send_button) {
            sendMessage();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Builder toLoadUriBuilder = SipMessage.THREAD_ID_URI_BASE.buildUpon().appendEncodedPath(remoteFrom.replaceAll("/", "%2F"));
        return new CursorLoader(getActivity(), toLoadUriBuilder.build(), null, null, null,
                SipMessage.FIELD_DATE + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    public static final int MENU_COPY = ContextMenu.FIRST;

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        menu.add(0, MENU_COPY, 0, R.string.copy_message_text);
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Cursor c = (Cursor) mAdapter.getItem(info.position);
        if (c != null) {
            SipMessage msg = new SipMessage(c);
            switch (item.getItemId()) {
                case MENU_COPY: {
                    clipboardManager.setText(msg.getDisplayName(), msg.getBody());
                    break;
                }
                default:
                    break;
            }

        }
        return super.onContextItemSelected(item);
    }

}
