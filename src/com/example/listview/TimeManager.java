package com.example.listview;

public class TimeManager {
	private long m_StartTime = 0;
	 // function : Start
	 // Abstract : ゲームを開始する
	 // Return : 開始時間を保持する
	 public void Start(){
		 m_StartTime = System.currentTimeMillis();
	 }
	 
	 // function : getElipseTime
	 // Abstract : 経過時間を取得する
	 // Return : 経過時間
	 public float getElipseTime(){
		 return (System.currentTimeMillis() - m_StartTime) / 1000.0f;
	 }
}
