package com.example.myvirtualcardwallet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class Options extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);
		
		final Bundle bunName=this.getIntent().getExtras();
		
		//Button viewallcards=(Button) findViewById(R.id.button2);
		
	//	viewallcards.setOnClickListener(new OnClickListener() {
	//			public void onClick(View v) {
		//		Intent listCardsIntent=new Intent(getApplicationContext(), DetailView.class);
		//		listCardsIntent.putExtras(bunName);
		//		startActivity(listCardsIntent);
		//	}
		//});
		
		
	//	Button addCard= (Button) findViewById(R.id.add_card_button);
		
//		addCard.setOnClickListener(new OnClickListener() {
			
	//		@Override
//			public void onClick(View v) {
		//		Intent addCardIntent=new Intent(getApplicationContext(), AddCard.class);
		//		addCardIntent.putExtras(bunName);
		//		startActivity(addCardIntent);
	//		}
	//	});
		
		ImageButton viewAllCardImage= (ImageButton) findViewById(R.id.view_all_card_image);
		
		viewAllCardImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent listCardIntent=new Intent(getApplicationContext(), ListCardsActivity.class);
				listCardIntent.putExtras(bunName);
				startActivity(listCardIntent);
			}
		});
		

ImageButton editCardImage= (ImageButton) findViewById(R.id.edit_card_image);
		
		editCardImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent listCardIntent=new Intent(getApplicationContext(), Edit_card.class);
				listCardIntent.putExtras(bunName);
				startActivity(listCardIntent);
			}
		});
		
		//Button editCard=(Button) findViewById(R.id.edit_card_button);
		
		//editCard.setOnClickListener(new OnClickListener() {
			
			//@Override
			//public void onClick(View v) {
			//	Intent editCardIntent=new Intent(getApplicationContext(), Edit_card.class);
			//	editCardIntent.putExtras(bunName);
			//	startActivity(editCardIntent);
			//}
		//});
		
		
	
	ImageButton addCardImage= (ImageButton) findViewById(R.id.add_image);
	
	addCardImage.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent addCardIntent=new Intent(getApplicationContext(), AddCardOptionsActivity.class);
			addCardIntent.putExtras(bunName);
			startActivity(addCardIntent);
		}
	});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.options, menu);
		return true;
	}
	
	

}
