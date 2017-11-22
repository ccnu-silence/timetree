package com.yey.kindergaten.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.AccountBean;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.util.UtilsLog;


//dlf
public class MeModifyKinderActivity extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.header_title)
    TextView title;
    @ViewInject(R.id.left_btn)
    ImageView leftbtn;
    @ViewInject(R.id.newkindergarten)
    EditText newkindergarten;
    @ViewInject(R.id.modiffy_kname_submitbtn)
    Button submitbtn;
    private AccountInfo accountInfo;
    private  String knameold;
    private final static String TAG = "MeModifyKinderActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_modify_kinder);
        ViewUtils.inject(this);
        knameold = getIntent().getStringExtra("kname");
        accountInfo = AppServer.getInstance().getAccountInfo();
        initView();
    }


    private void initView(){
        title.setText("修改幼儿园名称");
        if (knameold!=null && !knameold.equals("")) {
            newkindergarten.setText(knameold);
            if (newkindergarten.getText()!=null) {
                newkindergarten.setSelection(newkindergarten.getText().toString().length());
            }
        }
        leftbtn.setVisibility(View.VISIBLE);
    }

    @OnClick({(R.id.modiffy_kname_submitbtn),(R.id.left_btn)})
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.modiffy_kname_submitbtn:
                //accountInfo = AppContext.getInstance().getAccountInfo();
                int uid = accountInfo.getUid();
                int kid = accountInfo.getKid();
                // String kName = accountInfo.getKname();
                final String newName = newkindergarten.getText().toString().trim();

                if (newName == null || newName.equals("")){
                    showToast("幼儿园名称不能为空不能为空");
                    return;
                }
                if (newName.length() < 3 || newName.length() > 20){
                    showToast("对不起,幼儿园名称需在3至20字之间");
                    return;
                }

                AppServer.getInstance().updateGartenName(uid, kid, newName, new OnAppRequestListener() {

                    @Override
                    public void onAppRequest(int code, String message, Object obj) {
                        if (code == 0) {
                            showToast("修改成功");
                            accountInfo.setKname(newName);
                            AppServer.getInstance().setmAccountInfo(accountInfo);
                            AppServer.getInstance().setmAccountBean(new AccountBean(accountInfo));
                            DbHelper.updateAccountInfo(accountInfo);
                            Intent intent = new Intent();
                            intent.putExtra("kname", newName);
                            UtilsLog.i(TAG,"has modified the kname : " + newName);
//                            setResult(5, intent);
                            MeModifyKinderActivity.this.finish();
                        } else {
                            showToast("修改失败");
                        }
                    }
                });
                break;
            case R.id.left_btn:
                Intent intent = new Intent();
//                setResult(-1, intent);
                MeModifyKinderActivity.this.finish();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
//            setResult(-1, intent);
            MeModifyKinderActivity.this.finish();
            return  false;
        }
        return  false;
    }

}
