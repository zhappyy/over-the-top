package com.xiaomi.miui.myapplication.shake;

import android.app.Activity;
import android.app.Service;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xiaomi.miui.myapplication.ClientApplication;
import com.xiaomi.miui.myapplication.Global;
import com.xiaomi.miui.myapplication.R;
import com.xiaomi.miui.myapplication.SocketTcpClient;
import com.xiaomi.miui.myapplication.sound.utils.World;
import com.xiaomi.miui.paronamo.SensorInfo;


public class ShakeActivity extends Activity implements SensorEventListener {
    private SensorManager sensorManager = null;
    private Sensor accelerateSensor;
    private Vibrator vibrator = null;
    private LinearLayout topLayout, bottomLayout;
    private ImageView topLineIv, bottomLineIv;
    private boolean isShake = false;
    private int msgWhat = 1004;
    private static int sensorCount = 0;

    private static boolean isFirstCoord = true;
    private float old_X = 0;
    private float old_Y = 0;
    private float old_Z = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);
        topLayout = (LinearLayout) findViewById(R.id.shake_top_layout);
        topLineIv = (ImageView) findViewById(R.id.shake_top_line);
        bottomLayout = (LinearLayout) findViewById(R.id.shake_bottom_layout);
        bottomLineIv = (ImageView) findViewById(R.id.shake_bottom_line);
        topLineIv.setVisibility(View.GONE);
        bottomLineIv.setVisibility(View.GONE);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this,
                accelerateSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mHandler.removeMessages(101);
        ClientApplication.mClient.sendMsg(new SensorInfo(0, 0, 0));
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == msgWhat) {
                ClientApplication.mClient.sendMsg((SensorInfo) msg.obj);
                return;
            }
        }
    };

    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        int sensorType = event.sensor.getType();
        // values[0]:X轴，values[1]：Y轴，values[2]：Z轴
        float[] values = event.values;

        Log.e("Hunter", "x = " + values[0] + " y = " + values[1] + " z = " + values[2]);
        if (isFirstCoord) {
            old_X = values[0];
            old_Y = values[1];
            old_Z = values[2];
            Log.e("Hunter", "firstCoord");
            isFirstCoord = false;
            return;
        } else {
            Log.e("Hunter", "non firstCoord");
            float delta_x = values[0] - old_X;
            float delta_y = values[1] - old_Y;
            float delta_z = values[2] - old_Z;
            float delta = delta_x * delta_x + delta_y * delta_y + delta_z * delta_z;
            Log.e("delta", "(" + delta_x + "," + delta_y + "," + delta_z + ")^2=" + delta);
            old_X = values[0];
            old_Y = values[1];
            old_Z = values[2];
            if (delta < 5) {
                return;
            }
            Log.e("delta pass", "(" + delta_x + "," + delta_y + "," + delta_z + ")^2=" + delta);
        }

        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            Log.e("Hunter TYPE", "x = " + values[0] + " y = " + values[1] + " z = " + values[2]);
            Log.e("zy", "sensorInfo:00000");
            sensorCount++;
            if (sensorCount > 5) {

                sensorCount = 0;
                SensorInfo info = new SensorInfo(values[1], values[0], values[2]);
                float value = info.getSensorX() + info.getSensorY() + info.getSensorZ();
                Log.e("zy", "sensorInfo:" + value);
                Message msg = new Message();
                msg.what = msgWhat;
                msg.obj = info;
                mHandler.sendMessage(msg);
            }

            if ((Math.abs(values[0]) > 17 || Math.abs(values[1]) > 17 || Math
                    .abs(values[2]) > 17) && !isShake) {
                isShake = true;
                new Thread() {
                    public void run() {
                        try {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    // 摇动手机后，再伴随震动提示~~
                                    vibrator.vibrate(300);
                                    topLineIv.setVisibility(View.VISIBLE);
                                    bottomLineIv.setVisibility(View.VISIBLE);
                                    startAnimation(false);
                                }
                            });
                            Thread.sleep(500);
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    // 摇动手机后，再伴随震动提示~~
                                    vibrator.vibrate(300);
                                }
                            });
                            Thread.sleep(500);
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    isShake = false;
                                    startAnimation(true);
                                }
                            });
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    ;
                }.start();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    private void startAnimation(boolean isBack) {
        int type = TranslateAnimation.RELATIVE_TO_SELF;
        float topFromYValue;
        float topToYValue;
        float bottomFromYValue;
        float bottomToYValue;
        if (isBack) {
            topFromYValue = -0.5f;
            topToYValue = 0;
            bottomFromYValue = 0.5f;
            bottomToYValue = 0;
        } else {
            topFromYValue = 0;
            topToYValue = -0.5f;
            bottomFromYValue = 0;
            bottomToYValue = 0.5f;
        }
        TranslateAnimation topAnimation = new TranslateAnimation(type, 0, type,
                0, type, topFromYValue, type, topToYValue);
        topAnimation.setDuration(200);
        topAnimation.setFillAfter(true);
        TranslateAnimation bottomAnimation = new TranslateAnimation(type, 0,
                type, 0, type, bottomFromYValue, type, bottomToYValue);
        bottomAnimation.setDuration(200);
        bottomAnimation.setFillAfter(true);
        if (isBack) {
            bottomAnimation
                    .setAnimationListener(new TranslateAnimation.AnimationListener() {

                        @Override
                        public void onAnimationStart(Animation animation) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            // TODO Auto-generated method stub
                            topLineIv.setVisibility(View.GONE);
                            bottomLineIv.setVisibility(View.GONE);
                        }
                    });
        }
        bottomLayout.startAnimation(bottomAnimation);
        topLayout.startAnimation(topAnimation);
    }

}
