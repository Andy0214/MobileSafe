package com.ithaha.mobilesafe.service;

import com.ithaha.mobilesafe.NumberAddressQueryActivity;
import com.ithaha.mobilesafe.R;
import com.ithaha.mobilesafe.db.dao.NumAddressQueryUtils;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class AddressService extends Service {

	/**
	 * ��������
	 */
	private TelephonyManager tm;
	private MyPhoneStateListener listener;
	private OutCallReceiver receiver;
	/**
	 * ���������
	 */
	private WindowManager wm;
	private View view;
	private WindowManager.LayoutParams params;
	private SharedPreferences sp;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private class MyPhoneStateListener extends PhoneStateListener {
		/**
		 * state --> ״̬
		 * incomingNumber --> �绰����
		 */
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:	// ���������ʱ��,��ʵҲ�������ʱ��
				
				// ���ݵõ���������� ��ѯ������
				String address = NumAddressQueryUtils.queryNumber(incomingNumber);
//				Toast.makeText(getApplicationContext(), "���������:" + address, 1).show();
				myToast(address);
				
				break;
			case TelephonyManager.CALL_STATE_IDLE:		// �绰�Ŀ���״̬,
				// �����Զ�����˾��view
				if(view != null) {
					wm.removeView(view);
				}
				break;

			default:
				break;
			}
		}
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		// ��������
		listener = new MyPhoneStateListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		
		// �ô���ע��㲥������
		receiver = new OutCallReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(receiver, filter);
		
		// ʵ�������������
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		// ȡ����������
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
		
		// �ô���ȡ���㲥������
		unregisterReceiver(receiver);
		receiver = null;
	}
	
	/**
	 * ����������ڲ���
	 * @author hello
	 *
	 */
	class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// ����ȥ�ĵ绰����
			String phone = getResultData();
			String address = NumAddressQueryUtils.queryNumber(phone);
//			Toast.makeText(context, address, 1).show();
			myToast(address);
		}

	}
	
	/**
	 * �Զ�����˾
	 * @param address
	 */
	private void myToast(String address) {
		view = View.inflate(this, R.layout.address_show, null);
		TextView textViwe = (TextView) view.findViewById(R.id.tv_address);
		
		// ˫������
		final long[] mHits = new long[2];
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);
				mHits[mHits.length - 1] = SystemClock.uptimeMillis();
				if(mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
					// ˫������...
					params.x = wm.getDefaultDisplay().getWidth() / 2 - view.getWidth() / 2;
					wm.updateViewLayout(view, params);
					
					Editor edit = sp.edit();
					edit.putInt("lastx", params.x);
					edit.putInt("lasty", params.y);
					edit.commit();
				}
			}
		});
		// ��view��������һ�������ļ�����
		view.setOnTouchListener(new OnTouchListener() {
			// ������ָ�ĳ�ʼ��λ��
			int startX;
			int startY;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:		// ��ָ������Ļ
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					
					break;

				case MotionEvent.ACTION_MOVE:		// ��ָ����Ļ���ƶ�
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();
					
					// ƫ����
					int dx = newX - startX;
					int dy = newY - startY;

					// ����ImageView�ڴ����λ��
					params.x += dx;
					params.y += dy;
					
					// ���Ǳ߽�����
					if(params.x<0) {
						params.x = 0;
					}
					if(params.y<0) {
						params.y = 0;
					}
					if(params.x > (wm.getDefaultDisplay().getWidth() - view.getWidth())) {
						params.x = wm.getDefaultDisplay().getWidth() - view.getWidth();
					}
					if(params.y > (wm.getDefaultDisplay().getHeight() - view.getHeight())) {
						params.y = wm.getDefaultDisplay().getHeight() - view.getHeight();
					}
					
					wm.updateViewLayout(v, params);
					
					// ���³�ʼ����ָ�Ŀ�ʼ������λ��
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					
					break;
					
				case MotionEvent.ACTION_UP:			// ��ָ�뿪��Ļ
					// ��¼�ؼ�������Ļ���Ͻǵ�����
					Editor edit = sp.edit();
					edit.putInt("lastx", params.x);
					edit.putInt("lasty", params.y);
					edit.commit();
					
					break;
				default:
					break;
				}
				return true;						// �¼�������ϣ���Ҫ�ø��ؼ�����������Ӧ�����¼���
			}
		});
		
		
		textViwe.setText(address);
		// "��͸��","������","��ʿ��","������","ƻ����"
		int[] ids = {R.drawable.call_locate_white, R.drawable.call_locate_orange
				,R.drawable.call_locate_blue,R.drawable.call_locate_gray,R.drawable.call_locate_green}; 
		sp = getSharedPreferences("config", MODE_PRIVATE);
		textViwe.setBackgroundResource(ids[sp.getInt("which", 0)]);
		
		params = new WindowManager.LayoutParams();
		
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        // ���������ϽǶ���
        params.gravity = Gravity.TOP + Gravity.LEFT;
        // ���������ߺ��ϱ�100
        params.x = sp.getInt("lastx", 0);
        params.y = sp.getInt("lasty", 0);
        
        
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        // androidϵͳ������е绰���ȼ���һ�ִ������ͣ���Ҫ���Ȩ��
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        params.setTitle("Toast");
		
		
		wm.addView(view, params);
		
	}
}


