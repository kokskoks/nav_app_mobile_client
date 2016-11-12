package pl.lodz.p.navapp;

import android.os.AsyncTask;

import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

import pl.lodz.p.navapp.fragment.MapFragment;

/**
 * Created by Kamil on 2016-11-05.
 */
public class RouteFinder extends AsyncTask<NavigationInfo, Void, Road> {

    MapFragment mapFragment;

    public RouteFinder(MapFragment mapFragment) {
        this.mapFragment = mapFragment;
    }

    @Override
    protected Road doInBackground(NavigationInfo... params) {
        NavigationInfo info = params[0];
        ArrayList<GeoPoint> waypoints = (ArrayList<GeoPoint>) info.getWaypoints();
        RoadManager roadManager = new MapQuestRoadManager(mapFragment.getString(R.string.mapsKey));
        roadManager.addRequestOption("routeType=" + info.getRouteType());
        return roadManager.getRoad(waypoints);
    }

    @Override
    protected void onPostExecute(Road road) {
        mapFragment.drawRoute(road);
        super.onPostExecute(road);
    }

}
