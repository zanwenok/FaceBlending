 package com.buaa.faceblending.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.buaa.faceblending.R;

public class ToneView
{
	/**
	 * ���Ͷ�
	 */
	private TextView mSaturation;
	private SeekBar mSaturationBar;
	
	/**
	 * ɫ��
	 */
	private TextView mHue;
	private SeekBar mHueBar;
	
	/**
	 * ����
	 */
	private TextView mLum;
	private SeekBar mLumBar;
	
	private float mDensity;
	private static final int TEXT_WIDTH = 50;
	
	private LinearLayout mParent;
	
	private ColorMatrix mLightnessMatrix;
	private ColorMatrix mSaturationMatrix;
	private ColorMatrix mHueMatrix;
	private ColorMatrix mAllMatrix;
	
	/**
	 * ����
	 */
	private float mLightnessValue = 1F;
	
	/**
	 * ���Ͷ�
	 */
	private float mSaturationValue = 0F;
	
	/**
	 * ɫ��
	 */
	private float mHueValue = 0F;
	private final int MIDDLE_VALUE = 127;
	
	/**
	 * ������ͼƬ
	 */
	private Bitmap mBitmap;
	
	public ToneView(Context context)
	{
		init(context);
	}
	
	private void init(Context context)
	{
		mDensity = context.getResources().getDisplayMetrics().density;
		
		mSaturation = new TextView(context);
		mSaturation.setText(R.string.saturation);
		mHue = new TextView(context);
		mHue.setText(R.string.contrast);
		mLum = new TextView(context);
		mLum.setText(R.string.lightness);
		
		mSaturationBar = new SeekBar(context);
		mSaturationBar.setMax(255);
		mSaturationBar.setProgress(127);
		mSaturationBar.setTag(1);
		
		mHueBar = new SeekBar(context);
		mHueBar.setMax(255);
		mHueBar.setProgress(127);
		mHueBar.setTag(2);
		
		mLumBar = new SeekBar(context);
		mLumBar.setMax(255);
		mLumBar.setProgress(127);
		mLumBar.setTag(3);
		
		LinearLayout saturation = new LinearLayout(context);
		saturation.setOrientation(LinearLayout.HORIZONTAL);
		saturation.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		
		LinearLayout.LayoutParams txtLayoutparams = new LinearLayout.LayoutParams((int) (TEXT_WIDTH * mDensity), LinearLayout.LayoutParams.MATCH_PARENT);
		mSaturation.setGravity(Gravity.CENTER);
		saturation.addView(mSaturation, txtLayoutparams);
		
		LinearLayout.LayoutParams seekLayoutparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		saturation.addView(mSaturationBar, seekLayoutparams);
		
		
		LinearLayout hue = new LinearLayout(context);
		hue.setOrientation(LinearLayout.HORIZONTAL);
		hue.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		
		mHue.setGravity(Gravity.CENTER);
		hue.addView(mHue, txtLayoutparams);
		
		hue.addView(mHueBar, seekLayoutparams);
		
		
		LinearLayout lum = new LinearLayout(context);
		lum.setOrientation(LinearLayout.HORIZONTAL);
		lum.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		
		mLum.setGravity(Gravity.CENTER);
		lum.addView(mLum, txtLayoutparams);
		lum.addView(mLumBar, seekLayoutparams);
		
		mParent = new LinearLayout(context);
		mParent.setOrientation(LinearLayout.VERTICAL);
		mParent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		mParent.addView(saturation);
		mParent.addView(hue);
		mParent.addView(lum);
	}
	
	public View getParentView()
	{
		return mParent;
	}
	
	public void setSaturationBarListener(OnSeekBarChangeListener l)
	{
		mSaturationBar.setOnSeekBarChangeListener(l);
	}
	
	public void setHueBarListener(OnSeekBarChangeListener l)
	{
		mHueBar.setOnSeekBarChangeListener(l);
	}
	
	public void setLumBarListener(OnSeekBarChangeListener l)
	{
		mLumBar.setOnSeekBarChangeListener(l);
	}
	
	public void setSaturation(int saturation)
	{
		mSaturationValue = (float) (saturation * 1.0D / MIDDLE_VALUE);
	}
	
	public void setHue(int hue)
	{
		mHueValue = (float) (hue * 1.0D / MIDDLE_VALUE);
	}
	
	public void setLum(int lum)
	{
		mLightnessValue = (float) ((lum - MIDDLE_VALUE) * 1.0D / MIDDLE_VALUE * 180);
	}
	
	/**
	 * ���ش�����ͼƬ
	 * @return
	 */
	public Bitmap getBitmap()
	{
		return mBitmap;
	}
	
	/**
	 * 
	 * @param flag
	 *            ����λ0 ��ʾ�Ƿ�ı�ɫ�࣬��λ1��ʾ�Ƿ�ı䱥�Ͷ�,����λ2��ʾ�Ƿ�ı�������
	 */
	public Bitmap handleImage(Bitmap bm, int flag)
	{
		Bitmap bmp = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);
		// ����һ����ͬ�ߴ�Ŀɱ��λͼ��,���ڻ��Ƶ�ɫ���ͼƬ
		Canvas canvas = new Canvas(bmp); // �õ����ʶ���
		Paint paint = new Paint(); // �½�paint
		paint.setAntiAlias(true); // ���ÿ����,Ҳ���Ǳ�Ե��ƽ������
		if (null == mAllMatrix)
		{
			mAllMatrix = new ColorMatrix();
		}
		
		if (null == mLightnessMatrix)
		{
			mLightnessMatrix = new ColorMatrix(); // ������ɫ�任�ľ���androidλͼ��ɫ�仯������Ҫ�ǿ��ö������
		}
		
		if (null == mSaturationMatrix)
		{
			mSaturationMatrix = new ColorMatrix();
		}
		
		if (null == mHueMatrix)
		{
			mHueMatrix = new ColorMatrix();
		}

		switch (flag)
		{
		case 0: // ��Ҫ�ı�ɫ��
			// f ��ʾ���ȱ�����ȡֵС��1����ʾ���ȼ���������������ǿ
			mHueMatrix.reset();
			mHueMatrix.setScale(mHueValue, mHueValue, mHueValue, 1); // �졢�̡�������������ͬ�ı���,���һ������1��ʾ͸���Ȳ����仯���˺�����ϸ˵���ο�
			// // android
			// doc
			Log.d("may", "�ı�ɫ��");
			break;
		case 1: // ��Ҫ�ı䱥�Ͷ�
			// saturation ���Ͷ�ֵ����С����Ϊ0����ʱ��Ӧ���ǻҶ�ͼ(Ҳ�����׻��ġ��ڰ�ͼ��)��
			// Ϊ1��ʾ���ͶȲ��䣬���ô���1������ʾ������
			mSaturationMatrix.reset();
			mSaturationMatrix.setSaturation(mSaturationValue);
			Log.d("may", "�ı䱥�Ͷ�");
			break;
		case 2: // ����
			// hueColor����ɫ����ת�ĽǶ�,��ֵ��ʾ˳ʱ����ת����ֵ��ʾ��ʱ����ת
			mLightnessMatrix.reset(); // ��ΪĬ��ֵ
			mLightnessMatrix.setRotate(0, mLightnessValue); // �����ú�ɫ����ɫ������תhueColor��Ƕ�
			mLightnessMatrix.setRotate(1, mLightnessValue); // �������̺�ɫ����ɫ������תhueColor��Ƕ�
			mLightnessMatrix.setRotate(2, mLightnessValue); // ��������ɫ����ɫ������תhueColor��Ƕ�
			// �����൱�ڸı����ȫͼ��ɫ��
			Log.d("may", "�ı�����");
			break;
		}
		mAllMatrix.reset();
		mAllMatrix.postConcat(mHueMatrix);
		mAllMatrix.postConcat(mSaturationMatrix); // Ч������
		mAllMatrix.postConcat(mLightnessMatrix); // Ч������

		paint.setColorFilter(new ColorMatrixColorFilter(mAllMatrix));// ������ɫ�任Ч��
		canvas.drawBitmap(bm, 0, 0, paint); // ����ɫ�仯���ͼƬ������´�����λͼ��
		// �����µ�λͼ��Ҳ����ɫ������ͼƬ
		mBitmap = bmp;
		return bmp;
	}
	
}
