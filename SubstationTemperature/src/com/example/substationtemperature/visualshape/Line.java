package com.example.substationtemperature.visualshape;

import com.example.substationtemperature.base.PropertyForDrawPicture;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

public class Line {
	private Paint paint=new Paint();
	private Canvas canvas;
	private PropertyForDrawPicture pfdp;
	public Line(){}
	public Line(Canvas canvas,PropertyForDrawPicture pfdp){
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
		canvas.drawLine(pfdp.getStartPoint().x, pfdp.getStartPoint().y, pfdp.getEndPoint().x,pfdp.getEndPoint().y, paint);
	}

}
