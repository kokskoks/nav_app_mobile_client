package pl.lodz.p.navapp.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import pl.lodz.p.navapp.OnFragmentInteractionListener;
import pl.lodz.p.navapp.R;
import pl.lodz.p.navapp.RVAdapter;
import pl.lodz.p.navapp.domain.Classes;
import pl.lodz.p.navapp.domain.Classroom;
import pl.lodz.p.navapp.domain.Group;
import pl.lodz.p.navapp.domain.Lecturer;
import pl.lodz.p.navapp.domain.PlaceInfo;
import pl.lodz.p.navapp.service.RequestManager;

import static pl.lodz.p.navapp.utility.ApplicationConstants.URL;

public class TimetableFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private int groupID;
    private List<Group> groupList;
    private Group group;

    public TimetableFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.groupList = new ArrayList<>();
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timetable, container, false);
        this.groupID = getArguments().getInt("groupID");
        AutoCompleteTextView autocompleteLocation = (AutoCompleteTextView) getActivity().findViewById(R.id.mySearchView);
        autocompleteLocation.setAdapter(null);
        final RecyclerView rv = (RecyclerView) view.findViewById(R.id.group_recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        rv.setLayoutManager(gridLayoutManager);
        RequestManager.sendRequest(Request.Method.GET, URL + "/university-groups/" + groupID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    translateResponeGroup(response);
                    RVAdapter adapter = new RVAdapter(group);
                    rv.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        return view;
    }

    private void translateResponeGroup(String response) throws JSONException {
        JSONObject jsonGroup = new JSONObject(response);
        this.group = new Group();
        group.setID(Integer.parseInt(jsonGroup.getString("id")));
        group.setSubject(jsonGroup.getString("subject").trim());
        group.setCode(jsonGroup.getString("code").trim());
        group.setDescription(jsonGroup.getString("description").trim());
        group.setSemester(jsonGroup.getString("semester").trim());
        group.setSpecialisation(jsonGroup.getString("specialisation").trim());

        JSONArray subClasses = jsonGroup.getJSONArray("classes");
        List<Classes> classesList = new ArrayList<>();
        for (int i = 0; i < subClasses.length(); i++) {
            JSONObject responseSubclasses = (JSONObject) subClasses.get(i);
            Classes classes = new Classes();
            classes.setID(Integer.parseInt(responseSubclasses.getString("id")));
            classes.setName(responseSubclasses.getString("name").trim());
            classes.setModuleCode(responseSubclasses.getString("moduleCode").trim());
            classes.setDescription(responseSubclasses.getString("description").trim());
            classes.setType(responseSubclasses.getString("type").trim());
            classes.setStartHour(Integer.parseInt(responseSubclasses.getString("startHour").trim()));
            classes.setEndHour(Integer.parseInt(responseSubclasses.getString("endHour").trim()));
            classes.setWeekday(responseSubclasses.getString("weekday").trim());
            JSONArray subLecturer = responseSubclasses.getJSONArray("lecturers");
            List<Lecturer> lecturerList = new ArrayList<>();
            for (int j = 0; j < subLecturer.length(); j++) {
                JSONObject responseLecturer = (JSONObject) subLecturer.get(j);
                Lecturer lecturer = new Lecturer();
                lecturer.setID(Integer.parseInt(responseLecturer.getString("id")));
                lecturer.setFirstName(responseLecturer.getString("firstName").trim());
                lecturer.setLastName(responseLecturer.getString("lastName").trim());
                lecturer.setTitle(responseLecturer.getString("title").trim());
                lecturer.setDescription(responseLecturer.getString("description").trim());
                lecturer.setMail(responseLecturer.getString("mail").trim());
                lecturerList.add(lecturer);
            }
            classes.setLecturerList(lecturerList);
            classesList.add(classes);
            JSONObject subClassroom = responseSubclasses.getJSONObject("classroom");
            Classroom classroom = new Classroom();
            classroom.setID(Integer.parseInt(subClassroom.getString("id")));
            classroom.setName(subClassroom.getString("name"));
            classroom.setDescription(subClassroom.getString("desription"));


            JSONObject buildings = subClassroom.getJSONObject("building");
            PlaceInfo placeInfo = new PlaceInfo();
            placeInfo.setID(Integer.parseInt(buildings.getString("id")));
            placeInfo.setTitle(buildings.getString("name").trim());
            placeInfo.setPlaceNumber(buildings.getString("code").trim());
            placeInfo.setDescription(buildings.getString("description").trim());
            placeInfo.setAddress(buildings.getString("street").trim());
            if (!"null".equalsIgnoreCase(buildings.getString("longitude"))) {
                double lon = Double.valueOf(buildings.getString("longitude"));
                double lat = Double.valueOf(buildings.getString("latitude"));
                GeoPoint geoPoint = new GeoPoint(lat, lon);
                placeInfo.setGeoPoint(geoPoint);
            }
            classroom.setBuilding(placeInfo);
            group.setClassroom(classroom);
        }
        group.setClassesList(classesList);
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

}
