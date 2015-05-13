package com.tuku.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import com.example.tukuduang.R;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

public class TakePic extends Activity{
	  private final static int GET_IMAGE_VIA_CAMERA=1;
	  private String localTempImgDir="TuKuDuang";
	  private String localTempImgFileName;
	  private ImageView view;  
	  private String TAG="TakePic";
	  private int getway;
	  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_picture_activity);
		view=(ImageView)findViewById(R.id.large_view);
		
		getway=getIntent().getIntExtra("sortway", 0);
		Log.i(TAG,getway+"!");
		String status=Environment.getExternalStorageState(); 
		if(status.equals(Environment.MEDIA_MOUNTED)) 
		{ 
		try { 
		File dir=new File(Environment.getExternalStorageDirectory() + "/"+localTempImgDir); 
		if(!dir.exists())dir.mkdirs(); 

		Intent intent=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
		localTempImgFileName=UUID.randomUUID().toString()+"jpg";
		Log.i(TAG,localTempImgFileName);
		File f=new File(dir, localTempImgFileName);//localTempImgDir和localTempImageFileName是自己定义的名字 
		Uri u=Uri.fromFile(f); 
		intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0); 
		intent.putExtra(MediaStore.EXTRA_OUTPUT, u); 
		startActivityForResult(intent, GET_IMAGE_VIA_CAMERA); 
		} catch (ActivityNotFoundException  e) { 
		// TODO Auto-generated catch block 
		Toast.makeText( TakePic.this, "没有找到储存目录",Toast.LENGTH_LONG).show();  
		} 
		}else{ 
		Toast.makeText( TakePic.this, "没有储存卡",Toast.LENGTH_LONG).show(); 
		} 
		
		
	
  } 
	
	@Override 
    public void onConfigurationChanged(Configuration config) { 
    super.onConfigurationChanged(config); 
    } 
	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        // TODO Auto-generated method stub  
        super.onActivityResult(requestCode, resultCode, data);  
        if(resultCode==RESULT_OK ) 
        { 
        switch(requestCode) 
        { 
        case GET_IMAGE_VIA_CAMERA: 
        File f=new File(Environment.getExternalStorageDirectory() 
        +"/"+localTempImgDir+"/"+localTempImgFileName); 
        try { 
           Uri u = 
           Uri.parse(android.provider.MediaStore.Images.Media.insertImage(getContentResolver(), 
           f.getAbsolutePath(), null, null)); 
           try {
			Bitmap bitmap=MediaStore.Images.Media.getBitmap(this.getContentResolver(), u);
			view.setImageBitmap(bitmap);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
           Log.i(TAG,u.toString()); 
        } catch (FileNotFoundException e) { 
           // TODO Auto-generated catch block 
           e.printStackTrace(); 
        } 
        break; 
        } 
        } 
        super.onActivityResult(requestCode, resultCode, data); 
    }  
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent in=new Intent();
		in.putExtra("sortway", getway);
		Log.i(TAG,getway+"!");
		this.setResult(1,in);
		super.onBackPressed();
		finish();
	}
}
