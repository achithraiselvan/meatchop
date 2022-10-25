package com.meatchop.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.meatchop.R;
import com.meatchop.utils.SettingsUtil;
import com.meatchop.widget.TMCTextView;

public class HelpActivity extends BaseActivity {

    private static final int PDFVIEW_ACT_REQ_CODE = 1;

    private TMCTextView makeacall_textview;
    private TMCTextView writetous_textview;
    private View writetous_layout;
    private View makeacall_layout;
    private View aboutus_layout;
    private View faqs_layout;
    private View termsandconditions_layout;
    private View privacypolicy_layout;

    private SettingsUtil settingsUtil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_help);

        settingsUtil = new SettingsUtil(this);

        makeacall_layout = findViewById(R.id.makeacall_layout);
        writetous_layout = findViewById(R.id.writetous_layout);
        makeacall_textview = (TMCTextView) findViewById(R.id.makeacall_textview);
        writetous_textview = (TMCTextView) findViewById(R.id.writetous_textview);
        makeacall_textview.setPaintFlags(makeacall_textview.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        writetous_textview.setPaintFlags(writetous_textview.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        View back_icon = findViewById(R.id.back_icon);
        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        makeacall_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String supportmobileno = settingsUtil.getSupportMobileNo();
                Intent callIntent = new Intent(
                        Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + supportmobileno)); // NO I18N
                startActivity(callIntent);
            }
        });

        writetous_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String supportmailid = settingsUtil.getSupportMailid();
                    String extratext = "Customer Mobile No: +" + settingsUtil.getMobile();
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{supportmailid});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "The Meat Chop feedback");
                    intent.putExtra(Intent.EXTRA_TEXT, extratext);
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException ex) {
                 // ToastUtil.showShortToast(getActivity(), "There are no email client installed on your device.");
                }
            }
        });

        aboutus_layout = findViewById(R.id.aboutus_layout);
        faqs_layout = findViewById(R.id.faqs_layout);
        termsandconditions_layout = findViewById(R.id.termsandconditions_layout);
        privacypolicy_layout = findViewById(R.id.privacypolicy_layout);

        faqs_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startHTMLViewActivity(HTMLViewActivity.TYPE_FAQS);
            }
        });

        aboutus_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startHTMLViewActivity(HTMLViewActivity.TYPE_ABOUTUS);
            }
        });

        termsandconditions_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startHTMLViewActivity(HTMLViewActivity.TYPE_TERMSANDCONDITIONS);
            }
        });

        privacypolicy_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startHTMLViewActivity(HTMLViewActivity.TYPE_PRIVACYPOLICY);
            }
        });
    }

    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    private void startHTMLViewActivity(int type) {
        Intent intent = new Intent(HelpActivity.this, HTMLViewActivity.class);
        intent.putExtra("type", type);
        startActivityForResult(intent, PDFVIEW_ACT_REQ_CODE);
        overridePendingTransition(R.anim.slide_from_right, R.anim.stay);
    }

}
