package pl.lodz.p.navapp.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.lodz.p.navapp.NavAppApplication;
import pl.lodz.p.navapp.OnFragmentInteractionListener;
import pl.lodz.p.navapp.domain.PlaceInfo;
import pl.lodz.p.navapp.R;
import pl.lodz.p.navapp.domain.Sublocation;
import pl.lodz.p.navapp.fragment.MapFragment;
import pl.lodz.p.navapp.fragment.TimetableFragment;
import pl.lodz.p.navapp.service.DatabaseHelper;

import static pl.lodz.p.navapp.service.DatabaseConstants.DATABASE_NAME;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnFragmentInteractionListener {

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    private DatabaseHelper cordinatesDB;
    private static final String url = "https://nav-app.herokuapp.com/api/buildings";
    private List<PlaceInfo> placeInfos;
    private AutoCompleteTextView autocompleteLocation;
    private List<String> names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cordinatesDB = new DatabaseHelper(this);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions();
        }
        placeInfos = new ArrayList<>();
        names = new ArrayList<>();
        if(savedInstanceState==null){
            MapFragment mapFragment = MapFragment.getInstance();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, mapFragment);
            fragmentTransaction.commit();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        autocompleteLocation = (AutoCompleteTextView) findViewById(R.id.mySearchView);
        if(cordinatesDB.isPlacesEmpty()){
            getTimetableInfo();
        }else{
           populateFromDatabase();
       }
    }

    private void populateFromDatabase() {
        this.names = cordinatesDB.getPlacesNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplication(), R.layout.my_list_layout, names);
        autocompleteLocation.setAdapter(adapter);
    }

    private void getTimetableInfo() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    translateResponse(response);
                    insertToDatabase();
                    names= cordinatesDB.getPlacesNames();
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplication(), R.layout.my_list_layout, names);
                    autocompleteLocation.setAdapter(adapter);
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Błąd podczas parsowania danych", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Błąd podczas pobierania danych",Toast.LENGTH_SHORT).show();
            }
        }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                String creds = String.format("%s:%s","user","user");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }};
        NavAppApplication.getInstance().addToRequestQueue(stringRequest);
    }

    private void insertToDatabase() {
        for(int i=0;i<placeInfos.size();i++){
            cordinatesDB.insertPlace(placeInfos.get(i));
        }
    }


    private void translateResponse(String response) throws JSONException {
        JSONArray array = new JSONArray(response);
        for (int i = 0; i < array.length(); i++) {
            PlaceInfo placeInfo = new PlaceInfo();
            JSONObject object = (JSONObject) array.get(i);
            placeInfo.setID(Integer.parseInt(object.getString("id")));
            placeInfo.setTitle(object.getString("name").trim());
            placeInfo.setPlaceNumber(object.getString("code").trim());
            placeInfo.setDescription(object.getString("description").trim());
            placeInfo.setAddress(object.getString("street").trim());
            if(!"null".equalsIgnoreCase(object.getString("longitude"))){
                double lon = Double.valueOf(object.getString("longitude"));
                double lat = Double.valueOf(object.getString("latitude"));
                GeoPoint geoPoint = new GeoPoint(lat,lon);
                placeInfo.setGeoPoint(geoPoint);
            }
            JSONArray subLocations = object.getJSONArray("sublocations");
            List<Sublocation> sublocationList = placeInfo.getSublocations();
            for(int j=0;j<subLocations.length();j++){
                JSONObject responseSublocation = (JSONObject) subLocations.get(j);
                Sublocation sublocation = new Sublocation();
                sublocation.setId(Integer.parseInt(responseSublocation.getString("id")));
                sublocation.setName(responseSublocation.getString("name").trim());
                sublocation.setCode(responseSublocation.getString("code").trim());
                sublocation.setPlaceID(placeInfo.getID());
                sublocationList.add(sublocation);
            }
            placeInfos.add(placeInfo);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_map) {
            MapFragment mapFragment = MapFragment.getInstance();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, mapFragment);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_timetable) {
            TimetableFragment timetableFragment = new TimetableFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, timetableFragment);
            fragmentTransaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

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

    public List<String> getNames(){
        return this.names;
    }

    public DatabaseHelper getCordinatesDB() {
        return cordinatesDB;
    }
}
