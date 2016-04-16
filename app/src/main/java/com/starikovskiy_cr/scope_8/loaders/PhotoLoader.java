package com.starikovskiy_cr.scope_8.loaders;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.starikovskiy_cr.scope_8.db.dao.IPhotoDAO;
import com.starikovskiy_cr.scope_8.db.models.IPhoto;

import java.util.List;

/**
 * Created by starikovskiy_cr on 11.03.16.
 */
public class PhotoLoader extends AsyncTaskLoader<List<IPhoto>> {
    IPhotoDAO daoPhoto;

    @Override
    protected void onStartLoading() {
            forceLoad();
    }

    private PhotoLoader(Context context) {
        super(context);
        onContentChanged();
    }

    public PhotoLoader(IPhotoDAO daoPhoto, Context context) {
        this(context);
        this.daoPhoto = daoPhoto;
    }


    @Override
    public List<IPhoto> loadInBackground() {
        return daoPhoto.getData();
    }
}
