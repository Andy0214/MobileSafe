package com.ithaha.mobilesafe;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

public class Setup4Activity extends BaseSetupActivity {

	/**
	 * �豸���Է���
	 */
	private DevicePolicyManager dpm;

	private SharedPreferences sp;
	private Button cb_setup4_status;
	private boolean lockstatus;

	private Editor edit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);

		sp = getSharedPreferences("config", MODE_PRIVATE);
		edit = sp.edit();
		
		cb_setup4_status = (Button) findViewById(R.id.cb_setup4_status);
		
		lockstatus = sp.getBoolean("lockstatus", false);
		if(lockstatus) {
			// �Ѿ�����
			cb_setup4_status.setText("Զ�������Ѿ�����");
		} else {
			// ��δ����
			cb_setup4_status.setText("Զ��������δ����");
		}
	}

	public void lock(View view) {
		lockstatus = sp.getBoolean("lockstatus", false);
		if(!lockstatus) {
			// �Ѿ�����
			
			// ����һ��Intent
			Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			// ��Ҫ����˭
			ComponentName mDeviceAdminSample = new ComponentName(this,MyAdmin.class);

			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
					mDeviceAdminSample);
			// Ȱ˵�û���������ԱȨ��
			intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
					"���ǿ����ҿ���һ����������İ�ť�Ͳ��ᾭ��ʧ��");
			startActivity(intent);

			cb_setup4_status.setText("Զ�������Ѿ�����");
			edit.putBoolean("lockstatus", true);
			edit.commit();
		} else {
			// ��δ����
			edit.putBoolean("lockstatus", false);
			edit.commit();
			
			dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
			ComponentName mDeviceAdminSample = new ComponentName(Setup4Activity.this,MyAdmin.class);
			dpm.removeActiveAdmin(mDeviceAdminSample);
			
			cb_setup4_status.setText("Զ��������δ����");
			Toast.makeText(getApplicationContext(), "�Ѿ�ȡ��һ������", 0).show();		
		}
		
	}

	@Override
	public void showNext() {

		Intent intent = new Intent(Setup4Activity.this, Setup5Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	@Override
	public void showPre() {
		Intent intent = new Intent(Setup4Activity.this, Setup3Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);

	}

}
