package com.ithaha.mobilesafe.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.ithaha.mobilesafe.domain.AppInfo;

/**
 * �����ṩ�ֻ����氲װ������Ӧ�ó�����Ϣ
 * @author hello
 *
 */
public class AppInfoProvider {

	/**
	 * ��ȡ���еİ�װ��Ӧ�ó������Ϣ
	 * @return
	 */
	public static List<AppInfo> getAppInfos(Context context) {
		
		PackageManager pm = context.getPackageManager();
		// ���еİ�װ��ϵͳ�ϵ�Ӧ�ó������Ϣ
		List<PackageInfo> packInfos = pm.getInstalledPackages(0);
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		
		for(PackageInfo packInfo : packInfos) {
			AppInfo appInfo = new AppInfo();
			// packageInfo �൱��һ��Ӧ�ó���apk�����嵥�ļ�
			String packageName = packInfo.packageName;
			Drawable icon = packInfo.applicationInfo.loadIcon(pm);
			String name = packInfo.applicationInfo.loadLabel(pm).toString();
			// Ӧ�ó�����Ϣ�ı��
			int flags = packInfo.applicationInfo.flags;
			if((flags&ApplicationInfo.FLAG_SYSTEM) == 0) {
				// �û�����
				appInfo.setUserApp(true);
			} else {
				// ϵͳ����
				appInfo.setUserApp(false);
			}
			if((flags&ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0) {
				// �ֻ����ڴ���
				appInfo.setInRom(true);
			} else {
				// SD����
				appInfo.setInRom(false);
			}
			
			int uid = packInfo.applicationInfo.uid;	//����ϵͳ�����Ӧ�ó����һ���̶���id��
			
//			File rcvFile = new File("/proc/uid_stat/" + uid + "/tcp_rcv");
//			File sndFile = new File("/proc/uid_stat/" + uid + "/tcp_snd");
			
			appInfo.setUid(uid);
			appInfo.setIcon(icon);
			appInfo.setName(name);
			appInfo.setPackname(packageName);
			
			appInfos.add(appInfo);
		}
		
		return appInfos;
	}
}
