package com.example.listview;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 複行行表示可能なListのAdapterです。
 * 
 * templateはcom_multi_line_row.xmlにあるのでそちらも参照してください。

 *
 */
public class MultiLineListRowAdapter extends ArrayAdapter<MultiLineListRow> {
	
	private static final String TAG = "MultiLineListRowAdapter";
	
	/** displayed row */
	private List<MultiLineListRow> items;
	private Context context;
	/** viewをクリックしたときのlistener */
	private OnClickListener listener;
	
	private LayoutInflater inflater;
	
	private int resourceId;
	private Typeface typeface;
	private int currentPosition;
	/**
	 * @return the resourceId
	 */
	public int getResourceId() {
		return resourceId;
	}

	/**
	 * @param resourceId the resourceId to set
	 */
	public void setResourceId(int resourceId) {
		this.resourceId = resourceId;
	}

	public MultiLineListRowAdapter(Context context, int resourceId, List<MultiLineListRow> items,Typeface type) {
		this(context, resourceId, items, null,type);
	}
	
	public MultiLineListRowAdapter(Context context, final int resourceId, final List<MultiLineListRow> items, OnClickListener listener,Typeface type) {
		super(context, resourceId, items);
		this.context = context;
		this.resourceId = resourceId;
		this.items = items;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.listener = listener;
		this.typeface =type;
		//(new AdapterView.OnClickListener(){
		//@Override
	//public void onClick(View v){
		//TextView tv = (TextView)v;
		
		//TextView tv=(TextView)inflater.inflate(R.layout.com_multiline_row, null);
		//Toast.makeText(getContext(), "click:"+items.get(0).getText(0), Toast.LENGTH_LONG).show();
		//	}
        //});
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView; 
		// 初回はnulleがわたってくる。
		// 2回目以降は以前作成したものがわたってくるらしい。
		if (view == null) {
			// view = inflater.inflate(R.layout.com_multiline_row, null);
			view = inflater.inflate(resourceId, null);
		}
		view = populateView(position, view, parent);
		return view;
	}
	
	public View populateView (int position, View convertView, ViewGroup parent) {
		WindowManager wm = (WindowManager)context.getSystemService(context.WINDOW_SERVICE);
    	Display disp = wm.getDefaultDisplay();
    	float dWidth = disp.getWidth();
    	float dHeight = disp.getHeight();
    	float itemImgScale = dHeight/600;
    	currentPosition = position;
		Log.d(TAG, "populateView position [" + position + "]");
		MultiLineListRow item = items.get(position);
		if (item.getPrefixImageId() != null) {
			ImageView imageView = (ImageView) convertView.findViewById(R.id.row_prefix_image);
			imageView.setImageResource(item.getPrefixImageId());
		}
		if (!item.getBase64Image().equals(null)) {
			ImageView imageView = (ImageView) convertView.findViewById(R.id.row_prefix_image);
			byte[] base64 = android.util.Base64.decode(item.getBase64Image(),0);
			Bitmap b = BitmapFactory.decodeByteArray(base64,0,base64.length);
			Matrix matrix = new Matrix();
			matrix.postScale(itemImgScale, itemImgScale);
			imageView.setImageBitmap(Bitmap.createBitmap(b, 0, 0, b.getWidth(),b.getHeight(), matrix,false));
		}
		
		//ImageButton deleteButton = (ImageButton) convertView.findViewById(R.id.button_delete);
		ImageButton detailButton = (ImageButton) convertView.findViewById(R.id.button_detail);
		ImageButton useButton = (ImageButton) convertView.findViewById(R.id.button_use);
		ImageButton equButton = (ImageButton) convertView.findViewById(R.id.button_equ);
		int type = items.get(position).getItemType();
		
		//deleteButton.setBackgroundResource(R.drawable.item_del);
		//deleteButton.setTag(position);
		
		detailButton.setBackgroundResource(R.drawable.item_det);
		detailButton.setTag(position);
		if(useButton!=null){
			if(type==11){
				useButton.setBackgroundResource(R.drawable.item_use);
			}else{
				useButton.setBackgroundResource(R.drawable.itemsp);
			}
			useButton.setTag(position);
		}
		if(equButton!=null){
			if(type==21 ||type==31 ||type==41 ||type==51 ||type==61){
				equButton.setBackgroundResource(R.drawable.item_equ);
			}else{
				equButton.setBackgroundResource(R.drawable.itemsp);
			}
			equButton.setTag(position);
		}
		//if (item.getSuffixImageId() != null) {
		//	ImageView imageView = (ImageView) convertView.findViewById(R.id.row_suffix_image);
		//	imageView.setImageResource(item.getSuffixImageId());
		//}
		
		LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.row_list_area);
		// 全て消しているが・・・
		// パフォーマンスを考えると他の方法を探したほうがいいかも。
		// もっといい方法をご存知の方はご連絡下さい m(_ _)m
		layout.removeAllViews();
		Log.d(TAG, "PopulateTextView size [" + item.sieze() + "]");
		for (int i = 0, n = item.sieze(); i < n; i++) {
			TextView textView = new TextView(parent.getContext());
			textView.setText(item.getText(i));
			textView.setSingleLine(false);
			//textView.setTypeface(typeface);
			if (item.getTextSize(i) > 1) {
				textView.setTextSize(item.getTextSize(i));
			}
			Log.d(TAG, "Add TextView text [" + item.getText(i) + "]");
			layout.addView(textView, i);
		}
		if (listener != null) {
			convertView.setOnClickListener(listener);
			
		}
		
		return convertView;
	}
}

