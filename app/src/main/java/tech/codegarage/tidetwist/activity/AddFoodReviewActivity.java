package tech.codegarage.tidetwist.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.hsalf.smilerating.SmileRating;
import com.jaeger.library.StatusBarUtil;
import com.reversecoder.library.event.OnSingleClickListener;
import com.reversecoder.library.network.NetworkManager;
import com.reversecoder.library.storage.SessionManager;
import com.reversecoder.library.util.AllSettingsManager;

import org.parceler.Parcels;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import tech.codegarage.tidetwist.R;
import tech.codegarage.tidetwist.base.BaseActivity;
import tech.codegarage.tidetwist.model.AppUser;
import tech.codegarage.tidetwist.model.FoodItem;
import tech.codegarage.tidetwist.model.ParamReviewItem;
import tech.codegarage.tidetwist.model.Review;
import tech.codegarage.tidetwist.retrofit.APIClient;
import tech.codegarage.tidetwist.retrofit.APIInterface;
import tech.codegarage.tidetwist.retrofit.APIResponse;
import tech.codegarage.tidetwist.util.AllConstants;
import tech.codegarage.tidetwist.util.KeyboardManager;
import tech.codegarage.tidetwist.util.Logger;
import tech.codegarage.tidetwist.view.CanaroTextView;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class AddFoodReviewActivity extends BaseActivity {

    //ToolbarTag()
    ImageView ivBack;
    CanaroTextView tvTitle;
    private EditText edtAddReview;

    private Button btnAddReview;
    private SmileRating smileRating;
    private APIInterface mApiInterface;
    private AppUser mAppUser;
    private int ratingStar = -1;
    private FoodItem mFoodItem;

    private AddReviewTask addReviewTask;

    @Override
    public String[] initActivityPermissions() {
        return new String[]{};
    }

    @Override
    public int initActivityLayout() {
        return R.layout.activity_add_food_review;
    }

    @Override
    public void initStatusBarView() {
        StatusBarUtil.setTransparent(getActivity());
    }

    @Override
    public void initNavigationBarView() {

    }

    @Override
    public void initIntentData(Bundle savedInstanceState, Intent intent) {
        if (intent != null) {
            Parcelable mParcelableFoodItem = intent.getParcelableExtra(AllConstants.INTENT_KEY_FOOD_ITEM);

            if (mParcelableFoodItem != null) {
                mFoodItem = Parcels.unwrap(mParcelableFoodItem);
                Logger.d(TAG, TAG + " >>> " + "mFoodItem: " + mFoodItem.toString());
            }
        }
    }

    @Override
    public void initActivityViews() {
        ivBack = (ImageView) findViewById(R.id.iv_back);
        tvTitle = (CanaroTextView) findViewById(R.id.tv_title);
        tvTitle.setText(getString(R.string.title_activity_review));
        edtAddReview = (EditText) findViewById(R.id.edt_add_review);
        smileRating = (SmileRating) findViewById(R.id.smile_rating);
        btnAddReview = (Button) findViewById(R.id.btn_add_review);
    }

    @Override
    public void initActivityViewsData(Bundle savedInstanceState) {
        mApiInterface = APIClient.getClient(getActivity()).create(APIInterface.class);

        String appUser = SessionManager.getStringSetting(getActivity(), AllConstants.SESSION_KEY_USER);
        if (!AllSettingsManager.isNullOrEmpty(appUser)) {
            mAppUser = APIResponse.getResponseObject(appUser, AppUser.class);
            Logger.d(TAG, TAG + " >>> " + "mAppUser: " + mAppUser.toString());
        }
    }

    @Override
    public void initActivityActions(Bundle savedInstanceState) {
        ivBack.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                initActivityBackPress();
            }
        });

        btnAddReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hide keyboard
                KeyboardManager.hideKeyboard(getActivity());

                //Check internet connection
                if (!NetworkManager.isConnected(getActivity())) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                    return;
                }

                //Check empty field
                doAddReview();
            }
        });

        smileRating.setOnSmileySelectionListener(new SmileRating.OnSmileySelectionListener() {
            @Override
            public void onSmileySelected(int smiley, boolean reselected) {
                switch (smiley) {
                    case SmileRating.BAD:
                        Logger.d(TAG, TAG + " >>> " + "BAD");
                    case SmileRating.GOOD:
                        Logger.d(TAG, TAG + " >>> " + "GOOD");
                        break;
                    case SmileRating.GREAT:
                        Logger.d(TAG, TAG + " >>> " + "GREAT");
                        break;
                    case SmileRating.OKAY:
                        Logger.d(TAG, TAG + " >>> " + "OKAY");
                        break;
                    case SmileRating.TERRIBLE:
                        Logger.d(TAG, TAG + " >>> " + "TERRIBLE");
                        break;
                    case SmileRating.NONE:
                        Logger.d(TAG, TAG + " >>> " + "NONE");
                        break;
                }

            }
        });
        smileRating.setOnRatingSelectedListener(new SmileRating.OnRatingSelectedListener() {
            @Override
            public void onRatingSelected(int level, boolean reselected) {
                ratingStar = level;
                Logger.d(TAG, TAG + " >>> " + "ratingStar: " + ratingStar + " reselected: " + reselected);
            }
        });
    }

    private void doAddReview() {
        String etAddReview = edtAddReview.getText().toString();

        if (ratingStar == -1) {
            Toast.makeText(getActivity(), getString(R.string.toast_please_select_rating), Toast.LENGTH_SHORT).show();
            return;
        }

        if (AllSettingsManager.isNullOrEmpty(etAddReview)) {
            Toast.makeText(getActivity(), getString(R.string.toast_please_give_some_feedback), Toast.LENGTH_SHORT).show();
            return;
        }

        //User Review Add
        ParamReviewItem paramReview = new ParamReviewItem(mFoodItem.getProduct_id(), mAppUser.getApp_user_id(), edtAddReview.getText().toString(), ratingStar + "", "0");
        addReviewTask = new AddReviewTask(getActivity(), paramReview);
        addReviewTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void initActivityOnResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void initActivityBackPress() {
        finish();
    }

    @Override
    public void initActivityDestroyTasks() {
        dismissProgressDialog();

        if (addReviewTask != null && addReviewTask.getStatus() == AsyncTask.Status.RUNNING) {
            addReviewTask.cancel(true);
        }
    }

    @Override
    public void initActivityPermissionResult(int requestCode, String[] permissions, int[] grantResults) {

    }


    @Override
    public void onResume() {
        super.onResume();


    }

    private class AddReviewTask extends AsyncTask<String, Integer, Response> {

        Context mContext;
        ParamReviewItem paramsReview;

        public AddReviewTask(Context context, ParamReviewItem paramReview) {
            mContext = context;
            paramsReview = paramReview;
        }

        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected Response doInBackground(String... params) {
            try {
                Logger.d(TAG, TAG + " >>> " + "paramsReview: " + paramsReview.toString());
                Call<APIResponse<List<Review>>> call = mApiInterface.apiAddReview(paramsReview);

                Response response = call.execute();
                Logger.d(TAG, TAG + " >>> " + "response: " + response);
                if (response.isSuccessful()) {
                    return response;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Response result) {
            try {
                dismissProgressDialog();

                if (result != null && result.isSuccessful()) {
                    Logger.d(TAG, "APIResponse(AddReviewTask): onResponse-server = " + result.toString());
                    APIResponse<List<Review>> data = (APIResponse<List<Review>>) result.body();
                    Logger.d("ItemReviewList", data.toString() + "");

                    if (data != null && data.getStatus().equalsIgnoreCase("1")) {
                        Logger.d(TAG, "APIResponse(AddReviewTask()): onResponse-object = " + data.toString());

                        if (data.getData().size() > 0) {
                            Review review = data.getData().get(0);
                            Logger.d(TAG, "review>>>" + review.toString());

                            Intent intentAdd = new Intent();
                            intentAdd.putExtra(AllConstants.INTENT_KEY_FOOD_REVIEW, Parcels.wrap(review));
                            setResult(RESULT_OK, intentAdd);
                            finish();
                        }
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.toast_no_info_found), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_could_not_retrieve_info), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
