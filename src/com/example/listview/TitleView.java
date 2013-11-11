package com.example.listview;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class TitleView extends Activity {
    /** Called when the activity is first created. */
	
	final static int ITEM_DB_VERSION = 21;
	final static int ITEM_LIST_DB_VERSION = 2;
	final static int EQU_DB_VERSION = 2;
	final static int EQU_LIST_DB_VERSION = 1;
	
	Context myContext=null;
	Activity myActivity = null;
	AsyncTaskProgressDialogSimple m_SimpleThread;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.title);
        myContext=this;
        myActivity=this;
        Button titleButton=(Button)findViewById(R.id.title_button);
        // フォントを取得
        Typeface tf = Typeface.createFromAsset(getAssets(), "Blade.ttf");
        // フォントを設定
        titleButton.setTypeface(tf);
        // フォントサイズを20に設定
        titleButton.setTextSize(50.0f);
        titleButton.setOnClickListener(new OnClickListener(){
         @Override
           public void onClick(View v) {
        	 	//ImageView title = (ImageView)findViewById(R.id.titleView1);
        	 	//title.setImageResource(R.drawable.comm_mgk);
        	 	//title.setAlpha(255);
        	 	//Animation anim = AnimationUtils.loadAnimation(myContext, R.anim.rotate1);
        	 	//title.startAnimation(anim);
             	// 非同期(スレッド)処理クラスの生成
             	//m_SimpleThread = new AsyncTaskProgressDialogSimple(myActivity);
             	// 非同期(スレッド)処理の実行
             	//m_SimpleThread.execute();
             	
        	 	Intent intent = new Intent(TitleView.this, TestListView.class);
        	 	startActivity(intent);
        	 	overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
          }
        });
     	// 非同期(スレッド)処理クラスの生成
     	m_SimpleThread = new AsyncTaskProgressDialogSimple(myActivity);
     	// 非同期(スレッド)処理の実行
     	m_SimpleThread.execute();
     	
    	m_SimpleThread.m_ProgressDialog.dismiss();
    }
    /*
     * onPause時の処理
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.v("ActivityMain", "onPause()");
        // "プログレスダイアログ表示（シンプル）" のスレッド、プログレスダイアログが存在する場合
        if (this.m_SimpleThread != null &&
            this.m_SimpleThread.m_ProgressDialog != null) {
            Log.v("this.m_SimpleThread.m_ProgressDialog.isShowing()", String.valueOf(this.m_SimpleThread.m_ProgressDialog.isShowing()));
            // プログレスダイアログ表示中の場合
            if (this.m_SimpleThread.m_ProgressDialog.isShowing()) {
                // プログレスダイアログを閉じる
                this.m_SimpleThread.m_ProgressDialog.dismiss();
            }
        }
    }


}
