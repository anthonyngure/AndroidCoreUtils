/*
 * Copyright (c) 2018.
 *
 * Anthony Ngure
 *
 * Email : anthonyngure25@gmail.com
 */

package ke.co.toshngure.views.media;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;


import java.io.File;
import java.lang.ref.WeakReference;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import ke.co.toshngure.basecode.app.GlideRequests;
import ke.co.toshngure.views.CircleImageView;
import ke.co.toshngure.views.R;


/**
 * Created by Anthony Ngure on 20/02/2017.
 * Email : anthonyngure25@gmail.com.
 */

public class NetworkImage extends FrameLayout {

    private static final String TAG = NetworkImage.class.getSimpleName();

    protected ImageView mImageView;
    protected ImageView mBackgroundImageView;
    protected ImageView mErrorButton;
    protected ProgressBar mProgressBar;
    private LoadingCallBack loadingCallBack;

    public NetworkImage(Context context) {
        this(context, null);
    }

    public NetworkImage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NetworkImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.view_network_image, this, true);

        mErrorButton = findViewById(R.id.errorButton);
        mProgressBar = findViewById(R.id.progressBar);

        FrameLayout imageFL = findViewById(R.id.imageFL);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NetworkImage);

        boolean circled = typedArray.getBoolean(R.styleable.NetworkImage_niCircled, false);
        FrameLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        if (circled) {
            mImageView = new CircleImageView(context);
            mBackgroundImageView = new CircleImageView(context);
        } else {
            mImageView = new AppCompatImageView(context);
            mBackgroundImageView = new AppCompatImageView(context);
            mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mBackgroundImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        imageFL.addView(mBackgroundImageView, layoutParams);
        imageFL.addView(mImageView, layoutParams);

        /*Image*/
        setImageDrawable(typedArray.getDrawable(R.styleable.NetworkImage_niSrc));

        /*Background*/
        mBackgroundImageView.setImageDrawable(typedArray.getDrawable(R.styleable.NetworkImage_niBackground));

        typedArray.recycle();

    }

    public void loadImageFromNetwork(final String networkPath, GlideRequests glideRequests) {
        mProgressBar.setVisibility(VISIBLE);
        mErrorButton.setOnClickListener(v -> {
            Log.d(TAG, "Retrying to loadFromNetwork image");
            mProgressBar.setVisibility(VISIBLE);
            mErrorButton.setVisibility(GONE);
            loadImageFromNetwork(networkPath, glideRequests);
        });

        glideRequests.load(networkPath)
                .centerCrop()
                .placeholder(new ColorDrawable(Color.LTGRAY))
                .error(R.drawable.ic_place_holder)
                .listener(new Listener(mImageView, mProgressBar, mErrorButton, loadingCallBack))
                .into(mImageView);
    }

    public void loadImageFromMediaStore(String path, GlideRequests glideRequests) {
        mProgressBar.setVisibility(VISIBLE);
        File file = new File(path);
        glideRequests.load(Uri.fromFile(file))
                .centerCrop()
                .placeholder(new ColorDrawable(Color.LTGRAY))
                .error(R.drawable.ic_place_holder)
                .listener(new Listener(mImageView, mProgressBar, mErrorButton, loadingCallBack))
                .into(mImageView);
    }


    public void loadImageFromMediaStore(Uri uri,GlideRequests glideRequests) {
        mProgressBar.setVisibility(VISIBLE);
        glideRequests.load(uri)
                .centerCrop()
                .placeholder(new ColorDrawable(Color.LTGRAY))
                .error(R.drawable.ic_place_holder)
                .listener(new Listener(mImageView, mProgressBar, mErrorButton, loadingCallBack))
                .into(mImageView);
    }

    public NetworkImage setLoadingCallBack(LoadingCallBack loadingCallBack) {
        this.loadingCallBack = loadingCallBack;
        return this;
    }

    public void setImageDrawable(Drawable drawable) {
        mImageView.setImageDrawable(drawable);
        mImageView.setVisibility(VISIBLE);
        mProgressBar.setVisibility(GONE);
        mErrorButton.setVisibility(GONE);
    }

    public void setImageResource(@DrawableRes int resId) {
        mImageView.setImageResource(resId);
        setImageDrawable(ContextCompat.getDrawable(getContext(), resId));
    }


    public interface LoadingCallBack {
        void onSuccess(Drawable drawable);
    }

    /**
     * Glide Callback which clears the ImageView's background onSuccess. This is done to reduce
     * overdraw. A weak reference is used to avoid leaking the Activity context because the Callback
     * will be strongly referenced by Glide.
     */
    static class Listener implements RequestListener<Drawable> {

        final WeakReference<ImageView> imageViewWeakReference;
        final WeakReference<ProgressBar> progressBarWeakReference;
        final WeakReference<ImageView> errorImageViewWeakReference;
        final WeakReference<LoadingCallBack> loadingCallBackWeakReference;

        private Listener(ImageView imageView, ProgressBar progressBar, ImageView errorImageView, LoadingCallBack loadingCallBack) {
            imageViewWeakReference = new WeakReference<>(imageView);
            progressBarWeakReference = new WeakReference<>(progressBar);
            errorImageViewWeakReference = new WeakReference<>(errorImageView);
            loadingCallBackWeakReference = new WeakReference<>(loadingCallBack);
        }


        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            try {
                Log.e(TAG, String.valueOf(e));
            } catch (Exception e1) {
                Log.e(TAG, e1.toString());
            }

            ProgressBar progressBar = progressBarWeakReference.get();
            if (progressBar != null) {
                progressBar.setVisibility(GONE);
            }

            ImageView errorImageView = errorImageViewWeakReference.get();
            if (errorImageView != null) {
                errorImageView.setVisibility(VISIBLE);
            }

            return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            final ImageView imageView = imageViewWeakReference.get();
            LoadingCallBack loadingCallBack = loadingCallBackWeakReference.get();
            if (loadingCallBack != null) {
                loadingCallBack.onSuccess(resource);
            }
            ProgressBar progressBar = progressBarWeakReference.get();
            if (progressBar != null) {
                progressBar.setVisibility(GONE);
            }

            ImageView errorImageView = errorImageViewWeakReference.get();
            if (errorImageView != null) {
                errorImageView.setVisibility(GONE);
            }
            return false;
        }


    }

}
