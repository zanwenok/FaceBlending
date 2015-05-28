package com.buaa.faceblending;

import android.R.integer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.buaa.faceblending.util.EditImage;
import com.buaa.faceblending.util.ImageFrameAdder;
import com.buaa.faceblending.util.ImageSpecific;
import com.buaa.faceblending.util.ReverseAnimation;
import com.buaa.faceblending.view.BlendingView;
import com.buaa.faceblending.view.CropImageView;
import com.buaa.faceblending.view.ToneView;
import com.buaa.faceblending.view.BlendingView.BlendingListener;
import com.buaa.faceblending.view.ZoomImageView;
import com.buaa.faceblending.menu.MenuView;
import com.buaa.faceblending.menu.OnMenuClickListener;
import com.buaa.faceblending.menu.SecondaryListMenuView;
import com.buaa.faceblending.menu.ToneMenuView;

public class MainActivity extends Activity implements OnSeekBarChangeListener
{
	public boolean mWaitingToPick; // Whether we are wait the user to pick a face.
    public boolean mSaving; // Whether the "save" button is already clicked.
    
    private Handler mHandler = null;
    private ProgressDialog mProgress;
    
	private Bitmap mBitmap;
	private BlendingView mBlendingView;
	private ZoomImageView mZoomImageViwe;
	private ImageView scaleImageView;
	RelativeLayout showPic ;
	/**
	 * ��ʱ����
	 */
	private Bitmap mTmpBmp;
	
	private CropImageView mCropImageView;
	private EditImage mEditImage;
	private ImageFrameAdder mImageFrame;
	private ImageSpecific mImageSpecific;
	
	/**
	 * SourceImage
	 */
	private ImageView sourceImageView ;
	private final int sourceAlphaFull = 255;
	private final int sourceAlphaHalf = 100;
	/**
	 * һ���˵�
	 */
	private MenuView mMenuView;
	//�༭
	private final int[] EDIT_IMAGES = new int[] { R.drawable.ic_menu_crop, R.drawable.ic_menu_rotate_left, R.drawable.ic_menu_mapmode, R.drawable.btn_rotate_horizontalrotate };
	private final int[] EDIT_TEXTS = new int[] { R.string.crop, R.string.rotate, R.string.resize, R.string.reverse_transform };
	//�߿�
	private final int[] FRAME_IMAGE = new int[] { R.drawable.btn_mainmenu_frame_normal, R.drawable.btn_mainmenu_color_normal, R.drawable.old_remember };
	private final int[] FRAME_TEXTS = new int[] { R.string.frame, R.string.doodle, R.string.specific };
	
	/**
	 * �����˵�
	 */
	private SecondaryListMenuView mSecondaryListMenu;
	private final int[] ROTATE_IMGRES = new int[] { R.drawable.ic_menu_rotate_left, R.drawable.ic_menu_rotate_right };
	private final int[] ROTATE_TEXTS = new int[] { R.string.rotate_left, R.string.rotate_right };
	
	private final int[] RESIZE_TEXTS = new int[] { R.string.resize_one_to_two, R.string.resize_one_to_three, R.string.resize_one_to_four };
	
	private final int[] FRAME_ADD_IMAGES = new int[] { R.drawable.frame_around1, R.drawable.frame_around2, R.drawable.frame_small1 };
	
	private final int[] FRAME_DOODLE = new int[] { R.drawable.cloudy, R.drawable.qipao1, R.drawable.qipao2 };
	
	private final int[] EDIT_REVERSE = new int[] { R.drawable.btn_rotate_horizontalrotate, R.drawable.btn_rotate_verticalrotate };
	
	/**
	 * ��ɫ�˵�
	 */
	private ToneMenuView mToneMenu;
	private ToneView mToneView;
	
	
	/** ��ɫ */
	private final int FLAG_TONE = 0x1;
	/** �߿� */
	private final int FLAG_FRAME = FLAG_TONE + 1;
	/** ��ӱ߿� */
	private final int FLAG_FRAME_ADD = FLAG_TONE + 6;
	/** Ϳѻ */
	private final int FLAG_FRAME_DOODLE = FLAG_TONE + 7;
	/** ��Ч */
	private final int FLAG_FRAME_SPECIFIC = FLAG_TONE + 10;
	/** �༭ */
	private final int FLAG_EDIT = FLAG_TONE + 2;
	/** �ü� */
	private final int FLAG_EDIT_CROP = FLAG_TONE + 3;
	/** ��ת */
	private final int FLAG_EDIT_ROTATE = FLAG_TONE + 4;
	/** ���� */
	private final int FLAG_EDIT_RESIZE = FLAG_TONE + 5;
	/** ��ת */
	private final int FLAG_EDIT_REVERSE = FLAG_TONE + 8;
	
	private View mSaveAll;
	private View mSaveStep;
	
	private final int STATE_CROP = 0x1;
	private final int STATE_DOODLE = STATE_CROP <<1;
	private final int STATE_NONE = STATE_CROP <<2;
	private final int STATE_TONE = STATE_CROP <<3;
	private final int STATE_REVERSE = STATE_CROP <<4;
	private final int STATE_SCALE = STATE_CROP <<5;
	private int mState;
	
	/**
	 * ��ת����
	 */
	private ReverseAnimation mReverseAnim;
	private int mImageViewWidth;
	private int mImageViewHeight;
	
	private ProgressDialog mProgressDialog;
	
	private TextView mShowHandleName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		mHandler = new Handler()
		{
			public void handleMessage(Message msg)
			{
				closeProgress();
				reset();
			}
		};
		
		// ȫ����ʾ
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.image_main);
		
		/*final BlendingView blendingView = new BlendingView(this);
		final ProgressDialog blendProgressDialog = ProgressDialog.show(this, null, "Blending...  Please wait...", true, false);
		blendingView.startBlending(new BlendingListener() {
			@Override
			public void onStart() {
				blendProgressDialog.show();
			}
			
			@Override
			public void onEnd() {
				blendProgressDialog.dismiss();
			}
		});*/
		
		
	//	//sourceImageView = (ImageView)findViewById(R.id.source_image_noface);
		//���桢ȡ��
		mSaveAll = findViewById(R.id.save_all);
		//�̡���
		mSaveStep = findViewById(R.id.save_all_step);
		mShowHandleName = (TextView) findViewById(R.id.handle_name);
		
		Intent intent = getIntent();
		String path = intent.getStringExtra("path");
		Log.d("may", "MainActivity--->path="+path);
		if (null == path)
		{
			Toast.makeText(this, R.string.load_failure, Toast.LENGTH_SHORT).show();
			finish();
		}
		showPic = (RelativeLayout) findViewById(R.id.show_pic);
		
		//����
		//showSaveStep();
		scaleImageView = new ImageView(this);
	//	mState = STATE_SCALE;
	//	mImageView = (CropImageView) findViewById(R.id.crop_image);
		mCropImageView = (CropImageView) new CropImageView(this);
		
		//mZoomImageViwe = new ZoomImageView(this);
		//mZoomImageViwe.setImage(BitmapFactory.decodeFile(path));
		
		mBlendingView = new BlendingView(this,BitmapFactory.decodeFile(path));
		//����ʾmask
		showPic.addView(mBlendingView);
	    //showPic.addView(mZoomImageViwe);
		
/*		mBitmap = BitmapFactory.decodeFile(path);
	//	mBlendingView.setTargetBitmap(BitmapFactory.decodeFile(path));

		mTmpBmp = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
		
		mCropImageView.setImageBitmap(mBitmap);
		mCropImageView.setImageBitmapResetBase(mBitmap, true);
        
        mEditImage = new EditImage(this, mCropImageView, mBitmap);
        mImageFrame = new ImageFrameAdder(this, mCropImageView, mBitmap);
        mCropImageView.setEditImage(mEditImage);
        mImageSpecific = new ImageSpecific(this); */
	}
	
	// -----------------------------------�˵��¼�------------------------------------
	
	public void onClick(View v)
	{
		int flag = -1;
		switch (v.getId())
		{
		case R.id.save:
//			onSaveClicked();
			String path = saveBitmap(mBitmap);
			if (mProgressDialog != null)
			{
				mProgressDialog.dismiss();
				mProgressDialog = null;
			}
			Intent data = new Intent();
			data.putExtra("path", path);
			setResult(RESULT_OK, data);
			finish();
			return;
		case R.id.cancel:
			setResult(RESULT_CANCELED);
            finish();
			return;
		case R.id.save_step:
			//sourceImageView.setAlpha(sourceAlphaFull);
			if (mState == STATE_CROP)
			{
				mTmpBmp = mEditImage.cropAndSave(mTmpBmp);
			}
			else if (mState == STATE_DOODLE)
			{
				mTmpBmp = mImageFrame.combinate(mTmpBmp);
			}
			else if(mState == STATE_TONE)
			{
				// TODO �ڲ˵���ʧʱҪ���ɫ״̬ΪNONE״̬
				mTmpBmp = mToneView.getBitmap();
			}
			else if(mState == STATE_REVERSE)
			{
				// ��ת��ɣ�Ҫ��ImageView��ת��������ͼƬ
				mReverseAnim.cancel();
				mReverseAnim = null;
			}
			if (mState == STATE_SCALE){
				//TODO ��ɼ���
				Bitmap tmpBitmap = mZoomImageViwe.getImage();
	/*			
				Canvas canvas = new Canvas(bitmap)
				Canvas.drawBitmap(tmpBitmap, 0, 0, null);*/
			}else{
				mBitmap = mTmpBmp;
				showSaveAll();
				reset();
				
				mEditImage.mSaving = true;
				mImageViewWidth = mCropImageView.getWidth();
				mImageViewHeight = mCropImageView.getHeight();
				return;
			}
		case R.id.cancel_step:
			//sourceImageView.setAlpha(sourceAlphaFull);
			if (mState == STATE_CROP)
			{
				mEditImage.cropCancel();
			}
			else if (mState == STATE_DOODLE)
			{
				mImageFrame.cancelCombinate();
			}
			else if (mState == STATE_REVERSE)
			{
				mReverseAnim.cancel();
			}
			else if (mState == STATE_SCALE){
				finish();
				return;
			}
			showSaveAll();
			resetToOriginal();
			return;
		case R.id.blend:
			
//			final BlendingView blendingView = new BlendingView(this);
			final ProgressDialog blendProgressDialog = ProgressDialog.show(this, null, "Blending...  Please wait...", true, false);
			
			
			
			mBlendingView.startBlending(new BlendingListener() {
				@Override
				public void onStart() {
					blendProgressDialog.show();
				}
				
				@Override
				public void onEnd() {
					blendProgressDialog.dismiss();
					//------��ʾ���-------------
					showPic.removeView(mBlendingView);
					showPic.addView(mCropImageView);
					mBitmap = mBlendingView.getResultBitmap().copy(Bitmap.Config.ARGB_8888, true);
					//	mBlendingView.setTargetBitmap(BitmapFactory.decodeFile(path));

						mTmpBmp = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
						
						mCropImageView.setImageBitmap(mBitmap);
						mCropImageView.setImageBitmapResetBase(mBitmap, true);
				        
				        mEditImage = new EditImage(MainActivity.this, mCropImageView, mBitmap);
				        mImageFrame = new ImageFrameAdder(MainActivity.this, mCropImageView, mBitmap);
				        mCropImageView.setEditImage(mEditImage);
				        mImageSpecific = new ImageSpecific(MainActivity.this); 
				}
			});
			
			break;
		case R.id.edit:
			//sourceImageView.setAlpha(sourceAlphaHalf);
			flag = FLAG_EDIT;
			break;
		case R.id.tone:
			//sourceImageView.setAlpha(sourceAlphaHalf);
			initTone();
			showSaveStep();
			return;
		case R.id.frame:
			//sourceImageView.setAlpha(sourceAlphaHalf);
			flag = FLAG_FRAME;
			break;
		}
		
		initMenu(flag);
	}
	
	private void initTone()
	{
		if (null == mToneMenu)
		{
			mToneMenu = new ToneMenuView(this);
		}
		
		mToneMenu.show();
		
		mState = STATE_TONE;
		
		mToneView = mToneMenu.getToneView();
		mToneMenu.setHueBarListener(this);
		mToneMenu.setLumBarListener(this);
		mToneMenu.setSaturationBarListener(this);
	}
	
	private void initMenu(int flag)
    {
    	if (null == mMenuView)
    	{
    		mMenuView = new MenuView(this);
			mMenuView.setBackgroundResource(R.drawable.popup);
			mMenuView.setTextSize(16);
			
			switch (flag)
			{
			case FLAG_EDIT:
				mMenuView.setImageRes(EDIT_IMAGES);
				mMenuView.setText(EDIT_TEXTS);
				mMenuView.setOnMenuClickListener(editListener());
				break;
			case FLAG_TONE:
				break;
			case FLAG_FRAME:
				mMenuView.setImageRes(FRAME_IMAGE);
				mMenuView.setText(FRAME_TEXTS);
				mMenuView.setOnMenuClickListener(frameListener());
				break;
			}
		}

		mMenuView.show();
    }
	
	/**
	 * �༭������
	 * @return
	 */
	private OnMenuClickListener editListener()
	{
		return new OnMenuClickListener()
		{
			@Override
			public void onMenuItemClick(AdapterView<?> parent, View view,
					int position)
			{
				int[] location = new int[2];
				view.getLocationInWindow(location);
				int left = location[0];
				int flag = -1;
				switch (position)
				{
				case 0: // �ü�
					mMenuView.hide();
					crop();
					showSaveStep();
					return;
				case 1: // ��ת
					flag = FLAG_EDIT_ROTATE;
					break;
				case 2:// ����
					flag = FLAG_EDIT_RESIZE;
					break;
				case 3: // ��ת
					flag = FLAG_EDIT_REVERSE;
					break;
				}
				
				initSecondaryMenu(flag ,left);
			}

			@Override
			public void hideMenu()
			{
				dimissMenu();
			}
			
		};
	}
	
	private OnMenuClickListener frameListener()
	{
		return new OnMenuClickListener()
		{
			@Override
			public void onMenuItemClick(AdapterView<?> parent, View view,
					int position)
			{
				int[] location = new int[2];
				view.getLocationInWindow(location);
				int left = location[0];
				int flag = -1;
				switch (position)
				{
				case 0: // �ӱ߿�
					flag = FLAG_FRAME_ADD;
					break;
				case 1: // Ϳѻ
					flag = FLAG_FRAME_DOODLE;
					break;
				case 2: // ��Ч
					flag = FLAG_FRAME_SPECIFIC;
					break;
				}
				
				initSecondaryMenu(flag ,left);
			}

			@Override
			public void hideMenu()
			{
				dimissMenu();
			}
			
		};
	}
	
	/**
	 * �˵���ʧ����
	 */
	private void dimissMenu()
	{
		mMenuView.dismiss();
		mMenuView = null;
	}
	
	/**
	 * ��ʼ�������˵�
	 * @param flag
	 * @param left
	 */
	private void initSecondaryMenu(int flag, int left)
	{
		mSecondaryListMenu = new SecondaryListMenuView(this);
		mSecondaryListMenu.setBackgroundResource(R.drawable.popup_bottom_tip);
		mSecondaryListMenu.setTextSize(16);
		mSecondaryListMenu.setWidth(300);
		mSecondaryListMenu.setMargin(left);
		switch (flag)
		{
		case FLAG_EDIT_ROTATE: // ��ת
			mSecondaryListMenu.setImageRes(ROTATE_IMGRES);
			mSecondaryListMenu.setText(ROTATE_TEXTS);
			mSecondaryListMenu.setOnMenuClickListener(rotateListener());
			break;
		case FLAG_EDIT_RESIZE: // ����
			mSecondaryListMenu.setText(RESIZE_TEXTS);
			mSecondaryListMenu.setOnMenuClickListener(resizeListener());
			break;
		case FLAG_EDIT_REVERSE: // ��ת
			mSecondaryListMenu.setImageRes(EDIT_REVERSE);
			mSecondaryListMenu.setOnMenuClickListener(reverseListener());
			break;
		case FLAG_FRAME_ADD: // ��ӱ߿�
			mSecondaryListMenu.setWidth(480);
			mSecondaryListMenu.setImageRes(FRAME_ADD_IMAGES);
			mSecondaryListMenu.setOnMenuClickListener(addFrameListener());
			break;
		case FLAG_FRAME_DOODLE: // Ϳѻ
			mSecondaryListMenu.setImageRes(FRAME_DOODLE);
			mSecondaryListMenu.setOnMenuClickListener(doodleListener());
			break;
		case FLAG_FRAME_SPECIFIC: // ��Ч
			mSecondaryListMenu.setWidth(400);
			mSecondaryListMenu.setText(getResources().getStringArray(R.array.specific_item));
			mSecondaryListMenu.setOnMenuClickListener(specificListener());
			break;
		}

		mSecondaryListMenu.show();
	}
	
	/**
	 * �����˵��е���ת�¼�
	 * @return
	 */
	private OnMenuClickListener rotateListener()
	{
		return new OnMenuClickListener()
		{
			@Override
			public void onMenuItemClick(AdapterView<?> parent, View view,
					int position)
			{
				switch (position)
				{
				case 0: // ����ת
					rotate(-90);
					break;
				case 1: // ����ת
					rotate(90);
					break;
				}
				
				// һ���˵�����
				mMenuView.hide();
				showSaveStep();
			}

			@Override
			public void hideMenu()
			{
				dismissSecondaryMenu();
			}
			
		};
	}
	
	/**
	 * �����˵��е������¼�
	 * @return
	 */
	private OnMenuClickListener resizeListener()
	{
		return new OnMenuClickListener()
		{
			@Override
			public void onMenuItemClick(AdapterView<?> parent, View view,
					int position)
			{
				float scale = 1.0F;
				switch (position)
				{
				case 0: // 1:2
					scale /= 2;
					break;
				case 1: // 1:3
					scale /= 3;
					break;
				case 2: // 1:4
					scale /= 4;
					break;
				}
				
				resize(scale);
				// һ���˵�����
				mMenuView.hide();
				showSaveStep();
			}
			
			@Override
			public void hideMenu()
			{
				dismissSecondaryMenu();
			}
			
		};
	}
	
	private OnMenuClickListener reverseListener()
	{
		return new OnMenuClickListener()
		{
			@Override
			public void onMenuItemClick(AdapterView<?> parent, View view,
					int position)
			{
				int flag = -1;
				switch (position)
				{
				case 0: // ˮƽ��ת
					flag = 0;
					break;
				case 1: // ��ֱ��ת
					flag = 1;
					break;
				}
				
				reverse(flag);
				// һ���˵�����
				mMenuView.hide();
				showSaveStep();
			}

			@Override
			public void hideMenu()
			{
				dismissSecondaryMenu();
			}
			
		};
	}
	
	/**
	 * �����˵��е���ӱ߿��¼�
	 * @return
	 */
	private OnMenuClickListener addFrameListener()
	{
		return new OnMenuClickListener()
		{
			@Override
			public void onMenuItemClick(AdapterView<?> parent, View view,
					int position)
			{
				int flag = -1;
				int res = 0;
				switch (position)
				{
				case 0: // �߿�1
					flag = ImageFrameAdder.FRAME_SMALL;
					res = 0;
					break;
				case 1: // �߿�2
					flag = ImageFrameAdder.FRAME_SMALL;
					res = 1;
					break;
				case 2: // �߿�3
					flag = ImageFrameAdder.FRAME_BIG;
					res = R.drawable.frame_big1;
					break;
				case 3: // �߿�4
					flag = ImageFrameAdder.FRAME_BIG;
					res = 2;
					break;
				}
				
				addFrame(flag, res);
//				mImageView.center(true, true);
				
				// һ���˵�����
				mMenuView.hide();
				showSaveStep();
			}
			
			@Override
			public void hideMenu()
			{
				dismissSecondaryMenu();
			}
			
		};
	}
	
	/**
	 * �����˵��е�Ϳѻ�¼�
	 * @return
	 */
	private OnMenuClickListener doodleListener()
	{
		return new OnMenuClickListener()
		{
			@Override
			public void onMenuItemClick(AdapterView<?> parent, View view,
					int position)
			{
				// Ϳѻ״̬
				mCropImageView.setState(CropImageView.STATE_DOODLE);
				switch (position)
				{
				case 0: // Ϳѻ1
					doodle(R.drawable.cloudy);
					break;
				case 1: // Ϳѻ2
					doodle(R.drawable.qipao1);
					break;
				case 2: // Ϳѻ3
					doodle(R.drawable.qipao2);
					break;
				}
				
				// һ���˵�����
				mMenuView.hide();
				showSaveStep();
			}
			
			@Override
			public void hideMenu()
			{
				dismissSecondaryMenu();
			}
			
		};
	}
	
	/**
	 * ��Ч�����ʶ
	 */
	private int specFlag = -1;
	
	/**
	 * �����˵��е���Ч�¼�
	 * @return
	 */
	private OnMenuClickListener specificListener()
	{
		return new OnMenuClickListener()
		{
			@Override
			public void onMenuItemClick(AdapterView<?> parent, View view,
					int position)
			{
				switch (position)
				{
				case 0: // ����Ч��
					specFlag = ImageSpecific.FLAG_OLD_REMEMBER;
					break;
				case 1: // ģ��Ч��
					specFlag = ImageSpecific.FLAG_BLUR;
					break;
				case 2: // ��Ч��
					specFlag = ImageSpecific.FLAG_SHARPEN;
					break;
				case 3: // ������
					specFlag = ImageSpecific.FLAG_OVERLAY;
					break;
				case 4: // ��Ƭ
					specFlag = ImageSpecific.FLAG_FILM;
					break;
				case 5: // ����
					specFlag = ImageSpecific.FLAG_EMBOSS;
					break;
				case 6: // ����Ч��
					specFlag = ImageSpecific.FLAG_SUNSHINE;
					break;
				case 7: // �޺�
					specFlag = ImageSpecific.FLAG_NEON;
					break;
				case 8: // ����2
					specFlag = ImageSpecific.FLAG_ALPHA_LAYER;
					break;
				}
				
				imageSpecific(specFlag);
				// һ���˵�����
				mMenuView.hide();
				showSaveStep();
			}

			@Override
			public void hideMenu()
			{
				dismissSecondaryMenu();
			}
			
		};
	}
	
	/**
	 * ���ض����˵�
	 */
	private void dismissSecondaryMenu()
	{
		mSecondaryListMenu.dismiss();
		mSecondaryListMenu = null;
	}
	
	private void showSaveStep()
	{
		mSaveStep.setVisibility(View.VISIBLE);
		mSaveAll.setVisibility(View.GONE);
	}
	
	private void showSaveAll()
	{
		mSaveStep.setVisibility(View.GONE);
		mSaveAll.setVisibility(View.VISIBLE);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		switch (keyCode)
		{
		case KeyEvent.KEYCODE_BACK:
			if (mMenuView != null && mMenuView.isShow() || null != mToneMenu && mToneMenu.isShow())
			{
				mMenuView.hide();
				mToneMenu.hide();
				mToneMenu = null;
			} else
			{
				if (mSaveAll.getVisibility() == View.GONE)
				{
					showSaveAll();
				}
				else
				{
					finish();
				}
			}
			break;
		case KeyEvent.KEYCODE_MENU:
			break;
		
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	
	// ----------------------------------------------------����----------------------------------------------------

	/**
	 * ���в���ǰ��׼��
	 * @param state ��ǰ׼������Ĳ���״̬
	 * @param imageViewState ImageViewҪ�����״̬
	 * @param hideHighlight �Ƿ����زü���
	 */
	private void prepare(int state, int imageViewState, boolean hideHighlight)
	{
		resetToOriginal();
		mEditImage.mSaving = false;
		if (null != mReverseAnim)
		{
			mReverseAnim.cancel();
			mReverseAnim = null;
		}
		
		if (hideHighlight)
		{
			mCropImageView.hideHighlightView();
		}
		mState = state;
		mCropImageView.setState(imageViewState);
		mCropImageView.invalidate();
	}
	
	/**
	 * �ü�
	 */
	private void crop()
	{
		// ����ü�״̬
		prepare(STATE_CROP, CropImageView.STATE_HIGHLIGHT, false);
		mShowHandleName.setText(R.string.crop);
		mEditImage.crop(mTmpBmp);
		reset();
	}
	
	/**
	 * ��ת
	 * @param degree
	 */
	private void rotate(float degree)
	{
		// δ��������״̬
		mImageViewWidth = mCropImageView.getWidth();
		mImageViewHeight = mCropImageView.getHeight();
		
		prepare(STATE_NONE, CropImageView.STATE_NONE, true);
		mShowHandleName.setText(R.string.rotate);
		Bitmap bm = mEditImage.rotate(mTmpBmp, degree);
		mTmpBmp = bm;
		reset();
	}
	
	private void reverse(int flag)
	{
		// δ��������״̬
		prepare(STATE_REVERSE, CropImageView.STATE_NONE, true);
		mShowHandleName.setText(R.string.reverse_transform);
		int type = 0;
		switch (flag)
		{
		case 0:
			type = ReverseAnimation.HORIZONTAL;
			break;
		case 1:
			type = ReverseAnimation.VERTICAL;
			break;
		}
		
		mReverseAnim = new ReverseAnimation(0F, 180F, mImageViewWidth == 0 ? mCropImageView.getWidth() / 2 : mImageViewWidth / 2, mImageViewHeight == 0 ? mCropImageView.getHeight() / 2 : mImageViewHeight / 2, 0, true);
		mReverseAnim.setReverseType(type);
		mReverseAnim.setDuration(1000);
		mReverseAnim.setFillEnabled(true);
		mReverseAnim.setFillAfter(true);
		mCropImageView.startAnimation(mReverseAnim);
		Bitmap bm = mEditImage.reverse(mTmpBmp, flag);
		mTmpBmp = bm;
//		reset();
	}
	
	/**
	 * ����
	 * @param bm
	 * @param scale
	 */
	private void resize(float scale)
	{
		// δ��������״̬
		prepare(STATE_NONE, CropImageView.STATE_NONE, true);
		mShowHandleName.setText(R.string.resize);
		Bitmap bmp = mEditImage.resize(mTmpBmp, scale);
		mTmpBmp = bmp;
		reset();
	}
	
	/**
	 * ��ӱ߿�
	 */
	private void addFrame(int flag, int res)
	{
		// δ��������״̬
		prepare(STATE_NONE, CropImageView.STATE_NONE, true);
		mShowHandleName.setText(R.string.frame);
		mTmpBmp = mImageFrame.addFrame(flag, mBitmap, res);
		reset();
	}
	
	/**
	 * Ϳѻ
	 */
	private void doodle(int res)
	{
		// ����Ϳѻ״̬
		prepare(STATE_DOODLE, CropImageView.STATE_DOODLE, true);
		mShowHandleName.setText(R.string.frame_doodle);
		mImageFrame.doodle(res);
		reset();
	}
	
	/**
	 * ��Ч����
	 * @param flag
	 */
	private void imageSpecific(final int flag)
	{
		prepare(STATE_NONE, CropImageView.STATE_NONE, true);
		showProgress();
		new Thread(new Runnable()
		{
			public void run()
			{
				mTmpBmp = mImageSpecific.imageSpecific(mTmpBmp, flag);
				mHandler.sendEmptyMessage(0);
			}
		}).start();
	}
	
	/**
	 * ��������һ��ͼƬ
	 */
	private void reset()
	{
		mCropImageView.setImageBitmap(mTmpBmp);
		mCropImageView.invalidate();
	}
	
	private void resetToOriginal()
	{
		mTmpBmp = mBitmap;
		mCropImageView.setImageBitmap(mBitmap);
		// �Ѿ�����ͼƬ
		mEditImage.mSaving = true;
		// ��ղü�����
		mCropImageView.mHighlightViews.clear();
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser)
	{
		
		int flag = -1;
		switch ((Integer) seekBar.getTag())
		{
		case 1: // ���Ͷ�
			flag = 1;
			mToneView.setSaturation(progress);
			break;
		case 2: // ɫ��
			flag = 0;
			mToneView.setHue(progress);
			break;
		case 3: // ����
			flag = 2;
			mToneView.setLum(progress);
			break;
		}
		
		Bitmap bm = mToneView.handleImage(mTmpBmp, flag);
		mCropImageView.setImageBitmapResetBase(bm, true);
		mCropImageView.center(true, true);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar)
	{
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar)
	{
	}
	
	/**
	 * ��ʾ������
	 */
	private void showProgress()
	{
		Context context = this;
		mProgress = ProgressDialog.show(context, null, context.getResources().getString(R.string.handling));
		mProgress.show();
		Log.d("may", "show Progress");
	}
	
	/**
	 * �رս�����
	 */
	private void closeProgress()
	{
		if (null != mProgress)
		{
			mProgress.dismiss();
			mProgress = null;
		}
	}
	
	/**
	 * ����ͼƬ������
	 * @param bm
	 */
	private String saveBitmap(Bitmap bm)
	{
		mProgressDialog = ProgressDialog.show(this, null, getResources().getString(R.string.save_bitmap));
		mProgressDialog.show();
		return mEditImage.saveToLocal(bm);
	}
	
}
