package com.dev.wangri.muslimkeyboard.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.wangri.muslimkeyboard.R;
import com.dev.wangri.muslimkeyboard.bean.Dialog;
import com.dev.wangri.muslimkeyboard.fragment.ChatListFragment;
import com.dev.wangri.muslimkeyboard.fragment.ContactFragment;
import com.dev.wangri.muslimkeyboard.fragment.EmojiFragment;
import com.dev.wangri.muslimkeyboard.fragment.SettingFragment;
import com.dev.wangri.muslimkeyboard.utility.AdmobInterstitial;
import com.dev.wangri.muslimkeyboard.utility.AppConst;
import com.dev.wangri.muslimkeyboard.utility.BaseActivity;
import com.dev.wangri.muslimkeyboard.utility.FirebaseManager;
import com.dev.wangri.muslimkeyboard.utility.UI.ProgressDialogFragment;
import com.dev.wangri.muslimkeyboard.utility.Util;
import com.dev.wangri.muslimkeyboard.utility.models.FirebaseValueListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.TimerTask;


public class HomeActivity extends BaseActivity {

    public static final String MyPREFERENCES = "MyPrefs";
    private static HomeActivity activityInstance = null;
    FirebaseAuth mAuth;
    Boolean bFirstTime = true;
    FirebaseValueListener friendRequestsListener = null, sentListener = null;
    FirebaseValueListener valueListener;
    TextView tvBadgeContact, tvBadgeChat;
    TimerTask hourlyTask;
    private TextView contacts_txtview, chats_txtview, emoji_txtview, setting_txtview;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private Context mContext;
    private SharedPreferences sharedpreferences;
    private AppConst appConst = new AppConst();

    public static HomeActivity getInstance() {
        return activityInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        findViews();
        mContext = this;
        if (bFirstTime == true) {
            Util.getInstance().initEmoticanMap(this);
            init();
            AdmobInterstitial.loadInterstitial(this);
        }

        bFirstTime = false;
        activityInstance = this;
    }

    @Override
    protected void initUI() {

    }

    /**
     * This method is used to change color of home button when click Home Tab
     * return nothing
     */
    private void ShowCustomBottomContactColor() {

        contacts_txtview.setTextColor(getResources().getColor(R.color.green));
        chats_txtview.setTextColor(getResources().getColor(R.color.dark_gray));
        emoji_txtview.setTextColor(getResources().getColor(R.color.dark_gray));
        setting_txtview.setTextColor(getResources().getColor(R.color.dark_gray));

        contacts_txtview.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.unselected_contacts_tab, 0, 0);
        chats_txtview.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.chat_tab, 0, 0);
        emoji_txtview.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.emoji_tab, 0, 0);
        setting_txtview.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.setting_tab, 0, 0);

    }

    /**
     * Find the Views in the layout<br />
     * <br />
     */
    private void findViews() {
        contacts_txtview = (TextView) findViewById(R.id.txt_contacts);
        emoji_txtview = (TextView) findViewById(R.id.txt_emoji);
        setting_txtview = (TextView) findViewById(R.id.txt_setting);
        chats_txtview = (TextView) findViewById(R.id.txt_chats);
    }


    /**
     * This method is called when click Home Tab
     *
     * @param view
     */
    public void contactOnClick(View view) {
        ShowCustomBottomContactColor();
        ContactFragment fragment = new ContactFragment();
        loadFragments(fragment, ContactFragment.TAG);
    }


    /**
     * This method is called when Politica Tab clicked
     *
     * @param view
     */
    public void chatOnClick(View view) {
        ShowCustomBottomChatColor();
        ChatListFragment fragment = new ChatListFragment();
        loadFragments(fragment, ChatListFragment.TAG);
    }

    /**
     * This method is Used to change color of politica tab when clicked
     */
    private void ShowCustomBottomChatColor() {
        contacts_txtview.setTextColor(getResources().getColor(R.color.dark_gray));
        chats_txtview.setTextColor(getResources().getColor(R.color.green));
        emoji_txtview.setTextColor(getResources().getColor(R.color.dark_gray));
        setting_txtview.setTextColor(getResources().getColor(R.color.dark_gray));

        contacts_txtview.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.contact_tab, 0, 0);
        chats_txtview.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.unselected_chats_tab, 0, 0);
        emoji_txtview.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.emoji_tab, 0, 0);
        setting_txtview.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.setting_tab, 0, 0);

    }

    /**
     * This method is used to Load Fragments
     */
    private void loadFragments(Fragment fragment, String tag) {
        fragmentManager = this.getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.lytContent, fragment, tag);
        fragmentTransaction.commit();
    }

    public void removeFragment(Fragment fragment) {
        fragmentManager = this.getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
    }

    /**
     * This method is called when click CDC Tab
     *
     * @param view
     */
    public void emojiOnClick(View view) {
        ShowCustomEmojiChatColor();
        EmojiFragment fragment = new EmojiFragment();
        loadFragments(fragment, EmojiFragment.TAG);
    }

    /**
     * This method is used to change color of Emoji Tab.
     */
    private void ShowCustomEmojiChatColor() {

        contacts_txtview.setTextColor(getResources().getColor(R.color.dark_gray));
        chats_txtview.setTextColor(getResources().getColor(R.color.dark_gray));
        emoji_txtview.setTextColor(getResources().getColor(R.color.green));
        setting_txtview.setTextColor(getResources().getColor(R.color.dark_gray));

        contacts_txtview.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.contact_tab, 0, 0);
        chats_txtview.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.chat_tab, 0, 0);
        emoji_txtview.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.unselected_emoji_tab, 0, 0);
        setting_txtview.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.setting_tab, 0, 0);
    }

    /**
     * This method is called when you clicked Briefing Tab.
     *
     * @param view
     */
    public void settingOnClick(View view) {
        ShowCustomSettingChatColor();
        SettingFragment fragment = new SettingFragment();
        loadFragments(fragment, SettingFragment.TAG);
    }

    /**
     * This method is called for change color of Setting when clicked Setting Tab.
     */
    private void ShowCustomSettingChatColor() {
        contacts_txtview.setTextColor(getResources().getColor(R.color.dark_gray));
        chats_txtview.setTextColor(getResources().getColor(R.color.dark_gray));
        emoji_txtview.setTextColor(getResources().getColor(R.color.dark_gray));
        setting_txtview.setTextColor(getResources().getColor(R.color.green));

        contacts_txtview.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.contact_tab, 0, 0);
        chats_txtview.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.chat_tab, 0, 0);
        emoji_txtview.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.emoji_tab, 0, 0);
        setting_txtview.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.unselected_setting_tab, 0, 0);
    }

    void init() {

        tvBadgeChat = (TextView) findViewById(R.id.tvChatBadge);
        tvBadgeChat.setVisibility(View.INVISIBLE);

        tvBadgeContact = (TextView) findViewById(R.id.tvContactBadge);
        tvBadgeContact.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.remove("session");
            editor.remove("user_email");
            editor.remove("user_pass");
            editor.apply();

            finish();
        }

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String email = sharedpreferences.getString("user_email", "");
        String pass = sharedpreferences.getString("user_pass", "");

        ProgressDialogFragment.show(getSupportFragmentManager());
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        ProgressDialogFragment.hide(getSupportFragmentManager());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {

                            Toast.makeText(HomeActivity.this, task.getException().getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();

                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.remove("session");
                            editor.remove("user_email");
                            editor.remove("user_pass");
                            editor.apply();

                            finish();
                        }

                        chatOnClick(null);
                    }
                });

        friendRequestsListener = FirebaseManager.getInstance().addRequestListener(new FirebaseManager.OnUpdateListener() {
            @Override
            public void onUpdate() {
                if (FirebaseManager.getInstance().requestList.size() == 0) {
                    tvBadgeContact.setVisibility(View.INVISIBLE);
                } else {
                    tvBadgeContact.setVisibility(View.VISIBLE);
                    tvBadgeContact.setText("" + FirebaseManager.getInstance().requestList.size());
                }
            }
        });

        valueListener = FirebaseManager.getInstance().addDialogListener(new FirebaseManager.OnUpdateListener() {
            @Override
            public void onUpdate() {
                Log.e("DialogList:", String.valueOf(FirebaseManager.getInstance().dialogList.size()));
                updateChatBadge();
            }
        });

        sentListener = FirebaseManager.getInstance().addSentListener(new FirebaseManager.OnUpdateListener() {
            @Override
            public void onUpdate() {
            }
        });

    }

    public void updateChatBadge() {
        int totalBadge = 0;
        for (int i = 0; i < FirebaseManager.getInstance().dialogList.size(); i++) {
            Dialog dia = FirebaseManager.getInstance().dialogList.get(i);
            if (FirebaseManager.getInstance().dialogBadges.containsKey(dia.dialogID)) {
                totalBadge += Integer.valueOf(FirebaseManager.getInstance().dialogBadges.get(dia.dialogID));
            }
        }

        Log.e("UpdateChatBadge Called:", String.valueOf(totalBadge));

        if (totalBadge == 0) {
            tvBadgeChat.setText("");
            tvBadgeChat.setVisibility(View.INVISIBLE);
        } else {
            tvBadgeChat.setText(String.valueOf(totalBadge));
            tvBadgeChat.setVisibility(View.VISIBLE);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (friendRequestsListener != null) {
            friendRequestsListener.removeListener();
        }

        if (sentListener != null) {
            sentListener.removeListener();
        }
    }
}

