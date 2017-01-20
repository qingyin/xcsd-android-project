package com.yixia.camera.util;

import com.yixia.camera.model.MediaObject;

import java.io.File;

/**
 * Created by wangst on 16-7-20.
 */
public class VideoUtils {

    protected MediaObject mMediaObject;
    protected int mVideoBitrate = 800;

    public VideoUtils(){

    }

    public MediaObject setOutputDirectory(String key, String dirPath) {
        if(StringUtils.isNotEmpty(dirPath)) {
            File f = new File(dirPath);
            if(f != null) {
                if(f.exists()) {
                    if(f.isDirectory()) {
                        FileUtils.deleteDir(f);
                    } else {
                        FileUtils.deleteFile(f);
                    }
                }

                if(f.mkdirs()) {
                    this.mMediaObject = new MediaObject(key, dirPath, this.mVideoBitrate);
                    this.mMediaObject.mediaPath = dirPath + File.separator +  "0.v";
                    this.mMediaObject.audioPath = dirPath + File.separator +  "0.a";
                }
            }
        }
        return this.mMediaObject;
    }
}
