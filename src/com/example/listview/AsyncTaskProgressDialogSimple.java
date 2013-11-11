package com.example.listview;

import android.os.AsyncTask;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.widget.TextView;

/*
 * AsyncTask<型1, 型2,型3>
 *
 *   型1 … Activityから非同期（スレッド）処理へ渡したい変数の型
 *          ※ Activityから呼び出すexecute()の引数の型
 *          ※ doInBackground()の引数の型
 *
 *   型2 … 進捗度合を表示する時に利用したい型
 *          ※ onProgressUpdate()の引数の型
 *
 *   型3 … バックグラウンド処理完了時に受け取る型
 *          ※ doInBackground()の戻り値の型
 *          ※ onPostExecute()の引数の型
 *
 *   ※ それぞれ不要な場合は、Voidを設定すれば良い
 */
public class AsyncTaskProgressDialogSimple extends AsyncTask<Void, Void, String> {

    private Activity       m_Activity;
    public  ProgressDialog m_ProgressDialog;

    /*
     * コンストラクタ
     *
     *  @param activity: 読み出し元Activity
     */
    public AsyncTaskProgressDialogSimple(Activity activity) {

        // 呼び出し元のアクティビティを変数へセット
        this.m_Activity = activity;
    }

    /*
     * 実行前の事前処理
     */
    @Override
    protected void onPreExecute() {

        // プログレスダイアログの生成
        this.m_ProgressDialog = new ProgressDialog(this.m_Activity);

        // プログレスダイアログの設定
        this.m_ProgressDialog.setMessage("読み込み中...");  // メッセージをセット

        // プログレスダイアログの表示
        this.m_ProgressDialog.show();

        return;
    }

    /*
     * バックグラウンドで実行する処理
     *
     *  @param params: Activityから受け渡されるデータ
     *  @return onPostExecute()へ受け渡すデータ
     */
    @Override
    protected String doInBackground(Void... params) {

        String ret = "";

        try {
            // Sleep処理（例：HTTP通信）
            Thread.sleep(1000);

            // Sleep処理（例：HTTP解析／画像変換とか）
            Thread.sleep(1000);

            // 取得してきた文字列・画像などをreturnでonPostExecuteへ渡す
            ret = "読み込み完了！";

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return ret;
    }

    /*
     * メインスレッドで実行する処理
     *
     *  @param param: doInBackground()から受け渡されるデータ
     */
    @Override
    protected void onPostExecute(String result) {

        // 読み出し元Activityに設置してあるTextViewを取得
        //TextView textView = (TextView)this.m_Activity.findViewById(R.id.textView);

        // TextViewの文字列をセット
        //textView.setText(result);

        // プログレスダイアログを閉じる
        if (this.m_ProgressDialog != null && this.m_ProgressDialog.isShowing()) {
            this.m_ProgressDialog.dismiss();
        }

        return;
    }

    /*
     * キャンセル時の処理
     */
    @Override
    protected void onCancelled() {
        super.onCancelled();

        Log.v("AsyncTaskProgressDialogSimpleThread", "onCancelled()");

        if (this.m_ProgressDialog != null) {

            Log.v("this.m_ProgressDialog.isShowing()", String.valueOf(this.m_ProgressDialog.isShowing()));

            // プログレスダイアログ表示中の場合
            if (this.m_ProgressDialog.isShowing()) {

                // プログレスダイアログを閉じる
                this.m_ProgressDialog.dismiss();
            }
        }

        return;
    }
}
