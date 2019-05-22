package com.lx.checkameter.base;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
 
public class CircleImageView extends ImageView {
    private int mRadius;
    private Paint mPaint;
 
    public CircleImageView(Context context) {
        super(context);
        init();
    }
 
    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
 
    private void init() {
        mPaint = new Paint();
    }
 
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
 
        if (width > height) {
            mRadius = height / 2;
        } else {
            mRadius = width / 2;
        }
 
        setMeasuredDimension(mRadius * 2, 2 * mRadius);
    }
 
    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, mRadius*2, mRadius*2);
            drawable.draw(canvas);
            return bitmap;
        }
    }
 
    @Override
    protected void onDraw(Canvas canvas) {
        // 将Drawable转为Bitmap
        Bitmap bmp = drawableToBitmap(getDrawable());
        Path path = new Path(); 
        //按照逆时针方向添加一个圆
        path.addCircle(mRadius, mRadius, mRadius, Direction.CCW);
        //先将canvas保存
        canvas.save();
        //设置为在圆形区域内绘制
        canvas.clipPath(path);
        //绘制Bitmap
        canvas.drawBitmap(bmp, 0, 0, mPaint);
        //恢复Canvas
        canvas.restore();
    }
}
