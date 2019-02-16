package tech.codegarage.tidetwist.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.reversecoder.library.event.OnSingleClickListener;
import com.reversecoder.library.segmentradiogroup.SegmentedRadioGroup;
import com.reversecoder.library.storage.SessionManager;
import com.reversecoder.library.util.AllSettingsManager;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

import tech.codegarage.tidetwist.R;
import tech.codegarage.tidetwist.base.BaseFragment;
import tech.codegarage.tidetwist.model.AppUser;
import tech.codegarage.tidetwist.retrofit.APIResponse;
import tech.codegarage.tidetwist.util.AppUtil;
import tech.codegarage.tidetwist.util.ValidationManager;

import static tech.codegarage.tidetwist.util.AllConstants.SESSION_KEY_USER;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class CheckoutDeliveryInfoFragment extends BaseFragment {

    private EditText edtUserAddress, edtUserPhone, edtUserName, edtUserEmail;
    private TextView tvDeliveryDate, tvDeliveryTime;
    private String currentAddress = "", preferredAddress = "";
    private int selectedDate[], selectedTime[];
    private SegmentedRadioGroup srgAddressType;
    private RadioButton srgRbtnCurrentAddress, srgRbtnPreferredAddress;
    private AppUser mAppUser;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    public static CheckoutDeliveryInfoFragment newInstance() {
        CheckoutDeliveryInfoFragment fragment = new CheckoutDeliveryInfoFragment();
        return fragment;
    }

    @Override
    public int initFragmentLayout() {
        return R.layout.fragment_checkout_delivery_info;
    }

    @Override
    public void initFragmentBundleData(Bundle bundle) {

    }

    @Override
    public void initFragmentViews(View parentView) {
        edtUserAddress = (EditText) parentView.findViewById(R.id.edt_user_address);
        edtUserPhone = (EditText) parentView.findViewById(R.id.edt_user_phone);
        edtUserName = (EditText) parentView.findViewById(R.id.edt_user_name);
        edtUserEmail = (EditText) parentView.findViewById(R.id.edt_user_email);
        tvDeliveryDate = (TextView) parentView.findViewById(R.id.tv_delivery_date);
        tvDeliveryTime = (TextView) parentView.findViewById(R.id.tv_delivery_time);
        srgAddressType = (SegmentedRadioGroup) parentView.findViewById(R.id.srg_address_type);
        srgRbtnCurrentAddress = (RadioButton) parentView.findViewById(R.id.srg_rbtn_current_address);
        srgRbtnPreferredAddress = (RadioButton) parentView.findViewById(R.id.srg_rbtn_preferred_address);
    }

    @Override
    public void initFragmentViewsData() {
        String appUser = SessionManager.getStringSetting(getActivity(), SESSION_KEY_USER);
        if (!AllSettingsManager.isNullOrEmpty(appUser)) {
            mAppUser = APIResponse.getResponseObject(appUser, AppUser.class);

            edtUserPhone.setText(mAppUser.getPhone().startsWith("88") ? mAppUser.getPhone().substring(2, mAppUser.getPhone().length()) : mAppUser.getPhone());
            edtUserName.setText(mAppUser.getName());
            edtUserEmail.setText(mAppUser.getEmail());
        }
    }

    @Override
    public void initFragmentActions() {
        srgAddressType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.srg_rbtn_current_address:
                        preferredAddress = edtUserAddress.getText().toString();

                        edtUserAddress.setTextColor(getResources().getColor(R.color.Off_white));
                        edtUserAddress.setText(currentAddress);
                        edtUserAddress.setEnabled(false);
                        break;
                    case R.id.srg_rbtn_preferred_address:
                        edtUserAddress.setText((AllSettingsManager.isNullOrEmpty(preferredAddress) ? mAppUser.getAddress() : preferredAddress));
                        edtUserAddress.setEnabled(true);
                        break;
                }
            }
        });

        tvDeliveryDate.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                initDatePicker();
            }
        });

        tvDeliveryTime.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                initTimePicker();
            }
        });
    }

    @Override
    public void initFragmentBackPress() {
    }

    public void setUserCurrentAddress(String address) {
        currentAddress = address;
        if (srgRbtnCurrentAddress.isChecked()) {
            edtUserAddress.setText(currentAddress);
            edtUserAddress.setEnabled(false);
        }
    }

    @Override
    public void initFragmentOnResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void initFragmentUpdate(Object object) {

    }

    /***********************************************
     * Getter methods for accessing fragments data *
     ***********************************************/
    public boolean isAllFieldsVerified() {
        if (AllSettingsManager.isNullOrEmpty(edtUserAddress.getText().toString())) {
            Toast.makeText(getActivity(), getString(R.string.toast_please_input_your_address), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (tvDeliveryDate.getText().toString().equalsIgnoreCase(getString(R.string.view_delivery_date))) {
            Toast.makeText(getActivity(), getString(R.string.toast_please_input_your_delivery_date), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (tvDeliveryTime.getText().toString().equalsIgnoreCase(getString(R.string.view_delivery_time))) {
            Toast.makeText(getActivity(), getString(R.string.toast_please_input_your_delivery_time), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (AllSettingsManager.isNullOrEmpty(edtUserPhone.getText().toString())) {
            Toast.makeText(getActivity(), getString(R.string.toast_please_input_your_mobile_no), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!ValidationManager.isValidBangladeshiMobileNumber(edtUserPhone.getText().toString())) {
            Toast.makeText(getActivity(), getString(R.string.toast_please_input_valid_mobile_no), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (AllSettingsManager.isNullOrEmpty(edtUserName.getText().toString())) {
            Toast.makeText(getActivity(), getString(R.string.toast_please_input_your_name), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (AllSettingsManager.isNullOrEmpty(edtUserEmail.getText().toString())) {
            Toast.makeText(getActivity(), getString(R.string.toast_please_input_your_email), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!ValidationManager.isValidEmail(edtUserEmail.getText().toString())) {
            Toast.makeText(getActivity(), getString(R.string.toast_please_input_valid_email), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public String getUserAddress() {
        return edtUserAddress.getText().toString();
    }

    public String getDeliveryTime() {
        return tvDeliveryDate.getText().toString()+" "+tvDeliveryTime.getText().toString();
    }

    public String getUserPhone() {
        return edtUserPhone.getText().toString();
    }

    public String getUserName() {
        return edtUserName.getText().toString();
    }

    public String getUserEmail() {
        return edtUserEmail.getText().toString();
    }

    /****************************
     * Date time picker methods *
     ****************************/
    @Override
    public void onResume() {
        super.onResume();

        DatePickerDialog dpd = (DatePickerDialog) requireFragmentManager().findFragmentByTag(getString(R.string.view_delivery_date));
        if (dpd != null) dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                setDate(year, monthOfYear, dayOfMonth);
            }
        });

        TimePickerDialog tpd = (TimePickerDialog) requireFragmentManager().findFragmentByTag(getString(R.string.view_delivery_time));
        if (tpd != null) tpd.setOnTimeSetListener(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                setTime(hourOfDay, minute, second);
            }
        });
    }

    private void initDatePicker() {
        Calendar now = Calendar.getInstance();
        if (datePickerDialog == null) {
            datePickerDialog = DatePickerDialog.newInstance(
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                            setDate(year, monthOfYear, dayOfMonth);
                        }
                    },
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
        } else {
            datePickerDialog.initialize(
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                            setDate(year, monthOfYear, dayOfMonth);
                        }
                    },
                    (selectedDate != null) ? selectedDate[0] : now.get(Calendar.YEAR),
                    (selectedDate != null) ? selectedDate[1] : now.get(Calendar.MONTH),
                    (selectedDate != null) ? selectedDate[2] : now.get(Calendar.DAY_OF_MONTH)
            );
        }
        datePickerDialog.setThemeDark(true);
        datePickerDialog.vibrate(true);
        datePickerDialog.dismissOnPause(true);
        datePickerDialog.setTitle(getString(R.string.view_pick_delivery_date));
        datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });
        datePickerDialog.show(getActivity().getSupportFragmentManager(), getString(R.string.view_delivery_date));
    }

    private void setDate(int... yearMonthOfYearDayOfMonth) {
        if (yearMonthOfYearDayOfMonth.length == 3) {
            //Store selected date info
            selectedDate = new int[yearMonthOfYearDayOfMonth.length];
            selectedDate[0] = yearMonthOfYearDayOfMonth[0];
            selectedDate[1] = yearMonthOfYearDayOfMonth[1];
            selectedDate[2] = yearMonthOfYearDayOfMonth[2];

            String selectedDate = yearMonthOfYearDayOfMonth[0] + "-" + (++yearMonthOfYearDayOfMonth[1]) + "-" + yearMonthOfYearDayOfMonth[2];
            tvDeliveryDate.setText(selectedDate);
        }
    }

    private void initTimePicker() {
        Calendar now = Calendar.getInstance();
        if (timePickerDialog == null) {
            timePickerDialog = TimePickerDialog.newInstance(
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                            setTime(hourOfDay, minute, second);
                        }
                    },
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE),
                    false
            );
        } else {
            timePickerDialog.initialize(
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                            setTime(hourOfDay, minute, second);
                        }
                    },
                    (selectedTime != null) ? selectedTime[0] : now.get(Calendar.HOUR_OF_DAY),
                    (selectedTime != null) ? selectedTime[1] : now.get(Calendar.MINUTE),
                    (selectedTime != null) ? selectedTime[2] : now.get(Calendar.SECOND),
                    false
            );
        }
        timePickerDialog.setThemeDark(true);
        timePickerDialog.vibrate(true);
        timePickerDialog.dismissOnPause(true);
        timePickerDialog.setTitle(getString(R.string.view_pick_delivery_time));
        timePickerDialog.setVersion(TimePickerDialog.Version.VERSION_2);
        timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });
        timePickerDialog.show(getActivity().getSupportFragmentManager(), getString(R.string.view_delivery_time));
    }

    private void setTime(int... hourOfDayMinuteSecond) {
        if (hourOfDayMinuteSecond.length == 3) {
            //Store selected time info
            selectedTime = new int[hourOfDayMinuteSecond.length];
            selectedTime[0] = hourOfDayMinuteSecond[0];
            selectedTime[1] = hourOfDayMinuteSecond[1];
            selectedTime[2] = hourOfDayMinuteSecond[2];

            tvDeliveryTime.setText(AppUtil.get12HourTime(hourOfDayMinuteSecond[0], hourOfDayMinuteSecond[1], hourOfDayMinuteSecond[2]));
        }
    }
}