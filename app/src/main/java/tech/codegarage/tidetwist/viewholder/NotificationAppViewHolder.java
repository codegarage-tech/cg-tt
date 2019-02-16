package tech.codegarage.tidetwist.viewholder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;

import tech.codegarage.fcm.payload.AppPayload;
import tech.codegarage.fcm.payload.Payload;
import tech.codegarage.fcm.util.FcmUtil;
import tech.codegarage.tidetwist.R;
import tech.codegarage.tidetwist.util.Logger;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class NotificationAppViewHolder extends BaseViewHolder<Payload> {

    private static String TAG = NotificationAppViewHolder.class.getSimpleName();
    private TextView tvAppTimestamp, tvAppMessage;
    private Button btnAppRemove, btnAppInstall;

    public NotificationAppViewHolder(ViewGroup parent) {
        super(parent, R.layout.row_notification_app);

        tvAppTimestamp = (TextView) $(R.id.tv_app_timestamp);
        tvAppMessage = (TextView) $(R.id.tv_app_message);
        btnAppRemove = (Button) $(R.id.btn_app_remove);
        btnAppInstall = (Button) $(R.id.btn_app_install);
    }

    @Override
    public void setData(final Payload data) {
        final AppPayload appPayload = (AppPayload) data;
        Logger.d(TAG, TAG + ">> appPayload: " + appPayload.getFormattedCharSequence(getContext()));

        tvAppTimestamp.setText(appPayload.getFormattedTimestamp());
        tvAppMessage.setText(appPayload.getRawCharSequence(getContext()));

        btnAppRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    view.getContext().startActivity(appPayload.getRemoveIntent());
                } catch (Exception e) {
                    Toast.makeText(view.getContext(), FcmUtil.printStackTrace(e), Toast.LENGTH_LONG).show();
                }
            }
        });

        btnAppInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    view.getContext().startActivity(appPayload.getInstallIntent());
                } catch (Exception e) {
                    Toast.makeText(view.getContext(), FcmUtil.printStackTrace(e), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}