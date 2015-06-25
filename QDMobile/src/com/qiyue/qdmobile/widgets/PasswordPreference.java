package com.qiyue.qdmobile.widgets;

import android.content.Context;
import android.preference.EditTextPreference;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.CheckBox;
import android.widget.EditText;

import com.github.snowdream.android.util.Log;
import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.utils.Compatibility;

public class PasswordPreference extends EditTextPreference implements OnClickListener, TextWatcher {

    private static final String THIS_FILE = "PasswordPreference";
    private CheckBox showPwdCheckbox;

    private boolean canShowPassword = false;

    public PasswordPreference(Context context) {
        this(context, null);
    }

    public PasswordPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAddEditTextToDialogView(View dialogView, EditText editText) {
        super.onAddEditTextToDialogView(dialogView, editText);
        editText.addTextChangedListener(this);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        try {
            if (showPwdCheckbox == null) {
                showPwdCheckbox = new CheckBox(getContext());
                showPwdCheckbox.setText(R.string.show_password);
                showPwdCheckbox.setOnClickListener(this);
            }

            canShowPassword = TextUtils.isEmpty(getText());
            getEditText().setInputType(
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            updateCanShowPassword();
            ViewParent oldParent = showPwdCheckbox.getParent();
            if (oldParent != view) {
                if (oldParent != null) {
                    ((ViewGroup) oldParent).removeView(showPwdCheckbox);
                }
            }

            ViewGroup container = (ViewGroup) view;
            if (Compatibility.isCompatible(8)) {
                container = (ViewGroup) container.getChildAt(0);
            }
            if (container != null) {
                container.addView(showPwdCheckbox, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        } catch (Exception e) {
            // Just do nothing in case weird ROM in use
            Log.w(THIS_FILE, "Unsupported device for enhanced password", e);
        }
    }

    @Override
    public void onClick(View view) {
        if (!canShowPassword) {
            // Even if not shown, be very very sure we never come here
            return;
        }
        getEditText().setInputType(
                InputType.TYPE_CLASS_TEXT | (((CheckBox) view).isChecked() ?
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                        InputType.TYPE_TEXT_VARIATION_PASSWORD));
    }

    @Override
    public void setText(String text) {
        super.setText(text);
        setCanShowPassword(TextUtils.isEmpty(text));
    }

    private void updateCanShowPassword() {
        if (showPwdCheckbox != null) {
            showPwdCheckbox.setVisibility(canShowPassword ? View.VISIBLE : View.GONE);
        }
    }

    private void setCanShowPassword(boolean canShow) {
        canShowPassword = canShow;
        updateCanShowPassword();
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() == 0) {
            setCanShowPassword(true);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Nothing to do

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // Nothing to do
    }

}
