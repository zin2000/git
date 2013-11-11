package com.example.listview;

import android.view.MotionEvent;
import android.view.View;

class FlickTouchListener implements View.OnTouchListener {
	private float lastTouchX;
	private float currentX;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {

        case MotionEvent.ACTION_DOWN:
            lastTouchX = event.getX();
            break;

        case MotionEvent.ACTION_UP:
            currentX = event.getX();
            if (lastTouchX < currentX) {
                //前に戻る動作
            }
            if (lastTouchX > currentX) {
                //次に移動する動作
            }
            break;

        case MotionEvent.ACTION_CANCEL:
            currentX = event.getX();
            if (lastTouchX < currentX) {
                 //前に戻る動作
            }
            if (lastTouchX > currentX) {
                 //次に移動する動作
            }
            break;
        }
        return true;
    }

}
