package com.tuxing.app.receiveiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Environment;
import com.tuxing.app.util.AppUpdate;
import com.tuxing.app.util.PreferenceUtils;
import com.tuxing.app.util.SysConstants;

import java.io.File;

/**
 * Created by wangst on 15-8-24.
 */
public class DownloadReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        File existFile = null;
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equalsIgnoreCase(intent.getAction())) {
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            long localId = PreferenceUtils.getPrefLong(context, SysConstants.DownloadId, downId);
            if (localId != -1 && downId == localId) {
                PreferenceUtils.setPrefLong(context, SysConstants.DownloadId, -1);
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downId);
                Cursor c = downloadManager.query(query);
                if (c != null && c.moveToFirst()) {
                    int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            existFile = new File(
                                    c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME)));
                        } else {
                            existFile = new File(
                                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                            + "/"
                                            + c.getString(c
                                            .getColumnIndex(DownloadManager.COLUMN_TITLE)));
                        }
                    }
                }

                if(c != null){
                    c.close();
                }
            }

            if(existFile != null && existFile.exists()){
                AppUpdate.setupApk(context, existFile);
            }

        }
    }
}
