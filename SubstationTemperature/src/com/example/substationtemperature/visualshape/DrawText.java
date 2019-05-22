package com.example.substationtemperature.visualshape;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;

import com.example.substationtemperature.base.PropertyForDrawPicture;

public class DrawText {
	private Paint paint=new Paint();
	private Canvas canvas;
	private PropertyForDrawPicture pfdp;
	public DrawText(){}
	public DrawText(Canvas canvas,PropertyForDrawPicture pfdp){
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
		//todo
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
		
		if(pfdp.getVertical()){
			String str="";
			for(int s=0;s<pfdp.getText().length();s++){
				str=pfdp.getText().substring(s,s+1);
				canvas.drawText(str, pfdp.getStartPoint().x,pfdp.getStartPoint().y+s*paint.getTextSize(), paint);
			}
			
		}else{
			canvas.drawText(pfdp.getText(), pfdp.getStartPoint().x,pfdp.getStartPoint().y, paint);
		}
	}
}
