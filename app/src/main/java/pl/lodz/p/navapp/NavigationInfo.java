package pl.lodz.p.navapp;

import org.osmdroid.util.GeoPoint;

import java.util.List;

/**
 * Created by Łukasz Świtoń on 12.11.2016.
 */

public class NavigationInfo {
    private List<GeoPoint> waypoints;
    private String routeType;

    public void setWaypoints(List<GeoPoint> waypoints) {
        this.waypoints = waypoints;
    }

    public List<GeoPoint> getWaypoints() {
        return waypoints;
    }

    public void setRouteType(String routeType) {
        this.routeType = routeType;
    }

    public String getRouteType() {
        return routeType;
    }
}
