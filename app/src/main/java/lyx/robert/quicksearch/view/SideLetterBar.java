package lyx.robert.quicksearch.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import lyx.robert.quicksearch.utils.DisplayUtil;

public class SideLetterBar extends View{
	private String[] mSideLetter  = { "#","A", "B", "C", "D", "E", "F", "G", "H",
			"I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
			"V", "W", "X", "Y", "Z",};
	private Paint paint;
	private int letterWidth;
	private float letterHeight;
	public SideLetterBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public SideLetterBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SideLetterBar(Context context) {
		super(context);
		init(context);
	}
	
	private void init(Context context){
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);//设置抗锯齿
		paint.setColor(Color.WHITE);
		paint.setTextSize(new DisplayUtil().dip2px(context,16));
		paint.setTextAlign(Align.CENTER);////设置文本居中对齐
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		letterWidth = getMeasuredWidth();
		//得到一个字母要显示的高度
		letterHeight = getMeasuredHeight()*1f/mSideLetter .length;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		for (int i = 0; i < mSideLetter .length; i++) {
			float x = letterWidth/2;
			float y = letterHeight/2 + getTextHeight(mSideLetter [i])/2 + i*letterHeight;
			
			paint.setColor(lastIndex==i?Color.BLACK:Color.WHITE);
			
			canvas.drawText(mSideLetter [i], x, y, paint);
		}
	}
	private int lastIndex = -1;//标记上次的触摸字母的索引
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			float touchY = event.getY();
			int index = (int) (touchY/letterHeight);//得到字母对应的索引
			if(lastIndex!=index){
				//判断当前触摸字母跟上次触摸的是不是同一个字母
				if(index>=0 && index<mSideLetter .length){
					//判断触摸的范围是否在所有字母的高度之内
					if(listener!=null){
						listener.onTouchLetter(mSideLetter [index]);
					}
				}
			}
			lastIndex = index;
			break;
		case MotionEvent.ACTION_UP:
			//滑动触目事件消失，标记的位置索引就要进行重置
			lastIndex = -1;
			break;
		}
		//刷新界面进行从新绘制
		invalidate();
		return true;
	}

	/**
	 * 获取文本的高度
	 * @param text
	 * @return
	 */
	private int getTextHeight(String text) {
		//获取文本的高度
		Rect bounds = new Rect();
		paint.getTextBounds(text,0,text.length(), bounds);
		return bounds.height();
	}
	
	private OnTouchLetterListener listener;
	public void setOnTouchLetterListener(OnTouchLetterListener listener){
		this.listener = listener;
	}
	/**
	 * 触摸字母的监听事件
	 *
	 */
	public interface OnTouchLetterListener{
		void onTouchLetter(String letter);
	}

}
