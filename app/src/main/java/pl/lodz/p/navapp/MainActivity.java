package pl.lodz.p.navapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static final int MAX_ZOOM_LEVEL = 20;
    public static final int ZOOMLEVEL = 17;
    String locationProvider;
    private MapView mMapView;
    private MapController mMapController;
    LocationManager locationManager;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    List<PlaceInfo> placesList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        placesList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions();
        }
        setContentView(R.layout.activity_main);
        setupMap();
        createData();
        addMarkers();

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        FloatingActionButton fabLocation = (FloatingActionButton) findViewById(R.id.fabLocation);
        fabLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationProvider = LocationManager.NETWORK_PROVIDER;
                Location myLocation = locationManager.getLastKnownLocation(locationProvider);
                if (myLocation != null) {
                    GeoPoint gPt = new GeoPoint(myLocation.getLatitude(), myLocation.getLongitude());
                    mMapController.animateTo(gPt);
                }
            }
        });

        FloatingActionButton fabTimetable = (FloatingActionButton) findViewById(R.id.fabTimetable);
        fabTimetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent timetableIntent = new Intent(getApplicationContext(), TimetableActivity.class);
                startActivity(timetableIntent);
            }
        });
    }

    private void createData() {
        placesList.add(new PlaceInfo("WydziaĹ‚ Elektrotechniki Elektroniki Informatyki i Automatyki","Stefanowskiego 18/22",51.75252299,19.45303313));
        placesList.add(new PlaceInfo("WydziaĹ‚ Mechaniczny","Stefanowskiego 1/15",51.75284959,19.45258552));
        placesList.add(new PlaceInfo("WydziaĹ‚ Chemiczny","Ĺ»eromskiego 116",51.75365549,19.45091179));
        placesList.add(new PlaceInfo("WydziaĹ‚ Mechaniczny","Stefanowskiego 1/15",51.75373305,19.45180004));
        placesList.add(new PlaceInfo("WydziaĹ‚ Mechaniczny - Fabryka InĹĽynierĂłw","Stefanowskiego 2",51.75495143, 19.45124336));
        placesList.add(new PlaceInfo("Katedra WĹ‚Ăłkien Sztucznych","Ĺ»eromskiego 116",51.75437321,19.45109669));
        placesList.add(new PlaceInfo("Instytut Chemi OgĂłlnej i Ekologicznej","Ĺ»eromskiego",51.75424923,19.45077855));
        placesList.add(new PlaceInfo("Centrum KsztaĹ‚cenia MiÄ™dzynarodowego","Ĺ»wirki 36",51.75503501,19.45152691));
        placesList.add(new PlaceInfo("WydziaĹ‚ Biotechnologii i nauk o ĹĽywnoĹ›ci","Stefanowskiego",51.75472001,19.45264238));
        placesList.add(new PlaceInfo("Katedra aparatury przemysĹ‚owej","Stefanowskiego 12/16",51.75370351,19.4529498));
        placesList.add(new PlaceInfo("Instytut Mechatroniki i SystemĂłw Informatycznych","Stefanowskiego 18/22",51.7537103,19.45394666));
        placesList.add(new PlaceInfo("WydziaĹ‚ InĹĽynierii Procesowej i Ochorny Ĺšrodowiska","WĂłlczaĹ„ska 213",51.75412276,19.45414356));
        placesList.add(new PlaceInfo("WydziaĹ‚ Biotechnologii i nauk o ĹĽywnoĹ›ci","WĂłlczaĹ„ska 171/173",51.75448702,19.45450147));
        placesList.add(new PlaceInfo("Rekrutacja","Stefanowskiego 18/22",51.75259608,19.45366977));
        placesList.add(new PlaceInfo("WydziaĹ‚ Technologii MateriaĹ‚owych i Wzornictwa TekstyliĂłw","Ĺ»eromskiego 116",51.75283733,19.4502742));

    }

    private void setupMap() {
        mMapView = (MapView) findViewById(R.id.mapview);
        mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mMapView.setMaxZoomLevel(MAX_ZOOM_LEVEL);
        mMapView.setMultiTouchControls(true);
        mMapView.setClickable(true);
        mMapController = (MapController) mMapView.getController();
        mMapController.setZoom(ZOOMLEVEL);
        GeoPoint gPt = new GeoPoint(51.745649, 19.454488);
        mMapController.setCenter(gPt);
    }

    public void addMarkers() {
        mMapView.getOverlays().clear();
        for(PlaceInfo info:placesList){
            Marker marker = new Marker(mMapView);
            marker.setPosition(info.getGeoPoint());
            marker.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_BOTTOM);
            marker.setIcon(getResources().getDrawable(R.drawable.marker));
            marker.setTitle(info.getTitle());
            marker.setSubDescription(info.getSubDescription());
            //marker.setImage(getResources().getDrawable(drawableID));
            //mMapView.getOverlays().clear();
            mMapView.getOverlays().add(marker);
            mMapView.invalidate();
        }
    }

    // Request permissions to support Android Marshmallow and above devices  (api-23)
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        List<String> permissions = new ArrayList<>();
        String message = "OSMDroid permissions:";
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            message += "\nStorage access to store map tiles.";
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            message += "\nLocation to show user location.";
        }
        if (!permissions.isEmpty()) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            String[] params = permissions.toArray(new String[permissions.size()]);
            requestPermissions(params, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        }
    }

    // Request permissions to support Android Marshmallow and above devices. (api-23)
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<>();
                // Initial
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION and WRITE_EXTERNAL_STORAGE
                Boolean location = perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
                Boolean storage = perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
                if (location && storage) {
                    // All Permissions Granted
                    Toast.makeText(MainActivity.this, "All permissions granted", Toast.LENGTH_SHORT).show();
                } else if (location) {
                    Toast.makeText(this, "Storage permission is required to store map tiles to reduce data usage and for offline usage.", Toast.LENGTH_LONG).show();
                } else if (storage) {
                    Toast.makeText(this, "Location permission is required to show the user's location on map.", Toast.LENGTH_LONG).show();
                } else { // !location && !storage case
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "Storage permission is required to store map tiles to reduce data usage and for offline usage." +
                            "\nLocation permission is required to show the user's location on map.", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    // END PERMISSION CHECK
}
