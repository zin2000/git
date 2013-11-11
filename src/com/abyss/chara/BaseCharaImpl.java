package com.abyss.chara;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

import org.apache.http.client.utils.CloneUtils;

import android.content.ContentValues;

import com.example.listview.MultiLineListRow;

public class BaseCharaImpl implements BaseChara {
	protected int pLv =1;
	protected int pMaxHp =0;
	protected int pMaxMp =0;
	protected int pHp =0;
	protected int pMp =0;
	protected int pAtk =0;
	protected int pInt =0;
	protected int pDef =0;
	protected int pMdf =0;
	protected int pSpe =0;
	protected int pLuc =0;
	protected int pExp =0;
	protected int pNextExp =0;
	protected int pBonus =0;
	
	protected MultiLineListRow eLeftHand = null;
	protected MultiLineListRow eRightHand = null;
	protected MultiLineListRow eHead = null;
	protected MultiLineListRow eBody = null;
	protected MultiLineListRow eAccessory = null;
	
	/**
	 * @return the pMaxHp
	 */
	public int getAtkDamage(int atk, boolean dflag) {
		int dm = 0;
		int dmr=0;
		int dec = (atk-getpDef()/2);
		if(dec<100){
			dmr = new Random().nextInt(10);
		}else{
			dmr = new Random().nextInt(10)*dec/100;
		}
		if(dec>0){
			dm = dec+dmr;
		}
		if(dm<0){
			dm=0;
		}
		if(dflag){
			if(pHp-dm>0){
				pHp-=dm;
			}else{
				pHp=0;
			}
		}
		return dm;
	}
	/**
	 * @return the pMaxHp
	 */
	public int getIntDamage(int int_, boolean dflag) {
		int dm=0;
		int dmr=0;
		int dec = (int_-pMdf);
		if(dec<100){
			dmr = new Random().nextInt(10);
		}else{
			dmr = new Random().nextInt(dec/100);
		}
		if(dec>0){
			dm = dec/2+dmr;
		}
		if(dm<0){
			dm=0;
		}
		if(dflag){
			if(pHp-dm>0){
				pHp-=dm;
			}else{
				pHp=0;
			}
		}
		return dm;
	}
	
	/**
	 * @return the pMaxHp
	 */
	public boolean isLive() {
		return pHp>0;
	}
	
	/**
	 * @return the pMaxHp
	 */
	public BaseCharaImpl(int pLv, int pMaxHp, int pMaxMp, int pHp, int pMp, int pAtk,
			int pInt, int pDef, int pMdf, int pSpe, int pLuc, int pExp,
			int pNextExp) {
		super();
		this.pLv = pLv;
		this.pMaxHp = pMaxHp;
		this.pMaxMp = pMaxMp;
		this.pHp = pHp;
		this.pMp = pMp;
		this.pAtk = pAtk;
		this.pInt = pInt;
		this.pDef = pDef;
		this.pMdf = pMdf;
		this.pSpe = pSpe;
		this.pLuc = pLuc;
		this.pExp = pExp;
		this.pNextExp = pNextExp;
	}
	/**
	 * @return the pMaxHp
	 */
	public int getpMaxHp() {
		return pMaxHp+getAccessory(eAccessory,1);
	}
	/**
	 * @param pMaxHp the pMaxHp to set
	 */
	public void setpMaxHp(int pMaxHp) {
		this.pMaxHp = pMaxHp;
	}
	/**
	 * @return the pMaxMp
	 */
	public int getpMaxMp() {
		return pMaxMp+getAccessory(eAccessory,2);
	}
	/**
	 * @param pMaxMp the pMaxMp to set
	 */
	public void setpMaxMp(int pMaxMp) {
		this.pMaxMp = pMaxMp;
	}
	/**
	 * @return the pHp
	 */
	public int getpHp() {
		return pHp;
	}
	/**
	 * @param pHp the pHp to set
	 */
	public void setpHp(int pHp) {
		this.pHp = pHp;
	}
	/**
	 * @return the pMp
	 */
	public int getpMp() {
		return pMp;
	}
	/**
	 * @param pMp the pMp to set
	 */
	public void setpMp(int pMp) {
		this.pMp = pMp;
	}
	/**
	 * @return the pAtk
	 */
	public int getpAtk() {
		return pAtk+getAccessory(eAccessory,3)+getEquAtkPoint();
	}
	/**
	 * @param pAtk the pAtk to set
	 */
	public void setpAtk(int pAtk) {
		this.pAtk = pAtk;
	}
	/**
	 * @return the pInt
	 */
	public int getpInt() {
		return pInt+getAccessory(eAccessory,4);
	}
	/**
	 * @param pInt the pInt to set
	 */
	public void setpInt(int pInt) {
		this.pInt = pInt;
	}
	/**
	 * @return the pDef
	 */
	public int getpDef() {
		return pDef+getAccessory(eAccessory,5)+getEquDefPoint();
	}
	/**
	 * @param pDef the pDef to set
	 */
	public void setpDef(int pDef) {
		this.pDef = pDef;
	}
	/**
	 * @return the pMdf
	 */
	public int getpMdf() {
		return pMdf+getAccessory(eAccessory,6);
	}
	/**
	 * @param pMdf the pMdf to set
	 */
	public void setpMdf(int pMdf) {
		this.pMdf = pMdf;
	}
	/**
	 * @return the pSpe
	 */
	public int getpSpe() {
		return pSpe+getAccessory(eAccessory,7);
	}
	/**
	 * @param pSpe the pSpe to set
	 */
	public void setpSpe(int pSpe) {
		this.pSpe = pSpe;
	}
	/**
	 * @return the pLuc
	 */
	public int getpLuc() {
		return pLuc+getAccessory(eAccessory,8);
	}
	/**
	 * @param pLuc the pLuc to set
	 */
	public void setpLuc(int pLuc) {
		this.pLuc = pLuc;
	}
	/**
	 * @return the pExp
	 */
	public int getpExp() {
		return pExp;
	}
	/**
	 * @param pExp the pExp to set
	 */
	public void setpExp(int pExp) {
		this.pExp = pExp;
	}
	/**
	 * @return the pNextExp
	 */
	public int getpNextExp() {
		return pNextExp;
	}
	/**
	 * @param pNextExp the pNextExp to set
	 */
	public void setpNextExp(int pNextExp) {
		this.pNextExp = pNextExp;
	}
	
	/**
	 * @param pNextExp the pNextExp to set
	 */
	public int getAccessory(MultiLineListRow accessory,int skill) {
		int add=0;
		if(accessory!=null){
			if(accessory.getSkillId()==skill){
				add=accessory.getPoint();
			}
		}
		return add;
	}
	
	/**
	 * @return the pBonus
	 */
	public int getpBonus() {
		return pBonus;
	}


	/**
	 * @param pBonus the pBonus to set
	 */
	public void setpBonus(int pBonus) {
		this.pBonus = pBonus;
	}


	/**
	 * @return the eLeftHand
	 */
	public MultiLineListRow geteLeftHand() {
		return eLeftHand;
	}


	/**
	 * @return the eRightHand
	 */
	public MultiLineListRow geteRightHand() {
		return eRightHand;
	}


	/**
	 * @return the eHead
	 */
	public MultiLineListRow geteHead() {
		return eHead;
	}


	/**
	 * @return the eBody
	 */
	public MultiLineListRow geteBody() {
		return eBody;
	}


	/**
	 * @return the eAccessory
	 */
	public MultiLineListRow geteAccessory() {
		return eAccessory;
	}


	/**
	 * @param pNextExp the pNextExp to set
	 */
	public int getEquPoint(MultiLineListRow equ) {
		int add=0;
		if(equ!=null){
			add=equ.getPoint();
		}
		return add;
	}
	/**
	 * @param pNextExp the pNextExp to set
	 */
	public int getEquDefPoint() {
		int add = getEquPoint(eBody)+getEquPoint(eHead);
		if(eLeftHand!=null){
			if(eLeftHand.getItemType()==31){add+=eLeftHand.getPoint();}
		}
		if(eRightHand!=null){
			if(eRightHand.getItemType()==31){add+=eLeftHand.getPoint();}
		}
		return add;
	}
	/**
	 * @param pNextExp the pNextExp to set
	 */
	public int getEquAtkPoint() {
		int add = 0;
		if(eLeftHand!=null){
			if(eLeftHand.getItemType()==21){add+=eLeftHand.getPoint();}
		}
		if(eRightHand!=null){
			if(eRightHand.getItemType()==21){add+=eRightHand.getPoint();}
		}
		return add;
	}
	/**
	 * @param pNextExp the pNextExp to set
	 * @throws CloneNotSupportedException 
	 */
	public MultiLineListRow equLeftHand(MultiLineListRow item){
		MultiLineListRow row =null;
		if(eLeftHand!=null){
			row=eLeftHand;
		}
		eLeftHand = item;
		return row;
	}
	/**
	 * @param pNextExp the pNextExp to set
	 * @throws CloneNotSupportedException 
	 */
	public MultiLineListRow equRightHand(MultiLineListRow item){
		MultiLineListRow row =null;
		if(eRightHand!=null){
			row=eRightHand;
		}
		eRightHand = item;
		return row;
	}
	
	/**
	 * @param pNextExp the pNextExp to set
	 * @throws CloneNotSupportedException 
	 */
	public MultiLineListRow equHead(MultiLineListRow item){
		MultiLineListRow row =null;
		if(eHead!=null){
			row=eHead;
		}
		eHead = item;
		return row;
	}
	/**
	 * @param pNextExp the pNextExp to set
	 * @throws CloneNotSupportedException 
	 */
	public MultiLineListRow equBody(MultiLineListRow item){
		MultiLineListRow row =null;
		if(eBody!=null){
			row=eBody;
		}
		eBody = item;
		return row;
	}
	/**
	 * @param pNextExp the pNextExp to set
	 * @throws CloneNotSupportedException 
	 */
	public MultiLineListRow equAccessory(MultiLineListRow item){
		MultiLineListRow row =null;
		if(eAccessory!=null){
			row=eAccessory;
		}
		eAccessory = item;
		return row;
	}
	/**
	 * @param pNextExp the pNextExp to set
	 * @return 
	 * @throws CloneNotSupportedException 
	 */
	public MultiLineListRow equ(MultiLineListRow item){
		MultiLineListRow row =null;
		switch (item.getItemType()) {
		case 21:
			row = equRightHand(item);
			break;
		case 31:
			row = equLeftHand(item);
			break;
		case 41:
			row = equHead(item);
			break;
		case 51:
			row = equBody(item);
			break;
		case 61:
			row = equAccessory(item);
			break;
		default:
			break;
		}
		return row;
	}
	/**
	 * @param pNextExp the pNextExp to set
	 * @return 
	 * @throws CloneNotSupportedException 
	 */
	public Map<String,MultiLineListRow> equ(Map<String,MultiLineListRow> item){
		Map<String,MultiLineListRow> returnItem = new HashMap<String,MultiLineListRow>();
		for(String key : item.keySet()){
			MultiLineListRow row = equ(item.get(key));
			if(row!=null){
				returnItem.put(row.getItemId(), row);
			}
		}
		return returnItem;
	}
	/**
	 * 
	 */
	public List<MultiLineListRow> getEquList(){
		List<MultiLineListRow> equList = new ArrayList<MultiLineListRow>();
		equList.add(eRightHand);
		equList.add(eLeftHand);
		equList.add(eHead);
		equList.add(eBody);
		equList.add(eAccessory);
		return equList;
	}


	/**
	 * @return the pLv
	 */
	public int getpLv() {
		return pLv;
	}


	/**
	 * @param pLv the pLv to set
	 */
	public void setpLv(int pLv) {
		this.pLv = pLv;
	}
	
	/**
	 * @param pLv the pLv to set
	 */
	public void lvUp() {
		this.pLv++;
		this.pMaxHp+=10;
		this.pMaxMp+=2;
		this.pHp=pMaxHp;
		this.pMp=pMaxMp;
		this.pAtk+=2;
		this.pDef+=2;
		this.pLuc++;
		this.pBonus++;
		this.pInt++;
		this.pMdf++;
	}
	
	/**
	 * @param key
	 * @param row
	 * @return
	 */
	public ContentValues getCharaDataToContentValue() {
		ContentValues cv = new ContentValues();
		cv.put("status_detail_id", "1");
		cv.put("lv", pLv);
		cv.put("max_hp", pMaxHp);
		cv.put("max_mp", pMaxMp);
		cv.put("hp", pHp);
		cv.put("mp", pMp);
		cv.put("atk", pAtk);
		cv.put("int", pInt);
		cv.put("def", pDef);
		cv.put("mdf", pMdf);
		cv.put("luc", pLuc);
		cv.put("spe", pSpe);
		cv.put("bop", pBonus);
		cv.put("exp", pExp);
		cv.put("nxp", pNextExp);
		return cv;
	}
}
