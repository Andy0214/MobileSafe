package com.ithaha.mobilesafe.service;

import java.io.IOException;
import java.io.InputStream;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.TextView;

public class GPSService extends Service {

	// �õ�λ�÷���
	private LocationManager lm;
	private MyLocationListener listener;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		lm = (LocationManager) getSystemService(LOCATION_SERVICE);

		listener = new MyLocationListener();
		// ��λ���ṩ����������
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String provider = lm.getBestProvider(criteria, true);
		lm.requestLocationUpdates(provider, 0, 0, listener);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// ȡ������λ�÷���
		lm.removeUpdates(listener);
		listener = null;
	}

	class MyLocationListener implements LocationListener {

		/**
		 * ��λ�øı��ʱ��ص�
		 */
		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub

			String longitude = "j:" + location.getLongitude() + "\n";
			String latitude = "w��" + location.getLatitude() + "\n";
			String accuracy = "a:" + location.getAccuracy() + "\n";

//			try {
//				// �ѱ�׼��GPSת���ɻ�������
//				InputStream is = getAssets().open("axisoffset.dat");
//				ModifyOffset offset = ModifyOffset.getInstance(is);
//				
//				PointDouble newPoint = offset.s2c(new PointDouble(location.getLongitude(), location.getLatitude()));
//				longitude = "j" + offset.X + "\n";
//				latitude = "w" + offset.Y + "\n";
//				
//				
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
			
			// �����Ÿ���ȫ����
			SharedPreferences sp = getSharedPreferences("confgi", MODE_PRIVATE);
			Editor edit = sp.edit();
			edit.putString("lastlocation", longitude + latitude + accuracy);
			edit.commit();
		}

		/**
		 * ��״̬�����ı�ص��� ����->�ر� or �ر� -> ����
		 */
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

		/**
		 * ĳһ��λ���ṩ�߿�ʹ���ˣ��ͻص�
		 */
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		/**
		 * ĳһ��λ���ṩ�߲���ʹ���ˣ��ͻص�
		 */
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

	}
}
