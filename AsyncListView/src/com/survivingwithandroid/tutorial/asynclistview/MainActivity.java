/*
 * Copyright (C) 2013 Surviving with Android (http://www.survivingwithandroid.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.survivingwithandroid.tutorial.asynclistview;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;

import android.widget.ListView;

public class MainActivity extends Activity {
	private SimpleAdapter adpt;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        adpt  = new SimpleAdapter(new ArrayList<FeedItem>(), this);
        ListView lView = (ListView) findViewById(R.id.listview);
        
        lView.setAdapter(adpt);
        
        // Exec async load task
        //(new AsyncListViewLoader()).execute("http://10.0.2.2:8080/JSONServer/rest/ContactWS/get");
        (new AsyncListViewLoader()).execute("https://dl.dropboxusercontent.com/u/746330/facts.json");
    }


    private class AsyncListViewLoader extends AsyncTask<String, Void, List<FeedItem>> {
    	private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
    	
		@Override
		protected void onPostExecute(List<FeedItem> result) {			
			super.onPostExecute(result);
			dialog.dismiss();
			adpt.setItemList(result);
			adpt.notifyDataSetChanged();
		}

		@Override
		protected void onPreExecute() {		
			super.onPreExecute();
			dialog.setMessage("Downloading feed...");
			dialog.show();			
		}

		@Override
		protected List<FeedItem> doInBackground(String... params) {
			List<FeedItem> result = new ArrayList<FeedItem>();
			
			try {
				URL u = new URL(params[0]);
				
				HttpURLConnection conn = (HttpURLConnection) u.openConnection();
				conn.setRequestMethod("GET");
				
				conn.connect();
				InputStream is = conn.getInputStream();
				
				// Read the stream
				byte[] b = new byte[4096];
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				
				while ( is.read(b) != -1)
					baos.write(b);
				
				String JSONResp = new String(baos.toByteArray());
				JSONObject topObj = new JSONObject(JSONResp);
				String title = topObj.getString("title");
				
				JSONArray arr = topObj.getJSONArray("rows");
				for (int i=0; i < arr.length(); i++) {
					result.add(convertContact(arr.getJSONObject(i)));
				}
				
				return result;
			}
			catch(Throwable t) {
				t.printStackTrace();
			}
			return null;
		}
		
		
		private FeedItem convertContact(JSONObject obj) throws JSONException {
			String name = obj.getString("title");
			String surname = obj.getString("description");
			String email = obj.getString("imageHref");
			//String phoneNum = obj.getString("phoneNum");
			String phoneNum = " ";
			
			//return new Contact(name, surname, email, phoneNum);
			return new FeedItem("name", "surname", "email", "phoneNum");
		}
    	
    }
    
    
    
}
