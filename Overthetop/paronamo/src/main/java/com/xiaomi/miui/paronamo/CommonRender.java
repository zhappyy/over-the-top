package com.xiaomi.miui.paronamo;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.os.Build;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.frustumM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.scaleM;
import static android.opengl.Matrix.translateM;

@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class CommonRender implements Renderer {
	Context mContext;
	private int mProgram;
	private int mAPositionHandler;
	private int mUProjectMatrixHandler;
	private int mATextureCoordHandler;
	private final float[] mProjectMatrix = new float[16];
	private int mSize;
	private FloatBuffer mVertexBuff;

	private FloatBuffer mTextureBuff;
	private int mTextrueID;
	private Bitmap mBitmap;

	public CommonRender(Context context) {
		mContext = context;
		init();
	}

	public void init() {
		int perVertex = 36;

		double perRadius = 2 * Math.PI / (float) perVertex;
		double perW = 1 / (float) perVertex;
		double perH = 1 / (float) (perVertex);

		ArrayList<Float> vetexList = new ArrayList<Float>();
		ArrayList<Float> textureList = new ArrayList<Float>();
		for (int a = 0; a < perVertex; a++) {
			for (int b = 0; b < perVertex; b++) {
				float w1 = (float) (a * perH);
				float h1 = (float) (b * perW);

				float w2 = (float) ((a + 1) * perH);
				float h2 = (float) (b * perW);

				float w3 = (float) ((a + 1) * perH);
				float h3 = (float) ((b + 1) * perW);

				float w4 = (float) (a * perH);
				float h4 = (float) ((b + 1) * perW);

				textureList.add(h1);
				textureList.add(w1);

				textureList.add(h2);
				textureList.add(w2);

				textureList.add(h3);
				textureList.add(w3);

				textureList.add(h3);
				textureList.add(w3);

				textureList.add(h4);
				textureList.add(w4);

				textureList.add(h1);
				textureList.add(w1);

				float x1 = (float) (Math.sin(a * perRadius / 2) * Math.cos(b
						* perRadius));
				float z1 = (float) (Math.sin(a * perRadius / 2) * Math.sin(b
						* perRadius));
				float y1 = (float) Math.cos(a * perRadius / 2);

				float x2 = (float) (Math.sin((a + 1) * perRadius / 2) * Math
						.cos(b * perRadius));
				float z2 = (float) (Math.sin((a + 1) * perRadius / 2) * Math
						.sin(b * perRadius));
				float y2 = (float) Math.cos((a + 1) * perRadius / 2);

				float x3 = (float) (Math.sin((a + 1) * perRadius / 2) * Math
						.cos((b + 1) * perRadius));
				float z3 = (float) (Math.sin((a + 1) * perRadius / 2) * Math
						.sin((b + 1) * perRadius));
				float y3 = (float) Math.cos((a + 1) * perRadius / 2);

				float x4 = (float) (Math.sin(a * perRadius / 2) * Math
						.cos((b + 1) * perRadius));
				float z4 = (float) (Math.sin(a * perRadius / 2) * Math
						.sin((b + 1) * perRadius));
				float y4 = (float) Math.cos(a * perRadius / 2);

				vetexList.add(x1);
				vetexList.add(y1);
				vetexList.add(z1);

				vetexList.add(x2);
				vetexList.add(y2);
				vetexList.add(z2);

				vetexList.add(x3);
				vetexList.add(y3);
				vetexList.add(z3);

				vetexList.add(x3);
				vetexList.add(y3);
				vetexList.add(z3);

				vetexList.add(x4);
				vetexList.add(y4);
				vetexList.add(z4);

				vetexList.add(x1);
				vetexList.add(y1);
				vetexList.add(z1);
			}
		}
		mSize = vetexList.size() / 3;
		float texture[] = new float[mSize * 2];
		for (int i = 0; i < texture.length; i++) {
			texture[i] = textureList.get(i);
		}
		mTextureBuff = ByteBuffer.allocateDirect(texture.length * 4)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		mTextureBuff.put(texture);
		mTextureBuff.position(0);

		float vetex[] = new float[mSize * 3];
		for (int i = 0; i < vetex.length; i++) {
			vetex[i] = vetexList.get(i);
		}
		mVertexBuff = ByteBuffer.allocateDirect(vetex.length * 4)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		mVertexBuff.put(vetex);
		mVertexBuff.position(0);

	}

	@Override
	public void onDrawFrame(GL10 arg0) {
		rotateM(mCurrMatrix, 0, -xAngle, 1, 0, 0);
		rotateM(mCurrMatrix, 0, -yAngle, 0, 1, 0);
//		rotateM(mCurrMatrix, 0, -zAngle, 0, 0, 1);

		glClearColor(1, 1, 1, 1);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		glActiveTexture(GLES20.GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, mTextrueID);
		glUniformMatrix4fv(mUProjectMatrixHandler, 1, false,
				getfinalMVPMatrix(), 0);
		glDrawArrays(GL_TRIANGLES, 0, mSize);
	}

	public float xAngle;
	public float yAngle;
	public float zAngle;

	final float mCurrMatrix[] = new float[16];

	final float mMVPMatrix[] = new float[16];

	public float[] getfinalMVPMatrix() {
		Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mCurrMatrix, 0);
		Matrix.setIdentityM(mCurrMatrix, 0);
		return mMVPMatrix;
	}

	@TargetApi(Build.VERSION_CODES.FROYO)
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		glViewport(0, 0, width, height);
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		float ratio = width / (float) height;
		frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 1, 20);

		Matrix.setIdentityM(mCurrMatrix, 0);
		Matrix.setIdentityM(mMVPMatrix, 0);

		translateM(mProjectMatrix, 0, 0, 0, -2);
		// rotateM(mProjectMatrix, 0, -90, 1, 0, 0);
		scaleM(mProjectMatrix, 0, 4, 4, 4);

		mProgram = TimGL2Utils.getProgram(mContext);
		glUseProgram(mProgram);

		mAPositionHandler = glGetAttribLocation(mProgram, "aPosition");
		mUProjectMatrixHandler = glGetUniformLocation(mProgram,
				"uProjectMatrix");
		mATextureCoordHandler = glGetAttribLocation(mProgram, "aTextureCoord");

		mTextrueID = TimGL2Utils.initTexture(mContext, mBitmap);

		glVertexAttribPointer(mAPositionHandler, 3, GL_FLOAT, false, 0,
				mVertexBuff);
		glVertexAttribPointer(mATextureCoordHandler, 2, GL_FLOAT, false, 0,
				mTextureBuff);

		glEnableVertexAttribArray(mAPositionHandler);
		glEnableVertexAttribArray(mATextureCoordHandler);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {

	}

	public void setBitmap(Bitmap bitmap) {
		mBitmap = bitmap;
	}

}
