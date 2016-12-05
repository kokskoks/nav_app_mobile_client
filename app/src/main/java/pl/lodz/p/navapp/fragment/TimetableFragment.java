package pl.lodz.p.navapp.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

import pl.lodz.p.navapp.domain.ClassInfo;
import pl.lodz.p.navapp.OnFragmentInteractionListener;
import pl.lodz.p.navapp.R;
import pl.lodz.p.navapp.RVAdapter;

public class TimetableFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public TimetableFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timetable, container, false);
        AutoCompleteTextView autocompleteLocation = (AutoCompleteTextView) getActivity().findViewById(R.id.mySearchView);
        autocompleteLocation.setAdapter(null);
        RecyclerView rv = (RecyclerView)view.findViewById(R.id.group_recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),1);
        rv.setLayoutManager(gridLayoutManager);
        List<ClassInfo> classInfos = new ArrayList<>();
        ClassInfo przyrka = new ClassInfo("Pszyrka","Akwarium","8:00","10:00");
        ClassInfo religia = new ClassInfo("Religia","Akwarium","10:00","12:00");
        ClassInfo wychowanie = new ClassInfo("Wychowanie do życia w rodzinie","Akwarium","12:00","14:00");
        classInfos.add(przyrka);
        classInfos.add(religia);
        classInfos.add(wychowanie);
        RVAdapter adapter = new RVAdapter(classInfos);
        rv.setAdapter(adapter);
        return view;
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
