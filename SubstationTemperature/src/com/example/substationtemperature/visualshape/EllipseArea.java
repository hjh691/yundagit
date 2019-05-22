package com.example.substationtemperature.visualshape;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

import com.example.substationtemperature.base.PropertyForDrawPicture;

public class EllipseArea {
	private Paint paint=new Paint();
	private Canvas canvas=new Canvas();
	private PropertyForDrawPicture pfdp=new PropertyForDrawPicture();
	public EllipseArea(){}
	public EllipseArea(Canvas canvas,PropertyForDrawPicture pfdp){
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
		
		if(pfdp.getIsFill()){
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(pfdp.getFillColor());
			canvas.drawOval(rect, paint);
		}
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(pfdp.getStrokeColor());
		canvas.drawOval(rect, paint);
	}
}
