package com.yey.kindergaten.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.OrderInfo;
import com.yey.kindergaten.bean.WxEntity;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.pay.alipay.AlipayUtil;
import com.yey.kindergaten.pay.alipay.PayResult;
import com.yey.kindergaten.pay.wechat.WxPayUtil;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.Session;

public class OpenHealthServicesActivity extends BaseActivity implements View.OnClickListener{

    private RelativeLayout select_topay_ll; // 去支付
    private LinearLayout select_alipay_ll; // 选择支付宝支付
    private LinearLayout select_wechat_ll; // 选择微信支付
    private ImageView icon_alipay;
    private ImageView icon_wechat;
    @ViewInject(R.id.left_btn) ImageView left_btn;
    private TextView header_title; // 标题
    private TextView tv_service_name; // 服务名称
    private TextView tv_service_price; // 价格
    private TextView tv_child_name; // 小朋友名字
    private TextView tv_kindergarten_name; // 幼儿园名称
    private TextView tv_class_name; // 班级名称
    private TextView tv_summary_price; // 汇总价格
    private AccountInfo accountInfo;
    private String service_feename = "";
    private String service_feeid = "";
    private String service_price = "";
    private String service_desc = "";

    private OrderInfo putOrderInfo = null;

    private static final int SDK_PAY_FLAG = 1;

    private static final int SDK_CHECK_FLAG = 2;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);

                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();

                    String resultStatus = payResult.getResultStatus();

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(OpenHealthServicesActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        Intent resultintent = new Intent(OpenHealthServicesActivity.this, PayResultActivity.class);
                        resultintent.putExtra("order_number", putOrderInfo == null? "" : putOrderInfo.getOrderNo()); // 订单号
                        resultintent.putExtra("service_name", putOrderInfo == null? "" : putOrderInfo.getFeeName()); // 服务名称
                        resultintent.putExtra("service_price", ("").equals(service_price) ? putOrderInfo.getAmount() : service_price ); // 服务价格
                        resultintent.putExtra("pay_result", "true"); // 支付状态
                        resultintent.putExtra("pay_style", "支付宝"); // 支付方式
                        startActivity(resultintent);
                        OpenHealthServicesActivity.this.finish();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(OpenHealthServicesActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent resultintent = new Intent(OpenHealthServicesActivity.this, PayResultActivity.class);
                            resultintent.putExtra("order_number", putOrderInfo == null? "" : putOrderInfo.getOrderNo()); // 订单号
                            resultintent.putExtra("service_name", putOrderInfo == null? "" : putOrderInfo.getFeeName()); // 服务名称
//                            resultintent.putExtra("service_price", putOrderInfo == null? "" : putOrderInfo.getAmount()); // 服务价格
                            resultintent.putExtra("service_price", ("").equals(service_price) ? putOrderInfo.getAmount() : service_price ); // 服务价格
                            resultintent.putExtra("pay_result", "false"); // 支付状态
                            resultintent.putExtra("pay_style", "支付宝"); // 支付方式
                            startActivity(resultintent);
                            OpenHealthServicesActivity.this.finish();
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(OpenHealthServicesActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                }
                case SDK_CHECK_FLAG: { // 查询终端设备是否存在支付宝认证账户返回
                    Toast.makeText(OpenHealthServicesActivity.this, "检查结果为：" + msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                }
                default:
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_healthservices);
        ViewUtils.inject(this);
        accountInfo = AppServer.getInstance().getAccountInfo();
        if (getIntent().getExtras()!=null) {
            service_feename = getIntent().getExtras().getString("feename");
            service_feeid = getIntent().getExtras().getString("feeid");
            service_price = getIntent().getExtras().getString("price");
            service_desc = getIntent().getExtras().getString("desc");
        }
        initView();
    }

    private void initView() {
        select_topay_ll = (RelativeLayout) findViewById(R.id.select_topay_ll);
        select_topay_ll.setOnClickListener(this);
        select_alipay_ll = (LinearLayout) findViewById(R.id.select_alipay_ll);
        select_alipay_ll.setOnClickListener(this);
        select_wechat_ll = (LinearLayout) findViewById(R.id.select_wechat_ll);
        select_wechat_ll.setOnClickListener(this);
        icon_alipay = (ImageView) findViewById(R.id.icon_alipay);
        icon_wechat = (ImageView) findViewById(R.id.icon_wechat);
        header_title = (TextView) findViewById(R.id.header_title);
        tv_service_name = (TextView) findViewById(R.id.tv_service_name);
        tv_service_price = (TextView) findViewById(R.id.tv_service_price);
        tv_child_name = (TextView) findViewById(R.id.tv_child_name);
        tv_kindergarten_name = (TextView) findViewById(R.id.tv_kindergarten_name);
        tv_class_name = (TextView) findViewById(R.id.tv_class_name);
        tv_summary_price = (TextView) findViewById(R.id.tv_summary_price);
        icon_alipay.setImageResource(R.drawable.icon_has_selected); // 默认选择支付宝支付

        header_title.setText(service_feename);
        tv_service_name.setText(service_feename);
        tv_service_price.setText(service_desc);
        tv_child_name.setText(accountInfo.getRealname());
        tv_kindergarten_name.setText(accountInfo.getKname());
        tv_class_name.setText(accountInfo.getCname());
        tv_summary_price.setText("¥ " + service_price);
        header_title.setVisibility(View.VISIBLE);

        left_btn.setVisibility(View.VISIBLE);
        left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenHealthServicesActivity.this.finish();
            }
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    private boolean isAlipay = true; // 是否是支付宝支付
    private boolean isWechat = false; // 是否是微信支付

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.select_topay_ll: // 去支付
                if (!isAlipay && !isWechat) {
                    Toast.makeText(OpenHealthServicesActivity.this, "请选择支付方式", Toast.LENGTH_LONG).show();
                } else if (isAlipay && !isWechat) { // 支付宝支付
                    toPayByAlipay();
                } else if (isWechat && !isAlipay) { // 微信支付
                    toPayByWechat();
                }
                break;
            case R.id.select_alipay_ll: // 选择支付宝支付
                icon_alipay.setImageResource(R.drawable.icon_has_selected);
                icon_wechat.setImageResource(R.color.white);
                isAlipay = true;
                isWechat = false;
                break;
            case R.id.select_wechat_ll: // 选择微信支付
                if (AppContext.getInstance().getMsgApi().isWXAppInstalled()) {
                    icon_wechat.setImageResource(R.drawable.icon_has_selected);
                    icon_alipay.setImageResource(R.color.white);
                    isWechat = true;
                    isAlipay = false;
                } else {
                    Toast.makeText(OpenHealthServicesActivity.this, "对不起，您的手机未安装微信，不能使用微信支付。", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    public void toPayByAlipay() {
        showLoadingDialog("正在创建订单...");
        if (accountInfo!=null && accountInfo.getUid()!=0) {
            if (service_feeid == null || service_feeid.equals("")) {
                showToast("服务信息丢失，请返回个人资料再试");
                return;
            }
            AppServer.getInstance().createAlipayOrder(accountInfo.getUid(), service_feeid.trim() + "", new OnAppRequestListener() {
                @Override
                public void onAppRequest(int code, String message, Object obj) {
                    cancelLoadingDialog();
                    if (code == AppServer.REQUEST_SUCCESS) {
                        OrderInfo orderInfo = (OrderInfo) obj;
                        payByAlipay(orderInfo);
                    } else {
                        showToast(message);
                    }
                }
            });
        } else {
            showToast("登录信息出错，请重新登录");
        }
    }
    private void payByAlipay(OrderInfo orderInfo) {
        // 支付宝支付
        if (orderInfo!=null && orderInfo.getOrderNo()!=null) {

            putOrderInfo = orderInfo;

            AlipayUtil alipay = new AlipayUtil(orderInfo);
            alipay.payByAlipay(this, mHandler);
        } else {
            showToast("获取订单失败,请重试");
        }
    }

    public void toPayByWechat() {
        showLoadingDialog("正在创建订单...");
        if (accountInfo!=null && accountInfo.getUid()!=0) {
            if (service_feeid == null || service_feeid.equals("")) {
                showToast("服务信息丢失，请返回个人资料再试");
                return;
            }
            AppServer.getInstance().createWechatOrder(accountInfo.getUid(), service_feeid.trim() + "", new OnAppRequestListener() {
                @Override
                public void onAppRequest(int code, String message, Object obj) {
                    cancelLoadingDialog();
                    if (code == AppServer.REQUEST_SUCCESS) {
                        WxEntity entity = (WxEntity) obj;
                        payByWechat(entity);
                    } else {
                        showToast(message);
                    }
                }
            });
        } else {
            showToast("登录信息出错，请重新登录");
        }
    }
    private void payByWechat(WxEntity entity) {
        // 微信支付
        if (entity!=null && entity.getOut_trade_no()!=null) {

            Session session = Session.getSession();
            session.put(AppConstants.SESSION_ORDERNUMBER, entity.getOut_trade_no());
            session.put(AppConstants.SESSION_SERVICENAME, entity.getFee_name());
//            session.put(AppConstants.SESSION_SERVICEPRICE, entity.getFee_money());
            session.put(AppConstants.SESSION_SERVICEPRICE, ("").equals(service_price) ? putOrderInfo.getAmount() : service_price );
            session.put(AppConstants.SESSION_PAYSTYLE, "微信");

            WxPayUtil wxpay = new WxPayUtil(entity);
            wxpay.sendPayReq();
            OpenHealthServicesActivity.this.finish();
        } else {
            showToast("获取订单失败,请重试");
        }
    }

}
