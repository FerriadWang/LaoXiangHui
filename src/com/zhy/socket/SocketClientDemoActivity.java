package com.zhy.socket;
import java.io.DataInputStream;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.SamePlacesCommunity.Main.UserServiceActivity;
import com.example.thirdapp.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


public class SocketClientDemoActivity extends Activity
{
	//锟斤拷锟斤拷锟斤拷锟�
    private String reMsg=null;
	Thread thread=null;
	private EditText	MessageText;
	private Button		MessageButton;//锟斤拷钮
    private ListView talkView;
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
	String str,date,province,context=null;
	Intent intent = new Intent();
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
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
		setContentView(R.layout.activity_client);
		
		
		//锟斤拷锟绞碉拷锟�
		MessageButton = (Button)findViewById(R.id.MessageButton);
		
		MessageText=(EditText)findViewById(R.id.MessageText);
		talkView = (ListView) findViewById(R.id.list);
		//锟斤拷锟斤拷
		connect();
		//锟斤拷梅锟斤拷锟斤拷锟斤拷锟较�
		MessageButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{	
				str = MessageText.getText().toString().trim();
                N=true;
				try {
//					if(UserServiceActivity.userInformation.id!=null&&UserServiceActivity.userInformation.name!=null&&UserServiceActivity.userInformation.province!=null&&UserServiceActivity.userInformation.city!=null)
//					{ID=UserServiceActivity.userInformation.id;
//					PROVINCE = UserServiceActivity.userInformation.province;
//					CITY = UserServiceActivity.userInformation.city;
//					NAME = UserServiceActivity.userInformation.name;
//					}
					dos.writeUTF(NAME+"#"+ID+"#"+PROVINCE+"#"+CITY+"#"+LOCATION+"#"+str);					
					dos.flush();					
					MessageText.setText("");

                }catch (SocketTimeoutException  e) {
                      System.out.println("锟紹锟接筹拷锟絩锟斤拷锟斤拷锟斤拷锟斤拷未锟絖锟斤拷锟斤拷IP锟絜锟絗");                      
                      e.printStackTrace();
                  } catch (IOException e) {
                    // TODO Auto-generated catch block
                      System.out.println("锟紹锟接筹拷锟絩锟斤拷锟斤拷锟斤拷锟斤拷未锟絖锟斤拷锟斤拷IP锟絜锟絗");                    
                      e.printStackTrace();
                }
			}
		});
	}

	public void connect() {
        try {
            s = new Socket("10.1.7.4",7654);
            if(s.isConnected()){
                dos = new DataOutputStream (s.getOutputStream());
                dis = new DataInputStream (s.getInputStream());
             

               // dos.writeUTF("New::Land"+"#"+ID+"#"+province);
                /**
                 * 锟斤拷锟斤拷锟角关硷拷锟斤拷锟节此猴拷时8h+
                 * 原锟斤拷锟斤拷 锟斤拷锟竭程诧拷锟斤拷直锟接革拷锟斤拷UI
                 * 为锟剿ｏ拷锟斤拷锟斤拷锟斤拷要通锟斤拷Handler锟斤拷锟斤拷锟酵ㄖ拷锟斤拷叱锟経i Thread锟斤拷锟斤拷锟铰斤拷锟芥。
                 * 
*/
               
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

 
                    if (reMsg.startsWith("CONVERSATION")) {
                    
                    	String s1[]=reMsg.split("#");
                    	ID=s1[1];
                    	date=s1[2];
                    	context=s1[3];
                    	
                    	F=true;
                    	newMessage = new ChatMsgEntity(ID+"说:", date, context, R.layout.list_say_me_item);

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
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
        	switch (msg.what) {
            case 0x1981:
           while(N||F){           	
        	list.add(newMessage);
            talkView.setAdapter(new ChatMsgViewAdapter(SocketClientDemoActivity.this, list));
            N=false;
            F=false;          
            reMsg=null;          
           }			
                break;
            }
        }
    };
    public void onPause(){
    	super.onPause();
    }
   
	   	
    public void onStop(){
    	
    	try {
			dos.writeUTF("EXiT");

	
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onStop();
	}   	
 public void onDestroy(){    
		try {
			dos.writeUTF("EXiT");			
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onDestroy();
	}
}
