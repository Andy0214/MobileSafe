package com.ithaha.mobilesafe;

import com.ithaha.mobilesafe.service.AddressService;
import com.ithaha.mobilesafe.service.CallSmsSafeService;
import com.ithaha.mobilesafe.service.WatchDogService;
import com.ithaha.mobilesafe.ui.SettingClickView;
import com.ithaha.mobilesafe.ui.SettingItemView;
import com.ithaha.mobilesafe.utils.ServiceUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class SettingActivity extends Activity {
	
	/**
	 * �����Ƿ��Զ�����
	 */
	private SettingItemView siv_update;
	private SharedPreferences sp;
	/**
	 * �����Ƿ������������ʾ
	 */
	private SettingItemView siv_show_address;
	private Intent showAddressIntent;
	
	// ���ù�������ʾ��ı���
	private SettingClickView scv_changebg;
	
	// ����������
	private SettingItemView siv_callsms_safe;
	private Intent callSmsSafeIntent;
	
	// ������
	private SettingItemView siv_watchdog;
	private Intent watchdogIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		// �����Զ�����
		siv_update = (SettingItemView) findViewById(R.id.siv_update);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean update = sp.getBoolean("update", false);
		
		if(update) {
			// �Զ������Ѿ�����
			siv_update.setChecked(true);
		} else {
			// �Զ������Ѿ��ر�
			siv_update.setChecked(false);
		}
		
		siv_update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Editor edit = sp.edit();
				// �ж��Ƿ���ѡ��
				if(siv_update.isChecked()) {
					// �ر��Զ�������
					siv_update.setChecked(false);
					edit.putBoolean("update", false);
					
				} else {
					// �������Զ�����
					siv_update.setChecked(true);
					edit.putBoolean("update", true);
				}
				edit.commit();
			}
		});
		
		// ���������������ʾ
		siv_show_address = (SettingItemView) findViewById(R.id.siv_show_address);
		showAddressIntent = new Intent(this, AddressService.class);
		boolean serviceRunning = ServiceUtils.isServiceRunning(this, "com.ithaha.mobilesafe.service.AddressService");
		
		if(serviceRunning) {
			// ��������ط�����������
			siv_show_address.setChecked(true);
		} else {
			// ��������ط���û��������
			siv_show_address.setChecked(false);
		}
		
		siv_show_address.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// �ж��Ƿ���ѡ��
				if(siv_show_address.isChecked()) {
					// �رչ�������ʾ��
					siv_show_address.setChecked(false);
					stopService(showAddressIntent);
				} else {
					// ������������ʾ��
					siv_show_address.setChecked(true);
					startService(showAddressIntent);
				}
			}
		});
		
		// ���ú�������صı���
		final String[] items = {"��͸��","������","��ʿ��","������","ƻ����"};
		scv_changebg = (SettingClickView) findViewById(R.id.scv_changebg);
		scv_changebg.setTitle("��������ʾ����");
		int which = sp.getInt("which", 0);
		scv_changebg.setDesc(items[which]);
		scv_changebg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				int dd = sp.getInt("which", 0);
				
				// ����һ����ѡ��
				AlertDialog.Builder builder = new Builder(SettingActivity.this);
				builder.setTitle("��������ʾ����");
				builder.setSingleChoiceItems(items, dd, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// ����ѡ�����
						Editor edit = sp.edit();
						edit.putInt("which", which);
						edit.commit();
						scv_changebg.setDesc(items[which]);
						// ȡ���Ի���
						dialog.dismiss();
					}
				});
				builder.setNegativeButton("ȡ��", null);
				builder.show();
			}
		});
		
		// ��������������
		siv_callsms_safe = (SettingItemView) findViewById(R.id.siv_callsms_safe);
		callSmsSafeIntent = new Intent(this, CallSmsSafeService.class);
				
		siv_callsms_safe.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// �ж��Ƿ���ѡ��
				if(siv_callsms_safe.isChecked()) {
					// �رչ�������ʾ��
					siv_callsms_safe.setChecked(false);
					stopService(callSmsSafeIntent);
				} else {
					// ������������ʾ��
					siv_callsms_safe.setChecked(true);
					startService(callSmsSafeIntent);
				}
			}
		});
		
		// ����������
		siv_watchdog = (SettingItemView) findViewById(R.id.siv_watchdog);
		watchdogIntent = new Intent(this,WatchDogService.class);
		
		siv_watchdog.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String password = sp.getString("password", null);
				// �ж��Ƿ���ѡ��
				if(siv_watchdog.isChecked()) {
					// �رճ�������
					siv_watchdog.setChecked(false);
					stopService(watchdogIntent);
				} else {
					if(TextUtils.isEmpty(password)) {
						Toast.makeText(getApplicationContext(), "�Ƚ����ֻ�������������", 0).show();
						return ;
					}
					// ������������
					siv_watchdog.setChecked(true);
					startService(watchdogIntent);
				}
				
			}
		});
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		showAddressIntent = new Intent(this, AddressService.class);
		boolean serviceRunning = ServiceUtils.isServiceRunning(this, "com.ithaha.mobilesafe.service.AddressService");
		
		if(serviceRunning) {
			// ��������ط�����������
			siv_show_address.setChecked(true);
		} else {
			// ��������ط���û��������
			siv_show_address.setChecked(false);
		}
		
		boolean iscallSmsServiceRunning = ServiceUtils.isServiceRunning(this, "com.ithaha.mobilesafe.service.CallSmsSafeService");
		if(iscallSmsServiceRunning) {
			// ���������ط�����������
			siv_callsms_safe.setChecked(true);
		} else {
			// ���������ط���û��������
			siv_callsms_safe.setChecked(false);
		}
		
		boolean iswatchDogServiceRunning = ServiceUtils.isServiceRunning(this, "com.ithaha.mobilesafe.service.WatchDogService");
		if(iswatchDogServiceRunning) {
			// ������������
			siv_watchdog.setChecked(true);
		} else {
			// �رճ�������
			siv_watchdog.setChecked(false);
		}
	}
}
