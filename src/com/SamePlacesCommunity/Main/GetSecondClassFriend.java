package com.SamePlacesCommunity.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.thirdapp.R;

import com.renn.rennsdk.RennClient;
import com.renn.rennsdk.RennResponse;
import com.renn.rennsdk.RennExecutor.CallBack;
import com.renn.rennsdk.exception.RennException;
import com.renn.rennsdk.param.BatchUserParam;
import com.renn.rennsdk.param.ListUserFriendParam;

import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class GetSecondClassFriend extends Activity {
	private String id;
	private String province;
	private int count=1;
	private boolean flag = true;
	private TextView textView ;
	private MyHandler myHandler;
	private RennClient rennClient;
	private ProgressDialog mProgressDialog;
	private static  int length;
	private static  Long[] USERIDS;
	private static ListUserFriendParam param4;
	private ListView listview ;
	private static Map<String , Object> map;
	private static List<Map<String, Object>> list =new ArrayList<Map<String,Object>>();
   
	
	
	 @Override  
	    protected void onStop() {  
	        super.onStop();  
	        Log.e("onstop", "start onStop~~~"); 
	        
	        count = 0;
	      //退出时销毁定位
	      
	    }   
	 @Override
	    protected void onDestroy() {
	    	//退出时销毁定位
	        
		 list.clear();
	        super.onDestroy();
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
	 
	
	 
	@Override
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_get_second_class_friend);
		
		listview = (ListView)findViewById(R.id.listView);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		id = bundle.getString("id");
		province = bundle.getString("province");
//		textView.setText(id);
		
		myHandler = new MyHandler();
		
		 rennClient = RennClient.getInstance(this);
		
		param4 = new ListUserFriendParam();
    	
    	param4.setUserId(Long.parseLong(id));
    	
    	param4.setPageSize(100);
    	
    	param4.setPageNumber(count);
    	
    	if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(GetSecondClassFriend.this);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setTitle("请稍等片刻...");
            mProgressDialog.setIcon(android.R.drawable.ic_dialog_info);
            mProgressDialog.setMessage("正在获取好友信息");
            mProgressDialog.show();
            mProgressDialog.setCancelable(false);
        }
    	
    	
    	getSamePlaceFriend(param4,province);
		
		
	}
	
	
	 private void addView() {
		 SimpleAdapter adapterGetUserFriends = new SimpleAdapter(this, getUserFriendData(), R.layout.get_user_friends_display_adapter, new String[]{"USERNAME","PROVINCES","CITIES"}, new int[]{R.id.USERNAME,R.id.PROVINCES,R.id.CITIES});

		 listview.setAdapter(adapterGetUserFriends);
		 
		 listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				
				//获得选中项的HashMap对象
				HashMap<String, String> map = (HashMap<String, String>)listview.getItemAtPosition(position);
				String id = map.get("ID");
				Log.v("id",id);
				String URL= "http://mt.renren.com/profile/";
//				intent.setClass(GetSecondClassFriend.this, GetSecondClassFriend.class);
				URL = URL+id;
				Uri uri = Uri.parse(URL);
				Intent i = new Intent(Intent.ACTION_VIEW,uri); 
				
				startActivity(i);
				Log.v("ID", id);
				
				// TODO Auto-generated method stub
				
			}
			 
		});
		// TODO Auto-generated method stub
		
	}
	private void getSamePlaceFriend(ListUserFriendParam param,String province) {
		 try {
			   final String provincetemp = province;
			   param.setPageNumber(count);
	            rennClient.getRennService().sendAsynRequest(param, new CallBack() {    
	                
	                @Override
	                public void onSuccess(RennResponse response) {
	                	
	                	  StringBuffer databuffer = new StringBuffer("");
	                	  
	                 	 try {
	                 		 
	                    	String data = response.toString();
	                    	data = UserServiceActivity.dataArrayTrans(data);
	                //转换成ArrayJSON数据 
	                 		
	                //转成JSON数据		
								JSONObject jsonObject = new JSONObject(data);
							    JSONArray responseJSON = jsonObject.getJSONArray("response");
							   
							    length = responseJSON.length();
	              		       USERIDS = new Long[length];
	              		    
	              		      
				         
	            for(int x=0;x<responseJSON.length();x++){
	                // Log.v("the number of array ",responseJSON.length()+"");
				        	     
	                		 String id [] = new String[responseJSON.length()];
	                		 String name[] = new String[responseJSON.length()];
	                		 
						        
	                		 
	                //依次取出ID、Province、和City
	                		 id[x] = ((JSONObject)responseJSON.get(x)).getString("id");
	                		 name[x] =  ((JSONObject)responseJSON.get(x)).getString("name");
	                		 
	                		 USERIDS[x] = Long.parseLong(id[x]);
	                		
								
						//把ID给StringBuffer								
								databuffer.append("id"+id[x]+"\n"+"name"+name[x]+"\n");
								
							    
						        }
								
//				        textView.setText(databuffer);
				       
						
	                 	 } catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} 
	                	  
	                   
	                    BatchUserParam param1 = new BatchUserParam();
	                    Long[] userds = USERIDS.clone();
	                    	//{(long)469044509,(long) 234482808}
	                    ;
	                    		
	                    		
	                    param1.setUserIds(userds);
	                    
	                    try {
	                    	
	                        rennClient.getRennService().sendAsynRequest(param1, new CallBack() {    
	                            
	                            @Override
	                            public void onSuccess(RennResponse response) {
	                                     
	                           StringBuffer databuffer = new StringBuffer("");

	                            		 
	                            		 databuffer = AnalysisData(response, databuffer,provincetemp);
	                               		
//	                            		 textView.setText(databuffer);
                                           
	                                
	                                Toast.makeText(GetSecondClassFriend.this, "获取成功", Toast.LENGTH_SHORT).show();  
	                                if (mProgressDialog != null) {
	                                    mProgressDialog.dismiss();
	                                    mProgressDialog = null;
	                                }                           
	                            }
	                            
	                           

								@Override
	                            public void onFailed(String errorCode, String errorMessage) {

	                              
	                                Toast.makeText(GetSecondClassFriend.this, "获取失败了，再试试吧", Toast.LENGTH_SHORT).show();
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
	                    
	                }
	                
	                @Override
	                public void onFailed(String errorCode, String errorMessage) {
	                    textView.setText("");
	                    Toast.makeText(GetSecondClassFriend.this, "获取失败了，再试试吧", Toast.LENGTH_SHORT).show();
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
		 
		// TODO Auto-generated method stub
		
	}

	private StringBuffer AnalysisData(RennResponse response,StringBuffer databuffer,String Province){
	    	
    	 try {
    		 
    		 list.clear();
    		 
       		 String data = response.toString();
       		 
       		data = UserServiceActivity.dataArrayTrans(data);
       		
    		Log.v("data", data);
    		
			JSONObject jsonObject = new JSONObject(data);
			
		    JSONArray responseJSON = jsonObject.getJSONArray("response");
		    
		    Log.v("length",responseJSON.length()+" ");
       		 
       		 String province []= new String[responseJSON.length()];
       		 
        	 String city [] = new String[responseJSON.length()];
       		 
       		 String id [] = new String[responseJSON.length()];
       		 
       		 String name[] = new String[responseJSON.length()];
        	//转换成JSON数据 
    		
		    
     for(int x=0;x<responseJSON.length();x++){
//		    Log.v("the number of array ",responseJSON.length()+"");
    	
	        
   		 id[x] = ((JSONObject)responseJSON.get(x)).getString("id");
   		 
   		 name[x] = ((JSONObject)responseJSON.get(x)).getString("name");
   		
   if(((JSONObject)responseJSON.get(x)).getString("basicInformation")!="null" && 
		 ((JSONObject)responseJSON.get(x)).getJSONObject("basicInformation").getString("homeTown")!="null"){	
   		
   	     JSONObject homeTownInfo =  ((JSONObject)responseJSON.get(x)).getJSONObject("basicInformation").getJSONObject("homeTown");
		
   		 province[x] = homeTownInfo.getString("province");
			
		 city[x] = homeTownInfo.getString("city")+"     "+"加为好友";
		 
		 if(province[x].equals(Province) ){
		 setData(name[x],province[x],city[x],id[x]);
		 }
		
		 
		 
   		}
//   else{
  // 			province[x] = "未填写";
 //  			city[x] = "未填写";
  // 			setData(name[x],"他没有填写哦"," ",id[x]);
//   		}	
			
//			databuffer.append("id"+id[x]+"\n");
//			databuffer.append("name"+name[x]+"\n");
//			databuffer.append("province"+province[x]+"\n");
//			databuffer.append("city"+city[x]+"\n");
		    
	        }
     
    if(responseJSON.length()==100){
    	count++;
    	param4.setPageNumber(count);
    	getSamePlaceFriend(param4,Province);
    	
    }
    	
//Log.v("xxs", databuffer.toString());			
Log.v("aa", length+""); 

GetSecondClassFriend.this.myHandler.sendEmptyMessage(1);
// SimpleAdapter adapterGetUserFriends = new SimpleAdapter(this, getUserFriendData(), R.layout.get_user_friends_display_adapter, new String[]{"USERNAME","PROVINCES","CITIES"}, new int[]{R.id.USERNAME,R.id.PROVINCES,R.id.CITIES});
//
// listview.setAdapter(adapterGetUserFriends);
// 
// listview.setOnItemClickListener(new OnItemClickListener() {
//
//	@Override
//	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
//		
//		//获得选中项的HashMap对象
//		HashMap<String, String> map = (HashMap<String, String>)listview.getItemAtPosition(position);
//		String id = map.get("ID");
//		Log.v("id",id);
//		String URL= "http://mt.renren.com/profile/";
////		intent.setClass(GetSecondClassFriend.this, GetSecondClassFriend.class);
//		URL = URL+id;
//		Uri uri = Uri.parse(URL);
//		Intent i = new Intent(Intent.ACTION_VIEW,uri); 
//		
//		startActivity(i);
//		Log.v("ID", id);
//		
//		// TODO Auto-generated method stub
//		
//	}
//	 
//});

//textView.setText(databuffer);
	        
	        
	        
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	 
    	
    	
		return databuffer;
    	
    }
    
	 private List<Map<String , Object>> getUserFriendData() {
			// TODO Auto-generated method stub
			return list;
		}
	    
	    private void setData(String name,
				String province, String city ,String id) {
			
	    	map = new HashMap<String,Object>();
	    	map.put("ID", id);
	    	map.put("USERNAME", name);
	    	map.put("PROVINCES", province);
	    	map.put("CITIES", city);
	    	list.add(map);
	    	
	    	// TODO Auto-generated method stub
			
		}


	

}
