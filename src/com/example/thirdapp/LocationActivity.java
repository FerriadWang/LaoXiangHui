package com.example.thirdapp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.zhy.socket.ChatMsgEntity;
import com.zhy.socket.ChatMsgViewAdapter;
import com.zhy.socket.SocketClientDemoActivity;
/**
 * 此demo用来展示如何结合定位SDK实现定位，并使用MyLocationOverlay绘制定位位置
 * 同时展示如何使用自定义图标绘制并点击时弹出泡泡
 *
 */
public class LocationActivity extends Activity {
	private Handler handler;
	private String reMsg=null;
	Thread thread=null;
	String Returndata = null;
	Socket s=null;
	PrintWriter out=null;
	DataInputStream dis = null;
	DataOutputStream dos = null;
	String ID="320908234";
	String NAME = "wakeup";
	String PROVINCE = "四川";
	String CITY = "乐山";
	String LOCATION = "保定";
	private ArrayList<ChatMsgEntity> list = new ArrayList<ChatMsgEntity>();
	ChatMsgEntity newMessage = null;
	boolean N,F,OnLineBoolean=false;
	String str,date,context=null;
	Intent intent = new Intent();
	String city ;
	String province;
	// 定位相关
	LocationClient mLocClient;
	LocationData locData = null;
	public MyLocationListenner myListener = new MyLocationListenner();
	
	//定位图层
	locationOverlay myLocationOverlay = null;
	//弹出泡泡图层
	
	private TextView  popupText = null;//泡泡view
	private View viewCache = null;
	

	//UI相关
	OnCheckedChangeListener radioButtonListener = null;
	private ImageButton      RequestButton;
	private ImageButton requestLocButton = null;
	private TextView information = null;
	boolean isRequest = false;//是否手动触发请求定位
	boolean isFirstLoc = true;//是否首次定位
	
    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		.detectDiskReads()
		.detectDiskWrites()
		.detectNetwork() // 这里可以替换为detectAll() 就包括了磁盘读写和网络I/O
		.penaltyLog() //打印logcat，当然也可以定位到dropbox，通过文件保存相应的log
		.build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
		.detectLeakedSqlLiteObjects() //探测SQLite数据库操作
		.penaltyLog() //打印logcat
		.penaltyDeath()
		.build()); 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locationoverlay);
        CharSequence titleLable="定位功能";
        setTitle(titleLable);
        requestLocButton = (ImageButton)findViewById(R.id.button1);
        RequestButton = (ImageButton)findViewById(R.id.button2);
        information = (TextView)findViewById(R.id.information);
        
        OnClickListener btnClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//手动定位请求
//				
				requestLocClick();
			}
		};
	    requestLocButton.setOnClickListener(btnClickListener);
	    
	    
        OnClickListener FriendLocRequest = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//手动定位请求
				connect();
				   try {
//						
						dos.writeUTF(NAME+"#"+PROVINCE+"#"+LOCATION);					
						dos.flush();					
						

		            }catch (SocketTimeoutException  e) {
		                  System.out.println("锟紹锟接筹拷锟絩锟斤拷锟斤拷锟斤拷锟斤拷未锟絖锟斤拷锟斤拷IP锟絜锟絗");                      
		                  e.printStackTrace();
		              } catch (IOException e) {
		                // TODO Auto-generated catch block
		                  System.out.println("锟紹锟接筹拷锟絩锟斤拷锟斤拷锟斤拷锟斤拷未锟絖锟斤拷锟斤拷IP锟絜锟絗");                    
		                  e.printStackTrace();
		            }
				
			}
		};
		RequestButton.setOnClickListener(FriendLocRequest);
	    
		handler = new Handler() {
	        public void handleMessage(Message msg) {
	        	switch (msg.what) {
	            case 0x1981:
	           information.setText(Returndata);		
	                break;
	            }
	        }
	    };
       //定位初始化
        mLocClient = new LocationClient( this );
        mLocClient.setAK("3c9133fbd1b41473b42be28f090d61bc");
        locData = new LocationData();
        mLocClient.registerLocationListener( myListener );
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);//打开gps
        option.setCoorType("bd09ll");     //设置坐标类型
        option.setScanSpan(5000);
        option.setAddrType("all");
        mLocClient.setLocOption(option);
        
//        mLocClient.requestLocation();
        
        
       
    
		
    }
    private void connect() {
		// TODO Auto-generated method stub
    	try {
            s = new Socket("10.1.7.4",7654);
            if(s.isConnected()){
                dos = new DataOutputStream (s.getOutputStream());
                dis = new DataInputStream (s.getInputStream());
             

                thread = new Thread(null, doThread, "Message");
                thread.start();


            }
         }catch (UnknownHostException e) {
              System.out.println("锟紹锟斤拷失锟斤拷");
         
              e.printStackTrace();
          }catch (SocketTimeoutException  e) {
              System.out.println("锟紹锟接筹拷锟絩锟斤拷锟斤拷锟斤拷锟斤拷未锟絖锟斤拷锟斤拷IP锟絜锟絗");
              
              e.printStackTrace();
         }catch (IOException e) {
              System.out.println("锟紹锟斤拷失锟斤拷");
              e.printStackTrace();
         }
	}
    private Runnable doThread = new Runnable() {
        public void run() {
            System.out.println("running!");
            ReceiveMsg();  
        }
    };   
    
    private void ReceiveMsg() {
        if (true) {
            try {
                while ((reMsg = dis.readUTF()) != null) {
                    System.out.println(reMsg);

 
                    if (reMsg.startsWith("FRIEND")) {
                    
                    	String s1[]=reMsg.split("#");
                    	 Returndata = s1[1];
                    	 try {
                             Message msgMessage = new Message();                           
                             msgMessage.what = 0x1981;
                             handler.sendMessage(msgMessage);                          
                             Thread.sleep(100);
                         } catch (InterruptedException e) {
                             // TODO Auto-generated catch block
                             e.printStackTrace();
                         }
                    	
                    	
                    	

                       
                    }
                }
            } catch (SocketException e) {
                // TODO: handle exception
                System.out.println("exit!");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }
    
    
    
	/**
     * 手动触发一次定位请求
     */
    public void requestLocClick(){
    	if(mLocClient.isStarted()==false){
    	mLocClient.start();
    	isRequest = true;
        mLocClient.requestLocation();
        Toast.makeText(LocationActivity.this, "正在定位……", Toast.LENGTH_SHORT).show();}
    	else{
    		mLocClient.start();
        	isRequest = true;
            mLocClient.requestLocation();
            Toast.makeText(LocationActivity.this, "正在定位……", Toast.LENGTH_SHORT).show();
    	}
    }
  
	/**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {
    	
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return ;
            //Log.v("lacation",location.getAddrStr());
           
           
            LOCATION = location.getCity();
            PROVINCE = location.getProvince();
            Toast.makeText(getApplicationContext(), "原来你在"+PROVINCE+LOCATION+"呀！", 3000);
    
            //如果不显示定位精度圈，将accuracy赋值为0即可
            locData.accuracy = location.getRadius();
            locData.direction = location.getDerect();
            //更新定位数据
            //myLocationOverlay.setData(locData);
            //更新图层数据执行刷新后生效
          
            //是手动触发请求或首次定位时，移动到定位点
            if (isRequest || isFirstLoc){
            	//移动地图到定位点
//                mMapController.animateTo(new GeoPoint((int)(locData.latitude* 1e6), (int)(locData.longitude *  1e6)));
                isRequest = false;
            }
            //首次定位完成
            isFirstLoc = false;
         
        }
        
        public void onReceivePoi(BDLocation poiLocation) {
            if (poiLocation == null){
                return ;
            }
        }
    }
    
    //继承MyLocationOverlay重写dispatchTap实现点击处理
  	public class locationOverlay extends MyLocationOverlay{

  		public locationOverlay(MapView mapView) {
  			super(mapView);
  			// TODO Auto-generated constructor stub
  		}
  		@Override
  		protected boolean dispatchTap() {
  			// TODO Auto-generated method stub
  			//处理点击事件,弹出泡泡
  		
  			return true;
  		}
  		
  	}
    @Override
    protected void onStop(){
    	super.onStop();
    	if (mLocClient != null)
            mLocClient.stop();
    }
    @Override
    protected void onPause() {
        
        super.onPause();
    }
    
    @Override
    protected void onResume() {
       
        super.onResume();
    }
    
    @Override
    protected void onDestroy() {
    	//退出时销毁定位
        if (mLocClient != null)
            mLocClient.stop();
        
      
        super.onDestroy();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	
    	
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

}




