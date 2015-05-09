package com.ithaha.mobilesafe;

import com.ithaha.mobilesafe.utils.LockScreenActivity;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * �ֻ�����
 * @author hello
 *
 */
public class LostFindActivity extends Activity {
	
	private SharedPreferences sp;
	private TextView tv_safenum;
	private ImageView iv_protecting;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// �ж��Ƿ�����������
		sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean configed = sp.getBoolean("configed", false);
		
		if(configed) {
			// ����������
			setContentView(R.layout.activity_lost_find);
			tv_safenum = (TextView) findViewById(R.id.tv_safenum);
			iv_protecting = (ImageView) findViewById(R.id.iv_protecting);
			
			// �õ���ȫ����
			String safePhone = sp.getString("safephone", null);
			tv_safenum.setText(safePhone);
			
			// �õ��Ƿ�ʼ��������
			boolean protecting = sp.getBoolean("protecting", false);
			if(protecting) {
				// ���������	
				iv_protecting.setImageResource(R.drawable.lock);
			} else {
				// ���û�п���
				iv_protecting.setImageResource(R.drawable.unlock);
			}
			
			
		} else {
			// û������������
			Intent intent = new Intent(this,Setup1Activity.class);
			startActivity(intent);
			
			// �رյ�ǰҳ��
			finish();
		}
		
	}
	
	/**
	 * ���½������������
	 * @param view
	 */
	public void reEnterSetup(View view) {
		Intent intent = new Intent(LostFindActivity.this,Setup1Activity.class);
		startActivity(intent);
		
	}
		
}
