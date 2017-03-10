package com.xiaomi.miui.overthetop;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;

import com.xiaomi.miui.overthetop.brokenview.BrokenAnimator;
import com.xiaomi.miui.overthetop.brokenview.BrokenConfig;
import com.xiaomi.miui.overthetop.brokenview.BrokenView;
import com.xiaomi.miui.paronamo.SensorInfo;

/**
 * Created by mi on 17-3-9.
 */
public class BrokenActivity extends Activity{

    private BrokenView brokenView;
    private ImageView imageView;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            stop();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_broken);
        imageView = (ImageView) findViewById(R.id.broken_image);
        brokenView = BrokenView.add2Window(this);
        initSocketServer();
    }

    private void initSocketServer() {
        ServerApplication.server.setHandler(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                SensorInfo sensorInfo = (SensorInfo) msg.obj;
                if (sensorInfo != null) {
                    float value = sensorInfo.getSensorX();
                    if (value > 85) {
                        BrokenAnimator animator = brokenView.getAnimator(imageView);
                        if (animator == null) {
                            Point point = new Point(1000, 500);
                            animator = brokenView.createAnimator(imageView, point, new BrokenConfig());
                        }
                        start(animator);
                        handler.sendEmptyMessageDelayed(5, 8000);
                    } else if (value == 0) {
                        finish();
                    }
                }
            }
        });
    }

    private void start(BrokenAnimator animator) {
        if (!animator.isStarted()) {
            animator.start();
        }
    }

    private void stop() {
        BrokenAnimator animator = brokenView.getAnimator(imageView);
        if (animator != null && animator.isStarted()) {
            animator.doReverse();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

    }
}
