package com.example.app_server;

import android.app.Application;
import com.kakao.sdk.common.KakaoSdk;

public class GlobalApplication extends Application {
    private static GlobalApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // 네이티브 앱 키로 초기화
        KakaoSdk.init(this, "1d41520624a8d632dca6f98e7391750c");
    }
}