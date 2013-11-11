package com.abyss.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import android.util.Xml;

public class ToastUtil {
	public static List<Map<String,String>> getXml(URL url) throws XmlPullParserException, IOException {
		Map<String,String> xmiMap = new HashMap<String,String>();
		List<Map<String,String>> xmlMapList = new ArrayList<Map<String,String>>();

		InputStream istream = null;
		try {
            //URLクラスのインスタンス作成   
            //URL url = new URL("http://zins-work-shop.6.ql.bz/get_item.php?item_count="+count);   
            //URL url = new URL("http://doryu.dix.asia/get_item.php?item_count="+count);
            //URL url = new URL("http://doryu.dix.asia/get_v_item.php?ver=0");
            //コネクション開いて接続   
            URLConnection con = url.openConnection();   
            //接続先からストリーム読み込み   
			istream = con.getInputStream();
		} catch (IOException e) {
			
		}  
            // XMLPullParserの使用準備
            XmlPullParser parser = Xml.newPullParser();
            // XMLファイルのストリーム情報を取得
            InputStreamReader isr = new InputStreamReader(istream);
            // XMLPullParserにXMLファイルのストリームを設定
            parser.setInput(isr);
             
            // タグ名
            String tag = "";
            // 値
            String value = "";
            // XMLの解析
            
            
            for (int type = parser.getEventType(); type != XmlPullParser.END_DOCUMENT;
                    type = parser.next()) {
                switch(type) {
                case XmlPullParser.START_TAG: // 開始タグ
                    tag = parser.getName();
                    break;
                case XmlPullParser.TEXT: // タグの内容
                    value = parser.getText();
                    // 空白で取得したものは全て処理対象外とする
                    if(value.trim().length() != 0) {
                        // 取得した結果をTextViewに設定
                    	xmiMap.put(tag, value);
                    }
                    break;
                case XmlPullParser.END_TAG: // 終了タグ
                	tag = parser.getName();
                	if(tag.equals("row")){
                		xmlMapList.add(new HashMap<String,String>(xmiMap));
                	}
                    break;
                }
            }
		return xmlMapList;
	}
}
