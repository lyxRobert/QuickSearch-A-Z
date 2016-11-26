package lyx.robert.quicksearch.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import lyx.robert.quicksearch.Bean.ContactBean;
import lyx.robert.quicksearch.Bean.SortToken;
import lyx.robert.quicksearch.R;
import lyx.robert.quicksearch.view.SwipeLayout;

public class ContactAdapter extends BaseAdapter implements SwipeLayout.OnSwipeStateChangeListener {
	private Context context;
	private ArrayList<ContactBean> list;
	public SortToken sortToken;
	public ContactAdapter(Context context, ArrayList<ContactBean> list) {
		super();
		this.context = context;
		this.list = list;
		sortToken = new SortToken();
	}

	@Override
	public int getCount() {
		return list.size();
	}
	@Override
	public Object getItem(int position) {
		return null;
	}
	@Override
	public long getItemId(int position) {
		return 0;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			convertView = View.inflate(context, R.layout.adapter_friend,null);
		}
		
		ViewHolder holder = ViewHolder.getHolder(convertView);
		//设置数据
		ContactBean friend = list.get(position);
		holder.name.setText(friend.getName());
		holder.swp_delete.setTag(position);
		holder.swp_delete.setOnSwipeStateChangeListener(this);
		String currentWord=friend.getPinyin().charAt(0)+"";
		if(position>0){
			String lastWord = list.get(position-1).getPinyin().charAt(0)+"";
			//获取上一个item的首字母

				if(currentWord.equals(lastWord)){
						//拿当前的首字母和上一个首字母比较
						//说明首字母相同，需要隐藏当前item的first_word
						holder.first_word.setVisibility(View.GONE);
				}else {
						//不一样，需要显示当前的首字母
						//由于布局是复用的，所以在需要显示的时候，再次将first_word设置为可见
						holder.first_word.setVisibility(View.VISIBLE);
						holder.first_word.setText(currentWord);
					}
		}else {
			holder.first_word.setVisibility(View.VISIBLE);
			holder.first_word.setText(currentWord);
		}
		return convertView;
	}

	@Override
	public void onOpen(Object tag) {
	}
	@Override
	public void onClose(Object tag) {
	}

	static class ViewHolder{
		TextView name,first_word;
		SwipeLayout swp_delete;
		public ViewHolder(View convertView){
			name = (TextView) convertView.findViewById(R.id.name);
			first_word = (TextView) convertView.findViewById(R.id.first_word);
			swp_delete = (SwipeLayout) convertView.findViewById(R.id.swp_delete);
		}
		public static ViewHolder getHolder(View convertView){
			ViewHolder holder = (ViewHolder) convertView.getTag();
			if(holder==null){
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			}
			return holder;
		}
	}
}
