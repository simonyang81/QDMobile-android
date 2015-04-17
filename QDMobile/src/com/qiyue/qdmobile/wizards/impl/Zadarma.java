package com.qiyue.qdmobile.wizards.impl;

import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.api.SipProfile;
import com.qiyue.qdmobile.utils.Log;
import com.qiyue.qdmobile.utils.MD5;
import com.qiyue.qdmobile.wizards.utils.AccountCreationFirstView;
import com.qiyue.qdmobile.wizards.utils.AccountCreationFirstView.OnAccountCreationFirstViewListener;
import com.qiyue.qdmobile.wizards.utils.AccountCreationWebview;
import com.qiyue.qdmobile.wizards.utils.AccountCreationWebview.OnAccountCreationDoneListener;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class Zadarma extends SimpleImplementation implements OnAccountCreationDoneListener, OnAccountCreationFirstViewListener {

    protected static final String THIS_FILE = "ZadarmaW";

    private static final String webCreationPage = "https://ss.zadarma.com/android/registration/";

    private LinearLayout customWizard;
    private TextView customWizardText;
    private AccountCreationWebview extAccCreator;

    private AccountCreationFirstView firstView;

    private ViewGroup validationBar;

    private ViewGroup settingsContainer;

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDomain() {
        return "Zadarma.com";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDefaultName() {
        return "Zadarma";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fillLayout(final SipProfile account) {
        super.fillLayout(account);
        accountUsername.getEditText().setInputType(InputType.TYPE_CLASS_TEXT);

        // Get wizard specific row
        customWizardText = (TextView) parent.findViewById(R.id.custom_wizard_text);
        customWizard = (LinearLayout) parent.findViewById(R.id.custom_wizard_row);

        settingsContainer = (ViewGroup) parent.findViewById(R.id.settings_container);
        validationBar = (ViewGroup) parent.findViewById(R.id.validation_bar);
        
        updateAccountInfos(account);

        extAccCreator = new AccountCreationWebview(parent, webCreationPage, this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean needRestart() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SipProfile buildAccount(SipProfile account) {
        account = super.buildAccount(account);
        return account;
    }

    /**
     * {@inheritDoc}
     */
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
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean saveAndQuit() {
        if (canSave()) {
            parent.saveAndFinish();
            return true;
        }
        return false;
    }

    private void setFirstViewVisibility(boolean visible) {
        if(firstView != null) {
            firstView.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
        validationBar.setVisibility(visible ? View.GONE : View.VISIBLE);
        settingsContainer.setVisibility(visible ? View.GONE : View.VISIBLE);
    }
    // Balance consult

    private void updateAccountInfos(final SipProfile acc) {
        if (acc != null && acc.id != SipProfile.INVALID_ID) {
            setFirstViewVisibility(false);
            customWizard.setVisibility(View.GONE);
            accountBalanceHelper.launchRequest(acc);
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

    private AccountBalanceHelper accountBalanceHelper = new AccountBalance(this);
    
    private static class AccountBalance extends AccountBalanceHelper {
        
        WeakReference<Zadarma> w;
        
        AccountBalance(Zadarma wizard){
            w = new WeakReference<Zadarma>(wizard);
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public HttpRequestBase getRequest(SipProfile acc) throws IOException {

            String requestURL = "https://ss.zadarma.com/android/getbalance/?"
                    + "login=" + acc.username
                    + "&code=" + MD5.MD5Hash(acc.data);

            return new HttpGet(requestURL);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String parseResponseLine(String line) {
            try {
                float value = Float.parseFloat(line.trim());
                if (value >= 0) {
                    return "Balance : " + Math.round(value * 100.0) / 100.0 + " USD";
                }
            } catch (NumberFormatException e) {
                Log.e(THIS_FILE, "Can't get value for line");
            }
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void applyResultError() {
            Zadarma wizard = w.get();
            if(wizard != null) {
                wizard.customWizard.setVisibility(View.GONE);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void applyResultSuccess(String balanceText) {
            Zadarma wizard = w.get();
            if(wizard != null) {
                wizard.customWizardText.setText(balanceText);
                wizard.customWizard.setVisibility(View.VISIBLE);
            }
        }

    };
    

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
