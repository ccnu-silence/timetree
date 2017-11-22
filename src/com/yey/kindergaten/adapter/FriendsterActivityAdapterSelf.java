package com.yey.kindergaten.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.exception.DbException;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.R;
import com.yey.kindergaten.activity.ServiceFriendsterActivity;
import com.yey.kindergaten.activity.ServiceFriendsterFindActivity;
import com.yey.kindergaten.adapter.base.BaseListAdapter;
import com.yey.kindergaten.adapter.base.ViewHolder;
import com.yey.kindergaten.bean.FaceText;
import com.yey.kindergaten.bean.Twitter.comments;
import com.yey.kindergaten.bean.TwitterSelf;
import com.yey.kindergaten.bean.TwitterSelf.CommentsSelf;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.util.AppUtils;
import com.yey.kindergaten.util.FaceTextUtils;
import com.yey.kindergaten.util.ImageLoadOptions;
import com.yey.kindergaten.util.TimeUtil;
import com.yey.kindergaten.widget.CircleImageView;
import com.yey.kindergaten.widget.DialogTips;
import com.yey.kindergaten.widget.EmoticonsEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FriendsterActivityAdapterSelf extends BaseListAdapter<TwitterSelf>{
	private Context mContext;
	private String spanString ;
	private String textString="";
	private int inputtype=1;
	private FriendsterActivityItemAdapterSelf adapter;
	private List<EditText>  editlist =new ArrayList<EditText>();
	private List<comments> allist=new ArrayList<comments>();
	private List<comments> alist=new ArrayList<comments>();
	private int texesize=0;
	private int editaction;
	private  boolean flag ;
	private int tuid;
	private int cmid;
	List<Map<Integer, Integer>> facesize=new ArrayList<Map<Integer,Integer>>();
	List<FaceText> emos = null;
	public FriendsterActivityAdapterSelf(Context context,List<TwitterSelf> obList,boolean flag,int tuid) {
		super(context, obList);
		this.mContext=context;
		this.flag=flag;
		this.tuid=tuid;
	}

	
	@Override
	public View bindView(final int position,View convertView, final ViewGroup parent){
		Log.i("getViewposition", position+"");
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.activity_service_friendster_item, null);
		}
		CircleImageView iv_head=ViewHolder.get(convertView, R.id.iv_activity_service_friendster_item);
		TextView tv_name=ViewHolder.get(convertView, R.id.tv_activity_service_friendster_item_name);
		TextView tv_discuss=ViewHolder.get(convertView, R.id.tv_activity_service_friendster_item_discuss);
		TextView tv_time =ViewHolder.get(convertView, R.id.tv_activity_service_friendster_item_time);
		ListView  lv=ViewHolder.get(convertView, R.id.lv_activity_service_friendster_item);
		GridView gv=ViewHolder.get(convertView, R.id.gv_activity_service_friendster_item);
		ImageButton iv_discuss=ViewHolder.get(convertView, R.id.ivbtn_activity_service_friendster_item);
		ImageButton iv_delete=ViewHolder.get(convertView, R.id.ivbtn_activity_service_friendster_delete);
		final LinearLayout  ll=ViewHolder.get(convertView, R.id.ll_activity_service_friendster_item_input);
		ll.setFocusable(true);
		ll.requestFocus();
		final EmoticonsEditText et=ViewHolder.get(convertView, R.id.input_activity_service_friendster_item);
		if (flag) {
			iv_delete.setVisibility(View.VISIBLE);
		}else{
			iv_delete.setVisibility(View.GONE);
		}
//		editlist.add(et);
		if (spanString!=null) {
			int cursor = et.getSelectionStart();  
            et.getText().insert(cursor, spanString);
            Log.i("neirong", et.getText().toString());
            Log.i("zhuanhuan",AppUtils.getCharSequenceText(et.getText().toString())+"");
			spanString=null;
		}
		final ImageView iv_bq=ViewHolder.get(convertView, R.id.biaoqing_activity_service_friendster_item);
		final LinearLayout ll_bq=ViewHolder.get(convertView, R.id.service_publishspeak_facely);
		Button send_btn=ViewHolder.get(convertView, R.id.btn_activity_service_friendster_item);	
		tv_name.setText(getList().get(position).getPostername());
		imageLoader.displayImage(getList().get(position).getPosteravatar(), iv_head,ImageLoadOptions.getFriendOptions());
		SpannableString spannableString = FaceTextUtils.toSpannableString(this.mContext, getList().get(position).getContent());
		tv_discuss.setText(spannableString);
		tv_time.setText(TimeUtil.getChatTime(getList().get(position).getDate()));
		if ((!getList().get(position).getImgs().equals(""))&&getList().get(position).getImgs()!=null) {
			gv.setVisibility(View.VISIBLE);
			String[] headvision=getList().get(position).getImgs().split(",");
			FriendsterGridviewAdapter madapter=new FriendsterGridviewAdapter(headvision, mContext,"friendster",ImageLoadOptions.getFriendDataOptions());
			gv.setAdapter(madapter);
		}else{
			gv.setVisibility(View.GONE);
		}
		if (getList().get(position).getComment()!=null) {
			lv.setVisibility(View.VISIBLE);
			adapter=new FriendsterActivityItemAdapterSelf(mContext, getList().get(position).getComment());
			lv.setAdapter(adapter);
		}else{
			lv.setVisibility(View.GONE);
		}
		ViewPager faceViewPage=(ViewPager) ViewHolder.get(convertView, R.id.service_publishspeak_face);
		emos = FaceTextUtils.faceTexts;
		List<View> views = new ArrayList<View>();
		for (int i = 0; i < 1; ++i) {
			views.add(getGridView(i,et));
		}
		faceViewPage.setAdapter(new EmoViewPagerAdapter(views));                                                                                                              
		iv_discuss.setOnClickListener(new OnClickListener() {				
			@Override
			public void onClick(View v) {
				Log.i("twID",getList().get(position).getTwrid()+"");
			  if (ll.isShown()) {
				  ll.setVisibility(View.GONE);
				  hideSoftInput(et);
				  et.requestFocus();
			}else{
				et.setFocusable(true);
				et.requestFocus();
				ll.setVisibility(View.VISIBLE);
				showSoftInput(et);
				}
			}
		});
		iv_delete.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
			     showDialog("友情提示：", "删除说说", "确定", new DialogInterface.OnClickListener() {			
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						editaction=getList().get(position).getTwrid();			
						DbHelper.deletefriendster(editaction);
//						DbHelper.delfriendster(editaction);
						ServiceFriendsterActivity.ShowData(false);
						ServiceFriendsterFindActivity.ShowData();
					}
				});
				return;
			}
		});
		send_btn.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				ll.setVisibility(View.GONE);
				ll_bq.setVisibility(View.GONE);
				hideSoftInput(et);
				AppServer.getInstance().sentCommment(AppServer.getInstance().getAccountInfo().getUid(), getList().get(position).getTwrid(),0, et.getText().toString(), new OnAppRequestListener() {					
					@Override
					public void onAppRequest(int code, String message, Object obj) {
						allist.clear();
						alist.clear();
						if (code==AppServer.REQUEST_SUCCESS) {
							comments cm=(comments) obj;
							cmid=cm.getCmtid();
							comments newValues=new comments();
							newValues.setContent(et.getText().toString());
							newValues.setCmterid(AppServer.getInstance().getAccountInfo().getUid());
							newValues.setCmtid(cmid);
							newValues.setCmtername(AppServer.getInstance().getAccountInfo().getNickname());
							newValues.setTwrid(getList().get(position).getTwrid());
							try {
								DbHelper.getDB(mContext).save(newValues);
							} catch (DbException e) {
								e.printStackTrace();
							}
							ServiceFriendsterActivity.ShowData(false);
							CommentsSelf newvalues=new CommentsSelf();
							newvalues.setContent(et.getText().toString());
							newvalues.setCmterid(AppServer.getInstance().getAccountInfo().getUid());
							newvalues.setCmtid(cmid);
							newvalues.setCmtername(AppServer.getInstance().getAccountInfo().getNickname());
							newvalues.setTwrid(getList().get(position).getTwrid());
							try {
								DbHelper.getDB(mContext).save(newvalues);
							} catch (DbException e) {
								e.printStackTrace();
							}
							ServiceFriendsterFindActivity.ShowData();
							et.setText("");
						}						
					}
				});
			}
		});
		iv_head.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				Intent i=new Intent(mContext, ServiceFriendsterFindActivity.class);
				i.putExtra("tuid", getList().get(position).getPosterid());
				mContext.startActivity(i);
			}
		});
		convertView.setOnTouchListener(new OnTouchListener() {		
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				ll.setVisibility(View.GONE);
				ll_bq.setVisibility(View.GONE);
				hideSoftInput(et);
				return false;
			}
		});
		lv.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				ll.setVisibility(View.GONE);
				ll_bq.setVisibility(View.GONE);
				hideSoftInput(et);
				return false;
			}
		});
		gv.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				ll.setVisibility(View.GONE);
				ll_bq.setVisibility(View.GONE);
				hideSoftInput(et);
				return false;
			}
		});
		iv_bq.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				 if(ll_bq.isShown()){
					 et.requestFocus();
					 ll_bq.setVisibility(View.GONE);
					 showSoftInput(et);
				 }else{
					 et.requestFocus();
					 ll_bq.setVisibility(View.VISIBLE);
					 hideSoftInput(et);
				 }				
			}
		});	
	   	 et.addTextChangedListener(new TextWatcher() {           
	         @Override  
	         public void onTextChanged(CharSequence s, int start, int before, int count) {
	        	       	 
	         }  
	           
	         @Override  
	         public void beforeTextChanged(CharSequence s, int start, int count,  
	                 int after) {   
	        	
	         }              
	         @Override  
	         public void afterTextChanged(Editable s) {   
	        	     	 
	         }  
	     }); 
		return convertView;
	}

	public  void showSoftInput(EditText et) {
		  InputMethodManager imm = (InputMethodManager) et.getContext()
		    .getSystemService(Context.INPUT_METHOD_SERVICE);
		  imm.showSoftInput(et, InputMethodManager.HIDE_NOT_ALWAYS);
		 }
	public  void hideSoftInput(EditText et) {
		  InputMethodManager imm = (InputMethodManager) et.getContext()
		    .getSystemService(Context.INPUT_METHOD_SERVICE);
		  imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
		 }
	
	private View getGridView(final int i,final EmoticonsEditText et) {
		View view = View.inflate(AppContext.getInstance(), R.layout.include_emo_gridview, null);
		GridView gridview = (GridView) view.findViewById(R.id.gridview);
		List<FaceText> list = new ArrayList<FaceText>();
		if (i == 0) {
			list.addAll(emos.subList(0, 21));
		} else if (i == 1) {
			list.addAll(emos.subList(1, emos.size()));
		}
		final EmoteAdapter gridAdapter = new EmoteAdapter(this.mContext,
				list);
		gridview.setAdapter(gridAdapter);
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				FaceText name = (FaceText) gridAdapter.getItem(position);
				String key = name.text.toString();
				try {
					if (et != null && !TextUtils.isEmpty(key)) {
						int start = et.getSelectionStart();
						CharSequence content = et.getText()
								.insert(start, key);
						et.setText(content);
						CharSequence info = et.getText();
						if (info instanceof Spannable) {
							Spannable spanText = (Spannable) info;
							Selection.setSelection(spanText,
									start + key.length());
						}
					}
				} catch (Exception e) {

				}
			}
		});
		return view;
	}
	public void showDialog(String title,String message,String buttonText,DialogInterface.OnClickListener onSuccessListener) {
		DialogTips dialog = new DialogTips(mContext,title,message, buttonText,true,true);
		// 设置成功事件
		dialog.SetOnSuccessListener(onSuccessListener);
		// 显示确认对话框
		dialog.show();
		dialog = null;
	}
	

}


