package lyx.robert.quicksearch.view;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import lyx.robert.quicksearch.utils.SwipeLayoutManager;

public class SwipeLayout extends FrameLayout {

	private View contentView;// item内容区域的view
	private View deleteView;// delete区域的view
	private int deleteHeight;// delete区域的高度
	private int deleteWidth;// delete区域的宽度
	private int contentWidth;// content区域的宽度
	private ViewDragHelper viewDragHelper;

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
	
	private SwipeState currentState = SwipeState.Close;//当前默认是关闭状态
	
	private void init() {
		viewDragHelper = ViewDragHelper.create(this, callback);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		contentView = getChildAt(0);
		deleteView = getChildAt(1);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		deleteHeight = deleteView.getMeasuredHeight();
		deleteWidth = deleteView.getMeasuredWidth();
		contentWidth = contentView.getMeasuredWidth();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		// super.onLayout(changed, left, top, right, bottom);
		contentView.layout(0, 0, contentWidth, deleteHeight);
		deleteView.layout(contentView.getRight(), 0, contentView.getRight()
				+ deleteWidth, deleteHeight);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		boolean result = viewDragHelper.shouldInterceptTouchEvent(ev);
		
		//如果当前有打开的，则需要直接拦截，交给onTouch处理
		if(!SwipeLayoutManager.getInstance().isShouldSwipe(this)){
			//先关闭已经打开的layout
			SwipeLayoutManager.getInstance().closeCurrentLayout();
			
			result = true;
		}
		
		return result;
	}
	
	private float downX,downY;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//如果当前有打开的，则下面的逻辑不能执行
		if(!SwipeLayoutManager.getInstance().isShouldSwipe(this)){
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
		viewDragHelper.processTouchEvent(event);
		return true;
	}

	private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			return child==contentView||child==deleteView;
		}
		@Override
		public int getViewHorizontalDragRange(View child) {
			return deleteWidth;
		}
		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			if(child==contentView){
				if(left>0)left = 0;
				if(left<-deleteWidth)left = -deleteWidth;
			}else if (child==deleteView) {
				if(left>contentWidth)left = contentWidth;
				if(left<(contentWidth-deleteWidth))left = contentWidth-deleteWidth;
			}
			return left;
		}
		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			super.onViewPositionChanged(changedView, left, top, dx, dy);
			if(changedView==contentView){
				//手动移动deleteView
				deleteView.layout(deleteView.getLeft()+dx,deleteView.getTop()+dy,
						deleteView.getRight()+dx, deleteView.getBottom()+dy);
			}else if (deleteView==changedView) {
				//手动移动contentView
				contentView.layout(contentView.getLeft()+dx,contentView.getTop()+dy,
						contentView.getRight()+dx, contentView.getBottom()+dy);
			}
			
			//判断开和关闭的逻辑
			if(contentView.getLeft()==0 && currentState!=SwipeState.Close){
				//说明应该将state更改为关闭
				currentState = SwipeState.Close;
				
				//回调接口关闭的方法
				if(listener!=null){
					listener.onClose(getTag());
				}
				
				//说明当前的SwipeLayout已经关闭，需要让Manager清空一下
				SwipeLayoutManager.getInstance().clearCurrentLayout();
			}else if (contentView.getLeft()==-deleteWidth && currentState!=SwipeState.Open) {
				//说明应该将state更改为开
				currentState = SwipeState.Open;

				//回调接口打开的方法
				if(listener!=null){
					listener.onOpen(getTag());
				}
				//当前的Swipelayout已经打开，需要让Manager记录一下下
				SwipeLayoutManager.getInstance().setSwipeLayout(SwipeLayout.this);
			}
		}
		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			super.onViewReleased(releasedChild, xvel, yvel);
			if(contentView.getLeft()<-deleteWidth/2){
				//应该打开
				open();
			}else {
				//应该关闭
				close();
			}
		}
	};
	/**
	 * 打开的方法
	 */
	public void open() {
		viewDragHelper.smoothSlideViewTo(contentView,-deleteWidth,contentView.getTop());
		ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
	}
	/**
	 * 关闭的方法
	 */
	public void close() {
		viewDragHelper.smoothSlideViewTo(contentView,0,contentView.getTop());
		ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
	};
	public void computeScroll() {
		if(viewDragHelper.continueSettling(true)){
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}

	private OnSwipeStateChangeListener listener;
	public void setOnSwipeStateChangeListener(OnSwipeStateChangeListener listener){
		this.listener = listener;
	}
	
	public interface OnSwipeStateChangeListener{
		void onOpen(Object tag);
		void onClose(Object tag);
	}
	
}
