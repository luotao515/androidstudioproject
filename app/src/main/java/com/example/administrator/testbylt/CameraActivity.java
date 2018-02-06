package com.example.administrator.testbylt;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.media.FaceDetector;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2018-1-15.
 */

public class CameraActivity extends Activity implements SurfaceHolder.Callback {

    private static final String TAG = "camera";
    private Camera mCamera;
    private SurfaceHolder mHolder;
    private SurfaceView mView;
    private boolean firstTime;

    @Override
    // 创建Activity时执行的动作
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mView = (SurfaceView) findViewById(R.id.surfaceView);
        mHolder = mView.getHolder();
        mHolder.addCallback(this);
        firstTime=true;
    }

    @Override
    // apk暂停时执行的动作：把相机关闭，避免占用导致其他应用无法使用相机
    protected void onPause() {
        super.onPause();
        if(mCamera!=null){
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    // 恢复apk时执行的动作
    protected void onResume() {
        super.onResume();
        if(mHolder==null){
            Log.d(TAG, "走到了onresume方法，mHolder为空");
        }


        if(mCamera!=null){
            Log.d(TAG, "走到了onresume方法，mCamera不为空");
            mCamera = getCameraInstance();
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
            } catch(IOException e) {
                Log.d(TAG, "Error setting camera preview: " + e.getMessage());
            }
        }
        else {
            Log.d(TAG, "走到了onresume方法，mCamera为空");
        }
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(50);
//                    firstTime=false;
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//
//            }
//        }).start();
    }

//    public void switchCamera(){
//        Log.i(TAG, "Switching Camera");
//        if (mCamera != null) {
//            mCamera.stopPreview();
//            mCamera.release();
//            //mCamera = null;
//        }
//
//        //swap the id of the camera to be used
//        if (camId == Camera.CameraInfo.CAMERA_FACING_BACK) {
//            camId = Camera.CameraInfo.CAMERA_FACING_FRONT;
//        }else {
//            camId = Camera.CameraInfo.CAMERA_FACING_BACK;
//        }
//        try {
//            mCamera = Camera.open(camId);
//            //mCamera.setDisplayOrientation(90);
//            //You must get the holder of SurfaceView!!!
//            mCamera.setPreviewDisplay(mView.getHolder());
//
//            mCamera.startPreview();
//        }
//        catch (Exception e) { e.printStackTrace(); }
//    }


//    /**
//     * 获取Camera实例
//     * @return
//     */
//    private Camera getCamera(int id){
//        Camera camera = null;
//        try{
//            camera = Camera.open(id);
//        }catch (Exception e){
//
//        }
//        return camera;
//    }
//
//    /**
//     * 释放相机资源
//     */
//    private void releaseCamera(){
//        if(mCamera != null){
//            mCamera.setPreviewCallback(null);
//            mCamera.stopPreview();
//            mCamera.release();
//            mCamera = null;
//        }
//    }
//
//    /**
//     * 预览相机
//     */
//    private void startPreview(Camera camera, SurfaceHolder holder){
//        try {
//            //这里要设置相机的一些参数，下面会详细说下
//            setupCamera(camera);
//            camera.setPreviewDisplay(holder);
//            //亲测的一个方法 基本覆盖所有手机 将预览矫正
//            CameraUtil.getInstance().setCameraDisplayOrientation(this, mCameraId, camera);
////            camera.setDisplayOrientation(90);
//            camera.startPreview();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 设置
//     */
//    private void setupCamera(Camera camera) {
//        Camera.Parameters parameters = camera.getParameters();
//
//        List< String > focusModes = parameters.getSupportedFocusModes();
//        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
//            // Autofocus mode is supported 自动对焦
//            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
//        }
//
//        //这里第三个参数为最小尺寸 getPropPreviewSize方法会对从最小尺寸开始升序排列 取出所有支持尺寸的最小尺寸
//        Camera.Size previewSize = CameraUtil.getInstance().getPropPreviewSize(parameters.getSupportedPreviewSizes(), 1000);
//        parameters.setPreviewSize(previewSize.width, previewSize.height);
//
//        Camera.Size pictrueSize = CameraUtil.getInstance().getPropPictureSize(parameters.getSupportedPictureSizes(), 1000);
//        parameters.setPictureSize(pictrueSize.width, pictrueSize.height);
//
//        camera.setParameters(parameters);
//
//        Log.d("previewSize.width===", previewSize.width + "");
//        Log.d("previewSize.height===", previewSize.height + "");
//
//        /**
//         * 设置surfaceView的尺寸 因为camera默认是横屏，所以取得支持尺寸也都是横屏的尺寸
//         * 我们在startPreview方法里面把它矫正了过来，但是这里我们设置设置surfaceView的尺寸的时候要注意 previewSize.height<previewSize.width
//         * previewSize.width才是surfaceView的高度
//         * 一般相机都是屏幕的宽度 这里设置为屏幕宽度 高度自适应 你也可以设置自己想要的大小
//         */
//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(screenWidth, screenWidth * previewSize.width / previewSize.height);
//        //这里当然可以设置拍照位置 比如居中 我这里就置顶了
//        //params.gravity = Gravity.CENTER;
//        surfaceView.setLayoutParams(params);
//    }



    private long mScanBeginTime = 0;   // 扫描开始时间
    private long mScanEndTime = 0;   // 扫描结束时间
    private long mSpecPreviewTime = 0,mSpecCameraTime,mSpecStopTime;   // 扫描持续时间
    int numberOfFaceDetected,orientionOfCamera;

    // SurfaceHolder.Callback必须实现的方法
    public void surfaceCreated(SurfaceHolder holder){
        if(holder==null){
            Log.d(TAG, "走到了surfaceCreated,holder为空 " );
        }
        if(mCamera==null){
            Log.d(TAG, "走到了surfaceCreated,mCamera为空 " );
        }
        mCamera = getCameraInstance();
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
            mCamera.setPreviewCallback(new Camera.PreviewCallback(){
                public void onPreviewFrame(byte[] data, Camera camera){
                    mScanEndTime = System.currentTimeMillis();   //记录摄像头返回数据的时间
                    mSpecPreviewTime = mScanEndTime - mScanBeginTime;  //从onPreviewFrame获取摄像头数据的时间
                    Log.i(TAG, "onPreviewFrame and mSpecPreviewTime = " + String.valueOf(mSpecPreviewTime));
                    Camera.Size localSize = camera.getParameters().getPreviewSize();  //获得预览分辨率
                    YuvImage localYuvImage = new YuvImage(data, 17, localSize.width, localSize.height, null);
                    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
                    localYuvImage.compressToJpeg(new Rect(0, 0, localSize.width, localSize.height), 80, localByteArrayOutputStream);    //把摄像头回调数据转成YUV，再按图像尺寸压缩成JPEG，从输出流中转成数组
                    byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
//           todo         CameraRelease();   //及早释放camera资源，避免影响camera设备的正常调用
                    StoreByteImage(arrayOfByte);
                }
            });

        } catch(IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void setCameraDisplayOrientation(int paramInt, Camera paramCamera){
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(paramInt, info);
        @SuppressLint("WrongConstant")
        int rotation = ((WindowManager)getSystemService("window")).getDefaultDisplay().getRotation();  //获得显示器件角度
        int degrees = 0;
        Log.i(TAG,"getRotation's rotation is " + String.valueOf(rotation));
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        orientionOfCamera = info.orientation;      //获得摄像头的安装旋转角度
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        paramCamera.setDisplayOrientation(result);  //注意前后置的处理，前置是映象画面，该段是SDK文档的标准DEMO
    }

    public void StoreByteImage(byte[] paramArrayOfByte){
        mSpecStopTime = System.currentTimeMillis();
        mSpecCameraTime = mSpecStopTime - mScanBeginTime;

        Log.i(TAG, "StoreByteImage and mSpecCameraTime is " + String.valueOf(mSpecCameraTime));

        BitmapFactory.Options localOptions = new BitmapFactory.Options();
        Bitmap localBitmap1 = BitmapFactory.decodeByteArray(paramArrayOfByte, 0, paramArrayOfByte.length, localOptions);
        int i = localBitmap1.getWidth();
        int j = localBitmap1.getHeight();   //从上步解出的JPEG数组中接出BMP，即RAW->JPEG->BMP
        Matrix localMatrix = new Matrix();
        //int k = cameraResOr;
        Bitmap localBitmap2 = null;
        FaceDetector localFaceDetector = null;

        switch(orientionOfCamera){   //根据前置安装旋转的角度来重新构造BMP
            case 0:
                localFaceDetector = new FaceDetector(i, j, 1);
                localMatrix.postRotate(0.0F, i / 2, j / 2);
                localBitmap2 = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);
                break;
            case 90:
                localFaceDetector = new FaceDetector(j, i, 1);   //长宽互换
                localMatrix.postRotate(-270.0F, j / 2, i / 2);  //正90度的话就反方向转270度，一样效果
                localBitmap2 = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);
                break;
            case 180:
                localFaceDetector = new FaceDetector(i, j, 1);
                localMatrix.postRotate(-180.0F, i / 2, j / 2);
                localBitmap2 = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);
                break;
            case 270:
                localFaceDetector = new FaceDetector(j, i, 1);
                localMatrix.postRotate(-90.0F, j / 2, i / 2);
                localBitmap2 = Bitmap.createBitmap(j, i, Bitmap.Config.RGB_565);  //localBitmap2应是没有数据的
                break;
        }

        FaceDetector.Face[] arrayOfFace = new FaceDetector.Face[1];
        Paint localPaint1 = new Paint();
        Paint localPaint2 = new Paint();
        localPaint1.setDither(true);
        localPaint2.setColor(-65536);
        localPaint2.setStyle(Paint.Style.STROKE);
        localPaint2.setStrokeWidth(2.0F);
        Canvas localCanvas = new Canvas();
        localCanvas.setBitmap(localBitmap2);
        localCanvas.setMatrix(localMatrix);
        localCanvas.drawBitmap(localBitmap1, 0.0F, 0.0F, localPaint1); //该处将localBitmap1和localBitmap2关联（可不要？）

        numberOfFaceDetected = localFaceDetector.findFaces(localBitmap2, arrayOfFace); //返回识脸的结果
        localBitmap2.recycle();
        localBitmap1.recycle();   //释放位图资源

//       todo FaceDetectDeal(numberOfFaceDetected);
    }

    // SurfaceHolder.Callback必须实现的方法
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){
        refreshCamera(); // 这一步是否多余？在以后复杂的使用场景下，此步骤是必须的。
        int rotation = getDisplayOrientation(); //获取当前窗口方向
        mCamera.setDisplayOrientation(rotation); //设定相机显示方向
        Log.d(TAG, "走到了surfaceChanged " );
    }

    // SurfaceHolder.Callback必须实现的方法
    public void surfaceDestroyed(SurfaceHolder holder){
        mHolder.removeCallback(this);
        if(mCamera!=null){
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        Log.d(TAG, "走到了surfaceDestroyed " );
    }

    // === 以下是各种辅助函数 ===

    // 获取camera实例
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        } catch(Exception e){
            Log.d("TAG", "camera is not available");
        }
        return c;
    }

    // 获取当前窗口管理器显示方向
    private int getDisplayOrientation(){
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int rotation = display.getRotation();
        int degrees = 0;
        switch (rotation){
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        android.hardware.Camera.CameraInfo camInfo =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, camInfo);

        // 这里其实还是不太懂：为什么要获取camInfo的方向呢？相当于相机标定？？
        int result = (camInfo.orientation - degrees + 360) % 360;

        return result;
    }

    // 刷新相机
    private void refreshCamera(){
        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch(Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {

        }
    }

}
