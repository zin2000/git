package com.example.listview;

import java.util.Map;

import android.content.ContentValues;

/**
 * 複数行表示可能なListのRowです。

 *
 */
public interface MultiLineListRow {
	
	/**
	 * 先頭に付与するイメージのIDを取得します。
	 * 
	 * もし先頭にイメージを表示しない場合はnullが返却されます。
	 * @return 先頭に付与するイメージのID
	 */
	Integer getPrefixImageId();
	
	/**
	 * 末尾に付与するイメージのIDを取得します。
	 * 
	 * もし末尾にイメージを表示しない場合はnullが返却されます。
	 * @return 末尾に付与するイメージのID
	 */
	Integer getSuffixImageId();
	
	/**
	 * 表示するテキストの行数を取得します。
	 * @return テキストの行数
	 */
	int sieze();
	
	/**
	 * 表示するテキストを取得します。
	 * 
	 * @param position 表示する位置
	 * @return 表示するテキスト
	 */
	String getText(int position);
	
	/**
	 * 表示するテキストのサイズを取得します。
	 * 
	 * @param position 表示する位置
	 * @return 表示するテキストのサイズ
	 */
	float getTextSize(int position);
	
	public String getBase64Image();
	public MultiLineListRowImpl setBase64Image(String base64Image);
	
	public String getItemCount();
	public void setItemCount();
	public MultiLineListRowImpl setItemCount(String count);
	public void decItemCount();
	
	/**
	 * @return the itemId
	 */
	public String getItemId();
	/**
	 * @param itemId the itemId to set
	 */
	public MultiLineListRowImpl setItemId(String itemId);
	
	/**
	 * @return the equFlag
	 */
	public int getEquFlag();
	/**
	 * @param equFlag the equFlag to set
	 * @return 
	 */
	public MultiLineListRowImpl setEquFlag(int equFlag);
	/**
	 * @return the useCount
	 */
	public int getUseCount();
	/**
	 * @param useCount the useCount to set
	 */
	public MultiLineListRowImpl setUseCount(int useCount);
	/**
	 * @return the itemName
	 */
	public String getItemName();
	/**
	 * @param itemName the itemName to set
	 */
	public MultiLineListRowImpl setItemName(String itemName);
	/**
	 * @return the itemType
	 */
	public int getItemType();
	/**
	 * @param itemType the itemType to set
	 */
	public MultiLineListRowImpl setItemType(int itemType);
	/**
	 * @return the point
	 */
	public int getPoint();
	/**
	 * @param point the point to set
	 */
	public MultiLineListRowImpl setPoint(int point);
	/**
	 * @return the skillId
	 */
	public int getSkillId();
	/**
	 * @param skillId the skillId to set
	 */
	public MultiLineListRowImpl setSkillId(int skillId);
	/**
	 * @return the usedCount
	 */
	public int getUsedCount();
	/**
	 * @param usedCount the usedCount to set
	 */
	public MultiLineListRowImpl setUsedCount(int usedCount);
	
	MultiLineListRowImpl setXmlMap(Map<String,String> xmlMap);
	
	MultiLineListRowImpl setItemVersion(int itemVersion);
	public int getItemVersion();
	
	/**
	 * @param key
	 * @param row
	 * @return
	 */
	ContentValues getItemDataToContentValue();
}
