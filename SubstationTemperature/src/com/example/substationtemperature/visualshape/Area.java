package com.example.substationtemperature.visualshape;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.example.substationtemperature.base.PropertyForDrawPicture;

public class Area {
	private Paint paint=new Paint();
	private Canvas canvas=new Canvas();
	private PropertyForDrawPicture pfdp=new PropertyForDrawPicture();
	public Area(){}
	public Area(Canvas canvas,PropertyForDrawPicture pfdp){
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
		//Point[] points=new Point[pfdp.getPoints().length];
		Path path=new Path();
		path.moveTo(pfdp.getPoints()[0].x,pfdp.getPoints()[0].y);
		for(int i=1;i<pfdp.getPoints().length;i++){
			path.lineTo(pfdp.getPoints()[i].x,pfdp.getPoints()[i].y);
		}
		if(pfdp.getIsClosed())
			path.close();
			//path.lineTo(pfdp.getPoints()[0].x,pfdp.getPoints()[0].y);
		if(pfdp.getIsFill()){
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(pfdp.getFillColor());
			canvas.drawPath(path, paint);
		}
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(pfdp.getStrokeColor());
		canvas.drawPath(path,paint);
	}
}
