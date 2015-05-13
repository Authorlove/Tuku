package com.tuku.picturesearch;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.jar.Attributes.Name;

public class NetworkPictureFinder {
    private static final String TAG = "NetworkPictureFinder";
    private static final String BAIDU_API = "http://image.baidu.com/i?tn=baiduimagejson&width=&height=&";
    private int time_out = 5000;
    private int max_threads = 5;
    public List<Bitmap> bitmaps; //保存每次请求获得图片
    public List<String> filenames; 
    private List<String> urls;
    private Integer waiting;
    private final Object waiting_lock = new Object();
    private final Object urls_lock = new Object();
    private final Object bitmaps_lock = new Object();

    NetworkPictureFinder() {
        bitmaps = new LinkedList<Bitmap>();
        urls = new LinkedList<String>();
        filenames = new LinkedList<String>();
    }

    /**
     * 利用百度非公开的图片搜索API获取指定类型的图片。该API返回结果类似于百度图片搜索网页的搜索结果
     * 全部搜索结果被分为多个页面，每个页面包含number张图片的URL，每次请求指定获取第pages页的搜索结果
     * 由于百度API的坑爹，下载到的图片全部是原图大小，展示时务必先进行压缩
     * @param keyword 搜索图片时使用的关键字
     * @param number 每个页面包含的图片数量，即本次请求预计返回的图片总数
     * @param pages 获取指定页面的图片
     */
    public List<Bitmap> find(String keyword, int number, int pages) {
        Log.i(TAG, "find(" + keyword + ", " + number + ", " + pages + ") called");

        JSONObject obj = getJSON(keyword, number, pages);
        getUrl(obj);

        if(urls == null) {
            return null;
        }

        bitmaps.clear();
        filenames.clear();
        
        waiting = max_threads;
        //max_threads个线程同时下载
        for(int i = 0; i < max_threads; ++i) {
            FetchThread thread = new FetchThread();
            thread.start();
        }

        synchronized (waiting_lock) {
            try {
                waiting_lock.wait();  //主线程等待所有子线程完成
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return bitmaps;
    }

    private JSONObject getJSON(String keyword, int number, int pages) {
        Log.i(TAG, "getJSON called");

        JSONObject object = null;
        String request_url;
        InputStream input_stream;
        String encoded_word = null;

        try {
            encoded_word = URLEncoder.encode(keyword, "GB2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        request_url = BAIDU_API + "word=" + encoded_word + "&rn=" + number + "&pn=" + pages;
        Log.i(TAG, "request_url: " + request_url);

        try {
            URL url = new URL(request_url);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(time_out);
            conn.setReadTimeout(time_out);
            conn.setRequestMethod("GET");
            input_stream = conn.getInputStream();

            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                byte[] data = readStream(input_stream);
                String json = new String(data);
                Log.d(TAG, "json data: " + json);
                object = new JSONObject(json);
                input_stream.close();
            }
            else
                Log.e(TAG, "Cannot connect to search engine");
        } catch (Exception e) {
            Log.e(TAG, "Network exception");
            e.printStackTrace();
        }

        if(object == null)
            Log.e(TAG, "Cannot receive JSON");

        return object;
    }

    /**
     * 从接收到的JSONObject中解析图片url
     * @param object 需要进行解析的JSON内容
     */
    private void getUrl(JSONObject object) {
        if(object == null)
            return;

        //获取JSON内容中data段（即返回的搜索结果）的数据
        JSONArray data = object.optJSONArray("data");
        if(data == null) {
            Log.e(TAG, "Cannot find data section in JSON");
            return;
        }

        urls.clear();
        for(int i = 0; i < data.length(); ++i) {
            JSONObject data_object = data.optJSONObject(i);
            String obj_url = data_object.optString("objURL");

            if(obj_url != null && !obj_url.isEmpty()) {
                urls.add(obj_url);
                Log.i(TAG, "find url: " + obj_url);
            }
        }

        if(urls.isEmpty())
            Log.e(TAG, "Cannot find any image url from JSON");
    }

    public static byte[] readStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;

        while ((len = inputStream.read(buffer)) != -1) {
            bout.write(buffer, 0, len);
        }

        bout.close();
        inputStream.close();

        return bout.toByteArray();
    }

    private class FetchThread extends Thread {

        @Override
        public void run() {
            Log.i(TAG, "fetch thread is running");

            InputStream input_stream = null;
            String url;

            while(true) {
                //每个线程每次获取1个URL进行处理，当URL全部被获取完后，线程结束
                synchronized (urls_lock) {
                    if (urls.size() > 0)
                        url = urls.remove(0);
                    else
                        break;
                }

                Log.i(TAG, "Processing " + url);

                try {
                    URL file_url = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) file_url.openConnection();
                    conn.setConnectTimeout(time_out);
                    conn.setReadTimeout(time_out);
                    conn.setRequestMethod("GET");
                    input_stream = conn.getInputStream();
                } catch (Exception e) {
                    Log.e(TAG, "Thread network exception");
                    e.printStackTrace();
                }
                
                String name = url.substring(url.lastIndexOf('/') + 1);
                if(name.isEmpty()) {
                	name = UUID.randomUUID() + ".jpg";                   
                }
                synchronized (bitmaps_lock) {
                    bitmaps.add(BitmapFactory.decodeStream(input_stream));
                    filenames.add(name);
                }

                Log.i(TAG, "Download completed");
            }

            //当线程看到全部URL都被获取完，并且自己的下载任务已经完成后，就将waiting值减1，
            // 表示当前运行的线程数量减1。当waiting值为0时，全部线程均已完成下载任务，唤醒主线程
            synchronized (waiting_lock) {
                --waiting;
                if (waiting == 0) {
                    Log.d(TAG, "trying to notify main thread");
                    waiting_lock.notify();
                }
            }
        }
    }
}
