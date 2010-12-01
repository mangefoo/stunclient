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

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class ResultActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.result);
        
        String localIP = getIntent().getExtras().getString("localIP");
        String publicIP = getIntent().getExtras().getString("publicIP");
        String NATType = getIntent().getExtras().getString("NATType");
        
        TextView localView = (TextView) findViewById(R.id.local_ip);
        TextView publicView = (TextView) findViewById(R.id.public_ip);
        TextView natView = (TextView) findViewById(R.id.firewall_type);
        
        localView.setText(localIP);
        publicView.setText(publicIP);
        natView.setText(NATType);

    }
}
