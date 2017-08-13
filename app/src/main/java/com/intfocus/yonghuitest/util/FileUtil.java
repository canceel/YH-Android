package com.intfocus.yonghuitest.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.intfocus.yonghuitest.base.BaseActivity;
import com.intfocus.yonghuitest.subject.SubjectActivity;
import com.intfocus.yonghuitest.subject.selecttree.SelectItems;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import static com.intfocus.yonghuitest.util.K.kUserId;

public class FileUtil {
    public static String basePath(Context context) {
        String basePath = URLs.storage_base(context);
        FileUtil.makeSureFolderExist(basePath);

        return basePath;
    }

    /*
     * 判断用户是否处于登录状态并且设置了锁屏
     */
    public static boolean checkIsLocked(Context context) {
        try {
            String userConfigPath = String.format("%s/%s", FileUtil.basePath(context), K.kUserConfigFileName);
            if ((new File(userConfigPath)).exists()) {
                JSONObject userJSON = FileUtil.readConfigFile(userConfigPath);
                if (!userJSON.has(URLs.kUseGesturePassword)) {
                    userJSON.put(URLs.kUseGesturePassword, false);
                    Log.i("ScreenLock", "use_gesture_password not set");
                }
                if (!userJSON.has(URLs.kGesturePassword)) {
                    userJSON.put(URLs.kGesturePassword, "");
                    Log.i("ScreenLock", "gesture_password not set");
                }
                if (!userJSON.has(URLs.kIsLogin)) {
                    userJSON.put(URLs.kIsLogin, false);
                    Log.i("ScreenLock", "is_login not set");
                }

                FileUtil.writeFile(userConfigPath, userJSON.toString());

                return userJSON.getBoolean(URLs.kIsLogin) && userJSON.getBoolean(URLs.kUseGesturePassword) && !userJSON.getString(URLs.kGesturePassword).isEmpty();
            } else {
                Log.i("ScreenLock", "userConfigPath not exist");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static String userspace(Context context) {
        SharedPreferences mUserSP = context.getSharedPreferences("UserBean", Context.MODE_PRIVATE);
        String spacePath = "";

        spacePath = String.format("%s/User-%s", FileUtil.basePath(context), mUserSP.getString(kUserId, "0"));
        return spacePath;
    }

    /**
     * 传递目录名取得沙盒中的绝对路径(一级),不存在则创建，请慎用！
     *
     * @param dirName 目录名称，不存在则创建
     * @return 沙盒中的绝对路径
     */
    public static String dirPath(Context context, String dirName) {
        String pathName = String.format("%s/%s", FileUtil.userspace(context), dirName);
        FileUtil.makeSureFolderExist(pathName);

        return pathName;
    }

    public static String dirPath(Context context, String dirName, String fileName) {
        String pathName = FileUtil.dirPath(context, dirName);

        return String.format("%s/%s", pathName, fileName);
    }

    public static String dirsPath(Context context, String[] dirNames) {

        return FileUtil.dirPath(context, TextUtils.join("/", dirNames));
    }

    /*
     * 读取本地文件内容
     */
    public static String readFile(String pathName) {
        String string = null;
        File file = new File(pathName);
        if (file.exists()) {
            try {
                InputStream inputStream = new FileInputStream(new File(pathName));
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line;
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                bufferedReader.close();
                inputStreamReader.close();
                string = stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            string = "";
        }

        return string;
    }

    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                fs.close();
                inStream.close();
            }
            Log.i("response", "file is copy");
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }
    }

    /*
     * 读取本地文件内容，并转化为json
     */
    public static JSONObject readConfigFile(String jsonPath) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (new File(jsonPath).exists()) {
                String string = FileUtil.readFile(jsonPath);
                jsonObject = new JSONObject(string);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /*
     * 字符串写入本地文件
     */
    public static void writeFile(String pathName, String content) throws IOException {
        File file = new File(pathName);
        if (file.exists()) {
            file.delete();
        }

        file.createNewFile();
        FileOutputStream out = new FileOutputStream(file, true);
        out.write(content.getBytes("utf-8"));
        out.close();
    }

    /*
     *  共享资源
     *  1. assets资源
     *  2. loading页面
     *  3. 登录缓存页面
     */
    public static String sharedPath(Context context) {
        String pathName = FileUtil.basePath(context) + "/" + K.kSharedDirName;
        FileUtil.makeSureFolderExist(pathName);

        return pathName;
    }

    public static boolean makeSureFolderExist(String pathName) {
        File folder = new File(pathName);
        return folder.exists() && folder.isDirectory() || folder.mkdirs();
    }

    /*
     * 共享资源中的文件（夹）（忽略是否存在）
     */
    public static String sharedPath(Context context, String folderName) {
        if (!folderName.startsWith("/")) {
            folderName = String.format("/%s", folderName);
        }

        return String.format("%s%s", FileUtil.sharedPath(context), folderName);
    }

    /*
     * Generage MD5 value for ZIP file
     */
    private static String convertByteArrayToHexString(byte[] arrayBytes) {
        StringBuilder stringBuffer = new StringBuilder();
        for (byte bytes : arrayBytes) {
            stringBuffer.append(Integer.toString((bytes & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return stringBuffer.toString();
    }

    /*
     * algorithm can be "MD5", "SHA-1", "SHA-256"
     */
    private static String hashFile(File file) {
        try {
            FileInputStream inputStream = new FileInputStream(file);
            MessageDigest digest = MessageDigest.getInstance("MD5");

            byte[] bytesBuffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(bytesBuffer)) != -1) {
                digest.update(bytesBuffer, 0, bytesRead);
            }

            byte[] hashedBytes = digest.digest();
            inputStream.close();
            return convertByteArrayToHexString(hashedBytes);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "hashFile - exception catched";
    }

    public static String MD5(File file) {
        return hashFile(file);
    }

    /**
     * 解压assets的zip压缩文件到指定目录
     *
     * @throws IOException
     */
    public static void unZip(InputStream inputStream, String outputDirectory, boolean isReWrite) throws IOException {
        // 创建解压目标目录
        File file = new File(outputDirectory);
        // 如果目标目录不存在，则创建
        if (!file.exists()) {
            file.mkdirs();
        }
        // 打开压缩文件
        //InputStream inputStream = getApplicationContext().getAssets().open(assetName);
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        // 读取一个进入点
        ZipEntry zipEntry = zipInputStream.getNextEntry();
        Log.i("response", outputDirectory + File.separator + zipEntry.getName());
        // 使用1Mbuffer
        byte[] buffer = new byte[10 * 1024 * 1024];
        // 解压时字节计数
        int count;
        // 如果进入点为空说明已经遍历完所有压缩包中文件和目录
        while (zipEntry != null) {
            // 如果是一个目录
            if (zipEntry.isDirectory()) {
                file = new File(outputDirectory + File.separator + zipEntry.getName());
                // 文件需要覆盖或者是文件不存在
                if (isReWrite || !file.exists()) {
                    file.mkdir();
                }
            } else {
                // 如果是文件
                file = new File(outputDirectory + File.separator + zipEntry.getName());
                // 文件需要覆盖或者文件不存在，则解压文件
                if (isReWrite || !file.exists()) {
                    file.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    while ((count = zipInputStream.read(buffer)) > 0) {
                        fileOutputStream.write(buffer, 0, count);
                    }
                    fileOutputStream.close();
                }
            }
            // 定位到下一个文件入口
            zipEntry = zipInputStream.getNextEntry();
        }
        zipInputStream.close();
    }

    private static String MD5(InputStream inputStream) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");

            byte[] bytesBuffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(bytesBuffer)) != -1) {
                digest.update(bytesBuffer, 0, bytesRead);
            }

            byte[] hashedBytes = digest.digest();
            return convertByteArrayToHexString(hashedBytes);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return "MD5 - exception catched";
    }

    /**
     * 检测sharedPath/{assets.zip, loading.zip} md5值与缓存文件中是否相等
     *
     * @param mContext 上下文
     * @param fileName 静态文件名称
     */
    public static void checkAssets(Context mContext, String fileName, boolean isInAssets) {
        try {
            String sharedPath = FileUtil.sharedPath(mContext);
            String zipFileName = String.format("%s.zip", fileName);

            // InputStream zipStream = mContext.getApplicationContext().getAssets().open(zipName);
            String zipFilePath = String.format("%s/%s", sharedPath, zipFileName);
            String zipFolderPath = String.format("%s/%s", sharedPath, fileName);
            if (!(new File(zipFilePath)).exists()) {
                FileUtil.copyAssetFile(mContext, zipFileName, zipFilePath);
            }

            InputStream zipStream = new FileInputStream(zipFilePath);
            String md5String = FileUtil.MD5(zipStream);
            String keyName = String.format("local_%s_md5", fileName);

            String userConfigPath = String.format("%s/%s", FileUtil.basePath(mContext), K.kUserConfigFileName);
            boolean isShouldUnZip = true;
            JSONObject userJSON = new JSONObject();
            if ((new File(userConfigPath)).exists()) {
                userJSON = FileUtil.readConfigFile(userConfigPath);
                isShouldUnZip = !(userJSON.has(keyName) && userJSON.getString(keyName).equals(md5String));
            }

            if (isShouldUnZip) {
                Log.i("checkAssets", String.format("%s[%s] != %s", zipFileName, keyName, md5String));

                String folderPath = sharedPath;
                if (isInAssets) {
                    if (fileName.equals("icons")) {
                        fileName = "images";
                    }
                    folderPath = String.format("%s/assets/%s/", sharedPath, fileName);
                } else {
                    File file = new File(zipFolderPath);
                    if (file.exists()) {
                        FileUtils.deleteDirectory(file);
                    }
                }

                // zipStream = mContext.getApplicationContext().getAssets().open(zipName);
                zipStream = new FileInputStream(zipFilePath);
                FileUtil.unZip(zipStream, folderPath, true);
                Log.i("unZip", String.format("%s, %s", zipFileName, md5String));

                userJSON.put(keyName, md5String);
                FileUtil.writeFile(userConfigPath, userJSON.toString());
            }

            zipStream.close();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 拷贝程序自带的文件至指定文件
     *
     * @param assetName  程序自带的文件名称
     * @param outputPath 拷贝到指定文件的路径
     */
    public static void copyAssetFile(Context mContext, String assetName, String outputPath) {
        try {
            InputStream in = mContext.getApplicationContext().getAssets().open(assetName);
            FileOutputStream out = new FileOutputStream(outputPath);
            byte[] buffer = new byte[1024];
            int readPos;
            while ((readPos = in.read(buffer)) != -1) {
                out.write(buffer, 0, readPos);
            }
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取 assets 文件内容
     */
    public static String assetsFileContent(Context mContext, String assetName) {
        String content = "";
        try {
            InputStream in = mContext.getApplicationContext().getAssets().open(assetName);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            int count = -1;
            while ((count = in.read(data, 0, 1024)) != -1) {
                outStream.write(data, 0, count);
            }

            data = null;
            content = new String(outStream.toByteArray(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }

    /**
     * 更新服务器商品条形码信息
     *
     * @param responseString 服务器响应内容
     * @return
     */
    public static void barCodeScanResult(Context mContext, String responseString) {
        try {
            String javascriptPath = FileUtil.sharedPath(mContext) + "/BarCodeScan/assets/javascripts/bar_code_data.js";
            String javascriptContent = new StringBuilder()
                    .append("(function() {")
                    .append("  window.BarCodeData = " + responseString)
                    .append("}).call(this);")
                    .toString();

            Log.i("javascriptContent", javascriptContent);
            FileUtil.writeFile(javascriptPath, javascriptContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 内部报表是否支持筛选功能
     *
     * @param groupID    群组ID
     * @param templateID 模板ID
     * @param reportID   报表ID
     * @return 是否支持筛选功能
     */
    public static boolean reportIsSupportSearch(Context context, String groupID, String templateID, String reportID) {
        SelectItems items = reportSearchItems(context, groupID, templateID, reportID);
        if (items == null) {
            return false;
        }

        if (items != null) {
            if (items.getMax_deep() == 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * 内部报表 JavaScript 文件路径
     *
     * @param groupID    群组ID
     * @param templateID 模板ID
     * @param reportID   报表ID
     * @return 文件路径
     */
    public static String reportJavaScriptDataPath(Context context, String groupID, String templateID, String reportID) {
        String assetsPath = FileUtil.sharedPath(context);
        String fileName = String.format(K.kReportDataFileName, groupID, templateID, reportID);
        return String.format("%s/assets/javascripts/%s", assetsPath, fileName);
    }


    /**
     * 内部报表筛选文件路径
     *
     * @param groupID    群组ID
     * @param templateID 模板ID
     * @param reportID   报表ID
     * @return 文件路径
     */
    public static String reportSelectDataPath(Context context, String groupID, String templateID, String reportID) {
        String assetsPath = FileUtil.sharedPath(context);
        String fileName = String.format(K.kReportDataFileName, groupID, templateID, reportID);
        return String.format("%s/assets/javascripts/%s", assetsPath, fileName);
    }

    /**
     * 内部报表具有筛选功能时，选项列表
     *
     * @param groupID    群组ID
     * @param templateID 模板ID
     * @param reportID   报表ID
     * @return 选项列表
     */
    public static SelectItems reportSearchItems(Context context, String groupID, String templateID, String reportID) {
        String searchItemsPath = String.format("%s.search_items", FileUtil.reportJavaScriptDataPath(context, groupID, templateID, reportID));
        String itemsString = FileUtil.readFile(searchItemsPath);
        Gson gson = new Gson();
        return gson.fromJson(itemsString, SelectItems.class);
    }

    /**
     * 内部报表具有筛选功能时，用户选择的选项，默认第一个选项
     *
     * @param groupID    群组ID
     * @param templateID 模板ID
     * @param reportID   报表ID
     * @return 用户选择的选项，默认第一个选项
     */
    public static String reportSelectedItem(Context context, String groupID, String templateID, String reportID) {
        String selectedItem = "";
        String selectedItemPath = String.format("%s.selected_item", FileUtil.reportJavaScriptDataPath(context, groupID, templateID, reportID));
        if (new File(selectedItemPath).exists()) {
            selectedItem = FileUtil.readFile(selectedItemPath);
        }

        return selectedItem;
    }
    //json.put("size", searchItems.size());
    //for(int i = 0, len = searchItems.size(); i < len; i ++) {
    //    json.put(String.format("item%d", i), searchItems.get(i).toString());
    //}
    //
    //FileUtil.writeFile(searchItemsPath, json.toString());

    /*
     * 保存截屏文件
	 *
	 */
    public static File saveImage(String filePath, Bitmap bmp) {
        // 如果有目标文件，删除它
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        // 声明输出流
        FileOutputStream outStream = null;

        try {
            // 获得输出流，写入文件
            outStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 30, outStream);
            outStream.close();
            return file;
        } catch (IOException e) {
            Log.e("snapshot", e.toString());
        }
        return null;
    }

    /*
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    //以下代码，原本uri返回的是file:///...，由于android4.4返回的是content:///... 需要转化格式
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getBitmapUrlPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT;

        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static class CacheCleanAsync extends AsyncTask<String, Integer, String> {
        Context ctx;
        String type;

        public CacheCleanAsync(Context ctx, String type) {
            this.ctx = ctx;
            this.type = type;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            if (type.equals("new-install")) {
                CacheCleanManager.cleanFiles(ctx);
            }
            else {
                String sharedPath = FileUtil.sharedPath(ctx);
                String userSpace = FileUtil.userspace(ctx);
                CacheCleanManager.cleanCustomCache(sharedPath);
                CacheCleanManager.cleanCustomCache(userSpace);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            String sharedPath = FileUtil.sharedPath(ctx);

            /*
             *  基本目录结构
             */
            makeSureFolder(ctx, K.kSharedDirName);
            makeSureFolder(ctx, K.kCachedDirName);

            /*
             *  新安装、或升级后，把代码包中的静态资源重新拷贝覆盖一下
             *  避免再从服务器下载更新，浪费用户流量
             */
            copyAssetFiles(ctx, sharedPath);

            /*
             *  校正静态资源
             *
             *  sharedPath/filename.zip md5 值 <=> user.plist 中 filename_md5
             *  不一致时，则删除原解压后文件夹，重新解压 zip
             */
            FileUtil.checkAssets(ctx, URLs.kAssets, false);
            FileUtil.checkAssets(ctx, URLs.kLoading, false);
            FileUtil.checkAssets(ctx, URLs.kFonts, true);
            FileUtil.checkAssets(ctx, URLs.kImages, true);
            FileUtil.checkAssets(ctx, URLs.kIcons, true);
            FileUtil.checkAssets(ctx, URLs.kStylesheets, true);
            FileUtil.checkAssets(ctx, URLs.kJavaScripts, true);
            FileUtil.checkAssets(ctx, URLs.kBarCodeScan, false);
            // FileUtil.checkAssets(mContext, URLs.kAdvertisement, false);
        }
    }

    /**
     * 新安装、或升级后，把代码包中的静态资源重新拷贝覆盖一下
     * 避免再从服务器下载更新，浪费用户流量
     */
    public static void copyAssetFiles(Context ctx, String sharedPath) {
        String assetZipPath;
        File assetZipFile;
        String[] assetsName = {URLs.kAssets, URLs.kLoading, URLs.kFonts, URLs.kImages, URLs.kIcons, URLs.kStylesheets, URLs.kJavaScripts, URLs.kBarCodeScan}; // ,URLs.kAdvertisement

        for (String string : assetsName) {
            assetZipPath = String.format("%s/%s.zip", sharedPath, string);
            assetZipFile = new File(assetZipPath);
            if (!assetZipFile.exists()) {
                assetZipFile.delete();
            }
            FileUtil.copyAssetFile(ctx, String.format("%s.zip", string), assetZipPath);
        }
    }

    public static void makeSureFolder(Context ctx, String folderName) {
        String cachedPath = String.format("%s/%s", FileUtil.basePath(ctx), folderName);
        FileUtil.makeSureFolderExist(cachedPath);
    }

    /**
     * 读取assets目录中的文件
     * @param ctx
     * @param fileName
     * @return
     */
    public static String readAssetsFile(Context ctx,String fileName){
        try {
            InputStream is = ctx.getAssets().open(fileName);
            int size = is.available();

            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String text = new String(buffer, "UTF-8");
            return text;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * app升级后，清除缓存头文件
     */
    public static void checkVersionUpgrade(Context ctx, String assetsPath, String sharedPath) {
        try {
            SharedPreferences mUserSP = ctx.getSharedPreferences("UserBean", Context.MODE_PRIVATE);
            String versionConfigPath = String.format("%s/%s", assetsPath, K.kCurrentVersionFileName);
            PackageInfo packageInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);

            String localVersion = "new-installer";
            boolean isUpgrade = true;
            if ((new File(versionConfigPath)).exists()) {
                localVersion = FileUtil.readFile(versionConfigPath);
                isUpgrade = !localVersion.equals(packageInfo.versionName);
            }

            if (isUpgrade) {
                LogUtil.d("VersionUpgrade",
                        String.format("%s => %s remove %s/%s", localVersion, packageInfo.versionName,
                                assetsPath, K.kCachedHeaderConfigFileName));

                /*
                 * 用户报表数据js文件存放在公共区域
                 */
                String headerPath = String.format("%s/%s", sharedPath, K.kCachedHeaderConfigFileName);
                File headerFile = new File(headerPath);
                if (headerFile.exists()) {
                    headerFile.delete();
                }

                FileUtil.writeFile(versionConfigPath, packageInfo.versionName);

                // 强制消息配置，重新上传服务器
                String pushConfigPath = String.format("%s/%s", FileUtil.basePath(ctx), K.kPushConfigFileName);
                JSONObject pushJSON = FileUtil.readConfigFile(pushConfigPath);
                pushJSON.put(K.kPushIsValid, false);
                FileUtil.writeFile(pushConfigPath, pushJSON.toString());
            }
        } catch (IOException | PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
