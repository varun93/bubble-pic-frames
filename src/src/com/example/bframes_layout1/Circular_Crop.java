package com.example.bframes_layout1;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import uk.co.senab.photoview.PhotoViewAttacher;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aviary.android.feather.FeatherActivity;
import com.aviary.android.feather.library.Constants;

@SuppressLint("NewApi")
public class Circular_Crop extends Activity{

	private ImageView iv;
	private TextView text;	
	private ImageView background;
	private PhotoViewAttacher attacher;
    private Button okButton,cancelButton;
	private Uri uri;
	private int width,height;
    private static final int FEATHER_ACTIVITY= 1;
	private static final String IMAGE_DIRECTORY_NAME = "Bubble_Pic_Frames";
private Context context;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.ccrop_activity);

		iv = ((ImageView) findViewById(R.id.fgimview));
		background =  (ImageView) findViewById(R.id.maskimview);
		okButton = (Button) findViewById(R.id.btn_ok);
		cancelButton = (Button) findViewById(R.id.btn_cancel);
		text=(TextView)findViewById(R.id.titlebar);

        CustomFontHelper.setCustomFont(okButton, "fonts/Kirsty.ttf", this);
		CustomFontHelper.setCustomFont(cancelButton, "fonts/Kirsty.ttf", this);
		CustomFontHelper.setCustomFont(text, "fonts/Kirsty.ttf", this);
		
		
        DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		float logicalDensity = displaymetrics.density;

		int px = (int) ((60+60) * logicalDensity + 0.5);
		int px1 = (int) ((65) * logicalDensity + 0.5);

		width= displaymetrics.widthPixels - px1;
		height= displaymetrics.heightPixels -px;

		iv.getLayoutParams().height=width;
		iv.getLayoutParams().width=width;

		background.getLayoutParams().height=height;
		background.getLayoutParams().width=width;
            Bitmap bitmap=null; 
           if(getIntent().getParcelableExtra("imageuri")!=null)
           {
		    uri=getIntent().getParcelableExtra("imageuri");
		    BitmapFactory.Options options = new BitmapFactory.Options();

			// downsizing image as it throws OutOfMemory Exception for larger
			// images
			options.inSampleSize = 8;

			bitmap = BitmapFactory.decodeFile(uri.getPath(),
					options);
			
			
           String filePath = getPath(uri);
			
           
		    int rotate = getCameraPhotoOrientation(uri,filePath);
			Matrix matrix = new Matrix();
			
			matrix.setRotate(rotate);
			
			bitmap = Bitmap.createBitmap(bitmap , 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
			
			
           }
           
           else if(getIntent().getParcelableExtra("imageBack")!=null)
           {
        	   uri=getIntent().getParcelableExtra("imageBack");
        	   bitmap = getBitmap();
        	   
           }
        	   
	        
	       
		
        //Bitmap bitmap = getBitmap();
		iv.setImageBitmap(bitmap);
		attacher = new PhotoViewAttacher(iv);
		attacher.canZoom();
	    background.setImageBitmap(getMask());
		
		


	    
	    


		okButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Bitmap bitmap = getCroppedBitmap();

				Intent intent1 = new Intent(getApplicationContext(), FeatherActivity.class );

				Uri uri1 = null;
				try {
					uri1 = getImageUri(bitmap);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(bitmap!=null)
				 bitmap.recycle();
				
				intent1.setData(uri1);
				intent1.putExtra( Constants.EXTRA_EFFECTS_ENABLE_EXTERNAL_PACKS, true );				
				intent1.putExtra( Constants.EXTRA_FRAMES_ENABLE_EXTERNAL_PACKS, true );		
				intent1.putExtra( Constants.EXTRA_STICKERS_ENABLE_EXTERNAL_PACKS,true );
				intent1.putExtra( Constants.EXTRA_EFFECTS_ENABLE_FAST_PREVIEW, true );
				startActivityForResult(intent1,FEATHER_ACTIVITY);
				overridePendingTransition  (R.anim.slide_in_right, R.anim.slide_out_right);

			}
		});

		cancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intentresult = new Intent();
				setResult(Activity.RESULT_CANCELED, intentresult);
				finish();
				overridePendingTransition  (R.anim.slide_in_right, R.anim.slide_out_right);
			}
		});

	}

	
	
	
	public Bitmap getMask()
	{

		Bitmap crop = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
		Canvas temp = new Canvas(crop);
		
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);

		temp.drawRect(0, 0, width, width, paint);

		paint.setColor(Color.WHITE);
		paint.setStyle(Style.FILL);

		temp.drawCircle(width/2, width/2, width/2 , paint);
        paint.setColor(getResources().getColor(android.R.color.transparent));
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

		temp.drawCircle(width/2, width/2, width/2 - 5, paint);

		return crop;
	}
	
	private String getPath(Uri uri)
	{
		
		Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
		if(cursor == null)
			return uri.getPath();
		else
		{
			cursor.moveToFirst();
			int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
			return cursor.getString(index);
		}

	}

	
	public int getCameraPhotoOrientation(Uri imageUri,String imagePath){
	     int rotate = 0;
	     
	     try {
	         context.getContentResolver().notifyChange(imageUri, null);
	         File imageFile = new File(imagePath);
	         ExifInterface exif = new ExifInterface(
	                 imageFile.getAbsolutePath());
	         int orientation = exif.getAttributeInt(
	                 ExifInterface.TAG_ORIENTATION,
	                 ExifInterface.ORIENTATION_NORMAL);

	         switch (orientation) {
	         
	         case ExifInterface.ORIENTATION_ROTATE_270:
	             rotate = 270;
	             break;
	         case ExifInterface.ORIENTATION_ROTATE_180:
	             rotate = 180;
	             break;
	         case ExifInterface.ORIENTATION_ROTATE_90:
	             rotate = 90;
	             break;
	         }


	         
	     } catch (Exception e) {
	         e.printStackTrace();
	     }
	    return rotate;
	 }

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onStop() {
		super.onStop();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}     

	@Override
	public void onBackPressed() 
	{

		finish();
		overridePendingTransition  (R.anim.slide_in_right, R.anim.slide_out_right);
		return;
	}

	private File getOutputMediaFile() {

		File mediaStorageDir = new File(
				Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				IMAGE_DIRECTORY_NAME);

		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(IMAGE_DIRECTORY_NAME, "Failed to create "
						+ IMAGE_DIRECTORY_NAME + " directory");
				return null;
			}
		}

		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());
		File mediaFile = null;

		try {
			mediaFile= File.createTempFile("IMG_" + timeStamp, ".jpg", mediaStorageDir);
		} catch (IOException e) {
			e.printStackTrace();
		}


		return mediaFile;
	}


	public Bitmap getCroppedBitmap() {
		Canvas canvas;
		Bitmap bmap2,bmap1,bmap3;
		int imview_w;

		iv.buildDrawingCache();
		bmap3 = iv.getDrawingCache();
		
		imview_w = bmap3.getWidth();

		bmap1 = Bitmap.createBitmap(imview_w , imview_w, bmap3.getConfig());
		bmap1.eraseColor(Color.WHITE);

		bmap2 = Bitmap.createBitmap(imview_w , imview_w , bmap3.getConfig());
		canvas = new Canvas(bmap2);
		canvas.drawBitmap(bmap1, new Matrix(), null);
		canvas.drawBitmap(bmap3, 0, 0, null);		
      
		
		bmap1 = Bitmap.createBitmap(imview_w , imview_w , bmap3.getConfig());
		canvas = new Canvas(bmap1);

		Paint paint = new Paint();
		Rect rect = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(Color.BLACK);

		canvas.drawCircle(canvas.getWidth()/2, canvas.getHeight()/2, canvas.getWidth()/2 - 5, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bmap2, rect, rect, paint);

		return bmap1;

	}



	public Uri getImageUri(Bitmap inImage) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		inImage.compress(Bitmap.CompressFormat.JPEG, 80, bytes);
		byte[] byteArray = bytes.toByteArray();
		File file1 = getOutputMediaFile();
		FileOutputStream outstream;
		outstream = new FileOutputStream(file1);
		outstream.write(byteArray);
		outstream.close();

		return Uri.fromFile(file1);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == FEATHER_ACTIVITY && resultCode == RESULT_OK) {
			Uri uri1 = data.getData();
			Bundle extra = data.getExtras();
			if( null != extra ) {
				boolean changed = extra.getBoolean( Constants.EXTRA_OUT_BITMAP_CHANGED );
			}



			Intent intentresult = new Intent();
			intentresult.putExtra("CroppedImage", uri1);
			setResult(Activity.RESULT_OK, intentresult);
			finish();
			overridePendingTransition  (R.anim.slide_in_right, R.anim.slide_out_right);
		}

	}

	public Bitmap getBitmap() {
	    Bitmap bitmap = null;
	    try {
	        // Decode image size
	        BitmapFactory.Options o = new BitmapFactory.Options();
	        o.inJustDecodeBounds = true;
	        
	      InputStream   in = getContentResolver().openInputStream(uri);
	        BitmapFactory.decodeStream(in, null, o);
	        in.close();
	        int IMAGE_MAX_SIZE = 1000;
	        int scale = 1;
	        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
	            scale = (int) Math.pow(
	                    2,
	                    (int) Math.round(Math.log(IMAGE_MAX_SIZE
	                            / (double) Math.max(o.outHeight, o.outWidth))
	                            / Math.log(0.5)));
	        }

	        // Decode with inSampleSize
	        BitmapFactory.Options o2 = new BitmapFactory.Options();
	        o2.inSampleSize = scale;
	        in = getContentResolver().openInputStream(uri);
	        
	        bitmap = BitmapFactory.decodeStream(in, null, o2);
	        in.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return bitmap;
	}
	
	public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
	    int width = bm.getWidth();
	    int height = bm.getHeight();
	    float scaleWidth = ((float) newWidth) / width;
	    float scaleHeight = ((float) newHeight) / height;
	    // CREATE A MATRIX FOR THE MANIPULATION
	    Matrix matrix = new Matrix();
	    // RESIZE THE BIT MAP
	    matrix.postScale(scaleWidth, scaleHeight);

	    // "RECREATE" THE NEW BITMAP
	    Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
	    return resizedBitmap;
	}

	


	}
	
	
	
	
