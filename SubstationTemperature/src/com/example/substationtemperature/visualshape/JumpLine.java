package com.example.substationtemperature.visualshape;

import com.example.substationtemperature.base.PropertyForDrawPicture;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;

public class JumpLine {
	private Paint paint=new Paint();
	private Canvas canvas;
	private PropertyForDrawPicture pfdp;
	public JumpLine(){}
	public JumpLine(Canvas canvas,PropertyForDrawPicture pfdp){
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
        paint.setStyle(Paint.Style.STROKE);//不设置此项，drawpath不显示。
        //if(pfdp.getIsError()){
        //	paint.setColor(pfdp.getErrorColor());
        //}else{
        	paint.setColor(pfdp.getStrokeColor());
        //}
		paint.setStrokeWidth(pfdp.getStrokeThinkness());
		Matrix mt=new Matrix();
		if((pfdp.getMatrix()!=null)&&(!pfdp.getMatrix().equals(""))){
			mt=pfdp.getMatrix();
		}
		canvas.setMatrix(mt);
		
		Path path = new Path();
		path.reset();
        path.moveTo(pfdp.getStartPoint().x, pfdp.getStartPoint().y);
        path.lineTo(pfdp.getEndPoint().x, pfdp.getEndPoint().y);     
        PathEffect effects = new DashPathEffect(new float[]{8,8},0);
        paint.setPathEffect(effects);
        paint.setAntiAlias(true);
        canvas.drawPath(path, paint);
        //canvas.drawLine(pfdp.getStartPoint().x, pfdp.getStartPoint().y, pfdp.getEndPoint().x,pfdp.getEndPoint().y, paint);
	}
	
}
