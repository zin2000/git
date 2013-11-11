package com.example.listview;

import java.util.Arrays;

public class DonjonFloorDataImpl implements DonjonFloorData {
	
	private Integer currentAreaNo =0;
	private Integer[] areaMap;
	private Integer level;
	private boolean[] eventFlg;
	@Override
	public void setCurrentArea(Integer areaNo) {
		this.currentAreaNo = areaNo;
	}

	@Override
	public void addCurrentArea() {
		if(areaMap.length <= currentAreaNo++){
			this.currentAreaNo++;
		}
	}

	@Override
	public Integer getCurrentArea() {
		return this.currentAreaNo;
	}

	@Override
	public DonjonFloorDataImpl setAreaMap(Integer size, Integer level, Integer treasure,
			Integer enemy) {
		this.level=level;
		//エリアマップを初期化
		this.areaMap=new Integer[(int)size];
		Arrays.fill(this.areaMap,0);
		//イベントフラグを初期化
		eventFlg=new boolean[(int)size];
		Arrays.fill(this.eventFlg,false);
		
		for(int i=1;i<=treasure;i++){
			int rand = floorRandom(1,this.areaMap.length-3);
			this.areaMap[rand]+=1;
			this.eventFlg[rand]=true;
		}
		for(int i=1;i<=enemy;i++){
			int rand = floorRandom(1,this.areaMap.length-3);
			this.areaMap[rand]+=1000;
			this.eventFlg[rand]=true;
		}
		return this;
	}
	
	public DonjonFloorDataImpl setAreaMap(Integer currentArea, Integer level, String[] areaMap,
			String[] eventFlag) {
		this.level=level;
		//エリアマップを初期化
		this.areaMap=new Integer[areaMap.length];
		Arrays.fill(this.areaMap,0);
		//イベントフラグを初期化
		eventFlg=new boolean[areaMap.length];
		Arrays.fill(this.eventFlg,false);
		
		for(int i=1;i<areaMap.length;i++){
			this.areaMap[i-1]=Integer.parseInt(areaMap[i]);
			if(eventFlag[i].equals("1")){
				this.eventFlg[i-1]=true;
			}
		}
		return this;
	}

	@Override
	public Integer[] getAreaMap() {
		// TODO Auto-generated method stub
		return areaMap;
	}
	
	static int floorRandom(int _min, int _max){
		int range;	// range == 範囲
		range = _max - _min + 1;
		
		return (int)(Math.random() * range) + _min;
	}

	/**
	 * @return the eventFlg
	 */
	public boolean[] getEventFlg() {
		return eventFlg;
	}

	/**
	 * @param eventFlg the eventFlg to set
	 */
	public void setEventFlg(int area, boolean eventFlg) {
		this.eventFlg[area] = eventFlg;
	}
	/**
	 * @param eventFlg the eventFlg to set
	 */
	public String getEventFlgString() {
		StringBuffer sb=new StringBuffer();
		for(boolean event : eventFlg){
			if(event){
				sb.append(",1");
			}else{
				sb.append(",0");
			}
		}
		return sb.toString();
	}
	/**
	 * @param eventFlg the eventFlg to set
	 */
	public String getAreaMapString() {
		StringBuffer sb=new StringBuffer();
		for(Integer area : areaMap){
			sb.append(","+area);
		}
		return sb.toString();
	}
}
