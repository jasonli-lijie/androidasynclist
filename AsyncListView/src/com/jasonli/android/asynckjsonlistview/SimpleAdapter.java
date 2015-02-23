/*
 * Very simple adaptor for listactivity.
 * Jason Li
 */

package com.jasonli.android.asynckjsonlistview;

import java.util.List;

import com.survivingwithandroid.tutorial.asynclistview.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SimpleAdapter extends ArrayAdapter<FeedItem> {

	private List<FeedItem> itemList;
	private Context context;

	public SimpleAdapter(List<FeedItem> itemList, Context ctx) {
		super(ctx, android.R.layout.simple_list_item_1, itemList);
		this.itemList = itemList;
		this.context = ctx;
	}

	public int getCount() {
		if (itemList != null)
			return itemList.size();
		return 0;
	}

	public FeedItem getItem(int position) {
		if (itemList != null)
			return itemList.get(position);
		return null;
	}

	public long getItemId(int position) {
		if (itemList != null)
			return itemList.get(position).hashCode();
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View v = convertView;
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.list_item, null);
		}

		FeedItem c = itemList.get(position);
		TextView text = (TextView) v.findViewById(R.id.title);
		text.setText(c.getTitle());

		TextView text1 = (TextView) v.findViewById(R.id.description);
		text1.setText(c.getDescription());

		ImageView imgview = (ImageView) v.findViewById(R.id.icon);
//to make sure everyone has an image
		if (c.getImageUrl() == null || c.getImage()== null) {
			imgview.setImageResource(R.drawable.ic_launcher);
		} else {
			imgview.setImageBitmap(c.getImage());
		}
		return v;

	}

	public List<FeedItem> getItemList() {
		return itemList;
	}

	public void setItemList(List<FeedItem> itemList) {
		this.itemList = itemList;
	}

}
