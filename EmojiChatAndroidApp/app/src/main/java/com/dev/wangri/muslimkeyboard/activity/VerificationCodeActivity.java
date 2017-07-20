package com.dev.wangri.muslimkeyboard.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.wangri.muslimkeyboard.R;
import com.dev.wangri.muslimkeyboard.activity.profile.UserProfileActivity;
import com.dev.wangri.muslimkeyboard.utility.AppConst;
import com.dev.wangri.muslimkeyboard.utility.FontUtils;
import com.sinch.verification.CodeInterceptionException;
import com.sinch.verification.Config;
import com.sinch.verification.InitiationResult;
import com.sinch.verification.InvalidInputException;
import com.sinch.verification.ServiceErrorException;
import com.sinch.verification.SinchVerification;
import com.sinch.verification.Verification;
import com.sinch.verification.VerificationListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VerificationCodeActivity extends AppCompatActivity {
    private static final String TAG = VerificationCodeActivity.class.getSimpleName();
    @BindView(R.id.txt_phoneNumber)
    TextView mtxtPhoneNumber;
    @BindView(R.id.txt_back)
    TextView mtxtBack;
    @BindView(R.id.reVerification)
    TextView reVerification;

    @BindView(R.id.edt_confirmation_code)
    EditText edtConfirmationCode;
    @BindView(R.id.progressIndicator)
    ProgressBar mProgressIndicator;
    @BindView(R.id.verificationLayout)
    LinearLayout verificationLayout;
    AppConst appConst = new AppConst();

    private boolean mShouldFallback = true;

    private boolean mIsSmsVerification;
    private static final String APPLICATION_KEY = "13c34212-f919-4b65-9482-01f79a23186a";
    private Verification mVerification;
    private static final String[] SMS_PERMISSIONS = {Manifest.permission.INTERNET,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.ACCESS_NETWORK_STATE};
    private static final String[] FLASHCALL_PERMISSIONS = {Manifest.permission.INTERNET,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.ACCESS_NETWORK_STATE};
    private String mPhoneNumber;
    private BottomSheetDialog dialog;
    private String extraTitle;
    private String mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_code);
        ButterKnife.bind(this);
        FontUtils.setFont(verificationLayout, FontUtils.AvenirLTStdBook);

//        QBSettings.getInstance().init(this, appConst.app_id, appConst.auth_key, appConst.auth_secret);
//        QBSettings.getInstance().setAccountKey(appConst.account_key);

        mPhoneNumber = getIntent().getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        extraTitle = getIntent().getStringExtra(Intent.EXTRA_TITLE);
        if (extraTitle.equals("SignUp"))
            mPassword = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        mtxtPhoneNumber.setText(mPhoneNumber);
        mIsSmsVerification = true;
        requestPermissions();
    }

    private void requestPermissions() {
        List<String> missingPermissions;
        String methodText;

        if (mIsSmsVerification) {
            missingPermissions = getMissingPermissions(SMS_PERMISSIONS);
            methodText = "SMS";
        } else {
            missingPermissions = getMissingPermissions(FLASHCALL_PERMISSIONS);
            methodText = "calls";
        }

        if (missingPermissions.isEmpty()) {
            createVerification(0);
        } else {
            if (needPermissionsRationale(missingPermissions)) {
                Toast.makeText(this, "This application needs permissions to read your " + methodText + " to automatically verify your "
                        + "phone, you may disable the permissions once you have been verified.", Toast.LENGTH_LONG)
                        .show();
            }
            ActivityCompat.requestPermissions(this,
                    missingPermissions.toArray(new String[missingPermissions.size()]),
                    0);
        }
    }

    /**
     * @param type 0 means sms and 1 means call
     */
    private void createVerification(int type) {
        // It is important to pass ApplicationContext to the Verification config builder as the
        // verification process might outlive the activity.
        Config config = SinchVerification.config()
                .applicationKey(APPLICATION_KEY)
                .context(getApplicationContext())
                .build();
        VerificationListener listener = new MyVerificationListener();
        switch (type) {
            case 0:
                mVerification = SinchVerification.createSmsVerification(config, mPhoneNumber, listener);
                mVerification.initiate();
                break;
            default:
                mVerification = SinchVerification.createFlashCallVerification(config, mPhoneNumber, listener);
                mVerification.initiate();
                break;
        }
        showProgress();
    }

    private boolean needPermissionsRationale(List<String> permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // Proceed with verification after requesting permissions.
        // If the verification SDK fails to intercept the code automatically due to missing permissions,
        // the VerificationListener.onVerificationFailed(1) method will be executed with an instance of
        // CodeInterceptionException. In this case it is still possible to proceed with verification
        // by asking the user to enter the code manually.
        createVerification(0);
    }

    private List<String> getMissingPermissions(String[] requiredPermissions) {
        List<String> missingPermissions = new ArrayList<>();
        for (String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        return missingPermissions;
    }

    @OnClick(R.id.btn_submit)
    void submit() {
        String code = edtConfirmationCode.getText().toString();
        if (!code.isEmpty()) {
            if (mVerification != null) {
                mVerification.verify(code);
                showProgress();
            }
        } else {
            edtConfirmationCode.setError("Please enter code");
        }
    }


    private void hideProgressAndShowMessage() {
        hideProgress();
    }

    private void hideProgress() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressIndicator);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void showProgress() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressIndicator);
        progressBar.setVisibility(View.VISIBLE);
    }


    private class MyVerificationListener implements VerificationListener {

        @Override
        public void onInitiated(InitiationResult result) {
            Log.d(TAG, "Initialized!");
            showProgress();
        }

        @Override
        public void onInitiationFailed(Exception exception) {
            Log.e(TAG, "Verification initialization failed: " + exception.getMessage());
            hideProgressAndShowMessage();

            if (exception instanceof InvalidInputException) {
                // Incorrect number provided
            } else if (exception instanceof ServiceErrorException) {
                // Verification initiation aborted due to early reject feature,
                // client callback denial, or some other Sinch service error.
                // Fallback to other verification method here.

                if (mShouldFallback) {
                    mIsSmsVerification = !mIsSmsVerification;
                    if (mIsSmsVerification) {
                        Log.i(TAG, "Falling back to sms verification.");
                    } else {
                        Log.i(TAG, "Falling back to flashcall verification.");
                    }
                    mShouldFallback = false;
                    // Initiate verification with the alternative method.
                    requestPermissions();
                }
            } else {
                // Other system error, such as UnknownHostException in case of network error
            }
        }

        @Override
        public void onVerified() {
            Log.d(TAG, "Verified!");
            hideProgressAndShowMessage();
            if (extraTitle.equals("SignUp")) {
                openUserProfileActivity();
            } else if (extraTitle.equals("SignIn")) {
                openHomeActivity();
            } else if (extraTitle.equals("ForgotPassward")) {
                openPasswordCreateActvity();
            }
        }

        @Override
        public void onVerificationFailed(Exception exception) {
            Log.e(TAG, "Verification failed: " + exception.getMessage());
            if (exception instanceof CodeInterceptionException) {
                // Automatic code interception failed, probably due to missing permissions.
                // Let the user try and enter the code manually.
                hideProgress();
            } else {
                hideProgressAndShowMessage();
            }
        }
    }


    private void openPasswordCreateActvity() {
        startActivity(new Intent(VerificationCodeActivity.this, ForgotPwdActivity.class));
    }

    private void openUserProfileActivity() {
        Intent intent = new Intent(VerificationCodeActivity.this, UserProfileActivity.class);
        intent.putExtra(Intent.EXTRA_PHONE_NUMBER, mPhoneNumber);
        intent.putExtra(Intent.EXTRA_TEXT, mPassword);
        startActivity(intent);
    }

    private void openHomeActivity() {
        startActivity(new Intent(VerificationCodeActivity.this, HomeActivity.class));
        finish();
    }

    @OnClick(R.id.txt_back)
    void setMtxtBack() {
        finish();
    }

    public void setCancel(View view) {
        if (dialog.isShowing())
            dialog.dismiss();
    }

    public void setVerification_call(View view) {
        createVerification(1);
        if (dialog.isShowing())
            dialog.dismiss();
    }

    public void setVerification_resend(View view) {
        createVerification(0);
        if (dialog.isShowing())
            dialog.dismiss();
    }

    @OnClick(R.id.reVerification)
    void setReVerification() {
        dialog = new BottomSheetDialog(VerificationCodeActivity.this);
        dialog.setContentView(R.layout.dialog_revarification);
        LinearLayout dialogLayout = (LinearLayout) dialog.findViewById(R.id.reVerificationLayout);
        FontUtils.setFont(dialogLayout, FontUtils.AvenirLTStdBook);
        dialog.show();
    }

}
