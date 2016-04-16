package com.starikovskiy_cr.scope_8.db.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

/**
 * Created by starikovskiy_cr on 04.03.16.
 */
@Table(name = "photo")
public class Photo extends Model implements IPhoto {

    @Expose
    @Column(name = "path")
    private String path;

    @Expose
    @Column(name = "url")
    private String url;

    @Expose
    @Column(name = "latitude")
    private Double latitude;

    @Expose
    @Column(name = "longitude")
    private Double longitude;

    @Expose
    @Column(name = "is_loaded")
    private Boolean isLoaded;


    @Column(name = "parse_id")
    private String parseId;

    public String getParseId() {
        return parseId;
    }

    public void setParseId(String parseId) {
        this.parseId = parseId;
    }

    public Photo() {
        super();
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public Double getLatitude() {
        return latitude;
    }

    @Override
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    @Override
    public Double getLongitude() {
        return longitude;
    }

    @Override
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public Boolean getIsLoaded() {
        return isLoaded;
    }

    @Override
    public void setIsLoaded(Boolean isLoaded) {
        this.isLoaded = isLoaded;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "path='" + path + '\'' +
                ", uri='" + url + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", isLoaded=" + isLoaded +
                '}';
    }
}
