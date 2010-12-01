/*    
    This file is part of the STUN Client.
    
    Copyright (C) 2010  Magnus Eriksson <eriksson.mag@gmail.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.kodholken.stunclient;

import java.net.InetAddress;
import java.net.UnknownHostException;

import de.javawi.jstun.test.DiscoveryInfo;
import de.javawi.jstun.test.DiscoveryTest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity implements Logger.Observer {
	private Button goButton;
	private Spinner hostSpinner;
	private Handler handler;
	private LinearLayout resultLayout;
	private long timeReference;
	private ProgressDialog progressDialog;
	private DiscoveryInfo di;
	
	protected static final int OPTION_MENU_ABOUT = 0;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        LoggerFactory.setObserver(this);

        final Context mainContext = this;
        handler = new Handler() {
        	@Override
        	public void handleMessage(android.os.Message msg) {
        		if (msg.getData().getBoolean("done")) {
        			if (msg.getData().getString("error") != null) {
        				AlertDialog alertDialog = 
        					     new AlertDialog.Builder(mainContext).create();
        				alertDialog.setTitle("Discovery failed");
        				alertDialog.setMessage(msg.getData().getString("error"));
        				alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL,
        						              "Close",
        						        new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, 
									int which) {
							}
						});
        				alertDialog.show();
        				return ;
        			}
        			
        			Intent i = new Intent(mainContext, ResultActivity.class);
        			i.putExtra("localIP", di.getLocalIP().getHostAddress());
        			i.putExtra("publicIP", di.getPublicIP().getHostAddress());
        			
        			String NATType = "Unknown";
        			if (di.isOpenAccess()) NATType = "No NAT";
        			if (di.isBlockedUDP()) NATType = "Blocked UDP";
        			if (di.isFullCone()) NATType = "Full Cone NAT";
        			if (di.isRestrictedCone()) NATType = "Restricted Cone NAT";
        			if (di.isPortRestrictedCone()) NATType = "Port restricted Cone NAT";
        			if (di.isSymmetric()) NATType = "Symmetric Cone NAT";
        			if (di.isSymmetricUDPFirewall()) NATType = "Symmetric UDP";
        			i.putExtra("NATType", NATType);

        			startActivity(i);
        			return ;
        		}
        		
        		String message = msg.getData().getString("message");
        		
        		if (message == null) {
        			return;
        		}
        		
        		long timeDelta = System.currentTimeMillis() - timeReference;
        		
				TextView msgView = new TextView(mainContext);
				msgView.setTextColor(Color.BLACK);
				msgView.setText("[" + timeDelta / 1000.0 + "] " + message);
				
				LinearLayout res = (LinearLayout) findViewById(R.id.result_layout);

				if ((res.getChildCount() % 2) != 0) {
					msgView.setBackgroundColor(0xddaaaaaa);
				} else {
					msgView.setBackgroundColor(0xddcccccc);
				}
				
				res.addView(msgView);
				
				final ScrollView sv = (ScrollView) findViewById(R.id.scroll_view);
				
				sv.post(new Runnable() {
				    public void run() {
				        sv.fullScroll(ScrollView.FOCUS_DOWN);
				    } 
				}); 
        	}
        };
        
        resultLayout = (LinearLayout) findViewById(R.id.result_layout);
        
        hostSpinner = (Spinner) findViewById(R.id.stun_servers);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.hosts, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hostSpinner.setAdapter(adapter);
        
        final Context mainActivityContext = this;
        goButton = (Button) findViewById(R.id.go_button);
        goButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				resultLayout.removeAllViewsInLayout();
				progressDialog = ProgressDialog.show(mainActivityContext, "",
						              "Performing STUN discovery\u2026", true);
				new Thread(new Runnable() {
					@Override
					public void run() {
						doDiscovery();
					};
				}).start();
			}
        });
    }
    
    private void doDiscovery() {
    	String error = null;

    	String host = (String) hostSpinner.getSelectedItem();
		try {
			DiscoveryTest dt = new DiscoveryTest(InetAddress
					.getByName("0.0.0.0"), host,
					getHostPort(host));
			timeReference = System.currentTimeMillis();
			di = dt.test();
			System.out.println("DI: " + di);

			Message msg = new Message();
			Bundle bundle = new Bundle();
			bundle.putString("message", di.toString());
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (UnknownHostException ex) {
			error = "Could not resolve STUN server. Make sure that a network connection is available.";
		} catch (Exception ex) {
			ex.printStackTrace();
			error = "Discovery error: " + ex.getMessage();
		}
		
		progressDialog.dismiss();

		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putBoolean("done", true);
		if (error != null) {
			bundle.putString("error", error);
		}
		msg.setData(bundle);
		handler.sendMessage(msg);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuItem item = menu.add(Menu.NONE, OPTION_MENU_ABOUT, Menu.NONE, R.string.options_about);
		item.setIcon(android.R.drawable.ic_menu_info_details);

		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;

		switch (item.getItemId()) {
		case OPTION_MENU_ABOUT:
			i = new Intent(this, AboutActivity.class);
			startActivity(i);
			break;
		}
		
		return true;
	}
    
    private int getHostPort(String host) {
    	if (host.equals("stun.sipgate.net")) {
    		return 10000;
    	}
    	
    	return 3478;
    }

	@Override
	public void onLogEntry(String message) {
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("message", message);
		msg.setData(bundle);
		handler.sendMessage(msg);
	}
}