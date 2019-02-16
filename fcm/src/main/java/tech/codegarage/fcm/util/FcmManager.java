package tech.codegarage.fcm.util;

import android.content.Context;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class FcmManager {

    private static FcmBuilder mFcmBuilder;
    private Context mContext;
    private static FlavorType mFlavorType;

    public static FcmManager initFcmManager(Context context, String flavorType, FcmBuilder fcmBuilder) {
        return new FcmManager(context, flavorType, fcmBuilder);
    }

    private FcmManager(Context context, String flavorType, FcmBuilder fcmBuilder) {
        mContext = context;
        mFcmBuilder = fcmBuilder;
        mFlavorType = FlavorType.valueOf(flavorType);
    }

    public static Class<?> getOrderContentClass() {
        return mFcmBuilder.getOrderContentClass();
    }

    public static Class<?> getNotificationContentClass() {
        return mFcmBuilder.getNotificationContentClass();
    }

    public static FlavorType getFlavorType() {
        return mFlavorType;
    }

    public static class FcmBuilder {
        private Class<?> orderContentClass, notificationContentClass;

        public FcmBuilder() {
        }

        public FcmBuilder setOrderContentClass(Class<?> orderContentClass) {
            this.orderContentClass = orderContentClass;
            return this;
        }

        public Class<?> getOrderContentClass() {
            return orderContentClass;
        }

        public Class<?> getNotificationContentClass() {
            return notificationContentClass;
        }

        public void setNotificationContentClass(Class<?> notificationContentClass) {
            this.notificationContentClass = notificationContentClass;
        }

        public FcmBuilder buildGcmManager() {
            return this;
        }
    }
}