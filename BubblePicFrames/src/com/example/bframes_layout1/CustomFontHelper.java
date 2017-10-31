package com.example.bframes_layout1;

import java.util.Hashtable;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.Button;
import android.widget.TextView;

public class CustomFontHelper {

   
	public static void setCustomFont(Button button,String font,Context context)
	{
		
		 if(font == null) {
	            return;
	        }
	        Typeface tf = FontCache.get(font, context);
	        if(tf != null) {
	            button.setTypeface(tf);
	        }
		
	}
	
    public static void setCustomFont(TextView textview, String font, Context context) {
        if(font == null) {
            return;
        }
        Typeface tf = FontCache.get(font, context);
        if(tf != null) {
            textview.setTypeface(tf);
        }
    }

}

 class FontCache {

    private static Hashtable<String, Typeface> fontCache = new Hashtable<String, Typeface>();

    public static Typeface get(String name, Context context) {
        Typeface tf = fontCache.get(name);
        if(tf == null) {
            try {
                tf = Typeface.createFromAsset(context.getAssets(), name);
            }
            catch (Exception e) {
                return null;
            }
            fontCache.put(name, tf);
        }
        return tf;
    }
}