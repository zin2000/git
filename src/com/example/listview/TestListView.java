package com.example.listview;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.DropBoxManager;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.Typeface;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import com.example.listview.OnSerifListener;
import com.example.listview.SerifDirector;
import com.example.listview.SerifView;

import org.xmlpull.v1.XmlPullParser;

import com.abyss.chara.BaseChara;
import com.abyss.chara.BaseCharaImpl;
import com.example.listview.SerifDirector.PageInfo;
import com.example.listview.SerifDirector.Paragraph;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.sax.StartElementListener;
import android.util.Xml;
import android.os.StrictMode;
import android.content.res.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.text.*;
public class TestListView extends Activity implements OnSerifListener, OnClickListener, SurfaceHolder.Callback{
	final static int ITEM_DB_VERSION = 21;
	final static int ITEM_LIST_DB_VERSION = 2;
	final static int EQU_DB_VERSION = 2;
	final static int EQU_ITEM_DB_VERSION = 1;
	final static int EQU_LIST_DB_VERSION = 1;
	final static int DONJON_DB_VERSION = 1;

	Map<String,MultiLineListRow> map =new TreeMap<String,MultiLineListRow>();
	Map<String,MultiLineListRow> equ_map =new TreeMap<String,MultiLineListRow>();
	
	Toast toast = null;
	Toast dmTtoast = null;
	
	/** ビットマップサイズ定義 */
	final static int ITEM_ICON_SIZE =32;
	final static int PARAM_NO_ICON_SIZE_W =4;
	final static int PARAM_NO_ICON_SIZE_H =6;
	final static int PARAM_ICON_SIZE_W =16;
	float PARAM_SCALE;
	float ITEM_SCALE;
	
	/** １文字ずつ用 */
    private TextView textView;
    private TextView logView;
    private TextView textView2;
    private TextView logView2;
    String put_txt = "文字列を１文字ずつ出力するテスト";     
    int i = 0;
    int iTmp = 0;
    String put_word = "";     
    String put_text = "";
    String tmp_text = "";
    String tmp_set = "";
    private static int TIMEOUT_MESSAGE = 1;
    private static int INTERVAL = 1; 
	boolean startText = false;
	int mesCount = 100;
	Drawable mesImage=null;
	
	/** コマンドNO定義 */
	int command = 0;
	final int COMMAND_NO = 0;
	final int COMMAND_ATK = 1;
	final int COMMAND_MGK = 2;
	final int COMMAND_GUARD = 3;
	final int COMMAND_ESCAPE = 4;
	
	/** 表示メニュー種別定義 */
	int menuId = 0;
	final int MENU_USE_ITEM = 0;
	final int MENU_EQU_ITEM = 1;
	
	int moveRateDec = 1;
	public int posi;
    private SurfaceView mainView;
    private SurfaceView overView;
	static ArrayAdapter<String> adapter;
	float dWidth = 0;
	float dHeight = 0;
	float itemImgScale;
	Handler mHandler = new Handler();
	Typeface type;
	Map<Integer,DonjonFloorDataImpl> donjonMap =new TreeMap<Integer,DonjonFloorDataImpl>();
	boolean start = false;
	boolean fast = true;
	boolean eventFlg = false;
	boolean battleFlg = false;
	boolean moveFlag = true;
	boolean fadeFlag = false;
	int currentArea = 0;
	long currentTime = 0;
	BaseChara c_yusya = new BaseCharaImpl(1, 100, 50, 100, 50, 10, 20, 10, 20, 20, 10, 0, 100);
	BaseChara enemy ;
	int battleCount=0;
	int atwidth=1;
	int at_m_width=1;
	String mes ="";
	int d_floor = 1;
	List<BoundBean> bbList=new ArrayList<BoundBean>();
	Paint paintRed = new Paint();
	Paint paintGreen = new Paint();
	BoundBean userBound = new BoundBean(null, 0, 0, 0, 0, 0, 0, 0, 0,false,0);
	BoundBean enemyBound = new BoundBean(null, 0, 0, 0, 0, 0, 0, 0, 0,false,0);
	BoundBean userCareBound = new BoundBean(null, 0, 0, 0, 0, 0, 0, 0, 0,false,0);
	/*
	 * screenId(画面ID)
	 * 0=初期値 1=メニュー 2=ダンジョン
	 */
	int screenId=0;
	int savedScreenId=0;
	final int SCREEN_INIT = 0;
	final int SCREEN_MENU = 1;
	final int SCREEN_DONJON = 2;
	
	int donjonGetItemCount = 0;
	int donjonGetEnemyCount = 0;
	
	int userTurnCount = 1;
	int enemyTurnCount = 1;
	int atackTurn = 500;
	SQLiteDatabase item_wdb;
	SQLiteDatabase item_rdb;
	SQLiteDatabase equ_wdb;
	SQLiteDatabase equ_rdb;
	SQLiteDatabase donjon_wdb;
	SQLiteDatabase donjon_rdb;
	SQLiteDatabase equ_stock_wdb;
	SQLiteDatabase equ_stock_rdb;
	SQLiteDatabase item_list_wdb;
	SQLiteDatabase item_list_rdb;
	SQLiteDatabase equ_item_list_wdb;
	SQLiteDatabase equ_item_list_rdb;
	SQLiteDatabase status_wdb;
	SQLiteDatabase status_rdb;
	boolean atBarIsFull = false;

	
	boolean isGetEnemyDamege=false;
	int enemyDamege=0;
	int userDamege=0;
	ImageView enemyDamage;
	AnimationSet set;
	Bitmap dmgBitmap=null;
	Bitmap userDmgBitmap=null;
	Bitmap testDmgBitmap=null;
	Bitmap userCareBitmap=null;
	ImageView damage;
	
	TestListView tl;
    Context conte;
    AsyncTaskProgressDialogSimple m_SimpleThread;
    
	SerifView sv;
	TextView beforMesView;
	long lastevent;
	
	boolean deadFlag = false;
	
	@Override
    protected void onPause() {
		if(dmTtoast!=null){
			dmTtoast.cancel();
			toast.cancel();
		}
    	setPrif();
        super.onPause();
    }

    @Override
    protected void onStop() {
    	if(dmTtoast!=null){
			dmTtoast.cancel();
			toast.cancel();
		}
    	setPrif();
        super.onStop();
    }
 // Activityクラスのfinishメソッドをオーバーライド
    @Override
    public void finish()
    {
      showDialog(10);
    }

    // Activityクラスのfinishメソッドを呼ぶメソッドを新規で追加
    public void appEnd()
    {
      super.finish();
    }

    // ActivityクラスのonCreateDialogをオーバーライド
    @Override
    protected Dialog onCreateDialog(int id){
    	setPrif();
//    	switch(id){
//    	case 10:
//        return new AlertDialog.Builder(this)
//           .setTitle("終了確認")
//           .setMessage("アプリを終了してタイトルに戻ります。途中のデータ(宝箱、討伐記録)はリセットされますがよろしいでしょうか？")
//           .setPositiveButton("よろしい", new DialogInterface.OnClickListener()
//           {
//             public void onClick(DialogInterface dialog, int whichButton)
//             {
//               // アプリ終了
//               if(dmTtoast!=null){
//         			dmTtoast.cancel();
//         	   }
//               appEnd();
//               overridePendingTransition(R.anim.alpha_in,R.anim.alpha_out);
//             }
//           })
//           .setNegativeButton("それは困る", new DialogInterface.OnClickListener()
//           {
//             public void onClick(DialogInterface dialog, int whichButton)
//             {
//             }
//           }).create();
//      }
        // アプリ終了
        if(dmTtoast!=null){
  			dmTtoast.cancel();
  	   }
        appEnd();
        overridePendingTransition(R.anim.alpha_in,R.anim.alpha_out);
      return null;
    }

    @Override protected void onResume() {
    	// 二重になってしまうので、初期化時にはthread作成を行わない
    	if(!fast){
    		mainView = (SurfaceView)this.findViewById(R.id.imageMainView1);
    		SurfaceHolder holder = mainView.getHolder();
    		holder.addCallback( new SampleHolderCallBack(TestListView.this));
    		overView = (SurfaceView)this.findViewById(R.id.damageView1);
    		SurfaceHolder oberHolder = overView.getHolder();
    		oberHolder.addCallback( new OverLaySurfaceViewCallback(TestListView.this));
    	}else{
    		fast=false;
    	}
    	super.onResume();
    }
	/** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
     	// 非同期(スレッド)処理クラスの生成
     	m_SimpleThread = new AsyncTaskProgressDialogSimple(this);
     	// 非同期(スレッド)処理の実行
     	m_SimpleThread.execute();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		Resources a =getResources();
		Bitmap[] b = new Bitmap[6];
		b[0] = BitmapFactory.decodeResource(a,R.drawable.back);
		
		ImageView windowCenter = (ImageView)findViewById(R.id.windowCenter);  
		windowCenter.setOnTouchListener(new FlickTouchListener());
		damage = (ImageView)findViewById(R.id.damage);
		
		deadFlag = false;
		
		// アクティビティからインテントを取得
    	//donjonMap.put(1, new DonjonFloorDataImpl().setAreaMap(100,1,40,80));
		donjonMap.put(1, new DonjonFloorDataImpl().setAreaMap(10,1,5,5));
		//type = Typeface.createFromAsset(getAssets(),"misaki.ttf");
		//type = Typeface.createFromAsset(getAssets(),"cine.ttf");
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        
        // レイアウト関係の取得
        Button button = (Button) findViewById(R.id.button1);
        Button button2 = (Button) findViewById(R.id.button2);
        Button button3 = (Button) findViewById(R.id.button3);
        Button button_itemChange = (Button) findViewById(R.id.itemChange);
		ListView contentsView = (ListView) findViewById(R.id.listView1);
		contentsView.setScrollingCacheEnabled(false);
        this.mainView = (SurfaceView)this.findViewById(R.id.imageMainView1);
    	this.overView = (SurfaceView)this.findViewById(R.id.damageView1);
    	//beforMesView = (TextView) findViewById(R.id.beforMes);
    	//textView = (TextView) findViewById(R.id.mainMes);
    	logView = (TextView) findViewById(R.id.logMes);
    	textView2 = (TextView) findViewById(R.id.mainMes2);
    	logView2 = (TextView) findViewById(R.id.logMes2);
    	
		/** ビットマップスケール関係定数設定 */
        WindowManager wm = (WindowManager)getSystemService(WINDOW_SERVICE);
    	Display disp = wm.getDefaultDisplay();
        dWidth = disp.getWidth();
    	dHeight = disp.getHeight();
    	PARAM_SCALE = (dHeight/60)/(float)PARAM_NO_ICON_SIZE_H;
    	ITEM_SCALE = (dHeight/30)/(float)ITEM_ICON_SIZE;
    	itemImgScale = dHeight/160;
    	
		//テスト用ビットマップ作成
	  	testDmgBitmap = getNumBitLine(new Random().nextInt(99999),null,PARAM_SCALE,null);
    	
    	paintRed.setColorFilter(new LightingColorFilter(0xFFFFFF, Color.RED));
    	paintGreen.setColorFilter(new LightingColorFilter(0xFFFFFF, Color.GREEN));
    	
    	conte=this;
    	bbList = new ArrayList<BoundBean>();
    		  	
        try{
        	SurfaceHolder holder = this.mainView.getHolder();
        	holder.addCallback( new SampleHolderCallBack(this));
        	  // オーバーレイするSurfaceView  	  
        	  SurfaceHolder overLayHolder = this.overView.getHolder(); 
        	  // ここで半透明にする 
        	  overLayHolder.setFormat(PixelFormat.TRANSLUCENT); 
        	  overLayHolder.addCallback(new OverLaySurfaceViewCallback(this));
        }catch(Exception e){
        } 
        
        SharedPreferences sp = getSharedPreferences("test_pref", MODE_PRIVATE);
        
    	ItemOpenHelper itemHelper = new ItemOpenHelper(getApplicationContext(),"item",ITEM_DB_VERSION, "item");
    	ItemOpenHelper equHelper = new ItemOpenHelper(getApplicationContext(),"equ_item",EQU_DB_VERSION, "equ_item");
    	ItemOpenHelper equStockHelper = new ItemOpenHelper(getApplicationContext(),"equ_stock",EQU_ITEM_DB_VERSION, "equ_stock");
    	ItemOpenHelper itemListHelper = new ItemOpenHelper(getApplicationContext(),"item_list",ITEM_LIST_DB_VERSION, "item_list");
    	EquItemOpenHelper equListHelper = new EquItemOpenHelper(getApplicationContext(),"equ_list",EQU_LIST_DB_VERSION, "equ_list");
    	DonjonOpenHelper donjonHelper = new DonjonOpenHelper(getApplicationContext(),"donjon",DONJON_DB_VERSION, "donjon");
    	CharaStatusOpenHelper statusHelper = new CharaStatusOpenHelper(getApplicationContext(),"status",DONJON_DB_VERSION, "status");
    	try{
    		item_wdb = itemHelper.getWritableDatabase();
    		item_rdb = itemHelper.getReadableDatabase();
    		equ_wdb = equHelper.getWritableDatabase();
    		equ_rdb= equHelper.getReadableDatabase();
    		donjon_wdb = donjonHelper.getWritableDatabase();
    		donjon_rdb= donjonHelper.getReadableDatabase();
    		equ_stock_wdb = equStockHelper.getWritableDatabase();
    		equ_stock_rdb= equStockHelper.getReadableDatabase();
    		item_list_wdb = itemListHelper.getWritableDatabase();
    		item_list_rdb = itemListHelper.getReadableDatabase();
    		equ_item_list_wdb = equListHelper.getWritableDatabase();
    		equ_item_list_rdb = equListHelper.getReadableDatabase();
    		status_wdb = statusHelper.getWritableDatabase();
    		status_rdb = statusHelper.getReadableDatabase();
    		//もしくは、
    		//sdb = helper.getReadableDatabase();
    	}catch(SQLiteException e){
    		//異常終了
    		showToast("DB接続にしっぱいしました", null);
    	}
    	
    	try {
    		itemListHelper.setNewItemInsert();
		} catch (Exception e) {
			showToast("データ更新失敗", null);
		}
    	
    	try {
    		equListHelper.setNewItemInsert();
		} catch (Exception e) {
			showToast("データ更新失敗", null);
		}
    	
    	
    	
		map = loadDbDataToMap("select * from item order by _id desc ",item_rdb);
		equ_map = loadDbDataToMap("select * from equ_stock order by _id desc ",equ_stock_rdb);
		
		DonjonFloorDataImpl tmpDonjon = loadDonjonDataToMap("select * from donjon order by _id desc ", donjonHelper.getReadableDatabase());
		
		if(tmpDonjon!=null){
			if(tmpDonjon.getAreaMap()!=null && tmpDonjon.getAreaMap().length>1){
				donjonMap=new HashMap<Integer,DonjonFloorDataImpl>();
				donjonMap.put(d_floor, tmpDonjon);
			}
		}
		
		BaseCharaImpl tmpStatus = loadStatusDataToMap("select * from status order by _id desc ", statusHelper.getReadableDatabase());
		if(tmpStatus!=null){
			if(tmpStatus.getpLv()>0){
				c_yusya=tmpStatus;
			}
		}
		
		if(map.size()<1){
    		getItemLocalList(getItemLocalData("item_list", item_list_rdb, 1),map,false);
    		getItemLocalList(getItemLocalData("item_list", item_list_rdb, 2),map,false);
    		getItemLocalList(getItemLocalData("item_list", item_list_rdb, 3),map,false);
    		getItemLocalList(getItemLocalData("item_list", item_list_rdb, 4),map,false);
        }
		setContentsListView(map);
    	
		Map<String,MultiLineListRow> userEquMap = loadDbDataToMap("select * from equ_item order by _id desc ", equ_rdb);
    	c_yusya.equ(userEquMap);

        //イベントリスナをアクティビティに設定する
        //sv = (SerifView)findViewById(R.id.SerifView);
        //sv.sd.setOnSerifListener(this);
        
        // ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            	try {
                	if(donjonGetItemCount>0){
                		//if(populateContentsListView(getXml(new URL("http://doryu.dix.asia/get_item.php?item_count=1")))){
                		//if(getItemLocal(1,"item_list",item_list_rdb,0)){
                		if(getItemLocal(1,"item_list",item_list_rdb,0)){
                    		donjonGetItemCount--;
                		}
                	}else{
                		//showToast("宝箱を取得していません", null);
						setMessageView("宝箱を取得していません",null);
                		bbList.add(new BoundBean(testDmgBitmap, dHeight, dWidth, dWidth/2, dHeight/8, 2, new Random().nextInt(5)+3, dHeight/6,20,true,40));

                	}
    				//mainView.setLayoutParams(new LinearLayout.LayoutParams(dWidth, dHeight/4));
				} catch (Exception e) {
					//showToast("アイテム取得に失敗"+e.getStackTrace(), null);
					setMessageView("アイテム取得に失敗"+e.getStackTrace(),null);
				}

            }
            
        });
		// ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
        button2.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					screenId=2;
					textView2.setAlpha(128);
					logView2.setAlpha(128);
				}

		});

		// ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
        button3.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
		            //Intent intent = tl.getIntent();
					//Bundle bundle = new Bundle();
					//bundle.putString("", "");
					//tl.getIntent().putExtra("", (Serializable)c_yusya);
		            //startActivity(tl.getIntent());
		            //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
					
					if(screenId==1){
						//メニュー画面の場合は戻る
						screenId=savedScreenId;
						textView2.setAlpha(128);
						logView2.setAlpha(128);
					}else{	
						//画面を保持してメニューへ遷移
						savedScreenId=screenId;
						screenId=1;
						textView2.setAlpha(0);
						logView2.setAlpha(0);
					}
				}
		});
        
		// ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
        button_itemChange.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {					
					if(menuId==0){
						//使用アイテムの場合は装備アイテム表示
						menuId=MENU_EQU_ITEM;
						setContentsListView(equ_map);
					}else{
						//それ以外の場合は使用アイテム
						menuId=MENU_USE_ITEM;
						setContentsListView(map);
					}
				}
		});
	tl=this;
    }

	/**
	 * 
	 */
	public Bitmap getNumBitLine(int num, Bitmap head, float scale, Paint paint) {
		//テスト用ビットマップ作成
		Bitmap[] enemyD = getNumBitmap(num, head, scale);
		Bitmap b = Bitmap.createBitmap(enemyD[0].getWidth()*enemyD.length ,enemyD[0].getHeight() , Bitmap.Config.ARGB_8888);
	  	Canvas b_canvas = new Canvas(b);
	  	for(int i=0;i<enemyD.length;i++){
	  		b_canvas.drawBitmap(enemyD[i], enemyD[i].getWidth()*i , 0, paint);
	  	}
	  	return b;
	}

	/**
	 * @return
	 */
	
	protected Map<String, MultiLineListRow> loadDbDataToMap(String sqlstr, SQLiteDatabase rdb) {
		Cursor c=null;
    	c = rdb.rawQuery(sqlstr, null);
    	int countItem = c.getCount();
	    //0001.アイテム1.20.AAAAAAA:0002.アイテム2.20.AAAAAAA:
        c.moveToFirst();
        Map<String, MultiLineListRow> reMap = new HashMap<String, MultiLineListRow>();
        for(int i=0;i<countItem;i++){
        	MultiLineListRowImpl row = MultiLineListRowImpl.create();
        	//DB内容をセット
        	row.setCursor(c);
        	reMap.put(c.getString(c.getColumnIndex("item_detail_id")),row); 	
			c.moveToNext();
        }
		return reMap;
	}
	protected DonjonFloorDataImpl loadDonjonDataToMap(String sqlstr, SQLiteDatabase rdb) {
		Cursor c=null;
    	c = rdb.rawQuery(sqlstr, null);
    	int countItem = c.getCount();
	    //0001.アイテム1.20.AAAAAAA:0002.アイテム2.20.AAAAAAA:
        c.moveToFirst();
        Map<String, DonjonFloorDataImpl> reMap = new HashMap<String, DonjonFloorDataImpl>();
        DonjonFloorDataImpl row = new DonjonFloorDataImpl();
        for(int i=0;i<countItem;i++){      	
	  		
        	d_floor = (c.getInt(c.getColumnIndex("d_floor")));
	  		if(d_floor==0){
	  			d_floor=1;
	  		}
	  		currentTime = (c.getInt(c.getColumnIndex("current_time")));
	  		donjonGetItemCount = (c.getInt(c.getColumnIndex("item_count")));
	  		donjonGetEnemyCount = (c.getInt(c.getColumnIndex("enemy_count")));
	  		
	  		int currentArea = (c.getInt(c.getColumnIndex("current_area")));
	 		String[] areaMap = (c.getString(c.getColumnIndex("area_map"))).split(",");
	 		String[] eventFlag = (c.getString(c.getColumnIndex("event_flag"))).split(",");
	  		int level = (c.getInt(c.getColumnIndex("level")));
	  		
        	//DB内容をセット
        	row.setAreaMap(currentArea, level, areaMap, eventFlag);
			c.moveToNext();
        }
		return row;
	}
	protected BaseCharaImpl loadStatusDataToMap(String sqlstr, SQLiteDatabase rdb) {
		Cursor c=null;
    	c = rdb.rawQuery(sqlstr, null);
    	int countItem = c.getCount();
	    //0001.アイテム1.20.AAAAAAA:0002.アイテム2.20.AAAAAAA:
        c.moveToFirst();
        Map<String, BaseCharaImpl> reMap = new HashMap<String, BaseCharaImpl>();
        BaseCharaImpl row = null;
        for(int i=0;i<countItem;i++){      	
	  		row=new BaseCharaImpl(
	  				c.getInt(c.getColumnIndex("lv")), 
	  				c.getInt(c.getColumnIndex("max_hp")), 
	  				c.getInt(c.getColumnIndex("max_mp")), 
	  				c.getInt(c.getColumnIndex("hp")), 
	  				c.getInt(c.getColumnIndex("mp")), 
	  				c.getInt(c.getColumnIndex("atk")), 
	  				c.getInt(c.getColumnIndex("int")), 
	  				c.getInt(c.getColumnIndex("def")), 
	  				c.getInt(c.getColumnIndex("mdf")), 
	  				c.getInt(c.getColumnIndex("spe")), 
	  				c.getInt(c.getColumnIndex("luc")), 
	  				c.getInt(c.getColumnIndex("exp")), 
	  				c.getInt(c.getColumnIndex("nxp")));
			c.moveToNext();
        }
		return row;
	}
	public void deleteGo(View view) {
		truchItem((Integer)view.getTag(),map);
	}
	public void useGo(View view) {
		useItem((Integer)view.getTag(),map);
	}
	public void equGo(View view) {
		equItem((Integer)view.getTag(),equ_map);
	}
	public void detailGo(View view) {
		detailItem((Integer)view.getTag());
	}
	public void equDetailGo(View view) {
		detailEquItem((Integer)view.getTag());
	}
    private void showToast(String mes, Bitmap b) {
		if(toast != null){
			toast.cancel();
		}
		if(mes!=null){
			if(b!=null){
				Matrix matrix = new Matrix();
				matrix.postScale(ITEM_SCALE, ITEM_SCALE);
				toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
				LinearLayout layout = (LinearLayout)getLayoutInflater().inflate(R.layout.row_image, null);
				TextView text = (TextView)layout.findViewById(R.id.row_textview1);
				text.setText(mes);
				text.setBackgroundResource(R.drawable.w_cc);
				text.setTextColor(Color.BLACK);
				//text.setTypeface(type);
				ImageView im = (ImageView)layout.findViewById(R.id.imageView1);
				im.setImageBitmap(Bitmap.createBitmap(b, 0, 0, b.getWidth(),b.getHeight(), matrix,false));
				
				toast.setView(layout);
			}else{
				toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
				LinearLayout layout = (LinearLayout)getLayoutInflater().inflate(R.layout.row_no_image, null);
				TextView text = (TextView)layout.findViewById(R.id.row_textview1);
				text.setBackgroundResource(R.drawable.w_cc);
				text.setTextColor(Color.BLACK);
				text.setText(mes);
				//text.setTypeface(type);
				toast.setView(layout);
			}
			toast.setGravity(Gravity.TOP, 0, (int) (dHeight/4));
			toast.show();
		}
    }
    private void showToast(Bitmap b) {
		if(dmTtoast!=null){
			dmTtoast.cancel();
		}
		if(b!=null){
			Matrix matrix = new Matrix();
			matrix.postScale(ITEM_SCALE, ITEM_SCALE);
			dmTtoast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
			LinearLayout layout = (LinearLayout)getLayoutInflater().inflate(R.layout.row_onry_image, null);
			ImageView im = (ImageView)layout.findViewById(R.id.imageDamage);
			im.setImageBitmap(Bitmap.createBitmap(b, 0, 0, b.getWidth(),b.getHeight(), matrix,false));
			dmTtoast.setView(layout);
		}else{
			dmTtoast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
			LinearLayout layout = (LinearLayout)getLayoutInflater().inflate(R.layout.row_no_image, null);
			dmTtoast.setView(layout);
		}
		dmTtoast.setGravity(Gravity.TOP, 0, (int) (dHeight/4));
		dmTtoast.show();
    }

    public void truchItem(int position, Map<String,MultiLineListRow> map) {
    	MultiLineListRow row = map.get(map.keySet().toArray()[position]);
		int count = Integer.parseInt(row.getItemCount());
		if(count > 1){
			row.decItemCount();
		}else{
			map.remove(row.getItemId());
			//item_id = map.keySet();
		}
		setContentsListView(map);
	}
    public void useItem(int position, Map<String,MultiLineListRow> map) {
    	MultiLineListRow row = map.get(map.keySet().toArray()[position]);
		if(row.getUseCount()>0){
	    	int count = Integer.parseInt(row.getItemCount());
			if(count > 1){
				switch (row.getItemType()) {
				case 11:
					useHpCare(row);
					break;
				default:
					break;
				}
				row.decItemCount();
			}else{
				switch (row.getItemType()) {
				case 11:
					useHpCare(row);
					break;
				default:
					break;
				}
				map.remove(row.getItemId());
			}
			setContentsListView(map);
		}
	}
    public void equItem(int position, Map<String,MultiLineListRow> map) {		
    	MultiLineListRow setRow = map.get(map.keySet().toArray()[position]);
    	MultiLineListRow row = c_yusya.equ(setRow);
		byte[] base64 = android.util.Base64.decode(setRow.getBase64Image() ,0);
		//showToast(setRow.getItemName()+"を装備しました", 
		//BitmapFactory.decodeByteArray(base64,0,base64.length));
		truchItem(position,map);
		setMessageView(setRow.getItemName()+"を装備しました",new BitmapDrawable(BitmapFactory.decodeByteArray(base64,0,base64.length)));
		if(row!=null){
			String itemId=row.getItemId();
			if(!map.keySet().contains(itemId)){
				map.put(row.getItemId(),row);
			}else{
				map.get(row.getItemId()).setItemCount();
				//mitem.setItemCount();
			}
			setContentsListView(map);	
		}
	}
    public void detailItem(int position) {
    	MultiLineListRow row = map.get(map.keySet().toArray()[position]);
    	byte[] base64 = android.util.Base64.decode(row.getBase64Image() ,0);
		Bitmap b = BitmapFactory.decodeByteArray(base64,0,base64.length);
		Matrix matrix = new Matrix();
		matrix.postScale(itemImgScale, itemImgScale);
		posi = position;
		AlertDialog.Builder itemDialog = new AlertDialog.Builder(TestListView.this)
		.setTitle("アイテム")
		.setMessage(row.getText(0)+"アイテムを使用しますか？")
		.setIcon( new BitmapDrawable( Bitmap.createBitmap(b, 0, 0, b.getWidth(),b.getHeight(), matrix,false)))
		.setNegativeButton("キャンセル", null)
		.setPositiveButton("捨てる",new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int id) {
				truchItem(posi,map);
				dialog.cancel();
			}
		});
		if(row.getUseCount()>0){
			itemDialog.setNeutralButton("使用",new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int id) {
					useItem(posi,map);
					dialog.cancel();
				}
			});
		}	
		if(row.getEquFlag()>0){
			itemDialog.setNeutralButton("装備",new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int id) {
					MultiLineListRow setRow = map.get(map.keySet().toArray()[posi]);
					MultiLineListRow row = null;
					row = c_yusya.equ(setRow);
					byte[] base64 = android.util.Base64.decode(setRow.getBase64Image() ,0);
					//showToast(setRow.getItemName()+"を装備しました", 
					//BitmapFactory.decodeByteArray(base64,0,base64.length));
					truchItem(posi,map);
					setMessageView(setRow.getItemName()+"を装備しました",new BitmapDrawable(BitmapFactory.decodeByteArray(base64,0,base64.length)));
					if(row!=null){
						String itemId=row.getItemId();
						if(!map.keySet().contains(itemId)){
							map.put(row.getItemId(),row);
						}else{
							map.get(row.getItemId()).setItemCount();
							//mitem.setItemCount();
						}
						setContentsListView(map);	
					}
					dialog.cancel();
				}
			});
		}	
		itemDialog.show();
	}
    public void detailEquItem(int position) {
    	MultiLineListRow row = equ_map.get(equ_map.keySet().toArray()[position]);
    	byte[] base64 = android.util.Base64.decode(row.getBase64Image() ,0);
		Bitmap b = BitmapFactory.decodeByteArray(base64,0,base64.length);
		Matrix matrix = new Matrix();
		matrix.postScale(itemImgScale, itemImgScale);
		posi = position;
		AlertDialog.Builder itemDialog = new AlertDialog.Builder(TestListView.this)
		.setTitle("アイテム")
		.setMessage(row.getText(0)+"アイテムを使用しますか？")
		.setIcon( new BitmapDrawable( Bitmap.createBitmap(b, 0, 0, b.getWidth(),b.getHeight(), matrix,false)))
		.setNegativeButton("キャンセル", null)
		.setPositiveButton("捨てる",new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int id) {
				truchItem(posi,equ_map);
				dialog.cancel();
			}
		});
		if(row.getUseCount()>0){
			itemDialog.setNeutralButton("使用",new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int id) {
					useItem(posi,equ_map);
					dialog.cancel();
				}
			});
		}	
		if(row.getEquFlag()>0){
			itemDialog.setNeutralButton("装備",new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int id) {
					MultiLineListRow setRow = map.get(map.keySet().toArray()[posi]);
					MultiLineListRow row = null;
					row = c_yusya.equ(setRow);
					byte[] base64 = android.util.Base64.decode(setRow.getBase64Image() ,0);
					//showToast(setRow.getItemName()+"を装備しました", 
					//BitmapFactory.decodeByteArray(base64,0,base64.length));
					truchItem(posi,equ_map);
					setMessageView(setRow.getItemName()+"を装備しました",new BitmapDrawable(BitmapFactory.decodeByteArray(base64,0,base64.length)));
					if(row!=null){
						String itemId=row.getItemId();
						if(!equ_map.keySet().contains(itemId)){
							equ_map.put(row.getItemId(),row);
						}else{
							equ_map.get(row.getItemId()).setItemCount();
							//mitem.setItemCount();
						}
						setContentsListView(equ_map);	
					}
					dialog.cancel();
				}
			});
		}	
		itemDialog.show();
	}
    public void setContentsListView(Map<String,MultiLineListRow> setMap) {
		ListView contentsView = (ListView) findViewById(R.id.listView1);
		List<MultiLineListRow> itema = new ArrayList<MultiLineListRow>(setMap.values());
		//item_id = map.keySet();
		if(setMap.equals(map)){
			contentsView.setAdapter(new MultiLineListRowAdapter(this, R.layout.com_multiline_row, itema, type));
		}
		if(setMap.equals(equ_map)){
			contentsView.setAdapter(new MultiLineListRowAdapter(this, R.layout.com_multiline_equ_row, itema, type));
		}
	}
	/**
	 * @throws CloneNotSupportedException 
	 * 
	 */
	public void setPrif(){
		try {
			//アイテムの格納
			item_wdb.beginTransaction();
			item_wdb.delete("item", null, null);
			for(String key:map.keySet()){
				MultiLineListRow row=map.get(key);
				ContentValues cv = setItemDataToContentValue(key, row);
				item_wdb.insert("item", null, cv);
			}
			item_wdb.setTransactionSuccessful();
			
			//ダンジョンデータの格納
			donjon_wdb.beginTransaction();
			donjon_wdb.delete("donjon", null, null);
			if(deadFlag==false){
				ContentValues cvd = setDonjonDataToContentValue(donjonMap.get(d_floor));
				donjon_wdb.insert("donjon", null, cvd);
			}
			donjon_wdb.setTransactionSuccessful();
			
			//装備アイテムの格納
			equ_stock_wdb.beginTransaction();
			equ_stock_wdb.delete("equ_stock", null, null);
			for(String key:equ_map.keySet()){
				MultiLineListRow row=equ_map.get(key);
				ContentValues cv = setItemDataToContentValue(key, row);
				equ_stock_wdb.insert("equ_stock", null, cv);
			}
			equ_stock_wdb.setTransactionSuccessful();
			
			//装備の格納
			equ_wdb.beginTransaction();
			equ_wdb.delete("equ_item", null, null);
			List<MultiLineListRow> equList = c_yusya.getEquList();
			for(MultiLineListRow row:equList){
				if(row!=null){
					ContentValues cv = setItemDataToContentValue(row.getItemId(), row);
					equ_wdb.insert("equ_item", null, cv);
				}
			}
			equ_wdb.setTransactionSuccessful();
			
			//ステータスデータの格納
			status_wdb.beginTransaction();
			status_wdb.delete("status", null, null);
			if(deadFlag){
				c_yusya.setpHp(c_yusya.getpMaxHp());
			}
			ContentValues cvd = c_yusya.getCharaDataToContentValue();
			status_wdb.insert("status", null, cvd);
			status_wdb.setTransactionSuccessful();
			
    	}catch(SQLiteException e){
    		//異常終了
    		//showToast("DB接続にしっぱいしました", null);
    	}finally{
            // トランザクションの終了
            item_wdb.endTransaction();
            equ_wdb.endTransaction();
            equ_stock_wdb.endTransaction();
            donjon_wdb.endTransaction();
            status_wdb.endTransaction();
        }
	}
	/**
	 * @throws CloneNotSupportedException 
	 * 
	 */
	public boolean getItemLocal(int count,String tableName, SQLiteDatabase rdb, int did){
		for(int i=0;i<count;i++){
			String sqlstr = "select * " +
		  	      "from "+tableName+" "+
		  	      "order by _id desc ";
		    Cursor c = rdb.rawQuery(sqlstr, null);
		    int countItem = c.getCount()-1;
		    String rand="00000001";
		    if(did<1){
		    	rand = String.format("%1$08d", new Random().nextInt(countItem)+1);
		    }else{
		    	rand = String.format("%1$08d", did);
		    }
			sqlstr = "select * " +
	  	          "from "+tableName+" "+
	  	          "where item_detail_id ='"+rand+"' "+
	  	          "order by _id desc ";
	    	c = rdb.rawQuery(sqlstr, null);
	    	c.moveToFirst();
	    	MultiLineListRowImpl row = MultiLineListRowImpl.create().setCursor(c);
	    	String itemId=row.getItemId();
			if(!map.keySet().contains(itemId)){
				map.put(row.getItemId(),row);
			}else{
				map.get(row.getItemId()).setItemCount();
			}
			//itema = new ArrayList<MultiLineListRow>(map.values());
			if(this.map.equals(map)||menuId==MENU_USE_ITEM){
				setContentsListView(map);
			}else if(this.equ_map.equals(map)||menuId==MENU_EQU_ITEM){
				setContentsListView(equ_map);
			}
			if(toast != null){
				toast.cancel();
			}
			
			byte[] base64 = android.util.Base64.decode(row.getBase64Image() ,0);
			Bitmap b = BitmapFactory.decodeByteArray(base64,0,base64.length);
			Matrix matrix = new Matrix();
			matrix.postScale(itemImgScale, itemImgScale);
			
			toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
	    	LinearLayout layout = (LinearLayout)getLayoutInflater().inflate(R.layout.row_image, null);
	        TextView text = (TextView)layout.findViewById(R.id.row_textview1);
	        text.setText(row.getItemName()+"を手に入れた。");
			String message = row.getItemName()+"を手に入れた。";
	        setMessageView(message,new BitmapDrawable(b));
	        ImageView im = (ImageView)layout.findViewById(R.id.imageView1);
	        im.setImageBitmap(Bitmap.createBitmap(b, 0, 0, b.getWidth(),b.getHeight(), matrix,false));
	        
	    	toast.setGravity(Gravity.TOP, 0, (int) (dHeight/4));
	    	toast.setView(layout);
	    	//toast.show();
		}
		return true;
	}

	/**
	 * @param message
	 */
	protected void setMessageView(String message, Drawable d) {
		//beforMesView.setText(sv.sd.getSerif()+"\n\n"+beforMesView.getText());
		//message+="　";
		//sv.sd.setSerif(message);
		//sv.sd.parse();
		//sv.sd.play();
		// ハンドラ実行
		mesImage=d;
		put_txt=message;
		i=0;
		handler.sendEmptyMessage(TIMEOUT_MESSAGE);
	}
	/**
	 * @throws CloneNotSupportedException 
	 * 
	 */
	public MultiLineListRowImpl getItemLocalData(String tableName, SQLiteDatabase rdb, int did){

	    String rand="00000001";
	    rand = String.format("%1$08d", did);
	    String sqlstr = "select * " +
  	          "from "+tableName+" "+
  	          "where item_detail_id ='"+rand+"' "+
  	          "order by _id desc ";
	    Cursor c = rdb.rawQuery(sqlstr, null);
    	c.moveToFirst();
    	MultiLineListRowImpl row = MultiLineListRowImpl.create().setCursor(c);

		return row;
	}
	
	/**
	 * @throws CloneNotSupportedException 
	 * 
	 */
	public boolean getItemLocalList(MultiLineListRow multiLineListRow,Map<String,MultiLineListRow> map, boolean messageFlag){
    	String itemId=multiLineListRow.getItemId();
		if(!map.keySet().contains(itemId)){
			map.put(multiLineListRow.getItemId(),multiLineListRow);
		}else{
			map.get(multiLineListRow.getItemId()).setItemCount();
		}
		//itema = new ArrayList<MultiLineListRow>(map.values());
		if(this.map.equals(map)||menuId==MENU_USE_ITEM){
			setContentsListView(map);
		}else if(this.equ_map.equals(map)||menuId==MENU_EQU_ITEM){
			setContentsListView(equ_map);
		}
		if(messageFlag){
			if(toast != null){
				toast.cancel();
			}
			
			byte[] base64 = android.util.Base64.decode(multiLineListRow.getBase64Image() ,0);
			Bitmap b = BitmapFactory.decodeByteArray(base64,0,base64.length);
			Matrix matrix = new Matrix();
			matrix.postScale(itemImgScale, itemImgScale);
			
			toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
	    	LinearLayout layout = (LinearLayout)getLayoutInflater().inflate(R.layout.row_image, null);
	        TextView text = (TextView)layout.findViewById(R.id.row_textview1);
	        text.setText(multiLineListRow.getItemName()+"を手に入れた。");
	        setMessageView(multiLineListRow.getItemName()+"を手に入れた。",new BitmapDrawable(b));
	        ImageView im = (ImageView)layout.findViewById(R.id.imageView1);
	        im.setImageBitmap(Bitmap.createBitmap(b, 0, 0, b.getWidth(),b.getHeight(), matrix,false));
	        
	    	toast.setGravity(Gravity.TOP, 0, (int) (dHeight/4));
	    	toast.setView(layout);
	    	//toast.show();
		}
		return true;
	}
	/**
	 * @param key
	 * @param row
	 * @return
	 */
	protected ContentValues setItemDataToContentValue(String key,
			MultiLineListRow row) {
		ContentValues cv = new ContentValues();
		cv.put("item_detail_id", key);
		cv.put("item_text", row.getText(0));
		cv.put("item_type", row.getItemType());
		cv.put("item_name", row.getItemName());
		cv.put("item_count", Integer.parseInt(row.getItemCount()));
		cv.put("item_img_binary", row.getBase64Image());
		cv.put("point", row.getPoint());
		cv.put("equ_flag", row.getEquFlag());
		cv.put("skill_id", row.getSkillId());
		cv.put("max_use_count", row.getUseCount());
		cv.put("use_count", row.getUsedCount());
		cv.put("item_version", row.getItemVersion());
		return cv;
	}
	
	/**
	 * @param key
	 * @param row
	 * @return
	 */
	protected ContentValues setDonjonDataToContentValue(DonjonFloorDataImpl donjon) {
		ContentValues cv = new ContentValues();
		cv.put("d_floor", d_floor);
		cv.put("current_time", currentTime);
		cv.put("item_count", donjonGetItemCount);
		cv.put("enemy_count", donjonGetEnemyCount);
		cv.put("current_area", donjonMap.get(d_floor).getCurrentArea());
		cv.put("area_map", donjonMap.get(d_floor).getAreaMapString());
		cv.put("level", "1");
		cv.put("event_flag", donjonMap.get(d_floor).getEventFlgString());
		return cv;
	}
	/**
	 * @param key
	 * @param row
	 * @return
	 */
	protected ContentValues setDonjonDataToContentValue(BaseChara c) {
		ContentValues cv = new ContentValues();
		cv.put("status_detail_id", "1");
		cv.put("lv", c.getpLv());
		cv.put("max_hp", c.getpMaxHp());
		cv.put("max_mp", c.getpMaxMp());
		cv.put("hp", c.getpHp());
		cv.put("mp", c.getpMp());
		cv.put("atk", c.getpAtk());
		cv.put("int", c.getpInt());
		cv.put("def", c.getpDef());
		cv.put("mdf", c.getpMdf());
		cv.put("luc", c.getpLuc());
		cv.put("spe", c.getpSpe());
		cv.put("bop", c.getpBonus());
		cv.put("exp", c.getpExp());
		cv.put("nxp", c.getpNextExp());
		return cv;
	}
	
    protected Bitmap[] getNumBitmap(int num, Bitmap header,float scale) {
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.bsp);
	  	int width = b.getWidth();
	  	int height = b.getHeight();
	  	Matrix mat = new Matrix();
		mat.postScale(scale, scale);
		String nums = String.valueOf(num);
		Bitmap[] numBit = new Bitmap[12];
		numBit[10] = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bsp), 0, 0, width, height, mat, false);
        numBit[0] = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.b0), 0, 0, width, height, mat, false);
        numBit[1] = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.b1), 0, 0, width, height, mat, false);
        numBit[2] = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.b2), 0, 0, width, height, mat, false);
        numBit[3] = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.b3), 0, 0, width, height, mat, false);
        numBit[4] = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.b4), 0, 0, width, height, mat, false);
        numBit[5] = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.b5), 0, 0, width, height, mat, false);
        numBit[6] = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.b6), 0, 0, width, height, mat, false);
        numBit[7] = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.b7), 0, 0, width, height, mat, false);
        numBit[8] = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.b8), 0, 0, width, height, mat, false);
        numBit[9] = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.b9), 0, 0, width, height, mat, false);
        int headerCount=0;
        Bitmap[] bitmap = null;
        if(header!=null){
        	header = Bitmap.createBitmap(header, 0, 0, header.getWidth(), header.getHeight(), mat, false);
        	headerCount=1;
        }
        bitmap = new Bitmap[nums.length()+headerCount];
        if(header!=null){
        	bitmap[0]=header;
        	bitmap[1]=numBit[0];
        }
        for(int i=0;i<nums.length();i++){
        	bitmap[i+headerCount]=numBit[Integer.parseInt(String.valueOf(nums.toCharArray()[i]))];
		}
    	return bitmap;
    }
	
	/**
	 * 
	 */
	public boolean enemy() {
		
		if(toast != null){
			toast.cancel();
		}
		toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
    	LinearLayout layout = (LinearLayout)getLayoutInflater().inflate(R.layout.row_image, null);
        TextView text = (TextView)layout.findViewById(R.id.row_textview1);
        text.setText("モンスターが現れた！");
        ImageView im = (ImageView)layout.findViewById(R.id.imageView1);
        Matrix matrix = new Matrix();
		matrix.postScale(itemImgScale, itemImgScale);
        im.setImageBitmap(
        		Bitmap.createBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.enemy),
        		0, 0, BitmapFactory.decodeResource(this.getResources(), R.drawable.enemy).getWidth(),
        		BitmapFactory.decodeResource(this.getResources(), R.drawable.enemy).getHeight(), matrix,false));
        
    	toast.setGravity(Gravity.BOTTOM, 0, 200);
    	toast.setView(layout);
    	toast.show();
    	
    	return true;
	}
	/**
	 * 
	 */
	public boolean damage(int point) {
		String mes = "";
		int y_hp=c_yusya.getpHp();
		y_hp=y_hp-point;
		if(y_hp>0){
			mes = "モンスターから"+point+"ダメージを受けた！";
			c_yusya.setpHp(c_yusya.getpHp()-point);
		}else{
			mes = "モンスターから"+point+"ダメージを受けた！HPが0になってしまった！";
			c_yusya.setpHp(0);
		}
		setMessageView(mes,null);
		if(toast != null){
			toast.cancel();
		}
		toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
    	LinearLayout layout = (LinearLayout)getLayoutInflater().inflate(R.layout.row_image, null);
        TextView text = (TextView)layout.findViewById(R.id.row_textview1);
        text.setText(mes);
        ImageView im = (ImageView)layout.findViewById(R.id.imageView1);
        Matrix matrix = new Matrix();
		matrix.postScale(itemImgScale, itemImgScale);
        im.setImageBitmap(
        		Bitmap.createBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.enemy),
        		0, 0, BitmapFactory.decodeResource(this.getResources(), R.drawable.enemy).getWidth(),
        		BitmapFactory.decodeResource(this.getResources(), R.drawable.enemy).getHeight(), matrix,false));
    	toast.setGravity(Gravity.BOTTOM, 0, 200);
    	toast.setView(layout);
    	//toast.show();
    	return true;
	}
	
	/**
	 * 
	 */
	public boolean useHpCare(MultiLineListRow row) {
		String mes = "";
		int y_hp=c_yusya.getpHp();
		int point = row.getPoint();
		y_hp=y_hp+point;
		if(y_hp<c_yusya.getpMaxHp()){
			mes = "回復アイテムでHPを"+point+"回復した！";
			c_yusya.setpHp(c_yusya.getpHp()+point);
		}else{
			mes = "回復アイテムでHPを"+point+"回復した！HPが全回復した！";
			c_yusya.setpHp(c_yusya.getpMaxHp());
		}
		
		byte[] base64 = android.util.Base64.decode(row.getBase64Image() ,0);
		Bitmap bt = BitmapFactory.decodeByteArray(base64,0,base64.length);
		setMessageView(mes,new BitmapDrawable(bt));
		userCareBitmap=getNumBitLine(point, null, PARAM_SCALE, paintGreen);
		
		Bitmap b = BitmapFactory.decodeResource(conte.getResources(), R.drawable.kabe);
		float donjonScale = (dHeight/10)/(float)b.getHeight();
		Matrix matrix =new Matrix();
		matrix.postScale(donjonScale, donjonScale);
		int bitmap_w = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, false).getWidth();
		long s_position = (long) (dWidth/2-(bitmap_w*6/5));
		userCareBound = new BoundBean(userCareBitmap, dHeight, dWidth, s_position, dHeight/10, 2, 0, dHeight/8,50,true,10);
		
		//if(toast != null){
		//	toast.cancel();
		//}
		//toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
    	//LinearLayout layout = (LinearLayout)getLayoutInflater().inflate(R.layout.row_image, null);
        //TextView text = (TextView)layout.findViewById(R.id.row_textview1);
        //text.setText(mes);
        //ImageView im = (ImageView)layout.findViewById(R.id.imageView1);
        //Matrix matrix = new Matrix();
		//matrix.postScale(itemImgScale, itemImgScale);
        //im.setImageBitmap(
        //		Bitmap.createBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.enemy),
        //		0, 0, BitmapFactory.decodeResource(this.getResources(), R.drawable.enemy).getWidth(),
        // 		BitmapFactory.decodeResource(this.getResources(), R.drawable.enemy).getHeight(), matrix,false));
    	//toast.setGravity(Gravity.TOP, 0, (int) (dHeight/4));
    	//toast.setView(layout);
    	//toast.show();
		//showToast(mes, null);
    	return true;
	}
	/**
	 * 
	 */
	public boolean treasure() {
		
		if(toast != null){
			toast.cancel();
		}
		toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
    	LinearLayout layout = (LinearLayout)getLayoutInflater().inflate(R.layout.row_image, null);
        TextView text = (TextView)layout.findViewById(R.id.row_textview1);
        text.setText("宝箱を手に入れた！");
        setMessageView("宝箱を手に入れた！",null);
        ImageView im = (ImageView)layout.findViewById(R.id.imageView1);
        Matrix matrix = new Matrix();
		matrix.postScale(itemImgScale, itemImgScale);
        im.setImageBitmap(
        		Bitmap.createBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.item),
        		0, 0, BitmapFactory.decodeResource(this.getResources(), R.drawable.item).getWidth(),
        		BitmapFactory.decodeResource(this.getResources(), R.drawable.item).getHeight(), matrix,false));
        
    	toast.setGravity(Gravity.TOP, 0, (int) (dHeight/4));
    	toast.setView(layout);
    	//toast.show();
		
    	return true;
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
    

	public class SampleHolderCallBack implements SurfaceHolder.Callback, Runnable{

		private TestListView view;
		private SurfaceHolder holder = null;
		private Thread thread = null;
		private boolean isAttached = true;

		private float width, height;
		private Bitmap bitmap;
		private Bitmap kabe_n;
		private Bitmap item_b;
		private Bitmap item_b_o;
		private Bitmap enemy_b;
		private Bitmap item_m;
		private Bitmap item_m_o;
		private Bitmap enemy_m;
		private Bitmap main;
		private Bitmap main_w;
		private Bitmap enemy_b_ico;
		private Bitmap item_b_ico;
		private Bitmap item_m_ico;
		private Bitmap bar;
		private Context context;
		private Matrix barMatrix = new Matrix();
		
		public SampleHolderCallBack(Context context) {
			this.view = (TestListView)context;
			Matrix matrix = new Matrix();
			Matrix icoMat = new Matrix();
			barMatrix.postScale(2f, 2f);
			
			this.bitmap		= BitmapFactory.decodeResource(context.getResources(), R.drawable.kabe);
			this.kabe_n		= BitmapFactory.decodeResource(context.getResources(), R.drawable.kabe_o);
			this.item_b		= BitmapFactory.decodeResource(context.getResources(), R.drawable.item);
			this.item_b_o	= BitmapFactory.decodeResource(context.getResources(), R.drawable.item_o);
			this.enemy_b	= BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy);
			this.item_m 	= BitmapFactory.decodeResource(context.getResources(), R.drawable.item_m);
			this.item_m_o 	= BitmapFactory.decodeResource(context.getResources(), R.drawable.item_m_o);
			this.enemy_m 	= BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy_m);
			this.main 		= BitmapFactory.decodeResource(context.getResources(), R.drawable.main);
			this.main_w 	= BitmapFactory.decodeResource(context.getResources(), R.drawable.main_w);
			this.bar 		= BitmapFactory.decodeResource(context.getResources(), R.drawable.bar);
	    	WindowManager wm = (WindowManager)getSystemService(WINDOW_SERVICE);
	    	Display disp = wm.getDefaultDisplay();
	        dWidth = disp.getWidth();
	    	dHeight = disp.getHeight();
	    	
			float donjonScale = (dHeight/10)/(float)bitmap.getHeight();
			matrix.postScale(donjonScale, donjonScale);
			icoMat.postScale(1, 1);
			
			this.item_b_ico = Bitmap.createBitmap(item_b, 0, 0, item_b.getWidth(), item_b.getHeight(), icoMat, false);
			this.item_m_ico = Bitmap.createBitmap(item_m, 0, 0, item_m.getWidth(), item_m.getHeight(), icoMat, false);
			this.enemy_b_ico = Bitmap.createBitmap(enemy_b, 0, 0, enemy_b.getWidth(), enemy_b.getHeight(), icoMat, false);
			
			this.bitmap 	= Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
			this.kabe_n 	= Bitmap.createBitmap(kabe_n, 0, 0, kabe_n.getWidth(), kabe_n.getHeight(), matrix, false);
			this.item_b 	= Bitmap.createBitmap(item_b, 0, 0, item_b.getWidth(), item_b.getHeight(), matrix, false);
			this.item_b_o 	= Bitmap.createBitmap(item_b_o, 0, 0, item_b_o.getWidth(), item_b_o.getHeight(), matrix, false);
			this.enemy_b 	= Bitmap.createBitmap(enemy_b, 0, 0, enemy_b.getWidth(), enemy_b.getHeight(), matrix, false);
			this.item_m 	= Bitmap.createBitmap(item_m, 0, 0, item_m.getWidth(), item_m.getHeight(), matrix, false);
			this.item_m_o 	= Bitmap.createBitmap(item_m_o, 0, 0, item_m_o.getWidth(), item_m_o.getHeight(), matrix, false);
			this.enemy_m 	= Bitmap.createBitmap(enemy_m, 0, 0, enemy_m.getWidth(), enemy_m.getHeight(), matrix, false);
			this.main	 	= Bitmap.createBitmap(main, 0, 0, main.getWidth(), main.getHeight(), matrix, false);
			this.main_w	 	= Bitmap.createBitmap(main_w, 0, 0, main_w.getWidth(), main_w.getHeight(), matrix, false);
			
			this.bar	 	= Bitmap.createBitmap(bar, 0, 0, bar.getWidth(), bar.getHeight(), matrix, false);

			this.context=context;
		}
		
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height) {
	// TODO 自動生成されたメソッド・スタブ
			this.width = width;
			this.height = height;
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
	// TODO 自動生成されたメソッド・スタブ
			this.holder = holder;
			thread = new Thread(this);
			thread.start(); //スレッドを開始
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO 自動生成されたメソッド・スタブ
			isAttached = false;
			thread = null; //スレッドを終了
		}

		@Override
		public void run() {
		// TODO 自動生成されたメソッド・スタブ
		// メインループ（無限ループ）
			try{
				long time = 0;
				int bitmap_w = bitmap.getWidth();
				int icon_w = item_b_ico.getWidth();
				
				int areaValue = 0;
				int frame = 150;
				boolean wFlag = false;
				eventFlg = false;
				Paint paint = new Paint();
				paint.setColor(Color.WHITE);
				paint.setTextSize(dHeight/48);
				paint.setAntiAlias(false);
				
				Paint menuPaint = new Paint();
				menuPaint.setColor(Color.WHITE);
				menuPaint.setTextSize(dHeight/48);
				menuPaint.setAntiAlias(false);
				while( isAttached ){

				    if (thread.isInterrupted()) {
				         break;
				    }
					//描画処理を開始
					Canvas canvas = null;
					if(holder!=null){
						canvas = holder.lockCanvas();
						if(canvas!=null){
							canvas.drawColor(0,PorterDuff.Mode.CLEAR );
						}
					}
					
				    //初期画面
				    if(screenId==0){
				    	canvas.drawText("初期画面", 50, 100, paint);
				    }
				    //メニュー画面		    
				    if(screenId==1){
						Bitmap sp = BitmapFactory.decodeResource(getResources(), R.drawable.bsp);
						Bitmap isp = BitmapFactory.decodeResource(getResources(), R.drawable.itemsp);
				    	float equScale=ITEM_SCALE;
				    	int paramPosition = (int) (dHeight/28);
				    	Matrix matrix = new Matrix();
						matrix.postScale(equScale, equScale);
						Matrix matrixParam = new Matrix();
						matrixParam.postScale(PARAM_SCALE, PARAM_SCALE);
						sp = Bitmap.createBitmap(sp,0,0,sp.getWidth(),sp.getHeight(),matrixParam,false);
						isp = Bitmap.createBitmap(isp,0,0,isp.getWidth(),isp.getHeight(),matrix,false);
				    	Bitmap[] spa = new Bitmap[]{sp};
						Object[] param = new Object[10];
				    	Bitmap[] equBit = new Bitmap[]{isp,isp,isp,isp,isp};
				    	String[] equName= new String[]{"","","","",""};
				    	Object[] equPointBit= new Object[]{spa,spa,spa,spa,spa};
				    	param[0]=getNumBitmap(c_yusya.getpLv(),BitmapFactory.decodeResource(getResources(), R.drawable.blv),PARAM_SCALE);
				    	param[1]=getNumBitmap(c_yusya.getpHp(),BitmapFactory.decodeResource(getResources(), R.drawable.bhp),PARAM_SCALE);
				    	param[2]=getNumBitmap(c_yusya.getpMp(),BitmapFactory.decodeResource(getResources(), R.drawable.bmp),PARAM_SCALE);
				    	param[3]=getNumBitmap(c_yusya.getpAtk(),BitmapFactory.decodeResource(getResources(), R.drawable.bat),PARAM_SCALE);
				    	param[4]=getNumBitmap(c_yusya.getpInt(),BitmapFactory.decodeResource(getResources(), R.drawable.bin),PARAM_SCALE);
				    	param[5]=getNumBitmap(c_yusya.getpDef(),BitmapFactory.decodeResource(getResources(), R.drawable.bde),PARAM_SCALE);
				    	param[6]=getNumBitmap(c_yusya.getpMdf(),BitmapFactory.decodeResource(getResources(), R.drawable.bmd),PARAM_SCALE);
				    	param[7]=getNumBitmap(c_yusya.getpSpe(),BitmapFactory.decodeResource(getResources(), R.drawable.bsd),PARAM_SCALE);
				    	param[8]=getNumBitmap(c_yusya.getpLuc(),BitmapFactory.decodeResource(getResources(), R.drawable.blu),PARAM_SCALE);
				    	param[9]=getNumBitmap(c_yusya.getpExp(),BitmapFactory.decodeResource(getResources(), R.drawable.bex),PARAM_SCALE);
				    	Bitmap[] p_maxhp = getNumBitmap(c_yusya.getpMaxHp(),BitmapFactory.decodeResource(getResources(), R.drawable.bsl),PARAM_SCALE);
				    	Bitmap[] p_maxmp = getNumBitmap(c_yusya.getpMaxMp(),BitmapFactory.decodeResource(getResources(), R.drawable.bsl),PARAM_SCALE);
				    	Bitmap[] p_nextexp = getNumBitmap(c_yusya.getpNextExp(),BitmapFactory.decodeResource(getResources(), R.drawable.bsl),PARAM_SCALE);
				    	
				    	List<MultiLineListRow> rowList = c_yusya.getEquList();
				    	for(MultiLineListRow row : rowList){
						    if(row!=null){
						    	int i = rowList.indexOf(row);
						    	equPointBit[i]=BitmapFactory.decodeResource(getResources(), R.drawable.bsp);
						    	switch (row.getItemType()) {
								case 21:
									equPointBit[i]=BitmapFactory.decodeResource(getResources(), R.drawable.bat);
									break;
								case 31:
									equPointBit[i]=BitmapFactory.decodeResource(getResources(), R.drawable.bde);
									break;
								case 41:
									equPointBit[i]=BitmapFactory.decodeResource(getResources(), R.drawable.bde);
									break;
								case 51:
									equPointBit[i]=BitmapFactory.decodeResource(getResources(), R.drawable.bde);
									break;
								case 61:
									switch (row.getSkillId()) {
									case 1:
										equPointBit[i]=BitmapFactory.decodeResource(getResources(), R.drawable.bhp);
										break;
									case 2:
										equPointBit[i]=BitmapFactory.decodeResource(getResources(), R.drawable.bmp);
										break;
									case 3:
										equPointBit[i]=BitmapFactory.decodeResource(getResources(), R.drawable.bat);
										break;
									case 4:
										equPointBit[i]=BitmapFactory.decodeResource(getResources(), R.drawable.bin);
										break;
									case 5:
										equPointBit[i]=BitmapFactory.decodeResource(getResources(), R.drawable.bde);
										break;
									case 6:
										equPointBit[i]=BitmapFactory.decodeResource(getResources(), R.drawable.bmd);
										break;
									case 7:
										equPointBit[i]=BitmapFactory.decodeResource(getResources(), R.drawable.bsd);
										break;
									case 8:
										equPointBit[i]=BitmapFactory.decodeResource(getResources(), R.drawable.blu);
										break;
									default:
										break;
									}
									break;
								default:
									break;
								}
						    	
						    	byte[] base64 = android.util.Base64.decode(row.getBase64Image() ,0);
						    	Bitmap bit = BitmapFactory.decodeByteArray(base64,0,base64.length);
						    	equBit[i]=Bitmap.createBitmap(bit, 0, 0, bit.getWidth(), bit.getHeight(), matrix, false);
						    	equName[i]=row.getItemName();
						    	equPointBit[i]=getNumBitmap(row.getPoint(),(Bitmap) equPointBit[i],PARAM_SCALE);
						    }
				    	}
					    
				    	canvas.drawText("メニュー画面", dWidth/2, 100, menuPaint);
				    	for(int j=0;j<param.length;j++){
				    		Bitmap[] p_param = (Bitmap[]) param[j];
					    	canvas.drawBitmap(p_param[0], paramPosition, paramPosition+p_param[0].getHeight()*j, menuPaint);
					    	for(int i=1;i<p_param.length;i++){
					    		Bitmap b = p_param[i];
					    		canvas.drawBitmap(b, paramPosition+p_param[0].getWidth()+(b.getWidth()*(i-1)), paramPosition+(b.getHeight()*j), menuPaint);
					    	}
				    	}
				    	//最大HP
				    	for(int i=0;i<p_maxhp.length;i++){
				    		Bitmap[] p_hp = (Bitmap[]) param[1];
				    		int pSize = p_hp.length-1;
				    		Bitmap b = p_maxhp[i];
				    		canvas.drawBitmap(b, paramPosition+p_maxhp[0].getWidth()+(b.getWidth()*(i-1)+(p_hp[0].getWidth()+p_hp[1].getWidth()*pSize)), paramPosition+(b.getHeight()*1), menuPaint);
				    	}
				    	//最大MP
				    	for(int i=0;i<p_maxmp.length;i++){
				    		Bitmap[] p_mp = (Bitmap[]) param[2];
				    		int pSize = p_mp.length-1;
				    		Bitmap b = p_maxmp[i];
				    		canvas.drawBitmap(b, paramPosition+p_maxmp[0].getWidth()+(b.getWidth()*(i-1))+(p_mp[0].getWidth()+p_mp[1].getWidth()*pSize), paramPosition+(b.getHeight()*2), menuPaint);
				    	}
				    	//NEXT経験値
				    	for(int i=0;i<p_nextexp.length;i++){
				    		Bitmap[] p_exp = (Bitmap[]) param[9];
				    		int pSize = p_exp.length-1;
				    		Bitmap b = p_nextexp[i];
				    		canvas.drawBitmap(b, paramPosition+p_nextexp[0].getWidth()+(b.getWidth()*(i-1))+(p_exp[0].getWidth()+p_exp[1].getWidth()*pSize), paramPosition+(b.getHeight()*9), menuPaint);
				    	}
				    	
					    for(int i=0;i<equBit.length;i++){
					    	Bitmap b = equBit[i];
					    	Bitmap[] para = (Bitmap[])param[0];
					    	canvas.drawBitmap(b, paramPosition, paramPosition+(para[0].getHeight()*(param.length+1))+b.getHeight()*i, menuPaint);
				    	}
				    	for(int j=0;j<equPointBit.length;j++){
				    		int posiW = isp.getWidth();
				    		int posiH = sp.getHeight()*(param.length+1);
				    		Bitmap[] p_hp = (Bitmap[]) equPointBit[j];
					    	canvas.drawBitmap(p_hp[0], paramPosition+posiW, paramPosition+posiH+isp.getHeight()*j, menuPaint);
					    	for(int i=1;i<p_hp.length;i++){
					    		Bitmap b = p_hp[i];
					    		canvas.drawBitmap(b, paramPosition+posiW+p_hp[0].getWidth()+(b.getWidth()*(i-1)), paramPosition+posiH+(isp.getHeight()*j), menuPaint);
					    	}				    		
				    	}
					    
				    }
				    //ダンジョン画面
					if(screenId==2){
						Bitmap sp = BitmapFactory.decodeResource(getResources(), R.drawable.bsp);
						Bitmap isp = BitmapFactory.decodeResource(getResources(), R.drawable.itemsp);
						int paramPosition = (int) (dHeight/28);
				    	float paramScale = (dHeight/60)/(float)sp.getHeight();
				    	float itemScale = (dHeight/30)/(float)isp.getHeight();
				    	
				    	float scale = paramScale;
				    	float equScale=itemScale;
				    	Matrix matrixBar = new Matrix();
				    	matrixBar.postScale(scale, scale);
						
						thread.sleep(frame);
						if(currentTime!=0){
							time=currentTime;
						}
						if(moveFlag){
							time+=frame;
							currentTime=time;
						}
						wFlag=!wFlag;
						long stTime = System.currentTimeMillis();

						long decTime = time;
						double moveRate = dWidth/(360/moveRateDec);
						
						Bitmap[] p_hp = getNumBitmap(c_yusya.getpHp(),BitmapFactory.decodeResource(getResources(), R.drawable.bhp),scale);
						Bitmap[] p_maxhp = getNumBitmap(c_yusya.getpMaxHp(),BitmapFactory.decodeResource(getResources(), R.drawable.bsl),scale);
						Bitmap hpBar = BitmapFactory.decodeResource(getResources(), R.drawable.bar);
						hpBar = Bitmap.createBitmap(hpBar, 0, 0, hpBar.getWidth(), hpBar.getHeight(), matrixBar, false);
						Bitmap hpBarBase = BitmapFactory.decodeResource(getResources(), R.drawable.bar_base);
						hpBarBase = Bitmap.createBitmap(hpBarBase, 0, 0, hpBarBase.getWidth(), hpBarBase.getHeight(), matrixBar, false);
						Bitmap atkBar = BitmapFactory.decodeResource(getResources(), R.drawable.bar_atk);
						atkBar = Bitmap.createBitmap(atkBar, 0, 0, atkBar.getWidth(), atkBar.getHeight(), matrixBar, false);
						
						for(int i=0;i<=decTime*moveRate/(50*bitmap_w*4/6) && i<donjonMap.get(d_floor).getAreaMap().length ;i++){
							areaValue=donjonMap.get(d_floor).getAreaMap()[i];
							currentArea=i;
							eventFlg=donjonMap.get(d_floor).getEventFlg()[currentArea];
							long s_position = (long) (dWidth/2-(bitmap_w*7/6));
							long b_position = (long) ((dWidth/2+(bitmap_w*4*(i))/6)-(decTime*moveRate/50));
							long a_position = (long) (dWidth/2);
							if(i==0 && b_position>0-bitmap_w){
								canvas.drawBitmap(kabe_n, (float) (a_position-(bitmap_w*9/6)-(decTime*moveRate/50)), bitmap_w, paint);
								canvas.drawBitmap(bitmap, (float) (a_position-(bitmap_w*5/6)-(decTime*moveRate/50)), bitmap_w, paint);
								canvas.drawBitmap(bitmap, (float) (a_position-(bitmap_w*1/6)-(decTime*moveRate/50)), bitmap_w, paint);
							}

							canvas.drawBitmap(item_b_ico, icon_w*1*1/3, 0, paint);
							canvas.drawText(String.valueOf("×"+donjonGetItemCount), icon_w*7/6, icon_w, paint);
							canvas.drawBitmap(enemy_b_ico, icon_w*1*1/3, icon_w, paint);
							canvas.drawText(String.valueOf("×"+donjonGetEnemyCount), icon_w*7/6, icon_w*5/3, paint);
							
							canvas.drawText(String.valueOf(decTime/1000), 100, 200, paint);
							
							//フロア、宝箱、エネミー描画処理
							if(b_position>0-bitmap_w){
								if(i+3==donjonMap.get(d_floor).getAreaMap().length){
									canvas.drawBitmap(kabe_n, b_position, bitmap_w, paint);
								}else if(i+3<donjonMap.get(d_floor).getAreaMap().length){
									canvas.drawBitmap(bitmap, b_position, bitmap_w, paint);
								}
								if(donjonMap.get(d_floor).getAreaMap()[i]%1000>0){
									if(eventFlg){
										canvas.drawBitmap(item_b, b_position, bitmap_w, paint);
									}else{
										canvas.drawBitmap(item_b_o, b_position, bitmap_w, paint);
									}
								}
								if(donjonMap.get(d_floor).getAreaMap()[i]%1000>1){
									if(eventFlg){
										canvas.drawBitmap(item_m, b_position, bitmap_w, paint);
									}else{
										canvas.drawBitmap(item_m_o, b_position, bitmap_w, paint);
									}
								}
								if(donjonMap.get(d_floor).getAreaMap()[i]>=1000&&donjonMap.get(d_floor).getAreaMap()[i]<2000){
									canvas.drawBitmap(enemy_b, b_position, bitmap_w, paint);
								}
								if(donjonMap.get(d_floor).getAreaMap()[i]/1000>=2){
									canvas.drawBitmap(enemy_m, b_position, bitmap_w, paint);
								}
							}

						}
						
						//主人公のPosition
						long s_position = (long) (dWidth/2-(bitmap_w*6/5));
						
						if(battleFlg){
							barMatrix.postScale(2f, (float)2*userTurnCount/atackTurn);
							//アタックバーの処理
							canvas.drawBitmap(hpBarBase, s_position, bitmap_w-(p_hp[0].getHeight()*1)+hpBarBase.getHeight(), paint);
				    		int awidth = atkBar.getWidth();
				    		if(!atBarIsFull){
				    			atwidth+=c_yusya.getpSpe()*dWidth/1500;
				    		}else{
				    			
				    		}
				    		
				    		if(atwidth>=awidth){
				    			atBarIsFull=true;
				    			atwidth=awidth;
								mHandler.post(new Runnable() {
									@Override 
									public void run() {  
										ImageView img_V = (ImageView)findViewById(R.id.windowCenter);
						    			img_V.setImageResource(R.drawable.itemsp);
									}
								});
				    			switch (command) {
								case COMMAND_ATK:
					    			atwidth=1;
					    			atBarIsFull=false;
					    			command=COMMAND_NO;
									mHandler.post(new Runnable() {
										@Override 
										public void run() {  
											ImageView img_V = (ImageView)findViewById(R.id.windowCenter);
											img_V.setImageResource(R.drawable.item_m_sp);
										}
									});

					    			enemyDamege = enemy.getAtkDamage(c_yusya.getpAtk(),true);
					    			//Bitmap[] enemyD = getNumBitmap(enemyDamege, null, scale);
					    			//dmgBitmap = Bitmap.createBitmap(enemyD[0].getWidth()*enemyD.length ,enemyD[0].getHeight() , Bitmap.Config.ARGB_8888);
					    		  	//Canvas d_canvas = new Canvas(dmgBitmap);
					    		  	//for(int i=0;i<enemyD.length;i++){
					    		  	//	d_canvas.drawBitmap(enemyD[i], enemyD[i].getWidth()*i , 0, paint);
					    		  	//	canvas.drawBitmap(enemyD[i], dWidth/2+enemyD[i].getWidth()*i, bitmap_w, paint);
					    		  	//}
					    			dmgBitmap = getNumBitLine(enemyDamege, null, PARAM_SCALE, paint);
					    		  	isGetEnemyDamege=true;
					    		  	enemyBound = new BoundBean(dmgBitmap, dHeight, dWidth, dWidth/2, dHeight/8, 2, new Random().nextInt((int) (dWidth/100)), dHeight/6,30,true,40);
					    		  	
					    			if(enemy.isLive()){
					    				mes="モンスターに"+enemyDamege+"のダメージを与えた！";
					    				mHandler.post(new Runnable() {  
											@Override 
											public void run() {
												setMessageView(mes,null);
											}
										});
					    			}else{
					    				mes="モンスターに"+enemyDamege+"のダメージを与えた！\n\nモンスターを倒した！";
					    				c_yusya.setpExp(c_yusya.getpExp()+enemy.getpExp());
					    				if(c_yusya.getpExp()>=c_yusya.getpNextExp()){
					    					c_yusya.setpExp(c_yusya.getpExp()-c_yusya.getpNextExp());
					    					c_yusya.setpNextExp((int) (c_yusya.getpNextExp()*1.1));
					    					c_yusya.lvUp();
					    					mes+="\nレベルが上がった！";
					    				}
					    				mHandler.post(new Runnable() {  
											@Override 
											public void run() {
							    				try {
													thread.sleep(500);
												} catch (InterruptedException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
												List<MultiLineListRow> list = enemy.getEquList();
												int rand = new Random().nextInt(5);
												MultiLineListRow getitem = list.get(rand);
												Bitmap b = null;
												if(null!=getitem){
													getItemLocalList(getitem, equ_map, false);
													mes+="\nモンスターが"+getitem.getItemName()+"を落とした！";
													byte[] base64 = android.util.Base64.decode(getitem.getBase64Image() ,0);
													b = BitmapFactory.decodeByteArray(base64,0,base64.length);
												}
												//showToast(mes, b);
												setMessageView(mes,new BitmapDrawable(b));
											}
										});
					    				thread.sleep(1500);
										atwidth=1;
										at_m_width=1;
					    				donjonMap.get(d_floor).getAreaMap()[currentArea-1]=donjonMap.get(d_floor).getAreaMap()[currentArea-1]%1000;
					    				battleFlg=false;
					    				moveFlag=true;
					    			}
									break;
								case COMMAND_MGK:
									
									break;
								case COMMAND_GUARD:
									
									break;
								case COMMAND_ESCAPE:
									
									break;
								default:
									break;
								}
				    		}
				    		Bitmap drowAt=Bitmap.createBitmap(atkBar, 0, 0, atwidth, atkBar.getHeight());
							canvas.drawBitmap(drowAt, s_position, bitmap_w-(p_hp[0].getHeight()*1)+hpBarBase.getHeight(), paint);
							canvas.drawBitmap(main, s_position, bitmap_w, paint);
							
							Bitmap[] eHp = getNumBitmap(enemy.getpHp(),BitmapFactory.decodeResource(getResources(), R.drawable.bhp),scale);
							Bitmap[] eMaxHp = getNumBitmap(enemy.getpMaxHp(),BitmapFactory.decodeResource(getResources(), R.drawable.bsl),scale);
							//エネミーHP
							canvas.drawBitmap(eHp[0], s_position+bitmap_w, bitmap_w+hpBarBase.getHeight(), paint);
							for(int j=1;j<eHp.length;j++){
					    		Bitmap b = eHp[j];
					    		canvas.drawBitmap(b, s_position+(b.getWidth()*(j-1))+eHp[0].getWidth()+bitmap_w, bitmap_w+hpBarBase.getHeight(), paint);
					    	}
					    	//エネミー最大HP
					    	for(int j=0;j<eMaxHp.length;j++){
					    		int pSize = eHp.length-1;
					    		Bitmap b = eMaxHp[j];
					    		canvas.drawBitmap(b, s_position+(b.getWidth()*(j)+(eHp[0].getWidth()+eHp[1].getWidth()*pSize))+bitmap_w, bitmap_w+hpBarBase.getHeight(), paint);
					    	}
					    	canvas.drawBitmap(hpBarBase, s_position+bitmap_w, bitmap_w+eHp[0].getHeight()+hpBarBase.getHeight(), paint);
					    	double hp = enemy.getpHp();
					    	double mhp = enemy.getpMaxHp();
					    	if(hp>0){
					    		int hpwidth = (int) (hpBar.getWidth()*(hp/mhp));
					    		
					    		Bitmap drowHp=Bitmap.createBitmap(hpBar, 0, 0, hpwidth, hpBar.getHeight());
					    		canvas.drawBitmap(drowHp, s_position+bitmap_w, bitmap_w+eHp[0].getHeight()+hpBarBase.getHeight(), paint);
					    	}
							if(enemy.isLive()){
						    	//アタックバーの処理
								canvas.drawBitmap(hpBarBase, s_position+bitmap_w, bitmap_w+eHp[0].getHeight()+hpBarBase.getHeight()+hpBarBase.getHeight(), paint);
					    		awidth = atkBar.getWidth();
					    		at_m_width+=enemy.getpSpe()*dWidth/1500;
					    		if(at_m_width>awidth){
					    			at_m_width=1;
					    			userDamege = c_yusya.getAtkDamage(enemy.getpAtk(),true);
					    			//Bitmap[] enemyD = getNumBitmap(userDamege, null, PARAM_SCALE);
					    			//userDmgBitmap = Bitmap.createBitmap(enemyD[0].getWidth()*enemyD.length ,enemyD[0].getHeight() , Bitmap.Config.ARGB_8888);
					    		  	//Canvas d_canvas = new Canvas(userDmgBitmap);
					    		  	//for(int i=0;i<enemyD.length;i++){
					    		  	//	d_canvas.drawBitmap(enemyD[i], enemyD[i].getWidth()*i , 0, paintRed);
					    		  	//}
					    			userDmgBitmap=getNumBitLine(userDamege, null, PARAM_SCALE, paintRed);
					    		  	userBound = new BoundBean(userDmgBitmap, dHeight, dWidth, s_position, dHeight/8, 2, (new Random().nextInt((int) (dWidth/100)))*-1, dHeight/6,30,true,10);
					    		  	
					    			if(c_yusya.isLive()){
					    				mes="モンスターから"+userDamege+"のダメージを受けた！";
					    				mHandler.post(new Runnable() {  
											@Override 
											public void run() {
												setMessageView(mes,null);
											}
										});
					    			}else{
					    				deadFlag=true;
					    				mes="モンスターから"+userDamege+"のダメージを受けた！死んでしまった！";
					    				mHandler.post(new Runnable() {  
											@Override 
											public void run() {
												showToast(mes, null);
												setMessageView(mes,null);
											}
										});
					    			}
					    		}
					    		Bitmap drow_e_At=Bitmap.createBitmap(atkBar, 0, 0, at_m_width, atkBar.getHeight());
								canvas.drawBitmap(drow_e_At, s_position+bitmap_w, bitmap_w+eHp[0].getHeight()+hpBarBase.getHeight()*2, paint);
							}
						}else if(wFlag){
							canvas.drawBitmap(main_w, s_position, bitmap_w, paint);
							//battleCount++;
						}else{
							canvas.drawBitmap(main, s_position, bitmap_w, paint);
							battleCount++;
						}
						//canvas.drawText("battleTime:"+String.valueOf(battleCount), dWidth/3, (dHeight*3/7), paint);
						//if(battleCount>10){
						//	battleFlg=false;
						//	battleCount=0;
						//}
						
						//HP
						canvas.drawBitmap(p_hp[0], s_position, bitmap_w-(p_hp[0].getHeight()*2), paint);
						for(int i=1;i<p_hp.length;i++){
				    		Bitmap b = p_hp[i];
				    		canvas.drawBitmap(b, s_position+(b.getWidth()*(i-1))+p_hp[0].getWidth(), bitmap_w-(p_hp[0].getHeight()*2), paint);
				    	}
				    	//最大HP
				    	for(int i=0;i<p_maxhp.length;i++){
				    		int pSize = p_hp.length-1;
				    		Bitmap b = p_maxhp[i];
				    		canvas.drawBitmap(b, s_position+(b.getWidth()*(i)+(p_hp[0].getWidth()+p_hp[1].getWidth()*pSize)), bitmap_w-(p_hp[0].getHeight()*2), paint);
				    	}
				    	canvas.drawBitmap(hpBarBase, s_position, bitmap_w-(p_hp[0].getHeight()*1), paint);
				    	double hp = c_yusya.getpHp();
				    	double mhp = c_yusya.getpMaxHp();
				    	if(hp>0){
				    		int hwidth = hpBar.getWidth();
				    		int hpwidth = (int) (hpBar.getWidth()*(hp/mhp));
				    		Bitmap drowHp=Bitmap.createBitmap(hpBar, 0, 0, hpwidth, hpBar.getHeight());
				    		canvas.drawBitmap(drowHp, s_position, bitmap_w-(p_hp[0].getHeight()*1), paint);
				    	}else{
				    		appEnd();
				    	}
				    	if(!battleFlg){
							time += System.currentTimeMillis()-stTime;
						}
						if(currentArea>0){
							areaValue=donjonMap.get(d_floor).getAreaMap()[currentArea-1];
							if(donjonMap.
									
									get(d_floor).getEventFlg()[currentArea-1]){
								if (areaValue>=1000&&!battleFlg) {							
									eventFlg=true;
									if(areaValue<1500){
										enemy = new BaseCharaImpl(1, 10+2*d_floor, 10, 10+2*d_floor, 10, 2*d_floor, 2*d_floor, 2*d_floor, 10, 19, 10, 10+d_floor*2, 0);
										enemy.equ(getItemLocalData("equ_list", equ_item_list_rdb, 10000032));
										if(new Random().nextInt(2)!=0){
										enemy.equ(getItemLocalData("equ_list", equ_item_list_rdb, 10000033));}
										if(new Random().nextInt(2)!=0){
										enemy.equ(getItemLocalData("equ_list", equ_item_list_rdb, 10000034));}
										if(new Random().nextInt(2)!=0){
										enemy.equ(getItemLocalData("equ_list", equ_item_list_rdb, 10000035));}
									}else{
										if(d_floor>=3){
											enemy = new BaseCharaImpl(5, 50+5*d_floor, 10, 50+5*d_floor, 10, 15+2*d_floor, 2*d_floor, 10, 10, 18, 10, 50+d_floor*2, 0);
											enemy.equ(getItemLocalData("equ_list", equ_item_list_rdb, 10000036));
										}else{
											enemy = new BaseCharaImpl(5, 50+10*d_floor, 10, 50+5*d_floor, 10, 15+2*d_floor, 6*d_floor, 10, 10, 18, 10, 50+d_floor*2, 0);
										}
									}
									battleFlg=true;
									moveFlag=false;
									thread.sleep(1000);
								}
								if(areaValue%1000>0&&!battleFlg){
									mHandler.post(new Runnable() {  
										@Override 
										public void run() {  
											eventFlg = !treasure();
											donjonGetItemCount++;
										}  
									});
									thread.sleep(1000);
									donjonMap.get(d_floor).getEventFlg()[currentArea-1] = false;
								}else if(!battleFlg){
									donjonMap.get(d_floor).getEventFlg()[currentArea-1] = false;
								}
							}
						}
						if(currentArea+1>=donjonMap.get(d_floor).getAreaMap().length){
							moveFlag = false;
							int alpha=paint.getAlpha();
							paint.setAlpha(paint.getAlpha()-35);
							// 最小値を超えた場合
							if(alpha<paint.getAlpha()){
								paint.setAlpha(0);
								Map<Integer,DonjonFloorDataImpl> ma = new HashMap<Integer,DonjonFloorDataImpl>();
								DonjonFloorDataImpl dmap = new DonjonFloorDataImpl();
								d_floor++;
								dmap.setAreaMap(10,1,5,5);
								ma.put(d_floor, dmap);
								donjonMap = ma;
								currentArea = 0;
								time=0;
								currentTime=0;
								areaValue=0;
								decTime=0;
								fadeFlag=true;
								moveFlag=true;
							}
						}
						if(fadeFlag){
		    				mHandler.post(new Runnable() {  
								@Override 
								public void run() {
									showToast("フロア"+d_floor,null);
								}
							});
							int alpha=paint.getAlpha();
							paint.setAlpha(paint.getAlpha()+35);
							if(alpha>paint.getAlpha()){
								paint.setAlpha(255);
								fadeFlag=false;
							}
						}
					}else{
						
					}
					//canvas.drawText("横："+dWidth+"px 縦:"+dHeight+"px", 100, 250, paint);
					//描画処理を終了
					
					holder.unlockCanvasAndPost(canvas);

				}
			}catch(Exception e){
				//thread = null;
			}
			
		}
		
	}
	
	 /* 
	  * オーバーレイのMySurfaceViewCallback 
	  */
	 public class OverLaySurfaceViewCallback implements SurfaceHolder.Callback ,Runnable{ 
	  
	  private Context ovContext; 
		
	  private TestListView view;
		private SurfaceHolder holder = null;
		private Thread thread = null;
		private boolean isAttached = true;
		int width;
		int height;
		public OverLaySurfaceViewCallback(Context context) {
			ovContext=context;
		}
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height) {
	// TODO 自動生成されたメソッド・スタブ
			this.width = width;
			this.height = height;
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
	// TODO 自動生成されたメソッド・スタブ
			this.holder = holder;
			thread = new Thread(this);
			thread.start(); //スレッドを開始
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO 自動生成されたメソッド・スタブ
			isAttached = false;
			thread = null; //スレッドを終了
		}
		@Override
		public void run() {
			try {
				
		// TODO 自動生成されたメソッド・スタブ
		// メインループ（無限ループ）
			Bitmap kabe 	= BitmapFactory.decodeResource(ovContext.getResources(), R.drawable.kabe);
			Bitmap main_b 	= BitmapFactory.decodeResource(ovContext.getResources(), R.drawable.main_b);
			Bitmap main_w 	= BitmapFactory.decodeResource(ovContext.getResources(), R.drawable.main);
			Bitmap sp 	= BitmapFactory.decodeResource(ovContext.getResources(), R.drawable.bsp);
			Bitmap effect = BitmapFactory.decodeResource(ovContext.getResources(), R.drawable.e_slash);
			Bitmap eAtk01 = BitmapFactory.decodeResource(ovContext.getResources(), R.drawable.e_e_atk01);
			
			Matrix matrix = new Matrix();
			Matrix e_matrix = new Matrix();
			float donjonScale = (dHeight/10)/(float)main_b.getHeight();
			float efectScale = (dHeight/14)/(float)main_b.getHeight();
			Bitmap[] eArray = new Bitmap[9];
			Bitmap[] eEAtkArray = new Bitmap[9];
			matrix.postScale(donjonScale, donjonScale);
			e_matrix.postScale(efectScale, efectScale);
			for(int i=0;i<eArray.length;i++){
				eArray[i]=effect.createBitmap(effect, effect.getHeight()*i, 0, effect.getHeight(), effect.getHeight(), e_matrix, false);
			}
			for(int i=0;i<eEAtkArray.length;i++){
				eEAtkArray[i]=eAtk01.createBitmap(eAtk01, eAtk01.getHeight()*i, 0, eAtk01.getHeight(), eAtk01.getHeight(), e_matrix, false);
			}
			kabe = Bitmap.createBitmap(kabe, 0, 0, kabe.getWidth(), kabe.getHeight(), matrix, false);
			main_b = Bitmap.createBitmap(main_b, 0, 0, main_b.getWidth(), main_b.getHeight(), matrix, false);
			main_w = Bitmap.createBitmap(main_w, 0, 0, main_w.getWidth(), main_w.getHeight(), matrix, false);
			Paint paint = new Paint();
			paint.setColor(Color.WHITE);
			paint.setTextSize(dHeight/48);
			paint.setAntiAlias(false);
			int attackCount = 0;
			int e_attackCount = 0;
			
			//boolean isGetEnemyDamege=false;
			int rand = new Random().nextInt(5)+3;
			long s_position = (long) (dWidth/2-(kabe.getWidth()*6/5));
			long e_position = (long) (dWidth/2-(kabe.getWidth()*4/5));
			long e_atkPosition = (long) (dWidth/2-(kabe.getWidth()*8/5));
			List<Integer> bbDeleteList = new ArrayList<Integer>();
			//描画処理を開始
			while(isAttached){
				//描画処理を開始
				Canvas mCanvas = null;
				if(holder!=null){
					mCanvas = holder.lockCanvas();
					if(mCanvas!=null){
						mCanvas.drawColor(0,PorterDuff.Mode.CLEAR );
					}
				}
				for(BoundBean bb: bbList){
					if(bb.activeFlag){
						float[] xy=bb.getPrint();
						mCanvas.drawBitmap(bb.getBitmap(),xy[0], xy[1], null);
					}else{
						bbDeleteList.add(bbList.indexOf(bb));
					}
				}
				for(int deleteIndex:bbDeleteList){
					bbList.remove(deleteIndex);
				}
				if(bbDeleteList.size()>0){
					bbDeleteList= new ArrayList<Integer>(); 
				}
				if(userBound.activeFlag){
					e_attackCount++;
					if(e_attackCount>40){
						float[] xy=userBound.getPrint();
						mCanvas.drawBitmap(userBound.getBitmap(),xy[0], xy[1], null);
						if(userBound.isActiveFlag()==false){
							e_attackCount=0;
						}
					}else if(e_attackCount<40){
						if(e_attackCount/4<9){
							mCanvas.drawBitmap(eEAtkArray[e_attackCount/4], e_atkPosition, kabe.getWidth(), paint);
						}
					}
				}
				if(enemyBound.activeFlag){
					attackCount++;
					if(attackCount>40){
						//Bitmap[] enemyD = getNumBitmap(enemyDamege, null, PARAM_SCALE);
						float[] xy=enemyBound.getPrint();
						mCanvas.drawBitmap(enemyBound.getBitmap(),xy[0], xy[1], null);
						if(enemyBound.isActiveFlag()==false){
							attackCount=0;
						}
					}else if(attackCount>30){
						mCanvas.drawBitmap(main_w, s_position, kabe.getWidth(), paint);
						if(attackCount-30<9){
							mCanvas.drawBitmap(eArray[attackCount-30], e_position, kabe.getWidth(), paint);
						}
					}else if(attackCount>20){
						mCanvas.drawBitmap(main_b, s_position, kabe.getWidth(), paint);
					}else if(attackCount>10){
						mCanvas.drawBitmap(main_w, s_position, kabe.getWidth(), paint);
						if(attackCount-10<9){
							mCanvas.drawBitmap(eArray[attackCount-10], e_position, kabe.getWidth(), paint);
						}
					}else if(attackCount<10){
						mCanvas.drawBitmap(main_b, s_position, kabe.getWidth(), paint);
					}
				}
				if(userCareBound.isActiveFlag()){
					float[] xy=userCareBound.getPrint();
					mCanvas.drawBitmap(userCareBound.getBitmap(),xy[0], xy[1], null);
				}
				
				holder.unlockCanvasAndPost(mCanvas);
				}

			} catch (Exception e) {
				surfaceCreated(holder);
			}
		}
	 }

	private float lastTouchX;
	private float currentX;
	private float lastTouchY;
	private float currentY;
	private class FlickTouchListener implements View.OnTouchListener {

	    @Override
	    public boolean onTouch(View v, MotionEvent event) {
            ImageView windowTop = (ImageView)findViewById(R.id.windowTop);
            ImageView windowBottom = (ImageView)findViewById(R.id.windowBottom);
            ImageView windowLeft = (ImageView)findViewById(R.id.windowLeft);
            ImageView windowRight = (ImageView)findViewById(R.id.windowRight);
            int kando=(int) (dWidth/7);
            int idoX=0;
            int idoY=0;
            //atkBarがfullのときだけじっこう
            if(atBarIsFull&&screenId==SCREEN_DONJON){
		    	switch (event.getAction()) {
		        case MotionEvent.ACTION_DOWN:
		        		lastTouchX = event.getX();
		        		lastTouchY = event.getY();
		        		windowTop.setImageResource(R.drawable.comm_mgk);
		        		windowBottom.setImageResource(R.drawable.comm_escape);

		        		//if(c_yusya.geteRightHand()==null){
		        			windowRight.setImageResource(R.drawable.comm_atk);
		        		//}else{
		        		//	byte[] base64 = android.util.Base64.decode(c_yusya.geteRightHand().getBase64Image(),0);
		        		//	windowRight.setImageBitmap(BitmapFactory.decodeByteArray(base64,0,base64.length));
		        		//}
		        		//if(c_yusya.geteLeftHand()==null){
		        			windowLeft.setImageResource(R.drawable.comm_guard);
		        		//}else{
		        		//	byte[] base64 = android.util.Base64.decode(c_yusya.geteLeftHand().getBase64Image(),0);
		        		//	windowLeft.setImageBitmap(BitmapFactory.decodeByteArray(base64,0,base64.length));
		        		//}
		        	break;
		        case MotionEvent.ACTION_MOVE:
		            currentX = event.getX();
		            currentY = event.getY();
		            idoX = (int) Math.abs(lastTouchX - currentX);
		            idoY = (int) Math.abs(lastTouchY - currentY);
		            if(idoX>idoY){
		            	if (lastTouchX < currentX-kando) {
		            		//フリック右
		            	}else if (lastTouchX > currentX+kando) {
			            	//フリック左
			            }else{
			            }
		            }else{
		            	if (lastTouchY < currentY-kando) {
		            		//フリック下
		            	}else if (lastTouchY > currentY+kando) {
		            		//フリック上
		            	}else{
		            	}
		            }
		        break;
		        
		        case MotionEvent.ACTION_UP:
		            currentX = event.getX();
		            currentY = event.getY();
		            idoX = (int) Math.abs(lastTouchX - currentX);
		            idoY = (int) Math.abs(lastTouchY - currentY);
		            if(idoX>idoY){
		            	if (lastTouchX < currentX-kando) {
		            		//フリック右
		            		command=COMMAND_ATK;
		            	}
			            if (lastTouchX > currentX+kando) {
			            	//フリック左
			            	showToast("左フリック", null);
			            }
		            }else{
		            	if (lastTouchY < currentY-kando) {
		            		//フリック下
		            		overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
		            	}
		            	if (lastTouchY > currentY+kando) {
		            		//フリック上
		            		showToast("上フリック", null);
		            	}
		            }
		            windowTop.setImageResource(R.drawable.item_m_sp);
		            windowBottom.setImageResource(R.drawable.item_m_sp);
		            windowLeft.setImageResource(R.drawable.item_m_sp);
		            windowRight.setImageResource(R.drawable.item_m_sp);
		            break;
	
		        case MotionEvent.ACTION_CANCEL:
		            currentX = event.getX();
		            currentY = event.getY();
		            idoX = (int) Math.abs(lastTouchX - currentX);
		            idoY = (int) Math.abs(lastTouchY - currentY);
		            if(idoX>idoY){
		            	if (lastTouchX < currentX-kando) {
		            		//フリック右
		            		command=COMMAND_ATK;
		            	}
			            if (lastTouchX > currentX+kando) {
			            	//フリック左
			            	showToast("左フリック", null);
			            }
		            }else{
		            	if (lastTouchY < currentY-kando) {
		            		//フリック下
		            		showToast("下フリック", null);
		            	}
		            	if (lastTouchY > currentY+kando) {
		            		//フリック上
		            		showToast("上フリック", null);
		            	}
		            }
		            windowTop.setImageResource(R.drawable.item_m_sp);
		            windowBottom.setImageResource(R.drawable.item_m_sp);
		            windowLeft.setImageResource(R.drawable.item_m_sp);
		            windowRight.setImageResource(R.drawable.item_m_sp);
		            break;
		        }
            }
	        return true;
	    }
	}
	/** 再生終了イベント **/
	@Override
	public void onSerifStop(View v, SerifDirector director, 
			PageInfo currentPage, Paragraph activeParagraph) {
		setTitle("onSerifStop" + 
				" pageNo:" + currentPage.getPageIndex() +
				" paragraphIndex:" + activeParagraph.getParagraphIndex()+
				" paragraphCount:" + currentPage.getParagraphCount());
		
		//ビューの背景色を変更
		v.setBackgroundColor(Color.BLACK);
	}
	
	
	/** 次のページへ移動イベント **/
	@Override
	public void onSerifNextPage(View v, SerifDirector director, 
			PageInfo newPage, PageInfo oldPage) {
		setTitle("onSerifNextPage" +
				" newpageNo:" + newPage.getPageIndex() +
				" oldpageNo:" + oldPage.getPageIndex());
	}

	/** キャンバスエフェクトイベント **/
	@Override
	public void onSerifCanvasEffect(View v, SerifDirector director,
			PageInfo currentPage, Paragraph activeParagraph, Canvas c,
			float progress) {
		
		if (currentPage.getPageIndex() == 3) {
			
			//エフェクトフラグをいったんクリア
			director.effectFlag = 0;
			//左からの横スクロールエフェクトをかける
    		c.translate(director.getDrawWidth() * (progress - 1), 1);
		}
	}
	

	/** セリフ1文字描画イベント **/
	@Override
	public void onSerifUpdate(View v, SerifDirector director, 
			int currentPos, PageInfo currentPage, Paragraph activeParagraph) {
		setTitle("onSerifUpdate" +
				" currentPos:" + currentPos +
				" paragraphLength:" + activeParagraph.getTotalLength() + 
				" pageLength:" + currentPage.getTotalLength() + 
				" totalLength:" + director.getTotalLength());
		
		//以下、文字を１文字出すたびにbeep音を鳴らすコード
		//※注意！：最大ボリュームで再生されます
		//※作者の環境では20回連続で鳴らしたあたりでRuntimeException: init failedが発生します
		//あまり連続beep再生してはいけないのかも
		/*try {
		ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_SYSTEM, ToneGenerator.MAX_VOLUME);
		toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);
		} catch (Exception e) {
			e.printStackTrace();
		}*/		
	}

	/** セリフスキップイベント **/
	@Override
	public void onSerifSkip(View v, SerifDirector director, 
			PageInfo currentPage, Paragraph activeParagraph) {
		setTitle("onSerifSkip");
	}

	/** 段落・ページめくり待ちイベント **/
	@Override
	public void onSerifWait(View v, SerifDirector director, 
			PageInfo currentPage, Paragraph activeParagraph) {
		setTitle("onSerifWait" + 
				" pageNo:" + currentPage.getPageIndex() +
				" paragraphIndex:" + currentPage.getActiveParagraphIndex() +
				" paragraphCount:" + currentPage.getParagraphCount());
	}

	/** 次の段落イベント **/
	@Override
	public void onSerifNextParagraph(View v, SerifDirector director, 
			PageInfo currentPage, Paragraph newParagraph, Paragraph oldParagraph) {
		setTitle("onSerifNextParagraph" + 
				" pageNo:" + currentPage.getPageIndex() +
				" new paragraphIndex:" + newParagraph.getParagraphIndex() + 
				" old paragraphIndex:" + oldParagraph.getParagraphIndex() 
				);
	}

	/** 前のページへ移動イベント **/
	@Override
	public void onSerifPrevious(View v, SerifDirector director, 
			PageInfo newPage, PageInfo oldPage) {
		setTitle("onSerifPrevious" +
				" newpageNo:" + newPage.getPageIndex() +
				" oldpageNo:" + oldPage.getPageIndex() );
	}

	/** セリフ描画開始イベント **/
	@Override
	public void onSerifStart(View v, SerifDirector director) {
		setTitle("onSerifStart");
	}

	/** ページ変更イベント。ページが変更された時と、表示を開始した時に発生する。**/
	@Override
	public void onSerifPageChange(View v, SerifDirector director, 
			PageInfo newPage, PageInfo oldPage) {
		setTitle("onSerifMove" +
				" newpageNo:" + newPage.getPageIndex() +
				" oldpageNo:" + oldPage.getPageIndex() );
		
		v.setBackgroundColor(Color.BLACK);
	}
	
	// 文字列を一文字ずつ出力するハンドラ
    private Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            // 文字列を配列に１文字ずつセット
            char data[] = put_txt.toCharArray();
             
            // 配列数を取得
            int arr_num = data.length;
            if(startText==true && i==0){
            	///for (int j = iTmp; j < data.length; j++) {
                //    put_word = String.valueOf(data[j]);
                //    put_text = put_text + put_word;
				//}
            	tmp_text = tmp_set+"\n"+tmp_text;
            	put_text="";
            	i=0;
            	logView.setText(tmp_text);
            	logView2.setText(tmp_text);
            	startText=false;
            }      
            if(startText==false && i == 0){
                // 表示する画像を取得し、サイズを設定します。
            	if(mesImage!=null){
            		mesImage.setBounds(0, 0, (int)(mesImage.getIntrinsicWidth()*ITEM_SCALE*5), (int)(mesImage.getIntrinsicHeight()*ITEM_SCALE*5));
            	}
            	textView2.setCompoundDrawables(mesImage, null, null, null);
            	tmp_set=put_txt;
            	tmp_text = put_text+"\n"+tmp_text;
            	put_text="";
            	//logView.setText(tmp_text);
            	logView2.setText(tmp_text);
            	startText=false;
            }
            if(i < arr_num){
                if (msg.what == TIMEOUT_MESSAGE) {
                	startText=true;
                    put_word = String.valueOf(data[i]);
                    put_text = put_text + put_word;
                    //textView.setText(put_text);
                    textView2.setText(put_text+"◆");
                    handler.sendEmptyMessageDelayed(TIMEOUT_MESSAGE, INTERVAL * 50);
                    i++;
                    iTmp = i;
                    if(i==arr_num){
                    	logView.setText(put_text+"\n"+tmp_text);
                    	//textView2.setText(put_text+"◆");
                    }
                }else{
                    super.dispatchMessage(msg);
                }
            }
        }
    };
}
