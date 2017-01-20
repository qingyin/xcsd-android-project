package com.tuxing.app.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.tuxing.app.R;

public class CheckableGridViewItem extends RelativeLayout implements Checkable {
	
	private Context mContext;
	private boolean mChecked;// 判断该选项是否被选上的标志量
	private ImageView mImgView = null;
	private ImageView mSecletView = null;

	public CheckableGridViewItem(Context context) {
		this(context, null, 0);
	}

	public CheckableGridViewItem(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CheckableGridViewItem(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		mContext = context;
		LayoutInflater.from(mContext).inflate(R.layout.select_pics_grid_item, this);
		mImgView = (ImageView) findViewById(R.id.image);
		mSecletView = (ImageView) findViewById(R.id.select);
	}

	@Override
	public void setChecked(boolean checked) {
		// TODO Auto-generated method stub
		mChecked = checked;
		// setBackgroundDrawable(checked ? getResources().getDrawable(
		// R.drawable.background) : null);
		mSecletView.setVisibility(checked ? View.VISIBLE : View.GONE);// 选上了则显示小勾图片
		
//		this.setBackgroundColor(checked ? mContext.getResources().getColor(R.color.tv_red):mContext.getResources().getColor(R.color.grid_background)
//			);
	}

	@Override
	public boolean isChecked() {
		// TODO Auto-generated method stub
		return mChecked;
	}

	@Override
	public void toggle() {
		setChecked(!mChecked);
	}

	
    
    public void setBackgroudColor(int colorId){
    	this.setBackgroundColor(mContext.getResources().getColor(R.color.tv_red));
    }

    
   public void setImageBitmap(Bitmap bitmap){
	   mImgView.setImageBitmap(bitmap);
   }

	public ImageView getImageView() {
		return mImgView;
	}
   
   public void setImageURI(Uri uri){
	   mImgView.setImageURI(uri);
   }
   
   public void setImageResource(int resId){
	   mImgView.setImageResource(resId);
   }
}
