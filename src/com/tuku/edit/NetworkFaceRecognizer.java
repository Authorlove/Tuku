package com.tuku.edit;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;

import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class NetworkFaceRecognizer {
    private static final String TAG = "NetworkFaceRecognizer";
    private String api_key;
    private String api_secret;

    /**
     * 设置调用Face++ API所必需的API_key和API_secret
     * @param api_key
     */
    public void setApi_key(String api_key, String api_secret) {
        this.api_key = api_key;
        this.api_secret = api_secret;
    }


    /**
     * 调用Face++的API对传入的图片进行人脸识别操作，需要网络连接。被识别出的人脸会被框选出来
     * @param img 需要进行人脸检测的位图
     * @return 返回框选出人脸的位图
     */
    public Bitmap recognize(Bitmap img) {
        HttpRequests httpRequests = new HttpRequests(api_key, api_secret, true, false);
        //Log.v(TAG, "image size : " + img.getWidth() + " " + img.getHeight());

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        float scale = Math.min(1, Math.min(600f / img.getWidth(), 600f / img.getHeight()));
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        Bitmap imgSmall = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, false);
        //Log.v(TAG, "imgSmall size : " + imgSmall.getWidth() + " " + imgSmall.getHeight());

        imgSmall.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] array = stream.toByteArray();

        JSONObject result;
        try {
            //detect
            result = httpRequests.detectionDetect(new PostParameters().setImg(array));
        } catch (FaceppParseException e) {
            e.printStackTrace();
            Log.e(TAG, "Network error");
            return null;
        };
        return DrawFace(img, result);
    }

    private Bitmap DrawFace(Bitmap img, JSONObject rst)
    {
        //use the red paint
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(Math.max(img.getWidth(), img.getHeight()) / 100f);

        //create a new canvas
        Bitmap bitmap = Bitmap.createBitmap(img.getWidth(), img.getHeight(), img.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(img, new Matrix(), null);


        try {
            //find out all faces
            final int count = rst.getJSONArray("face").length();
            for (int i = 0; i < count; ++i) {
                float x, y, w, h;
                int age;
                
                age = rst.getJSONArray("face").getJSONObject(i)
                		.getJSONObject("attribute").getJSONObject("age").getInt("value");
                
                //get the center point
                x = (float)rst.getJSONArray("face").getJSONObject(i)
                        .getJSONObject("position").getJSONObject("center").getDouble("x");
                y = (float)rst.getJSONArray("face").getJSONObject(i)
                        .getJSONObject("position").getJSONObject("center").getDouble("y");

                //get face size
                w = (float)rst.getJSONArray("face").getJSONObject(i)
                        .getJSONObject("position").getDouble("width");
                h = (float)rst.getJSONArray("face").getJSONObject(i)
                        .getJSONObject("position").getDouble("height");
                

                //change percent value to the real size
                x = x / 100 * img.getWidth();
                w = w / 100 * img.getWidth() * 0.7f;
                y = y / 100 * img.getHeight();
                h = h / 100 * img.getHeight() * 0.7f;

                //draw the box to mark it out            
                paint.setTextSize(25);
                paint.setTypeface(Typeface.DEFAULT_BOLD);
                canvas.drawText(String.valueOf(age), x - w, y - h, paint);
                paint.setStrokeWidth(3);             
                canvas.drawLine(x - w, y - h, x - w, y + h, paint);
                canvas.drawLine(x - w, y - h, x + w, y - h, paint);
                canvas.drawLine(x + w, y + h, x - w, y + h, paint);
                canvas.drawLine(x + w, y + h, x + w, y - h, paint);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "json parse error");
            return null;
        }
        return bitmap;
    }
}
