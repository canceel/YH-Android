package com.intfocus.yh_android.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import java.io.File;

/**
 * Created by liuruilin on 2017/5/23.
 */

public class ImageUtil {
    /**
     * 打开相机获取拍照结果
     */
    public static Intent launchCamera(Context context) {
        Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        /*
         * 需要调用裁剪图片功能，无法读取内部存储，故使用 SD 卡先存储图片
         */
        if (FileUtil.hasSdcard()) {
            Uri imageUri;
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    imageUri = FileProvider.getUriForFile(context, "com.intfocus.yh_android.fileprovider", new File(Environment.getExternalStorageDirectory(),"icon.jpg"));
                    intentFromCapture.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intentFromCapture.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }else {
                    imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"icon.jpg"));
            }
            return intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        }
        else {
            return null;
        }
    }

    /**
     * 调用系统的裁剪
     */
    public static Intent launchSystemImageCrop(Context context, Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        File tempFile = new File(Environment.getExternalStorageDirectory(),"icon.jpg");
        Uri outPutUri = Uri.fromFile(tempFile);
        if (android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.KITKAT) {
            String url=FileUtil.getBitmapUrlPath(context, uri);
            intent.setDataAndType(Uri.fromFile(new File(url)), "image/*");
        }else{
            intent.setDataAndType(uri, "image/*");
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data",true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutUri);
        return intent;
    }

    /**
     * 打开系统相册, 获取相册图片
     */
    public static Intent getGallery() {
        Intent intentFromGallery = new Intent(Intent.ACTION_PICK,null);
        intentFromGallery.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        return intentFromGallery;
    }

    public static Bitmap takeScreenShot(Activity activity) {
         /*获取windows中最顶层的view*/
        View view = activity.getWindow().getDecorView();

        //允许当前窗口保存缓存信息
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();

        //获取状态栏高度
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top;

        WindowManager windowManager = activity.getWindowManager();

        //获取屏幕宽和高
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;

        //去掉状态栏
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache(), 0, statusBarHeight, width,
                height-statusBarHeight);

        //销毁缓存信息
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(false);

        return bitmap;
    }


}
