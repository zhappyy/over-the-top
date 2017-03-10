package com.xiaomi.miui.myapplication.sound;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.xiaomi.miui.myapplication.Global;
import com.xiaomi.miui.myapplication.R;

import com.xiaomi.miui.myapplication.SocketTcpClient;
import com.xiaomi.miui.myapplication.sound.utils.*;
import com.xiaomi.miui.paronamo.SensorInfo;

import java.io.File;

public class SoundActivity extends AppCompatActivity {

    float volume = 10000;
    private SoundDiscView soundDiscView;
    private MyMediaRecorder mRecorder;
    private static final int msgWhat = 0x1001;
    private static final int refreshTime = 100;
    private SocketTcpClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound);
        mRecorder = new MyMediaRecorder();
        initSocketClient();
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (this.hasMessages(msgWhat)) {
                return;
            }
            volume = mRecorder.getMaxAmplitude();  //获取声压值

            if(volume > 0 && volume < 1000000) {
                float value = 20 * (float)(Math.log10(volume));
                World.setDbCount(value);  //将声压值转为分贝值
                soundDiscView.refresh();
                mClient.sendMsg(new SensorInfo((value), value, value));
            }
            handler.sendEmptyMessageDelayed(msgWhat, refreshTime);
        }
    };

    private void initSocketClient() {
        mClient = new SocketTcpClient();
        //服务端的IP地址和端口号
        mClient.clintValue (this, Global.SERVER_IP, 6666);
        //开启客户端接收消息线程
        mClient.openClientThread();
    }

    private void startListenAudio() {
        handler.sendEmptyMessageDelayed(msgWhat, refreshTime);
    }

    /**
     * 开始记录
     * @param fFile
     */
    public void startRecord(File fFile){
        try{
            mRecorder.setMyRecAudioFile(fFile);
            if (mRecorder.startRecorder()) {
                startListenAudio();
            }else{
                Toast.makeText(this, "启动录音失败", Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e){
            Toast.makeText(this, "录音机已被占用或录音权限被禁止", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        soundDiscView = (SoundDiscView) findViewById(R.id.soundDiscView);
        File file = FileUtil.createFile("temp.amr");
        if (file != null) {
            Log.v("file", "file =" + file.getAbsolutePath());
            startRecord(file);
        } else {
            Toast.makeText(getApplicationContext(), "创建文件失败", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 停止记录
     */
    @Override
    protected void onPause() {
        super.onPause();
        mRecorder.delete(); //停止记录并删除录音文件
        handler.removeMessages(msgWhat);
    }

    @Override
    protected void onDestroy() {
        handler.removeMessages(msgWhat);
        mRecorder.delete();
        mClient.sendMsg(new SensorInfo(0, 0, 0));
        super.onDestroy();

    }
}
