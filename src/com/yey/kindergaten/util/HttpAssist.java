package com.yey.kindergaten.util;  
  
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import android.content.Context;

import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.yey.kindergaten.bean.Photo;
import com.yey.kindergaten.bean.ThreadsBean;
import com.yey.kindergaten.db.DbHelper;
  
  /**
   * 断点续传上传操作类
   * @author zy
   */
public class HttpAssist {  
    private static final String TAG = "uploadFile";  
    private static final int TIME_OUT = 10 * 10000000; // 超时时间   
    private static final String CHARSET = "utf-8"; // 设置编码   
    public static final String SUCCESS = "1";  
    public static final String FAILURE = "0";  
    public static final String PUASE ="2";//表示上传一半未完成
   
    public static InputStream isTwice; 
    
    public static RandomAccessFile raTwice;
    private Context context;
    private File file;
    private String path;
    private List<ThreadsBean> list;
    private int isPuase;
    private int longSize;
    private boolean pauseFlag=true;
    
    private List<Photo>photolist;
    
    private String photo_decs;
    private ArrayList<String>childlist;
    private String term;
    private String net;
    private String lifeworktype;
     
    public boolean isPauseFlag() {
		return pauseFlag;
	}

	public void setPauseFlag(boolean pauseFlag) {
		this.pauseFlag = pauseFlag;
	}

	ShowLoadingState loadingState;//显示适配情况的自定义回调接口
    public interface ShowLoadingState{
    	  public void setUploadAdapter(int sumsize,List<ThreadsBean> list,Boolean flag);
    }    
   
    ShowLoadingPercent loadingPercent;//显示进度条和百分比的回调函数
    public interface ShowLoadingPercent{
    	  public void setUploadPercent(int currentSize,boolean flag);
    }
 
    public HttpAssist(String lifeworktype,String photo_decs,ArrayList<String>childlist,String term) {  	
            this.photo_decs=photo_decs;
            this.childlist= childlist;
            this.term = term;   	
            this.lifeworktype= lifeworktype;
	}

    /**
     * 第一次上传时调用的方法
     * @param context
     * @param file
     * @return
     */
    public  String uploadFile(List<Photo> list,Context context,File file,int index,ShowLoadingPercent stateListener) {         
    	StringBuffer buffer=new StringBuffer(); 	
    	String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成   
        String CONTENT_TYPE = "multipart/form-data"; // 内容类型        
//        String RequestURL = "http://192.168.0.138:8077/hb/UploadHBPhotoLifeWork.ashx";
  //    String RequestURL = "http://58.220.10.100:808/hb/UploadHBPhotoLifeWork.ashx";
      String RequestURL = "http://totfup.zgyey.com/hb/UploadHBPhotoLifeWork.ashx";  
        try {  
            URL url = new URL(RequestURL); 
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
            conn.setReadTimeout(TIME_OUT);  
            conn.setConnectTimeout(TIME_OUT);  
            conn.setDoInput(true); // 允许输入流   
            conn.setDoOutput(true); // 允许输出流   
            conn.setUseCaches(false); // 不允许使用缓存   
            conn.setRequestMethod("POST"); // 请求方式   
            conn.setRequestProperty("Charset", CHARSET); // 设置编码   
            conn.setRequestProperty("connection", "keep-alive");  
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="  
                    + BOUNDARY);
            conn.setChunkedStreamingMode(4096*2);//上传内容过大，选择
            
            List<ThreadsBean> listbean= new ArrayList<ThreadsBean>();
            ThreadsBean beans=null;
            listbean.clear();
   
            for(int i=0;i<list.size();i++){
            	   beans =new ThreadsBean();
            	   beans.setCurrentSize(0);
                   beans.setUrl(list.get(i).imgPath);
                   if(i<list.size()-1){
                       buffer.append(list.get(i).imgPath).append("//");
                   }else {
                	   buffer.append(list.get(i).imgPath);
                   }
                   beans.setId(i);
                   if(i==0){
                	   beans.setFlag("1"); 
                   }else{
                	   beans.setFlag("3");
                   }             
                   beans.setIndexs(1);     
                   listbean.add(beans);
            }
                path=buffer.toString();         
                int sumSize=(int) file.length();
			    loadingState.setUploadAdapter(sumSize,listbean, true);             
			 if (file != null) {   
                OutputStream outputSteam = conn.getOutputStream();   
                DataOutputStream dos = new DataOutputStream(outputSteam); 
                InputStream is = new FileInputStream(file); 
                isTwice=is;
                /**客户端给这些流信息给服务端，让服务器判断断点续传后，在添加的文件的文件名*/
                String filename= file.getName(); //文件名
                Iterator<String> it = childlist.iterator();
                StringBuffer sbffer = new StringBuffer();
                while(it.hasNext()){
                	sbffer.append(it.next()).append(",");           
                }  
//                String params = 9123+"$"+filename+"$"+0+"$"+sumSize;                                       
                String params = StringUtils.StringToUnicode(photo_decs) +"$"+sbffer.substring(0, sbffer.lastIndexOf(","))+"$"
                		+term+"$"+lifeworktype+"$"+StringUtils.StringToUnicode(filename)+"$"+0+"$"+sumSize;             
                System.out.println("上传参数---->"+params);   
                byte[] sbyt=params.getBytes(); //文件名长度         
                byte[] byt =intToBytes(sbyt.length);// 文件转化成二进制流后的长度
                dos.write(byt);
                dos.write(sbyt);                
                byte[] bytes = new byte[1024];             
                int len = 0; 
                while ((len = is.read(bytes)) !=-1&&pauseFlag) {   

                     dos.write(bytes, 0, len);   
                 
                     if(stateListener!=null) stateListener.setUploadPercent((int)file.length()-is.available(), true);
                }  
                if(is.available()==0){  	 
                    DbHelper.getDB(context).delete(ThreadsBean.class, WhereBuilder.b("threadid", "=", 1));               	
                }else if(is.available()>0){//is.available()是剩下的，但是由于在第四次读的时候，is已经读了，不过dos没有输出，所以需要补一个字节的差位
                     ThreadsBean bean=new ThreadsBean(1,index, path, (int)file.length()-is.available()-1024,"1");           
   				     bean.setLifetype(lifeworktype);
   				     bean.setDecs(photo_decs);
   				     StringBuffer buffers =new StringBuffer();
   				     Iterator<String>its=childlist.iterator();
   				     while (its.hasNext()) {
						buffers.append(its.next()).append("//");						
					 }
   				     bean.setUids(buffers.substring(0,buffer.lastIndexOf("//")).toString());
   				     bean.setTerm(term);
                     DbHelper.getDB(context).save(bean);
                }
               
				/**接受response输入流，将流转化成string*/  
                InputStream stream = conn.getInputStream(); 
                int ch;
                StringBuilder b = new StringBuilder();
                while( (ch = stream.read()) != -1 ){
                 b.append((char)ch);}
                System.out.println(b.toString()+"----->上传保存成功了没");
             is.close();
             isTwice.close();
             dos.flush();
              }             
               /**获取response返回输入流接受完成*/  
        } catch (MalformedURLException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();
          
     /**---------捕获到I/O异常，添加到数据库----------*/
        try{
         if(isTwice!=null){
           if(isTwice.available()==0){
         	   if(DbHelper.getDB(context).tableIsExist(Thread.class)){
               	   DbHelper.getDB(context).delete(ThreadsBean.class, WhereBuilder.b("threadid", "=", 1));
               	 }
            }else if(isTwice.available()>0){//is.available()是剩下的，但是由于在第四次读的时候，is已经读了，不过dos没有输出，所以需要补一个字节的差位
                 ThreadsBean bean=new ThreadsBean(1,index, path, (int)file.length()-isTwice.available()-1024,"0");           
        	         bean.setLifetype(lifeworktype);
				     bean.setDecs(photo_decs);
				     StringBuffer buffers =new StringBuffer();
				     Iterator<String>its=childlist.iterator();
				     while (its.hasNext()) {
					   buffers.append(its.next()).append("//");						
				     }
				     
				     bean.setUids(buffers.substring(0,buffers.lastIndexOf("//")).toString());
				     bean.setTerm(term);
                     DbHelper.getDB(context).save(bean);
           }  
          } 
         } catch (IOException e1) {  
             e.printStackTrace();  
         }  catch (DbException e1) {
 			e.printStackTrace();
 		}
     /**-------异常在添加数据库的时候，再次抛出异常-------*/  
        
       }   catch (DbException e) {
			e.printStackTrace();
		} 
        
        return FAILURE;  
    }  
    
    /**
     * 断点续传时调用的方法
     * @param context
     * @param file
     * @param isPuase 是否暂停
     * @param longSize 
     * @return
     */
    public  String uploadFile(List<Photo>list, Context context,File file,int position,int longSize,String path,ShowLoadingPercent listener) {  
    	System.out.println("是断点续传的啊！！！！！！！！！！");
    	String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成   
//    	String RequestURL = "http://192.168.0.138:8077/class/UploadClassPhotoStream.ashx";
    	String CONTENT_TYPE = "multipart/form-data"; // 内容类型   
        String RequestURL = "http://totfup.zgyey.com/hb/UploadHBPhotoLifeWork.ashx";  
        int photosize=list.size();
        RandomAccessFile rFile = null;
        try {  
            URL url = new URL(RequestURL);  
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
            conn.setReadTimeout(TIME_OUT);  
            conn.setConnectTimeout(TIME_OUT);  
            conn.setDoInput(true); // 允许输入流   
            conn.setDoOutput(true); // 允许输出流   
            conn.setUseCaches(false); // 不允许使用缓存   
            conn.setRequestMethod("POST"); // 请求方式   
            conn.setRequestProperty("Charset", CHARSET); // 设置编码   
            conn.setRequestProperty("connection", "keep-alive");  
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="  
                    + BOUNDARY);
            conn.setChunkedStreamingMode(4096*2);//上传内容过大，选择分块上传
            if (file != null) {      
                OutputStream outputSteam = conn.getOutputStream();  
                DataOutputStream dos = new DataOutputStream(outputSteam);   
                StringBuffer buffer = new StringBuffer();
                List<ThreadsBean> listbean= new ArrayList<ThreadsBean>();
                ThreadsBean beans=null;
                listbean.clear();
                for(int i=0;i<list.size();i++){
                	   beans =new ThreadsBean();
                	   beans.setCurrentSize(0);
                       beans.setUrl(list.get(i).imgPath);
                       if(i<list.size()-1){
                           buffer.append(list.get(i).imgPath).append("//");
                       }else {
                    	   buffer.append(list.get(i).imgPath);
                       }
                       beans.setId(i);
                       if(i==0){
                    	   beans.setFlag("1"); 
                       }else{
                    	   beans.setFlag("3");
                       }             
                       beans.setIndexs(1);     
                       listbean.add(beans);
                }
                   this.path=buffer.toString();
                    int sumSize=(int) file.length();
                //初始化上传数据界面
    			loadingState.setUploadAdapter(sumSize,listbean, true);                          
                String filename= file.getName(); //文件名
//                Iterator<String> it = childlist.iterator();
//              
//                while(it.hasNext()){
//                	sbffer.append(it.next()).append(",");           
//                }                              
                List<ThreadsBean> lists=  DbHelper.getDB(context).findAll(ThreadsBean.class);
                ThreadsBean bean=lists.get(0);
                String photo_decs = bean.getDecs();
                String term =bean.getTerm();
                String lifeworktype = bean.getLifetype();
                String uids = bean.getUids();
                StringBuffer sbffer = new StringBuffer();
                String params=null;
                if(uids.contains("//")){
                	 String[]uidlist=uids.split("//");
                	 for(int i=0;i<uidlist.length;i++){
                		 sbffer.append(uidlist[i]).append(",");
                	 }
//                	   params = 9123+"$"+filename+"$"+longSize+"$"+sumSize; 
      				params= photo_decs+"$"+sbffer.substring(0, sbffer.lastIndexOf(","))+"$"
                      		+term+"$"+lifeworktype+"$"+filename+"$"+longSize+"$"+sumSize; 
                 }else{
//                	 params = 9123+"$"+filename+"$"+longSize+"$"+sumSize; 
                	params= photo_decs+"$"+uids+"$"+term+"$"+lifeworktype+"$"+filename+"$"+longSize+"$"+sumSize; 
                }    
                System.out.println("断点续传上传参数---->"+params); 
                byte[] sbyt=params.getBytes(); //文件名长度
                byte[] byt =intToBytes(sbyt.length);// 文件转化成二进制流后的长度
                dos.write(byt);
                dos.write(sbyt);                 
                byte[] bytes = new byte[1024];  
                int len = 0;              
                rFile=new RandomAccessFile(file, "rw");
                rFile.seek(longSize);            
                raTwice=rFile;
                while ((len = rFile.read(bytes)) != -1&&pauseFlag) { 
                	int nowSize=(int) rFile.getFilePointer();
                	//更新数据界面
                	if(listener!=null) listener.setUploadPercent(nowSize, true);
                    dos.write(bytes, 0, len);    
                    dos.flush();    
                }  
                int newCurrentSize= (int) rFile.getFilePointer();   
                if(newCurrentSize==file.length()){ 
                		 DbHelper.getDB(context).delete(ThreadsBean.class, WhereBuilder.b("threadid", "=", 1));
                	                                       
                }else if(newCurrentSize<file.length()){
                     ThreadsBean threadbean=new ThreadsBean(1,position,  this.path, newCurrentSize,"0");           
                     DbHelper.getDB(context).update(threadbean, WhereBuilder.b("threadid", "=", 1));			              			
                 }             
                rFile.close();   
                raTwice.close();
                dos.close();
				
                /**接受输入流，将流转化成string*/  
//                InputStream stream = conn.getInputStream(); 
//                int ch;
//                StringBuilder b = new StringBuilder();
//                while( (ch = stream.read()) != -1 ){
//                 b.append((char)ch);}
//                 String res=b.toString();
//                 int lengSize=Integer.valueOf(res);
//                 if (file.length()==lengSize) { 
//                 if(DbHelper.getDB(context).tableIsExist(Thread.class)){
//                 }
//                  return SUCCESS;                  
//                 } else if(file.length()>lengSize){                 	
//                	 return PUASE;
//                 }
              }            
               /**获取response返回输入流接受完成*/               
        } catch (MalformedURLException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
           /**------捕捉IO流异常------*/
            if(raTwice!=null){
            int newCurrentSize = 0;
			try {
				newCurrentSize = (int) raTwice.getFilePointer();
				  if(newCurrentSize==file.length()){        
		            	 DbHelper.getDB(context).delete(ThreadsBean.class, WhereBuilder.b("threadid", "=", 1));		            	                                      
		               }else if(newCurrentSize<file.length()){
		                     ThreadsBean bean=new ThreadsBean(1,position,  this.path, newCurrentSize,"0");           
						     DbHelper.getDB(context).update(bean, WhereBuilder.b("threadid", "=", 1));							              			
		             } 
					  raTwice.close();  
					  
			} catch (IOException e1) {
				e1.printStackTrace();
			}  catch (DbException e1) {
				e1.printStackTrace();
			}  
			}
		  /**------捕捉IO流异常结束------*/                                                 
        }  catch (DbException e) {
			e.printStackTrace();
		} 
        return FAILURE;  
    } 
    
    /**
     * int 类型转化成byte[]类型
     * @param value
     * @return
     */
    public static byte[] intToBytes( int value )   
    {   
        byte[] src = new byte[4];  
        src[3] =  (byte) ((value>>24) & 0xFF);  
        src[2] =  (byte) ((value>>16) & 0xFF);  
        src[1] =  (byte) ((value>>8) & 0xFF);    
        src[0] =  (byte) (value & 0xFF);                  
        return src;   
    }

	public ShowLoadingState getLoadingState() {
		return loadingState;
	}

	public void setLoadingState(ShowLoadingState loadingState) {
		this.loadingState = loadingState;
	}
       
}  
