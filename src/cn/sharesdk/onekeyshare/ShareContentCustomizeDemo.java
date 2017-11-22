/*
 * 官网地站:http://www.ShareSDK.cn
 * 技术支持QQ: 4006852216
 * 官方微信:ShareSDK   （如果发布新版本的话，我们将会第一时间通过微信将版本更新内容推送给您。如果使用过程中有任何问题，也可以通过微信与我们取得联系，我们将会在24小时内给予回复）
 *
 * Copyright (c) 2013年 ShareSDK.cn. All rights reserved.
 */

package cn.sharesdk.onekeyshare;

import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * 快捷分享项目现在添加为不同的平台添加不同分享内容的方法。 本类用于演示如何区别Twitter的分享内容和其他平台分享内容。 本类会在
 * {@link DemoPage#showShare(boolean, String)}方法 中被调用。
 */
public class ShareContentCustomizeDemo implements ShareContentCustomizeCallback {

	public void onShare(Platform platform, ShareParams paramsToShare) {
		// 改写短信分享内容中的text字段
		if ("ShortMessage".equals(platform.getName())) {
			String text = "分享一个好玩好用的免费APP给你。把幼儿园装进手机，一切尽在“掌”握。方便实用的管理工具,丰富的教育资源,人性化的家园沟通平台。把你从日常的琐碎中解放出来，让您的工作更轻松！赶快来下载吧！下载地址：http://sgs.yey.com";
			paramsToShare.setText(text);
            paramsToShare.setTitle("");
            paramsToShare.setAddress("");

		}
	}

}
