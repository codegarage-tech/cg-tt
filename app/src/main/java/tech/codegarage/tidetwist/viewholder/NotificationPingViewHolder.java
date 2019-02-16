package tech.codegarage.tidetwist.viewholder;

import android.view.ViewGroup;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;

import tech.codegarage.fcm.payload.Payload;
import tech.codegarage.fcm.payload.PingPayload;
import tech.codegarage.tidetwist.R;
import tech.codegarage.tidetwist.util.Logger;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class NotificationPingViewHolder extends BaseViewHolder<Payload> {

    private static String TAG = NotificationAppViewHolder.class.getSimpleName();
    private TextView tvPingTimestamp, tvPingMessage;

    public NotificationPingViewHolder(ViewGroup parent) {
        super(parent, R.layout.row_notification_ping);

        tvPingTimestamp = (TextView) $(R.id.tv_ping_timestamp);
        tvPingMessage = (TextView) $(R.id.tv_ping_message);
    }

    @Override
    public void setData(final Payload data) {
        final PingPayload pingPayload = (PingPayload) data;
        Logger.d(TAG, TAG + ">> pingPayload: " + pingPayload.getFormattedTimestamp());

        tvPingTimestamp.setText(pingPayload.getFormattedTimestamp());
        tvPingMessage.setText(pingPayload.getRawCharSequence(getContext()));
    }
}