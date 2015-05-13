/* TuKuDuang
 * 20150416
 * ������ʾͼƬimageView�����࣬��ҪΪ��ʵ�ֲ���ͼƬ��С�Ľӿڡ�
 */

package com.tuku.main;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class MyView extends ImageView {
	private OnMeasureListener onMeasureListener;
	
	public void setOnMeasureListener(OnMeasureListener onMeasureListener) {
		this.onMeasureListener = onMeasureListener;
	}

	public MyView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		//Measuring the size of the pictures(����ͼƬ��С)
		if(onMeasureListener != null){
			onMeasureListener.onMeasureSize(getMeasuredWidth(), getMeasuredHeight());
		}
	}

	public interface OnMeasureListener{
		public void onMeasureSize(int width, int height);
	}
	
}
