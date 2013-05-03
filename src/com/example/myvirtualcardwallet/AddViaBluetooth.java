package com.example.myvirtualcardwallet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddViaBluetooth extends Activity {
	public String username;

	// Unique UUID for this application (generated from the web)
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	//Friendly name to match while discovering
	private static final String SEARCH_NAME = "bluetooth.recipe";

	BluetoothAdapter mBtAdapter;
	BluetoothSocket mBtSocket;
	Button listenButton, scanButton;
	boolean sender, receiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("Activity");
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_add_via_bluetooth);
		Parse.initialize(this, "HIWiddAFOEElxePoa7qHp72CHvwtNuqOXf1bXkjf",
				"aJpMrnM1WfUOpLGGSBUSl4pLPh4vVSdQrgL4VBi3");
		ParseAnalytics.trackAppOpened(getIntent());
		final Bundle b=this.getIntent().getExtras();
		sender=false;
		receiver=false;
		username=b.getString("Username");

		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		if(mBtAdapter == null) {
			Toast.makeText(this, "Bluetooth is not supported.", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		if (!mBtAdapter.isEnabled()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE);
		}

		listenButton = (Button)findViewById(R.id.listenfor_card);
		listenButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//Make sure the device is discoverable first
				if (mBtAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
					Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
					discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 60);
					startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE);
					return;
				}
				{	
					receiver=true;
					startListening();
				}
			}
		});
		scanButton = (Button)findViewById(R.id.send_card_bluetooth);
		scanButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sender=true;
				mBtAdapter.startDiscovery();
				setProgressBarIndeterminateVisibility(true);

			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		//Register the activity for broadcast intents
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, filter);
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(mReceiver, filter);
	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(mReceiver);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			if(mBtSocket != null) {
				mBtSocket.close();
			}
			if(mBtAdapter.enable())
				mBtAdapter.disable();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static final int REQUEST_ENABLE = 1;
	private static final int REQUEST_DISCOVERABLE = 2;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
		case REQUEST_ENABLE:
			if(resultCode != Activity.RESULT_OK) {
				Toast.makeText(this, "Bluetooth Not Enabled.", Toast.LENGTH_SHORT).show();
				finish();
			}
			break;
		case REQUEST_DISCOVERABLE:
			if(resultCode == Activity.RESULT_CANCELED) {
				Toast.makeText(this, "Cannot listen unless we are discoverable.", Toast.LENGTH_SHORT).show();
			} else {
				startListening();
			}
			break;
		default:
			break;
		}
	}

	private void startListening() {
		AcceptTask task = new AcceptTask();
		receiver=true;
		task.execute(MY_UUID);
		setProgressBarIndeterminateVisibility(true);
	}

	//AsyncTask to accept incoming connections
	private class AcceptTask extends AsyncTask<UUID,Void,BluetoothSocket> {

		@Override
		protected BluetoothSocket doInBackground(UUID... params) {
			String name = mBtAdapter.getName();
			try {
				//While listening, set the discovery name to a specific value
				mBtAdapter.setName(SEARCH_NAME);
				BluetoothServerSocket socket = mBtAdapter.listenUsingRfcommWithServiceRecord("BluetoothRecipe", params[0]);
				BluetoothSocket connected = socket.accept();
				//Reset the BT adapter name
				mBtAdapter.setName(name);
				return connected;
			} catch (IOException e) {
				e.printStackTrace();
				mBtAdapter.setName(name);
				return null;
			}
		}

		@Override
		protected void onPostExecute(BluetoothSocket socket) {
			if(socket == null) {
				return;
			}
			mBtSocket = socket;
			ConnectedTask task = new ConnectedTask();
			task.execute(mBtSocket);
		}

	}

	public void onBackPressed()
	{
		finish();
	}

	//AsyncTask to receive a single line of data and post
	private class ConnectedTask extends AsyncTask<BluetoothSocket,Void,String> {

		@Override
		protected String doInBackground(BluetoothSocket... params) {
			InputStream in = null;
			OutputStream out = null;
			try {
				//Send your data
				out = params[0].getOutputStream();
				out.write(username.getBytes());
				//Receive the other's data
				in = params[0].getInputStream();
				byte[] buffer = new byte[1024];
				in.read(buffer);
				//Create a clean string from results
				String result = new String(buffer);
				//Close the connection
				mBtSocket.close();
				return result.trim();
			} catch (Exception exc) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(final String result) {

			
			if(receiver){
				Toast.makeText(
						getApplicationContext(),
						"In post execute.",
						Toast.LENGTH_SHORT).show();
				ParseQuery dup = new ParseQuery("Memberships");
				dup.whereEqualTo("Owner", username);
				dup.whereEqualTo("Contact", result);

				dup.getFirstInBackground(new GetCallback() {

					public void done(ParseObject founddup,
							ParseException arg1) {
						if (founddup == null) {
							ParseObject membership = new ParseObject(
									"Memberships");
							membership.put("Owner",
									username);
							membership.put("Contact", result);
							membership.saveInBackground();
							Toast.makeText(getApplicationContext(),
									"Card Added",
									Toast.LENGTH_SHORT).show();

						} else {
							Toast.makeText(
									getApplicationContext(),
									"You already have the card for this user.",
									Toast.LENGTH_SHORT).show();

						}
					}
				});
			}
			finish();
			//Toast.makeText(AddViaBluetooth.this, result, Toast.LENGTH_SHORT).show();
			setProgressBarIndeterminateVisibility(false);
		}

	}


	// The BroadcastReceiver that listens for discovered devices
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if(TextUtils.equals(device.getName(), SEARCH_NAME)) {
					//Matching device found, connect
					mBtAdapter.cancelDiscovery();
					try {
						mBtSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
						mBtSocket.connect();
						ConnectedTask task = new ConnectedTask();
						task.execute(mBtSocket);
					} catch (IOException e) {
						e.printStackTrace();
						Toast.makeText(AddViaBluetooth.this, "Error connecting to remote", Toast.LENGTH_SHORT).show();
					}
				}
				//When discovery is complete
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				setProgressBarIndeterminateVisibility(false);
			}

		}
	};
}
