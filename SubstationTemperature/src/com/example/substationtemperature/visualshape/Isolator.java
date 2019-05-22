package com.example.substationtemperature.visualshape;

import com.example.substationtemperature.base.PropertyForDrawPicture;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

public class Isolator {
	private Paint paint=new Paint();
	private Canvas canvas;
	private PropertyForDrawPicture pfdp;
	public Isolator(){}
	public Isolator(Canvas canvas,PropertyForDrawPicture pfdp){
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
		paint.setStrokeJoin(Paint.Join.ROUND);   
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(pfdp.getStrokeColor());
		paint.setStrokeWidth(pfdp.getStrokeThinkness());
		Matrix mt=new Matrix();
		if((pfdp.getMatrix()!=null)&&(!pfdp.getMatrix().equals(""))){
			mt=pfdp.getMatrix();
		}
		canvas.setMatrix(mt);
		Rect rect=new Rect(pfdp.getStartPoint().x,pfdp.getStartPoint().y,pfdp.getEndPoint().x,pfdp.getEndPoint().y);
		int mx=rect.left;
		int y1=rect.top+rect.height()/6;
		int y3=rect.top+rect.height()/4;
		int y2=rect.bottom-rect.height()/6;
		int r=rect.width()*3/20;
		canvas.drawLine(mx, rect.top, mx, y1-r, paint);
		if(pfdp.getIsClosed()){
			canvas.drawLine(mx+r, y2, mx+r, y1, paint);
		}else{
			canvas.drawLine(mx+r, y2, rect.right, y3, paint);
		}
		canvas.drawLine(mx, y2+r, mx, rect.bottom, paint);
		canvas.drawCircle(mx, y1, r, paint);
		canvas.drawCircle(mx, y2, r, paint);
	}
}
