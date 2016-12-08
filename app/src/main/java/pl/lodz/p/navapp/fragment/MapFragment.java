package pl.lodz.p.navapp.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pl.lodz.p.navapp.OnFragmentInteractionListener;
import pl.lodz.p.navapp.R;
import pl.lodz.p.navapp.RouteFinder;
import pl.lodz.p.navapp.activity.MainActivity;
import pl.lodz.p.navapp.domain.NavigationInfo;
import pl.lodz.p.navapp.domain.PlaceInfo;
import pl.lodz.p.navapp.service.DatabaseHelper;

import static pl.lodz.p.navapp.ApplicationConstants.MAX_ZOOM_LEVEL;
import static pl.lodz.p.navapp.ApplicationConstants.TravelType.BIKE;
import static pl.lodz.p.navapp.ApplicationConstants.TravelType.CAR;
import static pl.lodz.p.navapp.ApplicationConstants.TravelType.PEDESTRIAN;
import static pl.lodz.p.navapp.ApplicationConstants.ZOOMLEVEL;

public class MapFragment extends Fragment implements LocationListener, MapEventsReceiver {
    private MapView mMapView;
    private MapController mMapController;
    private LocationManager locationManager;
    private OnFragmentInteractionListener mListener;
    private PlaceInfo from;
    private PlaceInfo to;
    private boolean fromCurrentLocation = true;
    private List<String> namesList;
    private DatabaseHelper db;
    PlaceInfo placeInfo;

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
        MainActivity mainActivity = (MainActivity) getActivity();
        db = mainActivity.getCordinatesDB();
        setRetainInstance(true);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    public void addMarker(PlaceInfo place, boolean buildingInfoMarker) {
        Marker marker = new Marker(mMapView);
        GeoPoint point = place.getGeoPoint();
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setIcon(getResources().getDrawable(R.drawable.marker_red));
        InfoWindow infoWindow = new MyInfoWindow(R.layout.bubble, mMapView,place);

        marker.setInfoWindow(infoWindow);
        if (buildingInfoMarker) {
            mMapView.getOverlays().clear();
        }
        mMapView.getOverlays().add(marker);
        mMapView.invalidate();
        if (buildingInfoMarker) {
            mMapController.animateTo(point);
        }

        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(this.getContext(), this);
        mMapView.getOverlays().add(0, mapEventsOverlay);
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
                    addCurrentLocationMarker(currentLocation, false);
                }
            }
        });
        AutoCompleteTextView autocompleteLocation = (AutoCompleteTextView) getActivity().findViewById(R.id.mySearchView);
        autocompleteLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String title = (String) adapterView.getItemAtPosition(i);
                placeInfo = db.getPlace(title.trim());
                addMarker(placeInfo, true);
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
        namesList = ((MainActivity) getActivity()).getNames();
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
                info.setRouteType(BIKE.getType());
                break;
            case R.id.carRadioButton:
                info.setRouteType(CAR.getType());
                break;
            default:
                info.setRouteType(PEDESTRIAN.getType());
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
        namesList = db.getPlacesNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.my_list_layout, namesList);
        dialogFromLocation.setAdapter(adapter);
        dialogToLocation.setAdapter(adapter);
        Button ok = (Button) dialog.findViewById(R.id.dialogOkButton);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fromLocation = dialogFromLocation.getText().toString();
                String toLocation = dialogToLocation.getText().toString();
                to = db.getPlace(toLocation.trim());
                if (currentLocation.isChecked()) {
                    PlaceInfo currentPosition = findCurrentLocation();
                    if (currentPosition != null) {
                        fromCurrentLocation = true;
                        from = currentPosition;
                    }
                } else {
                    fromCurrentLocation = false;
                    from = db.getPlace(fromLocation.trim());
                }
                if (from != null && to != null) {
                    drawPath(from.getGeoPoint(), to.getGeoPoint(), locationType.getCheckedRadioButtonId());
                }
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    public void addCurrentLocationMarker(PlaceInfo place, boolean forNavigation) {
        try {
            Geocoder geo = new Geocoder(getContext(), Locale.getDefault());
            GeoPoint currentLocationGeoPoint = place.getGeoPoint();
            List<Address> addresses = geo.getFromLocation(currentLocationGeoPoint.getLatitude(), currentLocationGeoPoint.getLongitude(), 1);
            if (!addresses.isEmpty()) {
                Marker marker = new Marker(mMapView);
                marker.setPosition(currentLocationGeoPoint);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                marker.setIcon(getResources().getDrawable(R.drawable.marker_red));
                marker.setTitle(getString(R.string.currentLocation));
                marker.setSubDescription(addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryName());
                if (!forNavigation) {
                    mMapView.getOverlays().clear();
                }
                mMapView.getOverlays().add(marker);
                mMapView.invalidate();
                mMapController.animateTo(currentLocationGeoPoint);

                MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(this.getContext(), this);
                mMapView.getOverlays().add(0, mapEventsOverlay);
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint geoPoint) {
        InfoWindow.closeAllInfoWindowsOn(mMapView);
        return true;
    }

    @Override
    public boolean longPressHelper(GeoPoint geoPoint) {
        return false;
    }

    private class MyInfoWindow extends InfoWindow {
        private PlaceInfo info;
        public MyInfoWindow(int layoutResId, MapView mapView, PlaceInfo place) {
            super(layoutResId, mapView);
            this.info=place;
        }

        @Override
        public void onClose() {
        }

        @Override
        public void onOpen(Object arg0) {
            ImageView infoImage =  (ImageView) mView.findViewById(R.id.bubble_image);
            Button btnMoreInfo = (Button) mView.findViewById(R.id.goToInternet);
            TextView txtTitle = (TextView) mView.findViewById(R.id.bubble_title);
            TextView txtDescription = (TextView) mView.findViewById(R.id.bubble_description);

            txtTitle.setText(this.info.getTitle());
            txtDescription.setText(this.info.getAddress());

            int resourceId = getImageFromRes(this.info);
            infoImage.setBackground(getContext().getResources().getDrawable(resourceId));

            btnMoreInfo.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    openWebURL(info.getDescription());
                }
            });
        }
    }

    private int getImageFromRes(PlaceInfo info) {
        int resourceId;
        String place = info.getPlaceNumber();

/*        if(place.equals("A1") || place.equals("A6") || place.equals("B5"))
            resourceId = R.drawable.ipos2;
        else if(place.equals("A2") || place.equals("A8") || place.equals("A9") || place.equals("A24") || place.equals("A26") || place.equals("C1") || place.equals("C2") || place.equals("C18"))
            resourceId = R.drawable.chemiczny2;
        else if(place.equals("A4"))
            resourceId = R.drawable.binoz;
        else if(place.equals("A5"))
            resourceId = R.drawable.ck;
        else if(place.equals("A10"))
            resourceId = R.drawable.weeia;
        else if(place.equals("A11") || place.equals("A12") || place.equals("C6") || place.equals("C7"))
            resourceId = R.drawable.weeia2;
        else if(place.equals("A13"))
            resourceId = R.drawable.rekrutacja;
        else if(place.equals("A16"))
            resourceId = R.drawable.ife;
        else if(place.equals("A18"))
            resourceId = R.drawable.fabryka;
        else if(place.equals("A20") || place.equals("A21") || place.equals("A31"))
            resourceId = R.drawable.mechaniczny2;
        else if(place.equals("A22"))
            resourceId = R.drawable.mechaniczny;
        else if(place.equals("A27"))
            resourceId = R.drawable.chemiczny;
        else if(place.equals("A33"))
            resourceId = R.drawable.tmiwt;
        else if(place.equals("B1"))
            resourceId = R.drawable.rektorat;
        else if(place.equals("B4"))
            resourceId = R.drawable.ipos;
        else if(place.equals("B6"))
            resourceId = R.drawable.bais2;
        else if(place.equals("B7"))
            resourceId = R.drawable.bais;
        else if(place.equals("B9"))
            resourceId = R.drawable.lodex;
        else if(place.equals("C4"))
            resourceId = R.drawable.cs;
        else if(place.equals("C5"))
            resourceId = R.drawable.d6;
        else if(place.equals("C9"))
            resourceId = R.drawable.d7;
        else if(place.equals("C11"))
            resourceId = R.drawable.d4;
        else if(place.equals("C12"))
            resourceId = R.drawable.d3;
        else if(place.equals("C13"))
            resourceId = R.drawable.d2;
        else if(place.equals("C14"))
            resourceId = R.drawable.d1;
        else if(place.equals("C15"))
            resourceId = R.drawable.stolowka;
        else if(place.equals("C3"))
            resourceId = R.drawable.akwarium;
        else if(place.equals("C16"))
            resourceId = R.drawable.d8;
        else if(place.equals("D1")  || place.equals("D3") || place.equals("D4"))
            resourceId = R.drawable.oiz;
        else if(place.equals("E1"))
            resourceId = R.drawable.d9;
        else if(place.equals("W1"))
            resourceId = R.drawable.artefakt;
        else if(place.equals("W2"))
            resourceId = R.drawable.cotton;
        else if(place.equals("W3"))
            resourceId = R.drawable.futurysta;
        else if(place.equals("W4") || place.equals("W11"))
            resourceId = R.drawable.pko;
        else if(place.equals("W5"))
            resourceId = R.drawable.azs;
        else if(place.equals("W6"))
            resourceId = R.drawable.sukcesja;
        else if(place.equals("W7"))
            resourceId = R.drawable.expo;
        else if(place.equals("W8"))
            resourceId = R.drawable.finestra;
        else if(place.equals("W10"))
            resourceId = R.drawable.brodway;
        else if(place.equals("W12"))
            resourceId = R.drawable.indeks;
        else if(place.equals("W13"))
            resourceId = R.drawable.zabka;
        else if(place.equals("W14"))
            resourceId = R.drawable.drukarniastudencka;
        else if(place.equals("W15"))
            resourceId = R.drawable.dino;
        else if(place.equals("W18"))
            resourceId = R.drawable.d5;
        else
        resourceId = R.drawable.pl;*/

        resourceId = R.drawable.pl;

        return resourceId;
    }

    public void openWebURL(String inURL) {
        Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(inURL));
        startActivity(browse);
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

    public void drawRoute(Road road) {
        Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
        roadOverlay.setWidth(10);
        mMapView.getOverlays().clear();
        mMapView.getOverlays().add(roadOverlay);
        if (fromCurrentLocation) {
            addCurrentLocationMarker(from, true);
        } else {
            addMarker(from, false);
        }
        addMarker(to, false);
        mMapView.invalidate();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (fromCurrentLocation) {
            GeoPoint newLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
            drawPath(newLocation, to.getGeoPoint(), R.id.pedestrianRadioButton);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
