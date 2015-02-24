/*
 * Main Activity.
 * Jason Li
 */

package com.jasonli.android.asynckjsonlistview;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jasonli.android.asynckjsonlistview.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.widget.ListView;

public class MainActivity extends Activity {
	private SimpleAdapter adpt;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		adpt = new SimpleAdapter(new ArrayList<FeedItem>(), this);
		ListView lView = (ListView) findViewById(R.id.listview);

		lView.setAdapter(adpt);

		// Exec async load task for the feed
		(new AsyncListViewLoader()).execute(getResources().getString(
				R.string.feed_url));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// for the refresh menu item
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// refresh
				(new AsyncListViewLoader()).execute(getResources().getString(
						R.string.feed_url));
				return true;
	}

	private class AsyncListViewLoader extends
			AsyncTask<String, Void, List<FeedItem>> {
		private final ProgressDialog dialog = new ProgressDialog(
				MainActivity.this);
		private String appTitle; // to update application title when loading
									// completed

		private class ImageLoadTask extends AsyncTask<String, String, Bitmap> {
			private FeedItem currentItem = null;
	        public Bitmap getBitmapFromURL(String src) {
	        	//to load image file from url - called within async task
	            try {
	                URL url = new URL(src);
	                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	                connection.setDoInput(true);
	                connection.connect();
	                InputStream input = connection.getInputStream();
	                Bitmap myBitmap = BitmapFactory.decodeStream(input);
	                return myBitmap;
	            } catch (IOException e) {
	                e.printStackTrace();
	                return null;
	            }
	        }
	            	 
	        public ImageLoadTask(FeedItem item)
	        {
	        	currentItem = item;
	        }
	        @Override
	        protected void onPreExecute() {
	            //Log.i("ImageLoadTask", "Loading image..." + imageUrl);
	        }
	 
	        // PARAM[0] IS IMG URL
	        protected Bitmap doInBackground(String... param) {
	            Log.i("ImageLoadTask", "Attempting to load image URL: " + param[0]);
	            try {
	                Bitmap b = getBitmapFromURL(param[0]);
	                return b;
	            } catch (Exception e) {
	                e.printStackTrace();
	                return null;
	            }
	        }
	 
	        protected void onProgressUpdate(String... progress) {
	            // TODO: add percentage indicator in the status bar
	        }
	 
	        protected void onPostExecute(Bitmap ret) {
	            if (ret != null) {
	                //Log.i("ImageLoadTask", "Successfully loaded " + imageUrl );
	            	
	                currentItem.setImage(ret);
	                if (adpt != null) {
	                    // notify the adaptor when loaded
	                	adpt.notifyDataSetChanged();
	                }
	            } else {
	                Log.e("ImageLoadTask", "Failed to load " + currentItem.getImageUrl() );
	            }
	        }
	    }    
	    
		@Override
		protected void onPostExecute(List<FeedItem> result) {
			super.onPostExecute(result);
			dialog.dismiss();
			adpt.setItemList(result);
			adpt.notifyDataSetChanged();
			MainActivity.this.setTitle(appTitle);
			//now it is time to load all images in the background
			for(FeedItem item: result)
			{
				String imgUrl = item.getImageUrl();
				if(!imgUrl.contains("null"))
				{
					ImageLoadTask imgLoadTask = new ImageLoadTask(item);
					imgLoadTask.execute(item.getImageUrl());
				}
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.setMessage(getResources().getString(R.string.downloading)); 
			dialog.show();
		}

		@Override
		protected List<FeedItem> doInBackground(String... params) {
			List<FeedItem> result = new ArrayList<FeedItem>();
			String JSONResp=null;
			try {
				URL u = new URL(params[0]);
				//params[0] is the feed url
				HttpURLConnection conn = (HttpURLConnection) u.openConnection();
				conn.setRequestMethod("GET");

				conn.connect();
				InputStream is = conn.getInputStream();

		        // Convert response to string using String Builder
		        try {
		            BufferedReader bReader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
		            StringBuilder sBuilder = new StringBuilder();

		            String line = null;
		            while ((line = bReader.readLine()) != null) {
		                sBuilder.append(line + "\n");
		            }

		            is.close();
		            JSONResp = sBuilder.toString();

		        } catch (Exception e) {
		            Log.e("StringBuilding & BufferedReader", "Error converting result " + e.toString());
		        }		
//the following implementation has issue loading url from the feed
				
				// Read the stream
/*				byte[] b = new byte[1024];
				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				while (is.read(b) != -1)
					baos.write(b);

				String JSONResp = new String(baos.toByteArray());*/
		        
		        //feed in JSONResp, top level is an object rather than array
				JSONObject topObj = new JSONObject(JSONResp);
				//get title from top object
				appTitle = topObj.getString("title");
				JSONArray arr = topObj.getJSONArray("rows");
				for (int i = 0; i < arr.length(); i++) {
					result.add(convertContact(arr.getJSONObject(i)));
				}

				return result;
			} catch (Throwable t) {
				t.printStackTrace();
			}
			return null;
		}

		private FeedItem convertContact(JSONObject obj) throws JSONException {
			String title = obj.getString("title");
			if (title.isEmpty() || title.compareToIgnoreCase("null") == 0) 
			// "null" is not to be displayed in UI
			{
				title = "";
			}
			String description = obj.getString("description");
			if (description.isEmpty() || description.compareToIgnoreCase("null") == 0)
			//"null" is not to be displayed in UI
			{
				description = "";
			}
			String imageUrl = obj.getString("imageHref");
//			Log.i("Object Creation", "Title: " + title + "\nDescription: " + description + " \nImage URL: " + imageUrl);
			FeedItem feedItem = new FeedItem(title, description, imageUrl);
			return feedItem;
		}

	}
}
