package tech.codegarage.tidetwist.viewholder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;

import tech.codegarage.fcm.payload.Payload;
import tech.codegarage.fcm.payload.TextPayload;
import tech.codegarage.fcm.util.FcmUtil;
import tech.codegarage.tidetwist.R;
import tech.codegarage.tidetwist.util.Logger;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class NotificationTextViewHolder extends BaseViewHolder<Payload> {

    private static String TAG = NotificationTextViewHolder.class.getSimpleName();
    private TextView tvTextTimestamp, tvTextMessage;
    private Button btnTextCopy;

    public NotificationTextViewHolder(ViewGroup parent) {
        super(parent, R.layout.row_notification_text);

        tvTextTimestamp = (TextView) $(R.id.tv_text_timestamp);
        tvTextMessage = (TextView) $(R.id.tv_text_message);
        btnTextCopy = (Button) $(R.id.btn_text_copy);
    }

    @Override
    public void setData(final Payload data) {
        final TextPayload textPayload = (TextPayload) data;
        Logger.d(TAG, TAG + ">> textPayload: " + textPayload.getFormattedCharSequence(getContext()));

        tvTextTimestamp.setText(textPayload.getFormattedTimestamp());
        tvTextMessage.setText(textPayload.getRawCharSequence(getContext()));

        btnTextCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FcmUtil.copyToClipboard(view.getContext(), textPayload.text);
            }
        });
    }
}