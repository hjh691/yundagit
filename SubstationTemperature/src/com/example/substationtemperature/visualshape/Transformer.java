package com.example.substationtemperature.visualshape;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

import com.example.substationtemperature.base.PropertyForDrawPicture;

public class Transformer {
	private Paint paint=new Paint();
	private Canvas canvas;
	private PropertyForDrawPicture pfdp;
	public void setCanvas(Canvas cvas){
		this.canvas=cvas;
	}
	public void setPropertyForDrawPictrue(PropertyForDrawPicture fdp){
		this.pfdp=fdp;
	}
	public void setProperty(Canvas cvas,PropertyForDrawPicture pfdp){
		this.canvas=cvas;this.pfdp=pfdp;
	}
	public Transformer(){}
	public Transformer(Canvas canvas, PropertyForDrawPicture pfdp) {
		// TODO Auto-generated constructor stub
		this.canvas=canvas;
		this.pfdp=pfdp;
		CalcVisual();
	}
	private void CalcVisual() {
		// TODO Auto-generated method stub
		paint.setStrokeJoin(Paint.Join.ROUND);   
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        if(pfdp.getIsError()){
        	paint.setColor(pfdp.getErrorColor());
        }else{
        	paint.setColor(pfdp.getStrokeColor());
        }
		paint.setStrokeWidth(pfdp.getStrokeThinkness());
		paint.setTextSize(pfdp.getSize()+4);
		Matrix mt=new Matrix();
		if((pfdp.getMatrix()!=null)&&(!pfdp.getMatrix().equals(""))){
			mt=pfdp.getMatrix();
		}
		canvas.setMatrix(mt);
		Rect rect=new Rect(pfdp.getStartPoint().x,pfdp.getStartPoint().y,pfdp.getEndPoint().x,pfdp.getEndPoint().y);
		float r=rect.width()/2.0f;
		float mx = rect.left + r;
		float r1 = rect.top + r;
		float r2 = rect.bottom - r;
		float x1 = rect.left + rect.width() / 3;
		float x2 = rect.right - rect.width() / 3;
		float y11 = rect.top + r / 2;
		float y12 = rect.top + r;
		float y21 = rect.bottom - r - r / 6;
		float y23 = rect.bottom - r / 2;
		float y22 = (y21 + y23) / 2 - r / 12;
		
		Path path=new Path();
		path.moveTo(mx, y11);
		path.lineTo(x1, y12);
		path.lineTo(x2, y12);
		path.lineTo(mx, y11);
		canvas.drawPath(path, paint);
		canvas.drawCircle(mx, r1, r, paint);
		
		canvas.drawLine(x1, y21, mx, y22, paint);
		canvas.drawLine(x2, y21, mx, y22, paint);
		canvas.drawLine(mx, y23, mx, y22, paint);
		canvas.drawCircle(mx, r2, r, paint);
	}

}
