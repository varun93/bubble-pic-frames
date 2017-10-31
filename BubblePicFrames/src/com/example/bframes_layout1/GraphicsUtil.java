package com.example.bframes_layout1;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;


public class GraphicsUtil {


	public Bitmap getCircleBitmap(Bitmap scaleBitmapImage) {

		Bitmap targetBitmap = Bitmap.createBitmap(scaleBitmapImage.getWidth(), 
				scaleBitmapImage.getHeight(),Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(targetBitmap);

		Path path = new Path();


		path.addCircle(canvas.getWidth()/2,canvas.getHeight()/2,Math.min(canvas.getWidth(), canvas.getHeight()/2),Path.Direction.CCW);
		canvas.clipPath(path);

		Bitmap sourceBitmap = scaleBitmapImage;
		canvas.drawBitmap(sourceBitmap, 
				new Rect(0, 0, sourceBitmap.getWidth(),
						sourceBitmap.getHeight()), 
						new Rect(0, 0, sourceBitmap.getWidth(),
								sourceBitmap.getHeight()), null);
		return targetBitmap;

	}


	public static Bitmap getRoundedBitmap(Bitmap bitmap, int pixels) {

		Bitmap inpBitmap = bitmap;
		int width = 0;
		int height = 0;
		width = inpBitmap.getWidth();
		height = inpBitmap.getHeight();

		if (width <= height) {
			height = width;
		} else {
			width = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, width, height);
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(Color.GREEN);

		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(inpBitmap, rect, rect, paint);

		return output;
	}

	public Bitmap getBorderedOutput(Bitmap bitmap)
	{

		int w = bitmap.getWidth();                                          
		int h = bitmap.getHeight();                                         

		int radius = Math.min(h / 2, w / 2);                                
		Bitmap output = Bitmap.createBitmap(w + 8, h + 8, Config.ARGB_8888);

		Paint p = new Paint();                                              
		p.setAntiAlias(true);                                               

		Canvas c = new Canvas(output);                                      
		c.drawARGB(0, 0, 0, 0);                                             
		p.setStyle(Style.FILL);                                             

		c.drawCircle((w / 2) + 4, (h / 2) + 4, radius, p);                  

		p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));                 

		c.drawBitmap(bitmap, 4, 4, p);                                      
		p.setXfermode(null);                                                
		p.setStyle(Style.STROKE);                                           
		p.setColor(Color.BLACK);                                            
		p.setStrokeWidth(3);    

		c.drawCircle((w / 2) + 4, (h / 2) + 4, radius, p); 

		return output;

	}

	public Bitmap getColouredBitmap(int width,int height)
	{

		Bitmap circularBitmap = Bitmap.createBitmap(width,height,Config.ARGB_8888);
		Canvas canvas = new Canvas(circularBitmap);
		final Paint paint = new Paint();
		canvas.drawCircle(circularBitmap.getWidth()/2, circularBitmap.getHeight()/2, Math.min(circularBitmap.getWidth()/2, circularBitmap.getHeight()/2), paint);
		return circularBitmap;
	}



}
