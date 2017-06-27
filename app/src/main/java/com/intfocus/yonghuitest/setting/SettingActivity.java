package com.intfocus.yonghuitest.setting;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.intfocus.yonghuitest.base.BaseActivity;
import com.intfocus.yonghuitest.login.LoginActivity;
import com.intfocus.yonghuitest.R;
import com.intfocus.yonghuitest.util.HttpUtil;
import com.intfocus.yonghuitest.util.K;
import com.intfocus.yonghuitest.util.WidgetUtil;
import com.intfocus.yonghuitest.view.CircleImageView;

import org.json.JSONException;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.intfocus.yonghuitest.util.ImageUtil.getGallery;
import static com.intfocus.yonghuitest.util.ImageUtil.launchCamera;
import static com.intfocus.yonghuitest.util.ImageUtil.launchSystemImageCrop;

/**
 * Created by liuruilin on 2017/3/28.
 */

public class SettingActivity extends BaseActivity {
    @ViewInject(R.id.user_id)
    TextView tvUserID;
    @ViewInject(R.id.role_id)
    TextView tvRoleID;
    @ViewInject(R.id.group_id)
    TextView tvGroupID;
    @ViewInject(R.id.list_setting)
    ListView mListItem;
    @ViewInject(R.id.img_icon)
    CircleImageView mIconImageView;

    private ArrayAdapter<String> mListAdapter;
    private Context mContext;
    private SharedPreferences mSharedPreferences;

    /* 请求识别码 */
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        x.view().inject(this);
        mContext = this;
        mSharedPreferences = getSharedPreferences("SettingPreference", MODE_PRIVATE);
        initSettingListItem();
        initUserInfo();
        initUserIcon();
    }

    /*
     * 用户信息初始化
     */
    private void initUserInfo() {
        try {
            tvUserID.setText(user.getString("user_name"));
            tvRoleID.setText(user.getString("role_name"));
            tvGroupID.setText(user.getString("group_name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
     * 个人信息页菜单项初始化
     */
    private void initSettingListItem() {
        ArrayList<String> listItem = new ArrayList<>();
        String[] itemName = {"基本信息", "应用信息", "选项配置", "消息推送", "更新日志"};

        for (int i = 0; i < itemName.length; i++) {
            listItem.add(itemName[i]);
        }

        mListAdapter = new ArrayAdapter(this, R.layout.list_item_setting, R.id.item_setting, listItem);

        mListItem.setAdapter(mListAdapter);
        mListItem.setOnItemClickListener(mListItemListener);
    }

    /*
     * 个人信息菜单项点击事件
     */
    private ListView.OnItemClickListener mListItemListener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            TextView mItemText = (TextView) arg1.findViewById(R.id.item_setting);
            switch (mItemText.getText().toString()) {
                case "基本信息":
                    Intent userInfoIntent = new Intent(mContext, SettingListActivity.class);
                    userInfoIntent.putExtra("type", "基本信息");
                    startActivity(userInfoIntent);
                    break;

                case "应用信息":
                    Intent appInfoIntent = new Intent(mContext, SettingListActivity.class);
                    appInfoIntent.putExtra("type", "应用信息");
                    startActivity(appInfoIntent);
                    break;

                case "消息推送":
                    Intent pushIntent = new Intent(mContext, SettingListActivity.class);
                    pushIntent.putExtra("type", "消息推送");
                    startActivity(pushIntent);
                    break;

                case "更新日志":
                    Intent thursdaySayIntent = new Intent(SettingActivity.this, ThursdaySayActivity.class);
                    thursdaySayIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(thursdaySayIntent);
                    break;

                case "选项配置":
                    Intent settingPreferenceIntent = new Intent(mContext, SettingPreferenceActivity.class);
                    startActivity(settingPreferenceIntent);
                    break;
            }
        }
    };

    /*
     * 退出登录
     */
    public void loginOut(View v) {
        // 判断有无网络
        if (!isNetworkConnected(this)) {
            toast("未连接网络, 无法退出");
            return;
        }
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putBoolean("ScreenLock", false);
        mEditor.commit();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String postUrl = String.format(K.kDeleteDeviceIdAPIPath, K.kBaseUrl, user.getString("user_device_id"));
                    final Map<String, String> response = HttpUtil.httpPost(postUrl, new HashMap());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (response.get("code").equals("200")) {
                                modifiedUserConfig(false);
                                Intent intent = new Intent();
                                intent.setClass(SettingActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                toast(response.get("body"));
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initUserIcon() {
        Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/icon.jpg");
        if (bitmap != null) {
            mIconImageView.setImageBitmap(bitmap);
        }
        mIconImageView.setOnClickListener(mIconImageViewListener);
        final View iconMenuView = LayoutInflater.from(this).inflate(R.layout.activity_icon_dialog, null);

        Button btnTakePhoto = (Button) iconMenuView.findViewById(R.id.btn_icon_takephoto);
        Button btnGetPhoto = (Button) iconMenuView.findViewById(R.id.btn_icon_getphoto);
        Button btnCancel = (Button) iconMenuView.findViewById(R.id.btn_icon_cancel);

        popupWindow = new PopupWindow(this);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(iconMenuView);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = launchCamera(mContext);
                startActivityForResult(intent, CODE_CAMERA_REQUEST);
            }
        });

        btnGetPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getGallery();
                startActivityForResult(intent,CODE_GALLERY_REQUEST);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    private final View.OnClickListener mIconImageViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int cameraPermission = ContextCompat.checkSelfPermission(mAppContext, Manifest.permission.CAMERA);
            if(cameraPermission != PackageManager.PERMISSION_GRANTED) {
                setAlertDialog(mContext, "相机权限获取失败，是否到本应用的设置界面设置权限");
            }else{
                popupWindow.showAtLocation(mIconImageView, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // 用户没有选择图片，返回
        if (resultCode == RESULT_CANCELED) {
            WidgetUtil.showToastShort(mContext, "取消");
            return;
        }

        switch (requestCode) {
            case CODE_GALLERY_REQUEST:
                Intent cropIntent = launchSystemImageCrop(mContext, intent.getData());
                startActivityForResult(cropIntent, CODE_RESULT_REQUEST);
                break;
            case CODE_CAMERA_REQUEST:
                File tempFile = new File(Environment.getExternalStorageDirectory(), "icon.jpg");
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.intfocus.yonghuitest.fileprovider",
                            tempFile);
                    cropIntent = launchSystemImageCrop(mContext, photoURI);
                } else {
                    cropIntent = launchSystemImageCrop(mContext, Uri.fromFile(tempFile));
                }
                startActivityForResult(cropIntent, CODE_RESULT_REQUEST);
                break;
            default:
                if (intent != null) {
                    setImageToHeadView(intent);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    /*
     * 提取保存裁剪之后的图片数据，并设置头像部分的View
     */
    private void setImageToHeadView(Intent intent) {
        Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/icon.jpg");
        if (bitmap != null) {
            mIconImageView.setImageBitmap(bitmap);
            popupWindow.dismiss();
        }
    }
}
