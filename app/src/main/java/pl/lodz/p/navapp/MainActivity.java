package pl.lodz.p.navapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
    AutoCompleteTextView autocompleteLocation;
    String[] names;
    String[] addresses;
    double[] lat;
    double[] lon;
    List<Drawable> images;

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

        autocompleteLocation = (AutoCompleteTextView) findViewById(R.id.autocompleteLocation);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
        autocompleteLocation.setAdapter(adapter);
        autocompleteLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                addMarker(i);
            }
        });
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
        images = new ArrayList<>();
        Resources resources = getResources();
        images.add(resources.getDrawable(R.drawable.weeia));
        images.add(resources.getDrawable(R.drawable.mech));
        images.add(resources.getDrawable(R.drawable.chem));
        images.add(resources.getDrawable(R.drawable.mech));
        images.add(resources.getDrawable(R.drawable.mech_fabryka_inz));
        images.add(resources.getDrawable(R.drawable.chem));
        images.add(resources.getDrawable(R.drawable.ife));
        images.add(resources.getDrawable(R.drawable.binoz));
        images.add(resources.getDrawable(R.drawable.weeia));
        images.add(resources.getDrawable(R.drawable.weeia));
        images.add(resources.getDrawable(R.drawable.ipos));
        images.add(resources.getDrawable(R.drawable.binoz));
        images.add(resources.getDrawable(R.drawable.rekrutacja));
        images.add(resources.getDrawable(R.drawable.wzornictwa));

        names = new String[]{"Wydział Elektrotechniki Elektroniki Informatyki i Automatyki", "Wydział Mechaniczny", "Wydział Chemiczny", "Wydział Mechaniczny",
                "Wydział Mechaniczny - Fabryka Inżynierów", "Instytut Chemi Ogólnej i Ekologicznej", "Centrum Kształcenia Międzynarodowego"
                , "Wydział Biotechnologii i nauk o żywności", "Katedra aparatury przemysłowej", "Instytut Mechatroniki i Systemów Informatycznych",
                "Wydział Inżynierii Procesowej i Ochrony środowiska", "Wydział Biotechnologii i nauk o żywności", "Rekrutacja", "Wydział Technologii Materiałowych i Wzornictwa Tekstyliów"};
        addresses = new String[]{"Stefanowskiego 18/22", "Stefanowskiego 1/15", "Żeromskiego 116", "Stefanowskiego 1/15", "Stefanowskiego 2", "Żeromskiego 116", "Żwirki 36",
                "Stefanowskiego", "Stefanowskiego 12/16", "Stefanowskiego 18/22", "Wólczańska 213", "Wólczańska 171/173", "Stefanowskiego 18/22", "Żeromskiego 116"};
        lat = new double[]{51.75252299, 51.75284959, 51.75365549, 51.75373305, 51.75495143, 51.75424923, 51.75503501, 51.75472001, 51.75370351, 51.7537103, 51.75412276, 51.75448702, 51.75259608, 51.75283733};
        lon = new double[]{19.45303313, 19.45258552, 19.45091179, 19.45180004, 19.45124336, 19.45077855, 19.45152691, 19.45264238, 19.4529498, 19.45394666, 19.45414356, 19.45450147, 19.45366977, 19.4502742};

        for (int i = 0; i < names.length; i++) {
            placesList.add(new PlaceInfo(names[i], addresses[i], lat[i], lon[i]));
        }

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

    public void addMarker(int id) {
        mMapView.getOverlays().clear();
        Marker marker = new Marker(mMapView);
        GeoPoint point = placesList.get(id).getGeoPoint();
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setIcon(getResources().getDrawable(R.drawable.marker_red));
        marker.setTitle(placesList.get(id).getTitle());
        marker.setSubDescription(placesList.get(id).getSubDescription());
        marker.setImage(images.get(id));
        mMapView.getOverlays().clear();
        mMapView.getOverlays().add(marker);
        mMapView.invalidate();
        mMapController.animateTo(point);
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
