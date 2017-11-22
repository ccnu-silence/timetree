/**
 * 时光树
 * com.yey.kindergaten.widget
 * BottomMenuView.java
 * 
 * 2014年7月7日-下午4:10:29
 *  2014中幼信息科技公司-版权所有
 * 
 */
package com.yey.kindergaten.widget;

import com.yey.kindergaten.R;

import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 自定义菜单
 * BottomMenuView
 * chaowen
 * 511644784@qq.com
 * 2014年7月7日 下午4:10:29
 * @version 1.0.0
 * 
 */
public class BottomMenuView extends FrameLayout{	
	  private ImageView imageExpand;
	  private ImageView imageTabline;
      private ImageView menuTrigle;
	  private TextView textTitle;
	  public BottomMenuView(Context paramContext)
	  {
	    super(paramContext);
	    setupViews();
	  }
	  
	  public BottomMenuView(Context paramContext, AttributeSet paramAttributeSet)
	  {
	    super(paramContext, paramAttributeSet);
	    setupViews();
	  }
	  
	  private void setupViews()
	  {
	    View localView = View.inflate(getContext(), R.layout.bottom_menu, this);
	    this.imageTabline = ((ImageView)localView.findViewById(R.id.imageMenuTabLine));
	    this.textTitle = ((TextView)localView.findViewById(R.id.textMenuTitle));
	    this.imageExpand = ((ImageView)localView.findViewById(R.id.imageMenuExpand));
        this.menuTrigle= (ImageView) localView.findViewById(R.id.menu_triangeliv);
	  }
	  
	  public void setExpandable(boolean paramBoolean)
	  {
	    if (paramBoolean) {
	      this.imageExpand.setVisibility(View.VISIBLE);
	    }else{
	    	this.imageExpand.setVisibility(View.GONE);
	    }
	   
	  }

      public void setIshaveSub(Boolean ishave){
          if(ishave){
              menuTrigle.setVisibility(View.VISIBLE);
          }else{
              menuTrigle.setVisibility(View.GONE);
          }
      }
	  
	  public void setExpanded(boolean paramBoolean)
	  {
	    if (paramBoolean) {
	      this.imageExpand.setImageResource(R.drawable.icon_menuitem_expand_down);
	    }else{
	    	 this.imageExpand.setImageResource(R.drawable.icon_menuitem_expand_up);
	    }
	    
	  }
	  
	  public void setNeedTabLine(boolean paramBoolean)
	  {
	    if (paramBoolean) {
	      this.imageTabline.setVisibility(View.VISIBLE);
	    }else{
	    	this.imageTabline.setVisibility(View.INVISIBLE);
	    }
	   
	  }
	  
	  public void setTextSize(float paramFloat)
	  {
	    this.textTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, paramFloat);
	  }
	  
	  public void setTitle(String paramString)
	  {
	    if (!TextUtils.isEmpty(paramString)) {
	      this.textTitle.setText(paramString);
	    }
	  }
	  
	 
}
