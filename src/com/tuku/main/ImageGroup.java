/*
 * TuKuDuang
 * 20150416
 * 扫描手机里所有的多媒体文件，并以文件夹的形式显示出来
 */

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
import com.tuku.edit.Bianji_picture;


import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;  
public class ImageGroup  extends Activity{
	
	//定义一个文件夹名字，文件夹里面所有图片或者视频的名字的hashmap
	private HashMap<String, List<String>> mGroupMap = new HashMap<String, List<String>>();  
	private List<ScanImageVedio> list=new ArrayList<ScanImageVedio>();
	private  static int SCANOK=1;//定义扫描成功变量
//	private ProgressDialog mProgressDialog;//进度条对话框
	private final static int REQUEST_CODE=1;
	 
	public final static int RESULT_CODE=1;
	private GroupAdapter adapter;  //文件夹适配器类实例
    private GridView mGroupGridView; //文件夹显示的view
    private Thread mThead=null;
    private int  sortway=0;//排序方式
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
                  
                adapter = new GroupAdapter(ImageGroup.this, list = subGroupOfImage(mGroupMap,msg.what), mGroupGridView);  
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showgroup);
		 // 生成一个SpinnerAdapter
        SpinnerAdapter adapter = ArrayAdapter.createFromResource(this, R.array.sortway, android.R.layout.simple_spinner_dropdown_item);
        // 得到ActionBar
        ActionBar actionBar = getActionBar();
      
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setListNavigationCallbacks(adapter, new OnNavigationListener() {
 
            @Override
            public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                // TODO Auto-generated method stub
            	switch(itemPosition)
            	{
            	case 0:
            		mGroupMap.clear();
            		getImages(0);
            		break;
            	case 1:
            		mGroupMap.clear();
            		getImages(1);
            		break;
            	case 2:
            		mGroupMap.clear();
            		getImages(2);
            		break;
            	
            	}
                return false;
            }
             
        });
        
		mGroupGridView = (GridView) findViewById(R.id.id_mainGrid);  
		
		
        getImages(0);  //加载图片
          
        //判断点击的文件夹
        mGroupGridView.setOnItemClickListener(new OnItemClickListener() {  
  
            @Override  
            public void onItemClick(AdapterView<?> parent, View view,  
                    int position, long id) {  
                
                 String folderName=list.get(position).getFolderName();
                 List<String> childList = mGroupMap.get(folderName);  
                Intent mIntent = new Intent(ImageGroup.this, ShowImage.class);  
                mIntent.putStringArrayListExtra("data", (ArrayList<String>)childList);  //将文件夹唉里面的东西以arraylist的形式传递
//                mIntent.putExtra("foldername", folderName);
                startActivityForResult(mIntent,REQUEST_CODE);
                  
            }  
        });  
	}

	 /** 
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 
     */  
    private void getImages(int way) {  
        //显示进度条  
//        mProgressDialog = ProgressDialog.show(this, null, "正在加载...");  
        SCANOK=way;
       
       
        new Thread(new Runnable() {  
              
            @Override  
            public void run() {  
            	
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;  
            
                ContentResolver mContentResolver =ImageGroup.this.getContentResolver();  
  
                Cursor mCursor = mContentResolver.query(mImageUri, null,  null,null,null);
               
                if(mCursor == null){  
                    return;  
                }  
                 
                while (mCursor.moveToNext()) {  
                    //获取图片的路径  
                    String path = mCursor.getString(mCursor  
                            .getColumnIndex(MediaColumns.DATA));  
                      
                    //获取该图片的父路径名  
                    String parentName = new File(path).getParentFile().getName();  
  
                    
                    //根据父路径名将图片放入到mGroupMap中  
                    if (!mGroupMap.containsKey(parentName)) {  
                        List<String> childList = new ArrayList<String>();  
                        childList.add(path);  
                        mGroupMap.put(parentName, childList);  
                    } else {  
                        mGroupMap.get(parentName).add(path);  
                    }  
                }  
              
               
              
                //通知Handler扫描图片完成  
                mHandler.sendEmptyMessage(SCANOK);  
                mCursor.close();  
            	}
            
            
        }).start();  
         
    }  
    @Override  
    public boolean onCreateOptionsMenu(Menu menu) {  
        // Inflate the menu; this adds items to the action bar if it is present.  
        getMenuInflater().inflate(R.menu.main, menu);  
        return true;  
    }  
  
    @Override  
    public boolean onOptionsItemSelected(MenuItem item) {  
        switch (item.getItemId()) {  
       
        
    
        case R.id.action_search:  
            Toast.makeText(this, "Menu Item search selected",  
                    Toast.LENGTH_SHORT).show();  
            break;  
        case R.id.action_add:  
            Toast.makeText(this, "Menu Item  settings selected",  
                    Toast.LENGTH_SHORT).show();  
            break;  
            
        
        	
        default:  
            break;  
        }  
        return super.onOptionsItemSelected(item);  
    }  
    @Override
	protected void onActivityResult(int requestCode,int resultCode,Intent date){
		if(requestCode==REQUEST_CODE){
			if(resultCode==ShowImage.RESULT_CODE){
				mGroupMap.clear();
				 getImages(sortway);
			}
		}
	}


	
  
}
