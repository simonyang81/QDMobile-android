package com.qiyue.qdmobile.wizards.impl;

import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;

import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.api.SipConfigManager;
import com.qiyue.qdmobile.api.SipProfile;
import com.qiyue.qdmobile.utils.PreferencesWrapper;
import com.qiyue.qdmobile.wizards.utils.AccountCreationFirstView;
import com.qiyue.qdmobile.wizards.utils.AccountCreationFirstView.OnAccountCreationFirstViewListener;
import com.qiyue.qdmobile.wizards.utils.AccountCreationWebview;
import com.qiyue.qdmobile.wizards.utils.AccountCreationWebview.OnAccountCreationDoneListener;

public class VoipTel extends SimpleImplementation  implements OnAccountCreationDoneListener, OnAccountCreationFirstViewListener {

    private static final String webCreationPage = "http://212.4.110.135:8080/subscriber/newSubscriberFree/alta?execution=e2s1";

    private AccountCreationWebview extAccCreator;
    private AccountCreationFirstView firstView;

    private ViewGroup validationBar;
    private ViewGroup settingsContainer;

	@Override
	protected String getDomain() {
		return "voip.voiptel.ie";
	}
	
	@Override
	protected String getDefaultName() {
		return "Voiptel Mobile";
	}
	
	@Override
	protected boolean canTcp() {
		return false;
	}
	
	@Override
	public void fillLayout(SipProfile account) {
		super.fillLayout(account);

		accountUsername.getEditText().setInputType(InputType.TYPE_CLASS_PHONE);

        settingsContainer = (ViewGroup) parent.findViewById(R.id.settings_container);
        validationBar = (ViewGroup) parent.findViewById(R.id.validation_bar);
        extAccCreator = new AccountCreationWebview(parent, webCreationPage, this);
        
        updateAccountInfos(account);
	}
	
	@Override
	public void setDefaultParams(PreferencesWrapper prefs) {
		super.setDefaultParams(prefs);

		prefs.setCodecPriority("g729/8000/1", SipConfigManager.CODEC_NB, "240");
		prefs.setCodecPriority("g729/8000/1", SipConfigManager.CODEC_WB, "240");
	}
	
	@Override
	public boolean needRestart() {
		return true;
	}
	

    private void setFirstViewVisibility(boolean visible) {
        if(firstView != null) {
            firstView.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
        validationBar.setVisibility(visible ? View.GONE : View.VISIBLE);
        settingsContainer.setVisibility(visible ? View.GONE : View.VISIBLE);
    }
    
    private void updateAccountInfos(final SipProfile acc) {
        if (acc != null && acc.id != SipProfile.INVALID_ID) {
            setFirstViewVisibility(false);
        } else {
            if(firstView == null) {
                firstView = new AccountCreationFirstView(parent);
                ViewGroup globalContainer = (ViewGroup) settingsContainer.getParent();
                firstView.setOnAccountCreationFirstViewListener(this);
                globalContainer.addView(firstView);
            }
            setFirstViewVisibility(true);
        }
    }

    @Override
    public void onAccountCreationDone(String username, String password) {
        setUsername(username);
        setPassword(password);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAccountCreationDone(String username, String password, String extra) {
        onAccountCreationDone(username, password);
    }

    @Override
    public boolean saveAndQuit() {
        if(canSave()) {
            parent.saveAndFinish();
            return true;
        }
        return false;
    }

    @Override
    public void onCreateAccountRequested() {
        setFirstViewVisibility(false);
        extAccCreator.show();
    }

    @Override
    public void onEditAccountRequested() {
        setFirstViewVisibility(false);
    }
}
