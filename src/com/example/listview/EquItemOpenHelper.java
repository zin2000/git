package com.example.listview;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.abyss.util.XmlUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class EquItemOpenHelper extends ItemOpenHelper {
	String tableName = null;
	public EquItemOpenHelper(Context context, String name,
			int version, String tableName) {
		super(context, name, version, tableName);
		this.tableName = tableName;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table "+tableName+" (" +
				"_id integer primary key autoincrement not null, " +
				"item_detail_id varchar(10) NOT NULL ," +
				"item_type int(16) NOT NULL," +
				"item_img_id int(16) NULL," +
				"item_name varchar(64) NOT NULL," +
				"item_text varchar(64) NOT NULL," +
				"item_count int(16) NOT NULL default '0'," +
				"max_use_count int(16) NOT NULL default '0'," +
				"use_count int(16) NOT NULL default '0'," +
				"point int(16) NOT NULL default '0'," +
				"skill_id int(16) NOT NULL default '0'," +
				"equ_flag tinyint(1) NOT NULL default '0'," +
				"item_img_binary text NOT NULL," +
				"item_version int(16) NOT NULL default '0');");
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
	/**
	 * @throws IOException 
	 * @throws XmlPullParserException 
	 * @throws MalformedURLException 
	 * @throws CloneNotSupportedException 
	 * 
	 */
	public void setNewItemInsert() throws MalformedURLException, XmlPullParserException, IOException{
		
		String getv = "SELECT IFNULL(MAX(item_version),0) as max_v FROM "+tableName;
		Cursor c = getReadableDatabase().rawQuery(getv, null);
		c.moveToFirst();
		int userVersion = c.getInt(c.getColumnIndex("max_v"));
	
		int dbVersion=0;
		List<Map<String,String>> versionMap = XmlUtil.getXml(new URL("http://doryu.dix.asia/get_equ_item_max_version.php"));
		if(versionMap.size()==1){
			dbVersion =  Integer.parseInt(versionMap.get(0).get("item_version"));
		}
		if(dbVersion>userVersion){
			List<Map<String,String>> updateMap = XmlUtil.getXml(new URL("http://doryu.dix.asia/get_equ_item_v_to_v.php?user_v="+userVersion+"&db_v="+dbVersion));
    		//setItemInsert(item_list_wdb, "item_list", updateMap);
			getWritableDatabase().beginTransaction();
			try {
				for(Map<String,String> map : updateMap){
					MultiLineListRowImpl row = MultiLineListRowImpl.create().setXmlMap(map);
					ContentValues cv = row.getItemDataToContentValue();
					getWritableDatabase().insert(tableName, null, cv);
				}
				getWritableDatabase().setTransactionSuccessful();
			}finally{
				// トランザクションの終了
				getWritableDatabase().endTransaction();
			}
		}
	}
}
