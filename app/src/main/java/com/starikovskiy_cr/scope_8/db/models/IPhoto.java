package com.starikovskiy_cr.scope_8.db.models;

/**
 * Created by starikovskiy_cr on 04.03.16.
 */
public interface IPhoto {

    Long getId();

    String getPath();

    void setPath(String path);

    Double getLatitude();

    void setLatitude(Double latitude);

    Double getLongitude();

    void setLongitude(Double longitude);

    Boolean getIsLoaded();

    void setIsLoaded(Boolean isLoaded);

    String getUrl();

    void setUrl(String url);

    String getParseId();

    void setParseId(String parseId);
}
