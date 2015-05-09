package com.ithaha.mobilesafe.ui;

import com.ithaha.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * �����Զ������Ͽؼ�,������������TextView��һ��CheckBox��һ��View
 * @author hello
 *
 */
public class SettingClickView extends RelativeLayout {

	private TextView tv_description;
	private TextView tv_title;
	
	private String desc_on;
	private String desc_off;
	
	/**
	 * ��ʼ�������ļ�
	 * @param context
	 */
	private void iniView(Context context) {
		// ��һ�������ļ�--->View�����Ҽ�����SettingItemView
		View.inflate(context, R.layout.setting_click, this);
		tv_description = (TextView) this.findViewById(R.id.tv_description);
		tv_title = (TextView) this.findViewById(R.id.tv_title);
	}
	
	public SettingClickView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		iniView(context);
	}

	/**
	 * �������������Ĺ��췽���������ļ�ʹ�õ�ʱ�����
	 * @param context
	 * @param attrs
	 */
	public SettingClickView(Context context, AttributeSet attrs) {
		super(context, attrs);
		iniView(context);
		String title = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.ithaha.mobilesafe", "title1");
		desc_on = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.ithaha.mobilesafe", "desc_on");
		desc_off = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.ithaha.mobilesafe", "desc_off");
		
		tv_title.setText(title);
		setDesc(desc_off);
	}

	public SettingClickView(Context context) {
		super(context);
		iniView(context);
	}
	
	
	/**
	 * ������Ͽؼ���������Ϣ
	 */
	public void setDesc(String text) {
		tv_description.setText(text);
	}
	
	/**
	 * ������Ͽؼ��ı���
	 */
	public void setTitle(String title) {
		tv_title.setText(title);
	}
}
