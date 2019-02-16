package tech.codegarage.fcm.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import static tech.codegarage.fcm.util.FcmUtil.copyToClipboard;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class CopyToClipboardActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        copyToClipboard(this, getIntent().getCharSequenceExtra(Intent.EXTRA_TEXT));
        finish();
    }
}