package com.starikovskiy_cr.scope_8.db.dao;

import com.starikovskiy_cr.scope_8.db.models.IPhoto;

import java.util.List;

/**
 * Created by starikovskiy_cr on 04.03.16.
 */
public interface IPhotoDAO {

    List<IPhoto> getData();

    List<IPhoto> getPhotoWithCoordinates();

    long createCortege(IPhoto cortege);

    long updateCortege(IPhoto cortege);

    IPhoto getEmptyEntity();

    IPhoto getEntity(Long id);

    void createListPhoto(List<? extends IPhoto> list);
}
