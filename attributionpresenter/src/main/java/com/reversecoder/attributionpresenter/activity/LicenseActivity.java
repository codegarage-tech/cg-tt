package com.reversecoder.attributionpresenter.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.reversecoder.attributionpresenter.R;
import com.reversecoder.attributionpresenter.adapter.AttributionAdapter;
import com.reversecoder.attributionpresenter.model.Attribution;
import com.reversecoder.attributionpresenter.model.Library;
import com.reversecoder.attributionpresenter.view.AnimatedImageView;
import com.reversecoder.attributionpresenter.view.AnimatedTextView;
import com.reversecoder.attributionpresenter.view.ArcView;

import java.util.ArrayList;

public class LicenseActivity extends AppCompatActivity {

    //toolbar
    ArcView arcMenuView;
    AnimatedImageView arcMenuImage;
    AnimatedTextView toolbarTitle;
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);

        initView();
    }

    private void initView() {

        initToolBar();

        ListView lvLicense = (ListView) findViewById(R.id.list);
        AttributionAdapter attributionAdapter = new AttributionAdapter(LicenseActivity.this);
        lvLicense.setAdapter(attributionAdapter);
        attributionAdapter.setData(getAllAttributions());
    }

    private void initToolBar() {
        toolbarTitle = (AnimatedTextView) findViewById(R.id.toolbarTitle);
        arcMenuImage = (AnimatedImageView) findViewById(R.id.arcImage);
        arcMenuView = (ArcView) findViewById(R.id.arcView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbarTitle.setAnimatedText(getString(R.string.title_activity_third_party_notice), 0L);

        arcMenuImage.setAnimatedImage(R.drawable.arrow_left, 0L);
        arcMenuView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private ArrayList<Attribution> getAllAttributions() {

        ArrayList<Attribution> attributions = new ArrayList<>();

        //Gradle projects
        attributions.add(Library.VOLLEY.getAttribution());
        attributions.add(Library.PICASSO.getAttribution());
        attributions.add(Library.GLIDE.getAttribution());
        attributions.add(Library.RETROFIT.getAttribution());
        attributions.add(Library.GSON.getAttribution());
        attributions.add(Library.PARCELER_JOHNCARL81.getAttribution());
        attributions.add(Library.ZXING_ANDROID_EMBEDDED_JOURNEYAPPS.getAttribution());
        attributions.add(Library.REALM.getAttribution());
        attributions.add(Library.EVENT_BUS.getAttribution());
        attributions.add(Library.MATERIAL_DATE_TIME_PICKER_WDULLAER.getAttribution());

        //Library projects
        attributions.add(Library.MATERIAL_ABOUT_LIBRARY_DANIELSTONEUK.getAttribution());
        attributions.add(Library.ANDROID_ABOUT_BOX_EGGHEADGAMES.getAttribution());
        attributions.add(Library.ANDROID_IMAGE_SLIDER_DAIMAJIA.getAttribution());
        attributions.add(Library.ATTRIBUTE_PRESENTER_FRANMONTIEL.getAttribution());
        attributions.add(Library.COOKIEBAR2_AVIRANABADY.getAttribution());
        attributions.add(Library.COUNTRY_PICKER_PO10CIO.getAttribution());
        attributions.add(Library.DROP_DOWN_MENU_PLUS_66668.getAttribution());
        attributions.add(Library.EASY_RECYCLERVIEW_JUDE95.getAttribution());
        attributions.add(Library.FCM_TOOLBOX_SIMONMARQUIS.getAttribution());
        attributions.add(Library.GLAZY_VIEWPAGER_KANNANANBU.getAttribution());
        attributions.add(Library.IMAGE_ZIPPER_AMANJEETSINGH150.getAttribution());
        attributions.add(Library.MATERIAL_RATINGBAR_DREAMINGINCODEZH.getAttribution());
        attributions.add(Library.MATISSE_ZHIHU.getAttribution());
        attributions.add(Library.MULTI_WAVE_HEADER_SCWANG90.getAttribution());
        attributions.add(Library.NIFTY_DIALOG_EFFECTS_SD6352051.getAttribution());
        attributions.add(Library.OPTION_ROUND_CARDVIEW_CAPTAINMIAO.getAttribution());
        attributions.add(Library.PARALLAX_RECYCLERVIEW_YAYAA.getAttribution());
        attributions.add(Library.RECOLOR_SIMMORSAL.getAttribution());
        attributions.add(Library.RIBBLE_MENU_ARMCHA.getAttribution());
        attributions.add(Library.ELEME_SHOPPING_VIEW_JEASONWONG.getAttribution());
        attributions.add(Library.SMILEY_RATING_SUJITHKANNA.getAttribution());
        attributions.add(Library.STATUSBAR_UTIL_LAOBIE.getAttribution());
        attributions.add(Library.STEPPER_INDICATOR_BADOUALY.getAttribution());
        attributions.add(Library.ANDROID_SWITCH_ICON.getAttribution());
        attributions.add(Library.TIMELINE_ANDROID_JIXIESHI999.getAttribution());
        attributions.add(Library.EXPANSION_PANEL_FLORENT7.getAttribution());
        attributions.add(Library.FLOW_LAYOUT_NEXZ.getAttribution());
        attributions.add(Library.WAVE_SWIPE_REFRESH_LAYOUT_RECRUITLIFESTYLE.getAttribution());
        attributions.add(Library.ANDROID_SPEECH_GOTEV.getAttribution());

        return attributions;
    }
}