package pl.lodz.p.navapp;

import org.osmdroid.util.GeoPoint;

/**
 * Created by Łukasz Świtoń on 24.10.2016.
 */

public class PlaceInfo {
    private String title;
    private String subDescription;
    private int drawableID;
    private GeoPoint geoPoint;

    public PlaceInfo(String title, String subDescription, double lat, double lon) {
        this.title = title;
        this.subDescription = subDescription;
        this.geoPoint = new GeoPoint(lat,lon);
    }

    public int getDrawableID() {
        return drawableID;
    }

    public void setDrawableID(int drawableID) {
        this.drawableID = drawableID;
    }

    public String getSubDescription() {
        return subDescription;
    }

    public void setSubDescription(String subDescription) {
        this.subDescription = subDescription;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }
}
