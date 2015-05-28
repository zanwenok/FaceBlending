package com.buaa.faceblending.view;

import java.util.Arrays;

import android.content.Context;  
import android.graphics.Bitmap;  
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;  
import android.graphics.Canvas;  
import android.graphics.Color;  
import android.graphics.Paint;  
import android.graphics.drawable.BitmapDrawable;  
import android.os.AsyncTask;
import android.support.v4.os.AsyncTaskCompat;
import android.util.AttributeSet;  
import android.util.Log;
import android.view.View; 
import android.widget.Toast;

import com.buaa.faceblending.*;
public class BlendingView extends View{
	final int IMN = 65535;
	Paint mPaint;  
	Bitmap mResourceMaskBitmap;
	//Source Image
	Bitmap mResourceBitmap;
	
	int mResourceWidth = 0;  
	int mResourceHeight = 0;  
	
	int mResourceArrayColor[] = null;  
	int mResourceArrayColorLengh = 0;  
	//Target Image
	Bitmap mTargetBitmap;
	
	int mTargetWidth = 0;  
	int mTargetHeight = 0;  
	
	int mTargetArrayColor[] = null;  
	int mTargetArrayColorLengh = 0;  
	//Marsk Image
	Bitmap mMarskBitmap;
	
	int mMarskWidth = 0;  
	int mMarskHeight = 0;  
	
	int mMarskArrayColor[] = null;  
	int mMarskArrayColorLengh = 0; 
	//Result Image
	Bitmap mResultBitmap;
	
	int mResultWidth = 0;  
	int mResultHeight = 0;  
	
	int mResultArrayColor[] = null;  
	int mResultArrayColorLengh = 0;  


	public BlendingView(Context context,Bitmap targetBitmap){
		super(context);
		init(context,targetBitmap);
	}
	
	public BlendingView(Context context, AttributeSet attrs,Bitmap targetBitmap) {  
        super(context, attrs);  
        init(context,targetBitmap);  
    } 
	private void init(Context context,Bitmap targetBitmap) {
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);  
		//初始化四张图
		mResourceBitmap = BitmapFactory.decodeResource(context.getResources(),  
	            R.drawable.source_image); 
//		mResultBitmap = BitmapFactory.decodeResource(context.getResources(),            R.drawable.source_image); 
	
		mResultBitmap = mResourceBitmap.copy(Config.ARGB_8888, true);// Bitmap.createBitmap(mResourceBitmap.getWidth(), mResourceBitmap.getHeight(), Config.ARGB_8888);// Bitmap.createBitmap(mResourceBitmap, 0, 0, mResourceBitmap.getWidth(), mResourceBitmap.getHeight());
//		mResultBitmap.setPixels(mResourceBitmap.get, offset, stride, x, y, width, height);
		/*mTargetBitmap = BitmapFactory.decodeResource(context.getResources(),  
	            R.drawable.target_test);*/
		mTargetBitmap = targetBitmap.copy(Config.ARGB_8888, true);
		mMarskBitmap = BitmapFactory.decodeResource(context.getResources(),  
	            R.drawable.source_marsk);
		mResourceMaskBitmap = BitmapFactory.decodeResource(context.getResources(),  
	            R.drawable.source_image_mask);
	//mTargetBitmap = targetBitmap;
		//test
		
        mResourceWidth = mResourceBitmap.getWidth();
        mMarskWidth = mMarskBitmap.getWidth();
        mTargetWidth = mTargetBitmap.getWidth();
        
        mResourceHeight = mResourceBitmap.getHeight();  
        mMarskHeight = mMarskBitmap.getHeight();
        mTargetHeight = mTargetBitmap.getHeight();
 
        mResourceArrayColorLengh = mResourceWidth * mResourceHeight;  
        mMarskArrayColorLengh = mMarskWidth * mMarskHeight;  
        mTargetArrayColorLengh = mTargetWidth * mTargetHeight;  
        
        mResourceArrayColor = new int[mResourceArrayColorLengh];  
        mMarskArrayColor = new int[mMarskArrayColorLengh];  
        mTargetArrayColor = new int[mTargetArrayColorLengh];  
        int resourceCount = 0;  
        int marskCount = 0;
        int targetCount = 0;
        for (int i = 0; i < mResourceHeight; i++) {  
        for (int j = 0; j < mResourceWidth; j++) {  
            //获得Bitmap 图片中每一个点的color颜色值  
            int color = mResourceBitmap.getPixel(j, i);  
            //将颜色值存在一个数组中 方便后面修改  
            mResourceArrayColor[resourceCount] = color;  
              
            resourceCount++;  
        }  
        } 
        for (int i = 0; i < mMarskHeight; i++) {  
            for (int j = 0; j < mMarskWidth; j++) {  
                //获得Bitmap 图片中每一个点的color颜色值  
                int color = mMarskBitmap.getPixel(j, i);  
                //将颜色值存在一个数组中 方便后面修改  
                mMarskArrayColor[marskCount] = color;  
               
                marskCount++;  
            }  
            }
        for (int i = 0; i < mTargetHeight; i++) {  
            for (int j = 0; j < mTargetWidth; j++) {  
                //获得Bitmap 图片中每一个点的color颜色值  
                int color = mTargetBitmap.getPixel(j, i);  
                //将颜色值存在一个数组中 方便后面修改  
                mTargetArrayColor[targetCount] = color;  
                  
                targetCount++;  
            }  
            }
	}//init
	void jacobi(int aa[],double xx[],double bb[],int interiorNum){
		int i,j,k;
		int count =0;
		double eR,eG,eB;
		double error;
		double nextX[] = new double[IMN*3];
		for(int q=0;q<xx.length;q++){
			xx[q]=0;
		}
		error=9999;
		while(true){
			for(i=1;i<=interiorNum;i++){
				nextX[i*3]=bb[i*3];
				nextX[i*3+1]=bb[i*3+1];
				nextX[i*3+2]=bb[i*3+2];
				for(k=0;k<4;k++){
					if(aa[i*4+k]==0)//四邻域点是边界点
						continue;
					nextX[i*3]+=xx[aa[i*4+k]*3];
					nextX[i*3+1]+=xx[aa[i*4+k]*3+1];
					nextX[i*3+2]+=xx[aa[i*4+k]*3+2];
				}
				nextX[i*3]/=4;
				nextX[i*3+1]/=4;
				nextX[i*3+2]/=4;
			}
			for(int q=0;q<interiorNum*3;q++){
				xx[q]=nextX[q];
			}
			//计算误差
			count++;
			if(count%100==0){
				error=0;
				for(i=1;i<interiorNum;i++){
					eB=bb[i*3];
					eG=bb[i*3+1];
					eR=bb[i*3+2];
					for(k=0;k<4;k++){
						if(aa[i*4+k]==0)
							continue;
						eB+=xx[aa[i*4+k]*3];
						eG+=xx[aa[i*4+k]*3+1];
						eR+=xx[aa[i*4+k]*3+2];
					}
					eB-=4*xx[i*3];
					eG-=4*xx[i*3+1];
					eR-=4*xx[i*3+2];
					error=error+eR*eR+eG*eG+eB*eB;
				}
				Log.d("sqrt(error)", ""+Math.sqrt(error));
				//count=0;
			}
			if(error<1){
				Log.d("count", ""+count);
				break;
			}
		}
	}
	void gaussSeidel(int aa[],double xx[],double bb[],int interiorNum){
		int i,j,k;
		int count=0;
		double eR,eG,eB;
		double error;
		double nextX[] = new double[IMN*3];
		
		for(int q=0;q<xx.length;q++){
			xx[q]=0;
		}
		error=9999;
		while(true){
			for(i=1;i<=interiorNum;i++){//nextX=(b-(4x-xl-xr-xu-xd))/4+x=(b+xl+xr+xu+xd)/4
				nextX[i*3]=bb[i*3];
				nextX[i*3+1]=bb[i*3+1];
				nextX[i*3+2]=bb[i*3+2];
				for(k=0;k<4;k++){
					if(aa[i*4+k]==0)//四邻域点是边界点
						continue;
					if(i < aa[i*4+k]){//上三角阵元素
						nextX[i*3]+=xx[aa[i*4+k]*3];
						nextX[i*3+1]+=xx[aa[i*4+k]*3+1];
						nextX[i*3+2]+=xx[aa[i*4+k]*3+2];
					}else{//下三角阵元素
						nextX[i*3]+=nextX[aa[i*4+k]*3];
						nextX[i*3+1]+=nextX[aa[i*4+k]*3+1];
						nextX[i*3+2]+=nextX[aa[i*4+k]*3+2];
					}
				}
				nextX[i*3]/=4;
				nextX[i*3+1]/=4;
				nextX[i*3+2]/=4;
			}
			for(int q=0;q<interiorNum*3;q++){
				xx[q]=nextX[q];
			}

			//计算误差
			count++;
			if(count%100==0){
				error=0;
				for(i=1;i<interiorNum;i++){
					eB=bb[i*3];
					eG=bb[i*3+1];
					eR=bb[i*3+2];
					for(k=0;k<4;k++){
						if(aa[i*4+k]==0)
							continue;
						eB+=xx[aa[i*4+k]*3];
						eG+=xx[aa[i*4+k]*3+1];
						eR+=xx[aa[i*4+k]*3+2];
					}
					eB-=4*xx[i*3];
					eG-=4*xx[i*3+1];
					eR-=4*xx[i*3+2];
					error=error+eR*eR+eG*eG+eB*eB;
				}
				Log.d("sqrt(error)", ""+Math.sqrt(error));
				//count=0;
			}
			if(error<1){
				Log.d("count", ""+count);
				break;
			}
		}//while
	}
	void blend(){
		int i,j,k,interiorNum;
		double bb[] = new double[IMN*3];
		double xx[] = new double[IMN*3];
		int aa[] = new int[IMN*4];
		int myMask[] = new int[mResourceArrayColorLengh];
		int xMin,xMax,yMin,yMax;
		int dxy[][] = {
				{-1,0},{1,0},{0,-1},{0,1}
		};
		int tmpx,tmpy,index;
		int R,G,B;
		xMin=mMarskWidth;
		xMax=0;
		yMin=mMarskHeight;
		yMax=0;
		
		interiorNum =0;
		//区分内部点，边界点和外部点
		for(i=1;i<mMarskHeight-1;i++){
			for(j=1;j<mMarskWidth-1;j++){
				if((Color.red((mMarskArrayColor[i*mMarskWidth+j])))>128 
					&& ((Color.red(mMarskArrayColor[(i-1)*mMarskWidth+j])))>128 
					&& ((Color.red(mMarskArrayColor[(i+1)*mMarskWidth+j])))>128 
					&& ((Color.red(mMarskArrayColor[i*mMarskWidth+j-1])))>128 
					&& ((Color.red(mMarskArrayColor[i*mMarskWidth+j+1])))>128 ){//内部点
						interiorNum++;
						myMask[i*mMarskWidth+j]=interiorNum;
						//(Interior+interiorNum)->y=i;(Interior+interiorNum)->x=j;  
						if(xMin>j) 
							xMin=j;
						if(xMax<j)
							xMax=j;
						if(yMin>i)
							yMin=i;
						if(yMax<i)
							yMax=i;
				}else if(Color.red((mMarskArrayColor[i*mMarskWidth+j]))>128 )//边界点
					myMask[i*mMarskWidth+j]=0;
				else//外部点
					myMask[i*mMarskWidth+j]=-1;
			}//for
		}//for
		
		//计算source的梯度值
		for(int q=0;q<bb.length;q++){
			bb[q]=0;
		}
		for(int q=0;q<bb.length;q++){
			aa[q]=0;
		}
		for(i=yMin;i<=yMax;i++){
			for(j=xMin;j<=xMax;j++){
				if( myMask[i*mMarskWidth+j] >=1 ){//内部点
					for(k=0;k<4;k++){//与四邻域的点计算梯度值
						tmpx=j+dxy[k][0];tmpy=i+dxy[k][1];
						if(myMask[tmpy*mMarskWidth+tmpx] > 0){//四邻域点在所选区域内部
							index=myMask[i*mMarskWidth+j];
							bb[ index*3 ] += Color.blue(mTargetArrayColor[i*mResourceWidth+j])-Color.blue(mTargetArrayColor[tmpy*mResourceWidth+tmpx]);
							bb[ index*3 +1] += Color.green(mTargetArrayColor[i*mResourceWidth+j])-Color.green(mTargetArrayColor[tmpy*mResourceWidth+tmpx]);
							bb[ index*3 +2] += Color.red(mTargetArrayColor[i*mResourceWidth+j])-Color.red(mTargetArrayColor[tmpy*mResourceWidth+tmpx]);
							aa[index*4+k]= myMask[tmpy*mMarskWidth+tmpx];
						}else if( myMask[tmpy*mMarskWidth+tmpx] == 0 ){//四邻域点在边界点上，目标图像中像素点值作为常数加到等式右边
							index=myMask[i*mMarskWidth+j];
							bb[ index*3 ] += Color.blue(mResourceArrayColor[tmpy*mResourceWidth+tmpx]);
							bb[ index*3 +1] += Color.green(mResourceArrayColor[tmpy*mResourceWidth+tmpx]);
							bb[ index*3 +2] += Color.red(mResourceArrayColor[tmpy*mResourceWidth+tmpx]);
						}
					}
				}
			}
		}//for
		
		//雅可比迭代
		jacobi(aa,xx,bb,interiorNum);

		//高斯赛德尔（jacobi的两倍速度）
		//gaussSeidel(aa,xx,bb,interiorNum);
//		mResultBitmap = Bitmap.createBitmap(mResourceWidth, mResourceHeight, Config.ARGB_8888);
		for(i=yMin;i<=yMax;i++){
			for(j=xMin;j<xMax;j++){
				if( myMask[i*mMarskWidth+j]  >=1 ){
					B=(int)(xx[myMask[i*mMarskWidth+j]*3]+0.5);
					G=(int)(xx[myMask[i*mMarskWidth+j]*3+1]+0.5);
					R=(int)(xx[myMask[i*mMarskWidth+j]*3+2]+0.5);
					if(B>255)
						B=255;
					if(B<0)
						B=0;
					if(G>255)
						G=255;
					if(G<0)
						G=0;
					if(R>255)
						R=255;
					if(R<0)
						R=0;
					//创建新图
//					Log.i("blend", "x:" + j + " y:" + i + " color:" + Integer.toHexString(Color.rgb(R, G, B)) + " width : " + mResourceBitmap.getWidth() + " height:" + mResourceBitmap.getHeight());
					try {
						mResultBitmap.setPixel(j, i, Color.argb(255, R, G,B));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}		
				}
			}
		}
	}  
	@Override
	protected void onDraw(Canvas canvas){
//		super.onDraw(canvas);
		if(isInEditMode()){
			return ;
		}
		if(success){
			if(mResultBitmap!=null){
				canvas.drawBitmap(mTargetBitmap, 0, 0, null);
				canvas.drawBitmap(mResourceMaskBitmap,0,0,null);
			}
		}else{
			canvas.drawBitmap(mTargetBitmap, 0, 0, null);
			canvas.drawBitmap(mResourceMaskBitmap,0,0,null);
		}
	}
	boolean success = false;
	private BlendingListener mBlendingListener;
	public void startBlending(BlendingListener listener){
		this.mBlendingListener = listener;
		AsyncTaskCompat.executeParallel(new BlendTask());
	}
	
	public interface BlendingListener{
		public void onStart();
		public void onEnd();
	}
	
	private class BlendTask extends AsyncTask<Void, Void, Void>{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(mBlendingListener != null){
				mBlendingListener.onStart();
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
//			long start = System.currentTimeMillis();
			blend();
//			long duration = System.currentTimeMillis() - start;
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			success =true;
			invalidate();
			if(mBlendingListener != null){
				mBlendingListener.onEnd();
			}
		}
		
	}
	public void setTargetBitmap(Bitmap targetBitmap){
		mTargetBitmap = targetBitmap.copy(Bitmap.Config.ARGB_8888, true);	  
	}
	public Bitmap getResultBitmap(){
		return mResultBitmap;
	}
}
