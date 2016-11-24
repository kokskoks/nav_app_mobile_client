package pl.lodz.p.navapp;

import android.location.Location;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Łukasz Świtoń on 24.10.2016.
 */

public class PlaceInfo {
    private int id;
    private String title;
    private String placeNumber;
    private String address;
    private String description;
    private int drawableID;
    private GeoPoint geoPoint;
    private List<String> names;


    public PlaceInfo(Location myLocation, int id, String title, String placeNumber, String address, String description, int drawableID) {
        this.geoPoint = new GeoPoint(myLocation.getLatitude(), myLocation.getLongitude());
        this.id = id;
        this.title = title;
        this.placeNumber = placeNumber;
        this.address = address;
        this.description = description;
        this.drawableID = drawableID;
    }

    public PlaceInfo(Location location) {
        this.geoPoint = new GeoPoint(location);
    }

    public PlaceInfo() {
    }

    public PlaceInfo(int i, String name, String address, double v, double v1) {
        this.id = i;
        this.title = name;
        this.address = address;
        this.geoPoint = new GeoPoint(v, v1);
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlaceNumber() {
        return placeNumber;
    }

    public void setPlaceNumber(String placeNumber) {
        this.placeNumber = placeNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDrawableID() {
        return drawableID;
    }

    public void setDrawableID(int drawableID) {
        this.drawableID = drawableID;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public List<String> getNames() {
        if (this.names == null) {
            this.names = new ArrayList<>();
        }
        return this.names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }
}
