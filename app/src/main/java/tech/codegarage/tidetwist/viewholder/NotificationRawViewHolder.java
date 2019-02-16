package tech.codegarage.tidetwist.viewholder;

import android.view.ViewGroup;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;

import tech.codegarage.fcm.payload.Payload;
import tech.codegarage.fcm.payload.RawPayload;
import tech.codegarage.tidetwist.R;
import tech.codegarage.tidetwist.util.Logger;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class NotificationRawViewHolder extends BaseViewHolder<Payload> {

    private static String TAG = NotificationRawViewHolder.class.getSimpleName();
    private TextView tvRawTimestamp, tvRawMessage;

    public NotificationRawViewHolder(ViewGroup parent) {
        super(parent, R.layout.row_notification_raw);

        tvRawTimestamp = (TextView) $(R.id.tv_raw_timestamp);
        tvRawMessage = (TextView) $(R.id.tv_raw_message);
    }

    @Override
    public void setData(final Payload data) {
        final RawPayload rawPayload = (RawPayload) data;
        Logger.d(TAG, TAG + ">> rawPayload: " + rawPayload.getFormattedCharSequence(getContext()));

        tvRawTimestamp.setText(rawPayload.getFormattedTimestamp());
        tvRawMessage.setText(rawPayload.getRawCharSequence(getContext()));
    }
}