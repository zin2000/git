package com.example.listview;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DonjonOpenHelper extends SQLiteOpenHelper {
	String tableName = null;
	public DonjonOpenHelper(Context context, String name,
			int version, String tableName) {
		super(context, name, null, version);
		this.tableName = tableName;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table "+tableName+" (" +
				"_id integer primary key autoincrement not null, " +
				"d_floor int(16) NOT NULL ," +
				"current_time int(64) NOT NULL," +
				"item_count int(16) NOT NULL," +
				"enemy_count int(16) NOT NULL," +
				"current_area int(16) NOT NULL," +
				"area_map text NOT NULL," +
				"level int(16) NOT NULL," +
				"event_flag text NOT NULL);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion) {
     // 指定したテーブルのカラム構成をチェックし、
     // 同名のカラムについてはアップグレード後もデータを引き継ぎます。
     // 同名のカラムで型に互換性がない場合はエラーになるので注意。

     // 更新対象のテーブル
     final String targetTable = tableName;
     db.beginTransaction();
     try {
	     // 元カラム一覧
	     final List<String> columns = getColumns(db, targetTable);
	     // 初期化
	     db.execSQL("ALTER TABLE " + targetTable + " RENAME TO temp_"+ targetTable);
	     onCreate(db);
	     // 新カラム一覧
	     final List<String> newColumns = getColumns(db, targetTable);
	      
	     // 変化しないカラムのみ抽出
	     columns.retainAll(newColumns);
	      
	     // 共通データを移す。(OLDにしか存在しないものは捨てられ, NEWにしか存在しないものはNULLになる)
	     final String cols = join(columns, ",");
	     db.execSQL(String.format(
	     "INSERT INTO %s (%s) SELECT %s from temp_%s", targetTable,
	     cols, cols, targetTable));
	     // 終了処理
	     db.execSQL("DROP TABLE temp_" + targetTable);
	     db.setTransactionSuccessful();
	     } finally {
	    	 db.endTransaction();
	     }
	}
	
	/**
	 * 指定したテーブルのカラム名リストを取得する。
	 * 
	 * @param db
	 * @param tableName
	 * @return カラム名のリスト
	 */
	protected static List<String> getColumns(SQLiteDatabase db, String tableName) {
	List<String> ar = null;
	Cursor c = null;
	try {
	c = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 1", null);
	if (c != null) {
	ar = new ArrayList<String>(Arrays.asList(c.getColumnNames()));
	}
	} finally {
	if (c != null)
	c.close();
	}
	return ar;
	}
	 
	/**
	 * 文字列を任意の区切り文字で連結する。
	 * 
	 * @param list
	 * 文字列のリスト
	 * @param delim
	 * 区切り文字
	 * @return 連結後の文字列
	 */
	protected static String join(List<String> list, String delim) {
	final StringBuilder buf = new StringBuilder();
	final int num = list.size();
	for (int i = 0; i < num; i++) {
	if (i != 0)
	buf.append(delim);
	buf.append((String) list.get(i));
	}
	return buf.toString();
	}
	
}
