package com.meatchop.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.text.SpannedString;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;

import com.meatchop.R;
import com.meatchop.widget.TMCTextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class HTMLViewActivity extends BaseActivity {

    public static final int TYPE_ABOUTUS = 0;
    public static final int TYPE_TERMSANDCONDITIONS = 1;
    public static final int TYPE_FAQS = 3;
    public static final int TYPE_PRIVACYPOLICY = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_htmlview);

        int type = 0;
        if (getPackageName().equals("com.meatchop")) {
            type = getIntent().getIntExtra("type", 0);
        }

        TMCTextView topbartitle_textview = (TMCTextView) findViewById(R.id.topbartitle_textview);
        TMCTextView context_textview = (TMCTextView) findViewById(R.id.context_textview);
        View back_icon = findViewById(R.id.back_icon);

        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });

        if (type == TYPE_ABOUTUS) {
            int htmlfilename = R.raw.tmc_aboutus;
            topbartitle_textview.setText("About Us");
            context_textview.setText(Html.fromHtml(readTxt(htmlfilename)));
        } else if (type == TYPE_TERMSANDCONDITIONS) {
            int htmlfilename = R.raw.tmc_termsandconditions;
            topbartitle_textview.setText("Terms and Conditions");
            context_textview.setText(Html.fromHtml(readTxt(htmlfilename)));
        } else if (type == TYPE_FAQS) {
            int htmlfilename = R.raw.tmc_faqs;
            topbartitle_textview.setText("Frequently Asked Questions");
            context_textview.setText(Html.fromHtml(readTxt(htmlfilename)));
        } else if (type == TYPE_PRIVACYPOLICY) {
            int htmlfilename = R.raw.tmc_privacypolicy;
            topbartitle_textview.setText("Privacy Policy");
            context_textview.setText(Html.fromHtml(readTxt(htmlfilename)));
        }
    }

    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_to_right);
    }

    private String readTxt(int htmlfilename) {
        InputStream inputStream = getResources().openRawResource(htmlfilename);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int i;
        try {
            i = inputStream.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return byteArrayOutputStream.toString();
    }
}
