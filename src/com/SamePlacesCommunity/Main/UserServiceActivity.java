package com.SamePlacesCommunity.Main;





import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thirdapp.R;
import com.renn.rennsdk.RennClient;
import com.renn.rennsdk.RennExecutor.CallBack;
import com.renn.rennsdk.RennResponse;
import com.renn.rennsdk.exception.RennException;
import com.renn.rennsdk.param.BatchUserParam;
import com.renn.rennsdk.param.GetLoginUserParam;
import com.renn.rennsdk.param.GetUserParam;
import com.renn.rennsdk.param.ListUserFriendAppParam;
import com.renn.rennsdk.param.ListUserFriendMutualParam;
import com.renn.rennsdk.param.ListUserFriendParam;


public class UserServiceActivity extends Activity implements OnClickListener{
	private ListUserFriendParam param1;
    
    
    private ImageButton getUserBtn;
   
    private ImageButton listUserFriendBtn;
    private Button getLoginUserBtn;
    private TextView textView;
    private RennClient rennClient;
    private ProgressDialog mProgressDialog;
    private MyHandler myHandler ;
    private static int count=1;
    private static  int length;
	private static  Long[] USERIDS;
	private static boolean isUserInfoGet = false;
	public static LoginUserInfomationService userInformation;
	private ListView listview ;
	private static Map<String , Object> map;
	private static List<Map<String, Object>> list =new ArrayList<Map<String,Object>>();
    
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
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userservice);
        initView();
      
        myHandler = new MyHandler();
        
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
       // TODO Auto-generated method stub
       if (keyCode == KeyEvent.KEYCODE_BACK) {
        if(mProgressDialog != null ) {
         Toast.makeText(this, "正在努力获取信息，等一下吧", 3000);
        
         
        } else {
         finish();                                  
         System.exit(0);
        }
       }                  
      return false;          
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
    			Intent intent = new Intent();
    			intent.setClass(UserServiceActivity.this, GetSecondClassFriend.class);
    			intent.putExtra("id", id);
    			intent.putExtra("province", userInformation.province);
    			startActivity(intent);
    			Log.v("ID", id);
    			
    			// TODO Auto-generated method stub
    			
    		}
    		 
    	});
	}

	@Override
    protected void onDestroy() {
    	//退出时销毁定位
    	if(userInformation.city != null)
    	isUserInfoGet = true;
    	else
    	isUserInfoGet = false;
       
        super.onDestroy();
    }
 
   

    private void initView(){
        rennClient = RennClient.getInstance(this);
       
        
        getUserBtn = (ImageButton) findViewById(R.id.get_user_infor);
        getUserBtn.setOnClickListener(this);
      
        listUserFriendBtn = (ImageButton) findViewById(R.id.list_userFriend);
        listUserFriendBtn.setOnClickListener(this);
        getLoginUserBtn=(Button)findViewById(R.id.get_login_user_infor);
        getLoginUserBtn.setOnClickListener(this);
        textView = (TextView) findViewById(R.id.textView);
        listview = (ListView)findViewById(R.id.listViewUserFriends);
        listview.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}});
        
        
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//     
//          
          case R.id.get_user_infor:
                GetUserParam param = new GetUserParam();
                param.setUserId(rennClient.getUid());
                if (mProgressDialog == null) {
                    mProgressDialog = new ProgressDialog(UserServiceActivity.this);
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.setTitle("请等待");
                    mProgressDialog.setIcon(android.R.drawable.ic_dialog_info);
                    mProgressDialog.setMessage("正在获取信息");
                    mProgressDialog.show();
                }
                try {
                    rennClient.getRennService().sendAsynRequest(param, new CallBack() {    
                        
                        @Override
                        public void onSuccess(RennResponse response) {
                        	String data = response.toString();
                        	Log.v("data", data);
                        	String province = null;
                        	String city = null;
                        	isUserInfoGet = true;
                        	 //必须要从35截取到最后一位才是JSON数据,此处转化成ObjectJSON数据
                            data = dataObjectTrans(data);
                           
                            try {
								JSONObject jsonObject = null;
								jsonObject = new JSONObject(data);
								JSONObject basicinformation = jsonObject.getJSONObject("basicInformation").getJSONObject("homeTown");
								
								province = basicinformation.getString("province");
								city = basicinformation.getString("city");
								userInformation = new LoginUserInfomationService(" ", " "," "," ");
								userInformation.province = province;
								userInformation.city = city;
								userInformation.id = rennClient.getUid().toString();
								userInformation.name = jsonObject.getString("name");
								//Log.v("tag",userInformation.province+userInformation.city+userInformation.id+userInformation.name);
								
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						
//                            textView.setText(data+
//									"\nprovince:"+province+"\ncity:"+city
//									);
								
								Toast.makeText(UserServiceActivity.this, "获取成功啦！"+"你的家乡是"+province+" "+city, Toast.LENGTH_SHORT).show();  
								if (mProgressDialog != null) {
								    mProgressDialog.dismiss();
								    mProgressDialog = null;
								
							} 

                        
                        
                        }
                        
                        @Override
                        public void onFailed(String errorCode, String errorMessage) {
                            
                            Toast.makeText(UserServiceActivity.this, "获取失败了，再试试吧", Toast.LENGTH_SHORT).show();
                            if (mProgressDialog != null) {
                                mProgressDialog.dismiss();
                                mProgressDialog = null;
                            }
                            
                        }
                    });
                } catch (RennException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            
           
          

            
           
          
          
          case R.id.list_userFriend:
               
                
                param1 = new ListUserFriendParam();
            	
            	param1.setPageNumber(count);
            	
            	param1.setUserId(rennClient.getUid());
            	
            	param1.setPageSize(100);
            	
            	
            	
            
            	
            	if (mProgressDialog == null) {
                    mProgressDialog = new ProgressDialog(UserServiceActivity.this);
                    mProgressDialog.setCancelable(true);
                    mProgressDialog.setTitle("请稍等片刻...");
                    mProgressDialog.setIcon(android.R.drawable.ic_dialog_info);
                    mProgressDialog.setMessage("正在获取好友信息");
                    mProgressDialog.show();
                }
            	if(isUserInfoGet){
                getFriend(param1);
//               
                break;
                
        }else{
        	Toast.makeText(this, "我们需要先获取你的家乡哦！", 3000).show();
        }
          
          


        }
        
    }
    
    private void getFriend(ListUserFriendParam param1) {
    	  try {
    		 
              rennClient.getRennService().sendAsynRequest(param1, new CallBack() {    
                 
                  @Override
                  public void onSuccess(RennResponse response) {
                  	
                  	  StringBuffer databuffer = new StringBuffer("");
                  	  
                   	 try {
                   		 
                      	String data = response.toString();
                      	data = dataArrayTrans(data);
                  //转换成ArrayJSON数据 
                   		
                  //转成JSON数据		
							JSONObject jsonObject = new JSONObject(data);
						    JSONArray responseJSON = jsonObject.getJSONArray("response");
						   
						    length=responseJSON.length();
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
							
//			        textView.setText(databuffer);
			       
					
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

                                       
                              		 
                              		 databuffer = AnalysisData(response, databuffer);
                                 		 
                              		 

                           	 
                                  
                                  Toast.makeText(UserServiceActivity.this, "获取成功", Toast.LENGTH_SHORT).show();  
                                  if (mProgressDialog != null) {
                                      mProgressDialog.dismiss();
                                     
                                      mProgressDialog = null;
                                  }                           
                              }
                              
                             

								@Override
                              public void onFailed(String errorCode, String errorMessage) {

                                  textView.setText(errorCode+":"+errorMessage);
                                  Toast.makeText(UserServiceActivity.this, "获取失败了，再试试吧", Toast.LENGTH_SHORT).show();
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
                      textView.setText(errorCode+":"+errorMessage);
                      Toast.makeText(UserServiceActivity.this, "获取失败了，再试试吧", Toast.LENGTH_SHORT).show();
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
	private StringBuffer AnalysisData(RennResponse response,StringBuffer databuffer){
    	
    	 try {
    		 
       		 String data = response.toString();
       		 
       		data = dataArrayTrans(data);
       		
    		Log.v("data", data);
    		
			JSONObject jsonObject = new JSONObject(data);
			
		    JSONArray responseJSON = jsonObject.getJSONArray("response");
       		 
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
			
		 city[x] = homeTownInfo.getString("city");
		 
		 setData(name[x],province[x],city[x],id[x]);
		 
		 
   		}else{
   			province[x] = "未填写";
   			city[x] = "未填写";
   			setData(name[x],"他没有填写哦"," ",id[x]);
   		}	
   if(responseJSON.length() == 100){
	   count++;
	   param1.setPageNumber(count);
	   getFriend(param1);
	   
   }
			
		databuffer.append("city"+city[x]+"\n");
		    
	        }
 
UserServiceActivity.this.myHandler.sendEmptyMessage(1);

	        
	        
	        
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
    

	public static String dataObjectTrans(String data){
    	data = data.substring(35, data.length()-1);
    	
    	return data;
    }
    public static String dataArrayTrans(String data){
    	data = data.substring(23, data.length()-1);
    	
    	return data;
    }
    public StringBuffer getMyHome(){
		
    	
    	return null;
    	
    }
}
