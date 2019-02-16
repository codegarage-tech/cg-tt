package tech.codegarage.tidetwist.util;

import android.content.Context;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Md. Rashadul Alam
 *         Email: rashed.droid@gmail.com
 */
public class SimManager {

    public static int getSimCount(Context context) {
        return getSubscriptionInfo(context).size();
    }

    public static List<SubscriptionInfo> getSubscriptionInfo(Context context) {
        List<SubscriptionInfo> sis = new ArrayList<>();
        if (isSimExists(context)) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                SubscriptionManager sm = SubscriptionManager.from(context);
                sis = sm.getActiveSubscriptionInfoList();
            }
        }
        return sis;
    }

    public static boolean isSimExists(Context context) {
        if (context != null) {
            TelephonyManager telephony = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            if (telephony != null) {
                return (telephony.getSimState() != TelephonyManager.SIM_STATE_ABSENT);
            }
        }
        return false;
    }

    public String getIccId(Context context, int index) {
        String iccId = "";
        if (isSimExists(context)) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                SubscriptionManager sm = SubscriptionManager.from(context);
                List<SubscriptionInfo> sis = sm.getActiveSubscriptionInfoList();
                if (sis != null && sis.size() > 0 && sis.size() <= 2 && index < sis.size()) {
                    switch (index) {
                        case 0:
                            iccId = sis.get(index).getIccId();
                            break;
                        case 1:
                            iccId = sis.get(index).getIccId();
                            break;
                    }
                }
            }
        }
        return iccId;
    }
}