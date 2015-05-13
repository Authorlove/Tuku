package com.tuku.picturesearch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.LinkedList;
import java.util.List;

import com.example.tukuduang.R;


public class PicSearch extends Activity {

    public static final String TAG = "MainActivity";
    public static final int MIN_WIDTH = 16;
    public static final int MIN_HEIGHT = 16;
    public static Bitmap map_buffer;
    public EditText search_text;
    public Button search_button;
    public GridView grid_view;
    public ImageAdapter adapter;
    public NetworkPictureFinder finder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_main);

        search_text   = (EditText)findViewById(R.id.search_text);
        search_button = (Button)findViewById(R.id.search_button);
        grid_view     = (GridView)findViewById(R.id.grid_view);
        finder        = new NetworkPictureFinder();
        adapter       = new ImageAdapter(this);

        grid_view.setAdapter(adapter);
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchTask task = new SearchTask();
                task.execute(search_text.getText().toString());
            }
        });

        grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                map_buffer = adapter.bitmaps.get(position);
                intent.putExtra("category", search_text.getText().toString());
                intent.putExtra("filename", finder.filenames.get(position));
                intent.setClass(PicSearch.this, ShowPictureActivity.class);
                PicSearch.this.startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       getMenuInflater().inflate(R.menu.picsearch_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.id_pic_action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class SearchTask extends AsyncTask<String, Integer, String> {

        private static final String TAG = "SearchTask";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            adapter.bitmaps.clear();
            search_button.setText(R.string.searching);
            search_button.setClickable(false);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            search_button.setClickable(true);
            search_button.setText(R.string.search);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            for (int i = 0; i < finder.bitmaps.size(); ++i) {
                if(finder.bitmaps.get(i) != null
                        && finder.bitmaps.get(i).getWidth() > MIN_WIDTH
                        && finder.bitmaps.get(i).getHeight() > MIN_HEIGHT)
                    adapter.bitmaps.add(finder.bitmaps.get(i));
            }
            Log.i(TAG, "Progress: " + values[0] + " picture(s) downloaded");
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected String doInBackground(String... params) {
            for(int i = 0; i < 10; ++i)
            {
                finder.find(params[0], 1, i + 1);
                publishProgress(i);
            }
            return null;
        }
    }

    private class ImageAdapter extends BaseAdapter {
        private Context mContext;
        public List<Bitmap> bitmaps;

        public ImageAdapter(Context c) {
            mContext = c;
            bitmaps = new LinkedList<Bitmap>();
        }

        public int getCount() {
            return bitmaps.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                //imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageBitmap(ThumbnailUtils.extractThumbnail(bitmaps.get(position), 100, 200));
            return imageView;
        }       
    }
}
