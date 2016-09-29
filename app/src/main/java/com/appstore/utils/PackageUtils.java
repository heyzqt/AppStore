package com.appstore.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

/**
 * Created by 张艳琴 on 2016/9/29.
 */
public class PackageUtils {

    /**
     * 通过包名判断应用是否安装
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isAppInstalled(Context context, String packageName) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }

        if (packageInfo == null) {
            //没有安装该应用
            return false;
        } else {
            return true;
        }
    }

    /**
     * 通过包名启动一个app
     *
     * @param context
     * @param appPackageName
     */
    public static void startApp(Context context, String appPackageName) {
        try {
            Intent intent = context.getPackageManager
                    ().getLaunchIntentForPackage(appPackageName);
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "没有安装该应用", Toast.LENGTH_LONG).show();
        }
    }
}
