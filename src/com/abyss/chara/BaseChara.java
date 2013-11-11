package com.abyss.chara;

import java.util.List;
import java.util.Map;
import java.util.Random;

import android.content.ContentValues;

import com.example.listview.MultiLineListRow;

/**
 * 複数行表示可能なListのRowです。

 *
 */
public interface BaseChara {
	
	
	/**
	 * @return the pMaxHp
	 */
	int getpLv();
	/**
	 * @param pMaxHp the pMaxHp to set
	 */
	void setpLv(int pLv);
	/**
	 * @return the pMaxMp
	 */	
	/**
	 * @return the pMaxHp
	 */
	int getpMaxHp();
	/**
	 * @param pMaxHp the pMaxHp to set
	 */
	void setpMaxHp(int pMaxHp);
	/**
	 * @return the pMaxMp
	 */
	int getpMaxMp();
	/**
	 * @param pMaxMp the pMaxMp to set
	 */
	void setpMaxMp(int pMaxMp);
	/**
	 * @return the pHp
	 */
	int getpHp();
	/**
	 * @param pHp the pHp to set
	 */
	void setpHp(int pHp) ;
	/**
	 * @return the pMp
	 */
	int getpMp();
	/**
	 * @param pMp the pMp to set
	 */
	void setpMp(int pMp);
	/**
	 * @return the pAtk
	 */
	int getpAtk();
	/**
	 * @param pAtk the pAtk to set
	 */
	void setpAtk(int pAtk);
	/**
	 * @return the pInt
	 */
	int getpInt();
	/**
	 * @param pInt the pInt to set
	 */
	void setpInt(int pInt);
	/**
	 * @return the pDef
	 */
	int getpDef();
	/**
	 * @param pDef the pDef to set
	 */
	void setpDef(int pDef);
	/**
	 * @return the pMdf
	 */
	int getpMdf();
	/**
	 * @param pMdf the pMdf to set
	 */
	void setpMdf(int pMdf);
	/**
	 * @return the pSpe
	 */
	int getpSpe();
	/**
	 * @param pSpe the pSpe to set
	 */
	void setpSpe(int pSpe);
	/**
	 * @return the pLuc
	 */
	int getpLuc();
	/**
	 * @param pLuc the pLuc to set
	 */
	void setpLuc(int pLuc);
	/**
	 * @return the pExp
	 */
	int getpExp();
	/**
	 * @param pExp the pExp to set
	 */
	void setpExp(int pExp);
	/**
	 * @return the pNextExp
	 */
	int getpNextExp();
	/**
	 * @param pNextExp the pNextExp to set
	 */
	void setpNextExp(int pNextExp);
	
	/**
	 * @param pNextExp the pNextExp to set
	 */
	int getAccessory(MultiLineListRow accessory,int skill);
	
	/**
	 * @return the pBonus
	 */
	int getpBonus() ;


	/**
	 * @param pBonus the pBonus to set
	 */
	void setpBonus(int pBonus);

	/**
	 * @return the eLeftHand
	 */
	MultiLineListRow geteLeftHand();

	/**
	 * @return the eRightHand
	 */
	MultiLineListRow geteRightHand();


	/**
	 * @return the eHead
	 */
	MultiLineListRow geteHead();


	/**
	 * @return the eBody
	 */
	MultiLineListRow geteBody();


	/**
	 * @return the eAccessory
	 */
	MultiLineListRow geteAccessory();

	/**
	 * @param pNextExp the pNextExp to set
	 */
	int getEquPoint(MultiLineListRow equ);
	/**
	 * @param pNextExp the pNextExp to set
	 */
	int getEquDefPoint();
	/**
	 * @param pNextExp the pNextExp to set
	 */
	int getEquAtkPoint();
	/**
	 * @param pNextExp the pNextExp to set
	 * @throws CloneNotSupportedException 
	 */
	MultiLineListRow equLeftHand(MultiLineListRow item);
	/**
	 * @param pNextExp the pNextExp to set
	 * @throws CloneNotSupportedException 
	 */
	MultiLineListRow equRightHand(MultiLineListRow item);
	/**
	 * @param pNextExp the pNextExp to set
	 * @throws CloneNotSupportedException 
	 */
	MultiLineListRow equHead(MultiLineListRow item);
	/**
	 * @param pNextExp the pNextExp to set
	 * @throws CloneNotSupportedException 
	 */
	MultiLineListRow equBody(MultiLineListRow item);
	/**
	 * @param pNextExp the pNextExp to set
	 * @throws CloneNotSupportedException 
	 */
	MultiLineListRow equAccessory(MultiLineListRow item);
	/**
	 * @param pNextExp the pNextExp to set
	 * @throws CloneNotSupportedException 
	 */
	MultiLineListRow equ(MultiLineListRow item);
	
	Map<String, MultiLineListRow> equ(Map<String,MultiLineListRow> item);
	
	List<MultiLineListRow> getEquList();
	
	/**
	 * @return the pMaxHp
	 */
	public int getAtkDamage(int atk, boolean dflag);
	/**
	 * @return the pMaxHp
	 */
	public int getIntDamage(int int_, boolean dflag);
	
	/**
	 * @return the pMaxHp
	 */
	public boolean isLive();
	
	public void lvUp();
	
	/**
	 * @param key
	 * @param row
	 * @return
	 */
	ContentValues getCharaDataToContentValue();
}
