package com.example.myvirtualcardwallet;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class ListCardsActivity extends Activity {

	public LinkedHashMap<String, String> card;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_cards);

		final ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
				R.layout.activity_list_cards, R.id.textView1);
		final Bundle b = this.getIntent().getExtras();
		
		ListView listView = (ListView) findViewById(R.id.cardList);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				String selectedCard = listAdapter.getItem(position).toString();
				String selectedUsername = card.get(selectedCard);
				Intent detailViewIntent = new Intent(getApplicationContext(),
						DetailView.class);
				
				Bundle toDetail = new Bundle();
				toDetail.putString("selectedCardUsername", selectedUsername);
				toDetail.putString("MyUsername", b.getString("Username").toString());
				detailViewIntent.putExtras(toDetail);
				startActivity(detailViewIntent);

			}
		});
		listView.setAdapter(listAdapter);
		
		

		ParseQuery query = new ParseQuery("Memberships");
		query.whereEqualTo("Owner", b.getString("Username"));

		query.findInBackground(new FindCallback() {
			public void done(List<ParseObject> cardList, ParseException e) {

				if (e == null) {
					if (cardList.size() == 0) {
						Toast.makeText(getApplicationContext(),
								"No cards in your wallet yet.",
								Toast.LENGTH_SHORT).show();
						finish();
					}
					card = new LinkedHashMap<String, String>();
					for (int i = 0; i < cardList.size(); i++) {
						ParseQuery cardQ = new ParseQuery("Card");
						cardQ.whereEqualTo("Username",
								cardList.get(i).get("Contact").toString());
						cardQ.getFirstInBackground(new GetCallback() {

							@Override
							public void done(ParseObject cardElement,
									ParseException e) {
								String display = cardElement.getString("Name")
										+ "\n"
										+ cardElement.getString("Designation")
										+ ","
										+ cardElement.getString("Company");
								String uname = cardElement
										.getString("Username");
								card.put(display.toString(), uname.toString());
								listAdapter.add(display);
							}
						});

					}

				} else {
					Toast.makeText(getApplicationContext(),
							"No Cards in your wallet yet!", Toast.LENGTH_SHORT)
							.show();
					finish();
				}
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_cards, menu);
		return true;
	}
}
