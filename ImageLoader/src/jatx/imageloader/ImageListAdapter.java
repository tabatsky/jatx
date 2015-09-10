package jatx.imageloader;

import jatx.imageloader.lib.ImageLoader;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageListAdapter extends BaseAdapter {
	private List<String> mUrlList;
	
	public ImageListAdapter(List<String> urlList) {
		mUrlList = urlList;
	}
	
	@Override
	public int getCount() {
		return mUrlList.size();
	}

	@Override
	public String getItem(int position) {
		return mUrlList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolderItem viewHolder;
		
		if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.entry_image_list, null);
            
            viewHolder = new ViewHolderItem();
            
            viewHolder.imgView = (ImageView) convertView.findViewById(R.id.img_view);
            
            convertView.setTag(viewHolder);
        } else {
        	viewHolder = (ViewHolderItem) convertView.getTag();
        }
		
		ViewGroup.LayoutParams lp = viewHolder.imgView.getLayoutParams();
		lp.width = ImageLoader.getScreenWidth();
		lp.height = (ImageLoader.getScreenWidth() * 3) / 4;
		viewHolder.imgView.setLayoutParams(lp);
		viewHolder.imgView.setVisibility(View.INVISIBLE);
		
		ImageLoader.load(getItem(position), viewHolder.imgView);
		
		return convertView;
	}

	private static class ViewHolderItem {
		public ImageView imgView;
	}
}
