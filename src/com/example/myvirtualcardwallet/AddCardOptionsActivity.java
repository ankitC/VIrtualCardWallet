package com.example.myvirtualcardwallet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AddCardOptionsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_card_options);
		final Bundle b =this.getIntent().getExtras();

		Button bluetoothButton= (Button) findViewById(R.id.addBluetooth);
		bluetoothButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent addViaBluetooth=new Intent(getApplicationContext(), AddViaBluetooth.class);
				addViaBluetooth.putExtras(b);
				startActivity(addViaBluetooth);
			}
		});



		Button webButton=(Button) findViewById(R.id.addWeb);
		webButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent addViaWeb=new Intent(getApplicationContext(), AddCard.class);
				addViaWeb.putExtras(b);
				startActivity(addViaWeb);

			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_card_options, menu);
		return true;
	}

}
