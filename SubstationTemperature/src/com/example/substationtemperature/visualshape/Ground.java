package com.example.substationtemperature.visualshape;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.substationtemperature.base.PropertyForDrawPicture;

public class Ground {
	private Paint paint=new Paint();
	private Canvas canvas=new Canvas();
	private PropertyForDrawPicture pfdp=new PropertyForDrawPicture();
	public Ground(){}
	public Ground(Canvas canvas,PropertyForDrawPicture pfdp){
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
		paint.setStyle(Paint.Style.STROKE);
		Matrix mt=new Matrix();
		if((pfdp.getMatrix()!=null)&&(!pfdp.getMatrix().equals(""))){
			mt=pfdp.getMatrix();
		}
		canvas.setMatrix(mt);
		Rect rect=new Rect(pfdp.getStartPoint().x,pfdp.getStartPoint().y,pfdp.getEndPoint().x,pfdp.getEndPoint().y);
		float mx=rect.left+rect.width()/2;
		float y1=rect.bottom-rect.height()/3;
		float y2=rect.bottom-rect.height()/6;
		canvas.drawLine(mx, rect.top, mx, y1, paint);
		canvas.drawLine(rect.left, y1, rect.right, y1, paint);
		canvas.drawLine(rect.left+rect.width()/4, y2, rect.right-rect.width()/4,y2, paint);
		canvas.drawLine(rect.left+rect.width()/8*3, rect.bottom, rect.right-rect.width()/8*3, rect.bottom, paint);
		
	}
}
