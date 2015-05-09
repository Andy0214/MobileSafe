package com.ithaha.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * �������ݿ��ѯ��ҵ����
 * @author hello
 *
 */
public class AntivirusDao {
	/**
	 * ��ѯһ��md5�Ƿ��ڲ������ݿ��д���
	 * @param md5
	 * @return
	 */
	public static boolean isVirus(String md5) {
		boolean result = false;
		String path = "/data/data/com.ithaha.mobilesafe/files/antivirus.db";
		// �򿪲������ݿ�
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select * from datable where md5=?", new String[]{md5});
		if(cursor.moveToNext()) {
			result = true;
		}
		cursor.close();
		db.close();
		return result;
	}
}
