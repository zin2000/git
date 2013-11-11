package com.example.listview;

/**
 * ダンジョンのフロアデータ格納用です。

 *
 */
public interface DonjonFloorData {

	/**
	 * 現在地エリアをセットします。
	 * 
	 */
	void setCurrentArea(Integer areaNo);
	
	/**
	 * 現在地エリアを加算します。
	 * 
	 */
	void addCurrentArea();
	
	/**
	 * 現在地エリアを取得します。
	 * 
	 * @return 先頭に付与するイメージのID
	 */
	Integer getCurrentArea();
	
	/**
	 * エリアを生成します。
	 * @return 
	 * 
	 */
	DonjonFloorDataImpl setAreaMap(Integer size, Integer level, Integer treasure, Integer enemy);
	
	/**
	 * エリア情報を取得します。
	 * 
	 * @return 先頭に付与するイメージのID
	 */
	Integer[] getAreaMap();
	/**
	 * @param eventFlg the eventFlg to set
	 */
	String getEventFlgString();
	/**
	 * @param eventFlg the eventFlg to set
	 */
	String getAreaMapString();
	
	DonjonFloorDataImpl setAreaMap(Integer currentArea, Integer level, String[] areaMap,
			String[] eventFlag);
}
