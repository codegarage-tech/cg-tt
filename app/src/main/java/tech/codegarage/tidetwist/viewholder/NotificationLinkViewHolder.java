package tech.codegarage.tidetwist.viewholder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;

import tech.codegarage.fcm.payload.LinkPayload;
import tech.codegarage.fcm.payload.Payload;
import tech.codegarage.fcm.util.FcmUtil;
import tech.codegarage.tidetwist.R;
import tech.codegarage.tidetwist.util.Logger;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class NotificationLinkViewHolder extends BaseViewHolder<Payload> {

    private static String TAG = NotificationLinkViewHolder.class.getSimpleName();
    private TextView tvLinkTimestamp, tvLinkMessage;
    private Button btnLinkOpen;

    public NotificationLinkViewHolder(ViewGroup parent) {
        super(parent, R.layout.row_notification_link);

        tvLinkTimestamp = (TextView) $(R.id.tv_link_timestamp);
        tvLinkMessage = (TextView) $(R.id.tv_link_message);
        btnLinkOpen = (Button) $(R.id.btn_link_open);
    }

    @Override
    public void setData(final Payload data) {
        final LinkPayload linkPayload = (LinkPayload) data;
        Logger.d(TAG, TAG + ">> linkPayload: " + linkPayload.getFormattedCharSequence(getContext()));

        tvLinkTimestamp.setText(linkPayload.getFormattedTimestamp());
        tvLinkMessage.setText(linkPayload.getRawCharSequence(getContext()));

        btnLinkOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    view.getContext().startActivity(linkPayload.getIntent());
                } catch (Exception e) {
                    Toast.makeText(view.getContext(), FcmUtil.printStackTrace(e), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}