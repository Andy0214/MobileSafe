package com.ithaha.mobilesafe.service;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import com.ithaha.mobilesafe.db.dao.BlackNumberDao;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.text.BoringLayout;

/**
 * ���������ط���
 * @author hello
 *
 */
public class CallSmsSafeService extends Service {

	private InnerSmsReceiver receiver;
	private BlackNumberDao dao;
	private TelephonyManager tm;
	private MyListener listener;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private class InnerSmsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// ��鷢�����Ƿ��Ǻ��������룬���������˶������� or ȫ������
			Object[] objs = (Object[]) intent.getExtras().get("pdus");
			for(Object obj : objs) {
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[])obj);
				// �õ����ŷ�����
				String sender = smsMessage.getOriginatingAddress();
				String mode = dao.findMode(sender);
				if("2".equals(mode) || "3".equals(mode)){
					// ���ض���
					abortBroadcast();
				}
				// ����������ʾ
				String body = smsMessage.getMessageBody();
				if(body.contains("fapiao")) {
					abortBroadcast();
				}
			}
		}
		
	}
	
	@Override
	public void onCreate() {
		dao = new BlackNumberDao(this);
		receiver = new InnerSmsReceiver();
		
		// ��̬ע��һ���㲥������
		IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		registerReceiver(receiver, filter);
		
		
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		
		unregisterReceiver(receiver);
		receiver = null;
		
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		super.onDestroy();
	}
	
	private class MyListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:	// ����״̬
				String result = dao.findMode(incomingNumber);
				if("1".equals(result) || "3".equals(result)) {
					// �Ҷϵ绰
					endCall();
					
					// ɾ�����м�¼
					// ��ϵ��Ӧ�õ�˽�����ݿ�����,ֻ��ʹ�������ṩ��
					// deleteCallLog(incomingNumber);
					// �۲���м�¼���ݿ����ݵı仯
					getContentResolver().registerContentObserver(Uri.parse("content://call_log/calls"), true, new CallLogObserver(new Handler(),incomingNumber));
				}
				break;

			default:
				break;
			}
			
			super.onCallStateChanged(state, incomingNumber);
		}
		
	}
	
	private class CallLogObserver extends ContentObserver {

		private String incomingNumber;
		
		public CallLogObserver(Handler handler,String incomingNumber) {
			super(handler);
			this.incomingNumber = incomingNumber;
		}

		@Override
		public void onChange(boolean selfChange) {
			// ���ݿ����ݱ仯�ˣ������˺��м�¼
			getContentResolver().unregisterContentObserver(this);
			deleteCallLog(incomingNumber);
			super.onChange(selfChange);
		}
		
	}

	/**
	 * �Ҷϵ绰
	 */
	public void endCall() {
		try {
			// ����servicemanager���ֽ���
			Class clazz = CallSmsSafeService.class.getClassLoader().loadClass("android.os.ServiceManager");
			
			Method method = clazz.getDeclaredMethod("getService", String.class);
			IBinder ibinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
			ITelephony.Stub.asInterface(ibinder).endCall();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * ���������ṩ��ɾ�����м�¼
	 */
	public void deleteCallLog(String incomingNumber) {
		ContentResolver resolver = getContentResolver();
		// ���м�¼uri��·��   CallLog.CONTENT_URI;
		Uri uri = Uri.parse("content://call_log/calls");
		
		resolver.delete(uri, "number = ?", new String[]{incomingNumber});
	}
}
