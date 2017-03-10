package com.xiaomi.miui.myapplication;

import android.app.Application;

/**
 * Created by mi on 17-3-10.
 */
public class ClientApplication extends Application {

    public static SocketTcpClient mClient;
    @Override
    public void onCreate() {
        super.onCreate();
        mClient = new SocketTcpClient();
        //服务端的IP地址和端口号
        mClient.clintValue (this, Global.SERVER_IP, 6666);
        //开启客户端接收消息线程
        mClient.openClientThread();
    }
}
