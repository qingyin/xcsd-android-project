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

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.util.ImageUtils;
import com.tuxing.app.easemob.util.CommonUtils;
import com.tuxing.app.easemob.util.ImageCache;
import com.tuxing.app.util.Utils;

import java.io.File;

public class LoadImageTask extends AsyncTask<Object, Void, Bitmap> {
	private ImageView iv = null;
	String localFullSizePath = null;
	public LoadImageTask(String localFullSizePath, ImageView iv){
		this.localFullSizePath = localFullSizePath;
		this.iv = iv;
	}
	

	@Override
	protected Bitmap doInBackground(Object... args) {
		File file = new File(localFullSizePath);
		Bitmap bitmap = null;
		if (file.exists()) {
			bitmap = ImageUtils.decodeScaleImage(localFullSizePath, 240, 480);
			if(bitmap != null){
				ImageCache.getInstance().put(localFullSizePath,bitmap);
			}
				return bitmap;
		}
		return bitmap;

	}

	protected void onPostExecute(Bitmap image) {
		if (image != null) {
			iv.setImageBitmap(image);
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	

}
