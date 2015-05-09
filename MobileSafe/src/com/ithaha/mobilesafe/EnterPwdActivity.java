package com.ithaha.mobilesafe;

import com.ithaha.mobilesafe.utils.MD5Utils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EnterPwdActivity extends Activity {

	private EditText et_password;
	private TextView tv_name;
	private ImageView iv_icon;
	private SharedPreferences sp;
	private String psw;
	private String packname;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enteypwd);
		
		et_password = (EditText) findViewById(R.id.et_password);
		tv_name = (TextView) findViewById(R.id.tv_name);
		iv_icon = (ImageView) findViewById(R.id.iv_icon);
		
		sp = getSharedPreferences("config", MODE_PRIVATE);
		psw = sp.getString("password", null);
		
		System.out.println("psw..." + psw);
		
		Intent intent = getIntent();
		// ��ǰҪ������Ӧ�ó���İ���
		packname = intent.getStringExtra("packname");
		PackageManager pm = getPackageManager();
		try {
			ApplicationInfo info = pm.getApplicationInfo(packname, 0);
			tv_name.setText(info.loadLabel(pm));
			iv_icon.setImageDrawable(info.loadIcon(pm));
			
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	public void click(View view){
		String password = et_password.getText().toString().trim();
		if(TextUtils.isEmpty(password)) {
			Toast.makeText(this, "���벻��Ϊ��", 0).show();
			return ;
		}
		if(psw.equals(MD5Utils.md5Password(password))) {
			// ��ʱ�Ĺرտ��Ź�
			// �Զ���Ĺ㲥 ����  �󶨷���
			Intent intent = new Intent();
			intent.setAction("com.ithaha.mobilesafe.tempstop");
			intent.putExtra("packname", packname);
			sendBroadcast(intent);
			
			finish();
		} else {
			Toast.makeText(this, "�������", 0).show();
		}
	}
	
	@Override
	public void onBackPressed() {
		// ������
		Intent intent = new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.HOME");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addCategory("android.intent.category.MONKEY");
		startActivity(intent);
		// ���е�activity��С��������ִ��ondestory��ִֻ��stop
		
	}
	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}
	
}
