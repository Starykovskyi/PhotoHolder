package com.starikovskiy_cr.scope_8.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.starikovskiy_cr.scope_8.db.dao.IPhotoDAO;
import com.starikovskiy_cr.scope_8.db.models.IPhoto;

import java.util.List;

/**
 * Created by starikovskiy_cr on 17.03.16.
 */
public class PhotoWithCoordinatesLoader extends AsyncTaskLoader<List<IPhoto>> {
    IPhotoDAO daoPhoto;

    public PhotoWithCoordinatesLoader(IPhotoDAO daoPhoto, Context context) {
        super(context);
        this.daoPhoto = daoPhoto;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<IPhoto> loadInBackground() {
        return daoPhoto.getPhotoWithCoordinates();
    }
}