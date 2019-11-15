package com.sleepy.tok.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sleepy.tok.R;

public class UniversalImageLoader {
    //First thing to do with it is set a default image.
    private static final int defaultImage = R.drawable.ic_user_grey;
    private Context mContext;

    //mandatory constructor for this class.
    public UniversalImageLoader(Context context){
        mContext = context;
    }


    public ImageLoaderConfiguration getConfig(){
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(defaultImage)
                .showImageForEmptyUri(defaultImage)
                .cacheOnDisk(true).cacheInMemory(true)
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(mContext)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCacheSize(100*1024*1024)
                .build();

        return configuration;
    }


    public static void setImage(String imgURL, String append, ImageView image, final ProgressBar mProgressBar){
        ImageLoader imageLoader = ImageLoader.getInstance();

        //imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
        imageLoader.displayImage(append + imgURL, image, new ImageLoadingListener() {


            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if(mProgressBar!=null)
                    mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if(mProgressBar!=null)
                    mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if(mProgressBar!=null)
                    mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if(mProgressBar!=null)
                    mProgressBar.setVisibility(View.GONE);
            }
        });
    }
}
