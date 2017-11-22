package com.yey.kindergaten.wxapi;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.pay.wechat.WechatConstants;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.Session;
import com.yey.kindergaten.util.UtilsLog;

/**
 * 微信支付完成后回调类
 * 此类必须放在包名 + wxapi之下
 * @author lhd
 */
public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler, View.OnClickListener {

    @ViewInject(R.id.header_title)
    TextView header_title;
    @ViewInject(R.id.left_btn)
    ImageView left_btn;
    @ViewInject(R.id.btn_result_back)
    Button btn_result_back;
    @ViewInject(R.id.tv_pay_result) TextView tv_pay_result; // 支付结果
    @ViewInject(R.id.tv_order_number) TextView tv_order_number; // 订单号
    @ViewInject(R.id.tv_service_name) TextView tv_service_name; // 服务名称
    @ViewInject(R.id.tv_service_price) TextView tv_service_price; // 服务价格
    @ViewInject(R.id.tv_pay_style) TextView tv_pay_style; // 支付方式

    private String order_number;  // 订单号
    private String service_name; // 服务名称
    private String service_price; // 服务价格
    private String pay_style; // 支付方式
    private AccountInfo accountInfo;
    private IWXAPI api;

    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_result);
        ViewUtils.inject(this);
        accountInfo = AppServer.getInstance().getAccountInfo();
        initView();

        api = WXAPIFactory.createWXAPI(this, WechatConstants.APP_ID);
        api.handleIntent(getIntent(), this);
    }

    private void initView() {
//      header_title.setText("支付成功");
        left_btn.setOnClickListener(this);
        btn_result_back.setOnClickListener(this);

        Session session = Session.getSession();
        order_number = (String) session.get(AppConstants.SESSION_ORDERNUMBER);
        service_name = (String) session.get(AppConstants.SESSION_SERVICENAME);
        service_price = (String) session.get(AppConstants.SESSION_SERVICEPRICE);
        pay_style = (String) session.get(AppConstants.SESSION_PAYSTYLE);

        tv_order_number.setText(order_number == null ? "" : order_number);
        tv_service_name.setText(service_name == null ? "" : service_name);
        tv_service_price.setText(service_price == null ? "" : service_price);
        tv_pay_style.setText(pay_style == null ? "" : pay_style);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (BaseResp.ErrCode.ERR_OK == resp.errCode) {                // 成功
                header_title.setText("支付成功");
                tv_pay_result.setText("支付成功！交易订单号为：");
                AppServer.getInstance().openVIP(accountInfo.getUid(), order_number, new OnAppRequestListener() {
                    @Override
                    public void onAppRequest(int code, String message, Object obj) {
                        if (code == AppServer.REQUEST_SUCCESS) {
//                            showToast("成功开通vip" + order_number);
                            UtilsLog.i(TAG, "openvip success");
                        } else {
//                            showToast(message);
                            UtilsLog.i(TAG, "openvip fail");
                        }
                    }
                });
            } else if (BaseResp.ErrCode.ERR_USER_CANCEL == resp.errCode){ // 取消支付
                header_title.setText("取消支付");
                tv_pay_result.setText("取消支付");
                tv_pay_result.setTextColor(getResources().getColor(R.color.orange));
            } else {                                                      // 支付失败
                header_title.setText("支付失败");
                tv_pay_result.setText("支付失败");
                tv_pay_result.setTextColor(getResources().getColor(R.color.red));
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.app_tip);
                builder.setMessage(getString(R.string.pay_result_callback_msg, resp.errStr +";code=" + String.valueOf(resp.errCode)));
                builder.show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_btn:
                WXPayEntryActivity.this.finish();
                break;
            case R.id.btn_result_back:
                WXPayEntryActivity.this.finish();
                break;
            default:
                break;
        }
    }
}