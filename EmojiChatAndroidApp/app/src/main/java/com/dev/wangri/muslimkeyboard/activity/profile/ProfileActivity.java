package com.dev.wangri.muslimkeyboard.activity.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;

import com.dev.wangri.muslimkeyboard.R;
import com.dev.wangri.muslimkeyboard.bean.User;
import com.dev.wangri.muslimkeyboard.utility.BaseActivity;
import com.dev.wangri.muslimkeyboard.utility.FirebaseManager;
import com.squareup.picasso.Picasso;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Super on 5/15/2017.
 */

public class ProfileActivity extends BaseActivity implements View.OnClickListener {
    CircleImageView imvAvatar;
    TextView tvName;
    TextView tvFullname;
    TextView tvUsername;
    TextView tvLastSeen;
    String userID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
        if (intent == null || !intent.hasExtra("userID")) {
            finish();
            return;
        }

        userID = intent.getStringExtra("userID");
        initUI();
        getUserInfo();
    }

    @Override
    protected void initUI() {
        imvAvatar = (CircleImageView) findViewById(R.id.profile_image);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvFullname = (TextView) findViewById(R.id.tv_fullname);
        tvUsername = (TextView) findViewById(R.id.tv_username);
        tvLastSeen = (TextView) findViewById(R.id.tv_lastSeen);

        findViewById(R.id.img_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
        }
    }

    private void getUserInfo() {
        progressDialog.show();
        FirebaseManager.getInstance().getUser(userID, new FirebaseManager.OnUserResponseListener() {
            @Override
            public void onUserResponse(User user) {
                progressDialog.dismiss();
                if (user == null) {
                    finish();
                    return;
                }

                if (user.photo != null && user.photo.length() > 0) {
                    Picasso.with(ProfileActivity.this).load(user.photo).into(imvAvatar);
                }

                tvUsername.setText(user.username);
                tvFullname.setText(user.firstname + " " + user.lastname);
                tvName.setText(user.firstname + " " + user.lastname);


                Object objAry[] = user.dialogs.values().toArray();
                long latestTime;
                if (objAry.length == 0) {
                    tvLastSeen.setText("");
                } else {
                    Map map = (Map) objAry[0];
                    if (!map.containsKey("lastSeenDate"))
                        return;
                    if (objAry.length > 1) {
                        latestTime = (long) map.get("lastSeenDate");
                        for (int i = 1; i < objAry.length; i++) {
                            Map tmpDia = (Map) objAry[i];
                            if (tmpDia.get("lastSeenDate") == null) {
                                continue;
                            }
                            long tmpTime = (long) tmpDia.get("lastSeenDate");
                            if (latestTime < tmpTime)
                                latestTime = tmpTime;
                        }
                    } else {
                        latestTime = (long) map.get("lastSeenDate");
                    }

                    tvLastSeen.setText(DateUtils.formatDateTime(ProfileActivity.this, latestTime, DateUtils.FORMAT_SHOW_TIME));
                }
            }
        });
    }
}
