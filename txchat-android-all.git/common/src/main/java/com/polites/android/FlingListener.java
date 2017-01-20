/*
 * Copyright (c) 2012 Jason Polites
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.polites.android;

import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;


/**
 * @author Jason Polites
 *
 */
public class FlingListener extends SimpleOnGestureListener {
	
	private float velocityX;
	private float velocityY;
	
	private MotionEvent e1;
	private MotionEvent e2;
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		this.e1 = e1;
		this.e2 = e2;
		this.velocityX = velocityX;
		this.velocityY = velocityY;
		Log.e("FlingListener","onFling");
		return true;
	}

	public float getVelocityX() {
		return velocityX;
	}
	
	public float getVelocityY() {
		return velocityY;
	}

	public MotionEvent getE1() {
		return e1;
	}

	public void setE1(MotionEvent e1) {
		this.e1 = e1;
	}

	public MotionEvent getE2() {
		return e2;
	}

	public void setE2(MotionEvent e2) {
		this.e2 = e2;
	}
}
