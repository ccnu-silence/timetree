package com.yey.kindergaten.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;



public class JsonServer {
	private static final String TAG = "JsonServer";
	
	private static final int GET_JSON = 10;
	private static final int POST_JSON = 11;
	private static final int POST_FORM = 12;
	
	private static final int QUIT = 0;
	private Handler mHandler = new Handler();

	private static JsonServer mInstance;
	public static JsonServer getInstance(){
		if(mInstance == null){
			mInstance = new JsonServer();
			
		}
		return mInstance;
	}
	
	private MessageHandler mMessageHandler;
	
	public void start(){
		if(mMessageHandler == null){
			HandlerThread th = new HandlerThread("server_thread");
			th.start();
			mMessageHandler = new MessageHandler(th.getLooper());
		}
	}
	public void stop(){
		if(mMessageHandler != null){
			mMessageHandler.sendEmptyMessage(QUIT);
			mMessageHandler = null;
		}
	}
	public void getJsonFromServer(String url, OnRequestFinishedListener l){
		getJsonFromServer(url, true, l);
		
	}

    /**
     *
     * @param url
     * @param mainThread   是否主线程里返回
     * @param l
     */
    public void getJsonFromServer(String url, boolean mainThread, OnRequestFinishedListener l){
        if(mMessageHandler != null){
            Message msg = mMessageHandler.obtainMessage(GET_JSON, l);
            Bundle data = new Bundle();
            data.putString("url", url);
            data.putBoolean("mainthread",mainThread);
            msg.setData(data);
            msg.sendToTarget();
        }
    }
	private void getJsonByUrl(String url, boolean mainThread, final OnRequestFinishedListener l){
		Log.i(TAG, "request url : "+url);
		HttpClient client = new DefaultHttpClient();
		StringBuilder builder = new StringBuilder();
		HttpGet get = new HttpGet(url);
		try{
			HttpResponse response = client.execute(get);
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			for(String str = reader.readLine(); str != null; str=reader.readLine()){
				builder.append(str);
			}
//			Utils.logi(TAG, "reqeust back : " + builder.toString());
			
	
		} catch (ClientProtocolException e) {
			
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			
			Log.e(TAG, e.getMessage());
		} 
		if(l != null){
            if(mainThread){
                final String jstr = builder.toString();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        l.onRequestFinished(jstr);
                    }
                });
            }else{
                l.onRequestFinished(builder.toString());
            }

		}
		
	}
    public void sendJsonToServer(String jsonStr, String url, OnRequestFinishedListener l){
        sendJsonToServer(jsonStr, url, true, l);
    }
	public void sendJsonToServer(String jsonStr, String url, boolean mainThread, OnRequestFinishedListener l){
		if(mMessageHandler != null){
			Message msg = mMessageHandler.obtainMessage(POST_JSON, l);
			Bundle data = new Bundle();
			data.putString("json", jsonStr);
			data.putString("url", url);
            data.putBoolean("mainthread", mainThread);
			msg.setData(data);
			msg.sendToTarget();
		}
	}

    public void sendFormToServer(Bundle params, String url, OnRequestFinishedListener l){
         sendFormToServer(params, url, true, l);
    }
	public void sendFormToServer(Bundle params, String url,boolean mainThread, OnRequestFinishedListener l){
		Log.i(TAG, "post url : "+url);
		if(mMessageHandler != null){
			FormData formData = new FormData();
			
		    if(params != null){
	          Set<String> keys = params.keySet();
	          for(Iterator<String> i = keys.iterator(); i.hasNext();) {
	               String key = (String)i.next();
	               formData.setValue(key, params.getString(key));
//		               pairs.add(new BasicNameValuePair(key, parmas.getString(key)));
	          }
		    }

		    Bundle data = new Bundle();
		    data.putString("form", formData.toString());
		    data.putString("url", url);
		    Message msg = mMessageHandler.obtainMessage(POST_FORM, l);
			msg.setData(data);
			msg.sendToTarget();
		}
		
	}

	public void sendRequestForm(String formStr, String url, final OnRequestFinishedListener l){
		sendRequestForm(formStr, url, true, l);
   }

	public void sendRequestForm(final String formStr, final String url,boolean mainThread, final OnRequestFinishedListener l){
		 new AsyncTask<Void, Void, String>() {

				@Override
				protected String doInBackground(Void... arg0) {
					  HttpClient client = new DefaultHttpClient(); 
					
					  StringBuilder builder = new StringBuilder();
						try {		
						    HttpPost post =new HttpPost(url);     
						    
						    post.addHeader("Content-Type", "application/x-www-form-urlencoded");  
						    post.addHeader("charset", HTTP.UTF_8);
						    StringEntity se = new StringEntity(formStr, HTTP.UTF_8);
							post.setEntity(se);

						    HttpResponse response = client.execute(post);
						    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
							for(String str = reader.readLine(); str != null; str=reader.readLine()){
								builder.append(str);
							}

							

						} catch (ClientProtocolException e) {
							Log.e(TAG, e.getMessage());
						} catch (IOException e) {
							
							Log.e(TAG, e.getMessage());
						} 		
						if(client != null){
							client.getConnectionManager().shutdown();
						}
					return builder.toString();
				}
				
				@Override
				protected void onPostExecute(String result) {
					if(result !=null){
						 l.onRequestFinished(result);
					}else{
						 l.onRequestFinished("-1");
					}
					super.onPostExecute(result);
				}
				   
			}.execute();
			
	}

	
/*	public void sendRequestVolley(final HashMap<String, String> map, final String url, final OnRequestFinishedListener l){
		StringRequest stringRequest = new StringRequest(Method.POST,url,  
                new Response.Listener<String>() {  
                    @Override  
                    public void onResponse(String response) {  
                    	 l.onRequestFinished(response);
                    }  
                }, new Response.ErrorListener() {  
                    @Override  
                    public void onErrorResponse(VolleyError error) {  
                    	l.onRequestFinished("-1"); 
                    }  
                }
                
                ){
			@Override
			protected Map<String, String> getParams()
					throws AuthFailureError {
			
				return map;
			}
		};		
	}*/
	
	private void sendRequestJson(String jstr, String url, boolean mainThread, final OnRequestFinishedListener l){
		HttpClient client = new DefaultHttpClient();
		StringBuilder builder = new StringBuilder();
		try{
			HttpPost post =new HttpPost(url);
//			post.setHeader(HTTP.CONTENT_TYPE, "application/json");
		
			post.addHeader("Content-Type", "application/json");    
			post.addHeader("charset", HTTP.UTF_8);

			StringEntity se = new StringEntity(jstr, HTTP.UTF_8);
			post.setEntity(se);
			HttpResponse response = client.execute(post);
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			for(String str = reader.readLine(); str != null; str=reader.readLine()){
				builder.append(str);
			}

			
			
		} catch (ClientProtocolException e) {
			
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			
			Log.e(TAG, e.getMessage());
		} 
		if(l != null){				
			if(mainThread){
                final String str = builder.toString();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        l.onRequestFinished(str);
                    }
                });
            }else{
                l.onRequestFinished(builder.toString());
            }

		}
		if(client != null){
			client.getConnectionManager().shutdown();
		}

	}
	
	class MessageHandler extends Handler{
		public MessageHandler(Looper looper){
			super(looper);
		}
		public void quit(){
			Looper.myLooper().quit();			
		}
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case GET_JSON:
				Bundle data = msg.getData();
				
				getJsonByUrl(data.getString("url"), data.getBoolean("mainthread"),(OnRequestFinishedListener)msg.obj);
				break;
			case POST_JSON:
				data = msg.getData();
				
				sendRequestJson(data.getString("json"), data.getString("url"),  data.getBoolean("mainthread"), (OnRequestFinishedListener)msg.obj);
				break;
			case POST_FORM:
				data = msg.getData();
				sendRequestForm(data.getString("form"), data.getString("url"),data.getBoolean("mainthread"), (OnRequestFinishedListener)msg.obj);
				break;
			case QUIT:
				quit();
				break;
			}
		}
		
	}
}
