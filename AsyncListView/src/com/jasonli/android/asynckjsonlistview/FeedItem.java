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
}
