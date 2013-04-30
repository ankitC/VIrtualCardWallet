package com.example.myvirtualcardwallet;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
public class MakeCard extends Activity {
	public Card myCard =new Card();
	private Bitmap bitmap;
	private ImageView imageView;
	private String selectedImagePath;
	private final int CAMERA_PICTURE = 1;
	private final int GALLERY_PICTURE = 2;
	private Intent pictureActionIntent = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_make_card);		
		myCard.username=getIntent().getExtras().getString("Username");
		myCard.name=getIntent().getExtras().getString("Name");
		EditText name= (EditText)findViewById(R.id.mc_editname);
		name.setText(myCard.name);
		Parse.initialize(this, "HIWiddAFOEElxePoa7qHp72CHvwtNuqOXf1bXkjf", "aJpMrnM1WfUOpLGGSBUSl4pLPh4vVSdQrgL4VBi3"); 
		ParseAnalytics.trackAppOpened(getIntent());
		imageView = (ImageView) findViewById(R.id.add_photo_image);
		bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.photo_not_available_large);
		imageView.setImageBitmap(bitmap);
		imageView.bringToFront();		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.make_card, menu);
		return true;
	}

	public void onSubmitListener(View view)
	{
		//EditText name = (EditText)findViewById(R.id.editName);
		EditText address = (EditText)findViewById(R.id.editAddress);
		EditText designation = (EditText)findViewById(R.id.editDesignation);
		EditText company = (EditText)findViewById(R.id.editCompany);
		EditText email = (EditText)findViewById(R.id.editEmail);
		EditText number = (EditText)findViewById(R.id.editNumber);
		//myCard= new Card();
		myCard.address=address.getText().toString();
		myCard.designation=designation.getText().toString();
		myCard.company=company.getText().toString();
		myCard.email=email.getText().toString();
		myCard.telephone=number.getText().toString();
		ByteArrayOutputStream photoStream=new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, photoStream);
		byte[] photoArray=photoStream.toByteArray();

		ParseFile photoFile = new ParseFile("photo.jpeg", photoArray);
		try {
			photoFile.save();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		ParseObject makecard = new ParseObject("Card");
		makecard.put("Name", myCard.name);
		makecard.put("Designation", myCard.designation);
		makecard.put("Address", myCard.address);
		makecard.put("Company", myCard.company);
		makecard.put("Email", myCard.email);
		makecard.put("Telephone", myCard.telephone);
		makecard.put("Username", myCard.username);
		makecard.put("Photo", photoFile);
		
		makecard.saveInBackground();
		

		Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_LONG).show();
		Intent home = new Intent(getApplicationContext(), Options.class);
		home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Bundle b=new Bundle();
		b=this.getIntent().getExtras();
		home.putExtras(b);
		startActivity(home);
	}

	public void onAddPhotoListener(View view) {
		startDialog();
	}

	public String getPath(Uri contentUri) {
		String res = null;
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
		if(cursor.moveToFirst()){;
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		res = cursor.getString(column_index);
		}
		cursor.close();
		return res;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == GALLERY_PICTURE) {
				Uri selectedImageUri = data.getData();

				if (selectedImageUri != null) {
					//MEDIA GALLERY
					selectedImagePath = getPath(selectedImageUri);
					imageView = (ImageView) findViewById(R.id.add_photo_image);
					bitmap = BitmapFactory.decodeFile(selectedImagePath);
					imageView.setImageBitmap(bitmap);

					//HERE IS WHAT WE WANT
					Log.d("filepath", selectedImagePath);
					Log.d("done", "kaam ho gaya... path bhi mil gaya aur photo bhi aa gaya! :D");
				}
				else {
					Toast toast = Toast.makeText(this, "No Image is selected.", Toast.LENGTH_LONG);
					toast.show();
				}
			}

			else if (requestCode == CAMERA_PICTURE) {
				if (data.getExtras() != null) {
					// here is the image from camera
					bitmap = (Bitmap) data.getExtras().get("data");
					imageView.setImageBitmap(bitmap);
				}
			}
		}
	}

	private void startDialog() {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
		myAlertDialog.setTitle("Upload Pictures Option");
		myAlertDialog.setMessage("How do you want to select your picture?");

		myAlertDialog.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				pictureActionIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
				pictureActionIntent.setType("image/*");
				pictureActionIntent.putExtra("return-data", true);
				startActivityForResult(pictureActionIntent, GALLERY_PICTURE);
			}
		});

		myAlertDialog.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				pictureActionIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(pictureActionIntent, CAMERA_PICTURE);
			}
		});
		myAlertDialog.show();
	}




}
