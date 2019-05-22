package com.lx.checkameterclient.fragment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.lx.checkameterclient.Declare;
import com.lx.checkameterclient.R;
import com.lx.checkameterclient.view.FileShow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CATakePhoneFragment extends Fragment {
	private ImageView mVideoStartBtn;
	private static SurfaceView mSurfaceview;
	private static MediaRecorder mMediaRecorder;
	private static SurfaceHolder mSurfaceHolder;
	private File mRecVedioPath;
	private File mRecAudioFile;
	private TextView timer;
	private int hour = 0;
	private int minute = 0;
	private int second = 0;
	private boolean bool;
	private int parentId;
	public static Camera camera;
	protected static boolean isPreview;
	private Drawable iconStart;
	private Drawable iconStop;
	private static ImageView autoImage;
	private Button btnVideoBrowse;
	private ImageView btnImgBrowse;
	private ImageView btnImgStart;
	private String videoPath;
	private String imgPath;
	private boolean isRecording = true; // true表示没有录像，点击开始；false表示正在录像，点击暂停
	private String dbid;
	private RelativeLayout video_layout;
	private DisplayMetrics dm;
	private static float size_value;
	static int opt_width=0;
	static int opt_height=0;
	static int mwidth=0;
	static int mheight=0;
	static int temp_mwidth=0;
	static int temp_mheight=0;
	//****图片输出尺寸****
	static int bt_width=0;
	static int bt_height=0;
	private static Activity activity;
	private View view;
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.activity=activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.ca_take_phone_fragment, null);
		WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		mwidth=display.getWidth();
		mheight=display.getHeight();
		dm = getResources().getDisplayMetrics();
        size_value = dm.scaledDensity;//取得屏幕密度
        System.err.println("屏幕密度："+mwidth+"高度"+mheight);
		if(mwidth==1024 && mheight==720){
			temp_mwidth=710;temp_mheight=1084;
		}
		else if(mwidth==1280 && mheight==800){
			temp_mwidth=710;temp_mheight=1084;
		}
		else{
			//temp_mwidth=810;temp_mheight=1384;
			temp_mwidth=810;temp_mheight=1384;
		}
		
		video_layout=(RelativeLayout) view.findViewById(R.id.video_layout);
		iconStart = getResources().getDrawable(
				R.drawable.capture_on);
		iconStop = getResources().getDrawable(R.drawable.capture_stop);
		autoImage = (ImageView)view.findViewById(R.id.autofoucs);
		
		parentId = activity.getIntent().getIntExtra("parentId", 0);
		timer = (TextView) view.findViewById(R.id.arc_hf_video_timer);
		mVideoStartBtn = (ImageView) view.findViewById(R.id.arc_hf_video_start);
		mSurfaceview = (SurfaceView) view.findViewById(R.id.arc_hf_video_view);
		
		btnImgBrowse = (ImageView) view.findViewById(R.id.arc_hf_img_btnGridShow);
		btnImgBrowse.setOnClickListener(new btnListener());
		//取得电表ID
		dbid=getdbid();		
		//showImgCount();
		//showVideoCount();

		// 设置计时器不可见
		timer.setVisibility(View.INVISIBLE);

		// 设置缓存路径
		mRecVedioPath = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/bdlx/.Gallery/"+dbid+"/temp/");
		if (!mRecVedioPath.exists()) {
			mRecVedioPath.mkdirs();
		}
        
		display();
				mVideoStartBtn.setOnClickListener(new Button.OnClickListener() {

					@Override
					public void onClick(View v) {
						if (isRecording) {
							/*
							 * 点击开始录像
							 */
							if (isPreview) {
								camera.stopPreview();
								camera.release();
								camera = null;
							}
							second = 0;
							minute = 0;
							hour = 0;
							bool = true;
							if (mMediaRecorder == null)
								mMediaRecorder = new MediaRecorder();
							else
								mMediaRecorder.reset();
							if(mwidth==1280 && mheight==740)//录像与存储反转180
							{
							 	camera = Camera.open();
							 	camera.setDisplayOrientation(180);
							 	camera.unlock();
					            mMediaRecorder.setCamera(camera);
					            //c.release();
					        
					        mMediaRecorder.setOrientationHint(180);
							}else{
								camera = Camera.open();
							 	camera.setDisplayOrientation(90);
							 	camera.unlock();
					            mMediaRecorder.setCamera(camera);
								mMediaRecorder.setOrientationHint(90);
							}
							mMediaRecorder.setPreviewDisplay(mSurfaceHolder
									.getSurface());
							mMediaRecorder
									.setVideoSource(MediaRecorder.VideoSource.CAMERA);
							mMediaRecorder
									.setAudioSource(MediaRecorder.AudioSource.MIC);
							mMediaRecorder
									.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
							mMediaRecorder
									.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
							mMediaRecorder
									.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
							if(mwidth==1024 && mheight==720 && size_value<1.1)
							{
								mMediaRecorder.setVideoSize(800,600);
							}
							else{
								mMediaRecorder.setVideoSize(640, 480);
							}
							mMediaRecorder.setVideoFrameRate(30);
							try {
								mRecAudioFile = File.createTempFile("Vedio", ".mp4",
										mRecVedioPath);
							} catch (IOException e) {
								e.printStackTrace();
							}
							mMediaRecorder.setOutputFile(mRecAudioFile
									.getAbsolutePath());
							
							try {
								mMediaRecorder.prepare();
								timer.setVisibility(View.VISIBLE);
								handler.postDelayed(task, 1000);
								mMediaRecorder.start();
							} catch (Exception e) {
								e.printStackTrace();
							}
							showMsg("开始录制");
							mVideoStartBtn.setImageDrawable(iconStart);
							isRecording = !isRecording;
						} else {
							/*
							 * 点击停止
							 */
							try {
								bool = false;
								mMediaRecorder.stop();
								timer.setText(format(hour) + ":" + format(minute) + ":"
										+ format(second));
								mMediaRecorder.release();
								mMediaRecorder = null;
								videoRename();
							} catch (Exception e) {
								e.printStackTrace();
							}
							isRecording = !isRecording;
							mVideoStartBtn.setImageDrawable(iconStop);
							showMsg("录制完成，已保存");
							//录像反转180中使用相机记得释放
							//if(mwidth==1280 && mheight==740){
								if (camera != null) {
									camera.release();
									camera = null; // 记得释放
								}
							//}

							try {
								camera = Camera.open();
								Camera.Parameters parameters = camera.getParameters();
								
								//******取得预览尺寸*********
								List<Size> sizes = parameters.getSupportedPreviewSizes();						
								Size optimalSize = getOptimalPreviewSize(sizes,temp_mwidth,temp_mheight);
								int witdh_temp,height_temp;
								
								if(mwidth==1024 && mheight==720 && size_value<1.1)
								{
									witdh_temp=800;height_temp=600;
								}
								else if(mwidth==1280 && mheight==736 && size_value<1.4)
								{
									witdh_temp=1024;height_temp=768;
								}
								else{
									witdh_temp=optimalSize.width;height_temp=optimalSize.height;
								}
								mSurfaceview.setLayoutParams(new RelativeLayout.LayoutParams(witdh_temp,height_temp));
								//mSurfaceview.setLayoutParams(new RelativeLayout.LayoutParams(optimalSize.width,optimalSize.height));
								//System.out.println(optimalSize.width);
								//System.out.println(optimalSize.height);
								//******取得照片尺寸*********
								List<Size> sizes1 = parameters.getSupportedPictureSizes();
								
								Size optimalSize1 = getOptimalPreviewSize(sizes1,opt_width,opt_height);
								
								if(mwidth==1024 && mheight==720){
									opt_width=optimalSize.width;opt_height=optimalSize.height-100;
								}
								else if(mwidth==1280 && mheight==800){
									opt_width=optimalSize.width;opt_height=optimalSize.height-300;
									//opt_width=1280;opt_height=960;
								}
								else{
									opt_width=optimalSize.width;opt_height=optimalSize.height-100;
								}
								bt_width=optimalSize1.width;
								bt_height=optimalSize1.height;
								System.out.println(bt_width);
								System.out.println(bt_height);
								parameters.setPreviewSize(bt_width, bt_height);	//照片预览尺寸				
//								parameters.setPreviewSize(1280, 960);
//								parameters.setPreviewFrameRate(5); // 每秒5帧
								parameters.setPictureFormat(PixelFormat.JPEG);// 设置照片的输出格式
								parameters.setPictureSize(bt_width, bt_height);//照片输出尺寸
//								parameters.set("jpeg-quality", 85);// 照片质量
//								parameters.setPictureSize(optimalSize1.width, optimalSize1.width);
								if(mwidth==1280 && mheight==740){camera.setDisplayOrientation(180);} 
								else{camera.setDisplayOrientation(90);}
								camera.setParameters(parameters);
								camera.setPreviewDisplay(mSurfaceHolder);
								camera.startPreview();
								isPreview = true;
								showVideoCount();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				});
				btnImgStart = (ImageView)view.findViewById(R.id.arc_hf_img_start);
				btnImgStart.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						if (mMediaRecorder != null) {
							try {
								bool = false;
								mMediaRecorder.stop();
								timer.setText(format(hour) + ":" + format(minute) + ":"
										+ format(second));
								mMediaRecorder.release();
								mMediaRecorder = null;
								videoRename();
							} catch (Exception e) {
								e.printStackTrace();
							}
							isRecording = !isRecording;
							mVideoStartBtn.setImageDrawable(iconStop);
							showVideoCount();
							showMsg("录制完成，已保存");
							//录像反转180中使用相机记得释放
							if(mwidth==1280 && mheight==740){
								if (camera != null) {
									camera.release();
									camera = null; // 记得释放
								}
							}
						 

							try {
								camera = Camera.open();
								Camera.Parameters parameters = camera.getParameters();
								
								//******取得预览尺寸*********
								List<Size> sizes = parameters.getSupportedPreviewSizes();						
								Size optimalSize = getOptimalPreviewSize(sizes,temp_mwidth,temp_mheight);
								
								int witdh_temp,height_temp;
								
								if(mwidth==1024 && mheight==720 && size_value<1.1)
								{
									witdh_temp=800;height_temp=600;
								}
								else if(mwidth==1280 && mheight==736 && size_value<1.4)
								{
									witdh_temp=1024;height_temp=768;
								}
								else{
									witdh_temp=optimalSize.width;height_temp=optimalSize.height;
								}
								mSurfaceview.setLayoutParams(new RelativeLayout.LayoutParams(witdh_temp,height_temp));
								
								//mSurfaceview.setLayoutParams(new RelativeLayout.LayoutParams(optimalSize.width,optimalSize.height));
								//System.out.println(optimalSize.width);
								//System.out.println(optimalSize.height);
								//******取得照片尺寸*********
								List<Size> sizes1 = parameters.getSupportedPictureSizes();
								
								Size optimalSize1 = getOptimalPreviewSize(sizes1,opt_width,opt_height);
								
								if(mwidth==1024 && mheight==720){
									opt_width=optimalSize.width;opt_height=optimalSize.height-100;
								}
								else if(mwidth==1280 && mheight==800){
									opt_width=optimalSize.width;opt_height=optimalSize.height-300;
									//opt_width=1280;opt_height=960;
								}
								else{
									opt_width=optimalSize.width;opt_height=optimalSize.height;//-100
								}
								bt_width=optimalSize1.width;
								bt_height=optimalSize1.height;
								System.out.println(bt_width);
								System.out.println(bt_height);
								parameters.setPreviewSize(bt_width, bt_height);	//照片预览尺寸				
//								parameters.setPreviewSize(1280, 960);
//								parameters.setPreviewFrameRate(5); // 每秒5帧
								parameters.setPictureFormat(PixelFormat.JPEG);// 设置照片的输出格式
								parameters.setPictureSize(bt_width, bt_height);//照片输出尺寸
//								parameters.set("jpeg-quality", 100);// 照片质量
//								parameters.setPictureSize(optimalSize1.width, optimalSize1.width);
								if(mwidth==1280 && mheight==740){camera.setDisplayOrientation(180);} 
								else{camera.setDisplayOrientation(90);}
								camera.setParameters(parameters);
								camera.setPreviewDisplay(mSurfaceHolder);
								camera.startPreview();
								isPreview = true;
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						if (camera != null) {
							timer.setVisibility(View.INVISIBLE);
							camera.autoFocus(autoFocusCallback);
							
							try {

							camera.takePicture(shutterCallback, null, new PictureCallback() {
								@Override
								public void onPictureTaken(byte[] data, Camera camera) {
									
									Bitmap bitmap = BitmapFactory.decodeByteArray(data,
											0, data.length);
									
									Matrix matrix = new Matrix();
									 //设置缩放
									if(mwidth==1280 && mheight==740){matrix.setRotate(180);;} 
									else{matrix.setRotate(90);}////
									
									matrix.postScale(1f,1f);
									bitmap = Bitmap.createBitmap(bitmap, 0, 0,
											bt_width,bt_height,
											matrix, true);
//									Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(data, 0, data.length),
//											 1280, 800, false);
									String path = Environment
											.getExternalStorageDirectory()
											.getAbsolutePath()
											+ "/bdlx/.Gallery/"+dbid+"/img/";
//											+ String.valueOf(parentId) + "/";
									String fileName = new SimpleDateFormat(
											"yyyyMMddHHmmss").format(new Date())
											+ ".jpg";
									File out=null;
									out = new File(path);
									if (!out.exists()) {
										out.mkdirs();
									}
									out = new File(path, fileName);
									try {
										FileOutputStream outStream = new FileOutputStream(out);
										BufferedOutputStream bos = new BufferedOutputStream(outStream);
										bitmap.compress(CompressFormat.JPEG, 100,bos);
										bos.flush();
										bos.close();
										camera.startPreview();
									} catch (Exception e) {
										e.printStackTrace();
									}
									autoImage.setBackgroundResource(R.drawable.cam_focus_indicator_white_icn);
									
									showMsg("拍照成功");
									showImgCount();
								}
							}); // 拍照
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				});


		return view;
	}

	public static void display(){
	
	// 绑定预览视图
	SurfaceHolder holder = mSurfaceview.getHolder();
//	holder.setFormat(PixelFormat.TRANSLUCENT);
//	holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); 
//	holder.setFixedSize(176, 144); //设置分辨率

   
	holder.addCallback(new Callback() {

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			if (camera != null) {
				if (isPreview) {
					camera.stopPreview();
					isPreview = false;
				}
				camera.release();
				camera = null; // 记得释放
			}
			mSurfaceview = null;
			mSurfaceHolder = null;
			mMediaRecorder = null;
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			try {
				
				camera = Camera.open();
				Camera.Parameters parameters = camera.getParameters();
				camera.setDisplayOrientation(90);
				
				//******取得预览尺寸*********
				List<Size> sizes = parameters.getSupportedPreviewSizes();						
				Size optimalSize = getOptimalPreviewSize(sizes,temp_mwidth,temp_mheight);
				int witdh_temp,height_temp;
				
				if(mwidth==1024 && mheight==720 && size_value<1.1)
				{
					witdh_temp=800;height_temp=600;
				}
				else if(mwidth==1280 && mheight==736 && size_value<1.4)
				{
					witdh_temp=1024;height_temp=768;
				}
				else{
					witdh_temp=optimalSize.width;height_temp=optimalSize.height;
				}
				mSurfaceview.setLayoutParams(new RelativeLayout.LayoutParams(witdh_temp,height_temp));
				//mSurfaceview.setLayoutParams(new RelativeLayout.LayoutParams(optimalSize.width,optimalSize.height));
				//System.out.println(optimalSize.width);
				//System.out.println(optimalSize.height);
				if(mwidth==1024 && mheight==720){
					opt_width=optimalSize.width;opt_height=optimalSize.height-100;
				}
				else if(mwidth==1280 && mheight==800){
					opt_width=optimalSize.width;opt_height=optimalSize.height-300;
					//opt_width=1280;opt_height=960;
				}
				else{
					opt_width=optimalSize.width;opt_height=optimalSize.height-100;//
				}
				//******取得照片尺寸*********
				List<Size> sizes1 = parameters.getSupportedPictureSizes();
				
				Size optimalSize1 = getOptimalPreviewSize(sizes1,opt_width,opt_height);
				bt_width=optimalSize1.width;
				bt_height=optimalSize1.height;
				//System.out.println(bt_width);
				//System.out.println(bt_height);
				
				
				
				parameters.setPreviewSize(bt_width, bt_height);	//照片预览尺寸				
				//parameters.setPreviewSize(960, 1280);
//				parameters.setPreviewFrameRate(5); // 每秒5帧
				parameters.setPictureFormat(PixelFormat.JPEG);// 设置照片的输出格式
				parameters.setPictureSize(bt_width, bt_height);//照片输出尺寸
//				parameters.set("jpeg-quality", 85);// 照片质量
				if(mwidth==1280 && mheight==740){camera.setDisplayOrientation(180);} 
				else{camera.setDisplayOrientation(90);}
				camera.setParameters(parameters);
				camera.setPreviewDisplay(holder);
				camera.startPreview();
				isPreview = true;
				autoImage.setBackgroundResource(R.drawable.cam_focus_indicator_white_icn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			mSurfaceHolder = holder;
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {

			mSurfaceHolder = holder;
		}
	});
	 /*下面设置Surface不维护自己的缓冲区，而是等待屏幕的渲染引擎将内容推送到用户面前*/
	holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//	mSurfaceview.getHolder().setFixedSize(176, 144); //设置分辨率

}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onDestroyOptionsMenu() {
		// TODO Auto-generated method stub
		super.onDestroyOptionsMenu();
	}
	//======快门回调函数===========================
		 private ShutterCallback shutterCallback = new ShutterCallback(){
	         public void onShutter() {
	        	 ToneGenerator tone = null;
	                 if(tone == null)
	                         //发出提示用户的声音
	                         tone = new ToneGenerator(AudioManager.STREAM_DTMF,
	                                         ToneGenerator.MIN_VOLUME);
	                 tone.startTone(ToneGenerator.TONE_PROP_BEEP);
	         }
		 };
		 
		 private AutoFocusCallback autoFocusCallback = new AutoFocusCallback() {
	         
	         public void onAutoFocus(boolean success, Camera camera) {
	             // TODO Auto-generated method stub
	             if(success)//success表示对焦成功
	             {
	            	 autoImage.setBackgroundResource(R.drawable.cam_focus_indicator_green_icn); 
	                 
	             }
	             else
	             {
	                 //未对焦成功
	            	 autoImage.setBackgroundResource(R.drawable.cam_focus_indicator_white_icn); 

	             }
	                 
	             
	         }
	     };
	private static Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
		final double ASPECT_TOLERANCE = 0.05;
		double targetRatio = (double) w / h;
		if (sizes == null)
			return null;
		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;
		int targetHeight = h;
		
		// Try to find an size match aspect ratio and size
		for (Size size : sizes) {
		double ratio = (double) size.width / size.height;
		if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
			continue;
		if (Math.abs(size.height - targetHeight) < minDiff) {
			optimalSize = size;
			minDiff = Math.abs(size.height - targetHeight);
			}
		}

		// Cannot find the one match the aspect ratio, ignore the requirement
		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}
	
	// 按键监听
		class btnListener implements OnClickListener {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.arc_hf_img_btnGridShow:
					imgShow();

	 
					break;
				
				//case R.id.arc_hf_video_btnVideoBrowse:
				//	videoShow();
				//	break;
				default:
					break;
				}
			}

		}
		// 图片数量
		private void showImgCount() {
			imgPath = Environment.getExternalStorageDirectory().getAbsolutePath()
					+ "/bdlx/.Gallery/"+dbid+"/img/";
			File file = new File(imgPath);
			if (!file.exists()) {
				file.mkdirs();
			}
			File[] files = file.listFiles();
			int fileCount = files.length;
			if (fileCount == 0) {
				btnImgBrowse.setEnabled(false);
			} else {
				btnImgBrowse.setEnabled(true);
			}
			//btnImgBrowse.setText("浏览图片(" + fileCount + ")");
		}
		/**
		 * 录像数量
		 */
		public void showVideoCount() {
			videoPath = Environment.getExternalStorageDirectory().getAbsolutePath()
					+ "/bdlx/.Gallery/"+dbid+"/video/";
			File file = new File(videoPath);
			if (!file.exists()) {
				file.mkdirs();
			}
			File[] files = file.listFiles();
			int fileCount = files.length;
			/*if (fileCount == 0) {
				btnVideoBrowse.setEnabled(false);
			} else {
				btnVideoBrowse.setEnabled(true);
			}
			btnVideoBrowse.setText("浏览录像(" + fileCount + ")");*/
		}
		// 浏览图片
		public void imgShow() {
			imgPath = Environment.getExternalStorageDirectory().getAbsolutePath()
					+ "/bdlx/.Gallery/"+dbid+"/img/";
			Declare.file_path=imgPath;
			
			Intent intent = new Intent();
//			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("path", imgPath);
			intent.setClass(getActivity(), FileShow.class);
			startActivityForResult(intent, 2);
			//startActivity(intent);
		}
		//返回设置的电表ID号
	 	private String getdbid() {	
	 		String str;	
	 		SharedPreferences settings = activity.getSharedPreferences("config",  0); 
	 		str=settings.getString("dbid", "000000000001");
	 		return str;			        		 		             
	 	}
		/*
		 * 消息提示
		 */
		private Toast toast;

		public void showMsg(String arg) {
			if (toast == null) {
				toast = Toast.makeText(activity, arg, Toast.LENGTH_SHORT);
			} else {
				toast.cancel();
				toast.setText(arg);
			}
			toast.show();
		}

		/*
		 * 生成video文件名字
		 */
		protected void videoRename() {
			String path = Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ "/bdlx/.Gallery/"+dbid+"/img/";
//					+ String.valueOf(parentId) + "/";
			String fileName = new SimpleDateFormat("yyyyMMddHHmmss")
					.format(new Date()) + ".mp4";
			File out = new File(path);
			if (!out.exists()) {
				out.mkdirs();
			}
			out = new File(path, fileName);
			if (mRecAudioFile.exists())
				mRecAudioFile.renameTo(out);
		}

		/*
		 * 定时器设置，实现计时
		 */
		private Handler handler = new Handler();
		private Runnable task = new Runnable() {
			public void run() {
				if (bool) {
					handler.postDelayed(this, 1000);
					second++;
					if (second >= 60) {
						minute++;
						second = second % 60;
					}
					if (minute >= 60) {
						hour++;
						minute = minute % 60;
					}
					timer.setText(format(hour) + ":" + format(minute) + ":"
							+ format(second));
				}
			}
		};

		/*
		 * 格式化时间
		 */
		public String format(int i) {
			String s = i + "";
			if (s.length() == 1) {
				s = "0" + s;
			}
			return s;
		}
	public void onStart(){
		super.onStart();
		
		mSurfaceview = (SurfaceView) view.findViewById(R.id.arc_hf_video_view);
		display();
	}

}
