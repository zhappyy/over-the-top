package com.xiaomi.miui.overthetop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.xiaomi.miui.overthetop.Shake.ShakeActivity;

/**
 * Created by zhangyong on 17-3-10.
 */

public class LaunchActivity extends Activity {

    private Button mShakeButton;
    private Button mPanoramoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        init();
    }

    private void init() {
        mShakeButton = (Button) findViewById(R.id.shake);
        mPanoramoButton = (Button) findViewById(R.id.paronamo);

        mShakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LaunchActivity.this, ShakeActivity.class);
                startActivity(intent);
            }
        });

        mPanoramoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LaunchActivity.this, ServerActivity.class);
                startActivity(intent);
            }
        });
    }
}
