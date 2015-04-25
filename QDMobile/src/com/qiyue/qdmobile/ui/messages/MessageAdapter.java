package com.qiyue.qdmobile.ui.messages;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.ResourceCursorAdapter;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.api.SipMessage;
import com.qiyue.qdmobile.models.CallerInfo;
import com.qiyue.qdmobile.utils.ContactsAsyncHelper;
import com.qiyue.qdmobile.utils.SmileyParser;
import com.qiyue.qdmobile.widgets.contactbadge.QuickContactBadge;
import com.qiyue.qdmobile.widgets.contactbadge.QuickContactBadge.ArrowPosition;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageAdapter extends ResourceCursorAdapter {

    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss");
    TextAppearanceSpan mTextSmallSpan;
    private CallerInfo personalInfo;
    


    public MessageAdapter(Context context, Cursor c) {
        super(context, R.layout.message_list_item, c, 0);
        mTextSmallSpan = new TextAppearanceSpan(context, android.R.style.TextAppearance_Small);

        personalInfo = CallerInfo.getCallerInfoForSelf(mContext);
        
    }


    public static final class MessageListItemViews {
        TextView contentView;
        TextView errorView;
        ImageView deliveredIndicator;
        TextView dateView;
        RoundedImageView quickContactView;
        public LinearLayout containterBlock;
    }
    
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final MessageListItemViews tagView = (MessageListItemViews) view.getTag();
        
        SipMessage msg = new SipMessage(cursor);
        
        String number = msg.getRemoteNumber();
        long date = msg.getDate();
        String subject = msg.getBodyContent();
        String errorTxt = msg.getErrorContent();
        String mimeType = msg.getMimeType();
        int type = msg.getType();

        String timestamp = "";
        if (System.currentTimeMillis() - date > 1000 * 60 * 60 * 24) {
            // If it was recieved one day ago or more display relative
            // timestamp - SMS like behavior
            int flags = DateUtils.FORMAT_ABBREV_RELATIVE;
            timestamp = (String) DateUtils.getRelativeTimeSpanString(date,
                    System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS, flags);
        } else {
            // If it has been recieved recently show time of reception - IM
            // like behavior
            timestamp = dateFormatter.format(new Date(date));
        }
        
        tagView.dateView.setText(timestamp);
        

        // Delivery state
        if (type == SipMessage.MESSAGE_TYPE_QUEUED) {
            tagView.deliveredIndicator.setVisibility(View.VISIBLE);
            tagView.deliveredIndicator.setImageResource(R.drawable.ic_email_pending);
            tagView.deliveredIndicator
                    .setContentDescription(mContext.getString(R.string.status_pending));
        } else if (type == SipMessage.MESSAGE_TYPE_FAILED) {
            tagView.deliveredIndicator.setVisibility(View.VISIBLE);
            tagView.deliveredIndicator.setImageResource(R.drawable.ic_sms_mms_not_delivered);
            tagView.deliveredIndicator
                    .setContentDescription(mContext.getString(R.string.undelivered_msg_dialog_title));
        } else {
            tagView.deliveredIndicator.setVisibility(View.GONE);
            tagView.deliveredIndicator
                    .setContentDescription("");
        }

        if (TextUtils.isEmpty(errorTxt)) {
            tagView.errorView.setVisibility(View.GONE);
        } else {
            tagView.errorView.setVisibility(View.VISIBLE);
            tagView.errorView.setText(errorTxt);
        }

        // Subject
        tagView.contentView.setText(formatMessage(number, subject, mimeType));
        
        
        if(msg.isOutgoing()) {
            setPhotoSide(tagView, ArrowPosition.LEFT);
    
            // Photo
            tagView.quickContactView.setImageURI(personalInfo.contactContentUri);
            ContactsAsyncHelper.updateImageViewWithContactPhotoAsync(mContext, 
                    tagView.quickContactView,
                    personalInfo,
                    R.drawable.ic_contact_picture_holo_dark);
            
        }else {
            setPhotoSide(tagView, ArrowPosition.RIGHT);
            
            // Contact
            CallerInfo info = CallerInfo.getCallerInfoFromSipUri(mContext, msg.getFullFrom());
    
            // Photo
            tagView.quickContactView.setImageURI(info.contactContentUri);
            ContactsAsyncHelper.updateImageViewWithContactPhotoAsync(mContext, 
                    tagView.quickContactView,
                    info,
                    R.drawable.ic_contact_picture_holo_dark);
        }

    }

//    TODO
    private void setPhotoSide(MessageListItemViews tagView, ArrowPosition pos) {


        if (pos == ArrowPosition.LEFT) {
            tagView.containterBlock.setBackgroundResource(R.drawable.chat_head_nux_bubble_left);
        } else {
            tagView.containterBlock.setBackgroundResource(R.drawable.chat_head_nux_bubble_right);
        }

//        LayoutParams lp = (RelativeLayout.LayoutParams) tagView.quickContactView.getLayoutParams();
//
//        lp.addRule((pos == ArrowPosition.LEFT) ? RelativeLayout.ALIGN_PARENT_RIGHT
//                : RelativeLayout.ALIGN_PARENT_LEFT);
//        lp.addRule((pos == ArrowPosition.LEFT) ? RelativeLayout.ALIGN_PARENT_LEFT
//                : RelativeLayout.ALIGN_PARENT_RIGHT, 0);
//
//        lp = (RelativeLayout.LayoutParams) tagView.containterBlock.getLayoutParams();
//        lp.addRule((pos == ArrowPosition.LEFT) ? RelativeLayout.LEFT_OF : RelativeLayout.RIGHT_OF, R.id.quick_contact_photo);
//        lp.addRule((pos == ArrowPosition.LEFT) ? RelativeLayout.RIGHT_OF : RelativeLayout.LEFT_OF, 0);
//        tagView.quickContactView.setPosition(pos);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = super.newView(context, cursor, parent);

        MessageListItemViews tagView = new MessageListItemViews();
        tagView.containterBlock = (LinearLayout) view.findViewById(R.id.message_block);
        tagView.contentView = (TextView) view.findViewById(R.id.text_view);
        tagView.errorView = (TextView) view.findViewById(R.id.error_view);
        tagView.dateView = (TextView) view.findViewById(R.id.date_view);
        tagView.quickContactView = (RoundedImageView) view.findViewById(R.id.quick_contact_photo);
        tagView.deliveredIndicator = (ImageView) view.findViewById(R.id.delivered_indicator);

        view.setTag(tagView);

        return view;
    }


    private CharSequence formatMessage(String contact, String body,
            String contentType) {
        SpannableStringBuilder buf = new SpannableStringBuilder();
        if (!TextUtils.isEmpty(body)) {
            // Converts html to spannable if ContentType is "text/html".
            if (contentType != null && "text/html".equals(contentType)) {
                buf.append("\n");
                buf.append(Html.fromHtml(body));
            } else {
                SmileyParser parser = SmileyParser.getInstance();
                buf.append(parser.addSmileySpans(body));
            }
        }

        // We always show two lines because the optional icon bottoms are
        // aligned with the
        // bottom of the text field, assuming there are two lines for the
        // message and the sent time.
        buf.append("\n");
        int startOffset = buf.length();

        startOffset = buf.length();

        buf.setSpan(mTextSmallSpan, startOffset, buf.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return buf;
    }

}
