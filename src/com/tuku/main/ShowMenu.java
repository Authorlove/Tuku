package com.tuku.main;

import java.util.ArrayList;
import java.util.List;

import com.example.tukuduang.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

public class ShowMenu extends Activity{
	private Button myPicBtn;
	private Button myVideoBtn;
	private Button takePicBtn;
	private Button takeVideoBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainview);
		
		myPicBtn=(Button)findViewById(R.id.id_mypicbtn);
		myVideoBtn=(Button)findViewById(R.id.id_myvideobtn);
		takePicBtn=(Button)findViewById(R.id.id_takepicbtn);
		takeVideoBtn=(Button)findViewById(R.id.id_takevideobtn);
		myPicBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent mIntent=new Intent(ShowMenu.this,ImageGroup.class);
			    startActivity(mIntent);
			}
			
		});
		myVideoBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent mIntent=new Intent(ShowMenu.this,VideoGroup.class);
			    startActivity(mIntent);
			}
			
		});
	}
}
