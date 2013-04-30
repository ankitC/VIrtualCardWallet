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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class Edit_card extends Activity {

	public ParseObject localCard;
	private Bitmap bitmap;
	private ImageView imageView;
	private String newImagePath;
	ParseFile photoFile;
	private final int CAMERA_PICTURE = 1;
	private final int GALLERY_PICTURE = 2;
	private Intent pictureActionIntent = null;
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_card);
		Parse.initialize(this, "HIWiddAFOEElxePoa7qHp72CHvwtNuqOXf1bXkjf", "aJpMrnM1WfUOpLGGSBUSl4pLPh4vVSdQrgL4VBi3"); 
		ParseAnalytics.trackAppOpened(getIntent());

		Bundle b=this.getIntent().getExtras();
		String username=b.getString("Username").toString();
		imageView=(ImageView)findViewById(R.id.edit_card_image);



		final ParseQuery query = new ParseQuery("Card");
		query.whereEqualTo("Username", username);
		query.getFirstInBackground(new GetCallback(){

			public void done(ParseObject foundCard, ParseException arg1) {
				localCard=foundCard;

				EditText name= (EditText)findViewById(R.id.editcard_name);
				name.setText(foundCard.getString("Name").toString());

				EditText address= (EditText)findViewById(R.id.editcard_address);
				address.setText(foundCard.getString("Address").toString());

				EditText company= (EditText)findViewById(R.id.editcard_company);
				company.setText(foundCard.getString("Company"));

				EditText designation= (EditText)findViewById(R.id.editcard_designation);
				designation.setText(foundCard.getString("Designation"));

				EditText email= (EditText)findViewById(R.id.editcard_email);
				email.setText(foundCard.getString("Email"));

				EditText telephone= (EditText)findViewById(R.id.editcard_telephone);
				telephone.setText(foundCard.getString("Telephone").toString());


				photoFile= (ParseFile)foundCard.get("Photo");
				photoFile.getDataInBackground(new GetDataCallback() {
					public void done(byte[] data, ParseException e) {
						if (e == null) {
							bitmap=BitmapFactory.decodeByteArray(data, 0, data.length);
							ImageView image=(ImageView) findViewById(R.id.edit_photo_image);
							image.setImageBitmap(bitmap);
						} else {
							// something went wrong
						}
					}
				});

			}
		});

		Button edit_accept= (Button) findViewById(R.id.editcard_save);

		edit_accept.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				final EditText name= (EditText)findViewById(R.id.editcard_name);
				final EditText address= (EditText)findViewById(R.id.editcard_address);
				final EditText company= (EditText)findViewById(R.id.editcard_company);
				final EditText designation= (EditText)findViewById(R.id.editcard_designation);
				final EditText email= (EditText)findViewById(R.id.editcard_email);
				final EditText telephone= (EditText)findViewById(R.id.editcard_telephone);
				
				ByteArrayOutputStream photoStream=new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, photoStream);
				byte[] photoArray=photoStream.toByteArray();

				photoFile = new ParseFile("photo.jpeg", photoArray);
				try {
					photoFile.save();
				} catch (ParseException e) {
					e.printStackTrace();
				}

				final ParseObject updatedCard = localCard; 

				updatedCard.saveInBackground(new SaveCallback() {
					public void done(ParseException e) {
						updatedCard.put("Name",name.getText().toString() );
						updatedCard.put("Address",address.getText().toString());
						updatedCard.put("Company", company.getText().toString());
						updatedCard.put("Designation", designation.getText().toString());
						updatedCard.put("Email",email.getText().toString() );
						updatedCard.put("Telephone",telephone.getText().toString() );
						updatedCard.put("Photo", photoFile);
						updatedCard.saveInBackground();

						Toast.makeText(getApplicationContext(),"Update Successful.",Toast.LENGTH_SHORT).show();

						/*Intent options=new Intent(getApplicationContext(), Options.class);
						Bundle b = new Bundle();
						b.putString("Username", localCard.getString("Username").toString());
						options.putExtras(b);
						startActivity(options);*/

						finish();
					}
				});

			}
		});


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_card, menu);
		return true;
	}

	public void onEditPhotoListener(View view) {
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
					newImagePath = getPath(selectedImageUri);
					imageView = (ImageView) findViewById(R.id.edit_photo_image);
					bitmap = BitmapFactory.decodeFile(newImagePath);
					imageView.setImageBitmap(bitmap);

					//HERE IS WHAT WE WANT
				//	Log.d("filepath", newImagePath);
				//	Log.d("done", "kaam ho gaya... path bhi mil gaya aur photo bhi aa gaya! :D");
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
					imageView=(ImageView) findViewById(R.id.edit_photo_image);
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
