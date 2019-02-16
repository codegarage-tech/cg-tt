package tech.codegarage.tidetwist.base;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import tech.codegarage.tidetwist.util.Logger;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class BaseResultReceiver extends ResultReceiver {

    private String TAG = "BaseResultReceiver";
    private ResultReceiverCallBack mResultReceiverCallback;

    public BaseResultReceiver(Context context, ResultReceiverCallBack resultReceiverCallBack) {
        super(new Handler(context.getMainLooper()));

        mResultReceiverCallback = resultReceiverCallBack;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        Logger.d(TAG, "resultCode: " + resultCode + " resultData: " + resultData);
        if (mResultReceiverCallback != null) {
            mResultReceiverCallback.onResultReceived(resultCode, resultData);
        }
    }

    public interface ResultReceiverCallBack {
        public void onResultReceived(int resultCode, Bundle resultData);
    }
}