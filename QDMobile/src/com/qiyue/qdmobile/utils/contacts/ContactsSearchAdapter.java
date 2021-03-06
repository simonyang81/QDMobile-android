package com.qiyue.qdmobile.utils.contacts;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.support.v4.widget.CursorAdapter;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.AutoCompleteTextView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.github.snowdream.android.util.Log;
import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.api.SipProfile;
import com.qiyue.qdmobile.models.Filter;
import com.qiyue.qdmobile.utils.AccountUtils;

/**
 * This adapter is used to filter contacts on both name and number.
 */
public class ContactsSearchAdapter extends CursorAdapter implements SectionIndexer {

    private static final String TAG = ContactsSearchAdapter.class.getSimpleName();

    private final Context mContext;
    private long currentAccId = SipProfile.INVALID_ID;
    AlphabetIndexer alphaIndexer;
    private String currentFilter = "";

    private CharacterStyle boldStyle = new StyleSpan(android.graphics.Typeface.BOLD);
    private CharacterStyle highlightStyle = new ForegroundColorSpan(0xFF33B5E5);

    private AutoCompleteTextView mDialUser;

    public ContactsSearchAdapter(Context context, AutoCompleteTextView dialUser) {
        super(context, null, false);
        mContext = context;
        mDialUser = dialUser;
    }

    public final void setSelectedAccount(long accId) {
        currentAccId = accId;
    }

    public final void setSelectedText(String txt) {
        if (!TextUtils.isEmpty(txt)) {
            currentFilter = txt.toLowerCase();
        } else {
            currentFilter = "";
        }
        if (TextUtils.isEmpty(txt) || txt.length() >= 2) {
            getFilter().filter(txt);
        } else {
            currentFilter = "";
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.search_contact_list_item, parent, false);
    }

    private boolean highlightTextViewSearch(TextView tv) {
        if (currentFilter.length() > 0) {
            String value = tv.getText().toString();

            Log.d(TAG, "highlightTextViewSearch(), value : " + value);

            int foundIdx = value.toLowerCase().indexOf(currentFilter);
            if (foundIdx >= 0) {
                SpannableString spn = new SpannableString(value);
                spn.setSpan(boldStyle, foundIdx, foundIdx + currentFilter.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spn.setSpan(highlightStyle, foundIdx, foundIdx + currentFilter.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv.setText(spn);
                return true;
            }
        }
        return false;
    }

    @Override
    public final void bindView(final View view, final Context context, Cursor cursor) {
        ContactsWrapper.getInstance().bindContactPhoneView(view, context, cursor);
        highlightTextViewSearch((TextView) view.findViewById(R.id.name));
        highlightTextViewSearch((TextView) view.findViewById(R.id.number));

        if (view != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String number = (String) view.getTag();
                    SipProfile account = AccountUtils.getAccount();
                    String rewritten = Filter.rewritePhoneNumber(context, account.id, number);

                    if (mDialUser != null) {
                        Editable text = mDialUser.getText();
                        if (text != null) {
                            text.clear();
                            text.append(rewritten);
                        }
                    }


                }
            });
        }
    }

    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        Cursor c = ContactsWrapper.getInstance().getContactsPhones(mContext, constraint);

        if (alphaIndexer == null) {
            alphaIndexer = new AlphabetIndexer(c, ContactsWrapper.getInstance().getContactIndexableColumnIndex(c),
                    " ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        } else {
            alphaIndexer.setCursor(c);
        }
        return c;
    }

    @Override
    public final CharSequence convertToString(Cursor cursor) {
        CharSequence number = ContactsWrapper.getInstance().transformToSipUri(mContext, cursor);
        boolean isExternalPhone = ContactsWrapper.getInstance().isExternalPhoneNumber(mContext, cursor);
        if (!TextUtils.isEmpty(number) && isExternalPhone) {
            String stripNbr = PhoneNumberUtils.stripSeparators(number.toString());
            return Filter.rewritePhoneNumber(mContext, currentAccId, stripNbr);
        }
        return number;
    }

    @Override
    public int getPositionForSection(int section) {
        if (alphaIndexer != null) {
            try {
                return alphaIndexer.getPositionForSection(section);
            } catch (CursorIndexOutOfBoundsException e) {
                // Not a problem we are just not yet init
            }
        }
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        if (alphaIndexer != null) {
            try {
                return alphaIndexer.getSectionForPosition(position);
            } catch (CursorIndexOutOfBoundsException e) {
                // Not a problem we are just not yet init
            }
        }
        return 0;
    }

    @Override
    public Object[] getSections() {
        if (alphaIndexer != null) {
            return alphaIndexer.getSections();
        }
        return null;
    }

}

