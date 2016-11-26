package lyx.robert.quicksearch.utils;

import lyx.robert.quicksearch.view.SwipeLayout;

public class SwipeLayoutManager {
	private SwipeLayoutManager(){}
	private static SwipeLayoutManager mInstance = new SwipeLayoutManager();
	
	public static SwipeLayoutManager getInstance(){
		return mInstance;
	}
	
	private SwipeLayout currentLayout;//用来记录当前打开的SwipeLayout
	public void setSwipeLayout(SwipeLayout layout){
		this.currentLayout = layout;
	}
	
	/**
	 * 清空当前所记录的已经打开的layout
	 */
	public void clearCurrentLayout(){
		currentLayout = null;
	}
	
	/**
	 * 关闭当前已经打开的SwipeLayout
	 */
	public void closeCurrentLayout(){
		if(currentLayout!=null){
			currentLayout.close();
		}
	}
	
	/**
	 * 判断当前是否应该能够滑动，如果没有打开的，则可以滑动。
	 * 如果有打开的，则判断打开的layout和当前按下的layout是否是同一个
	 * 
	 * @return
	 */
	public boolean isShouldSwipe(SwipeLayout swipeLayout){
		if(currentLayout==null){
			//说明当前木有打开的layout
			return true;
		}else {
			//说明有打开的layout
			return currentLayout==swipeLayout;
		}
	}
}
