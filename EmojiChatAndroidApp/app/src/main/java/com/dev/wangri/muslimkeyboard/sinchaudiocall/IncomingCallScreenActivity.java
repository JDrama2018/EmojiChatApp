package com.dev.wangri.muslimkeyboard.sinchaudiocall;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.wangri.muslimkeyboard.R;
import com.dev.wangri.muslimkeyboard.bean.User;
import com.dev.wangri.muslimkeyboard.utility.FirebaseManager;
import com.sinch.android.rtc.MissingPermissionException;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class IncomingCallScreenActivity extends BaseActivity {

    static final String TAG = IncomingCallScreenActivity.class.getSimpleName();
    private String mCallId;
    private AudioPlayer mAudioPlayer;
    private TextView remoteUser;
    private ImageView img_userprofile;
    private String remoteUserId;
    private String callerName;
    private String callerPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incoming);

        ImageView answer = (ImageView) findViewById(R.id.answerButton);
        answer.setOnClickListener(mClickListener);
        ImageView decline = (ImageView) findViewById(R.id.declineButton);
        decline.setOnClickListener(mClickListener);
        remoteUser = (TextView) findViewById(R.id.remoteUser);
        img_userprofile = (ImageView) findViewById(R.id.img_userprofile);

        mAudioPlayer = new AudioPlayer(this);
        mAudioPlayer.playRingtone();
        mCallId = getIntent().getStringExtra(SinchService.CALL_ID);
        remoteUserId = getIntent().getStringExtra(SinchService.CALL_REMOTEUSER_ID);
        if (!TextUtils.isEmpty(mCallId)) {
            Log.d(TAG, "onCreate() called with: mCallId = [" + mCallId + "]");
            FirebaseManager.getInstance().getUser(remoteUserId, new FirebaseManager.OnUserResponseListener() {
                @Override
                public void onUserResponse(User user) {
                    try {
                        callerName = String.format("%s", user.username);
                        remoteUser.setText(callerName);
                        if (!TextUtils.isEmpty(user.photo)) {
                            callerPhoto = user.photo;
                            Picasso.with(IncomingCallScreenActivity.this)
                                    .load(user.photo)
                                    .placeholder(R.mipmap.profile)
                                    .error(R.mipmap.profile)
                                    .into(img_userprofile);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }

    }

    @Override
    protected void onServiceConnected() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.addCallListener(new SinchCallListener());
        } else {
            Log.e(TAG, "Started with invalid callId, aborting");
            finish();
        }
    }

    private void answerClicked() {
        mAudioPlayer.stopRingtone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            try {
                call.answer();
                Intent intent;
                if (call.getDetails().isVideoOffered()) {
                    intent = new Intent(this, CallVideoScreenActivity.class);
                } else {
                    intent = new Intent(this, CallScreenActivity.class);
                }
                intent.putExtra(SinchService.CALL_ID, mCallId);
                intent.putExtra(SinchService.CALLER_NAME, callerName);
                intent.putExtra(SinchService.CALLER_PHOTO, callerPhoto);
                startActivity(intent);
            } catch (MissingPermissionException e) {
                ActivityCompat.requestPermissions(this, new String[]{e.getRequiredPermission()}, 0);
            }
        } else {
            finish();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You may now answer the call", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "This application needs permission to use your microphone to function properly.", Toast
                    .LENGTH_LONG).show();
        }
    }

    private void declineClicked() {
        mAudioPlayer.stopRingtone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.hangup();
        }
        finish();
    }

    private class SinchCallListener implements CallListener {

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(TAG, "Call ended, cause: " + cause.toString());
            mAudioPlayer.stopRingtone();
            finish();
        }

        @Override
        public void onCallEstablished(Call call) {
            Log.d(TAG, "Call established");
        }

        @Override
        public void onCallProgressing(Call call) {
            Log.d(TAG, "Call progressing");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            // Send a push through your push provider here, e.g. GCM
        }

    }

    private OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.answerButton:
                    answerClicked();
                    break;
                case R.id.declineButton:
                    declineClicked();
                    break;
            }
        }
    };
}
