package com.starikovskiy_cr.scope_8.net;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.starikovskiy_cr.scope_8.db.models.IPhoto;

import java.util.Map;

/**
 * Created by starikovskiy_cr on 17.03.16.
 */
public class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    Map<String, IPhoto> photoMarkers;
    private Context context;

    public MyInfoWindowAdapter(Context context, Map<String, IPhoto> photoMarkers) {
        this.context = context;
        this.photoMarkers = photoMarkers;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(final Marker marker) {
        IPhoto photoInfo = photoMarkers.get(marker.getId());
        if (photoInfo != null) {
            final ImageView imageView = new ImageView(context);
            Glide.with(context.getApplicationContext())
                    .load(photoInfo.getPath())
                    .centerCrop()
                    .override(150, 150)
                    .into(new GlideDrawableImageViewTarget(imageView) {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                            if (marker != null && marker.isInfoWindowShown()) {
                                marker.hideInfoWindow();
                                marker.showInfoWindow();
                            }
                            super.onResourceReady(resource, animation);
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);
                        }
                    });
            return imageView;
        }
        return null;
    }
}
