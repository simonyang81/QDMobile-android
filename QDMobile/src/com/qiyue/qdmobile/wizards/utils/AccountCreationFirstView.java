package com.qiyue.qdmobile.wizards.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.qiyue.qdmobile.R;

public class AccountCreationFirstView extends RelativeLayout implements OnClickListener {

    private OnAccountCreationFirstViewListener mListener= null;

    public AccountCreationFirstView(Context context) {
        this(context, null);
    }

    public AccountCreationFirstView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.wizard_create_or_edit, this, true);

        bindElements();
    }
    
    private void bindElements() {
        findViewById(R.id.button0).setOnClickListener(this);
        findViewById(R.id.button1).setOnClickListener(this);
    }

    public AccountCreationFirstView(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }
    
    @Override
    public void onClick(View v) {
        if(mListener == null) {
            return;
        }
        int id = v.getId();
        if(id == R.id.button0) {
            mListener.onCreateAccountRequested();
        }else if(id == R.id.button1) {
            mListener.onEditAccountRequested();
        }
    }
    
    public void setOnAccountCreationFirstViewListener(OnAccountCreationFirstViewListener listener) {
        mListener = listener;
    }
    
    /**
     * Interface for listeners of {@link AccountCreationFirstView} 
     * see {@link AccountCreationFirstView#setOnAccountCreationFirstViewListener}
     */
    public interface OnAccountCreationFirstViewListener {
        /**
         * User asked to create the account
         */
        void onCreateAccountRequested();
        /**
         * User asked to edit : he has an existing account
         */
        void onEditAccountRequested();
    }

}
