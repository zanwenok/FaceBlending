package com.buaa.faceblending.util;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;

import com.buaa.faceblending.R;
import com.buaa.faceblending.view.CropImageView;
import com.buaa.faceblending.view.ImageMoveView;

public class ImageFrameAdder
{
	public static final int FRAME_BIG = 0x1;
	public static final int FRAME_SMALL = FRAME_BIG + 1;

	private Context mContext;
	private CropImageView mImageView;
	private ImageMoveView mMoveView;
	
	private final int[] mFrame1Res = new int[] { R.drawable.frame_around1_left_top, R.drawable.frame_around1_left, R.drawable.frame_around1_left_bottom, R.drawable.frame_around1_bottom, R.drawable.frame_around1_right_bottom, R.drawable.frame_around1_right, R.drawable.frame_around1_right_top, R.drawable.frame_around1_top };
	private final int[] mFrame2Res = new int[] { R.drawable.frame_around2_left_top, R.drawable.frame_around2_left, R.drawable.frame_around2_left_bottom, R.drawable.frame_around2_bottom, R.drawable.frame_around2_right_bottom, R.drawable.frame_around2_right, R.drawable.frame_around2_right_top, R.drawable.frame_around2_top };
	
	/**
	 * ԴͼƬ
	 */
	private Bitmap mBitmap;
	
	/**
	 * ͿѻͼƬ
	 */
	private Bitmap mWatermark;

	public ImageFrameAdder(Context context, CropImageView view, Bitmap bm)
	{
		mContext = context;
		mImageView = view;
		mBitmap = bm;
	}

	/**
	 * ��ӱ߿�
	 * 
	 * @param flag
	 */
	public Bitmap addFrame(int flag, Bitmap bm, int res)
	{
		Bitmap bmp = null;
		switch (flag)
		{
		case FRAME_BIG:
			bmp = addBigFrame(bm, res);
			break;
		case FRAME_SMALL:
			bmp = addSmallFrame(bm, res);
			break;
		}
		
		return bmp;
	}

	/**
	 * ��ӱ߿�
	 * @param bm ԭͼƬ
	 * @param res �߿���Դ
	 * @return
	 */
	private Bitmap addBigFrame(Bitmap bm, int res)
	{
		Bitmap bitmap = decodeBitmap(res);
		Drawable[] array = new Drawable[2];
		array[0] = new BitmapDrawable(bm);
		Bitmap b = resize(bitmap, bm.getWidth(), bm.getHeight());
		array[1] = new BitmapDrawable(b);
		LayerDrawable layer = new LayerDrawable(array);
		return drawableToBitmap(layer);
	}

	/**
	 * ��Drawableת����Bitmap
	 * @param drawable
	 * @return
	 */
	private Bitmap drawableToBitmap(Drawable drawable)
	{
		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		// canvas.setBitmap(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 * ��R.drawable.*ת����Bitmap
	 * @param res
	 * @return
	 */
	private Bitmap decodeBitmap(int res)
	{
		return BitmapFactory.decodeResource(mContext.getResources(), res);
	}
	
	/**
	 * ͼƬ����
	 * 
	 * @param bm
	 * @param w
	 * @param h
	 * @return
	 */
	public Bitmap resize(Bitmap bm, int w, int h)
	{
		Bitmap BitmapOrg = bm;

		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();
		int newWidth = w;
		int newHeight = h;

		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// if you want to rotate the Bitmap
		// matrix.postRotate(45);

		// recreate the new Bitmap
		Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
				height, matrix, true);
		return resizedBitmap;
	}

	/**
	 * ���С�߿�
	 */
	private Bitmap addSmallFrame(Bitmap bm, int res)
	{
		Bitmap bmp = null;
		
		switch (res) // Ŀǰ�������ֱ߿�
		{
		case 0:
			bmp = combinateFrame(bm, mFrame1Res);
			break;
		case 1:
			bmp = combinateFrame(bm, mFrame2Res);
			break;
		}
		
		return bmp;
	}
	
	/**
	 * ͼƬ��߿����
	 * @param bm ԭͼƬ
	 * @param res �߿���Դ
	 * @return
	 */
	private Bitmap combinateFrame(Bitmap bm, int[] res)
	{
		Bitmap bmp = decodeBitmap(res[0]);
		// �߿�Ŀ��
		final int smallW = bmp.getWidth();
		final int smallH = bmp.getHeight();
		
		// ԭͼƬ�Ŀ��
		final int bigW = bm.getWidth();
		final int bigH = bm.getHeight();
		
		int wCount = (int) Math.ceil(bigW * 1.0 / smallW);
		int hCount = (int) Math.ceil(bigH  * 1.0 / smallH);
		
		// ��Ϻ�ͼƬ�Ŀ��
		int newW = (wCount + 2) * smallW;
		int newH = (hCount + 2) * smallH;
		
		// ���¶����С
		Bitmap newBitmap = Bitmap.createBitmap(newW, newH, Config.ARGB_8888);
		Canvas canvas = new Canvas(newBitmap);
		Paint p = new Paint();
		p.setColor(Color.TRANSPARENT);
		canvas.drawRect(new Rect(0, 0, newW, newH), p);
		
		Rect rect = new Rect(smallW, smallH, newW - smallW, newH - smallH);
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		canvas.drawRect(rect, paint);
		
		// ��ԭͼ
		canvas.drawBitmap(bm, (newW - bigW - 2 * smallW) / 2 + smallW, (newH - bigH - 2 * smallH) / 2 + smallH, null);
		// ��߿�
		// ���ĸ���
		int startW = newW - smallW;
		int startH = newH - smallH;
		Bitmap leftTopBm = decodeBitmap(res[0]); // ���Ͻ�
		Bitmap leftBottomBm = decodeBitmap(res[2]); // ���½�
		Bitmap rightBottomBm = decodeBitmap(res[4]); // ���½�
		Bitmap rightTopBm = decodeBitmap(res[6]); // ���Ͻ�
		
		canvas.drawBitmap(leftTopBm, 0, 0, null);
		canvas.drawBitmap(leftBottomBm, 0, startH, null);
		canvas.drawBitmap(rightBottomBm, startW, startH, null);
		canvas.drawBitmap(rightTopBm, startW, 0, null);
		
		leftTopBm.recycle();
		leftTopBm = null;
		leftBottomBm.recycle();
		leftBottomBm = null;
		rightBottomBm.recycle();
		rightBottomBm = null;
		rightTopBm.recycle();
		rightTopBm = null;
		
		// �����ұ߿�
		Bitmap leftBm = decodeBitmap(res[1]);
		Bitmap rightBm = decodeBitmap(res[5]);
		for (int i = 0, length = hCount; i < length; i++)
		{
			int h = smallH * (i + 1);
			canvas.drawBitmap(leftBm, 0, h, null);
			canvas.drawBitmap(rightBm, startW, h, null);
		}
		
		leftBm.recycle();
		leftBm = null;
		rightBm.recycle();
		rightBm = null;
		
		// �����±߿�
		Bitmap bottomBm = decodeBitmap(res[3]);
		Bitmap topBm = decodeBitmap(res[7]);
		for (int i = 0, length = wCount; i < length; i++)
		{
			int w = smallW * (i + 1);
			canvas.drawBitmap(bottomBm, w, startH, null);
			canvas.drawBitmap(topBm, w, 0, null);
		}
		
		bottomBm.recycle();
		bottomBm = null;
		topBm.recycle();
		topBm = null;
		
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		
		return newBitmap;
	}
	
	/**
	 * ��ȡͼƬ���м��200X200������
	 * @param bm
	 * @return
	 */
	private Bitmap cropCenter(Bitmap bm)
	{
		int dstWidth = 200;
        int dstHeight = 200;
        int startWidth = (bm.getWidth() - dstWidth)/2;
        int startHeight = ((bm.getHeight() - dstHeight) / 2);
        Rect src = new Rect(startWidth, startHeight, startWidth + dstWidth, startHeight + dstHeight);
        return dividePart(bm, src);
	}
	
	/**
	 * ����ͼƬ
	 * @param bmp �����е�ͼƬ
	 * @param src ���е�λ��
	 * @return ���к��ͼƬ
	 */
	private Bitmap dividePart(Bitmap bmp, Rect src)
	{
		int width = src.width();
		int height = src.height();
		Rect des = new Rect(0, 0, width, height);
		Bitmap croppedImage = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(croppedImage);
		canvas.drawBitmap(bmp, src, des, null);
		return croppedImage;
	}

	/**
	 * Ϳѻ
	 */
	public void doodle(int res)
	{
		Bitmap bm = decodeBitmap(res);
		mWatermark = bm;
		
		ImageMoveView moveView = new ImageMoveView(mImageView);
		mMoveView = moveView;
		int[] location = new int[2];
		mImageView.getLocationInWindow(location);
		moveView.setup((mImageView.getWidth() - bm.getWidth()) / 2, (mImageView.getHeight() - bm.getHeight()) / 2, bm);
		mImageView.setMoveView(moveView);
	}
	
	/**
	 * ���ͿѻͼƬ��ԴͼƬ
	 */
	public Bitmap combinate(Bitmap src)
	{
		// ͿѻͼƬ
		Bitmap watermark = mWatermark;
		// ���ⴴ��һ��ͼƬ
		Bitmap newb = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Config.ARGB_8888);// ����һ���µĺ�SRC���ȿ��һ����λͼ
		Canvas canvas = new Canvas(newb);
		canvas.drawBitmap(src, 0, 0, null);// �� 0��0���꿪ʼ����ԭͼƬsrc
//		canvas.drawBitmap(watermark, mMoveView.getLeft(), mMoveView.getTop(), null);// ��src�����½ǻ���ˮӡ
		canvas.drawBitmap(watermark, (src.getWidth() - watermark.getWidth()) / 2, (src.getHeight() - watermark.getHeight()) / 2, null); // ͿѻͼƬ����ԭͼƬ�м�λ��
		canvas.save(Canvas.ALL_SAVE_FLAG);// ����
		canvas.restore();// �洢
		
		watermark.recycle();
		watermark = null;
		mWatermark = null;
//		Log.d("may", "watermark="+mWatermark+", recycle="+mWatermark.isRecycled());
		
		mBitmap = newb;
		mImageView.setState(CropImageView.STATE_NONE);
		return newb;
	}
	
//	/**
//	 * ���ͿѻͼƬ��ԴͼƬ
//	 * @param src ԴͼƬ
//	 * @param watermark ͿѻͼƬ
//	 * @return
//	 */
//	public Bitmap doodle(Bitmap src, Bitmap watermark)
//	{
//		// ���ⴴ��һ��ͼƬ
//		Bitmap newb = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Config.ARGB_8888);// ����һ���µĺ�SRC���ȿ��һ����λͼ
//		Canvas canvas = new Canvas(newb);
//		canvas.drawBitmap(src, 0, 0, null);// �� 0��0���꿪ʼ����ԭͼƬsrc
//		canvas.drawBitmap(watermark, (src.getWidth() - watermark.getWidth()) / 2, (src.getHeight() - watermark.getHeight()) / 2, null); // ͿѻͼƬ����ԭͼƬ�м�λ��
//		canvas.save(Canvas.ALL_SAVE_FLAG);
//		canvas.restore();
//		
//		watermark.recycle();
//		watermark = null;
//		
//		return newb;
//	}
	
	public void cancelCombinate()
	{
		mImageView.setState(CropImageView.STATE_NONE);
		mImageView.invalidate();
	}
	
	/**
	 * ����ͼƬ������
	 * @param bm
	 */
	private void saveBitmap(Bitmap bm)
	{
		// TODO �������
		FileOutputStream fos = null;
		try
		{
			fos = new FileOutputStream("/sdcard/pictures/aa.jpg");
			bm.compress(CompressFormat.JPEG, 75, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Bitmap to byte array
	 * @param bm
	 * @return
	 */
	private byte[] Bitmap2Bytes(Bitmap bm)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}
}
