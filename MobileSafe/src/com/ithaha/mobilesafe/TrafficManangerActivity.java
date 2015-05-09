package com.ithaha.mobilesafe;

import java.util.List;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.Bundle;

/**
 * ��ȡ����ͳ��
 * @author hello
 *
 */
public class TrafficManangerActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_traffic_manager);
		
		// 1.��ȡһ����������
		PackageManager pm = getPackageManager();
		
		// 2.�����ֻ�����ϵͳ����ȡ���е�Ӧ�ó����uid
		List<ApplicationInfo> appLicationInfos = pm.getInstalledApplications(0);
		for(ApplicationInfo info : appLicationInfos) {
			int uid = info.uid;
			long uidTxBytes = TrafficStats.getUidTxBytes(uid);		// �ϴ�������
			long uidRxBytes = TrafficStats.getUidRxBytes(uid);		// ���ص�����
			// ��������ֵΪ-1�������Ӧ�ó���û�в������������߲���ϵͳ��֧������ͳ��
		}
	}
}
