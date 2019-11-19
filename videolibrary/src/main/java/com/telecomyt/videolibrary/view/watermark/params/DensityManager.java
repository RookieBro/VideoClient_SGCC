package com.telecomyt.videolibrary.view.watermark.params;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * 设备管理,用于获取设备属性(在适配中用到)
 * 
 * @author wangxf
 * 
 */
public class DensityManager {

	private float scale = 0.0f;

	private int height;
	private int width;
	private int orientation;// 方向 1:竖屏 0:横屏

	private volatile static DensityManager densityManager;

	public static DensityManager getInstance(Context context) {
		if (context == null)
			return densityManager;
		if (densityManager == null) {
			synchronized (DensityManager.class) {
				if (densityManager == null)
					densityManager = new DensityManager(context);
			}
		} else {
			/*
			 * 如果横竖屏状态改变,应重新new一个对象,用于更好的适配
			 */
			int orientation = context.getResources().getConfiguration().orientation;
			if (orientation != densityManager.orientation) {
				densityManager = null;
				synchronized (DensityManager.class) {
					if (densityManager == null) {
						densityManager = new DensityManager(context);
					}
				}
			}
		}
		return densityManager;
	}

	private DensityManager(Context context) {
		DisplayMetrics displayMetrics = context.getResources()
				.getDisplayMetrics();
		orientation = context.getResources().getConfiguration().orientation;
		scale = displayMetrics.density;
		height = displayMetrics.heightPixels;
		width = displayMetrics.widthPixels;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public float getScale() {
		return scale;
	}

	public boolean isPortrait() {
		return orientation == 1;
	}
}
