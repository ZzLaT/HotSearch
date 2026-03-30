package com.example.hotsearch.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;

import com.example.hotsearch.R;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

public class ShareUtils {

    // 替换为你的 AppID
    private static final String WX_APP_ID = "YOUR_WECHAT_APPID";
    private static final String QQ_APP_ID = "YOUR_QQ_APPID";

    private static IWXAPI iwxapi;
    private static Tencent tencent;

    public static void init(Context context) {
        // 微信
        iwxapi = WXAPIFactory.createWXAPI(context.getApplicationContext(), WX_APP_ID, true);
        iwxapi.registerApp(WX_APP_ID);

        // QQ
        tencent = Tencent.createInstance(QQ_APP_ID, context.getApplicationContext());
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

    /**
     * 分享到 QQ
     * @param context Context
     * @param url 链接
     * @param title 标题
     * @param description 描述
     */
    public static void shareToQQ(Context context, String url, String title, String description) {
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, description);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "https://imgcache.qq.com/qzone/space_item/pre/0/66768.gif"); // 示例图片
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, context.getString(R.string.app_name));

        tencent.shareToQQ((android.app.Activity) context, params, new IUiListener() {
            @Override
            public void onComplete(Object o) {
                Toast.makeText(context, "分享成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(UiError e) {
                Toast.makeText(context, "分享失败: " + e.errorMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(context, "取消分享", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onWarning(int i) {

            }
        });
    }

    private static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
}
