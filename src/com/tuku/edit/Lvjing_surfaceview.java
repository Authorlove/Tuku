package com.tuku.edit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Lvjing_surfaceview extends SurfaceView implements SurfaceHolder.Callback{

	Lvjing_picture activity;
	String bgbmproot=null;
	int width,height;
	Paint paint;
	Lvjing_thread mythread;
	Bitmap bgbmp,drawbit;
	
	public Lvjing_surfaceview(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    // TODO Auto-generated constructor stub
	    this.activity=(Lvjing_picture) context;
    	this.width=this.activity.width;
    	this.height=this.activity.height;
	    this.requestFocus();
	    this.setFocusableInTouchMode(true);
	    getHolder().addCallback(this);
    }
	
//	public Lvjing_surfaceview(Lvjing_picture activity,int width,int height) {
//		super(activity);
//		// TODO Auto-generated constructor stub
//		this.activity=activity;
//		this.width=width;
//		this.height=height;
//		this.requestFocus();
//		this.setFocusableInTouchMode(true);
//		getHolder().addCallback(this);
//	}

	float left=0f;
	float top=0f;
	
	@Override
	public void draw(Canvas canvas){
		super.draw(canvas);
    	canvas.drawBitmap(drawbit, left,top, paint);
	}
	
	public Bitmap getnewsize(Bitmap oldbit){
		int bwidth=oldbit.getWidth();
		int bheight=oldbit.getHeight();
		int cwidth;
		int cheight;
		double wdbi=(double)height/(double)width;
		double bmbi=(double)bheight/(double)bwidth;
		if(wdbi>bmbi){
			cwidth=width;
			cheight=(int)(width*bmbi);
			top=((float)height-(float)cheight)/2;
		}else{
			cheight=height;
			cwidth=(int)(height/bmbi);
			left=((float)width-(float)cwidth)/2;
		}
		Bitmap newbit=changesize(oldbit,cwidth,cheight);
		return newbit;
	}

	public Bitmap changesize(Bitmap oldbit,int newwidth,int newheight){
		
		if(newwidth>0&&newheight>0){
		    Bitmap newbit;
		    int bwidth=oldbit.getWidth();
		    int bheight=oldbit.getHeight();
		    float scalewidth=((float)newwidth/(float)bwidth);
		    float scaleheight=((float)newheight/(float)bheight);
		    Matrix changematrix=new Matrix();
		    changematrix.postScale(scalewidth, scaleheight);
	    	newbit=Bitmap.createBitmap(oldbit,0,0,bwidth,bheight,changematrix,true);
	    	return newbit;
		}else return oldbit;
	}
	
	public String lei="yuantu";
	
	public void leixing(){
	    if(lei.equals("yuantu")){
	    	drawbit=bgbmp;
	    	lei="";
	    }
	    if(lei.equals("bingdong")){
	    	drawbit=ice(bgbmp);
	    	lei="";
	    }
	    if(lei.equals("heibai")){
	    	drawbit=black_white(bgbmp);
	    	lei="";
	    }
	    if(lei.equals("huidu")){
	    	drawbit=gray(bgbmp);
	    	lei="";
	    }
	}
	
	//灰度
	public Bitmap gray(Bitmap oldbit){
		Bitmap newbit;
		int bwidth=oldbit.getWidth();
		int bheight=oldbit.getHeight();
		int dst[]=new int[bwidth*bheight];
		oldbit.getPixels(dst, 0, bwidth, 0, 0, bwidth, bheight);
		int R,G,B,pixel;
		int pos,picolor;
		for(int y=0;y<bheight;y++){
			for(int x=0;x<bwidth;x++){
				pos=y*bwidth+x;
				picolor=dst[pos];
				R=Color.red(picolor);
				G=Color.green(picolor);
				B=Color.blue(picolor);
				pixel=(R+G+B)/3;
				R=pixel;
				G=pixel;
				B=pixel;
				dst[pos]=Color.rgb(R, G, B);
			}
		}
		newbit=Bitmap.createBitmap(bwidth,bheight,Bitmap.Config.ARGB_8888);
		newbit.setPixels(dst, 0, bwidth, 0, 0, bwidth, bheight);
		return newbit;
	}
	
	//黑白效果
	public Bitmap black_white(Bitmap oldbit){
		Bitmap newbit;
		int bwidth=oldbit.getWidth();
		int bheight=oldbit.getHeight();
		int dst[]=new int[bwidth*bheight];
		oldbit.getPixels(dst, 0, bwidth, 0, 0, bwidth, bheight);
		int R,G,B,pixel;
		int pos,picolor;
		for(int y=0;y<bheight;y++){
			for(int x=0;x<bwidth;x++){
				pos=y*bwidth+x;
				picolor=dst[pos];
				R=Color.red(picolor);
				G=Color.green(picolor);
				B=Color.blue(picolor);
				pixel=(R+G+B)/3;
				if(pixel>100)
					pixel=255;
				else
					pixel=0;
				R=pixel;
				G=pixel;
				B=pixel;
				dst[pos]=Color.rgb(R, G, B);
			}
		}
		newbit=Bitmap.createBitmap(bwidth,bheight,Bitmap.Config.ARGB_8888);
		newbit.setPixels(dst, 0, bwidth, 0, 0, bwidth, bheight);
		return newbit;
	}
	
	//冰冻效果
	public Bitmap ice(Bitmap oldbit){
		Bitmap newbit;
		int bwidth=oldbit.getWidth();
		int bheight=oldbit.getHeight();
		int dst[]=new int[bwidth*bheight];
		oldbit.getPixels(dst, 0, bwidth, 0, 0, bwidth, bheight);
		int R,G,B,pixel;
		int pos,picolor;
		for(int y=0;y<bheight;y++){
			for(int x=0;x<bwidth;x++){
				pos=y*bwidth+x;
				picolor=dst[pos];
				R=Color.red(picolor);
				G=Color.green(picolor);
				B=Color.blue(picolor);
				pixel=R-G-B;
				pixel=pixel*3/2;
				if(pixel<0)
					pixel=-pixel;
				if(pixel>255)
					pixel=255;
				R=pixel;
				pixel=G-B-R;
				pixel=pixel*3/2;
				if(pixel<0)
					pixel=-pixel;
				if(pixel>255)
					pixel=255;
				G=pixel;
				pixel=B-R-G;
				pixel=pixel*3/2;
				if(pixel<0)
					pixel=-pixel;
				if(pixel>255)
					pixel=255;
				B=pixel;
				dst[pos]=Color.rgb(R, G, B);
			}
		}
		newbit=Bitmap.createBitmap(bwidth,bheight,Bitmap.Config.ARGB_8888);
		newbit.setPixels(dst, 0, bwidth, 0, 0, bwidth, bheight);
		return newbit;
	}
	
	public void setbgbmp(Bitmap bgbmp){
		this.bgbmp=bgbmp;
	}
	
	public Bitmap getbgbmp(){
		return drawbit;
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		paint=new Paint();
		paint.setAntiAlias(true);
		//bgbmp=BitmapFactory.decodeResource(getResources(), com.example.tukuduang.R.drawable.pic);
		bgbmp=getnewsize(bgbmp);
		mythread=new Lvjing_thread(this);
		mythread.start();
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		mythread.setFlag(false);
	}
	
	

}
