package com.ithaha.mobilesafe;

import com.ithaha.mobilesafe.utils.MD5Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {

	private GridView list_home;
	private static String[] names = {
									"�ֻ�����","ͨѶ��ʿ","�������",
									"���̹���","����ͳ��","�ֻ�ɱ��",
									"��������","�߼�����","��������"};
	
	private static int[] ids = {
									R.drawable.safe, R.drawable.callmsgsafe, R.drawable.app,
									R.drawable.taskmanager, R.drawable.netmanager, R.drawable.trojan,
									R.drawable.sysoptimize, R.drawable.atools, R.drawable.settings};

	private SharedPreferences sp;
	private Editor edit;
	private EditText et_setup_pwd;
	private EditText et_setup_confirm;
	private Button ok;
	private Button cancel;
	private AlertDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		sp = getSharedPreferences("config", MODE_PRIVATE);
		edit = sp.edit();
		
		list_home = (GridView) findViewById(R.id.list_home);
		list_home.setAdapter(new MyAdapter());
		list_home.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = null;
				switch (position) {
				case 0:		// �����ֻ�����
					showLostFindDialog();
					break;
					
				case 1:		// ����ͨѶ��ʿ
					intent = new Intent(HomeActivity.this,CallSmsSafeActivity.class);
					startActivity(intent);
					break;
					
				case 2:		// ���������
					intent = new Intent(HomeActivity.this,AppManagerActivity.class);
					startActivity(intent);
					break;
					
				case 3:		// ���̹�����
					intent = new Intent(HomeActivity.this,TaskManagerActivity.class);
					startActivity(intent);
					break;
					
				case 4:		// ����ͳ��
					intent = new Intent(HomeActivity.this,TrafficManangerActivity.class);
					startActivity(intent);
					break;
				
				case 5:		// �ֻ�ɱ��
					intent = new Intent(HomeActivity.this,AntiVirusActivity.class);
					startActivity(intent);
					break;
					
				case 6:		// ��������
					intent = new Intent(HomeActivity.this,ClearCacheActivity.class);
					startActivity(intent);
					break;
					
				case 7:		// ����߼�����
					intent = new Intent(HomeActivity.this,AtoolsActivity.class);
					startActivity(intent);
					break;
					
				case 8:		// ������������
					intent = new Intent(HomeActivity.this,SettingActivity.class);
					startActivity(intent);
					break;

				default:
					break;
				}
			}
		});
		
	}
	
	/**
	 * ���÷�������
	 */
	protected void showLostFindDialog() {
		// �ж��Ƿ����ù�����
		if(isSetupPwd()) {
			// �Ѿ����ù������ˣ�������������Ի���
			showEnterDialog();
		} else {
			// û���������룬������������ĶԻ���
			showSetupPwdDialog();
		}
	}

	/**
	 * ��������ĶԻ���
	 */
	private void showSetupPwdDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
		// �Զ���һ�������ļ�
		View view = View.inflate(HomeActivity.this, R.layout.dialog_setup_password, null);
		et_setup_pwd = (EditText) view.findViewById(R.id.et_setup_pwd);
		et_setup_confirm = (EditText) view.findViewById(R.id.et_setup_confirm);
		ok = (Button) view.findViewById(R.id.ok);
		cancel = (Button) view.findViewById(R.id.cancel);
		
		ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// ȡ������
				String password = et_setup_pwd.getText().toString().trim();
				String password2 = et_setup_confirm.getText().toString().trim();
				
				if(TextUtils.isEmpty(password) || TextUtils.isEmpty(password2)) {
					Toast.makeText(HomeActivity.this, "���벻��Ϊ��", 0).show();
					return ;
				}
				if(password.equals(password2)) {
					// ���������������ͬ���������룬�����Ի��򣬽����ֻ�����ҳ��
					edit.putString("password", MD5Utils.md5Password(password));
					edit.commit();
					dialog.dismiss();
					Intent intent = new Intent(HomeActivity.this,LostFindActivity.class);
					startActivity(intent);

				} else {
					// ���������������벻��ͬ
					Toast.makeText(HomeActivity.this, "�������벻һ��", 0).show();
					return;
				}
			}
		});
		
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
//		builder.setView(view);
		// Creates a AlertDialog with the arguments supplied to this builder.
		dialog = builder.create();
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
	}

	/**
	 * ��������Ի���
	 */
	private void showEnterDialog() {
		AlertDialog.Builder builder = new Builder(HomeActivity.this);
		// �Զ���һ�������ļ�
		View view = View.inflate(HomeActivity.this, R.layout.dialog_enter_password, null);
		et_setup_pwd = (EditText) view.findViewById(R.id.et_setup_pwd);
		ok = (Button) view.findViewById(R.id.ok);
		cancel = (Button) view.findViewById(R.id.cancel);
		
		ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// ȡ������
				String password = et_setup_pwd.getText().toString().trim();
				String password2 = sp.getString("password", null);
				
				if(TextUtils.isEmpty(password)) {
					Toast.makeText(HomeActivity.this, "���벻��Ϊ��", 0).show();
					return ;
				}
				
				if(MD5Utils.md5Password(password).equals(password2)) {
					// ������ȷ�������Ի��򣬽����ֻ�����ҳ��
					dialog.dismiss();
					Intent intent = new Intent(HomeActivity.this,LostFindActivity.class);
					startActivity(intent);
					
				} else {
					// �������
					Toast.makeText(HomeActivity.this, "�������", 0).show();
					et_setup_pwd.setText("");
					return ;
				}
			}
		});
		
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
//		builder.setView(view);
		dialog = builder.create();
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
	}

	/**
	 * �ж��Ƿ����ù�����
	 * @return
	 */
	private boolean isSetupPwd() {
		String password = sp.getString("password", null);
		
//		if(TextUtils.isEmpty(password)) {
//			// û������
//			return false;
//		} else {
//			return true;
//		}
		
		return !TextUtils.isEmpty(password);
		
	}
	
	
	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return names.length;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View view = View.inflate(HomeActivity.this, R.layout.list_item_home, null);
			ImageView iv_item = (ImageView) view.findViewById(R.id.iv_item);
			TextView tv_item = (TextView) view.findViewById(R.id.tv_item);
			
			tv_item.setText(names[position]);
			iv_item.setImageResource(ids[position]);
			
			return view;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
		
	}
}
