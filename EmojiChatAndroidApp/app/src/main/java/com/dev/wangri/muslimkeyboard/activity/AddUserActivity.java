package com.dev.wangri.muslimkeyboard.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.wangri.muslimkeyboard.R;
import com.dev.wangri.muslimkeyboard.bean.User;
import com.dev.wangri.muslimkeyboard.tasks.SendPushTask;
import com.dev.wangri.muslimkeyboard.utility.FirebaseManager;
import com.dev.wangri.muslimkeyboard.utility.Util;
import com.dev.wangri.muslimkeyboard.utility.models.FirebaseValueListener;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.dev.wangri.muslimkeyboard.activity.HomeActivity.MyPREFERENCES;

/**
 * This activity class is used for Subscribing friends from all users.
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2017-1-20
 */

public class AddUserActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String PRESENCE_CHANGE_ERROR = "Presence change error: could not find friend in DB by id = ";
    private static final String ENTRIES_UPDATING_ERROR = "Failed to update friends list";
    private static final String ENTRIES_DELETED_ERROR = "Failed to delete friends";
    private static final String SUBSCRIPTION_ERROR = "Failed to confirm subscription";
    private static final String ROSTER_INIT_ERROR = "ROSTER isn't initialized. Please make relogin";
    private static final int REQUEST_INVITE = 201;
    private ImageView imgBack, ivCancelSearch;
    private EditText edtSearch;
    private ListView listViewAddFriends;
    private String strText = "";
    private FirebaseValueListener searchListener;
    private SearchAdapter searchAdapter;
    private TextView tvFindFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        FirebaseManager.getInstance().searchUserList.clear();
        initView();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (searchListener != null) {
            searchListener.removeListener();
        }
    }

    /**
     * Initialising view and ids
     */

    private void initView() {
        tvFindFriends = (TextView) findViewById(R.id.tvFindFriends);
        imgBack = (ImageView) findViewById(R.id.img_back);
        edtSearch = (EditText) findViewById(R.id.edt_search);
        listViewAddFriends = (ListView) findViewById(R.id.list_add_friends);
        imgBack.setOnClickListener(this);

        ivCancelSearch = (ImageView) findViewById(R.id.img_cancel_search);
        ivCancelSearch.setVisibility(View.INVISIBLE);
        ivCancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivCancelSearch.setVisibility(View.INVISIBLE);
                edtSearch.setText("");
            }
        });

        searchAdapter = new SearchAdapter(AddUserActivity.this);
        listViewAddFriends.setAdapter(searchAdapter);

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                strText = edtSearch.getText().toString().trim();
                if (TextUtils.isEmpty(strText))
                    ivCancelSearch.setVisibility(View.INVISIBLE);
                else
                    ivCancelSearch.setVisibility(View.VISIBLE);
                searchUsers(strText);
            }
        });
    }

    /**
     * This method is used for Handling all the click events
     *
     * @param v
     */

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
        }
    }

    private void searchUsers(String keyword) {
        if (searchListener != null) {
            searchListener.removeListener();
        }

        searchListener = FirebaseManager.getInstance().searchUsers(keyword, new FirebaseManager.OnUpdateListener() {
            @Override
            public void onUpdate() {
                searchAdapter.notifyDataSetChanged();
                if (FirebaseManager.getInstance().searchUserList.size() > 0) {
                    tvFindFriends.setText(String.format("Find %d friends", FirebaseManager.getInstance().searchUserList.size()));
                } else {
                    tvFindFriends.setText("");
                }
            }
        });
    }

    /**
     * Private adapter is used for handling searched user for subscribing.
     */

    private class SearchAdapter extends BaseAdapter {
        Context context1;
        LayoutInflater inflter;

        public SearchAdapter(Context context) {
            this.context1 = context;
            inflter = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return FirebaseManager.getInstance().searchUserList.size();
        }

        @Override
        public Object getItem(int i) {
            return FirebaseManager.getInstance().searchUserList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = inflter.inflate(R.layout.add_friend_list_item, null);
            }

            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.tv_name);
            viewHolder.phoneNumber = (TextView) view.findViewById(R.id.tv_phoneNumber);
            viewHolder.imgSendRequest = (ImageView) view.findViewById(R.id.img_send_request);
            viewHolder.circleImageView = (CircleImageView) view.findViewById(R.id.profile_image);

            User user = (User) getItem(position);

            if (user.photo != null && user.photo.length() > 0) {
                Picasso.with(context1).load(user.photo).into(viewHolder.circleImageView);
            } else
                viewHolder.circleImageView.setImageResource(R.mipmap.profile);

            viewHolder.name.setText(FirebaseManager.getInstance().searchUserList.get(position).firstname + " " + FirebaseManager.getInstance().searchUserList.get(position).lastname);
            viewHolder.phoneNumber.setText(FirebaseManager.getInstance().searchUserList.get(position).username);

            viewHolder.imgSendRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<User> tmpList = FirebaseManager.getInstance().searchUserList;
                    User u = tmpList.get(position);
                    FirebaseManager.getInstance().blockUser(u.id);
/*//                    onInviteClicked(u.username);
                    SharedPreferences sharedpreferences = HomeActivity.getInstance().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                    String username = sharedpreferences.getString("user_name", "");

                    new SendPushTask(Util.pushJsonObjectFrom("Title", username + " sent contact request.", u.pushToken)).execute();
                    FirebaseManager.getInstance().sendRequestToUser(FirebaseManager.getInstance().searchUserList.get(position).id);
                    Toast.makeText(AddUserActivity.this, "Friend request sent!", Toast.LENGTH_SHORT).show();
                    tmpList.remove(position);
                    notifyDataSetChanged();*/
                }
            });

            return view;
        }
    }

    private void onInviteClicked(String mPhoneNumber) {
      /*  try {
            DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setLink(Uri.parse("http://www.muslimemoji.com/"))
                    .setDynamicLinkDomain("g6wup.app.goo.gl")

                    // Open links with this app on Android

                    .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                    .setSocialMetaTagParameters(new DynamicLink.SocialMetaTagParameters.Builder()
                            .setTitle("token")
                            .setDescription(FirebaseManager.getInstance().getCurrentUserID())
                            .build())
                    // Open links with com.example.ios on iOS
//                    .setIosParameters(new DynamicLink.IosParameters.Builder("com.example.ios").build())
                    .buildDynamicLink();

            Uri dynamicLinkUri = dynamicLink.getUri();
            Log.d("ADdUserActivity", "onInviteClicked() " + dynamicLinkUri);
//            String sms = dynamicLinkUri.toString();
//            SmsManager smsManager = SmsManager.getDefault();
//            smsManager.sendTextMessage(mPhoneNumber, null, sms, null, null);
//            Toast.makeText(getApplicationContext(), "SMS Sent!",
//                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "SMS faild, please try again later!",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }*/
        Map<String, String> parameter = new HashMap<>();
        parameter.put("token", FirebaseManager.getInstance().getCurrentUserID());
        Intent intent = new AppInviteInvitation.IntentBuilder("Muslim Emoji App")
                .setMessage("Join me here")
                .setDeepLink(Uri.parse("https://g6wup.app.goo.gl"))
                .setCallToActionText("Accept Request")
                .setAdditionalReferralParameters(parameter)
                .build();
        startActivityForResult(intent, REQUEST_INVITE);

    }

    private class ViewHolder {

        public TextView name, tvAccept, tvCancel;
        public ImageView imgSendRequest;
        public CircleImageView circleImageView;
        public TextView phoneNumber;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("AddUserActivity", "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    Log.d("AddUserActivity", "onActivityResult: sent invitation " + id);
                }
            } else {
                // Sending failed or it was canceled, show failure message to the user
                // ...
            }
        }
    }
}
