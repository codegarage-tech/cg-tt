package tech.codegarage.tidetwist.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobServiceEngine;
import android.app.job.JobWorkItem;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;

import tech.codegarage.tidetwist.util.Logger;

/**
 * Helper for processing work that has been enqueued for a job/service.  When running on
 * {@link android.os.Build.VERSION_CODES#O Android O} or later, the work will be dispatched
 * as a job via {@link android.app.job.JobScheduler#enqueue JobScheduler.enqueue}.  When running
 * on older versions of the platform, it will use
 * {@link android.content.Context#startService Context.startService}.
 *
 * <p>You must publish your subclass in your manifest for the system to interact with.  This
 * should be published as a {@link android.app.job.JobService}, as described for that class,
 * since on O and later platforms it will be executed that way.</p>
 *
 * <p>Use {@link #enqueueWork(Context, Class, int, Intent)} to enqueue new work to be
 * dispatched to and handled by your service.  It will be executed in
 * {@link #onHandleWork(Intent)}.</p>
 *
 * <p>You do not need to use {@link android.support.v4.content.WakefulBroadcastReceiver}
 * when using this class.  When running on {@link android.os.Build.VERSION_CODES#O Android O},
 * the JobScheduler will take care of wake locks for you (holding a wake lock from the time
 * you enqueue work until the job has been dispatched and while it is running).  When running
 * on previous versions of the platform, this wake lock handling is emulated in the class here
 * by directly calling the PowerManager; this means the application must request the
 * {@link android.Manifest.permission#WAKE_LOCK} permission.</p>
 *
 * <p>There are a few important differences in behavior when running on
 * {@link android.os.Build.VERSION_CODES#O Android O} or later as a Job vs. pre-O:</p>
 *
 * <ul>
 * <li><p>When running as a pre-O service, the act of enqueueing work will generally start
 * the service immediately, regardless of whether the device is dozing or in other
 * conditions.  When running as a Job, it will be subject to standard JobScheduler
 * policies for a Job with a {@link android.app.job.JobInfo.Builder#setOverrideDeadline(long)}
 * of 0: the job will not run while the device is dozing, it may get delayed more than
 * a service if the device is under strong memory pressure with lots of demand to run
 * jobs.</p></li>
 * <li><p>When running as a pre-O service, the normal service execution semantics apply:
 * the service can run indefinitely, though the longer it runs the more likely the system
 * will be to outright kill its process, and under memory pressure one should expect
 * the process to be killed even of recently started services.  When running as a Job,
 * the typical {@link android.app.job.JobService} execution time limit will apply, after
 * which the job will be stopped (cleanly, not by killing the process) and rescheduled
 * to continue its execution later.  Job are generally not killed when the system is
 * under memory pressure, since the number of concurrent jobs is adjusted based on the
 * memory state of the device.</p></li>
 * </ul>
 * <p>
 * <p>
 * ************************************************************************************************
 * This class is using Handler-Thread instead of AsyncTask for background thread execution
 * ***********************************************************************************************
 * As per IntentService we were having one dedicated handler thread with Looper per Service
 * ***********************************************************************************************
 * With Android support v4 SmartJobIntentService were relying of AsynTask-Threaded Pool Executor
 * ***********************************************************************************************
 * Since AsyncTask with Threaded Pool Executor will break the serialization of task flow per service
 * which is current behaviour with IntentService because of handler-Thread,here also we are changing the
 * android actual implementation to use dedicated Handler thread per service instead of Asyn-Task
 * ***********************************************************************************************
 * Since AsyncTask with Threaded Pool Executor is shared per app/service and the max/core number
 * of parallel thread is limited to 3/5/9 based of device cpu core count, here also with Android
 * actual implementation we were reducing the concurrent execution limit compare to existing
 * IntentService behaviour which is again not generic and more specific to CPU count and thread usage
 * ******************************************************************
 * <p>
 * ************************************************************************************************
 * This class is using Handler-Thread instead of AsyncTask for background thread execution
 * ***********************************************************************************************
 * As per IntentService we were having one dedicated handler thread with Looper per Service
 * ***********************************************************************************************
 * With Android support v4 SmartJobIntentService were relying of AsynTask-Threaded Pool Executor
 * ***********************************************************************************************
 * Since AsyncTask with Threaded Pool Executor will break the serialization of task flow per service
 * which is current behaviour with IntentService because of handler-Thread,here also we are changing the
 * android actual implementation to use dedicated Handler thread per service instead of Asyn-Task
 * ***********************************************************************************************
 * Since AsyncTask with Threaded Pool Executor is shared per app/service and the max/core number
 * of parallel thread is limited to 3/5/9 based of device cpu core count, here also with Android
 * actual implementation we were reducing the concurrent execution limit compare to existing
 * IntentService behaviour which is again not generic and more specific to CPU count and thread usage
 * ******************************************************************
 * <p>
 * ************************************************************************************************
 * This class is using Handler-Thread instead of AsyncTask for background thread execution
 * ***********************************************************************************************
 * As per IntentService we were having one dedicated handler thread with Looper per Service
 * ***********************************************************************************************
 * With Android support v4 SmartJobIntentService were relying of AsynTask-Threaded Pool Executor
 * ***********************************************************************************************
 * Since AsyncTask with Threaded Pool Executor will break the serialization of task flow per service
 * which is current behaviour with IntentService because of handler-Thread,here also we are changing the
 * android actual implementation to use dedicated Handler thread per service instead of Asyn-Task
 * ***********************************************************************************************
 * Since AsyncTask with Threaded Pool Executor is shared per app/service and the max/core number
 * of parallel thread is limited to 3/5/9 based of device cpu core count, here also with Android
 * actual implementation we were reducing the concurrent execution limit compare to existing
 * IntentService behaviour which is again not generic and more specific to CPU count and thread usage
 * ******************************************************************
 */


/**
 * ************************************************************************************************
 *  This class is using Handler-Thread instead of AsyncTask for background thread execution
 ************************************************************************************************
 *  As per IntentService we were having one dedicated handler thread with Looper per Service
 ************************************************************************************************
 *  With Android support v4 SmartJobIntentService were relying of AsynTask-Threaded Pool Executor
 ************************************************************************************************
 *  Since AsyncTask with Threaded Pool Executor will break the serialization of task flow per service
 * which is current behaviour with IntentService because of handler-Thread,here also we are changing the
 * android actual implementation to use dedicated Handler thread per service instead of Asyn-Task
 ************************************************************************************************
 *  Since AsyncTask with Threaded Pool Executor is shared per app/service and the max/core number
 * of parallel thread is limited to 3/5/9 based of device cpu core count, here also with Android
 * actual implementation we were reducing the concurrent execution limit compare to existing
 * IntentService behaviour which is again not generic and more specific to CPU count and thread usage
 *******************************************************************
 */

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public abstract class SmartJobIntentService extends Service {
    /** Looper */
    private volatile Looper mServiceLooper;
    /** Handler */
    private volatile CommandProcessor mServiceHandler;
    /** serviceName */
    private final String serviceName;

    /** Logger tag */
    static final String TAG = SmartJobIntentService.class.getSimpleName();

    /** CompatJobEngine */
    CompatJobEngine mJobImpl;

    /** Worker */
    WorkEnqueuer mCompatWorkEnqueuer;

    /** Stopped flag */
    boolean mStopped = false;

    /** Destroyed flag */
    boolean mDestroyed = false;

    /** Wake lock */
    static final Object S_LOCK = new Object();

    /** Map of Component name and WorkEnqueuer */
    static final HashMap<ComponentName, WorkEnqueuer> S_CLASS_WORK_ENQUEUER = new HashMap<>();

    /** Timeout for wake lock in ms*/
    static final Long WAKE_LOCK_TIMEOUT = 60 * 1000L;

    /** Device awake time */
    static final Long DEVICE_AWAKE_TIME = 10 * 60 * 1000L;

    /**
     * Base class for the target service we can deliver work to and the implementation of
     * how to deliver that work.
     */
    abstract static class WorkEnqueuer {

        /** Component name*/
        final ComponentName mComponentName;

        /** Job id flag */
        boolean mHasJobId;

        /** Job id */
        int mJobId;

        /**
         * Constructor
         * @param context Android context
         * @param cn component name
         */
        WorkEnqueuer(final Context context, final ComponentName cn) {
            mComponentName = cn;
        }

        /**
         * Check Job ID
         * @param jobId Job id
         */
        void ensureJobId(final int jobId) {
            if (!mHasJobId) {
                mHasJobId = true;
                mJobId = jobId;
            } else if (mJobId != jobId) {
                throw new IllegalArgumentException("Given job ID " + jobId
                        + " is different than previous " + mJobId);
            }
        }

        /**
         *
         * @param work intent.
         */
        abstract void enqueueWork(final Intent work);

        /**
         *serviceStartReceived
         */
        public void serviceStartReceived() {
        }

        /**
         *serviceProcessingStarted
         */
        public void serviceProcessingStarted() {
        }

        /**
         *serviceProcessingFinished
         */
        public void serviceProcessingFinished() {
        }
    }

    /**
     * Get rid of lint warnings about API levels.
     */
    interface CompatJobEngine {
        /**
         *
         * @return IBinder
         */
        IBinder compatGetBinder();

    }

    /**
     * An implementation of WorkEnqueuer that works for pre-O (raw Service-based).
     */
    static final class CompatWorkEnqueuer extends WorkEnqueuer {
        /** Context */
        private final Context mContext;
        /** Wake Lock  */
        private final PowerManager.WakeLock mLaunchWakeLock;
        /** */
        private final PowerManager.WakeLock mRunWakeLock;
        /** Launch service flag */
        boolean mLaunchingService;
        /** Service processing flag */
        boolean mServiceProcessing;

        /**
         * Constructor
         * @param context Android context
         * @param cn component name
         */
        CompatWorkEnqueuer(final Context context, final ComponentName cn) {
            super(context, cn);
            mContext = context.getApplicationContext();
            // Make wake locks.  We need two, because the launch wake lock wants to have
            // a timeout, and the system does not do the right thing if you mix timeout and
            // non timeout (or even changing the timeout duration) in one wake lock.
            PowerManager pm = ((PowerManager) context.getSystemService(Context.POWER_SERVICE));
            mLaunchWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    cn.getClassName() + ":launch");
            mLaunchWakeLock.setReferenceCounted(false);
            mRunWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    cn.getClassName() + ":run");
            mRunWakeLock.setReferenceCounted(false);
        }

        @Override
        void enqueueWork(final Intent work) {
            Intent intent = new Intent(work);
            intent.setComponent(mComponentName);
            Logger.d(TAG, "Starting service for work: " + work);
            if (mContext.startService(intent) != null) {
                synchronized (this) {
                    if (!mLaunchingService) {
                        mLaunchingService = true;
                        if (!mServiceProcessing) {
                            // If the service is not already holding the wake lock for
                            // itself, acquire it now to keep the system running until
                            // we get this work dispatched.  We use a timeout here to
                            // protect against whatever problem may cause it to not get
                            // the work.
                            Logger.d(TAG, "Wake_Lock Acquire for 1 min");
                            mLaunchWakeLock.acquire(WAKE_LOCK_TIMEOUT);
                        }
                    }
                }
            }
        }

        @Override
        public void serviceStartReceived() {
            synchronized (this) {
                // Once we have started processing work, we can count whatever last
                // enqueueWork() that happened as handled.
                mLaunchingService = false;
            }
        }

        @Override
        public void serviceProcessingStarted() {
            synchronized (this) {
                // We hold the wake lock as long as the service is processing commands.
                if (!mServiceProcessing) {
                    mServiceProcessing = true;
                    // Keep the device awake, but only for at most 10 minutes at a time
                    // (Similar to JobScheduler.)
                    Logger.d(TAG, "Wake_Lock Acquire for 10 min");
                    mRunWakeLock.acquire(DEVICE_AWAKE_TIME);
                    mLaunchWakeLock.release();
                    Logger.d(TAG, "Launch_Lock released");

                }
            }
        }

        @Override
        public void serviceProcessingFinished() {
            synchronized (this) {
                if (mServiceProcessing) {
                    // If we are transitioning back to a wakelock with a timeout, do the same
                    // as if we had enqueued work without the service running.
                    if (mLaunchingService) {
                        Logger.d(TAG, "Wake_Lock Acquire for 1 min");
                        mLaunchWakeLock.acquire(WAKE_LOCK_TIMEOUT);
                    }
                    mServiceProcessing = false;
                    mRunWakeLock.release();
                    Logger.d(TAG, "Wake_Lock released");

                }
            }
        }
    }

    /**
     * Implementation of a JobServiceEngine for interaction with SmartJobIntentService.
     */
    @TargetApi(Build.VERSION_CODES.O)
    public static final class JobServiceEngineImpl extends JobServiceEngine
            implements SmartJobIntentService.CompatJobEngine {

        /** Job intent service */
        final SmartJobIntentService mService;

        /** WAKE LOCK */
        final Object mLock = new Object();

        /** Job params */
        JobParameters mParams;

        /**
         * Handler Thread
         */
        final CommandProcessor mServiceHandler;

        /**
         * SmartJobIntentService wrapper work item
         */
        final class WrapperWorkItem implements SmartJobIntentService.GenericWorkItem {
            /** Job work item */
            final JobWorkItem mJobWork;


            /**
             * Constructor
             * @param jobWork jobWork.
             */

            WrapperWorkItem(final JobWorkItem jobWork) {
                mJobWork = jobWork;
            }

            @Override
            public Intent getIntent() {
                return mJobWork.getIntent();
            }

            @Override
            public void complete() {
                Logger.d(TAG, "Calling completeWork() for completed Task of intent " + mJobWork.getIntent());
                synchronized (mLock) {
                    if (mParams != null) {
                        mParams.completeWork(mJobWork);
                        Logger.d(TAG, "Job Work Completed " + mParams);
                    }
                }
            }
        }

        /**
         *
         * @param service JobIntent Service
         * @param commandProcessor handler
         */
        JobServiceEngineImpl(final SmartJobIntentService service, final CommandProcessor commandProcessor) {
            super(service);
            mService = service;
            mServiceHandler = commandProcessor;

        }

        @Override
        public IBinder compatGetBinder() {
            return getBinder();
        }

        @Override
        public boolean onStartJob(final JobParameters params) {
            Logger.d(TAG, "onStartJob: " + params);
            mParams = params;
            // We can now start sending work!
            Message msg = mServiceHandler.obtainMessage();
            msg.arg1 = params.getJobId();
            msg.obj = params;
            mServiceHandler.sendMessage(msg);
            return true;
        }

        @Override
        public boolean onStopJob(final JobParameters params) {
            Logger.d(TAG, "onStopJob: " + params);
            boolean result = mService.doStopCurrentWork();
            synchronized (mLock) {
                // Once we return, the job is stopped, so its JobParameters are no
                // longer valid and we should not be doing anything with them.
                mParams = null;
            }
            return result;
        }
    }

    /**
     * JobWorkEnqueuer
     */
    @TargetApi(Build.VERSION_CODES.O)
    static final class JobWorkEnqueuer extends SmartJobIntentService.WorkEnqueuer {
        /** Job info */
        private final JobInfo mJobInfo;
        /** Job Scheduler */
        private final JobScheduler mJobScheduler;

        /**
         *
         * @param context Android context
         * @param cn component name
         * @param jobId job id
         */

        JobWorkEnqueuer(final Context context, final ComponentName cn, final int jobId) {
            super(context, cn);
            ensureJobId(jobId);
            JobInfo.Builder b = new JobInfo.Builder(jobId, mComponentName);
            mJobInfo = b.setOverrideDeadline(0).build();
            mJobScheduler = (JobScheduler) context.getApplicationContext().getSystemService(
                    Context.JOB_SCHEDULER_SERVICE);
        }

        @Override
        void enqueueWork(final Intent work) {
            Logger.d(TAG, "Enqueueing work: " + work);
            mJobScheduler.enqueue(mJobInfo, new JobWorkItem(work));
        }
    }

    /**
     * Abstract definition of an item of work that is being dispatched.
     */
    interface GenericWorkItem {
        /**
         *
         * @return Intent
         */
        Intent getIntent();

        /**
         * complete()
         */
        void complete();
    }

    /**
     * An implementation of GenericWorkItem that dispatches work for pre-O platforms: intents
     * received through a raw service's onStartCommand.
     */
    final class CompatWorkItem implements GenericWorkItem {
        /** */
        final Intent mIntent;
        /** */
        final int mStartId;

        /**
         * Constructor
         * @param intent intent.
         * @param startId startId.
         */
        CompatWorkItem(final Intent intent, final int startId) {
            mIntent = intent;
            mStartId = startId;
        }

        @Override
        public Intent getIntent() {
            return mIntent;
        }

        @Override
        public void complete() {
            Logger.d(TAG, "Stopping self: #" + mStartId);
            stopSelf(mStartId);
        }
    }

    /**
     * This is a Handler to dequeue and
     * process work in the background handler thread.
     */
    final class CommandProcessor extends Handler {

        /**
         * @param looper handler with its corresponding thread looper
         */
        CommandProcessor(final Looper looper) {
            super(looper);
        }

        /**
         * @param msg Message to be handled
         */
        @Override
        public void handleMessage(final Message msg) {
            SmartJobIntentService.GenericWorkItem work;
            Logger.d(TAG, "--Background Execution on " + Thread.currentThread());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                final JobParameters jobParameters = (JobParameters) msg.obj;
                JobWorkItem jobWorkItem;
                while ((jobWorkItem = jobParameters.dequeueWork()) != null) {
                    if (jobWorkItem.getIntent() != null) {
                        SmartJobIntentService.this.onHandleWork((jobWorkItem.getIntent()));
                        Logger.d(TAG, "Handled message was: intent->" + jobWorkItem.getIntent() + " arg1-> " + msg.arg1);
                        jobParameters.completeWork(jobWorkItem);
                        Logger.d(TAG, "Done with: " + jobWorkItem);
                    }
                }
                Logger.d(TAG, "All task has completed now OS will stop this Job with id -->" + msg.arg1);

            } else {
                work = new CompatWorkItem((Intent) msg.obj, msg.arg1);
                if (work.getIntent() != null) {
                    SmartJobIntentService.this.onHandleWork(work.getIntent());
                }
                workComplete(work, msg);
                Logger.d(TAG, "Handled message was: obj->" + work + " arg1-> " + msg.arg1);
            }
        }
    }

    /**
     * workComplete
     * @param genericWorkItem workItem POJO for received message
     * @param message Handler message
     */
    public void workComplete(final GenericWorkItem genericWorkItem, final Message message) {
        Logger.d(TAG, "Calling work complete for given Job task");
        genericWorkItem.complete();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread thread = new HandlerThread("Service[" + serviceName + "]");
        thread.start(); //Starting the new Handler thread
        mServiceLooper = thread.getLooper(); //Trying to get looper of new thread
        mServiceHandler = new CommandProcessor(mServiceLooper);
        Logger.d(TAG, "CREATING: " + this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mJobImpl = new JobServiceEngineImpl(this, mServiceHandler);
            mCompatWorkEnqueuer = null;
        } else {
            mJobImpl = null;
            ComponentName cn = new ComponentName(this, this.getClass());
            mCompatWorkEnqueuer = getWorkEnqueuer(this, cn, false, 0);
        }
    }

    /**
     * Processes start commands when running as a pre-O service, enqueueing them to be
     * later dispatched in {@link #onHandleWork(Intent)}.
     */
    @Override
    public int onStartCommand(@Nullable final Intent intent, final int flags, final int startId) {
        if (mServiceHandler != null) {
            ////***** Below 'if' case is only possible if we are calling startService() from Android 0 device *****///
            ////**** To make it backward compatible for both API <26 and API >=26 ****///
            ////**** Instead of Runtime Exception for Null object mCompatWorkEnqueuer better to have Android Warning***//
            if (mCompatWorkEnqueuer == null) {
                mJobImpl = null;
                ComponentName cn = new ComponentName(this, this.getClass());
                mCompatWorkEnqueuer = getWorkEnqueuer(this, cn, false, 0);
            }
            /// ********************************************************************************************///

            mCompatWorkEnqueuer.serviceStartReceived();
            Logger.d(TAG, "Received compat start command #" + startId + ": " + intent);
            final Message msg = mServiceHandler.obtainMessage();
            msg.arg1 = startId;
            msg.obj = intent;
            mServiceHandler.sendMessage(msg);
            ensureProcessorRunningLocked(true);
            return START_REDELIVER_INTENT;
        } else {
            Logger.d(TAG, "Ignoring start command: " + intent);
            return START_NOT_STICKY;
        }
    }

    /**
     * Returns the IBinder for the {@link android.app.job.JobServiceEngine} when
     * running as a JobService on O and later platforms.
     */
    @Override
    public IBinder onBind(@NonNull final Intent intent) {
        if (mJobImpl != null) {
            IBinder engine = mJobImpl.compatGetBinder();
            Logger.d(TAG, "Returning engine: " + engine);
            return engine;
        } else {
            return null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.d(TAG, "Quitting the Looper of handler thread");
        mServiceLooper.quit(); //Quits the looper of Handler Thread to avod any memory leak.
        mDestroyed = true;
        if (mCompatWorkEnqueuer != null) {
            mCompatWorkEnqueuer.serviceProcessingFinished(); // Releasing all acquired Locks of services
        }
    }


    /**
     * Call this to enqueue work for your subclass of {@link SmartJobIntentService}.  This will
     * either directly start the service (when running on pre-O platforms) or enqueue work
     * for it as a job (when running on O and later).  In either case, a wake lock will be
     * held for you to ensure you continue running.  The work you enqueue will ultimately
     * appear at {@link #onHandleWork(Intent)}.
     *
     * @param context Context this is being called from.
     * @param cls The concrete class the work should be dispatched to (this is the class that
     * is published in your manifest).
     * @param jobId A unique job ID for scheduling; must be the same value for all work
     * enqueued for the same class.
     * @param work The Intent of work to enqueue.
     */
    public static void enqueueWork(@NonNull final Context context, @NonNull final Class cls, final int jobId,
                                   @NonNull final Intent work) {
        enqueueWork(context, new ComponentName(context, cls), jobId, work);
    }

    /**
     * Like {@link #enqueueWork(Context, Class, int, Intent)}, but supplies a ComponentName
     * for the service to interact with instead of its class.
     *
     * @param context Context this is being called from.
     * @param component The published ComponentName of the class this work should be
     * dispatched to.
     * @param jobId A unique job ID for scheduling; must be the same value for all work
     * enqueued for the same class.
     * @param work The Intent of work to enqueue.
     */
    private static void enqueueWork(@NonNull final Context context, @NonNull final ComponentName component,
                                    final int jobId, @NonNull final Intent work) {
        if (work == null) {
            throw new IllegalArgumentException("work must not be null");
        }
        synchronized (S_LOCK) {
            WorkEnqueuer we = getWorkEnqueuer(context, component, true, jobId);
            we.ensureJobId(jobId);
            we.enqueueWork(work);
        }
    }

    /**
     *
     * @param context Android context
     * @param cn component name
     * @param hasJobId flag
     * @param jobId A unique job ID for scheduling
     * @return work enqueuer
     */
    static WorkEnqueuer getWorkEnqueuer(final Context context, final ComponentName cn, final boolean hasJobId,
                                        final int jobId) {
        WorkEnqueuer we = S_CLASS_WORK_ENQUEUER.get(cn);
        if (we == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!hasJobId) {
                    throw new IllegalArgumentException("Can't be here without a job id");
                }
                we = new JobWorkEnqueuer(context, cn, jobId);
            } else {
                we = new CompatWorkEnqueuer(context, cn);
            }
            S_CLASS_WORK_ENQUEUER.put(cn, we);
        }
        return we;
    }

    /**
     * Called serially for each work dispatched to and processed by the service.  This
     * method is called on a background thread, so you can do long blocking operations
     * here.  Upon returning, that work will be considered complete and either the next
     * pending work dispatched here or the overall service destroyed now that it has
     * nothing else to do.
     *
     * <p>Be aware that when running as a job, you are limited by the maximum job execution
     * time and any single or total sequential items of work that exceeds that limit will
     * cause the service to be stopped while in progress and later restarted with the
     * last unfinished work.  (There is currently no limit on execution duration when
     * running as a pre-O plain Service.)</p>
     *
     * @param intent The intent describing the work to now be processed.
     */
    protected abstract void onHandleWork(@NonNull final Intent intent);

    /**
     * * Returns true if {@link #onStopCurrentWork()} has been called.  You can use this,
     * while executing your work, to see if it should be stopped.
     * @return stopped flag
     */
    public boolean isStopped() {
        return mStopped;
    }

    /**
     * This will be called if the JobScheduler has decided to stop this job.  The job for
     * this service does not have any constraints specified, so this will only generally happen
     * if the service exceeds the job's maximum execution time.
     *
     * @return True to indicate to the JobManager whether you'd like to reschedule this work,
     * false to drop this and all following work. Regardless of the value returned, your service
     * must stop executing or the system will ultimately kill it.  The default implementation
     * returns true, and that is most likely what you want to return as well (so no work gets
     * lost).
     */
    public boolean onStopCurrentWork() {
        return true;
    }

    /**
     *
     * @return Flag to stop current work
     */
    boolean doStopCurrentWork() {
        mStopped = true;
        return onStopCurrentWork();
    }

    /**
     *
     * @param reportStarted Report started flag
     */
    private void ensureProcessorRunningLocked(final boolean reportStarted) {
        Logger.d(TAG, "ensureProcessorRunningLocked");
        if (mServiceHandler != null) {
            if (mCompatWorkEnqueuer != null && reportStarted) {
                mCompatWorkEnqueuer.serviceProcessingStarted();
                Logger.d(TAG, "Starting handler with WAKE_LOCK: " + mServiceHandler);
            }
        }
    }

    /**
     * Default empty constructor.
     */
    public SmartJobIntentService() {
        super();
        serviceName = getClass().getSimpleName();
    }

    /**
     * Non empty constructor.
     * @param sName service name
     */
    public SmartJobIntentService(final String sName) {
        super();
        serviceName = sName;
    }

}