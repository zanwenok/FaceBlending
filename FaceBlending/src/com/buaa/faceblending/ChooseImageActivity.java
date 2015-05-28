package com.buaa.faceblending;

import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class ChooseImageActivity extends Activity
{

	private static final int FLAG_CHOOSE = 1;
	private static final int FLAG_HANDLEBACK = 2;
	
	private ImageView mImageView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose);
		
		
		testBitmap();
	}
	private void testBitmap()
	{
		int width = 4;
		int height = 5;
		Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		for (int i = 0; i < height; i++)
		{
			for (int k = 0; k < width; k++)
			{
				if (k % 2 == 0)
				{
					bmp.setPixel(k, i, Color.RED);
				}
				else
				{
					bmp.setPixel(k, i, Color.BLACK);
				}
			}
		}
		
		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		Log.d("may", Arrays.toString(pixels));
		for (int i = 0; i < width; i++)
		{
			for (int k = 0; k < height; k++)
			{
				Log.d("may", pixels[i * height + k]+"");
			}
		}
	}
	
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.choose_img:
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_PICK);
			intent.setType("image/*");
			startActivityForResult(intent, FLAG_CHOOSE);
		
			
//			Intent intent = new Intent(this, MainActivity.class);
//			startActivityForResult(intent, FLAG_HANDLEBACK);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == RESULT_OK && null != data)
		{
			switch (requestCode)
			{
			case FLAG_CHOOSE:
				Uri uri = data.getData();
				Log.d("may", "uri="+uri+", authority="+uri.getAuthority());
				if (!TextUtils.isEmpty(uri.getAuthority()))
				{
					Cursor cursor = getContentResolver().query(uri, new String[]{ MediaStore.Images.Media.DATA }, null, null, null);
					if (null == cursor)
					{
						Toast.makeText(this, R.string.no_found, Toast.LENGTH_SHORT).show();
						return;
					}
					cursor.moveToFirst();
					String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
					Log.d("may", "path="+path);
					Intent intent = new Intent(this, MainActivity.class);
					intent.putExtra("path", path);
					startActivity(intent);
				}
				else
				{
					Log.d("may", "path="+uri.getPath());
					Intent intent = new Intent(this, MainActivity.class);
					intent.putExtra("path", uri.getPath());
					startActivity(intent);
				}
				break;
			}
		}
		
	}
	
}
