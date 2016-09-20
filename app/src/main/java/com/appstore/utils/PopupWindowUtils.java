package com.appstore.utils;


import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView.FindListener;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.appstore.R;

public class PopupWindowUtils {
	
	private Context context;
	
	private int screenWidth;
	
	private int screenHeigh;
	
	
	private PopupWindow popupWindow;
	
	View view;
	View downview;
   

	public PopupWindowUtils(Context context, int screenWidth, int screenHeigh,
			View view, View downview) {
		super();
		this.context = context;
		this.screenWidth = (int) (screenWidth*0.6);
		this.screenHeigh = screenHeigh;
		this.view = view;
		this.downview = downview;
		
		init();
	} 

	
	
	public void init(){
		
		 popupWindow = new PopupWindow(view, screenWidth, screenHeigh);
		 popupWindow.setFocusable(true);
			 ColorDrawable cd = new ColorDrawable(-0000);
			 popupWindow.setBackgroundDrawable(cd);  
			 
			// popupWindow.showAsDropDown(down);
			 popupWindow.setFocusable(true);   
			 popupWindow.setOutsideTouchable(true);
			 popupWindow.showAtLocation(downview,Gravity.LEFT|Gravity.TOP, 0,300);
			// popupWindow.setAnimationStyle(R.style.PopupAnimation);
			 popupWindow.update();
			 
			//setting popupWindow d�����ʧ
			 popupWindow.setTouchInterceptor(new View.OnTouchListener() {  
		         @Override
				public boolean onTouch(View v, MotionEvent event) {  
		             /****   ��������popupwindow���ⲿ��popupwindowҲ����ʧ ****/  
		             if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {  
		            	 popupWindow.dismiss();  
		                 return true;   
		             }  
		             return false;  
		         }  
			 });
		
	}



}
