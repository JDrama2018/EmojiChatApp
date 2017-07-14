package com.dev.wangri.muslimkeyboard.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dev.wangri.muslimkeyboard.R;
import com.dev.wangri.muslimkeyboard.activity.group.GroupInfoActivity;
import com.dev.wangri.muslimkeyboard.activity.profile.ProfileActivity;
import com.dev.wangri.muslimkeyboard.adapter.ChatActivityAdapter;
import com.dev.wangri.muslimkeyboard.adapter.ViewPagerAdapter;
import com.dev.wangri.muslimkeyboard.adapter.ViewPagerIndicator;
import com.dev.wangri.muslimkeyboard.bean.Dialog;
import com.dev.wangri.muslimkeyboard.bean.Message;
import com.dev.wangri.muslimkeyboard.bean.User;
import com.dev.wangri.muslimkeyboard.utility.BaseActivity;
import com.dev.wangri.muslimkeyboard.utility.FirebaseManager;
import com.dev.wangri.muslimkeyboard.utility.Util;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.MultiCallback;

import static android.Manifest.permission.CAMERA;

/**
 * This activity class is used for handling all the functionality regarding chat including receiving
 * and sending text messages.
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2017-1-20
 */

public class ChatActivity extends BaseActivity implements View.OnClickListener {
    private static final int CAMERA_REQUEST = 102;
    private final static int ALL_PERMISSIONS_RESULT = 107;
    private static ChatActivity activityInstance = null;

    ListView list;
    EditText editTextMessage;
    ImageView imgViewSend, imageView, selectEmoji, selectKeyboard, imgAttach;
    TextView chatNameHead;
    ChatActivityAdapter chatAdapter;
    Dialog dialog;
    Bitmap yourSelectedImage;
    ArrayList permissionsToRequest;
    ArrayList permissionsRejected = new ArrayList();
    ArrayList permissions = new ArrayList();
    RelativeLayout overlayLayout;
    ViewPagerAdapter adapter;
    LinearLayout customKeyboardLayout;
    View.OnTouchListener otl;
    private String selectedEmojiName = "";
    private ImageView imgBack;
    private CircleImageView imgProfile;
    private ArrayList<Message> messages = new ArrayList<>();
    private ViewPager viewPager;
    private ViewPagerIndicator viewPagerIndicator;
    private View.OnClickListener categoryClickListener;
    private int selCatIndex = 0;
    private AdView mAdView;
    private LinearLayout bannerLayout;

    private EmoticonHandler mEmoticonHandler;
    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    File rootsd = Environment.getExternalStorageDirectory();
                    File dcim = new File(rootsd.getAbsolutePath() + "/Pictures/MuslimEmojis");
                    if (!dcim.exists()) {
                        if (dcim.mkdirs()) {
                            Log.e("Cloud", "Succeeded in creation of Directory");
                        } else {
                            Log.e("Cloud", "Failed in creation of Directory");
                        }
                    } else {
                        Log.e("Cloud", "Directory already exist.");
                    }
                    Log.e("Cloud Filename", dcim.getPath() + "/" + String.valueOf(Calendar.getInstance().getTimeInMillis()) + ".png");
                    File file = new File(dcim.getPath() + "/" + String.valueOf(Calendar.getInstance().getTimeInMillis()) + ".png");
                    try {
                        file.createNewFile();
                        FileOutputStream ostream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
                        ostream.close();
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                    }
                    MediaScannerConnection.scanFile(ChatActivity.this, new String[]{file.getPath()}, new String[]{"image/png"}, null);
                    progressDialog.dismiss();
                }
            }).start();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            if (placeHolderDrawable != null) {
            }
        }
    };

    public static ChatActivity getInstance() {
        return activityInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        permissions.add(CAMERA);
        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }
        initView();

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("user")) {
                User user = (User) intent.getSerializableExtra("user");
                dialog = Dialog.createIndividualDialog(user);
                FirebaseManager.getInstance().createIndividualDialog(user);
            } else if (intent.hasExtra("dialog")) {
                dialog = (Dialog) intent.getSerializableExtra("dialog");
            } else {
                finish();
            }

            chatNameHead.setText(dialog.title);
            if (dialog.photo != null && dialog.photo.length() > 0) {
                Picasso.with(this).load(dialog.photo).into(imgProfile);
            } else {
                imgProfile.setImageResource(R.mipmap.profile);
            }

            loadChatHistory();
//            InputBoardView.getInstance(this).setOnOutKeyboard(this);
        } else {
            finish();
        }

        activityInstance = this;
    }

    @Override
    public void onBackPressed() {
        if (!bIsCustomKeyboardHiden()) {
            showCustomKeyboard(false);
            return;
        }

        super.onBackPressed();
        FirebaseManager.getInstance().removeMessageListener();
    }

    @Override
    protected void initUI() {

    }

    /**
     * Method used for initialising view.
     */

    private void initView() {

        mAdView = (AdView) findViewById(R.id.ad_view);

        bannerLayout = (LinearLayout) findViewById(R.id.bannerLayout);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("0EA25750FBB0506E9A2EF967197E18F3")
                .build();

        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                findViewById(R.id.bannerFrame).setVisibility(View.VISIBLE);
            }
        });

        customKeyboardLayout = (LinearLayout) findViewById(R.id.layoutKeyboard);

        overlayLayout = (RelativeLayout) findViewById(R.id.overlayLayout);
        overlayLayout.setVisibility(View.GONE);

        findViewById(R.id.layoutTakePhoto).setOnClickListener(this);
        findViewById(R.id.layoutChoosePhoto).setOnClickListener(this);
        findViewById(R.id.layoutCancel).setOnClickListener(this);
        findViewById(R.id.maskView).setOnClickListener(this);

        imgViewSend = (ImageView) findViewById(R.id.image_view_send);
        selectEmoji = (ImageView) findViewById(R.id.select_emoji);
        selectKeyboard = (ImageView) findViewById(R.id.select_keyboard);

        editTextMessage = (EditText) findViewById(R.id.edit_message);

        mEmoticonHandler = new EmoticonHandler(this, editTextMessage);

        chatNameHead = (TextView) findViewById(R.id.tvChatName);
        list = (ListView) findViewById(R.id.list);
        imageView = (ImageView) findViewById(R.id.image);
        imgBack = (ImageView) findViewById(R.id.iv_back_chat);
        imgProfile = (CircleImageView) findViewById(R.id.iv_profile);
        imgAttach = (ImageView) findViewById(R.id.attechment_file);
        imgViewSend.setOnClickListener(this);
        imgAttach.setOnClickListener(this);
        imgProfile.setOnClickListener(this);
        registerForContextMenu(imgAttach);

        selectEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectEmoji.setVisibility(View.GONE);
                selectKeyboard.setVisibility(View.VISIBLE);

                View view = ChatActivity.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                showCustomKeyboard(true);
            }
        });

        selectKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectEmoji.setVisibility(View.VISIBLE);
                selectKeyboard.setVisibility(View.GONE);
                if (!bIsCustomKeyboardHiden())
                    showCustomKeyboard(false);
                InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                im.toggleSoftInput(1, 0);
                editTextMessage.requestFocus();
            }
        });

        chatAdapter = new ChatActivityAdapter(ChatActivity.this, messages);
        list.setAdapter(chatAdapter);
        list.setLongClickable(true);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Message msg = (Message) chatAdapter.getItem(position);
                if (msg.type == Message.MessageType.Photo) {
                    showSaveAlertDialog(position);
                }
                return true;
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Message msg = (Message) chatAdapter.getItem(position);
                if (msg.type == Message.MessageType.Photo) {
                    Intent intent = new Intent(ChatActivity.this, PhotoActivity.class);
                    intent.putExtra("PhotoURL", msg.message);
                    startActivity(intent);
                }
            }
        });
        imgViewSend.setOnClickListener(this);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ChatActivity.super.onBackPressed();

            }
        });

        initCustomKeyboard();
    }

    public void hideKeyboards() {
        if (!bIsCustomKeyboardHiden()) {
            showCustomKeyboard(false);
        }

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void saveImageToGallery(int position) {
        Message message = (Message) chatAdapter.getItem(position);
        progressDialog.show();
//        Picasso.with(this)
//                .load(message.message)
//                .into(imageView, new Callback.EmptyCallback() {
//                    @Override public void onSuccess() {
//                        progressDialog.dismiss();
//                    }
//                    @Override
//                    public void onError() {
//                        progressDialog.dismiss();
//                    }
//                });

        Picasso.with(this)
                .load(message.message)
                .into(target);
    }

    public void showSaveAlertDialog(final int position) {
        AlertDialog alertDialog = new AlertDialog.Builder(ChatActivity.this).create();
        alertDialog.setTitle("");
        alertDialog.setMessage("Save this image to Gallery?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        saveImageToGallery(position);
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public boolean bIsCustomKeyboardHiden() {
        RelativeLayout.LayoutParams layoutParam = (RelativeLayout.LayoutParams) customKeyboardLayout.getLayoutParams();
        if (layoutParam.height == 0) {
            return true;
        } else {
            return false;
        }
    }

    public void showCustomKeyboard(boolean bShow) {
        if (bShow) {
            RelativeLayout.LayoutParams layoutParam = (RelativeLayout.LayoutParams) customKeyboardLayout.getLayoutParams();
            layoutParam.height = (int) Util.pxFromDp(this, 226);
            customKeyboardLayout.setLayoutParams(layoutParam);
        } else {
            RelativeLayout.LayoutParams layoutParam = (RelativeLayout.LayoutParams) customKeyboardLayout.getLayoutParams();
            layoutParam.height = 0;
            customKeyboardLayout.setLayoutParams(layoutParam);
        }
    }

    public void initCustomKeyboard() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPagerIndicator = (ViewPagerIndicator) findViewById(R.id.viewPagerIndicator);
        adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        viewPagerIndicator.initWithViewPager(viewPager);

        categoryClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.layoutCategory0:
                        selCatIndex = 0;
                        refreshKeyboardCategories(0);
                        break;
                    case R.id.layoutCategory1:
                        selCatIndex = 1;
                        refreshKeyboardCategories(1);
                        break;
                    case R.id.layoutCategory2:
                        selCatIndex = 2;
                        refreshKeyboardCategories(2);
                        break;
                    case R.id.layoutCategory3:
                        selCatIndex = 3;
                        refreshKeyboardCategories(3);
                        break;
                    case R.id.layoutCategory4:
                        selCatIndex = 4;
                        refreshKeyboardCategories(4);
                        break;
                    case R.id.layoutCategory5:
                        selCatIndex = 5;
                        refreshKeyboardCategories(5);
                        break;
                    case R.id.layoutCategory6:
                        selCatIndex = 6;
                        refreshKeyboardCategories(6);
                        break;
                    case R.id.layoutCategory7:
                        selCatIndex = 7;
                        refreshKeyboardCategories(7);
                        break;
                    case R.id.layoutCategory8:
                        selCatIndex = 8;
                        refreshKeyboardCategories(8);
                        break;
                    default:
                        break;
                }
                refreshEmojiGridView(selCatIndex);
            }
        };

        findViewById(R.id.layoutCategory0).setOnClickListener(categoryClickListener);
        findViewById(R.id.layoutCategory1).setOnClickListener(categoryClickListener);
        findViewById(R.id.layoutCategory2).setOnClickListener(categoryClickListener);
        findViewById(R.id.layoutCategory3).setOnClickListener(categoryClickListener);
        findViewById(R.id.layoutCategory4).setOnClickListener(categoryClickListener);
        findViewById(R.id.layoutCategory5).setOnClickListener(categoryClickListener);
        findViewById(R.id.layoutCategory6).setOnClickListener(categoryClickListener);
        findViewById(R.id.layoutCategory7).setOnClickListener(categoryClickListener);
        findViewById(R.id.layoutCategory8).setOnClickListener(categoryClickListener);

        refreshKeyboardCategories(selCatIndex);

        otl = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (bIsCustomKeyboardHiden()) {
                    selectEmoji.setVisibility(View.VISIBLE);
                    selectKeyboard.setVisibility(View.GONE);
                    return false;
                }

                final InputMethodManager imm = ((InputMethodManager) ChatActivity.this
                        .getSystemService(Context.INPUT_METHOD_SERVICE));
                try {
                    imm.hideSoftInputFromWindow(editTextMessage.getApplicationWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        };

        editTextMessage.setOnTouchListener(otl);
    }

    public void refreshEmojiGridView(int categoryIndex) {
        adapter.refreshData(categoryIndex);
        viewPager.setCurrentItem(0);
        viewPagerIndicator.initWithViewPager(viewPager);
    }

    public void refreshKeyboardCategories(int activeIndex) {
        ImageView ivCategory0 = (ImageView) findViewById(R.id.ivCategory0);
        ImageView ivCategory1 = (ImageView) findViewById(R.id.ivCategory1);
        ImageView ivCategory2 = (ImageView) findViewById(R.id.ivCategory2);
        ImageView ivCategory3 = (ImageView) findViewById(R.id.ivCategory3);
        ImageView ivCategory4 = (ImageView) findViewById(R.id.ivCategory4);
        ImageView ivCategory5 = (ImageView) findViewById(R.id.ivCategory5);
        ImageView ivCategory6 = (ImageView) findViewById(R.id.ivCategory6);
        ImageView ivCategory7 = (ImageView) findViewById(R.id.ivCategory7);
        ImageView ivCategory8 = (ImageView) findViewById(R.id.ivCategory8);

        ivCategory0.setImageResource(R.drawable.subcategory_0);
        ivCategory1.setImageResource(R.drawable.subcategory_1);
        ivCategory2.setImageResource(R.drawable.subcategory_2);
        ivCategory3.setImageResource(R.drawable.subcategory_3);
        ivCategory4.setImageResource(R.drawable.subcategory_4);
        ivCategory5.setImageResource(R.drawable.subcategory_5);
        ivCategory6.setImageResource(R.drawable.subcategory_6);
        ivCategory7.setImageResource(R.drawable.subcategory_7);
        ivCategory8.setImageResource(R.drawable.subcategory_8);

        switch (activeIndex) {
            case 0:
                ivCategory0.setImageResource(R.drawable.subcategory_0_active);
                break;
            case 1:
                ivCategory1.setImageResource(R.drawable.subcategory_1_active);
                break;
            case 2:
                ivCategory2.setImageResource(R.drawable.subcategory_2_active);
                break;
            case 3:
                ivCategory3.setImageResource(R.drawable.subcategory_3_active);
                break;
            case 4:
                ivCategory4.setImageResource(R.drawable.subcategory_4_active);
                break;
            case 5:
                ivCategory5.setImageResource(R.drawable.subcategory_5_active);
                break;
            case 6:
                ivCategory6.setImageResource(R.drawable.subcategory_6_active);
                break;
            case 7:
                ivCategory7.setImageResource(R.drawable.subcategory_7_active);
                break;
            case 8:
                ivCategory8.setImageResource(R.drawable.subcategory_8_active);
                break;
            default:
                break;
        }
    }

    /**
     * This is an override method for handling click events.
     *
     * @param v
     */


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.maskView:
                hideAlertMenu();
                break;
            case R.id.image_view_send:
                if (!TextUtils.isEmpty(editTextMessage.getText().toString())) {
                    sendChatMessage(editTextMessage.getText().toString(), Message.MessageType.Text);
                    editTextMessage.setText("");
                } else if (!selectedEmojiName.equals("")) {
                    String converted = convertAndroidToIos(selectedEmojiName);
                    sendChatMessage(converted, Message.MessageType.Emoji);
                    selectedEmojiName = "";
                }
                break;

            case R.id.attechment_file:
//                openContextMenu(imgAttach);
                showAlertMenu();
                break;

            case R.id.iv_profile:
                showProfileActivity();
                break;
            case R.id.layoutTakePhoto:
                hideAlertMenu();
                Intent dataBack = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivityForResult(dataBack, CAMERA_REQUEST);
                break;
            case R.id.layoutChoosePhoto:
                hideAlertMenu();
                Crop.pickImage(this);
                break;
            case R.id.layoutCancel:
                hideAlertMenu();
                break;
        }
    }

    private void showAlertMenu() {
        overlayLayout.setVisibility(View.VISIBLE);
    }

    private void hideAlertMenu() {
        overlayLayout.setVisibility(View.GONE);
    }

    public void emojiButtonClicked(String emojiString) {
        Editable text = editTextMessage.getText();
        text.insert(editTextMessage.getSelectionStart(), "(" + emojiString + ")");
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Upload profile picture from");
        menu.add(0, v.getId(), 0, "Gallery");
        menu.add(0, v.getId(), 0, "Camera");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Gallery") {
            Crop.pickImage(this);
            return true;
        } else if (item.getTitle() == "Camera") {
            Intent dataBack = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivityForResult(dataBack, CAMERA_REQUEST);
            closeContextMenu();
            return true;
        } else if (item.getTitle() == "Cancel") {
            closeContextMenu();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CAMERA_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        yourSelectedImage = (Bitmap) data.getExtras().get("data");
                        Uri tempUri = Util.getInstance().getImageUri(this, yourSelectedImage);
                        uploadPhoto(tempUri);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                break;

            default:
                if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
                    Uri uri = data.getData();

                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        // Log.d(TAG, String.valueOf(bitmap));
                        yourSelectedImage = bitmap;
                        Uri tempUri = Util.getInstance().getImageUri(this, yourSelectedImage);
                        uploadPhoto(tempUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mAdView != null) {
            mAdView.resume();

            mAdView.setVisibility(View.VISIBLE);
            bannerLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }


    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            try {
                uploadPhoto(Crop.getOutput(result));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (resultCode == Crop.RESULT_ERROR) {
            // Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(Object permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission((String) permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (Object perms : permissionsToRequest) {
                    if (hasPermission(perms)) {

                    } else {

                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale((String) permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                                                //Log.d("API123", "permisionrejected " + permissionsRejected.size());

                                                requestPermissions((String[]) permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    private void uploadPhoto(Uri uri) {
        progressDialog.show();
        FirebaseManager.getInstance().uploadPhoto(uri, new FirebaseManager.OnStringListener() {
            @Override
            public void onStringResponse(String value) {
                progressDialog.dismiss();
                if (value != null) {
                    sendChatMessage(value, Message.MessageType.Photo);
                }
            }
        });
    }

    private void sendChatMessage(String text, Message.MessageType messageType) {
        Message message = new Message();
        message.userID = FirebaseManager.getInstance().getCurrentUserID();
        message.type = messageType;
        message.message = text;
        message.dateSent = System.currentTimeMillis() / 1000;
        FirebaseManager.getInstance().createDialogMessage(dialog, message);
    }

    private void showProfileActivity() {
        if (dialog.type == Dialog.DialogType.Individual) {
            Intent i = new Intent(ChatActivity.this, ProfileActivity.class);
            i.putExtra("userID", dialog.occupantsIds.get(0));
            startActivity(i);
        } else if (dialog.type == Dialog.DialogType.Group) {
            Intent i = new Intent(ChatActivity.this, GroupInfoActivity.class);
            i.putExtra("dialog", dialog);
            startActivity(i);
            finish();
        }
    }

    /**
     * Method used for displaying received messages and emoji into chat box.
     *
     * @param message
     */

    /**
     * Method used for scrolling chat box mhen new message added to box.
     */

    private void scrollMessageListDown() {

        list.clearFocus();
        list.post(new Runnable() {
            @Override
            public void run() {
                list.setSelection(list.getCount() - 1);
            }
        });

    }

    /**
     * Loading chat history in the below method.
     **/

    private void loadChatHistory() {
        FirebaseManager.getInstance().addMessageListener(dialog, new FirebaseManager.OnMessageListener() {
            @Override
            public void onMessageResponse(Message message) {
                if (message.type == Message.MessageType.Emoji) {
                    message.message = convertIosToAndroid(message.message);
                }
                messages.add(message);
                chatAdapter.notifyDataSetChanged();
                scrollMessageListDown();

                if (dialog.type == Dialog.DialogType.Individual) {
                    String currentUserDialogName = FirebaseManager.getInstance().getIndividualDialogID(dialog.occupantsIds.get(0));
                    FirebaseManager.getInstance().getCurrentUsersDialogsRef().child(currentUserDialogName).child("lastSeenDate").setValue(message.dateSent);
                } else {
                    FirebaseManager.getInstance().getCurrentUsersDialogsRef().child(dialog.dialogID).child("lastSeenDate").setValue(message.dateSent);
                }
            }
        });
    }

    /**
     * Below method is used for converting string from Android to IOS.
     *
     * @param emojiName
     * @return
     */

    String convertAndroidToIos(String emojiName) {

        String temp[] = emojiName.split("_");
        String iosEmojiName = temp[1] + "_" + temp[2] + "_" + temp[3] + " ";
        String mainString = "group.com.dev.wangri.muslimojis" + iosEmojiName + "(" + temp[4] + ").png";

        return mainString;
    }

    /**
     * Below method is used for converting string from IOS to Android.
     *
     * @param emojiName
     * @return
     */

    String convertIosToAndroid(String emojiName) {

        String s1 = emojiName.substring(31, 40);
        char result1 = emojiName.charAt(42);

        //String mainString = "image-share_"+s1+"_"+result1;
        String mainString = "share_" + s1 + "_" + result1;


        return mainString;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mAdView != null) {
            mAdView.destroy();
        }

        activityInstance = null;
        FirebaseManager.getInstance().removeMessageListener();
    }

    private static class EmoticonHandler implements TextWatcher {

        private final EditText mEditor;
        private final Context mContext;
        private final ArrayList<ImageSpan> mEmoticonsToRemove = new ArrayList<ImageSpan>();
        private MultiCallback multiCallback = new MultiCallback(true);

        public EmoticonHandler(Context context, EditText editor) {
            // Attach the handler to listen for text changes.
            mContext = context;
            mEditor = editor;
            mEditor.addTextChangedListener(this);
        }

        public void insert(String emoticon, int resource, int start) {
            Log.e("Insert String:", emoticon + ", " + String.valueOf(resource));
            // Create the ImageSpan
            mEditor.removeTextChangedListener(this);
            GifDrawable gifFromResource = null;
            try {
                gifFromResource = new GifDrawable(mContext.getResources(), resource);
                gifFromResource.setBounds(0, 0, 40, 40);

                gifFromResource.setCallback(multiCallback);
                multiCallback.addView(mEditor);

                ImageSpan span = new ImageSpan(gifFromResource, ImageSpan.ALIGN_BASELINE);

                Editable message = mEditor.getEditableText();
                // Insert the emoticon.
                message.replace(start, start + emoticon.length(), emoticon);
                message.setSpan(span, start, start + emoticon.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            } catch (IOException e) {
                e.printStackTrace();
            }

            Drawable imgDrawable;
            if (gifFromResource == null) {
                imgDrawable = mEditor.getResources().getDrawable(resource);
                imgDrawable.setBounds(0, 0, 40, 40);
                ImageSpan span = new ImageSpan(imgDrawable, ImageSpan.ALIGN_BASELINE);
                Editable message = mEditor.getEditableText();

                // Insert the emoticon.
                message.replace(start, start + emoticon.length(), emoticon);
                message.setSpan(span, start, start + emoticon.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            mEditor.addTextChangedListener(this);
        }

        /*

        private EmoticonHandler mEmoticonHandler;
         */

        @Override
        public void beforeTextChanged(CharSequence text, int start, int count, int after) {
            // Check if some text will be removed.
            if (count > 0) {
                int end = start + count;
                Editable message = mEditor.getEditableText();
                ImageSpan[] list = message.getSpans(start, end, ImageSpan.class);

                for (ImageSpan span : list) {
                    // Get only the emoticons that are inside of the changed
                    // region.
                    int spanStart = message.getSpanStart(span);
                    int spanEnd = message.getSpanEnd(span);
                    if ((spanStart < end) && (spanEnd > start)) {
                        // Add to remove list
                        mEmoticonsToRemove.add(span);
                    }
                }
            }
        }

        @Override
        public void afterTextChanged(Editable text) {
            Editable message = mEditor.getEditableText();

            // Commit the emoticons to be removed.
            for (ImageSpan span : mEmoticonsToRemove) {
                int start = message.getSpanStart(span);
                int end = message.getSpanEnd(span);

                // Remove the span
                message.removeSpan(span);

                // Remove the remaining emoticon text.
                if (start != end) {
                    message.delete(start, end);
                }
            }
            mEmoticonsToRemove.clear();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            /*      emoji_0_0_22   */
            if (count >= 13) {
                String changedStr = s.toString().substring(start, start + count);
                Log.e("changedStr:=", changedStr);
                if ((changedStr.charAt(0) == '(') && (changedStr.charAt(changedStr.length() - 1) == ')') && (changedStr.contains("emoji_"))) {
                    String split[] = changedStr.split("_");
                    if (split.length == 4) {
                        String resName = changedStr.substring(1, changedStr.length() - 1);
                        if ((!split[1].equalsIgnoreCase("0")) && (!split[1].equalsIgnoreCase("1"))) {
                            resName = changedStr.substring(1, changedStr.length() - 1).replace("emoji_", "keyboard_");
                        }
                        int emojiRes = mContext.getResources().getIdentifier(resName, "drawable", mContext.getPackageName());
                        Log.e("Insert Call With (" + "(" + changedStr + "), ", String.valueOf(emojiRes));
                        insert(changedStr, emojiRes, start);
                    }
                }
            }
        }
    }
}