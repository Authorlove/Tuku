/*
 * TuKuDuang
 * 20150416
 * 用于显示文件夹下面所有图片的缩略图
 */

package com.tuku.main;
import java.util.ArrayList;
import java.util.List;

import com.example.tukuduang.R;
import com.tuku.edit.Bianji_picture;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

public class ShowImage extends Activity {
	private GridView mGridView;
	private List<String> list;
	private int sortway;
	private ChildAdapter adapter;
    private Context context;
    private final static int REQUEST_CODE=1;
  
	public final static int RESULT_CODE=1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showimage);
		
		mGridView = (GridView) findViewById(R.id.child_grid);
		list = getIntent().getStringArrayListExtra("data");
//		folderName=getIntent().getStringExtra("foldername");
		sortway=getIntent().getIntExtra("sortway",0);
		//初始化点击图片文件夹后的所有图片缩略图的适配器
		adapter = new ChildAdapter(this, list, mGridView);
		mGridView.setAdapter(adapter);
		//判断点击的图片
		   //判断点击的文件夹
		mGridView.setOnItemClickListener(new OnItemClickListener() {  
  
            @Override  
            public void onItemClick(AdapterView<?> parent, View view,  
                    int position, long id) {  
                
            	 Intent mIntent = new Intent(ShowImage.this, Bianji_picture.class);  
                 mIntent.putStringArrayListExtra("data", (ArrayList<String>)list);  
                 mIntent.putExtra("position", position);
                 startActivityForResult(mIntent,REQUEST_CODE);
            }  
        });  
		
	}

	@Override
	public void onBackPressed() {
		Intent in=new Intent();
//		in.putStringArrayListExtra("returnlist", (ArrayList<String>) list);
//		in.putExtra("sortway",sortway);
		this.setResult(RESULT_CODE,in);
		super.onBackPressed();
		finish();
	}


	@Override
	protected void onActivityResult(int requestCode,int resultCode,Intent date){
		if(requestCode==REQUEST_CODE){
			if(resultCode==Bianji_picture.RESULT_CODE){
				list=date.getStringArrayListExtra("date");
				adapter = new ChildAdapter(this, list, mGridView);
				mGridView.setAdapter(adapter);
			}
		}
	}

	
}