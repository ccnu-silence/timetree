package com.yey.kindergaten.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.umeng.analytics.MobclickAgent;
import com.yey.kindergaten.AppContext;
import com.yey.kindergaten.BaseActivity;
import com.yey.kindergaten.MainActivity;
import com.yey.kindergaten.R;
import com.yey.kindergaten.bean.AccountInfo;
import com.yey.kindergaten.bean.MessageRecent;
import com.yey.kindergaten.bean.PublicAccount;
import com.yey.kindergaten.bean.Services;
import com.yey.kindergaten.db.DbHelper;
import com.yey.kindergaten.net.AppServer;
import com.yey.kindergaten.util.AppConstants;
import com.yey.kindergaten.util.AppUtils;
import com.yey.kindergaten.util.GlideUtils;
import com.yey.kindergaten.util.Session;
import com.yey.kindergaten.util.SharedPreferencesHelper;
import com.yey.kindergaten.widget.CirclePageIndicator;

/**
 * Created by zy on 14-1-3.
 */
public class WizardActivity extends BaseActivity {

    private int formId;
    private MessageRecent message;
    int[] mWizard;
    Session session;
    private String type;
    private PublicAccount publicAccount;
    private int isFirstLook;
    /**服务引导首页打开方式*/
    private int openState; // 1表示网页，2表示其他
    private Services services;
    private int showDraw;
    private int fromServicerole;
    private ImageView iv;
    private int isFirstLogin; // 0表示第一次登陆，1表示不是第一次登陆

    private LinearLayout notice_ll;
    private WizardAdapter wizardAdapter = null;
    private Bitmap bitmap = null;

    @Override
    protected void onDestroy() {
        // imageLoader.clearMemoryCache();
        super.onDestroy();
//        System.gc();
        message = null;
        session = null;
        type = null;
        publicAccount = null;
        services = null;

//        if (iv !=  null &&  iv.getDrawable() != null) {
//            Bitmap oldBitmap =  ((BitmapDrawable) iv.getDrawable()).getBitmap();
//            iv.setImageDrawable(null);
//            if (oldBitmap != null && !oldBitmap.isRecycled()) {
//                oldBitmap.recycle();
//                oldBitmap =  null;
//            }
//        }
        if (bitmap!=null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        //  Other code.

//        System.gc();
        iv = null;
        notice_ll = null;
        wizardAdapter = null;
        System.gc();
        setContentView(R.layout.activity_null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 取消标题栏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                         WindowManager.LayoutParams. FLAG_FULLSCREEN); // 全屏

        setContentView(R.layout.activity_wizard);
        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.indicator);
        LinearLayout service_ll = (LinearLayout) findViewById(R.id.service_guide_ll);
        Button service_btn = (Button) findViewById(R.id.service_guide_btn);
        notice_ll = (LinearLayout) findViewById(R.id.service_notice_ll);
        service_btn.setText("开始使用");
        formId = getIntent().getIntExtra("fromdId", 0);
        type = getIntent().getStringExtra("type");
        fromServicerole = getIntent().getIntExtra("fromServicerole", -1);
        showDraw = getIntent().getIntExtra("showDraw", 0);
        message = (MessageRecent) getIntent().getSerializableExtra("message");
        isFirstLogin = SharedPreferencesHelper.getInstance(AppContext.getInstance()).getInt(AppConstants.isFirstLoginLook, 0);

        service_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openByState();
            }
        });

        AccountInfo info = AppServer.getInstance().getAccountInfo();
        if (type!=null && type.equals("fromLogin")) {
            SharedPreferencesHelper.getInstance(AppContext.getInstance()).setString(info.getUid() + "$", "1$" + info.getUid());
//          SharedPreferencesHelper.getInstance(AppContext.getInstance()).setInt(AppConstants.isFirstLoginLook,1);
        }

        if (type!=null && type.equals("fromService")) { // 用户服务引导显示
            services = (Services) getIntent().getSerializableExtra("service");
            openState = getIntent().getIntExtra("openState", 0);
            indicator.setVisibility(View.GONE);
            service_ll.setVisibility(View.VISIBLE);
            mWizard = new int[]{showDraw};
            if (wizardAdapter == null) {
                wizardAdapter = new WizardAdapter(this);
            }
            pager.setAdapter(wizardAdapter);
        } else { // 用户公众号引导显示
            try {
                publicAccount = DbHelper.getDB(AppContext.getInstance()).findFirst(PublicAccount.class, WhereBuilder.b("publicid", "=", formId));
            } catch (DbException e) {
                e.printStackTrace();
            }

            if (publicAccount!=null) {
                isFirstLook = publicAccount.getIsfirstlook();
                publicAccount.setIsfirstlook(1);
                try {
                    DbHelper.getDB(AppContext.getInstance()).update(publicAccount, WhereBuilder.b("publicid", "=", formId));
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }

        showWhatPage(formId);
        session = Session.getSession();
        if (wizardAdapter == null) {
            wizardAdapter = new WizardAdapter(this);
        }
        pager.setAdapter(wizardAdapter);
        indicator.setViewPager(pager);}

    }

    public void openByState(){
        Intent intent ;
        if (type!=null && type.equals("fromService")) {
            if (fromServicerole == 0 || fromServicerole == 1 || fromServicerole == 2) {
                setResult(fromServicerole + 1);
                this.finish();
                return;
            }
        }

        if (services!=null) {
            if (openState == 1) {
                intent = new Intent(WizardActivity.this, CommonBrowser.class);
                Bundle bundle = new Bundle();
                bundle.putString(AppConstants.INTENT_URL, AppUtils.replaceUrl(services.getUrl()));
                bundle.putString(AppConstants.INTENT_NAME, services.getName());
                bundle.putString(AppConstants.INTENT_SHOWTITLE, "1");
                if (services.getType() == 20) {
                    bundle.putInt(AppConstants.INTENT_FULL_SCREEN, services.getType()); // 是否全屏显示，针对一日流程是否全屏显示效果
                }
                intent.putExtras(bundle);
                startActivity(intent);
            } else if (openState == 2) {
                switch (services.getType()) {
                    case 2:
                        intent = new Intent(this, ServiceScheduleActivity.class);
                        startActivity(intent);
                        break;
                    case 12:
                        intent = new Intent(this, ServiceLifePhotoMainActivity.class);
                        intent.putExtra("type", "1");
                        startActivity(intent);
                        break;
                    case 13:
                        intent = new Intent(this, ServiceLifePhotoMainActivity.class);
                        intent.putExtra("type", "2");
                        startActivity(intent);
                        break;
                    case 14:
                        intent = new Intent(this, ClassPhotoMainActivity.class);
                        intent.putExtra("typefrom", AppConstants.FROMFRIENDSTER);
                        intent.putExtra("classphototype", AppConstants.HOMEACTIVITY);
                        startActivity(intent);
                        break;
                    case 15:
                        intent = new Intent(this, GrowthDiaryActivity.class);
                        startActivity(intent);
                        break;
                    case 18:
                        intent = new Intent(this, ServiceFriendsterActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        }
        this.finish();
    }

    public void showWhatPage(){
        if (publicAccount.getSubscription() == 1) {
            session.put(AppConstants.INTENT_KEY_FROMID, publicAccount.getPublicid() + "");
            session.put("state", AppConstants.PUACFRAGMENT_LOOKPUAC);
            session.put(AppConstants.INTENT_KEY_TYPEID, -1);
            Intent intent = new Intent(this, PublicAccountMessageList.class);
            intent.putExtra("type", "fromguide");
            intent.putExtra("isFirstLook", isFirstLook);
            startActivity(intent);
        } else if (publicAccount.getSubscription() == -1) {
            session.put(AppConstants.INTENT_KEY_FROMID, publicAccount.getPublicid() + "");
            session.put("state", AppConstants.PUACFRAGMENT_SPECIALPUAC);
            Intent intent=new Intent(this, PublicAccountMessageList.class);
            intent.putExtra("type", "fromguide");
            intent.putExtra("isFirstLook", isFirstLook);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this,ContactsPuacDatacardActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("state", AppConstants.PUACFRAGMENT_BOOKPUAC);
            bundle.putInt("role", 2);
            bundle.putInt(AppConstants.INTENT_KEY_TYPEID, -1);
            bundle.putInt("publicid", publicAccount.getPublicid());
            intent.putExtra("isFirstLook", isFirstLook);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    public void  showWhatPage(int fromId) {
        switch (fromId) {
            case AppConstants.TIMETREE_DIRECTOR_PUBLIC:
                mWizard = new int[]{R.drawable.directorr_pua_1, R.drawable.directorr_pua_2, R.drawable.directorr_pua_3, R.drawable.directorr_pua_4,
                        R.drawable.directorr_pua_5, R.drawable.directorr_pua_6};
                break;
            case AppConstants.TIMETREE_TEACHER_PUBLIC:
                mWizard = new int[]{R.drawable.teacher_pua_1, R.drawable.teacher_pua_2, R.drawable.teacher_pua_3, R.drawable.teacher_pua_4, R.drawable.teacher_pua_5,
                        R.drawable.teacher_pua_6, R.drawable.teacher_pua_7};
                break;
            case AppConstants.TIMETREE_PARENT_PUBLIC:
                mWizard = new int[]{R.drawable.parent_pua_1, R.drawable.parent_pua_2, R.drawable.parent_pua_3, R.drawable.parent_pua_4, R.drawable.parent_pua_5,
                        R.drawable.parent_pua_6, R.drawable.parent_pua_7};
                break;
            case AppConstants.TIMETREE_DO_DIRECTOR:
                mWizard = new int[]{R.drawable.timetree_director_1, R.drawable.timetree_director_2, R.drawable.timetree_director_3, R.drawable.timetree_director_4, R.drawable.timetree_director_5,
                        R.drawable.timetree_director_6, R.drawable.timetree_director_7};
                break;
            case AppConstants.TIMETREE_DO_TEACHER:
                mWizard = new int[]{R.drawable.timetree_teacher_1, R.drawable.timetree_teacher_2, R.drawable.timetree_teacher_3, R.drawable.timetree_teacher_4, R.drawable.timetree_teacher_5,
                        R.drawable.timetree_teacher_6, R.drawable.timetree_teacher_7, R.drawable.timetree_teacher_8};
                break;
            case AppConstants.TIMETREE_DO_PARENT:
                mWizard = new int[]{R.drawable.timetree_parent_1, R.drawable.timetree_parent_2, R.drawable.timetree_parent_3, R.drawable.timetree_parent_4, R.drawable.timetree_parent_5,
                        R.drawable.timetree_parent_6, R.drawable.timetree_parent_7};
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (type!=null && type.equals("fromLogin")) {
                AppContext.getInstance().addActivity(WizardActivity.this);
                Intent a = new Intent(WizardActivity.this, MainActivity.class);
                WizardActivity.this.startActivity(a);
            } else {
                this.finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    class WizardAdapter extends PagerAdapter {

        private Context mContext;
        public WizardAdapter(Context context){
            mContext = context;
        }
        @Override
        public int getCount() {
            return mWizard.length;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            iv = new ImageView(mContext);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            Integer biticon = (Integer) mWizard[position];

            try {
//                InputStream is = WizardActivity.this.getResources().openRawResource(biticon);
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inJustDecodeBounds = false;
//                options.inSampleSize = 10; // width, hight 设为原来的十分之一
//                Bitmap btp = BitmapFactory.decodeStream(is, null, options);
                bitmap = GlideUtils.readBitMap(mContext, biticon);
                iv.setImageBitmap(bitmap);
//                iv.setImageDrawable(this.mContext.getResources().getDrawable(biticon));
            } catch (Exception e){
                e.printStackTrace();
                Log.e("WizardActivity","图片设置失败");
            }

            // String imageUri = "drawable://" + mWizard[position];
            // ImageLoader.getInstance().displayImage(imageUri,iv);
            if (type == null) {
                type = "";
            }
            if (position == mWizard.length - 1) {
                if (type.equals("fromService")) {
                    iv.setOnClickListener(null);
                } else {
                    iv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (type == null|| type.equals("")) {
                                Intent inten = new Intent(WizardActivity.this, PublicAccountMessageList.class);
                                Bundle bundle = new Bundle();
                                session.put(AppConstants.INTENT_KEY_FROMID, message.getFromId() + "");
                                session.put(AppConstants.INTENT_KEY_TYPEID, message.getTypeid() == -1 ? -1 : message.getTypeid());
                                session.put(AppConstants.STATE, AppConstants.PUACFRAGMENT_LOOKPUAC);
                                bundle.putString(AppConstants.INTENT_KEY_FROMID, message.getFromId() + "");
                                bundle.putInt("isFirstLook", isFirstLook);
                                bundle.putString("type", "fromguide");
                                inten.putExtras(bundle);
                                WizardActivity.this.startActivity(inten);
                                WizardActivity.this.finish();
                            } else if (type.equals("fromPuaFragment")) {
                                showWhatPage();
                                WizardActivity.this.finish();
                            } else if (type.equals("fromLogin")) {
                                notice_ll.setVisibility(View.VISIBLE);
                                AppContext.getInstance().addActivity(WizardActivity.this);
                                Intent a = new Intent(WizardActivity.this, MainActivity.class);
                                WizardActivity.this.startActivity(a);
//                               WizardActivity.this.finish();
                            } else if (type.equals("from_PublicAccountMessage")) {
                                WizardActivity.this.finish();
                            } else {
                                WizardActivity.this.finish();
                            }
                        }
                    });
                }
            } else {
                iv.setOnClickListener(null);
            }
            container.addView(iv);
            return iv;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
//          container.removeViewAt(position);
        }
    }
    
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


}
