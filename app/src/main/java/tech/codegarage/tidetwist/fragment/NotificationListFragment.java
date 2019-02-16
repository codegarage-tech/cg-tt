package tech.codegarage.tidetwist.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.jude.easyrecyclerview.EasyRecyclerView;

import tech.codegarage.fcm.payload.Payload;
import tech.codegarage.tidetwist.R;
import tech.codegarage.tidetwist.adapter.NotificationListAdapter;
import tech.codegarage.tidetwist.base.BaseFragment;
import tech.codegarage.tidetwist.util.Logger;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class NotificationListFragment extends BaseFragment {

    private EasyRecyclerView rvNotification;
    private NotificationListAdapter notificationListAdapter;

    //Receivers
    private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener;

    public static NotificationListFragment newInstance() {
        NotificationListFragment fragment = new NotificationListFragment();
        return fragment;
    }

    @Override
    public int initFragmentLayout() {
        return R.layout.fragment_notification_list;
    }

    @Override
    public void initFragmentBundleData(Bundle bundle) {
    }

    @Override
    public void initFragmentViews(View parentView) {
        rvNotification = (EasyRecyclerView) parentView.findViewById(R.id.rv_notification);
    }

    @Override
    public void initFragmentViewsData() {
        notificationListAdapter = new NotificationListAdapter(getActivity());
        rvNotification.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvNotification.setAdapter(notificationListAdapter);
        notificationListAdapter.addAll(Payload.fetchPayloads(getActivity()));

        registerReceivers();
    }

    @Override
    public void initFragmentActions() {

    }

    @Override
    public void initFragmentBackPress() {

    }

    @Override
    public void initFragmentUpdate(Object object) {

    }

    @Override
    public void initFragmentOnResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onDestroy() {
        unregisterReceivers();
        super.onDestroy();

    }

    /********************
     * Receiver methods *
     ********************/
    private void registerReceivers() {
        Logger.d(TAG, TAG + ">> FCM: registering receiver");
        sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Logger.d(TAG, TAG + ">> FCM: shared preference changes found");

                if (sharedPreferences.contains(key)) {
                    Logger.d(TAG, TAG + ">> FCM: shared preference key is found");
                    final Payload payload = Payload.with(key, sharedPreferences.getString(key, ""));
                    notificationListAdapter.insert(payload, 0);
                    notificationListAdapter.notifyDataSetChanged();
                } else {
                    Logger.d(TAG, TAG + ">> FCM: shared preference key is not found");
                }
            }
        };
        Logger.d(TAG, TAG + ">> FCM: registering shared preference");
        Payload.registerOnSharedPreferenceChanges(getActivity(), sharedPreferenceChangeListener);
    }

    private void unregisterReceivers() {
        Logger.d(TAG, TAG + ">> FCM: unregistering shared preference");
        Payload.unregisterOnSharedPreferenceChanges(getActivity(), sharedPreferenceChangeListener);
    }
}