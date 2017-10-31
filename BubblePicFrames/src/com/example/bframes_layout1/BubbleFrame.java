package com.example.bframes_layout1;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;


@SuppressLint("NewApi")
public class BubbleFrame extends View  {



	private ArrayList<Bubble> bubbles = new ArrayList<Bubble>();
	private GestureDetector detector;
	private Bitmap crop;
	private Paint paint;
	private Context context;
	private Bubble bubbleC,lastBubble;
	private static final int INVALID_POINTER_ID = -1;
	private int mActivePointerId = INVALID_POINTER_ID;
	private Rect copy;
	private Dialog dialog;
	private ArrayAdapter<String> touchAdapter;
	private String touchOptions[] = new String[]{"Change","Delete","Cancel"};
	private AlertDialog.Builder touchBuilder;
	private int mode;
	private float intialSpacing;
	private static final int DRAG  =  1;
	private static final int ZOOM   =  2;
	private static final int NONE   = -1;
	private static float MIN_ZOOM = 0;
	private static float MAX_ZOOM = 5f;
	private float mCurrentScale = 1.0f,newscale;
    private static World world;
    
    
	public BubbleFrame(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
		this.context =  context;
		touchBuilder = new AlertDialog.Builder(context);
		initComponents();

	}


	public BubbleFrame(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context =  context;
		touchBuilder  =   new AlertDialog.Builder(context);
		initComponents();


	}

	public BubbleFrame(Context context) {
		super(context);
		this.context =  context;
		touchBuilder  =   new AlertDialog.Builder(context);
		initComponents();

	}

	public int dpToPx(int dp) {
		DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
		int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));       
		return px;
	}

	//--------------This is where the the frame size is measured----------------
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int size;
		if(widthMode == MeasureSpec.EXACTLY && widthSize == 0){
			size = widthSize;
		}
		else if(heightMode == MeasureSpec.EXACTLY && heightSize == 0){
			size = heightSize;
		}
		else{
			size = widthSize < heightSize ? widthSize : heightSize;
		}

		int finalMeasureSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
		setMeasuredDimension(finalMeasureSpec, finalMeasureSpec);
		super.onMeasure(finalMeasureSpec , finalMeasureSpec );


	}


	public void initComponents()
	{
		touchAdapter = new ArrayAdapter<String> (context, android.R.layout.select_dialog_item,touchOptions);
		createDialog();
		paint = new Paint();
		paint.setColor( Color.WHITE);
		paint.setStrokeWidth(8);
		paint.setStyle( Style.STROKE );
		detector = new GestureDetector(getContext(), new GestureListener());
     
	}


   public void createWorld()
   {
	  Vector2 gravity = new Vector2(0f,-9.8f);
	   world = new World(gravity,true);
	   world.setAutoClearForces(true);
	   
   }

	public void createDialog()
	{


		touchBuilder.setTitle("Bubble touched!");
		touchBuilder.setAdapter(touchAdapter, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int option) {

				if(option==0)
				{
					BubbleActivity.changeFlag=true;

					BubbleActivity.bubbleDialog.show();

					dialog.dismiss();

				}

				else if(option==1)
				{

					bubbles.remove(bubbleC);

					dialog.dismiss();

				}
				else
				{

					//cancel
				}

			}
		});
		dialog= touchBuilder.create();

		dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
		return;


	}



	public void init(Bitmap crop) {

		this.crop = crop;

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


	public void setBackground(Uri uri)
	{
		File f = new File(getPath(uri)); 
		String path = f.getAbsolutePath();
		int sdk = android.os.Build.VERSION.SDK_INT;

		if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			setBackgroundDrawable(Drawable.createFromPath(path));

		} else {
			setBackground(Drawable.createFromPath(path));

		}





	}




	@SuppressWarnings("deprecation")
	public void setBackground(Bitmap bitmap)
	{
		//File f = new File(getPath(uri)); 

		//Drawable d = Drawable.
		int sdk = android.os.Build.VERSION.SDK_INT;
		if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {

			setBackgroundDrawable(new BitmapDrawable(getResources(),bitmap));
		} else {
			setBackground(new BitmapDrawable(getResources(),bitmap));

		}



	}

	public void setColor(int color)
	{

		setBackgroundColor(color);

	}


	public RectF calculateBubbleBounds(Vector2 position,float radius)
	{
		RectF bounds = new RectF();
		bounds.left = position.x - radius;
		bounds.right = position.x + radius;
		bounds.top = position.y + radius;
		bounds.left = position.y - radius;
		
		return bounds;
	}

	public static void removeBodySafely(Body body) {
	    //to prevent some obscure c assertion that happened randomly once in a blue moon
	    final Array<JointEdge> list = body.getJointList();
	    while (list.size > 0) {
	        world.destroyJoint(list.get(0).joint);
	    }
	    // actual remove
	    world.destroyBody(body);
	}


	protected void onDraw(Canvas canvas) {

		canvas.drawRect( 0, 0, getWidth(), getHeight(), paint );
		/*
		Iterator<Bubble> itr = bubbles.iterator();

		
		while(itr.hasNext())
		{
			Bubble bubble = itr.next();
			bubble.draw(canvas);

		}
		*/

        Iterator<Body> itr = bodies.iterator();

        
		while(itr.hasNext())
		{
			Body body = itr.next();
			
			//bubble.draw(canvas);

		}
		
		
		invalidate();



	}


	public void changeBubble()
	{

		bubbles.remove(bubbleC);
		Bubble bubble = new Bubble(this, new BitmapDrawable(getResources(),this.crop));
		bubble.setBubblePosition(copy.left, copy.top);
		bubbles.add(bubble);
	}

	public void addBubbleTest()
	{
		
		if(bubbles == null)
		{
			
			createWorld();
		}
		Random position = new Random();
		// Create Dynamic Body
	    BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.allowSleep=true;
        bodyDef.position.x = position.nextInt((int)(this.getWidth()/2))+position.nextInt((int)(this.getWidth()/4));;
		bodyDef.position.y = position.nextInt((int)(this.getWidth()/2))+position.nextInt((int)(this.getWidth()/4));;
		
		Body body = world.createBody(bodyDef);   
		body.setUserData("Bubble");
		
		CircleShape circle = new CircleShape();
        circle.setRadius((int)(Math.max(this.getWidth(), this.getHeight())/8));
        
        
        FixtureDef ballFixtureDef = new FixtureDef();
		ballFixtureDef.density = 1.0f; 
	    ballFixtureDef.restitution = 0.9f; 
		ballFixtureDef.friction = 0.2f;
		ballFixtureDef.shape = circle;
        body.createFixture( ballFixtureDef );
        
        
        //bodies.add(body);
    
		

		
	}
	
	
	public void addBubble()
	{

		Bubble bubble = new Bubble(this, new BitmapDrawable(getResources(),crop));
		Random position = new Random();
		int x = position.nextInt((int)(this.getWidth()/2))+position.nextInt((int)(this.getWidth()/4));
		int y = position.nextInt((int)(this.getHeight()/2))+position.nextInt((int)(this.getHeight()/4));
		bubble.setBubblePosition(x, y);
		bubbles.add(bubble);

	}
	

	private Bubble touches_any_Bubble(float x,float y)
	{
		Iterator<Bubble> itr = bubbles.iterator();
		while(itr.hasNext())
		{
			Bubble bubble = itr.next();
			if(bubble.getBounds().contains((int )x, (int )y))
				return bubble;

		}

		return null;
	}

	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}



	public boolean onTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		detector.onTouchEvent(event);

		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: {


			final float x = event.getX();
			final float y = event.getY();

			Bubble bubbleN = touches_any_Bubble(x, y);

			if(bubbleN != null)
			{
				lastBubble = bubbleN;
				mode = DRAG;
				bubbleN.lastTouchX =  x;
				bubbleN.lastTouchY = y;

			}

			break;
		}
		case MotionEvent.ACTION_POINTER_DOWN:
		{
			final float x = event.getX();
			final float y = event.getY();

			intialSpacing = spacing(event);
			mode = ZOOM;
			break;
		}



		case MotionEvent.ACTION_MOVE: 

			final float x = event.getX();
			final float y = event.getY();


			Bubble bubbleN = touches_any_Bubble(x, y);


			if(bubbleN !=null && mode == DRAG)
			{
				lastBubble = bubbleN;
				final float dx = x - bubbleN.lastTouchX;
				final float dy = y - bubbleN.lastTouchY;

				if(bubbleN.checkExternalBounds(bubbleN.centerX+dx, dy+bubbleN.centerY))
				{

					bubbleN.centerX += dx;
					bubbleN.centerY += dy;
				}

				bubbleN.lastTouchX = (int) x;
				bubbleN.lastTouchY = (int) y;

			}

			else if(mode == ZOOM)
			{

				float newDist = spacing(event);
				float scale = newDist / intialSpacing;
				newscale = scale*mCurrentScale;

				if (newscale < 10 && newscale > 0.1)
				{
					lastBubble.setRadius(newscale);
					mCurrentScale = newscale;
				} 
				else
					return false;

			}

			invalidate();
			break;


		case MotionEvent.ACTION_UP: mode=NONE;


		case MotionEvent.ACTION_POINTER_UP: mode=NONE;


		break;

		case MotionEvent.ACTION_CANCEL: {
			mActivePointerId = INVALID_POINTER_ID;
			break;
		}




		// Extract the index of the pointer that left the touch sensor


		}

		return true;
	}





	public class GestureListener implements OnGestureListener
	{

		public boolean onDoubleTap(MotionEvent e) {

			return detector.onTouchEvent(e);

		}



		@Override
		public boolean onDown(MotionEvent event) {

			return true;
		}

		@Override
		public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
				float arg3) {
			// TODO Auto-generated method stub
			return false;
		}


		@Override
		public void onLongPress(final MotionEvent e) {


			bubbleC = touches_any_Bubble(e.getX(), e.getY());

			if(bubbleC != null)
			{

				copy = bubbleC.getBounds();

				dialog.show();

			}


		}

		@Override
		public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
				float arg3) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onShowPress(MotionEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean onSingleTapUp(MotionEvent arg0) {
			// TODO Auto-generated method stub
			return false;
		}



	}




}