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
 * �˴���Ҫ���������췽����ʵ��
 * @author hello
 *
 */
public class SettingItemView extends RelativeLayout {

	private CheckBox cb_states;
	private TextView tv_description;
	private TextView tv_title;
	
	private String desc_on;
	private String desc_off;
	
	/**
	 * ��ʼ�������ļ�
	 * @param context
	 */
	private void initView(Context context) {
		// ��һ�������ļ�--->View�����Ҽ�����SettingItemView
		View.inflate(context, R.layout.setting_item_view, this);
		cb_states = (CheckBox) this.findViewById(R.id.cb_states);
		tv_description = (TextView) this.findViewById(R.id.tv_description);
		tv_title = (TextView) this.findViewById(R.id.tv_title);
	}
	
	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	/**
	 * �������������Ĺ��췽���������ļ�ʹ�õ�ʱ�����
	 * @param context
	 * @param attrs
	 */
	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		String title = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.ithaha.mobilesafe", "title1");
		desc_on = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.ithaha.mobilesafe", "desc_on");
		desc_off = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.ithaha.mobilesafe", "desc_off");
		
		tv_title.setText(title);
		setDesc(desc_off);
	}

	public SettingItemView(Context context) {
		super(context);
		initView(context);
	}
	
	/**
	 * У����Ͽؼ��Ƿ�ѡ��
	 */
	public boolean isChecked() {
		return cb_states.isChecked();
	}
	
	/**
	 * ������Ͽؼ���״̬
	 */
	public void setChecked(boolean checked) {
		if(checked) {
			setDesc(desc_on);
		} else {
			setDesc(desc_off);
		}
		cb_states.setChecked(checked);
	}
	
	/**
	 * ������Ͽؼ���������Ϣ
	 */
	public void setDesc(String text) {
		tv_description.setText(text);
	}
}
