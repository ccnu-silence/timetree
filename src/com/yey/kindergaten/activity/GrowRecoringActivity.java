package com.yey.kindergaten.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.util.RecordManager;

/**
 * Created by cm_pc2 on 2015/2/4.
 */
public class GrowRecoringActivity extends BaseActivity{
    private ImageView iv_play;
    private ImageView iv_close;
    private Boolean isshow=true;
    private MediaPlayer mediaPlayer = null;
    public  boolean isPlaying = false;
    private  int play_statue=1;
    private  int play_begin=1;
    private  int play_ing=2;
    private  int play_end=3;
    public static int RECORD_NO = 0;  //不在录音
    public static int RECORD_ING = 1;   //正在录音
    public static int RECODE_ED = 2;   //完成录音
    private Boolean isshow_small=true;
    private int indexcount;
    RecordManager recordManager = null;
    private Thread recordThread;
    private Handler messagehandler;
    private TextView tv_time_left;
    private boolean isrun=true;
    private String file="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_record);
        initView();
    }

    private void initView() {
        recordManager = RecordManager.getInstance(this);
        messagehandler=new MessagHandler();
        showpopwindow();
        isshow=false;
    }
    private void showpopwindow() {
        if (isshow) {
            iv_close=(ImageView)findViewById(R.id.recordingvoice_close);
            iv_play=(ImageView)findViewById(R.id.recoringvoice_play);
            tv_time_left=(TextView) findViewById(R.id.time_recording);
            iv_close.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (play_statue==play_begin) {
                        finish();;
                        isshow=true;
                    }else if(play_statue==play_ing){
                        play_statue=play_end;
                        showinpopwindow();
                        isshow_small=false;
                    }else if(play_statue==play_end){
                        showinpopwindow();
                        isshow_small=false;
                    }
                }
            });
            iv_play.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(play_statue==play_begin){
                        play_statue=play_ing;
                        indexcount=0;
                        iv_play.setBackground(AppContext.getInstance().getResources().getDrawable(R.drawable.pauserecordin));
                        recordManager.startRecording(AppServer.getInstance().getAccountInfo().getUid()+"");
                        recordThread=new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i <=20; i++) {
                                    if (play_statue==play_ing) {
                                        indexcount=i;
                                        Message  m=new Message();
                                        m.what=RECORD_ING;
                                        messagehandler.sendMessage(m);
                                        try {
                                            Thread.sleep(1000);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                if (indexcount==20) {
                                    recordManager.stopRecording();
                                    Message m = new Message();
                                    m.what = RECODE_ED;
                                    messagehandler.sendMessage(m);

                                }
                            }
                        });
                        recordThread.start();
                    }else if(play_statue==play_ing){
                        iv_play.setBackground(AppContext.getInstance().getResources().getDrawable(R.drawable.pauserecordin));
                        if (indexcount>1) {
                            recordManager.stopRecording();
                            Message m=new Message();
                            m.what=RECODE_ED;
                            messagehandler.sendMessage(m);
//						    indexcount=0;
//                            tv_time.setText("00:00:00");
                        }else{
                            recordManager.cancelRecording();
                            Message m=new Message();
                            m.what=RECORD_NO;
                            messagehandler.sendMessage(m);
//						    indexcount=0;
//                            tv_time.setText("00:00:00");
                        }
                    }
                }
            });
        }

//		Button  btn_true=(Button)layout.findViewById(R.id.btn_true);
//		Button  btn_false=(Button)layout.findViewById(R.id.btn_false);
//		voice_playing=(ImageView) layout.findViewById(R.id.voice_playing);
//		btn_true.setOnClickListener(new OnClickListener() {
//						@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				flag=false;
//				if (indexcount>1) {
//					recordManager.stopRecording();
//					Message m=new Message();
//					m.what=RECODE_ED;
//					messagehandler.sendMessage(m);
//					indexcount=0;
//					isrun=true;
//					popwindow.dismiss();
//				}else{
//
//				}
//			}
//		});
//		btn_false.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				recordManager.cancelRecording();
//				Message m=new Message();
//				m.what=RECORD_NO;
//				messagehandler.sendMessage(m);
//				indexcount=0;
//				isrun=true;
//				popwindow.dismiss();
//			}
//		});
    }

    private void showinpopwindow() {
        if (isshow_small) {
//		LayoutInflater inflater=(LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
//		View layout=inflater.inflate(R.layout.activitity_popwindow_keepdairy, null);
            showDialog("是否保存录音",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (play_statue==play_begin) {
//				inpopwindow.dismiss();
                        finish();
                        isshow=true;
                        isshow_small=true;
                    }else if (play_statue==play_end) {
                        if (indexcount>1) {
                            recordManager.stopRecording();
                            Message m=new Message();
                            m.what=RECODE_ED;
                            messagehandler.sendMessage(m);
//					indexcount=0;
//					inpopwindow.dismiss();
                            finish();
                            isshow=true;
                            isshow_small=true;
                        }else{
                            recordManager.cancelRecording();
                            Message m=new Message();
                            m.what=RECORD_NO;
                            messagehandler.sendMessage(m);
//					indexcount=0;
//					inpopwindow.dismiss();
                            finish();
                            isshow=true;
                            isshow_small=true;
                        }
                    }
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//				inpopwindow.dismiss();
                    finish();;
                    Message m=new Message();
                    m.what=RECORD_NO;
                    messagehandler.sendMessage(m);
                    isshow_small=true;
                }
            });
//		inpopwindow=new PopupWindow(layout,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
//		inpopwindow.showAtLocation(ll_recording, Gravity.CENTER, 0, 0);
//		Button  btn_true=(Button) layout.findViewById(R.id.btn_true);
//		Button  btn_false=(Button) layout.findViewById(R.id.btn_false);
//		btn_true.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (play_statue==play_begin) {
//					inpopwindow.dismiss();
//					popwindow.dismiss();
//					isshow=true;
//					isshow_small=true;
//				}else if (play_statue==play_end) {
//					if (indexcount>1) {
//						recordManager.stopRecording();
//						Message m=new Message();
//						m.what=RECODE_ED;
//						messagehandler.sendMessage(m);
////						indexcount=0;
//						inpopwindow.dismiss();
//						popwindow.dismiss();
//						isshow=true;
//						isshow_small=true;
//					}else{
//						recordManager.cancelRecording();
//						Message m=new Message();
//						m.what=RECORD_NO;
//						messagehandler.sendMessage(m);
////						indexcount=0;
//						inpopwindow.dismiss();
//						popwindow.dismiss();
//						isshow=true;
//						isshow_small=true;
//					}
//				}
//
//			}
//		});
//		btn_false.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				inpopwindow.dismiss();
//				popwindow.dismiss();
//				Message m=new Message();
//				m.what=RECORD_NO;
//				messagehandler.sendMessage(m);
//			}
//		});
        }
    }

    final class MessagHandler extends Handler{
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (!Thread.currentThread().isInterrupted()) {
//					tv_time.setText(indexcount+"''");
                        if (indexcount>=10) {
                            tv_time_left.setText("00:00:"+indexcount);
                        }else{
                            tv_time_left.setText("00:00:0"+indexcount);
                        }
                    }
                    break;
                case 2:
                    showToast("录音结束");
//				pro_keepdariy.setProgress(0);
//                    tv_time.setText(indexcount+"''");
                    file = recordManager.getRecordFilePath(AppServer.getInstance().getAccountInfo().getUid()+"");
                    isrun=true;
//                    recording_play.setVisibility(View.VISIBLE);
//                    tv_time.setVisibility(View.VISIBLE);
                    play_statue=play_begin;
                    Intent data=new Intent();
                    data.putExtra("file", file);
                    setResult(20, data);
//                   popwindow.dismiss();
                    finish();;
                    isshow=true;
                    break;
                case 3:
//				pro_keepdariy.setProgress(0);
//                    tv_time.setText("");
//                    recording_play.setVisibility(View.GONE);
//                    tv_time.setVisibility(View.GONE);
                    play_statue=play_begin;
                    finish();
                    isshow=true;
                    break;
                case 0:
                    play_statue=play_begin;
//                    tv_time.setText("");
//                    recording_play.setVisibility(View.GONE);
                default:
                    break;
            }
        }
    }
}
