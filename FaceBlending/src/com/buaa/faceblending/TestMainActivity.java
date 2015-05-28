package com.buaa.faceblending;

import android.R.integer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.buaa.faceblending.R.id;
import com.buaa.faceblending.util.EditImage;
import com.buaa.faceblending.util.ImageFrameAdder;
import com.buaa.faceblending.util.ImageSpecific;
import com.buaa.faceblending.util.ReverseAnimation;
import com.buaa.faceblending.view.BlendingView;
import com.buaa.faceblending.view.BlendingView.BlendingListener;
import com.buaa.faceblending.view.CropImageView;
import com.buaa.faceblending.view.ToneView;
import com.buaa.faceblending.menu.MenuView;
import com.buaa.faceblending.menu.OnMenuClickListener;
import com.buaa.faceblending.menu.SecondaryListMenuView;
import com.buaa.faceblending.menu.ToneMenuView;

public class TestMainActivity extends Activity
{
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
			
		// 全屏显示
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	/*	setContentView(R.layout.image_main);
		final BlendingView blendingView = (BlendingView) findViewById(R.id.image_blending);
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
	  
	}
	
	// -----------------------------------菜单事件------------------------------------
	
	
}
