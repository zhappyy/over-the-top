package com.xiaomi.miui.overthetop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.xiaomi.miui.paronamo.CommonGLSurfaceView;
import com.xiaomi.miui.paronamo.SensorInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * 搜索主机 —— 简单demo
 */
public class ServerActivity extends AppCompatActivity implements SensorEventListener {

    private static String mIp;
    private static String BROADCAST_IP = "224.0.1.255";
    private static final int BROADCAST_PORT = 8088;
    private InetAddress mInetAddress = null;
    private MulticastSocket mMulticastSocket = null;

    private SocketServer server;

    private float mSensorPrevidousY;
    private float mSensorPrevidousX;
    private float mPreviousY;
    private float mPreviousX;
    // 重力感应
    private SensorManager mSensorManager;
    private Sensor mMagneticSensor;
    private Sensor mAccelerometerSensor;
    private Sensor mGyroscopeSensor;
    // 将纳秒转化为秒
    private static final float NS2S = 1.0f / 1000000000.0f;
    private float mTimestamp;
    private float mAngle[] = new float[3];

    private LinearLayout mViewGroup;
    private CommonGLSurfaceView mCommonGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initView();
//        initSensor();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initSocketServer();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        server.disconnectSocket();
        server = null;
    }

    private void initSocketServer() {
        server = new SocketServer(6666);
        server.beginListen();
        server.setHandler(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                 SensorInfo sensorInfo = (SensorInfo) msg.obj;
                 Log.e("zy", "sensorInfo.getSensorX():" + sensorInfo.getSensorX());
                if (sensorInfo != null) {
                    mCommonGLSurfaceView.setXangle(sensorInfo.getSensorX());
                    mCommonGLSurfaceView.setYangle(sensorInfo.getSensorY());
                }
            }
        });
    }

    private void initView() {
        getOwnWifiIP();
        mCommonGLSurfaceView = new CommonGLSurfaceView(this,
                getBitmapFromCache(Environment.getExternalStorageDirectory().getAbsolutePath() + "/sources/p4.jpg")
                /*BitmapFactory.decodeResource(getResources(), R.drawable.earth)*/);
        Log.e("zy", "path:" + Environment.getExternalStorageDirectory().getAbsolutePath() +"*********");
        mCommonGLSurfaceView.setHorizontalSlide(true);
        mCommonGLSurfaceView.setVerticalSlide(true);

        mViewGroup = (LinearLayout) findViewById(R.id.viewgroup);
        mViewGroup.addView(mCommonGLSurfaceView);
    }

    private void initSensor() {

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // 磁偏计
        mMagneticSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        // 加速度计
        mAccelerometerSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // 陀螺仪
        mGyroscopeSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        // 注册陀螺仪传感器，并设定传感器向应用中输出的时间间隔类型是SensorManager.SENSOR_DELAY_GAME(20000微秒)
        // SensorManager.SENSOR_DELAY_FASTEST(0微秒)：最快。最低延迟，一般不是特别敏感的处理不推荐使用，该模式可能在成手机电力大量消耗，由于传递的为原始数据，诉法不处理好会影响游戏逻辑和UI的性能
        // SensorManager.SENSOR_DELAY_GAME(20000微秒)：游戏。游戏延迟，一般绝大多数的实时性较高的游戏都是用该级别
        // SensorManager.SENSOR_DELAY_NORMAL(200000微秒):普通。标准延时，对于一般的益智类或EASY级别的游戏可以使用，但过低的采样率可能对一些赛车类游戏有跳帧现象
        // SensorManager.SENSOR_DELAY_UI(60000微秒):用户界面。一般对于屏幕方向自动旋转使用，相对节省电能和逻辑处理，一般游戏开发中不使用
        mSensorManager.registerListener(this, mGyroscopeSensor,
                SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mMagneticSensor,
                SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mAccelerometerSensor,
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // x,y,z分别存储坐标轴x,y,z上的加速度
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            // 根据三个方向上的加速度值得到总的加速度值a
            float a = (float) Math.sqrt(x * x + y * y + z * z);

        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            // 三个坐标轴方向上的电磁强度，单位是微特拉斯(micro-Tesla)，用uT表示，也可以是高斯(Gauss),1Tesla=10000Gauss
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];;

        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            // 从 x、y、z 轴的正向位置观看处于原始方位的设备，如果设备逆时针旋转，将会收到正值；否则，为负值
            if (mTimestamp != 0) {
                // 得到两次检测到手机旋转的时间差（纳秒），并将其转化为秒
                final float dT = (event.timestamp - mTimestamp) * NS2S;
                // 将手机在各个轴上的旋转角度相加，即可得到当前位置相对于初始位置的旋转弧度
                mAngle[0] += event.values[0] * dT;
                mAngle[1] += event.values[1] * dT;
                mAngle[2] += event.values[2] * dT;

                // 将弧度转化为角度
                float anglex = (float) Math.toDegrees(mAngle[0]);
                float angley = (float) Math.toDegrees(mAngle[1]);
                float anglez = (float) Math.toDegrees(mAngle[2]);

                SensorInfo info = new SensorInfo(anglex,angley, anglez);
//                server.sendMessage(info);
                Message msg = new Message();
                msg.what = 101;
                msg.obj = info;
//                mHandler.sendMessage(msg);

            }
            // 将当前时间赋值给mTimestamp
            mTimestamp = event.timestamp;

        }else if(event.sensor.getType()==Sensor.TYPE_ORIENTATION){

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
/*        float y = event.getY();
        float x = event.getX();
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                mSensorManager.unregisterListener(this, mGyroscopeSensor);
                mSensorManager.unregisterListener(this, mMagneticSensor);
                mSensorManager.unregisterListener(this, mAccelerometerSensor);
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = y - mPreviousY;// 计算触控笔Y位移
                float dx = x - mPreviousX;// 计算触控笔X位移
                mCommonGLSurfaceView.addXangle(dy * 0.3f);
                mCommonGLSurfaceView.addYangle(dx * 0.3f);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mSensorManager.registerListener(this, mGyroscopeSensor,
                        SensorManager.SENSOR_DELAY_FASTEST);
                mSensorManager.registerListener(this, mMagneticSensor,
                        SensorManager.SENSOR_DELAY_FASTEST);
                mSensorManager.registerListener(this, mAccelerometerSensor,
                        SensorManager.SENSOR_DELAY_FASTEST);
                break;
        }
        mPreviousY = y;// 记录触控笔位置
        mPreviousX = x;// 记录触控笔位置*/
        return true;
    }

    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 101:
                    if (mCommonGLSurfaceView != null) {
                        SensorInfo info = (SensorInfo) msg.obj;
                        float y = info.getSensorY();
                        float x = info.getSensorX();
                        float dy = y - mSensorPrevidousY;// 计算触控笔Y位移
                        float dx = x - mSensorPrevidousX;// 计算触控笔X位移
                        mCommonGLSurfaceView.addYangle(dx * 1.0f);
                        mCommonGLSurfaceView.addXangle(dy  * 1.0f);
                        mSensorPrevidousY = y;// 记录触控笔位置
                        mSensorPrevidousX = x;// 记录触控笔位置
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private <V extends View> V $(int id) {
        return (V) findViewById(id);
    }

    public Bitmap getBitmapFromCache(String path) {

        Bitmap bitmap = null;

        FileInputStream fs = null;
        try {
            File file = new File(path);
            if(file.exists()) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                fs = new FileInputStream(file);
                bitmap = BitmapFactory.decodeFileDescriptor(fs.getFD(), null, options);
            }
        } catch (OutOfMemoryError e) {
            Log.e("zy", "OutOfMemoryError", e);
        } catch (FileNotFoundException e) {
            Log.e("zy", "FileNotFoundException", e);
        } catch (IOException e) {
            Log.e("zy", "IOException", e);
        } finally {

        }
        Log.e("zy", "bitmap :" + (bitmap == null));
        return bitmap;
    }

    /**
     * 获取本机在Wifi中的IP
     */
    private String getOwnWifiIP() {
        WifiManager wm = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        if (!wm.isWifiEnabled()) {
            return "";
        }

        // 需加权限：android.permission.ACCESS_WIFI_STATE
        WifiInfo wifiInfo = wm.getConnectionInfo();
        int ipInt = wifiInfo.getIpAddress();
        String ipAddr = int2Ip(ipInt);
        Log.i("zy", "@@@zjun: 本机IP=" + ipAddr);
        return int2Ip(ipInt);
    }

    /**
     * 把int表示的ip转换成字符串ip
     */
    private String int2Ip(int i) {
        return String.format("%d.%d.%d.%d", i & 0xFF, (i >> 8) & 0xFF, (i >> 16) & 0xFF, (i >> 24) & 0xFF);
    }
}
