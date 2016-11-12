package pl.lodz.p.navapp.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pl.lodz.p.navapp.NavigationInfo;
import pl.lodz.p.navapp.OnFragmentInteractionListener;
import pl.lodz.p.navapp.PlaceInfo;
import pl.lodz.p.navapp.R;
import pl.lodz.p.navapp.RouteFinder;

public class MapFragment extends Fragment {
    private MapView mMapView;
    private MapController mMapController;
    public static final int MAX_ZOOM_LEVEL = 20;
    public static final int ZOOMLEVEL = 17;
    private LocationManager locationManager;
    private OnFragmentInteractionListener mListener;
    private List<PlaceInfo> placesList;
    private PlaceInfo from;
    private PlaceInfo to;
    private String[] names;
    private List<Drawable> images;
    private AutoCompleteTextView autocompleteLocation;
    boolean fromCurrentLocation = true;

    private static MapFragment instance = null;

    public static MapFragment getInstance() {
        if (instance == null) {
            instance = new MapFragment();
        }
        return instance;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        placesList = new ArrayList<>();
        createData();
    }

    public void addMarker(PlaceInfo place, boolean buildingInfoMarker) {
        Marker marker = new Marker(mMapView);
        GeoPoint point = place.getGeoPoint();
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setIcon(getResources().getDrawable(R.drawable.marker_red));
        marker.setTitle(place.getTitle());
        marker.setSubDescription(place.getSubDescription());
        marker.setImage(images.get(place.getID()));
        if (buildingInfoMarker) {
            mMapView.getOverlays().clear();
        }
        mMapView.getOverlays().add(marker);
        mMapView.invalidate();
        if(buildingInfoMarker){
            mMapController.animateTo(point);
        }
    }

    private void setupMap(View view) {
        mMapView = (MapView) view.findViewById(R.id.mapview);
        mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mMapView.setMaxZoomLevel(MAX_ZOOM_LEVEL);
        mMapView.setMultiTouchControls(true);
        mMapView.setClickable(true);
        mMapController = (MapController) mMapView.getController();
        mMapController.setZoom(ZOOMLEVEL);
        GeoPoint gPt = new GeoPoint(51.745649, 19.454488);
        mMapController.setCenter(gPt);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        setupMap(view);

        FloatingActionButton fabNavigate = (FloatingActionButton) view.findViewById(R.id.fabNavigate);
        fabNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getContext());
                initDialog(dialog);
            }
        });
        final FloatingActionButton fabLocation = (FloatingActionButton) view.findViewById(R.id.fabLocation);
        fabLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlaceInfo currentLocation = findCurrentLocation();
                if (currentLocation != null) {
                    addCurrentLocationMarker(currentLocation,false);
                }
            }
        });
        autocompleteLocation = (AutoCompleteTextView) getActivity().findViewById(R.id.mySearchView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, names);
        autocompleteLocation.setAdapter(adapter);
        autocompleteLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                addMarker(placesList.get(i), true);
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
        return view;
    }

    private PlaceInfo findCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        Location currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        return new PlaceInfo(currentLocation);
    }

    private void drawPath(GeoPoint from, GeoPoint to, int type) {
        NavigationInfo info = new NavigationInfo();
        ArrayList<GeoPoint> waypoints = new ArrayList<>();
        waypoints.add(from);
        waypoints.add(to);
        switch (type) {
            case R.id.bicycleRadioButton:
                info.setRouteType("bicycle");
                break;
            case R.id.carRadioButton:
                info.setRouteType("fastest");
                break;
            default:
                info.setRouteType("pedestrian");
                break;
        }
        info.setWaypoints(waypoints);
        new RouteFinder(MapFragment.getInstance()).execute(info);
        mMapController.animateTo(from);
    }

    private void initDialog(final Dialog dialog) {
        dialog.setContentView(R.layout.navigation_dialog);
        final AutoCompleteTextView dialogToLocation = (AutoCompleteTextView) dialog.findViewById(R.id.dialogToLocation);
        final AutoCompleteTextView dialogFromLocation = (AutoCompleteTextView) dialog.findViewById(R.id.dialogFromLocation);
        final CheckBox currentLocation = (CheckBox) dialog.findViewById(R.id.currentLocationCheckbox);
        final RadioGroup locationType = (RadioGroup) dialog.findViewById(R.id.radioGroup);
        currentLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    dialogFromLocation.setInputType(InputType.TYPE_NULL);
                    dialogFromLocation.setText(R.string.currentLocation);
                } else {
                    dialogFromLocation.setInputType(InputType.TYPE_CLASS_TEXT);
                    dialogFromLocation.setText("");
                }
            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, names);
        dialogFromLocation.setAdapter(adapter);
        dialogToLocation.setAdapter(adapter);
        Button ok = (Button) dialog.findViewById(R.id.dialogOkButton);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fromLocation = dialogFromLocation.getText().toString();
                String toLocation = dialogToLocation.getText().toString();
                if (fromLocation.isEmpty() || toLocation.isEmpty()) {
                    Toast.makeText(getContext(), "Wprowadź dane", Toast.LENGTH_SHORT).show();
                } else {
                    for (PlaceInfo place : placesList) {
                        if (toLocation.equalsIgnoreCase(place.getTitle())) {
                            to = place;
                        }
                        if (fromLocation.equalsIgnoreCase(place.getTitle())) {
                            from = place;
                        }
                    }
                    if (currentLocation.isChecked()) {
                        PlaceInfo currentPosition = findCurrentLocation();
                        if (currentPosition != null) {
                            fromCurrentLocation = true;
                            from = currentPosition;
                        }
                    } else {
                        fromCurrentLocation = false;
                    }
                    drawPath(from.getGeoPoint(), to.getGeoPoint(), locationType.getCheckedRadioButtonId());
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    public void addCurrentLocationMarker(PlaceInfo place, boolean forNavigation) {
        try {
            Geocoder geo = new Geocoder(getContext(), Locale.getDefault());
            GeoPoint currentLocationGeoPoint = place.getGeoPoint();
            List<Address> addresses = geo.getFromLocation(currentLocationGeoPoint.getLatitude(),currentLocationGeoPoint.getLongitude(), 1);
            if (!addresses.isEmpty()) {
                Marker marker = new Marker(mMapView);
                marker.setPosition(currentLocationGeoPoint);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                marker.setIcon(getResources().getDrawable(R.drawable.marker_red));
                marker.setTitle(getString(R.string.currentLocation));
                marker.setSubDescription(addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryName());
                if(!forNavigation){
                    mMapView.getOverlays().clear();
                }
                mMapView.getOverlays().add(marker);
                mMapView.invalidate();
                mMapController.animateTo(currentLocationGeoPoint);
            }
        } catch (Exception e) {
            Log.e("Error",e.getMessage());
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        String[] addresses = new String[]{"Stefanowskiego 18/22", "Stefanowskiego 1/15", "Żeromskiego 116", "Stefanowskiego 1/15", "Stefanowskiego 2", "Żeromskiego 116", "Żwirki 36",
                "Stefanowskiego", "Stefanowskiego 12/16", "Stefanowskiego 18/22", "Wólczańska 213", "Wólczańska 171/173", "Stefanowskiego 18/22", "Żeromskiego 116"};
        double[] lat = new double[]{51.75252299, 51.75284959, 51.75365549, 51.75373305, 51.75495143, 51.75424923, 51.75503501, 51.75472001, 51.75370351, 51.7537103, 51.75412276, 51.75448702, 51.75259608, 51.75283733};
        double[] lon = new double[]{19.45303313, 19.45258552, 19.45091179, 19.45180004, 19.45124336, 19.45077855, 19.45152691, 19.45264238, 19.4529498, 19.45394666, 19.45414356, 19.45450147, 19.45366977, 19.4502742};

        for (int i = 0; i < names.length; i++) {
            placesList.add(new PlaceInfo(i, names[i], addresses[i], lat[i], lon[i]));
        }

    }

    public void drawRoute(Road road) {
        Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
        roadOverlay.setWidth(10);
        mMapView.getOverlays().clear();
        mMapView.getOverlays().add(roadOverlay);
        if (fromCurrentLocation) {
            addCurrentLocationMarker(from,true);
        }else {
            addMarker(from, false);
        }
        addMarker(to, false);
        mMapView.invalidate();
    }
}
