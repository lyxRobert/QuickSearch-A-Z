package lyx.robert.quicksearch.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import lyx.robert.quicksearch.Bean.ContactBean;
import lyx.robert.quicksearch.Bean.PinYinStyle;
import lyx.robert.quicksearch.R;
import lyx.robert.quicksearch.view.SwipeLayout;

public class ContactAdapter extends BaseAdapter implements SwipeLayout.SwipeListener {
	private Context context;
	private ArrayList<ContactBean> list;
	public PinYinStyle sortToken;
	public ContactAdapter(Context context, ArrayList<ContactBean> list) {
		super();
		this.context = context;
		this.list = list;
		sortToken = new PinYinStyle();
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
			convertView = View.inflate(context, R.layout.item_contact,null);
		}
		
		ViewHolder holder = ViewHolder.getHolder(convertView);
		//设置数据
		ContactBean contactBean = list.get(position);
		holder.tv_contact_name.setText(contactBean.getName());
		holder.swp_slip.setTag(position);
		holder.swp_slip.setOnSwipeListener(this);
		String currentAlphabet=contactBean.getPinyin().charAt(0)+"";
		if(position>0){
			String lastAlphabet = list.get(position-1).getPinyin().charAt(0)+"";
			//获取上一个item的首字母

				if(currentAlphabet.equals(lastAlphabet)){
						//首字母相同，需要隐藏当前item的字母的TextView
						holder.tv_first_alphabet.setVisibility(View.GONE);
				}else {
						//不相同就要显示当前的首字母
						holder.tv_first_alphabet.setVisibility(View.VISIBLE);
						holder.tv_first_alphabet.setText(currentAlphabet);
					}
		}else {
			holder.tv_first_alphabet.setVisibility(View.VISIBLE);
			holder.tv_first_alphabet.setText(currentAlphabet);
		}
		return convertView;
	}

	@Override
	public void onOpen(Object obj) {
	}
	@Override
	public void onClose(Object obj) {
	}

	static class ViewHolder{
		TextView tv_contact_name;
		TextView tv_first_alphabet;
		SwipeLayout swp_slip;
		public ViewHolder(View convertView){
			tv_contact_name = (TextView) convertView.findViewById(R.id.tv_contact_name);
			tv_first_alphabet = (TextView) convertView.findViewById(R.id.tv_first_alphabet);
			swp_slip = (SwipeLayout) convertView.findViewById(R.id.swp_slip);
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
