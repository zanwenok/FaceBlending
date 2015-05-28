package com.buaa.faceblending.util;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Bitmap Drawable byte[] ����֮��ת��
 * @author maylian7700@126.com
 *
 */
public class ImageSwitch
{

	/**
	 * byte����ת����Bitmap
	 * @param bmp
	 * @return
	 */
	public static byte[] bitmap2Byte(Bitmap bmp)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}
	
	/**
	 * ����ת����Bitmap
	 * @param buffer
	 * @return
	 */
	public static Bitmap byte2Bitmap(byte[] buffer)
	{
		return BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
	}
	
	/**
	 * Bitmapת����Drawable
	 * @param bmp
	 * @return
	 */
	public static Drawable bitmap2Drawable(Bitmap bmp)
	{
		return new BitmapDrawable(bmp);
	}
	
	/**
	 * BitmapDrawableת����Bitmap
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawable2Bitmap(BitmapDrawable drawable)
	{
		return drawable.getBitmap();
	}
	
}
