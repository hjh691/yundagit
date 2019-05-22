package com.example.substationtemperature.base;

import org.json.JSONObject;

import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;

public class PropertyForDrawPicture {
	public PropertyForDrawPicture(){
	}
	private String _type="";//1
	public void setType(String type){
		_type=type;
	}
	public String getType(){return _type;}
	private int StrokeColor=0;//2
	public void setStrokeColor(int stcolor){
		StrokeColor=stcolor;
	}
	public int getStrokeColor(){return StrokeColor;}
	private float StrokeThinkness=1;//3
	public void setStrokeThinkness(float stthinkness){
		StrokeThinkness=stthinkness;
	}
	public float getStrokeThinkness(){return StrokeThinkness;}
	private  Point StartPoint=new Point();//4
	public void setStartPoint(Point startpoint){
		StartPoint=startpoint;
	}
	public Point getStartPoint(){return StartPoint;}
	private Point EndPoint=new Point();//5
	public void setEndPoint(Point endpoint){
		EndPoint=endpoint;
	}
	public Point getEndPoint(){return EndPoint;}
	private int ErrorColor=-1;//6
	public void setErrorColor(int errcolor){
		ErrorColor=errcolor;
	}
	public int getErrorColor(){return ErrorColor;}
	private Matrix _matrix;//7
	public void setMatrix(Matrix matrix){
		_matrix=matrix;
	}
	public Matrix getMatrix(){return _matrix;}
	private String Binding="null";//8
	public void setBinding(String binding){
		Binding=binding;
	}
	public String getBinding(){return Binding;}
	private String NodeType="";//9
	public void setNodeType(String nodetype){
		NodeType=nodetype;
	}
	public String getNodeType(){return NodeType;}
	private float Size=10f;//10
	public void setSize(float size){
		Size=size;
	}
	public float getSize(){return Size;}
	private String Text="";//11
	public void setText(String text){
		Text=text;
	}
	public String getText(){return Text;}
	private String FontFamily="";//12
	public void setFontFamily(String fontfamily){
		FontFamily=fontfamily;
	}
	public String getFontFamily(){return FontFamily;}
	private String FontStyle="";//13
	public void setFontStyle(String fontstyle){
		FontStyle=fontstyle;
	}
	public String getFontStyle(){return FontStyle;}
	private String FontWeight="";//14
	public void setFontWeight(String fontweight){
		FontWeight=fontweight;
	}
	public String getFontWeight(){return FontWeight;}
	private String FontStretch="";//15
	public void setFontStretch(String fontstretch){
		FontStretch=fontstretch;
	}
	public String getFontStretch(){return FontStretch;}
	private float TextSpace=0.1f;//16
	public void setTextSpace(float textspace){
		TextSpace=textspace;
	}
	public float getTextSpace(){return TextSpace;}
	private boolean Vertical=false;//17
	public void setVertical(boolean isvertical){
		Vertical=isvertical;
	}
	public boolean getVertical(){return Vertical;}
	private String TitleType="";//18
	public void setTitleType(String titletype){
		TitleType=titletype;
	}
	public String getTitleType(){return TitleType;}
	private boolean isError=false;//19
	public void setIsError(boolean iserror){
		isError=iserror;
	}
	public boolean getIsError(){return isError;}
	
	private boolean isClosed=false;//20
	public void setIsClosed(boolean isclosed){
		isClosed=isclosed;
	}
	public boolean getIsClosed(){return isClosed;}
	private boolean IsThreephase;//21
	public void setIsThreephase(boolean isthreephase){
		this.IsThreephase=isthreephase;
	}
	public boolean getIsThreephase(){return IsThreephase;}
	private Point[] Points;//22
	public void setPoints(Point[] points){
		this.Points=points;
	}
	public Point[] getPoints(){return Points;}
	private boolean isFill=false;//23
	public void setIsFill(boolean isfill){
		this.isFill=isfill;
	}
	public boolean getIsFill(){return isFill;}
	private int FillColor=0;//24
	public void setFillColor(int fillcolor){
		this.FillColor=fillcolor;
	}
	public int getFillColor(){return FillColor;}
	public void setProperty(String pname,Object pvalue){
		switch(pname){
		case "StrokeColor":
			this.setStrokeColor(Color.parseColor((String) pvalue));
			break;
		case "StrokeThinkness":
			this.setStrokeThinkness(Float.valueOf(String.valueOf(pvalue)));;
			break;
		case "StartPoint":
			this.setStartPoint(stringtopoint((String)pvalue));
			break;
		case "EndPoint":
			this.setEndPoint(stringtopoint((String)pvalue));
			break;
		case "ErrorColor":
			this.setErrorColor(Color.parseColor((String)pvalue));
			break;
		case "Binding":
			if(pvalue.toString()!="null")
			this.setBinding((String)pvalue);
			break;
		case "_type":
			this.setType((String)pvalue);
			break;
		case "NodeType":
			this.setNodeType((String)pvalue);
			break;
		case "Size":
			this.setSize(Float.valueOf(String.valueOf(pvalue)));
			break;
		case "IsClosed":
			this.setIsClosed((boolean)pvalue);
			break;
		case "IsFill":
			this.setIsFill((boolean)pvalue);
			break;
		case "FillColor":
			this.setFillColor(Color.parseColor((String)pvalue));
			break;
		case "Points":
			String key=(pvalue.toString()).substring(2,(pvalue.toString()).indexOf("]")-1);
			if(key.length()>0){
				//String sp="\\";
				//key=key.replaceAll("\\\"","");
				//key=key.replaceAll("\\\\", "");
				String ps[]=key.split("\",\"");
				Point[] points=new Point[ps.length];
				for (int i=0;i<ps.length;i++){
					String fs[]=ps[i].split(",");
					Point point=new Point();
					point.x=Integer.valueOf(fs[0]);
					point.y=Integer.valueOf(fs[1]);
					points[i]=point;
				}
				this.setPoints(points);
			}
			
			break;
		case "Text":
			this.setText((String)pvalue);
			break;
		case "FontFamily":
			this.setFontFamily((String)pvalue);
			break;
		case "FontStyle":
			this.setFontStyle((String)pvalue);
			break;
		case "FontWeight":
			this.setFontWeight((String)pvalue);
			break;
		case "FontStretch":
			this.setFontStretch((String)pvalue);
			break;
		case "FontSize":
			this.setSize(Float.valueOf(String.valueOf(pvalue)));
			break;
		case "TextSpace":
			this.setTextSpace(Float.valueOf(String.valueOf(pvalue)));
			break;
		case "Vertical":
			this.setVertical((boolean)pvalue);
			break;
		case "TitleType":
			this.setTitleType((String)pvalue);
			break;
		
		}
	}
	private Point stringtopoint(String str ){
		int j=str.indexOf(",");
		int x=0,y=0;
		String key=str.substring(0,str.indexOf(","));
		if(key.length()>0){
			x=Math.round(Float.valueOf(key));
		}
		key=str.substring(j+1);
		if(key.length()>0){
			y=Math.round(Float.valueOf(key));
		}
		Point point=new Point(x,y);
		return point;
		
	}
}
