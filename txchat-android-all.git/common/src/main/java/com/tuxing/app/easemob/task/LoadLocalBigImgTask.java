/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tuxing.app.easemob.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import com.easemob.util.ImageUtils;
import com.tuxing.app.R;
import com.tuxing.app.easemob.util.ImageCache;
import com.tuxing.app.easemob.widget.photo.PhotoView;
import com.tuxing.app.util.Utils;

import java.io.File;

public class LoadLocalBigImgTask extends AsyncTask<Void, Void, Bitmap> {

	private String path;
	private int width;
	private int height;
	private Context context;
	private LoadlLocalTaskListener listener;

	public LoadLocalBigImgTask(Context context,String path,LoadlLocalTaskListener listener) {
		this.context = context;
		this.path = path;
		this.listener = listener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

	}

	@Override
	protected Bitmap doInBackground(Void... params) {
		Bitmap bitmap;
		long total = Runtime.getRuntime().totalMemory();
		long max = Runtime.getRuntime().maxMemory();
		bitmap = Utils.revitionImageSize(path,Utils.getDisplayWidth(context),Utils.getDisplayHeight(context));

		return bitmap;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		super.onPostExecute(result);
		listener.onFinished(result);

	}

	public interface LoadlLocalTaskListener {
		public void onStartDownload();
		public void onProgress(long current, long total);
		public void onFinished(Bitmap bitmap);
	}
}
