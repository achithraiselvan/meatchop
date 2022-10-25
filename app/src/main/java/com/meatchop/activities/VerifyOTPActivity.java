package com.meatchop.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import cz.msebera.android.httpclient.Header;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.results.SignInResult;
import com.amazonaws.mobile.client.results.SignUpResult;
import com.amazonaws.mobile.client.results.UserCodeDeliveryDetails;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.meatchop.R;
import com.meatchop.data.TMCUser;
import com.meatchop.smsreceiver.SmsBroadcastReceiver;
import com.meatchop.utils.SettingsUtil;
import com.meatchop.utils.TMCRestClient;
import com.meatchop.utils.TMCUtil;
import com.meatchop.utils.TMCconstants;
import com.meatchop.widget.TMCAlert;
import com.meatchop.widget.TMCEditText;
import com.meatchop.widget.TMCTextView;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerifyOTPActivity extends BaseActivity implements View.OnClickListener {

    private TMCTextView otpdesptext;
    private TMCEditText otptext1;
    private TMCEditText otptext2;
    private TMCEditText otptext3;
    private TMCEditText otptext4;
    private TMCEditText otptext5;
    private TMCEditText otptext6;
    private View verifyotp_button;

    private View loadinganimmask_layout;
    private AVLoadingIndicatorView loadinganim_layout;

    private String generatedpassword;

    private TMCTextView resendcode_text;
    private TMCTextView resendcodein_text;
    private TMCTextView resendcodetimer;
    private View resendcodebtn_layout;
    private double resendcodecounter = 0.20;

    private String enteredmobilenumber;
    private boolean isResendBtnActive = false;

    private String TAG = "VerifyOTPActivity";

    private AWSMobileClient awsMobileClient = AWSMobileClient.getInstance();
    private SettingsUtil settingsUtil;
    private String fcmtoken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_verify_otp);

        settingsUtil = new SettingsUtil(this);
        Log.d(TAG, "onCreateView method");
        if (getPackageName().equals("com.meatchop")) {
            enteredmobilenumber = getIntent().getStringExtra("mobileno");
        }

        enteredmobilenumber = "91" + enteredmobilenumber;

        otpdesptext = (TMCTextView) findViewById(R.id.otpdesptext);

        loadinganimmask_layout = findViewById(R.id.loadinganimmask_layout);
        loadinganim_layout = findViewById(R.id.loadinganim_layout);

        generatedpassword = "" + System.nanoTime();
        generateFCMToken();
        signupusingMobile(enteredmobilenumber);

        otpdesptext.setText(getResources().getString(R.string.otpverification_text) + " +" + enteredmobilenumber);
        startSmsUserConsent();
        registerBroadcastReceiver();

        otptext1 = (TMCEditText) findViewById(R.id.otptext1);
        otptext2 = (TMCEditText) findViewById(R.id.otptext2);
        otptext3 = (TMCEditText) findViewById(R.id.otptext3);
        otptext4 = (TMCEditText) findViewById(R.id.otptext4);
        otptext5 = (TMCEditText) findViewById(R.id.otptext5);
        otptext6 = (TMCEditText) findViewById(R.id.otptext6);


        resendcode_text = findViewById(R.id.resendcode_text);
        resendcodein_text = findViewById(R.id.resendcodein_text);
        resendcodetimer = findViewById(R.id.resendcodetimer);

        resendcodebtn_layout = findViewById(R.id.resendcodebtn_layout);
        resendcodebtn_layout.setOnClickListener(this);
        resendcodebtn_layout.setClickable(false);
        isResendBtnActive = false;
        startResendBtnCountDownTimer();
        addTextChangedListenersAndOnKeyListeners();

        verifyotp_button = findViewById(R.id.verifyotp_button);
        verifyotp_button.setOnClickListener(this);
    }

    private void addTextChangedListenersAndOnKeyListeners() {
        otptext1.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                Integer textlength1 = otptext1.getText().length();
                if (textlength1 >= 1) {
                    otptext2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }
        });

        otptext2.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                Integer textlength1 = otptext2.getText().length();
                Log.d("VerifyOTPActivity", "otptext2 onTextChanged "+otptext2.getText().toString());

                if (textlength1 >= 1) {
                    otptext3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }
        });

     /* otptext2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return false;
            }
        }); */

        otptext3.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                Integer textlength1 = otptext3.getText().length();

                if (textlength1 >= 1) {
                    otptext4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }
        });

        otptext4.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                Integer textlength1 = otptext4.getText().length();

                if (textlength1 >= 1) {
                    otptext5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }
        });

        otptext5.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                Integer textlength1 = otptext5.getText().length();

                if (textlength1 >= 1) {
                    otptext6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }
        });

        otptext2.setOnKeyListener((v, keyCode, event) -> {
            if(event.getAction() == KeyEvent.ACTION_UP) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DEL:
                        otptext1.requestFocus();
                        break;
                }
            }
            return false;
        });

        otptext3.setOnKeyListener((v, keyCode, event) -> {
            if(event.getAction() == KeyEvent.ACTION_UP) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DEL:
                        otptext2.requestFocus();
                        break;
                }
            }
            return false;
        });

        otptext4.setOnKeyListener((v, keyCode, event) -> {
            if(event.getAction() == KeyEvent.ACTION_UP) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DEL:
                        otptext3.requestFocus();
                        break;
                }
            }
            return false;
        });

        otptext5.setOnKeyListener((v, keyCode, event) -> {
            if(event.getAction() == KeyEvent.ACTION_UP) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DEL:
                        otptext4.requestFocus();
                        break;
                }
            }
            return false;
        });

        otptext6.setOnKeyListener((v, keyCode, event) -> {
            if(event.getAction() == KeyEvent.ACTION_UP) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DEL:
                        otptext5.requestFocus();
                        break;
                }
            }
            return false;
        });

    }

    private void startResendBtnCountDownTimer() {
        final int resendcodeprimtextcolor = getResources().getColor(R.color.primary_text_color);
        final int resendcodesectextcolor = getResources().getColor(R.color.secondary_text_color);
        resendcode_text.setTextColor(resendcodesectextcolor);
        resendcodecounter = 0.20;
     // resendcodetimer.setText(String.format("%.2f", resendcodecounter));
        new CountDownTimer(20000, 2000){
            public void onTick(long millisUntilFinished) {
                resendcodein_text.setText(getResources().getString(R.string.in));
                resendcodecounter = resendcodecounter - 0.01;
                resendcodetimer.setText(String.format("%.2f", resendcodecounter));
            }
            public  void onFinish(){
                resendcodetimer.setText("");
                resendcodein_text.setText("");
                resendcodebtn_layout.setClickable(true);
                isResendBtnActive = true;
                resendcode_text.setTextColor(resendcodeprimtextcolor);
            }
        }.start();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.verifyotp_button) {
            String otp1 = otptext1.getText().toString();
            String otp2 = otptext2.getText().toString();
            String otp3 = otptext3.getText().toString();
            String otp4 = otptext4.getText().toString();
            String otp5 = otptext5.getText().toString();
            String otp6 = otptext6.getText().toString();

            if ((TextUtils.isEmpty(otp1)) || (TextUtils.isEmpty(otp2)) || (TextUtils.isEmpty(otp3)) ||
                    (TextUtils.isEmpty(otp4)) || (TextUtils.isEmpty(otp5)) || (TextUtils.isEmpty(otp6))) {
                Log.d(TAG, "invalid otp error");
                showTMCAlert(R.string.invalidotp_title, R.string.otpinvalid_msg);
                hideLoadingAnim();
                return;
            }
            String passcode = otp1 + otp2 + otp3 + otp4 + otp5 + otp6;
            verifyotp(passcode);
        } else if (id == R.id.resendcodebtn_layout) {
            if (!isResendBtnActive) { return; }
            Log.d(TAG, "resendcodebtn layout clicked");
            startResendBtnCountDownTimer();
            signinusingMobile(enteredmobilenumber);

        }
    }


    private void verifyotp(String passCode) {
        if ((passCode == null) || (TextUtils.isEmpty(passCode))) {
            return;
        }
        showLoadingAnim();
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("ANSWER", passCode);
        AWSMobileClient.getInstance().confirmSignIn(attributes, new Callback<SignInResult>() {
            @Override
            public void onResult(SignInResult signInResult) {
                Log.d(TAG, "Sign-in state: "+ signInResult.getSignInState());

                switch (signInResult.getSignInState()) {
                    case DONE:
                        Log.d(TAG, "Sign-in done access token "+getAccessToken() + " enteredmobilenumber "+enteredmobilenumber);
                        hideKeyboardsofOTPEditText();
                        TMCUser tmcUser = new TMCUser(enteredmobilenumber);
                        tmcUser.setAuthorizationcode(getAccessToken());
                        if ((fcmtoken != null) && !(TextUtils.isEmpty(fcmtoken))) {
                            tmcUser.setFcmtoken(fcmtoken);
                        }
                        updateUserDetailsInAWS(tmcUser);
                        break;
                    case SMS_MFA:
                        Log.d(TAG, "Please confirm sign-in with SMS");
                        break;
                    case NEW_PASSWORD_REQUIRED:
                        Log.d(TAG, "Please confirm sign-in with new password");
                        break;
                    case CUSTOM_CHALLENGE:
                        hideKeyboardsofOTPEditText();
                        showTMCAlert(R.string.invalidotp_title, R.string.incorrectotp_text);
                        Log.d(TAG,"Custom challenge");
                        hideLoadingAnim();
                        break;
                    default:
                        showTMCAlert(R.string.error_title, R.string.signinerror_text);
                        Log.d(TAG, "Unsupported sign-in confirmation:"+ signInResult.getSignInState().toString());
                        hideLoadingAnim();
                        break;
                }
            }
            @Override
            public void onError(Exception e) {
                hideKeyboardsofOTPEditText();
                showTMCAlert(R.string.invalidotp_title, R.string.incorrectotp_text);
                hideLoadingAnim();
                Log.e(TAG, "Sign-in error", e);
            }
        });
    }

    private void signupusingMobile(String mobilenumber) {
     // String phonenumber = "+91" + mobilenumber;
        String phonenumber = "+" + mobilenumber;
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("phone_number", phonenumber);
        attributes.put("name", "Guest");
        attributes.put("email", "");

        Log.d("LoginActivity", "generatedpassword "+generatedpassword+" phone_number "+phonenumber);
        awsMobileClient.signUp(phonenumber, "password", attributes, null,
                new Callback<SignUpResult>() {
                    @Override
                    public void onResult(final SignUpResult signUpResult) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "Sign-up callback state: " + signUpResult.getConfirmationState());
                                if (!signUpResult.getConfirmationState()) {
                                    final UserCodeDeliveryDetails details = signUpResult.getUserCodeDeliveryDetails();
                                    Log.d(TAG, "Sign-up callback state: " + details.getDestination());
                                    hideLoadingAnim();
                                    showTMCAlert(R.string.error_title, R.string.signuperror_text);
                                } else {
                                    startSmsUserConsent();
                                    signinusingMobile(mobilenumber);
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d(TAG, "e message "+e.getMessage());
                        if (e.getMessage().contains("An account with the given phone_number already exists")) {
                            startSmsUserConsent();
                            signinusingMobile(mobilenumber);
                        } else {
                            hideLoadingAnim();
                            showTMCAlert(R.string.error_title, R.string.signuperror_text);
                        }

                        Log.e(TAG, "Sign-up error", e);
                    }
                });
    }

    private void signinusingMobile(String mobilenumber) {
     // String phonenumber = "+91" + mobilenumber;
        String phonenumber = "+" + mobilenumber;
        AWSMobileClient.getInstance().signIn(phonenumber, "password", null, new Callback<SignInResult>() {
            @Override
            public void onResult(final SignInResult signInResult) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Sign-in callback state: " + signInResult.getSignInState());
                        switch (signInResult.getSignInState()) {
                            case DONE:
                                Log.d(TAG, "Sign-in done access token "+getAccessToken());
                                // hideLoadingAnim();
                                TMCUser tmcUser = new TMCUser(enteredmobilenumber);
                                tmcUser.setAuthorizationcode(getAccessToken());
                                if ((fcmtoken != null) && !(TextUtils.isEmpty(fcmtoken))) {
                                    tmcUser.setFcmtoken(fcmtoken);
                                }
                                updateUserDetailsInAWS(tmcUser);
                                break;
                            case SMS_MFA:
                                Log.d(TAG,"Please confirm sign-in with SMS.");
                                break;
                            case NEW_PASSWORD_REQUIRED:
                                Log.d(TAG,"Please confirm sign-in with new password.");
                                break;
                            case CUSTOM_CHALLENGE:
                                Log.d(TAG," signinusingMobile Custom challenge.");
                                // startVerifyOTPActivity(mobilenumber, "", TMCconstants.LOGINTYPE_MOBILE);
                                switchToOTPScreenandTriggerTimer();
                                break;
                            default:
                                Log.d(TAG,"Unsupported sign-in confirmation: " + signInResult.getSignInState());
                                break;
                        }
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Sign-in error", e);
            }
        });
    }

    private void switchToOTPScreenandTriggerTimer() {
        startResendBtnCountDownTimer();
        otpdesptext.setText(getResources().getString(R.string.otpverification_text) + " +" + enteredmobilenumber);
        hideLoadingAnim();
    }

    private String getAccessToken() {
        try {
            String accesstoken = AWSMobileClient.getInstance().getTokens().getAccessToken().getTokenString();
            Log.d(TAG, "accesstoken "+accesstoken);
            return accesstoken;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void getOtpFromMessage(String message) {
// This will match any 6 digit number in the message
        try {
            Pattern pattern = Pattern.compile("(|^)\\d{6}");
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                String code = matcher.group(0);
                code = code.replaceAll("\\s", "");
                if ((code == null) || (code.length() != 6)) {
                    return;
                }
                Log.d("LoginActivity", "code "+code);
                code = code.trim();
                Log.d("LoginActivity", "code after trim "+code);
                char[] codes = code.toCharArray();
                for (int i=0; i<codes.length; i++) {
                    Log.d(TAG, "codes[i] "+codes[i]);
                }
                if (codes.length != 6) {
                    return;
                }
                otptext1.setText(Character.toString(codes[0]));
                otptext2.setText(Character.toString(codes[1]));
                otptext3.setText(Character.toString(codes[2]));
                otptext4.setText(Character.toString(codes[3]));
                otptext5.setText(Character.toString(codes[4]));
                otptext6.setText(Character.toString(codes[5]));
                verifyotp(code);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            otptext1.setText("");
            otptext2.setText("");
            otptext3.setText("");
            otptext4.setText("");
            otptext5.setText("");
            otptext6.setText("");
        }

    }

    private void startSmsUserConsent() {
        SmsRetrieverClient client = SmsRetriever.getClient(this);
        client.startSmsUserConsent(null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "startSMSUserContent Success");
                // Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "startSMSUserContent Failure");
                // Toast.makeText(getApplicationContext(), "On OnFailure", Toast.LENGTH_LONG).show();
            }
        });
    }

    private SmsBroadcastReceiver smsBroadcastReceiver;
    private final int REQ_USER_CONTENT = 0;
    private boolean receiverregistered = false;
    private void registerBroadcastReceiver() {
        smsBroadcastReceiver = new SmsBroadcastReceiver();
        receiverregistered = true;
        smsBroadcastReceiver.smsBroadcastReceiverListener =
                new SmsBroadcastReceiver.SmsBroadcastReceiverListener() {
                    @Override
                    public void onSuccess(Intent intent) {
                        if (intent != null) {
                            ComponentName name = intent.resolveActivity(getPackageManager());
                            if (name != null) {
                                if ( (name.getPackageName().equals("com.google.android.gms")) &&
                                        (name.getClassName().equals("com.google.android.gms.auth.api.phone.ui.UserConsentPromptActivity")) ) {
                                    startActivityForResult(intent, REQ_USER_CONTENT);
                                    receiverregistered = true;
                                }
                            }
                        }

                    }
                    @Override
                    public void onFailure() {
                        receiverregistered = false;
                    }
                };
        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        registerReceiver(smsBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("LoginActivity", "onActivityResult requestCode " + requestCode + " resultCode "+resultCode);
        if (requestCode == REQ_USER_CONTENT) {
            if ((resultCode == RESULT_OK) && (data != null)) {
                String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                if ((smsBroadcastReceiver != null) && (receiverregistered)) {
                    receiverregistered = false;
                    unregisterReceiver(smsBroadcastReceiver);
                }
                getOtpFromMessage(message);
            }
        }
    }


    private void updateUserDetailsInAWS(TMCUser tmcUser) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
             // updateUserDataInAWS(tmcUser);
                addorupdateUserDataInTMCUser(tmcUser);
            }
        };
        mainHandler.post(myRunnable);
    }

 /* private void updateUserDataInAWS(TMCUser tmcUser) {
        // Get user data from mobile number
        // If mobile number exists, update mobile number, updatedtime and authroization code
        // If not exists, add mobile number, createdtime and authorization code
        String mobileno = tmcUser.getMobileno();
        mobileno = "%2b" + mobileno;
        Log.d(TAG, "updateUserDataInAWS mobileno "+mobileno);
        TMCRestClient.getUserDetails(mobileno, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);
                try {
                    Log.d(TAG, "getUserDetails jsonObject " + jsonObject); //No I18N
                    String message = jsonObject.getString("message");
                    JSONArray jsonArray = jsonObject.getJSONArray("content");
                    Log.d(TAG, "getUserDetails jsonArray " + jsonArray); //No I18N
                    if ((jsonArray != null) && (jsonArray.length() > 0)) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                        String key = jsonObject1.getString("key");
                        settingsUtil.setUserkey(key);
                        Log.d(TAG, "getUserDetails tmcUser.getMobileno() " + tmcUser.getMobileno());
                        settingsUtil.saveMobile(tmcUser.getMobileno());
                        tmcUser.setKey(key);
                        tmcUser.setUpdatedtime(TMCUtil.getCurrentTime());
                        updateUserData(tmcUser);
                    } else {
                        tmcUser.setCreatedtime(TMCUtil.getCurrentTime());
                        addUserData(tmcUser);
                    }
                    Intent intent = new Intent();
                    intent.putExtra("loginsuccess", true);
                    setResult(RESULT_OK, intent);
                    finish();
                    overridePendingTransition(0, 0);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,Throwable throwable, JSONObject jsonObject) {
                super.onFailure(statusCode, headers, throwable, jsonObject);
                Log.d(TAG, "addUserData push failed"); //No i18n
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String s, Throwable throwable) {
                super.onFailure(statusCode, headers, s, throwable);
                Log.d(TAG, "addUserData push failed"); //No i18n
            }
        });
    }
*/
    private void addorupdateUserDataInTMCUser(TMCUser tmcUser) {
        // Get user data from mobile number
        // If mobile number exists, update mobile number, updatedtime and authroization code
        // If not exists, add mobile number, createdtime and authorization code
        String mobileno = tmcUser.getMobileno();
        mobileno = "%2B" + mobileno;

        String url = TMCRestClient.AWS_GETUSERDETAILS_TMCUSER + "?mobileno=" + mobileno;
        Log.d(TAG, "addorupdateUserDataInTMCUser url "+url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        Log.d(TAG, "Volley response "+response.toString());
                        try {
                            Log.d(TAG, "getUserDetails jsonObject " + response); //No I18N
                            String message = response.getString("message");
                            JSONArray jsonArray = response.getJSONArray("content");
                            Log.d(TAG, "getUserDetails jsonArray " + jsonArray); //No I18N
                            if ((jsonArray != null) && (jsonArray.length() > 0)) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                             // String key = jsonObject1.getString("key");
                                String uniquekey = jsonObject1.getString("uniquekey");
                                String usercreatedtime = jsonObject1.getString("createdtime");
                                if (jsonObject1.has("status")) {
                                    String status = jsonObject1.getString("status");
                                    settingsUtil.setUserStatus(status);
                                }
                                settingsUtil.setUserkey(uniquekey);
                                settingsUtil.setUsercreatedtime(usercreatedtime);
                                // Log.d(TAG, "getUserDetails tmcUser.getMobileno() " + tmcUser.getMobileno());
                                settingsUtil.saveMobile(tmcUser.getMobileno());
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("mobileno", "+" + settingsUtil.getMobile());
                                jsonObject.put("updatedtime", TMCUtil.getCurrentTime());
                                jsonObject.put("appversion", getVersionname());
                                jsonObject.put("deviceos", "Android");
                                jsonObject.put("fcmtoken", fcmtoken);
                                jsonObject.put("authorizationcode", tmcUser.getAuthorizationcode());
                                String usertype = jsonObject1.getString("usertype");
                                if ((usertype == null) || (TextUtils.isEmpty(usertype))) {
                                    jsonObject.put("usertype", "APP");
                                } else if (!usertype.contains("APP")) {
                                    String newusertype = "APP_" + usertype;
                                    jsonObject.put("usertype", newusertype);
                                }
                                settingsUtil.setFCMToken(fcmtoken);
                                updateUserDataInTMCUser(jsonObject);
                            } else {
                                tmcUser.setCreatedtime(TMCUtil.getCurrentTime());
                                tmcUser.setUpdatedtime(tmcUser.getCreatedtime());
                                tmcUser.setCreateddate(TMCUtil.getCurrentDateNew());
                                tmcUser.setAppversion(getVersionname());
                                tmcUser.setDeviceos("Android");
                                tmcUser.setFcmtoken(fcmtoken);
                                tmcUser.setUsertype("APP");
                                settingsUtil.setFCMToken(fcmtoken);
                                getUserDataFromOldTable(tmcUser);

                             // tmcUser.setUniquekey("" + System.nanoTime());
                             // addUserDataInTMCUser(tmcUser);
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(@NonNull VolleyError error) {
                        Log.d(TAG, "Error: " + error.getLocalizedMessage());
                        Log.d(TAG, "Error: " + error.getMessage());
                        Log.d(TAG, "Error: " + error.toString());

                        error.printStackTrace();
                    } }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                final Map<String, String> params = new HashMap<>();
                params.put("modulename", "Store");
                return params;
            }

            @NonNull
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> header = new HashMap<>();
                header.put("Content-Type", "application/json");

                return header;
            }
        };
// Add the request to the RequestQueue.
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(10000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void getUserDataFromOldTable(TMCUser tmcUser) {
        String mobileno = "%2B" + tmcUser.getMobileno();

        String url = TMCRestClient.AWS_GETUSERDETAILS + "?mobileno=" + mobileno;
        Log.d(TAG, "getUserDataFromOldTable url "+url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        Log.d(TAG, "Volley response "+response.toString());
                        try {
                            Log.d(TAG, "getUserDetails jsonObject " + response); //No I18N
                            String message = response.getString("message");
                            JSONArray jsonArray = response.getJSONArray("content");
                            Log.d(TAG, "getUserDetails jsonArray " + jsonArray); //No I18N
                            if ((jsonArray != null) && (jsonArray.length() > 0)) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                                String uniquekey = jsonObject1.getString("key");
                                tmcUser.setUniquekey(uniquekey);
                                settingsUtil.setUserkey(uniquekey);
                            }
                            addUserDataInTMCUser(tmcUser);

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(@NonNull VolleyError error) {
                        Log.d(TAG, "Error: " + error.getLocalizedMessage());
                        Log.d(TAG, "Error: " + error.getMessage());
                        Log.d(TAG, "Error: " + error.toString());

                        error.printStackTrace();
                    } }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                final Map<String, String> params = new HashMap<>();
                params.put("modulename", "Store");
                return params;
            }

            @NonNull
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> header = new HashMap<>();
                header.put("Content-Type", "application/json");

                return header;
            }
        };
// Add the request to the RequestQueue.
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(6000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void addUserDataInTMCUser(TMCUser tmcUser) {
        JSONObject jsonObject = new JSONObject();
        String name = tmcUser.getName(); String email = tmcUser.getEmail();
        String mobileno = "+" + tmcUser.getMobileno(); String createdtime = tmcUser.getCreatedtime();
        String updatedtime = tmcUser.getUpdatedtime(); String authorizationcode = tmcUser.getAuthorizationcode();
        String deviceos = tmcUser.getDeviceos(); String appversion = tmcUser.getAppversion();
        String fcmtoken = tmcUser.getFcmtoken();
        try {
            if ((name != null) && (!TextUtils.isEmpty(name))) {
                jsonObject.put("name", name);
            }
            if ((email != null) && (!TextUtils.isEmpty(email))) {
                jsonObject.put("email", email);
            }
            if ((mobileno != null) && (!TextUtils.isEmpty(mobileno))) {
                jsonObject.put("mobileno", mobileno);
            }
            if ((createdtime != null) && (!TextUtils.isEmpty(createdtime))) {
                jsonObject.put("createdtime", createdtime);
            }
            if ((updatedtime != null) && (!TextUtils.isEmpty(updatedtime))) {
                jsonObject.put("updatedtime", updatedtime);
            }
            if ((authorizationcode != null) && (!TextUtils.isEmpty(authorizationcode))) {
                jsonObject.put("authorizationcode", authorizationcode);
            }
            if ((deviceos != null) && (!TextUtils.isEmpty(deviceos))) {
                jsonObject.put("deviceos", deviceos);
            }
            if ((appversion != null) && (!TextUtils.isEmpty(appversion))) {
                jsonObject.put("appversion", appversion);
            }
            if ((fcmtoken != null) && (!TextUtils.isEmpty(fcmtoken))) {
                jsonObject.put("fcmtoken", fcmtoken);
            }
            jsonObject.put("usertype", tmcUser.getUsertype());
            jsonObject.put("uniquekey", tmcUser.getUniquekey());
            jsonObject.put("createddate", tmcUser.getCreateddate());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        settingsUtil.saveMobile(tmcUser.getMobileno());
        String url = TMCRestClient.AWS_ADDUSERDETAILS_TMCUSER;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(@NonNull JSONObject response) {
                try {
                    Log.d(TAG, "addOrderDetails jsonobject " + response);
                    String message = response.getString("message");
                    if (message.equalsIgnoreCase("success")) {
                        JSONObject content = response.getJSONObject("content");
                        String uniquekey = content.getString("uniquekey");
                        settingsUtil.setUserkey(uniquekey);
                        settingsUtil.setUsercreatedtime(tmcUser.getCreatedtime());
                        Log.d(TAG, "addUserData tmcUser.getMobileno() " + tmcUser.getMobileno());
                        closeActivityAndParseIntent();
                    }
                    hideLoadingAnim();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(@NonNull VolleyError error) {
                Log.d(TAG, "Error: " + error.getLocalizedMessage());
                Log.d(TAG, "Error: " + error.getMessage());
                Log.d(TAG, "Error: " + error.toString());

                error.printStackTrace();
            }
        }) {
            @NonNull
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");

                return params;
            }
        };
        // Make the request
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(10000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void updateUserDataInTMCUser(JSONObject jsonObject) {
        String url = TMCRestClient.AWS_UPDATEUSERDETAILS_TMCUSER;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(@NonNull JSONObject response) {
                try {
                    Log.d(TAG, "updateUserData jsonobject " + response);
                    String message = response.getString("message");
                    if (message.equalsIgnoreCase("success")) {
                        closeActivityAndParseIntent();
                    }
                    hideLoadingAnim();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(@NonNull VolleyError error) {
                Log.d(TAG, "Error: " + error.getLocalizedMessage());
                Log.d(TAG, "Error: " + error.getMessage());
                Log.d(TAG, "Error: " + error.toString());

                error.printStackTrace();
            }
        }) {
            @NonNull
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");

                return params;
            }
        };
        // Make the request
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(10000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(mRetryPolicy);
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void showLoadingAnim() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                loadinganimmask_layout.setVisibility(View.VISIBLE);
                loadinganim_layout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void hideLoadingAnim() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                loadinganimmask_layout.setVisibility(View.GONE);
                loadinganim_layout.setVisibility(View.GONE);
            }
        });
    }

    private void showTMCAlert(int title, int message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new TMCAlert(VerifyOTPActivity.this, title, message,
                        R.string.ok_capt, 0,
                        new TMCAlert.AlertListener() {
                            @Override
                            public void onYes() {
                            }

                            @Override
                            public void onNo() {

                            }
                        });
            }
        });
    }

    public void showKeyboard(TMCEditText edittext) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edittext, InputMethodManager.SHOW_IMPLICIT);
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(otptext1.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(otptext2.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(otptext3.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(otptext4.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(otptext5.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(otptext6.getWindowToken(), 0);
    }

    public void hideKeyboardsofOTPEditText() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(otptext1.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(otptext2.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(otptext3.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(otptext4.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(otptext5.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(otptext6.getWindowToken(), 0);
    }

    private void generateFCMToken() {
        //getting the FCM token for the user device to send the notification
        try {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (!task.isSuccessful()) {
                        return;
                    }
                    // Get new FCM registration token
                    fcmtoken = task.getResult();
                    Log.i(TAG,"token:  "+fcmtoken);

                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



    private void signinusingEmail(String email) {
        AWSMobileClient.getInstance().signIn(email, generatedpassword, null, new Callback<SignInResult>() {
            @Override
            public void onResult(final SignInResult signInResult) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Sign-in callback state: " + signInResult.getSignInState());
                        switch (signInResult.getSignInState()) {
                            case DONE:
                                Log.d(TAG, "Sign-in done access token "+getAccessToken());
                                hideLoadingAnim();
                                break;
                            case SMS_MFA:
                                Log.d(TAG,"Please confirm sign-in with SMS.");
                                break;
                            case NEW_PASSWORD_REQUIRED:
                                Log.d(TAG,"Please confirm sign-in with new password.");
                                break;
                            case CUSTOM_CHALLENGE:
                                Log.d(TAG," Custom challenge.");
                                break;
                            default:
                                Log.d(TAG,"Unsupported sign-in confirmation: " + signInResult.getSignInState());
                                break;
                        }
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Sign-in error", e);
            }
        });
    }

    private void closeActivityAndParseIntent() {
        Intent intent = new Intent();
        try {
            if (smsBroadcastReceiver != null) {
                unregisterReceiver(smsBroadcastReceiver);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        intent.putExtra("loginsuccess", true);
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(0, 0);
    }

    private String getVersionname() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return pInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

 /* private void updateUserDataInAWS(TMCUser tmcUser) {
        // Get user data from mobile number
        // If mobile number exists, update mobile number, updatedtime and authroization code
        // If not exists, add mobile number, createdtime and authorization code
        String mobileno = tmcUser.getMobileno();
        mobileno = "%2B" + mobileno;

        String url = TMCRestClient.AWS_GETUSERDETAILS + "?mobileno=" + mobileno;
        Log.d(TAG, "updateUserDataInAWS url "+url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(@NonNull JSONObject response) {
                        // Display the first 500 characters of the response string.
                        Log.d(TAG, "Volley response "+response.toString());
                        try {
                            Log.d(TAG, "getUserDetails jsonObject " + response); //No I18N
                            String message = response.getString("message");
                            JSONArray jsonArray = response.getJSONArray("content");
                            Log.d(TAG, "getUserDetails jsonArray " + jsonArray); //No I18N
                            if ((jsonArray != null) && (jsonArray.length() > 0)) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                                String key = jsonObject1.getString("key");
                                String usercreatedtime = jsonObject1.getString("createdtime");
                                settingsUtil.setUserkey(key);
                                settingsUtil.setUsercreatedtime(usercreatedtime);
                                // Log.d(TAG, "getUserDetails tmcUser.getMobileno() " + tmcUser.getMobileno());
                                settingsUtil.saveMobile(tmcUser.getMobileno());
                                tmcUser.setKey(key);
                                tmcUser.setUpdatedtime(TMCUtil.getCurrentTime());
                                tmcUser.setAppversion(getVersionname());
                                tmcUser.setDeviceos("Android");
                                tmcUser.setFcmtoken(fcmtoken);
                                settingsUtil.setFCMToken(fcmtoken);
                                updateUserData(tmcUser);
                            } else {
                                tmcUser.setCreatedtime(TMCUtil.getCurrentTime());
                                tmcUser.setAppversion(getVersionname());
                                tmcUser.setDeviceos("Android");
                                tmcUser.setFcmtoken(fcmtoken);
                                settingsUtil.setFCMToken(fcmtoken);
                                addUserData(tmcUser);
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(@NonNull VolleyError error) {
                        Log.d(TAG, "Error: " + error.getLocalizedMessage());
                        Log.d(TAG, "Error: " + error.getMessage());
                        Log.d(TAG, "Error: " + error.toString());

                        error.printStackTrace();
                    } }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                final Map<String, String> params = new HashMap<>();
                params.put("modulename", "Store");
                return params;
            }

            @NonNull
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> header = new HashMap<>();
                header.put("Content-Type", "application/json");

                return header;
            }
        };
// Add the request to the RequestQueue.
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void addUserData(TMCUser tmcUser) {
        JSONObject jsonObject = new JSONObject();
        String name = tmcUser.getName(); String email = tmcUser.getEmail();
        String mobileno = "+" + tmcUser.getMobileno(); String createdtime = tmcUser.getCreatedtime();
        String updatedtime = tmcUser.getUpdatedtime(); String authorizationcode = tmcUser.getAuthorizationcode();
        String deviceos = tmcUser.getDeviceos(); String appversion = tmcUser.getAppversion();
        String fcmtoken = tmcUser.getFcmtoken();
        try {
            if ((name != null) && (!TextUtils.isEmpty(name))) {
                jsonObject.put("name", name);
            }
            if ((email != null) && (!TextUtils.isEmpty(email))) {
                jsonObject.put("email", email);
            }
            if ((mobileno != null) && (!TextUtils.isEmpty(mobileno))) {
                jsonObject.put("mobileno", mobileno);
            }
            if ((createdtime != null) && (!TextUtils.isEmpty(createdtime))) {
                jsonObject.put("createdtime", createdtime);
            }
            if ((updatedtime != null) && (!TextUtils.isEmpty(updatedtime))) {
                jsonObject.put("updatedtime", updatedtime);
            }
            if ((authorizationcode != null) && (!TextUtils.isEmpty(authorizationcode))) {
                jsonObject.put("authorizationcode", authorizationcode);
            }
            if ((deviceos != null) && (!TextUtils.isEmpty(deviceos))) {
                jsonObject.put("deviceos", deviceos);
            }
            if ((appversion != null) && (!TextUtils.isEmpty(appversion))) {
                jsonObject.put("appversion", appversion);
            }
            if ((fcmtoken != null) && (!TextUtils.isEmpty(fcmtoken))) {
                jsonObject.put("fcmtoken", fcmtoken);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        settingsUtil.saveMobile(tmcUser.getMobileno());
        String url = TMCRestClient.AWS_ADDUSERDETAILS;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(@NonNull JSONObject response) {
                try {
                    Log.d(TAG, "addOrderDetails jsonobject " + response);
                    String message = response.getString("message");
                    if (message.equalsIgnoreCase("success")) {
                        JSONObject content = response.getJSONObject("content");
                        String key = content.getString("key");
                        settingsUtil.setUserkey(key);
                        settingsUtil.setUsercreatedtime(tmcUser.getCreatedtime());
                        Log.d(TAG, "addUserData tmcUser.getMobileno() " + tmcUser.getMobileno());
                        closeActivityAndParseIntent();
                    }
                    hideLoadingAnim();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(@NonNull VolleyError error) {
                Log.d(TAG, "Error: " + error.getLocalizedMessage());
                Log.d(TAG, "Error: " + error.getMessage());
                Log.d(TAG, "Error: " + error.toString());

                error.printStackTrace();
            }
        }) {
            @NonNull
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");

                return params;
            }
        };
        // Make the request
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void updateUserData(TMCUser tmcUser) {
        TMCRestClient.updateUserDetails(getApplicationContext(), tmcUser, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);
                try {
                    Log.d(TAG, "updateuserdata jsonObject " + jsonObject); //No I18N
                    closeActivityAndParseIntent();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,Throwable throwable, JSONObject jsonObject) {
                super.onFailure(statusCode, headers, throwable, jsonObject);
                Log.d(TAG, "updateuserdata push failed"); //No i18n
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String s, Throwable throwable) {
                super.onFailure(statusCode, headers, s, throwable);
                Log.d(TAG, "updateuserdata push failed"); //No i18n
            }
        });
    }
*/





}
