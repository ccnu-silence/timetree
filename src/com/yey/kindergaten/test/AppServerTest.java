package com.yey.kindergaten.test;
import android.util.Log;

import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.bean.Twitter;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;

import junit.framework.TestCase;

public class AppServerTest extends TestCase{
	private AppContext context;
	private Twitter tv;
	 @Override
	    protected void setUp() throws Exception {
	        super.setUp();
            context = AppContext.getInstance();
	       
	    }
	
	public void testLogin() throws Exception {

        
    }
	
	public void testRegister() throws Exception {
	      AppServer.getInstance().register("yz13", "123456", 0,"2022222",new OnAppRequestListener() {
				
				@Override
				public void onAppRequest(int code, String message, Object obj) {
					Log.i("login", code+"");
					
				}
			});

	    }
	

}
