package com.SamePlacesCommunity.Main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.Toast;

import com.SamePlacesCommunity.Main.UserServiceActivity.MyHandler;
import com.example.thirdapp.R;
import com.renn.rennsdk.RennClient;
import com.renn.rennsdk.RennExecutor.CallBack;
import com.renn.rennsdk.RennResponse;
import com.renn.rennsdk.exception.RennException;
import com.renn.rennsdk.param.ListFeedParam;
import com.renn.rennsdk.param.PutFeedParam;



@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class FeedServiceActivity extends Activity implements OnClickListener{
    
	private MyHandler myHandler ;
    private ImageButton getFeedBtn;
//    private TextView textView;
    private RennClient rennClient;
    private ProgressDialog mProgressDialog;
    private static Map<String , Object> map;
    private ListView Listview;
    private static List<Map<String,Object>> list = new ArrayList<Map<String , Object>>();
    
	
	@SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
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
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedservice);
        myHandler = new MyHandler();
        initView();
        
    }
	
	 class MyHandler extends Handler{
	    	public MyHandler(){
	    		
	    	}
	    	public MyHandler(Looper L){
	    		super(L);
	    	}
	    	@Override
	    	
	    	public void handleMessage(Message msg){
	    		super.handleMessage(msg);
	    		switch (msg.what){
	    		case 1:
	    			addView();
	    			
	    			break;
	    		
	    	}
	      }
	    }
	
	

	

    private void initView(){
        rennClient = RennClient.getInstance(this);
        rennClient.setScope("read_user_feed");
        
       
        getFeedBtn = (ImageButton) findViewById(R.id.getFeed);
        getFeedBtn.setOnClickListener(this);
//        textView = (TextView) findViewById(R.id.textView);
        Listview = (ListView)findViewById(R.id.listView); 
    }

    public void addView() {
    	SimpleAdapter adapter = new SimpleAdapter(
 			   this,getData(),
 			   R.layout.new_feed_adapter_listview,
				   new String[]{"USERNAMES","MESSAGES","COMMENTSDIS","AVATARS","THUMBNAILS"},
				   new int[]{R.id.USERNAMES,R.id.MESSAGES,R.id.COMMENTSDIS,R.id.AVATARS,R.id.THUMBNAILS}
 			   );
 	
 	adapter.setViewBinder(new ViewBinder(){

			@Override
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {
				if(view instanceof ImageView && data instanceof Bitmap){
					ImageView iv = (ImageView) view;
					iv.setImageBitmap((Bitmap)data);
					return true;
				}else{
				// TODO Auto-generated method stub
				return false;}
			}
 		
 	});
 	
 	
 	Log.v("list",Listview.toString());
 	Listview.setAdapter(adapter);
		// TODO Auto-generated method stub
		
	}

	@Override
    public void onClick(View v) {
        switch (v.getId()) {

            
            
            
            
           case R.id.getFeed:
            	ListFeedParam param2 = new ListFeedParam();
            	param2.setFeedType("ALL");
            	param2.setPageSize(50);
            	param2.setPageNumber(1);
            	if (mProgressDialog == null) {
                    mProgressDialog = new ProgressDialog(FeedServiceActivity.this);
                    mProgressDialog.setCancelable(true);
                    mProgressDialog.setTitle("请等待");
                    mProgressDialog.setIcon(android.R.drawable.ic_dialog_info);
                    mProgressDialog.setMessage("正在获取新鲜事");
                    mProgressDialog.show();
                }
            	try {
                    rennClient.getRennService().sendAsynRequest(param2, new CallBack() {    
                        
                        @Override
                        public void onSuccess(RennResponse response) {
                        	
                        	AnalysisData(response);
                        	
//                        	textView.setText(databuffer);
                        	Toast.makeText(FeedServiceActivity.this, "获取成功", Toast.LENGTH_SHORT).show();	  
                            if (mProgressDialog != null) {
                                mProgressDialog.dismiss();
                                mProgressDialog = null;
                            }                           
                        }
                        
                        @Override
                        public void onFailed(String errorCode, String errorMessage) {
//                            textView.setText(errorCode+":"+errorMessage);
                            Toast.makeText(FeedServiceActivity.this, "获取失败", Toast.LENGTH_SHORT).show();
                            if (mProgressDialog != null) {
                                mProgressDialog.dismiss();
                                mProgressDialog = null;
                            }                            
                        }
                    });
                } catch (RennException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            	

            default:
                break;
        }
        
    }
    
    
    
    
    

	protected StringBuffer AnalysisData(RennResponse response) {
		
		String data = response.toString();
    	StringBuffer databuffer =new StringBuffer("");
    	
    	data = UserServiceActivity.dataArrayTrans(data);
    	Log.v("这里是新鲜事", data);
 
    	try {
			JSONObject bigData = new JSONObject(data);
			JSONArray bigArray = bigData.getJSONArray("response");
			
			int length = bigArray.length();
			
            String id []= new String[length]; 
			
		    Long[] USERIDS = new Long[length];
		    
		    String[] AVATARURL = new String[length];
		    
		    Bitmap[] AVATAR = new Bitmap[length];
			
			String[] USERNAMES = new String[length];
			
			String[] MESSAGES = new String[length];
			
			String[]   COMMENTSDIS = new String[length];
			
			String[][] COMMENTS = new String[length][2];
			
			String[][] COMMENTNAMES = new String[length][2];
			
		    String[]  THUMBNAILURL = new String[length];
			
			Bitmap[]  THUMBNAIL = new Bitmap[length];
			
			Log.v("长度",bigArray.length()+"");
			
		   
				 
			
for(int x=0;x<bigArray.length();x++){
		    
			String flag = ((JSONObject) bigArray.get(x)).getString("sourceUser");
			String comments = ((JSONObject)bigArray.get(x)).getString("comments");
			String thumbnailUrl = ((JSONObject)bigArray.get(x)).getString("thumbnailUrl");
			
			
				
				
				if(flag=="null"||flag.trim().length()<0||flag==""){
					//若判断为空那么可以不显示
					((JSONObject) bigArray.get(x)).put("sourceUser","未知用户");
					USERNAMES[x] = "未知用户";
					MESSAGES[x]="未知信息";
					USERIDS[x] = (long)00000000;
					THUMBNAILURL[x]="NONE";
					COMMENTSDIS[x]="NONE";
					
							
					continue;
		
                }else{
                //赋值正文	
                	
                	
                id[x] = ((JSONObject)bigArray.get(x)).getJSONObject("sourceUser").getString("id");
                //获取用户ID
				USERIDS[x]= Long.parseLong(id[x]);
				
				//头像部分
				PictureServiceClass entity = new PictureServiceClass();
				JSONObject avatar = (JSONObject) ((JSONObject)bigArray.get(x)).getJSONObject("sourceUser").getJSONArray("avatar").get(0);
				AVATARURL[x] = avatar.getString("url");
				
				
				
				
				try {
					entity.url = AVATARURL[x];
					entity.AnalysisPicture();
					AVATAR[x] = entity.bm;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//用户图片显示部分
				if(thumbnailUrl != "null"){
					try {
						entity.url = thumbnailUrl;
						entity.AnalysisPicture();
						THUMBNAIL[x] = entity.bm;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
				
				
//				handler.sendEmptyMessage(0);
//				}
//				}.start();
				
				//获取名字
				USERNAMES[x] = ((JSONObject)bigArray.get(x)).getJSONObject("sourceUser").getString("name");
				
				//获取用户输出信息
				if(((JSONObject)bigArray.get(x)).getString("message")!="null"&&((JSONObject)bigArray.get(x)).getJSONObject("resource").getString("content")!="null"&&((JSONObject)bigArray.get(x)).getString("message")!=""){
					
					MESSAGES[x] = ((JSONObject)bigArray.get(x)).getString("message")+"\n"+"\n"+((JSONObject)bigArray.get(x)).getJSONObject("resource").getString("content");
				
				}else if(((JSONObject)bigArray.get(x)).getString("message")=="null"&&((JSONObject)bigArray.get(x)).getJSONObject("resource").getString("content")!="null"){
				
					MESSAGES[x] = ((JSONObject)bigArray.get(x)).getJSONObject("resource").getString("content");
				}
				else if(((JSONObject)bigArray.get(x)).getString("message")!="null"&&((JSONObject)bigArray.get(x)).getJSONObject("resource").getString("content")=="null")
				
					MESSAGES[x] = ((JSONObject)bigArray.get(x)).getString("message");
				
				//评论部分
				if(comments!="null"){
					JSONArray CMArray = ((JSONObject)bigArray.get(x)).getJSONArray("comments");
					int lengtharray = CMArray.length();
					
					for(int y=0;y<lengtharray;y++){
						//这里在循环评论
						COMMENTS[x][y] = ((JSONObject)CMArray.get(lengtharray-y-1)).getString("content");
						COMMENTNAMES[x][y] = ((JSONObject)CMArray.get(lengtharray-y-1)).getJSONObject("author").getString("name");
						COMMENTSDIS[x] = COMMENTNAMES[x][y]+":"+COMMENTS[x][y];
					}
					
				}else{
					COMMENTSDIS[x]="";
				}
				
				
				
//				if(thumbnailUrl!="null"){
//					
//					THUMBNAILURL[x] = thumbnailUrl;
//					try {
//					THUMBNAIL[x]=Downloadbitmap.getImage(THUMBNAILURL[x]);
//					} catch (Exception e) {
//						
//						e.printStackTrace();
//					}
//				}else{
//					THUMBNAILURL[x] = "null";
//				}
				
				
				
				//将得到的值加入textview
				setData(USERNAMES[x], MESSAGES[x], COMMENTSDIS[x], AVATAR[x],THUMBNAIL[x]);
				
				
				
			   //在logcat中打印获得的信息
				Log.v("ID",id[x]);
				Log.v("USERNAMES",USERNAMES[x]);
				Log.v("MESSAGES",MESSAGES[x]);
				Log.v("AVATARURL",AVATARURL[x]);
				Log.v("AVATAR",AVATAR[x].toString());
				
				Log.v("","\n");
				Log.v("THUMBNAILURL",thumbnailUrl);
				 
                }
			}
		} catch (
		JSONException e) {
			
			e.printStackTrace();
		}
    	
    	
    	
    	/*SimpleAdapter adapter = new SimpleAdapter(
    			   this,getData(),
    			   R.layout.new_feed_adapter_listview,
				   new String[]{"USERNAMES","MESSAGES","COMMENTSDIS","AVATARS","THUMBNAILS"},
				   new int[]{R.id.USERNAMES,R.id.MESSAGES,R.id.COMMENTSDIS,R.id.AVATARS,R.id.THUMBNAILS}
    			   );
    	
    	adapter.setViewBinder(new ViewBinder(){

			@Override
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {
				if(view instanceof ImageView && data instanceof Bitmap){
					ImageView iv = (ImageView) view;
					iv.setImageBitmap((Bitmap)data);
					return true;
				}else{
				// TODO Auto-generated method stub
				return false;}
			}
    		
    	});
    	
    	
    	Log.v("list",Listview.toString());
    	Listview.setAdapter(adapter);
    */
    	FeedServiceActivity.this.myHandler.sendEmptyMessage(1);
       return databuffer;
    }

	
	
	private List<Map<String , Object>> getData() {
		return list;
	}
	
	private void setData(String USERNAMES,String MESSAGES,String COMMENTSDIS,Bitmap AVATAR,Bitmap THUMBNAIL){
		map = new HashMap<String, Object>();
		map.put("USERNAMES",USERNAMES);
		map.put("MESSAGES", MESSAGES);
		map.put("COMMENTSDIS", COMMENTSDIS);
		map.put("AVATARS", AVATAR);
		map.put("THUMBNAILS", THUMBNAIL);
		list.add(map);
    }
	

}
