package com.tuku.edit;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;

public class Myimageview extends ImageView implements OnGlobalLayoutListener, OnScaleGestureListener,OnTouchListener{

	boolean one=false;
	float mscale;
	float maxscale;
	Matrix scalematrix;
	ScaleGestureDetector myscalegd;
	int movestep;
	boolean ifcheckfr,ifchecktb;
	public Myimageview(Context context, AttributeSet attrs) {
		this(context, attrs,0);
		// TODO Auto-generated constructor stub
	}
	
	public Myimageview(Context context) {
		this(context,null);
		// TODO Auto-generated constructor stub
	}

	public Myimageview(Context context, AttributeSet attrs, int i) {
		// TODO Auto-generated constructor stub
		super(context,attrs,i);
		scalematrix=new Matrix();
		setScaleType(ScaleType.MATRIX);
		myscalegd=new ScaleGestureDetector(context, this);
		setOnTouchListener(this);
		movestep=ViewConfiguration.get(context).getScaledTouchSlop();
	}

	@Override
	protected void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
		getViewTreeObserver().addOnGlobalLayoutListener(this);
	}
	
	@Override
	protected void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		super.onDetachedFromWindow();
		getViewTreeObserver().removeGlobalOnLayoutListener(this);
	}
	
	@Override
	public void onGlobalLayout() {
		// TODO Auto-generated method stub
		
		if(!one){
			int width=getWidth();
			int height=getHeight();
			Drawable dr=getDrawable();
			if(dr==null)
				return;
			int dwidth=dr.getIntrinsicWidth();
			int dheight=dr.getIntrinsicHeight();
			float scale=1.0f;
			if(dwidth>width&&dheight<height){
				scale=width*1.0f/dwidth;
			}
			if(dheight>height&&dwidth<width){
				scale=height*1.0f/dheight;
			}
			if((dheight>height&&dwidth>width)||(dheight<height&&dwidth<width)){
				scale=Math.min(width*1.0f/dwidth, height*1.0f/dheight);
			}
			mscale=scale;
			maxscale=mscale*4.0f;
			int x=getWidth()/2-dwidth/2;
			int y=getHeight()/2-dheight/2;
			scalematrix.postTranslate(x, y);
			scalematrix.postScale(mscale, mscale,width/2,height/2);
			setImageMatrix(scalematrix);
			one=true;
		}
		
	}

	public float getcurrentscale(){
		float[] values=new float[9];
		scalematrix.getValues(values);
		return values[Matrix.MSCALE_X];
	}
	
	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		// TODO Auto-generated method stub
		float curscale=getcurrentscale();
		float scalefact=detector.getScaleFactor();
		if(getDrawable()==null)
			return true;
		if((curscale<maxscale&&scalefact>1.0f)||(curscale>mscale&&scalefact<1.0f)){
			if(curscale*scalefact<mscale){
				scalefact=mscale/curscale;
			}
			if(curscale*scalefact>maxscale){
				scalefact=maxscale/curscale;
			}
			scalematrix.postScale(scalefact, scalefact, detector.getFocusX(), detector.getFocusY());
			check();
			setImageMatrix(scalematrix);
		}
		return true;
	}
	
	public RectF getmatrixrectf(){
		Matrix matrix=scalematrix;
		RectF rect=new RectF();
		Drawable dr=getDrawable();
		if(dr!=null){
			rect.set(0,0,dr.getIntrinsicWidth(),dr.getIntrinsicHeight());
			matrix.mapRect(rect);
		}
		return rect;
	}
	
	public void check(){
		RectF rect=getmatrixrectf();
		float x=0;
		float y=0;
		int width=getWidth();
		int height=getHeight();
		if(rect.width()>=width){
			if(rect.left>0){
				x=-rect.left;
			}
			if(rect.right<width){
				x=width-rect.right;
			}
		}
		if(rect.height()>=height){
			if(rect.top>0){
				y=-rect.top;
			}
			if(rect.bottom<height){
				y=height-rect.bottom;
			}
		}
		if(rect.width()<width){
			x=width/2-rect.right+rect.width()/2;
		}
		if(rect.height()<height){
			y=height/2-rect.bottom+rect.height()/2;
		}
		scalematrix.postTranslate(x, y);
		
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {
		// TODO Auto-generated method stub
		
	}

	float x=0,y=0,x_last=0,y_last=0;
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		myscalegd.onTouchEvent(event);
		RectF rect=getmatrixrectf();
		switch(event.getAction()){
		case MotionEvent.ACTION_MOVE:
			if(rect.width()>getWidth()+0.1||rect.height()>getHeight()+0.1){
				this.getParent().requestDisallowInterceptTouchEvent(true);
			}else{
				this.getParent().requestDisallowInterceptTouchEvent(false);
			}
			x=event.getX();
			y=event.getY();
			float dx=x-x_last;
			float dy=y-y_last;
			x_last=x;
			y_last=y;
			if(ifmove(dx,dy)){
				RectF rect1=getmatrixrectf();
				if(getDrawable()!=null){
					ifcheckfr=ifchecktb=true;
					if(rect1.width()<getWidth()){
						ifcheckfr=false;
						dx=0;
					}
					if(rect1.height()<getHeight()){
						ifchecktb=false;
						dy=0;
					}
					
					scalematrix.postTranslate(dx, dy);
					checkboad();
					setImageMatrix(scalematrix);
				}
			}
			break;
		case MotionEvent.ACTION_DOWN:
			x_last=event.getX();
			y_last=event.getY();
			if(rect.width()>getWidth()+0.1||rect.height()>getHeight()+0.1){
				this.getParent().requestDisallowInterceptTouchEvent(true);
			}else{
				this.getParent().requestDisallowInterceptTouchEvent(false);
			}
			break;
		default:break;
		}
		return true;
	}

	public boolean ifmove(float dx,float dy){
		return Math.sqrt(dx*dx+dy*dy)>movestep;
	}
	
	public void xuanzhuan(float degree){
		scalematrix.postRotate(degree, getWidth()/2, getHeight()/2);
		setImageMatrix(scalematrix);
	}
	
	public void checkboad(){
		RectF rect=getmatrixrectf();
	    float x=0,y=0;
	    int width=getWidth();
	    int height=getHeight();
	    if(rect.top>0&&ifchecktb){
	    	y=-rect.top;
	    }
	    if(rect.bottom<height&&ifchecktb){
	    	y=height-rect.bottom;
	    }
	    if(rect.left>0&&ifcheckfr){
	    	x=-rect.left;
	    }
	    if(rect.right<width&&ifcheckfr){
	    	x=width-rect.right;
	    }
	    scalematrix.postTranslate(x, y);
	}
	
}
