package com.tuku.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.example.tukuduang.R;
import com.tuku.main.MyView.OnMeasureListener;
import com.tuku.main.NativeImageLoader.NativeImageCallBack;

public class VideoChildAdapter extends BaseAdapter {
	private Point mPoint = new Point(0, 0);//用来封装ImageView的宽和高的对象
	/**
	 * 用来存储图片的选中情况
	 */
	private HashMap<Integer, Boolean> mSelectMap = new HashMap<Integer, Boolean>();
	private GridView mGridView;
	private List<String> list;
	private List<Bitmap> list2;
	private List<String> list3;
	protected LayoutInflater mInflater;

	public VideoChildAdapter(Context context, List<String> list,List<Bitmap>list2, List<String>list3 ,GridView mGridView) {
		this.list = list;
		this.list2=list2;
		this.list3=list3;
		this.mGridView = mGridView;
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}


	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		String path = list.get(position);
		
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.videochildgrid, null);
			viewHolder = new ViewHolder();
			viewHolder.mImageView = (MyView) convertView.findViewById(R.id.videochild_image);
//			viewHolder.mCheckBox = (CheckBox) convertView.findViewById(R.id.videochild_checkbox);
			viewHolder.mTextView=(TextView)convertView.findViewById(R.id.videochild_name);
			String videoName=list3.get(position);
			//限制视频名字长度
			if(videoName.length()>10)
				videoName=videoName.substring(0,10);
			viewHolder.mTextView.setText(videoName);
			//用来监听ImageView的宽和高
			viewHolder.mImageView.setOnMeasureListener(new OnMeasureListener() {
				
				@Override
				public void onMeasureSize(int width, int height) {
					mPoint.set(width, height);
				}
			});
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
		}
		viewHolder.mImageView.setTag(path);
//		viewHolder.mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				//如果是未选中的CheckBox,则添加动画
//				if(!mSelectMap.containsKey(position) || !mSelectMap.get(position)){
////					addAnimation(viewHolder.mCheckBox);
//				}
//				mSelectMap.put(position, isChecked);
//			}
//		});
		
//		viewHolder.mCheckBox.setChecked(mSelectMap.containsKey(position) ? mSelectMap.get(position) : false);
		
		//利用NativeImageLoader类加载本地图片
		Bitmap bitmap = list2.get(position);		
		if(bitmap != null){
			viewHolder.mImageView.setImageBitmap(bitmap);
		}else{
			viewHolder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
		}
		
		return convertView;
	}
	

	public List<Integer> getSelectItems(){
		List<Integer> list = new ArrayList<Integer>();
		for(Iterator<Map.Entry<Integer, Boolean>> it = mSelectMap.entrySet().iterator(); it.hasNext();){
			Map.Entry<Integer, Boolean> entry = it.next();
			if(entry.getValue()){
				list.add(entry.getKey());
			}
		}
		
		return list;
	}
	
	
	public static class ViewHolder{
		public MyView mImageView;
//		public CheckBox mCheckBox;
		public TextView mTextView;
	}



}