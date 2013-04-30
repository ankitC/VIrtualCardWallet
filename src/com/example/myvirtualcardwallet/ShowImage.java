package com.example.myvirtualcardwallet;

import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;



import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;


public class ShowImage extends Activity implements OnTouchListener 
{
	private static final String TAG = "Touch";
	@SuppressWarnings("unused")
	private static final float MIN_ZOOM = 1f,MAX_ZOOM = 1f;

	// These matrices will be used to scale points of the image
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();

	// The 3 states (events) which the user is trying to perform
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;

	// these PointF objects are used to record the point(s) the user is touching
	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_image);
		Bundle b=this.getIntent().getExtras();
		String username=b.getString("Username");

		ParseQuery query = new ParseQuery("Card");
		query.whereEqualTo("Username", username);
		query.getFirstInBackground(new GetCallback() {

			public void done(final ParseObject foundCard, ParseException arg1) {
				ParseFile showImage=(ParseFile) foundCard.get("Photo");
				showImage.getDataInBackground(new GetDataCallback() {
					public void done(byte[] data, ParseException e) {
						if (e == null) {
							Bitmap bitmap=BitmapFactory.decodeByteArray(data, 0, data.length);
							ImageView image=(ImageView) findViewById(R.id.display_image);
							image.setImageBitmap(bitmap);
							image.setOnTouchListener(new OnTouchListener() {
								
								@Override
								public boolean onTouch(View v, MotionEvent event) {
									ImageView view = (ImageView) v;
									view.setScaleType(ImageView.ScaleType.MATRIX);
									float scale;

									dumpEvent(event);
									// Handle touch events here...

									switch (event.getAction() & MotionEvent.ACTION_MASK) 
									{
									case MotionEvent.ACTION_DOWN:   // first finger down only
										savedMatrix.set(matrix);
										start.set(event.getX(), event.getY());
										Log.d(TAG, "mode=DRAG"); // write to LogCat
										mode = DRAG;
										break;

									case MotionEvent.ACTION_UP: // first finger lifted

									case MotionEvent.ACTION_POINTER_UP: // second finger lifted

										mode = NONE;
										Log.d(TAG, "mode=NONE");
										break;

									case MotionEvent.ACTION_POINTER_DOWN: // first and second finger down

										oldDist = spacing(event);
										Log.d(TAG, "oldDist=" + oldDist);
										if (oldDist > 5f) {
											savedMatrix.set(matrix);
											midPoint(mid, event);
											mode = ZOOM;
											Log.d(TAG, "mode=ZOOM");
										}
										break;

									case MotionEvent.ACTION_MOVE:

										if (mode == DRAG) 
										{ 
											matrix.set(savedMatrix);
											matrix.postTranslate(event.getX() - start.x, event.getY() - start.y); // create the transformation in the matrix  of points
										} 
										else if (mode == ZOOM) 
										{ 
											// pinch zooming
											float newDist = spacing(event);
											Log.d(TAG, "newDist=" + newDist);
											if (newDist > 5f) 
											{
												matrix.set(savedMatrix);
												scale = newDist / oldDist; // setting the scaling of the
												// matrix...if scale > 1 means
												// zoom in...if scale < 1 means
												// zoom out
												matrix.postScale(scale, scale, mid.x, mid.y);
											}
										}
										break;
									}

									view.setImageMatrix(matrix); // display the transformation on screen

									return true; // indicate event was handled
								}
							});
						}

					}
				});
			}
		});
	}


	

	/*
	 * --------------------------------------------------------------------------
	 * Method: spacing Parameters: MotionEvent Returns: float Description:
	 * checks the spacing between the two fingers on touch
	 * ----------------------------------------------------
	 */

	private float spacing(MotionEvent event) 
	{
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/*
	 * --------------------------------------------------------------------------
	 * Method: midPoint Parameters: PointF object, MotionEvent Returns: void
	 * Description: calculates the midpoint between the two fingers
	 * ------------------------------------------------------------
	 */

	private void midPoint(PointF point, MotionEvent event) 
	{
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	/** Show an event in the LogCat view, for debugging */
	private void dumpEvent(MotionEvent event) 
	{
		String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE","POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
		StringBuilder sb = new StringBuilder();
		int action = event.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;
		sb.append("event ACTION_").append(names[actionCode]);

		if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP) 
		{
			sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
			sb.append(")");
		}

		sb.append("[");
		for (int i = 0; i < event.getPointerCount(); i++) 
		{
			sb.append("#").append(i);
			sb.append("(pid ").append(event.getPointerId(i));
			sb.append(")=").append((int) event.getX(i));
			sb.append(",").append((int) event.getY(i));
			if (i + 1 < event.getPointerCount())
				sb.append(";");
		}

		sb.append("]");
		Log.d("Touch Events ---------", sb.toString());
	}




	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}


	/*public class ShowImage extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_image);

		Bundle b=this.getIntent().getExtras();
		String username=b.getString("Username");

		ParseQuery query = new ParseQuery("Card");
		query.whereEqualTo("Username", username);
		query.getFirstInBackground(new GetCallback() {

			public void done(final ParseObject foundCard, ParseException arg1) {
				ParseFile showImage=(ParseFile) foundCard.get("Photo");
				showImage.getDataInBackground(new GetDataCallback() {
					public void done(byte[] data, ParseException e) {
						if (e == null) {
							//Bitmap bitmap=BitmapFactory.decodeByteArray(data, 0, data.length);
							//ImageView image=(ImageView) findViewById(R.id.display_image);
							WebView wv=(WebView)findViewById(R.id.webView1);
							String b64Image = Base64.encodeToString(data, Base64.DEFAULT); 
							wv.loadData(b64Image, "image/jpeg", "base64");
							wv.getSettings().setBuiltInZoomControls(true);
							wv.getSettings().setJavaScriptEnabled(true);
							wv.getSettings().setLoadWithOverviewMode(true);
							wv.getSettings().setUseWideViewPort(true);
							wv.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
							wv.setScrollbarFadingEnabled(true);
							wv.getSettings().setUseWideViewPort(true);
							wv.getSettings().setLoadWithOverviewMode(true);
							//image.setImageBitmap(bitmap);
						} else {
							// something went wrong
						}
					}
				});
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_image, menu);
		return true;
	}

}*/


}