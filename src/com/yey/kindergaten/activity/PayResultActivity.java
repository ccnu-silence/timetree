package com.yey.kindergaten.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.net.OnAppRequestListener;
import com.yey.kindergaten.util.UtilsLog;

public class PayResultActivity extends BaseActivity implements View.OnClickListener{

    @ViewInject(R.id.header_title) TextView header_title;
    @ViewInject(R.id.left_btn) ImageView left_btn;
    @ViewInject(R.id.btn_result_back) Button btn_result_back; // 返回btn
    @ViewInject(R.id.tv_pay_result) TextView tv_pay_result; // 支付结果
    @ViewInject(R.id.tv_order_number) TextView tv_order_number; // 订单号
    @ViewInject(R.id.tv_service_name) TextView tv_service_name; // 服务名称
    @ViewInject(R.id.tv_service_price) TextView tv_service_price; // 服务价格
    @ViewInject(R.id.tv_pay_style) TextView tv_pay_style; // 支付方式

    private String order_number;  // 订单号
    private String service_name; // 服务名称
    private String service_price; // 服务价格
    private String pay_style; // 支付方式
    private String pay_result; // 支付状态

    private AccountInfo accountInfo;
    private final static String TAG = "PayResultActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_result);
        ViewUtils.inject(this);
        accountInfo = AppServer.getInstance().getAccountInfo();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        UtilsLog.i(TAG, "to openVIP uid: " + accountInfo.getUid() + "ordernumber:" + order_number);
        if (pay_result!=null && pay_result.equals("true")) {
            AppServer.getInstance().openVIP(accountInfo.getUid(), order_number, new OnAppRequestListener() {
                @Override
                public void onAppRequest(int code, String message, Object obj) {
                    if (code == AppServer.REQUEST_SUCCESS) {
                        UtilsLog.i(TAG, "openvip success");
//                      showToast("成功开通vip" + order_number);
                    } else {
//                      showToast(message);
                        UtilsLog.i(TAG, "openvip fail");
                    }
                }
            });
        }
    }

    private void initView() {
        header_title.setText("支付成功");
        left_btn.setOnClickListener(this);
        btn_result_back.setOnClickListener(this);
        if (getIntent().getExtras()!=null) {
            order_number = getIntent().getExtras().getString("order_number");
            service_name = getIntent().getExtras().getString("service_name");
            service_price = getIntent().getExtras().getString("service_price");
            pay_style = getIntent().getExtras().getString("pay_style");
            pay_result = getIntent().getExtras().getString("pay_result");
        }

        if (pay_result!=null && pay_result.equals("true")) {
            tv_pay_result.setText("支付成功！交易订单号为：");
        } else if (pay_result!=null && pay_result.equals("false")) {
            tv_pay_result.setText("支付失败");
            tv_pay_result.setTextColor(getResources().getColor(R.color.red));
        }

        tv_order_number.setText(order_number == null ? "" : order_number);
        tv_service_name.setText(service_name == null ? "" : service_name);
        tv_service_price.setText(service_price == null ? "" : service_price);
        tv_pay_style.setText(pay_style == null ? "" : pay_style);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_btn:
                PayResultActivity.this.finish();
                break;
            case R.id.btn_result_back:
                PayResultActivity.this.finish();
                break;
            default:
                break;
        }
    }

}
