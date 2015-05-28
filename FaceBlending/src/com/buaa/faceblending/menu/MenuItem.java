package com.buaa.faceblending.menu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * �˵���
 * @author maylian7700@126.com
 *
 */
public class MenuItem
{
	private LinearLayout mLayout;
	
	/**
	 * �ı���ͼƬ���·���ʾ
	 */
	public static final int TEXT_BOTTOM = 0x0;
	
	/**
	 * �ı���ͼƬ���Ϸ���ʾ
	 */
	public static final int TEXT_TOP = 0x1;
	
	/**
	 * �ı���ͼƬ�������ʾ
	 */
	public static final int TEXT_LEFT = 0x2;
	
	/**
	 * �ı���ͼƬ���ұ���ʾ
	 */
	public static final int TEXT_RIGHT = 0x3;
	
	/**
	 * �ı��Ķ��뷽ʽ
	 */
	private int mAlign = TEXT_BOTTOM;
	
	/**
	 * �ı�
	 */
	private String mText;
	
	/**
	 * �ı���ɫ
	 */
	private int mTextColor;
	
	/**
	 * �ı���С
	 */
	private int mTextSize;
	
	/**
	 * ͼƬ����ԴID
	 */
	private int mImgRes;
	private Context mContext;
	
	public MenuItem(Context context)
	{
		this(context, 0, null, 0, 0, TEXT_BOTTOM);
	}
	
	public MenuItem(Context context, int imgRes, String text, int textColor, int textSize, int align)
	{
		mImgRes = imgRes;
		mText = text;
		mTextColor = textColor;
		mTextSize = textSize;
		mAlign = align;
		mContext = context;
	}
	
	public MenuItem(Context context, MenuItem item)
	{
		this(context, 0, null, item.getTextColor(), item.getTextSize(), item.getAlign());
	}
	
	/**
	 * ��ʼ���˵���
	 * @param context
	 */
	private void initlayout()
	{
		Context context = mContext;
		mLayout = new LinearLayout(context);
		mLayout.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		mLayout.setGravity(Gravity.CENTER);
		TextView textView = getTextView(context);
		ImageView imageView = getImageView(context);
		if (null != textView && null != imageView)
		{
			Point point = getImageDimension(context, mImgRes);
			switch (mAlign)
			{
			case TEXT_BOTTOM: // �ı�����
				mLayout.setOrientation(LinearLayout.VERTICAL);
				mLayout.addView(imageView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, point.y));
				mLayout.addView(textView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
				break;
			case TEXT_TOP:// �ı�����
				mLayout.setOrientation(LinearLayout.VERTICAL);
				mLayout.addView(textView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
				mLayout.addView(imageView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, point.y));
				break;
			case TEXT_LEFT:// �ı�����
				mLayout.setOrientation(LinearLayout.HORIZONTAL);
				mLayout.addView(textView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
				mLayout.addView(imageView, new ViewGroup.LayoutParams(point.x, ViewGroup.LayoutParams.MATCH_PARENT));
				break;
			case TEXT_RIGHT:// �ı�����
				mLayout.setOrientation(LinearLayout.HORIZONTAL);
				mLayout.addView(imageView, new ViewGroup.LayoutParams(point.x, ViewGroup.LayoutParams.MATCH_PARENT));
				mLayout.addView(textView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
				break;
			}
		}
		else
		{
			if (null != textView)
			{
				mLayout.addView(textView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
			}
			else if (null != imageView)
			{
				Point point = getImageDimension(context, mImgRes);
				mLayout.addView(imageView, new ViewGroup.LayoutParams(point.x, point.y));
			}
		}
	}
	
	/**
	 * ��ȡͼƬ�ĳߴ�
	 * @param context
	 * @param res
	 * @return
	 */
	private Point getImageDimension(Context context, int res)
	{
		Point point = new Point();
		Bitmap bm = BitmapFactory.decodeResource(context.getResources(), mImgRes);
		point.x = bm.getWidth();
		point.y = bm.getHeight();
		bm.recycle();
		bm = null;
		return point;
	}
	
	/**
	 * ����ͼƬ��Դ
	 * @param imgRes
	 */
	public void setImageRes(int imgRes)
	{
		mImgRes = imgRes;
	}
	
	/**
	 * �����ı���С
	 * @param size
	 */
	public void setTextSize(int size)
	{
		mTextSize = size;
	}
	
	/**
	 * �����ı���ɫ
	 * @param color
	 */
	public void setTextColor(int color)
	{
		mTextColor = color;
	}
	
	/**
	 * �����ı�����
	 * @param text
	 */
	public void setText(String text)
	{
		mText = text;
	}
	
	/**
	 * �����ı��Ķ��뷽ʽ
	 * @param align
	 */
	public void setTextAlign(int align)
	{
		mAlign = align;
	}
	
	public String getText()
	{
		return mText;
	}
	
	public int getTextColor()
	{
		return mTextColor;
	}
	
	public int getTextSize()
	{
		return mTextSize;
	}
	
	/**
	 * ����TextView
	 * @param context
	 * @return
	 */
	private TextView getTextView(Context context)
	{
		if (TextUtils.isEmpty(mText))
		{
			return null;
		}
		TextView txtView = new TextView(context);
		if (mTextColor != 0)
		{
			txtView.setTextColor(mTextColor);
		}
		
		if (mTextSize != 0)
		{
			txtView.setTextSize(mTextSize);
		}
		txtView.setText(mText);
		txtView.setGravity(Gravity.CENTER);
		return txtView;
	}
	
	public int getAlign()
	{
		return mAlign;
	}
	
	/**
	 * ����ImageView
	 * @param context
	 * @return
	 */
	private ImageView getImageView(Context context)
	{
		if (mImgRes == 0)
		{
			return null;
		}
		ImageView view = new ImageView(context);
		view.setImageResource(mImgRes);
		return view;
	}
	
	/**
	 * ��ȡ�˵���Ŀ������
	 * @return
	 */
	public View getView()
	{
		initlayout();
		return mLayout;
	}

}
