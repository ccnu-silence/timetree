package com.yey.kindergaten.net;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.litesuits.http.LiteHttpClient;
import com.litesuits.http.async.HttpAsyncExecutor;
import com.litesuits.http.data.HttpStatus;
import com.litesuits.http.data.NameValuePair;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.Request;
import com.litesuits.http.request.content.UrlEncodedFormBody;
import com.litesuits.http.response.handler.HttpResponseHandler;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.Contacts;
import com.yey.kindergaten.bean.GradeInfo;
import com.yey.kindergaten.bean.GroupInfoBean;
import com.yey.kindergaten.bean.GroupMemberInfo;
import com.yey.kindergaten.bean.Product;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.AppUtils;

public class GroupInfoServer {

	 public static LiteHttpClient liteclient;
	 private static GroupInfoServer mInstance;
	 public static final String TAG_CODE = "code";
	 public static final String TAG_INFO = "info";
	 public static final String TAG_RESULT = "result";
	 public static final String TAG_NEXTID = "nextid";
	 public static final int REQUEST_SUCCESS = 0;
	 public static final int REQUEST_FAILED = -1;
	 public static final int REQUEST_ERROR = 1;
	 public static final int REQUEST_NO_NETWORK = -2;
	 public static final int REQUEST_LOGIN_ERROR_ACCOUNT = 1; //账号不存在
	 public static final int REQUEST_LOGIN_ERROR_POSSWORD = 2; //密码不正确
	 
	 public static GroupInfoServer getInstance(){
	        if(mInstance == null){
	            mInstance = new GroupInfoServer();
	        }
	        if(liteclient ==null){
	        	liteclient = LiteHttpClient.newApacheHttpClient(AppContext.getInstance());
	        }
	        return mInstance;
	    }
	 
	  private interface OnSendRequestListener{
	        public void onSendRequest(int code, String message, String result);
	    }

	  private interface OnSendRequestListenerFriend{
	        public void onSendRequestfriend(int code, String message, String result,int nextid);
	   }
	  private void sendFormRequest(HashMap<String, String> params,String url, final OnSendRequestListener listener){
	    	StringBuffer paramsStr = new StringBuffer();
			for (Map.Entry<String, String> entry : params.entrySet()) {//构建表单字段内容  
				paramsStr.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
			}
			String newparams = paramsStr.toString().substring(0,paramsStr.toString().length()-1);
	    	System.out.println("url--"+url+"??"+newparams);
			JsonServer.getInstance().sendRequestForm(newparams, url, new OnRequestFinishedListener() {
				@Override
				public void onRequestFinished(String jsonStr) {
	                int code = REQUEST_NO_NETWORK;
	                String message = "当前网络不可用，请检查你的网络设置。";
	                String result = null;
	                try {
	                    JSONObject jObj = new JSONObject(jsonStr);
	                    code = Integer.valueOf(jObj.getString(TAG_CODE));
	                    message = jObj.getString(TAG_INFO);
	                    result = jObj.getString(TAG_RESULT);
	                } catch (JSONException e) {
	                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
	                }
	                if(listener != null){
	                    listener.onSendRequest(code, message, result);
	                }
				}
			});
	    }	
	  
//<<<<<<< .mine
//	    public void sendVolleyRequestString(final HashMap<String, String> map, final String url, final OnSendRequestListener l){
//	    	
//	    	HttpAsyncExecutor asyncExcutor = HttpAsyncExecutor.newInstance(liteclient);
//	    	Request req = new Request(url);
//	        req.setMethod(com.litesuits.http.request.param.HttpMethod.Post);
//	        LinkedList<NameValuePair> pList = new LinkedList<NameValuePair>();
//	        for (Map.Entry<String, String> entry : map.entrySet()) {//构建表单字段内容  
//	        	 pList.add(new NameValuePair(entry.getKey(), entry.getValue()));
//			}
//	        req.setHttpBody(new UrlEncodedFormBody(pList));
//	        asyncExcutor.execute(req, new HttpResponseHandler() {
//				@Override
//				protected void onFailure(com.litesuits.http.response.Response res,
//						HttpException e) {
//					 if(l != null){
//	                     l.onSendRequest(REQUEST_NO_NETWORK, e+"", res.toString());
//	                 }
//					
//				}
//
//				@Override
//				protected void onSuccess(com.litesuits.http.response.Response res,
//						HttpStatus status, NameValuePair[] headers) {
//					
//					 int code = REQUEST_NO_NETWORK;
//			         String message = "当前网络不可用，请检查你的网络设置。";
//	                 String result = null;
//	                 try {
//	                     JSONObject jObj = new JSONObject(res.getString());
//	                     code = Integer.valueOf(jObj.getString(TAG_CODE));
//	                     message = jObj.getString(TAG_INFO);
//	                     result = jObj.getString(TAG_RESULT);
//
//	                 } catch (JSONException e) {
//
//	                     e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//	                 }
//
//	                 if(l != null){
//	                     l.onSendRequest(code, message, result);
//	                 }
//					
//				}
//	        });
//		}
//=======
//	  public void sendVolleyRequestString(final HashMap<String, String> map, final String url, final OnSendRequestListener l){
//	    	
//	    	HttpAsyncExecutor asyncExcutor = HttpAsyncExecutor.newInstance(liteclient);
//	    	Request req = new Request(url);
//	        req.setMethod(com.litesuits.http.request.param.HttpMethod.Post);
//	        LinkedList<NameValuePair> pList = new LinkedList<NameValuePair>();
//	        for (Map.Entry<String, String> entry : map.entrySet()) {//构建表单字段内容  
//	        	 pList.add(new NameValuePair(entry.getKey(), entry.getValue()));
//			}
//	        req.setHttpBody(new UrlEncodedFormBody(pList));
//	        asyncExcutor.execute(req, new HttpResponseHandler() {
//				@Override
//				protected void onFailure(com.litesuits.http.response.Response res,
//						HttpException e) {
//					 if(l != null){
//	                     l.onSendRequest(REQUEST_NO_NETWORK, e+"", res.toString());
//	                 }
//					
//				}
//
//				@Override
//				protected void onSuccess(com.litesuits.http.response.Response res,
//						HttpStatus status, NameValuePair[] headers) {
//					
//					 int code = REQUEST_NO_NETWORK;
//			         String message = "当前网络不可用，请检查你的网络设置。";
//	                 String result = null;
//	                 try {
//	                     JSONObject jObj = new JSONObject(res.getString());
//	                     code = Integer.valueOf(jObj.getString(TAG_CODE));
//	                     message = jObj.getString(TAG_INFO);
//	                     result = jObj.getString(TAG_RESULT);
//
//	                 } catch (JSONException e) {
//
//	                     e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//	                 }
//
//	                 if(l != null){
//	                     l.onSendRequest(code, message, result);
//	                 }
//					
//				}
//	        });
//		}
//>>>>>>> .r1788
//	  
	     /**
	     * 创建幼儿园群
	     * @param uid
	     * @param uid=653548&gname=明日之星幼儿园&location=233&joincode=contact=李园长
	     * @param &phone=13682238844&desc=明日之星幼儿园群介绍内容&timestamp=&key=34253ydfg675hr56
	     * @param listener
	     */
	    public void createKinderGroup(int uid,String gname,String location,String joincode,String contact,String phone,
	    		String desc ,final OnAppRequestListener listener){
	    	HashMap<String, String> params =new HashMap<String, String>();
	    	params.put(AppConstants.PARAM_UID,uid+"");
	    	params.put("gname",gname+"");
	    	params.put("location",location+"");
	    	params.put("joincode",joincode+"");
	    	params.put("contact",contact+"");
	    	params.put("phone",phone+"");
	    	params.put("desc",desc+"");
	     	String timestamp = URL.urlkey;
	    	params.put(AppConstants.PARAM_TIMESTAMP,  timestamp);
	    	StringBuffer sb =new StringBuffer();
	    	sb.append(uid).append(gname).append(location).append(joincode).append(contact).append(phone).append(desc).append(timestamp);
	    	params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
	    	sendFormRequest(params, GroupInfoURL.CREATEKINDERGROUP, new OnSendRequestListener() {			
				@Override
				public void onSendRequest(int code, String message, String result) {
					Object  obj  ;
					String gid="";
					if(code == REQUEST_SUCCESS){
						Gson  gson =new Gson();	
						try {
							JSONObject jsonObject=new JSONObject(result);
							gid=jsonObject.get(AppConstants.GNUM).toString();;
						} catch (JSONException e) {
							e.printStackTrace();
						}	
						obj=gid;
					}else{
						obj=message;
					}
					if (listener!=null) {
						listener.onAppRequest(code, message, obj);
					}
				}
			});
	      }
	    
	      /**
	     * 创建班级群
	     * @param uid
	     * @param uid=653548&gname=小一班&grade=35&joincode=&desc=小一班欢迎家长们的加入&timestamp=&key=34253ydfg675hr56
	     * @param listener
	     */
	    public void createCLASSGroup(int uid,String gname,String grade,String joincode,
	    		String desc ,final OnAppRequestListener listener){
	    	HashMap<String, String> params =new HashMap<String, String>();
	    	params.put(AppConstants.PARAM_UID,uid+"");
	    	params.put("gname",gname+"");
	    	params.put("grade",grade+"");
	    	params.put("joincode",joincode+"");
	    	params.put("desc",desc+"");
	     	String timestamp = URL.urlkey;
	    	params.put(AppConstants.PARAM_TIMESTAMP,  timestamp);
	    	StringBuffer sb =new StringBuffer();
	    	sb.append(uid).append(gname).append(grade).append(joincode).append(desc).append(timestamp);
	    	params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
	    	sendFormRequest(params, GroupInfoURL.CREATECLASSGROUP, new OnSendRequestListener() {			
				@Override
				public void onSendRequest(int code, String message, String result) {
					Object  obj  ;
					String gid="";
					if(code == REQUEST_SUCCESS){
						Gson  gson =new Gson();	
						try {
							JSONObject jsonObject=new JSONObject(result);
							gid=jsonObject.get(AppConstants.GNUM).toString();;
						} catch (JSONException e) {
							e.printStackTrace();
						}	
						obj=gid;
					}else{
						obj=message;
					}
					if (listener!=null) {
						listener.onAppRequest(code, message, obj);
					}
				}
			});
	      }
	    
	    /**
	     * 创建普通群
	     * @param uid
	     * @param uid=653548&gname=
	     * 亲子阅读交流群&joincode=&desc=亲子阅读交流群，亲子阅读的空间&timestamp=&key=34253ydfg675hr56
	     * @param listener
	     */
	    public void createGeneralGroup(int uid,String gname,String joincode,
	    		String desc ,final OnAppRequestListener listener){
	    	HashMap<String, String> params =new HashMap<String, String>();
	    	params.put(AppConstants.PARAM_UID,uid+"");
	    	params.put("gname",gname+"");
	    	params.put("joincode",joincode+"");
	    	params.put("desc",desc+"");
	     	String timestamp = URL.urlkey;
	    	params.put(AppConstants.PARAM_TIMESTAMP,  timestamp);
	    	StringBuffer sb =new StringBuffer();
	    	sb.append(uid).append(gname).append(joincode).append(desc).append(timestamp);
	    	params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
	    	sendFormRequest(params, GroupInfoURL.CREATEGENERALGROUP, new OnSendRequestListener() {			
				@Override
				public void onSendRequest(int code, String message, String result) {
					Object  obj  ;
					String gnum="";
					if(code == REQUEST_SUCCESS){
						Gson  gson =new Gson();	
						try {
							JSONObject jsonObject=new JSONObject(result);
							gnum=jsonObject.get(AppConstants.GNUM).toString();;
						} catch (JSONException e) {
							e.printStackTrace();
						}	
						obj=gnum;
					}else{
						obj=message;
					}
					if (listener!=null) {
						listener.onAppRequest(code, message, obj);
					}
				}
			});
	      }
	    
	    /**
	     * 加入群
	     * @param uid
	     * @param uid=653548&gid=1&joincode=1234&timestamp=&key=34253ydfg675hr56
	     * @param listener
	     */
	    public void addToGroup(int uid,int gnum,String joincode,final OnAppRequestListener listener){
	    	HashMap<String, String> params =new HashMap<String, String>();
	    	params.put(AppConstants.PARAM_UID,uid+"");
	    	params.put(AppConstants.GNUM,gnum+"");
	    	params.put("joincode",joincode+"");
	     	String timestamp = URL.urlkey;
	    	params.put(AppConstants.PARAM_TIMESTAMP,  timestamp);
	    	StringBuffer sb =new StringBuffer();
	    	sb.append(uid).append(gnum).append(joincode).append(timestamp);
	    	params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
	    	sendFormRequest(params, GroupInfoURL.ADDGROUP, new OnSendRequestListener() {			
				@Override
				public void onSendRequest(int code, String message, String result) {
					Object  obj = null;
					if (code==REQUEST_SUCCESS) {	
					}else{
						obj=result;
					}
					if (listener!=null) {
						listener.onAppRequest(code, message, obj);
					}
				}
			});
	      }
	    
	    
	    /**
	     * 查看群资料
	     * @param uid
	     * @param uid=653548&gid=1&&timestamp=&key=34253ydfg675hr56
	     * @param listener
	     */
	    public void LookGroupDataById(int uid,int gid,final OnAppRequestListener listener){
	    	HashMap<String, String> params =new HashMap<String, String>();
	    	params.put(AppConstants.PARAM_UID,uid+"");
	    	params.put("gid",gid+"");    	
	     	String timestamp = URL.urlkey;
	    	params.put(AppConstants.PARAM_TIMESTAMP,  timestamp);
	    	StringBuffer sb =new StringBuffer();
	    	sb.append(uid).append(gid).append(timestamp);
	    	params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
	    	sendFormRequest(params, GroupInfoURL.LOOKGROUPDATABYID, new OnSendRequestListener() {			
				@Override
				public void onSendRequest(int code, String message, String result) {
					Object  obj;
					if(code == REQUEST_SUCCESS){
						Gson  gson =new Gson();	
						JSONArray data = null;
						try {
							JSONObject jsonObject=new JSONObject(result);
							data=jsonObject.getJSONArray("result");
						} catch (JSONException e) {
							e.printStackTrace();
						}
						GroupInfoBean groupInfoBean=gson.fromJson(data.toString(), GroupInfoBean.class);									
						obj=groupInfoBean;
					}else{
						obj=message;
					}
					if (listener!=null) {
						listener.onAppRequest(code, message, obj);
					}
				}
			});
	      }
	    /**
	     * 查看群分享文本
	     * @param uid
	     * @param uid=653548&gid=1&&timestamp=&key=34253ydfg675hr56
	     * @param listener
	     */
	    public void getShareText(int uid,String gnum,final OnAppRequestListener listener){
	    	HashMap<String, String> params =new HashMap<String, String>();
	    	params.put(AppConstants.PARAM_UID,uid+"");
	    	params.put(AppConstants.GNUM,gnum+"");    	
	     	String timestamp = URL.urlkey;
	    	params.put(AppConstants.PARAM_TIMESTAMP,  timestamp);
	    	StringBuffer sb =new StringBuffer();
	    	sb.append(uid).append(gnum).append(timestamp);
	    	params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
	    	sendFormRequest(params, GroupInfoURL.GETSHARETEXT, new OnSendRequestListener() {			
				@Override
				public void onSendRequest(int code, String message, String result) {
					Object  obj;
					if(code == REQUEST_SUCCESS){
						Gson  gson =new Gson();	
						String data = null;
						try {
							JSONObject jsonObject=new JSONObject(result);
							data=jsonObject.get("txt").toString();
						} catch (JSONException e) {
							e.printStackTrace();
						}											
						obj=data;
					}else{
						obj=message;
					}
					if (listener!=null) {
						listener.onAppRequest(code, message, obj);
					}
				}
			});
	      }
	    
	    /**
	     * 编辑幼儿群资料
	     * @param uid
	     * @param uid=653548&gname=明日之星幼儿园1&location=233&joincode=contact=李园长1&phone=13682238844&
	     * desc=明日之星幼儿园群介绍内容1&timestamp=&key=34253ydfg675hr56
	     * @param listener
	     */
	    public void editKinderGroupData(int uid,int gnum, String gname,String location,String joincode,String contact,
	    		String phone,String desc,int joinable,final OnAppRequestListener listener){
	    	HashMap<String, String> params =new HashMap<String, String>();
	    	params.put(AppConstants.PARAM_UID,uid+"");
	    	params.put("gnum",gnum+"");   
	    	params.put("gname",gname+"");    
	    	params.put("location",location+"");   
	    	params.put("joincode",joincode+"");   
	    	params.put("contact",contact+"");   
	    	params.put("phone",phone+""); 
	    	params.put("desc",desc+"");  
	    	params.put("joinable",joinable+"");  
	     	String timestamp = URL.urlkey;
	    	params.put(AppConstants.PARAM_TIMESTAMP,  timestamp);
	    	StringBuffer sb =new StringBuffer();
	    	sb.append(uid).append(gnum).append(gname).append(location).append(joincode).append(contact).append(phone).append(desc).append(joinable).append(timestamp);
	    	params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
	    	sendFormRequest(params, GroupInfoURL.EDIRTKINDERGROUP, new OnSendRequestListener() {			
				@Override
				public void onSendRequest(int code, String message, String result) {
					Object  obj = null;
					if(code == REQUEST_SUCCESS){
					}else{
						obj=message;
					}
					if (listener!=null) {
						listener.onAppRequest(code, message, obj);
					}
				}
			});
	      }
	    
	    /**
	     * 编辑班级资料
	     * @param uid
	     * @param uid=653548&gnum=2637934&gname=小一班1&grade=35&joincode=&desc=小一班欢迎家长们的加入1&joinable=1&timestamp=&key=34253ydfg675hr56
	     * @param listener
	     */
	    public void editClassGroupData(int uid,int gnum, String gname,String grade,String joincode,
	    		String desc,int joinable,final OnAppRequestListener listener){
	    	HashMap<String, String> params =new HashMap<String, String>();
	    	params.put(AppConstants.PARAM_UID,uid+"");
	    	params.put("gnum",gnum+"");  
	    	params.put("gname",gname+"");    
	    	params.put("grade",grade+"");   
	    	params.put("joincode",joincode+"");   	 
	    	params.put("desc",desc+"");  
	    	params.put("joinable",joinable+"");  
	     	String timestamp = URL.urlkey;
	    	params.put(AppConstants.PARAM_TIMESTAMP,  timestamp);
	    	StringBuffer sb =new StringBuffer();
	    	sb.append(uid).append(gnum).append(gname).append(grade).append(joincode).append(desc).append(joinable).append(timestamp);
	    	params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
	    	sendFormRequest(params, GroupInfoURL.EDIRTCLASSGROUP, new OnSendRequestListener() {			
				@Override
				public void onSendRequest(int code, String message, String result) {
					Object  obj = null;
					if(code == REQUEST_SUCCESS){
					}else{
						obj=message;
					}
					if (listener!=null) {
						listener.onAppRequest(code, message, obj);
					}
				}
			});
	      }
	    /**
	     * 编辑普通群
	     * @param uid
	     * @param uid=653548&gname=亲子阅读交流群1&joincode=&desc=亲子阅读交流群，亲子阅读的空间1&joinable=1&timestamp=&key=34253ydfg675hr56
	     * @param listener
	     */ 
	    public void edirtGeneralGroup(int uid,int gnum,String gname,String joincode,
	    		String desc,int joinable,final OnAppRequestListener listener){
	    	HashMap<String, String> params =new HashMap<String, String>();
	    	params.put(AppConstants.PARAM_UID,uid+"");
	    	params.put("gnum",gnum+"");     
	    	params.put("gname",gname+"");       
	    	params.put("joincode",joincode+"");   	 
	    	params.put("desc",desc+"");  
	    	params.put("joinable",joinable+"");  
	     	String timestamp = URL.urlkey;
	    	params.put(AppConstants.PARAM_TIMESTAMP,  timestamp);
	    	StringBuffer sb =new StringBuffer();
	    	sb.append(uid).append(gnum).append(gname).append(joincode).append(desc).append(joinable).append(timestamp);
	    	params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
	    	sendFormRequest(params, GroupInfoURL.EDIRTGENERALGROUP, new OnSendRequestListener() {			
				@Override
				public void onSendRequest(int code, String message, String result) {
					Object  obj = null;
					if(code == REQUEST_SUCCESS){
					}else{
						obj=message;
					}
					if (listener!=null) {
						listener.onAppRequest(code, message, obj);
					}
				}
			});
	      }
	    
	    
	    /**
	     * 查看群资料
	     * @param uid
	     * @param uid=653548&gid=1&&timestamp=&key=34253ydfg675hr56
	     * @param listener
	     */
	    public void LookGroupDataByNum(int uid,String gnum,final OnAppRequestListener listener){
	    	HashMap<String, String> params =new HashMap<String, String>();
	    	params.put(AppConstants.PARAM_UID,uid+"");
	    	params.put("gnum",gnum+"");    	
	     	String timestamp = URL.urlkey;
	    	params.put(AppConstants.PARAM_TIMESTAMP,  timestamp);
	    	StringBuffer sb =new StringBuffer();
	    	sb.append(uid).append(gnum).append(timestamp);
	    	params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
	    	sendFormRequest(params, GroupInfoURL.LOOKGROUPDATABYNUM, new OnSendRequestListener() {			
				@Override
				public void onSendRequest(int code, String message, String result) {
					Object  obj = null;
					if(code == REQUEST_SUCCESS){
						Gson  gson =new Gson();		
						if(result!=null&&!result.equals("[]")){
							GroupInfoBean groupInfoBean=gson.fromJson(result, GroupInfoBean.class);									
							obj=groupInfoBean;
						}
					}else{
						obj=message;
					}
					if (listener!=null) {
						listener.onAppRequest(code, message, obj);
					}
				}
			});
	      }
	    /**
	     * 删除群成员
	     * @param uid
	     * @param 
	     * @param listener
	     */
	    public void DelGroupMember(int uid,int deluid,final OnAppRequestListener listener){
	    	HashMap<String, String> params =new HashMap<String, String>();
	    	params.put(AppConstants.PARAM_UID,uid+"");
	    	params.put("deluid",deluid+"");    	
	     	String timestamp = URL.urlkey;
	    	params.put(AppConstants.PARAM_TIMESTAMP,  timestamp);
	    	StringBuffer sb =new StringBuffer();
	    	sb.append(uid).append(deluid).append(timestamp);
	    	params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
	    	sendFormRequest(params, GroupInfoURL.DELGROUPMENBER, new OnSendRequestListener() {			
				@Override
				public void onSendRequest(int code, String message, String result) {
					Object  obj = null;
					if(code == REQUEST_SUCCESS){
						
					}else{
						obj=message;
					}
					if (listener!=null) {
						listener.onAppRequest(code, message, obj);
					}
				}
			});
	      }
	    
	    /**
	     * 获取群成员
	     * @param uid
	     * @param 
	     * @param listener
	     */
	    public void GetGroupMember(int uid,int gnum,final OnAppRequestListener listener){
	    	HashMap<String, String> params =new HashMap<String, String>();
	    	params.put(AppConstants.PARAM_UID,uid+"");
	    	params.put("gnum",gnum+"");    	
	     	String timestamp = URL.urlkey;
	    	params.put(AppConstants.PARAM_TIMESTAMP,  timestamp);
	    	StringBuffer sb =new StringBuffer();
	    	sb.append(uid).append(gnum).append(timestamp);
	    	params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
	    	sendFormRequest(params, GroupInfoURL.GETGROUPMEMBER, new OnSendRequestListener() {			
				@Override
				public void onSendRequest(int code, String message, String result) {
					Object  obj = null;
					if(code == REQUEST_SUCCESS){
						Gson gson=new Gson();
						GroupMemberInfo []groupinfo=gson.fromJson(result, GroupMemberInfo[].class);
						obj=groupinfo;
					}else{
						obj=message;
					}
					if (listener!=null) {
						listener.onAppRequest(code, message, obj);
					}
				}
			});
	      }
	    
	    
	    /**
	     * 获取班级列表
	     * @param uid
	     * @param 
	     * @param listener
	     */
	    public void GetGradelist(int uid,final OnAppRequestListener listener){
	    	HashMap<String, String> params =new HashMap<String, String>();
	    	params.put(AppConstants.PARAM_UID,uid+"");	 	
	     	String timestamp = URL.urlkey;
	    	params.put(AppConstants.PARAM_TIMESTAMP,  timestamp);
	    	StringBuffer sb =new StringBuffer();
	    	sb.append(uid).append(timestamp);
	    	params.put(AppConstants.PARAM_KEY, AppUtils.Md5(sb.toString()));
	    	sendFormRequest(params, GroupInfoURL.GETGRADELIST, new OnSendRequestListener() {			
				@Override
				public void onSendRequest(int code, String message, String result) {
					Object  obj = null;
					if(code == REQUEST_SUCCESS){
						Gson gson=new Gson();
						GradeInfo []gradeInfos=gson.fromJson(result, GradeInfo[].class);
						obj=gradeInfos;
					}else{
						obj=message;
					}
					if (listener!=null) {
						listener.onAppRequest(code, message, obj);
					}
				}
			});
	      }
	    
	    
	    
	    
	   
}
