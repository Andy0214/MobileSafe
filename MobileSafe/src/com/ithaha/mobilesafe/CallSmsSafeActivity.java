package com.ithaha.mobilesafe;

import java.util.List;

import com.ithaha.mobilesafe.db.dao.BlackNumberDao;
import com.ithaha.mobilesafe.domain.BlackNumberInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ͨѶ��ʿ,����������
 * @author hello
 *
 */
public class CallSmsSafeActivity extends Activity {
	public static final String TAG = "CallSmsSafeActivity";
	private ListView lv_callsms_safe;
	private List<BlackNumberInfo> infos;
	private BlackNumberDao dao;
	private CallSmsSafeAdapter adapter;
	private LinearLayout ll_loading;
	private int offset = 0;
	private int maxnumber = 20;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_sms_safe);
		
		lv_callsms_safe = (ListView) findViewById(R.id.lv_callsms_safe);
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		
		dao = new BlackNumberDao(this);
		
		fillData();
		
		// listviewע��һ�������¼��ļ�����
		lv_callsms_safe.setOnScrollListener(new OnScrollListener() {
			
			/**
			 * ��������״̬���ͱ仯
			 */
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:			// ����״̬
					// �жϵ�ǰlistview�Ĺ���λ��
					// ��ȡ���һ���ɼ���Ŀ�ڼ��������λ��
					int lastposition = lv_callsms_safe.getLastVisiblePosition();
					if(lastposition == infos.size()-1) {
						// �б��ƶ������һ��λ��
						offset+=maxnumber;
						fillData();
					}
					break;

				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:	// ��ָ��������
					
					break;
					
				case OnScrollListener.SCROLL_STATE_FLING:			// ���Ի���״̬
					
					break;
				default:
					break;
				}
			}
			
			/**
			 * ������ʱ�����
			 */
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
			}
		});
		

		lv_callsms_safe.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.i(TAG, "���ڽ��г���...");
				// �õ�item�ĺ���������
				TextView tv_black_number = (TextView) view.findViewById(R.id.tv_black_number);
				
				String blackNumber = tv_black_number.getText().toString().trim();
				
				// ���ݺ��������������Һ�����ģʽ
				String mode = dao.findMode(blackNumber);
				alterBlackNumber(view, blackNumber, mode);
				
				String newMode = dao.findMode(blackNumber);
				if(mode != newMode) {
					infos.remove(position);
				}
				
				return true;
			}
			
		});
		
	}

	private void fillData() {
		ll_loading.setVisibility(View.VISIBLE);
		new Thread(){
			public void run() {
				if(infos == null) {
					infos = dao.findPart(offset, maxnumber);
				} else {
					// ԭ���Ѿ����ع�������
					infos.addAll(dao.findPart(offset, maxnumber));
				}
				// �����߳��и��½���
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						ll_loading.setVisibility(View.INVISIBLE);
						if(adapter == null) {
							adapter = new CallSmsSafeAdapter();
							lv_callsms_safe.setAdapter(adapter);			
						} else {
							adapter.notifyDataSetChanged();
						}
					}
				});
			};
		}.start();
	}
	
	private class CallSmsSafeAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return infos.size();
		}
		//�ж��ٸ���Ŀ����ʾ����������ͻᱻ���ö��ٴ�
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			//1.�����ڴ���view���󴴽��ĸ���
			if(convertView==null){
				Log.i(TAG,"�����µ�view����"+position);
				//��һ�������ļ�ת����  view����
				view  = View.inflate(getApplicationContext(), R.layout.list_item_callsms, null);
				//2.�����Ӻ��Ӳ�ѯ�Ĵ���  �ڴ��ж���ĵ�ַ��
				holder = new ViewHolder();
				holder.tv_number = (TextView) view.findViewById(R.id.tv_black_number);
				holder.tv_mode = (TextView) view.findViewById(R.id.tv_black_mode);
				holder.iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
				//��������������ʱ���ҵ����ǵ����ã�����ڼ��±������ڸ��׵Ŀڴ�
				view.setTag(holder);
			}else{
				Log.i(TAG,"��������ʷ��view���󣬸�����ʷ�����view����"+position);
				view = convertView;
				holder = (ViewHolder) view.getTag();//5%
			}
			holder.tv_number.setText(infos.get(position).getNumber());
			String mode = infos.get(position).getMode();
			if("1".equals(mode)){
				holder.tv_mode.setText("�绰����");
			}else if("2".equals(mode)){
				holder.tv_mode.setText("��������");
			}else{
				holder.tv_mode.setText("ȫ������");
			}
			holder.iv_delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder = new Builder(CallSmsSafeActivity.this);
					builder.setTitle("����");
					builder.setMessage("ȷ��Ҫɾ��������¼ô��");
					builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//ɾ�����ݿ������
							dao.delete(infos.get(position).getNumber());
							//���½��档
							infos.remove(position);
							//֪ͨlistview��������������
							adapter.notifyDataSetChanged();
						}
					});
					builder.setNegativeButton("ȡ��", null);
					builder.show();
				}
			});
			return view;
		}
		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
	}
	/**
	 * view���������
	 *��¼���ӵ��ڴ��ַ��
	 *�൱��һ�����±�
	 */
	static class ViewHolder{
		TextView tv_number;
		TextView tv_mode;
		ImageView iv_delete;
	}
	
	
	private EditText et_blacknumber;
	private CheckBox cb_phone;
	private CheckBox cb_sms;
	private Button bt_ok;
	private Button bt_cancel;
	
	/**
	 * ���Ӻ�������Ա
	 * @param view
	 */
	public void addBlackNumber(View view){
		AlertDialog.Builder builder = new Builder(this);
		final AlertDialog dialog = builder.create();
		View contentView = View.inflate(this, R.layout.dialog_add_blacknum, null);
		et_blacknumber = (EditText) contentView.findViewById(R.id.et_blacknum);
		cb_phone = (CheckBox) contentView.findViewById(R.id.cb_phone);
		cb_sms = (CheckBox) contentView.findViewById(R.id.cb_sms);
		bt_cancel = (Button) contentView.findViewById(R.id.cancel);
		bt_ok = (Button) contentView.findViewById(R.id.ok);
		dialog.setView(contentView, 0, 0, 0, 0);
		dialog.show();
		
		bt_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		bt_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String blacknumber = et_blacknumber.getText().toString().trim();
				if(dao.find(blacknumber)) {
					Toast.makeText(getApplicationContext(), "�ú������ں�������", 0).show();
					return;
				} 
				
				
				if(TextUtils.isEmpty(blacknumber)){
					Toast.makeText(getApplicationContext(), "���������벻��Ϊ��", 0).show();
					return;
				}
				String mode ;
				if(cb_phone.isChecked()&&cb_sms.isChecked()){
					//ȫ������
					mode = "3";
				}else if(cb_phone.isChecked()){
					//�绰����
					mode = "1";
				}else if(cb_sms.isChecked()){
					//��������
					mode = "2";
				}else{
					Toast.makeText(getApplicationContext(), "��ѡ������ģʽ", 0).show();
					return;
				}
				//���ݱ��ӵ����ݿ�
				dao.add(blacknumber, mode);
				//����listview������������ݡ�
				BlackNumberInfo info = new BlackNumberInfo();
				info.setMode(mode);
				info.setNumber(blacknumber);
				infos.add(0, info);
				//֪ͨlistview�������������ݸ����ˡ�
				adapter.notifyDataSetChanged();
				dialog.dismiss();
			}
		});
	}
	
	/**
	 * �޸ĺ�������������
	 * @param view
	 */
	public void alterBlackNumber(final View view, final String blackNumber, String mode) {
		AlertDialog.Builder builder = new Builder(this);
		final AlertDialog dialog = builder.create();
		View contentView = View.inflate(this, R.layout.dialog_add_blacknum, null);
		et_blacknumber = (EditText) contentView.findViewById(R.id.et_blacknum);
		et_blacknumber.setText(blackNumber);
		// Set the enabled state of this view. The interpretation of the enabled state varies by subclass.
		et_blacknumber.setEnabled(false);
		cb_phone = (CheckBox) contentView.findViewById(R.id.cb_phone);
		cb_sms = (CheckBox) contentView.findViewById(R.id.cb_sms);
		
		if("1".equals(mode)) {
			// �绰����
			cb_phone.setChecked(true);
			cb_sms.setChecked(false);
		} else if("2".equals(mode)){
			// ��������
			cb_phone.setChecked(false);
			cb_sms.setChecked(true);
		} else if("3".equals(mode)) {
			// ȫ������
			cb_phone.setChecked(true);
			cb_sms.setChecked(true);
		}
		bt_cancel = (Button) contentView.findViewById(R.id.cancel);
		bt_ok = (Button) contentView.findViewById(R.id.ok);
		dialog.setView(contentView, 0, 0, 0, 0);
		dialog.show();
		bt_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		bt_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				String newMode ;
				if(cb_phone.isChecked()&&cb_sms.isChecked()){
					//ȫ������
					newMode = "3";
				}else if(cb_phone.isChecked()){
					//�绰����
					newMode = "1";
				}else if(cb_sms.isChecked()){
					//��������
					newMode = "2";
				}else{
					Toast.makeText(getApplicationContext(), "��ѡ������ģʽ", 0).show();
					return;
				}
								
				// �������ݿ�
				dao.update(blackNumber, newMode);
								
				//֪ͨlistview�������������ݸ����ˡ�
				adapter.notifyDataSetChanged();
				dialog.dismiss();
				fillData();
			}
		});
	}
}
