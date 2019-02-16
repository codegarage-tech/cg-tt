package tech.codegarage.tidetwist.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;

import tech.codegarage.tidetwist.service.SmartJobIntentService;
import tech.codegarage.tidetwist.util.Logger;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public abstract class BaseJobIntentService extends SmartJobIntentService {

    //Abstract declaration
    public abstract <JobParam extends Parcelable> Bundle doBackgroundJob(String jobTypeName, JobParam jobParam);

    private String TAG;
    private static int mJobId = 32695;
    private static final int RESULT_CODE_JOB = 6211;
    private static final String KEY_TASK_PARAM = "KEY_TASK_PARAM";
    public static final String KEY_TASK_TYPE = "KEY_TASK_TYPE";
    public static final String KEY_TASK_RESULT = "KEY_TASK_RESULT";
    private static final String KEY_RESULT_RECEIVER = "KEY_RESULT_RECEIVER";

    @Override
    public void onCreate() {
        super.onCreate();
        TAG = getClass().getSimpleName();

        Logger.d(TAG, TAG + ">> onCreate");
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Logger.d(TAG, TAG + ">> onHandleWork");

        //Get background result
        Bundle mBundle = doBackgroundJob(intent.getAction(), intent.getParcelableExtra(KEY_TASK_PARAM));

        //Send job result
        ResultReceiver mBaseResultReceiver = (ResultReceiver) intent.getParcelableExtra(KEY_RESULT_RECEIVER);
        mBaseResultReceiver.send(RESULT_CODE_JOB, mBundle);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.d(TAG, TAG + ">> onDestroy");
    }

    public static <JobParam extends Parcelable> void enqueueJob(Context context, Class<?> jobIntentServiceClass, JobParam jobParam, String jobTypeName, BaseResultReceiver.ResultReceiverCallBack resultReceiverCallBack) {
        Logger.d(jobIntentServiceClass.getSimpleName(), jobIntentServiceClass.getSimpleName() + ">> enqueueWork");

        BaseResultReceiver baseResultReceiver = new BaseResultReceiver(context, resultReceiverCallBack);

        Bundle mBundle = new Bundle();
        mBundle.putParcelable(KEY_RESULT_RECEIVER, baseResultReceiver);
        mBundle.putParcelable(KEY_TASK_PARAM, jobParam);

        Intent jobIntent = new Intent(context, jobIntentServiceClass);
        jobIntent.setAction(jobTypeName);
        jobIntent.putExtras(mBundle);

        enqueueWork(context, jobIntentServiceClass, mJobId, jobIntent);
    }
}