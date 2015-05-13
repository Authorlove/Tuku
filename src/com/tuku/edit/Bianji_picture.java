package com.tuku.edit;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class Bianji_picture extends Activity{

	String bgbmproot,returnroot="";
	private final static int REQUEST_CODE=1;  
	public final static int RESULT_CODE=1;
    List<View> viewlist;
    int num=0;
    Bitmap[] bgbmp;
    Myimageview[] image;
    private ViewPager pager;
    List<String> strlist;
    int position;
    int size;
    Bianji_viewpager viewpager;
    NetworkFaceRecognizer recognizer;
    
	@Override
	public void onCreate(Bundle savedInstanceState){
	    super.onCreate(savedInstanceState);
	    this.setContentView(com.example.tukuduang.R.layout.bianji_layout);
	    
	    strlist=new ArrayList();
	    Intent intent=getIntent();
	    strlist=intent.getStringArrayListExtra("data");
	    position=intent.getIntExtra("position", 0);
	    size=strlist.size();
	    image=new Myimageview[size];
	    pager=(ViewPager)findViewById(com.example.tukuduang.R.id.pager);
	    viewpager=new Bianji_viewpager(image,strlist,this);
	    pager.setAdapter(viewpager);
	    pager.setCurrentItem(position);
	    recognizer = new NetworkFaceRecognizer();
	    recognizer.setApi_key("a11191cb0fe6b33db66415ecdc4d2415", "s4KqJWpsC7vknkvpxoZngljF3xAD2lrl");
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, 0, 0, com.example.tukuduang.R.string.left);
		menu.add(0,1,0,com.example.tukuduang.R.string.right);
		menu.add(0,2,0,com.example.tukuduang.R.string.lvjing);
		menu.add(0,3,0,com.example.tukuduang.R.string.share);
		menu.add(0,4,0,com.example.tukuduang.R.string.recognize);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		getnum();
		switch(item.getItemId()){
		case 0:zuozhuan();break;
		case 1:youzhuan();break;
		case 2:lvjing();break;
		case 3:share();break;
		case 4:recognize(); break;
		default:break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void recognize() {
		Bitmap cur_map = BitmapFactory.decodeFile(strlist.get(num));
		if(cur_map != null) {
			if(cur_map.getWidth() < 16 || cur_map.getHeight() < 16) {
				Toast.makeText(this, "图片太小", Toast.LENGTH_SHORT).show();
				return;
			}
			RecognizeTask task = new RecognizeTask();
			task.execute(cur_map);
		} else {
			Log.e("recognize", "Cannot read current bitmap");
		}
	}
	
	public void share(){
		bgbmproot=strlist.get(num);
		File file = new File(bgbmproot);
		Intent intent = new Intent(Intent.ACTION_SEND);
		 
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
        Uri uri = Uri.fromFile(file);
        if(uri != null) {
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(intent, getTitle()));
        }
	}
	
	public int getnum(){
		num=pager.getCurrentItem();
		return num;
	}
	
	public void lvjing(){
		Intent in=new Intent();
		bgbmproot=strlist.get(num);
		in.putExtra("bgbmproot", bgbmproot);
		in.setClass(this, Lvjing_picture.class);
		startActivityForResult(in,REQUEST_CODE);
	}
	
	public void zuozhuan(){
		int n=getnum();
		viewpager.image[n].xuanzhuan(-90);
	}
	
	public void youzhuan(){
		int n=getnum();
		viewpager.image[n].xuanzhuan(90);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent in=new Intent();
//		File jpg_file = new File(Environment.getExternalStorageDirectory(), "buffer.jpg");
//		jpg_file.delete();
		in.putStringArrayListExtra("date", (ArrayList<String>) strlist);
		this.setResult(RESULT_CODE,in);
		super.onBackPressed();
		finish();
	}
	
	@Override
	protected void onActivityResult(int requestCode,int resultCode,Intent date){
		if(requestCode==REQUEST_CODE){
			if(resultCode==Lvjing_picture.RESULT_CODE){
				returnroot=date.getStringExtra("bgbmproot");
				strlist.add(returnroot);
				size=strlist.size();
				image=new Myimageview[size];
				viewpager=new Bianji_viewpager(image,strlist,this);
			    pager.setAdapter(viewpager);
			    pager.setCurrentItem(size-1);
			}
		}
	}
	
    private class RecognizeTask extends AsyncTask<Bitmap, Integer, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();   
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(bitmap != null) {
            	strlist.add(saveImage(bitmap).getPath());
            	size=strlist.size();
				image=new Myimageview[size];
				viewpager=new Bianji_viewpager(image,strlist,Bianji_picture.this);
			    pager.setAdapter(viewpager);
			    pager.setCurrentItem(size-1);
            } else {
            	Toast.makeText(Bianji_picture.this, "无法识别人脸", Toast.LENGTH_SHORT).show();
            }
        }
        
        public Uri saveImage(Bitmap bitmap) {
            File jpg_file;
            BufferedOutputStream output_stream;
            try {
                jpg_file = new File(Environment.getExternalStorageDirectory(), "buffer.jpg");
                output_stream = new BufferedOutputStream(new FileOutputStream(jpg_file));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, output_stream);
                output_stream.flush();
                output_stream.close();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return Uri.fromFile(jpg_file);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(Bitmap bitmap) {
            super.onCancelled(bitmap);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected Bitmap doInBackground(Bitmap... params) {
            return recognizer.recognize(params[0]);
        }
    }
	
}