package com.xiaomi.miui.overthetop.Shake;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.xiaomi.miui.overthetop.R;
import com.xiaomi.miui.overthetop.ServerApplication;
import com.xiaomi.miui.overthetop.brokenview.BrokenAnimator;
import com.xiaomi.miui.overthetop.brokenview.BrokenConfig;
import com.xiaomi.miui.overthetop.brokenview.BrokenView;
import com.xiaomi.miui.paronamo.SensorInfo;

/**
 * Created by zhangyong on 17-3-10.
 */

public class ShakeActivity extends Activity {

    AdProcessView mAdView;
    WaveView mWaveView;
    ProgressBar mEnergyProgressBar;
    VerticalProgressBar mVerticalProgressBar;

    int mOldEnergy;
    float mCurrentEnergy;
    float mOldProcess;
    float mCurrentProcess;
    private float mSumValue;
    private BrokenView brokenView;
    BrokenAnimator animator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shake);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        init();
        initSocketServer();
    }

    private void init() {
        mOldEnergy = 0;
        mCurrentEnergy = 0.0f;
        mOldProcess = 0.0f;
        mCurrentProcess = 0.0f;

        mAdView = (AdProcessView) findViewById(R.id.adView);
//        mAdView.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.mipmap.xiaomi_note2,720, 480));
//        mAdView.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.mipmap.wanglaoji,1920, 1080));
        mAdView.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.mipmap.toyota,3840, 2160));
        brokenView = BrokenView.add2Window(this);
        animator = brokenView.getAnimator(mAdView);

        mWaveView = (WaveView) findViewById(R.id.waveView);
        mVerticalProgressBar = (VerticalProgressBar) findViewById(R.id.progressBar);

//        updateEnergyProgressBar();
    }

    private float mOldProgress = 0.0f;
    private void initSocketServer() {
        ServerApplication.server.setHandler(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                SensorInfo sensorInfo = (SensorInfo) msg.obj;
                Log.e("zy", "sensorInfo.getSensorX():" + sensorInfo.getSensorX() + "sensorInfo.getSensorY():" + sensorInfo.getSensorY() +
                        "sensorInfo.getSensorZ():" + sensorInfo.getSensorZ());
                if (sensorInfo != null ) {
//                    if (sensorInfo.getSensorX() == 0) {
//                        finish();
//                    } else {
                        float value = Math.abs(sensorInfo.getSensorX()) + Math.abs(sensorInfo.getSensorY()) + Math.abs(sensorInfo.getSensorZ());
                        float finalValue = Math.abs(value) ;
                        Log.e("zy", "sensorInfo:" + finalValue);
                    ValueAnimator valueAnimator = ValueAnimator.ofInt(0, (int) value * 2);
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            mVerticalProgressBar.setProgress((int)animation.getAnimatedValue());
                        }
                    });
                    valueAnimator.start();
                        if (finalValue > 10.0f) {
//                            mWaveView.setSpeed(finalValue);
                            /*mWaveView.setLevel(finalValue * 0.8f);
                            mWaveView.setSpeed(10.8f);*/
                        } else {
                            /*mWaveView.setSpeed(30.8f);
                            mWaveView.setLevel(0.04f);*/
                        }
                        mWaveView.minusToLevelLine(finalValue/1.0f);
                        if (mWaveView.getLevelLine() <= 0.0f) {
                            if (animator == null) {
                                Point point = new Point(1000, 500);
                                animator = brokenView.createAnimator(mAdView, point, new BrokenConfig());
                            }
                            mWaveView.setVisibility(View.GONE);
                            mVerticalProgressBar.setVisibility(View.GONE);
                            start(animator);
                            animator.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    finish();
                                }

                                @Override
                                public void onAnimationCancel(Animator animator) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animator) {

                                }
                            });

                        }
                    }
                }
//            }
        });
    }

    private void start(BrokenAnimator animator) {
        if (!animator.isStarted()) {
            animator.start();
        }
    }

    int mValue;
    int[] mValues = {10,50,0,90,20,70,50,60,100,40,0,60,40,20,80,100};
    private void updateEnergyProgressBar() {

        new Thread() {
            @Override
            public void run() {
                super.run();
                int i = 0;
                while(i++ < 100) {

                        try {
                            final int finalI = i;
                            sleep(2000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    mProcessProgressBar
                                    mAdView.setPer(finalI);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

        }.start();

    }

    public  Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeResource(res, resId, options);
    }
    public int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
}
