package com.ithaha.mobilesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceUtils {

	/**
	 * ����ĳ�������Ƿ���
	 * @param context
	 * @param nameService ������
	 * @return
	 */
	public static boolean isServiceRunning(Context context, String nameService) {
		// ActivityManager���Թ���Activity��Service
		ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> infos = am.getRunningServices(100);
		for(RunningServiceInfo info : infos) {
			// �õ��������еķ��������
			String name = info.service.getClassName();
			if(nameService.equals(name)) {
				return true;
			}
		}
		
		return false;
	}
}
