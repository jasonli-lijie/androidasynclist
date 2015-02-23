/*
 * Class to represent the feed item.
 * Jason Li
 */

package com.jasonli.android.asynckjsonlistview;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class FeedItem implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String title;
	private String description;
	private String imageUrl;
	private Bitmap image=null;
	private SimpleAdapter adpt;
	
	public FeedItem(String title, String description, String imageUrl) {
		super();
		this.title = title;
		this.description = description;
		this.imageUrl = imageUrl;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Bitmap getImage() {
        return image;
    }
	public void setImage(Bitmap img) {
        this.image = img;
    }
	public SimpleAdapter getAdapter() {
	    return adpt;
	}
	 
	public void setAdapter(SimpleAdapter adpt) {
	        this.adpt = adpt;
	}
	
    public void loadImage(SimpleAdapter adpt) {
        // HOLD A REFERENCE TO THE ADAPTER
        this.adpt = adpt;
        if (imageUrl.compareToIgnoreCase("null") !=0) {
            new ImageLoadTask().execute(imageUrl);
        }
    }
 
	private class ImageLoadTask extends AsyncTask<String, String, Bitmap> {
        public Bitmap getBitmapFromURL(String src) {
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
            	 
        @Override
        protected void onPreExecute() {
            Log.i("ImageLoadTask", "Loading image..." + imageUrl);
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
            // NO OP
        }
 
        protected void onPostExecute(Bitmap ret) {
            if (ret != null) {
                Log.i("ImageLoadTask", "Successfully loaded " + imageUrl );
                image = ret;
                if (adpt != null) {
                    // WHEN IMAGE IS LOADED NOTIFY THE ADAPTER
                	adpt.notifyDataSetChanged();
                }
            } else {
                Log.e("ImageLoadTask", "Failed to load " + imageUrl );
            }
        }
    }    
    

}
