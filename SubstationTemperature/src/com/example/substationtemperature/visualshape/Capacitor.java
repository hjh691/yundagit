package com.example.substationtemperature.visualshape;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.substationtemperature.base.PropertyForDrawPicture;

public class Capacitor {
	private Paint paint=new Paint();
	private Canvas canvas;
	private PropertyForDrawPicture pfdp;
	public Capacitor(){}
	public Capacitor(Canvas canvas,PropertyForDrawPicture pfdp){
		this.canvas=canvas;
		this.pfdp=pfdp;
		CalcVisual();
		
	}
	private void CalcVisual() {
		// TODO Auto-generated method stub
		paint.setStrokeJoin(Paint.Join.ROUND);   
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
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
		float mx=rect.left+rect.width()/2.0f;
		int y1=rect.top+rect.height()/3;
		int y2=rect.bottom-rect.height()/3;
		if(pfdp.getIsThreephase()){
			float per=rect.width()/8.0f;
			float lxm=rect.left+per;
			float lxr=rect.left+per*2;
			float mxl=rect.left+per*3;
			float mxr=rect.right-per*3;
			float rxl=rect.right-per*2;
			float rxm=rect.right-per;
			y1+=rect.height()/12;
			y2-=rect.height()/12;
			canvas.drawLine(mx, rect.top, mx, y1,paint);
			canvas.drawLine(mxl, y1, mxr, y1, paint);
			canvas.drawLine(mxl, y2, mxr, y2, paint);
			canvas.drawLine(mx, y2, mx, rect.bottom, paint);
			
			canvas.drawLine(lxm, rect.top, lxm, y1, paint);
			canvas.drawLine(rect.left, y1, lxr, y1, paint);
			canvas.drawLine(rect.left, y2, lxr, y2, paint);
			canvas.drawLine(lxm,y2,lxm,rect.bottom,paint);
			
			canvas.drawLine(rxm, rect.top, rxm, y1, paint);
			canvas.drawLine(rxl,y1,rect.right,y1,paint);
			canvas.drawLine(rxl, y2, rect.right, y2, paint);
			canvas.drawLine(rxm,y2, rxm, rect.bottom, paint);
			
			canvas.drawLine(lxm, rect.bottom, rxm, rect.bottom, paint);
		}else{
			canvas.drawLine(mx, rect.top, mx, y1, paint);
			canvas.drawLine(rect.left, y1, rect.right, y1, paint);
			canvas.drawLine(rect.left, y2, rect.right, y2, paint);
			canvas.drawLine(mx, y2, mx, rect.bottom, paint);
		}
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
}
