package com.xiaomi.miui.overthetop;

import android.app.Application;

/**
 * Created by mi on 17-3-10.
 */
public class ServerApplication extends Application {

    public static SocketServer server;

    @Override
    public void onCreate() {
        super.onCreate();
        server = new SocketServer(6666);
        server.beginListen();
    }
}
