package com.tuku.edit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class Lvjing_picture extends Activity{
	
	public final static int RESULT_CODE=1;
	Lvjing_surfaceview surfaceview;
	String bgbmproot=new String();
	StringBuffer saveroot;
	Bitmap bgbmp,savebit;
	int width ;
    int height ;
    Button save;
    String name="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		WindowManager wm = this.getWindowManager();
	    width = wm.getDefaultDisplay().getWidth();
	    height = wm.getDefaultDisplay().getHeight();
	    this.setContentView(com.example.tukuduang.R.layout.lvjing_layout);
	    surfaceview=(Lvjing_surfaceview) findViewById(com.example.tukuduang.R.id.lvjingsurface);
	    Intent in=getIntent();
		bgbmproot=in.getStringExtra("bgbmproot");
		bgbmp=BitmapFactory.decodeFile(bgbmproot);
		surfaceview.setbgbmp(bgbmp);
		save=(Button) findViewById(com.example.tukuduang.R.id.save);
		save.setText(com.example.tukuduang.R.string.save);
		save.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				savebit=surfaceview.getbgbmp();
				saveroot=new StringBuffer(bgbmproot);
				int last=saveroot.lastIndexOf("/")+1;
				String root=saveroot.substring(0,last);
				long time=System.currentTimeMillis();
				name=new String(root+time+".jpeg");
				try {
					FileOutputStream f=new FileOutputStream(new File(name));
					savebit.compress(Bitmap.CompressFormat.JPEG, 100, f); 
					f.flush();
					f.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block	
					Log.e("lvjing", "File not found: " + name);
					e.printStackTrace();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				MediaScannerConnection.scanFile(getactivity(), new String[] { name }, null, 
					new OnScanCompletedListener() {
	                @Override
	                public void onScanCompleted(String path, Uri uri) {
	                    save();
	                }
	            });
			}
		});
	}
	
	private Lvjing_picture getactivity(){
		return this;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, 0, 0, com.example.tukuduang.R.string.yuantu);
		menu.add(0, 1, 0, com.example.tukuduang.R.string.bingdong);
		menu.add(0, 2, 0, com.example.tukuduang.R.string.heibai);
		menu.add(0, 3, 0, com.example.tukuduang.R.string.huidu);
		return super.onCreateOptionsMenu(menu);
	}



	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case 0:surfaceview.lei="yuantu";break;
		case 1:surfaceview.lei="bingdong";break;
		case 2:surfaceview.lei="heibai";break;
		case 3:surfaceview.lei="huidu";break;
		default:break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

    public void save(){
    	Intent in=new Intent();
		in.putExtra("bgbmproot", name);
		this.setResult(RESULT_CODE,in);
		finish();
    }

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}
	

}
