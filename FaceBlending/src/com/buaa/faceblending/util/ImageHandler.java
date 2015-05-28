package com.buaa.faceblending.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * ͼƬ����
 * 
 * @author maylian7700@126.com
 * 
 */
public class ImageHandler {

	/**
	 * ͼƬ��ת
	 * 
	 * @param bmp
	 *            Ҫ��ת��ͼƬ
	 * @param degree
	 *            ͼƬ��ת�ĽǶȣ���ֵΪ��ʱ����ת����ֵΪ˳ʱ����ת
	 * @return
	 */
	public static Bitmap rotateBitmap(Bitmap bmp, float degree) {
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
	}

	/**
	 * ͼƬ����
	 * 
	 * @param bm
	 * @param scale
	 *            ֵС����Ϊ��С������Ϊ�Ŵ�
	 * @return
	 */
	public static Bitmap resizeBitmap(Bitmap bm, float scale) {
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
	}

	/**
	 * ͼƬ����
	 * 
	 * @param bm
	 * @param w
	 *            ��С��Ŵ�ɵĿ�
	 * @param h
	 *            ��С��Ŵ�ɵĸ�
	 * @return
	 */
	public static Bitmap resizeBitmap(Bitmap bm, int w, int h) {
		Bitmap BitmapOrg = bm;

		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();

		float scaleWidth = ((float) w) / width;
		float scaleHeight = ((float) h) / height;

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		return Bitmap.createBitmap(BitmapOrg, 0, 0, width, height, matrix, true);
	}

	/**
	 * ͼƬ��ת
	 * 
	 * @param bm
	 * @param flag
	 *            0Ϊˮƽ��ת��1Ϊ��ֱ��ת
	 * @return
	 */
	public static Bitmap reverseBitmap(Bitmap bmp, int flag) {
		float[] floats = null;
		switch (flag) {
		case 0: // ˮƽ��ת
			floats = new float[] { -1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f };
			break;
		case 1: // ��ֱ��ת
			floats = new float[] { 1f, 0f, 0f, 0f, -1f, 0f, 0f, 0f, 1f };
			break;
		}

		if (floats != null) {
			Matrix matrix = new Matrix();
			matrix.setValues(floats);
			return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
		}

		return null;
	}

}
