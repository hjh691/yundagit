package com.example.substationtemperature.view;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class AutoScroll extends TextView {
  private float textLength = 0f;//文本长度
  private float viewWidth = 0f;
  private float step = 0f;//文字的横坐标
  private float y = 0f;//文字的纵坐标
  private float temp_view_plus_text_length = 0.0f;//用于计算的临时变量
  private float temp_view_plus_two_text_length = 0.0f;//用于计算的临时变量
  public boolean isStarting = false;//是否开始滚动
  private Paint paint = null;//绘图样式
  private String text = "";//文本内容
  public static boolean isend=false;
  Canvas acanvas;
 
 
  private Handler handler = new Handler()
     {
      @SuppressLint("WrongCall") @Override
   public void handleMessage(Message msg)
      {
       onDraw(acanvas);
   }
      
     };
 public AutoScroll(Context context)
 {
  super(context);
//  initView();
 }
 public AutoScroll(Context context, AttributeSet attrs)
 {
  super(context, attrs);
//  initView();
 }
 public AutoScroll(Context context, AttributeSet attrs, int defStyle)
 {
  super(context, attrs, defStyle);
//  initView();
 }
 
// public void onClick(View v) {
//   if(isStarting)
//             stopScroll();
//         else
//             startScroll();
// }
//  private void initView()
//     {
//         setOnClickListener(this);
//     }
  public void init(WindowManager windowManager)
     {
         paint = getPaint();
         text = getText().toString();
         textLength = paint.measureText(text);//textview中字数的长度
         viewWidth = getWidth();
         if(viewWidth == 0)
         {
             if(windowManager != null)
             {
                 Display display = windowManager.getDefaultDisplay();
                 viewWidth = display.getWidth();
             }
         }
         step = textLength;
         temp_view_plus_text_length = viewWidth + textLength;
         temp_view_plus_two_text_length = viewWidth + textLength * 2;
         y = getTextSize() + getPaddingTop();
         setLayerType(View.LAYER_TYPE_HARDWARE, paint);
     }
     public void startScroll()
     {
         isStarting = true;
         invalidate();
     }
   
   
     public void stopScroll()
     {
         isStarting = false;
         invalidate();
     }
     public void onDraw(Canvas canvas) {
      acanvas = canvas;
         canvas.drawText(text, (temp_view_plus_text_length-step), y, paint);
    	 //canvas.drawText(text, 100, 100, paint);
         if(!isStarting)
         {
             return;
         }
         step +=8;//0.5为文字滚动速度。
         if(step > temp_view_plus_two_text_length)
         {    
        	 step = textLength;
        	 isend=true;
         }
         invalidate();
     /**/
     }

}
