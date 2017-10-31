package com.example.bframes_layout1;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;


public class Bubble{

	float posX,posY;
	float lastTouchX,lastTouchY;
	private  Drawable bubble; 
	private BubbleFrame view;
    private int width,height;
    private float factor;
    float radius,centerX,centerY;
    RectF bounds;

	public Bubble(BubbleFrame view,Drawable bubble) {

		this.view = view;
		width = this.view.getWidth();
		height = this.view.getHeight();
		this.bubble = bubble;
		this.radius = (int)(Math.max(width, height)/8);
		
	}
	
	public float getLeft()
	{
		return this.bounds.left;
	}
	public float getRight()
	{
		return this.bounds.right;
	}
	public float getTop()
	{
		return this.bounds.top;
	}
	
	public float getBottom()
	{
		return this.bounds.bottom;
	}
	public void setBubbleBounds(RectF bounds)
	{
		this.bounds = bounds;
	}
	
	public RectF getBubbleBounds()
	{
		
		return this.bounds;
	
	}

	
	public void setRadius(double scale)
	{    
		this.radius = (float)(this.radius*scale);
	}
	
	public float getRadius()
	{
		return this.radius;
	}
	
    public void setBubblePosition(float x,float y)
    {
    	
    	this.centerX=x;
    	this.centerY=y;
    
 	 }
   
	 
	public Rect getBounds()
	{
		return bubble.getBounds();
	}

	
	
	public void draw(Canvas canvas){
         
		bubble.setBounds((int)(this.centerX - this.radius), (int)(this.centerY - this.radius), (int)(this.centerX + this.radius), (int)(this.centerY + this.radius));
		bubble.draw(canvas);
	}

//-------considering  margin ----------//
	public boolean checkExternalBounds(float x,float y) {
		return ((x + radius  < width - 4) && (x - radius  > 4)   &&  (y + radius < height - 4) && (y - radius > 4));
	}


}
