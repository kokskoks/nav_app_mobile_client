package pl.lodz.p.navapp.fragment;

import android.Manifest;
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
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pl.lodz.p.navapp.OnFragmentInteractionListener;
import pl.lodz.p.navapp.PlaceInfo;
import pl.lodz.p.navapp.R;

public class MapFragment extends Fragment {
    private MapView mMapView;
    private MapController mMapController;
    String locationProvider;
    public static final int MAX_ZOOM_LEVEL = 20;
    public static final int ZOOMLEVEL = 17;
    LocationManager locationManager;
    private SearchView search;
    private OnFragmentInteractionListener mListener;
    List<PlaceInfo> placesList;
    String[] names;
    String[] addresses;
    double[] lat;
    double[] lon;
    List<Drawable> images;
    AutoCompleteTextView autocompleteLocation;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        placesList = new ArrayList<>();
        createData();


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
        FloatingActionButton fabLocation = (FloatingActionButton) view.findViewById(R.id.fabLocation);
        fabLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationProvider = LocationManager.NETWORK_PROVIDER;
                Location myLocation = locationManager.getLastKnownLocation(locationProvider);
                if (myLocation != null) {
                    addCurrentLocationMarker(myLocation);
                }
            }
        });
        autocompleteLocation = (AutoCompleteTextView) getActivity().findViewById(R.id.mySearchView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, names);
        autocompleteLocation.setAdapter(adapter);
        autocompleteLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                addMarker(i);
            }
        });
        return view;
    }

    public void addCurrentLocationMarker(Location myLocation) {
        try {
            Geocoder geo = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(myLocation.getLatitude(), myLocation.getLongitude(), 1);
            if (addresses.size() > 0) {
                mMapView.getOverlays().clear();
                Marker marker = new Marker(mMapView);
                GeoPoint point = new GeoPoint(myLocation.getLatitude(), myLocation.getLongitude());
                marker.setPosition(point);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                marker.setIcon(getResources().getDrawable(R.drawable.marker_red));
                marker.setTitle(getString(R.string.currentLocation));
                marker.setSubDescription(addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryName());
                mMapView.getOverlays().clear();
                mMapView.getOverlays().add(marker);
                mMapView.invalidate();
                mMapController.animateTo(point);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        GeoPoint gPt = new GeoPoint(myLocation.getLatitude(), myLocation.getLongitude());
        mMapController.animateTo(gPt);

    }

    // TODO: Rename method, update argument and hook method into UI event
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
        addresses = new String[]{"Stefanowskiego 18/22", "Stefanowskiego 1/15", "Żeromskiego 116", "Stefanowskiego 1/15", "Stefanowskiego 2", "Żeromskiego 116", "Żwirki 36",
                "Stefanowskiego", "Stefanowskiego 12/16", "Stefanowskiego 18/22", "Wólczańska 213", "Wólczańska 171/173", "Stefanowskiego 18/22", "Żeromskiego 116"};
        lat = new double[]{51.75252299, 51.75284959, 51.75365549, 51.75373305, 51.75495143, 51.75424923, 51.75503501, 51.75472001, 51.75370351, 51.7537103, 51.75412276, 51.75448702, 51.75259608, 51.75283733};
        lon = new double[]{19.45303313, 19.45258552, 19.45091179, 19.45180004, 19.45124336, 19.45077855, 19.45152691, 19.45264238, 19.4529498, 19.45394666, 19.45414356, 19.45450147, 19.45366977, 19.4502742};

        for (int i = 0; i < names.length; i++) {
            placesList.add(new PlaceInfo(names[i], addresses[i], lat[i], lon[i]));
        }

    }

}
