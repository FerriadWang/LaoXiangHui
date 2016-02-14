package com.LaunchView.Main;

import com.SamePlacesCommunity.Main.MainActivity;
import com.example.thirdapp.R;
import com.renn.rennsdk.RennClient;
import com.renn.rennsdk.RennClient.LoginListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class TestRolateAnimActivity extends Activity {
    /** Called when the activity is first created. */
	MyImageView Feeds;
	MyImageView Friends;
	MyImageView Chat;
	MyImageView VersionInformation;
	private Button loginBtn;
	private Button logoutBtn;
	private RennClient rennClient;
	
	
	
	private static final String APP_ID = "239767";

	private static final String API_KEY = "f2923d6b4fe54c3388d947b924631f11";

	private static final String SECRET_KEY = "d1b24b547ee349769a188704629293a5";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        rennClient = RennClient.getInstance(this);
		rennClient.init(APP_ID, API_KEY, SECRET_KEY);
		rennClient
				.setScope("read_user_blog read_user_photo read_user_status read_user_album "
						+ "read_user_feed read_user_notification read_user_request "
						+ "read_user_comment read_user_share publish_blog publish_share "
						+ "publish_checkin send_request operate_like "
						+ "send_notification photo_upload status_update create_album "
						+ "publish_comment publish_feed");
		
		rennClient.setTokenType("bearer");
        setContentView(R.layout.main);
        initView();
        	
        
       
        
        
       
        Feeds.setOnClickIntent(new MyImageView.OnViewClick() {
			
			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				Toast.makeText(TestRolateAnimActivity.this, "正在跳转", 1000).show();
				Intent intentSwitch = new Intent(TestRolateAnimActivity.this,com.SamePlacesCommunity.Main.FeedServiceActivity.class); 
				startActivity(intentSwitch);
				System.out.println("1");
			}
		});
        
      
        Friends.setOnClickIntent(new MyImageView.OnViewClick() {
			
			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				Toast.makeText(TestRolateAnimActivity.this, "正在跳转", 1000).show();
				Intent intentSwitch = new Intent(TestRolateAnimActivity.this,com.SamePlacesCommunity.Main.UserServiceActivity.class); 
				startActivity(intentSwitch);
				System.out.println("2");
			}
		});
        
        Chat.setOnClickIntent(new MyImageView.OnViewClick() {
			
			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				Toast.makeText(TestRolateAnimActivity.this, "正在跳转", 1000).show();
				Intent intentSwitch = new Intent(TestRolateAnimActivity.this,com.zhy.socket.SocketClientDemoActivity.class); 
				startActivity(intentSwitch);
				System.out.println("1");
			}
		});
       
       
        
        VersionInformation.setOnClickIntent(new MyImageView.OnViewClick() {
			
			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				Toast.makeText(TestRolateAnimActivity.this, "正在跳转", 1000).show();
				Intent intentSwitch = new Intent(TestRolateAnimActivity.this,com.example.thirdapp.LocationActivity.class); 
				startActivity(intentSwitch);
				System.out.println("4");
			}
		});
       //这里绑定登陆
        
        
        
        
        
        
        
        
        
    }
	private void initView() {
		// TODO Auto-generated method stu
		loginBtn = (Button) findViewById(R.id.login_btn);
		
		logoutBtn = (Button) findViewById(R.id.logout_btn);
		Feeds=(MyImageView) findViewById(R.id.c_joke);
		Friends=(MyImageView) findViewById(R.id.c_idea);
		Chat=(MyImageView) findViewById(R.id.c_constellation);
		VersionInformation=(MyImageView) findViewById(R.id.c_recommend);
		 
		if (rennClient.isLogin()) {
				loginBtn.setVisibility(View.GONE);
				logoutBtn.setVisibility(View.VISIBLE);
			} else {
				loginBtn.setVisibility(View.VISIBLE);
				logoutBtn.setVisibility(View.GONE);
			}
		
		loginBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				rennClient.logout();
				rennClient.setLoginListener(new LoginListener() {
					@Override
					public void onLoginSuccess() {
						// TODO Auto-generated method stub
						Toast.makeText(TestRolateAnimActivity.this, "登录成功",
								Toast.LENGTH_SHORT).show();
						loginBtn.setVisibility(View.GONE);
						logoutBtn.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoginCanceled() {
						loginBtn.setVisibility(View.VISIBLE);
						logoutBtn.setVisibility(View.GONE);
					}
				});
				rennClient.login(TestRolateAnimActivity.this);
			}
		});
		logoutBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				rennClient.logout();
				loginBtn.setVisibility(View.VISIBLE);
				logoutBtn.setVisibility(View.GONE);
			}
		});
}
}