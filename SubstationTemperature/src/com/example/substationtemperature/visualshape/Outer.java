package com.example.substationtemperature.visualshape;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

import com.example.substationtemperature.base.PropertyForDrawPicture;

public class Outer {
	private Paint paint=new Paint();
	private Canvas canvas=new Canvas();
	private PropertyForDrawPicture pfdp=new PropertyForDrawPicture();
	public Outer(){}
	public Outer(Canvas canvas,PropertyForDrawPicture pfdp){
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
		paint.setColor(pfdp.getStrokeColor());
		paint.setStyle(Paint.Style.FILL);
		Matrix mt=new Matrix();
		if((pfdp.getMatrix()!=null)&&(!pfdp.getMatrix().equals(""))){
			mt=pfdp.getMatrix();
		}
		canvas.setMatrix(mt);
		Rect rect=new Rect(pfdp.getStartPoint().x,pfdp.getStartPoint().y,pfdp.getEndPoint().x,pfdp.getEndPoint().y);
		float head=rect.height()/10.0f;
		Path path=new Path();
		path.moveTo(rect.left, rect.bottom);
		path.lineTo(rect.left-head/2, rect.bottom-head);
		path.lineTo(rect.left+head/2, rect.bottom-head);
		path.close();
		canvas.drawPath(path, paint);
		canvas.drawLine(rect.left, rect.top, rect.left , rect.bottom-head, paint);
	}
}
