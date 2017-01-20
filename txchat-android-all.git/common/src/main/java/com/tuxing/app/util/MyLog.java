package com.tuxing.app.util;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;

public class MyLog extends Logger {

	private static final String APP_TAG = "tuxing";
	private static final String LOG_FILE_NAME = "tuxing.log";
	private static PrintStream logStream;
	private static final String LOG_ENTRY_FORMAT = "[%tF %tT]%s";
	
	public static File logFile;
	
	public static String logDir="/sdcard";

	public MyLog(String name) {
		super(name);
	}

	@Override
	protected void debug(String str) {
		android.util.Log.d(APP_TAG, str);
		write(str, null);
	}

	@Override
	protected void error(String str) {
		android.util.Log.e(APP_TAG, str);
		write(str, null);
	}

	@Override
	protected void info(String str) {
		android.util.Log.i(APP_TAG, str);
		write(str, null);
	}

	@Override
	protected void warn(String str) {
		android.util.Log.w(APP_TAG, str);
		write(str, null);
	}

	@Override
	protected void debug(String str, Throwable tr) {
		android.util.Log.d(APP_TAG, str);
		write(str, tr);
	}

	@Override
	protected void error(String str, Throwable tr) {
		android.util.Log.e(APP_TAG, str);
		write(str, tr);
	}

	@Override
	protected void info(String str, Throwable tr) {
		android.util.Log.i(APP_TAG, str);
		write(str, tr);
	}

	@Override
	protected void warn(String str, Throwable tr) {
		android.util.Log.w(APP_TAG, str);
		write(str, tr);
	}

	private void write(String msg, Throwable tr) {
		if (!MyLog.DBG) {
			return;
		}
		try {

			if (null == logStream) {
				synchronized (MyLog.class) {
					if (null == logStream) {
						init();
					}
				}
			}

			Date now = new Date();
			if (null != logStream) {
				logStream.printf(LOG_ENTRY_FORMAT, now, now, msg);
				logStream.print("\n");
			}
			if (null != tr) {
				tr.printStackTrace(logStream);
				if (null != logStream) {
					logStream.print("\n");
				}
			}

		} catch (Throwable t) {
			// Empty catch block
		}
	}

	public static void init() {
		if (!MyLog.DBG) {
			return;
		}
		try {
			File sdRoot = null;
			String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state)) {
				sdRoot = Environment.getExternalStorageDirectory();
			}else{
				sdRoot = Environment.getDataDirectory();
			}
			if (sdRoot != null) {
				android.util.Log.d(APP_TAG, "XLog to path : " + sdRoot.getAbsolutePath());
				logDir = sdRoot.getAbsolutePath();
				logFile = new File(SysConstants.DATA_DIR_ROOT + "/logs", LOG_FILE_NAME);
				logStream = new PrintStream(new FileOutputStream(logFile, true), true);
				
			}
		} catch (Throwable e) {
			// Empty catch block
		}
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			super.finalize();
			if (logStream != null) {
				logStream.close();
				logStream=null;
			}
		} catch (Throwable t) {
			// Empty catch block
		}
	}
	
	public static  void close() throws Throwable {
		try {
			
			if (logStream != null) {
				logStream.close();
				logStream=null;
			}
		} catch (Throwable t) {
			// Empty catch block
		}
	}


	public static void truncate() {
		if(logFile!=null &&logFile.exists()){			
			logFile.delete();
//			try {
//				logStream = new PrintStream(new FileOutputStream(logFile, true), true);
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			logStream = null;
		}		
	}
}
