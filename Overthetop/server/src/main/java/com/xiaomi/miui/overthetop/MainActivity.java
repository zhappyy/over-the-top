package com.xiaomi.miui.overthetop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;

import com.xiaomi.miui.paronamo.SensorInfo;

/**
 * Created by mi on 17-3-10.
 */
public class MainActivity extends Activity {

    private Button shakeBtn;
    private Button paronamoBtn;
    private Button soundBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        shakeBtn = (Button) findViewById(R.id.shake_btn);
        paronamoBtn = (Button) findViewById(R.id.paronamo_btn);
        soundBtn = (Button) findViewById(R.id.sound_btn);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initSocketServer();
    }

    private void initSocketServer() {
        ServerApplication.server.setHandler(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                SensorInfo sensorInfo = (SensorInfo) msg.obj;
                if (sensorInfo != null) {
                    float value = sensorInfo.getSensorX();
                    if (value == 1) {
                        shakeBtn.setSelected(true);
                    } else if (value == 2) {
                        paronamoBtn.setSelected(true);
                        Intent intent = new Intent(MainActivity.this, ServerActivity.class);
                        startActivity(intent);
                    } else if (value == 3) {
                        Log.e("Hunter", "jump to Broken");
                        soundBtn.setSelected(true);
                        Intent intent = new Intent(MainActivity.this, BrokenActivity.class);
                        startActivity(intent);
                    } else if (value == 4) {
                        finish();
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
