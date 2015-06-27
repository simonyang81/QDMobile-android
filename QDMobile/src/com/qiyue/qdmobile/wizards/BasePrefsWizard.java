package com.qiyue.qdmobile.wizards;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.actionbarsherlock.view.Menu;
import com.github.snowdream.android.util.Log;
import com.google.gson.Gson;
import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.api.SipManager;
import com.qiyue.qdmobile.api.SipProfile;
import com.qiyue.qdmobile.db.DBProvider;
import com.qiyue.qdmobile.models.Filter;
import com.qiyue.qdmobile.models.ZXingAccountJSON;
import com.qiyue.qdmobile.ui.prefs.GenericPrefs;
import com.qiyue.qdmobile.utils.Constants;
import com.qiyue.qdmobile.utils.PreferencesWrapper;
import com.qiyue.qdmobile.wizards.WizardUtils.WizardInfo;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.List;
import java.util.UUID;

public class BasePrefsWizard extends GenericPrefs {
	
	public static final int SAVE_MENU 		= Menu.FIRST + 1;
	public static final int TRANSFORM_MENU 	= Menu.FIRST + 2;
	public static final int FILTERS_MENU 	= Menu.FIRST + 3;
	public static final int DELETE_MENU 	= Menu.FIRST + 4;

	private static final String THIS_FILE = BasePrefsWizard.class.getSimpleName();

	protected SipProfile account = null;
	private Button saveButton;
	private String wizardId = "";
	private WizardIface wizard = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Get back the concerned account and if any set the current (if not a
		// new account is created)
		Intent intent = getIntent();
		long accountId = intent.getLongExtra(SipProfile.FIELD_ID, SipProfile.INVALID_ID);

		// TODO : ensure this is not null...
		setWizardId(intent.getStringExtra(SipProfile.FIELD_WIZARD));

		account = SipProfile.getProfileFromDbId(this, accountId, DBProvider.ACCOUNT_FULL_PROJECTION);

		super.onCreate(savedInstanceState);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setTranslucentStatus(true);
		}
		SystemBarTintManager tintManager = new SystemBarTintManager(this);
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setTintColor(Color.parseColor(Constants.HOME_SCREEN_STATUS_BAR_COLOR));

		// Bind buttons to their actions
		Button bt = (Button) findViewById(R.id.cancel_bt);
		bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED, getIntent());
				finish();
			}
		});

		saveButton = (Button) findViewById(R.id.save_bt);
		saveButton.setEnabled(false);
		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveAndFinish();
			}
		});

		String accountJSON = intent.getStringExtra(Constants.EXTRA_ACCOUNT_JSON);

		if (TextUtils.isEmpty(accountJSON) == false) {
			Gson gson = new Gson();
			ZXingAccountJSON accountJsonObj = gson.fromJson(accountJSON, ZXingAccountJSON.class);
			wizard.fillLayout(accountJsonObj);
		} else {
			wizard.fillLayout(account);
		}

	}

	@TargetApi(19)
	protected void setTranslucentStatus(boolean on) {
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}

	private boolean isResumed = false;
	@Override
	protected void onResume() {
		super.onResume();
        isResumed = true;
		updateDescriptions();
		updateValidation();
		wizard.onStart();
	}
	
	@Override
	protected void onPause() {
	    super.onPause();
	    isResumed = false;
	    wizard.onStop();
	}
	
	private boolean setWizardId(String wId) {
		if (wizardId == null) {
			return setWizardId(WizardUtils.EXPERT_WIZARD_TAG);
		}

		WizardInfo wizardInfo = WizardUtils.getWizardClass(wId);
		if (wizardInfo == null) {
			if (!wizardId.equals(WizardUtils.EXPERT_WIZARD_TAG)) {
				return setWizardId(WizardUtils.EXPERT_WIZARD_TAG);
			}
			return false;
		}

		try {
			wizard = (WizardIface) wizardInfo.classObject.newInstance();
		} catch (IllegalAccessException e) {
			Log.e(THIS_FILE, "Can't access wizard class", e);
			if (!wizardId.equals(WizardUtils.EXPERT_WIZARD_TAG)) {
				return setWizardId(WizardUtils.EXPERT_WIZARD_TAG);
			}
			return false;
		} catch (InstantiationException e) {
			Log.e(THIS_FILE, "Can't access wizard class", e);
			if (!wizardId.equals(WizardUtils.EXPERT_WIZARD_TAG)) {
				return setWizardId(WizardUtils.EXPERT_WIZARD_TAG);
			}
			return false;
		}
		wizardId = wId;
		wizard.setParent(this);
//		if (getSupportActionBar() != null) {
//		    getSupportActionBar().setIcon(WizardUtils.getWizardIconRes(wizardId));
//		}
		return true;
	}

	@Override
	protected void beforeBuildPrefs() {
		// Use our custom wizard view
		setContentView(R.layout.wizard_prefs_base);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
	    if (isResumed) {
    		updateDescriptions();
    		updateValidation();
	    }
	}

	/**
	 * Update validation state of the current activity.
	 * It will check if wizard can be saved and if so 
	 * will enable button
	 */
	public void updateValidation() {
		saveButton.setEnabled(wizard.canSave());
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(SAVE_MENU).setVisible(wizard.canSave());

		return super.onPrepareOptionsMenu(menu);
	}

    private static final int CHOOSE_WIZARD = 0;
    private static final int MODIFY_FILTERS = CHOOSE_WIZARD + 1;
    
    private static final int FINAL_ACTIVITY_CODE = MODIFY_FILTERS;
    
    private int currentActivityCode = FINAL_ACTIVITY_CODE;
    public int getFreeSubActivityCode() {
        currentActivityCode ++;
        return currentActivityCode;
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CHOOSE_WIZARD && resultCode == RESULT_OK && data != null && data.getExtras() != null) {
			String wizardId = data.getStringExtra(WizardUtils.ID);
			if (wizardId != null) {
				saveAccount(wizardId);
				setResult(RESULT_OK, getIntent());
				finish();
			}
		}
		
		if (requestCode > FINAL_ACTIVITY_CODE) {
		    wizard.onActivityResult(requestCode, resultCode, data);
		}
	}

	/**
	 * Save account and end the activity
	 */
	public void saveAndFinish() {
		saveAccount();
		Intent intent = getIntent();
		setResult(RESULT_OK, intent);
		finish();
	}

	/*
	 * Save the account with current wizard id
	 */
	private void saveAccount() {
		saveAccount(wizardId);
	}
	
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    getSharedPreferences(WIZARD_PREF_NAME, MODE_PRIVATE).edit().clear().commit();
	}
	
	/**
	 * Save the account with given wizard id
	 * @param wizardId the wizard to use for account entry
	 */
	private void saveAccount(String wizardId) {
		boolean needRestart = false;

		PreferencesWrapper prefs = new PreferencesWrapper(getApplicationContext());
		account = wizard.buildAccount(account);
		account.wizard = wizardId;
		if (account.id == SipProfile.INVALID_ID) {
			// This account does not exists yet
		    prefs.startEditing();
			wizard.setDefaultParams(prefs);
			prefs.endEditing();
			applyNewAccountDefault(account);
			Uri uri = getContentResolver().insert(SipProfile.ACCOUNT_URI, account.getDbContentValues());
			
			// After insert, add filters for this wizard 
			account.id = ContentUris.parseId(uri);
			List<Filter> filters = wizard.getDefaultFilters(account);
			if (filters != null) {
				for (Filter filter : filters) {
					// Ensure the correct id if not done by the wizard
					filter.account = (int) account.id;
					getContentResolver().insert(SipManager.FILTER_URI, filter.getDbContentValues());
				}
			}
			// Check if we have to restart
			needRestart = wizard.needRestart();

		} else {
			// TODO : should not be done there but if not we should add an
			// option to re-apply default params
            prefs.startEditing();
			wizard.setDefaultParams(prefs);
            prefs.endEditing();
			getContentResolver().update(ContentUris.withAppendedId(SipProfile.ACCOUNT_ID_URI_BASE, account.id),
					account.getDbContentValues(), null, null);
		}

		// Mainly if global preferences were changed, we have to restart sip stack 
		if (needRestart) {
			Intent intent = new Intent(SipManager.ACTION_SIP_REQUEST_RESTART);
			sendBroadcast(intent);
		}
	}

	/**
	 * Apply default settings for a new account to check very basic coherence of settings and auto-modify settings missing
     * @param account
     */
    private void applyNewAccountDefault(SipProfile account) {
        if (account.use_rfc5626) {
            if (TextUtils.isEmpty(account.rfc5626_instance_id)) {
                String autoInstanceId = (UUID.randomUUID()).toString();
                account.rfc5626_instance_id = "<urn:uuid:"+autoInstanceId+">";
            }
        }
    }

    @Override
	protected int getXmlPreferences() {
		return wizard.getBasePreferenceResource();
	}

	@Override
	protected void updateDescriptions() {
		wizard.updateDescriptions();
	}

	@Override
	protected String getDefaultFieldSummary(String fieldName) {
		return wizard.getDefaultFieldSummary(fieldName);
	}
	
	private static final String WIZARD_PREF_NAME = "Wizard";
	
	@Override
	public SharedPreferences getSharedPreferences(String name, int mode) {
	    return super.getSharedPreferences(WIZARD_PREF_NAME, mode);
	}

}
