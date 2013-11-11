/** copyright(c) 2011 KobadroID **/
/** 改変可能・商用利用可能・許諾不要 **/
package com.example.listview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;

/**
 * ゲームのセリフのように文字を１文字ずつ表示するクラス
 */
public class SerifDirector {

	/** 文字のアルファ値：第一段階 **/
	public static final int ALPHA_FIRST = 85;
	/** 文字のアルファ値：第二段階 **/
	public static final int ALPHA_SECOND = 170;
	/** 文字のアルファ値：最大 **/
	public static final int ALPHA_MAX = 255;

	/** 横書き **/
	public static final int VIEW_HORIZONTAL = 0;
	/** 縦書き **/
	public static final int VIEW_VERTICAL = 1;
	/** 改ページ **/
	public static final int ATTR_PAGE_FEED = 1;
	/** 一時停止 **/
	public static final int ATTR_PARAGRAPH_END = 2;
	/** 改行 **/
	public static final int ATTR_NEW_LINE = 4;
	/** 被ルビ文字 **/
	public static final int ATTR_HAS_RUBY = 8;
	/** ルビ文字 **/
	public static final int ATTR_RUBY = 16;
	/** CJK統合漢字 **/
	public static final int ATTR_CJK_UNIFIED_IDEOGRAPHS = 32;
	/** 行停止位置ビット **/
	public static final int ATTR_LINE_STOP_BITS = ATTR_PAGE_FEED | ATTR_PARAGRAPH_END | ATTR_NEW_LINE; 
	
	/** ルビ幅自動調整 **/
	public static final int RUBY_SPACE_AUTO = -1;
	
	/** 特殊文字指定 **/
	public static final String SPECIAL_CHAR = "$";
	/** 段落終了 **/
	public static final String PARAGRAPH_END = "g";
	/** 改ページ **/
	public static final String PAGE_FEED = "p";
	/** 改行コード **/
	public final String independNewLine;
	/** ルビストレッチ時の最小フォントサイズ **/
	public static final int MIN_FONT_SIZE = 2;

	/** エフェクトフラグ：横拡大 **/
	public static final int EFFECT_SCALE_X  = 1;
	/** エフェクトフラグ：縦拡大 **/
	public static final int EFFECT_SCALE_Y  = 2;
	/** エフェクトフラグ：縦横拡大 **/
	public static final int EFFECT_SCALE_XY = 4;
	/** エフェクトフラグ：横スクロール **/
	public static final int EFFECT_TRANS_X  = 8;
	/** エフェクトフラグ：縦スクロール **/
	public static final int EFFECT_TRANS_Y  = 16;
	/** エフェクトフラグ：縦横スクロール **/
	public static final int EFFECT_TRANS_XY = 32;
	/** エフェクトフラグ：横スキュー **/
	public static final int EFFECT_SKEW_X   = 64;
	/** エフェクトフラグ：縦スキュー **/
	public static final int EFFECT_SKEW_Y   = 128;
	/** エフェクトフラグ：縦横スキュー **/
	public static final int EFFECT_SKEW_XY  = 256;
	/** エフェクトフラグ：回転 **/
	public static final int EFFECT_ROTATE   = 512;

	/** 文字再生状態：停止中 **/
	public static final int SERIF_STOP			= 1;
	/** 文字再生状態：再生中 **/
	public static final int SERIF_PLAY			= 2;
	/** 文字再生状態：ページめくり待ち中 **/
	public static final int SERIF_WAIT_CONTINUE	= 3;

	/** 描画先となるビュー **/
	protected  View parent;

	/** セリフイベントリスナ **/
	protected  OnSerifListener onSerifListener = null;
	
	/** ページデータを1ページごとに管理 **/
	protected ArrayList<PageInfo> pages = new ArrayList<PageInfo>();

	/** 文字エフェクタを管理 **/
	protected HashMap<String, CharEffector> charEffectors = new HashMap<String, CharEffector>();

	/** フォントサイズ計算用 **/
	protected FontMetrics fontMetrics;

	/** 左余白 **/
	protected int spacingLeft = 40;
	/** 右余白 **/
	protected int spacingRight = 40;
	/** 上余白 **/
	protected int spacingTop = 40;
	/** 下余白 **/
	protected int spacingBottom = 40;

	/** 行間サイズ **/
	protected int lineSpace = 20;

	/** 繰り返し処理用 **/
	protected Handler handler = new Handler();
	protected Runnable runnable;
	protected Handler cursorHandler = new Handler();
	protected Runnable cursorRunnable;

	/** ステータス **/
	protected int status = SERIF_STOP;

	/** 表示文字列 **/
	protected  String serif = "";

	/** 文字描画用 **/
	protected  Paint serifPaint = new Paint();

	/** カーソル描画用 **/
	protected  Paint cursorPaint = new Paint();

	/** カーソルアルファ **/
	protected  int cursorAlpha = 0;

	/** カーソルアルファ状態 **/
	protected  int cursorDirection = 1;

	/** 現在何文字めまで描画したか **/
	protected int currentPos = 0;

	/** 全部で何文字か **/
	protected int totalLength = 0;

	/** 描画幅 あらかじめ計算しておく**/
	protected int drawWidth = 0;

	/** 描画高さ あらかじめ計算しておく**/
	protected int drawHeight = 0;

	/** 描画領域の中心点 あらかじめ計算しておく**/
	protected Point center = new Point();

	/** 現在のページのページインデックス ゼロオリジン**/
	protected int pageIndex = 0;

	/** 右端で折り返すかどうか（折り返さない場合1行あたり65535ピクセルあるものとして文字幅が計算される） **/
	protected boolean wordWrap = true;

	/** 禁則文字。行頭禁則のみ実装。追加可能。 **/
	protected HashSet<String> hyphenationString  = new HashSet<String>();

	/** 一文字表示する時間間隔、短いと早い（ミリ秒） **/
	public int playInterval = 50;

	/** ページめくり待ち状態でカーソル表示 **/
	public boolean viewCursor = true;

	/** カーソルの幅 **/
	public int cursorWidth = 30;
	
	/** カーソルの高さ **/
	public int cursorHeight = 30;

	/** 描画する領域 **/
	protected Rect drawRect = new Rect();

	/** 描画する際に、クリッピングするかどうか。しない場合、エフェクト時にはみ出すかも（回転とか **/
	public boolean drawOnClipping = true;

	/** エフェクト属性 **/
	public int effectFlag = 0;

	/** 文字を表示するときアルファエフェクトをかけるかどうか **/
	public boolean drawAlphaText = true;

	/** パースが必要な状態（文字サイズ変更や各種描画パラメータ変更など）になったときに自動でパースするかどうか ※一部例外あり **/
	public boolean autoParse = true;

	/** 書く方向 **/
	protected int viewDirection = VIEW_HORIZONTAL;
	
	/** カーソル用のビットマップ **/
	protected Bitmap cursorBitmap = null;
	
	/** カーソルビットマップの元のサイズ **/
	protected Rect cursorSrcRect = new Rect();
	
	/** カーソルを描画する領域 **/
	protected RectF cursorDrawRect = new RectF();

	/** カーソルの余白 **/
	public int cursorSpacing = 10;
	
	/** ルビ機能ON OFF **/
	protected boolean rubyEnable = false;
	
	/** ルビ開始文字と区切り文字  **/
	protected String[] rubyPunctuation = new String[3];
	
	/** 漢字の部分でセクションを分けるかどうか **/
	protected boolean splitCJKUnifiedIdeographs = false;
	
	/** ルビ用のPaint **/
	protected Paint rubyPaint = new Paint();
	
	/** ルビのフォントサイズ **/
	protected int rubyTextSize;
	
	/** ルビのフォント情報 **/
	protected FontMetrics rubyFontMetrics;
	
	/** ルビの描画幅 デフォルトは自動計算 **/
	protected int rubyLineSpace = RUBY_SPACE_AUTO;
	
	/** ルビと行の間のマージン **/
	protected int rubyMargin = 0;
	
	/** ルビのフォントサイズを被ルビ文字の描画領域に合わせて縮小するかどうか **/
	protected boolean rubyStretch = false;
	
	/**
	 * セリフディレクタを作成
	 * @param v ビュー
	 */
	public  SerifDirector(View v) {
		super();
		parent = v;
		serifPaint.setAntiAlias(true);
		serifPaint.setColor(Color.WHITE);
		rubyPaint.setAntiAlias(true);
		rubyPaint.setColor(Color.WHITE);
	    cursorPaint.setAntiAlias(true);
		updateFontMetrics();
		updateRubyFontMetrics();
		independNewLine = System.getProperty("line.separator");
		
		//ルビ区切り文字初期化
		for (int i = 0; i < rubyPunctuation.length; i++) {
			rubyPunctuation[i] = "";
		}
		rubyTextSize = (int)(rubyPaint.getTextSize());
	}
	
	/**
	 * 右端で折り返すかどうか。折り返さない場合1行あたり65535ピクセルあるものとして文字幅が計算される。
	 * @param state true 折り返す false 折り返さない
	 */
	public void setWordWrap(boolean state) {
		if (wordWrap != state) {
			wordWrap = state;
			if (autoParse) { parse(); }
		}
	}
	
	/**
	 * ルビ指定区切り文字を設定する。親文字開始指定文字・ルビ指定括弧FROM・TOの順で連続で指定すること。<br>
	 * 青空文庫だと｜《》で ｜遠《とほ》い のような感じになる。｜は無いこともある。setSplitCJKUnifiedIdeographsも参照。
	 * @param mark
	 */
	public void setRubyPunctuation(String mark) {
		for (int i = 0; i < rubyPunctuation.length; i++) {
			rubyPunctuation[i] = mark.substring(i, i+1);
		}
		if (autoParse) { parse(); }
	}
	
	/**
	 * ルビ機能の動作可否。ルビが不要の場合はOFFにしておいたほうがパースが早い。
	 * @param state true ON false OFF
	 */
	public void setRubyEnable(boolean state) {
		if (rubyEnable != state) {
			rubyEnable = state;
			if (autoParse) { parse(); }
		}
	}
	
	/**
	 * ルビを被ルビ文字の描画サイズに合わせてフィットさせるかどうか。
	 * @param state true ON false OFF
	 */
	public void setRubyStretch(boolean state) {
		if (rubyStretch != state) {
			rubyStretch = state;
			if (autoParse) { parse(); }
		}
	}
	
	/**
	 * 漢字と非漢字の境界でセクションを分けるかどうか。<br>
	 * ルビ開始文字が省略されている場合（青空文庫で多い）場合に、事前に<br>
	 * 漢字でセクションを分けておく事で、ルビ開始文字が省略され、ルビの区切り文字《》<br>
	 * が来た場合に直前の漢字に対してルビを割り振る事ができる。<br>
	 * それ以外の用途では単に遅くなるだけで有用ではない。<br>
	 * 注意：このオプションがONのとき、被ルビ文字が漢字と漢字以外が混在の場合は正常にルビが振れない。<br>
	 * 例）遠き天命《とおきてんめい》 ルビは”天命”の上に振られる。
	 * @param state true ON false OFF
	 */
	public void setSplitCJKUnifiedIdeographs(boolean state) {
		if (splitCJKUnifiedIdeographs != state) {
			splitCJKUnifiedIdeographs = state;
			if (autoParse) { parse(); }
		}
	}
	
	/**
	 * ルビ幅設定。RUBY_AUTO_SPACEを指定した場合は自動で設定される。autoParseがtrueの場合、再パースされる。
	 * @param space ルビ幅
	 */
	public void setRubySpace(int space) {
		rubyLineSpace = space;
		if (autoParse) { parse(); }
	}
	
	/**
	 * ルビと行の間のマージン。autoParseがtrueの場合、再パースされる。
	 * @param space マージン
	 */
	public void setRubyMargin(int space) {
		rubyMargin = space;
		if (autoParse) { parse(); }
	}
	
	/**
	 * 禁則文字を追加する。autoParseがtrueの場合、再パースされる。
	 * @param str 禁則文字。複数指定した場合、全て追加される
	 */
	public void addHyphenationString(String str){
		int sz = str.length();
		for (int i = 0; i < sz; i++) {
			hyphenationString.add(str.substring(i, i + 1));
		}
		if (autoParse) { parse(); }
	}
	
	/**
	 * 禁則文字をクリアする。autoParseがtrueの場合、再パースされる。
	 */
	public void clearHyphenationString(){
		hyphenationString.clear();
		if (autoParse) { parse(); }
	}
	
	/**
	 * 描画先のビューを取得する
	 * @return
	 */
	public View getParent(){ return parent; }
	
	/**
	 * イベントリスナをセットする
	 * @param l リスナインターフェイスの実装クラス
	 */
	public void setOnSerifListener(OnSerifListener l){
		onSerifListener = l;
	}

	/**
	 * 縦書き・横書きを設定する。autoParseがtrueの場合、再パースされる。
	 * @param state
	 */
	public void setViewDirection(int state) {
		if (viewDirection != state) {
			viewDirection = state;
			if (autoParse) { parse(); }
		}
	}
	
	/**
	 * カーソルにビットマップを設定する
	 * @param bmp 対象ビットマップ
	 */
	public void setCursorBitmap(Bitmap bmp){
		if (bmp == null) {
			cursorBitmap = null;
		} else {
			cursorSrcRect.set(0, 0, bmp.getWidth(), bmp.getHeight());
			cursorBitmap = bmp;
		}
	}

	/**
	 * 描画領域を設定する。自動パースされないので必要ならパースを行うこと。
	 * @param left 左座標
	 * @param top 上座標
	 * @param right 右座標
	 * @param bottom 下座標
	 */
	public void clipRect(int left, int top, int right, int bottom) {
		drawRect.set(left, top, right, bottom);
		drawWidth = right - left;
		drawHeight = bottom - top;
		center.x = left + (drawWidth / 2);
		center.y = top + (drawHeight / 2);
	}
	
	/**
	 * 描画領域矩形を取得する。
	 * @return 描画領域矩形
	 */
	public Rect getDrawRect(){ return drawRect; }
	
	/**
	 * 描画領域の幅を取得する。
	 * @return 幅
	 */
	public int getDrawWidth(){ return drawWidth; }
	
	/**
	 * 描画領域の高さを取得する
	 * @return 高さ
	 */
	public int getDrawHeight(){ return drawHeight; }

	/**
	 * 現在のページ番号を返す。 ゼロオリジン
	 * @return ページ番号
	 */
	public int getCurrentPageIndex(){ return pageIndex;}

	/**
	 * 現在のページ情報を返す。 
	 * @return ページ情報
	 */
	public PageInfo getCurrentPage(){ return pages.get(pageIndex);}
	
	/**
	 * 指定ページのページ情報を返す。
	 * @param index ゼロオリジン
	 * @return PageInfo
	 */
	public PageInfo getPageInfo(int index){ return pages.get(index); }

	/**
	 * トータルページ数を返す
	 * @return ページ数
	 */
	public int getPageSize(){ return pages.size(); }

	/**
	 * テキストフォントサイズ取得
	 * @return
	 */
	public  int getTextSize() {
		return (int)serifPaint.getTextSize();
	}

	/**
	 * テキストフォントサイズ設定 intのみ。autoParseがtrueの場合、再パースされる。
	 * @param size
	 */
	public  void setTextSize(int size) {
		serifPaint.setTextSize(size);
		updateFontMetrics();
		if (autoParse) { parse(); }
	}

	/**
	 * 文字飾り設定。autoParseがtrueの場合、再パースされる。
	 * @param face
	 */
	public  void setTypeface(Typeface face) {
	    serifPaint.setTypeface(face);
		updateFontMetrics();
		if (autoParse) { parse(); }
	}
	
	/**
	 * ルビのフォントサイズ設定 intのみ。autoParseがtrueの場合、再パースされる。
	 * @param size
	 */
	public  void setRubyTextSize(int size) {
		rubyTextSize = size;
		rubyPaint.setTextSize(rubyTextSize);
	    updateRubyFontMetrics();
		if (autoParse) { parse(); }
	}

	/**
	 * ルビ文字飾り設定。autoParseがtrueの場合、再パースされる。
	 * @param face
	 */
	public  void setRubyTypeface(Typeface face) {
	    rubyPaint.setTypeface(face);
	    updateRubyFontMetrics();
		if (autoParse) { parse(); }
	}
	
	/**
	 * ルビの文字色設定
	 * @param cl
	 */
	public  void setRubyColor(int cl) {
		rubyPaint.setColor(cl);
	}

	/**
	 * 文字色設定
	 * @param cl
	 */
	public  void setSerifColor(int cl) {
		serifPaint.setColor(cl);
	}

	/**
	 * 余白設定 autoParseがtrueの場合、再パースされる。
	 * @param size
	 */
	public  void setSpacing(int left, int top, int right, int bottom) {
		spacingLeft = left;
		spacingTop = top;
		spacingRight = right;
		spacingBottom = bottom;
		if (autoParse) { parse(); }
	}

	/**
	 * 行間設定 autoParseがtrueの場合、再パースされる。
	 * @param size
	 */
	public  void setLineSpace(int size) {
		if (lineSpace != size) {
			lineSpace = size;
			if (autoParse) { parse(); }
		}
	}

	/**
	 * ステータス取得
	 * @return
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * セリフのフォント情報を更新
	 */
	public  void updateFontMetrics() {
		fontMetrics = serifPaint.getFontMetrics();
	}

	/**
	 * ルビのフォント情報を更新
	 */
	public  void updateRubyFontMetrics() {
		rubyFontMetrics = rubyPaint.getFontMetrics();
	}
	
	/**
	 * テキスト設定 autoParseがtrueの場合、再パースされる。
	 * @param src
	 */
	public  void setSerif(String src) {
		if (serif.equals(src) == false) {
			serif = new String(src);
			if (autoParse) { parse(); }
		}
	}
	
	/**
	 * セリフの全文字数を返す
	 * @return 文字数
	 */
	public int getTotalLength(){
		return totalLength;
	}
	/**
	 * 文字列のサイズを再計算。文字を変更したりフォントサイズが変わるような処理をしたら再実行する。autoParseがtrueなら自動実行される。
	 * @return パースするのににかかった時間 ミリ秒
	 */
	public long parse() {
		if (serif.length() == 0) { return 0; }
		
		long starttime = SystemClock.uptimeMillis();

		//パース中は排他
		synchronized (pages) {
			
			//この時点で描画領域が初期状態ならば、親ビューの描画領域にデフォルト設定
			if (drawRect.left == 0 && drawRect.right == 0 &&
				drawRect.top == 0 && drawRect.bottom == 0){
				clipRect(0, 0, parent.getWidth(), parent.getHeight());
			}
			
			totalLength = 0;
			pages.clear();	//ページ情報初期化
			
			ArrayList<SerifInfo> serifs = new ArrayList<SerifInfo>(1000);

			int serifWidth;

			if (wordWrap) {
				if (viewDirection == VIEW_HORIZONTAL) {
					serifWidth  = (drawRect.right - drawRect.left) - (spacingLeft + spacingRight);
				} else {
					serifWidth  = (drawRect.bottom - drawRect.top) - (spacingTop + spacingBottom);
				}
			} else {
				serifWidth  = 65535;
			}
			
			//ルビ有効な場合、ルビの描画幅計算
			if (rubyEnable) {
				
				//現在のフォントサイズでフォント情報更新
				rubyPaint.setTextSize(rubyTextSize);	
				rubyFontMetrics = rubyPaint.getFontMetrics();
				
				//ルビ幅がRUBY_SPACE_AUTOの場合、自動設定
				if (rubyLineSpace == RUBY_SPACE_AUTO) {
					
					//ルビ文字の描画幅（横書き：高さ、縦書き：幅）を計算
					if (viewDirection == VIEW_HORIZONTAL) {
						//横書きの場合には、abs(ascent)+descent
						rubyLineSpace = (int)((rubyFontMetrics.ascent * -1) + rubyFontMetrics.descent);
					} else {
						//縦書きの場合、日本語の「あ」の文字で現在のフォントサイズの横幅とする
						rubyLineSpace = (int)(rubyPaint.measureText("\u3042"));
					}
				}

			} else {
				rubyLineSpace = 0;
				rubyMargin = 0;
			}
			
			//セリフをパースしセクションリストを得る
			SectionStream strm = new SectionStream(); 
			parseSection(strm);
			
			//セクションリストを行情報にアタッチする
			//どのセクションがどの行のどの位置に割り当てられるかを計算する。
			//行をまたぐセクションも発生する。
			totalLength = attachLine(serifWidth, strm, serifs);
			
			//各ページに入るサイズを計算しページ情報を集計する
			int sz = serifs.size();
			if (sz == 0) { return 0; }
			
			SerifInfo si;
			int fromIndex = 0;
			int toIndex = 0;
			int pageNo = 0;
			int start, limit, base, row;
			
			if (viewDirection == VIEW_HORIZONTAL) {
				start = drawRect.top + spacingTop;
				limit = drawRect.bottom - spacingBottom;
				base = drawRect.left + spacingLeft;
			} else {
				start = drawRect.right - spacingRight;
				limit = drawRect.left + spacingLeft;
				base = drawRect.top + spacingTop;
			}

			while (true) {
				
				//縦書き横書きそれぞれフォーマット
				if (viewDirection == VIEW_HORIZONTAL) {
					toIndex = formatHorizontalTopToBottom(
							serifs, 
							sz,
							fromIndex,
							start,
							limit,
							base, 
							rubyLineSpace,
							rubyMargin);
				} else {
					toIndex = formatVerticalRightToLeft(
							serifs, 
							sz,
							fromIndex,
							start,
							limit,
							base, 
							rubyLineSpace,
							rubyMargin);
				}
				
				//ページ情報設定
				PageInfo pi = new PageInfo();
				Paragraph para = null;
				boolean paracommit = true;
				
				row = fromIndex;
				while (row <= toIndex) {
					if (paracommit) {
						para = new Paragraph();
						paracommit = false;
					}
					si = serifs.get(row);
					
					para.addSerifInfo(si);
					
					if ((si.specialState & ATTR_PARAGRAPH_END) != 0) {
						pi.addParagraphInfo(para);
						paracommit = true;
					}
					
					row++;
				}
				
				if (paracommit == false) {
					pi.addParagraphInfo(para);
				}
				
				pi.parse(pageNo);

				pages.add(pi);
				pageNo++;

				fromIndex = toIndex + 1;

				if (fromIndex == sz) { break; }
			}

			//先頭ページと最終ページにマーク
			pages.get(0).isFirst = true;
			pages.get(pages.size() - 1).isLast = true;
			
			//ルビのフォーマット
			if (rubyEnable) {
				formatRuby(strm.sections);
			}
		}
		
		long endtime = SystemClock.uptimeMillis();
		
		return endtime - starttime;
	}
	
	/**
	 * 文字列を文章の改行、段落、改頁、ルビで区切ってSectionのストリームを得る。
	 * ・ルビは被ルビ文字の子セクションにする
	 * ・被ルビ文字の最後が改行、段落、改頁で終わっている場合同じセクションにする（セクションを分けない。
	 * @param result セクションストリーム
	 */
	protected void parseSection(SectionStream result) {
		int len = serif.length();
		int currentlen = 0;
		int pos = 0;
		StringBuilder current = new StringBuilder(1024);
		String one, nextone, lastone;
		Section sec = null;
		
		//CJK統合漢字で区切る場合に使う
        Pattern pattern = null;
        boolean currentIdeograph, lastIdeograph;
		if (splitCJKUnifiedIdeographs) {
	        pattern = Pattern.compile("\\p{InCJKUnifiedIdeographs}");
		}
        
		while (pos < len) {
			
			//現在位置の文字を取り出す
			one = serif.substring(pos, pos + 1);
			
			//被ルビ文字が来たら
			if (one.equals(rubyPunctuation[0])) {
				//セクションバッファに文字列があれば
				if (currentlen > 0) {
					//セクションバッファの文字列を別セクションとして追い出し新セクション開始
					sec = new Section(current.toString(), 0);
					result.sections.add(sec);
					
					current.setLength(0);
					currentlen = 0;
				}
				pos++;
				continue;
			}
			//ルビ開始文字が来たら
			if (one.equals(rubyPunctuation[1])) {
				//セクションバッファに文字列があれば
				if (currentlen > 0) { 
					//セクションバッファの文字列を別セクションとして追い出し新セクション開始
					sec = new Section(current.toString(), ATTR_HAS_RUBY);
					result.sections.add(sec);
					
					current.setLength(0);
					currentlen = 0;
					pos++;
					continue;
				}
			}
			//ルビ終了文字が来たら
			if (one.equals(rubyPunctuation[2])) {
				//セクションバッファに文字列があれば
				if (currentlen > 0) {
					//セクションバッファの文字列をルビのセクションとして
					//一個前のセクションに関連付ける
					
					//一個前のセクションがあれば
					if (sec != null) {
						
						//このセクションをルビ文字の子セクションとして追加開始
						sec.addChildSection(new Section(current.toString(), ATTR_RUBY));
						
						current.setLength(0);
						currentlen = 0;
						
						pos++;
						
						int last = pos;

						//ルビ文字の後に改行・改段落・改ページがきたら被ルビ文字と同じセクションとする
						if (last < len) {
							one = serif.substring(last, last + 1);
							if (one.equals(independNewLine)) {
								sec.attribute = sec.attribute | ATTR_NEW_LINE;
								pos++;
							}
						}
						if (last + 1 < len) {
							one = serif.substring(last, last + 1);
							nextone = serif.substring(last + 1, last + 2);
							if (one.equals(SPECIAL_CHAR) && nextone.equals(PAGE_FEED)) {
								sec.attribute = sec.attribute | ATTR_PAGE_FEED;
								pos += 2;
							} else
							if (one.equals(SPECIAL_CHAR) && nextone.equals(PARAGRAPH_END)) {
								sec.attribute = sec.attribute | ATTR_PARAGRAPH_END;
								pos += 2;
							}
						}
						
						continue;
					}
				}
			}
			
			//現在位置の「文字」が（文字列ではない）改行かどうか判断
			if (one.equals(independNewLine)){
				//セクションバッファの文字列を別セクションとして追い出し新セクション開始

				sec = new Section(current.toString(), ATTR_NEW_LINE);
				result.sections.add(sec);
				
				current.setLength(0);
				currentlen = 0;
				pos++;
				continue;
			}

			//現在位置の文字が特殊文字かどうか判断
			if (one.equals(SPECIAL_CHAR)){

				//現在位置の次の文字が存在するなら、特殊文字チェックを行う
				if (pos + 1 < len) {
					nextone = serif.substring(pos + 1, pos+ 2);

					//改ページ判断
					if (nextone.equals(PAGE_FEED)) {
						//セクションバッファの文字列を別セクションとして追い出し新セクション開始

						sec = new Section(current.toString(), ATTR_PAGE_FEED);
						result.sections.add(sec);
						
						current.setLength(0);
						currentlen = 0;
						pos += 2;
						continue;
						
					} else

					//段落終了判断
					if (nextone.equals(PARAGRAPH_END)) {
						//セクションバッファの文字列を別セクションとして追い出し新セクション開始
						
						sec = new Section(current.toString(), ATTR_PARAGRAPH_END);
						result.sections.add(sec);
						
						current.setLength(0);
						currentlen = 0;
						pos += 2;
						continue;
					}
				}
			} 
			
			//セクションバッファに文字列があり、漢字セクション区切りが有効であれば
			if (splitCJKUnifiedIdeographs && currentlen > 0){

				lastone = current.substring(currentlen - 1, currentlen);

				//現在位置の文字が漢字かどうか
		        currentIdeograph = pattern.matcher(one).find();
		        
				//直前の文字が漢字かどうか
		        lastIdeograph = pattern.matcher(lastone).find();
		        
				//現在位置の文字と直前の文字が漢字かどうかが異なるならセクションを区切る
		        //例）あいう漢字え の並びならば 漢字 の前後でセクションを切る 
				if (currentIdeograph != lastIdeograph) {
					
					if (lastIdeograph) {
						sec = new Section(current.toString(), 
								ATTR_CJK_UNIFIED_IDEOGRAPHS);
					} else {
						sec = new Section(current.toString(), 0);
					}
					result.sections.add(sec);
					
					current.setLength(0);
					currentlen = 0;
				}
			}
				
			current.append(one);
			currentlen++;
			
			pos++;
		}
		
		if (currentlen > 0) {
			sec = new Section(current.toString(), 0);
			result.sections.add(sec);
		}			
	}
	
	/**
	 * リスト化されたセクションをストリームとして直列に扱うクラス。パース処理を簡略化するため使う。
	 * リスト構造を意識させず文字を先頭から一文字ずつ取り出す機能と
	 * 取り出し開始位置をマーキングしておき行情報に後で反映する機能を持つ。
	 */
	class SectionStream {
		/** 空文字比較用 **/
		public static final String BLANK_STR = "";
		/** パースされたセクションリスト **/
		public ArrayList<Section> sections = new ArrayList<Section>(10000);
		/** セクション内ポジション **/
		public int positionOfSection;
		/** ストリーム全体でのポジション **/
		public int positionOfAll;
		/** 現在のセクションインデックス **/
		public int currentSectionIndex;
		/** セクションリストのサイズ **/
		public int sectionSize;
		/** マーキング開始位置 セクションインデックス **/
		public int startSectionIndex;
		/** マーキング終了位置 セクションインデックス **/
		public int endSectionIndex;
		/** マーキング開始位置 セクション内インデックス **/
		public int startPositionOfSection;
		/** マーキング終了位置 セクション内インデックス **/
		public int endPositionOfSection;
		/** 現在処理中のセクション **/
		public Section currentSection;
		/** 直前に処理中だったセクション **/
		public Section prevSection;
		
		/** コンストラクタ **/
		public SectionStream(){ super(); }
		
		/**
		 * ストリームの位置を初期化する。各ポジションは先頭にセットされ初期位置をマーキングする。
		 */
		public void reset() {
			//各ポジションを先頭に設定
			positionOfAll = 0;
			positionOfSection = 0;
			currentSectionIndex = 0;
			prevSection = null;
			sectionSize = sections.size();
			if (sections.size() == 0) {
				currentSection = null;
			} else {
				currentSection = sections.get(0);
			}
			startMark();
		}
		
		/**
		 * ストリームから１文字取り出し、ポジションを一文字前に進める。ポジションは次回の読み出し位置に設定される。
		 * @return null もう文字がない 非null　取り出した文字。空セクションの場合、空文字。
		 */
		public String getChar() {
			String result;
			
			if (currentSection == null) { return null; }
			
			if (currentSection.wordLength > 0) {
				result = currentSection.word.substring(positionOfSection, positionOfSection + 1);
			} else {
				//空セクションの場合ブランクを返す。
				result = BLANK_STR;
			}
			
			positionOfSection++;
			positionOfAll++;

			if (positionOfSection >= currentSection.wordLength) {
				
				if (currentSectionIndex  + 1 >= sectionSize) {
					currentSection = null;
				} else {
					prevSection = currentSection;
					currentSectionIndex++;
					currentSection = sections.get(currentSectionIndex);
					positionOfSection = 0;
				}
			}
			
			return result;
		}
		
		/**
		 * ストリームから次の１文字を取り出すが、ポジションを進めない。
		 * @return null もう文字がない 非null　取り出した文字。空セクションの場合、空文字。
		 */
		public String getCharNoFoward() {
			String result = null;
			
			if (currentSection == null) { return null; }
			
			if (currentSection.wordLength > 0) {
				if (currentSection.wordLength > positionOfSection){
					result = currentSection.word.substring(positionOfSection, positionOfSection + 1);
				}
			} else {
				//空セクションの場合ブランクを返す。
				result = BLANK_STR;
			}
			
			return result;
		}
		
		
		/**
		 * マーキング開始・終了位置を現在のポジションに設定する。
		 */
		public void startMark() {
			startSectionIndex = currentSectionIndex;
			startPositionOfSection = positionOfSection;  
			endSectionIndex = currentSectionIndex;
			endPositionOfSection = positionOfSection;
		}
		
		/**
		 * マーキング終了位置を現在のポジションの一つ前に設定する（getChar後に実行されることを前提とする）。開始位置は何もしない。
		 */
		public void commitChar() {
			if (positionOfSection == 0) {
				if (currentSectionIndex > 0) {
					endSectionIndex = currentSectionIndex - 1;
					endPositionOfSection = sections.get(endSectionIndex).wordLength - 1;
				} else {
					endSectionIndex = 0;
					endPositionOfSection = 0;
				}
			} else {
				endSectionIndex = currentSectionIndex;
				endPositionOfSection = positionOfSection - 1;
			}
		}
		
		/**
		 * マーキング位置に従って行情報にリンクすべきセクション内インデックスを設定する
		 */
		public void setSectionLink(SerifInfo si) {
			Section sec;
			
			//開始・終了セクションが同一の場合
			if (startSectionIndex == endSectionIndex) {
				si.addSectionLink(sections.get(startSectionIndex), startPositionOfSection, endPositionOfSection);
				
			} else {
				//開始・終了セクションが異なる場合
				for (int i =  startSectionIndex; i <= endSectionIndex; i++) {
					sec = sections.get(i);
					if ( i == startSectionIndex) {
						si.addSectionLink(sec, startPositionOfSection, sec.wordLength - 1);
					}  else
					if ( i == endSectionIndex) {
						si.addSectionLink(sec, 0, endPositionOfSection);
					} else {
						si.addSectionLink(sec, 0, sec.wordLength - 1);
					}
				}
			}
		}
		
		/**
		 * ストリームを１文字前に戻す。
		 */
		public void back(){
			if (positionOfSection > 0) {
				positionOfSection--;
				positionOfAll--;
				
			} else {
				
				if (currentSectionIndex > 0) {
					currentSectionIndex--;
					positionOfAll--;
					currentSection = sections.get(currentSectionIndex);
					if (currentSection.wordLength > 0) {
						positionOfSection = currentSection.wordLength - 1;
					} else {
						positionOfSection = 0;
					}
				}
			}
		}
	}
	
	/**
	 * セクションリストを行情報（serifinfo）にアタッチする
	 * @param linePixel 行の幅
	 * @param strm セクションストリーム
	 * @param serifs アタッチされた行リスト
	 * @return トータル文字数
	 */
	protected int attachLine(int linePixel, SectionStream strm, ArrayList<SerifInfo> serifs ) {
		final int MAX_LEN = 2048;
		StringBuilder line = new StringBuilder(MAX_LEN);
		int asc[] = new int[MAX_LEN];
		int desc[] = new int[MAX_LEN];
		int wid[] = new int[MAX_LEN];
		
		String one, nextone;
		SerifInfo si = null;
		boolean overflag;
		boolean sicommit = true;
		int total = 0;
		int totalMeasure = 0;
		int lineindex = 0;
		int measure;
		
		//ストリームをリセットし最初の一文字を取得する
		strm.reset();
		one = strm.getChar();
		
		//ストリームから文字が取得できる限りループする
		while (one != null){
			
			if (sicommit) {
				si = new SerifInfo(viewDirection);
				serifs.add(si);
				line.setLength(0);
				sicommit = false;
				totalMeasure = 0;
				lineindex = 0;
			}
			
			//空セクションの場合、行情報確定
			if (one.equals(SectionStream.BLANK_STR)) {
				
				line.append(" ");
				
				getMeasureText(si, one, serifPaint, 
						viewDirection, fontMetrics, charEffectors, 
						asc, desc, wid, lineindex);
				
				strm.commitChar();
				
				//行情報確定
				si.specialState = si.specialState | strm.currentSection.attribute;
				si.setMeasureParams(line.toString(), asc, desc, wid,  
						lineSpace, rubyLineSpace, rubyMargin);
				strm.setSectionLink(si);
				total += si.lineLength;
				sicommit = true;
				
				//次の読み取り位置からマーキング開始
				strm.startMark();	
				
			} else {
				
				line.append(one);
				
				//先頭から現在位置までの文字列が占有するピクセルサイズを取得する
				totalMeasure += getMeasureText(si, one, serifPaint, 
						viewDirection, fontMetrics, charEffectors, 
						asc, desc, wid, lineindex);
				
				overflag = totalMeasure > linePixel;
				
				//現在位置までの幅が１行に入りきるか
				if (overflag == false) {
					
					//現在位置がセクション境界で改行・改ぺーじ・改段落があるならば禁則処理不要
					if (strm.positionOfSection == 0 && 
						strm.prevSection != null &&
						(strm.prevSection.attribute & ATTR_LINE_STOP_BITS) != 0 ) {
						//nop
					} else {
					//それ以外は禁則処理実行
						
						//禁則処理のために1つ次の文字を取得
						nextone = strm.getCharNoFoward();
						
						//次の文字が禁則文字で、次の文字まで行内に入りきらないならば禁則処理判断
						if (hyphenationString.contains(nextone)) {
							
							//次の文字まで含めて行サイズチェック
							measure = getMeasureText(si, nextone, serifPaint, 
									viewDirection, fontMetrics, charEffectors, 
									asc, desc, wid, lineindex + 1);
							overflag = (totalMeasure + measure) > linePixel;
						}
					}
				}
				
				if (overflag) {
					//入りきらない、または禁則にひっかかったら現在位置の一個前で切る
					line.setLength(line.length() - 1);
					
					si.setMeasureParams(line.toString(), asc, desc, wid,  
							lineSpace, rubyLineSpace, rubyMargin);
					strm.setSectionLink(si);
					total += si.lineLength;
					sicommit = true;
					
					strm.back();		//ストリーム一個戻して
					strm.startMark();	//そこからマーキング再開
					
				} else {
					//入りきるならばストリームの現在位置までをコミット					
					strm.commitChar();
					
					//現在位置がセクション境界で改行・改ぺーじ・改段落があるならばここで行終わり
					if (strm.positionOfSection == 0 && 
						strm.prevSection != null &&
						(strm.prevSection.attribute & ATTR_LINE_STOP_BITS) != 0 ) {
						
						//セクション末尾はセクション属性を行にコピー
						si.specialState = si.specialState | strm.prevSection.attribute;
						
						//行情報確定
						si.setMeasureParams(line.toString(), asc, desc, wid,  
								lineSpace, rubyLineSpace, rubyMargin);
						strm.setSectionLink(si);
						total += si.lineLength;
						sicommit = true;
						
						//次の読み取り位置からマーキング開始
						strm.startMark();	
					}
				}
			}
				
			//次の文字取得
			one = strm.getChar();
			lineindex++;
		}
					
		//ループを抜けてまだ未確定の行情報があれば確定する
		if (sicommit == false && si != null ) { 
			si.specialState = si.specialState | strm.sections.get(strm.sections.size() - 1).attribute;
			si.setMeasureParams(line.toString(), asc, desc, wid,  
					lineSpace, rubyLineSpace, rubyMargin);
			strm.setSectionLink(si);
			total += si.lineLength;
		}
		
		return total;
	}
	
	/**
	 * 該当文字の幅または高さを取得する。該当文字が文字エフェクタの対象ならばエフェクト後のサイズで返す。<br>
	 * ビットマップエフェクタがある場合、相対位置を行情報に設定する。同時に各配列にも値を設定する。
	 * @param si IN OUT 行情報
	 * @param one IN 文字
	 * @param p IN 評価するPaint
	 * @param direction IN 向き
	 * @param fm IN フォント情報
	 * @param charEffectors IN 文字エフェクタ
	 * @param asc OUT 文字ascent
	 * @param desc OUT 文字descent
	 * @param wid OUT 文字の幅
	 * @param index IN 処理すべき配列のインデックス
	 * @return 幅または高さ
	 */
	protected static final int getMeasureText(SerifInfo si, String one, 
			Paint p, int direction,
			FontMetrics fm, HashMap<String, CharEffector> charEffectors,
			int asc[], int desc[], int wid[], int index) {
		CharEffector ce;
		int effectFlag, bitmapStyle;
		
		if (direction == SerifDirector.VIEW_HORIZONTAL) {

			effectFlag = -1;
			bitmapStyle = -1;
			ce = charEffectors.get(one);
			
			if (ce != null) {
				effectFlag = ce.effectFlag;
				bitmapStyle = ce.bitmapStyle;
			}

			if (effectFlag == CharEffector.EFFECT_BITMAP  && 
				bitmapStyle == CharEffector.DRAW_BITMAP_NORMAL) {
				//ビットマップ文字エフェクタでサイズ指定表示がある場合はそのサイズをとる
				//ascentはbaselineから相対値で負の値が入る。下記を仕様とする。
				//文字の描画位置＝baseline
				//ビットマップの描画位置＝baseline + ascent
				//したがって、ascentは後でビットマップ描画時に用いるため保存しておく。
				asc[index] = (int)fm.ascent;
				desc[index] = ce.drawBitmapHeight + (int)fm.ascent;
				wid[index] = ce.drawBitmapWidth;
				si.rowAlign = ce.rowAlign;
				return ce.drawBitmapWidth;
				
			} else {
	
				asc[index] = (int)fm.ascent;
				desc[index] = (int)fm.descent;
				wid[index] = (int)p.measureText(one);
				
				return wid[index]; 
			}

		} else {
			effectFlag = -1;
			bitmapStyle = -1;
			ce = charEffectors.get(one);

			if (ce != null) {
				effectFlag = ce.effectFlag;
				bitmapStyle = ce.bitmapStyle;
			}
			
			if (effectFlag == CharEffector.EFFECT_BITMAP  && 
				bitmapStyle == CharEffector.DRAW_BITMAP_NORMAL) {

				asc[index] = (int)fm.ascent;
				desc[index] = ce.drawBitmapHeight + (int)fm.ascent;
				wid[index] = ce.drawBitmapWidth;
				si.rowAlign = ce.rowAlign;
				return ce.drawBitmapHeight;
				
			} else {

				asc[index] = (int)fm.ascent;
				desc[index] = (int)fm.descent;
				wid[index] = (int)p.measureText(one);
				return (int)((fm.ascent * -1) + fm.descent);
			}
		}
	}

	/**
	 * 縦書きで１ページに入りきる行範囲を計算し、描画座標を確定する
	 * @param serifs セリフのリスト
	 * @param fromIndex 計算を開始する行番号 ゼロオリジン
	 * @param start 描画開始座標
	 * @param limit 描画終了座標
	 * @param top top座標
	 * @param rspace ルビの描画幅
	 * @param rmargin ルビと行のマージン
	 * @return そのページに入りきる行番号 ゼロオリジン
	 */
	protected static int formatVerticalRightToLeft(ArrayList<SerifInfo> serifs, 
			int serifsize, int fromIndex, int start, int limit, 
			int top, int rspace, int rmargin) {
		int drawX, drawY;
		SerifInfo si;

		for (int i = fromIndex; i < serifsize; i++) {

			si = serifs.get(i);

			//描画位置計算
			drawX = start - si.lineWidth;

			//カレント行が入りきらない場合、一個前の行番号を返す
			if (drawX < limit && i > 0) {
				return i - 1;
			}
			
			drawY = top;
			int len = si.lineLength;
			
			for (int x = 0; x < len; x++) {

				//rowAlignから、その文字のX座標を設定
				switch (si.rowAlign) {
				case SerifInfo.ROW_ALIGN_CENTER:	//センタリング
					si.drawX[x] = drawX + ((si.lineWidth - si.width[x]) / 2) - rspace - rmargin;
					break;
				case SerifInfo.ROW_ALIGN_LOWER:	//左寄せ
					si.drawX[x] = drawX - rspace - rmargin;
					break;
				case SerifInfo.ROW_ALIGN_UPPER:	//右寄せ
				default:
					si.drawX[x] = drawX + si.lineWidth - si.width[x] - rspace - rmargin;
				}
				
				si.drawY[x] = drawY;
				drawY += si.height[x];
			}

			//改ページをする行の場合、この行の行番号を返す
			if ((si.specialState & ATTR_PAGE_FEED) != 0) {
				return i;
			}

			start -= si.lineWidth;
		}
		return serifsize - 1;
	}

	/**
	 * 横書きで１ページに入りきる行範囲を計算し、描画座標を確定する
	 * @param serifs セリフのリスト
	 * @param fromIndex 計算を開始する行番号 ゼロオリジン
	 * @param start 描画開始座標
	 * @param limit 描画終了座標
	 * @param top top座標
	 * @param rspace ルビの描画幅
	 * @param rmargin ルビと行のマージン
	 * @return そのページに入りきる行番号 ゼロオリジン
	 */
	protected static int formatHorizontalTopToBottom(ArrayList<SerifInfo> serifs, 
			int serifsize, int fromIndex, int start, int limit, 
			int left, int rspace, int rmargin) {
		int drawX, drawY;
		SerifInfo si;

		for (int i = fromIndex; i < serifsize; i++) {

			si = serifs.get(i);

			//描画位置計算
			drawY = start;

			//カレント行が入りきらない場合、一個前の行番号を返す
			if (drawY + si.lineHeight > limit && i > 0) {
				return i - 1;
			}
			
			drawX = left;
			int len = si.lineLength;
			
			for (int x = 0; x < len; x++) {
				
				//rowAlignから、その文字のY座標を設定
				switch (si.rowAlign) {
				case SerifInfo.ROW_ALIGN_CENTER:	//センタリング
					si.drawY[x] = drawY + ((si.lineHeight - si.height[x]) / 2) + rspace + rmargin;
					break;
				case SerifInfo.ROW_ALIGN_LOWER:	//下寄せ
					si.drawY[x] = drawY + si.lineHeight - si.height[x] + rspace + rmargin;
					break;
				case SerifInfo.ROW_ALIGN_UPPER:	//上寄せ
				default:
					si.drawY[x] = drawY + rspace + rmargin;
				}
				
				si.drawX[x] = drawX;
				drawX += si.width[x];
			}
			
			//改ページをする行の場合、この行の行番号を返す
			if ((si.specialState & ATTR_PAGE_FEED) != 0) {
				return i;
			}

			start += si.lineHeight;
		}
		return serifsize - 1;
	}
	
	/**
	 * ルビをフォーマットする
	 * @param sections セクションのリスト
	 */
	protected void formatRuby(ArrayList<Section> sections) {
		ArrayList<SectionDrawInfo> sdis = new ArrayList<SectionDrawInfo>();
		ArrayList<Integer> intlist = new ArrayList<Integer>();
		Section sec;
		int sz = sections.size();

		for (int k = 0; k < sz; k++) {
			sec = sections.get(k);
			
			//もしルビ属性を持つセクションならば
			if ((sec.attribute & SerifDirector.ATTR_HAS_RUBY) != 0) {
				
				RubyInfo ri;
				FontMetrics tmp;
				
				//子セクションの一番目からルビを取り出す
				String rubyFullStr = sec.childSections.get(0).word;
				
				//セクションが割り当てられた行それぞれの描画領域サイズとトータルサイズを得る
				int sectionttl = sec.getSectionDrawInfo(sdis);
				
				//割り当てられた行それぞれに、描画領域の大きさの比率に応じてルビ文字を分割して割り振る
				splitStringBySizeRatio(rubyFullStr, viewDirection, sectionttl, sdis);
				
				int ssz = sdis.size();
				SectionDrawInfo ssr;
				
				for (int i = 0; i < ssz; i++) {
					ssr = sdis.get(i);
					
					if (ssr.str != null) {
						//ルビ文字保管インスタンスを準備
						if (ssr.serifInfo.rubyInfos == null) {
							ssr.serifInfo.rubyInfos = new ArrayList<RubyInfo>();
						}
						
						//ルビ文字インスタンス生成
						ri = new RubyInfo(viewDirection);
						
						if (viewDirection == VIEW_HORIZONTAL) {
							//ルビ文字フォントサイズ計算
							tmp = ri.calcFontSize(
									ssr.str, 
									rubyStretch, 
									ssr.width, 
									rubyLineSpace, 
									rubyTextSize, 
									rubyPaint, 
									rubyFontMetrics);
							//ルビ文字の描画座標計算
							ri.calcLocation(
									ssr.drawOriginX, 
									ssr.drawOriginY - ssr.height - rubyMargin, 
									rubyPaint, 
									tmp);
						} else {
							tmp = ri.calcFontSize(
									ssr.str, 
									rubyStretch, 
									rubyLineSpace, 
									ssr.height, 
									rubyTextSize, 
									rubyPaint, 
									rubyFontMetrics);
							ri.calcLocation(
									ssr.drawOriginX + ssr.width + rubyMargin, 
									ssr.drawOriginYAdjustAscent, 
									rubyPaint, 
									tmp);
						}
						
						//ルビ文字と被ルビ文字の描画タイミングのマッピング
						ri.setRubyTiming(ssr, intlist);
						
						//ルビ文字を行情報に関連付ける
						ssr.serifInfo.rubyInfos.add(ri);
					}
				}
			}
		}
	}

	/**
	 * 再生開始
	 */
    public void play() {
    	if (pages.size() == 0) { return; }
    	
    	pageIndex = 0;
    	PageInfo pi = getCurrentPage();
    	pi.setActiveParagraphIndex(0);

    	start();
    	if (onSerifListener != null) {
    		onSerifListener.onSerifStart(parent, this);
       		onSerifListener.onSerifPageChange(parent, this, pi, pi);
    	}
    }

    /**
     * 繰り返し再描画処理開始
     */
    protected void start() {
    	if (pages.size() == 0) { return; }
    	
    	stopBlinkCursor(); //カーソル点滅停止
    	currentPos = 0;	//描画文字位置初期化
    	status = SERIF_PLAY; //ステータス更新

        handler.removeCallbacks(runnable);
        
        runnable = new Runnable() {
            public void run() {
            	
                update();

                handler.postDelayed(this, playInterval);
            }
        };

        handler.postDelayed(runnable, playInterval);
    }

    /**
     * 文字を一個すすめて再描画
     */
    protected void update() {
    	if (status != SERIF_PLAY) { return; }	//念のため
    	currentPos++;
    	
    	PageInfo pi = getCurrentPage();
    	Paragraph para = pi.getActiveParagraph();
    	
    	int maxpos = para.getTotalLength();
    	if (currentPos > maxpos) { currentPos = maxpos; }

    	parent.invalidate();
    	
    	if (onSerifListener != null) {
    		onSerifListener.onSerifUpdate(parent, this, currentPos, pi, para);
    	}
    }
    
    /**
     * 現在のページを最後までいっきに表示
     * @param forceSkip true ページの最後までスキップ false アクティブな段落の最後までスキップ
     */
    public void skip(boolean forceSkip) {
    	if (pages.size() == 0) { return; }
    	if (status != SERIF_PLAY) { return; }

    	PageInfo pi = getCurrentPage();
    	if (forceSkip) {
    		pi.setActiveParagraphIndex(pi.getParagraphCount() - 1);
    	}
    	Paragraph para = pi.getActiveParagraph();
    	currentPos = pi.getActiveParagraph().getTotalLength();
    	
    	update();
    	
    	if (onSerifListener != null) {
    		onSerifListener.onSerifSkip(parent, this, pi, para);
    	}
    }

    /**
     * 次の段落を表示
     */
    public void nextParagraph() {
    	if (pages.size() == 0) { return; }
    	
    	PageInfo pi = getCurrentPage();
    	Paragraph oldpara = pi.getActiveParagraph();
    	
    	//もしアクティブなら段落がそのページの最終段落ならば
    	if (pi.isLastParagraph()) {
    		//最終ページならば何もしない
        	if (pi.isLast) { return; }
    		//次のページへ
    		nextPage();
    		return;
    	}
    	
    	pi.nextParagraph();
    	Paragraph newpara = pi.getActiveParagraph();

    	start();
    	
    	if (onSerifListener != null) {
    		onSerifListener.onSerifNextParagraph(parent, this, pi, newpara, oldpara);
    	}
    }
    
    /**
     * 次のページを表示
     */
    public void nextPage() {
    	if (pages.size() == 0) { return; }
    	
    	PageInfo oldpi = getCurrentPage();
    	if (oldpi.isLast) { return; }

    	pageIndex++;
    	PageInfo newpi = getCurrentPage(); 
    	newpi.setActiveParagraphIndex(0);

    	start();
    	
    	if (onSerifListener != null) {
    		onSerifListener.onSerifNextPage(parent, this, newpi, oldpi);
       		onSerifListener.onSerifPageChange(parent, this, newpi, oldpi);
    	}
    }

    /**
     * 前のページを表示
     */
    public void previous() {
    	if (pages.size() == 0) { return; }

    	PageInfo oldpi = getCurrentPage();
    	if (oldpi.isFirst) { return; }

    	pageIndex--;
    	PageInfo newpi = getCurrentPage(); 
    	newpi.setActiveParagraphIndex(0);

    	start();
    	if (onSerifListener != null) {
    		onSerifListener.onSerifPrevious(parent, this, newpi, oldpi);
       		onSerifListener.onSerifPageChange(parent, this, newpi, oldpi);
    	}
    }
    
    /**
     * 指定ページに移動
     * @param pageIdx ページインデックス ゼロオリジン
     */
    public void gotoPage(int pageIdx) {
    	if (pages.size() == 0) { return; }
    	if (pageIndex < 0 || pageIndex >= pages.size()) { return; }
    	
    	PageInfo oldpi = getCurrentPage();

    	pageIndex = pageIdx;
    	PageInfo newpi = getCurrentPage(); 
    	newpi.setActiveParagraphIndex(0);

    	start();
    	
    	if (onSerifListener != null) {
    		onSerifListener.onSerifPageChange(parent, this, newpi, oldpi);
    	}
    }

    /**
     * 停止
     */
    public void stop() {
    	if (pages.size() == 0) { return; }
    	if (status == SERIF_STOP) { return; }

    	PageInfo pi = getCurrentPage();
    	Paragraph para = pi.getActiveParagraph();
    	
    	status = SERIF_STOP;
        handler.removeCallbacks(runnable);
               
    	stopBlinkCursor();
    	parent.invalidate();

    	if (onSerifListener != null) {
    		onSerifListener.onSerifStop(parent, this, pi, para);
    	}
    }

    /**
     * 一時停止状態にする
     */
    protected void waitContinue() {
    	if (pages.size() == 0) { return; }
    	if (status == SERIF_WAIT_CONTINUE || status == SERIF_STOP ) { return; }

    	startBlinkCursor();
    	parent.invalidate();

    	status = SERIF_WAIT_CONTINUE;
        handler.removeCallbacks(runnable);

    	PageInfo pi = getCurrentPage();
    	Paragraph para = pi.getActiveParagraph();
    	
    	if (onSerifListener != null) {
    		onSerifListener.onSerifWait(parent, this, pi, para);
    	}
    }

    /**
     * キャンバスエフェクト
     * @param canvas
     * @param rate 係数 ～1f
     */
    protected void canvasEffect(Canvas canvas, float progress){
    	if (onSerifListener != null) {
        	PageInfo pi = getCurrentPage();
        	Paragraph para = pi.getActiveParagraph();
    		
    		onSerifListener.onSerifCanvasEffect(
    				parent, 
    				this, 
    				pi, 
    				para, 
    				canvas, 
    				progress);
    	}
    	
    	if (effectFlag != 0) {
	    	if ((effectFlag & EFFECT_SCALE_X) != 0) {
	    		canvas.scale(progress, 1, center.x, center.y);
	    	}
	    	if ((effectFlag & EFFECT_SCALE_Y) != 0) {
	    		canvas.scale(1, progress, center.x, center.y);
	    	}
	    	if ((effectFlag & EFFECT_SCALE_XY) != 0) {
	    		canvas.scale(progress, progress, center.x, center.y);
	    	}
	    	if ((effectFlag & EFFECT_TRANS_X) != 0) {
	    		canvas.translate((float)drawWidth * (1 - progress), 1);
	    	}
	    	if ((effectFlag & EFFECT_TRANS_Y) != 0) {
	    		canvas.translate(1, (float)drawHeight * (1 - progress));
	    	}
	    	if ((effectFlag & EFFECT_TRANS_XY) != 0) {
	    		canvas.translate((float)drawWidth * (1 - progress), (float)drawHeight * (1 - progress));
	    	}
	    	if ((effectFlag & EFFECT_SKEW_X) != 0) {
	    		canvas.skew(1 - progress, 1);
	    	}
	    	if ((effectFlag & EFFECT_SKEW_Y) != 0) {
	    		canvas.skew(1, 1 - progress);
	    	}
	    	if ((effectFlag & EFFECT_SKEW_XY) != 0) {
	    		canvas.skew(1 - progress, 1 - progress);
	    	}
	    	if ((effectFlag & EFFECT_ROTATE) != 0) {
	    		canvas.rotate(360f * progress, center.x, center.y);
	    	}
    	}
    }

    /**
     * 描画処理
     * @param canvas 対象キャンバス
     */
	public void drawSerif(Canvas canvas) {
		if (pages.size() == 0) { return; }

		//パース中は描画しない
		synchronized (pages) {

			PageInfo pi = pages.get(pageIndex);
			Paragraph para = pi.getActiveParagraph();
			int activeIndex = pi.getActiveParagraphIndex();

			//背景描画タイミング１（エフェクト前）
			pi.draw(canvas, false, drawRect);
			
			try{
				canvas.save();
	
				//クリッピング。やらないとはみ出すかも
				if (drawOnClipping) { canvas.clipRect(drawRect); }
	
		    	//エフェクト
		    	canvasEffect(canvas, (float)currentPos / (float) para.getTotalLength());
	
				//背景描画タイミング２（エフェクト後）
				pi.draw(canvas, true, drawRect);
	
				//文字描画タイミング１（エフェクト後に、現在アクティブな段落を描画）
				draw(canvas, activeIndex, activeIndex);

			} finally {
				canvas.restore();
			}
			
			//文字描画タイミング２（エフェクト後に、既に表示済みの段落を描画）
			draw(canvas, 0, activeIndex - 1);

			//カーソル描画
	    	if (status == SERIF_WAIT_CONTINUE) { drawCursor(canvas); }
		}
	}
	
	/**
	 * 段落番号from～toを指定して描画を行う
	 * @param canvas canvas
	 * @param fromIndex 段落インデックスfrom
	 * @param toIndex 段落インデックスto
	 */
	protected void draw(Canvas canvas, int fromIndex, int toIndex) {
		PageInfo pi = getCurrentPage();
		int paragraphLast = pi.getParagraphCount() - 1;
		int activeIndex = pi.getActiveParagraphIndex();
		boolean fin;

		for (int i = fromIndex; i <= toIndex; i++) {
			Paragraph para = pi.paragraphs.get(i);
			
			fin = drawParagraph(canvas, para, (i == activeIndex));
			
			//全部の文字を描画し終えたら
			if (fin) {
				if (i == paragraphLast && pi.isLast) {
					//その段落がそのページの最後の段落で
					//かつそのページが最終ページならば、stop
					stop();
				} else 
				if (i == activeIndex) {
					//アクティブな段落なら待ち状態へ
					waitContinue();
				}
			}
		}
	}
	
	/**
	 * 指定した段落を描画する 
	 * @param canvas canvas
	 * @param para 段落
	 * @param active アクティブな段落かどうか
	 * @return 最後まで描画できたかどうか
	 */
	protected boolean drawParagraph(Canvas canvas, Paragraph para, boolean active) {
		int sz, len, alpha;
		int playcount = 0;
		SerifInfo si;
		Object[] rubyIndexs;

		sz = para.serifs.size();

		//指定された段落の先頭行から最終行まで描画
		for (int i = 0; i < sz; i++) {

			si = para.serifs.get(i);
			len = si.line.length();

			for (int x = 0; x < len; x++) {

				if ((active == false)||(currentPos >= playcount + x + 1)) {

					String one = si.line.substring(x, x + 1);

					if (active) {
						//現在表示中の段落ならば、アルファ値付きで文字を描画
						//表示済みの段落ならば何もしない
						alpha = ALPHA_MAX;
	
						if (status == SERIF_PLAY && drawAlphaText) {
							if ((currentPos - (playcount + x + 1)) == 1) {
								alpha = ALPHA_SECOND; 	//二回目表示
							}
							if ((currentPos - (playcount + x + 1)) == 0) {
								alpha = ALPHA_FIRST;	//初回表示
							}
						}
						serifPaint.setAlpha(alpha);
						if (rubyEnable) { rubyPaint.setAlpha(alpha); }
					}

					//文字描画
					drawTextWithCharEffector(
							one,
							charEffectors,
							canvas,
							si.drawX[x],
							si.drawY[x],
							si.drawX[x],
							//文字のY座標 + ascent（負）の位置が、ビットマップを描画するY座標となる
							si.drawY[x] + si.ascent[x],
							si.width[x],
							si.height[x],
							serifPaint);
					
					//ルビ描画
					if (si.rubyInfos != null) {
						
						int rsz = si.rubyInfos.size();
						RubyInfo ri;
						for (int k = 0; k < rsz; k++) {
							
							ri = si.rubyInfos.get(k);
							rubyPaint.setTextSize(ri.fontSize);
							
							//ルビ文字インデックスマップから、現在文字と同時に描画すべきルビ文字のインデックス配列を取り出す
							rubyIndexs = ri.rubyTimingMap.get(x);
							
							if (rubyIndexs != null) {
								
								int isz = rubyIndexs.length;
								int rubyidx;
								
								for (int z = 0; z < isz; z++) {
									//ルビ文字インデックスを取り出しそのルビを描画
									rubyidx = (Integer)(rubyIndexs[z]);
									drawTextWithCharEffector(
										ri.ruby.substring(rubyidx, rubyidx + 1),
										charEffectors,
										canvas,
										ri.drawX[rubyidx],
										ri.drawY[rubyidx],
										ri.drawX[rubyidx],
										//文字のY座標 + ascent（負）の位置が、ビットマップを描画するY座標となる
										ri.drawY[rubyidx] + ri.ascent[rubyidx],
										ri.width[rubyidx],
										ri.height[rubyidx],
										rubyPaint);
								}
							}
						}
					}

					//アルファ値を元に戻す
					if (active) { 
						serifPaint.setAlpha(ALPHA_MAX); 
						if (rubyEnable) { rubyPaint.setAlpha(ALPHA_MAX); }
					}

				} else {
					return false;
				}
			}

			playcount += len;
		}
		
		return true;
	}

	/**
	 * 文字エフェクタに指定されていれば文字エフェクトを行って描画する。指定されていなければ通常の描画を行う、
	 * @param one 対象文字
	 * @param charEffectors 文字エフェクタのリスト
	 * @param canvas 描画対象キャンバス
	 * @param x 文字を描画するX原点
	 * @param y 文字を描画するY原点
	 * @param bmpx ビットマップを描画するX原点
	 * @param bmpy ビットマップを描画するY原点
	 * @param width 文字の幅
	 * @param height 文字の高さ
	 * @param p 描画に用いるPaint
	 */
	protected static void drawTextWithCharEffector(
			String one,
			HashMap<String, CharEffector> charEffectors,
			Canvas canvas, int x, int y, int bmpx, int bmpy,
			int width, int height, Paint p){

		CharEffector ce = charEffectors.get(one);
		
		if (ce != null) {
			ce.draw(canvas, x, y, bmpx, bmpy, width, height, p);
		} else {
			canvas.drawText(one, x, y , p);
		}
	}

	/**
	 * その文字がビットマップ文字エフェクタの変換対象かどうかを返す
	 * @param one 評価対象
	 * @param charEffectors 文字エフェクタセット
	 * @return 対象の文字エフェクタ or null
	 */
	public static CharEffector isCharToBitmapEffector(	
			String one, HashMap<String, CharEffector> charEffectors){
		
		CharEffector ce = charEffectors.get(one);
		
		if (ce != null) {
			if (ce.effectFlag == CharEffector.EFFECT_BITMAP ) {
				return ce;
			}
		}
		return null;
	}

	/**
	 * その文字が文字ポジションエフェクタの変換対象かどうかを返す
	 * @param one 評価対象
	 * @param charEffectors 文字エフェクタリスト
	 * @return 対象の文字エフェクタ or null
	 */
	public static CharEffector isCharPositionEffector(
			String one, HashMap<String, CharEffector> charEffectors){

		CharEffector ce = charEffectors.get(one);
		
		if (ce != null) {
			if (ce.effectFlag == CharEffector.EFFECT_CHAR_POSITION ) {
				return ce;
			}
		}
		return null;
	}


	/**
	 * カーソル描画
	 * @param c
	 */
	protected void drawCursor(Canvas c) {
    	if (viewCursor == false) { return; }

	    cursorPaint.setColor(
	    		Color.argb(cursorAlpha,
	    				Color.red(serifPaint.getColor()),
	    				Color.green(serifPaint.getColor()),
	    				Color.blue(serifPaint.getColor())));

	    if (viewDirection == VIEW_HORIZONTAL ) {
		    //横書きの場合描画領域の右下にカーソルを描画する
    		cursorDrawRect.set(
    				drawRect.right  - cursorWidth  - cursorSpacing,
    				drawRect.bottom - cursorHeight - cursorSpacing,
    				drawRect.right  - cursorSpacing,
    				drawRect.bottom - cursorSpacing);
    		
	    	if (cursorBitmap == null) {
	    		c.drawOval(cursorDrawRect, cursorPaint);
	    	} else {
	    		c.drawBitmap(cursorBitmap, cursorSrcRect, cursorDrawRect, cursorPaint);
	    	}
	    } else {
		    //縦書きの場合描画領域の左下にカーソルを描画する
    		cursorDrawRect.set(
    				drawRect.left   + cursorSpacing,
    				drawRect.bottom - cursorHeight - cursorSpacing,
    				drawRect.left   + cursorWidth  + cursorSpacing,
    				drawRect.bottom - cursorSpacing);
    		
	    	if (cursorBitmap == null) {
	    		c.drawOval(cursorDrawRect, cursorPaint);
	    	} else {
	    		c.drawBitmap(cursorBitmap, cursorSrcRect, cursorDrawRect, cursorPaint);
	    	}
	    }
	}

	/**
	 * カーソルブリンク開始
	 */
    protected void startBlinkCursor() {
    	if (viewCursor == false) { return; }

        cursorHandler.removeCallbacks(cursorRunnable);
        
    	cursorRunnable = new Runnable() {
            public void run() {

           		cursorAlpha += 32 * cursorDirection;

            	if (cursorAlpha > 255) {
            		cursorAlpha = 255;
            		cursorDirection = -1;
            	}
            	if (cursorAlpha < 0) {
            		cursorAlpha = 0;
            		cursorDirection = 1;
            	}
            	parent.invalidate();

                cursorHandler.postDelayed(this, 50);
            }
        };

        cursorHandler.postDelayed(cursorRunnable, 50);
    }

    /**
     * カーソルブリンク停止
     */
    protected void stopBlinkCursor() {
        cursorHandler.removeCallbacks(cursorRunnable);
    }

	/**
	 * セリフ１行１行のデータを持つクラス 縦・横共用
	 */
	class SerifInfo {
		/** 横書き：上 縦書き：右 **/
		public static final int ROW_ALIGN_UPPER = 0;
		/** 横書き：中央 縦書き：中央 **/
		public static final int ROW_ALIGN_CENTER = 1;
		/** 横書き：下 縦書き：左 **/
		public static final int ROW_ALIGN_LOWER = 2;

		/** １行文字列データ。情報を変更しても再パースで失われるため注意。 **/
		public String line;
		/** この行の長さ **/
		public int lineLength;
		/** 1文字ごとのX基準座標。情報を変更しても再パースで失われるため注意。 **/
		public int drawX[];
		/** 1文字ごとのY基準座標。情報を変更しても再パースで失われるため注意。 **/
		public int drawY[];
		/** 1文字ごとの高さデータ。情報を変更しても再パースで失われるため注意。 **/
		public int ascent[];
		/** 1文字ごとの高さデータ。情報を変更しても再パースで失われるため注意。 **/
		public int descent[];
		/** abs(ascent) + descent。情報を変更しても再パースで失われるため注意。 **/
		public int height[];
		/** 1文字ごとの幅データ。情報を変更しても再パースで失われるため注意。 **/
		public int width[];
		/** １行の幅。情報を変更しても再パースで失われるため注意。**/
		public int lineWidth;
		/** １行の高さ。情報を変更しても再パースで失われるため注意。 **/
		public int lineHeight;
		/** 横書き/縦書き。情報を変更しても再パースで失われるため注意。 **/
		public int direction;
		/** 行特殊属性。情報を変更しても再パースで失われるため注意。 **/
		public int specialState;
		/** 横書き：垂直位置調整、縦書き：水平位置調整。現実装では固定ピッチフォントのため、ビットマップ文字エフェクタから自動設定される。 **/
		public int rowAlign = ROW_ALIGN_CENTER;
		/** この行に属する文節 **/
		public ArrayList<SectionLink> sectionLinks = new ArrayList<SectionLink>();
		/** この行に属するルビ文字 **/
		public ArrayList<RubyInfo> rubyInfos;
		
		/**
		 * コンストラクタ
		 * @param dir 向き
		 */
		public SerifInfo(int dir){
			super();
			direction = dir;
		}
		
		/**
		 * セクションへの参照と、セクション内のどこからどこまでを描画するかを設定する。
		 * @param sec この行が参照するセクション
		 * @param fromIdx セクション内開始インデックス ゼロオリジン
		 * @param toIdx セクション内終了インデックス ゼロオリジン
		 */
		public void addSectionLink(Section sec, int fromIdx, int toIdx) {
			SectionLink seclink = new SectionLink(sec, this, fromIdx, toIdx);
			
			int sz = sectionLinks.size();
			int lineidx = 0;
			
			for (int i = 0; i < sz; i++){
				lineidx += sectionLinks.get(i).linkLength;  
			}
			
			seclink.lineFromIndex = lineidx;
			seclink.lineToIndex = lineidx + seclink.linkLength - 1;
			sectionLinks.add(seclink);
		}
		
		/**
		 * 行情報に各種情報を設定する。
		 * @param msg 行に割り当てる文字列
		 * @param asc 文字のascentの配列
		 * @param desc 文字のdescentの配列
		 * @param wid 文字の幅の配列
		 * @param lspace 行スペース
		 * @param rspace ルビ描画スペース
		 * @param rmargin ルビと行の間のマージン
		 */
		public void setMeasureParams(String msg, int asc[], int desc[], int wid[],  
				int lspace, int rspace, int rmargin){
			line = new String(msg);
			lineLength = line.length();
			
			ascent = new int[lineLength];
			descent = new int[lineLength];
			height = new int[lineLength];
			width = new int[lineLength];
			drawX = new int[lineLength];
			drawY = new int[lineLength];
			
			System.arraycopy(asc, 0, ascent, 0, lineLength);
			System.arraycopy(desc, 0, descent, 0, lineLength);
			System.arraycopy(wid, 0, width, 0, lineLength);
			
			for (int i = 0; i < lineLength; i++) {
				height[i] = (ascent[i] * -1) + descent[i];
			}
			
			calcLineSize(lspace, rspace, rmargin);
		}
		
		/**
		 * １行の幅、高さを計算する
		 */
		public void calcLineSize(int lspace, int rspace, int rmargin) {

			if (direction == SerifDirector.VIEW_HORIZONTAL) {
				lineWidth = 0;
				for (int i = 0; i < lineLength; i++) {
					lineWidth += width[i];
				}

				lineHeight = getMaxHeight() + lspace + rspace + rmargin;
			} else {
				lineWidth = getMaxWidth() + lspace + rspace + rmargin;

				lineHeight = 0;
				for (int i = 0; i < lineLength; i++) {
					lineHeight += height[i];
				}
			}

		}

		/**
		 * 文字幅の最大を取得する
		 * @return 最大幅
		 */
		public int getMaxWidth(){
			int max = 0;
			for (int i = 0; i < lineLength; i++) {
				if (max < width[i]) {
					max = width[i];
				}
			}
			return max;
		}

		/**
		 * 文字の高さの最大を取得する
		 * @return 最大高さ
		 */
		public int getMaxHeight(){
			int max = 0;
			for (int i = 0; i < lineLength; i++) {
				if (max < height[i]) {
					max = height[i];
				}
			}
			return max;
		}
	}

	/**
	 * 段落情報を管理するクラス
	 */
	class Paragraph{
		/** その段落のトータル文字数 **/
		protected int totalLength;
		/** その段落の1行ごとのセリフ情報を取得可能。情報を変更しても再パースで失われるため注意。 **/
		public ArrayList<SerifInfo> serifs = new ArrayList<SerifInfo>();
		/** 背景ビットマップ **/
		//protected Bitmap background;
		/** ビットマップの元の大きさを表すRect。drawのたびに毎回生成するオーバーヘッドを避ける。 **/
		//protected Rect backgroundRect = new Rect();
		/** ビットマップにエフェクトを行うかどうか **/
		//protected boolean backgroundWithEffect;
		/** ビットマップ用Paint **/
		//protected Paint bmpPaint = new Paint();
		/** ページ内段落NO **/
		protected int paragraphIndex;

		/**
		 * 新規段落を生成する。
		 */
		public Paragraph(){	super(); }
		
		/** ページ内段落インデックスを返す ゼロオリジン **/
		public int getParagraphIndex(){
			return paragraphIndex;
		}

		/**
		 * セリフを一行追加する
		 * @param serifInfo
		 */
		public void addSerifInfo(SerifInfo serifInfo){
			serifs.add(serifInfo);
		}

		/**
		 * 段落の背景ビットマップを設定する。
		 * @param bmp 背景ビットマップ
		 * @param withEffect ビットマップの描画タイミングを、エフェクト前とエフェクト後どちらで行うか。
		 */
		/*public void setBitmap(Bitmap bmp, boolean withEffect) {
			if (bmp == null) {
				background = null;
			} else {
				background = bmp;
				backgroundRect.set(0, 0, bmp.getWidth(), bmp.getHeight());
				backgroundWithEffect = withEffect;
			}
		}*/

		/**
		 * その段落の情報を集計する（トータル文字数など）
		 */
		public void parse(int paragraphNoIs) {
			paragraphIndex = paragraphNoIs;
			int sz = serifs.size();
			totalLength = 0;
			for (int i = 0; i < sz; i++) {
				totalLength += serifs.get(i).line.length();
			}
		}

		/** 段落内の全文字数を返す **/
		public int getTotalLength(){ return totalLength; }

		/**
		 * 背景の描画を行う
		 * @param canvas キャンバス
		 * @param isEffected エフェクト実行前か、後か。適切なタイミングで描画が行われる。
		 * @param targetRect 描画先の領域
		 */
		/*public void draw(Canvas canvas, boolean isEffected, Rect targetRect) {

			if (background != null) {
				if ((isEffected && backgroundWithEffect) ||
					(isEffected == false && backgroundWithEffect == false)) {
					//エフェクト前かどうかから、描画してよいかどうかを判断する

					canvas.drawBitmap(background, backgroundRect, targetRect, bmpPaint);

				}
			}
		}*/


	}

	/**
	 * 1ページぶんの情報を管理するクラス
	 */
	class PageInfo{
		/** 先頭ページかどうか **/
		public boolean isFirst;
		/** 最終ページかどうか **/
		public boolean isLast;
		/** そのページのトータル文字数 **/
		protected int totalLength;
		/** そのページの段落を取得可能。情報を変更しても再パースで失われるため注意。 **/
		protected ArrayList<Paragraph> paragraphs = new ArrayList<Paragraph>();
		/** 背景ビットマップ **/
		protected Bitmap background;
		/** ビットマップの元の大きさを表すRect。drawのたびに毎回生成するオーバーヘッドを避ける。 **/
		protected Rect backgroundRect = new Rect();
		/** ビットマップにエフェクトを行うかどうか **/
		protected boolean backgroundWithEffect;
		/** ビットマップ用Paint **/
		protected Paint bmpPaint = new Paint();
		/** 現在アクティブな段落のインデックス **/
		protected int activeParagraphIndex = 0;
		/** ページNO **/
		protected int pageIndex;

		/**
		 * 新規ページを生成する。
		 */
		public PageInfo(){	super(); }
		
		/** ページNOを取得 ゼロオリジン **/
		public int getPageIndex(){
			return pageIndex;
		}

		/**
		 * 段落を一つ追加する
		 * @param serifInfo
		 */
		public void addParagraphInfo(Paragraph para){
			paragraphs.add(para);
		}

		/**
		 * ページの背景ビットマップを設定する。
		 * @param bmp 背景ビットマップ
		 * @param withEffect ビットマップの描画タイミングを、エフェクト前とエフェクト後どちらで行うか。
		 */
		public void setBitmap(Bitmap bmp, boolean withEffect) {
			if (bmp == null) {
				background = null;
			} else {
				background = bmp;
				backgroundRect.set(0, 0, bmp.getWidth(), bmp.getHeight());
				backgroundWithEffect = withEffect;
			}
		}

		/**
		 * そのページの情報を集計する（トータル文字数など）
		 */
		public void parse(int pageNoIs) {
			this.pageIndex = pageNoIs;
			int sz = paragraphs.size();
			Paragraph para;
			totalLength = 0;
			for (int i = 0; i < sz; i++) {
				para = paragraphs.get(i);
				para.parse(i);
				totalLength += para.totalLength;
			}
		}

		/** ページの内の全文字数を返す **/
		public int getTotalLength(){ return totalLength; }

		/**
		 * 背景の描画を行う
		 * @param canvas キャンバス
		 * @param isEffected エフェクト実行前か、後か。適切なタイミングで描画が行われる。
		 * @param targetRect 描画先の領域
		 */
		public void draw(Canvas canvas, boolean isEffected, Rect targetRect) {

			if (background != null) {
				if ((isEffected && backgroundWithEffect) ||
					(isEffected == false && backgroundWithEffect == false)) {
					//エフェクト前かどうかから、描画してよいかどうかを判断する

					canvas.drawBitmap(background, backgroundRect, targetRect, bmpPaint);

				}
			}
		}
		
		/**
		 * 現在アクティブな段落のインデックスを取得する
		 * @return インデックス ゼロオリジン
		 */
		public int getActiveParagraphIndex() { return activeParagraphIndex; }
		
		/**
		 * アクティブな段落のインデックスを設定する
		 * @param index
		 */
		public void setActiveParagraphIndex(int index) { 
			activeParagraphIndex = index;
		}
		
		/**
		 * 次の段落をアクティブにする
		 */
		public void nextParagraph() {
			if (isLastParagraph() == false) {
				activeParagraphIndex++;
			}
		}
		
		/**
		 * アクティブな段落が、そのページの最後の段落かどうかを返す
		 * @return
		 */
		public boolean isLastParagraph(){
			return activeParagraphIndex >= (paragraphs.size() - 1); 
		}
		
		/**
		 * 現在アクティブな段落を取得する
		 * @return 段落情報
		 */
		public Paragraph getActiveParagraph() { return paragraphs.get(activeParagraphIndex); }
		
		/**
		 * そのページの段落数を取得する
		 * @return 段落数
		 */
		public int getParagraphCount() { return paragraphs.size(); }
	}

	/**
	 * 文字エフェクタを全てクリアする。autoParseがtrueの場合、再パースされる。
	 */
	public void clearCharEffector(){
		charEffectors.clear();
		if (autoParse) { parse(); }
	}

	/**
	 * 文字エフェクタを取得する
	 * @param index 取得するインデックス
	 * @return CharEffector
	 */
	public CharEffector getCharEffector(String character){
		return charEffectors.get(character);
	}

	/**
	 * 文字エフェクタを削除する。autoParseがtrueの場合、再パースされる。
	 * @param index 削除するインデックス
	 */
	public void removeCharEffector(String character){
		charEffectors.remove(character);
		if (autoParse) { parse(); }
	}

	/**
	 * 文字エフェクタの数を返す
	 * @return サイズ
	 */
	public int getCharEffectorSize(){
		return charEffectors.size();
	}

	/**
	 * ブランクの文字エフェクタを１文字追加して返す。パースされないため注意。
	 * @param s 対象文字
	 * @param effect EFFECT_BITMAP or EFFECT_CHAR_POSITION
	 * @return 追加された文字エフェクタ
	 */
	public CharEffector addCharEffector(String s, int effect){
		CharEffector ce = new CharEffector(s, effect);
		charEffectors.put(s, ce);
		return ce;
	}

	/**
	 * 文字ポジションエフェクタを作成する。主に縦書きでの補正で使うが、横書きでも使える。autoParseがtrueの場合、再パースされる。
	 * @param commaString 文字, 回転角度, X補正比率, Y補正比率のカンマ区切り文字列。スペースは無視される。反復して複数指定も可能。
	 */
	public void addCharPositionEffector(String commaString){
	    String[] effectData;
	    CharEffector ce;

	    effectData = parseCommaData(commaString);
	    int sz = effectData.length;
	    
	    for (int i = 0; i < sz; i += 4) {
		    ce = addCharEffector(effectData[i], CharEffector.EFFECT_CHAR_POSITION);
		    ce.angle = Float.parseFloat(effectData[i + 1]);
		    ce.translateRateX = Float.parseFloat(effectData[i + 2]);
		    ce.translateRateY = Float.parseFloat(effectData[i + 3]);
	    }

		if (autoParse) { parse(); }
	}

	/**
	 * ビットマップ文字エフェクタを作成する。対象文字の位置にはビットマップが描画される。autoParseがtrueの場合、再パースされる。
	 * @param str 対象文字
	 * @param bmp 変換するビットマップ
	 * @param style DRAW_BITMAP_NORMAL サイズ指定で描画する DRAW_BITMAP_FONT_REPLACE 置換前文字の大きさで描画する
	 * @param drawWidth 描画域幅 DRAW_BITMAP_FONT_REPLACEの場合無視される
	 * @param drawHeight 描画域高さ DRAW_BITMAP_FONT_REPLACEの場合無視される
	 * @param rowalign 横書き：垂直位置調整、縦書き：水平位置調整。ROW_ALIGN_UPPER,ROW_ALIGN_CENTER,ROW_ALIGN_LOWER
	 */
	public void addCharToBitmapEffector(String str, Bitmap bmp, int style, 
			int drawWidth, int drawHeight, int rowalign){
	    CharEffector ce;

	    ce = addCharEffector(str, CharEffector.EFFECT_BITMAP);
	    ce.setBitmap(bmp, style, drawWidth, drawHeight, rowalign);

		if (autoParse) { parse(); }
	}

	/**
	 * 文字エフェクタ。1文字単位のエフェクトを行う
	 */
	class CharEffector {
		/** ビットマップ変換：文字をビットマップへ変換する **/
		public static final int EFFECT_BITMAP = 1;
		/** 回転・位置補正：文字に回転・位置補正エフェクトをかける。 **/
		public static final int EFFECT_CHAR_POSITION = 2;

		/** effectFlagがEFFECT_BITMAPの時に、ビットマップの描画領域を直接指定する。 **/
		public static final int DRAW_BITMAP_NORMAL = 1;
		/** effectFlagがEFFECT_BITMAPの時に、ビットマップを変換前文字と同じ大きさで描画する **/
		public static final int DRAW_BITMAP_FONT_REPLACE = 2;

		/** EFFECT_BITMAP or EFFECT_CHAR_POSITIONを指定 **/
		public int effectFlag = EFFECT_CHAR_POSITION;
		/** エフェクト対象文字。 **/
		public String character = null;
		/** 変換するビットマップ **/
		protected Bitmap charBmp = null;
		/** ビットマップの元の大きさを表すRect。drawのたびに毎回生成するオーバーヘッドを避ける。 **/
		protected Rect charBmpRect = new Rect();
		/** ビットマップの描画先を表すRect。drawのたびに毎回生成するオーバーヘッドを避ける。 **/
		protected Rect tmpRect = new Rect();
		/** DRAW_BITMAP_NORMAL or DRAW_BITMAP_FONT_REPLACEを指定。 **/
		protected int bitmapStyle = DRAW_BITMAP_FONT_REPLACE;
		/** ビットマップの描画域幅。bitmapStyleがDRAW_BITMAP_FONT_REPLACEの時は無視される。 **/
		protected int drawBitmapWidth = 0;
		/** ビットマップの描画域高さ。bitmapStyleがDRAW_BITMAP_FONT_REPLACEの時は無視される。 **/
		protected int drawBitmapHeight = 0;
		/** 横書き：垂直位置調整、縦書き：水平位置調整。この変更による再パースの必要はない。 **/
		public int rowAlign = SerifInfo.ROW_ALIGN_CENTER;
		/** 文字の回転角度 正または負で0～360。正で右回り、負で左回りに補正。この変更による再パースの必要はない。 **/
		public float angle = 0f;
		/** 文字のX座標位置の補正比率。正で右に、負で左に補正。補正値は1fで一文字ぶん、0.5fで文字の半分の大きさに相当。この変更による再パースの必要はない。 **/
		public float translateRateX = 0f;
		/** 文字のY座標位置の補正比率。正で下に、負で上に補正。補正値は1fで一文字ぶん、0.5fで文字の半分の大きさに相当。この変更による再パースの必要はない。 **/
		public float translateRateY = 0f;

		/**
		 * 文字エフェクタを作成する。
		 * @param s エフェクト対象とする文字。Serif文中のこの文字は全てエフェクト対象となる。
		 * @param effect EFFECT_BITMAP(文字をビットマップへ変換する) or EFFECT_CHAR_POSITION(文字に回転・位置補正エフェクトをかける)を指定
		 */
		public CharEffector(String s, int effect){
			super();
			character = new String(s);
			effectFlag = effect;
		}

		/**
		 * 文字を変換するビットマップを設定する。
		 * @param bmp 変換するビットマップ
		 * @param style DRAW_BITMAP_NORMAL(ビットマップの描画幅を直接指定) or DRAW_BITMAP_FONT_REPLACE(ビットマップを変換前文字と同じ大きさで描画)を指定。
		 * @param drawWidth ビットマップの描画幅。bitmapStyleがDRAW_BITMAP_FONT_REPLACEの時は無視される。
		 * @param drawHeight ビットマップの描画高さ。bitmapStyleがDRAW_BITMAP_FONT_REPLACEの時は無視される。
		 * @param rowalign 横書き：垂直位置調整、縦書き：水平位置調整。
		 */
		public void setBitmap(Bitmap bmp, int style, 
				int drawWidth, int drawHeight, int rowalign) {
			if (bmp == null) {
				charBmp = null;
			} else {
				charBmp = bmp;
				charBmpRect.set(0, 0, bmp.getWidth(), bmp.getHeight());
				drawBitmapWidth = drawWidth;
				drawBitmapHeight = drawHeight;
				bitmapStyle = style;
				rowAlign = rowalign;
			}
		}

		/**
		 * 文字エフェクタによるエフェクトをかけて描画する。呼び出し元のPaintインスタンスが使われる。
		 * @param canvas 描画対象キャンバス
		 * @param x 文字を描画する場合のX座標
		 * @param y 文字を描画する場合のY座標 通常はFontMetricsのベースラインの位置を指定する
		 * @param bmpx ビットマップを描画する場合のX座標
		 * @param bmpy ビットマップを描画する場合のY座標 通常は y + FontMetrics.ascent(負)の位置を指定する。つまり文字を描画する場合よりかなり上の座標を指定する。
		 * @param width 文字の幅 (FontMetrics.ascentmeasureTextの値)
		 * @param height 文字の高さ (abs(ascent) + descentの値)
		 * @param p 描画に用いるPaint
		 */
		public void draw(Canvas canvas, int x, int y, int bmpx, int bmpy,
				int width, int height, Paint p) {

			if (effectFlag == EFFECT_BITMAP) {
			//ビットマップ描画

				if (bitmapStyle == DRAW_BITMAP_NORMAL) {
					//ビットマップをあらかじめ指定しておいたサイズで描画
					tmpRect.set(bmpx, bmpy, bmpx + drawBitmapWidth, bmpy + drawBitmapHeight);
				} else {
					//ビットマップを、置換前文字と同じ大きさで描画
					tmpRect.set(bmpx, bmpy, bmpx + width, bmpy + height);
				}

				canvas.drawBitmap(charBmp, charBmpRect, tmpRect, p);
			} else {
				try {
					//文字を補正して描画

					canvas.save();

					//回転角がある場合のみ回転
					if (angle != 0) { canvas.rotate(angle, x, y); };

	                canvas.drawText(character, x + (float)width * translateRateX, y + (float)height * translateRateY,p);

				} finally {
					canvas.restore();
				}

			}

		}
	}
	
	/**
	 * 行のどの位置にどのセクションを描画するかを関連付けるクラス。
	 */
	class SectionLink {
		/** セクション内開始インデックス ゼロオリジン **/
		public int fromIndex;
		/** セクション内終了インデックス ゼロオリジン **/
		public int toIndex;
		/** 行内開始インデックス ゼロオリジン **/
		public int lineFromIndex;
		/** 行内終了インデックス ゼロオリジン **/
		public int lineToIndex;
		/** 長さ **/
		public int linkLength;
		/** 参照するセクション **/
		public Section section;
		/** 参照する行情報 **/
		public SerifInfo serifInfo;
		
		/**
		 * コンストラクタ
		 * @param sec 参照するセクション
		 * @param si 参照する行情報
		 * @param fromIdx セクション内開始インデックス ゼロオリジン
		 * @param toIdx セクション内終了インデックス ゼロオリジン
		 */
		public SectionLink(Section sec, SerifInfo si, int fromIdx, int toIdx) {
			super();
			section = sec;
			fromIndex = fromIdx;
			toIndex = toIdx;
			linkLength = (toIdx - fromIdx) + 1; 
			serifInfo = si;
			//セクションへの参照をセット
			sec.addSectionLink(this);
		}
	}
	
	/**
	 * 文節クラス
	 * 文字列を文章の改行、段落、改頁、ルビで区切って管理する
	 */
	class Section {
		/** 長さ **/ 
		public int wordLength;
		/** 文節 **/ 
		public String word;
		/** 文節属性 ルビや漢字属性は他属性との混合がありえる。**/ 
		public int attribute = 0;
		/** この文節に関連した文節（ルビ **/ 
		public ArrayList<Section> childSections = null;
		/** セクションリンクへの参照、複数 **/
		public ArrayList<SectionLink> sectionLinks = new ArrayList<SectionLink>();
		
		/**
		 * コンストラクタ
		 * @param s セクション文字列
		 * @param attr セクション属性
		 */
		public Section(String s, int attr){
			super();
			word = s;
			wordLength = s.length();
			attribute = attribute | attr;
		}
		
		/**
		 * 子セクションを紐付ける
		 * @param extra 子セクション
		 */
		public void addChildSection(Section extra){
			if (childSections == null) {
				childSections = new ArrayList<Section>(); 
			}
			childSections.add(extra);
		}
		
		/**
		 * セクションリンクと紐づける
		 * @param secLink
		 */
		public void addSectionLink(SectionLink secLink){
			sectionLinks.add(secLink);
		}
		
		/**
		 * そのセクションが割り当てられた行それぞれの描画サイズと座標を返す 
		 * @param sdis OUT SectionDrawInfoに行ごとの情報を設定して返す
		 * @return トータルサイズ 横書きの場合幅 縦書きの場合高さ
		 */
		public int getSectionDrawInfo(ArrayList<SectionDrawInfo> sdis){
			SectionLink seclink;
			SectionDrawInfo sdi;
			int result = 0;
			int sz = sectionLinks.size();
			sdis.clear();
			
			//セクションが複数の行にまたがっている場合、複数の行情報に関連付けられている。
			for (int i = 0; i < sz; i++) {
				//そのセクションに関連する行情報を全てたどる
				seclink = sectionLinks.get(i);
				//その行についての値返却用インスタンス生成
				sdi = new SectionDrawInfo(seclink.serifInfo, seclink);
				sdis.add(sdi);
				
				sdi.drawOriginX = sdi.serifInfo.drawX[seclink.lineFromIndex];
				sdi.drawOriginY = sdi.serifInfo.drawY[seclink.lineFromIndex];
				sdi.drawOriginYAdjustAscent = sdi.serifInfo.drawY[seclink.lineFromIndex] + 
					sdi.serifInfo.ascent[seclink.lineFromIndex];
				
				sdi.str = sdi.serifInfo.line.substring(seclink.lineFromIndex, seclink.lineToIndex + 1);
				
				int max = 0;
				
				//関連付けられた行の該当位置のwidthまたはheightを合算して行ごとの描画サイズを計算する
				if (sdi.serifInfo.direction == VIEW_HORIZONTAL) {
					for (int x = seclink.lineFromIndex; x <= seclink.lineToIndex; x++) {
						sdi.width += sdi.serifInfo.width[x];
						if (max < sdi.serifInfo.height[x]) {
							max = sdi.serifInfo.height[x];
						}
						
						result += sdi.serifInfo.width[x];
					}
					sdi.height = max;
				} else {
					for (int x = seclink.lineFromIndex; x <= seclink.lineToIndex; x++) {
						sdi.height += sdi.serifInfo.height[x];
						if (max < sdi.serifInfo.width[x]) {
							max = sdi.serifInfo.width[x];
						}
						
						result += sdi.serifInfo.height[x];
					}
					sdi.width = max;
				}
			}
			
			return result;
		}
	}
	
	/**
	 * セクションが各行に占めるサイズと座標。Section.getSectionDrawInfo()を参照。 
	 */
	class SectionDrawInfo{
		/** 関連付けられた行情報 **/
		public SerifInfo serifInfo;
		/** 関連付けられた行情報における文字位置情報 **/
		public SectionLink seclink;
		/** その行での描画開始座標X **/
		public int drawOriginX;
		/** その行での描画開始座標Y(baseline) **/
		public int drawOriginY;				
		/** その行での描画開始座標Y(baseline + ascent(負）) **/
		public int drawOriginYAdjustAscent;	
		/**　描画する幅 **/
		public int width;
		/**　描画する高さ **/
		public int height;
		/** 描画する文字 **/
		public String str;
		/** コンストラクタ **/
		public SectionDrawInfo(SerifInfo si, SectionLink secl) {
			super();
			serifInfo = si;
			seclink = secl;
		}
	}
	
	/**
	 * ルビ情報 
	 */
	class RubyInfo {
		/** ルビ文字 **/
		public String ruby;
		/** 長さ **/
		public int rubyLength;
		/** 縦書き横書き **/
		public int direction;
		/** フォントサイズ **/
		public int fontSize;
		/** 描画領域の幅 **/
		public int clipWidth;
		/** 描画領域の高さ **/
		public int clipHeight;
		/** 実際に描画する領域の幅 **/
		public int drawWidth;
		/** 実際に描画する領域の高さ **/
		public int drawHeight;
		/** 描画開始座標X **/
		public int drawX[];
		/** 描画開始座標Y **/
		public int drawY[];
		/** 1文字ごとの高さデータ。 **/
		public int ascent[];
		/** 1文字ごとの高さデータ。 **/
		public int descent[];
		/** abs(ascent) + descent。**/
		public int height[];
		/** 1文字ごとの幅データ。**/
		public int width[];
		/** 被ルビ文字それぞれと同時に描画すべきルビ文字のインデックスのマップ **/
		public HashMap<Integer, Object[]> rubyTimingMap = new HashMap<Integer, Object[]>();
		
		/**
		 * コンストラクタ
		 * @param dir 向き
		 */
		public RubyInfo(int dir) {
			super();
			direction = dir; 
		}
		
		/**
		 * ルビ文字の基本情報を割り当て、フォントサイズを計算しフォント情報を返す
		 * @param rubystr 割り当てるルビ文字
		 * @param stretch 描画領域にフォントをフィットさせるかどうか
		 * @param wid ルビの描画領域全体の幅
		 * @param hei ルビの描画領域全体の高さ
		 * @param defaultFontMetrics デフォルトのフォントメトリクス
		 * @param defaultFontSize フォントサイズ。stretchがtrueの場合、最大フォントサイズとなる。
		 * @param tester 評価するフォント情報(typefaceなど)が設定されたPaintクラス。フォントサイズは変更される。
		 * @return フォント情報
		 */
		public FontMetrics calcFontSize(String rubystr, boolean stretch, int wid, int hei, 
				int defaultFontSize, Paint tester, FontMetrics defaultFontMetrics){
			ruby = new String(rubystr);
			rubyLength = ruby.length();
			clipWidth = wid;
			clipHeight = hei;
			FontMetrics fm;
			
			//フォントストレッチの場合
			if (stretch) { 
				//フォントサイズを少しずつ下げていって丁度入りきるフォントサイズを探す
				if (direction == VIEW_HORIZONTAL) {
					fontSize = fontTestHorizontal(ruby, defaultFontSize, clipWidth, clipHeight, 1, tester);
				} else {
					fontSize = fontTestVertical(ruby, defaultFontSize, clipWidth, clipHeight, 1, tester);
				}
				tester.setTextSize(fontSize);
				fm = tester.getFontMetrics();
			} else {
				fontSize = defaultFontSize;
				fm = defaultFontMetrics;
			}
			
			if (direction == VIEW_HORIZONTAL) {
				
				drawWidth = (int)tester.measureText(ruby);
				drawHeight = (int)((fm.ascent * -1) + fm.descent);
				
			} else {
				int ascentabs = (int)(fm.ascent * -1);
				int max = 0;
				int measure;
				
				drawHeight = 0;
				
				for (int x = 0; x < rubyLength; x++) {
					drawHeight += ascentabs + fm.descent;
					measure = (int)tester.measureText(ruby.substring(x, x + 1)); 
					if (max < measure) {
						max = measure;
					}
				}
				
				drawWidth = max;
			}
			
			return fm;
		}
		
		/**
		 * 被ルビ文字の座標を基準としてルビ文字の描画座標を計算する。
		 * @param drawOriginX ルビ文字の描画開始座標X
		 * @param drawOriginY ルビ文字の描画開始座標Y
		 * @param tester 評価用のPaintクラス。
		 * @param fm 評価用のフォント情報。
		 */
		public void calcLocation(int drawOriginX, int drawOriginY, Paint tester, FontMetrics fm){
			
			drawX = new int[rubyLength];
			drawY = new int[rubyLength];
			ascent = new int[rubyLength];
			descent = new int[rubyLength];
			height = new int[rubyLength];
			width = new int[rubyLength];
			
			int charwidth, charheight;
			
			charheight = (int)((fm.ascent * -1) + fm.descent);
			
			if (direction == VIEW_HORIZONTAL) {
				drawOriginX = drawOriginX - ((drawWidth - clipWidth) / 2); //中央揃え
				
				for (int i = 0; i < rubyLength; i++) {
					drawX[i] = drawOriginX; 	
					drawY[i] = drawOriginY; 	
					charwidth = (int)tester.measureText(ruby.substring(i, i + 1));
					width[i] = charwidth;
					height[i] = charheight;
					ascent[i] = (int)fm.ascent;
					descent[i] = (int)fm.descent;
					
					drawOriginX += charwidth;
				}

			} else {
				drawOriginY = drawOriginY + (int)(fm.ascent * -1);
				//注：縦書きの場合のルビの基準Y座標は
				//被ルビ文字のベースラインからトップを取得しそこにルビ文字の
				//abs(ascent)を足した位置がルビ文字のベースラインとなる
				//つまり
				//被ルビ文字のベースライン + ascent（負）+ ルビ文字のabs(ascent) = ルビ文字のベースライン
				
				drawOriginY = drawOriginY - ((drawHeight - clipHeight) / 2); //中央揃え
				
				for (int i = 0; i < rubyLength; i++) {
					drawX[i] = drawOriginX; 	
					drawY[i] = drawOriginY; 	
					charwidth = (int)tester.measureText(ruby.substring(i, i + 1));
					width[i] = charwidth;
					height[i] = charheight;
					ascent[i] = (int)fm.ascent;
					descent[i] = (int)fm.descent;
					
					drawOriginY += charheight;
				}
			}
		}
		
		/**
		 * 被ルビ文字とルビ文字の描画タイミング（どの被ルビ文字を描画するときにどのルビを描画するか）を同期する。<br>
		 * 横書きの上から下へ描画、縦書きの右から左へ描画のみに対応。
		 * @param ssr その行の描画情報
		 * @param intlist バッファ用のリスト。呼び出し側で用意する。
		 */
		public void setRubyTiming(SectionDrawInfo ssr, ArrayList<Integer> intlist){
			int parentProgress;
			int next = 0;	
			Object[] rubyindexs;
			int lastmapindex = 0;
			int sz;
			
			if (direction == VIEW_HORIZONTAL) {
				
				for (int i = ssr.seclink.lineFromIndex; i <= ssr.seclink.lineToIndex; i++) {
					
					//被ルビ文字の座標終端を取得
					parentProgress = ssr.serifInfo.drawX[i] + ssr.serifInfo.width[i];
					
					intlist.clear();
					
					//被ルビ文字の座標終端より前方（文章先頭方向）にあるルビ文字を被ルビ文字とマッピングする
					for (int x = next; x < rubyLength; x++) {
						if (drawX[x] + width[x] <= parentProgress) {
							
							intlist.add(new Integer(x));
							next = x + 1;
						}
					}
					
					//見つかったなら、被ルビ文字インデックスをハッシュとして、配列化してマップに保存
					if (intlist.size() > 0) {
						lastmapindex = i;
						rubyTimingMap.put(new Integer(lastmapindex), intlist.toArray());
					}
					
				}
				
				//マップできなかったルビ文字は、被ルビ文字の最後の文字にマップ
				//ルビ文字の座標終端が被ルビ文字よりはみ出ている場合に発生
				if (next < rubyLength) {
					intlist.clear();
					
					//最後にマップした配列をいったん取り出す
					rubyindexs = rubyTimingMap.get(lastmapindex);
					sz = rubyindexs.length;
					 
					for (int x = 0; x < sz; x++) {
						intlist.add(new Integer((Integer)rubyindexs[x]));
					}
					
					for (int x = next; x < rubyLength; x++) {
						intlist.add(new Integer(x));
					}
					
					rubyTimingMap.put(new Integer(lastmapindex), intlist.toArray());
				}
			} else {
				//縦書きも同様
				for (int i = ssr.seclink.lineFromIndex; i <= ssr.seclink.lineToIndex; i++) {
					
					parentProgress = ssr.serifInfo.drawY[i] + ssr.serifInfo.height[i];
					
					intlist.clear();
					
					for (int x = next; x < rubyLength; x++) {
						if (drawY[x] + height[x] <= parentProgress) {
							
							intlist.add(new Integer(x));
							next = x + 1;
						}
					}
					
					if (intlist.size() > 0) {
						lastmapindex = i;
						rubyTimingMap.put(new Integer(lastmapindex), intlist.toArray());
					}
					
				}
				
				if (next < rubyLength) {
					intlist.clear();
					
					rubyindexs = rubyTimingMap.get(lastmapindex);
					sz = rubyindexs.length;
					
					for (int x = 0; x < sz; x++) {
						intlist.add(new Integer((Integer)rubyindexs[x]));
					}
					
					for (int x = next; x < rubyLength; x++) {
						intlist.add(new Integer(x));
					}
					
					rubyTimingMap.put(new Integer(lastmapindex), intlist.toArray());
				}
			}
		}
	}
	
	/**
	/**
	 * 文字列を、各描画領域の比率を元に分割しSectionDrawInfoのリストに設定して返す。例えば"みかん"を60px,40pxの幅を指標に2分割する場合は6対4として"みか","ん"に分割する。
	 * @param target 対象文字列
	 * @param direction 向き
	 * @param totalsize sdis.sizeのトータル 比率の分母となる
	 * @param sdis OUT 描画領域サイズ 横書き 幅 縦書き 高さ
	 */
	protected static void splitStringBySizeRatio(String target, 
			int direction, int totalsize, ArrayList<SectionDrawInfo> sdis) {
		int sz = sdis.size();
		
		//分割する必要がなければ文字列をフルでセットして返す
		if (sz == 1) {
			sdis.get(0).str = target;
			return;
		}
		
		float ratio[] = new float[sz];
		int len[] = new int[sz];
		int ttl = 0;
		int over;
		int strlen = target.length();
		
		for (int i = 0; i < sz; i++) {
			//比率計算
			if (direction == VIEW_HORIZONTAL) {
				ratio[i] = (float)sdis.get(i).width / (float)totalsize;
				
			} else {
				ratio[i] = (float)sdis.get(i).height / (float)totalsize;
			}
			//比率ｘ文字数を四捨五入して分割文字数をセット
			len[i] = Math.round(((float)strlen) * ratio[i]);
			//四捨五入した結果のトータルを計算
			ttl += len[i]; 
		}
		
		over = ttl - strlen;
		//もし文字を分割した結果、トータル文字長からずれてしまったら補正
		while (over != 0) {
			if (over > 0) {
			//多くなってしまった場合
			//後ろから順に1文字ずつ減らしていく
				for (int i = sz - 1; i >= 0; i--) {
					if (len[i] > 0) {
						len[i]--;
						over--;
						if (over == 0) { break; }
					}
				}
			}
			if (over < 0) {
			//少なくなってしまった場合
			//前から順に1文字ずつ足していく
				for (int i = 0; i < sz; i++) {
					len[i]++;
					over++;
					if (over == 0) { break; }
				}
			}
		}
		
		//計算結果から、各描画領域に文字列を割り当て
		int index = 0;
		for (int i = 0; i < sz; i++){
			
			if (len[i] > 0) {
				sdis.get(i).str = target.substring(index, index + len[i]);
				index += len[i];
			} else {
				sdis.get(i).str = null;
			}
		}
	}
	
	/**
	 * 指定した幅、高さに横書きで入りきる最大フォントサイズを返す
	 * @param target IN 評価文字列
	 * @param defaultFontSize IN 最大フォントサイズ
	 * @param width IN 描画領域の幅
	 * @param height IN 描画領域の高さ
	 * @param resolution IN 評価のきめ細かさ 最小1 少ないほど精度が上がるが遅くなる
	 * @param tester OUT 評価するフォント情報(typefaceなど)が設定されたPaintクラス。評価結果のフォントサイズが設定されて返される。最小2。
	 * @return サイズ
	 */
	public static final int fontTestHorizontal(String target, int defaultFontSize, 
			int width, int height, int resolution, Paint tester){
		int result = 0;
		
		for (int i = defaultFontSize; i >= MIN_FONT_SIZE; i = i - resolution){
			tester.setTextSize(i);
			FontMetrics fontMetrics = tester.getFontMetrics();
			
			result = (int)tester.measureText(target);
			
			if (( height >= (int)((fontMetrics.ascent * -1) + fontMetrics.descent))&&
				( width >= result)) {
				return i;
			}
		}
		return MIN_FONT_SIZE;
	}
	
	/**
	 * 指定した幅、高さに縦書きで入りきる最大フォントサイズを返す
	 * @param target IN 評価文字列
	 * @param defaultFontSize IN 最大フォントサイズ
	 * @param width IN 描画領域の幅
	 * @param height IN 描画領域の高さ
	 * @param resolution IN 評価のきめ細かさ 最小1 少ないほど精度が上がるが遅くなる
	 * @param tester OUT 評価するフォント情報(typefaceなど)が設定されたPaintクラス。評価結果のフォントサイズが設定されて返される。最小2。
	 * @return サイズ
	 */
	public static final int fontTestVertical(String target, int defaultFontSize, 
			int width, int height, int resolution, Paint tester){
		boolean overflag;
		int ascentabs;
		int sz = target.length();
		int result = 0;
		
		for (int i = defaultFontSize; i >= MIN_FONT_SIZE; i = i - resolution){
			tester.setTextSize(i);
			FontMetrics fontMetrics = tester.getFontMetrics();
			
			//文字の高さを集計しオーバーしたらアウト
			overflag = false;
			result = 0;
			ascentabs = (int)(fontMetrics.ascent * -1);
				
			for (int x = 0; x < sz; x++) {
				result += ascentabs + fontMetrics.descent;
				if (height < result) {
					overflag = true;
					break;
				}
			}
			if (overflag) { continue; }
			
			//文字幅を一文字ずつ取得し、一つでもオーバーしたらアウト
			for (int x = 0; x < sz; x++) {
				if (tester.measureText(target.substring(x, x + 1)) > width) {
					overflag = true;
					break;
				}
			}
			if (overflag) { continue; }
			
			return i;
		}
		return MIN_FONT_SIZE;
	}
	
	/**
	 * カンマ区切り文字列をカンマごとに配列に分解し、さらにtrim()を掛けて返す
	 * @param data カンマ区切り文字列
	 * @return 分解したString配列
	 */
	public static final String[] parseCommaData(String data) {
		String[] strAry = data.split(",");
		for (int i = 0; i < strAry.length; i++) {
			strAry[i] = strAry[i].trim();
		}
		return strAry;
	}

	/**
	 * 現在の表示テキストを取得
	 * @return the serif
	 */
	public String getSerif() {
		return serif;
	}
	
	

}
