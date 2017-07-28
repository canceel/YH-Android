package com.intfocus.yonghuitest.subject.template_v2.utils;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zbaoliang on 17-4-28.
 */
public class FileUtil {

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
}
