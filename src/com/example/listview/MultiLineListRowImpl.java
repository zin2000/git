package com.example.listview;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;

public class MultiLineListRowImpl implements MultiLineListRow {
	
	private Integer prefixImage;
	private Integer suffixImage;
	private String itemCount="1";
	private String base64Image;
	private String itemId;
	private String itemName;
	private int itemType;
	private int point;
	private int skillId;
	private int equFlag;
	private int usedCount;
	private int useCount;
	private int itemVersion;
	/**
	 * @return the itemVersion
	 */
	public int getItemVersion() {
		return itemVersion;
	}
	/**
	 * @param itemVersion the itemVersion to set
	 */
	public MultiLineListRowImpl setItemVersion(int itemVersion) {
		this.itemVersion = itemVersion;
		return this;
	}
	List<String> texts;
	List<Float> size;
	
	public static MultiLineListRowImpl create() {
		
		MultiLineListRowImpl m = new MultiLineListRowImpl();
		m.texts = new ArrayList<String>();
		m.size = new ArrayList<Float>();
		return m;
	}
	public MultiLineListRowImpl prefixImage(Integer id) {
		this.prefixImage = id;
		return this;
	}
	public MultiLineListRowImpl suffixImage(Integer id) {
		this.suffixImage = id;
		return this;
	}
	public MultiLineListRowImpl addText(String text, float size) {
		this.texts.add(text+itemCount);
		this.size.add(size);
		return this;
	}
	
	public MultiLineListRowImpl addText(String text) {
		//this.texts.add(text);
		this.texts.add(text+itemCount);
		this.size.add(0f);
		return this;
	}
	public MultiLineListRowImpl setText(String text,String c) {
		//this.texts.add(text);
		this.texts.add(text+c);
		return this;
	}

	public Integer getPrefixImageId() {
		return prefixImage;
	}

	public Integer getSuffixImageId() {
		return suffixImage;
	}

	public String getText(int position) {
		return texts.get(position);
	}

	public float getTextSize(int position) {
		return size.get(position);
	}

	public int sieze() {
		return texts.size();
	}
	
	public String getBase64Image() {
		return base64Image;
	}
	public MultiLineListRowImpl setBase64Image(String base64Image) {
		this.base64Image = base64Image;
		return this;
	}
	public String getItemCount() {
		return itemCount;
	}
	public MultiLineListRowImpl setItemCount(int i) {
		this.itemCount = String.valueOf(i);
		return this;
	}
	public void setItemCount() {
		List<String> text = new ArrayList<String>();
		Integer count = Integer.parseInt(this.itemCount);
		count++;
		this.itemCount = String.valueOf(count);
		text.add(getText(0).split(":")[0]+":"+itemCount);
		this.texts = text;
	}
	public void decItemCount() {
		List<String> text = new ArrayList<String>();
		Integer count = Integer.parseInt(this.itemCount);
		count--;
		this.itemCount = String.valueOf(count);
		text.add(getText(0).split(":")[0]+":"+itemCount);
		this.texts = text;
	}
	public void resetItemCount() {
		List<String> text = new ArrayList<String>();
		Integer count = Integer.parseInt(this.itemCount);
		this.itemCount = String.valueOf(count);
		text.add(getText(0).split(":")[0]+":"+itemCount);
		this.texts = text;
	}
	
	public MultiLineListRowImpl setItemCount(String count) {
		this.itemCount=count;
		return this;
	}
	/**
	 * @return the itemId
	 */
	public String getItemId() {
		return itemId;
	}
	/**
	 * @param itemId the itemId to set
	 */
	public MultiLineListRowImpl setItemId(String itemId) {
		this.itemId = itemId;
		return this;
	}
	/**
	 * @return the equFlag
	 */
	public int getEquFlag() {
		return equFlag;
	}

	/**
	 * @param equFlag the equFlag to set
	 */
	public MultiLineListRowImpl setEquFlag(int equFlag) {
		this.equFlag = equFlag;
		return this;
	}
	/**
	 * @return the useCount
	 */
	public int getUseCount() {
		return useCount;
	}
	/**
	 * @param useCount the useCount to set
	 */
	public MultiLineListRowImpl setUseCount(int useCount) {
		this.useCount = useCount;
		return this;
	}
	/**
	 * @return the itemName
	 */
	public String getItemName() {
		return itemName;
	}
	/**
	 * @param itemName the itemName to set
	 */
	public MultiLineListRowImpl setItemName(String itemName) {
		this.itemName = itemName;
		return this;
	}
	/**
	 * @return the itemType
	 */
	public int getItemType() {
		return itemType;
	}
	/**
	 * @param itemType the itemType to set
	 */
	public MultiLineListRowImpl setItemType(int itemType) {
		this.itemType = itemType;
		return this;
	}
	/**
	 * @return the point
	 */
	public int getPoint() {
		return point;
	}
	/**
	 * @param point the point to set
	 */
	public MultiLineListRowImpl setPoint(int point) {
		this.point = point;
		return this;
	}
	/**
	 * @return the skillId
	 */
	public int getSkillId() {
		return skillId;
	}
	/**
	 * @param skillId the skillId to set
	 */
	public MultiLineListRowImpl setSkillId(int skillId) {
		this.skillId = skillId;
		return this;
	}
	/**
	 * @return the usedCount
	 */
	public int getUsedCount() {
		return usedCount;
	}
	/**
	 * @param usedCount the usedCount to set
	 */
	public MultiLineListRowImpl setUsedCount(int usedCount) {
		this.usedCount = usedCount;
		return this;
	}
	/**
	 * @param Cursorをセット
	 */
	public MultiLineListRowImpl setCursor(Cursor c) {
			addText(c.getString(c.getColumnIndex("item_name"))+"\n個数 :"+String.valueOf(c.getString(c.getColumnIndex("item_count"))),20);
	  		setItemCount(c.getString(c.getColumnIndex("item_count")));
	  		setBase64Image(c.getString(c.getColumnIndex("item_img_binary")));
	  		setItemId(c.getString(c.getColumnIndex("item_detail_id")));
	  		setEquFlag(c.getInt(c.getColumnIndex("equ_flag")));
	  		setUseCount(c.getInt(c.getColumnIndex("max_use_count")));
	 		setUsedCount(c.getInt(c.getColumnIndex("use_count")));
	  		setItemType(c.getInt(c.getColumnIndex("item_type")));
	  		setItemName(c.getString(c.getColumnIndex("item_name")));
	  		setSkillId(c.getInt(c.getColumnIndex("skill_id")));
	  		setPoint(c.getInt(c.getColumnIndex("point")));
	  		setItemVersion(c.getInt(c.getColumnIndex("item_version")));
			resetItemCount();
		return this;
	}
	/**
	 * @param Mapをセット
	 */
	public MultiLineListRowImpl setXmlMap(Map<String,String> xmlMap) {
		addText(xmlMap.get("item_text"),20);
		if(null!=xmlMap.get("item_count")){
			setItemCount(xmlMap.get("item_count"));
		}
  		setBase64Image(xmlMap.get("item_img_binary"));
  		setItemId(xmlMap.get("item_detail_id"));
  		setEquFlag(Integer.parseInt(xmlMap.get("equ_flag")));
  		setUseCount(Integer.parseInt(xmlMap.get("use_count")));
 		setUsedCount(Integer.parseInt(xmlMap.get("use_count")));
  		setItemType(Integer.parseInt(xmlMap.get("item_type_id")));
  		setItemName(xmlMap.get("item_name"));
  		setSkillId(Integer.parseInt(xmlMap.get("skill_id")));
  		setPoint(Integer.parseInt(xmlMap.get("point")));
  		setItemVersion(Integer.parseInt(xmlMap.get("item_version")));
		resetItemCount();
		return this;
	}
	
	/**
	 * @param key
	 * @param row
	 * @return
	 */
	public ContentValues getItemDataToContentValue() {
		ContentValues cv = new ContentValues();
		cv.put("item_detail_id", getItemId());
		cv.put("item_text", getText(0));
		cv.put("item_type", getItemType());
		cv.put("item_name", getItemName());
		cv.put("item_count", Integer.parseInt(getItemCount()));
		cv.put("item_img_binary", getBase64Image());
		cv.put("point", getPoint());
		cv.put("equ_flag", getEquFlag());
		cv.put("skill_id", getSkillId());
		cv.put("max_use_count", getUseCount());
		cv.put("use_count", getUsedCount());
		cv.put("item_version", getItemVersion());
		return cv;
	}
}
