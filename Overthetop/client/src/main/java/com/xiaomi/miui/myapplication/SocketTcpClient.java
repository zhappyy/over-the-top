package com.xiaomi.miui.myapplication;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.xiaomi.miui.paronamo.SensorInfo;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by zhangyong on 17-3-1.
 */

public class SocketTcpClient
{
    private Socket client;
    private Context context;
    private int port;           //IP
    private String site;            //端口
    private Thread thread;
    private Handler mHandler;
    private boolean isClient=false;
    private PrintWriter out;
    private InputStream in;
    private String str;

    private ObjectInputStream mInObject;
    private ObjectOutputStream mOutObject;
    /**
     * @effect 开启线程建立连接开启客户端
     * */
    public void openClientThread(){
        thread=new Thread ( new Runnable ( ) {
            @Override
            public void run() {
                try {
                    /**
                     *  connect()步骤
                     * */
                    Log.e("zy", "client:" + (client == null));
                    client=new Socket ( site,port );
                    Log.e("zy", "client:" + (client == null));
//                    client.setSoTimeout ( 5000 );//设置超时时间
                    if (client.isConnected()) {
                        isClient=true;
                        forOut();
//                        forIn ();
                    }else {
                        Log.e("Hunter", "connect fail");
                        isClient=false;
                    }
                }catch (UnknownHostException e) {
                    e.printStackTrace ();
                    Log.i ( "socket","6" );
                }catch (IOException e) {
                    e.printStackTrace ();
                    Log.i ( "socket","7" );
                }

            }
        } );
        thread.start ();
    }

    /**
     * 调用时向类里传值
     * */
    public void clintValue(Context context,String site,int port)
    {
        this.context=context;
        this.site=site;
        this.port=port;
    }

    /**
     * @effect 得到输出字符串
     * */
    public void forOut()
    {
        try {
            out=new PrintWriter ( client.getOutputStream () );
            mOutObject = new ObjectOutputStream(client.getOutputStream());
        }catch (IOException e){
            e.printStackTrace ();
            Log.i ( "socket","8" );
        }
    }

    /**
     * @steps read();
     * @effect 得到输入字符串
     * */
    public void forIn(){

        while (isClient) {
            SensorInfo sensorInfo = null;
            try {
                mInObject = new ObjectInputStream(new BufferedInputStream(client.getInputStream()));
                sensorInfo = (SensorInfo) mInObject.readObject();

            } catch (IOException e) {} catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            if (sensorInfo != null) {
                Message msg = new Message();
                msg.obj = sensorInfo;
                mHandler.sendMessage(msg);
            }

        }
    }

    /**
     * @steps write();
     * @effect 发送消息
     * */
    public void sendMsg(final/* String  str*/SensorInfo sensorInfo)
    {
        new Thread ( new Runnable ()
        {
            @Override
            public void run()
            {
                try {
                    if (client == null) {
                        client = new Socket(site, port);
                    }
                    if (!client.isConnected()) {
                        client.connect(new InetSocketAddress(site, port));
                    }
                    Log.e("Hunter", "client sendMessage");
                    mOutObject.writeObject(sensorInfo);
                    mOutObject.flush();
//                        mOutObject.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } ).start ();

    }

    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    public void close() {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}