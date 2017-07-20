package com.dev.wangri.muslimkeyboard.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dev.wangri.muslimkeyboard.R;
import com.dev.wangri.muslimkeyboard.activity.ChatActivity;
import com.dev.wangri.muslimkeyboard.activity.FriendActivity;
import com.dev.wangri.muslimkeyboard.activity.HomeActivity;
import com.dev.wangri.muslimkeyboard.activity.group.GroupChatActivity;
import com.dev.wangri.muslimkeyboard.adapter.ContactAdapter;
import com.dev.wangri.muslimkeyboard.adapter.widget.IndexableListView;
import com.dev.wangri.muslimkeyboard.bean.User;
import com.dev.wangri.muslimkeyboard.utility.FirebaseManager;
import com.dev.wangri.muslimkeyboard.utility.models.FirebaseValueListener;
import com.google.android.gms.appinvite.AppInviteInvitation;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = ContactFragment.class.getName();

    LinearLayout actionMenuLayout;
    View maskView;
    IndexableListView list;
    ImageView imgMenu;
    EditText edtSearch;

    TextView tvHeader;

    RelativeLayout friendRequestsNotificationLayout;
    TextView tvFriendRequestsNotificationCount;

    LinearLayout friendRequestsLayout;
    TextView tvFriendRequestsCount;
    ListView listFriendRequests;
    ImageView imgBack, ivCancelSearch;

    View view;
    FirebaseValueListener friendsListener, friendRequestsListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_contact, container, false);

        initView();
        getContact();
        getFriendRequests();
        return view;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (friendsListener != null) {
            friendsListener.removeListener();
        }

        if (friendRequestsListener != null) {
            friendRequestsListener.removeListener();
        }
    }

    private void initView() {
        actionMenuLayout = (LinearLayout) view.findViewById(R.id.actionMenuLayout);
        maskView = view.findViewById(R.id.maskView);
        list = (IndexableListView) view.findViewById(R.id.list);
        imgMenu = (ImageView) view.findViewById(R.id.img_add);
        edtSearch = (EditText) view.findViewById(R.id.edt_search);
        View empty = view.findViewById(R.id.emptyView);
        list.setEmptyView(empty);

        ivCancelSearch = (ImageView) view.findViewById(R.id.img_cancel_search);
        ivCancelSearch.setVisibility(View.INVISIBLE);

        ivCancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtSearch.setText("");
            }
        });

        tvHeader = (TextView) view.findViewById(R.id.headName);
        tvHeader.setText("Contacts");

        maskView.setOnClickListener(this);
        view.findViewById(R.id.img_add).setOnClickListener(this);
        view.findViewById(R.id.new_chat).setOnClickListener(this);
        view.findViewById(R.id.add_friends).setOnClickListener(this);
        view.findViewById(R.id.group_chat).setOnClickListener(this);

        friendRequestsNotificationLayout = (RelativeLayout) view.findViewById(R.id.friendRequestsNotificationLayout);
        tvFriendRequestsNotificationCount = (TextView) view.findViewById(R.id.tvFriendRequestsNotificationCount);
        friendRequestsLayout = (LinearLayout) view.findViewById(R.id.friendRequestsLayout);
        tvFriendRequestsCount = (TextView) view.findViewById(R.id.tvFriendRequestsCount);
        listFriendRequests = (ListView) view.findViewById(R.id.listFriendRequests);
        imgBack = (ImageView) view.findViewById(R.id.img_back);

        friendRequestsNotificationLayout.setOnClickListener(this);
        imgBack.setOnClickListener(this);
    }

    public void getFriendRequests() {
        final RequestAdapter adapter = new RequestAdapter(getActivity());
        listFriendRequests.setAdapter(adapter);

        friendRequestsListener = FirebaseManager.getInstance().addRequestListener(new FirebaseManager.OnUpdateListener() {
            @Override
            public void onUpdate() {
                if (FirebaseManager.getInstance().requestList.size() == 0) {
                    friendRequestsNotificationLayout.setVisibility(View.GONE);
                    tvFriendRequestsCount.setVisibility(View.GONE);
                } else {
                    friendRequestsNotificationLayout.setVisibility(View.VISIBLE);
                    tvFriendRequestsCount.setVisibility(View.VISIBLE);
                    tvFriendRequestsCount.setText("" + FirebaseManager.getInstance().requestList.size());
                    tvFriendRequestsNotificationCount.setText("" + FirebaseManager.getInstance().requestList.size());
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void getContact() {
//        final ContactListAdapter adapter = new ContactListAdapter(getActivity());
        final ContactAdapter adapter = new ContactAdapter(FirebaseManager.getInstance().friendList, getActivity());
        list.setAdapter(adapter);
        list.setFastScrollEnabled(true);

        friendsListener = FirebaseManager.getInstance().addFriendListener(new FirebaseManager.OnUpdateListener() {
            @Override
            public void onUpdate() {
                String text = edtSearch.getText().toString().toLowerCase(Locale.getDefault());
                adapter.filter(text);

                if (FirebaseManager.getInstance().friendList.size() > 0)
                    tvHeader.setText("Contacts(" + FirebaseManager.getInstance().friendList.size() + ")");
                else
                    tvHeader.setText("Contacts");
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = (User) adapter.getItem(position);
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                String text = edtSearch.getText().toString().toLowerCase(Locale.getDefault());
                adapter.filter(text);
                if (TextUtils.isEmpty(text))
                    ivCancelSearch.setVisibility(View.INVISIBLE);
                else
                    ivCancelSearch.setVisibility(View.VISIBLE);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void showActionMenu() {
        actionMenuLayout.setVisibility(View.VISIBLE);
        maskView.setVisibility(View.VISIBLE);
    }

    private void hideActionMenu() {
        actionMenuLayout.setVisibility(View.GONE);
        maskView.setVisibility(View.GONE);
    }

    private void showFriendRequests() {
        friendRequestsLayout.setVisibility(View.VISIBLE);
        imgBack.setVisibility(View.VISIBLE);
    }

    private void hideFriendRequests() {
        friendRequestsLayout.setVisibility(View.GONE);
        imgBack.setVisibility(View.GONE);
    }

    /**
     * This method is used for Handling all the click events
     *
     * @param v
     */


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_add:
//                showActionMenu();
                break;
            case R.id.maskView:
//                hideActionMenu();
                break;
            case R.id.new_chat:
//                hideActionMenu();
                Intent intent1 = new Intent(getActivity(), FriendActivity.class);
                startActivity(intent1);
                break;
            case R.id.add_friends:
//                hideActionMenu();
                onInviteClicked();
//                Intent i = new Intent(getActivity(), AddUserActivity.class);
//                startActivity(i);

                break;
            case R.id.group_chat:
//                hideActionMenu();

                Intent intent = new Intent(getActivity(), GroupChatActivity.class);
                startActivity(intent);
                break;
            case R.id.friendRequestsNotificationLayout:
                showFriendRequests();
                break;
            case R.id.img_back:
                hideFriendRequests();
                break;
        }
    }

    private void onInviteClicked() {

        Map<String, String> parameter = new HashMap<>();
        parameter.put("token", FirebaseManager.getInstance().getCurrentUserID());
        Intent intent = new AppInviteInvitation.IntentBuilder("Muslim Emoji App")
                .setMessage("Join me here")
                .setDeepLink(Uri.parse("https://g6wup.app.goo.gl?token=" + FirebaseManager.getInstance().getCurrentUserID()))
                .setCallToActionText("Accept Request")
//                .setAdditionalReferralParameters(parameter)
//                .setOtherPlatformsTargetApplication(AppInviteInvitation.IntentBuilder.PlatformMode.PROJECT_PLATFORM_IOS, "com.dev.wangri.muslimojis")
                .build();
        startActivityForResult(intent, HomeActivity.REQUEST_INVITE);

    }

    private class RequestAdapter extends BaseAdapter {
        Context context1;
        LayoutInflater inflter;

        public RequestAdapter(Context context) {
            this.context1 = context;
            inflter = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return FirebaseManager.getInstance().requestList.size();
        }

        @Override
        public Object getItem(int i) {
            return FirebaseManager.getInstance().requestList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {

            view = inflter.inflate(R.layout.friend_request_list_item, null);

            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.tv_name);
            viewHolder.tv_phoneNumber = (TextView) view.findViewById(R.id.tv_phoneNumber);
            viewHolder.imgAcceptRequest = (ImageView) view.findViewById(R.id.img_accept_friend_request);
            viewHolder.imgRejectRequest = (ImageView) view.findViewById(R.id.img_reject_friend_request);
            viewHolder.circleImageView = (CircleImageView) view.findViewById(R.id.profile_image);
            User user = FirebaseManager.getInstance().requestList.get(position);
            viewHolder.name.setText(String.format("%s %s", user.firstname, user.lastname));
            viewHolder.tv_phoneNumber.setText(String.format("%s", user.username));

            viewHolder.imgAcceptRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseManager.getInstance().acceptFriendRequest(FirebaseManager.getInstance().requestList.get(position).id);
                    FirebaseManager.getInstance().requestList.remove(position);
                    notifyDataSetChanged();
                }
            });
            viewHolder.imgRejectRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseManager.getInstance().rejectFriendRequest(FirebaseManager.getInstance().requestList.get(position).id);
                    FirebaseManager.getInstance().requestList.remove(position);
                    notifyDataSetChanged();
                }
            });

            return view;
        }
    }

    private class ViewHolder {

        public TextView name, tv_phoneNumber;
        public ImageView imgAcceptRequest, imgRejectRequest;
        public CircleImageView circleImageView;
    }
}
