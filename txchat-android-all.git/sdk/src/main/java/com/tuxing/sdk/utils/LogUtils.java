package com.tuxing.sdk.utils;

import de.mindpipe.android.logging.log4j.LogConfigurator;
import org.apache.log4j.Level;

import java.io.File;

/**
 * Created by Alan on 2015/6/5.
 */
public class LogUtils {
    public static void configure() {
        try {
            final LogConfigurator logConfigurator = new LogConfigurator();

            File logRootDir = new File(Constants.APP_ROOT_DIR, "logs");
            if (!logRootDir.exists()) {
                logRootDir.mkdirs();
            }

            logConfigurator.setFileName(logRootDir.getAbsolutePath() + File.separator + "tuxing.log");
            logConfigurator.setRootLevel(Level.DEBUG);
            logConfigurator.setMaxFileSize(1 * 1024 * 1024);
            logConfigurator.setMaxBackupSize(3);
            logConfigurator.configure();
        }catch (Exception e){
        }
    }
}
