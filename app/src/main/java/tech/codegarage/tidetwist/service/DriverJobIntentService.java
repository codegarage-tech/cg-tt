package tech.codegarage.tidetwist.service;

import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;

import com.reversecoder.library.network.NetworkManager;

import retrofit2.Call;
import retrofit2.Response;
import tech.codegarage.tidetwist.base.BaseJobIntentService;
import tech.codegarage.tidetwist.model.ParamDriverLocationUpdate;
import tech.codegarage.tidetwist.retrofit.APIClient;
import tech.codegarage.tidetwist.retrofit.APIInterface;
import tech.codegarage.tidetwist.retrofit.APIResponse;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class DriverJobIntentService extends BaseJobIntentService {

    public enum DRIVER_JOB_TYPE {LOCATION_UPDATE}

    @Override
    public <JobParam extends Parcelable> Bundle doBackgroundJob(String jobTypeName, JobParam jobParam) {
        Bundle bundle = new Bundle();
        try {
            DRIVER_JOB_TYPE driverJobType = DRIVER_JOB_TYPE.valueOf(jobTypeName);
            bundle.putString(KEY_TASK_TYPE, jobTypeName);

            switch (driverJobType) {
                case LOCATION_UPDATE:
                    //Prepare param for server request
                    Location location = (Location) jobParam;
                    ParamDriverLocationUpdate paramDriverLocationUpdate = new ParamDriverLocationUpdate(location.getLatitude() + "", location.getLongitude() + "");

                    //Server request for location update
                    if (NetworkManager.isConnected(this)) {
                        APIInterface apiInterface = APIClient.getClient(this).create(APIInterface.class);
                        Call<APIResponse> call = apiInterface.apiUpdateDriverLocation(paramDriverLocationUpdate);
                        Response response = call.execute();
                        if (response.isSuccessful()) {
                            APIResponse data = (APIResponse) response.body();
                            bundle.putString(KEY_TASK_RESULT, data.toString());
                        }
                    }
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return bundle;
    }
}