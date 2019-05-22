package com.example.substationtemperature.visualshape;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.substationtemperature.base.PropertyForDrawPicture;

public class Monitor {
	private Paint paint=new Paint();
	private Canvas canvas=new Canvas();
	private PropertyForDrawPicture pfdp=new PropertyForDrawPicture();
	public Monitor(){}
	public Monitor(Canvas canvas,PropertyForDrawPicture pfdp){
		this.canvas=canvas;
		this.pfdp=pfdp;
		new DrawText(canvas,pfdp);
		
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
