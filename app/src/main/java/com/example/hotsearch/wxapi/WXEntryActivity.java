package com.example.hotsearch.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 注意：这里需要调用 handleIntent 方法，并且第三个参数是 this
        // App.getWXApi().handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        // 微信发送请求到你的应用，可以在这里处理
    }

    @Override
    public void onResp(BaseResp baseResp) {
        Logger.d("微信分享回调, errCode: %d", baseResp.errCode);
        String result;
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = "分享成功";
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "取消分享";
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "分享被拒绝";
                break;
            default:
                result = "分享返回";
                break;
        }
        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
        finish();
    }
}
