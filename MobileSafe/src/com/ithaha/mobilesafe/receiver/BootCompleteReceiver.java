package com.ithaha.mobilesafe.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

public class BootCompleteReceiver extends BroadcastReceiver {

	private SharedPreferences sp;
	private TelephonyManager tm;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		
		boolean protecting = sp.getBoolean("protecting", false);
		if(protecting) {
			// �����˷�������
			
			// ��ȡ֮ǰ�����sim����Ϣ
			String saveSim = sp.getString("sim", null);

			// ��ȡ��ǰ��sim����Ϣ
			String realSim = tm.getSimSerialNumber();
			
			// �Ƚ��Ƿ�һ��
			if(saveSim.equals(realSim)) {
				// sim��û�б�
				
			} else {
				// sim���ı���
				String safephone = sp.getString("safephone", null);
				SmsManager smsManager = SmsManager.getDefault();
				smsManager.sendTextMessage(safephone, null, "sim has changed....", null, null);
			}
		}
		
		
		
	}

}
