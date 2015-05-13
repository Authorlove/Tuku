package com.tuku.main;


import java.util.List;

import com.example.tukuduang.R;
import com.tuku.main.MyView.OnMeasureListener;
import com.tuku.main.NativeImageLoader.NativeImageCallBack;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Video;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
 

public class GroupAdapter extends BaseAdapter{
	private List<ScanImageVedio> list;
	private Point mPoint = new Point(0, 0);//用来封装ImageView的宽和高的对象
	private GridView mGridView;
	protected LayoutInflater mInflater;
	
	public GroupAdapter(Context context, List<ScanImageVedio> list, GridView mGridView){
		this.list = list;
		this.mGridView = mGridView;
		mInflater = LayoutInflater.from(context);//layoutInflater controller
	}
	@Override
	public int getCount() {
		//If there is no pictures,the list is a null object.(如果相册没有图片，则list不能直接调用其实例方法，否则会NPE异常)
		if(list==null)
			return 0;
		else
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
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;//user-defined inner class,has members:imageView,2 textViews(用户自定义内部类,有一个图片控件和两个文字控件)
		ScanImageVedio mScanImageVedio = list.get(position);
		String path = mScanImageVedio.getFirstImagePath();
			
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.griditem, null);
			viewHolder.mImageView = (MyView) convertView.findViewById(R.id.id_groupImage);
			viewHolder.mTextViewTitle = (TextView) convertView.findViewById(R.id.id_groupTitle);
			viewHolder.mTextViewCounts = (TextView) convertView.findViewById(R.id.id_groupNum);
			
			//use to listen the width and height of ImageView(用来监听ImageView的宽和高)
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
		
		viewHolder.mTextViewTitle.setText(mScanImageVedio.getFolderName());
		viewHolder.mTextViewCounts.setText(Integer.toString(mScanImageVedio.getImageNum()));
		//给ImageView设置路径Tag,这是异步加载图片的小技巧
		viewHolder.mImageView.setTag(path);
		
		
		//利用NativeImageLoader类加载本地图片
		Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(path, mPoint, new NativeImageCallBack() {
			
			@Override
			public void onImageLoader(Bitmap bitmap, String path) {
				ImageView mImageView = (ImageView) mGridView.findViewWithTag(path);
				if(bitmap != null && mImageView != null){
					mImageView.setImageBitmap(bitmap);
				}
			}
		});
		
		if(bitmap != null){
			viewHolder.mImageView.setImageBitmap(bitmap);
		}else{
			
			viewHolder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
		}
		
		
		return convertView;
	}
	
	
	
	public static class ViewHolder{
		public MyView mImageView;
		public TextView mTextViewTitle;
		public TextView mTextViewCounts;
	}

	
}
