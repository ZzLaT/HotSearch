package com.example.hotsearch.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.example.hotsearch.BuildConfig;
import com.example.hotsearch.R;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class ShareUtils {

    private static final String WX_APP_ID = BuildConfig.WECHAT_APP_ID;

    private static IWXAPI iwxapi;

    public static void init(Context context) {
        // 微信
        iwxapi = WXAPIFactory.createWXAPI(context.getApplicationContext(), WX_APP_ID, true);
        iwxapi.registerApp(WX_APP_ID);
    }

    /**
     * 分享到微信
     * @param context Context
     * @param url 链接
     * @param title 标题
     * @param description 描述
     * @param isTimeline 是否分享到朋友圈
     */
    public static void shareToWechat(Context context, String url, String title, String description, boolean isTimeline) {
        if (!iwxapi.isWXAppInstalled()) {
            Toast.makeText(context, "您还未安装微信客户端", Toast.LENGTH_SHORT).show();
            return;
        }

        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;

        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = description;
        Bitmap thumb = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        msg.thumbData = BitmapUtil.bmpToByteArray(thumb, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = isTimeline ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;

        iwxapi.sendReq(req);
    }

    private static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
}
