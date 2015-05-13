/*
 * TuKuDuang
 * 20140516
 * 扫描图片类，主要封装图片文件夹的路径，文件名，文件数目
 */
package com.tuku.main;

import android.graphics.Bitmap;

public class ScanImageVedio {
	
	private String firstImagePath;//第一张图片的路径
	private String folderName;
	private int imageNum;//图片数目
	private long lastModifiedTime;//最后一次修改时间
	private Bitmap videoBitmap;
	//基本的set和get方法
	public void setFirstImagePath(String path)
	{
		this.firstImagePath=path;
		
	}
	public String getFirstImagePath()
	{
		return firstImagePath;
		
	}
	
	public void setFolderName(String name)
	{
		this.folderName=name;
	}
	public String getFolderName()
	{
		return folderName;
		
	}
	
	public void setImageNum(int num)
	{
		this.imageNum=num;
	}
	public int getImageNum()
	{
		return imageNum;
	}
	public void setLastModifiedTime(long time)
	{
		this.lastModifiedTime=time;
	}
	public long getLastModifiedTime()
	{
		return lastModifiedTime;
	}
	public void setVideoBitmap(Bitmap bitmap)
	{
		this.videoBitmap=bitmap;
	}
	public Bitmap getVideoBitmap()
	{
		return videoBitmap;
	}
}
