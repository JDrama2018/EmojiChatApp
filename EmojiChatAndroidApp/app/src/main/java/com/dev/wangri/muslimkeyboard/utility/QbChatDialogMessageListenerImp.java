package com.dev.wangri.muslimkeyboard.utility;

import android.util.Log;

import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.model.QBChatMessage;

public class QbChatDialogMessageListenerImp implements QBChatDialogMessageListener {
    public QbChatDialogMessageListenerImp() {
        Log.d("", "asdasdsadsaa");
    }

    @Override
    public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
        Log.d("", "asdasdsadsaa");
    }

    @Override
    public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {
        Log.d("", "asdasdsadsaa");
    }
}
