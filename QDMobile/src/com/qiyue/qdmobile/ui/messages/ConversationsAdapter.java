package com.qiyue.qdmobile.ui.messages;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.format.DateUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.api.SipMessage;
import com.qiyue.qdmobile.api.SipUri;
import com.qiyue.qdmobile.models.CallerInfo;
import com.qiyue.qdmobile.utils.ContactsAsyncHelper;

public class ConversationsAdapter extends SimpleCursorAdapter {

    private static final String TAG = ConversationsAdapter.class.getSimpleName();

	private Context mContext;
	
    public ConversationsAdapter(Context context, Cursor c) {
        super(context, R.layout.conversation_list_item, c, new String[] {
                SipMessage.FIELD_BODY
        },
                new int[] {
                        R.id.subject
                }, 0);
        mContext = context;
    }

    public interface ConversationItemClick {
        void callback(View view, int position);
    }

    private ConversationItemClick mConversationItemClick;

    public void setCallbackListener(ConversationItemClick callbackListener) {
        mConversationItemClick = callbackListener;
    }

    public static final class ConversationListItemViews {
        TextView fromView;
        TextView dateView;
        RoundedImageView quickContactView;
        int position;
        String to;
        String from;
        String fromFull;
        
        String getRemoteNumber() {
            String number = from;
            if (SipMessage.SELF.equals(number)) {
                number = to;
            }
            return number;
        }
    }
    
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = super.newView(context, cursor, parent);
        ConversationListItemViews tagView = new ConversationListItemViews();
        tagView.fromView = (TextView) view.findViewById(R.id.from);
        tagView.dateView = (TextView) view.findViewById(R.id.date);
        tagView.quickContactView = (RoundedImageView) view.findViewById(R.id.quick_contact_photo);
        view.setTag(tagView);
        //view.setOnClickListener(mPrimaryActionListener);

        return view;
    }

    @Override
    public void bindView(final View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);

        final ConversationListItemViews tagView = (ConversationListItemViews) view.getTag();
        String nbr = cursor.getString(cursor.getColumnIndex(SipMessage.FIELD_FROM));
        String fromFull = cursor.getString(cursor.getColumnIndex(SipMessage.FIELD_FROM_FULL));
        String to_number = cursor.getString(cursor.getColumnIndex(SipMessage.FIELD_TO));
        
        //int read = cursor.getInt(cursor.getColumnIndex(SipMessage.FIELD_READ));
        long date = cursor.getLong(cursor.getColumnIndex(SipMessage.FIELD_DATE));

        tagView.fromFull = fromFull;
        tagView.to = to_number;
        tagView.from = nbr;
        tagView.position = cursor.getPosition();
        
        String number = cursor.getString(cursor.getColumnIndex(SipMessage.FIELD_FROM_FULL));
        CallerInfo info = CallerInfo.getCallerInfoFromSipUri(mContext, number);
        
        // Photo
        tagView.quickContactView.setImageURI(info.contactContentUri);
        ContactsAsyncHelper.updateImageViewWithContactPhotoAsync(mContext, 
                tagView.quickContactView,
                info,
                R.drawable.ic_contact_picture_holo_dark);

        // From
        tagView.fromView.setText(formatMessage(cursor));

        //Date
        // Set the date/time field by mixing relative and absolute times.
        int flags = DateUtils.FORMAT_ABBREV_RELATIVE;
        tagView.dateView.setText(DateUtils.getRelativeTimeSpanString(date, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS, flags));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick() message item ");

                if (mConversationItemClick != null) {
                    mConversationItemClick.callback(v, tagView.position);
                }
            }
        });
    }
    
    
    private static final StyleSpan STYLE_BOLD = new StyleSpan(Typeface.BOLD);

    private CharSequence formatMessage(Cursor cursor) {
        SpannableStringBuilder buf = new SpannableStringBuilder();
        /*
        String remoteContact = cursor.getString(cursor.getColumnIndex(SipMessage.FIELD_FROM));
        if (remoteContact.equals("SELF")) {
            remoteContact = cursor.getString(cursor.getColumnIndex(SipMessage.FIELD_TO));
            buf.append("To: ");
        }
        */
        String remoteContactFull = cursor.getString(cursor.getColumnIndex(SipMessage.FIELD_FROM_FULL));
        CallerInfo callerInfo = CallerInfo.getCallerInfoFromSipUri(mContext, remoteContactFull);
        if (callerInfo != null && callerInfo.contactExists) {
        	buf.append(callerInfo.name);
        	buf.append(" / ");
            buf.append(SipUri.getDisplayedSimpleContact(remoteContactFull));
        } else {
            buf.append(SipUri.getDisplayedSimpleContact(remoteContactFull));
        }
        
        int counter = cursor.getInt(cursor.getColumnIndex("counter"));
        if (counter > 1) {
            buf.append(" (" + counter + ") ");
        }

        int read = cursor.getInt(cursor.getColumnIndex(SipMessage.FIELD_READ));
        // Unread messages are shown in bold
        if (read == 0) {
            buf.setSpan(STYLE_BOLD, 0, buf.length(),
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return buf;
    }
}
