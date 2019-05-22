package com.example.substationtemperature.visualshape;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import com.example.substationtemperature.base.PropertyForDrawPicture;

public class Warning {
	private Paint paint=new Paint();
	private Canvas canvas=new Canvas();
	private PropertyForDrawPicture pfdp=new PropertyForDrawPicture();
	public Warning(){}
	public Warning(Canvas canvas,PropertyForDrawPicture pfdp){
		this.canvas=canvas;
		this.pfdp=pfdp;
		CalcVisual();
		
	}
	public void setCanvas(Canvas cvas){
		this.canvas=cvas;
	}
	public void setPropertyForDrawPictrue(PropertyForDrawPicture fdp){
		this.pfdp=fdp;
	}
	public void setProperty(Canvas cvas,PropertyForDrawPicture pfdp){
		this.canvas=cvas;this.pfdp=pfdp;
	}
	private void CalcVisual() {
		// TODO Auto-generated method stub
		paint.setAntiAlias(true);
		paint.setStrokeJoin(Paint.Join.ROUND);   
        paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(pfdp.getStrokeThinkness());
		Matrix mt=new Matrix();
		if((pfdp.getMatrix()!=null)&&(!pfdp.getMatrix().equals(""))){
			mt=pfdp.getMatrix();
		}
		canvas.setMatrix(mt);
		RectF rect=new RectF(pfdp.getStartPoint().x,pfdp.getStartPoint().y,pfdp.getEndPoint().x,pfdp.getEndPoint().y);
		float mx = rect.left + rect.width() / 2;
		float x1 = rect.left + rect.width() / 4;
		float x2 = rect.right - rect.width() / 4;
		float y1 = rect.top + rect.height() / 9 * 4;
		float y2 = rect.bottom - rect.height() / 9 * 4;
        Path path=new Path();
        path.moveTo(x2, rect.top);
        path.lineTo(rect.left, y2);
        path.lineTo(mx, y2);
        path.lineTo(x1, rect.bottom);
        path.lineTo(rect.right, y1);
        path.lineTo(mx, y1);
        path.close();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.YELLOW);
		canvas.drawPath(path, paint);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.RED);
		canvas.drawPath(path, paint);
	}
}
