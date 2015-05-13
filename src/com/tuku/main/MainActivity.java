package com.tuku.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.example.tukuduang.R;
import com.tuku.picturesearch.PicSearch;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	private ViewPager viewPager;
	private ActionBar actionBar;
	private int mType;
	FirstFragment fr1=new FirstFragment();
	SecondFragment fr2=new SecondFragment();
	private int REQUEST_CODE=1;
	private Fragment fragment1 ;
	private Fragment fragment2 ;
//	private Button mBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		viewPager = (ViewPager) findViewById(R.id.id_mainviewpager);
	
	 fragment1=fr1;
	 fragment2=fr2;
//		Fragment fragment3 = new ThirdFragment();
		Fragment[] fragmentArray = new Fragment[] { fragment1, fragment2,
				 };
		FFragmentPagerAdapter adapter = new FFragmentPagerAdapter(
				getSupportFragmentManager(), fragmentArray);
		
		viewPager.setAdapter(adapter);
		viewPager.setOffscreenPageLimit(2);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
//				System.out.println("arg0:" + arg0);
				actionBar.setSelectedNavigationItem(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
		
		actionBar = getActionBar();
		  
		// 设置ActionBar 的导航方式: Tab导航
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		Tab tab1 = actionBar.newTab().setText("图片")
				
				.setTabListener(new ActionTabListener(fragment1));
		
		
		Tab tab2 = actionBar.newTab().setText("视频")
				
				.setTabListener(new ActionTabListener(fragment2));

		
		actionBar.addTab(tab1);
		actionBar.addTab(tab2);
}

	class ActionTabListener implements ActionBar.TabListener {



		private Fragment fragment;

		public ActionTabListener(Fragment fragment) {
			this.fragment = fragment;
		}

		@Override
		public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
			// TODO Auto-generated method stub
			
			mType = tab.getPosition();
			System.out.println("tab.getPosition():" + tab.getPosition());
			viewPager.setCurrentItem(tab.getPosition());
			invalidateOptionsMenu();
		}

		@Override
		public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
			// TODO Auto-generated method stub

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub

		System.out.println("当前mType:" + mType);
		menu.clear();
		MenuInflater inflater = this.getMenuInflater();
		switch (mType) {
		case 0:
			inflater.inflate(R.menu.main, menu);
			break;

		case 1:
//			inflater.inflate(R.menu.videomenu, menu);
			
			break;

		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		Fragment[] fragmentArray;
		FFragmentPagerAdapter adapter;
		switch (item.getItemId()) {
		case R.id.action_rank_name:
			fr1.clearMap();fr2.clearMap();
			fr1.setWay(0);
			fragment1=fr1;
			fragmentArray = new Fragment[] { fragment1, fragment2,
			 };
			adapter = new FFragmentPagerAdapter(
			getSupportFragmentManager(), fragmentArray);
	
	      viewPager.setAdapter(adapter);
			break;
		case R.id.action_rank_num:
			fr1.clearMap();fr2.clearMap();
			fr1.setWay(1);
			fragment1=fr1;
			 fragmentArray = new Fragment[] { fragment1, fragment2,
			 };
	        adapter = new FFragmentPagerAdapter(
			getSupportFragmentManager(), fragmentArray);
	
	      viewPager.setAdapter(adapter);
			break;
		case R.id.action_rank_time:
			fr1.clearMap();fr2.clearMap();
			fr1.setWay(2);
			fragment1=fr1;
			 fragmentArray = new Fragment[] { fragment1, fragment2,
			 };
	        adapter = new FFragmentPagerAdapter(
			getSupportFragmentManager(), fragmentArray);
	
	      viewPager.setAdapter(adapter);
			break;
		case R.id.action_search:
			Intent mIntent=new Intent(MainActivity.this,PicSearch.class);
			startActivity(mIntent);
//			Toast.makeText(this, "点击了第一个按钮", Toast.LENGTH_SHORT).show();
			break;
		case R.id.action_add:
			Intent mIntent2=new Intent(MainActivity.this,TakePic.class);
			mIntent2.putExtra("sortway", fr1.getWay());
			startActivityForResult(mIntent2,REQUEST_CODE);
			break;
//		case R.id.video_action_rank_name:
////			fr2.clearMap();
////			fr2.setWay(0);
////			fragment2=fr2;
////			 fragmentArray = new Fragment[] { fragment1, fragment2,
////			 };
////	        adapter = new FFragmentPagerAdapter(
////			getSupportFragmentManager(), fragmentArray);
////	
////	      viewPager.setAdapter(adapter);
//			break;
//		case R.id.video_action_rank_num:
////			fr2.clearMap();
////			fr2.setWay(1);
////			fragment2=fr2;
////			 fragmentArray = new Fragment[] { fragment1, fragment2,
////			 };
////	        adapter = new FFragmentPagerAdapter(
////			getSupportFragmentManager(), fragmentArray);
////	
////	      viewPager.setAdapter(adapter);
//			break;
//		case R.id.video_action_rank_time:
////			fr2.clearMap();
////			fr2.setWay(2);
////			
////			fragment2=fr2;
////			 fragmentArray = new Fragment[] { fragment2,fragment1};
////	        adapter = new FFragmentPagerAdapter(
////			getSupportFragmentManager(), fragmentArray);
////	
////	      viewPager.setAdapter(adapter);
	     
//			break;
		}

		return super.onOptionsItemSelected(item);
	}
//	@Override 
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
//        // TODO Auto-generated method stub  
//        super.onActivityResult(requestCode, resultCode, data);  
//        if(resultCode==1 ) 
//        { 
//        	Fragment[] fragmentArray;
//    		FFragmentPagerAdapter adapter;
//        	fr1.clearMap();
//        	int way=data.getIntExtra("sortway",0);
//        	Log.i("MainActivity",way+"!");
//			fr1.setWay(way);
//			fragment1=fr1;
//			fragmentArray = new Fragment[] { fragment1, fragment2,
//			 };
//			adapter = new FFragmentPagerAdapter(
//			getSupportFragmentManager(), fragmentArray);
//	
//	      viewPager.setAdapter(adapter);
//        } 
//        super.onActivityResult(requestCode, resultCode, data); 
//    }  
}
