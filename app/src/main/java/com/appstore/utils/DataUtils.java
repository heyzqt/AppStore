package com.appstore.utils;

import com.appstore.entity.AppInfo;
import com.appstore.entity.DownLoadInfo;

/**
 * Created by heyzqt on 2016/9/26.
 */
public class DataUtils {

    /**
     * 把AppInfo对象转换成DownLoadInfo对象
     *
     * @param appInfo
     * @param status
     * @return
     */
    public static DownLoadInfo convertAppInfoToDownloadInfo(AppInfo appInfo,int status){
        DownLoadInfo downLoadInfo = new DownLoadInfo();
        downLoadInfo.setAppId(appInfo.getId()+"");
        downLoadInfo.setStatus(status);
        return downLoadInfo;
    }

}
