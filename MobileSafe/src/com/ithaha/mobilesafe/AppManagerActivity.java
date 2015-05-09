package com.ithaha.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import com.ithaha.mobilesafe.db.dao.AppLockDao;
import com.ithaha.mobilesafe.domain.AppInfo;
import com.ithaha.mobilesafe.engine.AppInfoProvider;
import com.ithaha.mobilesafe.utils.DensityUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ���������
 * @author hello
 *
 */
public class AppManagerActivity extends Activity implements OnClickListener {

	private TextView tv_avail_rom;
	private TextView tv_avail_sd;
	private ListView lv_app_manager;
	private LinearLayout ll_loading;
	private TextView tv_status;
	/**
	 * ���е�Ӧ�ó������Ϣ
	 */
	private List<AppInfo> appInfos;
	/**
	 * �û�Ӧ�ó���ļ���
	 */
	private List<AppInfo> userAppInfos;

	/**
	 * ϵͳӦ�ó���ļ���
	 */
	private List<AppInfo> systemAppInfos;

	/**
	 * ��������������
	 */
	private PopupWindow popupWindow;
	
	private LinearLayout ll_share;
	private LinearLayout ll_start;
	private LinearLayout ll_uninstall;
	
	/**
	 * ���������Ŀ
	 */
	private AppInfo appInfo;
	private String TAG = "AppManagerActivity";
	
	private AppLockDao dao;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);

		tv_avail_rom = (TextView) findViewById(R.id.tv_avail_rom);
		tv_avail_sd = (TextView) findViewById(R.id.tv_avail_sd);
		lv_app_manager = (ListView) findViewById(R.id.lv_app_manager);
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		tv_status = (TextView) findViewById(R.id.tv_status);
		dao = new AppLockDao(this);
		
		long sdSize = getAvailSpace(Environment.getExternalStorageDirectory().getAbsolutePath());
		long romSize = getAvailSpace(Environment.getDataDirectory().getAbsolutePath());

		// ��ʾ�����ڴ��С  					Formats a content size to be in the form of bytes, kilobytes, megabytes, etc
		tv_avail_sd.setText("SD�����ÿռ䣺" + Formatter.formatFileSize(this, sdSize));
		tv_avail_rom.setText("�ڲ����ÿռ䣺" + Formatter.formatFileSize(this, romSize));

		ll_loading.setVisibility(View.VISIBLE);
		
		new Thread() {
			public void run() {
				appInfos = AppInfoProvider.getAppInfos(AppManagerActivity.this);
				userAppInfos = new ArrayList<AppInfo>();
				systemAppInfos = new ArrayList<AppInfo>();
				
				for (AppInfo appInfo : appInfos) {
					if (appInfo.isUserApp()) {
						userAppInfos.add(appInfo);
					} else {
						systemAppInfos.add(appInfo);
					}
				}

				// ����listview������������
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						lv_app_manager.setAdapter(new AppManagerAdapter());
						ll_loading.setVisibility(View.INVISIBLE);
					}
				});
			};
		}.start();

		/**
		 * ����������
		 */
		lv_app_manager.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			/**
			 * ������ʱ����õķ��� firstVisibleItem ��һ���ɼ���Ŀ��ListView�е�λ��
			 * 
			 */
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				dismissPopuWindow();
				if (userAppInfos != null && systemAppInfos != null) {
					if (firstVisibleItem > userAppInfos.size()) {
						tv_status.setText("ϵͳ����:" + systemAppInfos.size() + "��");
					} else {
						tv_status.setText("�û�����:" + userAppInfos.size() + "��");
					}
				}
			}
		});

		/**
		 * ����listview�ĵ���¼�
		 */
		lv_app_manager.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					return;
				} else if (position == (userAppInfos.size() + 1)) {
					return;
				} else if (position <= userAppInfos.size()) {
					// �û�����
					int newPosition = position - 1;
					appInfo = userAppInfos.get(newPosition);
				} else {
					// ϵͳ����
					int newPosition = position - 1 - userAppInfos.size() - 1;
					appInfo = systemAppInfos.get(newPosition);
				}

				dismissPopuWindow();
				View contentView = View.inflate(getApplicationContext(), R.layout.popup_app_item, null);
				
				ll_start = (LinearLayout) contentView.findViewById(R.id.ll_start);
				ll_share = (LinearLayout) contentView.findViewById(R.id.ll_share);
				ll_uninstall = (LinearLayout) contentView.findViewById(R.id.ll_uninstall);
				
				ll_start.setOnClickListener(AppManagerActivity.this);
				ll_share.setOnClickListener(AppManagerActivity.this);
				ll_uninstall.setOnClickListener(AppManagerActivity.this);
				
				
				popupWindow = new PopupWindow(contentView, -2,ViewGroup.LayoutParams.WRAP_CONTENT);
				// ����Ч���Ĳ��ű���Ҫ�����б�����ɫ
				popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
				
				int[] location = new int[2];
				view.getLocationInWindow(location);
				// �ڴ��������õĿ�߶������أ���Ҫ--> dip
				int dip = 60;
				int px = DensityUtils.dip2px(getApplicationContext(), dip);
				
				popupWindow.showAtLocation(parent, Gravity.LEFT | Gravity.TOP, px, location[1]);
				
				// ���Ŷ���
//				ScaleAnimation sa = new ScaleAnimation(0.3f, 1.0f, 0.3f, 1.0f, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.5f);
//				sa.setDuration(500);
//				
//				AlphaAnimation aa = new AlphaAnimation(0.5f, 0.5f);
//				aa.setDuration(500);
//				
//				AnimationSet set = new AnimationSet(false);
//				set.addAnimation(sa);
//				set.addAnimation(aa);
//				contentView.startAnimation(set);
			}
		});
	
		/**
		 * ����listview�ĳ����¼�
		 */
		lv_app_manager.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					return true;
				} else if (position == (userAppInfos.size() + 1)) {
					return true;
				} else if (position <= userAppInfos.size()) {
					// �û�����
					int newPosition = position - 1;
					appInfo = userAppInfos.get(newPosition);
				} else {
					// ϵͳ����
					int newPosition = position - 1 - userAppInfos.size() - 1;
					appInfo = systemAppInfos.get(newPosition);
				}
				System.out.println("�������:" + appInfo.getPackname());
				// �ж���Ŀ�Ƿ�����ڳ��������ݿ�����
				ViewHolder holder = (ViewHolder) view.getTag();
				if(dao.find(appInfo.getPackname())){
					// �������ĳ��򣬽��������½���
					dao.delete(appInfo.getPackname());
					holder.iv_status.setImageResource(R.drawable.unlock);
				} else {
					// ������������
					dao.add(appInfo.getPackname());
					holder.iv_status.setImageResource(R.drawable.lock);
				}
				
				return true;
			}
		});
	}

	private void dismissPopuWindow() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			popupWindow = null;
		}
	}

	private class AppManagerAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			// return appInfos.size();
			return userAppInfos.size() + 1 + systemAppInfos.size() + 1;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (position == 0) {
				// ��ʾ�û������ж��ٸ�С��ǩ
				TextView tv = new TextView(getApplicationContext());
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("�û�����:" + userAppInfos.size() + "��");
				return tv;
			} else if (position == userAppInfos.size() + 1) {
				// ��ʾϵͳ�����ж��ٸ�С��ǩ
				TextView tv = new TextView(getApplicationContext());
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("ϵͳ����:" + systemAppInfos.size() + "��");
				return tv;
			} else if (position <= userAppInfos.size()) {
				// �û�����
				appInfo = userAppInfos.get(position - 1);
			} else {
				// λ���Ǹ�ϵͳ������ʾ��
				appInfo = systemAppInfos.get(position - userAppInfos.size() - 2);
			}

			View view;
			ViewHolder holder;

			if (convertView != null && convertView instanceof RelativeLayout) {
				// �жϻ����Ƿ�Ϊ�պ��Ƿ��Ǻ��ʵ�����������
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(getApplicationContext(),R.layout.list_item_appinfo, null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) view
						.findViewById(R.id.iv_app_icon);
				holder.tv_name = (TextView) view.findViewById(R.id.tv_app_name);
				holder.tv_location = (TextView) view
						.findViewById(R.id.tv_app_location);
				holder.iv_status = (ImageView) view.findViewById(R.id.iv_status);

				view.setTag(holder);
			}
			holder.iv_icon.setImageDrawable(appInfo.getIcon());
			holder.tv_name.setText(appInfo.getName());
			if (appInfo.isInRom()) {
				holder.tv_location.setText("�ֻ��ڴ�" + "     uid" + appInfo.getUid());
			} else {
				holder.tv_location.setText("�ⲿ�洢" + "     uid" + appInfo.getUid());
			}
			if(dao.find(appInfo.getPackname())) {
				holder.iv_status.setImageResource(R.drawable.lock);
			} else {
				holder.iv_status.setImageResource(R.drawable.unlock);
			}
			
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

	static class ViewHolder {
		TextView tv_name;
		TextView tv_location;
		ImageView iv_icon;
		ImageView iv_status;
	}

	/**
	 * ��ȡĳ��Ŀ¼�Ŀ��ÿռ�
	 * 
	 * @param path
	 * @return
	 */
	private long getAvailSpace(String path) {

		StatFs statFs = new StatFs(path);
		statFs.getBlockCount(); // ��ȡ�����ĸ���
		long size = statFs.getBlockSize(); // ��ȡ�����Ĵ�С
		long count = statFs.getAvailableBlocks(); // ��ȡ���õķ�������

		return size * count;
	}

	@Override
	protected void onDestroy() {
		dismissPopuWindow();
		super.onDestroy();
	}

	/**
	 * ��Ŀ�еĵ���¼�
	 */
	@Override
	public void onClick(View v) {
		dismissPopuWindow();
		switch (v.getId()) {
		case R.id.ll_share:		// ����
			shareApplication();
			Log.i(TAG , "����" + appInfo.getName());
			break;
		case R.id.ll_start:		// ��ʼ
			startApplication();
			Log.i(TAG , "��ʼ: " + appInfo.getPackname());
			break;
			
		case R.id.ll_uninstall:	// ж��
			if(appInfo.isUserApp()) {
				uninstallApplication();
				Log.i(TAG , appInfo.getName());
			} else {
				Toast.makeText(this, "�޷�ж��ϵͳӦ��", 0).show();
//				Runtime.getRuntime().exec("");
			}
			break;
		default:
			break;
		}
	}

	/**
	 * ����Ӧ�ó���
	 */
	private void shareApplication() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.SEND");
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "�Ƽ���ʹ��һ����������ƽ�:" + appInfo.getName());
		startActivity(intent);
	}

	/**
	 * ж��Ӧ��
	 */
	private void uninstallApplication() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.setAction("android.intent.action.DELETE");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setData(Uri.parse("package:"+appInfo.getPackname()));
		startActivityForResult(intent, 0);
	}

	/**
	 * ����Ӧ�ó���
	 */
	private void startApplication() {
		// ��ѯӦ�ó�������activity
		PackageManager pm = getPackageManager();
//		Intent intent = new Intent();
//		intent.setAction("android.intent.action.MAIN");
//		intent.addCategory("android.intent.category.LAUNCHER");
//		// ��ѯ���ֻ������п���������activity
//		List<ResolveInfo> infos = pm.queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);
		
		Intent intent = pm.getLaunchIntentForPackage(appInfo.getPackname());
		if(intent != null) {
			startActivity(intent);
		} else {
			Toast.makeText(this, "�Բ����޷�������Ӧ��", 0).show();
		}
		
	}
	
	/**
	 * ж��Ӧ��֮��
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// ˢ�½���
		ll_loading.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				appInfos = AppInfoProvider.getAppInfos(AppManagerActivity.this);
				userAppInfos = new ArrayList<AppInfo>();
				systemAppInfos = new ArrayList<AppInfo>();
				for (AppInfo appInfo : appInfos) {
					if (appInfo.isUserApp()) {
						userAppInfos.add(appInfo);
					} else {
						systemAppInfos.add(appInfo);
					}
				}

				// ����listview������������
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						lv_app_manager.setAdapter(new AppManagerAdapter());
						ll_loading.setVisibility(View.INVISIBLE);
					}
				});
			};
		}.start();
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
}
