package com.example.substationtemperature.visualshape;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;

import com.example.substationtemperature.base.PropertyForDrawPicture;

public class Node {
	private Paint paint=new Paint();
	private Canvas canvas;
	private PropertyForDrawPicture pfdp;
	public Node(){}
	public Node(Canvas canvas,PropertyForDrawPicture pfdp){
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
		Matrix mt=new Matrix();
		if((pfdp.getMatrix()!=null)&&(!pfdp.getMatrix().equals(""))){
			mt=pfdp.getMatrix();
		}
		canvas.setMatrix(mt);
		Point point=new Point(pfdp.getStartPoint().x,pfdp.getStartPoint().y);
		int x=point.x;
		int y=point.y;
		float r=pfdp.getSize()/2.0f;
		if(pfdp.getNodeType().equals("方形")){
			canvas.drawRect(x, y, pfdp.getEndPoint().x, pfdp.getEndPoint().y, paint);
		}else{
			canvas.drawCircle(x+r,y+r,r, paint);
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
