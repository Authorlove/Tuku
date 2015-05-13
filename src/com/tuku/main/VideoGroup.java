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
	
	//����һ���ļ������֣��ļ�����������ͼƬ������Ƶ�����ֵ�hashmap
	private HashMap<String, List<String>> mGruopMap = new HashMap<String, List<String>>();  
	
	private HashMap<String, List<Bitmap>> mThumbnailsMap = new HashMap<String, List<Bitmap>>();  
	private List<ScanImageVedio> list=new ArrayList<ScanImageVedio>();
	private final static int SCANOK=1;//����ɨ��ɹ�����
//	private ProgressDialog mProgressDialog;//�������Ի���
	
	private VideoGroupAdapter adapter;  //�ļ�����������ʵ��
    private GridView mGroupGridView; //�ļ�����ʾ��view
   
    
    private Handler mHandler = new Handler(){  
    	  
        @Override  
        public void handleMessage(Message msg) {  
            super.handleMessage(msg);  
            switch (msg.what) {  
            case SCANOK:  
                //�رս�����  
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
    
        getImages();  //����ͼƬ
          
        //�жϵ�����ļ���
        mGroupGridView.setOnItemClickListener(new OnItemClickListener() {  
  
            @Override  
            public void onItemClick(AdapterView<?> parent, View view,  
                    int position, long id) {  
                List<String> childList = mGruopMap.get(list.get(position).getFolderName());  
                
                Intent mIntent = new Intent(VideoGroup.this, ShowVideo.class);  
                mIntent.putStringArrayListExtra("data", (ArrayList<String>)childList);
               //���ļ��а�����Ķ�����arraylist����ʽ����
                startActivity(mIntent);  
                  
            }  
        });  
	}

	 /** 
     * ����ContentProviderɨ���ֻ��е�ͼƬ���˷��������������߳��� 
     */  
    private void getImages() {  
        //��ʾ������  
//        mProgressDialog = ProgressDialog.show(this, null, "���ڼ���...");  
          
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
                    //��ȡ��Ƶ��·��  
                    String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DATA));  
                    long origId=mCursor.getLong(mCursor.getColumnIndex("_ID"));//��ȡID
                     Bitmap bitmap=MediaStore.Video.Thumbnails.getThumbnail(mContentResolver, origId,Video.Thumbnails.MICRO_KIND, null);//��ȡ����ͼ
                    //��ȡ����Ƶ�ĸ�·����  
                    String parentName = new File(path).getParentFile().getName();  
  
                      
                    //���ݸ�·��������Ƶ���뵽mGruopMap��  
                    if (!mGruopMap.containsKey(parentName)) {  
                        List<String> childList = new ArrayList<String>();  
                        childList.add(path);  
                        mGruopMap.put(parentName, childList);  
                    } else {  
                        mGruopMap.get(parentName).add(path);  
                    }  
                    //���ݸ�·����������ͼ����mThumbnailMap��
                    if (!mThumbnailsMap.containsKey(parentName)) {  
                        List<Bitmap> childList = new ArrayList<Bitmap>();  
                        childList.add(bitmap);  
                        mThumbnailsMap.put(parentName, childList);  
                    } else {  
                    	mThumbnailsMap.get(parentName).add(bitmap);  
                    }  
                }  
                  
                  
                //֪ͨHandlerɨ��ͼƬ���  
                mHandler.sendEmptyMessage(SCANOK);  
                mCursor.close();  
            
            }  
        }).start();  
          
    }  
}
