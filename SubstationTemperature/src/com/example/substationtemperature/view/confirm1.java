package com.example.substationtemperature.view;


import com.example.substationtemperature.R;

import android.app.Dialog; 
import android.content.Context; 
import android.os.Bundle; 
public class confirm1 extends Dialog 
{       
Context context;     
public confirm1(Context context)
{         
super(context);         
// TODO Auto-generated constructor stub         
this.context = context;     
}     
public confirm1(Context context, int theme)
{         
super(context, theme);         
this.context = context;     
}     
@Override    
protected void onCreate(Bundle savedInstanceState) 
{         
// TODO Auto-generated method stub         
super.onCreate(savedInstanceState); 
this.setContentView(R.layout.confirm1);  
    
}   
} 
