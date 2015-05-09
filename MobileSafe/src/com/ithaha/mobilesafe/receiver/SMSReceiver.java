package com.ithaha.mobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.widget.Toast;

import com.ithaha.mobilesafe.MyAdmin;
import com.ithaha.mobilesafe.R;
import com.ithaha.mobilesafe.service.GPSService;
import com.ithaha.mobilesafe.utils.LockScreenActivity;

public class SMSReceiver extends BroadcastReceiver {

	private SharedPreferences sp;
	private DevicePolicyManager dpm;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// д���ն��ŵĴ���
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		
		sp = context.getSharedPreferences("config", context.MODE_PRIVATE);
		
		for(Object b : objs) {
			// �����ĳһ������
			SmsMessage sms = SmsMessage.createFromPdu((byte[]) b);
			String sender = sms.getOriginatingAddress();
			String body = sms.getMessageBody();
			
			String safephone = sp.getString("safephone", null);
			
			if(sender.contains(safephone)) {
				
				if("#*location*#".equals(body)) {
					// �õ��ֻ���GPS
					Intent i = new Intent(context,GPSService.class);
					context.startService(i);
					
					SharedPreferences sp = context.getSharedPreferences("config", context.MODE_PRIVATE);
					String lastlocation = sp.getString("lastlocation", null);
					
					if(TextUtils.isEmpty(lastlocation)) {
						// λ��û�еõ�
						SmsManager.getDefault().sendTextMessage(sender, null, "location is getting...", null, null);
					} else {
						// λ�õõ�
						SmsManager.getDefault().sendTextMessage(sender, null, "location :" + lastlocation, null, null);
					}
					
					// ������㲥�ض�
					abortBroadcast();
					
				} else if("#*alarm*#".equals(body)){
					// ���ű�������
					abortBroadcast();
					
					MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
					// ѭ������
//					player.setLooping(true);
					// ��������
					player.setVolume(1.0f, 1.0f);
					player.start();
					
				} else if("#*wipedata*#".equals(body)){
					// Զ����������
					abortBroadcast();
				} else if("#*lockscreen*#".equals(body)) {
					Intent lockIntent = new Intent(context,LockScreenActivity.class);
					lockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(lockIntent);
					abortBroadcast();
				}
			}
			
		}
		
	}

}
