package com.tuxing.app.util;

import android.content.Context;
import android.text.TextUtils;
import com.tuxing.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理文字显示的类
 * 
 * 
 */

public class LevelUtils {



	public static List<Integer> getLevel(Context mContext,String levelNum){
		List<Integer> levelList = null;
		int xing = 0;
		int moon = 0;
		int sun = 0;
		if(!TextUtils.isEmpty(levelNum)) {
			levelList = new ArrayList<Integer>();
			levelList.clear();
			int level = Integer.valueOf(levelNum);

			sun = level/9;
			if(sun > 0){
				for(int i = 0; i < sun; i++){
					levelList.add(R.drawable.qzq_xing_sun);
				}
			}

			moon = level%9/3;
			if (moon > 0) {
				for (int i = 0; i < moon; i++) {
					levelList.add(R.drawable.qzq_xing_yue);
				}
			}

			xing = level % 9 % 3;
			if (xing > 0) {
				for (int i = 0; i < xing; i++) {
					levelList.add(R.drawable.qzq_xing_all);
				}
			}
		}

		return levelList;
	}

}
