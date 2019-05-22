package com.example.substationtemperature.visualshape;

import com.example.substationtemperature.base.PropertyForDrawPicture;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

public class Breaker {
	private Paint paint=new Paint();
	private Canvas canvas;
	private PropertyForDrawPicture pfdp;
	public Breaker(){}
	public Breaker(Canvas canvas,PropertyForDrawPicture pfdp){
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
        if(pfdp.getIsError()){
        	paint.setColor(pfdp.getErrorColor());
        }else{
        	paint.setColor(pfdp.getStrokeColor());
        }
		paint.setStrokeWidth(pfdp.getStrokeThinkness());
		Matrix mt=new Matrix();
		if((pfdp.getMatrix()!=null)&&(!pfdp.getMatrix().equals(""))){
			mt=pfdp.getMatrix();
		}
		canvas.setMatrix(mt);
		int mx=pfdp.getStartPoint().x+(pfdp.getEndPoint().x-pfdp.getStartPoint().x)/2;
		int y1=pfdp.getStartPoint().y+(pfdp.getEndPoint().y-pfdp.getStartPoint().y)/6;
		int y2=pfdp.getEndPoint().y-(pfdp.getEndPoint().y-pfdp.getStartPoint().y)/6;
		canvas.drawLine(mx,pfdp.getStartPoint().y, mx, y1, paint);
		canvas.drawLine(mx, y2, mx, pfdp.getEndPoint().y, paint);
		paint.setStyle(Paint.Style.FILL);
		if(pfdp.getIsClosed()){
			paint.setColor(Color.RED);
		}else{
			paint.setColor(Color.GREEN);
		}
		Rect r=new Rect(pfdp.getStartPoint().x,y1,pfdp.getEndPoint().x,y2);
		canvas.drawRect(r, paint);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.WHITE);
		canvas.drawRect(r, paint);
	}
}
