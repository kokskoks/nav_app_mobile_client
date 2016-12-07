package pl.lodz.p.navapp.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.lodz.p.navapp.NavAppApplication;
import pl.lodz.p.navapp.OnFragmentInteractionListener;
import pl.lodz.p.navapp.R;
import pl.lodz.p.navapp.domain.Classes;
import pl.lodz.p.navapp.domain.Classroom;
import pl.lodz.p.navapp.domain.Lecturer;
import pl.lodz.p.navapp.domain.PlaceInfo;
import pl.lodz.p.navapp.domain.Sublocation;
import pl.lodz.p.navapp.fragment.MapFragment;
import pl.lodz.p.navapp.fragment.TimetableFragment;
import pl.lodz.p.navapp.service.DatabaseHelper;
import pl.lodz.p.navapp.service.RequestManager;

import static pl.lodz.p.navapp.ApplicationConstants.FILE_READ_ERROR;
import static pl.lodz.p.navapp.ApplicationConstants.NO_INTERNET_ACCESS;
import static pl.lodz.p.navapp.ApplicationConstants.URL;
import static pl.lodz.p.navapp.ApplicationConstants.VERSION_FILE_NAME;
import static pl.lodz.p.navapp.service.DatabaseConstants.DATABASE_NAME;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnFragmentInteractionListener {

    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    private DatabaseHelper cordinatesDB;
    private List<PlaceInfo> placeInfos;
    private AutoCompleteTextView autocompleteLocation;
    private List<String> names;
    private int version;
    private Fragment lastAddedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cordinatesDB = new DatabaseHelper(this);
        getApplicationContext().deleteDatabase(DATABASE_NAME);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions();
        }
        placeInfos = new ArrayList<>();
        names = new ArrayList<>();
        if (savedInstanceState == null) {
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
        names.add("CH Sukcesja");
        //test placeinfo
        PlaceInfo placeInfo = new PlaceInfo();
        placeInfo.setID(1);
        placeInfo.setTitle("CH Sukcesja");
        placeInfo.setAddress("Politechniki 1");
        placeInfo.setPlaceNumber("W6");
        GeoPoint geoPoint = new GeoPoint(51.750647,19.449435);
        placeInfo.setGeoPoint(geoPoint);
        placeInfo.setDescription("http://sukcesja.eu/");
        Sublocation sublocation = new Sublocation();
        sublocation.setId(2);
        sublocation.setName("CH Sukcesja");
        sublocation.setCode("204");
        List<Sublocation> sublocations = new ArrayList<>();
        sublocations.add(sublocation);
        placeInfo.setSublocations(sublocations);
        cordinatesDB.insertPlace(placeInfo);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplication(), R.layout.my_list_layout, names);
        autocompleteLocation.setAdapter(adapter);
        //test placeinfo

          checkDatabaseVersion();
    }

    private void checkDatabaseVersion() {
        version = cordinatesDB.checkDBVersion();
        if (version != NO_INTERNET_ACCESS) {
            int localVersion = readFromFile(this);
            if (localVersion == FILE_READ_ERROR || version != localVersion) {
                getTimetableInfo();
            } else {
                populateFromDatabase();
            }
        } else {
            populateFromDatabase();
        }
    }

    private void writeVersionToFile(int data, Context context) {
        try {
            String version = String.valueOf(data);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(VERSION_FILE_NAME, Context.MODE_PRIVATE));
            outputStreamWriter.write(version);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private int readFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(VERSION_FILE_NAME);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
            return FILE_READ_ERROR;
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
            return FILE_READ_ERROR;
        }

        return Integer.parseInt(ret);
    }

    private void populateFromDatabase() {
        this.names = cordinatesDB.getPlacesNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplication(), R.layout.my_list_layout, names);
        autocompleteLocation.setAdapter(adapter);
    }

    private void getTimetableInfo() {
        RequestManager.sendRequest(Request.Method.GET, URL + "/buildings", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    translateResponsePlaceInfo(response);
                    insertToDatabase();
                    names = cordinatesDB.getPlacesNames();
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplication(), R.layout.my_list_layout, names);
                    autocompleteLocation.setAdapter(adapter);
                    writeVersionToFile(version, getApplicationContext());
                    Toast.makeText(getApplicationContext(), "Pomyślnie sparsowano", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Błąd podczas parsowania danych", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Błąd podczas pobierania danych", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void insertToDatabase() {
        for (int i = 0; i < placeInfos.size(); i++) {
            cordinatesDB.insertPlace(placeInfos.get(i));
        }
    }
    private List<Classes> classesList;
    private void translateResponeClasses(String response) throws JSONException {
        JSONArray array = new JSONArray(response);
        for (int i = 0; i < array.length(); i++) {
            Classes classes = new Classes();
            JSONObject object = (JSONObject) array.get(i);
            classes.setID(Integer.parseInt(object.getString("id")));
            classes.setName(object.getString("name").trim());
            classes.setModuleCode(object.getString("moduleCode").trim());
            classes.setDescription(object.getString("description").trim());
            classes.setType(object.getString("type").trim());
            classes.setStartHour(Integer.parseInt(object.getString("startHour").trim()));
            classes.setEndHour(Integer.parseInt(object.getString("endHour").trim()));
            classes.setWeekday(object.getString("weekDay").trim());
            classesList.add(classes);
        }
    }

    private List<Classroom> classroomList;
    private void translateResponeClassroom(String response) throws JSONException {
        JSONArray array = new JSONArray(response);
        for (int i = 0; i < array.length(); i++) {
            Classroom classroom = new Classroom();
            JSONObject object = (JSONObject) array.get(i);
            classroom.setID(Integer.parseInt(object.getString("id")));
            classroom.setName(object.getString("name").trim());
            classroom.setDescription(object.getString("description").trim());
            classroom.setFloor(Integer.parseInt(object.getString("floor").trim()));
            classroomList.add(classroom);
        }
    }
    private List<Lecturer> lecturerList;
    private void translateResponeLecturer(String response) throws JSONException {
        JSONArray array = new JSONArray(response);
        for (int i = 0; i < array.length(); i++) {
            Lecturer lecturer = new Lecturer();
            JSONObject object = (JSONObject) array.get(i);
            lecturer.setID(Integer.parseInt(object.getString("id")));
            lecturer.setFirstName(object.getString("firstName").trim());
            lecturer.setLastName(object.getString("lastName").trim());
            lecturer.setTitle(object.getString("title").trim());
            lecturer.setDescription(object.getString("description").trim());
            lecturer.setMail(object.getString("mail").trim());
            lecturerList.add(lecturer);
        }
    }






    private void translateResponsePlaceInfo(String response) throws JSONException {
        JSONArray array = new JSONArray(response);
        for (int i = 0; i < array.length(); i++) {
            PlaceInfo placeInfo = new PlaceInfo();
            JSONObject object = (JSONObject) array.get(i);
            placeInfo.setID(Integer.parseInt(object.getString("id")));
            placeInfo.setTitle(object.getString("name").trim());
            placeInfo.setPlaceNumber(object.getString("code").trim());
            placeInfo.setDescription(object.getString("description").trim());
            placeInfo.setAddress(object.getString("street").trim());
            if (!"null".equalsIgnoreCase(object.getString("longitude"))) {
                double lon = Double.valueOf(object.getString("longitude"));
                double lat = Double.valueOf(object.getString("latitude"));
                GeoPoint geoPoint = new GeoPoint(lat, lon);
                placeInfo.setGeoPoint(geoPoint);
            }
            JSONArray subLocations = object.getJSONArray("sublocations");
            List<Sublocation> sublocationList = placeInfo.getSublocations();
            for (int j = 0; j < subLocations.length(); j++) {
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
    private void initDialog(final Dialog dialog) {
        dialog.setContentView(R.layout.group_choser_dialog);
        Button ok = (Button) dialog.findViewById(R.id.groupConfirmButton);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                TimetableFragment timetableFragment = new TimetableFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, timetableFragment);
                fragmentTransaction.commit();
                lastAddedFragment = timetableFragment;
            }
        });
        Button cancel = (Button) dialog.findViewById(R.id.group_cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_map) {
            MapFragment mapFragment = MapFragment.getInstance();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, mapFragment);
            fragmentTransaction.commit();
            lastAddedFragment = mapFragment;
        } else if (id == R.id.nav_timetable) {
            if(lastAddedFragment == null || lastAddedFragment instanceof MapFragment) {
                final Dialog dialog = new Dialog(this);
                initDialog(dialog);
            }
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
        if (requestCode == REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS) {
            Map<String, Integer> perms = new HashMap<>();
            perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            for (int i = 0; i < permissions.length; i++)
                perms.put(permissions[i], grantResults[i]);
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

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    // END PERMISSION CHECK

    public List<String> getNames() {
        return this.names;
    }

    public DatabaseHelper getCordinatesDB() {
        return cordinatesDB;
    }
}
