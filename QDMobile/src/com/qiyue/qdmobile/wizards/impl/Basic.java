package com.qiyue.qdmobile.wizards.impl;

import android.preference.EditTextPreference;

import com.github.snowdream.android.util.Log;
import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.api.SipProfile;
import com.qiyue.qdmobile.api.SipUri;
import com.qiyue.qdmobile.api.SipUri.ParsedSipContactInfos;
import com.qiyue.qdmobile.models.ZXingAccountJSON;

import java.util.HashMap;

public class Basic extends BaseImplementation {

	protected static final String THIS_FILE = Basic.class.getSimpleName();

	private EditTextPreference accountDisplayName;
	private EditTextPreference accountUserName;
	private EditTextPreference accountServer;
	private EditTextPreference accountPassword;

	private void bindFields() {
		accountDisplayName = (EditTextPreference) findPreference("display_name");
		accountUserName = (EditTextPreference) findPreference("username");
		accountServer = (EditTextPreference) findPreference("server");
		accountPassword = (EditTextPreference) findPreference("password");
	}
	
	public void fillLayout(final SipProfile account) {
		bindFields();
		
		accountDisplayName.setText(account.display_name);
		
		String serverFull = account.reg_uri;
		if (serverFull == null) {
			serverFull = "";
		} else {
			serverFull = serverFull.replaceFirst("sip:", "");
		}
		
		ParsedSipContactInfos parsedInfo = SipUri.parseSipContact(account.acc_id);		
		accountUserName.setText(parsedInfo.userName);
		accountServer.setText(serverFull);
		accountPassword.setText(account.data);
	}

	public void fillLayout(final ZXingAccountJSON accountJsonObj) {
		bindFields();

		accountDisplayName.setText(accountJsonObj.display);
		accountUserName.setText(accountJsonObj.account);
		accountServer.setText(accountJsonObj.sip);
		accountPassword.setText(accountJsonObj.passwd);
	}

	public void updateDescriptions() {
		setStringFieldSummary("display_name");
		setStringFieldSummary("username");
		setStringFieldSummary("server");
		setPasswordFieldSummary("password");
	}
	
	private static HashMap<String, Integer>SUMMARIES = new  HashMap<String, Integer>(){/**
		 * 
		 */
		private static final long serialVersionUID = -5743705263738203615L;

	{
		put("display_name", R.string.w_common_display_name_desc);
		put("username", R.string.w_basic_username_desc);
		put("server", R.string.w_common_server_desc);
		put("password", R.string.w_basic_password_desc);
	}};
	
	@Override
	public String getDefaultFieldSummary(String fieldName) {
		Integer res = SUMMARIES.get(fieldName);
		if(res != null) {
			return parent.getString( res );
		}
		return "";
	}

	public boolean canSave() {
		boolean isValid = true;
		
		isValid &= checkField(accountDisplayName, isEmpty(accountDisplayName));
		isValid &= checkField(accountPassword, isEmpty(accountPassword));
		isValid &= checkField(accountServer, isEmpty(accountServer));
		isValid &= checkField(accountUserName, isEmpty(accountUserName));

		return isValid;
	}

	public SipProfile buildAccount(SipProfile account) {
		Log.d(THIS_FILE, "begin of save ....");
		account.display_name = accountDisplayName.getText().trim();
		
		String[] serverParts = accountServer.getText().split(":");
		account.acc_id = "<sip:" + SipUri.encodeUser(accountUserName.getText().trim()) + "@"+serverParts[0].trim()+">";
		
		String regUri = "sip:" + accountServer.getText();
		account.reg_uri = regUri;
		account.proxies = new String[] { regUri } ;


		account.realm = "*";
		account.username = getText(accountUserName).trim();
		account.data = getText(accountPassword);
		account.scheme = SipProfile.CRED_SCHEME_DIGEST;
		account.datatype = SipProfile.CRED_DATA_PLAIN_PASSWD;
		//By default auto transport
		account.transport = SipProfile.TRANSPORT_UDP;
		return account;
	}

	@Override
	public int getBasePreferenceResource() {
		return R.xml.w_basic_preferences;
	}

	@Override
	public boolean needRestart() {
		return false;
	}
}
