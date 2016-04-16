package com.starikovskiy_cr.scope_8.db.dao;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.query.Select;
import com.starikovskiy_cr.scope_8.db.models.IPhoto;
import com.starikovskiy_cr.scope_8.db.models.Photo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by starikovskiy_cr on 04.03.16.
 */
public class PhotoDAO  implements IPhotoDAO{

    @Override
    public List<IPhoto> getData() {
        List<IPhoto> result = new ArrayList<>();
        for (Model exam : new Select().from(Photo.class).execute()) {
            result.add((IPhoto) exam);
        }
        return result;

    }

    @Override
    public List<IPhoto> getPhotoWithCoordinates() {
        List<IPhoto> result = new ArrayList<>();
        for (Model exam : new Select().from(Photo.class).where("latitude is not null").and("longitude is not null").execute()) {
            result.add((IPhoto) exam);
        }
        return result;
    }

    @Override
    public long createCortege(IPhoto cortege) {
        Photo photo = (Photo) cortege;
        return photo.save();
    }

    @Override
    public long updateCortege(IPhoto cortege) {
        Photo photo = (Photo) cortege;
        return photo.save();
    }

    @Override
    public IPhoto getEmptyEntity() {
        return new Photo();
    }

    @Override
    public IPhoto getEntity(Long id) {
        return Photo.load(Photo.class, id);
    }

    @Override
    public void createListPhoto(List<? extends IPhoto> list) {
        ActiveAndroid.beginTransaction();
        try {
           for(IPhoto photo: list){
               ((Photo)photo).save();
           }
            ActiveAndroid.setTransactionSuccessful();
        }
        finally {
            ActiveAndroid.endTransaction();
        }
    }
}
