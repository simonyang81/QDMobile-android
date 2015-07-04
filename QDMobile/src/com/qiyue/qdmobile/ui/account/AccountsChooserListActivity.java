package com.qiyue.qdmobile.ui.account;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.ui.account.AccountsChooserListFragment.OnAccountClickListener;

public abstract class AccountsChooserListActivity extends FragmentActivity implements OnAccountClickListener {
    
    private AccountsChooserListFragment listFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accounts_chooser_view);
        
        listFragment = (AccountsChooserListFragment) getSupportFragmentManager().findFragmentById(R.id.listFragment);
        
        listFragment.setShowCallHandlerPlugins(showInternalAccounts());
        listFragment.setOnAccountClickListener(this);
    }

    /**
     * Should this activity propose external accounts?
     * @return true if the activity should show external plugin handlers
     */
    protected boolean showInternalAccounts() {
        return false;
    }

}
