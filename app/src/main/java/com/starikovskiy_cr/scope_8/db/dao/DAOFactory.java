package com.starikovskiy_cr.scope_8.db.dao;

/**
 * Created by starikovskiy_cr on 11.03.16.
 */
public class DAOFactory {
    public static IPhotoDAO getPhotoDAO(){return new PhotoDAO();}
}
