/** copyright(c) 2011 KobadroID **/
/** 改変可能・商用利用可能・許諾不要 **/
package com.example.listview;
import com.example.listview.SerifDirector.SerifInfo;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

/**
 * SerifDirectorを用いたビューの実装例
 */
public class SerifView extends View {

    public SerifDirector sd = new SerifDirector(this);
    boolean first = true;

	public SerifView(Context context) {
		super(context);
    }

	/* レイアウトxmlを使うためコンストラクタ定義しておく */
	public SerifView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		//初回のみ実行
		if (first == false) { return; }
		first = false;

		//セリフ設定
	    //親ビューのサイズが確定した後でないと、描画すべき大きさが
	    //計算できないため、onWindowFocusChangedでパースしている
		//onCreateだとまだビューの大きさが確定されていないため正常にパースできないので注意
	    //アクティビィが画面に表示された後ならどこでもよい
		
		sd.autoParse = false;	//自動パースをオフにすると、毎回毎回パースされないため
								//大きな文章を扱う場合はお勧め。最後に手動パースする。
	    sd.setTextSize(28);		//フォントサイズ
	    sd.playInterval = 50;	//１文字描画速度 ミリ秒
	    sd.setLineSpace(5);		//行間
	    sd.setSpacing(30, 30, 30, 30);	//上下左右マージン設定
	    
	    sd.setRubyEnable(true);	//ルビ有効
	    sd.setRubyColor(Color.CYAN);//ルビ色
	    sd.setRubyMargin(5);	//ルビと行の間のマージンを設定
	    sd.setRubyTextSize(16);	//ルビフォントサイズ。ルビ文字が被ルビ文字より長い場合、はみ出ます（デフォルト）。
	    						//setRubyStretch(true)をすると被ルビ文字の幅に収まるように縮小します。
	    						//その場合、ルビのフォントサイズは各文字によってバラバラになります。
	    //ルビ区切り文字設定
	    sd.setRubyPunctuation(getContext().getResources().getString(R.string.rubyPuctuation));
	    //漢字で文節を区切る。ルビ開始文字｜が省略されている場合に使う。
	    //sd.setSplitCJKUnifiedIdeographs(true);
	    
	    //ドロイドアイコンをカーソルに設定
	    sd.setCursorBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.item));
	    sd.cursorWidth = 70;
	    sd.cursorHeight = 70;
	    sd.cursorSpacing = 20;
	    
	    //禁則文字設定
	    sd.addHyphenationString(getContext().getResources().getString(R.string.hyphenationString));
	    
	    //縦書き
	    sd.setViewDirection(SerifDirector.VIEW_HORIZONTAL);
	    
	    //縦書きの場合、文字補正エフェクタで特定の文字を補正
	    //sd.addCharPositionEffector(getContext().getResources().getString(R.string.charEffects));

	    //文章の水平・垂直位置調整　（ 横書きのとき：垂直位置 縦書きのとき：水平位置
	    //※addCharToBitmapEffectorでビットマップを貼った場合に
	    //・ビットマップが文字より大きい場合＞文字をビットマップに対しどう揃えるか
	    //・ビットマップが文字り小さい場合＞ビットマップを文字に対しどう揃えるか
	    //を設定する。
	    int rowAlign = SerifInfo.ROW_ALIGN_CENTER; 
	    	
	    //文字エフェクタで"-"の位置に大きさを指定してビットマップを挿入
//	    sd.addCharToBitmapEffector("-",
//	    		BitmapFactory.decodeResource(getContext().getResources(), R.drawable.logo),
//	    		CharEffector.DRAW_BITMAP_NORMAL,
//	    		180, 80, rowAlign);
//	    //文字エフェクタで"三","国","志"の表示領域に文字と同じサイズでビットマップを挿入
//	    sd.addCharToBitmapEffector("三",
//	    		BitmapFactory.decodeResource(getContext().getResources(), R.drawable.san),
//	    		CharEffector.DRAW_BITMAP_FONT_REPLACE,
//	    		0, 0, rowAlign);
//	    sd.addCharToBitmapEffector("国",
//	    		BitmapFactory.decodeResource(getContext().getResources(), R.drawable.koku),
//	    		CharEffector.DRAW_BITMAP_FONT_REPLACE,
//	    		0, 0, rowAlign);
//	    sd.addCharToBitmapEffector("志",
//	    		BitmapFactory.decodeResource(getContext().getResources(), R.drawable.si),
//	    		CharEffector.DRAW_BITMAP_FONT_REPLACE,
//	    		0, 0, rowAlign);

	    //セリフ設定
	    sd.setSerif("");
	    
	    //パース
	    sd.parse();
	    
	    
	    //背景設定（パースした後に行う。再度パースすると背景設定は消えるので注意）
	    for (int i = 0; i < sd.getPageSize(); i++) {
	    	sd.getPageInfo(i).setBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.bsp), true);
	    }
	}

	@Override
	public void onDraw(Canvas c){
		//スーパークラスの描画を行わない場合は下記をコメントアウトする
		super.onDraw(c);	
		//セリフディレクタの描画を行う
		sd.drawSerif(c);
	}

}