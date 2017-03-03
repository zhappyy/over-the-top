package com.xiaomi.miui.overthetop;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.xiaomi.miui.paronamo.SensorInfo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.UTFDataFormatException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by zhangyong on 17-3-1.
 */

public class SocketServer
{
    private ServerSocket server;
    private Socket socket;
    private InputStream in;
    private ObjectInputStream mInObject;
    private ObjectOutputStream mOutObject;

    private String str=null;
    private boolean isClint=false;
    private Handler mServerHandler;

    /**
     * @steps bind();绑定端口号
     * @effect 初始化服务端
     * @param port 端口号
     * */
    public SocketServer(int port) {
        try {
            server= new ServerSocket ( port );
            Log.e("zy", "server is null :" + (server ==null));
            isClint=true;
        }catch (IOException e){
            e.printStackTrace ();
        }
    }

    /**
     * @steps listen();
     * @effect socket监听数据
     * */
    public void beginListen() {
        new Thread ( new Runnable () {
            @Override
            public void run() {
                try {
                    /**
                     * accept();
                     * 接受请求
                     * */
                    Log.e("zy", "socket server:" + (server == null));
                    socket=server.accept();
                    Log.e("zy", "socket socket:" + (socket == null));
                    /**得到输入流*/
                    mInObject = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                    mOutObject = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                    /**
                     * 实现数据循环接收
                     **/
                    Log.e("zy", "mInObject:" + (mInObject == null));
                    while (!socket.isClosed()) {
                        try{
                            SensorInfo sensorInfo = (SensorInfo) mInObject.readObject();
                            Log.e("zy", "sensorInfo :" + sensorInfo.getSensorX());
                            if (sensorInfo != null) {
                                returnMessage(sensorInfo);
                            } else
                                break;
                        } catch (Exception e) {
                            socket.close();
                            break;
                        }
                    }
                } catch (IOException e) {
                        e.printStackTrace ( );
                        socket.isClosed ();
                }
            }
        } ).start ();
    }

    public void disconnectSocket() {
        if (null != server) {
            try {
                server.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @steps write();
     * @effect socket服务端发送信息
     * */
    public void sendMessage(final SensorInfo sensorInfo)
    {
        Thread thread=new Thread ( new Runnable ( )
        {
            @Override
            public void run()
            {
                try {
                    /*PrintWriter out=new PrintWriter ( socket.getOutputStream () );
                    out.print ( chat );
                    out.flush ();*/
                    mOutObject.writeObject(sensorInfo);
                    mOutObject.flush();
                } catch (IOException e) {
                    e.printStackTrace ( );
                }
            }
        } );
        thread.start ();
    }

    /**
     * @steps read();
     * @effect socket服务端得到返回数据并发送到主界面
     * */
    public void returnMessage(SensorInfo sensorInfo) {
        Message msg=new Message ();
        msg.obj=sensorInfo;
        mServerHandler.sendMessage ( msg );
    }

    public void setHandler(Handler handler) {
        mServerHandler = handler;
    }
}
