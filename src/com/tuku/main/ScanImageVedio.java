/*
 * TuKuDuang
 * 20140516
 * ɨ��ͼƬ�࣬��Ҫ��װͼƬ�ļ��е�·�����ļ������ļ���Ŀ
 */
package com.tuku.main;

import android.graphics.Bitmap;

public class ScanImageVedio {
	
	private String firstImagePath;//��һ��ͼƬ��·��
	private String folderName;
	private int imageNum;//ͼƬ��Ŀ
	private long lastModifiedTime;//���һ���޸�ʱ��
	private Bitmap videoBitmap;
	//������set��get����
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
