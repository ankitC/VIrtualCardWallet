package com.example.myvirtualcardwallet;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DetailView extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_view);

		final Card selectedCard = new Card();

		selectedCard.name = "Ankit Chheda";
		selectedCard.designation = "Graduate Student";
		selectedCard.company = "Carnegie Mellon University";
		selectedCard.telephone = "4126084159";
		selectedCard.email = "achheda@andrew.cmu.edu";
		selectedCard.address = "5733, Phillips Avenue, Pittsburgh, PA-15213";

		TextView name = (TextView) findViewById(R.id.makecard_name);
		TextView designation = (TextView) findViewById(R.id.textCompany);
		TextView company = (TextView) findViewById(R.id.textNumber);
		TextView telephone = (TextView) findViewById(R.id.textEmail);
		TextView email = (TextView) findViewById(R.id.textAddress);
		TextView address = (TextView) findViewById(R.id.textView6);

		name.setText(selectedCard.name);
		designation.setText(selectedCard.designation);
		company.setText(selectedCard.company);
		telephone.setText(selectedCard.telephone);
		email.setText(selectedCard.email);
		address.setText(selectedCard.address);

		Button sendemail = (Button) findViewById(R.id.email);

		sendemail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String to = selectedCard.email;
				Intent email = new Intent(Intent.ACTION_SEND);
				email.putExtra(Intent.EXTRA_EMAIL, new String[] { to });
				email.setType("message/rfc822");
				startActivity(Intent.createChooser(email,
						"Choose an Email client :"));
			}
		});

		Button message = (Button) findViewById(R.id.message);

		message.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
				smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
				smsIntent.setType("vnd.android-dir/mms-sms");
				smsIntent.setData(Uri.parse("sms:" + selectedCard.telephone));
				startActivity(smsIntent);
			}
		});

		Button telcall = (Button) findViewById(R.id.call);

		telcall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:" + selectedCard.telephone));
				startActivity(callIntent);

			}
		});

		Button maps = (Button) findViewById(R.id.mapView);

		maps.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getNavigation(selectedCard.address);
			}
		});
		
		Button linkedIn= (Button) findViewById(R.id.social);
		
		linkedIn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				launchLI(selectedCard);
				
			}
		});
	}

	public void getNavigation(String address) {

		String uri = "http://maps.google.com/maps?f=d&hl=en&daddr=" + address;
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
				Uri.parse(uri));
		intent.setClassName("com.google.android.apps.maps",
				"com.google.android.maps.MapsActivity");
		startActivity(intent);

	}
	
	public void launchLI(Card card){
		
		String uri = "http://www.linkedin.com/search/fpsearch?type=people&keywords=Khushal+Shah&pplSearchOrigin=GLHD&pageKey=member-home";
		Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.linkedin.android");
		Bundle b=new Bundle();
		Intent trial=new Intent("com.linkedin.android", Uri.parse(uri));
		
		startActivity(trial);
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detail_view, menu);
		return true;
	}

}