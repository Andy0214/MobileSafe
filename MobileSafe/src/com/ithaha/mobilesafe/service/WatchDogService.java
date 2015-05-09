package com.ithaha.mobilesafe.service;

import java.util.List;

import com.ithaha.mobilesafe.EnterPwdActivity;
import com.ithaha.mobilesafe.db.dao.AppLockDao;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

/**
 * ����ϵͳ���������״̬
 * @author hello
 *
 */
public class WatchDogService extends Service {

	private ActivityManager am;
	private boolean flag;
	private AppLockDao dao;
	private InnerReceiver innerReceiver;
	private String tempStopPackname;
	private ScreenOffReceiver receiver;
	private ScreenOffReceiver offReceiver;
	
	private List<String> protectPacknames;
	private Intent intent;
	private DataChangeReceiver dataChangeReceiver;
	
	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}
	
	private class InnerReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("���յ�����ʱֹͣ�Ĺ㲥");
			tempStopPackname = intent.getStringExtra("packname");
		}
	}

	private class ScreenOffReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// ��Ļ������
			tempStopPackname = null;
		}
	}
	
	private class DataChangeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			protectPacknames = dao.findAll();
		}
	}
	
	@Override
	public void onCreate() {
		
		// ��̬ע��㲥
		dataChangeReceiver = new DataChangeReceiver();
		registerReceiver(dataChangeReceiver, new IntentFilter("com.ithaha.mobilesafe.applockchange"));
		
		offReceiver = new ScreenOffReceiver();
		registerReceiver(offReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
		
		innerReceiver = new InnerReceiver();
		registerReceiver(innerReceiver, new IntentFilter("com.ithaha.mobilesafe.tempstop"));
		
		dao = new AppLockDao(this);
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		protectPacknames = dao.findAll();
		flag = true;
		
		new Thread(){
			public void run() {
				while(flag){
					List<RunningTaskInfo> infos = am.getRunningTasks(1);
					String packageName = infos.get(0).topActivity.getPackageName();
//					System.out.println("��ǰ�û������ĳ���:" + packageName);
					if(dao.find(packageName)) {		// ��ѯ���ݿ�̫���ˣ������ڴ�,�ĳɲ�ѯ�ڴ�  findAll()
//					if(protectPacknames.contains(packageName)) {
						// �ж����Ӧ�ó����Ƿ���Ҫ��ʱ����
						if(packageName.equals(tempStopPackname)) {
							
						} else {
							intent = new Intent(getApplicationContext(),EnterPwdActivity.class);
							// ������û������ջ��Ϣ�ģ��ڷ�����activity��Ҫָ�����activity���е�����ջ
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							// ����Ҫ��������İ���
							intent.putExtra("packname", packageName);
							startActivity(intent);
						}
					}
					
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
		
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		unregisterReceiver(innerReceiver);
		innerReceiver = null;
		
		unregisterReceiver(offReceiver);
		offReceiver = null;
		
		unregisterReceiver(dataChangeReceiver);
		dataChangeReceiver = null;
		
		flag = false;
		super.onDestroy();
	}
	
}
