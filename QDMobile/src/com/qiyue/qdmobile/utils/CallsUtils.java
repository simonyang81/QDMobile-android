package com.qiyue.qdmobile.utils;

import android.content.Context;

import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.api.SipCallSession;

public class CallsUtils {
	/**
	 * Get the corresponding string for a given state
	 * Can be used to translate or debug current state
	 * @return the string reprensenting this call info state
	 */
	public static final String getStringCallState(SipCallSession session, Context context) {

		int callState = session.getCallState();
		switch(callState) {
		case SipCallSession.InvState.CALLING:
			return context.getString(R.string.call_state_calling);
		case SipCallSession.InvState.CONFIRMED:
			return context.getString(R.string.call_state_confirmed);
		case SipCallSession.InvState.CONNECTING:
			return context.getString(R.string.call_state_connecting);
		case SipCallSession.InvState.DISCONNECTED:
			return context.getString(R.string.call_state_disconnected);
		case SipCallSession.InvState.EARLY:
			return context.getString(R.string.call_state_early);
		case SipCallSession.InvState.INCOMING:
			return context.getString(R.string.call_state_incoming);
		case SipCallSession.InvState.NULL:
			return context.getString(R.string.call_state_null);
		}
		
		return "";
	}
}
