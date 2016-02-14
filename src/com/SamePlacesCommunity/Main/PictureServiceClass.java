package com.SamePlacesCommunity.Main;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;
import java.util.concurrent.Executor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;


public class PictureServiceClass {
	protected   Bitmap bm;
	protected   URL strURL;
	protected   String url;
//	private static Handler handler = new Handler();
	public PictureServiceClass(){
		this.bm = null;
		this.strURL = null;
		this.url = null;
	}
	
	public  void AnalysisPicture() throws IOException{
		            strURL = new URL(url);
					InputStream in = strURL.openStream();
					bm = BitmapFactory.decodeStream(in);
					Log.v("bitmap",bm.toString());
					in.close();
		
		
	}
//	  Thread runnable = new Thread() {
//		
//		@Override
//		public void run() {
//			try {
//				strURL = new URL(url);
//				InputStream in = strURL.openStream();
//				bm = BitmapFactory.decodeStream(in);
//				Log.v("bitmap",bm.toString());
//				in.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//		}
	};


