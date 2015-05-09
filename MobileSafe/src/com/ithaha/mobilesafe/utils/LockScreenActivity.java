package com.ithaha.mobilesafe.utils;

import com.ithaha.mobilesafe.MyAdmin;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.os.Bundle;
import android.widget.Toast;

public class LockScreenActivity extends Activity {

	/**
	 * �豸���Է���
	 */
	private DevicePolicyManager dpm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
		ComponentName mDeviceAdminSample = new ComponentName(this,MyAdmin.class);
		if(dpm.isAdminActive(mDeviceAdminSample)) {
			dpm.lockNow(); // ����
			dpm.resetPassword("123456", 0); // ������Ļ����
			
			// ���SD��
			// dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
			// �ָ���������
			// dpm.wipeData(0);
		} else {
			Toast.makeText(this, "��û�д򿪹���ԱȨ��", 1).show();
			return ;
		}
		finish();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
