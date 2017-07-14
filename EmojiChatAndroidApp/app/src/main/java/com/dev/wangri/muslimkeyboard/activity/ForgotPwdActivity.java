package com.dev.wangri.muslimkeyboard.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dev.wangri.muslimkeyboard.R;
import com.dev.wangri.muslimkeyboard.utility.BaseActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPwdActivity extends BaseActivity {

    EditText etEmail;
    TextView tvInvalidEmail;
    RelativeLayout layoutDone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pwd);

        etEmail = (EditText) findViewById(R.id.et_email);
        layoutDone = (RelativeLayout) findViewById(R.id.doneOverlayLayout);
        layoutDone.setVisibility(View.GONE);

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvInvalidEmail.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.tvDone).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                layoutDone.setVisibility(View.GONE);
                etEmail.setEnabled(true);
            }
        });

        tvInvalidEmail = (TextView) findViewById(R.id.tvInvalidEmail);
        tvInvalidEmail.setVisibility(View.INVISIBLE);

        findViewById(R.id.btn_recover).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strEmail = etEmail.getText().toString();
                if (strEmail.equals("")) {
                    etEmail.setError("Please enter your email");
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
                    etEmail.setError("Please enter a valid email address");
                    return;
                }
                progressDialog.show();
                resetPwd(strEmail);
            }
        });

        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void resetPwd(String strEmail) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.sendPasswordResetEmail(strEmail)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        progressDialog.hide();
                        Log.e("ForgotPwd", "Success");
                        layoutDone.setVisibility(View.VISIBLE);
                        etEmail.setEnabled(false);

                    }
                }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.hide();
                Log.e("ForgotPwd", "Failed");
                tvInvalidEmail.setVisibility(View.VISIBLE);

            }
        });
    }

    @Override
    protected void initUI() {

    }

    @Override
    public void onBackPressed() {
        if (layoutDone.getVisibility() == View.VISIBLE) {
            layoutDone.setVisibility(View.GONE);
            etEmail.setEnabled(true);
            return;
        }
        super.onBackPressed();
    }
}
