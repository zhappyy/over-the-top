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
import java.util.HashMap;

/**
 * Created by zhangyong on 17-3-1.
 */

public class SocketServer
{
    private ServerSocket server;
    private InputStream in;
    private ObjectInputStream mInObject;
    private ObjectOutputStream mOutObject;

    private String str=null;
    private boolean isClint=false;
    private Handler mServerHandler;
    private Socket currentSocket;

    private HashMap<Socket, Handler> map = new HashMap<>();
    /**
     * @steps bind();绑定端口号
     * @effect 初始化服务端
     * @param port 端口号
     * */
    public SocketServer(int port) {
        try {
            server= new ServerSocket ( port );
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
                    while (true) {
                        Socket socket = server.accept();
                        Log.d("Hunter", "socket = " + socket.toString());
                        new Thread(new SocketRunnable(socket)).start();
                    }
                } catch (IOException e) {
                    e.printStackTrace ( );
                }
            }
        } ).start ();
    }

    private class SocketRunnable implements Runnable {

        private Socket socket;

        public  SocketRunnable(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                mInObject = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                while (true) {
                    SensorInfo sensorInfo = (SensorInfo) mInObject.readObject();
                    if (sensorInfo != null) {
                        returnMessage(sensorInfo);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnectSocket() {
        if (null != server) {
            try {
                server.close();
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
        Thread thread=new Thread ( new Runnable ()
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
        Log.e("zy", "returnMessage");
        Message msg=new Message ();
        msg.obj=sensorInfo;
        mServerHandler.sendMessage (msg);
    }

    public void setHandler(Handler handler) {
        mServerHandler = handler;
    }
}
