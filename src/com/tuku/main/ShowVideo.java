/*
 * TuKuDuang
 * 20150416
 * 用于显示文件夹下面所有图片的缩略图
 */

package com.tuku.main;
import java.util.ArrayList;
import java.util.List;

import com.example.tukuduang.R;
import com.tuku.video.PlayVideo;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

public class ShowVideo extends Activity {
	private GridView mGridView;
	private List<String> list;
	private List<Bitmap> list2;//存视频缩略图
	private List<String> list3;//存视频名称
	private VideoChildAdapter adapter;
    private Context context;
    private final static int REQUEST_CODE=1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showimage);
		
		mGridView = (GridView) findViewById(R.id.child_grid);
		list = getIntent().getStringArrayListExtra("data");
		list2=new ArrayList<Bitmap>();
		list3=new ArrayList<String>();
		for(int i=0;i<list.size();i++)
		{
			String fileName=list.get(i);
			String whereClause = MediaStore.Video.Media.DATA + " = '"+ fileName + "'";
			System.out.println(whereClause);
			Uri mVideoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;  
	        ContentResolver mContentResolver =ShowVideo.this.getContentResolver();  
	        Cursor mCursor=mContentResolver.query(mVideoUri, null,whereClause,null,null);
	       //cursor从-1开始
	        if(mCursor.moveToFirst())
	        {
	        	String videoName=mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
	        	list3.add(videoName);
	        	int origId=mCursor.getInt(mCursor.getColumnIndex(MediaStore.Video.Media._ID));//获取ID
				Log.i("videoName",videoName);
	            
		        Bitmap bitmap=MediaStore.Video.Thumbnails.getThumbnail(mContentResolver,origId,Images.Thumbnails.MICRO_KIND, null );//获取缩略图
			       if(bitmap==null)
			    	   System.out.println("bitmap is null");
			       else
					list2.add(bitmap);
	        }
			

		}
	
		//初始化点击图片文件夹后的所有图片缩略图的适配器
		adapter = new VideoChildAdapter(this, list, list2,list3,mGridView);
		mGridView.setAdapter(adapter);
		//判断点击的图片
		   //判断点击的文件夹
		mGridView.setOnItemClickListener(new OnItemClickListener() {  
  
            @Override  
            public void onItemClick(AdapterView<?> parent, View view,  
                    int position, long id) {  
                
            	 Intent mIntent = new Intent( ShowVideo.this, PlayVideo.class);  
                 mIntent.putStringArrayListExtra("data", (ArrayList<String>)list);  
                 mIntent.putExtra("position", position);
                 startActivity(mIntent);
            }  
        });  
		
	}

//	@Override
//	public void onBackPressed() {
//		Toast.makeText(this, "选中 " + adapter.getSelectItems().size() + " item", Toast.LENGTH_LONG).show();
//		super.onBackPressed();
//	}
//	

//	@Override
//	protected void onActivityResult(int requestCode,int resultCode,Intent date){
//		if(requestCode==REQUEST_CODE){
//			if(resultCode==PlayVideo.RESULT_CODE){
//				list=date.getStringArrayListExtra("date");
//				adapter = new ChildAdapter(this, list, mGridView);
//				mGridView.setAdapter(adapter);
//			}
//		}
//	}

	
}