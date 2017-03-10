package com.xiaomi.miui.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.xiaomi.miui.myapplication.shake.ShakeActivity;
import com.xiaomi.miui.myapplication.sound.SoundActivity;
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
        shakeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClientApplication.mClient.sendMsg(new SensorInfo(1, 1, 1));
                Intent intent = new Intent(MainActivity.this, ShakeActivity.class);
                startActivity(intent);
            }
        });
        paronamoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClientApplication.mClient.sendMsg(new SensorInfo(2, 2, 2));
                Intent intent = new Intent(MainActivity.this, ClientActivity.class);
                startActivity(intent);
            }
        });
        soundBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClientApplication.mClient.sendMsg(new SensorInfo(3, 3, 3));
                Intent intent = new Intent(MainActivity.this, SoundActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        ClientApplication.mClient.sendMsg(new SensorInfo(4, 4, 4));
        super.onDestroy();
    }
}
