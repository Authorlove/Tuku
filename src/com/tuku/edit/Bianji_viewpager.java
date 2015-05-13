package com.tuku.edit;

import java.util.List;

import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class Bianji_viewpager extends PagerAdapter{

	Bianji_picture activity;
	Myimageview[] image;
	List<String> strlist;
	
	public Bianji_viewpager(Myimageview[] image,List<String> strlist,Bianji_picture activity){
		this.activity=activity;
		this.image=image;
		this.strlist=strlist;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return image.length;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0==arg1;
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// TODO Auto-generated method stub
		Myimageview imageview=new Myimageview(this.activity);
		imageview.setImageURI(Uri.parse(strlist.get(position)));
		container.addView(imageview);
		image[position]=imageview;
		return imageview;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		container.removeView(image[position]);
	}

}
