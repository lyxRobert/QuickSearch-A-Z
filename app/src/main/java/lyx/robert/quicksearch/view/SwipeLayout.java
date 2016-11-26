package lyx.robert.quicksearch.view;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import lyx.robert.quicksearch.utils.SwipeManager;

public class SwipeLayout extends FrameLayout {

	private View contentView;// 默认显示内容的view
	private View slipView;// 侧滑控件的view
	private int slipHeight;//  侧滑控件的高度
	private int slipWidth;// 侧滑控件的宽度
	private int contentWidth;// 默认显示内容的宽度
	private ViewDragHelper mDrag;

	public SwipeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public SwipeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SwipeLayout(Context context) {
		super(context);
		init();
	}

	enum SwipeState{
		Open,Close;
	}
	
	private SwipeState currentState = SwipeState.Close;//默认是关闭状态
	
	private void init() {
		//使用静态方法构造ViewDragHelper,其中需要传入一个ViewDragHelper.Callback回调对象.
		mDrag = ViewDragHelper.create(this, callback);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		contentView = getChildAt(0);
		slipView = getChildAt(1);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		slipHeight = slipView.getMeasuredHeight();
		slipWidth = slipView.getMeasuredWidth();
		contentWidth = contentView.getMeasuredWidth();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		contentView.layout(0, 0, contentWidth, slipHeight);
		slipView.layout(contentView.getRight(), 0, contentView.getRight()
				+ slipWidth, slipHeight);
	}
	//重写此方法回调ViewDragHelper中对应的方法
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		boolean result = mDrag.shouldInterceptTouchEvent(ev);
		
		//如果当前有打开的，则需要直接拦截，交给onTouch处理
		if(!SwipeManager.getInstance().isSwipe(this)){
			//先关闭已经打开的layout
			SwipeManager.getInstance().closeCurrentLayout();
			
			result = true;
		}
		
		return result;
	}
	
	private float downX,downY;
	//重写此方法回调ViewDragHelper中对应的方法.
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//如果当前有打开的，调用requestDisallowInterceptTouchEvent进行拦截，触摸事件的逻辑将不会执行
		if(!SwipeManager.getInstance().isSwipe(this)){
			requestDisallowInterceptTouchEvent(true);
			return true;
		}
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downX = event.getX();
			downY = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			//1.获取x和y方向移动的距离
			float moveX = event.getX();
			float moveY = event.getY();
			float delatX = moveX - downX;//x方向移动的距离
			float delatY = moveY - downY;//y方向移动的距离
			if(Math.abs(delatX)>Math.abs(delatY)){
				//表示移动是偏向于水平方向，那么应该SwipeLayout应该处理，请求listview不要拦截
				requestDisallowInterceptTouchEvent(true);
			}
			//更新downX，downY
			downX = moveX;
			downY = moveY;
			break;
		case MotionEvent.ACTION_UP:
			
			break;
		}
		mDrag.processTouchEvent(event);
		return true;
	}

	private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
		//返回值就是当前捕捉到的也就是你当前拖拽的某个子View
		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			return child==contentView||child==slipView;
		}
		//返回值是可以水平拖拽的范围
		@Override
		public int getViewHorizontalDragRange(View child) {
			return slipWidth;
		}
		//手指触摸移动时实时回调, left表示要移动到的x位置,dx表示移动的距离
		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			if(child==contentView){
				if(left>0)left = 0;
				if(left<-slipWidth)left = -slipWidth;
			}else if (child==slipView) {
				if(left>contentWidth)left = contentWidth;
				if(left<(contentWidth-slipWidth))left = contentWidth-slipWidth;
			}
			return left;
		}
		//拖拽的子View完全挪出屏幕则防止过度绘制
		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			super.onViewPositionChanged(changedView, left, top, dx, dy);
			if(changedView==contentView){
				//手动移动slipView
				slipView.layout(slipView.getLeft()+dx,slipView.getTop()+dy,
						slipView.getRight()+dx, slipView.getBottom()+dy);
			}else if (slipView==changedView) {
				//手动移动contentView
				contentView.layout(contentView.getLeft()+dx,contentView.getTop()+dy,
						contentView.getRight()+dx, contentView.getBottom()+dy);
			}
			
			//判断开和关闭的逻辑
			if(contentView.getLeft()==0 && currentState!=SwipeState.Close){
				//说明应该将state更改为关闭
				currentState = SwipeState.Close;
				
				//回调接口关闭的方法
				if(swipeListener!=null){
					swipeListener.onClose(getTag());
				}
				
				//说明当前的SwipeLayout已经关闭，需要让Manager清空一下
				SwipeManager.getInstance().clearCurrentLayout();
			}else if (contentView.getLeft()==-slipWidth && currentState!=SwipeState.Open) {
				//说明应该将state更改为开
				currentState = SwipeState.Open;

				//回调接口打开的方法
				if(swipeListener!=null){
					swipeListener.onOpen(getTag());
				}
				//当前的Swipelayout已经打开，需要让Manager记录一下下
				SwipeManager.getInstance().setSwipeLayout(SwipeLayout.this);
			}
		}
		//手指释放时回调此方法
		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			super.onViewReleased(releasedChild, xvel, yvel);
			if(contentView.getLeft()<-slipWidth/2){
				//如果滑动的内容大于侧滑内容的一半应当打开
				open();
			}else {
				//否则就是关闭
				close();
			}
		}
	};
	/**
	 * 打开的方法
	 */
	public void open() {
		mDrag.smoothSlideViewTo(contentView,-slipWidth,contentView.getTop());
		ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
	}
	/**
	 * 关闭的方法
	 */
	public void close() {
		mDrag.smoothSlideViewTo(contentView,0,contentView.getTop());
		ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
	}
	public void computeScroll() {
		if(mDrag.continueSettling(true)){
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}

	private SwipeListener swipeListener;
	public void setOnSwipeListener(SwipeListener listener){
		this.swipeListener = listener;
	}
	
	public interface SwipeListener{
		void onOpen(Object obj);
		void onClose(Object obj);
	}
}
