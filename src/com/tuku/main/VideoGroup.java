package com.tuku.main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.example.tukuduang.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Video;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

public class VideoGroup extends Activity{
	
	//定义一个文件夹名字，文件夹里面所有图片或者视频的名字的hashmap
	private HashMap<String, List<String>> mGruopMap = new HashMap<String, List<String>>();  
	
	private HashMap<String, List<Bitmap>> mThumbnailsMap = new HashMap<String, List<Bitmap>>();  
	private List<ScanImageVedio> list=new ArrayList<ScanImageVedio>();
	private final static int SCANOK=1;//定义扫描成功变量
//	private ProgressDialog mProgressDialog;//进度条对话框
	
	private VideoGroupAdapter adapter;  //文件夹适配器类实例
    private GridView mGroupGridView; //文件夹显示的view
   
    
    private Handler mHandler = new Handler(){  
    	  
        @Override  
        public void handleMessage(Message msg) {  
            super.handleMessage(msg);  
            switch (msg.what) {  
            case SCANOK:  
                //关闭进度条  
//                mProgressDialog.dismiss();  
                  
                adapter = new VideoGroupAdapter(VideoGroup.this, list = subGroupOfImage(mGruopMap,mThumbnailsMap), mGroupGridView);  
                mGroupGridView.setAdapter(adapter);  
                break;  
            }  
        }

		private List<ScanImageVedio> subGroupOfImage(HashMap<String, List<String>> mGruopMap,HashMap<String, List<Bitmap>> mThumbnailsMap) {
			
			// TODO Auto-generated method stub
			
			if(mGruopMap.size() == 0){  
	            return null;  
	        }  
	        List<ScanImageVedio> list = new ArrayList<ScanImageVedio>();  
	        Iterator<Map.Entry<String, List<Bitmap>>> it2 = mThumbnailsMap.entrySet().iterator();  
	        Iterator<Map.Entry<String, List<String>>> it = mGruopMap.entrySet().iterator();  
	        while (it.hasNext()) {  
	            Map.Entry<String, List<String>> entry = it.next(); 
	            Map.Entry<String,  List<Bitmap>> entry2 = it2.next(); 
	            
	            ScanImageVedio mScanImageVedio = new ScanImageVedio();  
	            String key = entry.getKey();  
	            List<String> value = entry.getValue();  
	            mScanImageVedio.setVideoBitmap(entry2.getValue().get(0));
	            mScanImageVedio.setFolderName(key);  
	            mScanImageVedio.setImageNum(value.size());  
	            mScanImageVedio.setFirstImagePath(value.get(0));
	              
	            list.add(mScanImageVedio);  
	        }  
	          
	        return list;  
		
		}  
          
    };  
  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showgroup);
		mGroupGridView = (GridView) findViewById(R.id.id_mainGrid);  
    
        getImages();  //加载图片
          
        //判断点击的文件夹
        mGroupGridView.setOnItemClickListener(new OnItemClickListener() {  
  
            @Override  
            public void onItemClick(AdapterView<?> parent, View view,  
                    int position, long id) {  
                List<String> childList = mGruopMap.get(list.get(position).getFolderName());  
                
                Intent mIntent = new Intent(VideoGroup.this, ShowVideo.class);  
                mIntent.putStringArrayListExtra("data", (ArrayList<String>)childList);
               //将文件夹唉里面的东西以arraylist的形式传递
                startActivity(mIntent);  
                  
            }  
        });  
	}

	 /** 
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 
     */  
    private void getImages() {  
        //显示进度条  
//        mProgressDialog = ProgressDialog.show(this, null, "正在加载...");  
          
        new Thread(new Runnable() {  
              
            @Override  
            public void run() {  
               
                Uri mVideoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;  
                ContentResolver mContentResolver =VideoGroup.this.getContentResolver();  
  
                 Cursor mCursor=mContentResolver.query(mVideoUri, null,null,null,null);  
                if(mCursor == null){  
                    return;  
                }  
                 
             
               
                while (mCursor.moveToNext()) {  
                    //获取视频的路径  
                    String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DATA));  
                    long origId=mCursor.getLong(mCursor.getColumnIndex("_ID"));//获取ID
                     Bitmap bitmap=MediaStore.Video.Thumbnails.getThumbnail(mContentResolver, origId,Video.Thumbnails.MICRO_KIND, null);//获取缩略图
                    //获取该视频的父路径名  
                    String parentName = new File(path).getParentFile().getName();  
  
                      
                    //根据父路径名将视频放入到mGruopMap中  
                    if (!mGruopMap.containsKey(parentName)) {  
                        List<String> childList = new ArrayList<String>();  
                        childList.add(path);  
                        mGruopMap.put(parentName, childList);  
                    } else {  
                        mGruopMap.get(parentName).add(path);  
                    }  
                    //根据父路径名将缩略图存在mThumbnailMap中
                    if (!mThumbnailsMap.containsKey(parentName)) {  
                        List<Bitmap> childList = new ArrayList<Bitmap>();  
                        childList.add(bitmap);  
                        mThumbnailsMap.put(parentName, childList);  
                    } else {  
                    	mThumbnailsMap.get(parentName).add(bitmap);  
                    }  
                }  
                  
                  
                //通知Handler扫描图片完成  
                mHandler.sendEmptyMessage(SCANOK);  
                mCursor.close();  
            
            }  
        }).start();  
          
    }  
}
