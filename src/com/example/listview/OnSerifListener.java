/** copyright(c) 2011 KobadroID **/
/** 改変可能・商用利用可能・許諾不要 **/
package com.example.listview;

import android.graphics.Canvas;
import android.view.View;
import com.example.listview.SerifDirector.PageInfo;
import com.example.listview.SerifDirector.Paragraph;

/**
 * セリフ再生イベントのインターフェイス
 */
public interface OnSerifListener {
	
	/**
	 * セリフ描画開始イベント
	 * @param v イベント発生元ビュー
	 * @param director イベント発生元ディレクタ
	 */
	public void onSerifStart(View v, SerifDirector director);
	
	/**
	 * 	セリフ1文字描画イベント
	 * @param v イベント発生元ビュー
	 * @param director イベント発生元ディレクタ
	 * @param currentPos 現在描画する文字がその段落の先頭から何文字目か。
	 * @param currentPage 現在のページ
	 * @param activeParagraph 現在アクティブな段落
	 */
	public void onSerifUpdate(View v, SerifDirector director, int currentPos, 
			PageInfo currentPage, Paragraph activeParagraph);
	
	/**
	 * セリフスキップイベント
	 * @param v イベント発生元ビュー
	 * @param director イベント発生元ディレクタ
	 * @param currentPage 現在のページ
	 * @param activeParagraph 現在アクティブな段落
	 */
	public void onSerifSkip(View v, SerifDirector director, 
			PageInfo currentPage, Paragraph activeParagraph);
	
	/**
	 * 段落・ページめくり待ちイベント
	 * @param v イベント発生元ビュー
	 * @param director イベント発生元ディレクタ
	 * @param currentPage 現在のページ
	 * @param activeParagraph 現在アクティブな段落
	 */
	public void onSerifWait(View v, SerifDirector director, 
			PageInfo currentPage, Paragraph activeParagraph);
	
	/**
	 * 次の段落イベント
	 * @param v イベント発生元ビュー
	 * @param director イベント発生元ディレクタ
	 * @param currentPage 現在のページ
	 * @param newParagraph 現在アクティブな段落
	 * @param oldParagraph 直前の段落
	 */
	public void onSerifNextParagraph(View v, SerifDirector director, 
			PageInfo currentPage, Paragraph newParagraph, Paragraph oldParagraph);
	
	/**
	 * 次ページイベント 
	 * @param v イベント発生元ビュー
	 * @param director イベント発生元ディレクタ
	 * @param newPage 現在のページ
	 * @param oldPage 直前のページ
	 */
	public void onSerifNextPage(View v, SerifDirector director, 
			PageInfo newPage, PageInfo oldPage);
	
	/**
	 * 前ページイベント
	 * @param v イベント発生元ビュー
	 * @param director イベント発生元ディレクタ
	 * @param newPage 現在のページ
	 * @param oldPage 直前のページ
	 */
	public void onSerifPrevious(View v, SerifDirector director, 
			PageInfo newPage, PageInfo oldPage);
	
	/**
	 * ページ変更イベント。ページが変更された時と、表示を開始した時に発生する。
	 * @param v イベント発生元ビュー
	 * @param director イベント発生元ディレクタ
	 * @param newPage 現在のページ
	 * @param oldPage 直前のページ
	 */
	public void onSerifPageChange(View v, SerifDirector director, 
			PageInfo newPage, PageInfo oldPage);
	
	/**
	 * 再生終了イベント
	 * @param v イベント発生元ビュー
	 * @param director イベント発生元ディレクタ
	 * @param currentPage 現在のページ
	 * @param activeParagraph 現在アクティブな段落
	 */
	public void onSerifStop(View v, SerifDirector director, 
			PageInfo currentPage, Paragraph activeParagraph);
	
	/**
	 * キャンバスエフェクトイベント。文字描画の進捗に合わせてカスタムエフェクトをかけることができる。
	 * @param v イベント発生元ビュー
	 * @param director イベント発生元ディレクタ
	 * @param currentPage 現在のページ
	 * @param activeParagraph 現在アクティブな段落
	 * @param c キャンバス
	 * @param progress 文字描画の進捗度合い 0(0%) ～ 1f(100%)
	 */
	public void onSerifCanvasEffect(View v, SerifDirector director, 
			PageInfo currentPage, Paragraph activeParagraph,
			Canvas c, float progress);
	
}
