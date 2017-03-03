package com.xiaomi.miui.paronamo;

import java.io.Serializable;

public class SensorInfo implements Serializable{
	float mSensorX;
	float mSensorY;
	float mSensorZ;

    public SensorInfo(float x, float y, float z) {
		mSensorX = x;
		mSensorY = y;
		mSensorZ = z;
	}

	public float getSensorX() {
		return mSensorX;
	}

	public void setSensorX(float mSensorX) {
		this.mSensorX = mSensorX;
	}

	public float getSensorY() {
		return mSensorY;
	}

	public void setSensorY(float mSensorY) {
		this.mSensorY = mSensorY;
	}

	public float getSensorZ() {
		return mSensorZ;
	}

	public void setSensorZ(float mSensorZ) {
		this.mSensorZ = mSensorZ;
	}

}
