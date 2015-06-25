package com.qiyue.qdmobile.ui.account;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.api.SipProfile;
import com.qiyue.qdmobile.utils.AccountListUtils;
import com.qiyue.qdmobile.utils.AccountListUtils.AccountStatusDisplay;
import com.qiyue.qdmobile.wizards.WizardUtils;
import com.qiyue.qdmobile.wizards.WizardUtils.WizardInfo;

public class AccountsEditListAdapter extends SimpleCursorAdapter implements OnClickListener {

    public static final class AccountListItemViews {
        public TextView labelView;
        public TextView statusView;
        public View indicator;
        public View grabber;
        public CheckBox activeCheckbox;
        public ImageView barOnOff;
    }

    private boolean draggable = false;

    public static final class AccountRowTag {
        public long accountId;
        public boolean activated;
    }

    private OnCheckedRowListener checkListener;

    public interface OnCheckedRowListener {
        void onToggleRow(AccountRowTag tag);
    }

    private static final String THIS_FILE = "AccEditListAd";

    public AccountsEditListAdapter(Context context, Cursor c) {
        super(context,
                R.layout.accounts_edit_list_item, c,
                new String[] {
                        SipProfile.FIELD_DISPLAY_NAME
                },
                new int[] {
                        R.id.AccTextView
                }, 0);
    }

    public void setOnCheckedRowListener(OnCheckedRowListener l) {
        checkListener = l;
    }

    private AccountListItemViews tagRowView(View view) {
        AccountListItemViews tagView = new AccountListItemViews();
        tagView.labelView = (TextView) view.findViewById(R.id.AccTextView);
        tagView.indicator = view.findViewById(R.id.indicator);
        tagView.grabber = view.findViewById(R.id.grabber);
        tagView.activeCheckbox = (CheckBox) view.findViewById(R.id.AccCheckBoxActive);
        tagView.statusView = (TextView) view.findViewById(R.id.AccTextStatusView);
        tagView.barOnOff = (ImageView) tagView.indicator.findViewById(R.id.bar_onoff);

        view.setTag(tagView);

        tagView.indicator.setOnClickListener(this);

        return tagView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);

        AccountListItemViews tagView = (AccountListItemViews) view.getTag();
        if (tagView == null) {
            tagView = tagRowView(view);
        }

        // Get the view object and account object for the row
        final SipProfile account = new SipProfile(cursor);
        AccountRowTag tagIndicator = new AccountRowTag();
        tagIndicator.accountId = account.id;
        tagIndicator.activated = account.active;
        tagView.indicator.setTag(tagIndicator);

        tagView.indicator.setVisibility(draggable ? View.GONE : View.VISIBLE);
        tagView.grabber.setVisibility(draggable ? View.VISIBLE : View.GONE);

        // Get the status of this profile

        tagView.labelView.setText(account.display_name);

        // Update status label and color
        if (account.active) {
            AccountStatusDisplay accountStatusDisplay = AccountListUtils.getAccountDisplay(context,
                    account.id);
            tagView.statusView.setText(accountStatusDisplay.statusLabel);
            tagView.labelView.setTextColor(accountStatusDisplay.statusColor);

            // Update checkbox selection
            tagView.activeCheckbox.setChecked(true);
            tagView.barOnOff.setImageResource(accountStatusDisplay.checkBoxIndicator);

        } else {
            tagView.statusView.setText(R.string.acct_inactive);
            tagView.labelView.setTextColor(mContext.getResources().getColor(
                    R.color.account_inactive));

            // Update checkbox selection
            tagView.activeCheckbox.setChecked(false);
            tagView.barOnOff.setImageResource(R.drawable.ic_indicator_off);

        }

        // Update account image
        final WizardInfo wizardInfos = WizardUtils.getWizardClass(account.wizard);
        if (wizardInfos != null) {
            tagView.activeCheckbox.setBackgroundResource(wizardInfos.icon);
        }
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (checkListener != null && tag != null) {
            checkListener.onToggleRow((AccountRowTag) tag);
        }
    }

    /**
     * Set draggable mode of the adapter Ie show or hide the grabber icon
     * 
     * @param aDraggable if true we enter dragging mode
     */
    public void setDraggable(boolean aDraggable) {
        draggable = aDraggable;
        notifyDataSetChanged();
    }

    /**
     * Toggle dragable mode
     * 
     */
    public void toggleDraggable() {
        setDraggable(!draggable);
    }

    /**
     * Get draggable mode of the adapter
     * 
     * @return true if in dragging mode
     */
    public boolean isDraggable() {
        return draggable;
    }

}
