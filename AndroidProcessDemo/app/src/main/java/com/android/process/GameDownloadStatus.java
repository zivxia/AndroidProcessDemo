package com.android.process;

import androidx.annotation.IntDef;

/**
 * 游戏下载状态
 *
 * @author zivxia
 */
@IntDef({
        GameDownloadStatus.UNLOAD,
        GameDownloadStatus.DOWNLOADING,
        GameDownloadStatus.PAUSEING,
        GameDownloadStatus.COMPLETED,
        GameDownloadStatus.INSTALLING,
        GameDownloadStatus.INSTALL_COMPLETED,
        GameDownloadStatus.UPGRADE,
})
public @interface GameDownloadStatus {

    /**
     * 未加载
     */
    int UNLOAD = 0;
    /**
     * 下载中
     */
    int DOWNLOADING = 1;
    /**
     * 暂停中
     */
    int PAUSEING = 2;
    /**
     * 下载完成
     */
    int COMPLETED = 3;
    /**
     * 安装中
     */
    int INSTALLING = 4;
    /**
     * 安装完毕
     */
    int INSTALL_COMPLETED = 5;
    /**
     * 更新
     */
    int UPGRADE = 6;

}
