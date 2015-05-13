package com.tuku.edit;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class Lvjing_thread extends Thread{
	
	private boolean flag=true;
	private int sleep=100;
	Lvjing_surfaceview surfaceview;
	SurfaceHolder surfaceholder;
	public Lvjing_thread(Lvjing_surfaceview surfaceview){
	    this.surfaceview=surfaceview;
	    this.surfaceholder=surfaceview.getHolder();
	}
	@Override
	public void run(){
		Canvas c;
		while(this.flag){
	        c=null;
		    try{
		    	c=this.surfaceholder.lockCanvas(null);
		    	synchronized(this.surfaceholder){
		    		surfaceview.leixing();
	    			surfaceview.draw(c);
			    }
		    }finally{
			    if(c!=null){
				    this.surfaceholder.unlockCanvasAndPost(c);
			    }
		    }
		    try{
		    	Thread.sleep(sleep);
		    }catch(Exception e){
		    	e.printStackTrace();
		    }
		}
	}
	public void setFlag(boolean flag){
		this.flag=flag;
	}

}
