package com.dev.wangri.muslimkeyboard.utility;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.dev.wangri.muslimkeyboard.utility.configs.CoreConfigUtils;
import com.dev.wangri.muslimkeyboard.utility.models.QbConfigs;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.core.ServiceZone;

public class CoreApp extends Application {
    public static final String TAG = CoreApp.class.getSimpleName();
    private static final String QB_CONFIG_DEFAULT_FILE_NAME = "qb_config.json";
    private static CoreApp instance;
    private QbConfigs qbConfigs;

    public static synchronized CoreApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initQbConfigs();
        initCredentials();
    }

    private void initQbConfigs() {
        Log.e(TAG, "QB CONFIG FILE NAME: " + getQbConfigFileName());
        qbConfigs = CoreConfigUtils.getCoreConfigsOrNull(getQbConfigFileName());
    }

    public void initCredentials() {
        if (qbConfigs != null) {
            QBSettings.getInstance().init(getApplicationContext(), qbConfigs.getAppId(), qbConfigs.getAuthKey(), qbConfigs.getAuthSecret());
            QBSettings.getInstance().setAccountKey(qbConfigs.getAccountKey());

            if (!TextUtils.isEmpty(qbConfigs.getApiDomain()) && !TextUtils.isEmpty(qbConfigs.getChatDomain())) {
                QBSettings.getInstance().setEndpoints(qbConfigs.getApiDomain(), qbConfigs.getChatDomain(), ServiceZone.PRODUCTION);
                QBSettings.getInstance().setZone(ServiceZone.PRODUCTION);
            }
        }
    }

    public QbConfigs getQbConfigs() {
        return qbConfigs;
    }

    protected String getQbConfigFileName() {
        return QB_CONFIG_DEFAULT_FILE_NAME;
    }
}