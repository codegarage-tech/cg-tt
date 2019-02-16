package tech.codegarage.tidetwist.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.expansionpanel.ExpansionHeader;
import com.github.florent37.expansionpanel.ExpansionLayout;
import com.jaeger.library.StatusBarUtil;
import com.nex3z.flowlayout.FlowLayout;
import com.nex3z.flowlayout.FlowLayoutManager;
import com.reversecoder.library.event.OnSingleClickListener;
import com.reversecoder.library.network.NetworkManager;
import com.reversecoder.library.storage.SessionManager;
import com.reversecoder.library.util.AllSettingsManager;

import net.gotev.speech.GoogleVoiceTypingDisabledException;
import net.gotev.speech.Speech;
import net.gotev.speech.SpeechDelegate;
import net.gotev.speech.SpeechRecognitionNotAvailable;
import net.gotev.speech.SpeechUtil;
import net.gotev.speech.ui.SpeechProgressView;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import tech.codegarage.tidetwist.R;
import tech.codegarage.tidetwist.base.BaseActivity;
import tech.codegarage.tidetwist.enumeration.KitchenType;
import tech.codegarage.tidetwist.model.VoiceSearchItem;
import tech.codegarage.tidetwist.realm.DataManager;
import tech.codegarage.tidetwist.realm.RealmManager;
import tech.codegarage.tidetwist.util.Logger;
import tech.codegarage.tidetwist.util.XmlManager;
import tech.codegarage.tidetwist.view.CanaroTextView;

import static tech.codegarage.tidetwist.util.AllConstants.ASSETS_PATH_VOICE_SEARCH_KEYWORD;
import static tech.codegarage.tidetwist.util.AllConstants.SESSION_KEY_SELECTED_VOICE_SEARCH;
import static tech.codegarage.tidetwist.util.AllConstants.TABLE_KEY_VOICE_SEARCH_ITEM_CATEGORY;
import static tech.codegarage.tidetwist.util.AllConstants.TABLE_KEY_VOICE_SEARCH_KEY_1;
import static tech.codegarage.tidetwist.util.AllConstants.TABLE_KEY_VOICE_SEARCH_KEY_10;
import static tech.codegarage.tidetwist.util.AllConstants.TABLE_KEY_VOICE_SEARCH_KEY_2;
import static tech.codegarage.tidetwist.util.AllConstants.TABLE_KEY_VOICE_SEARCH_KEY_3;
import static tech.codegarage.tidetwist.util.AllConstants.TABLE_KEY_VOICE_SEARCH_KEY_4;
import static tech.codegarage.tidetwist.util.AllConstants.TABLE_KEY_VOICE_SEARCH_KEY_5;
import static tech.codegarage.tidetwist.util.AllConstants.TABLE_KEY_VOICE_SEARCH_KEY_6;
import static tech.codegarage.tidetwist.util.AllConstants.TABLE_KEY_VOICE_SEARCH_KEY_7;
import static tech.codegarage.tidetwist.util.AllConstants.TABLE_KEY_VOICE_SEARCH_KEY_8;
import static tech.codegarage.tidetwist.util.AllConstants.TABLE_KEY_VOICE_SEARCH_KEY_9;
import static tech.codegarage.tidetwist.util.AllConstants.TAG_VOICE_SEARCH_KEYWORD;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class VoiceSearchActivity extends BaseActivity {

    //Toolbar
    private ImageView ivBack;
    private CanaroTextView tvTitle;
    private LinearLayout llMenu;

    private LinearLayout llSearchProgressView;
    private SpeechProgressView speechProgressView;
    private Button btnVoiceSearch;
    private TextView tvTranslatedText;

    // Flow Layout
    private FlowLayout flowLayoutVoiceSearchCategory;
    private FlowLayoutManager flowLayoutManagerVoiceSearchCategory;
    private TextView tvVoiceSearchCategory;
    private KitchenType mVoiceSearchCategory = KitchenType.NONE;
    private ExpansionLayout expansionLayout;
    private ExpansionHeader expansionHeader;

    @Override
    public String[] initActivityPermissions() {
        return new String[]{};
    }

    @Override
    public int initActivityLayout() {
        return R.layout.activity_voice_search;
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

    }

    @Override
    public void initActivityViews() {
        ivBack = (ImageView) findViewById(R.id.iv_back);
        tvTitle = (CanaroTextView) findViewById(R.id.tv_title);
        tvTitle.setText(getString(R.string.title_activity_voice_search));
        llMenu = (LinearLayout) findViewById(R.id.ll_menu);

        //Flow layout
        flowLayoutVoiceSearchCategory = (FlowLayout) findViewById(R.id.fl_voice_search_type);
        tvVoiceSearchCategory = (TextView) findViewById(R.id.tv_voice_search_category);
        expansionHeader = (ExpansionHeader) findViewById(R.id.expansion_header);
        expansionLayout = (ExpansionLayout) findViewById(R.id.expansion_layout);

        llSearchProgressView = (LinearLayout) findViewById(R.id.ll_search_progress_view);
        tvTranslatedText = (TextView) findViewById(R.id.tv_translated_text);
        speechProgressView = (SpeechProgressView) findViewById(R.id.speech_progress_view);
        btnVoiceSearch = (Button) findViewById(R.id.btn_voice_search);
    }

    @Override
    public void initActivityViewsData(Bundle savedInstanceState) {
        initFlowLayoutVoiceSearchCategory();

        //Initialize speech to text
        Speech.init(getActivity(), getPackageName());
        int[] colors = {
                ContextCompat.getColor(this, android.R.color.holo_blue_dark),
                ContextCompat.getColor(this, android.R.color.holo_green_dark),
                ContextCompat.getColor(this, android.R.color.holo_purple),
                ContextCompat.getColor(this, android.R.color.holo_orange_dark),
                ContextCompat.getColor(this, android.R.color.holo_red_dark)
        };
        speechProgressView.setColors(colors);

        //Initialize voice search keyword
        initVoiceSearchKeyword();
    }

    @Override
    public void initActivityActions(Bundle savedInstanceState) {
        ivBack.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                initActivityBackPress();
            }
        });

        btnVoiceSearch.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                //Check internet connection
                if (!NetworkManager.isConnected(getActivity())) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                    return;
                }
                //Check there if any category is selected or not
                if (mVoiceSearchCategory == KitchenType.NONE) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_please_select_any_voice_search_category), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Speech.getInstance().isListening()) {
                    Speech.getInstance().stopListening();
                } else {
                    btnVoiceSearch.setVisibility(View.GONE);
                    speechProgressView.setVisibility(View.VISIBLE);

                    try {
                        Speech.getInstance().stopTextToSpeech();
                        Speech.getInstance().startListening(speechProgressView, new SpeechDelegate() {
                            @Override
                            public void onStartOfSpeech() {
                                tvTranslatedText.setText("");
                            }

                            @Override
                            public void onSpeechRmsChanged(float value) {
                            }

                            @Override
                            public void onSpeechPartialResults(List<String> results) {
                                Logger.d(TAG, "onSpeechPartialResults: " + results);
                                String result = "";
                                for (String partial : results) {
                                    result = result + partial + " ";
                                }
                                if (AllSettingsManager.isNullOrEmpty(result.trim())) {
                                    tvTranslatedText.setText(getString(R.string.view_tap_mic_icon_from_below_and_say_whatever_you_want_to_search));
                                } else {
                                    tvTranslatedText.setText(getString(R.string.view_searching_kitchen_using) + " \"" + result + "\" " + getString(R.string.view_keyword));
                                }
                            }

                            @Override
                            public void onSpeechResult(String result) {
                                Logger.d(TAG, "onSpeechResult: " + result);
                                btnVoiceSearch.setVisibility(View.VISIBLE);
                                speechProgressView.setVisibility(View.GONE);

                                if (AllSettingsManager.isNullOrEmpty(result)) {
                                    Speech.getInstance().say(getString(R.string.view_repeat_please));
                                    tvTranslatedText.setText(getString(R.string.view_tap_mic_icon_from_below_and_say_whatever_you_want_to_search));
                                } else {
                                    tvTranslatedText.setText(getString(R.string.view_searching_kitchen_using) + " \"" + result + "\" " + getString(R.string.view_keyword));

                                    //Search keyword into database
                                    if (!AllSettingsManager.isNullOrEmpty(mVoiceSearchCategory.toString())) {
                                        RealmManager mRealmManager = RealmManager.with(getActivity());
                                        Realm mRealm = mRealmManager.getRealm();
                                        VoiceSearchItem mVoiceSearchItem = (VoiceSearchItem) mRealm.where(VoiceSearchItem.class)
                                                .equalTo(TABLE_KEY_VOICE_SEARCH_ITEM_CATEGORY, mVoiceSearchCategory.toString())
                                                .and()
                                                .beginGroup()
                                                .contains(TABLE_KEY_VOICE_SEARCH_KEY_1, result, Case.INSENSITIVE)
                                                .contains(TABLE_KEY_VOICE_SEARCH_KEY_2, result, Case.INSENSITIVE)
                                                .contains(TABLE_KEY_VOICE_SEARCH_KEY_3, result, Case.INSENSITIVE)
                                                .contains(TABLE_KEY_VOICE_SEARCH_KEY_4, result, Case.INSENSITIVE)
                                                .contains(TABLE_KEY_VOICE_SEARCH_KEY_5, result, Case.INSENSITIVE)
                                                .contains(TABLE_KEY_VOICE_SEARCH_KEY_6, result, Case.INSENSITIVE)
                                                .contains(TABLE_KEY_VOICE_SEARCH_KEY_7, result, Case.INSENSITIVE)
                                                .contains(TABLE_KEY_VOICE_SEARCH_KEY_8, result, Case.INSENSITIVE)
                                                .contains(TABLE_KEY_VOICE_SEARCH_KEY_9, result, Case.INSENSITIVE)
                                                .contains(TABLE_KEY_VOICE_SEARCH_KEY_10, result, Case.INSENSITIVE)
                                                .endGroup().findFirst();
                                        if (mVoiceSearchItem != null) {
                                            Logger.d(TAG, "mVoiceSearchItem: " + mVoiceSearchItem.toString());
                                        } else {
                                            Logger.d(TAG, "No search keyword found");
                                        }
                                    }
                                }
                            }
                        });
                    } catch (SpeechRecognitionNotAvailable exc) {
                        showSpeechNotSupportedDialog();

                    } catch (GoogleVoiceTypingDisabledException exc) {
                        showEnableGoogleVoiceTyping();
                    }
                }
            }
        });
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
        Speech.getInstance().shutdown();
    }

    @Override
    public void initActivityPermissionResult(int requestCode, String[] permissions, int[] grantResults) {

    }

    private void initVoiceSearchKeyword() {
        Document document = XmlManager.loadXmlFromAsset(getActivity(), ASSETS_PATH_VOICE_SEARCH_KEYWORD);
        NodeList nodeList = XmlManager.getElementsByTagName(document, TAG_VOICE_SEARCH_KEYWORD);

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            NamedNodeMap attList = node.getAttributes();
            List<String> data = new ArrayList<>();
            for (int a = 0; a < attList.getLength(); a++) {
                Node att = attList.item(a);
                data.add(att.getNodeValue() != null ? att.getNodeValue() : "");
            }

            //Insert data into database
            VoiceSearchItem voiceSearchItem = new VoiceSearchItem();
            voiceSearchItem.setItemCategory(data.get(0));
            voiceSearchItem.setItemId(data.get(1));
            voiceSearchItem.setItemName(data.get(2));
            voiceSearchItem.setKey1(data.get(3));
            voiceSearchItem.setKey2(data.get(4));
            voiceSearchItem.setKey3(data.get(5));
            voiceSearchItem.setKey4(data.get(6));
            voiceSearchItem.setKey5(data.get(7));
            voiceSearchItem.setKey6(data.get(8));
            voiceSearchItem.setKey7(data.get(9));
            voiceSearchItem.setKey8(data.get(10));
            voiceSearchItem.setKey9(data.get(11));
            voiceSearchItem.setKey10(data.get(12));

            Logger.d(TAG, "voiceSearchItem: " + voiceSearchItem.toString());
            DataManager.storeVoiceSearchItem(getActivity(), voiceSearchItem);
//            insertLanguageMapEntry(data.get(0), data.get(1), data.get(2),
//                    data.size() > 3 ? data.get(3) : "",
//                    data.size() > 4 ? data.get(4) : "",
//                    data.size() > 5 ? data.get(5) : "",
//                    data.size() > 6 ? data.get(6) : "",
//                    data.size() > 7 ? data.get(7) : "",
//                    data.size() > 8 ? data.get(8) : ""
//            );
        }

    }

    private void showSpeechNotSupportedDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        SpeechUtil.redirectUserToGoogleAppOnPlayStore(getActivity());
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.view_speech_not_available)
                .setCancelable(false)
                .setPositiveButton(R.string.view_yes, dialogClickListener)
                .setNegativeButton(R.string.view_no, dialogClickListener)
                .show();
    }

    private void showEnableGoogleVoiceTyping() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.view_enable_google_voice_typing)
                .setCancelable(false)
                .setPositiveButton(R.string.view_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // do nothing
                    }
                })
                .show();
    }

    /***************************
     * Methods for flow layout *
     ***************************/
    private void initFlowLayoutVoiceSearchCategory() {
        //Arrange voice search category key
        List<String> mVoiceSearchCategoryKeys = new ArrayList<>();
        mVoiceSearchCategoryKeys.add(KitchenType.TIME.toString());
        mVoiceSearchCategoryKeys.add(KitchenType.AREA.toString());
        mVoiceSearchCategoryKeys.add(KitchenType.CUISINE.toString());
        mVoiceSearchCategoryKeys.add(KitchenType.KITCHEN_NAME.toString());
        mVoiceSearchCategoryKeys.add(KitchenType.FOOD_NAME.toString());

        //Set flow layout with voice search category key
        flowLayoutManagerVoiceSearchCategory = new FlowLayoutManager.FlowViewBuilder(getActivity(), flowLayoutVoiceSearchCategory, mVoiceSearchCategoryKeys, new FlowLayoutManager.onFlowViewClick() {
            @Override
            public void flowViewClick(TextView updatedTextView) {
                List<TextView> selectedVoiceSearchKeys = flowLayoutManagerVoiceSearchCategory.getSelectedFlowViews();
                String mSelectedVoiceSearchKey = (selectedVoiceSearchKeys.size() > 0) ? selectedVoiceSearchKeys.get(0).getText().toString() : "";

                //Select selected voice search key
                if (AllSettingsManager.isNullOrEmpty(mSelectedVoiceSearchKey)) {
                    tvVoiceSearchCategory.setText(getString(R.string.view_voice_search_category));
                    mVoiceSearchCategory = KitchenType.NONE;
                } else {
                    tvVoiceSearchCategory.setText(mSelectedVoiceSearchKey);
                    mVoiceSearchCategory = KitchenType.getKitchenType(mSelectedVoiceSearchKey);

                    //Close the flow layout
                    expansionLayout.toggle(true);
                }
                //Save selected voice search key
                SessionManager.setStringSetting(getActivity(), SESSION_KEY_SELECTED_VOICE_SEARCH, mVoiceSearchCategory.toString());

                //Set translated default text
                tvTranslatedText.setText(getString(R.string.view_tap_mic_icon_from_below_and_say_whatever_you_want_to_search));
            }
        })
                .setSingleChoice(true)
                .build();

        //Set last selected voice search key
        String mLastSelectedVoiceSearchKey = SessionManager.getStringSetting(getActivity(), SESSION_KEY_SELECTED_VOICE_SEARCH);
        if (!AllSettingsManager.isNullOrEmpty(mLastSelectedVoiceSearchKey)) {
            flowLayoutManagerVoiceSearchCategory.clickFlowView(mLastSelectedVoiceSearchKey);
        } else {
            mVoiceSearchCategory = KitchenType.NONE;
            tvVoiceSearchCategory.setText(getString(R.string.view_voice_search_category));
        }
    }
}