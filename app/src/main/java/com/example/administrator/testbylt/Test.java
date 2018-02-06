package com.example.administrator.testbylt;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.media.FaceDetector;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Administrator on 2018-1-6.
 */

public class Test extends Activity {

    private boolean isPolling,isLastRequestEnded;
    TextView textView;

    int count;

    private static final int START_REQUEST_TASK_PROGRESS=1;
    protected Camera mCameraDevice = null;// 摄像头对象实例

    private long mScanBeginTime = 0,mSpecStopTime,mSpecCameraTime;   // 扫描开始时间
    private long mScanEndTime = 0;   // 扫描结束时间
    private long mSpecPreviewTime = 0;   // 扫描持续时间

    private int orientionOfCamera ;   //前置摄像头layout角度

    int numberOfFaceDetected;    //最终识别人脸数目


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        //做出改变

        final MyRatingBar ratingbar=findViewById(R.id.ratingbar);
        textView=findViewById(R.id.showRating);
        Button button=findViewById(R.id.getRating);
        ratingbar.setRating(0);
        ratingbar.setIsIndicator(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ratingbar.setRating(0);
                textView.setText(ratingbar.getRating()+"");
                isLastRequestEnded=!isLastRequestEnded;
            }
        });
        Button button_startcamre=findViewById(R.id.button_startcamre);
        button_startcamre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startFaceDetection();
//                getCameraInstance();
                startActivity(new Intent(Test.this,TakePhoteActivity.class));
            }
        });


        List<TaskItemBean> listOfOrder=new ArrayList<>();
        listOfOrder.add(new TaskItemBean("2616a571-fca1-4e47-ab93-39dc4f18a87b"));
        listOfOrder.add(new TaskItemBean("9457949b-63b7-4523-a12f-4c775411affa"));
        listOfOrder.add(new TaskItemBean("0cfdd56d-3fbf-4dbf-ae32-81c6d4282612"));
        listOfOrder.add(new TaskItemBean("d3021116-4684-4807-a034-aa8d5b956819"));
//        listOfOrder.add(new TaskItemBean("4c775411affa"));
//        listOfOrder.add(new TaskItemBean("39dc4f18a87b"));
        List<TaskItemBean> listOfSeverData=new ArrayList<>();
//        listOfSeverData.add(new TaskItemBean("-3"));
        listOfSeverData.add(new TaskItemBean("0cfdd56d-3fbf-4dbf-ae32-81c6d4282612"));
//        listOfSeverData.add(new TaskItemBean("4c775411affa"));
        listOfSeverData.add(new TaskItemBean("9457949b-63b7-4523-a12f-4c775411affa"));
        listOfSeverData.add(new TaskItemBean("2616a571-fca1-4e47-ab93-39dc4f18a87b"));
//        listOfSeverData.add(new TaskItemBean("-3"));
        listOfSeverData.add(new TaskItemBean("d3021116-4684-4807-a034-aa8d5b956819"));

        List<TaskItemBean> listOfResult=sort(listOfSeverData,listOfOrder);
        for(int i=0;i<listOfResult.size();i++){
            Log.d("test","数组listOfResult元素是"+ listOfResult.get(i).getSeverid());
        }
//        RatingBar ratingBar_evaluate_taskDetails_quantitativeSurvey=findViewById(R.id.ratingBar_evaluate_taskDetails_quantitativeSurvey);
//        ratingBar_evaluate_taskDetails_quantitativeSurvey.setIsIndicator(false);
    }

    public void startFaceDetection() {
        try {
            mCameraDevice = Camera.open(1);		//打开前置
            if (mCameraDevice != null)
                Log.i(TAG, "open cameradevice success! ");
        } catch (Exception e) {				//Exception代替很多具体的异常
            mCameraDevice = null;
            Log.w(TAG, "open cameraFail");
            CameraRelease();
//            mHandler.postDelayed(r,5000);	//如果摄像头被占用，人眼识别每5秒检测看有没有释放前置
            return;
        }

        Log.i(TAG, "startFaceDetection");
        Camera.Parameters parameters = mCameraDevice.getParameters();
        setCameraDisplayOrientation(1,mCameraDevice);	           //设置预览方向

        mCameraDevice.setPreviewCallback(new Camera.PreviewCallback(){
            public void onPreviewFrame(byte[] data, Camera camera){
                mScanEndTime = System.currentTimeMillis();   //记录摄像头返回数据的时间
                mSpecPreviewTime = mScanEndTime - mScanBeginTime;  //从onPreviewFrame获取摄像头数据的时间
                Log.i(TAG, "onPreviewFrame and mSpecPreviewTime = " + String.valueOf(mSpecPreviewTime));
                Camera.Size localSize = camera.getParameters().getPreviewSize();  //获得预览分辨率
                YuvImage localYuvImage = new YuvImage(data, 17, localSize.width, localSize.height, null);
                ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
                localYuvImage.compressToJpeg(new Rect(0, 0, localSize.width, localSize.height), 80, localByteArrayOutputStream);    //把摄像头回调数据转成YUV，再按图像尺寸压缩成JPEG，从输出流中转成数组
                byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
                CameraRelease();   //及早释放camera资源，避免影响camera设备的正常调用
                StoreByteImage(arrayOfByte);
            }
        });

        mCameraDevice.startPreview();         //该语句可放在回调后面，当执行到这里，调用前面的setPreviewCallback
        mScanBeginTime = System.currentTimeMillis();// 记录下系统开始扫描的时间
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

//        FaceDetectDeal(numberOfFaceDetected);
    }


    private List<TaskItemBean> sort(List<TaskItemBean> list,List<TaskItemBean> listOfSeverData){
        if(list!=null&&list.size()>0&&listOfSeverData!=null&&listOfSeverData.size()>0){
            List<TaskItemBean> listOfResult=new ArrayList<>();
            List<String> listOfOrder=new ArrayList<>();
            for(int i=0;i<listOfSeverData.size();i++){
                listOfOrder.add(listOfSeverData.get(i).getSeverid());
            }
            List<TaskItemBean> listOfIntersection=new ArrayList<>();
            List<TaskItemBean> listOfImport=new ArrayList<>();
            listOfImport.addAll(list);
            for(int i=0;i<listOfOrder.size();i++){//取交集可以优化
                for(int j=0;j<listOfImport.size();j++){
                    if(listOfImport.get(j)!=null&&listOfOrder.get(i)!=null&&listOfImport.get(j).getSeverid().equals(listOfOrder.get(i))){
                        listOfIntersection.add(listOfImport.get(j));
                    }
                }
            }
            //让交集之外的元素排在交集之后
            listOfResult.addAll(listOfIntersection);
            if(listOfImport.size()>listOfIntersection.size()){//如果传入的数组数大于交集的数量
                listOfImport.removeAll(listOfIntersection);
                listOfResult.addAll(listOfImport);
                for(int i=0;i<listOfResult.size();i++){
                    Log.d("test","输入数组大于交集数组，listOfResult元素是"+ listOfResult.get(i).getSeverid());
                }
            }
            else {//只可能等于
                Log.d("test","交集等于输入数组");
            }
            list.clear();
            list.addAll(listOfResult);
        }
        else {
            Log.d("test","数组为空或无数据");
        }
        return list;
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(newLoopThread, 3000);
        isPolling=false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPolling=false;
    }

    private void CameraRelease(){
        if(mCameraDevice!=null){
            mCameraDevice.setPreviewCallback(null);
            mCameraDevice.stopPreview();
            mCameraDevice.release();
            mCameraDevice = null;
        }
    }

    /**
     * 获取摄像头实例
     * @return
     */
    private Camera getCameraInstance() {
        Camera c = null;
        try {
            int cameraCount = 0;
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            cameraCount = Camera.getNumberOfCameras(); // get cameras number

            for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
                Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
                // 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    try {
                        c = Camera.open(camIdx);   //打开后置摄像头
                    } catch (RuntimeException e) {
                        Toast.makeText(this, "摄像头打开失败！"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            if (c == null) {
                c = Camera.open(0); // attempt to get a Camera instance
            }
        } catch (Exception e) {
            Toast.makeText(this, "摄像头打开失败！"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return c;
    }




    private Runnable newLoopThread = new Runnable() {
        @Override
        public void run() {

            Log.d("thread","线程在运行,count="+count);

            if (isPolling) {
                Log.d("thread","轮询中时线程在运行,count="+count);

                if(isNetworkConnected(getApplicationContext())){
                    Log.d("thread","当有网时线程在运行,count="+count);
                    if(isLastRequestEnded){
                        count++;
                        Message message = Message.obtain();
                        message.what = START_REQUEST_TASK_PROGRESS;
                        handler.sendMessage(message);
                    }
                }
                handler.postDelayed(newLoopThread, 2000);
            }
        }
    };



    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case START_REQUEST_TASK_PROGRESS:
                    Log.d("thread","开启调用接口,count="+count);
                    textView.setText(""+count);
                    break;
            }
        }
    };

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    private void updataAdditionProgress() {
//        if (id == null||userName==null) {
//            EventBus.getDefault().post(new EventThreadCommunication(EventThreadCommunication.GET_TASK_SCHEDULE, EventThreadCommunication.REQUEST_FAILURE, 0));
//            return;
//        }
//        FormEncodingBuilder builder = new FormEncodingBuilder();
//        builder.add("taskId", id);
//        builder.add("userName", userName);
//        Call call = OkHttpUtil.requestUrlForInfo(Constant.URL_TASK_ADDITIONAL_RECORDING_PROGRESS, builder);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Request request, IOException e) {
//                ToastUtil.showToastOnUiThread(TaskDetailsActivity_QuantitativeSurvey.this, "获取任务进度失败");
//                EventBus.getDefault().post(new EventThreadCommunication(EventThreadCommunication.GET_TASK_SCHEDULE, EventThreadCommunication.REQUEST_FAILURE, 0));
//            }
//
//            @Override
//            public void onResponse(Response response) throws IOException {
//                String result = response.body().string();
//                try {
//                    JSONObject jsonObject = new JSONObject(result);
//                    String progress = (String) jsonObject.opt("data");
//                    String[] str = progress.split(",");
//
//                    int second = Integer.parseInt(str[0]);
//                    int first = Integer.parseInt(str[1]);
//                    int max = Integer.parseInt(str[2]);
//                    Log.d(TAG,"进度是max="+max+",first"+first+",second"+second);
//                    done=second;
//                    failed=first-second;
//                    unfinished=max-first;
//                    EventBus.getDefault().post(new EventThreadCommunication(EventThreadCommunication.GET_TASK_SCHEDULE, EventThreadCommunication.REQUEST_SUCCESS, 0));
//
//                } catch (Exception e) {
//                    ToastUtil.showToastOnUiThread(TaskDetailsActivity_QuantitativeSurvey.this, "获取任务进度数据异常");
//                    CrashReport.postCatchedException(e);EventBus.getDefault().post(new EventThreadCommunication(EventThreadCommunication.GET_TASK_SCHEDULE, EventThreadCommunication.REQUEST_FAILURE, 0));
//                    EventBus.getDefault().post(new EventThreadCommunication(EventThreadCommunication.GET_TASK_SCHEDULE, EventThreadCommunication.REQUEST_FAILURE, 0));
//                }
//            }
//        });

    }

}
