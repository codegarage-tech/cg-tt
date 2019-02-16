package tech.codegarage.tidetwist.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import tech.codegarage.tidetwist.viewholder.NotificationAppViewHolder;
import tech.codegarage.tidetwist.viewholder.NotificationLinkViewHolder;
import tech.codegarage.tidetwist.viewholder.NotificationPingViewHolder;
import tech.codegarage.tidetwist.viewholder.NotificationRawViewHolder;
import tech.codegarage.tidetwist.viewholder.NotificationTextViewHolder;

import java.security.InvalidParameterException;

import tech.codegarage.fcm.payload.AppPayload;
import tech.codegarage.fcm.payload.LinkPayload;
import tech.codegarage.fcm.payload.Payload;
import tech.codegarage.fcm.payload.PingPayload;
import tech.codegarage.fcm.payload.RawPayload;
import tech.codegarage.fcm.payload.TextPayload;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class NotificationListAdapter extends RecyclerArrayAdapter<Payload> {

    private static final int VIEW_TYPE_PING = 1;
    private static final int VIEW_TYPE_TEXT = 2;
    private static final int VIEW_TYPE_LINK = 3;
    private static final int VIEW_TYPE_APP = 4;
    private static final int VIEW_TYPE_RAW = 5;
    private static final int VIEW_TYPE_NONE = -1;

    public NotificationListAdapter(Context context) {
        super(context);
    }

    @Override
    public int getViewType(int position) {
        Payload payload = getItem(position);
        if (payload instanceof PingPayload) {
            return VIEW_TYPE_PING;
        } else if (payload instanceof TextPayload) {
            return VIEW_TYPE_TEXT;
        } else if (payload instanceof LinkPayload) {
            return VIEW_TYPE_LINK;
        } else if (payload instanceof AppPayload) {
            return VIEW_TYPE_APP;
        } else if (payload instanceof RawPayload) {
            return VIEW_TYPE_RAW;
        }

        return VIEW_TYPE_NONE;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_PING:
                return new NotificationPingViewHolder(parent);
            case VIEW_TYPE_TEXT:
                return new NotificationTextViewHolder(parent);
            case VIEW_TYPE_LINK:
                return new NotificationLinkViewHolder(parent);
            case VIEW_TYPE_APP:
                return new NotificationAppViewHolder(parent);
            case VIEW_TYPE_RAW:
                return new NotificationRawViewHolder(parent);
            default:
                throw new InvalidParameterException();
        }
    }
}