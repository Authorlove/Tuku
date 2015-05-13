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

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;

import android.widget.GridView;

import android.widget.AdapterView.OnItemClickListener;

public class FirstFragment extends Fragment {
	private HashMap<String, List<String>> mGroupMap = new HashMap<String, List<String>>();  
	private List<ScanImageVedio> list=new ArrayList<ScanImageVedio>();
	private  static int SCANOK=1;//����ɨ��ɹ�����
//	private ProgressDialog mProgressDialog;//�������Ի���
	private final static int REQUEST_CODE=1;
//	private static Context context;
	public final static int RESULT_CODE=1;
	private GroupAdapter adapter;  //�ļ�����������ʵ��
    private GridView mGroupGridView; //�ļ�����ʾ��view
//    private Thread mThead=null;
    private int  sortway=0;//����ʽ
    
    
    
    public void setWay(int i){
    	sortway=i;
    }
    public int getWay()
    {
    	return sortway;
    }
    public void clearMap(){
    	 mGroupMap.clear();
    }
	
    private Handler mHandler = new Handler(){  
    	  
        @Override  
        public void handleMessage(Message msg) {  
        	
            super.handleMessage(msg);  
            switch (msg.what) {  
            case 0:  
            case 1:
            case 2:
                //�رս�����  
//                mProgressDialog.dismiss();  
                  
                adapter = new GroupAdapter(FirstFragment.this.getActivity().getApplicationContext(), list = subGroupOfImage(mGroupMap,msg.what), mGroupGridView);  
                mGroupGridView.setAdapter(adapter);  
                break;  
            }  
        }

		private List<ScanImageVedio> subGroupOfImage(HashMap<String, List<String>> mGroupMap,int way) {
			sortway=way;
			// TODO Auto-generated method stub
			
			if(mGroupMap.size() == 0){  
	            return null;  
	        }  
//			 List<Map.Entry<String, List<String>>> mHashMapEntryList=new ArrayList<Map.Entry<String,List<String>>>(mGroupMap.entrySet());   
//           if(way==0)
//           {
//			 Collections.sort(mHashMapEntryList, new Comparator<Map.Entry<String,List<String>>>() {
//               @Override
//               public int compare(Map.Entry<String,List<String>> firstMapEntry,Map.Entry<String,List<String>> secondMapEntry) {
//             	  	
//             	     return firstMapEntry.getKey().toLowerCase().compareTo(secondMapEntry.getKey().toLowerCase());
//             	
//             	}
//             
//             });
//           }
	        List<ScanImageVedio> alist = new ArrayList<ScanImageVedio>();  
	       // Iterator<Map.Entry<String, List<String>>> it = mHashMapEntryList.iterator();
	        Iterator<Map.Entry<String, List<String>>> it = mGroupMap.entrySet().iterator();  
	        while (it.hasNext()) {  
	            Map.Entry<String, List<String>> entry = it.next();  
	            ScanImageVedio mScanImageVedio = new ScanImageVedio();  
	            String key = entry.getKey();  
	            List<String> value = entry.getValue();  
	            if(key.equals("Camera"))
	            {
	            	System.out.println("camera:"+new File(value.get(0)).getParentFile().lastModified());
	            }
	            long parentTime=new File(value.get(0)).getParentFile().lastModified();
                mScanImageVedio.setLastModifiedTime(parentTime);
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
  
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.showgroup, container,
				false);
		
       
		mGroupGridView = (GridView)rootView.findViewById(R.id.id_mainGrid);  
		
		
       getImages(sortway);  //����ͼƬ
         
//       //�жϵ�����ļ���
       mGroupGridView.setOnItemClickListener(new OnItemClickListener() {  
 
           @Override  
           public void onItemClick(AdapterView<?> parent, View view,  
                   int position, long id) {  
               
                String folderName=list.get(position).getFolderName();
                List<String> childList = mGroupMap.get(folderName);  
               Intent mIntent = new Intent(FirstFragment.this.getActivity(), ShowImage.class);  
               mIntent.putStringArrayListExtra("data", (ArrayList<String>)childList);  //���ļ��а�����Ķ�����arraylist����ʽ����
//            mIntent.putExtra("sortway", sortway);
               startActivityForResult(mIntent,REQUEST_CODE);
                 
           }  
       });  
	
		return rootView;
	}
	 private void getImages(int way) {  
	        //��ʾ������  
//	        mProgressDialog = ProgressDialog.show(this, null, "���ڼ���...");  
	        SCANOK=way;
	       
	       
	        new Thread(new Runnable() {  
	              
	            @Override  
	            public void run() {  
	            	
	                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;  
	            
	                ContentResolver mContentResolver =FirstFragment.this.getActivity().getApplicationContext().getContentResolver();  
	  
	                Cursor mCursor = mContentResolver.query(mImageUri, null,  null,null,null);
	               
	                if(mCursor == null){  
	                    return;  
	                }  
	                 
	                while (mCursor.moveToNext()) {  
	                    //��ȡͼƬ��·��  
	                    String path = mCursor.getString(mCursor  
	                            .getColumnIndex(MediaColumns.DATA));  
	                      
	                    //��ȡ��ͼƬ�ĸ�·����  
	                    String parentName = new File(path).getParentFile().getName();  
	  
	                    
	                    //���ݸ�·������ͼƬ���뵽mGroupMap��  
	                    if (!mGroupMap.containsKey(parentName)) {  
	                        List<String> childList = new ArrayList<String>();  
	                        childList.add(path);  
	                        mGroupMap.put(parentName, childList);  
	                    } else {  
	                        mGroupMap.get(parentName).add(path);  
	                    }  
	                }  
	              
	               
	              
	                //֪ͨHandlerɨ��ͼƬ���  
	                mHandler.sendEmptyMessage(SCANOK);  
	                mCursor.close();  
	            	}
	            
	            
	        }).start();  
	         
	    }  
	 
	 
	   @Override
	    public void onActivityResult(int requestCode,int resultCode,Intent date){
			if(requestCode==REQUEST_CODE){
				if(resultCode==ShowImage.RESULT_CODE){
					mGroupMap.clear();
					 getImages(sortway);
				}
			}
		}
}
