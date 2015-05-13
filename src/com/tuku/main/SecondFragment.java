package com.tuku.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.example.tukuduang.R;

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
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

public class SecondFragment extends Fragment {
private HashMap<String, List<String>> mGruopMap = new HashMap<String, List<String>>();  
	
	private HashMap<String, List<Bitmap>> mThumbnailsMap = new HashMap<String, List<Bitmap>>();  
	private List<ScanImageVedio> list=new ArrayList<ScanImageVedio>();
	private static int SCANOK=1;//定义扫描成功变量
//	private ProgressDialog mProgressDialog;//进度条对话框
	
	private VideoGroupAdapter adapter;  //文件夹适配器类实例
    private GridView mGroupGridView; //文件夹显示的view
   
    private int sortway=0;
    
   
    private Handler mHandler = new Handler(){  
    	  
        @Override  
        public void handleMessage(Message msg) {  
            super.handleMessage(msg);  
            switch (msg.what) {  
            case 0:
            case 1:
            case 2:
                //关闭进度条  
//                mProgressDialog.dismiss();  
                  
                adapter = new VideoGroupAdapter(SecondFragment.this.getActivity(), list = subGroupOfImage(mGruopMap,mThumbnailsMap,msg.what), mGroupGridView);  
                mGroupGridView.setAdapter(adapter);  
                break;  
            }  
        }

		private List<ScanImageVedio> subGroupOfImage(HashMap<String, List<String>> mGruopMap,HashMap<String, List<Bitmap>> mThumbnailsMap,int way) {
			
			// TODO Auto-generated method stub
			
			if(mGruopMap.size() == 0){  
	            return null;  
	        }
			sortway=way;
	        List<ScanImageVedio> alist = new ArrayList<ScanImageVedio>();  
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
	              
	            alist.add(mScanImageVedio);  
	        }  
	        Collections.sort(alist, new Comparator<ScanImageVedio>() {

				@Override
				public int compare(ScanImageVedio arg0, ScanImageVedio arg1) {
					// TODO Auto-generated method stub
					switch(sortway)
					{
					case 0:
						return arg0.getFolderName().toLowerCase().compareTo(arg1.getFolderName().toLowerCase());
					case 1:
						return arg1.getImageNum()-arg0.getImageNum();
					case 2:
						return (int)(arg1.getLastModifiedTime()-arg0.getLastModifiedTime());
					}
					
					return 0;
				}
	              
	             
	             });
	        return alist;  
		
		}  
          
    };  
    public void setWay(int i){
    	sortway=i;
    }
    public void clearMap(){
    	mGruopMap.clear();
    	mThumbnailsMap.clear();
    }
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.showgroup, container,
				false);
		mGroupGridView = (GridView)  rootView.findViewById(R.id.id_mainGrid);  
	    
        getImages(sortway);  //加载图片
          
        //判断点击的文件夹
        mGroupGridView.setOnItemClickListener(new OnItemClickListener() {  
  
            @Override  
            public void onItemClick(AdapterView<?> parent, View view,  
                    int position, long id) {  
                List<String> childList = mGruopMap.get(list.get(position).getFolderName());  
                
                Intent mIntent = new Intent(SecondFragment.this.getActivity(), ShowVideo.class);  
                mIntent.putStringArrayListExtra("data", (ArrayList<String>)childList);
               //将文件夹唉里面的东西以arraylist的形式传递
                startActivity(mIntent);  
                  
            }  
        });  
		return rootView;
	}
	 private void getImages(int way) {  
	        //显示进度条  
//	        mProgressDialog = ProgressDialog.show(this, null, "正在加载...");  
		 SCANOK=way;
	        new Thread(new Runnable() {  
	              
	            @Override  
	            public void run() {  
	               
	                Uri mVideoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;  
	                ContentResolver mContentResolver =SecondFragment.this.getActivity().getContentResolver();  
	  
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
