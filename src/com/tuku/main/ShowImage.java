/*
 * TuKuDuang
 * 20150416
 * ������ʾ�ļ�����������ͼƬ������ͼ
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
		//��ʼ�����ͼƬ�ļ��к������ͼƬ����ͼ��������
		adapter = new ChildAdapter(this, list, mGridView);
		mGridView.setAdapter(adapter);
		//�жϵ����ͼƬ
		   //�жϵ�����ļ���
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