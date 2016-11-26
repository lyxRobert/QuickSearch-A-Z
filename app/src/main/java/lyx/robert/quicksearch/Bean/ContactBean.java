package lyx.robert.quicksearch.Bean;

import lyx.robert.quicksearch.utils.PinYinUtil;

public class ContactBean implements Comparable<ContactBean>{
	private String pinyin;
	public String name;
	public PinYinStyle pinYinStyle=new PinYinStyle();
	//使用成员变量生成构造方法：alt+shift+s->o


	public ContactBean(String name) {
		super();
		this.name = name;
		//一开始就转化好拼音
		setPinyin(PinYinUtil.getPinyin(name));
	}

	@Override
	public int compareTo(ContactBean another) {
		return getPinyin().compareTo(another.getPinyin());
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
