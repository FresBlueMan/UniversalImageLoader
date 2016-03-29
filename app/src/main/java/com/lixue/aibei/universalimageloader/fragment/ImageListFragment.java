package com.lixue.aibei.universalimageloader.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lixue.aibei.universalimageloader.Constants;
import com.lixue.aibei.universalimageloader.R;
import com.lixue.aibei.universalimageloaderlib.core.DisplayImageOptions;
import com.lixue.aibei.universalimageloaderlib.core.UniversalImageLoader;
import com.lixue.aibei.universalimageloaderlib.core.display.CircleBitmapDisplayer;
import com.lixue.aibei.universalimageloaderlib.core.display.FadeInBitmapDisplayer;
import com.lixue.aibei.universalimageloaderlib.core.listener.ImageLoadingListener;
import com.lixue.aibei.universalimageloaderlib.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2016/3/29.
 */
public class ImageListFragment extends AbsListViewBaseFragment {
    public static final int INDEX = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fr_image_list, container, false);
        listView = (ListView) rootView.findViewById(android.R.id.list);
        ((ListView) listView).setAdapter(new ImageAdapter(getActivity()));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startImagePagerActivity(position);
            }
        });
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AnimateFirstDisplayListener.displayedImages.clear();
    }

    private static class ImageAdapter extends BaseAdapter {

        private static final String[] IMAGE_URLS = Constants.IMAGES;

        private LayoutInflater inflater;
        private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

        private DisplayImageOptions options;

        ImageAdapter(Context context) {
            inflater = LayoutInflater.from(context);

            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.ic_stub)
                    .showImageForEmptyUri(R.drawable.ic_empty)
                    .showImageOnFail(R.drawable.ic_error)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .consinderExifParams(true)
                    .displayer(new CircleBitmapDisplayer(Color.WHITE, 5))
                    .build();
        }

        @Override
        public int getCount() {
            return IMAGE_URLS.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            final ViewHolder holder;
            if (convertView == null) {
                view = inflater.inflate(R.layout.item_list_image, parent, false);
                holder = new ViewHolder();
                holder.text = (TextView) view.findViewById(R.id.text);
                holder.image = (ImageView) view.findViewById(R.id.image);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.text.setText("Item " + (position + 1));

            UniversalImageLoader.getInstance().displayImage(IMAGE_URLS[position], holder.image, options, animateFirstListener);

            return view;
        }
    }

    static class ViewHolder {
        TextView text;
        ImageView image;
    }

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingCompleted(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}
