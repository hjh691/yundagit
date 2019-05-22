package com.example.substationtemperature.floatwindow;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.example.substationtemperature.R;

public class Alertdialog extends Dialog 
{       
Context context;     
public Alertdialog(Context context)
{         
super(context);         
// TODO Auto-generated constructor stub         
this.context = context;     
}     
public Alertdialog(Context context, int theme)
{         
super(context, theme);         
this.context = context;     
}     
@Override    
protected void onCreate(Bundle savedInstanceState) 
{         
// TODO Auto-generated method stub         
super.onCreate(savedInstanceState); 
this.setContentView(R.layout.layout_window);  
    
}   
} 
