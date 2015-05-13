package com.tuku.picturesearch;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.example.tukuduang.R;


public class ShowPictureActivity extends Activity {
    private static final String TAG = "ShowPictureActivity";
    private ImageView image;
    private String category;
    private String filename;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreated()");
        setContentView(R.layout.show_picture_activity);
        image = (ImageView)findViewById(R.id.large_view);

        category = getIntent().getStringExtra("category");
        filename = getIntent().getStringExtra("filename");
        image.setImageBitmap(PicSearch.map_buffer);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, R.string.share);
        menu.add(0, 1, 0, R.string.download);
        return true;
    }

  
    private Uri saveImage() {
        if(image.getDrawable() != null) {
        	String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/buffer.jpg";
            Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
            try {
                saveTo(path, bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return Uri.fromFile(new File(path));
        }
        return null;
    }
    
    public void saveTo(String path, Bitmap bitmap) throws IOException {
    	File jpg_file = new File(path);
        BufferedOutputStream output_stream = new BufferedOutputStream(new FileOutputStream(jpg_file));
              
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, output_stream);
        output_stream.flush();
        output_stream.close();     
        if(jpg_file.exists()) {
        	Log.i(TAG, "file saved to: " + jpg_file.getPath());
        } else {
        	Log.e(TAG, "failed to save: " + jpg_file.getPath());
        }
    }
    

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/jpg");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
                Uri uri = saveImage();
                if(uri != null) {
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(intent, getTitle()));
                    return true;
                }
                break;
            case 1:
            	String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + category;
            	File file = new File(path);
            	if(file.mkdir() || file.isDirectory()) {
            		try {
						saveTo(path + "/" + filename, ((BitmapDrawable) image.getDrawable()).getBitmap());
					} catch (IOException e) {
						e.printStackTrace();
					}
            	} else {
            		Log.e(TAG, "cannot create dir: " + path);
            	}
            	
            	break;
        }
        return false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
