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

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.lang.ref.WeakReference;

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
    private boolean noRetry = false;

    @Nullable
    private Drawable placeholder = new ColorDrawable(Color.LTGRAY);

    @Nullable
    private Drawable error = new ColorDrawable(Color.LTGRAY);

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
            CircleImageView imageView = new CircleImageView(context);
            int borderColor = typedArray.getColor(R.styleable.NetworkImage_niBorderColor, Color.WHITE);
            int borderWidth = typedArray.getDimensionPixelOffset(R.styleable.NetworkImage_niBorderWidth, 0);
            imageView.setBorderColor(borderColor);
            imageView.setBorderWidth(borderWidth);
            imageView.setBorderOverlay(true);
            mImageView = imageView;
            // mImageView = new CircleImageView(context);
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
        int drawableResId = typedArray.getResourceId(R.styleable.NetworkImage_niSrc, -1);
        if (drawableResId != -1) {
            Drawable drawable = AppCompatResources.getDrawable(getContext(), drawableResId);
            setImageDrawable(drawable);
        }

        /*Background*/
        int backgroundResId = typedArray.getResourceId(R.styleable.NetworkImage_niBackground, -1);
        if (backgroundResId != -1) {
            Drawable backgroundDrawable = AppCompatResources.getDrawable(getContext(), backgroundResId);
            mBackgroundImageView.setImageDrawable(backgroundDrawable);
        }
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
                .placeholder(placeholder)
                .error(error)
                .listener(new Listener(mImageView, mProgressBar, mErrorButton, loadingCallBack, noRetry))
                .into(mImageView);
    }

    public void loadImageFromMediaStore(String path, GlideRequests glideRequests) {
        mProgressBar.setVisibility(VISIBLE);
        File file = new File(path);
        glideRequests.load(Uri.fromFile(file))
                .centerCrop()
                .placeholder(new ColorDrawable(Color.LTGRAY))
                .error(R.drawable.ic_place_holder)
                .listener(new Listener(mImageView, mProgressBar, mErrorButton, loadingCallBack, noRetry))
                .into(mImageView);
    }


    public void loadImageFromMediaStore(Uri uri, GlideRequests glideRequests) {
        mProgressBar.setVisibility(VISIBLE);
        glideRequests.load(uri)
                .centerCrop()
                .placeholder(new ColorDrawable(Color.LTGRAY))
                .error(R.drawable.ic_place_holder)
                .listener(new Listener(mImageView, mProgressBar, mErrorButton, loadingCallBack, noRetry))
                .into(mImageView);
    }

    public NetworkImage setLoadingCallBack(LoadingCallBack loadingCallBack) {
        this.loadingCallBack = loadingCallBack;
        return this;
    }

    public NetworkImage setImageDrawable(Drawable drawable) {
        mImageView.setImageDrawable(drawable);
        mImageView.setVisibility(VISIBLE);
        mProgressBar.setVisibility(GONE);
        mErrorButton.setVisibility(GONE);
        return this;
    }

    public NetworkImage setImageUri(Uri uri) {
        mImageView.setImageURI(uri);
        mImageView.setVisibility(VISIBLE);
        mProgressBar.setVisibility(GONE);
        mErrorButton.setVisibility(GONE);
        return this;
    }

    public ImageView getImageView() {
        return this.mImageView;
    }


    public NetworkImage setImageBackgroundColor(@ColorInt int color) {
        mBackgroundImageView.setImageDrawable(new ColorDrawable(color));
        return this;
    }


    public NetworkImage setImageResource(@DrawableRes int resId) {
        Drawable drawable = AppCompatResources.getDrawable(getContext(), resId);
        mImageView.setImageDrawable(drawable);
        return this;
    }

    public NetworkImage setPlaceholderDrawable(@Nullable Drawable drawable) {
        this.placeholder = drawable;
        // 0702 238 023
        return this;
    }

    public NetworkImage setErrorDrawable(@Nullable Drawable drawable) {
        this.error = drawable;
        return this;
    }

    public NetworkImage setNoRetry() {
        this.noRetry = true;
        return this;
    }


    public NetworkImage setScaleType(ImageView.ScaleType scaleType) {
        mImageView.setScaleType(scaleType);
        return this;
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
        final WeakReference<Boolean> noRetryWeakReference;
        final WeakReference<LoadingCallBack> loadingCallBackWeakReference;

        private Listener(ImageView imageView, ProgressBar progressBar, ImageView errorImageView,
                         LoadingCallBack loadingCallBack, Boolean noRetry) {
            imageViewWeakReference = new WeakReference<>(imageView);
            progressBarWeakReference = new WeakReference<>(progressBar);
            errorImageViewWeakReference = new WeakReference<>(errorImageView);
            loadingCallBackWeakReference = new WeakReference<>(loadingCallBack);
            noRetryWeakReference = new WeakReference<>(noRetry);
        }


        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            Log.e(TAG, String.valueOf(e));

            ProgressBar progressBar = progressBarWeakReference.get();
            if (progressBar != null) {
                progressBar.setVisibility(GONE);
            }

            ImageView errorImageView = errorImageViewWeakReference.get();

            boolean noRetry = noRetryWeakReference.get();
            if (errorImageView != null) {
                errorImageView.setVisibility(noRetry ? GONE : VISIBLE);
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
