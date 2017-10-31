package com.example.bframes_layout1;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aviary.android.feather.FeatherActivity;
import com.aviary.android.feather.library.Constants;
import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest.ErrorCode;

public class BubbleActivity extends Activity {

	private static final int SELECT_PICTURE1 = 1; // for bubble --> gallery
	private static final int CAMERA_REQUEST1 = 2;
	private static final int CCROP_IMAGE= 3;
	private static final int SELECT_PICTURE2 = 4;
	private static final int CAMERA_REQUEST2 = 5;
	private static final int RCROP_IMAGE= 6;
	private static final int FEATHER_ACTIVITY= 7;
	private static final String IMAGE_DIRECTORY_NAME = "Bubble_Pic_Frames";
	public static boolean changeFlag;
	private Button add,background,change,export;
	private Context context = this;
	private BubbleFrame view;
	private Uri fileUri;
	private TextView text;	
	private int width,height;
	private Bitmap crop,circularBitmap;
	private GraphicsUtil graphicUtil = new GraphicsUtil();
	public static AlertDialog bubbleDialog = null;
	private AlertDialog backgroundDialog;
	private ArrayAdapter<String> bubbleAdapter;
	private ArrayAdapter<String> backgroundAdapter;
	private String bubbleOptions[] = new String[]{"Library","Camera","Color","Cancel"};
	private String backgroundOptions[] = new String[]{"Library","Camera","Color","Cancel"};
	private AlertDialog.Builder bubbleOption,backgroundOption;
	private Handler mHandler = new Handler();
	private int bubbleColor = Color.GREEN;
	
	
	  public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);	
        bubbleAdapter = new ArrayAdapter<String> (BubbleActivity.this, android.R.layout.select_dialog_item,bubbleOptions);
		backgroundAdapter = new ArrayAdapter<String> (BubbleActivity.this, android.R.layout.select_dialog_item,backgroundOptions);
		view = (BubbleFrame) findViewById(R.id.surfaceView1);
		
		
		add = (Button) findViewById(R.id.add);
		change = (Button) findViewById(R.id.change);
		background = (Button) findViewById(R.id.background);
		export = (Button) findViewById(R.id.export);
		text=(TextView)findViewById(R.id.title);
       
		CustomFontHelper.setCustomFont(text, "fonts/Kirsty.ttf", this);
		CustomFontHelper.setCustomFont(add, "fonts/Kirsty.ttf", this);
		CustomFontHelper.setCustomFont(change, "fonts/Kirsty.ttf", this);
		CustomFontHelper.setCustomFont(background, "fonts/Kirsty.ttf", this);
		CustomFontHelper.setCustomFont(export, "fonts/Kirsty.ttf", this);

		
		
		add.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				if(crop!=null)
				{
					view.init(crop);
					view.addBubble();
				}



			}
		});

		background.setOnClickListener(new View.OnClickListener() {


			public void onClick(View v) {

				backgroundDialog.show();

			}
		});

		export.setOnClickListener(new View.OnClickListener() {


			public void onClick(View v) {

				send();

			}
		});


		change.setOnClickListener(new View.OnClickListener() {


			public void onClick(View v) {

				bubbleDialog.show();

			}
		});

		

	}





	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		createBubbleDialog();
		createBackgroundDialog();

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

	public Uri getOutputMediaFileUri() {
		return Uri.fromFile(getOutputMediaFile());
	}

	private File getOutputMediaFile() {

		File mediaStorageDir = new File(
				Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				IMAGE_DIRECTORY_NAME);

		if (!mediaStorageDir.exists()) {
		
			if (!mediaStorageDir.mkdirs()) {
			
				Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
						+ IMAGE_DIRECTORY_NAME + " directory");
				return null;
			
			}
		}

		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());
		File mediaFile = null;
		/*	mediaFile = new File(mediaStorageDir.getPath() + File.separator
				+ "IMG_" + timeStamp + ".jpg");
		 */
		
		try {
			mediaFile= File.createTempFile("IMG_" + timeStamp, ".jpg", mediaStorageDir);
		} catch (IOException e) {
			e.printStackTrace();
		}

		/*		sendBroadcast(new Intent(
				Intent.ACTION_MEDIA_MOUNTED,
				Uri.parse("file://" + Environment.getExternalStorageDirectory())));
		 */				
		
		return mediaFile;

	}

	public  void createBubbleDialog()
	{


		bubbleOption	= new AlertDialog.Builder(BubbleActivity.this);
		bubbleOption.setTitle("How do you want to fill bubble?");
		bubbleOption.setAdapter(bubbleAdapter, new DialogInterface.OnClickListener() {


			public void onClick(DialogInterface dialog, int option) {

				if(option==0)
				{

					Intent intent = new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(Intent.createChooser(intent, "Select Picture"),
							SELECT_PICTURE1);
	
				}

				else if(option==1)
				{
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					 
				    fileUri = getOutputMediaFileUri();
				 
				    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
				 
				    startActivityForResult(intent, CAMERA_REQUEST1);
				    
				}
				else if(option==2)
				{					
					Runnable runnable = new Runnable() {
						public void run() {

							circularBitmap.eraseColor(bubbleColor);
							crop = graphicUtil.getBorderedOutput(circularBitmap);
							
							
							if(!changeFlag)
							{

						        view.init(crop);
								view.addBubble();


							}
							else if(changeFlag)
							{
							
								view.init(crop);
								view.changeBubble();
								changeFlag=false;
							}

						}
					};


					bubbleColor(runnable);

				}
				else if(option==3) //cancel
				{

				}


			}
		});
		bubbleDialog = bubbleOption.create();
		return;

	}



	public void createBackgroundDialog()
	{



		backgroundOption	= new AlertDialog.Builder(BubbleActivity.this);
		backgroundOption.setTitle("How do you want to fill the background?");
		backgroundOption.setAdapter(backgroundAdapter, new DialogInterface.OnClickListener() {


			public void onClick(DialogInterface dialog, int option) {

				if(option==0)
				{

					
					Intent intent = new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(Intent.createChooser(intent, "Select Picture"),
							SELECT_PICTURE2);
	

				}
				else if(option==1)
				{
					
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					 
				    fileUri = getOutputMediaFileUri();
				 
				    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
				 
				    startActivityForResult(intent, CAMERA_REQUEST2);
				

				}
				else if(option==2)
				{
				
					chooseColor();
				}


				else if(option==3) //cANCEL
				{

				}


			}
		});
		backgroundDialog = backgroundOption.create();
		return;


	}

	public void bubbleColor(final Runnable colorStart){
		
		AmbilWarnaDialog dialog = new AmbilWarnaDialog(context, 0xff0000ff, new OnAmbilWarnaListener() {

			public void onOk(AmbilWarnaDialog dialog, int color) {
				bubbleColor = color;
				mHandler.postDelayed(colorStart, 1);///important
				return;
			}

            public void onCancel(AmbilWarnaDialog dialog) {

			}

		});
		dialog.show();

	}


	public Bitmap getCrop()
	{

		return crop;
	}


	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if(hasFocus)
		{
			width = view.getMeasuredWidth();
			height = view.getMeasuredHeight();
			circularBitmap =  graphicUtil.getColouredBitmap(width, height);


		}

	}

	private void chooseColor()
	{

		AmbilWarnaDialog dialog = new AmbilWarnaDialog(context, 0xff0000ff, new OnAmbilWarnaListener() {



			public void onOk(AmbilWarnaDialog dialog, int color) {


				view.setColor(color);

			}


			public void onCancel(AmbilWarnaDialog dialog) {


			}
		});
		dialog.show();



	}

	public Bitmap TakeScreenshot(){    
		
		view.setDrawingCacheEnabled(true); 
		view.buildDrawingCache(true);
		Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
		view.setDrawingCacheEnabled(false); // clear drawing cache
		return bitmap;              

	}

	void doCrop(final Uri mImageCaptureUri) {
		final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setType("image/*");

		List<ResolveInfo> list = getPackageManager().queryIntentActivities( intent, 0 );

		int size = list.size();

		if (size == 0) {	        
			Toast.makeText(this, "Can not find image crop app", Toast.LENGTH_SHORT).show();

			return;
		} else {
			intent.setData(mImageCaptureUri);

			intent.putExtra("outputX", 200);
			intent.putExtra("outputY", 200);
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("scale", true);
			intent.putExtra("return-data", true);

			if (size == 1) {
				Intent i 		= new Intent(intent);
				ResolveInfo res	= list.get(0);

				i.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

				startActivityForResult(i, RCROP_IMAGE);
				overridePendingTransition  (R.anim.slide_in_left, R.anim.slide_out_left);
			} else {
				for (ResolveInfo res : list) {
					final CropOption co = new CropOption();

					co.title 	= getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
					co.icon		= getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
					co.appIntent= new Intent(intent);

					co.appIntent.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

					cropOptions.add(co);
				}

				CropOptionAdapter adapter = new CropOptionAdapter(getApplicationContext(), cropOptions);

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Choose Crop App");
				builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
					public void onClick( DialogInterface dialog, int item ) {
						startActivityForResult( cropOptions.get(item).appIntent, RCROP_IMAGE);
					}
				});

				builder.setOnCancelListener( new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel( DialogInterface dialog ) {
						/*
						if (mImageCaptureUri != null ) {
							getContentResolver().delete(mImageCaptureUri, null, null );
							mImageCaptureUri = null;
						}
						 */
					}
				} );

				AlertDialog alert = builder.create();

				alert.show();
			}
		}

	}



	public void send()
	{
		Uri uri = null;
		try {
			uri = getImageUri(context,TakeScreenshot());
		} catch (IOException e) {
			e.printStackTrace();
		}
		Intent exportIntent = new Intent(android.content.Intent.ACTION_SEND);  
		exportIntent.setType("image/jpeg");  
		exportIntent.putExtra(Intent.EXTRA_STREAM, uri);  
		startActivity(Intent.createChooser(exportIntent, "Send your picture using:"));

	}

	public Uri getImageUri(Context inContext, Bitmap inImage) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		byte[] byteArray = bytes.toByteArray();

		//	String path = Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
		//	return Uri.parse(path);
		File file1 = getOutputMediaFile();
		FileOutputStream outstream;
		outstream = new FileOutputStream(file1);
		outstream.write(byteArray);
		outstream.close();

		return Uri.fromFile(file1);
	}
	

	@Override
	public void onBackPressed() 
	{

		AlertDialog alertbox = new AlertDialog.Builder(this)
		.setMessage("Do you want to exit application?")
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {

				finish();
			}
		})
		.setNegativeButton("No", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface arg0, int arg1) {
			}
		})
		.show();

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		Bitmap bitmap = null;
		if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE2) {
			Uri selectedImageUri = data.getData();
			try {
				bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImageUri));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			doCrop(selectedImageUri);

		}
		else if (requestCode == CCROP_IMAGE )
		{
			if(resultCode == RESULT_OK) {

				Uri uri=data.getParcelableExtra("CroppedImage");
				try {
					bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				crop = graphicUtil.getBorderedOutput(graphicUtil.getCircleBitmap(Bitmap.createScaledBitmap(bitmap, width, height, false)));
				
				if(!changeFlag)
				{
					view.init(crop);
					view.addBubble();
				}
				else if(changeFlag)
				{
					view.init(crop);
					view.changeBubble();
				}



			}
			else if(resultCode == RESULT_CANCELED) {
				//do nothing
			}
		}
		else if (requestCode == CAMERA_REQUEST2 && resultCode == RESULT_OK) {
			
			sendBroadcast(new Intent(
					Intent.ACTION_MEDIA_MOUNTED,
					Uri.parse("file://" + Environment.getExternalStorageDirectory())));

			doCrop(fileUri);			
		}

		else if (requestCode == RCROP_IMAGE && resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();

			if (extras != null) {	        	
				Bitmap photo = extras.getParcelable("data");
				bitmap=photo;

				Intent intent1 = new Intent( getApplicationContext(), FeatherActivity.class );
				Uri uri1 = null;
				try {
					uri1 = getImageUri(getApplicationContext(),bitmap);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				intent1.setData(uri1);
				intent1.putExtra( Constants.EXTRA_EFFECTS_ENABLE_EXTERNAL_PACKS, true );				
				intent1.putExtra( Constants.EXTRA_FRAMES_ENABLE_EXTERNAL_PACKS, true );		
				intent1.putExtra( Constants.EXTRA_STICKERS_ENABLE_EXTERNAL_PACKS,true );
				intent1.putExtra( Constants.EXTRA_EFFECTS_ENABLE_FAST_PREVIEW, true );
				startActivityForResult(intent1,FEATHER_ACTIVITY);
				overridePendingTransition  (R.anim.slide_in_right, R.anim.slide_out_right);

			
			}
		}

		else if (requestCode == FEATHER_ACTIVITY && resultCode == RESULT_OK) {
			Uri uri1 = data.getData();
			
			Bundle extra = data.getExtras();
			if( null != extra ) {
				boolean changed = extra.getBoolean( Constants.EXTRA_OUT_BITMAP_CHANGED );
			}

			try {
				bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri1));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			view.setBackground(uri1);
			//view.setBackground(bitmap);
		}

		else if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE1) {
			Uri selectedImageUri = data.getData();
			try {
				bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImageUri));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			Intent intent1 = new Intent(getApplicationContext(),Circular_Crop.class);
			intent1.putExtra("imageBack",selectedImageUri);
			startActivityForResult(intent1, CCROP_IMAGE);
			overridePendingTransition  (R.anim.slide_in_left, R.anim.slide_out_left);

		}
		else if (requestCode == CAMERA_REQUEST1 && resultCode == RESULT_OK) {
			
			sendBroadcast(new Intent(
					Intent.ACTION_MEDIA_MOUNTED,
					Uri.parse("file://" + Environment.getExternalStorageDirectory())));

			Intent intent1 = new Intent(getApplicationContext(),Circular_Crop.class);
			intent1.putExtra("imageuri",fileUri);
			startActivityForResult(intent1, CCROP_IMAGE);
			overridePendingTransition  (R.anim.slide_in_left, R.anim.slide_out_left);

		}	 
	}

}