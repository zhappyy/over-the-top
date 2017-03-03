package com.xiaomi.miui.paronamo;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;

/**
 * Created by zhangyong on 17-2-20.
 */

public class CommonGLSurfaceView extends GLSurfaceView {

    private CommonRender mCommonRender;
    private boolean mHorizontalSlide;
    private boolean mVerticalSlide;

    public CommonGLSurfaceView(Context context, Bitmap bitmap) {
        super(context);
        mCommonRender = new CommonRender(context);
        mHorizontalSlide = true;
        mVerticalSlide = true;

        setEGLContextClientVersion(2);
        mCommonRender.setBitmap(bitmap);
        setRenderer(mCommonRender);
    }

    // 更改填充椭圆围绕X轴旋转角度
    public void addXangle(float angle) {
        if (mVerticalSlide)
            mCommonRender.xAngle += angle;
    }

    // 更改填充椭圆围绕Y轴旋转角度
    public void addYangle(float angle) {
        if (mHorizontalSlide)
            mCommonRender.yAngle += angle;
    }

    public float getXangle() {
        return mCommonRender.xAngle;
    }

    public float getYangle() {
        return mCommonRender.yAngle;
    }

    public float getZangle() {
        return mCommonRender.zAngle;
    }

    public void setXangle(float xAngle) {
        mCommonRender.xAngle = xAngle;
    }

    public void setYangle(float yAngle) {
        mCommonRender.yAngle = yAngle;
    }

    public void setZangle(float zAngle) {
        mCommonRender.zAngle = zAngle;
    }

    public void setHorizontalSlide(boolean canHorizontalSlide) {
        mHorizontalSlide = canHorizontalSlide;
    }

    public void setVerticalSlide(boolean canVerticalSlide) {
        mVerticalSlide = canVerticalSlide;
    }

    public boolean canHorizontalSlide() {
        return mHorizontalSlide;
    }

    public boolean canVerticalSlide() {
        return mVerticalSlide;
    }
}
