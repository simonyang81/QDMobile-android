package com.qiyue.qdmobile.ui.incall;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.github.snowdream.android.util.Log;
import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.widgets.Dialpad;
import com.qiyue.qdmobile.widgets.Dialpad.OnDialKeyListener;

public class DtmfDialogFragment extends SherlockDialogFragment implements OnDialKeyListener {

    private static final String THIS_FILE = "DtmfDialogFragment";

    private static final String EXTRA_CALL_ID = "call_id";
    private TextView dialPadTextView;

    public static DtmfDialogFragment newInstance(int callId) {
        DtmfDialogFragment instance = new DtmfDialogFragment();
        Bundle args = new Bundle();
        args.putInt(EXTRA_CALL_ID, callId);
        instance.setArguments(args);
        return instance;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new AlertDialog.Builder(getActivity())
                .setView(getCustomView(getActivity().getLayoutInflater(), null, savedInstanceState))
                .setCancelable(true)
                .setNeutralButton(R.string.done, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .create();
    }


    public View getCustomView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.in_call_dialpad, container, false);

        Dialpad dialPad = (Dialpad) v.findViewById(R.id.dialPad);
        //dialPad.setForceWidth(true);
        dialPad.setOnDialKeyListener(this);
        dialPadTextView = (TextView) v.findViewById(R.id.digitsText);
        return v;
    }

    public interface OnDtmfListener {
        void OnDtmf(int callId, int keyCode, int dialTone);
    }

    @Override
    public void onTrigger(int keyCode, int dialTone) {
        if (dialPadTextView != null) {
            // Update text view
            KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
            char nbr = event.getNumber();
            StringBuilder sb = new StringBuilder(dialPadTextView.getText());
            sb.append(nbr);
            dialPadTextView.setText(sb.toString());
        }
        if (getSherlockActivity() instanceof OnDtmfListener) {
            Integer callId = getArguments().getInt(EXTRA_CALL_ID);
            if (callId != null) {
                ((OnDtmfListener) getSherlockActivity()).OnDtmf(callId, keyCode, dialTone);
            } else {
                Log.w(THIS_FILE, "Impossible to find the call associated to this view");
            }
        }

    }


}
