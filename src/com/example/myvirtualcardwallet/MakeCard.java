package com.example.myvirtualcardwallet;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseObject;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
public class MakeCard extends Activity {
	public Card myCard =new Card();
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
		ParseObject makecard = new ParseObject("Card");
		makecard.put("Name", myCard.name);
		makecard.put("Designation", myCard.designation);
		makecard.put("Address", myCard.address);
		makecard.put("Company", myCard.company);
		makecard.put("Email", myCard.email);
		makecard.put("Telephone", myCard.telephone);
		makecard.put("Username", myCard.username);
		//makecard.put("Mycard", myCard);
		makecard.saveInBackground();
		//gameScore.put("playerName", "Sean Plott");
		//gameScore.put("cheatMode", false);
		//gameScore.saveInBackground();
	
		Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_LONG).show();
		Intent home = new Intent(getApplicationContext(), Options.class);
        home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(home);
	}

}
