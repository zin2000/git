package com.example.listview;

import java.util.List;

import android.graphics.Bitmap;

public class BoundBean {
	float g;
	float gsp;
	float grand;
	float y;
	float x;
	float sty;
	float stx;
	int addx;
	int xmax;
	int boundCountMax;
	int boundCount;
	int endCount;
	int endCountMax;
	int boundTiming;
	Bitmap bit;
	List<Bitmap[]> effectList;
	boolean activeFlag=true;
	public BoundBean(Bitmap bit, float dHeight, float dWidth, float stx, float sty, int bound, int addx, float grand, int endCount,boolean activeFlag, int boundTiming) {
		super();
		this.g = (float) (9.8*(dHeight/10000));
		this.gsp= (float) (9.8*(dHeight/10000));
		this.y=sty;
		this.x=stx;
		this.addx = addx;
		this.stx = stx;
		this.sty = sty;
		this.boundCount=0;
		this.boundCountMax=bound;
		this.grand = grand;
		this.bit=bit;
		this.xmax = (int) dWidth;
		this.endCount=0;
		this.endCountMax=endCount;
		this.activeFlag=activeFlag;
		this.boundTiming = boundTiming;
	}
	public float[] getPrint(){
		float[] xy = new float[]{x,y};
		if(y>grand){
			gsp=(float) (gsp*-0.8);
			boundCount++;
		}
		y+=gsp;
		gsp+=g;

		if(x<xmax){
			x+=addx;
		}else{
			end();
		}
		if(boundCount>=boundCountMax){
			end();
		}
		return xy;
	}
	/**
	 * 
	 */
	public void end() {
		addx=0;
		gsp=0;
		g=0;
		y=grand;
		endCount++;
		if(endCountMax<endCount){
			activeFlag=false;
		}
	}
	public boolean isActiveFlag(){
		return activeFlag;
	}
	public Bitmap getBitmap(){
		return bit;
	}
	public void setBitmap(Bitmap bit){
		this.bit=bit;
	}
	public int getBoundTiming(){
		return boundTiming;
	}
	public void addEffectList(Bitmap[] bit){
		this.effectList.add(bit);
	}
	public List<Bitmap[]> getEffectList(){
		return effectList;
	}
}
