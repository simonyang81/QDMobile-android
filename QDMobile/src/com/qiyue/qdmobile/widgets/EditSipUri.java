package com.qiyue.qdmobile.widgets;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.github.snowdream.android.util.Log;
import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.api.SipProfile;
import com.qiyue.qdmobile.models.Filter;
import com.qiyue.qdmobile.utils.AccountUtils;
import com.qiyue.qdmobile.utils.contacts.ContactsSearchAdapter;

import java.util.regex.Pattern;

public class EditSipUri extends LinearLayout implements TextWatcher, OnItemClickListener {

    protected static final String THIS_FILE = "EditSipUri";
    private AutoCompleteTextView dialUser;
    private ListView completeList;
    private ContactsSearchAdapter contactsAdapter;

    public EditSipUri(Context context, AttributeSet attrs) {
        super(context, attrs);
        setGravity(Gravity.CENTER_HORIZONTAL);
        setOrientation(VERTICAL);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.edit_sip_uri, this, true);

        dialUser = (AutoCompleteTextView) findViewById(R.id.dialtxt_user);
        completeList = (ListView) findViewById(R.id.autoCompleteList);

        contactsAdapter = new ContactsSearchAdapter(getContext(), dialUser);

        updateDialTextHelper();
        long accId = SipProfile.INVALID_ID;
        SipProfile account = AccountUtils.getAccount();
        if (account != null) {
            accId = account.id;
        }
        if (contactsAdapter != null) {
            contactsAdapter.setSelectedAccount(accId);
        }

        dialUser.addTextChangedListener(this);

    }

    /* (non-Javadoc)
     * @see android.view.View#onAttachedToWindow()
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (isInEditMode()) {
            // Don't bind cursor in this case
            return;
        }


        completeList.setAdapter(contactsAdapter);
        completeList.setOnItemClickListener(this);

//        autoCompleteAdapter = new ContactsAutocompleteAdapter(getContext());
//        dialUser.setAdapter(autoCompleteAdapter);
    }

    /* (non-Javadoc)
     * @see android.view.View#onDetachedFromWindow()
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (isInEditMode()) {
            // Don't bind cursor in this case
            return;
        }
        if (contactsAdapter != null) {
            contactsAdapter.changeCursor(null);
        }
//        if(autoCompleteAdapter != null) {
//            autoCompleteAdapter.changeCursor(null);
//        }
    }

    public class ToCall {
        private Long accountId;
        private String callee;

        public ToCall(Long acc, String uri) {
            accountId = acc;
            callee = uri;
        }

        /**
         * @return the pjsipAccountId
         */
        public Long getAccountId() {
            return accountId;
        }

        /**
         * @return the callee
         */
        public String getCallee() {
            return callee;
        }
    }

    private void updateDialTextHelper() {

        String dialUserValue = dialUser.getText().toString();
        if (contactsAdapter != null) {
            contactsAdapter.setSelectedText(dialUserValue);
        }

//        accountChooserButtonText.setChangeable(TextUtils.isEmpty(dialUserValue));
//        SipProfile acc = accountChooserButtonText.getSelectedAccount();
//        if (!Pattern.matches(".*@.*", dialUserValue) && acc != null
//                && acc.id > SipProfile.INVALID_ID) {
//            domainTextHelper.setText("@" + acc.getDefaultDomain());
//        } else {
//            domainTextHelper.setText("");
//        }

    }

    /**
     * Retrieve the value of the selected sip uri
     *
     * @return the contact to call as a ToCall object containing account to use
     * and number to call
     */
    public ToCall getValue() {
        String userName = dialUser.getText().toString();
        String toCall = "";
        Long accountToUse = null;
        if (TextUtils.isEmpty(userName)) {
            return null;
        }
        userName = userName.replaceAll("[ \t]", "");
        SipProfile acc = AccountUtils.getAccount();
        if (acc != null) {
            accountToUse = acc.id;
            // If this is a sip account
            if (accountToUse > SipProfile.INVALID_ID) {
                if (Pattern.matches(".*@.*", userName)) {
                    toCall = "sip:" + userName + "";
                } else if (!TextUtils.isEmpty(acc.getDefaultDomain())) {
                    toCall = "sip:" + userName + "@" + acc.getDefaultDomain();
                } else {
                    toCall = "sip:" + userName;
                }
            } else {
                toCall = userName;
            }
        } else {
            toCall = userName;
        }

        return new ToCall(accountToUse, toCall);
    }

//    public SipProfile getSelectedAccount() {
//        return accountChooserButtonText.getSelectedAccount();
//    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }

    @Override
    public void onTextChanged(CharSequence newText, int arg1, int arg2, int arg3) {
        updateDialTextHelper();
    }

    @Override
    public void afterTextChanged(Editable s) {
        updateDialTextHelper();
    }

    /**
     * Reset content of the field
     *
     * @see Editable#clear()
     */
    public void clear() {
        dialUser.getText().clear();
    }

    /**
     * Set the content of the field
     *
     * @param number The new content to set in the field
     */
    public void setTextValue(String number) {
        clear();
        dialUser.getText().append(number);
    }

    /**
     * Retrieve the underlying text field of this widget to modify it's behavior directly
     *
     * @return the underlying widget
     */
    public EditText getTextField() {
        return dialUser;
    }


    @Override
    public void onItemClick(AdapterView<?> ad, View view, int position, long arg3) {
        String number = (String) view.getTag();

        Log.d(THIS_FILE, "position: " + position + "; number: " + number);

        SipProfile account = AccountUtils.getAccount();
        String rewritten = Filter.rewritePhoneNumber(getContext(), account.id, number);
        setTextValue(rewritten);
        Log.d(THIS_FILE, "Clicked contact " + number);
    }

//    public void setShowExternals(boolean b) {
//        accountChooserButtonText.setShowExternals(b);
//    }
}
